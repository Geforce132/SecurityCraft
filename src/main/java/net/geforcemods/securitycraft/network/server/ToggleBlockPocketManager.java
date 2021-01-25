package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleBlockPocketManager
{
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerTileEntity te, boolean enabling, int size)
	{
		pos = te.getPos();
		this.enabling = enabling;
		this.size = size;
	}

	public static void encode(ToggleBlockPocketManager message, PacketBuffer buf)
	{
		buf.writeLong(message.pos.toLong());
		buf.writeBoolean(message.enabling);
		buf.writeInt(message.size);
	}

	public static ToggleBlockPocketManager decode(PacketBuffer buf)
	{
		ToggleBlockPocketManager message = new ToggleBlockPocketManager();

		message.pos = BlockPos.fromLong(buf.readLong());
		message.enabling = buf.readBoolean();
		message.size = buf.readInt();
		return message;
	}

	public static void onMessage(ToggleBlockPocketManager message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = ctx.get().getSender().world.getTileEntity(message.pos);

			if(te instanceof BlockPocketManagerTileEntity)
			{
				((BlockPocketManagerTileEntity)te).size = message.size;

				if(message.enabling)
					((BlockPocketManagerTileEntity)te).enableMultiblock();
				else
					((BlockPocketManagerTileEntity)te).disableMultiblock();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
