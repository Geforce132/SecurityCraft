package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSetBlock implements IMessage{

	private int x, y, z;
	private int metadata = -1;
	private String blockID;

	public PacketSetBlock(){

	}

	public PacketSetBlock(int x, int y, int z, String id){
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = id;
	}

	public PacketSetBlock(int x, int y, int z, String id, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = id;
		metadata = meta;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, blockID);
		if(metadata != -1)
			buf.writeInt(metadata);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		blockID = ByteBufUtils.readUTF8String(buf);
		if(metadata != -1)
			metadata = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlock, IMessage> {

		@Override
		public IMessage onMessage(PacketSetBlock packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			String blockID = packet.blockID;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			Block block = (Block)Block.blockRegistry.getObject(blockID);
			if(blockID != 55) // see this video for see the usebug https://youtu.be/0X2ZmO8-EM4?t=397
				return null;
			else
			if(packet.metadata != -1)
				getWorld(par1EntityPlayer).setBlock(x, y, z, block, packet.metadata, 3);
			else
				getWorld(par1EntityPlayer).setBlock(x, y, z, block);

			return null;
		}
	}

}
