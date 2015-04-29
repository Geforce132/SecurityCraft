package org.freeforums.geforce.securitycraft.network.packets;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.client.Minecraft;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketCSetCameraUsePosition implements IMessage {
	
	private double posX;
	private double posY;
	private double posZ;
	private float rotationYaw;
	private float rotationPitch;
	
	public PacketCSetCameraUsePosition(){
		
	}

	public PacketCSetCameraUsePosition(double posX, double posY, double posZ, float rotationYaw, float rotationPitch) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.rotationYaw = rotationYaw;
		this.rotationPitch = rotationPitch;
	}

	public void fromBytes(ByteBuf buf) {
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
		this.rotationYaw = buf.readFloat();
		this.rotationPitch = buf.readFloat();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationPitch);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCSetCameraUsePosition, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCSetCameraUsePosition packet, MessageContext ctx) {
		mod_SecurityCraft.instance.setUsePosition(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), packet.posX, packet.posY, packet.posZ, packet.rotationYaw, packet.rotationPitch);
		return null;
	}
		
}

}
