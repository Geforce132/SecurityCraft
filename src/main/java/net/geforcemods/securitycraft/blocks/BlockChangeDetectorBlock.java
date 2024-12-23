package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockChangeDetectorBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	public static final VoxelShape FLOOR = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D), Block.box(7.0D, 9.0D, 7.0D, 9.0D, 16.0D, 9.0D));
	public static final VoxelShape CEILING = Shapes.or(Block.box(0.0D, 7.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 7.0D, 9.0D));
	public static final VoxelShape WALL_N = Shapes.or(Block.box(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 16.0D), Block.box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 7.0D));
	public static final VoxelShape WALL_E = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 9.0D, 16.0D, 16.0D), Block.box(9.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D));
	public static final VoxelShape WALL_S = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 9.0D), Block.box(7.0D, 7.0D, 9.0D, 9.0D, 9.0D, 16.0D));
	public static final VoxelShape WALL_W = Shapes.or(Block.box(7.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(0.0D, 7.0D, 7.0D, 7.0D, 9.0D, 9.0D));

	public BlockChangeDetectorBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(FACE, AttachFace.FLOOR).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else {
			return switch (state.getValue(FACE)) {
				case FLOOR -> FLOOR;
				case CEILING -> CEILING;
				case WALL -> switch (state.getValue(FACING)) {
					case NORTH -> WALL_N;
					case EAST -> WALL_E;
					case SOUTH -> WALL_S;
					case WEST -> WALL_W;
					default -> Shapes.empty();
				};
			};
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction direction = ctx.getClickedFace();
		BlockState state;

		if (direction.getAxis() == Direction.Axis.Y)
			state = defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.FLOOR : AttachFace.CEILING).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
		else
			state = defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction);

		return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			player.openMenu(be, pos);

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be)
			Block.popResource(level, pos, be.getStackInSlot(36));

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, this, AbstractPanelBlock.getConnectedDirection(state).getOpposite());
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) && AbstractPanelBlock.getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED, FACE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockChangeDetectorBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
