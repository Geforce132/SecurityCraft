package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.ShulkerBoxBlock;
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
	public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");
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
			if (entity instanceof Player player && !EntityUtils.isInvisible(player)) {
				if (connectedScanner.isAllowed(entity))
					return Shapes.empty();

				ItemStack[] stacks = connectedScanner.getAllProhibitedStackInSlotCopy();

				if (stacks.length != 0 && checkInventory(player, connectedScanner, stacks, false))
					return getShape(state, level, pos, ctx);
			}
			else if (entity instanceof ItemEntity item) {
				for (int i = 0; i < 10; i++) {
					if (!connectedScanner.getStackInSlotCopy(i).isEmpty() && !item.getItem().isEmpty() && checkItemEntity(item, connectedScanner, connectedScanner.getStackInSlotCopy(i), false))
						return getShape(state, level, pos, ctx);
				}
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

		if (entity instanceof Player player && !EntityUtils.isInvisible(player)) {
			if (connectedScanner.isAllowed(entity))
				return;

			ItemStack[] stacks = connectedScanner.getAllProhibitedStackInSlotCopy();

			if (stacks.length == 0)
				return;

			checkInventory(player, connectedScanner, stacks, true);
		}
		else if (entity instanceof ItemEntity item) {
			for (int i = 0; i < 10; i++) {
				if (!connectedScanner.getStackInSlotCopy(i).isEmpty() && !item.getItem().isEmpty())
					checkItemEntity(item, connectedScanner, connectedScanner.getStackInSlotCopy(i), true);
			}
		}
	}

	public static boolean checkInventory(Player player, InventoryScannerBlockEntity be, ItemStack[] stacks, boolean allowInteraction) {
		boolean hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && be.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && be.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction) || (be.isOwnedBy(player) && be.ignoresOwner()))
			return false;

		return loopInventory(player.getInventory().items, stacks, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.getInventory().armor, stacks, be, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.getInventory().offhand, stacks, be, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean loopInventory(NonNullList<ItemStack> inventory, ItemStack[] stacks, InventoryScannerBlockEntity be, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		boolean itemFound = false;
		
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStackChecking = inventory.get(i);

			if (!itemStackChecking.isEmpty()) {
				for(ItemStack stack : stacks)
					if (areItemsEqual(itemStackChecking, stack, hasSmartModule)) {
						if (hasStorageModule) {
							be.addItemToStorage(inventory.get(i)); // Warning, if inventory is full, the item will be voided
							inventory.set(i, ItemStack.EMPTY);
						}

						if (hasRedstoneModule)
							updateInventoryScannerPower(be);

						itemFound = true;
					} else {
						if(checkForShulkerBox(itemStackChecking, stack, be, hasSmartModule, hasStorageModule, hasRedstoneModule)) {
							itemFound = true;
						}
					}
			}
		}

		return itemFound;
	}

	public static boolean checkItemEntity(ItemEntity entity, InventoryScannerBlockEntity be, ItemStack stack, boolean allowInteraction) {
		boolean hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && be.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && be.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction))
			return false;

		if (areItemsEqual(entity.getItem(), stack, hasSmartModule)) {
			if (hasStorageModule) {
				be.addItemToStorage(entity.getItem());
				entity.discard();
			}

			if (hasRedstoneModule)
				updateInventoryScannerPower(be);

			return true;
		}

		return checkForShulkerBox(entity.getItem(), stack, be, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean checkForShulkerBox(ItemStack item, ItemStack stackToCheck, InventoryScannerBlockEntity be, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		if (item != null && !item.isEmpty() && item.getTag() != null && Block.byItem(item.getItem()) instanceof ShulkerBoxBlock) {
			ListTag list = item.getTag().getCompound("BlockEntityTag").getList("Items", Tag.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++) {
				ItemStack itemInChest = ItemStack.of(list.getCompound(i));

				if (areItemsEqual(itemInChest, stackToCheck, hasSmartModule)) {
					if (hasStorageModule) {
						be.addItemToStorage(itemInChest);
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

		updateInvScanner(be);
		updateInvScanner(connectedScanner);
	}

	private static void updateInvScanner(InventoryScannerBlockEntity be) {
		be.activate();
		BlockUtils.updateAndNotify(be.getLevel(), be.getBlockPos(), be.getBlockState().getBlock(), 1, true);
		BlockUtils.updateIndirectNeighbors(be.getLevel(), be.getBlockPos(), SCContent.INVENTORY_SCANNER.get());
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
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
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
		return null;
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return false;
	}
}
