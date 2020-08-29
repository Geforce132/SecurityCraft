package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCToggleBlockPocketManager implements IMessage
{
	private BlockPos pos;
	private int dimension, size;
	private boolean enabling;

	public PacketCToggleBlockPocketManager() {}

	public PacketCToggleBlockPocketManager(TileEntityBlockPocketManager te, boolean enabling, int size)
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

	public static class Handler implements IMessageHandler<PacketCToggleBlockPocketManager, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCToggleBlockPocketManager message, MessageContext ctx)
		{
			TileEntity te = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension).getTileEntity(message.pos);

			if(te instanceof TileEntityBlockPocketManager)
			{
				((TileEntityBlockPocketManager)te).size = message.size;

				if(message.enabling)
					((TileEntityBlockPocketManager)te).enableMultiblock();
				else
					((TileEntityBlockPocketManager)te).disableMultiblock();
			}
			return null;
		}
	}
}
