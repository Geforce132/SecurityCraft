package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WorldUtils{

	/**
	 * Correctly schedules a task for execution on the main thread depending on if the
	 * provided world is client- or serverside
	 */
	public static void addScheduledTask(IWorld w, Runnable r)
	{
		if(w.isRemote()) //clientside
			Minecraft.getInstance().execute(r);
		else //serverside
			ServerLifecycleHooks.getCurrentServer().execute(r);
	}

	public static void spawnLightning(World world, Vector3d pos, boolean effectOnly)
	{
		world.addEntity(createLightning(world, pos, effectOnly));
	}

	public static LightningBoltEntity createLightning(World world, Vector3d pos, boolean effectOnly)
	{
		LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);

		lightning.moveForced(pos);
		lightning.setEffectOnly(effectOnly);
		return lightning;
	}

	/**
	 * Checks to see if the given coordinates are the same as the given GlobalPos' coordinates.
	 *
	 * @param pos The GlobalPos to check against
	 * @param coordinates a String[] which contains the x, y, and z coordinates, and the dimension ID of the view
	 * @return true if the x, y, z and dimension match, false otherwise
	 */
	public static boolean checkCoordinates(GlobalPos pos, String[] coordinates)
	{
		int xPos = Integer.parseInt(coordinates[0]);
		int yPos = Integer.parseInt(coordinates[1]);
		int zPos = Integer.parseInt(coordinates[2]);
		ResourceLocation dim = new ResourceLocation(coordinates.length == 4 ? coordinates[3] : "");

		return pos.getPos().getX() == xPos && pos.getPos().getY() == yPos && pos.getPos().getZ() == zPos && pos.getDimension().getLocation().equals(dim);
	}

	/**
	 * @param pos The GlobalPos to use
	 * @return A formatted string of the GlobalPos' location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public static String toNBTString(GlobalPos pos)
	{
		return pos.getPos().getX() + " " + pos.getPos().getY() + " " + pos.getPos().getZ() + " " + pos.getDimension().getLocation();
	}
}