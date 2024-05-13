package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleNightVision {
	public ToggleNightVision() {}

	public ToggleNightVision(PacketBuffer buf) {}

	public void encode(PacketBuffer buf) {}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();

		if (ConfigHandler.SERVER.allowCameraNightVision.get() && PlayerUtils.isPlayerMountedOnCamera(player)) {
			if (player.hasEffect(Effects.NIGHT_VISION)) {
				if (player.getEffect(Effects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
					player.removeEffect(Effects.NIGHT_VISION);
			}
			else
				player.addEffect(new CameraNightVisionEffectInstance());
		}
	}
}
