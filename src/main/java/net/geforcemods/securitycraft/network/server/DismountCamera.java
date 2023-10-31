package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

public class DismountCamera {
	public DismountCamera() {}

	public DismountCamera(FriendlyByteBuf buf) {}

	public void encode(FriendlyByteBuf buf) {}

	public void handle(NetworkEvent.Context ctx) {
		ServerPlayer player = ctx.getSender();

		if (player.getCamera() instanceof SecurityCamera cam)
			cam.stopViewing(player);
	}
}
