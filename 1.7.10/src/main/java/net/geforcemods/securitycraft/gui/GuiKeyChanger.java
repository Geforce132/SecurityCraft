package net.geforcemods.securitycraft.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiKeyChanger extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private GuiTextField textboxNewPasscode;
	private GuiTextField textboxConfirmPasscode;
	private GuiButton confirmButton;

	private TileEntity tileEntity;

	public GuiKeyChanger(InventoryPlayer inventoryPlayer, TileEntity te) {
		super(new ContainerGeneric(inventoryPlayer, null));
		tileEntity = te;
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(confirmButton = new GuiButton(0, width / 2 - 52, height / 2 + 52, 100, 20, StatCollector.translateToLocal("gui.securitycraft:universalKeyChanger.confirm")));
		confirmButton.enabled = false;

		textboxNewPasscode = new GuiTextField(fontRendererObj, width / 2 - 57, height / 2 - 47, 110, 12);

		textboxNewPasscode.setTextColor(-1);
		textboxNewPasscode.setDisabledTextColour(-1);
		textboxNewPasscode.setEnableBackgroundDrawing(true);
		textboxNewPasscode.setMaxStringLength(20);

		textboxConfirmPasscode = new GuiTextField(fontRendererObj, width / 2 - 57, height / 2 - 7, 110, 12);

		textboxConfirmPasscode.setTextColor(-1);
		textboxConfirmPasscode.setDisabledTextColour(-1);
		textboxConfirmPasscode.setEnableBackgroundDrawing(true);
		textboxConfirmPasscode.setMaxStringLength(20);

	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_LIGHTING);
		textboxNewPasscode.drawTextBox();
		textboxConfirmPasscode.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(StatCollector.translateToLocal("item.securitycraft:universalKeyChanger.name"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("item.securitycraft:universalKeyChanger.name")) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:universalKeyChanger.enterNewPasscode"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.securitycraft:universalKeyChanger.enterNewPasscode")) / 2, 25, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:universalKeyChanger.confirmNewPasscode"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.securitycraft:universalKeyChanger.confirmNewPasscode")) / 2, 65, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char charTyped, int keyCode){
		if(!isValidChar(charTyped))
			return;

		if(textboxNewPasscode.isFocused())
			textboxNewPasscode.textboxKeyTyped(charTyped, keyCode);
		else if(textboxConfirmPasscode.isFocused())
			textboxConfirmPasscode.textboxKeyTyped(charTyped, keyCode);
		else
			super.keyTyped(charTyped, keyCode);

		checkToEnableSaveButton();
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
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
		super.mouseClicked(mouseX, mouseY, mouseButton);
		textboxNewPasscode.mouseClicked(mouseX, mouseY, mouseButton);
		textboxConfirmPasscode.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0:
				((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getText());
				SecurityCraft.network.sendToServer(new PacketSSetPassword(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, textboxNewPasscode.getText()));

				ClientUtils.closePlayerScreen();
				PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().thePlayer, StatCollector.translateToLocal("item.securitycraft:universalKeyChanger.name"), StatCollector.translateToLocal("messages.securitycraft:universalKeyChanger.passcodeChanged"), EnumChatFormatting.GREEN);
		}
	}

}
