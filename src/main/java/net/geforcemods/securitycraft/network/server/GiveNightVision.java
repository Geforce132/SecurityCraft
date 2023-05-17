package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;

public class GiveNightVision {
	public GiveNightVision() {}

	public GiveNightVision(FriendlyByteBuf buf) {}

	public void encode(FriendlyByteBuf buf) {}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (PlayerUtils.isPlayerMountedOnCamera(ctx.get().getSender()))
			ctx.get().getSender().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3, -1, false, false));
	}
}
