package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncTrophySystem(BlockPos pos, ResourceLocation projectileTypeLocation, boolean allowed) implements CustomPacketPayload {

	public static final Type<SyncTrophySystem> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "sync_trophy_system"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncTrophySystem> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncTrophySystem::pos,
			ResourceLocation.STREAM_CODEC, SyncTrophySystem::projectileTypeLocation,
			ByteBufCodecs.BOOL, SyncTrophySystem::allowed,
			SyncTrophySystem::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		EntityType<?> projectileType = BuiltInRegistries.ENTITY_TYPE.get(projectileTypeLocation);

		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof TrophySystemBlockEntity be && be.isOwnedBy(player)) {
			BlockState state = level.getBlockState(pos);

			be.setFilter(projectileType, allowed);
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}
}
