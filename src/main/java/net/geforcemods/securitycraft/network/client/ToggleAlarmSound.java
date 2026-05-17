package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleAlarmSound(BlockPos bePos, boolean shouldPlay, int soundX, int soundY, int soundZ, float volume, long seed) implements CustomPacketPayload {
	public static final Type<ToggleAlarmSound> TYPE = new Type<>(SecurityCraft.resLoc("toggle_alarm_sound"));
	//formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleAlarmSound> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ToggleAlarmSound::bePos,
			ByteBufCodecs.BOOL, ToggleAlarmSound::shouldPlay,
			ByteBufCodecs.VAR_INT, ToggleAlarmSound::soundX,
			ByteBufCodecs.VAR_INT, ToggleAlarmSound::soundY,
			ByteBufCodecs.VAR_INT, ToggleAlarmSound::soundZ,
			ByteBufCodecs.FLOAT, ToggleAlarmSound::volume,
			ByteBufCodecs.VAR_LONG, ToggleAlarmSound::seed,
			ToggleAlarmSound::new);
	//formatter:on

	public ToggleAlarmSound(BlockPos bePos, boolean shoulyPlay, float volume, long seed) {
		this(bePos, shoulyPlay, (int) (bePos.getX() * ClientboundSoundPacket.LOCATION_ACCURACY), (int) (bePos.getY() * ClientboundSoundPacket.LOCATION_ACCURACY), (int) (bePos.getZ() * ClientboundSoundPacket.LOCATION_ACCURACY), volume, seed);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public double getX() {
		return soundX / ClientboundSoundPacket.LOCATION_ACCURACY;
	}

	public double getY() {
		return soundY / ClientboundSoundPacket.LOCATION_ACCURACY;
	}

	public double getZ() {
		return soundZ / ClientboundSoundPacket.LOCATION_ACCURACY;
	}

	public void handle(IPayloadContext ctx) {
		Level level = ctx.player().level();

		if (level.getBlockEntity(bePos) instanceof AlarmBlockEntity be) {
			if (shouldPlay)
				be.playSound(level, getX(), getY(), getZ(), volume, seed);
			else
				be.stopPlayingSound();
		}
	}
}
