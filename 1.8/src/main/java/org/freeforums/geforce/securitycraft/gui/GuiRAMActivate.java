package org.freeforums.geforce.securitycraft.gui;

import org.freeforums.geforce.securitycraft.api.IExplosive;
import org.freeforums.geforce.securitycraft.containers.ContainerGeneric;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetExplosiveState;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRAM;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiRAMActivate extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack item;
	private GuiButton[] buttons = new GuiButton[6];

	public GuiRAMActivate(InventoryPlayer inventory, TileEntityRAM tile_entity, ItemStack item) {
        super(new ContainerGeneric(inventory, tile_entity));
        this.item = item;
	}
	
	public void initGui(){
    	super.initGui();
    	for(int i = 1; i < 7; i++){  		
    		this.buttons[i - 1] = new GuiButton(i - 1, this.width / 2 - 49 - 25, this.height / 2 - 7 - 60  + ((i - 1) * 25), 149, 20, "Not bound!");
    		this.buttons[i - 1].enabled = false;
    		
    		if(this.item.getItem() != null && this.item.getItem() == mod_SecurityCraft.remoteAccessMine && this.item.getTagCompound() != null &&  this.item.getTagCompound().getIntArray("mine" + i) != null && this.item.getTagCompound().getIntArray("mine" + i).length > 0){
    			int[] coords = this.item.getTagCompound().getIntArray("mine" + i);
    			
    			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
    				this.buttonList.add(this.buttons[i - 1]);
    				continue;
    			}
    			
    			this.buttons[i - 1].displayString = "Mine at X: " + coords[0] + " Y: " + coords[1] + " Z: " + coords[2];
    			this.buttons[i - 1].enabled = (BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2]) instanceof IExplosive && ((IExplosive) BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2])).isDefusable() && !((IExplosive) BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2])).isActive(mc.theWorld, BlockUtils.toPos(coords[0], coords[1], coords[2]))) ? true : false;
    			this.buttons[i - 1].id = i - 1;
    		}
    		
    		this.buttonList.add(this.buttons[i - 1]);
    	}
    }

	
	public void onGuiClosed(){
		super.onGuiClosed();
	}

	/**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2){
        this.fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + "Activate", this.xSize / 2 - this.fontRendererObj.getStringWidth("Detonate") / 2, 6, 4210752);
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
    
    protected void actionPerformed(GuiButton guibutton){
    	int[] coords = this.item.getTagCompound().getIntArray("mine" + (guibutton.id + 1));
    	
    	if(BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2]) instanceof IExplosive){
    		mod_SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "activate"));
    	}
    	
		this.updateButton(guibutton);	 
    }
    
    private void updateButton(GuiButton guibutton) {
		guibutton.enabled = false;
		guibutton.displayString = guibutton.enabled ? "" : "Activated";
	}
    
}
