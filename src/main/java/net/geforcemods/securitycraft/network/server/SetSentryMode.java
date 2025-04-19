package net.geforcemods.securitycraft.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSentryMode(List<Info> sentriesToUpdate) implements CustomPacketPayload {
	public static final Type<SetSentryMode> TYPE = new Type<>(SecurityCraft.resLoc("set_sentry_mode"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetSentryMode> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.collection(ArrayList::new, Info.STREAM_CODEC), packet -> {
				packet.sentriesToUpdate.removeIf(Objects::isNull);
				return packet.sentriesToUpdate;
			},
			SetSentryMode::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();

		if (!player.isSpectator()) {
			for (Info info : sentriesToUpdate) {
				if (level.isLoaded(info.pos)) {
					List<Sentry> sentries = level.<Sentry>getEntitiesOfClass(Sentry.class, new AABB(info.pos));

					if (!sentries.isEmpty() && sentries.get(0).isOwnedBy(player))
						sentries.get(0).toggleMode(player, info.mode, false);
				}
			}
		}
	}

	public static record Info(BlockPos pos, int mode) {
		//@formatter:off
		public static final StreamCodec<RegistryFriendlyByteBuf, Info> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, Info::pos,
				ByteBufCodecs.VAR_INT, Info::mode,
				Info::new);
		//@formatter:on
	}
}
