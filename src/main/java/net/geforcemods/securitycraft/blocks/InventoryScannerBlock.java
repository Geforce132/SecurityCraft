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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

	public InventoryScannerBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (isFacingAnotherScanner(world, pos) && player instanceof ServerPlayerEntity) {
			TileEntity tile = world.getBlockEntity(pos);

			if (!world.isClientSide && tile instanceof InventoryScannerBlockEntity) {
				InventoryScannerBlockEntity te = (InventoryScannerBlockEntity) tile;

				if (te.isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else
					NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
			}

			return ActionResultType.SUCCESS;
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.INVENTORY_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);
			return ActionResultType.SUCCESS;
		}
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(world, pos, state, entity, stack);

		if (world.isClientSide)
			return;

		checkAndPlaceAppropriately(world, pos, false);
	}

	public static void checkAndPlaceAppropriately(World world, BlockPos pos, boolean force) {
		if (world.isClientSide)
			return;

		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(world, pos);
		InventoryScannerBlockEntity thisTe = (InventoryScannerBlockEntity) world.getBlockEntity(pos);

		if (connectedScanner == null || !connectedScanner.getOwner().owns(thisTe))
			return;

		if (!force) {
			if (connectedScanner.isDisabled()) {
				thisTe.setDisabled(true);
				return;
			}
		}
		else {
			thisTe.setDisabled(false);
			connectedScanner.setDisabled(false);
		}

		boolean horizontal = false;

		if (connectedScanner.getBlockState().getValue(HORIZONTAL))
			horizontal = true;

		thisTe.setHorizontal(horizontal);

		Direction facing = world.getBlockState(pos).getValue(FACING);
		int loopBoundary = facing == Direction.WEST || facing == Direction.EAST ? Math.abs(pos.getX() - connectedScanner.getBlockPos().getX()) : (facing == Direction.NORTH || facing == Direction.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getBlockPos().getZ()) : 0);

		for (int i = 1; i < loopBoundary; i++) {
			if (world.getBlockState(pos.relative(facing, i)).getBlock() == SCContent.INVENTORY_SCANNER_FIELD.get())
				return;
		}

		Option<?>[] customOptions = thisTe.customOptions();

		for (int i = 1; i < loopBoundary; i++) {
			BlockPos offsetPos = pos.relative(facing, i);

			world.setBlockAndUpdate(offsetPos, SCContent.INVENTORY_SCANNER_FIELD.get().defaultBlockState().setValue(FACING, facing).setValue(HORIZONTAL, horizontal));

			TileEntity te = world.getBlockEntity(offsetPos);

			if (te instanceof IOwnable)
				((IOwnable) te).setOwner(thisTe.getOwner().getUUID(), thisTe.getOwner().getName());
		}

		for (ModuleType type : connectedScanner.getInsertedModules()) {
			thisTe.insertModule(connectedScanner.getModule(type), false);
		}

		((BooleanOption) customOptions[0]).setValue(connectedScanner.isHorizontal());
		((BooleanOption) customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
		((BooleanOption) customOptions[2]).setValue(false);
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (world.isClientSide || state.getBlock() == newState.getBlock())
			return;

		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(world, pos, state, null);
		TileEntity tile = world.getBlockEntity(pos);

		BlockUtils.removeInSequence(SCContent.INVENTORY_SCANNER_FIELD.get(), world, pos, state.getValue(FACING));

		if (tile instanceof InventoryScannerBlockEntity) {
			InventoryScannerBlockEntity te = (InventoryScannerBlockEntity) tile;

			//first 10 slots (0-9) are the prohibited slots
			for (int i = 10; i < te.getContainerSize(); i++) {
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getContents().get(i));
			}
		}

		if (connectedScanner != null) {
			for (int i = 0; i < connectedScanner.getContents().size(); i++) {
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos) {
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(World world, BlockPos pos) {
		return getConnectedInventoryScanner(world, pos, world.getBlockState(pos), null);
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(World world, BlockPos pos, BlockState stateAtPos, Consumer<OwnableBlockEntity> fieldModifier) {
		Direction facing = stateAtPos.getValue(FACING);
		List<BlockPos> fields = new ArrayList<>();

		for (int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();
			boolean isField = block == SCContent.INVENTORY_SCANNER_FIELD.get();

			if (!isField && !state.isAir(world, offsetPos) && !state.getMaterial().isReplaceable() && block != SCContent.INVENTORY_SCANNER.get())
				return null;

			if (isField)
				fields.add(offsetPos);

			if (block == SCContent.INVENTORY_SCANNER.get() && state.getValue(FACING) == facing.getOpposite()) {
				if (fieldModifier != null)
					fields.stream().map(world::getBlockEntity).forEach(be -> fieldModifier.accept((OwnableBlockEntity) be));

				return (InventoryScannerBlockEntity) world.getBlockEntity(offsetPos);
			}
		}

		return null;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		checkAndPlaceAppropriately((World) world, pos, false);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity te = blockAccess.getBlockEntity(pos);

		if (!(te instanceof InventoryScannerBlockEntity))
			return 0;

		return (((InventoryScannerBlockEntity) te).isModuleEnabled(ModuleType.REDSTONE) && ((InventoryScannerBlockEntity) te).shouldProvidePower()) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getSignal(blockState, blockAccess, pos, side);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getPlayer().getDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HORIZONTAL, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
		public boolean isPowering(World world, BlockPos pos, BlockState state, TileEntity te, Direction direction, int distance) {
			return ((InventoryScannerBlockEntity) te).isModuleEnabled(ModuleType.REDSTONE) && ((InventoryScannerBlockEntity) te).shouldProvidePower();
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
