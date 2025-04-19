package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncAlarmSettings(BlockPos pos, ResourceLocation soundEvent, float pitch, int soundLength) implements CustomPacketPayload {

	public static final Type<SyncAlarmSettings> TYPE = new Type<>(SecurityCraft.resLoc("sync_alarm_settings"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncAlarmSettings> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncAlarmSettings::pos,
			ResourceLocation.STREAM_CODEC, SyncAlarmSettings::soundEvent,
			ByteBufCodecs.FLOAT, SyncAlarmSettings::pitch,
			ByteBufCodecs.VAR_INT, SyncAlarmSettings::soundLength,
			SyncAlarmSettings::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof AlarmBlockEntity be && be.isOwnedBy(player)) {
			if (!soundEvent.equals(be.getSound().location()))
				be.setSound(soundEvent);

			if (pitch != be.getPitch())
				be.setPitch(pitch);

			if (soundLength != be.getSoundLength())
				be.setSoundLength(soundLength);
		}
	}
}
