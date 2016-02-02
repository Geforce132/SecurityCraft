package net.geforcemods.securitycraft.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiIRCInfo extends GuiContainer
{
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiIRCInfo()
	{
		super(new ContainerGeneric(null, null));
	}

	public void initGui()
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(0, width / 2 - 48, height / 2 + 50, 100, 20, "Ok."));
	}
	
	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
    
    public void drawScreen(int par1, int par2, float par3)
    {
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
    	fontRendererObj.drawSplitString(StatCollector.translateToLocal("gui.ircInfo.explanation"), xSize / 12, ySize / 12, 150, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
    	int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(field_110410_t);
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
    }
    
    protected void actionPerformed(GuiButton guibutton)
    {
    	ClientUtils.closePlayerScreen();
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
    	return true;
    }
}
