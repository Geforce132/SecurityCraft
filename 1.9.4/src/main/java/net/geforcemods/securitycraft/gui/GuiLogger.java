package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiLogger extends GuiContainer{

	private TileEntityLogger tileEntity;
	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiLogger(InventoryPlayer par1InventoryPlayer, TileEntityLogger par2TileEntityFurnace) {
		super(new ContainerGeneric(par1InventoryPlayer, par2TileEntityFurnace));
		tileEntity = par2TileEntityFurnace;
	}

	@Override
	public void initGui(){
		super.initGui();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRendererObj.drawString(ClientUtils.localize("gui.securitycraft:logger.logged"), xSize / 2 - fontRendererObj.getStringWidth("Logged players:") / 2, 6, 4210752);

		for(int i = 0; i < tileEntity.players.length; i++)
			if(tileEntity.players[i] != "")
				fontRendererObj.drawString(tileEntity.players[i], xSize / 2 - fontRendererObj.getStringWidth(tileEntity.players[i]) / 2, 25 + (10 * i), 4210752);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

}
