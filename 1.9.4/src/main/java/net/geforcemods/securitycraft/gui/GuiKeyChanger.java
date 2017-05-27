package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
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
		this.tileEntity = tile_entity;
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
	    buttonList.add(confirmButton = new GuiButton(0, this.width / 2 - 52, this.height / 2 + 52, 100, 20, I18n.translateToLocal("gui.universalKeyChanger.confirm")));
	    confirmButton.enabled = false;
		
		textboxNewPasscode = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 57, this.height / 2 - 47, 110, 12);

		textboxNewPasscode.setTextColor(-1);
		textboxNewPasscode.setDisabledTextColour(-1);
		textboxNewPasscode.setEnableBackgroundDrawing(true);
		textboxNewPasscode.setMaxStringLength(20);
		
		textboxConfirmPasscode = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 57, this.height / 2 - 7, 110, 12);

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
		GL11.glDisable(GL11.GL_LIGHTING);
		textboxNewPasscode.drawTextBox();
		textboxConfirmPasscode.drawTextBox();
    }
	
    @Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){	
        this.fontRendererObj.drawString(I18n.translateToLocal("item.universalKeyChanger.name"), this.xSize / 2 - this.fontRendererObj.getStringWidth(I18n.translateToLocal("item.universalKeyChanger.name")) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.translateToLocal("gui.universalKeyChanger.enterNewPasscode"), this.xSize / 2 - this.fontRendererObj.getStringWidth(I18n.translateToLocal("gui.universalKeyChanger.enterNewPasscode")) / 2, 25, 4210752);
        this.fontRendererObj.drawString(I18n.translateToLocal("gui.universalKeyChanger.confirmNewPasscode"), this.xSize / 2 - this.fontRendererObj.getStringWidth(I18n.translateToLocal("gui.universalKeyChanger.confirmNewPasscode")) / 2, 65, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
	
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(!isValidChar(par1))
			return;
		
		if(textboxNewPasscode.isFocused()) {
			textboxNewPasscode.textboxKeyTyped(par1, par2);
		}
		else if(textboxConfirmPasscode.isFocused()) {
			textboxConfirmPasscode.textboxKeyTyped(par1, par2);
		}
		else {
			super.keyTyped(par1, par2);
		}
		
		checkToEnableSaveButton();
	}
	
    private boolean isValidChar(char par1) {
		for(int x = 1; x <= this.allowedChars.length; x++){
			if(par1 == this.allowedChars[x - 1]){
				return true;
			}else{
				continue;
			}
		}
		
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
    		mod_SecurityCraft.network.sendToServer(new PacketSSetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), textboxNewPasscode.getText()));    		

    		ClientUtils.closePlayerScreen();
    		PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().thePlayer, I18n.translateToLocal("item.universalKeyChanger.name"), I18n.translateToLocal("messages.universalKeyChanger.passcodeChanged"), TextFormatting.GREEN);
    	}
    }

}
