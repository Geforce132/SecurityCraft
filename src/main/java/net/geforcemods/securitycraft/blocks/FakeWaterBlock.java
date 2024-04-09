package net.geforcemods.securitycraft.blocks;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWaterBlock extends BlockDynamicLiquid {
	int adjacentSourceBlocks;

	public FakeWaterBlock(Material material) {
		super(material);
	}

	private void placeStaticBlock(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, getStaticBlock(material).getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
	}

	public static BlockStaticLiquid getStaticBlock(Material material) {
		if (material == Material.WATER)
			return SCContent.fakeWater;
		else if (material == Material.LAVA)
			return SCContent.fakeLava;
		else
			throw new IllegalArgumentException("Invalid material");
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		int level = state.getValue(LEVEL);
		byte levelToAdd = 1;

		if (material == Material.LAVA && !world.provider.doesWaterVaporize())
			levelToAdd = 2;

		int tickRate = tickRate(world);
		int levelAbove;

		if (level > 0) {
			int currentMinLevel = -100;
			adjacentSourceBlocks = 0;
			EnumFacing facing;

			for (Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator(); iterator.hasNext(); currentMinLevel = checkAdjacentBlock(world, pos.offset(facing), currentMinLevel)) {
				facing = (EnumFacing) iterator.next();
			}

			int nextLevel = currentMinLevel + levelToAdd;

			if (nextLevel >= 8 || currentMinLevel < 0)
				nextLevel = -1;

			if (getDepth(world.getBlockState(pos.up())) >= 0) {
				levelAbove = getDepth(world.getBlockState(pos.up()));

				if (levelAbove >= 8)
					nextLevel = levelAbove;
				else
					nextLevel = levelAbove + 8;
			}

			if (adjacentSourceBlocks >= 2 && material == Material.WATER) {
				IBlockState stateBelow = world.getBlockState(pos.down());

				if (stateBelow.getMaterial().isSolid() || (stateBelow.getMaterial() == material && stateBelow.getValue(LEVEL) == 0))
					nextLevel = 0;
			}

			if (material == Material.LAVA && level < 8 && nextLevel < 8 && nextLevel > level && rand.nextInt(4) != 0)
				tickRate *= 4;

			if (nextLevel == level)
				placeStaticBlock(world, pos, state);
			else {
				level = nextLevel;

				if (nextLevel < 0)
					world.setBlockToAir(pos);
				else {
					state = state.withProperty(LEVEL, nextLevel);
					world.setBlockState(pos, state, 2);
					world.scheduleUpdate(pos, this, tickRate);
					world.notifyNeighborsOfStateChange(pos, this, false);
				}
			}
		}
		else
			placeStaticBlock(world, pos, state);

		BlockPos downPos = pos.down();
		IBlockState stateBelow = world.getBlockState(downPos);

		if (canFlowInto(stateBelow)) {
			if (material == Material.LAVA && world.getBlockState(downPos).getBlock().getMaterial(world.getBlockState(downPos)) == Material.WATER) {
				world.setBlockState(downPos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, downPos, pos, Blocks.STONE.getDefaultState()));
				triggerMixEffects(world, downPos);
				return;
			}

			if (level >= 8)
				tryFlowInto(world, downPos, stateBelow, level);
			else
				tryFlowInto(world, downPos, stateBelow, level + 8);
		}
		else if (level >= 0 && (level == 0 || isBlocked(stateBelow))) {
			Set<?> flowDirections = getPossibleFlowDirections(world, pos);
			levelAbove = level + levelToAdd;

			if (level >= 8)
				levelAbove = 1;

			if (levelAbove >= 8)
				return;

			Iterator<?> flowDirectionsIterator = flowDirections.iterator();

			while (flowDirectionsIterator.hasNext()) {
				EnumFacing facing = (EnumFacing) flowDirectionsIterator.next();
				tryFlowInto(world, pos.offset(facing), world.getBlockState(pos.offset(facing)), levelAbove);
			}
		}
	}

	private void tryFlowInto(World world, BlockPos pos, IBlockState state, int level) {
		if (canFlowInto(state)) {
			if (state.getBlock() != Blocks.AIR)
				if (material == Material.LAVA)
					triggerMixEffects(world, pos);
				else
					state.getBlock().dropBlockAsItem(world, pos, state, 0);

			world.setBlockState(pos, getDefaultState().withProperty(LEVEL, level), 3);
		}
	}

	private int calculateFlowCost(World world, BlockPos pos, int distance, EnumFacing previousDirection) {
		int cost = 1000;
		Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();

		while (iterator.hasNext()) {
			EnumFacing facing = (EnumFacing) iterator.next();

			if (facing != previousDirection) {
				BlockPos offsetPos = pos.offset(facing);
				IBlockState offsetState = world.getBlockState(offsetPos);

				if (!isBlocked(offsetState) && (offsetState.getMaterial() != material || offsetState.getValue(LEVEL) > 0)) {
					BlockPos downPos = offsetPos.down();

					if (!isBlocked(world.getBlockState(downPos)))
						return distance;

					if (distance < 4) {
						int oppositeCost = calculateFlowCost(world, offsetPos, distance + 1, facing.getOpposite());

						if (oppositeCost < cost)
							cost = oppositeCost;
					}
				}
			}
		}

		return cost;
	}

	private Set<?> getPossibleFlowDirections(World world, BlockPos pos) {
		int cost = 1000;
		EnumSet<EnumFacing> facings = EnumSet.noneOf(EnumFacing.class);
		Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();

		while (iterator.hasNext()) {
			EnumFacing facing = (EnumFacing) iterator.next();
			BlockPos offsetPos = pos.offset(facing);
			IBlockState offsetState = world.getBlockState(offsetPos);

			if (!isBlocked(offsetState) && (offsetState.getMaterial() != material || offsetState.getValue(LEVEL) > 0)) {
				int oppositeCost;
				BlockPos downPos = offsetPos.down();

				if (isBlocked(world.getBlockState(downPos)))
					oppositeCost = calculateFlowCost(world, offsetPos, 1, facing.getOpposite());
				else
					oppositeCost = 0;

				if (oppositeCost < cost)
					facings.clear();

				if (oppositeCost <= cost) {
					facings.add(facing);
					cost = oppositeCost;
				}
			}
		}

		return facings;
	}

	private boolean isBlocked(IBlockState state) {
		Block block = state.getBlock();
		Material mat = state.getMaterial();

		if (!(block instanceof BlockDoor) && block != Blocks.STANDING_SIGN && block != Blocks.LADDER && block != Blocks.REEDS && mat != Material.PORTAL && mat != Material.STRUCTURE_VOID)
			return mat.blocksMovement();

		return true;
	}

	@Override
	protected int checkAdjacentBlock(World world, BlockPos pos, int currentMinLevel) {
		int level = getDepth(world.getBlockState(pos));

		if (level < 0)
			return currentMinLevel;
		else {
			if (level == 0)
				++adjacentSourceBlocks;

			if (level >= 8)
				level = 0;

			return currentMinLevel >= 0 && level >= currentMinLevel ? currentMinLevel : level;
		}
	}

	private boolean canFlowInto(IBlockState state) {
		Material material = state.getBlock().getMaterial(state);

		return material != this.material && material != Material.LAVA && !isBlocked(state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!checkForMixing(world, pos, state))
			world.scheduleUpdate(pos, this, tickRate(world));
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && !(entity instanceof EntityItem) && !(entity instanceof EntityBoat)) {
			if (!(entity instanceof EntityPlayer) || (!((EntityPlayer) entity).capabilities.isCreativeMode && !(((EntityPlayer) entity).getRidingEntity() instanceof EntityBoat)))
				entity.attackEntityFrom(CustomDamageSources.FAKE_WATER, 5F);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}
}
