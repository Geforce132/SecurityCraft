package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InventoryScannerFieldBlock extends OwnableBlock implements IOverlayDisplay, SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = InventoryScannerBlock.HORIZONTAL;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_EW = Block.box(0, 0, 6, 16, 16, 10);
	private static final VoxelShape SHAPE_NS = Block.box(6, 0, 0, 10, 16, 16);
	private static final VoxelShape HORIZONTAL_SHAPE = Block.box(0, 6, 0, 16, 10, 16);

	public InventoryScannerFieldBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
		if (!(collisionContext instanceof EntityCollisionContext ctx) || ctx.getEntity() == null)
			return Shapes.empty();

		Entity entity = ctx.getEntity();
		Level level = entity.getCommandSenderWorld();
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos);

		if (connectedScanner != null && connectedScanner.doesFieldSolidify()) {
			if (entity instanceof Player player && !connectedScanner.isConsideredInvisible(player)) {
				if (connectedScanner.isAllowed(entity))
					return Shapes.empty();

				List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

				if (!prohibitedItems.isEmpty() && checkInventory(player, connectedScanner, prohibitedItems, false))
					return getShape(state, level, pos, ctx);
			}
			else if (entity instanceof ItemEntity item) {
				List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

				if (!prohibitedItems.isEmpty() && checkItemEntity(item, connectedScanner, prohibitedItems, false))
					return getShape(state, level, pos, ctx);
			}
		}

		return Shapes.empty();
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
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!getShape(state, level, pos, CollisionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
			return;

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos);

		if (connectedScanner == null || connectedScanner.doesFieldSolidify())
			return;

		if (entity instanceof Player player && !connectedScanner.isConsideredInvisible(player)) {
			if (connectedScanner.isAllowed(entity))
				return;

			List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

			if (!prohibitedItems.isEmpty())
				checkInventory(player, connectedScanner, prohibitedItems, true);
		}
		else if (entity instanceof ItemEntity item) {
			List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

			if (!prohibitedItems.isEmpty())
				checkItemEntity(item, connectedScanner, prohibitedItems, true);
		}
	}

	public static boolean checkInventory(Player player, InventoryScannerBlockEntity be, List<ItemStack> prohibitedItems, boolean allowInteraction) {
		boolean hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && be.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && be.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction) || (be.isOwnedBy(player) && be.ignoresOwner()))
			return false;

		return loopInventory(player.getInventory().items, prohibitedItems, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.getInventory().armor, prohibitedItems, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.getInventory().offhand, prohibitedItems, be, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean loopInventory(NonNullList<ItemStack> inventory, List<ItemStack> prohibitedItems, InventoryScannerBlockEntity be, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		boolean itemFound = false;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stackToCheck = inventory.get(i);

			if (!stackToCheck.isEmpty()) {
				for (ItemStack prohibitedItem : prohibitedItems) {
					if (areItemsEqual(stackToCheck, prohibitedItem, hasSmartModule)) {
						if (hasStorageModule) {
							ItemStack remainder = be.addItemToStorage(inventory.get(i));

							if (!remainder.isEmpty())
								Block.popResource(be.getLevel(), be.getBlockPos(), remainder.copy());

							inventory.set(i, ItemStack.EMPTY);
						}

						if (hasRedstoneModule)
							updateInventoryScannerPower(be);

						itemFound = true;
						break;
					}
					else if (checkForContainer(stackToCheck, prohibitedItem, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || checkForBundle(stackToCheck, prohibitedItem, be, hasSmartModule, hasStorageModule, hasRedstoneModule)) {
						itemFound = true;
						break;
					}
				}
			}
		}

		return itemFound;
	}

	public static boolean checkItemEntity(ItemEntity entity, InventoryScannerBlockEntity be, List<ItemStack> prohibitedItems, boolean allowInteraction) {
		boolean hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && be.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && be.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction))
			return false;

		for (ItemStack prohibitedItem : prohibitedItems) {
			if (areItemsEqual(entity.getItem(), prohibitedItem, hasSmartModule)) {
				if (hasStorageModule) {
					ItemStack remainder = be.addItemToStorage(entity.getItem());

					if (!remainder.isEmpty())
						Block.popResource(be.getLevel(), be.getBlockPos(), remainder.copy());

					entity.discard();
				}

				if (hasRedstoneModule)
					updateInventoryScannerPower(be);

				return true;
			}
			else if (checkForContainer(entity.getItem(), prohibitedItem, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || checkForBundle(entity.getItem(), prohibitedItem, be, hasSmartModule, hasStorageModule, hasRedstoneModule))
				return true;
		}

		return false;
	}

	private static boolean checkForContainer(ItemStack item, ItemStack stackToCheck, InventoryScannerBlockEntity be, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		if (item != null && item.has(DataComponents.CONTAINER)) {
			NonNullList<ItemStack> list = NonNullList.withSize(27, ItemStack.EMPTY);

			item.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(list);

			for (int i = 0; i < list.size(); i++) {
				ItemStack itemInChest = list.get(i);

				if (areItemsEqual(itemInChest, stackToCheck, hasSmartModule)) {
					if (hasStorageModule) {
						ItemStack remainder = be.addItemToStorage(itemInChest);

						if (!remainder.isEmpty())
							Block.popResource(be.getLevel(), be.getBlockPos(), remainder.copy());

						list.set(i, ItemStack.EMPTY);
						item.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
					}

					if (hasRedstoneModule)
						updateInventoryScannerPower(be);

					return true;
				}
			}
		}

		return false;
	}

	private static boolean checkForBundle(ItemStack item, ItemStack stackToCheck, InventoryScannerBlockEntity be, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		if (item != null && item.has(DataComponents.BUNDLE_CONTENTS)) {
			List<ItemStack> items = item.get(DataComponents.BUNDLE_CONTENTS).itemCopyStream().collect(Collectors.toList());

			for (int i = 0; i < items.size(); i++) {
				ItemStack itemInChest = items.get(i);

				if (areItemsEqual(itemInChest, stackToCheck, hasSmartModule)) {
					if (hasStorageModule) {
						ItemStack remainder = be.addItemToStorage(itemInChest);

						if (!remainder.isEmpty())
							Block.popResource(be.getLevel(), be.getBlockPos(), remainder.copy());

						items.remove(i);
						item.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
					}

					if (hasRedstoneModule)
						updateInventoryScannerPower(be);

					return true;
				}
			}
		}

		return false;
	}

	private static boolean areItemsEqual(ItemStack firstItemStack, ItemStack secondItemStack, boolean hasSmartModule) {
		return (hasSmartModule && areItemStacksEqual(firstItemStack, secondItemStack) && ItemStack.isSameItemSameComponents(firstItemStack, secondItemStack)) || (!hasSmartModule && firstItemStack.getItem() == secondItemStack.getItem());
	}

	private static void updateInventoryScannerPower(InventoryScannerBlockEntity be) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(be.getLevel(), be.getBlockPos());

		if (connectedScanner == null)
			return;

		be.togglePowerOutput();
		connectedScanner.togglePowerOutput();
	}

	/**
	 * See {@link ItemStack#matches(ItemStack, ItemStack)} but without size restriction
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.setCount(1);
		s2.setCount(1);
		return ItemStack.matches(s1, s2);
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			Direction facing = state.getValue(FACING);

			BlockUtils.removeInSequence((direction, stateToCheck) -> {
				if (stateToCheck.getBlock() != SCContent.INVENTORY_SCANNER_FIELD.get())
					return false;

				Direction stateToCheckFacing = stateToCheck.getValue(FACING);

				return stateToCheckFacing == direction || stateToCheckFacing == direction.getOpposite();
			}, level, pos, facing, facing.getOpposite());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		if (state.getValue(HORIZONTAL))
			return HORIZONTAL_SHAPE;

		Direction facing = state.getValue(FACING);

		if (facing == Direction.EAST || facing == Direction.WEST)
			return SHAPE_EW; //ew
		else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			return SHAPE_NS; //ns

		return Shapes.block();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HORIZONTAL, WATERLOGGED);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		if ((side == Direction.UP || side == Direction.DOWN) && state.getBlock() == adjacentBlockState.getBlock())
			return true;

		return super.skipRendering(state, adjacentBlockState, side);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return false;
	}
}
