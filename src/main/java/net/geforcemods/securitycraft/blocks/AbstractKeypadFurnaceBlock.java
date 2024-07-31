package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;

public abstract class AbstractKeypadFurnaceBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final VoxelShape NORTH_OPEN = Stream.of(Block.box(11, 1, 1, 12, 2, 2), Block.box(0, 0, 2, 16, 16, 16), Block.box(4, 1, 0, 12, 2, 1), Block.box(4, 1, 1, 5, 2, 2)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape NORTH_CLOSED = Stream.of(Block.box(4, 14, 1, 5, 15, 2), Block.box(11, 14, 1, 12, 15, 2), Block.box(0, 0, 2, 16, 16, 16), Block.box(4, 14, 0, 12, 15, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape EAST_OPEN = Stream.of(Block.box(14, 1, 11, 15, 2, 12), Block.box(0, 0, 0, 14, 16, 16), Block.box(15, 1, 4, 16, 2, 12), Block.box(14, 1, 4, 15, 2, 5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape EAST_CLOSED = Stream.of(Block.box(14, 14, 4, 15, 15, 5), Block.box(14, 14, 11, 15, 15, 12), Block.box(0, 0, 0, 14, 16, 16), Block.box(15, 14, 4, 16, 15, 12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape SOUTH_OPEN = Stream.of(Block.box(4, 1, 14, 5, 2, 15), Block.box(0, 0, 0, 16, 16, 14), Block.box(4, 1, 15, 12, 2, 16), Block.box(11, 1, 14, 12, 2, 15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape SOUTH_CLOSED = Stream.of(Block.box(11, 14, 14, 12, 15, 15), Block.box(4, 14, 14, 5, 15, 15), Block.box(0, 0, 0, 16, 16, 14), Block.box(4, 14, 15, 12, 15, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape WEST_OPEN = Stream.of(Block.box(1, 1, 4, 2, 2, 5), Block.box(2, 0, 0, 16, 16, 16), Block.box(0, 1, 4, 1, 2, 12), Block.box(1, 1, 11, 2, 2, 12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape WEST_CLOSED = Stream.of(Block.box(1, 14, 11, 2, 15, 12), Block.box(1, 14, 4, 2, 15, 5), Block.box(2, 0, 0, 16, 16, 16), Block.box(0, 14, 4, 1, 15, 12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape NORTH_COLLISION = Block.box(0, 0, 2, 16, 16, 16);
	private static final VoxelShape EAST_COLLISION = Block.box(0, 0, 0, 14, 16, 16);
	private static final VoxelShape SOUTH_COLLISION = Block.box(0, 0, 0, 16, 16, 14);
	private static final VoxelShape WEST_COLLISION = Block.box(2, 0, 0, 16, 16, 16);

	protected AbstractKeypadFurnaceBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof BaseContainerBlockEntity baseContainer)
			baseContainer.setCustomName(stack.getHoverName());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else {
			return switch (disguisedState.getValue(FACING)) {
				case NORTH -> disguisedState.getValue(OPEN) ? NORTH_OPEN : NORTH_CLOSED;
				case EAST -> disguisedState.getValue(OPEN) ? EAST_OPEN : EAST_CLOSED;
				case SOUTH -> disguisedState.getValue(OPEN) ? SOUTH_OPEN : SOUTH_CLOSED;
				case WEST -> disguisedState.getValue(OPEN) ? WEST_OPEN : WEST_CLOSED;
				default -> Shapes.block();
			};
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else {
			return switch (disguisedState.getValue(FACING)) {
				case NORTH -> NORTH_COLLISION;
				case EAST -> EAST_COLLISION;
				case SOUTH -> SOUTH_COLLISION;
				case WEST -> WEST_COLLISION;
				default -> Shapes.block();
			};
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof Container container) {
				Containers.dropContents(level, pos, container);
				level.updateNeighbourForOutputSignal(pos, this);
			}

			if (level.getBlockEntity(pos) instanceof IPasscodeProtected be)
				SaltData.removeSalt(be.getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide) {
			AbstractKeypadFurnaceBlockEntity be = (AbstractKeypadFurnaceBlockEntity) level.getBlockEntity(pos);

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

					activate(be, level, pos, player);
				}
				else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get()))
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(AbstractKeypadFurnaceBlockEntity be, Level level, BlockPos pos, Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			level.gameEvent(player, GameEvent.CONTAINER_OPEN, pos);
			serverPlayer.openMenu(be, pos);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof AbstractKeypadFurnaceBlockEntity be)
			be.recheckOpen();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(OPEN, false).setValue(LIT, false);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, LIT, WATERLOGGED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class Convertible implements IPasscodeConvertible {
		private final Block unprotectedBlock, protectedBlock;

		public Convertible(Block unprotectedBlock, Block protectedBlock) {
			this.unprotectedBlock = unprotectedBlock;
			this.protectedBlock = protectedBlock;
		}

		@Override
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(unprotectedBlock);
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(protectedBlock);
		}

		@Override
		public boolean protect(Player player, Level level, BlockPos pos) {
			return convert(player, level, pos, protectedBlock, true);
		}

		@Override
		public boolean unprotect(Player player, Level level, BlockPos pos) {
			return convert(player, level, pos, unprotectedBlock, false);
		}

		public boolean convert(Player player, Level level, BlockPos pos, Block convertedBlock, boolean protect) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			boolean lit = state.getValue(LIT);
			AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(pos);
			CompoundTag tag;
			BlockState convertedState = convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(LIT, lit);

			if (protect)
				convertedState = convertedState.setValue(OPEN, false);
			else
				((IModuleInventory) furnace).dropAllModules();

			tag = furnace.saveWithFullMetadata();
			furnace.clearContent();
			level.setBlockAndUpdate(pos, convertedState);
			furnace = (AbstractFurnaceBlockEntity) level.getBlockEntity(pos);
			furnace.load(tag);

			if (protect && player != null)
				((IOwnable) furnace).setOwner(player.getUUID().toString(), player.getName().getString());

			return true;
		}
	}
}
