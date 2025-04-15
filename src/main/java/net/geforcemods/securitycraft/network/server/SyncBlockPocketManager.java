package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

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

	public SyncBlockPocketManager(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		size = buf.readVarInt();
		showOutline = buf.readBoolean();
		autoBuildOffset = buf.readVarInt();
		color = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(size);
		buf.writeBoolean(showOutline);
		buf.writeVarInt(autoBuildOffset);
		buf.writeInt(color);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level;

		if (!player.isSpectator() && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity bpm && bpm.isOwnedBy(player)) {
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
