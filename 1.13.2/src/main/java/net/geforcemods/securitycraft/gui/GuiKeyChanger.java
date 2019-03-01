package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiKeyChanger extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private GuiTextField textboxNewPasscode;
	private GuiTextField textboxConfirmPasscode;
	private GuiButton confirmButton;

	private TileEntity tileEntity;

	public GuiKeyChanger(TileEntity tile_entity) {
		super(new ContainerGeneric());
		tileEntity = tile_entity;
	}

	@Override
	public void initGui(){
		super.initGui();
		mc.keyboardListener.enableRepeatEvents(true);
		buttons.add(confirmButton = new GuiButtonClick(0, width / 2 - 52, height / 2 + 52, 100, 20, ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::actionPerformed));
		confirmButton.enabled = false;

		textboxNewPasscode = new GuiTextField(0, fontRenderer, width / 2 - 57, height / 2 - 47, 110, 12);

		textboxNewPasscode.setTextColor(-1);
		textboxNewPasscode.setDisabledTextColour(-1);
		textboxNewPasscode.setEnableBackgroundDrawing(true);
		textboxNewPasscode.setMaxStringLength(20);

		textboxConfirmPasscode = new GuiTextField(1, fontRenderer, width / 2 - 57, height / 2 - 7, 110, 12);

		textboxConfirmPasscode.setTextColor(-1);
		textboxConfirmPasscode.setDisabledTextColour(-1);
		textboxConfirmPasscode.setEnableBackgroundDrawing(true);
		textboxConfirmPasscode.setMaxStringLength(20);

	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		mc.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		textboxNewPasscode.drawTextField(mouseX, mouseY, partialTicks);
		textboxConfirmPasscode.drawTextField(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(ClientUtils.localize("item.securitycraft:universalKeyChanger.name"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("item.securitycraft:universalKeyChanger.name")) / 2, 6, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode")) / 2, 25, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode")) / 2, 65, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		drawDefaultBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(!isValidChar(typedChar))
			return false;

		if(textboxNewPasscode.isFocused())
			textboxNewPasscode.charTyped(typedChar, keyCode);
		else if(textboxConfirmPasscode.isFocused())
			textboxConfirmPasscode.charTyped(typedChar, keyCode);
		else
			return super.charTyped(typedChar, keyCode);

		checkToEnableSaveButton();
		return true;
	}

	private boolean isValidChar(char c) {
		for(int x = 1; x <= allowedChars.length; x++)
			if(c == allowedChars[x - 1])
				return true;
			else
				continue;

		return false;
	}

	private void checkToEnableSaveButton() {
		String newPasscode = !textboxNewPasscode.getText().isEmpty() ? textboxNewPasscode.getText() : null;
		String confirmedPasscode = !textboxConfirmPasscode.getText().isEmpty() ? textboxConfirmPasscode.getText() : null;

		if(newPasscode == null || confirmedPasscode == null) return;
		if(!newPasscode.equals(confirmedPasscode)) return;

		confirmButton.enabled = true;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		textboxNewPasscode.mouseClicked(mouseX, mouseY, mouseButton);
		textboxConfirmPasscode.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0:
				((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getText());
				SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), textboxNewPasscode.getText()));

				ClientUtils.closePlayerScreen();
				PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize("item.securitycraft:universalKeyChanger.name"), ClientUtils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), TextFormatting.GREEN);
		}
	}

}
