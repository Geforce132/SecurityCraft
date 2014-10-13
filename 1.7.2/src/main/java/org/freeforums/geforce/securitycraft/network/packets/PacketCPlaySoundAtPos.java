package org.freeforums.geforce.securitycraft.network.packets;

import net.minecraft.client.Minecraft;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketCPlaySoundAtPos implements IMessage{
	
	private int x, y, z;
	private String sound;
	private double volume;
	
	public PacketCPlaySoundAtPos(){
		
	}
	
	public PacketCPlaySoundAtPos(int par1, int par2, int par3, String par4String, double par5){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.sound = par4String;
		this.volume = par5;
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.sound = ByteBufUtils.readUTF8String(buf);
		this.volume = buf.readDouble();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeUTF8String(buf, this.sound);
		buf.writeDouble(this.volume);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCPlaySoundAtPos, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCPlaySoundAtPos message, MessageContext ctx) {
		Minecraft.getMinecraft().theWorld.playSound((double) message.x,(double) message.y,(double) message.z, message.sound, (float) message.volume, 1.0F, true);
		return null;
	}
	
}

}
