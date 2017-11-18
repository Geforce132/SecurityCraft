package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IExplosive;
import net.minecraft.entity.player.EntityPlayer;

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
	public void fromBytes(ByteBuf par1ByteBuf) {
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
		state = ByteBufUtils.readUTF8String(par1ByteBuf);
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, state);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetExplosiveState, IMessage> {

		@Override
		public IMessage onMessage(PacketSetExplosiveState packet, MessageContext context) {
			EntityPlayer player = context.getServerHandler().playerEntity;

			if(getWorld(player).getBlock(packet.x, packet.y, packet.z) != null && getWorld(player).getBlock(packet.x, packet.y, packet.z) instanceof IExplosive)
				if(packet.state.equalsIgnoreCase("activate"))
					((IExplosive) getWorld(player).getBlock(packet.x, packet.y, packet.z)).activateMine(getWorld(player), packet.x, packet.y, packet.z);
				else if(packet.state.equalsIgnoreCase("defuse"))
					((IExplosive) getWorld(player).getBlock(packet.x, packet.y, packet.z)).defuseMine(getWorld(player), packet.x, packet.y, packet.z);
				else if(packet.state.equalsIgnoreCase("detonate"))
					((IExplosive) getWorld(player).getBlock(packet.x, packet.y, packet.z)).explode(getWorld(player), packet.x, packet.y, packet.z);

			return null;
		}

	}

}
