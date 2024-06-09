package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

public class SSSConnectionList<T extends Screen & ConnectionAccessor> extends ScrollPanel {
	private static final ResourceLocation CANCEL_SPRITE = SecurityCraft.mcResLoc("container/beacon/cancel");
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

		for (GlobalPos globalPos : parent.getPositions()) {
			if (globalPos == null)
				continue;

			BlockEntity be = level.getBlockEntity(globalPos.pos());
			Component blockName;

			if (be instanceof Nameable nameable)
				blockName = nameable.getDisplayName();
			else if (be != null)
				blockName = Utils.localize(be.getBlockState().getBlock().getDescriptionId());
			else
				blockName = Component.literal("????");

			connectionInfo.add(new ConnectionInfo(globalPos, blockName));
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
	protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {
		drawGradientRect(guiGraphics, left, top, right, bottom, 0xC0101010, 0xD0101010);
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
			BufferBuilder bufferBuilder;

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			bufferBuilder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.addVertex(min, slotTop + slotBuffer + 2, 0).setUv(0, 1).setColor(0x80, 0x80, 0x80, 0xFF);
			bufferBuilder.addVertex(max, slotTop + slotBuffer + 2, 0).setUv(1, 1).setColor(0x80, 0x80, 0x80, 0xFF);
			bufferBuilder.addVertex(max, slotTop - 2, 0).setUv(1, 0).setColor(0x80, 0x80, 0x80, 0xFF);
			bufferBuilder.addVertex(min, slotTop - 2, 0).setUv(0, 0).setColor(0x80, 0x80, 0x80, 0xFF);
			bufferBuilder.addVertex(min + 1, slotTop + slotBuffer + 1, 0).setUv(0, 1).setColor(0x00, 0x00, 0x00, 0xFF);
			bufferBuilder.addVertex(max - 1, slotTop + slotBuffer + 1, 0).setUv(1, 1).setColor(0x00, 0x00, 0x00, 0xFF);
			bufferBuilder.addVertex(max - 1, slotTop - 1, 0).setUv(1, 0).setColor(0x00, 0x00, 0x00, 0xFF);
			bufferBuilder.addVertex(min + 1, slotTop - 1, 0).setUv(0, 0).setColor(0x00, 0x00, 0x00, 0xFF);
			BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
			RenderSystem.disableBlend();

			guiGraphics.blitSprite(CANCEL_SPRITE, left + 2, slotTop - 2, 11, 11);
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

			guiGraphics.drawString(font, Utils.getFormattedCoordinates(connectionInfo.get(slotIndex).globalPos.pos()), left + 13, top + height + 5, 4210752, false);
		}
	}

	@Override
	protected boolean clickPanel(double mouseX, double mouseY, int button) {
		int slotIndex = (int) (mouseY + (border / 2)) / SLOT_HEIGHT;

		if (slotIndex >= 0 && slotIndex < connectionInfo.size()) {
			Minecraft mc = Minecraft.getInstance();
			double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

			if (relativeMouseY >= top && relativeMouseY <= bottom && mouseX < 13) {
				parent.removePosition(connectionInfo.get(slotIndex).globalPos);
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
		public List<GlobalPos> getPositions();

		public void removePosition(GlobalPos globalPos);
	}

	private record ConnectionInfo(GlobalPos globalPos, Component blockName) {}
}