package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class PlayAlarmSound {
	private BlockPos bePos;
	private SoundEvent sound;
	private int soundX, soundY, soundZ;
	private float volume, pitch;

	public PlayAlarmSound() {}

	public PlayAlarmSound(BlockPos bePos, SoundEvent sound, float volume, float pitch) {
		this.bePos = bePos;
		this.sound = sound;
		this.soundX = (int) (bePos.getX() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundY = (int) (bePos.getY() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundZ = (int) (bePos.getZ() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.volume = volume;
		this.pitch = pitch;
	}

	public PlayAlarmSound(FriendlyByteBuf buf) {
		bePos = buf.readBlockPos();
		sound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
		soundX = buf.readInt();
		soundY = buf.readInt();
		soundZ = buf.readInt();
		volume = buf.readFloat();
		pitch = buf.readFloat();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(bePos);
		buf.writeResourceLocation(ForgeRegistries.SOUND_EVENTS.getKey(sound));
		buf.writeInt(soundX);
		buf.writeInt(soundY);
		buf.writeInt(soundZ);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Level level = ClientHandler.getClientLevel();

		if (level.getBlockEntity(bePos) instanceof AlarmBlockEntity be)
			be.playSound(getX(), getY(), getZ(), sound, volume, pitch);
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
}
