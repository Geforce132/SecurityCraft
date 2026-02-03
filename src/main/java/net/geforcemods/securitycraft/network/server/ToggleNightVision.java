package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleNightVision() implements CustomPacketPayload {
	public static final Type<ToggleNightVision> TYPE = new Type<>(SecurityCraft.resLoc("toggle_night_vision"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleNightVision> STREAM_CODEC = StreamCodec.unit(new ToggleNightVision());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (ConfigHandler.SERVER.allowCameraNightVision.get() && PlayerUtils.isPlayerMountedOnCamera(player)) {
			if (player.hasEffect(MobEffects.NIGHT_VISION)) {
				if (player.getEffect(MobEffects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
					player.removeEffect(MobEffects.NIGHT_VISION);
			}
			else
				player.addEffect(new CameraNightVisionEffectInstance());
		}
	}
}
