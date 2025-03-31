package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

public class AlarmBlock extends OwnableBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_EAST = Block.box(0, 4, 4, 8, 12, 12);
	private static final VoxelShape SHAPE_WEST = Block.box(8, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_NORTH = Block.box(4, 4, 8, 12, 12, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.box(4, 4, 0, 12, 12, 8);
	private static final VoxelShape SHAPE_UP = Block.box(4, 0, 4, 12, 8, 12);
	private static final VoxelShape SHAPE_DOWN = Block.box(4, 8, 4, 12, 16, 12);

	public AlarmBlock(BlockBehaviour.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof AlarmBlockEntity be && be.isOwnedBy(player)) {
			if (!level.isClientSide) {
				if (be.isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else
					SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.ALARM, pos));
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction != null && direction.getAxis() != Axis.Y;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, level, pos))
			level.destroyBlock(pos, true);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction facing = ctx.getClickedFace();

		return BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing) ? defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER) : null;
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
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean flag) {
		if (!level.isClientSide)
			level.scheduleTick(pos, state.getBlock(), 5);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide) {
			playSoundAndUpdate(level, pos);
			level.scheduleTick(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader levelReader, BlockPos pos, BlockPos neighbor) {
		if (levelReader.isClientSide() || !(levelReader instanceof Level level))
			return;

		playSoundAndUpdate(level, pos);

		Direction facing = state.getValue(FACING);

		if (!BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing))
			level.destroyBlock(pos, true);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(FACING)) {
			case EAST -> SHAPE_EAST;
			case WEST -> SHAPE_WEST;
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case UP -> SHAPE_UP;
			case DOWN -> SHAPE_DOWN;
			default -> Shapes.block();
		};
	}

	private void playSoundAndUpdate(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.getBlock() != SCContent.ALARM.get())
			return;

		if (level.getBlockEntity(pos) instanceof AlarmBlockEntity be) {
			if (level.getBestNeighborSignal(pos) > 0) {
				boolean isPowered = be.isPowered();

				if (!isPowered) {
					level.setBlockAndUpdate(pos, state.setValue(LIT, true));
					be.setPowered(true);
				}
			}
			else {
				boolean isPowered = be.isPowered();

				if (isPowered) {
					level.setBlockAndUpdate(pos, state.setValue(LIT, false));
					be.setPowered(false);
				}
			}
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		super.playerWillDestroy(level, pos, state, player);
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
		return new AlarmBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.ALARM_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
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
