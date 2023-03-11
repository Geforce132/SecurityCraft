package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

public class SSSConnectionList<T extends GuiScreen & ConnectionAccessor> extends ColorableScrollPanel {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private final int slotHeight = 12;
	private final T parent;
	private final List<ConnectionInfo> connectionInfo = new ArrayList<>();
	private final FontRenderer font;

	public SSSConnectionList(T parent, Minecraft client, int width, int height, int top, int left) {
		super(client, width, height, top, left);
		this.parent = parent;
		font = client.fontRenderer;
		refreshPositions();
	}

	public void refreshPositions() {
		World level = Minecraft.getMinecraft().world;

		connectionInfo.clear();

		for (BlockPos pos : parent.getPositions()) {
			TileEntity be = level.getTileEntity(pos);
			String blockName;

			if (be instanceof IWorldNameable)
				blockName = ((IWorldNameable) be).getDisplayName().getFormattedText();
			else if (be != null)
				blockName = Utils.localize(level.getBlockState(pos).getBlock()).getFormattedText();
			else
				blockName = "????";

			connectionInfo.add(new ConnectionInfo(pos, blockName));
		}
	}

	@Override
	public int getContentHeight() {
		int height = connectionInfo.size() * 12;

		if (height < bottom - top - 4)
			height = bottom - top - 4;

		return height;
	}

	@Override
	public int getSize() {
		return connectionInfo.size();
	}

	@Override
	public void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY) {
		int slotBuffer = slotHeight - 4;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / slotHeight;

		//highlight hovered slot
		if (mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < connectionInfo.size() && mouseY >= top && mouseY <= bottom) {
			int min = left;
			int max = entryRight - scrollBarWidth;
			int slotTop = relativeY + slotIndex * slotHeight;
			BufferBuilder bufferBuilder = tesselator.getBuffer();

			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferBuilder.pos(min, slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.pos(max, slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.pos(max, slotTop - 2, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.pos(min, slotTop - 2, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.pos(min + 1, slotTop + slotBuffer + 1, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.pos(max - 1, slotTop + slotBuffer + 1, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.pos(max - 1, slotTop - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.pos(min + 1, slotTop - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			tesselator.draw();
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();

			Minecraft.getMinecraft().getTextureManager().bindTexture(BEACON_GUI);
			Gui.drawScaledCustomSizeModalRect(left, slotTop - 3, 110, 219, 21, 22, 14, 14, 256, 256);
		}

		int i = 0;

		for (ConnectionInfo info : connectionInfo) {
			int yStart = relativeY + (slotHeight * i++);

			font.drawString(info.blockName, left + 13, yStart, 0xC6C6C6);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		//draw tooltip for long block names
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / slotHeight;

		if (slotIndex >= 0 && slotIndex < connectionInfo.size() && mouseListY >= 0 && mouseX >= left && mouseX < right - 6 && mouseY >= top && mouseY <= bottom) {
			String blockName = connectionInfo.get(slotIndex).blockName;
			int length = font.getStringWidth(blockName);
			int baseY = top + border - (int) scrollDistance;

			if (length + 13 >= listWidth - scrollBarWidth) {
				parent.drawHoveringText(blockName, left + 1, baseY + (slotHeight * slotIndex + slotHeight));
				GlStateManager.disableLighting();
			}

			font.drawString(Utils.getFormattedCoordinates(connectionInfo.get(slotIndex).pos).getFormattedText(), left + 13, top + listHeight + 5, 4210752);
		}
	}

	@Override
	public void elementClicked(int mouseX, int mouseY, int slotIndex) {
		if (mouseX < left + 13) {
			parent.removePosition(connectionInfo.get(slotIndex).pos);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	}

	public interface ConnectionAccessor {
		public Set<BlockPos> getPositions();

		public void removePosition(BlockPos pos);
	}

	private class ConnectionInfo {
		private final BlockPos pos;
		private final String blockName;

		public ConnectionInfo(BlockPos pos, String blockName) {
			this.pos = pos;
			this.blockName = blockName;
		}
	}
}