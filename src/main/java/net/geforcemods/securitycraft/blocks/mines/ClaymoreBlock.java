package net.geforcemods.securitycraft.blocks.mines;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.MineBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class ClaymoreBlock extends ExplosiveBlock implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape NORTH = Stream.of(Block.box(4, 0, 8, 12, 6, 9), Block.box(5, 0, 7, 11, 6, 8), Block.box(6, 6, 8, 10, 7, 9)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape EAST = Stream.of(Block.box(7, 0, 4, 8, 6, 12), Block.box(8, 0, 5, 9, 6, 11), Block.box(7, 6, 6, 8, 7, 10)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape SOUTH = Stream.of(Block.box(4, 0, 7, 12, 6, 8), Block.box(5, 0, 8, 11, 6, 9), Block.box(6, 6, 7, 10, 7, 8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape WEST = Stream.of(Block.box(8, 0, 4, 9, 6, 12), Block.box(7, 0, 5, 8, 6, 11), Block.box(8, 6, 6, 9, 7, 10)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public ClaymoreBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DEACTIVATED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ClaymoreBlockEntity be && be.isOwnedBy(player)) {
			if (!level.isClientSide)
				NetworkHooks.openScreen((ServerPlayer) player, be, pos);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (level.getBlockState(pos.below()).isAir())
			level.destroyBlock(pos, true);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return BlockUtils.isSideSolid(level, pos.below(), Direction.UP);
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
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!player.isCreative() && !level.isClientSide && !level.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			level.destroyBlock(pos, false);

			if (!EntityUtils.doesPlayerOwn(player, level, pos) && ((MineBlockEntity) level.getBlockEntity(pos)).getTargetingMode().allowsPlayers())
				explode(level, pos);
		}

		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		if (!level.isClientSide && level.getBlockState(pos).hasProperty(ClaymoreBlock.DEACTIVATED) && !level.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			if (pos.equals(BlockPos.containing(explosion.getPosition())))
				return;

			explode(level, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getPlayer().getDirection()).setValue(DEACTIVATED, false).setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public boolean activateMine(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (!state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}

		return false;
	}

	@Override
	public void explode(Level level, BlockPos pos) {
		if (!level.isClientSide) {
			level.destroyBlock(pos, false);
			level.explode(null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.5F : 3.5F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());
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
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ClaymoreBlockEntity be) {
			be.dropAllModules();
			Containers.dropContents(level, pos, be.getLensContainer());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(FACING)) {
			case NORTH -> NORTH;
			case EAST -> EAST;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			default -> Shapes.block();
		};
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, DEACTIVATED, WATERLOGGED);
	}

	@Override
	public boolean isActive(Level level, BlockPos pos) {
		return !level.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ClaymoreBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, SCContent.CLAYMORE_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
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
