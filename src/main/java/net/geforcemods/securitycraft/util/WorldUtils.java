package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.IWorld;
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
}