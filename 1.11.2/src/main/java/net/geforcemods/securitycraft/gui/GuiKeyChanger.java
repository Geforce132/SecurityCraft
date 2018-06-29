package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKeyChanger extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private GuiTextField textboxNewPasscode;
	private GuiTextField textboxConfirmPasscode;
	private GuiButton confirmButton;

	private TileEntity tileEntity;

	public GuiKeyChanger(InventoryPlayer inventoryPlayer, TileEntity tile_entity) {
		super(new ContainerGeneric(inventoryPlayer, null));
		tileEntity = tile_entity;
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(confirmButton = new GuiButton(0, width / 2 - 52, height / 2 + 52, 100, 20, ClientUtils.localize("gui.universalKeyChanger.confirm")));
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
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GlStateManager.disableLighting();
		textboxNewPasscode.drawTextBox();
		textboxConfirmPasscode.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){
		fontRenderer.drawString(ClientUtils.localize("item.universalKeyChanger.name"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("item.universalKeyChanger.name")) / 2, 6, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.universalKeyChanger.enterNewPasscode"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.universalKeyChanger.enterNewPasscode")) / 2, 25, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.universalKeyChanger.confirmNewPasscode"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.universalKeyChanger.confirmNewPasscode")) / 2, 65, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(!isValidChar(par1))
			return;

		if(textboxNewPasscode.isFocused())
			textboxNewPasscode.textboxKeyTyped(par1, par2);
		else if(textboxConfirmPasscode.isFocused())
			textboxConfirmPasscode.textboxKeyTyped(par1, par2);
		else
			super.keyTyped(par1, par2);

		checkToEnableSaveButton();
	}

	private boolean isValidChar(char par1) {
		for(int x = 1; x <= allowedChars.length; x++)
			if(par1 == allowedChars[x - 1])
				return true;
			else
				continue;

		return false;
	}

	private void checkToEnableSaveButton() {
		String newPasscode = !textboxNewPasscode.getText().isEmpty() ? textboxNewPasscode.getText() : null;
		String confirmedPasscode = !textboxConfirmPasscode.getText().isEmpty() ? textboxConfirmPasscode.getText() : null;

		if(newPasscode == null || confirmedPasscode == null) return;
		if(!newPasscode.matches(confirmedPasscode)) return;

		confirmButton.enabled = true;
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException {
		super.mouseClicked(par1, par2, par3);
		textboxNewPasscode.mouseClicked(par1, par2, par3);
		textboxConfirmPasscode.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getText());
				SecurityCraft.network.sendToServer(new PacketSSetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), textboxNewPasscode.getText()));

				ClientUtils.closePlayerScreen();
				PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, ClientUtils.localize("item.universalKeyChanger.name"), ClientUtils.localize("messages.universalKeyChanger.passcodeChanged"), TextFormatting.GREEN);
		}
	}

}
