package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.containers.ContainerRetinalScannerSetup;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Deprecated
//TODO
public class GuiRetinalScannerSetup extends GuiContainer
{
    private static final ResourceLocation field_110410_t = new ResourceLocation("textures/gui/container/blank.png");

	private GuiTextField textboxAllowedPlayers;

	public GuiRetinalScannerSetup(InventoryPlayer par1InventoryPlayer, TileEntityOwnable par2TileEntityFurnace)
    {
        super(new ContainerRetinalScannerSetup(par1InventoryPlayer, par2TileEntityFurnace));
    }
    
    public void initGui(){
    	super.initGui();
		Keyboard.enableRepeatEvents(true);

    	int j = (this.height - this.height) / 2;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 48, this.height / 2 + 30 + 20, 100, 20, "Save & continue."));


		this.textboxAllowedPlayers = new GuiTextField(this.fontRendererObj, this.width / 2 - 37 - 24, this.height / 2 - 23, 125, 12);
//		
		this.textboxAllowedPlayers.setTextColor(-1);
		this.textboxAllowedPlayers.setDisabledTextColour(-1);
		this.textboxAllowedPlayers.setEnableBackgroundDrawing(true);
		this.textboxAllowedPlayers.setMaxStringLength(200);
    }
    
    public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
    
    
    public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.textboxAllowedPlayers.drawTextBox();
		//this.updateButtonText();
		//this.drawString(this.fontRenderer, "Allowed Players:", this.width / 2 - 67, this.height / 2 - 47 + 2, 4210752);
        this.fontRendererObj.drawString("Allowed Players:", this.width / 2 - 38, this.height / 2 - 52, 4210752);
        this.fontRendererObj.drawString("e.g: Me,My_Friend,TheOtherGuy", this.width / 2 - 72, this.height / 2 - 37, 4210752);
        this.fontRendererObj.drawString("Type the usernames of the", this.width / 2 - 66, this.height / 2 - 5, 4210752);
        this.fontRendererObj.drawString("players allowed to open this", this.width / 2 - 68, this.height / 2 + 8, 4210752);
        this.fontRendererObj.drawString("retinal scanner separated by", this.width / 2 - 71, this.height / 2 + 21, 4210752);
        this.fontRendererObj.drawString("a comma.", this.width / 2 - 21, this.height / 2 + 34, 4210752);

    }
    
    
    protected void keyTyped(char par1, int par2){
		if(this.textboxAllowedPlayers.textboxKeyTyped(par1, par2)){
			//this.mc.thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload("SecurityCraft", this.textboxAllowedPlayers.getText().getBytes()));
		}
		
		else{
			super.keyTyped(par1, par2);
		}
	
	}
    
    protected void mouseClicked(int par1, int par2, int par3){
		super.mouseClicked(par1, par2, par3);
		this.textboxAllowedPlayers.mouseClicked(par1, par2, par3);
		

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
		switch(guibutton.id){
		case 0:
			String players = this.textboxAllowedPlayers.getText();
			World W = Minecraft.getMinecraft().theWorld;
		    MinecraftServer minecraftserver = MinecraftServer.getServer();

			try{
				if(this.mc.isSingleplayer()){
				//String string = "saves/" + BlockRetinalScanner.worldObj.getSaveHandler().getWorldDirectoryName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ;
				//File file = new File(string);
				//file.mkdirs();
				//file.createNewFile();
				//PrintWriter out = new PrintWriter("saves/" + BlockRetinalScanner.worldObj.getSaveHandler().getWorldDirectoryName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ + "/players.txt");		
				//out.println(players);
				//out.close();
				}else{
					//String string = minecraftserver.getWorldName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ;
					//File file = new File(string);
					//file.mkdirs();
					//file.createNewFile();
					//PrintWriter out = new PrintWriter(minecraftserver.getWorldName() + "/SecurityCraft/retScanner/players/" + BlockRetinalScanner.lastScannerX + "-" + BlockRetinalScanner.lastScannerY + "-" + BlockRetinalScanner.lastScannerZ + "/players.txt");		
					//out.println(players);
					//out.close();
				}
				if(this.mc.isSingleplayer()){
					EntityClientPlayerMP P = Minecraft.getMinecraft().thePlayer;
					P.closeScreen();
					P.openGui(mod_SecurityCraft.instance, 3, W, (int) P.posX, (int) P.posY, (int) P.posZ);
				}else{
					//HelpfulMethods.closePlayerScreen(BlockRetinalScanner.playerObj);
					//BlockRetinalScanner.playerObj.openGui(mod_SecurityCraft.instance, 3, W, (int) BlockRetinalScanner.playerObj.posX, (int) BlockRetinalScanner.playerObj.posY, (int) BlockRetinalScanner.playerObj.posZ);

				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		}
	}	
	
	
	
	
	
	

}
