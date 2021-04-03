package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class AssembleBlockPocket
{
	private BlockPos pos;
	private int dimension, size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerTileEntity te, int size)
	{
		pos = te.getPos();
		dimension = te.getWorld().getDimension().getType().getId();
		this.size = size;
	}

	public static void encode(AssembleBlockPocket message, PacketBuffer buf)
	{
		buf.writeLong(message.pos.toLong());
		buf.writeInt(message.size);
	}

	public static AssembleBlockPocket decode(PacketBuffer buf)
	{
		AssembleBlockPocket message = new AssembleBlockPocket();

		message.pos = BlockPos.fromLong(buf.readLong());
		message.size = buf.readInt();
		return message;
	}

	public static void onMessage(AssembleBlockPocket message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(message.dimension)).getTileEntity(message.pos);

			if(te instanceof BlockPocketManagerTileEntity && ((BlockPocketManagerTileEntity)te).getOwner().isOwner(ctx.get().getSender()))
			{
				((BlockPocketManagerTileEntity)te).size = message.size;
				((BlockPocketManagerTileEntity)te).autoAssembleMultiblock();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
