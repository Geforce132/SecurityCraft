package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketMountCamera implements IMessage{
	
	private int x, y, z, id;
	
	public PacketMountCamera(){
		
	}
	
	public PacketMountCamera(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(id);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.id = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketMountCamera, IMessage> {

	public IMessage onMessage(PacketMountCamera packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		int id = packet.id;
		EntityPlayerMP player = context.getServerHandler().playerEntity;
	
		if(getWorld(player).getBlock(x, y, z) instanceof BlockSecurityCamera){
			((BlockSecurityCamera) getWorld(player).getBlock(x, y, z)).mountCamera(getWorld(player), x, y, z, id, player);
		}
		
		return null;
	}
}
	
}
