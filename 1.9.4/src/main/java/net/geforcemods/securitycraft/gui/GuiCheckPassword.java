package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSCheckPassword;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCheckPassword extends GuiContainer {
	
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', ''};
	private String blockName;
	
	private GuiTextField keycodeTextbox;
	private String currentString = "";

	public GuiCheckPassword(InventoryPlayer inventoryPlayer, TileEntity tileEntity, Block block){
        super(new ContainerGeneric(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;   
		this.blockName = I18n.translateToLocal(block.getUnlocalizedName() + ".name");
    }
    
    public void initGui(){
    	super.initGui();	
    	Keyboard.enableRepeatEvents(true);
    	
		this.buttonList.add(new GuiButton(0, this.width / 2 - 38, this.height / 2 + 30 + 10, 80, 20, "0"));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 38, this.height / 2 - 60 + 10, 20, 20, "1"));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 8, this.height / 2 - 60 + 10, 20, 20, "2"));
		this.buttonList.add(new GuiButton(3, this.width / 2 + 22, this.height / 2 - 60 + 10, 20, 20, "3"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 38, this.height / 2 - 30 + 10, 20, 20, "4"));
		this.buttonList.add(new GuiButton(5, this.width / 2 - 8, this.height / 2 - 30 + 10, 20, 20, "5"));
		this.buttonList.add(new GuiButton(6, this.width / 2 + 22, this.height / 2 - 30 + 10, 20, 20, "6"));
		this.buttonList.add(new GuiButton(7, this.width / 2 - 38, this.height / 2 + 10, 20, 20, "7"));
		this.buttonList.add(new GuiButton(8, this.width / 2 - 8, this.height / 2 + 10, 20, 20, "8"));
		this.buttonList.add(new GuiButton(9, this.width / 2 + 22, this.height / 2 + 10, 20, 20, "9"));
		this.buttonList.add(new GuiButton(10, this.width / 2 + 48, this.height / 2 + 30 + 10, 25, 20, "<-"));
		
		this.keycodeTextbox = new GuiTextField(11, this.fontRendererObj, this.width / 2 - 37, this.height / 2 - 67, 77, 12);
		
		this.keycodeTextbox.setTextColor(-1);
		this.keycodeTextbox.setDisabledTextColour(-1);
		this.keycodeTextbox.setEnableBackgroundDrawing(true);
		this.keycodeTextbox.setMaxStringLength(11);
    }
    
    public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
    
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.keycodeTextbox.drawTextBox();
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2){
        this.fontRendererObj.drawString(blockName, this.xSize / 2 - this.fontRendererObj.getStringWidth(blockName) / 2, 6, 4210752);
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
     
    protected void keyTyped(char par1, int par2) throws IOException {
		if(this.isValidChar(par1) && par1 != ''){
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			this.currentString += par1;
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);			
		}else if(this.isValidChar(par1) && par1 == ''){
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			this.currentString = Utils.removeLastChar(currentString);
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);
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
    
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
		case 0:
			this.currentString += "0";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);
			break;
		case 1:
			this.currentString += "1";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);
			break;
		case 2:
			this.currentString += "2";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);				
			break;
		case 3:
			this.currentString += "3";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);	
			break;
		case 4:
			this.currentString += "4";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);			
			break;
		case 5:
			this.currentString += "5";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);		
			break;	
		case 6:
			this.currentString += "6";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);				
			break;
		case 7:
			this.currentString += "7";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);	
			break;
		case 8:
			this.currentString += "8";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);			
			break;
		case 9:
			this.currentString += "9";
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			this.checkCode(this.currentString);			
			break;
			
		case 10:
			this.currentString = Utils.removeLastChar(currentString);
			this.setTextboxCensoredText(this.keycodeTextbox, currentString);
			break;
		
		}
	}

	private void setTextboxCensoredText(GuiTextField textField, String par2) {
		String x = "";
		for(int i = 1; i <= par2.length(); i++){
			x += "*";
		}
		
		textField.setText(x);
	}

	public void checkCode(String par1String) {
		mod_SecurityCraft.network.sendToServer(new PacketSCheckPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), par1String));		
	}
	
}
