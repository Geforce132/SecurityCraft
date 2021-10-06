package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final ResourceLocation STREAMER_ICONS = new ResourceLocation("textures/gui/stream_indicator.png");

	private final SonicSecuritySystemTileEntity te;
	private int xSize = 176, ySize = 166;
	private IdButton recordingButton, clearButton, powerButton;
	private TogglePictureButton soundButton;

	public SonicSecuritySystemScreen(SonicSecuritySystemTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
	}

	@Override
	public void init()
	{
		super.init();

		powerButton = addButton(new IdButton(0, width / 2 - 80, height / 2 - 59, 100, 20, getPowerString(te.isActive()), button -> {
			boolean toggledState = !te.isActive();

			te.setActive(toggledState);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), toggledState ? SyncSSSSettingsOnServer.DataType.POWER_ON : SyncSSSSettingsOnServer.DataType.POWER_OFF));
			powerButton.setMessage(new StringTextComponent(getPowerString(toggledState)));
		}));

		recordingButton = addButton(new IdButton(1, width / 2 - 80, height / 2 - 32, 100, 20, getRecordingString(te.isRecording()), button -> {
			boolean recording = !te.isRecording();

			te.setRecording(recording);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), recording ? SyncSSSSettingsOnServer.DataType.RECORDING_ON : SyncSSSSettingsOnServer.DataType.RECORDING_OFF));
			recordingButton.setMessage(new StringTextComponent(getRecordingString(te.isRecording())));
		}));

		soundButton = addButton(new TogglePictureButton(2, width / 2 - 45, height / 2 + 32, 20, 20, STREAMER_ICONS, new int[]{0, 0}, new int[]{32, 48}, 2, 16, 16, 16, 16, 16, 64, 2, button -> {
			boolean toggledPing = !te.pings();

			te.setPings(toggledPing);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), toggledPing ? SyncSSSSettingsOnServer.DataType.SOUND_ON : SyncSSSSettingsOnServer.DataType.SOUND_OFF));
		}));
		soundButton.setCurrentIndex(!te.pings() ? 1 : 0); // Use the disabled mic icon if the SSS is not emitting sounds

		clearButton = addButton(new IdButton(3, width / 2 - 80, height / 2 - 10, 100, 20, "Clear recording", button -> {
			te.clearNotes();
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), SyncSSSSettingsOnServer.DataType.CLEAR_NOTES));
			clearButton.active = false;
		}));

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

		font.drawString(matrix, "Sound:", startX + 10, startY + 121, 4210752);
		font.drawString(matrix, "Notes recorded: " + te.getNumberOfNotes(), startX + 13, startY + 98, 4210752);
	}

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

	// TODO: Replace hard-coded strings with .lang translations
	private String getRecordingString(boolean recording)
	{
		return recording ? "Recording: on" : "Recording: off";
	}

	private String getPowerString(boolean on)
	{
		return on ? "Power: on" : "Power: off";
	}

	private String getSoundString(boolean emittingSound)
	{
		return emittingSound ? "Ping sound: enabled" : "Ping sound: disabled";
	}

}
