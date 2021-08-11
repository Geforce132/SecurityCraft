package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class WorldUtils{

	/**
	 * Correctly schedules a task for execution on the main thread depending on if the
	 * provided world is client- or serverside
	 */
	public static void addScheduledTask(LevelAccessor w, Runnable r)
	{
		if(w.isClientSide()) //clientside
			Minecraft.getInstance().execute(r);
		else //serverside
			ServerLifecycleHooks.getCurrentServer().execute(r);
	}

	public static void spawnLightning(Level world, Vec3 pos, boolean effectOnly)
	{
		world.addFreshEntity(createLightning(world, pos, effectOnly));
	}

	public static LightningBolt createLightning(Level world, Vec3 pos, boolean effectOnly)
	{
		LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);

		lightning.moveTo(pos);
		lightning.setVisualOnly(effectOnly);
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

		return pos.pos().getX() == xPos && pos.pos().getY() == yPos && pos.pos().getZ() == zPos && pos.dimension().location().equals(dim);
	}

	/**
	 * @param pos The GlobalPos to use
	 * @return A formatted string of the GlobalPos' location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public static String toNBTString(GlobalPos pos)
	{
		return pos.pos().getX() + " " + pos.pos().getY() + " " + pos.pos().getZ() + " " + pos.dimension().location();
	}
}