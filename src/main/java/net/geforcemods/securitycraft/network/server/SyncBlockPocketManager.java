package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncBlockPocketManager
{
	private BlockPos pos;
	private int size;
	private boolean showOutline;
	private int autoBuildOffset;

	public SyncBlockPocketManager() {}

	public SyncBlockPocketManager(BlockPos pos, int size, boolean showOutline, int autoBuildOffset)
	{
		this.pos = pos;
		this.size = size;
		this.showOutline = showOutline;
		this.autoBuildOffset = autoBuildOffset;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
		buf.writeVarInt(size);
		buf.writeBoolean(showOutline);
		buf.writeVarInt(autoBuildOffset);
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = buf.readBlockPos();
		size = buf.readVarInt();
		showOutline = buf.readBoolean();
		autoBuildOffset = buf.readVarInt();
	}

	public static void encode(SyncBlockPocketManager message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SyncBlockPocketManager decode(PacketBuffer packet)
	{
		SyncBlockPocketManager message = new SyncBlockPocketManager();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SyncBlockPocketManager message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			World world = ctx.get().getSender().world;
			TileEntity te = world.getTileEntity(pos);

			if(world.isBlockPresent(pos) && te instanceof BlockPocketManagerTileEntity)
			{
				BlockPocketManagerTileEntity bpm = (BlockPocketManagerTileEntity)te;
				BlockState state = world.getBlockState(pos);

				bpm.size = message.size;
				bpm.showOutline = message.showOutline;
				bpm.autoBuildOffset = message.autoBuildOffset;
				world.notifyBlockUpdate(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
