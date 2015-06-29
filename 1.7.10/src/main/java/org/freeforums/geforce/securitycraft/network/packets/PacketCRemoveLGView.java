package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;

import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketCRemoveLGView implements IMessage{
	
	private int camX, camY, camZ;
	
	public PacketCRemoveLGView(){
		
	}
	
	public PacketCRemoveLGView(int camX, int camY, int camZ){
		this.camX = camX;
		this.camY = camY;
		this.camZ = camZ;
	}

	public void fromBytes(ByteBuf buf) {
		this.camX = buf.readInt();
		this.camY = buf.readInt();
		this.camZ = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.camX);
		buf.writeInt(this.camY);
		buf.writeInt(this.camZ);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCRemoveLGView, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCRemoveLGView packet, MessageContext ctx) {
		System.out.println("Removing view at coords" + Utils.getFormattedCoordinates(packet.camX, packet.camY, packet.camZ));
		
		if(mod_SecurityCraft.instance.hasViewForCoords(packet.camX + " " + packet.camY + " " + packet.camZ)){
			mod_SecurityCraft.instance.getViewFromCoords(packet.camX + " " + packet.camY + " " + packet.camZ).release();
			mod_SecurityCraft.instance.removeViewForCoords(packet.camX + " " + packet.camY + " " + packet.camZ);
		}
		
		return null;
	}
	
}

}
