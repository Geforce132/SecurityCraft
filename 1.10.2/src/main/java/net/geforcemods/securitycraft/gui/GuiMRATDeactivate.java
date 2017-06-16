package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class GuiMRATDeactivate extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack item;
	private GuiButton[] buttons = new GuiButton[6];

	public GuiMRATDeactivate(InventoryPlayer inventory, ItemStack item) {
        super(new ContainerGeneric(inventory, null));
        this.item = item;
	}
	
	@Override
	public void initGui(){
    	super.initGui();
    	for(int i = 1; i < 7; i++){    		
    		this.buttons[i - 1] = new GuiButton(i - 1, this.width / 2 - 49 - 25, this.height / 2 - 7 - 60  + ((i - 1) * 25), 149, 20, I18n.format("gui.mrat.notBound"));
    		this.buttons[i - 1].enabled = false;
    		
    		if(this.item.getItem() != null && this.item.getItem() == mod_SecurityCraft.remoteAccessMine && this.item.getTagCompound() != null &&  this.item.getTagCompound().getIntArray("mine" + i) != null && this.item.getTagCompound().getIntArray("mine" + i).length > 0){
    			int[] coords = this.item.getTagCompound().getIntArray("mine" + i);
    			
    			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
    				this.buttonList.add(this.buttons[i - 1]);
    				continue;
    			}
    			
    			this.buttons[i - 1].displayString = I18n.format("gui.mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])));
    			this.buttons[i - 1].enabled = (BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2]) instanceof IExplosive && ((IExplosive) BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2])).isDefusable() && ((IExplosive) BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2])).isActive(mc.theWorld, BlockUtils.toPos(coords[0], coords[1], coords[2]))) ? true : false;
    			this.buttons[i - 1].id = i - 1;
    		}
    		
    		this.buttonList.add(this.buttons[i - 1]);
    	}
    }

	
	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
	}

	/**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){
        this.fontRendererObj.drawString(TextFormatting.UNDERLINE + I18n.format("gui.mrat.deactivate"), this.xSize / 2 - this.fontRendererObj.getStringWidth(I18n.format("gui.mrat.detonate")) / 2, 6, 4210752);
    }
    
	/**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_110410_t);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
	protected void actionPerformed(GuiButton guibutton){
    	int[] coords = this.item.getTagCompound().getIntArray("mine" + (guibutton.id + 1));

    	if(BlockUtils.getBlock(mc.theWorld, coords[0], coords[1], coords[2]) instanceof IExplosive){
    		mod_SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "defuse"));
    	}
		
		this.updateButton(guibutton);
    }
    
    private void updateButton(GuiButton guibutton) {
		guibutton.enabled = false;
		guibutton.displayString = guibutton.enabled ? "" : I18n.format("gui.mrat.deactivated");
	}
    
}
