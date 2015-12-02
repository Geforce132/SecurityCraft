package net.geforcemods.securitycraft.gui;

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
import net.minecraft.util.StatCollector;

public class GuiBriefcaseSetup extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', ''};

	private GuiTextField keycodeTextbox;
	private boolean flag = false;
	private GuiButton saveAndContinueButton;
		
	public GuiBriefcaseSetup(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
	}
	
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(this.saveAndContinueButton = new GuiButton(0, this.width / 2 - 48, this.height / 2 + 30 + 10, 100, 20, !this.flag ? StatCollector.translateToLocal("gui.keycardSetup.save") : StatCollector.translateToLocal("gui.password.invalidCode")));

		this.keycodeTextbox = new GuiTextField(this.fontRendererObj, this.width / 2 - 37, this.height / 2 - 47, 77, 12);

		this.keycodeTextbox.setTextColor(-1);
		this.keycodeTextbox.setDisabledTextColour(-1);
		this.keycodeTextbox.setEnableBackgroundDrawing(true);
		this.keycodeTextbox.setMaxStringLength(4);

		this.updateButtonText();
	}
	
	public void onGuiClosed() {
		super.onGuiClosed();
		this.flag = false;
		Keyboard.enableRepeatEvents(false);
	}
    
    public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.keycodeTextbox.drawTextBox();
		this.drawString(this.fontRendererObj, "CODE:", this.width / 2 - 67, this.height / 2 - 47 + 2, 4210752);		
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.briefcase.setupTitle"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.briefcase.setupTitle")) / 2, 6, 4210752);
    }

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
	
	protected void keyTyped(char par1, int par2) {
		if(this.keycodeTextbox.isFocused() && isValidChar(par1)) {
			this.keycodeTextbox.textboxKeyTyped(par1, par2);
		}else{
			super.keyTyped(par1, par2);
		}
	}
    
    private boolean isValidChar(char par1) {
		for(int x = 1; x <= this.allowedChars.length; x++) {
			if(par1 == this.allowedChars[x - 1]) {
				return true;
			}else{
				continue;
			}
		}
		
		return false;
	}
    
    protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		this.keycodeTextbox.mouseClicked(par1, par2, par3);
	}

    private void updateButtonText() {
    	this.saveAndContinueButton.displayString = !this.flag ? StatCollector.translateToLocal("gui.keycardSetup.save") : StatCollector.translateToLocal("gui.password.invalidCode");
    }

    protected void actionPerformed(GuiButton guibutton) {
    	switch(guibutton.id){
    	case 0:
    		if(this.keycodeTextbox.getText().isEmpty()) {
    			this.flag  = true;
    			this.updateButtonText();
    			return;
    		}	
    		
    		if(PlayerUtils.isHoldingItem(Minecraft.getMinecraft().thePlayer, mod_SecurityCraft.briefcase)) {
    			if(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().stackTagCompound == null) {
    				Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().stackTagCompound = new NBTTagCompound();
    			}
    			
    			Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().stackTagCompound.setString("passcode", keycodeTextbox.getText());
	    		ClientUtils.syncItemNBT(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem());
	    		Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, GuiHandler.BRIEFCASE_INSERT_CODE_GUI_ID, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
    		}
    	}
    }	

}
