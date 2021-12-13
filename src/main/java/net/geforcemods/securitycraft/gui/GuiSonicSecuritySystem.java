package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.ClickButton;
import net.geforcemods.securitycraft.gui.components.TogglePictureButton;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem.NoteWrapper;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockNote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSonicSecuritySystem extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final ResourceLocation STREAMER_ICONS = new ResourceLocation("textures/gui/stream_indicator.png");
	private static final TextComponentTranslation SOUND_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.sound");
	/** The number of ticks between each note when playing back a recording **/
	private static final int PLAYBACK_DELAY = 10;
	private final TileEntitySonicSecuritySystem te;
	private int xSize = 176, ySize = 166;
	private ClickButton recordingButton, clearButton, powerButton, playButton;
	private TogglePictureButton soundButton;
	/** If a recording is currently being played back **/
	private boolean playback = false;
	/** The number of ticks that has elapsed since the last note played **/
	private int tickCount = PLAYBACK_DELAY;
	private int currentNote = 0;
	private boolean isOwner;
	private String title;

	public GuiSonicSecuritySystem(InventoryPlayer inv, TileEntitySonicSecuritySystem te)
	{
		super(new ContainerGeneric(inv, te));
		this.te = te;
		title = te.getDisplayName().getFormattedText();
		isOwner = te.getOwner().isOwner(Minecraft.getMinecraft().player);
	}
	@Override
	public void updateScreen() {
		// Play the note combination of this SSS when the player clicks on the play button
		if(playback) {
			tickCount++;

			// Only emit the note sound after a certain delay and if there are still notes to play
			if(tickCount >= PLAYBACK_DELAY) {
				if(currentNote < te.getNumberOfNotes()) {
					NoteWrapper note = te.getRecordedNotes().get(currentNote++);
					SoundEvent sound = ((BlockNote)Blocks.NOTEBLOCK).getInstrument(Instrument.valueOf(note.instrumentName.toUpperCase()).ordinal());
					float pitch = (float)Math.pow(2.0D, (note.noteID - 12) / 12.0D);

					tickCount = 0;
					mc.world.playSound(mc.player, te.getPos(), sound, SoundCategory.RECORDS, 3.0F, pitch);
				}
				// Reset the counters when we are finished playing the final note
				else if(currentNote >= te.getNumberOfNotes()) {
					currentNote = 0;
					playback = false;
				}
			}
		}
	}

	@Override
	public void initGui()
	{
		super.initGui();

		boolean isActive = te.isActive();
		boolean hasNotes = te.getNumberOfNotes() > 0;

		powerButton = addButton(new ClickButton(0, width / 2 - 75, height / 2 - 59, 150, 20, getPowerString(te.isActive()), button -> {
			boolean toggledState = !te.isActive();
			boolean containsNotes = te.getNumberOfNotes() > 0;

			te.setActive(toggledState);
			SecurityCraft.network.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), toggledState ? SyncSSSSettingsOnServer.DataType.POWER_ON : SyncSSSSettingsOnServer.DataType.POWER_OFF));
			powerButton.displayString = getPowerString(toggledState);

			if(!toggledState)
				recordingButton.displayString = getRecordingString(false);

			// Disable the recording-related buttons when the SSS is powered off
			recordingButton.enabled = toggledState;
			soundButton.enabled = toggledState;
			playButton.enabled = toggledState && containsNotes;
			clearButton.enabled = toggledState && containsNotes;
		}));

		recordingButton = addButton(new ClickButton(1, width / 2 - 75, height / 2 - 32, 150, 20, getRecordingString(te.isRecording()), button -> {
			boolean recording = !te.isRecording();
			te.setRecording(recording);
			SecurityCraft.network.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), recording ? SyncSSSSettingsOnServer.DataType.RECORDING_ON : SyncSSSSettingsOnServer.DataType.RECORDING_OFF));
			recordingButton.displayString = getRecordingString(te.isRecording());
		}));

		playButton = addButton(new ClickButton(2, width / 2 - 75, height / 2 - 10, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.play").getFormattedText(), button -> {
			// Start playing back any notes that have been recorded
			if(te.getNumberOfNotes() > 0)
				playback = true;
		}));

		clearButton = addButton(new ClickButton(3, width / 2 - 75, height / 2 + 12, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.clear").getFormattedText(), button -> {
			te.clearNotes();
			SecurityCraft.network.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), SyncSSSSettingsOnServer.DataType.CLEAR_NOTES));
			playButton.enabled = false;
			clearButton.enabled = false;
		}));

		soundButton = addButton(new TogglePictureButton(4, width / 2 + 55, height / 2 + 52, 20, 20, STREAMER_ICONS, new int[]{0, 0}, new int[]{32, 48}, 2, 16, 16, 16, 16, 16, 64, 2, button -> {
			boolean toggledPing = !te.pings();

			te.setPings(toggledPing);
			SecurityCraft.network.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), toggledPing ? SyncSSSSettingsOnServer.DataType.SOUND_ON : SyncSSSSettingsOnServer.DataType.SOUND_OFF));
		}));
		soundButton.setCurrentIndex(!te.pings() ? 1 : 0); // Use the disabled mic icon if the SSS is not emitting sounds

		powerButton.enabled = isOwner;
		recordingButton.enabled = isActive && isOwner;
		soundButton.enabled = isActive && isOwner;
		playButton.enabled = isActive && hasNotes;
		clearButton.enabled = isActive && hasNotes && isOwner;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button instanceof ClickButton)
			((ClickButton)button).onClick();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String soundText = SOUND_TEXT.getFormattedText();
		int textWidth = fontRenderer.getStringWidth(title);
		int soundTextLength = fontRenderer.getStringWidth(soundText);

		fontRenderer.drawString(title, xSize / 2 - textWidth / 2, 6, 4210752);
		fontRenderer.drawString(soundText, ySize / 2 + 50 - soundTextLength, 141, 4210752);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private String getRecordingString(boolean recording)
	{
		return (recording ? Utils.localize("gui.securitycraft:sonic_security_system.stop_recording") : Utils.localize("gui.securitycraft:sonic_security_system.start_recording")).getFormattedText();
	}

	private String getPowerString(boolean on)
	{
		return (on ? Utils.localize("gui.securitycraft:sonic_security_system.power.on") : Utils.localize("gui.securitycraft:sonic_security_system.power.off")).getFormattedText();
	}

}
