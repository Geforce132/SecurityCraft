package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class DismountCamera {
	public DismountCamera() {}

	public DismountCamera(PacketBuffer buf) {}

	public void encode(PacketBuffer buf) {}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();

		if (player.getCamera() instanceof SecurityCamera)
			((SecurityCamera) player.getCamera()).stopViewing(player);
	}
}
