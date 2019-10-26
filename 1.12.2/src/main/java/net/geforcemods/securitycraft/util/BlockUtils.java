package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedPressurePlate;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtils{

	public static void setBlockInBox(World world, int x, int y, int z, Block block){
		BlockUtils.setBlock(world, x + 1, y + 1, z, block);
		BlockUtils.setBlock(world, x + 1, y + 2, z, block);
		BlockUtils.setBlock(world, x + 1, y + 3, z, block);
		BlockUtils.setBlock(world, x + 1, y + 1, z + 1, block);
		BlockUtils.setBlock(world, x + 1, y + 2, z + 1, block);
		BlockUtils.setBlock(world, x + 1, y + 3, z + 1, block);
		BlockUtils.setBlock(world, x - 1, y + 1, z, block);
		BlockUtils.setBlock(world, x - 1, y + 2, z, block);
		BlockUtils.setBlock(world, x - 1, y + 3, z, block);
		BlockUtils.setBlock(world, x - 1, y + 1, z + 1, block);
		BlockUtils.setBlock(world, x - 1, y + 2, z + 1, block);
		BlockUtils.setBlock(world, x - 1, y + 3, z + 1, block);
		BlockUtils.setBlock(world, x, y + 1, z + 1, block);
		BlockUtils.setBlock(world, x, y + 2, z + 1, block);
		BlockUtils.setBlock(world, x, y + 3, z + 1, block);
		BlockUtils.setBlock(world, x + 1, y + 1, z, block);
		BlockUtils.setBlock(world, x + 1, y + 2, z, block);
		BlockUtils.setBlock(world, x + 1, y + 3, z, block);

		BlockUtils.setBlock(world, x, y + 1, z - 1, block);
		BlockUtils.setBlock(world, x, y + 2, z - 1, block);
		BlockUtils.setBlock(world, x, y + 3, z - 1, block);
		BlockUtils.setBlock(world, x + 1, y + 1, z - 1, block);
		BlockUtils.setBlock(world, x + 1, y + 2, z - 1, block);
		BlockUtils.setBlock(world, x + 1, y + 3, z - 1, block);

		BlockUtils.setBlock(world, x - 1, y + 1, z - 1, block);
		BlockUtils.setBlock(world, x - 1, y + 2, z - 1, block);
		BlockUtils.setBlock(world, x - 1, y + 3, z - 1, block);

		BlockUtils.setBlock(world, x + 1, y + 4, z + 1, block);
		BlockUtils.setBlock(world, x + 1, y + 4, z - 1, block);
		BlockUtils.setBlock(world, x - 1, y + 4, z + 1, block);
		BlockUtils.setBlock(world, x - 1, y + 4, z - 1, block);
	}

	/**
	 * Updates a block and notify's neighboring blocks of a change.
	 *
	 * Args: worldObj, pos, blockID, tickRate, shouldUpdate
	 *
	 *
	 */
	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.scheduleUpdate(pos, block, delay);
		world.neighborChanged(pos.east(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.west(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.south(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.north(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.up(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.down(), world.getBlockState(pos).getBlock(), pos);
	}

	public static void destroyBlock(World world, BlockPos pos, boolean drop){
		world.destroyBlock(pos, drop);
	}

	public static int getBlockMeta(World world, BlockPos pos){
		return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
	}

	public static void setBlock(World world, BlockPos pos, Block block){
		world.setBlockState(pos, block.getDefaultState());
	}

	public static void setBlock(World world, int x, int y, int z, Block block){
		setBlock(world, toPos(x, y, z), block);
	}

	public static Block getBlock(World world, BlockPos pos){
		return world.getBlockState(pos).getBlock();
	}

	public static Block getBlock(IBlockAccess access, BlockPos pos){
		return access.getBlockState(pos).getBlock();
	}

	public static Block getBlock(World world, int x, int y, int z){
		return world.getBlockState(toPos(x, y, z)).getBlock();
	}

	public static void setBlockProperty(World world, BlockPos pos, PropertyBool property, boolean value) {
		setBlockProperty(world, pos, property, value, false);
	}

	public static void setBlockProperty(World world, BlockPos pos, PropertyBool property, boolean value, boolean retainOldTileEntity) {
		if(retainOldTileEntity){
			NonNullList<ItemStack> modules = null;
			NonNullList<ItemStack> inventory = null;
			int[] times = new int[4];
			String password = "";
			Owner owner = null;
			int cooldown = -1;

			if(world.getTileEntity(pos) instanceof CustomizableSCTE)
				modules = ((CustomizableSCTE) world.getTileEntity(pos)).modules;

			if(world.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				inventory = ((TileEntityKeypadFurnace) world.getTileEntity(pos)).furnaceItemStacks;
				times[0] = ((TileEntityKeypadFurnace) world.getTileEntity(pos)).furnaceBurnTime;
				times[1] = ((TileEntityKeypadFurnace) world.getTileEntity(pos)).currentItemBurnTime;
				times[2] = ((TileEntityKeypadFurnace) world.getTileEntity(pos)).cookTime;
				times[3] = ((TileEntityKeypadFurnace) world.getTileEntity(pos)).totalCookTime;
			}

			if(world.getTileEntity(pos) instanceof TileEntityOwnable && ((TileEntityOwnable) world.getTileEntity(pos)).getOwner() != null)
				owner = ((TileEntityOwnable) world.getTileEntity(pos)).getOwner();

			if(world.getTileEntity(pos) instanceof TileEntityKeypad && ((TileEntityKeypad) world.getTileEntity(pos)).getPassword() != null)
				password = ((TileEntityKeypad) world.getTileEntity(pos)).getPassword();

			if(world.getTileEntity(pos) instanceof TileEntityKeypadFurnace && ((TileEntityKeypadFurnace) world.getTileEntity(pos)).getPassword() != null)
				password = ((TileEntityKeypadFurnace) world.getTileEntity(pos)).getPassword();

			if(world.getTileEntity(pos) instanceof TileEntityKeypadChest && ((TileEntityKeypadChest) world.getTileEntity(pos)).getPassword() != null)
				password = ((TileEntityKeypadChest) world.getTileEntity(pos)).getPassword();

			if(world.getTileEntity(pos) instanceof TileEntityPortableRadar && ((TileEntityPortableRadar) world.getTileEntity(pos)).getAttackCooldown() != 0)
				cooldown = ((TileEntityPortableRadar) world.getTileEntity(pos)).getAttackCooldown();

			TileEntity tileEntity = world.getTileEntity(pos);
			world.setBlockState(pos, world.getBlockState(pos).withProperty(property, value));
			world.setTileEntity(pos, tileEntity);

			if(modules != null)
				((CustomizableSCTE) world.getTileEntity(pos)).modules = modules;

			if(inventory != null && world.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).furnaceItemStacks = inventory;
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).furnaceBurnTime = times[0];
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).currentItemBurnTime = times[1];
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).cookTime = times[2];
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).totalCookTime = times[3];
			}

			if(owner != null)
				((TileEntityOwnable) world.getTileEntity(pos)).getOwner().set(owner);

			if(!password.isEmpty() && world.getTileEntity(pos) instanceof TileEntityKeypad)
				((TileEntityKeypad) world.getTileEntity(pos)).setPassword(password);

			if(!password.isEmpty() && world.getTileEntity(pos) instanceof TileEntityKeypadFurnace)
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).setPassword(password);

			if(!password.isEmpty() && world.getTileEntity(pos) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) world.getTileEntity(pos)).setPassword(password);

			if(cooldown != -1 && world.getTileEntity(pos) instanceof TileEntityPortableRadar)
				((TileEntityPortableRadar) world.getTileEntity(pos)).setAttackCooldown(cooldown);
		}
		else
			world.setBlockState(pos, world.getBlockState(pos).withProperty(property, value));
	}

	public static void setBlockProperty(World world, BlockPos pos, PropertyInteger property, int value) {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(property, value));
	}

	public static void setBlockProperty(World world, BlockPos pos, PropertyEnum property, EnumFacing value) {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(property, value));
	}

	public static boolean hasBlockProperty(World world, BlockPos pos, IProperty<?> property){
		try{
			world.getBlockState(pos).getValue(property);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}
	}

	public static boolean getBlockPropertyAsBoolean(World world, BlockPos pos, PropertyBool property){
		return world.getBlockState(pos).getValue(property).booleanValue();
	}

	public static boolean getBlockPropertyAsBoolean(IBlockAccess access, BlockPos pos, PropertyBool property){
		return access.getBlockState(pos).getValue(property).booleanValue();
	}

	public static int getBlockPropertyAsInteger(World world, BlockPos pos, PropertyInteger property){
		return world.getBlockState(pos).getValue(property).intValue();
	}

	public static EnumFacing getBlockPropertyAsEnum(World world, BlockPos pos, PropertyEnum<?> property){
		return ((EnumFacing) world.getBlockState(pos).getValue(property));
	}

	public static EnumFacing getBlockPropertyAsEnum(IBlockAccess world, BlockPos pos, PropertyEnum<?> property){
		return ((EnumFacing) world.getBlockState(pos).getValue(property));
	}

	public static EnumFacing getBlockProperty(World world, BlockPos pos, PropertyDirection property) {
		return world.getBlockState(pos).getValue(property);
	}

	public static Material getBlockMaterial(World world, BlockPos pos){
		return world.getBlockState(pos).getMaterial();
	}

	/**
	 * returns an AABB with corners x1, y1, z1 and x2, y2, z2
	 */
	public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		double d6 = Math.min(x1, x2);
		double d7 = Math.min(y1, y2);
		double d8 = Math.min(z1, z2);
		double d9 = Math.max(x1, x2);
		double d10 = Math.max(y1, y2);
		double d11 = Math.max(z1, z2);
		return new AxisAlignedBB(d6, d7, d8, d9, d10, d11);
	}

	public static BlockPos toPos(int x, int y, int z){
		return new BlockPos(x, y, z);
	}

	public static int[] fromPos(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos)
	{
		return hasActiveLaserNextTo(world, pos) || hasActiveScannerNextTo(world, pos) || hasActiveKeypadNextTo(world, pos) || hasActiveReaderNextTo(world, pos) || hasActiveInventoryScannerNextTo(world, pos) || hasActiveReinforcedPressurePlateNextTo(world, pos);
	}

	private static boolean hasActiveLaserNextTo(World world, BlockPos pos) {
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockLaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveScannerNextTo(World world, BlockPos pos) {
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockRetinalScanner.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveKeypadNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockKeypad.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveReaderNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), BlockKeycardReader.POWERED))
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), BlockKeycardReader.POWERED))
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), BlockKeycardReader.POWERED))
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), BlockKeycardReader.POWERED))
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), BlockKeycardReader.POWERED))
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), BlockKeycardReader.POWERED))
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveInventoryScannerNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.east())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.east())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.west())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.west())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.south())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.south())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.north())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.north())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.up())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.up())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(pos.down())).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(pos.down())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveReinforcedPressurePlateNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.reinforcedStonePressurePlate && world.getBlockState(pos.east()).getValue(BlockReinforcedPressurePlate.POWERED))
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.reinforcedStonePressurePlate && world.getBlockState(pos.west()).getValue(BlockReinforcedPressurePlate.POWERED))
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.reinforcedStonePressurePlate && world.getBlockState(pos.south()).getValue(BlockReinforcedPressurePlate.POWERED))
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.reinforcedStonePressurePlate && world.getBlockState(pos.north()).getValue(BlockReinforcedPressurePlate.POWERED))
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.reinforcedStonePressurePlate && world.getBlockState(pos.up()).getValue(BlockReinforcedPressurePlate.POWERED))
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.reinforcedStonePressurePlate && world.getBlockState(pos.down()).getValue(BlockReinforcedPressurePlate.POWERED))
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}
}
