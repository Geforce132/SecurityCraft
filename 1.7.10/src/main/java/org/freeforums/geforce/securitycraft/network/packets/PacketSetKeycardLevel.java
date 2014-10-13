package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetKeycardLevel implements IMessage{
	
	private int x, y, z, level;
	
	public PacketSetKeycardLevel(){
		
	}
	
	public PacketSetKeycardLevel(int x, int y, int z, int level){
		this.x = x;
		this.y = y;
		this.z = z;
		this.level = level;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(level);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.level = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetKeycardLevel, IMessage> {

	public IMessage onMessage(PacketSetKeycardLevel packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		int level = packet.level;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

		((TileEntityKeycardReader) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).setPassLV(level);
		
		return null;
	}
}

	
}
