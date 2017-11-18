package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCRequestTEOwnableUpdate implements IMessage
{
	private BlockPos pos;
	private int dimension;

	public PacketCRequestTEOwnableUpdate() {}

	/**
	 * Initializes this packet with a tile entity
	 * @param te The tile entity to initialize with
	 */
	public PacketCRequestTEOwnableUpdate(TileEntityOwnable te)
	{
		this(te.getPos(), te.getWorld().provider.getDimension());
	}

	/**
	 * Initializes this packet
	 * @param p The position of the tile entity
	 * @param dim The dimension it is in
	 */
	public PacketCRequestTEOwnableUpdate(BlockPos p, int dim)
	{
		pos = p;
		dimension = dim;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
	}

	public static class Handler implements IMessageHandler<PacketCRequestTEOwnableUpdate, PacketSUpdateTEOwnable>
	{
		@Override
		public PacketSUpdateTEOwnable onMessage(PacketCRequestTEOwnableUpdate message, MessageContext ctx)
		{
			TileEntityOwnable te = (TileEntityOwnable)FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension).getTileEntity(message.pos);

			if(te != null)
				return new PacketSUpdateTEOwnable(te);
			else
				return null;
		}
	}
}
