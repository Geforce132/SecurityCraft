package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
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
}