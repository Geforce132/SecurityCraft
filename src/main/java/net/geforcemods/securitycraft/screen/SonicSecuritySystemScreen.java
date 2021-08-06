package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
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

	private final SonicSecuritySystemTileEntity te;
	private int xSize = 176, ySize = 166;
	private IdButton recordingButton;

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public SonicSecuritySystemScreen(SonicSecuritySystemTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
	}

	@Override
	public void init()
	{
		super.init();

		recordingButton = new IdButton(0, width / 2 - 38, height / 2 - 60, 80, 20, getRecordingString(te.isRecording()), this::actionPerformed);

		addButton(recordingButton);
		addButton(new IdButton(1, width / 2 - 38, height / 2 - 20, 80, 20, "Unlink all", this::actionPerformed));
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
		font.drawString(matrix, "Notes recorded: " + te.recordedNotes.size(), startX + xSize / 2 - textWidth / 2, startY + 46, 4210752);
	}

	protected void actionPerformed(IdButton button)
	{
		if(button.id == 0)
		{
			boolean recording = !te.isRecording();

			te.setRecording(recording);
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), recording ? SyncSSSSettingsOnServer.DataType.RECORDING_ON : SyncSSSSettingsOnServer.DataType.RECORDING_OFF));
			recordingButton.setMessage(new StringTextComponent(getRecordingString(te.isRecording())));
		}
		else if(button.id == 1)
		{
			te.clearNotes();
			SecurityCraft.channel.sendToServer(new SyncSSSSettingsOnServer(te.getPos(), SyncSSSSettingsOnServer.DataType.CLEAR_NOTES));
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private String getRecordingString(boolean recording) {
		return recording ? "Recording: on" : "Recording: off";
	}

}
