package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class ToggleScrollList<T> extends ScrollPanel {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final int SLOT_HEIGHT = 12;
	private final int listLength;
	private final List<T> orderedFilterList;
	private final Map<T, Component> typeNames = new HashMap<>();
	private final Minecraft mc;
	private final IToggleableEntries<T> be;
	private final boolean hasSmartModule;
	private final boolean hasRedstoneModule;
	private final Screen parent;

	public ToggleScrollList(IToggleableEntries<T> be, boolean hasSmartModule, boolean hasRedstoneModule, Minecraft mc, int width, int height, int top, int left, Screen parent) {
		super(mc, width, height, top, left);
		this.mc = mc;
		this.be = be;
		this.hasSmartModule = hasSmartModule;
		this.hasRedstoneModule = hasRedstoneModule;
		orderedFilterList = new ArrayList<>(be.getFilters().keySet());
		orderedFilterList.sort((e1, e2) -> {
			//the default entry always shows at the bottom of the list
			if (e1 == be.getDefaultType())
				return 1;
			else if (e2 == be.getDefaultType())
				return -1;
			else
				return Utils.localize(e1.toString()).getString().compareTo(Utils.localize(e2.toString()).getString());
		});
		listLength = orderedFilterList.size();
		this.parent = parent;
	}

	@Override
	protected int getContentHeight() {
		int height = listLength * (mc.font.lineHeight + 3);

		if (height < bottom - top - 4)
			height = bottom - top - 4;

		return height;
	}

	@Override
	protected boolean clickPanel(double mouseX, double mouseY, int button) {
		if (hasSmartModule) {
			int slotIndex = (int) (mouseY + (border / 2)) / SLOT_HEIGHT;

			if (slotIndex >= 0 && slotIndex < listLength) {
				double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

				if (relativeMouseY >= top && relativeMouseY <= bottom) {
					be.toggleFilter(orderedFilterList.get(slotIndex));
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		super.render(pose, mouseX, mouseY, partialTick);

		int baseY = top + border - (int) scrollDistance;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOT_HEIGHT;
		int slotBottom = baseY + (slotIndex + 1) * SLOT_HEIGHT;

		if (hasRedstoneModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
			int comparatorOutput = be.getComparatorOutputFunction().applyAsInt(orderedFilterList.get(slotIndex));

			if (comparatorOutput > 0)
				parent.renderTooltip(pose, new TranslatableComponent("gui.securitycraft:toggleList.comparatorOutput", comparatorOutput), right - 8, slotBottom);
		}
	}

	@Override
	protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
		Font font = Minecraft.getInstance().font;
		int baseY = top + border - (int) scrollDistance;
		int slotBuffer = SLOT_HEIGHT - 4;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOT_HEIGHT;

		//highlight hovered slot
		if (hasSmartModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
			int min = left;
			int max = entryRight - 6; //6 is the width of the scrollbar
			int slotTop = baseY + slotIndex * SLOT_HEIGHT;
			BufferBuilder bufferBuilder = tess.getBuilder();

			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
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
			bufferBuilder.end();
			BufferUploader.end(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}

		int i = 0;

		//draw entry strings and indicators whether the filter is enabled
		for (T type : orderedFilterList) {
			Component name = typeNames.computeIfAbsent(type, t -> Utils.localize(t == be.getDefaultType() ? be.getDefaultTypeName() : t.toString()));
			int yStart = relativeY + (SLOT_HEIGHT * i);

			font.draw(pose, name, left + width / 2 - font.width(name) / 2, yStart, 0xC6C6C6);
			RenderSystem.setShaderTexture(0, BEACON_GUI);
			blit(pose, left, yStart - 3, 14, 14, be.getFilter(type) ? 88 : 110, 219, 21, 22, 256, 256);
			i++;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean returnValue = super.mouseClicked(mouseX, mouseY, button);
		int barLeft = ObfuscationReflectionHelper.getPrivateValue(ScrollPanel.class, this, "barLeft");
		int barWidth = ObfuscationReflectionHelper.getPrivateValue(ScrollPanel.class, this, "barWidth");
		boolean previousScrolling = ObfuscationReflectionHelper.getPrivateValue(ScrollPanel.class, this, "scrolling");

		if (previousScrolling) {
			boolean scrolling = button == 0 && mouseX >= barLeft && mouseX < barLeft + barWidth && mouseY >= top && mouseY <= bottom;

			if (!scrolling)
				ObfuscationReflectionHelper.setPrivateValue(ScrollPanel.class, this, scrolling, "scrolling");

			if (!isMouseOver(mouseX, mouseY) || !scrolling)
				return clickPanel(mouseX - left, mouseY - top + (int) scrollDistance - border, button);
		}

		return previousScrolling || (!previousScrolling && returnValue);
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}