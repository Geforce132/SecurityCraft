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

	public SyncBlockPocketManager(PacketBuffer buf) {
		pos = buf.readBlockPos();
		size = buf.readVarInt();
		showOutline = buf.readBoolean();
		autoBuildOffset = buf.readVarInt();
		color = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(size);
		buf.writeBoolean(showOutline);
		buf.writeVarInt(autoBuildOffset);
		buf.writeInt(color);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		World level = player.level;
		TileEntity te = level.getBlockEntity(pos);

		if (!player.isSpectator() && level.isLoaded(pos) && te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
			BlockPocketManagerBlockEntity bpm = (BlockPocketManagerBlockEntity) te;
			BlockState state = level.getBlockState(pos);

			bpm.setSize(size);
			bpm.setShowOutline(showOutline);
			bpm.setAutoBuildOffset(autoBuildOffset);
			bpm.setColor(color);
			bpm.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
