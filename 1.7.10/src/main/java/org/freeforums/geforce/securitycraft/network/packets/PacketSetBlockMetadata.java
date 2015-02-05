package org.freeforums.geforce.securitycraft.network.packets;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetBlockMetadata implements IMessage{
	
	private int x, y, z;
	private int blockMetadata;
	private boolean shouldUpdateBlock;
	private int amountOfTicks;
	private String extraOwnerUUID, extraOwnerName;
	
	public PacketSetBlockMetadata(){
		
	}

	public PacketSetBlockMetadata(int x, int y, int z, int meta, boolean shouldUpdate, int amountOfTicks, String extraOwnerUUID, String extraOwnerName){
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockMetadata = meta;
		this.shouldUpdateBlock = shouldUpdate;
		this.amountOfTicks = amountOfTicks;
		this.extraOwnerUUID = extraOwnerUUID;
		this.extraOwnerName = extraOwnerName;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(blockMetadata);
		par1ByteBuf.writeBoolean(shouldUpdateBlock);
		par1ByteBuf.writeInt(amountOfTicks);
		ByteBufUtils.writeUTF8String(par1ByteBuf, extraOwnerUUID);
		ByteBufUtils.writeUTF8String(par1ByteBuf, extraOwnerName);

	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.blockMetadata = par1ByteBuf.readInt();
		this.shouldUpdateBlock = par1ByteBuf.readBoolean();
		this.amountOfTicks = par1ByteBuf.readInt();
	    this.extraOwnerUUID = ByteBufUtils.readUTF8String(par1ByteBuf);
	    this.extraOwnerName = ByteBufUtils.readUTF8String(par1ByteBuf);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlockMetadata, IMessage> { 
 
	public IMessage onMessage(PacketSetBlockMetadata packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		int blockMetadata = packet.blockMetadata;
		boolean shouldUpdateBlock = packet.shouldUpdateBlock;
		int amountOfTicks = packet.amountOfTicks;
		String extraOwnerUUID = packet.extraOwnerUUID;
		String extraOwnerName = packet.extraOwnerName;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

		getWorld(par1EntityPlayer).setBlockMetadataWithNotify(x, y, z, blockMetadata, 3);
		
		if(!extraOwnerUUID.isEmpty() && !extraOwnerName.isEmpty() && getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null){
			if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityOwnable){
				((TileEntityOwnable) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).setOwner(extraOwnerUUID, extraOwnerName);
			}
		}
		
		if(shouldUpdateBlock){
			getWorld(par1EntityPlayer).scheduleBlockUpdate(x, y, z, getWorld(par1EntityPlayer).getBlock(x, y, z), amountOfTicks);
		}		return null;
	}
}

}
