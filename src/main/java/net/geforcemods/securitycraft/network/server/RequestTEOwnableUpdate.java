package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.network.client.UpdateTEOwnable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class RequestTEOwnableUpdate
{
	private BlockPos pos;

	public RequestTEOwnableUpdate() {}

	/**
	 * Initializes this packet with a tile entity
	 * @param te The tile entity to initialize with
	 */
	public RequestTEOwnableUpdate(OwnableTileEntity te)
	{
		this(te.getPos());
	}

	/**
	 * Initializes this packet
	 * @param p The position of the tile entity
	 */
	public RequestTEOwnableUpdate(BlockPos p)
	{
		pos = p;
	}

	public static void encode(RequestTEOwnableUpdate message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
	}

	public static RequestTEOwnableUpdate decode(PacketBuffer buf)
	{
		RequestTEOwnableUpdate message = new RequestTEOwnableUpdate();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(RequestTEOwnableUpdate message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = ctx.get().getSender().world.getTileEntity(message.pos);
			boolean syncTag = te instanceof CustomizableTileEntity || te instanceof IPasswordProtected;
			CompoundNBT tag = syncTag ? te.write(new CompoundNBT()) : null;

			if(te instanceof IOwnable)
				SecurityCraft.channel.reply(new UpdateTEOwnable(te.getPos(), ((IOwnable)te).getOwner().getName(), ((IOwnable)te).getOwner().getUUID(), syncTag, tag), ctx.get());
		});

		ctx.get().setPacketHandled(true);
	}
}
