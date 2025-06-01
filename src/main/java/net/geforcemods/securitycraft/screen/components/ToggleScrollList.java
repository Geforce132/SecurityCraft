package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

public class ToggleScrollList<T> extends ScrollPanel {
	private static final ResourceLocation CONFIRM_SPRITE = SecurityCraft.mcResLoc("container/beacon/confirm");
	private static final ResourceLocation CANCEL_SPRITE = SecurityCraft.mcResLoc("container/beacon/cancel");
	private static final int SLOT_HEIGHT = 12;
	private final int listLength;
	private final List<T> orderedFilterList;
	private final Map<T, Component> typeNames = new HashMap<>();
	private final Minecraft mc;
	private final IToggleableEntries<T> be;
	private final boolean hasSmartModule;
	private final boolean hasRedstoneModule;

	public ToggleScrollList(IToggleableEntries<T> be, boolean hasSmartModule, boolean hasRedstoneModule, Minecraft mc, int width, int height, int top, int left) {
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
	protected void drawBackground(GuiGraphics guiGraphics, float partialTick) {
		drawGradientRect(guiGraphics, left, top, right, bottom, 0xC0101010, 0xD0101010);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.flush();
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		int baseY = top + border - (int) scrollDistance;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / SLOT_HEIGHT;
		int slotBottom = baseY + (slotIndex + 1) * SLOT_HEIGHT;

		if (hasRedstoneModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
			int comparatorOutput = be.getComparatorOutputFunction().applyAsInt(orderedFilterList.get(slotIndex));

			if (comparatorOutput > 0)
				guiGraphics.renderTooltip(mc.font, Component.translatable("gui.securitycraft:toggleList.comparatorOutput", comparatorOutput), right - 8, slotBottom);
		}
	}

	@Override
	protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, int mouseX, int mouseY) {
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

			guiGraphics.fill(min, slotTop - 2, max, slotTop + slotBuffer + 2, 0xFF808080);
			guiGraphics.fill(min + 1, slotTop - 1, max - 1, slotTop + slotBuffer + 1, 0xFF000000);
		}

		int i = 0;

		//draw entry strings and indicators whether the filter is enabled
		for (T type : orderedFilterList) {
			Component name = typeNames.computeIfAbsent(type, t -> Utils.localize(t == be.getDefaultType() ? be.getDefaultTypeName() : t.toString()));
			int yStart = relativeY + (SLOT_HEIGHT * i);

			guiGraphics.drawString(font, name, left + width / 2 - font.width(name) / 2, yStart, 0xC6C6C6, false);
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, be.getFilter(type) ? CONFIRM_SPRITE : CANCEL_SPRITE, left + 1, yStart - 3, 12, 12);
			i++;
		}

		guiGraphics.flush();
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}