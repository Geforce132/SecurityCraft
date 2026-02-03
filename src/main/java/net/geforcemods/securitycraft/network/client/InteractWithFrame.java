package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InteractWithFrame(BlockPos pos, boolean owner) implements CustomPacketPayload {
	public static final Type<InteractWithFrame> TYPE = new Type<>(SecurityCraft.resLoc("interact_with_frame"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, InteractWithFrame> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, InteractWithFrame::pos,
			ByteBufCodecs.BOOL,InteractWithFrame::owner,
			InteractWithFrame::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Level level = ctx.player().level();

		if (level.getBlockEntity(pos) instanceof FrameBlockEntity be) {
			if (!be.redstoneSignalDisabled() && !be.hasClientInteracted() && be.getCurrentCamera() != null)
				be.setCameraOnClientAndUpdate(be.getCurrentCamera());
			else
				ClientHandler.displayFrameScreen(be, !owner);
		}
	}
}
