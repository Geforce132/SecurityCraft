package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.widget.ScrollPanel;

public class SSSConnectionList<T extends Screen & ConnectionAccessor> extends ScrollPanel {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final int SLOT_HEIGHT = 12;
	private final T parent;
	private final List<ConnectionInfo> connectionInfo = new ArrayList<>();
	private final Font font;

	public SSSConnectionList(T parent, Minecraft client, int width, int height, int top, int left) {
		super(client, width, height, top, left);
		this.parent = parent;
		font = client.font;
		refreshPositions();
	}

	public void refreshPositions() {
		Level level = Minecraft.getInstance().level;

		connectionInfo.clear();

		for (BlockPos pos : parent.getPositions()) {
			BlockEntity be = level.getBlockEntity(pos);
			Component blockName;

			if (be instanceof Nameable nameable)
				blockName = nameable.getDisplayName();
			else if (be != null)
				blockName = Utils.localize(be.getBlockState().getBlock().getDescriptionId());
			else
				blockName = Component.literal("????");

			connectionInfo.add(new ConnectionInfo(pos, blockName));
		}
	}

	@Override
	protected int getContentHeight() {
		int height = connectionInfo.size() * 12;

		if (height < bottom - top - 4)
			height = bottom - top - 4;

		return height;
	}

	@Override
	protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
		int baseY = top + border - (int) scrollDistance;
		int slotBuffer = SLOT_HEIGHT - 4;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOT_HEIGHT;

		//highlight hovered slot
		if (mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < connectionInfo.size() && mouseY >= top && mouseY <= bottom) {
			int min = left;
			int max = entryRight - 6; //6 is the width of the scrollbar
			int slotTop = baseY + slotIndex * SLOT_HEIGHT;
			BufferBuilder bufferBuilder = tesselator.getBuilder();

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop - 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min, slotTop - 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop - 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop - 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			BufferUploader.drawWithShader(bufferBuilder.end());
			RenderSystem.disableBlend();

			guiGraphics.blit(BEACON_GUI, left, slotTop - 3, 14, 14, 110, 219, 21, 22, 256, 256);
		}

		int i = 0;

		for (ConnectionInfo info : connectionInfo) {
			int yStart = relativeY + (SLOT_HEIGHT * i++);

			guiGraphics.drawString(font, info.blockName, left + 13, yStart, 0xC6C6C6, false);
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		//draw tooltip for long block names
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOT_HEIGHT;

		if (slotIndex >= 0 && slotIndex < connectionInfo.size() && mouseListY >= 0 && mouseX >= left && mouseX < right - 6 && mouseY >= top && mouseY <= bottom) {
			Component blockName = connectionInfo.get(slotIndex).blockName;
			int length = font.width(blockName);
			int baseY = top + border - (int) scrollDistance;

			if (length + 13 >= width - 6) //6 = barWidth
				guiGraphics.renderTooltip(font, List.of(blockName), Optional.empty(), left + 1, baseY + (SLOT_HEIGHT * slotIndex + SLOT_HEIGHT));

			guiGraphics.drawString(font, Utils.getFormattedCoordinates(connectionInfo.get(slotIndex).pos), left + 13, top + height + 5, 4210752, false);
		}
	}

	@Override
	protected boolean clickPanel(double mouseX, double mouseY, int button) {
		int slotIndex = (int) (mouseY + (border / 2)) / SLOT_HEIGHT;

		if (slotIndex >= 0 && slotIndex < connectionInfo.size()) {
			Minecraft mc = Minecraft.getInstance();
			double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

			if (relativeMouseY >= top && relativeMouseY <= bottom && mouseX < 13) {
				parent.removePosition(connectionInfo.get(slotIndex).pos);
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				return true;
			}
		}

		return false;
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}

	public interface ConnectionAccessor {
		public Set<BlockPos> getPositions();

		public void removePosition(BlockPos pos);
	}

	private record ConnectionInfo(BlockPos pos, Component blockName) {}
}