package net.geforcemods.securitycraft.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtils{

	/**
	 * Used by the Cage Trap to create the cage. <p>
	 *
	 * Args: world, x, y, z, block.
	 */
	public static void setBlockInBox(World par1World, int par2, int par3, int par4, Block par5){
		par1World.setBlock(par2 + 1, par3 + 1, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 1, par4 + 1, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4 + 1, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 1, par4, par5);
		par1World.setBlock(par2 - 1, par3 + 2, par4, par5);
		par1World.setBlock(par2 - 1, par3 + 3, par4, par5);
		par1World.setBlock(par2 - 1, par3 + 1, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 2, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 3, par4 + 1, par5);
		par1World.setBlock(par2, par3 + 1, par4 + 1, par5);
		par1World.setBlock(par2, par3 + 2, par4 + 1, par5);
		par1World.setBlock(par2, par3 + 3, par4 + 1, par5);

		par1World.setBlock(par2 + 1, par3 + 1, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4, par5);

		par1World.setBlock(par2, par3 + 1, par4 - 1, par5);
		par1World.setBlock(par2, par3 + 2, par4 - 1, par5);
		par1World.setBlock(par2, par3 + 3, par4 - 1, par5);
		par1World.setBlock(par2 + 1, par3 + 1, par4 - 1, par5);
		par1World.setBlock(par2 + 1, par3 + 2, par4 - 1, par5);
		par1World.setBlock(par2 + 1, par3 + 3, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 1, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 2, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 3, par4 - 1, par5);

		par1World.setBlock(par2 + 1, par3 + 4, par4 + 1, par5);
		par1World.setBlock(par2 + 1, par3 + 4, par4 - 1, par5);
		par1World.setBlock(par2 - 1, par3 + 4, par4 + 1, par5);
		par1World.setBlock(par2 - 1, par3 + 4, par4 - 1, par5);
	}

	/**
	 * Updates a block and notify's neighboring blocks of a change. <p>
	 *
	 * Args: worldObj, x, y, z, blockID, tickRate, shouldUpdate.
	 */
	public static void updateAndNotify(World par1World, int par2, int par3, int par4, Block par5, int par6, boolean par7){
		if(par7)
			par1World.scheduleBlockUpdate(par2, par3, par4, par5, par6);

		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, par5, par1World.getBlockMetadata(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2 + 1, par3, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2 - 1, par3, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3, par4 + 1, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3, par4 - 1, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3 + 1, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3 - 1, par4, par1World.getBlock(par2, par3, par4));
	}

	/**
	 * Breaks the block at the given coordinates. <p>
	 *
	 * Args: world, x, y, z, shouldDropItem.
	 */
	public static void destroyBlock(World par1World, int par2, int par3, int par4, boolean par5){
		par1World.func_147480_a(par2, par3, par4, par5);
	}

	/**
	 * Checks if the block at the given coordinates is a beacon, and currently producing that light beam? <p>
	 *
	 * Args: World, x, y, z.
	 */
	public static boolean isActiveBeacon(World par1World, int beaconX, int beaconY, int beaconZ){
		if(par1World.getBlock(beaconX, beaconY, beaconZ) == Blocks.beacon){
			float f = ((TileEntityBeacon) par1World.getTileEntity(beaconX, beaconY, beaconZ)).func_146002_i();

			return f > 0.0F ? true : false;
		}
		else
			return false;
	}

	/**
	 * Checks if the block at the given coordinates is touching the specified block on any side. <p>
	 *
	 * Args: world, x, y, z, blockToCheckFor, checkYAxis.
	 */
	public static boolean blockSurroundedBy(World world, int x, int y, int z, Block blockToCheckFor, boolean checkYAxis) {
		if(!checkYAxis && (world.getBlock(x + 1, y, z) == blockToCheckFor || world.getBlock(x - 1, y, z) == blockToCheckFor || world.getBlock(x, y, z + 1) == blockToCheckFor || world.getBlock(x, y, z - 1) == blockToCheckFor))
			return true;
		else if(checkYAxis && (world.getBlock(x + 1, y, z) == blockToCheckFor || world.getBlock(x - 1, y, z) == blockToCheckFor || world.getBlock(x, y, z + 1) == blockToCheckFor || world.getBlock(x, y, z - 1) == blockToCheckFor || world.getBlock(x, y + 1, z) == blockToCheckFor || world.getBlock(x, y - 1, z) == blockToCheckFor))
			return true;
		else
			return false;
	}

	/**
	 * Returns true if the metadata of the block at X, Y, and Z is within (or equal to) the minimum and maximum given.
	 */
	public static boolean isMetadataBetween(IBlockAccess world, int x, int y, int z, int min, int max) {
		return (world.getBlockMetadata(x, y, z) >= min && world.getBlockMetadata(x, y, z) <= max);
	}
}
