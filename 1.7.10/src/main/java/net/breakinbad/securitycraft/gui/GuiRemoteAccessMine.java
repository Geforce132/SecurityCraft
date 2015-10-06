package net.breakinbad.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.breakinbad.securitycraft.containers.ContainerGeneric;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.tileentity.TileEntityRAM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings({"unused", "unchecked"})
public class GuiRemoteAccessMine extends GuiContainer{
	
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityRAM RADInventory;

	public GuiRemoteAccessMine(InventoryPlayer inventory, TileEntityRAM tile_entity) {
        super(new ContainerGeneric(inventory, tile_entity));
        this.RADInventory = tile_entity;
	}
	
	public void initGui(){
    	super.initGui();

    	int j = (this.height - this.height) / 2;
    	
		this.buttonList.add(new GuiButton(0, this.width / 2 - 49, this.height / 2 - 7 - 50, 99, 20, "Activate."));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 49, this.height / 2 - 7, 99, 20, "Deactivate."));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 49, this.height / 2 - 7 + 50, 99, 20, "Detonate!"));
    }

	/**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString("Remote access tool for mines", this.xSize / 2 - this.fontRendererObj.getStringWidth("Remote access tool for mines") / 2, 6, 4210752);
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
			 Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, 6, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
			 break;
			 
		 case 1:
			 Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, 7, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
			 break;
			 
		 case 2:
			 Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, 8, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
			 break;
			 
		 }
	 }
}
