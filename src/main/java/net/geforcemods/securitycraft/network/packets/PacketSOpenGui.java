package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
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
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSOpenGui, IMessage> {

		@Override
		public IMessage onMessage(PacketSOpenGui packet, MessageContext context) {
			int id = packet.id;
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			EntityPlayerMP player = context.getServerHandler().playerEntity;

			player.openGui(SecurityCraft.instance, id, getWorld(player), x, y, z);
			return null;
		}
	}

}