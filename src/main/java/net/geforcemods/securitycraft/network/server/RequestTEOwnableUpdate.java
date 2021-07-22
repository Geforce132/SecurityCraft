package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.network.client.UpdateTEOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
		this(te.getBlockPos());
	}

	/**
	 * Initializes this packet
	 * @param p The position of the tile entity
	 */
	public RequestTEOwnableUpdate(BlockPos p)
	{
		pos = p;
	}

	public static void encode(RequestTEOwnableUpdate message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
	}

	public static RequestTEOwnableUpdate decode(FriendlyByteBuf buf)
	{
		RequestTEOwnableUpdate message = new RequestTEOwnableUpdate();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(RequestTEOwnableUpdate message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockEntity te = ctx.get().getSender().level.getBlockEntity(message.pos);
			boolean syncTag = te instanceof CustomizableTileEntity || te instanceof IPasswordProtected;
			CompoundTag tag = syncTag ? te.save(new CompoundTag()) : null;

			if(te instanceof IOwnable)
				SecurityCraft.channel.reply(new UpdateTEOwnable(te.getBlockPos(), ((IOwnable)te).getOwner().getName(), ((IOwnable)te).getOwner().getUUID(), syncTag, tag), ctx.get());
		});

		ctx.get().setPacketHandled(true);
	}
}
