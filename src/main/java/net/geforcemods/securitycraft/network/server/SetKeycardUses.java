package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.containers.ContainerKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetKeycardUses implements IMessage
{
	private BlockPos pos;
	private int uses;

	public SetKeycardUses() {}

	public SetKeycardUses(BlockPos pos, int uses)
	{
		this.pos = pos;
		this.uses = uses;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeVarInt(buf, uses, 5);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		uses = ByteBufUtils.readVarInt(buf, 5);
	}

	public static class Handler implements IMessageHandler<SetKeycardUses, IMessage>
	{
		@Override
		public IMessage onMessage(SetKeycardUses message, MessageContext context)
		{
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				BlockPos pos = message.pos;
				EntityPlayer player = context.getServerHandler().player;
				TileEntity tile = player.world.getTileEntity(pos);

				if(tile instanceof TileEntityKeycardReader)
				{
					TileEntityKeycardReader te = (TileEntityKeycardReader)tile;

					if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
					{
						Container container = player.openContainer;

						if(container instanceof ContainerKeycardReader)
							((ContainerKeycardReader)container).setKeycardUses(message.uses);
					}
				}
			});
			return null;
		}
	}
}
