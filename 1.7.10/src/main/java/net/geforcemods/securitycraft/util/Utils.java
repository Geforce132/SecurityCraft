package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * SecurityCraft's utility methods are found here. Frequently used or simplified vanilla code can be found here or in other util classes in this package.
 *
 * @author Geforce
 */
public class Utils {

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String par1){
		if(par1 == null || par1.isEmpty())
			return "";

		return par1.substring(0, par1.length() - 1);
	}

	/**
	 * Returns the given X, Y, and Z coordinates in a nice String, useful for chat messages. <p>
	 *
	 * Args: x, y, z.
	 */
	public static String getFormattedCoordinates(int par1, int par2, int par3){
		return "X: " + par1 + " Y: " + par2 + " Z: " + par3;
	}

	public static void setISinTEAppropriately(World par1World, int par2, int par3, int par4, ItemStack[] contents, String type) {
		if(par1World.getBlockMetadata(par2, par3, par4) == 4 && par1World.getBlock(par2 - 2, par3, par4) == SCContent.inventoryScanner && par1World.getBlock(par2 - 1, par3, par4) == SCContent.inventoryScannerField && par1World.getBlockMetadata(par2 - 2, par3, par4) == 5){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).setType(type);
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 5 && par1World.getBlock(par2 + 2, par3, par4) == SCContent.inventoryScanner && par1World.getBlock(par2 + 1, par3, par4) == SCContent.inventoryScannerField && par1World.getBlockMetadata(par2 + 2, par3, par4) == 4){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).setType(type);
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 2 && par1World.getBlock(par2, par3, par4 - 2) == SCContent.inventoryScanner && par1World.getBlock(par2, par3, par4 - 1) == SCContent.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 - 2) == 3){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).setType(type);
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 3 && par1World.getBlock(par2, par3, par4 + 2) == SCContent.inventoryScanner && par1World.getBlock(par2, par3, par4 + 1) == SCContent.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 + 2) == 2){
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).setType(type);
		}
	}

	public static boolean hasInventoryScannerFacingBlock(World par1World, int par2, int par3, int par4) {
		if(par1World.getBlock(par2 + 1, par3, par4) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) == 4 && par1World.getBlock(par2 - 1, par3, par4) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) == 5)
			return true;
		else if(par1World.getBlock(par2 - 1, par3, par4) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) == 5 && par1World.getBlock(par2 + 1, par3, par4) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) == 4)
			return true;
		else if(par1World.getBlock(par2, par3, par4 + 1) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2 && par1World.getBlock(par2, par3, par4 - 1) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) == 3)
			return true;
		else if(par1World.getBlock(par2, par3, par4 - 1) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) == 3 && par1World.getBlock(par2, par3, par4 + 1) == SCContent.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2)
			return true;
		else
			return false;
	}
}
