package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearChangeDetectorServer(BlockPos pos) implements CustomPacketPayload {
	public static final Type<ClearChangeDetectorServer> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "clear_change_detector_server"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ClearChangeDetectorServer> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ClearChangeDetectorServer::pos,
			ClearChangeDetectorServer::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (player.level().getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be && be.isOwnedBy(player)) {
			be.getEntries().clear();
			be.setChanged();
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
		}
	}
}
