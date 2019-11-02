package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGivePotionEffect implements IMessage{

	private int potionId, duration, amplifier;

	public PacketGivePotionEffect(){

	}

	public PacketGivePotionEffect(int potionId, int duration, int amplifier){
		this.potionId = potionId;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		potionId = buf.readInt();
		duration = buf.readInt();
		amplifier = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(potionId);
		buf.writeInt(duration);
		buf.writeInt(amplifier);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketGivePotionEffect, IMessage> {

		@Override
		public IMessage onMessage(PacketGivePotionEffect message, MessageContext ctx) {
			ctx.getServerHandler().playerEntity.addPotionEffect(new PotionEffect(message.potionId, message.duration, message.amplifier, false, true));
			return null;
		}

	}

}
