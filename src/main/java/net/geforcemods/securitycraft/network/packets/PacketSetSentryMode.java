package net.geforcemods.securitycraft.network.packets;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetSentryMode implements IMessage
{
	public BlockPos pos;
	public int mode;

	public PacketSetSentryMode() {}

	public PacketSetSentryMode(BlockPos sentryPos, int mode)
	{
		pos = sentryPos;
		this.mode = mode;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		mode = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(mode);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetSentryMode, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSetSentryMode message, MessageContext context)
		{
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				EntityPlayer player = context.getServerHandler().player;

				List<EntityCreature> sentries = getWorld(player).<EntityCreature>getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(message.pos));

				if(!sentries.isEmpty())
					((EntitySentry)sentries.get(0)).toggleMode(player, message.mode);
			});

			return null;
		}
	}
}
