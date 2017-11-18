package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSOpenGui implements IMessage {

	private int id;
	private int x;
	private int y;
	private int z;

	public PacketSOpenGui(){}

	public PacketSOpenGui(int id, int x, int y, int z){
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(id);
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		id = par1ByteBuf.readInt();
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSOpenGui, IMessage> {

		@Override
		public IMessage onMessage(PacketSOpenGui packet, MessageContext context) {
			int id = packet.id;
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			EntityPlayerMP player = context.getServerHandler().playerEntity;

			player.openGui(mod_SecurityCraft.instance, id, getWorld(player), x, y, z);
			return null;
		}
	}

}