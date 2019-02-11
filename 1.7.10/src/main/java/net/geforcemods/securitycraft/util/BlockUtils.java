package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtils{

	/**
	 * Used by the Cage Trap to create the cage. <p>
	 *
	 * Args: world, x, y, z, block.
	 */
	public static void setBlockInBox(World world, int x, int y, int z, Block block){
		world.setBlock(x + 1, y + 1, z, block);
		world.setBlock(x + 1, y + 2, z, block);
		world.setBlock(x + 1, y + 3, z, block);
		world.setBlock(x + 1, y + 1, z + 1, block);
		world.setBlock(x + 1, y + 2, z + 1, block);
		world.setBlock(x + 1, y + 3, z + 1, block);
		world.setBlock(x - 1, y + 1, z, block);
		world.setBlock(x - 1, y + 2, z, block);
		world.setBlock(x - 1, y + 3, z, block);
		world.setBlock(x - 1, y + 1, z + 1, block);
		world.setBlock(x - 1, y + 2, z + 1, block);
		world.setBlock(x - 1, y + 3, z + 1, block);
		world.setBlock(x, y + 1, z + 1, block);
		world.setBlock(x, y + 2, z + 1, block);
		world.setBlock(x, y + 3, z + 1, block);

		world.setBlock(x + 1, y + 1, z, block);
		world.setBlock(x + 1, y + 2, z, block);
		world.setBlock(x + 1, y + 3, z, block);

		world.setBlock(x, y + 1, z - 1, block);
		world.setBlock(x, y + 2, z - 1, block);
		world.setBlock(x, y + 3, z - 1, block);
		world.setBlock(x + 1, y + 1, z - 1, block);
		world.setBlock(x + 1, y + 2, z - 1, block);
		world.setBlock(x + 1, y + 3, z - 1, block);
		world.setBlock(x - 1, y + 1, z - 1, block);
		world.setBlock(x - 1, y + 2, z - 1, block);
		world.setBlock(x - 1, y + 3, z - 1, block);

		world.setBlock(x + 1, y + 4, z + 1, block);
		world.setBlock(x + 1, y + 4, z - 1, block);
		world.setBlock(x - 1, y + 4, z + 1, block);
		world.setBlock(x - 1, y + 4, z - 1, block);
	}

	/**
	 * Updates a block and notify's neighboring blocks of a change. <p>
	 *
	 * Args: worldObj, x, y, z, blockID, tickRate, shouldUpdate.
	 */
	public static void updateAndNotify(World world, int x, int y, int z, Block block, int tickRate, boolean shouldUpdate){
		if(shouldUpdate)
			world.scheduleBlockUpdate(x, y, z, block, tickRate);

		world.notifyBlocksOfNeighborChange(x, y, z, block, world.getBlockMetadata(x, y, z));
		world.notifyBlockOfNeighborChange(x + 1, y, z, world.getBlock(x, y, z));
		world.notifyBlockOfNeighborChange(x - 1, y, z, world.getBlock(x, y, z));
		world.notifyBlockOfNeighborChange(x, y, z + 1, world.getBlock(x, y, z));
		world.notifyBlockOfNeighborChange(x, y, z - 1, world.getBlock(x, y, z));
		world.notifyBlockOfNeighborChange(x, y + 1, z, world.getBlock(x, y, z));
		world.notifyBlockOfNeighborChange(x, y - 1, z, world.getBlock(x, y, z));
	}

	//laziness when backporting
	public static Block getBlock(World world, int x, int y, int z)
	{
		return world.getBlock(x, y, z);
	}

	/**
	 * Breaks the block at the given coordinates. <p>
	 *
	 * Args: world, x, y, z, shouldDropItem.
	 */
	public static void destroyBlock(World world, int x, int y, int z, boolean shouldDropItem){
		world.breakBlock(x, y, z, shouldDropItem);
	}

	/**
	 * Returns true if the metadata of the block at X, Y, and Z is within (or equal to) the minimum and maximum given.
	 */
	public static boolean isMetadataBetween(IBlockAccess world, int x, int y, int z, int min, int max) {
		return (world.getBlockMetadata(x, y, z) >= min && world.getBlockMetadata(x, y, z) <= max);
	}

	public static boolean hasActiveSCBlockNextTo(World par1World, int x, int y, int z)
	{
		return hasActiveLaserNextTo(par1World, x, y, z) || hasActiveScannerNextTo(par1World, x, y, z) || hasActiveKeypadNextTo(par1World, x, y, z) || hasActiveReaderNextTo(par1World, x, y, z) || hasActiveInventoryScannerNextTo(par1World, x, y, z);
	}

	private static boolean hasActiveLaserNextTo(World world, int x, int y, int z) {
		if(world.getBlock(x + 1, y, z) == SCContent.laserBlock && world.getBlockMetadata(x + 1, y, z) == 2)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.laserBlock && world.getBlockMetadata(x - 1, y, z) == 2)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.laserBlock && world.getBlockMetadata(x, y, z + 1) == 2)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.laserBlock && world.getBlockMetadata(x, y, z - 1) == 2)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y + 1, z) == SCContent.laserBlock && world.getBlockMetadata(x, y + 1, z) == 2)
			return ((IOwnable) world.getTileEntity(x, y + 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y - 1, z) == SCContent.laserBlock && world.getBlockMetadata(x, y - 1, z) == 2)
			return ((IOwnable) world.getTileEntity(x, y - 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private static boolean hasActiveScannerNextTo(World world, int x, int y, int z) {
		if(world.getBlock(x + 1, y, z) == SCContent.retinalScanner && world.getBlockMetadata(x + 1, y, z) > 6 && world.getBlockMetadata(x + 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.retinalScanner && world.getBlockMetadata(x - 1, y, z) > 6 && world.getBlockMetadata(x - 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.retinalScanner && world.getBlockMetadata(x, y, z + 1) > 6 && world.getBlockMetadata(x, y, z + 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.retinalScanner && world.getBlockMetadata(x, y, z - 1) > 6 && world.getBlockMetadata(x, y, z - 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y + 1, z) == SCContent.retinalScanner && world.getBlockMetadata(x, y + 1, z) > 6 && world.getBlockMetadata(x, y + 1, z) < 11)
			return ((IOwnable) world.getTileEntity(x, y + 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y - 1, z) == SCContent.retinalScanner && world.getBlockMetadata(x, y - 1, z) > 6 && world.getBlockMetadata(x, y - 1, z) < 11)
			return ((IOwnable) world.getTileEntity(x, y - 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private static boolean hasActiveKeypadNextTo(World world, int x, int y, int z){
		if(world.getBlock(x + 1, y, z) == SCContent.keypad && world.getBlockMetadata(x + 1, y, z) > 6 && world.getBlockMetadata(x + 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.keypad && world.getBlockMetadata(x - 1, y, z) > 6 && world.getBlockMetadata(x - 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.keypad && world.getBlockMetadata(x, y, z + 1) > 6 && world.getBlockMetadata(x, y, z + 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.keypad && world.getBlockMetadata(x, y, z - 1) > 6 && world.getBlockMetadata(x, y, z - 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y + 1, z) == SCContent.keypad && world.getBlockMetadata(x, y + 1, z) > 6 && world.getBlockMetadata(x, y + 1, z) < 11)
			return ((IOwnable) world.getTileEntity(x, y + 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y - 1, z) == SCContent.keypad && world.getBlockMetadata(x, y - 1, z) > 6 && world.getBlockMetadata(x, y - 1, z) < 11)
			return ((IOwnable) world.getTileEntity(x, y - 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private static boolean hasActiveReaderNextTo(World world, int x, int y, int z){
		if(world.getBlock(x + 1, y, z) == SCContent.keycardReader && world.getBlockMetadata(x + 1, y, z) > 6 && world.getBlockMetadata(x + 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.keycardReader && world.getBlockMetadata(x - 1, y, z) > 6 && world.getBlockMetadata(x - 1, y, z) < 11)
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.keycardReader && world.getBlockMetadata(x, y, z + 1) > 6 && world.getBlockMetadata(x, y, z + 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.keycardReader && world.getBlockMetadata(x, y, z - 1) > 6 && world.getBlockMetadata(x, y, z - 1) < 11)
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y + 1, z) == SCContent.keycardReader && world.getBlockMetadata(x, y + 1, z) > 6 && world.getBlockMetadata(x, y + 1, z) < 11)
			return ((IOwnable) world.getTileEntity(x, y + 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y - 1, z) == SCContent.keycardReader && world.getBlockMetadata(x, y - 1, z) > 6 && world.getBlockMetadata(x, y - 1, z) < 11)
			return ((IOwnable) world.getTileEntity(x, y - 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}

	private static boolean hasActiveInventoryScannerNextTo(World world, int x, int y, int z){
		if(world.getBlock(x + 1, y, z) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x + 1, y, z)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x + 1, y, z)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x + 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x - 1, y, z) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x - 1, y, z)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x - 1, y, z)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x - 1, y, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z + 1) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z + 1)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z + 1)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x, y, z + 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y, z - 1) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z - 1)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x, y, z - 1)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x, y, z - 1)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y + 1, z) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x, y + 1, z)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x, y + 1, z)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x, y + 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else if(world.getBlock(x, y - 1, z) == SCContent.inventoryScanner && ((TileEntityInventoryScanner) world.getTileEntity(x, y - 1, z)).getType().equals("redstone") && ((TileEntityInventoryScanner) world.getTileEntity(x, y - 1, z)).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(x, y - 1, z)).getOwner().owns((IOwnable)world.getTileEntity(x, y, z));
		else
			return false;
	}
}
