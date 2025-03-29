package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.MineBlockEntity;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MineBlock extends ExplosiveBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 3, 11);

	public MineBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean flag) {
		if (level.getBlockState(pos.below()).isAir()) {
			if (level.getBlockState(pos).getValue(DEACTIVATED))
				level.destroyBlock(pos, true);
			else
				explode(level, pos);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return BlockUtils.isSideSolid(level, pos.below(), Direction.UP);
	}

	@Override
	public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		if (state.getValue(WATERLOGGED))
			tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!level.isClientSide) {
			MineBlockEntity mine = (MineBlockEntity) level.getBlockEntity(pos);
			TargetingMode mode = mine.getTargetingMode();

			if (!mode.allowsPlayers() || player != null && player.isCreative() && !ConfigHandler.SERVER.mineExplodesWhenInCreative.get())
				return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
			else if (mine.isOwnedBy(player) && !mine.ignoresOwner()) {
				explode(level, pos);
				return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
			}
		}

		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide && entity instanceof LivingEntity livingEntity && getShape(state, level, pos, CollisionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox())) {
			MineBlockEntity mine = (MineBlockEntity) level.getBlockEntity(pos);
			TargetingMode mode = mine.getTargetingMode();

			if (mode.canAttackEntity(livingEntity, mine, e -> false))
				explode(level, pos);
		}
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
		if (!level.isClientSide && !level.getBlockState(pos).getValue(DEACTIVATED)) {
			level.destroyBlock(pos, false);
			level.explode(null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.0F : 3.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(DEACTIVATED, WATERLOGGED);
	}

	@Override
	public boolean isActive(Level level, BlockPos pos) {
		return !level.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MineBlockEntity(pos, state);
	}
}
