package net.geforcemods.securitycraft.blocks;

import java.util.function.BiFunction;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

public class InventoryScannerFieldBlock extends OwnableBlock implements IIntersectable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE_EW = Block.makeCuboidShape(0, 0, 6, 16, 16, 10);
	private static final VoxelShape SHAPE_NS = Block.makeCuboidShape(6, 0, 0, 10, 16, 16);

	public InventoryScannerFieldBlock(Block.Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		if(entity instanceof PlayerEntity && !EntityUtils.isInvisible((PlayerEntity)entity))
		{
			if(ModuleUtils.checkForModule(world, connectedScanner.getPos(), (PlayerEntity)entity, ModuleType.WHITELIST))
				return;

			for(int i = 0; i < 10; i++)
			{
				if(!connectedScanner.getStackInSlotCopy(i).isEmpty())
					checkInventory((PlayerEntity)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
			}
		}
		else if(entity instanceof ItemEntity)
		{
			for(int i = 0; i < 10; i++)
			{
				if(!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((ItemEntity)entity).getItem().isEmpty())
					checkItemEntity((ItemEntity)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
			}
		}
	}

	public static void checkInventory(PlayerEntity player, InventoryScannerTileEntity te, ItemStack stack)
	{
		boolean hasSmartModule = te.hasModule(ModuleType.SMART);
		boolean hasStorageModule = te.hasModule(ModuleType.STORAGE);

		if(te.hasModule(ModuleType.REDSTONE))
		{
			redstoneLoop(player.inventory.mainInventory, stack, te, hasSmartModule, hasStorageModule);
			redstoneLoop(player.inventory.armorInventory, stack, te, hasSmartModule, hasStorageModule);
			redstoneLoop(player.inventory.offHandInventory, stack, te, hasSmartModule, hasStorageModule);
		}

		if(hasStorageModule && !te.getOwner().isOwner(player))
		{
			checkLoop(player.inventory.mainInventory, stack, te, hasSmartModule, hasStorageModule);
			checkLoop(player.inventory.armorInventory, stack, te, hasSmartModule, hasStorageModule);
			checkLoop(player.inventory.offHandInventory, stack, te, hasSmartModule, hasStorageModule);
		}
	}

	private static void redstoneLoop(NonNullList<ItemStack> inventory, ItemStack stack, InventoryScannerTileEntity te, boolean hasSmartModule, boolean hasStorageModule)
	{
		for(int i = 1; i <= inventory.size(); i++)
		{
			ItemStack itemStackChecking = inventory.get(i - 1);

			if(!itemStackChecking.isEmpty())
			{
				if((hasSmartModule && areItemStacksEqual(itemStackChecking, stack) && ItemStack.areItemStackTagsEqual(itemStackChecking, stack))
						|| (!hasSmartModule && itemStackChecking.getItem() == stack.getItem()) || checkForShulkerBox(itemStackChecking, stack, te, hasSmartModule, hasStorageModule))
				{
					updateInventoryScannerPower(te);
				}
			}
		}
	}

	private static void checkLoop(NonNullList<ItemStack> inventory, ItemStack stack, InventoryScannerTileEntity te, boolean hasSmartModule, boolean hasStorageModule)
	{
		for(int i = 1; i <= inventory.size(); i++)
		{
			ItemStack itemStackChecking = inventory.get(i - 1);

			if(!itemStackChecking.isEmpty())
			{
				checkForShulkerBox(itemStackChecking, stack, te, hasSmartModule, hasStorageModule);

				if((hasSmartModule && areItemStacksEqual(itemStackChecking, stack) && ItemStack.areItemStackTagsEqual(itemStackChecking, stack))
						|| (!hasSmartModule && itemStackChecking.getItem() == stack.getItem()))
				{
					if(hasStorageModule)
						te.addItemToStorage(inventory.get(i - 1));

					inventory.set(i - 1, ItemStack.EMPTY);
				}
			}
		}
	}

	public static void checkItemEntity(ItemEntity entity, InventoryScannerTileEntity te, ItemStack stack)
	{
		boolean hasSmartModule = te.hasModule(ModuleType.SMART);
		boolean hasStorageModule = te.hasModule(ModuleType.STORAGE);

		if(te.hasModule(ModuleType.REDSTONE))
		{
			if((hasSmartModule && areItemStacksEqual(entity.getItem(), stack) && ItemStack.areItemStackTagsEqual(entity.getItem(), stack))
					|| (!hasSmartModule && entity.getItem().getItem() == stack.getItem()) || checkForShulkerBox(entity.getItem(), stack, te, hasSmartModule, hasStorageModule))
			{
				updateInventoryScannerPower(te);
			}
		}

		if(hasStorageModule)
		{
			checkForShulkerBox(entity.getItem(), stack, te, hasSmartModule, hasStorageModule);

			if((hasSmartModule && areItemStacksEqual(entity.getItem(), stack) && ItemStack.areItemStackTagsEqual(entity.getItem(), stack))
					|| (!hasSmartModule && entity.getItem().getItem() == stack.getItem()))
			{
				if(hasStorageModule)
					te.addItemToStorage(entity.getItem());

				entity.remove();
			}
		}
	}

	private static boolean checkForShulkerBox(ItemStack item, ItemStack stackToCheck, InventoryScannerTileEntity te, boolean hasSmartModule, boolean hasStorageModule) {
		boolean deletedItem = false;

		if(item != null) {
			if(!item.isEmpty() && item.getTag() != null && Block.getBlockFromItem(item.getItem()) instanceof ShulkerBoxBlock) {
				ListNBT list = item.getTag().getCompound("BlockEntityTag").getList("Items", NBT.TAG_COMPOUND);

				for(int i = 0; i < list.size(); i++) {
					ItemStack itemInChest = ItemStack.read(list.getCompound(i));
					if((hasSmartModule && areItemStacksEqual(itemInChest, stackToCheck) && ItemStack.areItemStackTagsEqual(itemInChest, stackToCheck)) || (!hasSmartModule && areItemStacksEqual(itemInChest, stackToCheck))) {
						list.remove(i);
						deletedItem = true;

						if(hasStorageModule)
							te.addItemToStorage(itemInChest);
					}
				}
			}
		}

		return deletedItem;
	}

	private static void updateInventoryScannerPower(InventoryScannerTileEntity te)
	{
		if(!te.shouldProvidePower())
			te.setShouldProvidePower(true);

		te.setCooldown(60);
		checkAndUpdateTEAppropriately(te);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()).getBlock(), 1, true);
	}

	/**
	 * See {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} but without size restriction
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.setCount(1);
		s2.setCount(1);
		return ItemStack.areItemStacksEqual(s1, s2);
	}

	private static void checkAndUpdateTEAppropriately(InventoryScannerTileEntity te)
	{
		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(te.getWorld(), te.getPos());

		if(connectedScanner == null)
			return;

		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getBlockState().getBlock(), 1, true);
		connectedScanner.setShouldProvidePower(true);
		connectedScanner.setCooldown(60);
		BlockUtils.updateAndNotify(connectedScanner.getWorld(), connectedScanner.getPos(), connectedScanner.getBlockState().getBlock(), 1, true);
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state)
	{
		if(!world.isRemote())
		{
			Direction facing = state.get(FACING);

			if (facing == Direction.EAST || facing == Direction.WEST)
			{
				checkAndDestroyFields(world, pos, (p, i) -> p.west(i));
				checkAndDestroyFields(world, pos, (p, i) -> p.east(i));
			}
			else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			{
				checkAndDestroyFields(world, pos, (p, i) -> p.north(i));
				checkAndDestroyFields(world, pos, (p, i) -> p.south(i));
			}
		}
	}

	private void checkAndDestroyFields(IWorld world, BlockPos pos, BiFunction<BlockPos,Integer,BlockPos> posModifier)
	{
		for(int i = 0; i < ConfigHandler.CONFIG.inventoryScannerRange.get(); i++)
		{
			BlockPos modifiedPos = posModifier.apply(pos, i);

			if(BlockUtils.getBlock(world, modifiedPos) == SCContent.INVENTORY_SCANNER.get())
			{
				for(int j = 1; j < i; j++)
				{
					world.destroyBlock(posModifier.apply(pos, j), false);
				}

				break;
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		Direction facing = state.get(FACING);

		if (facing == Direction.EAST || facing == Direction.WEST)
			return SHAPE_EW; //ew
		else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			return SHAPE_NS; //ns
		return VoxelShapes.fullCube();
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SecurityCraftTileEntity().intersectsEntities();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		if (side == Direction.UP || side == Direction.DOWN)
			if (state.getBlock() == adjacentBlockState.getBlock())
				return true;

		return super.isSideInvisible(state, adjacentBlockState, side);
	}

}
