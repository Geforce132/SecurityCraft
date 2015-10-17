package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketCSetPlayerPositionAndRotation implements IMessage{
	
	private double x, y, z;
	private float rotationYaw, rotationPitch;
	
	public PacketCSetPlayerPositionAndRotation(){
		
	}
	
	public PacketCSetPlayerPositionAndRotation(double par1, double par2, double par3, float par4, float par5){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.rotationYaw = par4;
		this.rotationPitch = par5;
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.rotationYaw = buf.readFloat();
		this.rotationPitch = buf.readFloat();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeFloat(this.rotationYaw);
		buf.writeFloat(this.rotationPitch);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCSetPlayerPositionAndRotation, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCSetPlayerPositionAndRotation message, MessageContext ctx) {
		Minecraft.getMinecraft().thePlayer.setPositionAndRotation(message.x, message.y, message.z, message.rotationYaw, message.rotationPitch);
		return null;
	}
	
}

}
