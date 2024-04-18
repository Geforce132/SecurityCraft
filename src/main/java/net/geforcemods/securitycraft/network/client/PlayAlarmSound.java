package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class PlayAlarmSound implements CustomPacketPayload {
	public static final Type<PlayAlarmSound> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "play_alarm_sound"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PlayAlarmSound> STREAM_CODEC = new StreamCodec<>() {
		public PlayAlarmSound decode(RegistryFriendlyByteBuf buf) {
			PlayAlarmSound packet = new PlayAlarmSound();

			packet.bePos = buf.readBlockPos();
			packet.sound = buf.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
			packet.soundX = buf.readInt();
			packet.soundY = buf.readInt();
			packet.soundZ = buf.readInt();
			packet.volume = buf.readFloat();
			packet.pitch = buf.readFloat();
			packet.seed = buf.readLong();
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, PlayAlarmSound packet) {
			buf.writeBlockPos(packet.bePos);
			buf.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), packet.sound, (buffer, soundEvent) -> soundEvent.writeToNetwork(buffer));
			buf.writeInt(packet.soundX);
			buf.writeInt(packet.soundY);
			buf.writeInt(packet.soundZ);
			buf.writeFloat(packet.volume);
			buf.writeFloat(packet.pitch);
			buf.writeLong(packet.seed);
		}
	};
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

	public void handle(PlayPayloadContext ctx) {
		Level level = ClientHandler.getClientLevel();

		if (level.getBlockEntity(bePos) instanceof AlarmBlockEntity be)
			be.playSound(level, getX(), getY(), getZ(), sound, volume, pitch, seed);
	}
}
