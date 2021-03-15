package net.geforcemods.securitycraft.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiLogger extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
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
		playerList = new PlayerList(mc, xSize - 24, ySize - 40, guiTop + 20, guiLeft + 12, width, height);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
		{
			tileEntity.players = new String[100];
			SecurityCraft.network.sendToServer(new ClearLoggerServer(tileEntity.getPos()));
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
		String localized = ClientUtils.localize("gui.securitycraft:logger.logged").getFormattedText();

		fontRenderer.drawString(localized, xSize / 2 - fontRenderer.getStringWidth(localized) / 2, 6, 4210752);

		if(mouseX >= guiLeft + 4 && mouseY >= guiTop + 4 && mouseX < guiLeft + 4 + 8 && mouseY < guiTop + 4 + 8)
			drawHoveringText(ClientUtils.localize("gui.securitycraft:editModule.clear").getFormattedText(), mouseX - guiLeft, mouseY - guiTop);

		int slotIndex = playerList.hoveredSlot;

		//draw extra info
		if(slotIndex != -1)
		{
			if(tileEntity.players[slotIndex] != null && !tileEntity.players[slotIndex].isEmpty())
			{
				if(tileEntity.getOwner().isOwner(mc.player))
				{
					localized = ClientUtils.localize("gui.securitycraft:logger.date", DATE_FORMAT.format(new Date(tileEntity.timestamps[slotIndex]))).getFormattedText();

					if(tileEntity.uuids[slotIndex] != null && !tileEntity.uuids[slotIndex].isEmpty())
						drawHoveringText(tileEntity.uuids[slotIndex], mouseX - guiLeft, mouseY - guiTop);

					fontRenderer.drawString(localized, xSize / 2 - fontRenderer.getStringWidth(localized) / 2, ySize - 15, 4210752);
				}
			}
		}
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
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		playerList.handleMouseInput(mouseX, mouseY);
	}

	class PlayerList extends GuiScrollingList
	{
		private int hoveredSlot = -1;
		private int i = 0;
		private boolean isHovering = false;

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
		protected void elementClicked(int index, boolean doubleClick)
		{
			if(tileEntity.getOwner().isOwner(mc.player))
			{
				String uuid = tileEntity.uuids[index];

				//copy UUID to clipboard
				if(uuid != null)
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uuid), null);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			return false;
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected void drawSlot(int slotIndex, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
		{
			//highlighted hovered slot
			if(mouseX >= left && mouseX <= entryRight && slotIndex >= 0 && slotIndex < getSize() && mouseY >= slotTop - 1 && mouseY <= slotTop + slotBuffer + 2)
			{
				if(tileEntity.players[slotIndex] != null && !tileEntity.players[slotIndex].isEmpty())
				{
					int min = left;
					int max = entryRight + 1;
					BufferBuilder bufferBuilder = tess.getBuffer();

					this.hoveredSlot = slotIndex;
					isHovering = true;
					GlStateManager.disableTexture2D();
					bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
					bufferBuilder.pos(min, slotTop + slotBuffer + 3, 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(max, slotTop + slotBuffer + 3, 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(max, slotTop - 2, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(min, slotTop - 2, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(min + 1, slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.pos(max - 1, slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.pos(max - 1, slotTop - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.pos(min + 1, slotTop - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					tess.draw();
					GlStateManager.enableTexture2D();
				}
			}

			//trickery to make correctly set the currently hovered slot back if no slot is being hovered
			if(i++ == getSize() - 1)
			{
				if(!isHovering)
				{
					hoveredSlot = -1;
					i = 0;
				}
				else
				{
					isHovering = false;
					i = 0;
				}
			}

			if(slotIndex >= 0 && slotIndex < tileEntity.players.length && tileEntity.players[slotIndex] != null && !tileEntity.players[slotIndex].equals(""))
				fontRenderer.drawString(tileEntity.players[slotIndex], width / 2 - fontRenderer.getStringWidth(tileEntity.players[slotIndex]) / 2, slotTop, 0xC6C6C6);
		}
	}
}
