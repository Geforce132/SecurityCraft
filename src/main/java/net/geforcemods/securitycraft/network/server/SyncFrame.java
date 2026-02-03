package net.geforcemods.securitycraft.network.server;

import java.util.Optional;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncFrame(BlockPos pos, int requestedRenderDistance, Optional<GlobalPos> removedCamera, Optional<GlobalPos> currentCamera, boolean disableCurrentCamera) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SyncFrame> TYPE = new CustomPacketPayload.Type<>(SecurityCraft.resLoc("sync_frame"));
	//@formatter:off
	public static final StreamCodec<ByteBuf, SyncFrame> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncFrame::pos,
			ByteBufCodecs.VAR_INT, SyncFrame::requestedRenderDistance,
			ByteBufCodecs.optional(GlobalPos.STREAM_CODEC), SyncFrame::removedCamera,
			ByteBufCodecs.optional(GlobalPos.STREAM_CODEC), SyncFrame::currentCamera,
			ByteBufCodecs.BOOL, SyncFrame::disableCurrentCamera,
			SyncFrame::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		GlobalPos currentCamera = this.currentCamera.orElse(null);

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof FrameBlockEntity be) {
			if (be.isDisabled())
				return;

			boolean isOwner = be.isOwnedBy(player);

			if (isOwner)
				removedCamera.ifPresent(be::removeCamera);

			if (isOwner || be.isAllowed(player))
				be.switchCameraOnServer(currentCamera, player, requestedRenderDistance, disableCurrentCamera);
		}
	}
}
