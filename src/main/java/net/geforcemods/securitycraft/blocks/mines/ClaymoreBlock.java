package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClaymoreBlock extends ExplosiveBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape NORTH_OFF = Shapes.or(Block.box(4, 0, 5, 12, 4, 7), Shapes.or(Block.box(4, 4, 5, 12, 5, 6), Shapes.or(Block.box(5, 4, 4, 6, 5, 5), Shapes.or(Block.box(10, 4, 4, 11, 5, 5), Shapes.or(Block.box(4, 4, 3, 5, 5, 4), Block.box(11, 4, 3, 12, 5, 4))))));
	private static final VoxelShape NORTH_ON = Shapes.or(NORTH_OFF, Shapes.or(Block.box(3, 4, 2, 4, 5, 3), Block.box(12, 4, 2, 13, 5, 3)));
	private static final VoxelShape EAST_OFF = Shapes.or(Block.box(9, 0, 4, 11, 4, 12), Shapes.or(Block.box(10, 4, 4, 11, 5, 12), Shapes.or(Block.box(11, 4, 5, 12, 5, 6), Shapes.or(Block.box(11, 4, 10, 12, 5, 11), Shapes.or(Block.box(12, 4, 4, 13, 5, 5), Block.box(12, 4, 11, 13, 5, 12))))));
	private static final VoxelShape EAST_ON = Shapes.or(EAST_OFF, Shapes.or(Block.box(13, 4, 3, 14, 5, 4), Block.box(13, 4, 12, 14, 5, 13)));
	private static final VoxelShape SOUTH_OFF = Shapes.or(Block.box(4, 0, 9, 12, 4, 11), Shapes.or(Block.box(4, 4, 10, 12, 5, 11), Shapes.or(Block.box(5, 4, 11, 6, 5, 12), Shapes.or(Block.box(10, 4, 11, 11, 5, 12), Shapes.or(Block.box(4, 4, 12, 5, 5, 13), Block.box(11, 4, 12, 12, 5, 13))))));
	private static final VoxelShape SOUTH_ON = Shapes.or(SOUTH_OFF, Shapes.or(Block.box(3, 4, 13, 4, 5, 14), Block.box(12, 4, 13, 13, 5, 14)));
	private static final VoxelShape WEST_OFF = Shapes.or(Block.box(5, 0, 4, 7, 4, 12), Shapes.or(Block.box(5, 4, 4, 6, 5, 12), Shapes.or(Block.box(4, 4, 5, 5, 5, 6), Shapes.or(Block.box(4, 4, 10, 5, 5, 11), Shapes.or(Block.box(3, 4, 4, 4, 5, 5), Block.box(3, 4, 11, 4, 5, 12))))));
	private static final VoxelShape WEST_ON = Shapes.or(WEST_OFF, Shapes.or(Block.box(2, 4, 3, 3, 5, 4), Block.box(2, 4, 12, 3, 5, 13)));

	public ClaymoreBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DEACTIVATED, false));
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
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!player.isCreative() && !level.isClientSide && !level.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			level.destroyBlock(pos, false);

			if (!EntityUtils.doesPlayerOwn(player, level, pos))
				explode(level, pos);
		}

		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		if (!level.isClientSide && level.getBlockState(pos).hasProperty(ClaymoreBlock.DEACTIVATED) && !level.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			if (pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(level, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level level, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection()).setValue(DEACTIVATED, false);
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
			level.explode(null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.5F : 3.5F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
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
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (!newState.hasBlockEntity())
				level.removeBlockEntity(pos);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(FACING)) {
			case NORTH -> state.getValue(DEACTIVATED) ? NORTH_OFF : NORTH_ON;
			case EAST -> state.getValue(DEACTIVATED) ? EAST_OFF : EAST_ON;
			case SOUTH -> state.getValue(DEACTIVATED) ? SOUTH_OFF : SOUTH_ON;
			case WEST -> state.getValue(DEACTIVATED) ? WEST_OFF : WEST_ON;
			default -> Shapes.block();
		};
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, DEACTIVATED);
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
