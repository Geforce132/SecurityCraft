package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class GuiMRAT extends GuiContainer{
	
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiMRAT(InventoryPlayer inventory) {
        super(new ContainerGeneric(inventory, null));
	}
	
	@Override
	public void initGui(){
    	super.initGui();

    	int j = (this.height - this.height) / 2;
    	
		this.buttonList.add(new GuiButton(0, this.width / 2 - 49, this.height / 2 - 7 - 50, 99, 20, I18n.format("gui.mrat.activate")));
		this.buttonList.add(new GuiButton(1, this.width / 2 - 49, this.height / 2 - 7, 99, 20, I18n.format("gui.mrat.deactivate")));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 49, this.height / 2 - 7 + 50, 99, 20, I18n.format("gui.mrat.detonate")));
    }

	/**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString(I18n.format("gui.mrat.name"), this.xSize / 2 - this.fontRendererObj.getStringWidth(I18n.format("gui.mrat.name")) / 2, 6, 4210752);
    }
    
	/**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
    
	 @Override
	protected void actionPerformed(GuiButton guibutton){
		 switch(guibutton.id){
		 case 0:
			 Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_ACTIVATE_ID, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
			 break;
			 
		 case 1:
			 Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_DEACTIVATE_ID, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
			 break;
			 
		 case 2:
			 Minecraft.getMinecraft().thePlayer.openGui(mod_SecurityCraft.instance, GuiHandler.MRAT_DETONATE_ID, Minecraft.getMinecraft().theWorld, (int) Minecraft.getMinecraft().thePlayer.posX, (int) Minecraft.getMinecraft().thePlayer.posY, (int) Minecraft.getMinecraft().thePlayer.posZ);
			 break;
			 
		 }
	 }
}
