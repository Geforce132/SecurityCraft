package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class AssembleBlockPocket
{
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerTileEntity te, int size)
	{
		pos = te.getPos();
		this.size = size;
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		size = buf.readInt();
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(size);
	}

	public static void encode(AssembleBlockPocket message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static AssembleBlockPocket decode(PacketBuffer packet)
	{
		AssembleBlockPocket message = new AssembleBlockPocket();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(AssembleBlockPocket message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = ctx.get().getSender().world.getTileEntity(message.pos);

			if(te instanceof BlockPocketManagerTileEntity)
			{
				((BlockPocketManagerTileEntity)te).size = message.size;
				((BlockPocketManagerTileEntity)te).autoAssembleMultiblock(ctx.get().getSender());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
