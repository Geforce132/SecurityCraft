package org.freeforums.geforce.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PacketSSyncTENBTTag implements IMessage{
	
	private int x, y, z;
	private NBTTagCompound tag;
	
	public PacketSSyncTENBTTag(){
		
	}
	
	public PacketSSyncTENBTTag(int par1, int par2, int par3, NBTTagCompound par4NBTTagCompound){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.tag = par4NBTTagCompound;
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.tag = ByteBufUtils.readTag(buf);
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeTag(buf, this.tag);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSSyncTENBTTag, IMessage> {

	public IMessage onMessage(PacketSSyncTENBTTag packet, MessageContext ctx) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		NBTTagCompound tag = packet.tag;
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		
		if(getWorld(player).getTileEntity(x, y, z) != null){
			getWorld(player).getTileEntity(x, y, z).readFromNBT(tag);
		}
		
		return null;
	}
	
}

}
