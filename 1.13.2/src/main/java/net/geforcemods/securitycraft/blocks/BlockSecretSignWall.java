package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSecretSignWall extends BlockSecretSign
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	protected static final AxisAlignedBB SIGN_EAST_AABB = new AxisAlignedBB(0.0D, 0.28125D, 0.0D, 0.125D, 0.78125D, 1.0D);
	protected static final AxisAlignedBB SIGN_WEST_AABB = new AxisAlignedBB(0.875D, 0.28125D, 0.0D, 1.0D, 0.78125D, 1.0D);
	protected static final AxisAlignedBB SIGN_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.28125D, 0.0D, 1.0D, 0.78125D, 0.125D);
	protected static final AxisAlignedBB SIGN_NORTH_AABB = new AxisAlignedBB(0.0D, 0.28125D, 0.875D, 1.0D, 0.78125D, 1.0D);

	public BlockSecretSignWall()
	{
		super();
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		//		switch (state.getValue(FACING))
		//		{
		//			case NORTH:
		//			default:
		//				return SIGN_NORTH_AABB;
		//			case SOUTH:
		//				return SIGN_SOUTH_AABB;
		//			case WEST:
		//				return SIGN_WEST_AABB;
		//			case EAST:
		//				return SIGN_EAST_AABB;
		//		}
		return VoxelShapes.fullCube();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		EnumFacing facing = state.get(FACING);

		if (!world.getBlockState(pos.offset(facing.getOpposite())).getMaterial().isSolid())
		{
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}

		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public IBlockState mirror(IBlockState state, Mirror mirror) {
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
	}
}