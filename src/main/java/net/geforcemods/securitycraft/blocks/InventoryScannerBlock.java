package net.geforcemods.securitycraft.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class InventoryScannerBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");

	public InventoryScannerBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false).setValue(WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (isFacingAnotherScanner(level, pos) && player instanceof ServerPlayer serverPlayer) {
			if (!level.isClientSide && level.getBlockEntity(pos) instanceof InventoryScannerBlockEntity be) {
				if (be.isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else
					NetworkHooks.openScreen(serverPlayer, be, pos);
			}

			return InteractionResult.SUCCESS;
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.INVENTORY_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:invScan.notConnected"), ChatFormatting.RED);
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		if (level.isClientSide)
			return;

		checkAndPlaceAppropriately(level, pos, false);
	}

	public static void checkAndPlaceAppropriately(Level level, BlockPos pos, boolean force) {
		InventoryScannerBlockEntity thisBe = (InventoryScannerBlockEntity) level.getBlockEntity(pos);
		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(level, pos);

		if (connectedScanner == null || !connectedScanner.getOwner().owns(thisBe))
			return;

		if (!force) {
			if (connectedScanner.isDisabled()) {
				thisBe.setDisabled(true);
				return;
			}
		}
		else {
			thisBe.setDisabled(false);
			connectedScanner.setDisabled(false);
		}

		boolean horizontal = false;

		if (connectedScanner.getBlockState().getValue(HORIZONTAL))
			horizontal = true;

		Direction facing = level.getBlockState(pos).getValue(FACING);
		int loopBoundary = switch (facing) {
			case WEST, EAST -> Math.abs(pos.getX() - connectedScanner.getBlockPos().getX());
			case NORTH, SOUTH -> Math.abs(pos.getZ() - connectedScanner.getBlockPos().getZ());
			default -> 0;
		};

		thisBe.setHorizontal(horizontal);

		for (int i = 1; i < loopBoundary; i++) {
			if (level.getBlockState(pos.relative(facing, i)).getBlock() == SCContent.INVENTORY_SCANNER_FIELD.get())
				return;
		}

		Option<?>[] customOptions = thisBe.customOptions();

		for (int i = 1; i < loopBoundary; i++) {
			BlockPos offsetPos = pos.relative(facing, i);

			level.setBlockAndUpdate(offsetPos, SCContent.INVENTORY_SCANNER_FIELD.get().defaultBlockState().setValue(FACING, facing).setValue(HORIZONTAL, horizontal).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER));

			if (level.getBlockEntity(offsetPos) instanceof IOwnable ownable)
				ownable.setOwner(thisBe.getOwner().getUUID(), thisBe.getOwner().getName());
		}

		for (ModuleType type : connectedScanner.getInsertedModules()) {
			thisBe.insertModule(connectedScanner.getModule(type), false);
		}

		((BooleanOption) customOptions[0]).setValue(connectedScanner.isHorizontal());
		((BooleanOption) customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
		((BooleanOption) customOptions[2]).setValue(false);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (level.isClientSide || state.getBlock() == newState.getBlock())
			return;

		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(level, pos, state, null);

		BlockUtils.removeInSequence((direction, stateToCheck) -> {
			if (stateToCheck.getBlock() != SCContent.INVENTORY_SCANNER_FIELD.get())
				return false;

			Direction stateToCheckFacing = stateToCheck.getValue(FACING);

			return stateToCheckFacing == direction || stateToCheckFacing == direction.getOpposite();
		}, level, pos, state.getValue(FACING));

		if (level.getBlockEntity(pos) instanceof InventoryScannerBlockEntity be) {
			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < be.getContainerSize(); i++) {
				Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getContents().get(i));
			}

			Containers.dropContents(level, pos, be.getLensContainer());

			if (be.isProvidingPower()) {
				level.updateNeighborsAt(pos, this);
				BlockUtils.updateIndirectNeighbors(level, pos, this);
			}
		}

		if (connectedScanner != null) {
			for (int i = 0; i < connectedScanner.getContents().size(); i++) {
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}

			connectedScanner.getLensContainer().clearContent();
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	private boolean isFacingAnotherScanner(Level level, BlockPos pos) {
		return getConnectedInventoryScanner(level, pos) != null;
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(Level level, BlockPos pos) {
		return getConnectedInventoryScanner(level, pos, level.getBlockState(pos), null);
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(Level level, BlockPos pos, BlockState stateAtPos, Consumer<OwnableBlockEntity> fieldModifier) {
		if (!stateAtPos.is(SCContent.INVENTORY_SCANNER.get()) && !stateAtPos.is(SCContent.INVENTORY_SCANNER_FIELD.get()))
			return null;

		Direction facing = stateAtPos.getValue(FACING);
		List<BlockPos> fields = new ArrayList<>();

		for (int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState state = level.getBlockState(offsetPos);
			Block block = state.getBlock();
			boolean isField = block == SCContent.INVENTORY_SCANNER_FIELD.get();

			if (!isField && !state.isAir() && !state.canBeReplaced() && block != SCContent.INVENTORY_SCANNER.get())
				return null;

			if (isField)
				fields.add(offsetPos);

			if (block == SCContent.INVENTORY_SCANNER.get() && state.getValue(FACING) == facing.getOpposite()) {
				if (fieldModifier != null)
					fields.stream().map(level::getBlockEntity).forEach(be -> fieldModifier.accept((OwnableBlockEntity) be));

				return (InventoryScannerBlockEntity) level.getBlockEntity(offsetPos);
			}
		}

		return null;
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		checkAndPlaceAppropriately((Level) level, pos, false);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		if (!(level.getBlockEntity(pos) instanceof InventoryScannerBlockEntity be))
			return 0;

		return be.isProvidingPower() ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return getSignal(state, level, pos, side);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HORIZONTAL, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new InventoryScannerBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class DoorActivator implements IDoorActivator {
		private List<Block> blocks = Arrays.asList(SCContent.INVENTORY_SCANNER.get());

		@Override
		public boolean isPowering(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction direction, int distance) {
			return ((InventoryScannerBlockEntity) be).isProvidingPower();
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
