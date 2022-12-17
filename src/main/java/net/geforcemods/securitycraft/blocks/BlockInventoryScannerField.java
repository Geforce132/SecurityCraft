package net.geforcemods.securitycraft.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryScannerField extends BlockOwnable implements IOverlayDisplay {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool HORIZONTAL = PropertyBool.create("horizontal");
	public static final AxisAlignedBB EAST_WEST_SHAPE = new AxisAlignedBB(0.000F, 0.000F, 6F / 16F, 1.000F, 1.000F, 10F / 16F);
	public static final AxisAlignedBB NORTH_SOUTH_SHAPE = new AxisAlignedBB(6F / 16F, 0.000F, 0.000F, 10F / 16F, 1.000F, 1.000F);
	public static final AxisAlignedBB HORIZONTAL_SHAPE = new AxisAlignedBB(0.000F, 6F / 16F, 0.000F, 1.000F, 10F / 16F, 1.000F);

	public BlockInventoryScannerField(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HORIZONTAL, false));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
		return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if (connectedScanner != null && connectedScanner.doesFieldSolidify()) {
			if (entity instanceof EntityPlayer && !EntityUtils.isInvisible((EntityPlayer) entity)) {
				if (connectedScanner.isAllowed(entity))
					addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);

				for (int i = 0; i < 10; i++) {
					if (!connectedScanner.getStackInSlotCopy(i).isEmpty()) {
						if (checkInventory((EntityPlayer) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), false))
							addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoundingBox(state, world, pos));
					}
				}
			}
			else if (entity instanceof EntityItem) {
				for (int i = 0; i < 10; i++) {
					if (!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((EntityItem) entity).getItem().isEmpty()) {
						if (checkEntityItem((EntityItem) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), false))
							addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoundingBox(state, world, pos));
					}
				}
			}
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!state.getBoundingBox(world, pos).offset(pos).intersects(entity.getEntityBoundingBox()))
			return;

		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if (connectedScanner == null || connectedScanner.doesFieldSolidify())
			return;

		if (entity instanceof EntityPlayer && !EntityUtils.isInvisible((EntityLivingBase) entity)) {
			if (connectedScanner.isAllowed(entity))
				return;

			for (int i = 0; i < 10; i++) {
				if (!connectedScanner.getStackInSlotCopy(i).isEmpty())
					checkInventory((EntityPlayer) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), true);
			}
		}
		else if (entity instanceof EntityItem) {
			for (int i = 0; i < 10; i++) {
				if (!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((EntityItem) entity).getItem().isEmpty())
					checkEntityItem((EntityItem) entity, connectedScanner, connectedScanner.getStackInSlotCopy(i), true);
			}
		}
	}

	public static boolean checkInventory(EntityPlayer player, TileEntityInventoryScanner te, ItemStack stack, boolean allowInteraction) {
		boolean hasSmartModule = te.isModuleEnabled(EnumModuleType.SMART);
		boolean hasStorageModule = allowInteraction && te.isModuleEnabled(EnumModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && te.isModuleEnabled(EnumModuleType.REDSTONE);

		if ((!hasRedstoneModule && !hasStorageModule && allowInteraction) || te.isOwnedBy(player))
			return false;

		return loopInventory(player.inventory.mainInventory, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.inventory.armorInventory, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule) || loopInventory(player.inventory.offHandInventory, stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean loopInventory(NonNullList<ItemStack> inventory, ItemStack stack, TileEntityInventoryScanner te, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
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

	public static boolean checkEntityItem(EntityItem entity, TileEntityInventoryScanner te, ItemStack stack, boolean allowInteraction) {
		boolean hasSmartModule = te.isModuleEnabled(EnumModuleType.SMART);
		boolean hasStorageModule = allowInteraction && te.isModuleEnabled(EnumModuleType.STORAGE);
		boolean hasRedstoneModule = allowInteraction && te.isModuleEnabled(EnumModuleType.REDSTONE);

		if (!hasRedstoneModule && !hasStorageModule && allowInteraction)
			return false;

		if (areItemsEqual(entity.getItem(), stack, hasSmartModule)) {
			if (hasStorageModule) {
				te.addItemToStorage(entity.getItem());
				entity.setDead();
			}

			if (hasRedstoneModule)
				updateInventoryScannerPower(te);

			return true;
		}

		return checkForShulkerBox(entity.getItem(), stack, te, hasSmartModule, hasStorageModule, hasRedstoneModule);
	}

	private static boolean checkForShulkerBox(ItemStack item, ItemStack stackToCheck, TileEntityInventoryScanner te, boolean hasSmartModule, boolean hasStorageModule, boolean hasRedstoneModule) {
		if (item != null) {
			if (!item.isEmpty() && item.getTagCompound() != null && Block.getBlockFromItem(item.getItem()) instanceof BlockShulkerBox) {
				NBTTagList list = item.getTagCompound().getCompoundTag("BlockEntityTag").getTagList("Items", NBT.TAG_COMPOUND);

				for (int i = 0; i < list.tagCount(); i++) {
					ItemStack itemInChest = new ItemStack(list.getCompoundTagAt(i));

					if (areItemsEqual(itemInChest, stackToCheck, hasSmartModule)) {
						if (hasStorageModule) {
							te.addItemToStorage(itemInChest);
							list.removeTag(i);
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
		return (hasSmartModule && areItemStacksEqual(firstItemStack, secondItemStack) && ItemStack.areItemStackTagsEqual(firstItemStack, secondItemStack)) || (!hasSmartModule && areItemStacksEqual(firstItemStack, secondItemStack));
	}

	private static void updateInventoryScannerPower(TileEntityInventoryScanner te) {
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(te.getWorld(), te.getPos());

		if (connectedScanner == null)
			return;

		updateInvScanner(te);
		updateInvScanner(connectedScanner);
	}

	private static void updateInvScanner(TileEntityInventoryScanner te) {
		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), SCContent.inventoryScanner, 1, true);
		BlockUtils.updateIndirectNeighbors(te.getWorld(), te.getPos(), SCContent.inventoryScanner);
	}

	/**
	 * See {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} but without size restriction
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.setCount(1);
		s2.setCount(1);
		return ItemStack.areItemStacksEqual(s1, s2);
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			EnumFacing facing = state.getValue(FACING);

			if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
				BlockUtils.destroyInSequence(this, world, pos, EnumFacing.EAST, EnumFacing.WEST);
			else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
				BlockUtils.destroyInSequence(this, world, pos, EnumFacing.NORTH, EnumFacing.SOUTH);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(HORIZONTAL))
			return HORIZONTAL_SHAPE;
		else if (state.getValue(FACING) == EnumFacing.EAST || state.getValue(FACING) == EnumFacing.WEST)
			return EAST_WEST_SHAPE;
		else if (state.getValue(FACING) == EnumFacing.NORTH || state.getValue(FACING) == EnumFacing.SOUTH)
			return NORTH_SOUTH_SHAPE;

		return super.getBoundingBox(state, world, pos);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4)).withProperty(HORIZONTAL, meta > 3);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() + (state.getValue(HORIZONTAL) ? 4 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, HORIZONTAL);
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState offsetState = world.getBlockState(pos.offset(side));
		Block block = offsetState.getBlock();

		if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
			if (block == this)
				return false;
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return null;
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}
}
