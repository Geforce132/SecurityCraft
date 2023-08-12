package net.geforcemods.securitycraft.network;

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
}
