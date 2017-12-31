package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utils {

	//North: Z-  South: Z+  East: X+  West: X-  Up: Y+  Down: Y-

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String par1){
		if(par1 == null || par1.isEmpty())
			return "";

		return par1.substring(0, par1.length() - 1);
	}

	public static String getFormattedCoordinates(BlockPos pos){
		return "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
	}

	public static void setISinTEAppropriately(World par1World, BlockPos pos, ItemStack[] contents, String type) {
		if(par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == SCContent.inventoryScanner && BlockUtils.getBlock(par1World, pos.west()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.west(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setType(type);
		}
		else if(par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == SCContent.inventoryScanner && BlockUtils.getBlock(par1World, pos.east()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.east(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setType(type);
		}
		else if(par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == SCContent.inventoryScanner && BlockUtils.getBlock(par1World, pos.north()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.north(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setType(type);
		}
		else if(par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == SCContent.inventoryScanner && BlockUtils.getBlock(par1World, pos.south()) == SCContent.inventoryScannerField && par1World.getBlockState(pos.south(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setType(type);
		}
	}

	public static boolean hasInventoryScannerFacingBlock(World par1World, BlockPos pos) {
		if(BlockUtils.getBlock(par1World, pos.east()) == SCContent.inventoryScanner && par1World.getBlockState(pos.east()).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west()) == SCContent.inventoryScanner && par1World.getBlockState(pos.west()).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST)
			return true;
		else if(BlockUtils.getBlock(par1World, pos.west()) == SCContent.inventoryScanner && par1World.getBlockState(pos.west()).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east()) == SCContent.inventoryScanner && par1World.getBlockState(pos.east()).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST)
			return true;
		else if(BlockUtils.getBlock(par1World, pos.south()) == SCContent.inventoryScanner && par1World.getBlockState(pos.south()).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north()) == SCContent.inventoryScanner && par1World.getBlockState(pos.north()).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH)
			return true;
		else if(BlockUtils.getBlock(par1World, pos.north()) == SCContent.inventoryScanner && par1World.getBlockState(pos.north()).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south()) == SCContent.inventoryScanner && par1World.getBlockState(pos.south()).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH)
			return true;
		else
			return false;
	}
}
