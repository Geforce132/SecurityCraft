package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;

public class ToggleNightVision {
	public ToggleNightVision() {}

	public ToggleNightVision(FriendlyByteBuf buf) {}

	public void encode(FriendlyByteBuf buf) {}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();

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
