package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.screen.components.HintEditBox;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class AlarmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png");
	protected final AlarmBlockEntity be;
	private final boolean hasSmartModule;
	private final ITextComponent smartModuleTooltip;
	private final ITextComponent currentlySelectedText = Utils.localize("gui.securitycraft:alarm.currently_selected").withStyle(TextFormatting.UNDERLINE);
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private ITextComponent selectedSoundEventText;
	private int imageWidth = 256, imageHeight = 246, leftPos, topPos;
	private SoundScrollList soundList;
	protected int previousSoundLength, soundLength;
	protected float previousPitch, pitch;
	private HintEditBox searchBar;

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

		ITextComponent searchText = Utils.localize("gui.securitycraft:alarm.search");

		children.add(soundList = new SoundScrollList(minecraft, imageWidth - 10, imageHeight - 105, topPos + 40, leftPos + 5));
		searchBar = addButton(new HintEditBox(font, leftPos + 30, topPos + 20, imageWidth - 60, 15, searchText));
		searchBar.setHint(searchText);
		searchBar.setFilter(s -> s.matches("[a-zA-Z0-9\\._]*"));
		searchBar.setResponder(soundList::updateFilteredEntries);
		addButton(new ExtendedButton(leftPos + imageWidth / 2 - 170 / 2, topPos + 215, 170, 20, Utils.localize("menu.options"), b -> Minecraft.getInstance().pushGuiLayer(new AlarmOptionsScreen(this))));
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
	}

	public void selectSound(ResourceLocation eventId) {
		this.selectedSoundEvent = eventId;
		selectedSoundEventText = Utils.localize(toLanguageKey(selectedSoundEvent));
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
			SecurityCraft.channel.sendToServer(new SyncAlarmSettings(be.getBlockPos(), selectedSoundEvent, pitch, soundLength));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!searchBar.isFocused() && minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private String toLanguageKey(ResourceLocation resourceLocation) {
		return resourceLocation.getNamespace() + "." + resourceLocation.getPath();
	}

	public class SoundScrollList extends ScrollPanel {
		private static final int SLOTH_HEIGHT = 12, TEXT_OFFSET = 11;
		public final List<ResourceLocation> allSoundEvents = minecraft.getSoundManager().getAvailableSounds().stream().sorted(Comparator.comparing(rl -> toLanguageKey(rl))).collect(Collectors.toList());
		private final Map<ResourceLocation, ITextComponent> soundEventKeys = new HashMap<>();
		private List<ResourceLocation> filteredSoundEvents;
		private ISound playingSound;
		private int selectedSoundIndex, contentHeight = 0;
		private String previousSearchText = "";

		public SoundScrollList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			updateFilteredEntries("");
			scrollDistance = selectedSoundIndex * SLOTH_HEIGHT;

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
			int slotIndex = (int) (mouseY + (border / 2)) / SLOTH_HEIGHT;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size()) {
				Minecraft mc = Minecraft.getInstance();
				double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

				if (relativeMouseY < top || relativeMouseY > bottom)
					return false;
				else if (mouseX >= 0 && mouseX <= TEXT_OFFSET - 2)
					playSound(filteredSoundEvents.get(slotIndex));
				else if (hasSmartModule && mouseX > TEXT_OFFSET - 2 && mouseX <= right - 6 && slotIndex != selectedSoundIndex) {
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
			int slotIndex = mouseListY / SLOTH_HEIGHT;

			if (slotIndex >= 0 && slotIndex < filteredSoundEvents.size() && mouseX >= left && mouseX < right - 6 && mouseListY >= 0 && mouseY >= top && mouseY <= bottom) {
				ITextComponent soundEventKey = getSoundEventComponent(filteredSoundEvents.get(slotIndex));
				int length = font.width(soundEventKey);

				if (length >= width - 6 - TEXT_OFFSET)
					renderTooltip(pose, soundEventKey, left + TEXT_OFFSET - 12, baseY + (SLOTH_HEIGHT * slotIndex + SLOTH_HEIGHT));
			}
		}

		@Override
		protected void drawPanel(MatrixStack pose, int entryRight, int baseY, Tessellator tesselator, int mouseX, int mouseY) {
			FontRenderer font = minecraft.font;
			int slotBuffer = SLOTH_HEIGHT - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / SLOTH_HEIGHT;
			int min = left + TEXT_OFFSET - 2;

			//highlight hovered slot
			if (hasSmartModule && slotIndex != selectedSoundIndex && mouseX >= min && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < filteredSoundEvents.size() && mouseY >= top && mouseY <= bottom)
				renderHighlightBox(entryRight, tesselator, baseY, slotBuffer, slotIndex, min);

			//highlight slot of the currently selected sound
			if (selectedSoundIndex >= 0)
				renderHighlightBox(entryRight, tesselator, baseY, slotBuffer, selectedSoundIndex, min);

			//draw entry strings and sound icons
			for (int i = 0; i < filteredSoundEvents.size(); i++) {
				int yStart = baseY + (SLOTH_HEIGHT * i);

				if (yStart + SLOTH_HEIGHT < top)
					continue;
				else if (yStart > top + height)
					break;

				ResourceLocation soundEvent = filteredSoundEvents.get(i);
				ITextComponent name = getSoundEventComponent(soundEvent);

				font.draw(pose, name, left + TEXT_OFFSET, yStart, 0xC6C6C6);
				minecraft.textureManager.bind(GUI_TEXTURE);
				blit(pose, left, yStart - 1, getBlitOffset(), i == slotIndex && mouseX >= left && mouseX < min && mouseY >= top && mouseY <= bottom ? 9 : 0, 246, 10, 10, 256, 256);
			}
		}

		private ITextComponent getSoundEventComponent(ResourceLocation soundEvent) {
			return soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(toLanguageKey(soundEvent)));
		}

		private void renderHighlightBox(int entryRight, Tessellator tesselator, int baseY, int slotBuffer, int slotIndex, int min) {
			int max = entryRight - 6;
			int slotTop = baseY + slotIndex * SLOTH_HEIGHT;
			BufferBuilder bufferBuilder = tesselator.getBuilder();

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

		public void selectSound(int slotIndex) {
			selectedSoundIndex = slotIndex;
			AlarmScreen.this.selectSound(filteredSoundEvents.get(slotIndex));
		}

		public void playSound(ResourceLocation soundEvent) {
			SoundHandler soundManager = Minecraft.getInstance().getSoundManager();

			if (playingSound != null)
				soundManager.stop(playingSound);

			playingSound = SimpleSound.forUI(new SoundEvent(soundEvent), pitch, 1.0F);
			soundManager.play(playingSound);
		}

		public void updateFilteredEntries(String searchText) {
			//@formatter:off
			filteredSoundEvents = new ArrayList<>(allSoundEvents
					.stream()
					.filter(e -> toLanguageKey(e).contains(searchText))
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
			selectedSoundIndex = Iterables.indexOf(filteredSoundEvents, se -> se.equals(selectedSoundEvent));
		}
	}
}
