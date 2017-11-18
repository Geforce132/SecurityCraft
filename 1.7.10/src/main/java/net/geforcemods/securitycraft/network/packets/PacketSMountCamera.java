package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketSMountCamera implements IMessage {

	private int x;
	private int y;
	private int z;
	private int id;

	public PacketSMountCamera(){}

	public PacketSMountCamera(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(id);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
		id = par1ByteBuf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSMountCamera, IMessage> {

		@Override
		public IMessage onMessage(PacketSMountCamera packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			int id = packet.id;
			EntityPlayerMP player = context.getServerHandler().playerEntity;

			if((getWorld(player).getBlock(x, y, z) instanceof BlockSecurityCamera))
				((BlockSecurityCamera)getWorld(player).getBlock(x, y, z)).mountCamera(getWorld(player), x, y, z, id, player);

			return null;
		}
	}

}