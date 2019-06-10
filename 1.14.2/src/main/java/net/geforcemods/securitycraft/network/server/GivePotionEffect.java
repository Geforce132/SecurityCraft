package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
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

	public void fromBytes(ByteBuf buf) {
		potionID = buf.readInt();
		duration = buf.readInt();
		amplifier = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(potionID);
		buf.writeInt(duration);
		buf.writeInt(amplifier);
	}

	public static void encode(GivePotionEffect message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static GivePotionEffect decode(PacketBuffer packet)
	{
		GivePotionEffect message = new GivePotionEffect();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(GivePotionEffect message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> ctx.get().getSender().addPotionEffect(new EffectInstance(Effect.getPotionById(message.potionID), message.duration, message.amplifier, false, true)));
		ctx.get().setPacketHandled(true);
	}

}
