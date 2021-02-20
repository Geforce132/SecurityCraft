package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldUtils{

	/**
	 * Correctly schedules a task for execution on the main thread depending on if the
	 * provided world is client- or serverside
	 */
	public static void addScheduledTask(World w, Runnable r)
	{
		if(w.isRemote) //clientside
			Minecraft.getMinecraft().addScheduledTask(r);
		else //serverside
			((WorldServer)w).addScheduledTask(r);
	}
}