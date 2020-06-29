package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
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

	public void fromBytes(PacketBuffer buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
		size = buf.readInt();
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
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
			TileEntity te = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(message.dimension)).getTileEntity(message.pos);

			if(te instanceof BlockPocketManagerTileEntity)
			{
				((BlockPocketManagerTileEntity)te).size = message.size;
				((BlockPocketManagerTileEntity)te).autoAssembleMultiblock(ctx.get().getSender());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
