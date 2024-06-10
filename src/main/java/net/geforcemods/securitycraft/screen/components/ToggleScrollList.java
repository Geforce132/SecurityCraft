package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ToggleScrollList<T> extends ScrollPanel {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final int SLOTH_HEIGHT = 12;
	private final int listLength;
	private final List<T> orderedFilterList;
	private final Map<T, ITextComponent> typeNames = new HashMap<>();
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
			int slotIndex = (int) (mouseY + (border / 2)) / SLOTH_HEIGHT;

			if (slotIndex >= 0 && slotIndex < listLength) {
				double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

				if (relativeMouseY >= top && relativeMouseY <= bottom) {
					be.toggleFilter(orderedFilterList.get(slotIndex));
					Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
		super.render(pose, mouseX, mouseY, partialTick);

		int baseY = top + border - (int) scrollDistance;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOTH_HEIGHT;
		int slotBottom = baseY + (slotIndex + 1) * SLOTH_HEIGHT;

		if (hasRedstoneModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
			int comparatorOutput = be.getComparatorOutputFunction().applyAsInt(orderedFilterList.get(slotIndex));

			if (comparatorOutput > 0)
				parent.renderTooltip(pose, new TranslationTextComponent("gui.securitycraft:toggleList.comparatorOutput", comparatorOutput), right - 8, slotBottom);
		}
	}

	@Override
	protected void drawPanel(MatrixStack pose, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
		FontRenderer font = Minecraft.getInstance().font;
		int baseY = top + border - (int) scrollDistance;
		int slotBuffer = SLOTH_HEIGHT - 4;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOTH_HEIGHT;

		//highlight hovered slot
		if (hasSmartModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
			int min = left;
			int max = entryRight - 6; //6 is the width of the scrollbar
			int slotTop = baseY + slotIndex * SLOTH_HEIGHT;
			BufferBuilder bufferBuilder = tess.getBuilder();

			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop - 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min, slotTop - 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop - 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop - 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.end();
			WorldVertexBufferUploader.end(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}

		int i = 0;

		//draw entry strings and indicators whether the filter is enabled
		for (T type : orderedFilterList) {
			ITextComponent name = typeNames.computeIfAbsent(type, t -> Utils.localize(t == be.getDefaultType() ? be.getDefaultTypeName() : t.toString()));
			int yStart = relativeY + (SLOTH_HEIGHT * i);

			font.draw(pose, name, left + width / 2 - font.width(name) / 2, yStart, 0xC6C6C6);
			mc.getTextureManager().bind(BEACON_GUI);
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
}