package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSetKeycardLevel implements IMessage{

	private int x, y, z, level;
	private boolean exactCard;

	public PacketSetKeycardLevel(){

	}

	public PacketSetKeycardLevel(int x, int y, int z, int level, boolean exactCard){
		this.x = x;
		this.y = y;
		this.z = z;
		this.level = level;
		this.exactCard  = exactCard;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(level);
		buf.writeBoolean(exactCard);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		level = buf.readInt();
		exactCard = buf.readBoolean();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetKeycardLevel, IMessage> {

		@Override
		public IMessage onMessage(PacketSetKeycardLevel packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			int level = packet.level;
			boolean exactCard = packet.exactCard;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			((TileEntityKeycardReader) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).setPassword(String.valueOf(level));
			((TileEntityKeycardReader) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).setRequiresExactKeycard(exactCard);
			return null;
		}
	}


}
