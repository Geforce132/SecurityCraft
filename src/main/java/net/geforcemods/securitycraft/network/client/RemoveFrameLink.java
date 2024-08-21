package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveFrameLink(BlockPos pos) implements CustomPacketPayload {
	public static final Type<RemoveFrameLink> TYPE = new Type<>(SecurityCraft.resLoc("remove_frame_link"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveFrameLink> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RemoveFrameLink::pos,
			RemoveFrameLink::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Level level = ctx.player().level();
		BlockEntity be = level.getBlockEntity(pos);

		if (be instanceof FrameBlockEntity frame)
			CameraController.removeFrameLink(frame, GlobalPos.of(level.dimension(), pos));
	}
}
