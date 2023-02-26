package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

public class PlayAlarmSound {
	private BlockPos bePos;
	private Holder<SoundEvent> sound;
	private int soundX, soundY, soundZ;
	private float volume;
	private long seed;

	public PlayAlarmSound() {}

	public PlayAlarmSound(BlockPos bePos, Holder<SoundEvent> sound, float volume, long seed) {
		this.bePos = bePos;
		this.sound = sound;
		this.soundX = (int) (bePos.getX() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundY = (int) (bePos.getY() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.soundZ = (int) (bePos.getZ() * ClientboundSoundPacket.LOCATION_ACCURACY);
		this.volume = volume;
		this.seed = seed;
	}

	public static void encode(PlayAlarmSound message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.bePos);
		buf.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), message.sound, (buffer, soundEvent) -> soundEvent.writeToNetwork(buffer));
		buf.writeInt(message.soundX);
		buf.writeInt(message.soundY);
		buf.writeInt(message.soundZ);
		buf.writeFloat(message.volume);
		buf.writeLong(message.seed);
	}

	public static PlayAlarmSound decode(FriendlyByteBuf buf) {
		PlayAlarmSound message = new PlayAlarmSound();

		message.bePos = buf.readBlockPos();
		message.sound = buf.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
		message.soundX = buf.readInt();
		message.soundY = buf.readInt();
		message.soundZ = buf.readInt();
		message.volume = buf.readFloat();
		message.seed = buf.readLong();
		return message;
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

	public static void onMessage(PlayAlarmSound message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();

			if (mc.level.getBlockEntity(message.bePos) instanceof AlarmBlockEntity be)
				be.playSound(mc.level, message.getX(), message.getY(), message.getZ(), message.sound, SoundSource.BLOCKS, message.volume, 1.0F, message.seed);
		});

		ctx.get().setPacketHandled(true);
	}
}
