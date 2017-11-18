package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSetBlockAndMetadata implements IMessage{

	private int x, y, z;
	private int metadata = -1;
	private String blockID;

	public PacketSetBlockAndMetadata(){

	}

	public PacketSetBlockAndMetadata(int x, int y, int z, String id, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = id;
		metadata = meta;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, blockID);
		par1ByteBuf.writeInt(metadata);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
		blockID = ByteBufUtils.readUTF8String(par1ByteBuf);
		metadata = par1ByteBuf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlockAndMetadata, IMessage> {

		@Override
		public IMessage onMessage(PacketSetBlockAndMetadata packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			String blockID = packet.blockID;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			Block block = (Block)Block.blockRegistry.getObject(blockID);
			getWorld(par1EntityPlayer).setBlock(x, y, z, block, packet.metadata, 3);

			return null;
		}
	}

}
