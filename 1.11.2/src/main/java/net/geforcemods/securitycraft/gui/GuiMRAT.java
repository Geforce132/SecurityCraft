package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
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

		int j = (height - height) / 2;

		buttonList.add(new GuiButton(0, width / 2 - 49, height / 2 - 7 - 50, 99, 20, ClientUtils.localize("gui.mrat.activate")));
		buttonList.add(new GuiButton(1, width / 2 - 49, height / 2 - 7, 99, 20, ClientUtils.localize("gui.mrat.deactivate")));
		buttonList.add(new GuiButton(2, width / 2 - 49, height / 2 - 7 + 50, 99, 20, ClientUtils.localize("gui.mrat.detonate")));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRendererObj.drawString(ClientUtils.localize("gui.mrat.name"), xSize / 2 - fontRendererObj.getStringWidth(ClientUtils.localize("gui.mrat.name")) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				Minecraft.getMinecraft().player.openGui(SecurityCraft.instance, GuiHandler.MRAT_ACTIVATE_ID, Minecraft.getMinecraft().world, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ);
				break;

			case 1:
				Minecraft.getMinecraft().player.openGui(SecurityCraft.instance, GuiHandler.MRAT_DEACTIVATE_ID, Minecraft.getMinecraft().world, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ);
				break;

			case 2:
				Minecraft.getMinecraft().player.openGui(SecurityCraft.instance, GuiHandler.MRAT_DETONATE_ID, Minecraft.getMinecraft().world, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ);
				break;

		}
	}
}
