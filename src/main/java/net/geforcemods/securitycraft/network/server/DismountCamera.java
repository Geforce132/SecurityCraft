package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class DismountCamera implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "dismount_camera");

	public DismountCamera() {}

	public DismountCamera(FriendlyByteBuf buf) {}

	@Override
	public void write(FriendlyByteBuf buf) {}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (player instanceof ServerPlayer serverPlayer && serverPlayer.getCamera() instanceof SecurityCamera cam)
			cam.stopViewing(serverPlayer);
	}
}
