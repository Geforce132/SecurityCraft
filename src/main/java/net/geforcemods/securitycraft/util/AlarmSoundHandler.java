package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.PlayLevelSoundEvent;

public final class AlarmSoundHandler {
	private static final Map<AlarmBlockEntity, SoundInstance> SOUND_STORAGE = new HashMap<>();

	private AlarmSoundHandler() {}

	public static void playSound(AlarmBlockEntity be, Level level, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
		PlayLevelSoundEvent.AtPosition event = ForgeEventFactory.onPlaySoundAtPosition(level, x, y, z, sound, source, volume, pitch);

		if (event.isCanceled() || event.getSound() == null)
			return;

		SimpleSoundInstance soundInstance = new SimpleSoundInstance(event.getSound().value(), event.getSource(), event.getNewVolume(), event.getNewPitch(), RandomSource.create(seed), x, y, z);

		stopCurrentSound(be);
		Minecraft.getInstance().getSoundManager().play(soundInstance);
		SOUND_STORAGE.put(be, soundInstance);
	}

	public static void stopCurrentSound(AlarmBlockEntity be) {
		SoundInstance soundInstance = SOUND_STORAGE.remove(be);

		if (soundInstance != null)
			Minecraft.getInstance().getSoundManager().stop(soundInstance);
	}
}
