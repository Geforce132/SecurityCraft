package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSSetOwner implements IMessage {

	private int x, y, z;
	private String uuid, name;

	public PacketSSetOwner(){

	}

	public PacketSSetOwner(int x, int y, int z, String uuid, String name){
		this.x = x;
		this.y = y;
		this.z = z;
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		uuid = ByteBufUtils.readUTF8String(buf);
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, uuid);
		ByteBufUtils.writeUTF8String(buf, name);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetOwner, IMessage>{

		@Override
		public IMessage onMessage(PacketSSetOwner packet, MessageContext ctx) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			EntityPlayer player = ctx.getServerHandler().playerEntity;

			if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof IOwnable)
				((IOwnable) getWorld(player).getTileEntity(x, y, z)).getOwner().set(packet.uuid, packet.name);

			return null;
		}

	}

}
