package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetKeycardLevel;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiKeycardSetup extends GuiContainer{
	
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityKeycardReader keypadInventory;
	private GuiButton lvlOfSecurityButton;
	private GuiButton requiresExactCardButton;
	private boolean requiresExactCard = false;
	private int lvlOfSecurity = 0;

	public GuiKeycardSetup(InventoryPlayer inventory, TileEntityKeycardReader tile_entity) {
		 super(new ContainerGeneric(inventory, tile_entity));
	     this.keypadInventory = tile_entity;
	}
	
	public void initGui(){
    	super.initGui();

		this.buttonList.add(this.lvlOfSecurityButton = new GuiButton(0, this.width / 2 - (48 * 2 - 23), this.height / 2 + 20, 150, 20, ""));
		this.buttonList.add(this.requiresExactCardButton = new GuiButton(1, this.width / 2 - (48 * 2 - 11), this.height / 2 - 28, 125, 20, this.requiresExactCard ? StatCollector.translateToLocal("gui.keycardSetup.equal") : StatCollector.translateToLocal("gui.keycardSetup.equalOrHigher")));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 48, this.height / 2 + 30 + 20, 100, 20, StatCollector.translateToLocal("gui.keycardSetup.save")));
		
		this.updateButtonText();
    }
	
	 /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
    	this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.1"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.keycardSetup.explanation.1")) / 2, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.2"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.keycardSetup.explanation.2")) / 2 - 2, 30 - 10, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.3"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.keycardSetup.explanation.3")) / 2 - 11, 42 - 10, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.4"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.keycardSetup.explanation.4")) / 2 - 10, 54 - 10, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.5"), this.xSize / 2 + 45, 66 - 5, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.6"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.keycardSetup.explanation.6")) / 2 - 6, 78 - 1, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.keycardSetup.explanation.7"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.keycardSetup.explanation.7")) / 2 - 20, 90 - 1, 4210752);
    }

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
	
	 private void updateButtonText(){
		 this.lvlOfSecurity++;
		 if(this.lvlOfSecurity <= 5){
			 this.lvlOfSecurityButton.displayString = StatCollector.translateToLocal("gui.keycardSetup.lvlNeeded") + " " + this.lvlOfSecurity;
		 }else{
			 this.lvlOfSecurity = 1;
			 this.lvlOfSecurityButton.displayString = StatCollector.translateToLocal("gui.keycardSetup.lvlNeeded") + " " + this.lvlOfSecurity;

		 }
	 }
	 
	 protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				this.updateButtonText();
				break;
			
			case 1:
				this.requiresExactCard = !this.requiresExactCard;
				this.requiresExactCardButton.displayString = this.requiresExactCard ? StatCollector.translateToLocal("gui.keycardSetup.equal") : StatCollector.translateToLocal("gui.keycardSetup.equalOrHigher");
				break;
				
			case 2:
				this.saveLVs();
				break;
		}
	 }

	private void saveLVs() {
		keypadInventory.setPassword(String.valueOf(lvlOfSecurity));
		keypadInventory.setRequiresExactKeycard(requiresExactCard);
		
		mod_SecurityCraft.network.sendToServer(new PacketSetKeycardLevel(keypadInventory.xCoord, keypadInventory.yCoord, keypadInventory.zCoord, this.lvlOfSecurity, this.requiresExactCard));
		
		Minecraft.getMinecraft().thePlayer.closeScreen();
	}

}
