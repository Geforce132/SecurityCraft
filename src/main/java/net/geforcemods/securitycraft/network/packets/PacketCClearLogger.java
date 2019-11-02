package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketCClearLogger implements IMessage
{
	private int x, y, z;

	public PacketCClearLogger() {}

	public PacketCClearLogger(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCClearLogger,IMessage>
	{
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCClearLogger message, MessageContext context)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			TileEntityLogger te = (TileEntityLogger) getClientWorld(player).getTileEntity(message.x, message.y, message.z);

			if(te != null)
				te.players = new String[100];

			return null;
		}
	}
}
