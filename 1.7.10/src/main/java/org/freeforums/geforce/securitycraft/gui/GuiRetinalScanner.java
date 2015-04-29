package org.freeforums.geforce.securitycraft.gui;

import java.util.Scanner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.freeforums.geforce.securitycraft.containers.ContainerRetinalScanner;
import org.freeforums.geforce.securitycraft.main.Utils.ClientUtils;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Deprecated
public class GuiRetinalScanner extends GuiContainer{

	private TileEntityOwnable scannerInventory;
    private static final ResourceLocation field_110410_t = new ResourceLocation("textures/gui/container/blank.png");

	public GuiRetinalScanner(InventoryPlayer par1InventoryPlayer, TileEntityOwnable par2TileEntityFurnace)
    {
        super(new ContainerRetinalScanner(par1InventoryPlayer, par2TileEntityFurnace));
        this.scannerInventory = par2TileEntityFurnace;
    }

	public void initGui(){
    	super.initGui();
		Keyboard.enableRepeatEvents(true);

    	int j = (this.height - this.height) / 2;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 48, this.height / 2 + 30 + 20, 100, 20, "Continue."));
		this.buttonList.add(new GuiButton(1, this.width / 4, this.height / 2, 20, 20, "X."));

	}
    
    public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
    
    
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		//this.drawString(this.fontRenderer, "Allowed Players:", this.width / 2 - 67, this.height / 2 - 47 + 2, 4210752);
        this.fontRendererObj.drawString("Allowed Players:", this.width / 2 - 38, this.height / 2 - 52, 4210752);
        this.fontRendererObj.drawString("e.g: Me,My_Friend,TheOtherGuy", this.width / 2 - 72, this.height / 2 - 37, 4210752);
        this.fontRendererObj.drawString("Type the usernames of the", this.width / 2 - 66, this.height / 2 - 5, 4210752);
        this.fontRendererObj.drawString("players allowed to open this", this.width / 2 - 68, this.height / 2 + 8, 4210752);
        this.fontRendererObj.drawString("retinal scanner separated by", this.width / 2 - 71, this.height / 2 + 21, 4210752);
        this.fontRendererObj.drawString("a comma.", this.width / 2 - 21, this.height / 2 + 34, 4210752);

    }
    
    
    protected void keyTyped(char par1, int par2){
//		if(this.textboxAllowedPlayers.textboxKeyTyped(par1, par2)){
//			this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("SecurityCraft", this.textboxAllowedPlayers.getText().getBytes()));
//		}
//		
//		else{
//			super.keyTyped(par1, par2);
//		}
	
	}
    
    protected void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		//this.textboxAllowedPlayers.mouseClicked(par1, par2, par3);
		

	}
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString("Retinal Scanner Setup", this.xSize / 2 - this.fontRendererObj.getStringWidth("Retinal Scanner Setup") / 2, 6, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
       
    }
    
	

	
    
	protected void actionPerformed(GuiButton guibutton){
		EntityPlayer P = Minecraft.getMinecraft().thePlayer;
		Scanner scanner = null;
		

		
		if(guibutton.id == 0){
		
			//try {
				//scanner = new Scanner(new File("saves/" + BlockRetinalScanner.worldObj.getSaveHandler().getWorldDirectoryName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ + "/players.txt"));
			//} catch (FileNotFoundException e) {
			//	e.printStackTrace();
			//}

			if(this.getUsernameFromFile(P.getCommandSenderName()) == P.getCommandSenderName()){
				//System.out.println("true");
				
			}else{
				//System.out.println("false");
				//System.out.println(scanner.findInLine(P.getCommandSenderName()));

			}
		}else if(guibutton.id == 1){
			//try {
				//scanner = new Scanner(new File("saves/" + BlockRetinalScanner.worldObj.getSaveHandler().getWorldDirectoryName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ + "/players.txt"));
			//} catch (FileNotFoundException e) {
			//	e.printStackTrace();
			//}
			
			
			//System.out.println(scanner.findInLine(P.getCommandSenderName()));
			ClientUtils.closePlayerScreen();
			
		}
			
		}
		
	
	private static String getUsernameFromFile(String par1String){
		//try{
			//Scanner scanner = new Scanner(new File("saves/" + BlockRetinalScanner.worldObj.getSaveHandler().getWorldDirectoryName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ + "/players.txt"));
			//return scanner.findInLine(par1String);
		//}catch(FileNotFoundException e){
		//	e.printStackTrace();
		//}
		return "";
	}

	

}
