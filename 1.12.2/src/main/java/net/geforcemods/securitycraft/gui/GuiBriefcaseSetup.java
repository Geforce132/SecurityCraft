package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBriefcaseSetup extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape

	private GuiTextField keycodeTextbox;
	private boolean flag = false;
	private GuiButton saveAndContinueButton;

	public GuiBriefcaseSetup(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(saveAndContinueButton = new GuiButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, !flag ? ClientUtils.localize("gui.keycardSetup.save") : ClientUtils.localize("gui.password.invalidCode")));

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
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		keycodeTextbox.drawTextBox();
		drawString(fontRenderer, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(ClientUtils.localize("gui.briefcase.setupTitle"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.briefcase.setupTitle")) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(keycodeTextbox.isFocused() && isValidChar(par1))
			keycodeTextbox.textboxKeyTyped(par1, par2);
		else
			super.keyTyped(par1, par2);
	}

	private boolean isValidChar(char par1) {
		for(int x = 1; x <= allowedChars.length; x++)
			if(par1 == allowedChars[x - 1])
				return true;
			else
				continue;

		return false;
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException {
		super.mouseClicked(par1, par2, par3);
		keycodeTextbox.mouseClicked(par1, par2, par3);
	}

	private void updateButtonText() {
		saveAndContinueButton.displayString = !flag ? ClientUtils.localize("gui.keycardSetup.save") : ClientUtils.localize("gui.password.invalidCode");
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		switch(guibutton.id){
			case 0:
				if(keycodeTextbox.getText().length() < 4) {
					flag  = true;
					updateButtonText();
					return;
				}

				if(PlayerUtils.isHoldingItem(Minecraft.getMinecraft().player, mod_SecurityCraft.briefcase)) {
					if(Minecraft.getMinecraft().player.inventory.getCurrentItem().getTagCompound() == null)
						Minecraft.getMinecraft().player.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());

					Minecraft.getMinecraft().player.inventory.getCurrentItem().getTagCompound().setString("passcode", keycodeTextbox.getText());
					ClientUtils.syncItemNBT(Minecraft.getMinecraft().player.inventory.getCurrentItem());
					Minecraft.getMinecraft().player.openGui(mod_SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, Minecraft.getMinecraft().world, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ);
				}
		}
	}

}
