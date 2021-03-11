package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClearLoggerClient implements IMessage
{
	private BlockPos pos;

	public ClearLoggerClient() {}

	public ClearLoggerClient(BlockPos pos)
	{
		this.pos = pos;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
	}

	public static class Handler implements IMessageHandler<ClearLoggerClient,IMessage>
	{
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(ClearLoggerClient message, MessageContext context)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				EntityPlayer player = Minecraft.getMinecraft().player;
				TileEntityLogger te = (TileEntityLogger) player.world.getTileEntity(message.pos);

				if(te != null)
					te.players = new String[100];
			});

			return null;
		}
	}
}
