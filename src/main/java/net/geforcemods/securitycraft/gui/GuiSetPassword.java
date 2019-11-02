package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSetPassword extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private String blockName;

	private GuiTextField keycodeTextbox;
	private boolean invalid = false;
	private GuiButton saveAndContinueButton;

	public GuiSetPassword(InventoryPlayer inventoryPlayer, TileEntity tileEntity, Block block){
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		blockName = StatCollector.translateToLocal(block.getUnlocalizedName() + ".name");
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(saveAndContinueButton = new GuiButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, !invalid ? StatCollector.translateToLocal("gui.securitycraft:keycardSetup.save") : StatCollector.translateToLocal("gui.securitycraft:password.invalidCode")));

		keycodeTextbox = new GuiTextField(1, fontRendererObj, width / 2 - 37, height / 2 - 47, 77, 12);

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(11);

		updateButtonText();
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		invalid = false;
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		keycodeTextbox.drawTextBox();
		drawString(fontRendererObj, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){

		//If the "*blockName* + setup" string goes outside of the GUI, draw the word "setup" on the next line.
		if(fontRendererObj.getStringWidth(blockName + " " + StatCollector.translateToLocal("gui.securitycraft:password.setup")) >= 170){
			fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2, 6, 4210752);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:password.setup"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.securitycraft:password.setup")) / 2, 16, 4210752);
		}
		else
			fontRendererObj.drawString(blockName + " " + StatCollector.translateToLocal("gui.securitycraft:password.setup"), xSize / 2 - fontRendererObj.getStringWidth(blockName + " " + StatCollector.translateToLocal("gui.securitycraft:password.setup")) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char charTyped, int keyCode) throws IOException{
		if(keycodeTextbox.isFocused() && isValidChar(charTyped))
			keycodeTextbox.textboxKeyTyped(charTyped, keyCode);
		else
			super.keyTyped(charTyped, keyCode);
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
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		keycodeTextbox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private void updateButtonText(){
		saveAndContinueButton.displayString = !invalid ? StatCollector.translateToLocal("gui.securitycraft:keycardSetup.save") : StatCollector.translateToLocal("gui.securitycraft:password.invalidCode");
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0:
				if(keycodeTextbox.getText().isEmpty()){
					invalid  = true;
					updateButtonText();
					return;
				}

				((IPasswordProtected) tileEntity).setPassword(keycodeTextbox.getText());
				SecurityCraft.network.sendToServer(new PacketSSetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), keycodeTextbox.getText()));

				ClientUtils.closePlayerScreen();
				Minecraft.getMinecraft().thePlayer.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, tileEntity.getWorld(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
		}
	}

}
