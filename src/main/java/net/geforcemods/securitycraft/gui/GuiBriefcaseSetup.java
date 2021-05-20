package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBriefcaseSetup extends GuiContainer implements GuiResponder {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private GuiTextField keycodeTextbox;
	private GuiButton saveAndContinueButton;

	public GuiBriefcaseSetup(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(saveAndContinueButton = new GuiButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, Utils.localize("gui.securitycraft:password.save").getFormattedText()));
		saveAndContinueButton.enabled = false;

		keycodeTextbox = new GuiTextField(1, fontRenderer, width / 2 - 37, height / 2 - 47, 77, 12);
		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(4);
		keycodeTextbox.setFocused(true);
		keycodeTextbox.setGuiResponder(this);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		keycodeTextbox.drawTextBox();
		drawString(fontRenderer, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(Utils.localize("gui.securitycraft:briefcase.setupTitle").getFormattedText(), xSize / 2 - fontRenderer.getStringWidth(Utils.localize("gui.securitycraft:briefcase.setupTitle").getFormattedText()) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode != Keyboard.KEY_ESCAPE && keycodeTextbox.isFocused() && isValidChar(typedChar))
			keycodeTextbox.textboxKeyTyped(typedChar, keyCode);
		else
			super.keyTyped(typedChar, keyCode);
	}

	private boolean isValidChar(char c) {
		for(int i = 0; i < allowedChars.length; i++)
			if(c == allowedChars[i])
				return true;
			else
				continue;

		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		keycodeTextbox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == saveAndContinueButton.id){
			if(PlayerUtils.isHoldingItem(Minecraft.getMinecraft().player, SCContent.briefcase, null)) {
				ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getMinecraft().player, SCContent.briefcase);

				if(!briefcase.hasTagCompound())
					briefcase.setTagCompound(new NBTTagCompound());

				briefcase.getTagCompound().setString("passcode", keycodeTextbox.getText());

				if (!briefcase.getTagCompound().hasKey("owner")) {
					briefcase.getTagCompound().setString("owner", Minecraft.getMinecraft().player.getName());
					briefcase.getTagCompound().setString("ownerUUID", Minecraft.getMinecraft().player.getUniqueID().toString());
				}

				ClientUtils.syncItemNBT(briefcase);
				Minecraft.getMinecraft().player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, Minecraft.getMinecraft().world, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ);
			}
		}
	}

	@Override
	public void setEntryValue(int id, String text)
	{
		saveAndContinueButton.enabled = text.length() == 4;
	}

	@Override
	public void setEntryValue(int id, boolean value) {}

	@Override
	public void setEntryValue(int id, float value) {}
}
