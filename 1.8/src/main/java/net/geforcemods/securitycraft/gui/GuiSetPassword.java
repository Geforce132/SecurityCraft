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
import net.minecraft.util.StatCollector;
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
		this.blockName = StatCollector.translateToLocal(block.getUnlocalizedName() + ".name");
	}

	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(this.saveAndContinueButton = new GuiButton(0, this.width / 2 - 48, this.height / 2 + 30 + 10, 100, 20, !this.flag ? StatCollector.translateToLocal("gui.keycardSetup.save") : StatCollector.translateToLocal("gui.password.invalidCode")));

		this.keycodeTextbox = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 37, this.height / 2 - 47, 77, 12);

		this.keycodeTextbox.setTextColor(-1);
		this.keycodeTextbox.setDisabledTextColour(-1);
		this.keycodeTextbox.setEnableBackgroundDrawing(true);
		this.keycodeTextbox.setMaxStringLength(11);

		this.updateButtonText();
	}
	
	public void onGuiClosed(){
		super.onGuiClosed();
		this.flag = false;
		Keyboard.enableRepeatEvents(false);
	}
    
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.keycodeTextbox.drawTextBox();
		this.drawString(this.fontRendererObj, "CODE:", this.width / 2 - 67, this.height / 2 - 47 + 2, 4210752);		
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2){
    	
    	//If the "*blockName* + setup" string goes outside of the GUI, draw the word "setup" on the next line. TODO: change to drawSplitString
    	if(this.fontRendererObj.getStringWidth(blockName + " " + StatCollector.translateToLocal("gui.password.setup")) >= 170){
            this.fontRendererObj.drawString(blockName, this.xSize / 2 - this.fontRendererObj.getStringWidth(blockName) / 2, 6, 4210752);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.password.setup"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.password.setup")) / 2, 16, 4210752);
    	}else{
            this.fontRendererObj.drawString(blockName + " " + StatCollector.translateToLocal("gui.password.setup"), this.xSize / 2 - this.fontRendererObj.getStringWidth(blockName + " " + StatCollector.translateToLocal("gui.password.setup")) / 2, 6, 4210752);
    	}
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
    
    protected void keyTyped(char par1, int par2) throws IOException{
		if(this.keycodeTextbox.isFocused() && isValidChar(par1)){
			this.keycodeTextbox.textboxKeyTyped(par1, par2);
		}else{
			super.keyTyped(par1, par2);
		}
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
    
    protected void mouseClicked(int par1, int par2, int par3) throws IOException{
		super.mouseClicked(par1, par2, par3);
		this.keycodeTextbox.mouseClicked(par1, par2, par3);
	}

    private void updateButtonText(){
    	this.saveAndContinueButton.displayString = !this.flag ? StatCollector.translateToLocal("gui.keycardSetup.save") : StatCollector.translateToLocal("gui.password.invalidCode");
    }

    protected void actionPerformed(GuiButton guibutton){
    	switch(guibutton.id){
    	case 0:
    		if(this.keycodeTextbox.getText().isEmpty()){
    			this.flag  = true;
    			this.updateButtonText();
    			return;
    		}
    		
    		((IPasswordProtected) tileEntity).setPassword(this.keycodeTextbox.getText());
    		mod_SecurityCraft.network.sendToServer(new PacketSSetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), this.keycodeTextbox.getText()));
    			
    		ClientUtils.closePlayerScreen();
    		Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, tileEntity.getWorld(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());	
    	}
    }	

}
