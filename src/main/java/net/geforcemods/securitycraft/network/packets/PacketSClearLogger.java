package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSClearLogger implements IMessage
{
	private BlockPos pos;

	public PacketSClearLogger() {}

	public PacketSClearLogger(BlockPos pos)
	{
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSClearLogger,IMessage>
	{
		@Override
		public IMessage onMessage(PacketSClearLogger message, MessageContext context)
		{
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				EntityPlayer player = context.getServerHandler().player;
				TileEntityLogger te = (TileEntityLogger)player.world.getTileEntity(message.pos);

				if(te != null)
				{
					te.players = new String[100];
					te.sendChangeToClient(true);
				}
			});

			return null;
		}
	}
}
