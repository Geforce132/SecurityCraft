package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedObserverBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedObserverBlock extends DisguisableBlock implements IReinforcedBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public ReinforcedObserverBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED))
			level.setBlock(pos, state.setValue(POWERED, false), 2);
		else {
			if (level.getBlockEntity(pos) instanceof IOwnable ownable && ownable.getOwner().isValidated()) {
				level.setBlock(pos, state.setValue(POWERED, true), 2);
				level.scheduleTick(pos, this, 2);
			}
			else
				return;
		}

		updateNeighborsInFront(level, pos, state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (!level.isClientSide() && state.getValue(FACING) == facing && !state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(currentPos, this))
			level.scheduleTick(currentPos, this, 2);

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	protected void updateNeighborsInFront(Level level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		BlockPos relativePos = pos.relative(direction.getOpposite());

		level.neighborChanged(relativePos, this, pos);
		level.updateNeighborsAtExceptFromFacing(relativePos, this, direction);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getSignal(level, pos, side);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) && state.getValue(FACING) == side ? 15 : 0;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!level.isClientSide() && !state.is(oldState.getBlock()) && state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
			BlockState newState = state.setValue(POWERED, false);

			level.setBlock(pos, newState, 18);
			updateNeighborsInFront(level, pos, newState);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (!level.isClientSide && state.getValue(POWERED) && level.getBlockTicks().hasScheduledTick(pos, this))
				updateNeighborsInFront(level, pos, state.setValue(POWERED, false));

			level.removeBlockEntity(pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite().getOpposite());
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return side == state.getValue(DirectionalBlock.FACING);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.OBSERVER;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedObserverBlockEntity(pos, state);
	}
}
