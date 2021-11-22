package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity.NoteWrapper;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final ResourceLocation STREAMER_ICONS = new ResourceLocation("textures/gui/stream_indicator.png");

	/** The number of ticks between each note when playing back a recording **/
	private static final int PLAYBACK_DELAY = 15;

	private final SonicSecuritySystemTileEntity te;
	private int xSize = 176, ySize = 166;
	private IdButton recordingButton, clearButton, powerButton, playButton;
	private TogglePictureButton soundButton;

	/** If a recording is currently being played back **/
	private boolean playback = false;
	private int tickCount = PLAYBACK_DELAY;
	private int currentNote = 0;
	private int numNotes;

	public SonicSecuritySystemScreen(SonicSecuritySystemTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
		numNotes = te.getNumberOfNotes();
	}

	@Override
	public void tick() {

		// Play the note combination of this SSS when the player clicks on the play button
		if(playback) {
			tickCount++;

			// Only emit the note sound after a certain delay and if there are still notes to play
			if(tickCount >= PLAYBACK_DELAY && currentNote < numNotes) {
				tickCount = 0;

				NoteWrapper note = te.getRecordedNotes().get(currentNote++);
				SoundEvent se = NoteBlockInstrument.valueOf(note.instrumentName.toUpperCase()).getSound();
				float f = (float)Math.pow(2.0D, (note.noteID - 12) / 12.0D);
				minecraft.world.playSound(minecraft.player, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), se, SoundCategory.RECORDS, 3.0f, f);

				// Reset the counters when we are finished playing the final note
				if(currentNote == numNotes) {
					currentNote = 0;
					playback = false;
				}
			}
		}
	}

	@Override
	public void init()
	{
		super.init();

		powerButton = addButton(new IdButton(0, width / 2 - 75, height / 2 - 59, 150, 20, getPowerString(te.isActive()), button -> {
			boolean toggledState = !te.isActive();

			te.setActive(toggledState);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), toggledState ? SyncSSSSettingsOnServer.DataType.POWER_ON : SyncSSSSettingsOnServer.DataType.POWER_OFF));
			powerButton.setMessage(getPowerString(toggledState));

			// Disable the recording-related buttons when the SSS is powered off
			recordingButton.active = toggledState;
			soundButton.active = toggledState;
			clearButton.active = toggledState;
			playButton.active = toggledState;
		}));

		recordingButton = addButton(new IdButton(1, width / 2 - 75, height / 2 - 32, 150, 20, getRecordingString(te.isRecording()), button -> {
			boolean recording = !te.isRecording();
			te.setRecording(recording);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), recording ? SyncSSSSettingsOnServer.DataType.RECORDING_ON : SyncSSSSettingsOnServer.DataType.RECORDING_OFF));
			recordingButton.setMessage(getRecordingString(te.isRecording()));
		}));

		playButton = addButton(new IdButton(2, width / 2 - 75, height / 2 - 10, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.play"), button -> {
			// Start playing back any notes that have been recorded
			playback = true;
		}));

		clearButton = addButton(new IdButton(3, width / 2 - 75, height / 2 + 12, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.clear"), button -> {
			te.clearNotes();
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), SyncSSSSettingsOnServer.DataType.CLEAR_NOTES));
			clearButton.active = false;
		}));

		soundButton = addButton(new TogglePictureButton(4, width / 2 - 45, height / 2 + 52, 20, 20, STREAMER_ICONS, new int[]{0, 0}, new int[]{32, 48}, 2, 16, 16, 16, 16, 16, 64, 2, button -> {
			boolean toggledPing = !te.pings();

			te.setPings(toggledPing);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), toggledPing ? SyncSSSSettingsOnServer.DataType.SOUND_ON : SyncSSSSettingsOnServer.DataType.SOUND_OFF));
		}));
		soundButton.setCurrentIndex(!te.pings() ? 1 : 0); // Use the disabled mic icon if the SSS is not emitting sounds

		// Disable the "clear notes" button if no notes are recorded
		if(te.getNumberOfNotes() == 0)
			clearButton.active = false;
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
		super.render(matrix, mouseX, mouseY, partialTicks);

		TranslationTextComponent text = Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey());
		int textWidth = font.getStringPropertyWidth(text);
		font.drawText(matrix, text, startX + xSize / 2 - textWidth / 2, startY + 6, 4210752);

		font.drawText(matrix, Utils.localize("gui.securitycraft:sonic_security_system.sound"), startX + 10, startY + 141, 4210752);
		font.drawText(matrix, Utils.localize("gui.securitycraft:sonic_security_system.recording.notes", te.getNumberOfNotes()), startX + 14, startY + 120, 4210752);
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	private ITextComponent getRecordingString(boolean recording)
	{
		return recording ? Utils.localize("gui.securitycraft:sonic_security_system.recording.on") : Utils.localize("gui.securitycraft:sonic_security_system.recording.off");
	}

	private ITextComponent getPowerString(boolean on)
	{
		return on ? Utils.localize("gui.securitycraft:sonic_security_system.power.on") : Utils.localize("gui.securitycraft:sonic_security_system.power.off");
	}

}
