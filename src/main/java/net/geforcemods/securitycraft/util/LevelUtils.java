package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class LevelUtils {
	/**
	 * Correctly schedules a task for execution on the main thread depending on if the provided world is client- or
	 * serverside
	 */
	public static void addScheduledTask(IWorld w, Runnable r) {
		if (w.isClientSide())
			Minecraft.getInstance().execute(r);
		else
			ServerLifecycleHooks.getCurrentServer().execute(r);
	}

	/**
	 * Checks to see if the given coordinates are the same as the given GlobalPos' coordinates.
	 *
	 * @param pos The GlobalPos to check against
	 * @param coordinates a String[] which contains the x, y, and z coordinates, and the dimension ID of the view
	 * @return true if the x, y, z and dimension match, false otherwise
	 */
	public static boolean checkCoordinates(GlobalPos pos, String[] coordinates) {
		int xPos = Integer.parseInt(coordinates[0]);
		int yPos = Integer.parseInt(coordinates[1]);
		int zPos = Integer.parseInt(coordinates[2]);
		int dim = coordinates.length == 4 ? Integer.parseInt(coordinates[3]) : 0;

		return pos.pos().getX() == xPos && pos.pos().getY() == yPos && pos.pos().getZ() == zPos && pos.dimension().getId() == dim;
	}

	/**
	 * @param pos The GlobalPos to use
	 * @return A formatted string of the GlobalPos' location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public static String toNBTString(GlobalPos pos) {
		return pos.pos().getX() + " " + pos.pos().getY() + " " + pos.pos().getZ() + " " + pos.dimension().getId();
	}
}