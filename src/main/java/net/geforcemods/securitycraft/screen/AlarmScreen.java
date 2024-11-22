package net.geforcemods.securitycraft.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.ColorableScrollPanel;
import net.geforcemods.securitycraft.screen.components.HintEditBox;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;

public class AlarmScreen extends GuiScreen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png");
	protected final AlarmBlockEntity be;
	private final boolean hasSmartModule;
	private final String smartModuleTooltip;
	private final String currentlySelectedText = Utils.localize("gui.securitycraft:alarm.currently_selected").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private String title;
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private String selectedSoundEventText;
	protected int imageWidth = 256, imageHeight = 246;
	protected int leftPos, topPos;
	protected SoundScrollList soundList;
	protected HintEditBox searchBar;
	protected ClickButton optionsButton;
	protected int previousSoundLength, soundLength;
	protected float previousPitch, pitch;

	public AlarmScreen(AlarmBlockEntity be) {
		this.be = be;
		this.hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		smartModuleTooltip = Utils.localize(hasSmartModule ? "gui.securitycraft:alarm.smart_module" : "gui.securitycraft:alarm.no_smart_module").getFormattedText();
		previousSelectedSoundEvent = be.getSound();
		previousSoundLength = be.getSoundLength();
		soundLength = previousSoundLength;
		previousPitch = be.getPitch();
		pitch = previousPitch;
		title = be.getDisplayName().getFormattedText();
		selectSound(previousSelectedSoundEvent);
	}

	@Override
	public void initGui() {
		super.initGui();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		int id = 0;

		soundList = new SoundScrollList(mc, imageWidth - 10, imageHeight - 105, topPos + 40, leftPos + 5);
		searchBar = new HintEditBox(id++, fontRenderer, leftPos + 30, topPos + 20, imageWidth - 60, 15);
		searchBar.setHint(Utils.localize("gui.securitycraft:alarm.search").getFormattedText());
		searchBar.setValidator(s -> s.matches("[a-zA-Z0-9\\._]*"));
		searchBar.setGuiResponder(new GuiResponder() {
			@Override
			public void setEntryValue(int id, String value) {
				soundList.updateFilteredEntries(value);
			}

			@Override
			public void setEntryValue(int id, float value) {}

			@Override
			public void setEntryValue(int id, boolean value) {}
		});
		optionsButton = addButton(new ClickButton(id++, leftPos + imageWidth / 2 - 170 / 2, topPos + 215, 170, 20, Utils.localize("menu.options").getFormattedText(), b -> mc.displayGuiScreen(new AlarmOptionsScreen(this))));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (soundList != null)
			soundList.drawScreen(mouseX, mouseY);

		searchBar.drawTextBox();
		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 4210752);
		fontRenderer.drawString(currentlySelectedText, width / 2 - fontRenderer.getStringWidth(currentlySelectedText) / 2, topPos + imageHeight - 62, 4210752);
		fontRenderer.drawString(selectedSoundEventText, width / 2 - fontRenderer.getStringWidth(selectedSoundEventText) / 2, topPos + imageHeight - 49, 4210752);
		GuiUtils.renderModuleInfo(ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
	}

	public void selectSound(ResourceLocation eventId) {
		this.selectedSoundEvent = eventId;
		selectedSoundEventText = Utils.localize(toLanguageKey(selectedSoundEvent)).getFormattedText();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchBar.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		double mouseX = Mouse.getEventX() * width / mc.displayWidth;
		double mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		if (soundList != null)
			soundList.handleMouseInput((int) mouseX, (int) mouseY);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		searchBar.textboxKeyTyped(typedChar, keyCode);

		if (keyCode == 1 || (mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) && !searchBar.isFocused()))
			mc.player.closeScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (soundList != null && soundList.playingSound != null)
			Minecraft.getMinecraft().getSoundHandler().stopSound(soundList.playingSound);

		boolean changed = false;

		if (selectedSoundEvent != previousSelectedSoundEvent) {
			be.setSound(selectedSoundEvent);
			changed = true;
		}

		if (pitch != previousPitch) {
			be.setPitch(pitch);
			changed = true;
		}

		if (soundLength != previousSoundLength) {
			be.setSoundLength(soundLength);
			changed = true;
		}

		if (changed)
			SecurityCraft.network.sendToServer(new SyncAlarmSettings(be.getPos(), selectedSoundEvent, pitch, soundLength));
	}

	private String toLanguageKey(ResourceLocation resourceLocation) {
		return resourceLocation.getNamespace() + "." + resourceLocation.getPath();
	}

	public class SoundScrollList extends ColorableScrollPanel {
		private static final int TEXT_OFFSET = 11;
		public final List<ResourceLocation> allSoundEvents = mc.getSoundHandler().soundRegistry.getKeys().stream().sorted(Comparator.comparing(rl -> toLanguageKey(rl))).collect(Collectors.toList());
		private final Map<ResourceLocation, String> soundEventKeys = new HashMap<>();
		private List<ResourceLocation> filteredSoundEvents;
		private ISound playingSound;
		private int selectedSoundIndex, contentHeight = 0;
		private String previousSearchText = "";

		public SoundScrollList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			updateFilteredEntries("");
			scrollDistance = selectedSoundIndex * slotHeight;

			int maxScroll = getContentHeight() - (height - BORDER);

			if (scrollDistance > maxScroll)
				scrollDistance = maxScroll;
		}

		@Override
		public int getContentHeight() {
			return contentHeight;
		}

		@Override
		public void elementClicked(int mouseX, int mouseY, int slotIndex) {
			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size()) {
				mouseX -= left;

				if (mouseX >= 0 && mouseX <= TEXT_OFFSET - 3)
					playSound(filteredSoundEvents.get(slotIndex));
				else if (hasSmartModule && mouseX > TEXT_OFFSET - 3 && mouseX <= right - 6 && slotIndex != selectedSoundIndex) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					selectSound(slotIndex);
				}
			}
		}

		@Override
		public void drawScreen(int mouseX, int mouseY) {
			super.drawScreen(mouseX, mouseY);

			int baseY = top + BORDER - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (BORDER / 2));
			int slotIndex = mouseListY / slotHeight;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size() && mouseX >= left && mouseX < right - 6 && mouseListY >= 0 && mouseY >= top && mouseY <= bottom) {
				String soundEventKey = getSoundEventString(filteredSoundEvents.get(slotIndex));
				int length = fontRenderer.getStringWidth(soundEventKey);

				if (length >= width - 6 - TEXT_OFFSET) {
					net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Collections.singletonList(soundEventKey), left + TEXT_OFFSET - 12, baseY + (slotHeight * slotIndex + slotHeight), width, height, -1, fontRenderer);
					RenderHelper.disableStandardItemLighting(); //fixes the screen title rendering darker if a tooltip is displayed
				}
			}
		}

		@Override
		public void drawPanel(int entryRight, int baseY, Tessellator tesselator, int mouseX, int mouseY) {
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - (BORDER / 2));
			int slotIndex = mouseListY / slotHeight;
			int min = left + TEXT_OFFSET - 2;

			//highlight hovered slot
			if (hasSmartModule && slotIndex != selectedSoundIndex && mouseX >= min && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < filteredSoundEvents.size() && mouseY >= top && mouseY <= bottom)
				renderHighlightBox(entryRight, tesselator, baseY, slotBuffer, slotIndex, min);

			//highlight slot of the currently selected sound
			if (selectedSoundIndex >= 0)
				renderHighlightBox(entryRight, tesselator, baseY, slotBuffer, selectedSoundIndex, min);

			//draw entry strings and sound icons
			for (int i = 0; i < filteredSoundEvents.size(); i++) {
				int yStart = baseY + (slotHeight * i);

				if (yStart + slotHeight < top)
					continue;
				else if (yStart > top + height)
					break;

				ResourceLocation soundEvent = filteredSoundEvents.get(i);
				String name = getSoundEventString(soundEvent);

				fontRenderer.drawString(name, left + TEXT_OFFSET, yStart, 0xC6C6C6);
				mc.getTextureManager().bindTexture(GUI_TEXTURE);
				drawModalRectWithCustomSizedTexture(left, yStart - 1, i == slotIndex && mouseX >= left && mouseX < min && mouseY >= top && mouseY <= bottom ? 9 : 0, 246, 10, 10, 256, 256);
			}
		}

		private String getSoundEventString(ResourceLocation soundEvent) {
			return soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(toLanguageKey(soundEvent)).getFormattedText());
		}

		private void renderHighlightBox(int entryRight, Tessellator tesselator, int baseY, int slotBuffer, int slotIndex, int min) {
			int max = entryRight - 6;
			int slotTop = baseY + slotIndex * slotHeight;
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
		}

		public void selectSound(int slotIndex) {
			selectedSoundIndex = slotIndex;
			AlarmScreen.this.selectSound(filteredSoundEvents.get(slotIndex));
		}

		public void playSound(ResourceLocation soundEvent) {
			SoundHandler soundManager = Minecraft.getMinecraft().getSoundHandler();

			if (playingSound != null)
				soundManager.stopSound(playingSound);

			playingSound = PositionedSoundRecord.getRecord(new SoundEvent(soundEvent), 1.0F, 1.0F);
			soundManager.playSound(playingSound);
		}

		public void updateFilteredEntries(String searchText) {
			//@formatter:off
			filteredSoundEvents = new ArrayList<>(allSoundEvents
					.stream()
					.filter(e -> toLanguageKey(e).contains(searchText))
					.sorted(Comparator.comparing(ResourceLocation::toString))
					.collect(Collectors.toList()));
			//@formatter:on
			recalculateContentHeight();
			updateSelectedSoundIndex();

			if (!searchText.equals(previousSearchText)) {
				previousSearchText = searchText;
				scrollDistance = 0;
			}
		}

		public void recalculateContentHeight() {
			int height = filteredSoundEvents.size() * (fontRenderer.FONT_HEIGHT + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			contentHeight = height;
		}

		public void updateSelectedSoundIndex() {
			selectedSoundIndex = Iterables.indexOf(filteredSoundEvents, se -> se.equals(selectedSoundEvent));
		}

		@Override
		public int getSize() {
			return filteredSoundEvents.size();
		}

		public void updateSize(int leftDifference, int topDifference) {
			left += leftDifference;
			top += topDifference;
			right = left + listWidth;
			bottom = top + listHeight;
			scrollBarRight = left + listWidth;
			scrollBarLeft = scrollBarRight - SCROLL_BAR_WIDTH;
		}
	}
}
