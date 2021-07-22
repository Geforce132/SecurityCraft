package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.network.NetworkEvent;

public class GiveNightVision
{
	public GiveNightVision() {}

	public static void encode(GiveNightVision message, PacketBuffer buf) {}

	public static GiveNightVision decode(PacketBuffer buf)
	{
		return new GiveNightVision();
	}

	public static void onMessage(GiveNightVision message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			if(PlayerUtils.isPlayerMountedOnCamera(ctx.get().getSender()))
				ctx.get().getSender().addEffect(new EffectInstance(Effects.NIGHT_VISION, 3, -1, false, false));
		});
		ctx.get().setPacketHandled(true);
	}
}
