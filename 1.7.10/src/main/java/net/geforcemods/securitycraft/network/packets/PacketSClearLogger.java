package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSClearLogger implements IMessage
{
	private int x, y, z;

	public PacketSClearLogger() {}

	public PacketSClearLogger(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSClearLogger,IMessage>
	{
		@Override
		public IMessage onMessage(PacketSClearLogger message, MessageContext context)
		{
			EntityPlayer player = context.getServerHandler().playerEntity;
			TileEntityLogger te = (TileEntityLogger)player.worldObj.getTileEntity(message.x, message.y, message.z);

			if(te != null)
			{
				te.players = new String[100];
				te.sendChangeToClient(true);
			}

			return null;
		}
	}
}
