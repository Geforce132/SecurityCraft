package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

	public static void encode(SyncBlockPocketManager message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.size);
		buf.writeBoolean(message.showOutline);
		buf.writeVarInt(message.autoBuildOffset);
	}

	public static SyncBlockPocketManager decode(FriendlyByteBuf buf)
	{
		SyncBlockPocketManager message = new SyncBlockPocketManager();

		message.pos = buf.readBlockPos();
		message.size = buf.readVarInt();
		message.showOutline = buf.readBoolean();
		message.autoBuildOffset = buf.readVarInt();
		return message;
	}

	public static void onMessage(SyncBlockPocketManager message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			Player player = ctx.get().getSender();
			Level world = player.level;

			if(world.isLoaded(pos) && world.getBlockEntity(pos) instanceof BlockPocketManagerTileEntity bpm && bpm.getOwner().isOwner(player))
			{
				BlockState state = world.getBlockState(pos);

				bpm.size = message.size;
				bpm.showOutline = message.showOutline;
				bpm.autoBuildOffset = message.autoBuildOffset;
				world.sendBlockUpdated(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
