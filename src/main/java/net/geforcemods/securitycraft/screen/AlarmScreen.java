package net.geforcemods.securitycraft.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.ColorableScrollPanel;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class AlarmScreen extends GuiScreen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private final AlarmBlockEntity be;
	private final boolean hasSmartModule;
	private final String smartModuleTooltip;
	private final String currentlySelectedText = Utils.localize("gui.securitycraft:alarm.currently_selected").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private final String soundLengthText = new TextComponentTranslation("gui.securitycraft:alarm.sound_length").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private String title;
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private String selectedSoundEventText;
	private int imageWidth = 256, imageHeight = 246, leftPos, topPos;
	private SoundScrollList soundList;
	private GuiButton minusMinute, minusTenSeconds, minusSecond, reset, plusSecond, plusTenSeconds, plusMinute;
	private int previousSoundLength, soundLength;
	private int soundLengthTextXPosition;
	private HintEditBox searchBar;
	private SoundLengthEditBox timeEditBox;

	public AlarmScreen(AlarmBlockEntity be, ResourceLocation selectedSoundEvent) {
		this.be = be;
		this.hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		smartModuleTooltip = Utils.localize(hasSmartModule ? "gui.securitycraft:alarm.smart_module" : "gui.securitycraft:alarm.no_smart_module").getFormattedText();
		previousSelectedSoundEvent = selectedSoundEvent;
		previousSoundLength = be.getSoundLength();
		soundLength = previousSoundLength;
		title = be.getName();
		selectSound(selectedSoundEvent);
	}

	@Override
	public void initGui() {
		super.initGui();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		int buttonHeight = 13;
		int buttonsX = leftPos + 20;
		int buttonY = topPos + imageHeight - 20;
		int timeEditBoxWidth = 34;
		int soundLengthTextWidthPlusBuffer = fontRenderer.getStringWidth(soundLengthText) + 5;
		int combinedTextAndBoxWidth = soundLengthTextWidthPlusBuffer + timeEditBoxWidth;
		int id = 0;

		soundLengthTextXPosition = width / 2 - combinedTextAndBoxWidth / 2;
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
		timeEditBox = new SoundLengthEditBox(id++, fontRenderer, soundLengthTextXPosition + soundLengthTextWidthPlusBuffer, buttonY - 15, timeEditBoxWidth, 12);
		timeEditBox.setValidator(string -> string.matches("[0-9:]*"));
		buttonList.add(minusMinute = new ClickButton(id++, buttonsX, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.minus_one_minute").getFormattedText(), b -> changeSoundLength(soundLength - 60)));
		buttonList.add(minusTenSeconds = new ClickButton(id++, buttonsX + 34, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.minus_ten_seconds").getFormattedText(), b -> changeSoundLength(soundLength - 10)));
		buttonList.add(minusSecond = new ClickButton(id++, buttonsX + 68, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.minus_one_second").getFormattedText(), b -> changeSoundLength(soundLength - 1)));
		buttonList.add(reset = new PictureButton(id++, buttonsX + 102, buttonY, 12, buttonHeight, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSoundLength(previousSoundLength)) {
			@Override
			public ResourceLocation getTextureLocation() {
				return enabled ? RESET_TEXTURE : RESET_INACTIVE_TEXTURE;
			}
		});
		buttonList.add(plusSecond = new ClickButton(id++, buttonsX + 116, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.plus_one_second").getFormattedText(), b -> changeSoundLength(soundLength + 1)));
		buttonList.add(plusTenSeconds = new ClickButton(id++, buttonsX + 150, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.plus_ten_seconds").getFormattedText(), b -> changeSoundLength(soundLength + 10)));
		buttonList.add(plusMinute = new ClickButton(id++, buttonsX + 184, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.plus_one_minute").getFormattedText(), b -> changeSoundLength(soundLength + 60)));
		changeSoundLength(soundLength);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (soundList != null)
			soundList.drawScreen(mouseX, mouseY, partialTicks);

		searchBar.drawTextBox();
		timeEditBox.drawTextBox();
		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 4210752);
		fontRenderer.drawString(currentlySelectedText, width / 2 - fontRenderer.getStringWidth(currentlySelectedText) / 2, topPos + imageHeight - 62, 4210752);
		fontRenderer.drawString(selectedSoundEventText, width / 2 - fontRenderer.getStringWidth(selectedSoundEventText) / 2, topPos + imageHeight - 49, 4210752);
		fontRenderer.drawString(soundLengthText, soundLengthTextXPosition, topPos + imageHeight - 33, 4210752);
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
		timeEditBox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		double mouseX = Mouse.getEventX() * width / mc.displayWidth;
		double mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		if (timeEditBox.isHovered(mouseX, mouseY))
			timeEditBox.mouseScrolled(mouseX, mouseY, Mouse.getEventDWheel());

		if (soundList != null)
			soundList.handleMouseInput((int) mouseX, (int) mouseY);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		searchBar.textboxKeyTyped(typedChar, keyCode);
		timeEditBox.textboxKeyTyped(typedChar, keyCode);

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

		if (soundLength != previousSoundLength) {
			be.setSoundLength(soundLength);
			changed = true;
		}

		if (changed)
			SecurityCraft.network.sendToServer(new SyncAlarmSettings(be.getPos(), selectedSoundEvent, soundLength));
	}

	public void changeSoundLength(int newSoundLength) {
		changeSoundLength(newSoundLength, true);
	}

	public void changeSoundLength(int newSoundLength, boolean updateTimeEditBox) {
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		soundLength = Math.max(1, Math.min(newSoundLength, AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH));

		if (updateTimeEditBox)
			timeEditBox.setText(String.format("%02d:%02d", soundLength / 60, soundLength % 60));

		enablePlusButtons = soundLength < AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH;
		enableMinusButtons = soundLength > 1;
		minusMinute.enabled = enableMinusButtons;
		minusTenSeconds.enabled = enableMinusButtons;
		minusSecond.enabled = enableMinusButtons;
		reset.enabled = soundLength != previousSoundLength;
		plusSecond.enabled = enablePlusButtons;
		plusTenSeconds.enabled = enablePlusButtons;
		plusMinute.enabled = enablePlusButtons;
	}

	private String toLanguageKey(ResourceLocation resourceLocation) {
		return resourceLocation.getNamespace() + "." + resourceLocation.getPath();
	}

	public class SoundScrollList extends ColorableScrollPanel {
		public final List<SoundEvent> allSoundEvents = new ArrayList<>(ForgeRegistries.SOUND_EVENTS.getValues());
		private final int textOffset = 11;
		private final Map<SoundEvent, String> soundEventKeys = new HashMap<>();
		private List<SoundEvent> filteredSoundEvents;
		private ISound playingSound;
		private int selectedSoundIndex, contentHeight = 0;
		private String previousSearchText = "";

		public SoundScrollList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			updateFilteredEntries("");
			scrollDistance = selectedSoundIndex * slotHeight;

			int maxScroll = getContentHeight() - (height - border);

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

				if (mouseX >= 0 && mouseX <= textOffset - 3)
					playSound(filteredSoundEvents.get(slotIndex));
				else if (hasSmartModule && mouseX > textOffset - 3 && mouseX <= right - 6 && slotIndex != selectedSoundIndex) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					selectSound(slotIndex);
				}
			}
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTick) {
			super.drawScreen(mouseX, mouseY, partialTick);

			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size() && mouseX >= left && mouseX < right - 6 && mouseListY >= 0 && mouseY >= top && mouseY <= bottom) {
				String soundEventKey = getSoundEventString(filteredSoundEvents.get(slotIndex));
				int length = fontRenderer.getStringWidth(soundEventKey);

				if (length >= width - 6 - textOffset) {
					net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(Collections.singletonList(soundEventKey), left + textOffset - 12, baseY + (slotHeight * slotIndex + slotHeight), width, height, -1, fontRenderer);
					RenderHelper.disableStandardItemLighting(); //fixes the screen title rendering darker if a tooltip is displayed
				}
			}
		}

		@Override
		public void drawPanel(int entryRight, int baseY, Tessellator tesselator, int mouseX, int mouseY) {
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;
			int min = left + textOffset - 2;

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

				SoundEvent soundEvent = filteredSoundEvents.get(i);
				String name = getSoundEventString(soundEvent);

				fontRenderer.drawString(name, left + textOffset, yStart, 0xC6C6C6);
				mc.getTextureManager().bindTexture(GUI_TEXTURE);
				drawModalRectWithCustomSizedTexture(left, yStart - 1, i == slotIndex && mouseX >= left && mouseX < min && mouseY >= top && mouseY <= bottom ? 9 : 0, 246, 10, 10, 256, 256);
			}
		}

		private String getSoundEventString(SoundEvent soundEvent) {
			return soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(toLanguageKey(soundEvent.getRegistryName())).getFormattedText());
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
			AlarmScreen.this.selectSound(filteredSoundEvents.get(slotIndex).getRegistryName());
		}

		public void playSound(SoundEvent soundEvent) {
			SoundHandler soundManager = Minecraft.getMinecraft().getSoundHandler();

			if (playingSound != null)
				soundManager.stopSound(playingSound);

			playingSound = PositionedSoundRecord.getRecord(soundEvent, 1.0F, 1.0F);
			soundManager.playSound(playingSound);
		}

		public void updateFilteredEntries(String searchText) {
			//@formatter:off
			filteredSoundEvents = new ArrayList<>(allSoundEvents
					.stream()
					.filter(e -> toLanguageKey(e.getRegistryName()).contains(searchText))
					.sorted((se1, se2) -> se1.getRegistryName().toString().compareTo(se2.getRegistryName().toString()))
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
			selectedSoundIndex = Iterables.indexOf(filteredSoundEvents, se -> se.getRegistryName().equals(selectedSoundEvent));
		}

		@Override
		public int getSize() {
			return filteredSoundEvents.size();
		}
	}

	public class SoundLengthEditBox extends GuiTextField {
		public SoundLengthEditBox(int id, FontRenderer font, int x, int y, int width, int height) {
			super(id, font, x, y, width, height);
		}

		public void mouseScrolled(double mouseX, double mouseY, double delta) {
			changeSoundLength(soundLength + (int) Math.signum(delta));
		}

		@Override
		public boolean textboxKeyTyped(char typedChar, int keyCode) {
			if (isFocused() && keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER)
				checkAndProcessInput();

			return super.textboxKeyTyped(typedChar, keyCode);
		}

		@Override
		public void setFocused(boolean focused) {
			if (isFocused() && !focused)
				checkAndProcessInput();

			super.setFocused(focused);
		}

		private void checkAndProcessInput() {
			int minutes = 0;
			int seconds = 2;

			if (text != null && !text.isEmpty()) {
				if (!text.contains(":")) {
					int numeric = Integer.parseInt(text);

					minutes = numeric / 60;
					seconds = numeric % 60;
				}
				else {
					String[] split = text.split(":");

					if (split.length == 2) {
						if (!split[0].isEmpty()) {
							try {
								minutes = Integer.parseInt(split[0]);
							}
							catch (NumberFormatException e) { //usually happens when the entered number is too big
								minutes = 60;
							}
						}

						if (!split[1].isEmpty()) {
							try {
								seconds = Integer.parseInt(split[1]);
							}
							catch (NumberFormatException e) {} //usually happens when the entered number is too big
						}
						else
							seconds = 0;
					}
				}
			}

			changeSoundLength(minutes * 60 + seconds, true);
			setCursorPositionEnd();
		}

		public boolean isHovered(double mouseX, double mouseY) {
			return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		}
	}

	public class HintEditBox extends GuiTextField {
		private String hint;

		public HintEditBox(int id, FontRenderer font, int x, int y, int width, int height) {
			super(id, font, x, y, width, height);
		}

		@Override
		public void drawTextBox() {
			super.drawTextBox();

			if (getVisible() && hint != null && text.isEmpty() && !isFocused())
				fontRenderer.drawStringWithShadow(hint, x + 4, y + (height - 8) / 2, 0xE0E0E0);
		}

		public void setHint(String hint) {
			this.hint = hint;
		}
	}
}
