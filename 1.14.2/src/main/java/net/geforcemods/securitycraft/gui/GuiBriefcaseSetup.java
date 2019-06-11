package net.geforcemods.securitycraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiBriefcaseSetup extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape

	private GuiTextField keycodeTextbox;
	private boolean flag = false;
	private GuiButton saveAndContinueButton;

	public GuiBriefcaseSetup() {
		super(new ContainerGeneric());
	}

	@Override
	public void initGui() {
		super.initGui();
		mc.keyboardListener.enableRepeatEvents(true);
		addButton(saveAndContinueButton = new GuiButtonClick(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, !flag ? ClientUtils.localize("gui.securitycraft:keycardSetup.save") : ClientUtils.localize("gui.securitycraft:password.invalidCode"), this::actionPerformed));

		keycodeTextbox = new GuiTextField(1, fontRenderer, width / 2 - 37, height / 2 - 47, 77, 12);

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(4);

		updateButtonText();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		flag = false;
		mc.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		keycodeTextbox.drawTextField(mouseX, mouseY, partialTicks);
		drawString(fontRenderer, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:briefcase.setupTitle"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:briefcase.setupTitle")) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(keycodeTextbox.isFocused() && isValidChar(typedChar))
		{
			keycodeTextbox.charTyped(typedChar, keyCode);
			return true;
		}
		else
			return super.charTyped(typedChar, keyCode);
	}

	private boolean isValidChar(char c) {
		for(int x = 1; x <= allowedChars.length; x++)
			if(c == allowedChars[x - 1])
				return true;
			else
				continue;

		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		keycodeTextbox.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private void updateButtonText() {
		saveAndContinueButton.displayString = !flag ? ClientUtils.localize("gui.securitycraft:keycardSetup.save") : ClientUtils.localize("gui.securitycraft:password.invalidCode");
	}

	protected void actionPerformed(GuiButton button) {
		switch(button.id){
			case 0:
				if(keycodeTextbox.getText().length() < 4) {
					flag  = true;
					updateButtonText();
					return;
				}

				if(PlayerUtils.isHoldingItem(Minecraft.getInstance().player, SCContent.briefcase)) {
					if(Minecraft.getInstance().player.inventory.getCurrentItem().getTag() == null)
						Minecraft.getInstance().player.inventory.getCurrentItem().setTag(new CompoundNBT());

					Minecraft.getInstance().player.inventory.getCurrentItem().getTag().putString("passcode", keycodeTextbox.getText());
					ClientUtils.syncItemNBT(Minecraft.getInstance().player.inventory.getCurrentItem());
					SecurityCraft.channel.sendToServer(new OpenGui(GuiHandler.BRIEFCASE_INSERT, Minecraft.getInstance().player.getPosition()));
				}
		}
	}

}
