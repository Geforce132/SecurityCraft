package net.geforcemods.securitycraft.screen;

import java.util.Set;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity.NoteWrapper;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.network.PacketDistributor;

public class SonicSecuritySystemScreen extends Screen implements ConnectionAccessor {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/sonic_security_system.png");
	private static final Component SOUND_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.sound");
	/** The number of ticks between each note when playing back a recording **/
	private static final int PLAYBACK_DELAY = 10;
	private final SonicSecuritySystemBlockEntity be;
	private int xSize = 300, ySize = 166, leftPos, topPos;
	private Button recordingButton, clearButton, powerButton, playButton, invertButton;
	private TogglePictureButton soundButton;
	private SSSConnectionList<SonicSecuritySystemScreen> connectionList;
	/** If a recording is currently being played back **/
	private boolean playback = false;
	/** The number of ticks that has elapsed since the last note played **/
	private int tickCount = PLAYBACK_DELAY;
	private int currentNote = 0;
	private boolean isOwner;

	public SonicSecuritySystemScreen(SonicSecuritySystemBlockEntity be) {
		super(be.getName());
		this.be = be;
		isOwner = be.isOwnedBy(Minecraft.getInstance().player);
	}

	@Override
	public void tick() {
		// Play the note combination of this SSS when the player clicks on the play button
		if (playback) {
			tickCount++;

			// Only emit the note sound after a certain delay and if there are still notes to play
			if (tickCount >= PLAYBACK_DELAY) {
				if (currentNote < be.getNumberOfNotes()) {
					NoteWrapper note = be.getRecordedNotes().get(currentNote++);
					NoteBlockInstrument instrument = NoteBlockInstrument.valueOf(note.instrumentName().toUpperCase());
					SoundEvent sound = instrument.hasCustomSound() && !note.customSoundId().isEmpty() ? SoundEvent.createVariableRangeEvent(new ResourceLocation(note.customSoundId())) : instrument.getSoundEvent().value();
					float pitch = instrument.isTunable() ? (float) Math.pow(2.0D, (note.noteID() - 12) / 12.0D) : 1.0F;

					tickCount = 0;
					minecraft.level.playSound(minecraft.player, be.getBlockPos(), sound, SoundSource.RECORDS, 3.0F, pitch);
				}
				// Reset the counters when we are finished playing the final note
				else if (currentNote >= be.getNumberOfNotes()) {
					currentNote = 0;
					playback = false;
				}
			}
		}
	}

	@Override
	public void init() {
		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		boolean isActive = be.isActive();
		boolean hasNotes = be.getNumberOfNotes() > 0;
		int leftPos = (width - xSize) / 2;
		int buttonX = leftPos + xSize - 155;

		powerButton = addRenderableWidget(new Button(buttonX, height / 2 - 59, 150, 20, getPowerString(be.isActive()), button -> {
			boolean toggledState = !be.isActive();
			boolean containsNotes = be.getNumberOfNotes() > 0;

			be.setActive(toggledState);
			PacketDistributor.SERVER.noArg().send(new SyncSSSSettingsOnServer(be.getBlockPos(), toggledState ? SyncSSSSettingsOnServer.DataType.POWER_ON : SyncSSSSettingsOnServer.DataType.POWER_OFF));
			powerButton.setMessage(getPowerString(toggledState));

			if (!toggledState)
				recordingButton.setMessage(getRecordingString(false));

			// Disable the recording-related buttons when the SSS is powered off
			recordingButton.active = toggledState;
			soundButton.active = toggledState;
			playButton.active = toggledState && containsNotes;
			clearButton.active = toggledState && containsNotes;
		}, Button.DEFAULT_NARRATION));

		recordingButton = addRenderableWidget(new Button(buttonX, height / 2 - 37, 150, 20, getRecordingString(be.isRecording()), button -> {
			boolean recording = !be.isRecording();
			be.setRecording(recording);
			PacketDistributor.SERVER.noArg().send(new SyncSSSSettingsOnServer(be.getBlockPos(), recording ? SyncSSSSettingsOnServer.DataType.RECORDING_ON : SyncSSSSettingsOnServer.DataType.RECORDING_OFF));
			recordingButton.setMessage(getRecordingString(be.isRecording()));
		}, Button.DEFAULT_NARRATION));

		playButton = addRenderableWidget(new Button(buttonX, height / 2 - 15, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.play"), button -> {
			// Start playing back any notes that have been recorded
			if (be.getNumberOfNotes() > 0)
				playback = true;
		}, Button.DEFAULT_NARRATION));

		clearButton = addRenderableWidget(new Button(buttonX, height / 2 + 7, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.recording.clear"), button -> {
			be.clearNotes();
			PacketDistributor.SERVER.noArg().send(new SyncSSSSettingsOnServer(be.getBlockPos(), SyncSSSSettingsOnServer.DataType.CLEAR_NOTES));
			playButton.active = false;
			clearButton.active = false;
		}, Button.DEFAULT_NARRATION));

		invertButton = addRenderableWidget(new Button(buttonX, height / 2 + 29, 150, 20, Utils.localize("gui.securitycraft:sonic_security_system.invert_functionality"), button -> {
			be.setDisableBlocksWhenTuneIsPlayed(!be.disablesBlocksWhenTuneIsPlayed());
			updateInvertButtonTooltip();
			PacketDistributor.SERVER.noArg().send(new SyncSSSSettingsOnServer(be.getBlockPos(), SyncSSSSettingsOnServer.DataType.INVERT_FUNCTIONALITY));
		}, Button.DEFAULT_NARRATION));
		updateInvertButtonTooltip();
		//@formatter:off
		soundButton = addRenderableWidget(new TogglePictureButton(buttonX + 130, height / 2 + 52, 20, 20, 2, 16, 16, 2, button -> {
			//@formatter:on
			boolean toggledPing = !be.pings();

			be.setPings(toggledPing);
			PacketDistributor.SERVER.noArg().send(new SyncSSSSettingsOnServer(be.getBlockPos(), toggledPing ? SyncSSSSettingsOnServer.DataType.SOUND_ON : SyncSSSSettingsOnServer.DataType.SOUND_OFF));
		}, new ResourceLocation(SecurityCraft.MODID, "sonic_security_system/sound"), new ResourceLocation(SecurityCraft.MODID, "sonic_security_system/no_sound")));
		soundButton.setCurrentIndex(!be.pings() ? 1 : 0); // Use the disabled mic icon if the SSS is not emitting sounds

		connectionList = addRenderableWidget(new SSSConnectionList<>(this, minecraft, 130, 120, powerButton.getY(), leftPos + 10));

		powerButton.active = !be.isShutDown() && isOwner;
		recordingButton.active = isActive && isOwner;
		soundButton.active = isActive && isOwner;
		playButton.active = isActive && hasNotes;
		clearButton.active = isActive && hasNotes && isOwner;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int textWidth = font.width(title);
		int soundTextLength = font.width(SOUND_TEXT);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, title, leftPos + xSize / 2 - textWidth / 2, topPos + 6, 4210752, false);
		guiGraphics.drawString(font, SOUND_TEXT, soundButton.getX() - soundTextLength - 5, topPos + 141, 4210752, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize, 512, 512);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public Set<BlockPos> getPositions() {
		if (isOwner)
			return be.getLinkedBlocks();
		else
			return Set.of();
	}

	@Override
	public void removePosition(BlockPos pos) {
		be.delink(pos, true);
		connectionList.refreshPositions();
		PacketDistributor.SERVER.noArg().send(new SyncSSSSettingsOnServer(be.getBlockPos(), SyncSSSSettingsOnServer.DataType.REMOVE_POS, pos));
	}

	private Component getRecordingString(boolean recording) {
		return recording ? Utils.localize("gui.securitycraft:sonic_security_system.stop_recording") : Utils.localize("gui.securitycraft:sonic_security_system.start_recording");
	}

	private Component getPowerString(boolean on) {
		return on ? Utils.localize("gui.securitycraft:sonic_security_system.power.on") : Utils.localize("gui.securitycraft:sonic_security_system.power.off");
	}

	private void updateInvertButtonTooltip() {
		invertButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:sonic_security_system.invert.tooltip_" + (be.disablesBlocksWhenTuneIsPlayed() ? "inverted" : "default"))));
	}
}
