package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.network.client.UpdateTEOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

//TODO: remove dimension
public class RequestTEOwnableUpdate
{
	private BlockPos pos;
	private int dimension;

	public RequestTEOwnableUpdate() {}

	/**
	 * Initializes this packet with a tile entity
	 * @param te The tile entity to initialize with
	 */
	public RequestTEOwnableUpdate(TileEntityOwnable te)
	{
		this(te.getPos(), te.getWorld().dimension.getType().getId());
	}

	/**
	 * Initializes this packet
	 * @param p The position of the tile entity
	 * @param dim The dimension it is in
	 */
	public RequestTEOwnableUpdate(BlockPos p, int dim)
	{
		pos = p;
		dimension = dim;
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
	}

	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
	}

	public static void encode(RequestTEOwnableUpdate message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static RequestTEOwnableUpdate decode(PacketBuffer packet)
	{
		RequestTEOwnableUpdate message = new RequestTEOwnableUpdate();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(RequestTEOwnableUpdate message, Supplier<NetworkEvent.Context> ctx)
	{
		TileEntity te = ctx.get().getSender().world.getTileEntity(message.pos);
		boolean customizable = te instanceof CustomizableSCTE;
		NBTTagCompound tag = customizable ? ((CustomizableSCTE)te).write(new NBTTagCompound()) : null;

		if(te != null && te instanceof IOwnable)
			return new UpdateTEOwnable(te.getPos(), ((IOwnable)te).getOwner().getName(), ((IOwnable)te).getOwner().getUUID(), customizable, tag);

		ctx.get().setPacketHandled(true);
	}
}
