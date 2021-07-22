package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.fml.network.NetworkEvent;

public class GiveNightVision
{
	public GiveNightVision() {}

	public static void encode(GiveNightVision message, FriendlyByteBuf buf) {}

	public static GiveNightVision decode(FriendlyByteBuf buf)
	{
		return new GiveNightVision();
	}

	public static void onMessage(GiveNightVision message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			if(PlayerUtils.isPlayerMountedOnCamera(ctx.get().getSender()))
				ctx.get().getSender().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3, -1, false, false));
		});
		ctx.get().setPacketHandled(true);
	}
}
