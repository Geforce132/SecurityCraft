package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSClearLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiLogger extends GuiContainer{

	private TileEntityLogger tileEntity;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public GuiLogger(InventoryPlayer playerInv, TileEntityLogger te) {
		super(new ContainerGeneric(playerInv, te));
		tileEntity = te;
	}

	@Override
	public void initGui(){
		super.initGui();

		GuiButton button = new GuiButton(0, guiLeft + 4, guiTop + 4, 8, 8, "x");

		buttonList.add(button);
		button.enabled = tileEntity.getOwner().isOwner(mc.thePlayer);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			tileEntity.players = new String[100];
			SecurityCraft.network.sendToServer(new PacketSClearLogger(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String localized = StatCollector.translateToLocal("gui.securitycraft:logger.logged");

		fontRendererObj.drawString(localized, xSize / 2 - fontRendererObj.getStringWidth(localized) / 2, 6, 4210752);

		for(int i = 0; i < tileEntity.players.length; i++)
			if(tileEntity.players[i] != "")
				fontRendererObj.drawString(tileEntity.players[i], xSize / 2 - fontRendererObj.getStringWidth(tileEntity.players[i]) / 2, 25 + (10 * i), 4210752);

		if(mouseX >= guiLeft + 4 && mouseY >= guiTop + 4 && mouseX < guiLeft + 4 + 8 && mouseY < guiTop + 4 + 8)
			drawCreativeTabHoveringText(ClientUtils.localize("gui.securitycraft:editModule.clear"), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

}
