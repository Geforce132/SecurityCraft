package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGivePotionEffect implements IMessage{
	
	private int potionID, duration, amplifier;
	
	public PacketGivePotionEffect(){
		
	}
	
	public PacketGivePotionEffect(int potionID, int duration, int amplifier){
		this.potionID = potionID;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	public void fromBytes(ByteBuf buf) {
		this.potionID = buf.readInt();
		this.duration = buf.readInt();
		this.amplifier = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.potionID);
		buf.writeInt(this.duration);
		buf.writeInt(this.amplifier);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketGivePotionEffect, IMessage> {

	public IMessage onMessage(PacketGivePotionEffect packet, MessageContext ctx) {
		ctx.getServerHandler().playerEntity.addPotionEffect(new PotionEffect(packet.potionID, packet.duration, packet.amplifier, false, true));
		return null;
	}
	
}

}
