package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockReinforcedCarpet extends BlockReinforcedBase
{
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	public BlockReinforcedCarpet(Block block, String registryPath)
	{
		super(SoundType.CLOTH, Material.CLOTH, block, registryPath);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return SHAPE;
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return !stateIn.isValidPosition(world, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
		return !worldIn.isAirBlock(pos.down());
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		//needed so reinforced carpets do not drop when the block below them is broken
	}
}
