package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.network.NetworkEvent;

public class GiveNightVision {
	public GiveNightVision() {}

	public GiveNightVision(PacketBuffer buf) {}

	public void encode(PacketBuffer buf) {}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (PlayerUtils.isPlayerMountedOnCamera(ctx.get().getSender()))
			ctx.get().getSender().addEffect(new EffectInstance(Effects.NIGHT_VISION, 3, -1, false, false));
	}
}
