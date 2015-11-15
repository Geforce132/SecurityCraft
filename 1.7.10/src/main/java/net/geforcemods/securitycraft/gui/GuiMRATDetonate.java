package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.tileentity.TileEntityMRAT;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiMRATDetonate extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack item;
	private GuiButton[] buttons = new GuiButton[6];

	public GuiMRATDetonate(InventoryPlayer inventory, TileEntityMRAT tile_entity, ItemStack item) {
        super(new ContainerGeneric(inventory, tile_entity));
        this.item = item;
	}
	
	public void initGui(){
    	super.initGui();
    	for(int i = 1; i < 7; i++){    		
    		this.buttons[i - 1] = new GuiButton(i - 1, this.width / 2 - 49 - 25, this.height / 2 - 7 - 60  + ((i - 1) * 25), 149, 20, StatCollector.translateToLocal("gui.mrat.notBound"));
    		this.buttons[i - 1].enabled = false;
    		
    		if(this.item.getItem() != null && this.item.getItem() == mod_SecurityCraft.remoteAccessMine && this.item.stackTagCompound != null &&  this.item.stackTagCompound.getIntArray("mine" + i) != null && this.item.stackTagCompound.getIntArray("mine" + i).length > 0){
    			int[] coords = this.item.stackTagCompound.getIntArray("mine" + i);
    			
    			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
    				this.buttonList.add(this.buttons[i - 1]);
    				continue;
    			}
    			
    			this.buttons[i - 1].displayString = StatCollector.translateToLocal("gui.mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(coords[0], coords[1], coords[2]));
    			this.buttons[i - 1].enabled = (mc.theWorld.getBlock(coords[0], coords[1], coords[2]) instanceof IExplosive && (!((IExplosive) mc.theWorld.getBlock(coords[0], coords[1], coords[2])).isDefusable() || ((IExplosive) mc.theWorld.getBlock(coords[0], coords[1], coords[2])).isActive(Minecraft.getMinecraft().theWorld, coords[0], coords[1], coords[2]))) ? true : false;
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
        this.fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("gui.mrat.detonate"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.mrat.detonate")) / 2, 6, 4210752);
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
    	int[] coords = this.item.stackTagCompound.getIntArray("mine" + (guibutton.id + 1));
    	
    	if(Minecraft.getMinecraft().theWorld.getBlock(coords[0], coords[1], coords[2]) instanceof IExplosive){
    		mod_SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "detonate"));
    	}
		
		this.updateButton(guibutton);
		
		this.removeTagFromItemAndUpdate(item, coords[0], coords[1], coords[2], Minecraft.getMinecraft().thePlayer);
    }
    
    @SideOnly(Side.CLIENT)
    private void removeTagFromItemAndUpdate(ItemStack par1ItemStack, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
    	if(par1ItemStack.stackTagCompound == null){
			return;
		}
		
		for(int i = 1; i <= 6; i++){
			if(par1ItemStack.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.stackTagCompound.getIntArray("mine" + i);
				
				if(coords[0] == par2 && coords[1] == par3 && coords[2] == par4){
					par1ItemStack.stackTagCompound.setIntArray("mine" + i, new int[]{0, 0, 0});
					mod_SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(par1ItemStack));
					return;
				}
			}else{
				continue;
			}
		}
    		
    	return;
	}

	private void updateButton(GuiButton guibutton) {
		guibutton.enabled = false;
		guibutton.displayString = guibutton.enabled ? "" : StatCollector.translateToLocal("gui.mrat.detonated");
	}
	
}
