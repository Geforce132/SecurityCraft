package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityReinforcedDoor;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateClient implements IMessage{
	
	private int x, y, z;
	private String username;
	
	public PacketUpdateClient(){
		
	}
	
	public PacketUpdateClient(int x, int y, int z, String username){
		this.x = x;
		this.y = y;
		this.z = z;
		this.username = username;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, username);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		ByteBufUtils.readUTF8String(par1ByteBuf);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketUpdateClient, IMessage> {

	public IMessage onMessage(PacketUpdateClient packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		String username = packet.username;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

		if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null && getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityReinforcedDoor){
			((TileEntityReinforcedDoor) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).setOwner(username);
		}
		
		return null;
	}
}
	
}
