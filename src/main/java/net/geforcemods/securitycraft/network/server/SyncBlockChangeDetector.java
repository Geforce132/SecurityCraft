package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SyncBlockChangeDetector {
	private BlockPos pos;
	private DetectionMode mode;
	private boolean showHighlights;
	private int color;

	public SyncBlockChangeDetector() {}

	public SyncBlockChangeDetector(BlockPos pos, DetectionMode mode, boolean showHighlights, int color) {
		this.pos = pos;
		this.mode = mode;
		this.showHighlights = showHighlights;
		this.color = color;
	}

	public SyncBlockChangeDetector(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		mode = buf.readEnum(DetectionMode.class);
		showHighlights = buf.readBoolean();
		color = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(mode);
		buf.writeBoolean(showHighlights);
		buf.writeInt(color);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level();

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			be.setMode(mode);
			be.showHighlights(showHighlights);
			be.setColor(color);
			be.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
