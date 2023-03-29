package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
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

	public InventoryScannerFieldBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext ctx) {
		if (ctx.getEntity() == null)
			return VoxelShapes.empty();

		World world = ctx.getEntity().getCommandSenderWorld();
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);
		Entity entity = ctx.getEntity();

		if (connectedScanner != null && connectedScanner.doesFieldSolidify()) {
			if (entity instanceof PlayerEntity && !EntityUtils.isInvisible((PlayerEntity) entity)) {
				if (connectedScanner.isAllowed(entity))
					return VoxelShapes.empty();

				for (int i = 0; i < 10; i++) {
					if (!connectedScanner.getStackInSlotCopy(i).isEmpty()) {
						if (checkInventory((PlayerEntity) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), false))
							return getShape(state, world, pos, ctx);
					}
				}
			}
			else if (entity instanceof ItemEntity) {
				for (int i = 0; i < 10; i++) {
					if (!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((ItemEntity) entity).getItem().isEmpty()) {
						if (checkItemEntity((ItemEntity) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), false))
							return getShape(state, world, pos, ctx);
					}
				}
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
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!getShape(state, world, pos, ISelectionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
			return;

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if (connectedScanner == null || connectedScanner.doesFieldSolidify())
			return;

		if (entity instanceof PlayerEntity && !EntityUtils.isInvisible((PlayerEntity) entity)) {
			if (connectedScanner.isAllowed(entity))
				return;

			for (int i = 0; i < 10; i++) {
				if (!connectedScanner.getStackInSlotCopy(i).isEmpty())
					checkInventory((PlayerEntity) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), true);
			}
		}
		else if (entity instanceof ItemEntity) {
			for (int i = 0; i < 10; i++) {
				if (!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((ItemEntity) entity).getItem().isEmpty())
					checkItemEntity((ItemEntity) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), true);
			}
		}
	}

	public static boolean checkInventory(PlayerEntity player, InventoryScannerBlockEntity te, ItemStack stack, boolean allowInteraction) {
		boolean hasSmartModule = te.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && te.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && te.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction) || (te.isOwnedBy(player) && te.ignoresOwner()))
			return false;

		return loopInventory(player.inventory.items, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.inventory.armor, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.inventory.offhand, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean loopInventory(NonNullList<ItemStack> inventory, ItemStack stack, InventoryScannerBlockEntity te, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		for (int i = 1; i <= inventory.size(); i++) {
			ItemStack itemStackChecking = inventory.get(i - 1);

			if (!itemStackChecking.isEmpty()) {
				if (areItemsEqual(itemStackChecking, stack, hasSmartModule)) {
					if (hasStorageModule) {
						te.addItemToStorage(inventory.get(i - 1));
						inventory.set(i - 1, ItemStack.EMPTY);
					}

					if (hasRedstoneModule)
						updateInventoryScannerPower(te);

					return true;
				}

				if (checkForShulkerBox(itemStackChecking, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule))
					return true;
			}
		}

		return false;
	}

	public static boolean checkItemEntity(ItemEntity entity, InventoryScannerBlockEntity te, ItemStack stack, boolean allowInteraction) {
		boolean hasSmartModule = te.isModuleEnabled(ModuleType.SMART);
		boolean hasStorageModule = allowInteraction && te.isModuleEnabled(ModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && te.isModuleEnabled(ModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction))
			return false;

		if (areItemsEqual(entity.getItem(), stack, hasSmartModule)) {
			if (hasStorageModule) {
				te.addItemToStorage(entity.getItem());
				entity.remove();
			}

			if (hasRedstoneModule)
				updateInventoryScannerPower(te);

			return true;
		}

		return checkForShulkerBox(entity.getItem(), stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean checkForShulkerBox(ItemStack item, ItemStack stackToCheck, InventoryScannerBlockEntity te, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		if (item != null) {
			if (!item.isEmpty() && item.getTag() != null && Block.byItem(item.getItem()) instanceof ShulkerBoxBlock) {
				ListNBT list = item.getTag().getCompound("BlockEntityTag").getList("Items", NBT.TAG_COMPOUND);

				for (int i = 0; i < list.size(); i++) {
					ItemStack itemInChest = ItemStack.of(list.getCompound(i));

					if (areItemsEqual(itemInChest, stackToCheck, hasSmartModule)) {
						if (hasStorageModule) {
							te.addItemToStorage(itemInChest);
							list.remove(i);
						}

						if (hasRedstoneModule)
							updateInventoryScannerPower(te);

						return true;
					}
				}
			}
		}

		return false;
	}

	private static boolean areItemsEqual(ItemStack firstItemStack, ItemStack secondItemStack, boolean hasSmartModule) {
		return (hasSmartModule && areItemStacksEqual(firstItemStack, secondItemStack) && ItemStack.tagMatches(firstItemStack, secondItemStack)) || (!hasSmartModule && firstItemStack.getItem() == secondItemStack.getItem());
	}

	private static void updateInventoryScannerPower(InventoryScannerBlockEntity te) {
		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(te.getLevel(), te.getBlockPos());

		if (connectedScanner == null)
			return;

		updateInvScanner(te);
		updateInvScanner(connectedScanner);
	}

	private static void updateInvScanner(InventoryScannerBlockEntity te) {
		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getLevel(), te.getBlockPos(), te.getBlockState().getBlock(), 1, true);
		BlockUtils.updateIndirectNeighbors(te.getLevel(), te.getBlockPos(), SCContent.INVENTORY_SCANNER.get());
	}

	/**
	 * See {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} but without size restriction
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.setCount(1);
		s2.setCount(1);
		return ItemStack.matches(s1, s2);
	}

	@Override
	public void destroy(IWorld world, BlockPos pos, BlockState state) {
		if (!world.isClientSide()) {
			Direction facing = state.getValue(FACING);

			BlockUtils.removeInSequence((direction, stateToCheck) -> {
				if (stateToCheck.getBlock() != SCContent.INVENTORY_SCANNER_FIELD.get())
					return false;

				Direction stateToCheckFacing = stateToCheck.getValue(FACING);

				return stateToCheckFacing == direction || stateToCheckFacing == direction.getOpposite();
			}, world, pos, facing, facing.getOpposite());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx) {
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
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (side == Direction.UP || side == Direction.DOWN) {
			if (state.getBlock() == adjacentBlockState.getBlock())
				return true;
		}

		return super.skipRendering(state, adjacentBlockState, side);
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
