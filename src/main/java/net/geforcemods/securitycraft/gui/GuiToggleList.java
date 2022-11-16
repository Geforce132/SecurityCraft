package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiToggleList<T> extends GuiScreen {
	private final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/blank.png");
	private final int imageWidth = 176;
	private final int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private final String title, scrollListTitle, moduleRequired, toggle;
	private final boolean isSmart;
	private final boolean isRedstone;
	private IToggleableEntries<T> te;
	private ToggleScrollList entryList;

	public GuiToggleList(IToggleableEntries<T> te, ITextComponent title, ITextComponent scrollListTitle, ITextComponent moduleRequired, ITextComponent toggle) {
		this.te = te;
		isSmart = te instanceof IModuleInventory && ((IModuleInventory) te).isModuleEnabled(EnumModuleType.SMART);
		isRedstone = te instanceof IModuleInventory && ((IModuleInventory) te).isModuleEnabled(EnumModuleType.REDSTONE);
		this.title = title.getFormattedText();
		this.scrollListTitle = scrollListTitle.getFormattedText();
		this.moduleRequired = moduleRequired.getFormattedText();
		this.toggle = toggle.getFormattedText();
	}

	@Override
	public void initGui() {
		super.initGui();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		entryList = new ToggleScrollList(mc, imageWidth - 24, imageHeight - 60, topPos + 40, leftPos + 12, width, height);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

		if (entryList != null)
			entryList.drawScreen(mouseX, mouseY, partialTicks);

		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 4210752);
		fontRenderer.drawString(scrollListTitle, width / 2 - fontRenderer.getStringWidth(scrollListTitle) / 2, topPos + 31, 4210752);
		GuiUtils.renderModuleInfo(EnumModuleType.SMART, toggle, moduleRequired, isSmart, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if (keyCode == 1)
			mc.player.closeScreen();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		entryList.handleMouseInput(mouseX, mouseY);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	class ToggleScrollList extends GuiScrollingList {
		private int hoveredSlot = -1, slotTop = -1;
		private final List<T> orderedFilterList;
		private final Map<T, ITextComponent> typeNames = new HashMap<>();

		public ToggleScrollList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight) {
			super(client, width, height, top, top + height, left, 12, screenWidth, screenHeight);
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
			return isSmart && index == hoveredSlot;
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected int getContentHeight() {
			int height = getSize() * (fontRenderer.FONT_HEIGHT + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		protected void elementClicked(int index, boolean doubleClick) {
			if (isSmart) {
				te.toggleFilter(orderedFilterList.get(index));
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);

			int slotBottom = slotTop + slotHeight;

			if (isRedstone && mouseX >= left && mouseX <= right - 7 && hoveredSlot >= 0 && hoveredSlot < getSize() && mouseY >= top && mouseY <= bottom) {
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

			fontRenderer.drawString(name, left + listWidth / 2 - fontRenderer.getStringWidth(name) / 2, slotTop, 0xC6C6C6);
			mc.getTextureManager().bindTexture(BEACON_GUI);
			drawScaledCustomSizeModalRect(left, slotTop - 3, te.getFilter(type) ? 88 : 110, 219, 21, 22, 14, 14, 256, 256);
		}
	}
}
