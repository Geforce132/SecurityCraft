package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiLogger extends GuiContainer{

	private TileEntityLogger tileEntity;
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiLogger(InventoryPlayer inventory, TileEntityLogger te) {
		super(new ContainerGeneric(inventory, te));
		tileEntity = te;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:logger.logged"), xSize / 2 - fontRendererObj.getStringWidth("Logged players:") / 2, 6, 4210752);

		for(int i = 0; i < tileEntity.players.length; i++)
			if(tileEntity.players[i] != "")
				fontRendererObj.drawString(tileEntity.players[i], xSize / 2 - fontRendererObj.getStringWidth(tileEntity.players[i]) / 2, 25 + (10 * i), 4210752);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

}
