package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class ToggleAlarmSound {
	private final BlockPos bePos;
	private final boolean shouldPlay;
	private final int soundX, soundY, soundZ;
	private final float volume;
	private final long seed;

	private ToggleAlarmSound(BlockPos bePos, boolean shouldPlay, float volume, long seed) {
		this.bePos = bePos;
		this.shouldPlay = shouldPlay;
		this.soundX = (int) (bePos.getX() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundY = (int) (bePos.getY() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundZ = (int) (bePos.getZ() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.volume = volume;
		this.seed = seed;
	}

	public ToggleAlarmSound(FriendlyByteBuf buf) {
		bePos = buf.readBlockPos();
		shouldPlay = buf.readBoolean();
		soundX = buf.readVarInt();
		soundY = buf.readVarInt();
		soundZ = buf.readVarInt();
		volume = buf.readFloat();
		seed = buf.readVarLong();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(bePos);
		buf.writeBoolean(shouldPlay);
		buf.writeVarInt(soundX);
		buf.writeVarInt(soundY);
		buf.writeVarInt(soundZ);
		buf.writeFloat(volume);
		buf.writeVarLong(seed);
	}

	public static ToggleAlarmSound off(BlockPos bePos) {
		return new ToggleAlarmSound(bePos, false, 0.0F, 0);
	}

	public static ToggleAlarmSound on(BlockPos bePos, float volume, long seed) {
		return new ToggleAlarmSound(bePos, true, volume, seed);
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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Level level = ClientHandler.getClientLevel();

		if (level.getBlockEntity(bePos) instanceof AlarmBlockEntity be) {
			if (shouldPlay)
				be.playSound(level, getX(), getY(), getZ(), volume, seed);
			else
				be.stopPlayingSound();
		}
	}
}
