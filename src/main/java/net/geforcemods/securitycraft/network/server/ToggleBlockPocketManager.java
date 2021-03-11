package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleBlockPocketManager implements IMessage
{
	private BlockPos pos;
	private int dimension, size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(TileEntityBlockPocketManager te, boolean enabling, int size)
	{
		pos = te.getPos();
		dimension = te.getWorld().provider.getDimension();
		this.enabling = enabling;
		this.size = size;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public static class Handler implements IMessageHandler<ToggleBlockPocketManager, IMessage>
	{
		@Override
		public IMessage onMessage(ToggleBlockPocketManager message, MessageContext ctx)
		{
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if(te instanceof TileEntityBlockPocketManager && ((TileEntityBlockPocketManager)te).getOwner().isOwner(player))
				{
					((TileEntityBlockPocketManager)te).size = message.size;

					if(message.enabling)
						((TileEntityBlockPocketManager)te).enableMultiblock();
					else
						((TileEntityBlockPocketManager)te).disableMultiblock();
				}
			});
			return null;
		}
	}
}
