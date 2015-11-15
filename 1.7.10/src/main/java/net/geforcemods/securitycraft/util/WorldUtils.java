package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCSpawnLightning;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class WorldUtils{
	
	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 * 
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(World world, int x1, int y1, int z1, int x2, int y2, int z2){
		return isPathObstructed(world, (double) x1, (double) y1, (double) z1, (double) x2, (double) y2, (double) z2);
	}

	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 * 
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		return world.rayTraceBlocks(Vec3.createVectorHelper(x1, y1, z1), Vec3.createVectorHelper(x2, y2, z2)) != null;
	}
	
	public static void spawnLightning(World world, double x, double y, double z){
		EntityLightningBolt lightning = new EntityLightningBolt(world, x, y, z);
    	world.addWeatherEffect(lightning);
    	
    	if(!world.isRemote) {
    		mod_SecurityCraft.network.sendToAll(new PacketCSpawnLightning(x, y, z));	
    	}
	}
}
