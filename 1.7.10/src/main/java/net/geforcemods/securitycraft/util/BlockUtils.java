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
	 * Checks if the block at the given coordinates is a beacon, and currently producing that light beam? <p>
	 *
	 * Args: World, x, y, z.
	 */
	public static boolean isActiveBeacon(World world, int beaconX, int beaconY, int beaconZ){
		if(world.getBlock(beaconX, beaconY, beaconZ) == Blocks.beacon){
			float f = ((TileEntityBeacon) world.getTileEntity(beaconX, beaconY, beaconZ)).shouldBeamRender();

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
