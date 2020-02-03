package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSClearLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiLogger extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityLogger tileEntity;
	private PlayerList playerList;

	public GuiLogger(InventoryPlayer inventory, TileEntityLogger te) {
		super(new ContainerGeneric(inventory, te));
		tileEntity = te;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		addButton(new GuiButton(0, guiLeft + 4, guiTop + 4, 8, 8, "x")).enabled = tileEntity.getOwner().isOwner(mc.player);
		playerList = new PlayerList(mc, xSize - 24, ySize - 30, guiTop + 20, guiLeft + 12, width, height);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			tileEntity.players = new String[100];
			SecurityCraft.network.sendToServer(new PacketSClearLogger(tileEntity.getPos()));
		}

		if(playerList != null)
			playerList.actionPerformed(button);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String localized = ClientUtils.localize("gui.securitycraft:logger.logged");

		fontRenderer.drawString(localized, xSize / 2 - fontRenderer.getStringWidth(localized) / 2, 6, 4210752);

		if(mouseX >= guiLeft + 4 && mouseY >= guiTop + 4 && mouseX < guiLeft + 4 + 8 && mouseY < guiTop + 4 + 8)
			drawHoveringText(ClientUtils.localize("gui.securitycraft:editModule.clear"), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);

		if(playerList != null)
			playerList.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(playerList != null)
			playerList.handleMouseInput(mouseX, mouseY);

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	class PlayerList extends GuiScrollingList
	{
		public PlayerList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight)
		{
			super(client, width, height, top, top + height, left, 10, screenWidth, screenHeight);
		}

		@Override
		protected int getSize()
		{
			return tileEntity.players.length;
		}

		@Override
		protected void elementClicked(int index, boolean doubleClick) {}

		@Override
		protected boolean isSelected(int index)
		{
			return index == selectedIndex;
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
		{
			if(slotIdx >= 0 && slotIdx < tileEntity.players.length && tileEntity.players[slotIdx] != "")
				fontRenderer.drawString(tileEntity.players[slotIdx], width / 2 - fontRenderer.getStringWidth(tileEntity.players[slotIdx]) / 2, slotTop, 0xC6C6C6);
		}
	}
}
