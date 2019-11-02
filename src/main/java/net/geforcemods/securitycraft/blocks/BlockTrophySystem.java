package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockTrophySystem extends BlockOwnable {

	private static final VoxelShape SHAPE = Stream.of(
			Block.makeCuboidShape(6.5, 0, 12, 9.5, 1.5, 15),
			Block.makeCuboidShape(5.5, 7, 5.5, 10.5, 11, 10.5),
			Block.makeCuboidShape(7, 12, 7, 9, 13, 9),
			Block.makeCuboidShape(6.5, 12.5, 6.5, 9.5, 15, 9.5),
			Block.makeCuboidShape(7, 14.5, 7, 9, 15.5, 9),
			Block.makeCuboidShape(7.25, 9, 7.25, 8.75, 12, 8.75),
			Block.makeCuboidShape(1, 0, 6.5, 4, 1.5, 9.5),
			Block.makeCuboidShape(12, 0, 6.5, 15, 1.5, 9.5),
			Block.makeCuboidShape(6.5, 0, 1, 9.5, 1.5, 4)
			).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube());

	public BlockTrophySystem(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos) {
		return world.getBlockState(pos.down()).isTopSolid();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if(!isValidPosition(state, world, pos)) {
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		return SHAPE;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityTrophySystem();
	}

}
