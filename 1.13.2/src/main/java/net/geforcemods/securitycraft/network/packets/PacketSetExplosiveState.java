package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSetExplosiveState implements IMessage{

	private int x, y, z;
	private String state;

	public PacketSetExplosiveState(){

	}

	public PacketSetExplosiveState(int x, int y, int z, String state){
		this.x = x;
		this.y = y;
		this.z = z;
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		state = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, state);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetExplosiveState, IMessage> {

		@Override
		public IMessage onMessage(PacketSetExplosiveState message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				EntityPlayer player = context.getServerHandler().player;

				if(BlockUtils.getBlock(getWorld(player), message.x, message.y, message.z) != null && BlockUtils.getBlock(getWorld(player), message.x, message.y, message.z) instanceof IExplosive)
					if(message.state.equalsIgnoreCase("activate"))
						((IExplosive) BlockUtils.getBlock(getWorld(player), message.x, message.y, message.z)).activateMine(getWorld(player), BlockUtils.toPos(message.x, message.y, message.z));
					else if(message.state.equalsIgnoreCase("defuse"))
						((IExplosive) BlockUtils.getBlock(getWorld(player), message.x, message.y, message.z)).defuseMine(getWorld(player), BlockUtils.toPos(message.x, message.y, message.z));
					else if(message.state.equalsIgnoreCase("detonate"))
						((IExplosive) BlockUtils.getBlock(getWorld(player), message.x, message.y, message.z)).explode(getWorld(player), BlockUtils.toPos(message.x, message.y, message.z));
			});

			return null;
		}

	}

}
