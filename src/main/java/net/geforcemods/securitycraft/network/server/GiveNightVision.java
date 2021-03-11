package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.network.NetworkEvent;

public class GivePotionEffect {

	private int potionID, duration, amplifier;

	public GivePotionEffect(){

	}

	public GivePotionEffect(int potionID, int duration, int amplifier){
		this.potionID = potionID;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	public static void encode(GivePotionEffect message, PacketBuffer buf)
	{
		buf.writeInt(message.potionID);
		buf.writeInt(message.duration);
		buf.writeInt(message.amplifier);
	}

	public static GivePotionEffect decode(PacketBuffer buf)
	{
		GivePotionEffect message = new GivePotionEffect();

		message.potionID = buf.readInt();
		message.duration = buf.readInt();
		message.amplifier = buf.readInt();
		return message;
	}

	public static void onMessage(GivePotionEffect message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> ctx.get().getSender().addPotionEffect(new EffectInstance(Effect.get(message.potionID), message.duration, message.amplifier, false, true)));
		ctx.get().setPacketHandled(true);
	}

}
