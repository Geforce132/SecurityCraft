package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;

import org.freeforums.geforce.securitycraft.main.Utils.ClientUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketCSetCameraZoom implements IMessage{
	
	private double zoom;
	
	public PacketCSetCameraZoom(){
		
	}
	
	public PacketCSetCameraZoom(double par1){
		this.zoom = par1;
	}

	public void fromBytes(ByteBuf buf) {
		this.zoom = buf.readDouble();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.zoom);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCSetCameraZoom, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCSetCameraZoom packet, MessageContext ctx) {
		ClientUtils.setCameraZoom(packet.zoom - 1D);
		return null;
	}
	
}

}
