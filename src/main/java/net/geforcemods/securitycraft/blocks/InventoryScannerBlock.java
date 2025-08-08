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
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InventoryScannerBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");

	public InventoryScannerBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (isFacingAnotherScanner(level, pos) && player instanceof ServerPlayerEntity) {
			TileEntity te = level.getBlockEntity(pos);

			if (!level.isClientSide && te instanceof InventoryScannerBlockEntity) {
				InventoryScannerBlockEntity be = (InventoryScannerBlockEntity) te;

				if (be.isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else
					NetworkHooks.openGui((ServerPlayerEntity) player, be, pos);
			}

			return ActionResultType.SUCCESS;
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.INVENTORY_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);
			return ActionResultType.SUCCESS;
		}
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		if (level.isClientSide)
			return;

		checkAndPlaceAppropriately(level, pos, false);
	}

	public static void checkAndPlaceAppropriately(World level, BlockPos pos, boolean force) {
		if (level.isClientSide)
			return;

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
		int loopBoundary = facing == Direction.WEST || facing == Direction.EAST ? Math.abs(pos.getX() - connectedScanner.getBlockPos().getX()) : (facing == Direction.NORTH || facing == Direction.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getBlockPos().getZ()) : 0);

		thisBe.setHorizontal(horizontal);

		for (int i = 1; i < loopBoundary; i++) {
			if (level.getBlockState(pos.relative(facing, i)).getBlock() == SCContent.INVENTORY_SCANNER_FIELD.get())
				return;
		}

		Option<?>[] customOptions = thisBe.customOptions();

		for (int i = 1; i < loopBoundary; i++) {
			BlockPos offsetPos = pos.relative(facing, i);

			level.setBlockAndUpdate(offsetPos, SCContent.INVENTORY_SCANNER_FIELD.get().defaultBlockState().setValue(FACING, facing).setValue(HORIZONTAL, horizontal).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER));

			TileEntity te = level.getBlockEntity(offsetPos);

			if (te instanceof IOwnable)
				((IOwnable) te).setOwner(thisBe.getOwner().getUUID(), thisBe.getOwner().getName());
		}

		for (ModuleType type : connectedScanner.getInsertedModules()) {
			thisBe.insertModule(connectedScanner.getModule(type), false);
		}

		((BooleanOption) customOptions[0]).setValue(connectedScanner.isHorizontal());
		((BooleanOption) customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
		((BooleanOption) customOptions[2]).setValue(false);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (level.isClientSide || state.getBlock() == newState.getBlock())
			return;

		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(level, pos, state, null);
		TileEntity te = level.getBlockEntity(pos);

		BlockUtils.removeInSequence((direction, stateToCheck) -> {
			if (stateToCheck.getBlock() != SCContent.INVENTORY_SCANNER_FIELD.get())
				return false;

			Direction stateToCheckFacing = stateToCheck.getValue(FACING);

			return stateToCheckFacing == direction || stateToCheckFacing == direction.getOpposite();
		}, level, pos, state.getValue(FACING));

		if (te instanceof InventoryScannerBlockEntity) {
			InventoryScannerBlockEntity be = (InventoryScannerBlockEntity) te;

			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < be.getContainerSize(); i++) {
				InventoryHelper.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getContents().get(i));
			}

			InventoryHelper.dropContents(level, pos, be.getLensContainer());

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

	private boolean isFacingAnotherScanner(World level, BlockPos pos) {
		return getConnectedInventoryScanner(level, pos) != null;
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(World level, BlockPos pos) {
		return getConnectedInventoryScanner(level, pos, level.getBlockState(pos), null);
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(World level, BlockPos pos, BlockState stateAtPos, Consumer<OwnableBlockEntity> fieldModifier) {
		if (!stateAtPos.is(SCContent.INVENTORY_SCANNER.get()))
			return null;

		Direction facing = stateAtPos.getValue(FACING);
		List<BlockPos> fields = new ArrayList<>();

		for (int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState state = level.getBlockState(offsetPos);
			Block block = state.getBlock();
			boolean isField = block == SCContent.INVENTORY_SCANNER_FIELD.get();

			if (!isField && !state.isAir(level, offsetPos) && !state.getMaterial().isReplaceable() && block != SCContent.INVENTORY_SCANNER.get())
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
	public void onNeighborChange(BlockState state, IWorldReader level, BlockPos pos, BlockPos neighbor) {
		checkAndPlaceAppropriately((World) level, pos, false);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		TileEntity te = level.getBlockEntity(pos);

		if (!(te instanceof InventoryScannerBlockEntity))
			return 0;

		return ((InventoryScannerBlockEntity) te).isProvidingPower() ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getSignal(state, blockAccess, pos, side);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HORIZONTAL, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new InventoryScannerBlockEntity();
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
		public boolean isPowering(World level, BlockPos pos, BlockState state, TileEntity be, Direction direction, int distance) {
			return ((InventoryScannerBlockEntity) be).isProvidingPower();
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
