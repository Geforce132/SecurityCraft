package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncRiftStabilizer(BlockPos pos, TeleportationType teleportationType, boolean allowed) implements CustomPacketPayload {

	public static final Type<SyncRiftStabilizer> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_rift_stabilizer"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncRiftStabilizer> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncRiftStabilizer::pos,
			NeoForgeStreamCodecs.enumCodec(TeleportationType.class), SyncRiftStabilizer::teleportationType,
			ByteBufCodecs.BOOL, SyncRiftStabilizer::allowed,
			SyncRiftStabilizer::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		if (teleportationType != null) {
			Player player = ctx.player();
			Level level = player.level();

			if (level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity be && be.isOwnedBy(player)) {
				BlockState state = level.getBlockState(pos);

				be.setFilter(teleportationType, allowed);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}
