package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

public final class AlarmSoundHandler {
	private static final Map<AlarmBlockEntity, SoundInstance> SOUND_STORAGE = new HashMap<>();

	private AlarmSoundHandler() {}

	public static void playSound(AlarmBlockEntity be, Level level, double x, double y, double z, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, long seed) {
		PlayLevelSoundEvent.AtPosition event = EventHooks.onPlaySoundAtPosition(level, x, y, z, sound, source, volume, pitch);

		if (event.isCanceled() || event.getSound() == null)
			return;

		SimpleSoundInstance soundInstance = new SimpleSoundInstance(event.getSound().value(), event.getSource(), event.getNewVolume(), event.getNewPitch(), RandomSource.create(seed), x, y, z);
		SoundManager soundManager = Minecraft.getInstance().getSoundManager();

		if (soundInstance.resolve(soundManager) == null)
			soundInstance = new SimpleSoundInstance(SCSounds.ALARM.event, event.getSource(), event.getNewVolume(), event.getNewPitch(), RandomSource.create(seed), x, y, z);

		stopCurrentSound(be);
		soundManager.play(soundInstance);
		SOUND_STORAGE.put(be, soundInstance);
	}

	public static void stopCurrentSound(AlarmBlockEntity be) {
		SoundInstance soundInstance = SOUND_STORAGE.remove(be);

		if (soundInstance != null)
			Minecraft.getInstance().getSoundManager().stop(soundInstance);
	}
}
