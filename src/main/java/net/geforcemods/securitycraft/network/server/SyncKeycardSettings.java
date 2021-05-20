package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.containers.ContainerKeycardReader;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncKeycardSettings implements IMessage
{
	private BlockPos pos;
	private int signature;
	private boolean[] acceptedLevels;
	private boolean link;

	public SyncKeycardSettings() {}

	public SyncKeycardSettings(BlockPos pos, boolean[] acceptedLevels, int signature, boolean link)
	{
		this.pos = pos;
		this.acceptedLevels = acceptedLevels;
		this.signature = signature;
		this.link = link;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(signature);
		buf.writeBoolean(link);

		for(int i = 0; i < 5; i++)
		{
			buf.writeBoolean(acceptedLevels[i]);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		signature = buf.readInt();
		link = buf.readBoolean();
		acceptedLevels = new boolean[5];

		for(int i = 0; i < 5; i++)
		{
			acceptedLevels[i] = buf.readBoolean();
		}
	}

	public static class Handler implements IMessageHandler<SyncKeycardSettings, IMessage>
	{
		@Override
		public IMessage onMessage(SyncKeycardSettings message, MessageContext context)
		{
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				BlockPos pos = message.pos;
				EntityPlayer player = context.getServerHandler().player;
				TileEntity tile = player.world.getTileEntity(pos);

				if(tile instanceof TileEntityKeycardReader)
				{
					TileEntityKeycardReader te = (TileEntityKeycardReader)tile;
					boolean isOwner = te.getOwner().isOwner(player);

					if(isOwner || (te.hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(te.getModule(EnumModuleType.ALLOWLIST)).contains(player.getName().toLowerCase())))
					{
						if(isOwner)
						{
							te.setAcceptedLevels(message.acceptedLevels);
							te.setSignature(message.signature);
						}

						if(message.link)
						{
							Container container = player.openContainer;

							if(container instanceof ContainerKeycardReader)
								((ContainerKeycardReader)container).link();
						}
					}
				}
			});
			return null;
		}
	}
}
