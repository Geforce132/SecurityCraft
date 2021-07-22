package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

public class UsernameLoggerScreen extends ContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent logged = Utils.localize("gui.securitycraft:logger.logged");
	private final TranslationTextComponent clear = Utils.localize("gui.securitycraft:editModule.clear");
	private UsernameLoggerTileEntity tileEntity;
	private PlayerList playerList;

	public UsernameLoggerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = (UsernameLoggerTileEntity)container.te;
	}

	@Override
	protected void init()
	{
		super.init();

		addButton(new IdButton(0, leftPos + 4, topPos + 4, 8, 8, "x", b -> {
			tileEntity.players = new String[100];
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(tileEntity.getBlockPos()));
		})).active = tileEntity.getOwner().isOwner(minecraft.player);
		children.add(playerList = new PlayerList(minecraft, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.draw(matrix, logged, imageWidth / 2 - font.width(logged) / 2, 6, 4210752);

		if(mouseX >= leftPos + 4 && mouseY >= topPos + 4 && mouseX < leftPos + 4 + 8 && mouseY < topPos + 4 + 8)
			renderTooltip(matrix, clear, mouseX - leftPos, mouseY - topPos);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(playerList != null)
			playerList.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
	{
		if(playerList != null && playerList.isMouseOver(mouseX, mouseY))
			playerList.mouseScrolled(mouseX, mouseY, scroll);

		return super.mouseScrolled(mouseX, mouseY, scroll);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(playerList != null)
			playerList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(playerList != null)
			playerList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(playerList != null)
			playerList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class PlayerList extends ScrollPanel
	{
		private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		private final int slotHeight = 12, listLength = 100;

		public PlayerList(Minecraft client, int width, int height, int top, int left)
		{
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight()
		{
			int height = 50 + (tileEntity.players.length * font.lineHeight);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
		{
			super.render(matrix, mouseX, mouseY, partialTicks);

			if(tileEntity.getOwner().isOwner(minecraft.player))
			{
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if(mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				{
					if(tileEntity.players[slotIndex] != null  && !tileEntity.players[slotIndex].isEmpty())
					{
						TranslationTextComponent localized = Utils.localize("gui.securitycraft:logger.date", dateFormat.format(new Date(tileEntity.timestamps[slotIndex])));

						if(tileEntity.uuids[slotIndex] != null && !tileEntity.uuids[slotIndex].isEmpty())
							renderTooltip(matrix, new StringTextComponent(tileEntity.uuids[slotIndex]), mouseX, mouseY);

						font.draw(matrix, localized, leftPos + (imageWidth / 2 - font.width(localized) / 2), bottom + 5, 4210752);
					}
				}
			}
		}

		@Override
		protected void drawPanel(MatrixStack matrix, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY)
		{
			int baseY = top + border - (int)scrollDistance;
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int)(mouseY - top + scrollDistance - border);
			int slotIndex = mouseListY / slotHeight;

			//highlight hovered slot
			if(mouseX >= left && mouseX <= right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
			{
				if(tileEntity.players[slotIndex] != null && !tileEntity.players[slotIndex].isEmpty())
				{
					int min = left;
					int max = entryRight - 6; //6 is the width of the scrollbar
					int slotTop = baseY + slotIndex * slotHeight;
					BufferBuilder bufferBuilder = tess.getBuilder();

					RenderSystem.enableBlend();
					RenderSystem.disableTexture();
					RenderSystem.defaultBlendFunc();
					bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).uv(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).uv(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(max, slotTop - 2, 0).uv(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(min, slotTop - 2, 0).uv(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).uv(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).uv(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.vertex(max - 1, slotTop - 1, 0).uv(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.vertex(min + 1, slotTop - 1, 0).uv(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.end();
					WorldVertexBufferUploader.end(bufferBuilder);
					RenderSystem.enableTexture();
					RenderSystem.disableBlend();
				}
			}

			//draw entry strings
			for(int i = 0; i < tileEntity.players.length; i++)
			{
				if(tileEntity.players[i] != null && !tileEntity.players[i].equals(""))
					font.draw(matrix, tileEntity.players[i], left + width / 2 - font.width(tileEntity.players[i]) / 2, relativeY + (slotHeight * i), 0xC6C6C6);
			}
		}
	}
}
