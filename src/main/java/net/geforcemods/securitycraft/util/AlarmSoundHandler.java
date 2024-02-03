package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

public final class AlarmSoundHandler {
	private static final Map<AlarmBlockEntity, ISound> SOUND_STORAGE = new HashMap<>();

	private AlarmSoundHandler() {}

	public static void playSound(AlarmBlockEntity be, double x, double y, double z, SoundEvent sound, SoundCategory source, float volume, float pitch) {
		Minecraft mc = Minecraft.getInstance();
		PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(mc.player, sound, source, volume, pitch);

		if (event.isCanceled() || event.getSound() == null)
			return;

		SimpleSound soundInstance = new SimpleSound(event.getSound(), event.getCategory(), event.getVolume(), event.getPitch(), x, y, z);
		SoundHandler soundManager = mc.getSoundManager();

		if (soundInstance.resolve(soundManager) == null)
			soundInstance = new SimpleSound(SCSounds.ALARM.event, event.getCategory(), event.getVolume(), event.getPitch(), x, y, z);

		stopCurrentSound(be);
		soundManager.play(soundInstance);
		SOUND_STORAGE.put(be, soundInstance);
	}

	public static void stopCurrentSound(AlarmBlockEntity be) {
		ISound soundInstance = SOUND_STORAGE.remove(be);

		if (soundInstance != null)
			Minecraft.getInstance().getSoundManager().stop(soundInstance);
	}
}
