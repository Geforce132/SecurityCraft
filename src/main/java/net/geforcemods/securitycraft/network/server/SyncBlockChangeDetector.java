package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBlockChangeDetector(BlockPos pos, DetectionMode mode, boolean showHighlights, int color) implements CustomPacketPayload {

	public static final Type<SyncBlockChangeDetector> TYPE = new Type<>(SecurityCraft.resLoc("sync_block_change_detector"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockChangeDetector> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncBlockChangeDetector::pos,
			NeoForgeStreamCodecs.enumCodec(DetectionMode.class), SyncBlockChangeDetector::mode,
			ByteBufCodecs.BOOL, SyncBlockChangeDetector::showHighlights,
			ByteBufCodecs.VAR_INT, SyncBlockChangeDetector::color,
			SyncBlockChangeDetector::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			be.setMode(mode);
			be.showHighlights(showHighlights);
			be.setColor(color);
			be.setChanged();
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
