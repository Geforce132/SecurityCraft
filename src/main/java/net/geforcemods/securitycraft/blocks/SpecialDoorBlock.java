package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SpecialDoorBlock extends BlockDoor implements ITileEntityProvider, IOverlayDisplay, IDisguisable {
	protected SpecialDoorBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		onNeighborChanged(world, pos, fromPos);
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 *
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(IBlockAccess access, BlockPos pos, BlockPos neighbor) {
		World world = (World) access;

		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			Block neighborBlock = world.getBlockState(neighbor).getBlock();

			if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
				BlockPos blockBelow = pos.down();
				IBlockState stateBelow = world.getBlockState(blockBelow);

				if (stateBelow.getBlock() != this)
					world.setBlockToAir(pos);
				else if (neighborBlock != this)
					onNeighborChanged(world, blockBelow, neighbor);
			}
			else {
				boolean drop = false;
				BlockPos blockAbove = pos.up();
				IBlockState stateAbove = world.getBlockState(blockAbove);

				if (stateAbove.getBlock() != this) {
					world.setBlockToAir(pos);
					drop = true;
				}

				if (!world.isSideSolid(pos.down(), EnumFacing.UP)) {
					world.setBlockToAir(pos);
					drop = true;

					if (stateAbove.getBlock() == this)
						world.setBlockToAir(blockAbove);
				}

				if (drop && !world.isRemote)
					dropBlockAsItem(world, pos, state, 0);
			}
		}
	}

	@Override
	public void updateTick(World world, BlockPos upperPos, IBlockState state, Random random) {
		IBlockState upperState = world.getBlockState(upperPos);

		if (!upperState.getValue(BlockDoor.OPEN))
			return;

		BlockPos lowerPos;
		IBlockState lowerState;

		if (upperState.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
			lowerPos = upperPos;
			lowerState = upperState;
			upperPos = upperPos.up();
			upperState = world.getBlockState(upperPos);
		}
		else {
			lowerPos = upperPos.down();
			lowerState = world.getBlockState(lowerPos);
		}

		world.setBlockState(upperPos, upperState.withProperty(BlockDoor.OPEN, false));
		world.setBlockState(lowerPos, lowerState.withProperty(BlockDoor.OPEN, false));
		world.playEvent(null, 1011, upperPos, 0);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		if (te instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

		world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		TileEntity te = world.getTileEntity(pos);

		return te != null && te.receiveClientEvent(id, param);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : getDoorItem();
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world, pos);

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getLightValue(world, pos);
		else
			return super.getLightValue(state, world, pos);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState actualState = getDisguisedBlockState(world, pos);

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getBlock().getSoundType(actualState, world, pos, entity);
		else
			return blockSoundType;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world, pos);

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getBoundingBox(world, pos);
		else
			return super.getBoundingBox(state, world, pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		IBlockState actualState = getDisguisedBlockState(world, pos);

		if (actualState != null && actualState.getBlock() != this) {
			if (!state.getValue(OPEN))
				actualState.addCollisionBoxToList(world, pos, entityBox, collidingBoxes, entity, true);
		}
		else
			addCollisionBoxToList(pos, entityBox, collidingBoxes, getCollisionBoundingBox(state, world, pos));
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return IDisguisable.getDisguisedBlockFaceShape(world, state, pos, face);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return IDisguisable.shouldDisguisedSideBeRendered(state, world, pos, side);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState disguisedState = getDisguisedBlockState(world, pos);

		return disguisedState != null ? disguisedState : state;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return getDisguisedStack(world, pos).getItem() == Item.getItemFromBlock(this);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getDisguisedStack(world, pos);
	}

	@Override
	public ItemStack getDefaultStack() {
		return new ItemStack(getDoorItem());
	}

	public abstract Item getDoorItem();
}
