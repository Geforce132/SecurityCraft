package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.registries.ForgeRegistries;

public class AlarmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private final AlarmBlockEntity be;
	private final boolean hasSmartModule;
	private final ITextComponent smartModuleTooltip;
	private final ITextComponent currentlySelectedText = Utils.localize("gui.securitycraft:alarm.currently_selected").withStyle(TextFormatting.UNDERLINE);
	private final ITextComponent soundLengthText = new TranslationTextComponent("gui.securitycraft:alarm.sound_length").withStyle(TextFormatting.UNDERLINE);
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private ITextComponent selectedSoundEventText;
	private int imageWidth = 256, imageHeight = 246, leftPos, topPos;
	private SoundScrollList soundList;
	private Button minusMinute, minusTenSeconds, minusSecond, reset, plusSecond, plusTenSeconds, plusMinute;
	private int previousSoundLength, soundLength;
	private int soundLengthTextXPosition;
	private TextFieldWidget timeEditBox;

	public AlarmScreen(AlarmBlockEntity be, ResourceLocation selectedSoundEvent) {
		super(be.getDisplayName());
		this.be = be;
		this.hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		smartModuleTooltip = Utils.localize(hasSmartModule ? "gui.securitycraft:alarm.smart_module" : "gui.securitycraft:alarm.no_smart_module");
		previousSelectedSoundEvent = selectedSoundEvent;
		previousSoundLength = be.getSoundLength();
		soundLength = previousSoundLength;
		selectSound(selectedSoundEvent);
	}

	@Override
	protected void init() {
		super.init();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		HintEditBox searchBar;
		ITextComponent searchText = Utils.localize("gui.securitycraft:alarm.search");
		int buttonHeight = 13;
		int buttonsX = leftPos + 20;
		int buttonY = topPos + imageHeight - 20;
		int timeEditBoxWidth = 34;
		int soundLengthTextWidthPlusBuffer = font.width(soundLengthText) + 5;
		int combinedTextAndBoxWidth = soundLengthTextWidthPlusBuffer + timeEditBoxWidth;

		soundLengthTextXPosition = width / 2 - combinedTextAndBoxWidth / 2;
		children.add(soundList = new SoundScrollList(minecraft, imageWidth - 10, imageHeight - 105, topPos + 40, leftPos + 5));
		searchBar = addButton(new HintEditBox(font, leftPos + 30, topPos + 20, imageWidth - 60, 15, searchText));
		searchBar.setHint(searchText);
		searchBar.setFilter(s -> s.matches("[a-zA-Z0-9\\._]*"));
		searchBar.setResponder(soundList::updateFilteredEntries);
		timeEditBox = addButton(new SoundLengthEditBox(font, soundLengthTextXPosition + soundLengthTextWidthPlusBuffer, buttonY - 15, timeEditBoxWidth, 12, StringTextComponent.EMPTY));
		timeEditBox.setFilter(string -> string.matches("[0-9:]*"));
		minusMinute = addButton(new ExtendedButton(buttonsX, buttonY, 32, buttonHeight, new TranslationTextComponent("gui.securitycraft:alarm.minus_one_minute"), b -> changeSoundLength(soundLength - 60)));
		minusTenSeconds = addButton(new ExtendedButton(buttonsX + 34, buttonY, 32, buttonHeight, new TranslationTextComponent("gui.securitycraft:alarm.minus_ten_seconds"), b -> changeSoundLength(soundLength - 10)));
		minusSecond = addButton(new ExtendedButton(buttonsX + 68, buttonY, 32, buttonHeight, new TranslationTextComponent("gui.securitycraft:alarm.minus_one_second"), b -> changeSoundLength(soundLength - 1)));
		reset = addButton(new ActiveBasedTextureButton(buttonsX + 102, buttonY, 12, buttonHeight, RESET_TEXTURE, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSoundLength(previousSoundLength)));
		plusSecond = addButton(new ExtendedButton(buttonsX + 116, buttonY, 32, buttonHeight, new TranslationTextComponent("gui.securitycraft:alarm.plus_one_second"), b -> changeSoundLength(soundLength + 1)));
		plusTenSeconds = addButton(new ExtendedButton(buttonsX + 150, buttonY, 32, buttonHeight, new TranslationTextComponent("gui.securitycraft:alarm.plus_ten_seconds"), b -> changeSoundLength(soundLength + 10)));
		plusMinute = addButton(new ExtendedButton(buttonsX + 184, buttonY, 32, buttonHeight, new TranslationTextComponent("gui.securitycraft:alarm.plus_one_minute"), b -> changeSoundLength(soundLength + 60)));
		changeSoundLength(soundLength);
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(GUI_TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);

		if (soundList != null)
			soundList.render(pose, mouseX, mouseY, partialTicks);

		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
		font.draw(pose, currentlySelectedText, width / 2 - font.width(currentlySelectedText) / 2, topPos + imageHeight - 62, 4210752);
		font.draw(pose, selectedSoundEventText, width / 2 - font.width(selectedSoundEventText) / 2, topPos + imageHeight - 49, 4210752);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
		font.draw(pose, soundLengthText, soundLengthTextXPosition, topPos + imageHeight - 33, 4210752);
	}

	public void selectSound(ResourceLocation eventId) {
		this.selectedSoundEvent = eventId;
		selectedSoundEventText = Utils.localize(toLanguageKey(selectedSoundEvent));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		super.onClose();

		if (soundList != null && soundList.playingSound != null)
			Minecraft.getInstance().getSoundManager().stop(soundList.playingSound);

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
			SecurityCraft.channel.sendToServer(new SyncAlarmSettings(be.getBlockPos(), selectedSoundEvent, soundLength));
	}

	public void changeSoundLength(int newSoundLength) {
		changeSoundLength(newSoundLength, true);
	}

	public void changeSoundLength(int newSoundLength, boolean updateTimeEditBox) {
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		soundLength = Math.max(1, Math.min(newSoundLength, AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH));

		if (updateTimeEditBox)
			timeEditBox.setValue(String.format("%02d:%02d", soundLength / 60, soundLength % 60));

		enablePlusButtons = soundLength < AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH;
		enableMinusButtons = soundLength > 1;
		minusMinute.active = enableMinusButtons;
		minusTenSeconds.active = enableMinusButtons;
		minusSecond.active = enableMinusButtons;
		reset.active = soundLength != previousSoundLength;
		plusSecond.active = enablePlusButtons;
		plusTenSeconds.active = enablePlusButtons;
		plusMinute.active = enablePlusButtons;
	}

	private String toLanguageKey(ResourceLocation resourceLocation) {
		return resourceLocation.getNamespace() + "." + resourceLocation.getPath();
	}

	public class SoundScrollList extends ScrollPanel {
		public final List<SoundEvent> allSoundEvents = new ArrayList<>(ForgeRegistries.SOUND_EVENTS.getValues());
		private final int slotHeight = 12, textOffset = 11;
		private final Map<SoundEvent, ITextComponent> soundEventKeys = new HashMap<>();
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
		protected int getContentHeight() {
			return contentHeight;
		}

		@Override
		protected boolean clickPanel(double mouseX, double mouseY, int button) {
			int slotIndex = (int) (mouseY + (border / 2)) / slotHeight;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size()) {
				Minecraft mc = Minecraft.getInstance();
				double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

				if (relativeMouseY < top || relativeMouseY > bottom)
					return false;
				else if (mouseX >= 0 && mouseX <= textOffset - 2)
					playSound(filteredSoundEvents.get(slotIndex));
				else if (hasSmartModule && mouseX > textOffset - 2 && mouseX <= right - 6 && slotIndex != selectedSoundIndex) {
					Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					selectSound(slotIndex);
				}

				return true;
			}

			return false;
		}

		@Override
		public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
			super.render(pose, mouseX, mouseY, partialTick);

			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size() && mouseX >= left && mouseX < right - 6 && mouseListY >= 0 && mouseY >= top && mouseY <= bottom) {
				ITextComponent soundEventKey = getSoundEventComponent(filteredSoundEvents.get(slotIndex));
				int length = font.width(soundEventKey);

				if (length >= width - 6 - textOffset)
					renderTooltip(pose, soundEventKey, left + textOffset - 12, baseY + (slotHeight * slotIndex + slotHeight));
			}
		}

		@Override
		protected void drawPanel(MatrixStack pose, int entryRight, int baseY, Tessellator tesselator, int mouseX, int mouseY) {
			FontRenderer font = minecraft.font;
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
				ITextComponent name = getSoundEventComponent(soundEvent);

				font.draw(pose, name, left + textOffset, yStart, 0xC6C6C6);
				minecraft.textureManager.bind(GUI_TEXTURE);
				blit(pose, left, yStart - 1, getBlitOffset(), i == slotIndex && mouseX >= left && mouseX < min && mouseY >= top && mouseY <= bottom ? 9 : 0, 246, 10, 10, 256, 256);
			}
		}

		private ITextComponent getSoundEventComponent(SoundEvent soundEvent) {
			return soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(toLanguageKey(soundEvent.getLocation())));
		}

		private void renderHighlightBox(int entryRight, Tessellator tesselator, int baseY, int slotBuffer, int slotIndex, int min) {
			int max = entryRight - 6;
			int slotTop = baseY + slotIndex * slotHeight;
			BufferBuilder bufferBuilder = tesselator.getBuilder();

			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).uv(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).uv(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop - 2, 0).uv(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min, slotTop - 2, 0).uv(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).uv(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).uv(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop - 1, 0).uv(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop - 1, 0).uv(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.end();
			WorldVertexBufferUploader.end(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}

		public void selectSound(int slotIndex) {
			selectedSoundIndex = slotIndex;
			AlarmScreen.this.selectSound(filteredSoundEvents.get(slotIndex).getLocation());
		}

		public void playSound(SoundEvent soundEvent) {
			SoundHandler soundManager = Minecraft.getInstance().getSoundManager();

			if (playingSound != null)
				soundManager.stop(playingSound);

			playingSound = SimpleSound.forUI(soundEvent, 1.0F, 1.0F);
			soundManager.play(playingSound);
		}

		public void updateFilteredEntries(String searchText) {
			//@formatter:off
			filteredSoundEvents = new ArrayList<>(allSoundEvents
					.stream()
					.filter(e -> toLanguageKey(e.getLocation()).contains(searchText))
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
			int height = filteredSoundEvents.size() * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			contentHeight = height;
		}

		public void updateSelectedSoundIndex() {
			selectedSoundIndex = Iterables.indexOf(filteredSoundEvents, se -> se.getLocation().equals(selectedSoundEvent));
		}
	}

	public class SoundLengthEditBox extends TextFieldWidget {
		public SoundLengthEditBox(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
			super(font, x, y, width, height, message);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
			changeSoundLength(soundLength + (int) Math.signum(delta));
			return super.mouseScrolled(mouseX, mouseY, delta);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
				checkAndProcessInput();

			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void setFocus(boolean focused) {
			if (isFocused() && !focused)
				checkAndProcessInput();

			super.setFocus(focused);
		}

		private void checkAndProcessInput() {
			int minutes = 0;
			int seconds = 2;

			if (value != null && !value.isEmpty()) {
				if (!value.contains(":")) {
					int numeric = Integer.parseInt(value);

					minutes = numeric / 60;
					seconds = numeric % 60;
				}
				else {
					String[] split = value.split(":");

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
			moveCursorToEnd();
		}
	}

	public class HintEditBox extends TextFieldWidget {
		private ITextComponent hint;

		public HintEditBox(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
			super(font, x, y, width, height, message);
		}

		@Override
		public void renderButton(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
			super.renderButton(pose, mouseX, mouseY, partialTick);

			if (isVisible() && hint != null && value.isEmpty() && !isFocused())
				font.drawShadow(pose, hint, x + 4, y + (height - 8) / 2, 0xE0E0E0);
		}

		public void setHint(ITextComponent hint) {
			this.hint = hint;
		}
	}
}
