package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.network.PacketDistributor;

public class AlarmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/alarm.png");
	private static final ResourceLocation PLAY_SOUND_SPRITE = SecurityCraft.resLoc("alarm/play_sound");
	private static final ResourceLocation PLAY_SOUND_HIGHLIGHTED_SPRITE = SecurityCraft.resLoc("alarm/play_sound_highlighted");
	protected final AlarmBlockEntity be;
	private final boolean hasSmartModule;
	private final Component smartModuleTooltip;
	private final Component currentlySelectedText = Utils.localize("gui.securitycraft:alarm.currently_selected").withStyle(ChatFormatting.UNDERLINE);
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private Component selectedSoundEventText;
	private int imageWidth = 256, imageHeight = 246, leftPos, topPos;
	private SoundScrollList soundList;
	protected int previousSoundLength, soundLength;
	protected float previousPitch, pitch;
	private EditBox searchBar;

	public AlarmScreen(AlarmBlockEntity be, ResourceLocation selectedSoundEvent) {
		super(be.getDisplayName());
		this.be = be;
		this.hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		smartModuleTooltip = Utils.localize(hasSmartModule ? "gui.securitycraft:alarm.smart_module" : "gui.securitycraft:alarm.no_smart_module");
		previousSelectedSoundEvent = selectedSoundEvent;
		previousSoundLength = be.getSoundLength();
		soundLength = previousSoundLength;
		previousPitch = be.getPitch();
		pitch = previousPitch;
		selectSound(selectedSoundEvent);
	}

	@Override
	protected void init() {
		super.init();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		Component searchText = Utils.localize("gui.securitycraft:alarm.search");

		soundList = addRenderableWidget(new SoundScrollList(minecraft, imageWidth - 10, imageHeight - 105, topPos + 40, leftPos + 5));
		searchBar = addRenderableWidget(new EditBox(font, leftPos + 30, topPos + 20, imageWidth - 60, 15, searchText));
		searchBar.setHint(searchText);
		searchBar.setFilter(s -> s.matches("[a-zA-Z0-9\\._]*"));
		searchBar.setResponder(soundList::updateFilteredEntries);
		addRenderableWidget(new Button(leftPos + imageWidth / 2 - 170 / 2, topPos + 215, 170, 20, Utils.localize("menu.options"), b -> Minecraft.getInstance().pushGuiLayer(new AlarmOptionsScreen(this)), Button.DEFAULT_NARRATION));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752, false);
		guiGraphics.drawString(font, currentlySelectedText, width / 2 - font.width(currentlySelectedText) / 2, topPos + imageHeight - 62, 4210752, false);
		guiGraphics.drawString(font, selectedSoundEventText, width / 2 - font.width(selectedSoundEventText) / 2, topPos + imageHeight - 49, 4210752, false);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(RenderType::guiTextured, GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	public void selectSound(ResourceLocation eventId) {
		this.selectedSoundEvent = eventId;
		selectedSoundEventText = Utils.localize(selectedSoundEvent.toLanguageKey());
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

		if (pitch != previousPitch) {
			be.setPitch(pitch);
			changed = true;
		}

		if (soundLength != previousSoundLength) {
			be.setSoundLength(soundLength);
			changed = true;
		}

		if (changed)
			PacketDistributor.sendToServer(new SyncAlarmSettings(be.getBlockPos(), selectedSoundEvent, pitch, soundLength));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!searchBar.isFocused() && minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public class SoundScrollList extends ScrollPanel {
		private static final int SLOT_HEIGHT = 12, TEXT_OFFSET = 11;
		public final List<ResourceLocation> allSoundEvents = minecraft.getSoundManager().getAvailableSounds().stream().sorted(Comparator.comparing(ResourceLocation::toLanguageKey)).toList();
		private final Map<ResourceLocation, Component> soundEventKeys = new HashMap<>();
		private List<ResourceLocation> filteredSoundEvents;
		private SoundInstance playingSound;
		private int selectedSoundIndex, contentHeight = 0;
		private String previousSearchText = "";

		public SoundScrollList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			updateFilteredEntries("");
			scrollDistance = selectedSoundIndex * SLOT_HEIGHT;

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
			int slotIndex = (int) (mouseY + (border / 2)) / SLOT_HEIGHT;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size()) {
				Minecraft mc = Minecraft.getInstance();
				double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

				if (relativeMouseY < top || relativeMouseY > bottom)
					return false;
				else if (mouseX >= 0 && mouseX <= TEXT_OFFSET - 2)
					playSound(filteredSoundEvents.get(slotIndex));
				else if (hasSmartModule && mouseX > TEXT_OFFSET - 2 && mouseX <= right - 6 && slotIndex != selectedSoundIndex) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					selectSound(slotIndex);
				}

				return true;
			}

			return false;
		}

		@Override
		protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {
			drawGradientRect(guiGraphics, left, top, right, bottom, 0xC0101010, 0xD0101010);
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			guiGraphics.flush();
			super.render(guiGraphics, mouseX, mouseY, partialTick);

			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / SLOT_HEIGHT;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size() && mouseX >= left && mouseX < right - 6 && mouseListY >= 0 && mouseY >= top && mouseY <= bottom) {
				Component soundEventKey = getSoundEventComponent(filteredSoundEvents.get(slotIndex));
				int length = font.width(soundEventKey);

				if (length >= width - 6 - TEXT_OFFSET)
					guiGraphics.renderTooltip(font, List.of(soundEventKey), Optional.empty(), left + TEXT_OFFSET - 12, baseY + (SLOT_HEIGHT * slotIndex + SLOT_HEIGHT));
			}
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int baseY, Tesselator tesselator, int mouseX, int mouseY) {
			Font font = Minecraft.getInstance().font;
			int slotBuffer = SLOT_HEIGHT - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / SLOT_HEIGHT;
			int min = left + TEXT_OFFSET - 2;

			//highlight hovered slot
			if (hasSmartModule && slotIndex != selectedSoundIndex && mouseX >= min && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < filteredSoundEvents.size() && mouseY >= top && mouseY <= bottom)
				renderHighlightBox(entryRight, guiGraphics, baseY, slotBuffer, slotIndex, min);

			//highlight slot of the currently selected sound
			if (selectedSoundIndex >= 0)
				renderHighlightBox(entryRight, guiGraphics, baseY, slotBuffer, selectedSoundIndex, min);

			//draw entry strings and sound icons
			for (int i = 0; i < filteredSoundEvents.size(); i++) {
				int yStart = baseY + (SLOT_HEIGHT * i);

				if (yStart + SLOT_HEIGHT < top)
					continue;
				else if (yStart > top + height)
					break;

				ResourceLocation soundEvent = filteredSoundEvents.get(i);
				Component name = getSoundEventComponent(soundEvent);

				guiGraphics.drawString(font, name, left + TEXT_OFFSET, yStart, 0xC6C6C6, false);
				guiGraphics.blitSprite(RenderType::guiTextured, i == slotIndex && mouseX >= left && mouseX < min && mouseY >= top && mouseY <= bottom ? PLAY_SOUND_HIGHLIGHTED_SPRITE : PLAY_SOUND_SPRITE, left, yStart - 1, 10, 10);
			}

			guiGraphics.flush();
		}

		private Component getSoundEventComponent(ResourceLocation soundEvent) {
			return soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(soundEvent.toLanguageKey()));
		}

		private void renderHighlightBox(int entryRight, GuiGraphics guiGraphics, int baseY, int slotBuffer, int slotIndex, int min) {
			int max = entryRight - 6;
			int slotTop = baseY + slotIndex * SLOT_HEIGHT;

			guiGraphics.fill(min, slotTop - 2, max, slotTop + slotBuffer + 2, 0xFF808080);
			guiGraphics.fill(min + 1, slotTop - 1, max - 1, slotTop + slotBuffer + 1, 0xFF000000);
		}

		public void selectSound(int slotIndex) {
			selectedSoundIndex = slotIndex;
			AlarmScreen.this.selectSound(filteredSoundEvents.get(slotIndex));
		}

		public void playSound(ResourceLocation soundEvent) {
			SoundManager soundManager = Minecraft.getInstance().getSoundManager();

			if (playingSound != null)
				soundManager.stop(playingSound);

			playingSound = SimpleSoundInstance.forUI(SoundEvent.createFixedRangeEvent(soundEvent, 1.0F), pitch, 1.0F);
			soundManager.play(playingSound);
		}

		public void updateFilteredEntries(String searchText) {
			//@formatter:off
			filteredSoundEvents = new ArrayList<>(allSoundEvents
					.stream()
					.filter(e -> e.toLanguageKey().contains(searchText))
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
			selectedSoundIndex = Iterables.indexOf(filteredSoundEvents, se -> se.equals(selectedSoundEvent));
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}
}
