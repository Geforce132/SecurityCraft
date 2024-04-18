package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record DismountCamera() implements CustomPacketPayload {
	public static final Type<DismountCamera> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "dismount_camera"));
	public static final StreamCodec<RegistryFriendlyByteBuf, DismountCamera> STREAM_CODEC = StreamCodec.unit(new DismountCamera());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (player instanceof ServerPlayer serverPlayer && serverPlayer.getCamera() instanceof SecurityCamera cam)
			cam.stopViewing(serverPlayer);
	}
}
