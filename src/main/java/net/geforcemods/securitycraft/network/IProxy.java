package net.geforcemods.securitycraft.network;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IProxy {
	public default void registerEntityRenderingHandlers() {}

	public default void registerRenderThings() {}

	public default void registerVariants() {}

	public default EntityPlayer getClientPlayer() {
		return null;
	}

	public default World getClientLevel() {
		return null;
	}

	public default void updateBlockColorAroundPosition(BlockPos pos) {}

	public default void addEffect(IParticleFactory factory, World level, double x, double y, double z) {}
}
