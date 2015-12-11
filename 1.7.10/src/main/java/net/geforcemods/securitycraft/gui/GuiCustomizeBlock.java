package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.gui.components.GuiItemButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiCustomizeBlock extends GuiContainer{
	
    private CustomizableSCTE tileEntity;
    private GuiItemButton[] buttons = new GuiItemButton[5];
    private HoverChecker[] hoverCheckers = new HoverChecker[5];
    
    private final String blockName;
    
    public GuiCustomizeBlock(InventoryPlayer par1InventoryPlayer, CustomizableSCTE par2TileEntity)
    {
        super(new ContainerCustomizeBlock(par1InventoryPlayer, par2TileEntity));
        this.tileEntity = par2TileEntity;
        this.blockName = Minecraft.getMinecraft().theWorld.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord).getUnlocalizedName().substring(5);
    }
    
    public void initGui(){
    	super.initGui();

    	for(int i = 0; i < tileEntity.getNumberOfCustomizableOptions(); i++){
    		buttons[i] = new GuiItemButton(i, guiLeft + 130, (guiTop + 10) + (i * 25), 20, 20, "", itemRender, new ItemStack(tileEntity.acceptedModules()[i].getItem()));
    		this.buttonList.add(buttons[i]);
    		this.hoverCheckers[i] = new HoverChecker(buttons[i], 20);
    	}
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	
    	for(int i = 0; i < hoverCheckers.length; i++){
    		if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
    			this.drawHoveringText(this.mc.fontRenderer.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, this.mc.fontRenderer);
    		}
    	}
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = this.tileEntity.hasCustomInventoryName() ? this.tileEntity.getInventoryName() : I18n.format(this.tileEntity.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("securitycraft:textures/gui/container/customize" + tileEntity.getNumberOfCustomizableOptions() + ".png"));
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
    
    private String getModuleDescription(int buttonID) {
    	String moduleDescription = "module." + blockName + "." + buttons[buttonID].getItemStack().getUnlocalizedName().substring(5) + ".description";
    	
    	return StatCollector.translateToLocal(buttons[buttonID].getItemStack().getUnlocalizedName() + ".name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal(moduleDescription);
    }
    
}