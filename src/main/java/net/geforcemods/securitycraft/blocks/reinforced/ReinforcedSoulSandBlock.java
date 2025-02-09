package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedSoulSandBlock extends BaseReinforcedBlock {
	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

	public ReinforcedSoulSandBlock(AbstractBlock.Properties properties, Block vanillaBlock) {
		super(properties, vanillaBlock);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos) {
		return VoxelShapes.block();
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.block();
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		BubbleColumnBlock.growColumn(level, pos.above(), false);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.UP && facingState.is(Blocks.WATER))
			level.getBlockTicks().scheduleTick(currentPos, this, 20);

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void onPlace(BlockState state, World level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.getBlockTicks().scheduleTick(pos, this, 20);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader level, BlockPos pos, PathType pathComputationType) {
		return false;
	}
}
