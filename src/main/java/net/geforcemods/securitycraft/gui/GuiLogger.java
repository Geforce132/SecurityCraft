package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLogger extends GuiContainer{

	private TileEntityLogger tileEntity;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiLogger(TileEntityLogger te) {
		super(new ContainerGeneric());
		tileEntity = te;
	}

	@Override
	public void initGui(){
		super.initGui();

		addButton(new GuiButtonClick(0, guiLeft + 4, guiTop + 4, 8, 8, "x", b -> {
			tileEntity.players = new String[100];
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(tileEntity.getPos()));
		})).enabled = tileEntity.getOwner().isOwner(mc.player);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String localized = ClientUtils.localize("gui.securitycraft:logger.logged");

		fontRenderer.drawString(localized, xSize / 2 - fontRenderer.getStringWidth(localized) / 2, 6, 4210752);

		for(int i = 0; i < tileEntity.players.length; i++)
			if(tileEntity.players[i] != "")
				fontRenderer.drawString(tileEntity.players[i], xSize / 2 - fontRenderer.getStringWidth(tileEntity.players[i]) / 2, 25 + (10 * i), 4210752);

		if(mouseX >= guiLeft + 4 && mouseY >= guiTop + 4 && mouseX < guiLeft + 4 + 8 && mouseY < guiTop + 4 + 8)
			drawHoveringText(ClientUtils.localize("gui.securitycraft:editModule.clear"), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

}
