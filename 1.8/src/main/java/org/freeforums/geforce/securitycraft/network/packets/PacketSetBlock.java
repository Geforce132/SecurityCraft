package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetBlock implements IMessage{
	
	private int x, y, z;
	private String blockID;
	
	public PacketSetBlock(){
		
	}
	
	public PacketSetBlock(int x, int y, int z, String id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockID = id;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, blockID);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.blockID = ByteBufUtils.readUTF8String(par1ByteBuf);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlock, IMessage> {

	public IMessage onMessage(PacketSetBlock packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		String blockID = packet.blockID;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;
	
		Block block = (Block)Block.blockRegistry.getObject(blockID);
		getWorld(par1EntityPlayer).setBlockState(new BlockPos(x, y, z), block.getDefaultState());
		
		return null;
	}
}
	
}
