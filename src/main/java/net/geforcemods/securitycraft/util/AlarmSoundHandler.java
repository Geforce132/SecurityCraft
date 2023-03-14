package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

public final class AlarmSoundHandler {
	private static final Map<AlarmBlockEntity, ISound> SOUND_STORAGE = new HashMap<>();

	private AlarmSoundHandler() {}

	public static void playSound(AlarmBlockEntity be, World level, double x, double y, double z, SoundEvent sound, SoundCategory source, float volume, float pitch) {
		Minecraft mc = Minecraft.getMinecraft();
		PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(mc.player, sound, source, volume, pitch);

		if (event.isCanceled() || event.getSound() == null)
			return;

		PositionedSoundRecord soundInstance = new PositionedSoundRecord(event.getSound(), event.getCategory(), event.getVolume(), event.getPitch(), (float) x, (float) y, (float) z);

		stopCurrentSound(be);
		mc.getSoundHandler().playSound(soundInstance);
		SOUND_STORAGE.put(be, soundInstance);
	}

	public static void stopCurrentSound(AlarmBlockEntity be) {
		ISound soundInstance = SOUND_STORAGE.remove(be);

		if (soundInstance != null)
			Minecraft.getMinecraft().getSoundHandler().stopSound(soundInstance);
	}
}
