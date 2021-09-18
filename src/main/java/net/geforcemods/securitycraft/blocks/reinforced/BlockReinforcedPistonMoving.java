package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPiston;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedPistonMoving extends BlockPistonMoving {

	public static TileEntity createTilePiston(IBlockState state, NBTTagCompound tag, EnumFacing facing, boolean extending, boolean shouldHeadBeRendered) {
		return new TileEntityReinforcedPiston(state, tag, facing, extending, shouldHeadBeRendered);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityReinforcedPiston) {
			((TileEntityReinforcedPiston)te).clearPistonTileEntity();
		}
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
		BlockPos oppositePos = pos.offset(state.getValue(FACING).getOpposite());
		IBlockState oppositeState = world.getBlockState(oppositePos);

		if (oppositeState.getBlock() instanceof BlockReinforcedPistonBase && oppositeState.getValue(BlockPistonBase.EXTENDED)) {
			world.setBlockToAir(oppositePos);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
		TileEntityReinforcedPiston tileEntityReinforcedPiston = getTilePistonAt(world, pos);

		return tileEntityReinforcedPiston == null ? null : tileEntityReinforcedPiston.getAABB(world, pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
		TileEntityReinforcedPiston tileEntityReinforcedPiston = getTilePistonAt(world, pos);

		if (tileEntityReinforcedPiston != null) {
			tileEntityReinforcedPiston.addCollisionAABBs(world, pos, entityBox, collidingBoxes, entity);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntityReinforcedPiston tileEntityReinforcedPiston = getTilePistonAt(source, pos);

		return tileEntityReinforcedPiston != null ? tileEntityReinforcedPiston.getAABB(source, pos) : FULL_BLOCK_AABB;
	}

	private TileEntityReinforcedPiston getTilePistonAt(IBlockAccess world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);

		return tileentity instanceof TileEntityReinforcedPiston ? (TileEntityReinforcedPiston)tileentity : null;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		TileEntityReinforcedPiston tileEntityReinforcedPiston = getTilePistonAt(world, pos);

		if (tileEntityReinforcedPiston != null) {
			IBlockState pushed = tileEntityReinforcedPiston.getPistonState();
			drops.addAll(pushed.getBlock().getDrops(world, pos, pushed, fortune));
		}
	}
}
