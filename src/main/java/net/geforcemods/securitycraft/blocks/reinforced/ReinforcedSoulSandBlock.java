package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedSoulSandBlock extends BaseReinforcedBlock {
	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

	public ReinforcedSoulSandBlock(BlockBehaviour.Properties properties, Block vanillaBlock) {
		super(properties, vanillaBlock);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BubbleColumnBlock.updateColumn(level, pos.above(), state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.UP && facingState.is(Blocks.WATER))
			level.scheduleTick(currentPos, this, 20);

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		level.scheduleTick(pos, this, 20);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 0.2F;
	}
}
