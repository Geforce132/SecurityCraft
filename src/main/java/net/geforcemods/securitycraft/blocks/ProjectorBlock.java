package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ProjectorBlock extends DisguisableBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	private static final VoxelShape FLOOR_NORTH = Shapes.or(Block.box(12, 0, 12, 15, 2, 15), Block.box(1, 2, 1, 15, 7, 15), Block.box(9, 2, 15, 14, 7, 16), Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 0, 12, 4, 2, 15));
	private static final VoxelShape FLOOR_SOUTH = Shapes.or(Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 2, 1, 15, 7, 15), Block.box(2, 2, 0, 7, 7, 1), Block.box(1, 0, 12, 4, 2, 15), Block.box(12, 0, 12, 15, 2, 15), Block.box(12, 0, 1, 15, 2, 4));
	private static final VoxelShape FLOOR_WEST = Shapes.or(Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 2, 1, 15, 7, 15), Block.box(15, 2, 2, 16, 7, 7), Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 0, 12, 4, 2, 15), Block.box(12, 0, 12, 15, 2, 15));
	private static final VoxelShape FLOOR_EAST = Shapes.or(Block.box(1, 0, 12, 4, 2, 15), Block.box(1, 2, 1, 15, 7, 15), Block.box(0, 2, 9, 1, 7, 14), Block.box(12, 0, 12, 15, 2, 15), Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 0, 1, 4, 2, 4));
	private static final VoxelShape CEILING_NORTH = Shapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(9, 6, 15, 14, 11, 16), Block.box(7, 11, 4, 9, 16, 6));
	private static final VoxelShape CEILING_SOUTH = Shapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(2, 6, 0, 7, 11, 1), Block.box(7, 11, 10, 9, 16, 12));
	private static final VoxelShape CEILING_WEST = Shapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(15, 6, 2, 16, 11, 7), Block.box(4, 11, 7, 6, 16, 9));
	private static final VoxelShape CEILING_EAST = Shapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(0, 6, 9, 1, 11, 14), Block.box(10, 11, 7, 12, 16, 9));

	public ProjectorBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HANGING, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else if (!disguisedState.getValue(HANGING)) {
			return switch (disguisedState.getValue(FACING)) {
				case NORTH -> FLOOR_NORTH;
				case EAST -> FLOOR_EAST;
				case SOUTH -> FLOOR_SOUTH;
				case WEST -> FLOOR_WEST;
				default -> Shapes.block();
			};
		}
		else {
			return switch (disguisedState.getValue(FACING)) {
				case NORTH -> CEILING_NORTH;
				case EAST -> CEILING_EAST;
				case SOUTH -> CEILING_SOUTH;
				case WEST -> CEILING_WEST;
				default -> Shapes.block();
			};
		}
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof ProjectorBlockEntity be))
			return InteractionResult.FAIL;

		boolean isOwner = be.isOwnedBy(player);

		if (!level.isClientSide() && isOwner)
			player.openMenu(be);

		return isOwner ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction != null && direction.getAxis() != Axis.Y;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ProjectorBlockEntity be && be.isActivatedByRedstone()) {
			be.setActive(level.hasNeighborSignal(pos));
			level.sendBlockUpdated(pos, state, state, 3);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if (!level.hasNeighborSignal(pos) && level.getBlockEntity(pos) instanceof ProjectorBlockEntity be && be.isActivatedByRedstone())
			be.setActive(false);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(HANGING, ctx.getClickedFace() == Direction.DOWN);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HANGING, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ProjectorBlockEntity(pos, state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
