package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.screen.RiftStabilizerScreen;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.GuiScrollingList;

public class ToggleScrollList<T> extends GuiScrollingList {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private int hoveredSlot = -1, slotTop = -1;
	private final List<T> orderedFilterList;
	private final Map<T, ITextComponent> typeNames = new HashMap<>();
	private final Minecraft mc;
	private final IToggleableEntries<T> te;
	private final boolean hasSmartModule;
	private final boolean hasRedstoneModule;

	public ToggleScrollList(IToggleableEntries<T> te, boolean hasSmartModule, boolean hasRedstoneModule, Minecraft mc, int width, int height, int top, int left, int screenWidth, int screenHeight) {
		super(mc, width, height, top, top + height, left, 12, screenWidth, screenHeight);
		this.mc = mc;
		this.te = te;
		this.hasSmartModule = hasSmartModule;
		this.hasRedstoneModule = hasRedstoneModule;
		orderedFilterList = new ArrayList<>(te.getFilters().keySet());
		orderedFilterList.sort((e1, e2) -> {
			//the default entry always shows at the bottom of the list
			if (e1 == te.getDefaultType())
				return 1;
			else if (e2 == te.getDefaultType())
				return -1;
			else
				return te.getTypeName(e1).compareTo(te.getTypeName(e2));
		});
	}

	@Override
	protected int getSize() {
		return orderedFilterList.size();
	}

	@Override
	protected boolean isSelected(int index) {
		return hasSmartModule && index == hoveredSlot;
	}

	@Override
	protected void drawBackground() {}

	@Override
	protected int getContentHeight() {
		int height = getSize() * (mc.fontRenderer.FONT_HEIGHT + 3);

		if (height < bottom - top - 4)
			height = bottom - top - 4;

		return height;
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		if (hasSmartModule) {
			te.toggleFilter(orderedFilterList.get(index));
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		int slotBottom = slotTop + slotHeight;

		if (hasRedstoneModule && mouseX >= left && mouseX <= right - 7 && hoveredSlot >= 0 && hoveredSlot < getSize() && mouseY >= top && mouseY <= bottom) {
			int comparatorOutput = te.getComparatorOutputFunction().applyAsInt(orderedFilterList.get(hoveredSlot));

			if (comparatorOutput > 0) {
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Collections.singletonList(Utils.localize("gui.securitycraft:toggleList.comparatorOutput", comparatorOutput).getFormattedText()), right - 8, slotBottom, screenWidth, screenHeight, -1, mc.fontRenderer);
				RenderHelper.disableStandardItemLighting(); //fixes the screen title rendering darker if a tooltip is displayed
			}
		}
	}

	@Override
	protected void drawSlot(int slotIndex, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
		//highlight hovered slot
		if (mouseX >= left && mouseX <= entryRight && slotIndex >= 0 && slotIndex < getSize() && mouseY >= slotTop - 1 && mouseY <= slotTop + slotBuffer + 2) {
			hoveredSlot = slotIndex;
			this.slotTop = slotTop;
		}
		else if (mouseX < left || mouseX > right || mouseY < top || mouseY > bottom || mouseY > top + slotHeight * getSize())
			hoveredSlot = -1;

		//draw entry strings and indicators whether the filter is enabled
		T type = orderedFilterList.get(slotIndex);
		String name = typeNames.computeIfAbsent(type, t -> Utils.localize(t == te.getDefaultType() ? te.getDefaultTypeName() : te.getTypeName(t))).getFormattedText();

		mc.fontRenderer.drawString(name, left + listWidth / 2 - mc.fontRenderer.getStringWidth(name) / 2, slotTop, 0xC6C6C6);
		mc.getTextureManager().bindTexture(BEACON_GUI);
		RiftStabilizerScreen.drawScaledCustomSizeModalRect(left, slotTop - 3, te.getFilter(type) ? 88 : 110, 219, 21, 22, 14, 14, 256, 256);
	}
}