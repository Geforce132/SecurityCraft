package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSMountCamera implements IMessage {

	private int x;
	private int y;
	private int z;
	private int id;

	public PacketSMountCamera(){

	}

	public PacketSMountCamera(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSMountCamera, IMessage>{
		@Override
		public IMessage onMessage(PacketSMountCamera message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), (() -> {
				int x = message.x;
				int y = message.y;
				int z = message.z;
				int id = message.id;
				EntityPlayerMP player = context.getServerHandler().player;

				if((BlockUtils.getBlock(getWorld(player), BlockUtils.toPos(x, y, z)) instanceof BlockSecurityCamera))
					((BlockSecurityCamera) BlockUtils.getBlock(getWorld(player), x, y, z)).mountCamera(getWorld(player), x, y, z, id, player);
			}));

			return null;
		}
	}

}