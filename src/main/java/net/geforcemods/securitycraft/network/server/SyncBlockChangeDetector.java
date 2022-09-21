package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SyncBlockChangeDetector {
	private BlockPos pos;
	private DetectionMode mode;
	private boolean showHighlights;

	public SyncBlockChangeDetector() {}

	public SyncBlockChangeDetector(BlockPos pos, DetectionMode mode, boolean showHighlights) {
		this.pos = pos;
		this.mode = mode;
		this.showHighlights = showHighlights;
	}

	public static void encode(SyncBlockChangeDetector message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.mode);
		buf.writeBoolean(message.showHighlights);
	}

	public static SyncBlockChangeDetector decode(FriendlyByteBuf buf) {
		SyncBlockChangeDetector message = new SyncBlockChangeDetector();

		message.pos = buf.readBlockPos();
		message.mode = buf.readEnum(DetectionMode.class);
		message.showHighlights = buf.readBoolean();
		return message;
	}

	public static void onMessage(SyncBlockChangeDetector message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level level = ctx.get().getSender().level;
			BlockPos pos = message.pos;

			if (level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && be.getOwner().isOwner(ctx.get().getSender())) {
				BlockState state = level.getBlockState(pos);

				be.setMode(message.mode);
				be.showHighlights(message.showHighlights);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
