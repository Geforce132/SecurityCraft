package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MotionActivatedLightBlock extends OwnableBlock implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(6, 3, 13, 10, 4, 14), Shapes.or(Block.box(6, 6, 13, 10, 9, 14), Shapes.joinUnoptimized(Block.box(7, 3, 14, 9, 8, 16), Block.box(7, 4, 14, 9, 7, 15), BooleanOp.ONLY_FIRST)));
	private static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(2, 3, 6, 3, 4, 10), Shapes.or(Block.box(2, 6, 6, 3, 9, 10), Shapes.joinUnoptimized(Block.box(0, 3, 7, 2, 8, 9), Block.box(1, 4, 7, 2, 7, 9), BooleanOp.ONLY_FIRST)));
	private static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(6, 3, 2, 10, 4, 3), Shapes.or(Block.box(6, 6, 2, 10, 9, 3), Shapes.joinUnoptimized(Block.box(7, 3, 0, 9, 8, 2), Block.box(7, 4, 1, 9, 7, 2), BooleanOp.ONLY_FIRST)));
	private static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(13, 3, 6, 14, 4, 10), Shapes.or(Block.box(13, 6, 6, 14, 9, 10), Shapes.joinUnoptimized(Block.box(14, 3, 7, 16, 8, 9), Block.box(14, 4, 7, 15, 7, 9), BooleanOp.ONLY_FIRST)));

	public MotionActivatedLightBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(FACING)) {
			case NORTH -> SHAPE_NORTH;
			case EAST -> SHAPE_EAST;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			default -> Shapes.block();
		};
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction side = state.getValue(FACING);

		return side != Direction.UP && side != Direction.DOWN && BlockUtils.isSideSolid(level, pos.relative(side.getOpposite()), side);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction facing = ctx.getClickedFace();

		return facing != Direction.UP && facing != Direction.DOWN && BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER) : null;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, level, pos))
			level.destroyBlock(pos, true);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && !ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.dropAllModules();

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MotionActivatedLightBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(type, SCContent.MOTION_LIGHT_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Direction facing = state.getValue(FACING);

		switch (mirror) {
			case LEFT_RIGHT:
				if (facing.getAxis() == Axis.Z)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if (facing.getAxis() == Axis.X)
					return state.setValue(FACING, facing.getOpposite());
				break;
			case NONE:
				break;
		}

		return state;
	}
}
