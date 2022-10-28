package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncBlockPocketManager {
	private BlockPos pos;
	private int size;
	private boolean showOutline;
	private int autoBuildOffset;
	private int color;

	public SyncBlockPocketManager() {}

	public SyncBlockPocketManager(BlockPos pos, int size, boolean showOutline, int autoBuildOffset, int color) {
		this.pos = pos;
		this.size = size;
		this.showOutline = showOutline;
		this.autoBuildOffset = autoBuildOffset;
		this.color = color;
	}

	public static void encode(SyncBlockPocketManager message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.size);
		buf.writeBoolean(message.showOutline);
		buf.writeVarInt(message.autoBuildOffset);
		buf.writeInt(message.color);
	}

	public static SyncBlockPocketManager decode(PacketBuffer buf) {
		SyncBlockPocketManager message = new SyncBlockPocketManager();

		message.pos = buf.readBlockPos();
		message.size = buf.readVarInt();
		message.showOutline = buf.readBoolean();
		message.autoBuildOffset = buf.readVarInt();
		message.color = buf.readInt();
		return message;
	}

	public static void onMessage(SyncBlockPocketManager message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			World world = player.level;
			TileEntity te = world.getBlockEntity(pos);

			if (world.isLoaded(pos) && te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).getOwner().isOwner(player)) {
				BlockPocketManagerBlockEntity bpm = (BlockPocketManagerBlockEntity) te;
				BlockState state = world.getBlockState(pos);

				bpm.size = message.size;
				bpm.showOutline = message.showOutline;
				bpm.autoBuildOffset = message.autoBuildOffset;
				bpm.setColor(message.color);
				bpm.setChanged();
				world.sendBlockUpdated(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
