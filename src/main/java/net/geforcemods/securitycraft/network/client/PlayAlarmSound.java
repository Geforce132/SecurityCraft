package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class PlayAlarmSound implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "play_alarm_sound");
	private BlockPos bePos;
	private Holder<SoundEvent> sound;
	private int soundX, soundY, soundZ;
	private float volume, pitch;
	private long seed;

	public PlayAlarmSound() {}

	public PlayAlarmSound(BlockPos bePos, Holder<SoundEvent> sound, float volume, float pitch, long seed) {
		this.bePos = bePos;
		this.sound = sound;
		this.soundX = (int) (bePos.getX() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundY = (int) (bePos.getY() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundZ = (int) (bePos.getZ() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.volume = volume;
		this.pitch = pitch;
		this.seed = seed;
	}

	public PlayAlarmSound(FriendlyByteBuf buf) {
		bePos = buf.readBlockPos();
		sound = buf.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
		soundX = buf.readInt();
		soundY = buf.readInt();
		soundZ = buf.readInt();
		volume = buf.readFloat();
		pitch = buf.readFloat();
		seed = buf.readLong();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(bePos);
		buf.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), sound, (buffer, soundEvent) -> soundEvent.writeToNetwork(buffer));
		buf.writeInt(soundX);
		buf.writeInt(soundY);
		buf.writeInt(soundZ);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
		buf.writeLong(seed);
	}

	@Override
	public ResourceLocation id() {
		return ID;
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

	public void handle(PlayPayloadContext ctx) {
		Level level = ClientHandler.getClientLevel();

		if (level.getBlockEntity(bePos) instanceof AlarmBlockEntity be)
			be.playSound(level, getX(), getY(), getZ(), sound, volume, pitch, seed);
	}
}
