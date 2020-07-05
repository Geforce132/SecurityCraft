package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
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
import net.minecraftforge.client.gui.ScrollPanel;

public class UsernameLoggerScreen extends ContainerScreen<GenericTEContainer>{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private UsernameLoggerTileEntity tileEntity;
	private PlayerList playerList;

	public UsernameLoggerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = (UsernameLoggerTileEntity)container.te;
	}

	@Override
	protected void func_231160_c_()
	{
		super.func_231160_c_();

		func_230480_a_(new ClickButton(0, guiLeft + 4, guiTop + 4, 8, 8, "x", b -> {
			tileEntity.players = new String[100];
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(tileEntity.getPos()));
		})).field_230693_o_ = tileEntity.getOwner().isOwner(field_230706_i_.player);
		field_230705_e_.add(playerList = new PlayerList(field_230706_i_, xSize - 24, ySize - 40, guiTop + 20, guiLeft + 12));
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY)
	{
		String localized = ClientUtils.localize("gui.securitycraft:logger.logged");

		field_230712_o_.drawString(localized, xSize / 2 - field_230712_o_.getStringWidth(localized) / 2, 6, 4210752);

		if(mouseX >= guiLeft + 4 && mouseY >= guiTop + 4 && mouseX < guiLeft + 4 + 8 && mouseY < guiTop + 4 + 8)
			func_238652_a_(matrix, ClientUtils.localize("gui.securitycraft:editModule.clear"), mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);

		if(playerList != null)
			playerList.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		func_230446_a_(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean func_231043_a_(double mouseX, double mouseY, double scroll)
	{
		if(playerList != null)
			playerList.func_231043_a_(mouseX, mouseY, scroll);

		return super.func_231043_a_(mouseX, mouseY, scroll);
	}

	@Override
	public boolean func_231044_a_(double mouseX, double mouseY, int button)
	{
		if(playerList != null)
			playerList.func_231044_a_(mouseX, mouseY, button);

		return super.func_231044_a_(mouseX, mouseY, button);
	}

	@Override
	public boolean func_231048_c_(double mouseX, double mouseY, int button)
	{
		if(playerList != null)
			playerList.func_231048_c_(mouseX, mouseY, button);

		return super.func_231048_c_(mouseX, mouseY, button);
	}

	@Override
	public boolean func_231045_a_(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(playerList != null)
			playerList.func_231045_a_(mouseX, mouseY, button, deltaX, deltaY);

		return super.func_231045_a_(mouseX, mouseY, button, deltaX, deltaY);
	}

	class PlayerList extends ScrollPanel
	{
		private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		private final int slotHeight = 10, listLength = 100;

		public PlayerList(Minecraft client, int field_230708_k_, int field_230709_l_, int top, int left)
		{
			super(client, field_230708_k_, field_230709_l_, top, left);
		}

		@Override
		protected int getContentHeight()
		{
			int field_230709_l_ = 50 + (tileEntity.players.length * field_230712_o_.FONT_HEIGHT);

			if(field_230709_l_ < bottom - top - 8)
				field_230709_l_ = bottom - top - 8;

			return field_230709_l_;
		}

		@Override
		public boolean func_231044_a_(double mouseX, double mouseY, int button)
		{
			if(tileEntity.getOwner().isOwner(field_230706_i_.player))
			{
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if(mouseX >= left && mouseX <= right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				{
					if(tileEntity.players[slotIndex] != null  && !tileEntity.players[slotIndex].isEmpty())
						GLFW.glfwSetClipboardString(field_230706_i_.getMainWindow().getHandle(), tileEntity.uuids[slotIndex]);
				}
			}

			return super.func_231044_a_(mouseX, mouseY, button);
		}

		@Override
		public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
		{
			super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);

			if(tileEntity.getOwner().isOwner(field_230706_i_.player))
			{
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if(mouseX >= left && mouseX <= right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				{
					if(tileEntity.players[slotIndex] != null  && !tileEntity.players[slotIndex].isEmpty())
					{
						String localized = ClientUtils.localize("gui.securitycraft:logger.date", dateFormat.format(new Date(tileEntity.timestamps[slotIndex])));

						if(tileEntity.uuids[slotIndex] != null && !tileEntity.uuids[slotIndex].isEmpty())
							func_238652_a_(matrix, new StringTextComponent(tileEntity.uuids[slotIndex]), mouseX, mouseY);

						field_230712_o_.drawString(localized, guiLeft + (xSize / 2 - field_230712_o_.getStringWidth(localized) / 2), bottom + 5, 4210752);
					}
				}
			}
		}

		@Override
		protected void drawPanel(int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY)
		{
			int baseY = top + border - (int)scrollDistance;
			int slotBuffer = slotHeight - 3;
			int mouseListY = (int)(mouseY - top + scrollDistance - border);
			int slotIndex = mouseListY / slotHeight;

			//highlighted hovered slot
			if(mouseX >= left && mouseX <= right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
			{
				if(tileEntity.players[slotIndex] != null && !tileEntity.players[slotIndex].isEmpty())
				{
					int min = left;
					int max = entryRight - 6; //6 is the field_230708_k_ of the scrollbar
					int slotTop = baseY + slotIndex * slotHeight;
					BufferBuilder bufferBuilder = tess.getBuffer();

					RenderSystem.enableBlend();
					RenderSystem.disableTexture();
					RenderSystem.defaultBlendFunc();
					bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					bufferBuilder.pos(min, slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(max, slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(max, slotTop - 2, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(min, slotTop - 2, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.pos(min + 1, slotTop + slotBuffer + 1, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.pos(max - 1, slotTop + slotBuffer + 1, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.pos(max - 1, slotTop - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.pos(min + 1, slotTop - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.finishDrawing();
					WorldVertexBufferUploader.draw(bufferBuilder);
					RenderSystem.enableTexture();
					RenderSystem.disableBlend();
				}
			}

			//draw entry strings
			for(int i = 0; i < tileEntity.players.length; i++)
			{
				if(tileEntity.players[i] != null && !tileEntity.players[i].equals(""))
					field_230712_o_.drawString(tileEntity.players[i], left + field_230708_k_ / 2 - field_230712_o_.getStringWidth(tileEntity.players[i]) / 2, relativeY + (10 * i), 0xC6C6C6);
			}
		}
	}
}
