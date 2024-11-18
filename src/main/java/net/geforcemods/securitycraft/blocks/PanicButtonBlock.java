package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.PanicButtonBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

public class PanicButtonBlock extends ButtonBlock implements EntityBlock, SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape FLOOR_NS_POWERED = Block.box(3, 0, 5, 13, 1, 11);
	private static final VoxelShape FLOOR_NS_UNPOWERED = Block.box(3, 0, 5, 13, 2, 11);
	private static final VoxelShape FLOOR_EW_POWERED = Block.box(5, 0, 3, 11, 1, 13);
	private static final VoxelShape FLOOR_EW_UNPOWERED = Block.box(5, 0, 3, 11, 2, 13);
	private static final VoxelShape WALL_N_POWERED = Block.box(3, 5, 15, 13, 11, 16);
	private static final VoxelShape WALL_N_UNPOWERED = Block.box(3, 5, 14, 13, 11, 16);
	private static final VoxelShape WALL_S_POWERED = Block.box(3, 5, 0, 13, 11, 1);
	private static final VoxelShape WALL_S_UNPOWERED = Block.box(3, 5, 0, 13, 11, 2);
	private static final VoxelShape WALL_E_POWERED = Block.box(0, 5, 3, 1, 11, 13);
	private static final VoxelShape WALL_E_UNPOWERED = Block.box(0, 5, 3, 2, 11, 13);
	private static final VoxelShape WALL_W_POWERED = Block.box(15, 5, 3, 16, 11, 13);
	private static final VoxelShape WALL_W_UNPOWERED = Block.box(14, 5, 3, 16, 11, 13);
	private static final VoxelShape CEILING_NS_POWERED = Block.box(3, 15, 5, 13, 16, 11);
	private static final VoxelShape CEILING_NS_UNPOWERED = Block.box(3, 14, 5, 13, 16, 11);
	private static final VoxelShape CEILING_EW_POWERED = Block.box(5, 15, 3, 11, 16, 13);
	private static final VoxelShape CEILING_EW_UNPOWERED = Block.box(5, 14, 3, 11, 16, 13);

	public PanicButtonBlock(boolean isWooden, BlockBehaviour.Properties properties) {
		super(isWooden, properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState superState = super.getStateForPlacement(ctx);

		if (superState != null)
			return superState.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);

		return null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof PanicButtonBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player))) {
			boolean newPowered = !state.getValue(POWERED);

			level.setBlockAndUpdate(pos, state.setValue(POWERED, newPowered));
			playSound(player, level, pos, newPowered);
			notifyNeighbors(level, pos, switch (state.getValue(FACE)) {
				case WALL -> state.getValue(FACING);
				case CEILING -> Direction.DOWN;
				case FLOOR -> Direction.UP;
			});
			return InteractionResult.SUCCESS;
		}
		else
			return InteractionResult.FAIL;
	}

	private void notifyNeighbors(Level level, BlockPos pos, Direction facing) {
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(facing.getOpposite()), this);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		BlockEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
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
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(FACE)) {
			case FLOOR -> switch (state.getValue(FACING)) {
				case NORTH, SOUTH -> state.getValue(POWERED) ? FLOOR_NS_POWERED : FLOOR_NS_UNPOWERED;
				case EAST, WEST -> state.getValue(POWERED) ? FLOOR_EW_POWERED : FLOOR_EW_UNPOWERED;
				default -> Shapes.block();
			};
			case WALL -> switch (state.getValue(FACING)) {
				case NORTH -> state.getValue(POWERED) ? WALL_N_POWERED : WALL_N_UNPOWERED;
				case SOUTH -> state.getValue(POWERED) ? WALL_S_POWERED : WALL_S_UNPOWERED;
				case EAST -> state.getValue(POWERED) ? WALL_E_POWERED : WALL_E_UNPOWERED;
				case WEST -> state.getValue(POWERED) ? WALL_W_POWERED : WALL_W_UNPOWERED;
				default -> Shapes.block();
			};
			case CEILING -> switch (state.getValue(FACING)) {
				case NORTH, SOUTH -> state.getValue(POWERED) ? CEILING_NS_POWERED : CEILING_NS_UNPOWERED;
				case EAST, WEST -> state.getValue(POWERED) ? CEILING_EW_POWERED : CEILING_EW_UNPOWERED;
				default -> Shapes.block();
			};
			default -> Shapes.block();
		};
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return Shapes.empty();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PanicButtonBlockEntity(pos, state);
	}

	@Override
	protected SoundEvent getSound(boolean turningOn) {
		return turningOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}
}
