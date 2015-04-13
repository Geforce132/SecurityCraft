package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketMountCamera implements IMessage{
	
	private int x, y, z;
	
	public PacketMountCamera(){
		
	}
	
	public PacketMountCamera(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketMountCamera, IMessage> {

	public IMessage onMessage(PacketMountCamera packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		EntityPlayer player = context.getServerHandler().playerEntity;
	
		if(getWorld(player).getBlock(x, y, z) == mod_SecurityCraft.securityCamera){
			((BlockSecurityCamera) getWorld(player).getBlock(x, y, z)).mountCamera(getWorld(player), x, y, z, player);
		}
		
		return null;
	}
}
	
}
