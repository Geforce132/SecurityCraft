package net.geforcemods.securitycraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
}