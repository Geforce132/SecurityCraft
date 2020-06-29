package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ToggleBlockPocketManager
{
	private BlockPos pos;
	private int dimension, size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerTileEntity te, boolean enabling, int size)
	{
		pos = te.getPos();
		dimension = te.getWorld().getDimension().getType().getId();
		this.enabling = enabling;
		this.size = size;
	}

	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public static void encode(ToggleBlockPocketManager message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static ToggleBlockPocketManager decode(PacketBuffer packet)
	{
		ToggleBlockPocketManager message = new ToggleBlockPocketManager();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(ToggleBlockPocketManager message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(message.dimension)).getTileEntity(message.pos);

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
