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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFakeWater extends BlockDynamicLiquid{

	int adjacentSourceBlocks;

	public BlockFakeWater(Material material)
	{
		super(material);
	}

	/**
	 * Updates the flow for the BlockFlowing object.
	 */
	private void placeStaticBlock(World world, BlockPos pos, IBlockState state)
	{
		world.setBlockState(pos, getStaticBlock(blockMaterial).getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
	}

	public static BlockStaticLiquid getStaticBlock(Material material)
	{
		if (material == Material.water)
			return SCContent.bogusWater;
		else if (material == Material.lava)
			return SCContent.bogusLava;
		else
			throw new IllegalArgumentException("Invalid material");
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		int level = ((Integer)state.getValue(LEVEL)).intValue();
		byte b0 = 1;

		if (blockMaterial == Material.lava && !world.provider.doesWaterVaporize())
			b0 = 2;

		int tickRate = tickRate(world);
		int i1;

		if (level > 0)
		{
			int k = -100;
			adjacentSourceBlocks = 0;
			EnumFacing enumfacing;

			for (Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator(); iterator.hasNext(); k = checkAdjacentBlock(world, pos.offset(enumfacing), k))
				enumfacing = (EnumFacing)iterator.next();

			int l = k + b0;

			if (l >= 8 || k < 0)
				l = -1;

			if (getLevel(world, pos.up()) >= 0)
			{
				i1 = getLevel(world, pos.up());

				if (i1 >= 8)
					l = i1;
				else
					l = i1 + 8;
			}

			if (adjacentSourceBlocks >= 2 && blockMaterial == Material.water)
			{
				IBlockState blockBelow = world.getBlockState(pos.down());

				if (blockBelow.getBlock().getMaterial().isSolid())
					l = 0;
				else if (blockBelow.getBlock().getMaterial() == blockMaterial && ((Integer)blockBelow.getValue(LEVEL)).intValue() == 0)
					l = 0;
			}

			if (blockMaterial == Material.lava && level < 8 && l < 8 && l > level && rand.nextInt(4) != 0)
				tickRate *= 4;

			if (l == level)
				placeStaticBlock(world, pos, state);
			else
			{
				level = l;

				if (l < 0)
					world.setBlockToAir(pos);
				else
				{
					state = state.withProperty(LEVEL, Integer.valueOf(l));
					world.setBlockState(pos, state, 2);
					world.scheduleUpdate(pos, this, tickRate);
					world.notifyNeighborsOfStateChange(pos, this);
				}
			}
		}
		else
			placeStaticBlock(world, pos, state);

		IBlockState blockBelow = world.getBlockState(pos.down());

		if (canFlowInto(world, pos.down(), blockBelow))
		{
			if (blockMaterial == Material.lava && world.getBlockState(pos.down()).getBlock().getMaterial() == Material.water)
			{
				world.setBlockState(pos.down(), Blocks.stone.getDefaultState());
				triggerMixEffects(world, pos.down());
				return;
			}

			if (level >= 8)
				tryFlowInto(world, pos.down(), blockBelow, level);
			else
				tryFlowInto(world, pos.down(), blockBelow, level + 8);
		}
		else if (level >= 0 && (level == 0 || isBlocked(world, pos.down(), blockBelow)))
		{
			Set<?> flowDirections = getPossibleFlowDirections(world, pos);
			i1 = level + b0;

			if (level >= 8)
				i1 = 1;

			if (i1 >= 8)
				return;

			Iterator<?> flowDirectionsIterator = flowDirections.iterator();

			while (flowDirectionsIterator.hasNext())
			{
				EnumFacing facing = (EnumFacing)flowDirectionsIterator.next();
				tryFlowInto(world, pos.offset(facing), world.getBlockState(pos.offset(facing)), i1);
			}
		}
	}

	private void tryFlowInto(World world, BlockPos pos, IBlockState state, int level)
	{
		if (canFlowInto(world, pos, state))
		{
			if (state.getBlock() != Blocks.air)
				if (blockMaterial == Material.lava)
					triggerMixEffects(world, pos);
				else
					state.getBlock().dropBlockAsItem(world, pos, state, 0);

			world.setBlockState(pos, getDefaultState().withProperty(LEVEL, Integer.valueOf(level)), 3);
		}
	}

	private int calculateFlowCost(World world, BlockPos pos, int distance, EnumFacing previousDirection)
	{
		int cost = 1000;
		Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();

		while (iterator.hasNext())
		{
			EnumFacing facing = (EnumFacing)iterator.next();

			if (facing != previousDirection)
			{
				BlockPos blockpos1 = pos.offset(facing);
				IBlockState iblockstate = world.getBlockState(blockpos1);

				if (!isBlocked(world, blockpos1, iblockstate) && (iblockstate.getBlock().getMaterial() != blockMaterial || ((Integer)iblockstate.getValue(LEVEL)).intValue() > 0))
				{
					if (!isBlocked(world, blockpos1.down(), iblockstate))
						return distance;

					if (distance < 4)
					{
						int oppositeCost = calculateFlowCost(world, blockpos1, distance + 1, facing.getOpposite());

						if (oppositeCost < cost)
							cost = oppositeCost;
					}
				}
			}
		}

		return cost;
	}

	private Set<?> getPossibleFlowDirections(World world, BlockPos pos)
	{
		int cost = 1000;
		EnumSet<EnumFacing> facings = EnumSet.noneOf(EnumFacing.class);
		Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();

		while (iterator.hasNext())
		{
			EnumFacing facing = (EnumFacing)iterator.next();
			BlockPos blockpos1 = pos.offset(facing);
			IBlockState iblockstate = world.getBlockState(blockpos1);

			if (!isBlocked(world, blockpos1, iblockstate) && (iblockstate.getBlock().getMaterial() != blockMaterial || ((Integer)iblockstate.getValue(LEVEL)).intValue() > 0))
			{
				int oppositeCost;

				if (isBlocked(world, blockpos1.down(), world.getBlockState(blockpos1.down())))
					oppositeCost = calculateFlowCost(world, blockpos1, 1, facing.getOpposite());
				else
					oppositeCost = 0;

				if (oppositeCost < cost)
					facings.clear();

				if (oppositeCost <= cost)
				{
					facings.add(facing);
					cost = oppositeCost;
				}
			}
		}

		return facings;
	}

	private boolean isBlocked(World world, BlockPos pos, IBlockState state)
	{
		Block block = world.getBlockState(pos).getBlock();
		return !(block instanceof BlockDoor) && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds ? (block.getMaterial() == Material.portal ? true : block.getMaterial().blocksMovement()) : true;
	}

	@Override
	protected int checkAdjacentBlock(World world, BlockPos pos, int currentMinLevel)
	{
		int level = getLevel(world, pos);

		if (level < 0)
			return currentMinLevel;
		else
		{
			if (level == 0)
				++adjacentSourceBlocks;

			if (level >= 8)
				level = 0;

			return currentMinLevel >= 0 && level >= currentMinLevel ? currentMinLevel : level;
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote)
			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
				((EntityPlayer) entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			else
				entity.attackEntityFrom(CustomDamageSources.fakeWater, 5F);
	}

	private boolean canFlowInto(World world, BlockPos pos, IBlockState state)
	{
		Material material = state.getBlock().getMaterial();
		return material != blockMaterial && material != Material.lava && !isBlocked(world, pos, state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if (!checkForMixing(world, pos, state))
			world.scheduleUpdate(pos, this, tickRate(world));
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos)
	{
		return null;
	}
}
