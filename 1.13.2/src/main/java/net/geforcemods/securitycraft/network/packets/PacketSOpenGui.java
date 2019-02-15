package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

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
		public IMessage onMessage(PacketSOpenGui message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				int id = message.id;
				int x = message.x;
				int y = message.y;
				int z = message.z;
				EntityPlayerMP player = context.getServerHandler().player;

				player.openGui(SecurityCraft.instance, id, getWorld(player), x, y, z);
			});

			return null;
		}
	}

}