package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSetPassword extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private String blockName;

	private GuiTextField keycodeTextbox;
	private boolean flag = false;
	private GuiButton saveAndContinueButton;

	public GuiSetPassword(InventoryPlayer inventoryPlayer, TileEntity tileEntity, Block block){
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		blockName = ClientUtils.localize(block.getUnlocalizedName() + ".name");
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(saveAndContinueButton = new GuiButton(0, width / 2 - 48, height / 2 + 30 + 10, 100, 20, !flag ? ClientUtils.localize("gui.keycardSetup.save") : ClientUtils.localize("gui.password.invalidCode")));

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
		flag = false;
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		keycodeTextbox.drawTextBox();
		drawString(fontRendererObj, "CODE:", width / 2 - 67, height / 2 - 47 + 2, 4210752);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){

		//If the "*blockName* + setup" string goes outside of the GUI, draw the word "setup" on the next line. TODO: change to drawSplitString
		if(fontRendererObj.getStringWidth(blockName + " " + ClientUtils.localize("gui.password.setup")) >= 170){
			fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2, 6, 4210752);
			fontRendererObj.drawString(ClientUtils.localize("gui.password.setup"), xSize / 2 - fontRendererObj.getStringWidth(ClientUtils.localize("gui.password.setup")) / 2, 16, 4210752);
		}
		else
			fontRendererObj.drawString(blockName + " " + ClientUtils.localize("gui.password.setup"), xSize / 2 - fontRendererObj.getStringWidth(blockName + " " + ClientUtils.localize("gui.password.setup")) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException{
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
	protected void mouseClicked(int par1, int par2, int par3) throws IOException{
		super.mouseClicked(par1, par2, par3);
		keycodeTextbox.mouseClicked(par1, par2, par3);
	}

	private void updateButtonText(){
		saveAndContinueButton.displayString = !flag ? ClientUtils.localize("gui.keycardSetup.save") : ClientUtils.localize("gui.password.invalidCode");
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				if(keycodeTextbox.getText().isEmpty()){
					flag  = true;
					updateButtonText();
					return;
				}

				((IPasswordProtected) tileEntity).setPassword(keycodeTextbox.getText());
				mod_SecurityCraft.network.sendToServer(new PacketSSetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), keycodeTextbox.getText()));

				ClientUtils.closePlayerScreen();
				Minecraft.getMinecraft().player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, tileEntity.getWorld(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
		}
	}

}
