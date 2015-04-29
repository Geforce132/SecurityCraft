package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketCUpdateOwner implements IMessage {
	
	private int x, y, z;
	private String owner, uuid;
	private boolean checkForNull;
	
	public PacketCUpdateOwner(){
		
	}
	
	public PacketCUpdateOwner(int x, int y, int z, String owner, String uuid, boolean checkForNull){
		this.x = x;
		this.y = y;
		this.z = z;
		this.owner = owner;
		this.uuid = uuid;
		this.checkForNull = checkForNull;
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.owner = ByteBufUtils.readUTF8String(buf);
		this.uuid = ByteBufUtils.readUTF8String(buf);
		this.checkForNull = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, owner);
		ByteBufUtils.writeUTF8String(buf, uuid);
		buf.writeBoolean(checkForNull);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCUpdateOwner, IMessage>{

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCUpdateOwner message, MessageContext ctx) {
		if(message.checkForNull && Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z) != null && Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z) instanceof TileEntityOwnable && ((TileEntityOwnable)Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z)).getOwnerUUID() == null){
			((TileEntityOwnable)Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z)).setOwner(message.uuid, message.owner);
		}else if(!message.checkForNull){
			((TileEntityOwnable)Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z)).setOwner(message.uuid, message.owner);
		}
		return null;
	}
	
}

}
