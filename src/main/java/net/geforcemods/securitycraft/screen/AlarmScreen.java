package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.registries.ForgeRegistries;

public class AlarmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private final AlarmBlockEntity be;
	private final boolean hasSmartModule;
	private final Component smartModuleTooltip, currentlySelectedText = Utils.localize("gui.securitycraft:alarm.currently_selected").withStyle(ChatFormatting.UNDERLINE);
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private Component selectedSoundEventText;
	private int imageWidth = 256, imageHeight = 246, leftPos, topPos;
	private SoundScrollList soundList;
	private Button minusMinute, minusTenSeconds, minusSecond, reset, plusSecond, plusTenSeconds, plusMinute;
	private int previousSoundLength, soundLength;
	private Component soundLengthText;
	private int soundLengthTextLength, soundLengthTextStartX;

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

		EditBox searchBar;
		Component searchText = Utils.localize("gui.securitycraft:alarm.edit_box");
		int buttonHeight = 13;
		int buttonsX = leftPos + 20;
		int buttonY = topPos + imageHeight - 20;

		soundList = addRenderableWidget(new SoundScrollList(minecraft, imageWidth - 10, imageHeight - 105, topPos + 40, leftPos + 5));
		searchBar = addRenderableWidget(new EditBox(font, leftPos + 30, topPos + 20, imageWidth - 60, 15, searchText));
		searchBar.setHint(searchText);
		searchBar.setFilter(s -> s.matches("[a-zA-Z0-9\\._]*"));
		searchBar.setResponder(soundList::updateFilteredEntries);
		minusMinute = addRenderableWidget(new ExtendedButton(buttonsX, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.minus_one_minute"), b -> changeSoundLength(soundLength - 60)));
		minusTenSeconds = addRenderableWidget(new ExtendedButton(buttonsX + 34, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.minus_ten_seconds"), b -> changeSoundLength(soundLength - 10)));
		minusSecond = addRenderableWidget(new ExtendedButton(buttonsX + 68, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.minus_one_second"), b -> changeSoundLength(soundLength - 1)));
		reset = addRenderableWidget(new ActiveBasedTextureButton(buttonsX + 102, buttonY, 12, buttonHeight, RESET_TEXTURE, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSoundLength(previousSoundLength)));
		plusSecond = addRenderableWidget(new ExtendedButton(buttonsX + 116, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.plus_one_second"), b -> changeSoundLength(soundLength + 1)));
		plusTenSeconds = addRenderableWidget(new ExtendedButton(buttonsX + 150, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.plus_ten_seconds"), b -> changeSoundLength(soundLength + 10)));
		plusMinute = addRenderableWidget(new ExtendedButton(buttonsX + 184, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.plus_one_minute"), b -> changeSoundLength(soundLength + 60)));
		changeSoundLength(soundLength);//24,18,12
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, GUI_TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
		font.draw(pose, currentlySelectedText, width / 2 - font.width(currentlySelectedText) / 2, topPos + imageHeight - 62, 4210752);
		font.draw(pose, selectedSoundEventText, width / 2 - font.width(selectedSoundEventText) / 2, topPos + imageHeight - 49, 4210752);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
		font.draw(pose, soundLengthText, width / 2 - font.width(soundLengthText) / 2, topPos + imageHeight - 33, 4210752);
	}

	public void selectSound(ResourceLocation eventId) {
		this.selectedSoundEvent = eventId;
		selectedSoundEventText = Utils.localize(selectedSoundEvent.toLanguageKey());
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

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (mouseX >= leftPos + soundLengthTextStartX && mouseY >= topPos + 23 && mouseX <= leftPos + soundLengthTextStartX + soundLengthTextLength && mouseY <= topPos + 43)
			changeSoundLength(soundLength + (int) Math.signum(delta));

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	public void changeSoundLength(int newSoundLength) {
		boolean enablePlusButtons;
		boolean enableMinusButtons;

		soundLength = Math.max(1, Math.min(newSoundLength, Integer.MAX_VALUE));
		soundLengthText = Component.translatable("gui.securitycraft:alarm.sound_length", Component.literal(String.format("%02d:%02d", soundLength / 60, soundLength % 60)).withStyle(ChatFormatting.RESET)).withStyle(ChatFormatting.UNDERLINE);
		soundLengthTextLength = font.width(soundLengthText);
		soundLengthTextStartX = imageWidth / 2 - soundLengthTextLength / 2;
		enablePlusButtons = soundLength != Integer.MAX_VALUE;
		enableMinusButtons = soundLength > 1;
		minusMinute.active = enableMinusButtons;
		minusTenSeconds.active = enableMinusButtons;
		minusSecond.active = enableMinusButtons;
		reset.active = soundLength != previousSoundLength;
		plusSecond.active = enablePlusButtons;
		plusTenSeconds.active = enablePlusButtons;
		plusMinute.active = enablePlusButtons;
	}

	public class SoundScrollList extends ScrollPanel {
		public final List<SoundEvent> allSoundEvents = new ArrayList<>(ForgeRegistries.SOUND_EVENTS.getValues());
		private final int slotHeight = 12, textOffset = 11;
		private final Map<SoundEvent, Component> soundEventKeys = new HashMap<>();
		private List<SoundEvent> filteredSoundEvents;
		private SoundInstance playingSound;
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
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					selectSound(slotIndex);
				}

				return true;
			}

			return false;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
			super.render(pose, mouseX, mouseY, partialTick);

			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;

			if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < filteredSoundEvents.size() && mouseY >= top && mouseY <= bottom) {
				Component soundEventKey = getSoundEventComponent(filteredSoundEvents.get(slotIndex));
				int length = font.width(soundEventKey);

				if (length >= width - 6 - textOffset)
					renderTooltip(pose, List.of(soundEventKey), Optional.empty(), left + textOffset - 12, baseY + (slotHeight * slotIndex + slotHeight));
			}
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int baseY, Tesselator tesselator, int mouseX, int mouseY) {
			Font font = Minecraft.getInstance().font;
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
				Component name = getSoundEventComponent(soundEvent);

				font.draw(pose, name, left + textOffset, yStart, 0xC6C6C6);
				RenderSystem._setShaderTexture(0, GUI_TEXTURE);
				blit(pose, left, yStart - 1, getBlitOffset(), i == slotIndex && mouseX >= left && mouseX < min && mouseY >= top && mouseY <= bottom ? 9 : 0, 246, 10, 10, 256, 256);
			}
		}

		private Component getSoundEventComponent(SoundEvent soundEvent) {
			return soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(soundEvent.getLocation().toLanguageKey()));
		}

		private void renderHighlightBox(int entryRight, Tesselator tesselator, int baseY, int slotBuffer, int slotIndex, int min) {
			int max = entryRight - 6;
			int slotTop = baseY + slotIndex * slotHeight;
			BufferBuilder bufferBuilder = tesselator.getBuilder();

			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).uv(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).uv(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop - 2, 0).uv(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min, slotTop - 2, 0).uv(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).uv(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).uv(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop - 1, 0).uv(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop - 1, 0).uv(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			BufferUploader.drawWithShader(bufferBuilder.end());
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}

		public void selectSound(int slotIndex) {
			selectedSoundIndex = slotIndex;
			AlarmScreen.this.selectSound(filteredSoundEvents.get(slotIndex).getLocation());
		}

		public void playSound(SoundEvent soundEvent) {
			SoundManager soundManager = Minecraft.getInstance().getSoundManager();

			if (playingSound != null)
				soundManager.stop(playingSound);

			playingSound = SimpleSoundInstance.forUI(soundEvent, 1.0F, 1.0F);
			soundManager.play(playingSound);
		}

		public void updateFilteredEntries(String searchText) {
			//@formatter:off
			filteredSoundEvents = new ArrayList<>(allSoundEvents
					.stream()
					.filter(e -> e.getLocation().toLanguageKey().contains(searchText))
					.toList());
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

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}
}
