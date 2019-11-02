package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.minecraft.tileentity.TileEntity;

public class PacketCToggleBlockPocketManager implements cpw.mods.fml.common.network.simpleimpl.IMessage
{
	private int x, y, z, dimension, size;
	private boolean enabling;

	public PacketCToggleBlockPocketManager() {}

	public PacketCToggleBlockPocketManager(TileEntityBlockPocketManager te, boolean enabling, int size)
	{
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
		dimension = te.getWorld().provider.dimensionId;
		this.enabling = enabling;
		this.size = size;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		dimension = buf.readInt();
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(dimension);
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public static class Handler implements IMessageHandler<PacketCToggleBlockPocketManager, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCToggleBlockPocketManager message, MessageContext ctx)
		{
			TileEntity te = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.dimension).getTileEntity(message.x, message.y, message.z);

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
