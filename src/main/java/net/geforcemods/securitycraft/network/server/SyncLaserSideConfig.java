package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncLaserSideConfig(BlockPos pos, CompoundTag sideConfig) implements CustomPacketPayload {
	public static final Type<SyncLaserSideConfig> TYPE = new Type<>(SecurityCraft.resLoc("sync_laser_side_config"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncLaserSideConfig> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncLaserSideConfig::pos,
			ByteBufCodecs.COMPOUND_TAG, SyncLaserSideConfig::sideConfig,
			SyncLaserSideConfig::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (level.getBlockEntity(pos) instanceof LaserBlockBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			be.applyNewSideConfig(LaserBlockBlockEntity.loadSideConfig(sideConfig), player);
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
