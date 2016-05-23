package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.gui.components.GuiItemButton;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomizeBlock extends GuiContainer{
	
    private CustomizableSCTE tileEntity;
    private GuiItemButton[] descriptionButtons = new GuiItemButton[5];
    private GuiButton[] optionButtons = new GuiButton[5];
    private HoverChecker[] hoverCheckers = new HoverChecker[10];
    
    private final String blockName;
    
    public GuiCustomizeBlock(InventoryPlayer par1InventoryPlayer, CustomizableSCTE par2TileEntity)
    {
        super(new ContainerCustomizeBlock(par1InventoryPlayer, par2TileEntity));
        this.tileEntity = par2TileEntity;
        this.blockName = BlockUtils.getBlock(Minecraft.getMinecraft().theWorld, tileEntity.getPos()).getUnlocalizedName().substring(5);
    }
    
    public void initGui(){
    	super.initGui();

    	for(int i = 0; i < tileEntity.getNumberOfCustomizableOptions(); i++){
    		descriptionButtons[i] = new GuiItemButton(i, guiLeft + 130, (guiTop + 10) + (i * 25), 20, 20, "", itemRender, new ItemStack(tileEntity.acceptedModules()[i].getItem()));
    		this.buttonList.add(descriptionButtons[i]);
    		this.hoverCheckers[i] = new HoverChecker(descriptionButtons[i], 20);
    	}
    	
    	if(tileEntity.customOptions() != null) {
	    	for(int i = 0; i < tileEntity.customOptions().length; i++){
	    		optionButtons[i] = new GuiButton(i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, getOptionButtonTitle(tileEntity.customOptions()[i]));
	    		optionButtons[i].packedFGColour = tileEntity.customOptions()[i].toString().matches(tileEntity.customOptions()[i].getDefaultValue().toString()) ? 16777120 : 14737632; 
	    		this.buttonList.add(optionButtons[i]);
	    		this.hoverCheckers[i + tileEntity.getNumberOfCustomizableOptions()] = new HoverChecker(optionButtons[i], 20);
	    	}
    	}
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	
    	for(int i = 0; i < hoverCheckers.length; i++){
    		if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
    			if(i < tileEntity.getNumberOfCustomizableOptions()) {
        			this.drawHoveringText(this.mc.fontRendererObj.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, this.mc.fontRendererObj);
    			}
    			else {
        			this.drawHoveringText(this.mc.fontRendererObj.listFormattedStringToWidth(getOptionDescription(i), 150), mouseX, mouseY, this.mc.fontRendererObj);
    			}
    		}
    	}
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = this.tileEntity.hasCustomName() ? this.tileEntity.getDisplayName().getUnformattedText() : I18n.format(this.tileEntity.getDisplayName().getUnformattedText(), new Object[0]);
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
    
	protected void actionPerformed(GuiButton guibutton) {
		if(!(guibutton instanceof GuiItemButton)) {
			Option<?> tempOption = tileEntity.customOptions()[guibutton.id];
			tempOption.toggle();
			guibutton.packedFGColour = tempOption.toString().matches(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632;
			guibutton.displayString = getOptionButtonTitle(tempOption);
			mod_SecurityCraft.network.sendToServer(new PacketSToggleOption(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), guibutton.id));
		}
	}
    
    private String getModuleDescription(int buttonID) {
    	String moduleDescription = "module." + blockName + "." + descriptionButtons[buttonID].getItemStack().getUnlocalizedName().substring(5) + ".description";
    	
    	return StatCollector.translateToLocal(descriptionButtons[buttonID].getItemStack().getUnlocalizedName() + ".name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal(moduleDescription);
    }
    
    private String getOptionDescription(int buttonID) {
    	String optionDescription = "option." + blockName + "." + tileEntity.customOptions()[buttonID - tileEntity.getNumberOfCustomizableOptions()].getName() + ".description";
    	
    	return StatCollector.translateToLocal(optionDescription);
    }
    
    private String getOptionButtonTitle(Option<?> option) {
    	return (StatCollector.translateToLocal("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString());
    }
    
}