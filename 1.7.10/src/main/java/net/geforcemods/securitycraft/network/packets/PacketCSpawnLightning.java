package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;

public class PacketCSpawnLightning implements IMessage{
	
	private double x, y, z;
	
	public PacketCSpawnLightning(){
		
	}
	
	public PacketCSpawnLightning(double par1, double par2, double par3){
		this.x = par1;
		this.y = par2;
		this.z = par3;
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCSpawnLightning, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCSpawnLightning packet, MessageContext ctx) {
		EntityLightningBolt lightning = new EntityLightningBolt(Minecraft.getMinecraft().theWorld, packet.x, packet.y, packet.z);
		Minecraft.getMinecraft().theWorld.addWeatherEffect(lightning);
		
		return null;
	}
	
}

}
