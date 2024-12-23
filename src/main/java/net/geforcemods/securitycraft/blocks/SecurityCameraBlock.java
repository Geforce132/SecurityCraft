package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;

public class SecurityCameraBlock extends DisguisableBlock implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty BEING_VIEWED = BooleanProperty.create("being_viewed");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_SOUTH = Shapes.create(new AABB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = Shapes.create(new AABB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = Shapes.create(new AABB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = Shapes.create(new AABB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = Shapes.or(Block.box(7, 15, 5, 9, 16, 11), Shapes.or(Block.box(6, 15, 6, 7, 16, 10), Shapes.or(Block.box(5, 15, 7, 6, 16, 9), Shapes.or(Block.box(9, 15, 6, 10, 16, 10), Shapes.or(Block.box(10, 15, 7, 11, 16, 9), Block.box(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(BEING_VIEWED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (stack.is(SCContent.CAMERA_MONITOR.get()))
			return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be && be.isOwnedBy(player)) {
			if (!level.isClientSide)
				player.openMenu(be, pos);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
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
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be) {
			be.dropAllModules();
			Containers.dropContents(level, pos, be.getLensContainer());
		}

		level.updateNeighborsAt(pos.north(), state.getBlock());
		level.updateNeighborsAt(pos.south(), state.getBlock());
		level.updateNeighborsAt(pos.east(), state.getBlock());
		level.updateNeighborsAt(pos.west(), state.getBlock());
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else {
			return switch (state.getValue(FACING)) {
				case SOUTH -> SHAPE_SOUTH;
				case NORTH -> SHAPE_NORTH;
				case WEST -> SHAPE_WEST;
				case DOWN -> SHAPE_DOWN;
				default -> SHAPE;
			};
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction direction = ctx.getClickedFace();

		if (direction != Direction.UP) {
			Level level = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			BlockState state = defaultBlockState().setValue(FACING, direction);

			if (!canSurvive(state, level, pos)) {
				for (Direction newFacing : Direction.Plane.HORIZONTAL) {
					state = state.setValue(FACING, newFacing);

					if (canSurvive(state, level, pos))
						break;
				}
			}

			return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
		}
		else
			return null;
	}

	public void mountCamera(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			ServerLevel serverLevel = (ServerLevel) level;
			ServerPlayer serverPlayer = (ServerPlayer) player;
			SecurityCamera dummyEntity;
			SectionPos chunkPos = SectionPos.of(pos);
			int viewDistance = Mth.clamp(serverPlayer.requestedViewDistance(), 2, serverPlayer.server.getPlayerList().getViewDistance());

			if (serverPlayer.getCamera() instanceof SecurityCamera cam)
				cam.discard();

			dummyEntity = new SecurityCamera(level, pos);
			dummyEntity.setChunkLoadingDistance(viewDistance);
			//can't use ServerPlayer#setCamera here because it also teleports the player
			serverPlayer.camera = dummyEntity;
			level.addFreshEntity(dummyEntity);

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					SecurityCraft.CAMERA_TICKET_CONTROLLER.forceChunk(serverLevel, dummyEntity, x, z, true, false);
				}
			}

			PacketDistributor.sendToPlayer(serverPlayer, new SetCameraView(dummyEntity.getId()));

			if (level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity cam)
				cam.startViewing();
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction facing = state.getValue(FACING);

		return BlockUtils.isSideSolid(level, pos.relative(facing.getOpposite()), facing);
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
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED) && ((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED) && ((IModuleInventory) level.getBlockEntity(pos)).isModuleEnabled(ModuleType.REDSTONE) && state.getValue(FACING) == side)
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, level, pos))
			level.destroyBlock(pos, true);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, BEING_VIEWED, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SecurityCameraBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
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
