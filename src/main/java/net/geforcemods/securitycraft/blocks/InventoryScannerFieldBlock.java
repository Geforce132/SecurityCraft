package net.geforcemods.securitycraft.blocks;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryScannerFieldBlock extends OwnableBlock implements IOverlayDisplay, IWaterLoggable {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_EW = Block.box(0, 0, 6, 16, 16, 10);
	private static final VoxelShape SHAPE_NS = Block.box(6, 0, 0, 10, 16, 16);
	private static final VoxelShape HORIZONTAL_SHAPE = Block.box(0, 6, 0, 16, 10, 16);

	public InventoryScannerFieldBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader blockGetter, BlockPos pos, ISelectionContext ctx) {
		if (ctx.getEntity() == null)
			return VoxelShapes.empty();

		Entity entity = ctx.getEntity();
		World level = entity.getCommandSenderWorld();
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos);

		if (connectedScanner != null && connectedScanner.doesFieldSolidify()) {
			if (entity instanceof PlayerEntity && !connectedScanner.isConsideredInvisible((PlayerEntity) entity)) {
				if (connectedScanner.isAllowed(entity))
					return VoxelShapes.empty();

				List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

				if (!prohibitedItems.isEmpty() && checkInventory((PlayerEntity) entity, connectedScanner, prohibitedItems, false))
					return getShape(state, level, pos, ctx);
			}
			else if (entity instanceof ItemEntity) {
				List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

				if (!prohibitedItems.isEmpty() && checkItemEntity((ItemEntity) entity, connectedScanner, prohibitedItems, false))
					return getShape(state, level, pos, ctx);
			}
		}

		return VoxelShapes.empty();
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (!getShape(state, level, pos, ISelectionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
			return;

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos);

		if (connectedScanner == null || connectedScanner.doesFieldSolidify())
			return;

		if (entity instanceof PlayerEntity && !connectedScanner.isConsideredInvisible((PlayerEntity) entity)) {
			if (connectedScanner.isAllowed(entity))
				return;

			List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

			if (!prohibitedItems.isEmpty())
				checkInventory((PlayerEntity) entity, connectedScanner, prohibitedItems, true);
		}
		else if (entity instanceof ItemEntity) {
			List<ItemStack> prohibitedItems = connectedScanner.getAllProhibitedItems();

			if (!prohibitedItems.isEmpty())
				checkItemEntity((ItemEntity) entity, connectedScanner, prohibitedItems, true);
		}
	}

	public static boolean checkInventory(PlayerEntity player, InventoryScannerBlockEntity be, List<ItemStack> prohibitedItems, boolean allowInteraction) {
		boolean hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && be.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && be.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction) || (be.isOwnedBy(player) && be.ignoresOwner()))
			return false;

		return loopInventory(player.inventory.items, prohibitedItems, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.inventory.armor, prohibitedItems, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.inventory.offhand, prohibitedItems, be, hasSmartModule, hasStorageModule, hasRedstoneModule);
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
					else if (checkForShulkerBox(stackToCheck, prohibitedItem, be, hasSmartModule, hasStorageModule, hasRedstoneModule)) {
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

					entity.remove();
				}

				if (hasRedstoneModule)
					updateInventoryScannerPower(be);

				return true;
			}
			else if (checkForShulkerBox(entity.getItem(), prohibitedItem, be, hasSmartModule, hasStorageModule, hasRedstoneModule))
				return true;
		}

		return false;
	}

	private static boolean checkForShulkerBox(ItemStack item, ItemStack stackToCheck, InventoryScannerBlockEntity be, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		if (item != null && !item.isEmpty() && item.getTag() != null && Block.byItem(item.getItem()) instanceof ShulkerBoxBlock) {
			ListNBT list = item.getTag().getCompound("BlockEntityTag").getList("Items", NBT.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++) {
				ItemStack itemInChest = ItemStack.of(list.getCompound(i));

				if (areItemsEqual(itemInChest, stackToCheck, hasSmartModule)) {
					if (hasStorageModule) {
						ItemStack remainder = be.addItemToStorage(itemInChest);

						if (!remainder.isEmpty())
							Block.popResource(be.getLevel(), be.getBlockPos(), remainder.copy());

						list.remove(i);
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
		return (hasSmartModule && areItemStacksEqual(firstItemStack, secondItemStack) && ItemStack.tagMatches(firstItemStack, secondItemStack)) || (!hasSmartModule && firstItemStack.getItem() == secondItemStack.getItem());
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
	public void destroy(IWorld level, BlockPos pos, BlockState state) {
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
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		if (state.getValue(HORIZONTAL))
			return HORIZONTAL_SHAPE;

		Direction facing = state.getValue(FACING);

		if (facing == Direction.EAST || facing == Direction.WEST)
			return SHAPE_EW; //ew
		else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			return SHAPE_NS; //ns

		return VoxelShapes.block();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HORIZONTAL, WATERLOGGED);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return ((side == Direction.UP || side == Direction.DOWN) && state.getBlock() == adjacentBlockState.getBlock()) || super.skipRendering(state, adjacentBlockState, side);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public ItemStack getDisplayStack(World level, BlockState state, BlockPos pos) {
		return null;
	}

	@Override
	public boolean shouldShowSCInfo(World level, BlockState state, BlockPos pos) {
		return false;
	}
}
