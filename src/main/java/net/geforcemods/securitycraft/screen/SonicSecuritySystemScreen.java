package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final ResourceLocation STREAMER_ICONS = new ResourceLocation("textures/gui/stream_indicator.png");
	private static final TranslationTextComponent SOUND_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.sound");
	/** The number of ticks between each note when playing back a recording **/
	private static final int PLAYBACK_DELAY = 10;
	private final SonicSecuritySystemBlockEntity te;
	private int xSize = 176, ySize = 166;
	private Button recordingButton, clearButton, powerButton, playButton;
	private TogglePictureButton soundButton;
	/** If a recording is currently being played back **/
	private boolean playback = false;
	/** The number of ticks that has elapsed since the last note played **/
	private int tickCount = PLAYBACK_DELAY;
	private int currentNote = 0;
	private boolean isOwner;

	public SonicSecuritySystemScreen(SonicSecuritySystemBlockEntity te) {
		super(te.getName());
		this.te = te;
		isOwner = te.getOwner().isOwner(Minecraft.getInstance().player);
	}

	@Override
	public void tick() {
		// Play the note combination of this SSS when the player clicks on the play button
		if (playback) {
			tickCount++;

			// Only emit the note sound after a certain delay and if there are still notes to play
			if (tickCount >= PLAYBACK_DELAY) {
				if (currentNote < te.getNumberOfNotes()) {
					NoteWrapper note = te.getRecordedNotes().get(currentNote++);
					SoundEvent sound = NoteBlockInstrument.valueOf(note.instrumentName.toUpperCase()).getSoundEvent();
					float pitch = (float) Math.pow(2.0D, (note.noteID - 12) / 12.0D);

					tickCount = 0;
					minecraft.level.playSound(minecraft.player, te.getBlockPos(), sound, SoundCategory.RECORDS, 3.0F, pitch);
				}
				// Reset the counters when we are finished playing the final note
				else if (currentNote >= te.getNumberOfNotes()) {
					currentNote = 0;
					playback = false;
				}
			}
		}
	}

	@Override
	public void init() {
		super.init();

		boolean isActive = te.isActive();
		boolean hasNotes = te.getNumberOfNotes() > 0;

		powerButton = addButton(new ExtendedButton(width / 2 - 75, height / 2 - 59, 150, 20, getPowerString(te.isActive()), button -> {
			boolean toggledState = !te.isActive();
			boolean containsNotes = te.getNumberOfNotes() > 0;

			te.setActive(toggledState);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getBlockPos(), toggledState ? SyncSSSSettingsOnServer.DataType.POWER_ON : SyncSSSSettingsOnServer.DataType.POWER_OFF));
			powerButton.setMessage(getPowerString(toggledState));

			if (!toggledState)
				recordingButton.setMessage(getRecordingString(false));

			// Disable the recording-related buttons when the SSS is powered off
			recordingButton.active = toggledState;
			soundButton.active = toggledState;
			playButton.active = toggledState && containsNotes;
			clearButton.active = toggledState && containsNotes;
		}));

		recordingButton = addButton(new ExtendedButton(width / 2 - 75, height / 2 - 32, 150, 20, getRecordingString(te.isRecording()), button -> {
			boolean recording = !te.isRecording();
			te.setRecording(recording);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getBlockPos(), recording ? SyncSSSSettingsOnServer.DataType.RECORDING_ON : SyncSSSSettingsOnServer.DataType.RECORDING_OFF));
			recordingButton.setMessage(getRecordingString(te.isRecording()));
		}));

		playButton = addButton(new ExtendedButton(width / 2 - 75, height / 2 - 10, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.play").getColoredString(), button -> {
			// Start playing back any notes that have been recorded
			if (te.getNumberOfNotes() > 0) {
				playback = true;
			}
		}));

		clearButton = addButton(new ExtendedButton(width / 2 - 75, height / 2 + 12, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.clear").getColoredString(), button -> {
			te.clearNotes();
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getBlockPos(), SyncSSSSettingsOnServer.DataType.CLEAR_NOTES));
			playButton.active = false;
			clearButton.active = false;
		}));

		//@formatter:off
		soundButton = addButton(new TogglePictureButton(width / 2 + 55, height / 2 + 52, 20, 20, STREAMER_ICONS, new int[] {0, 0}, new int[] {32, 48}, 2, 16, 16, 16, 16, 16, 64, 2, button -> {
			//@formatter:on
			boolean toggledPing = !te.pings();

			te.setPings(toggledPing);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getBlockPos(), toggledPing ? SyncSSSSettingsOnServer.DataType.SOUND_ON : SyncSSSSettingsOnServer.DataType.SOUND_OFF));
		}));
		soundButton.setCurrentIndex(!te.pings() ? 1 : 0); // Use the disabled mic icon if the SSS is not emitting sounds

		powerButton.active = isOwner;
		recordingButton.active = isActive && isOwner;
		soundButton.active = isActive && isOwner;
		playButton.active = isActive && hasNotes;
		clearButton.active = isActive && hasNotes && isOwner;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		String title = this.title.getColoredString();
		String soundText = SOUND_TEXT.getColoredString();
		int textWidth = font.width(title);
		int soundTextLength = font.width(soundText);

		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(startX, startY, 0, 0, xSize, ySize);
		super.render(mouseX, mouseY, partialTicks);
		font.draw(title, startX + xSize / 2 - textWidth / 2, startY + 6, 4210752);
		font.draw(soundText, width / 2 + 50 - soundTextLength, startY + 141, 4210752);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private String getRecordingString(boolean recording) {
		return (recording ? Utils.localize("gui.securitycraft:sonic_security_system.stop_recording") : Utils.localize("gui.securitycraft:sonic_security_system.start_recording")).getColoredString();
	}

	private String getPowerString(boolean on) {
		return (on ? Utils.localize("gui.securitycraft:sonic_security_system.power.on") : Utils.localize("gui.securitycraft:sonic_security_system.power.off")).getColoredString();
	}
}
