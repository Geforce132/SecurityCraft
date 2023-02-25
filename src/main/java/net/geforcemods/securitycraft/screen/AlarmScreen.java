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
import net.geforcemods.securitycraft.network.server.SetAlarmSound;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.registries.ForgeRegistries;

public class AlarmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png");
	private final AlarmBlockEntity be;
	private final ResourceLocation previousSelectedSoundEvent;
	private ResourceLocation selectedSoundEvent;
	private int imageWidth = 256, imageHeight = 211, leftPos, topPos;
	private SoundScrollList soundList;

	public AlarmScreen(AlarmBlockEntity be, ResourceLocation selectedSoundEvent) {
		super(be.getDisplayName());
		this.be = be;
		previousSelectedSoundEvent = selectedSoundEvent;
		this.selectedSoundEvent = selectedSoundEvent;
	}

	@Override
	protected void init() {
		super.init();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		soundList = addRenderableWidget(new SoundScrollList(minecraft, imageWidth - 10, imageHeight - 65, topPos + 40, leftPos + 5));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, GUI_TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
	}

	public void selectSound(ResourceLocation eventId) {
		this.selectedSoundEvent = eventId;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		super.onClose();

		if (soundList.playingSound != null)
			Minecraft.getInstance().getSoundManager().stop(soundList.playingSound);

		if (selectedSoundEvent != previousSelectedSoundEvent) {
			be.setSound(selectedSoundEvent);
			SecurityCraft.channel.sendToServer(new SetAlarmSound(be.getBlockPos(), selectedSoundEvent));
		}
	}

	public class SoundScrollList extends ScrollPanel {
		public final List<SoundEvent> soundEvents = new ArrayList<>(ForgeRegistries.SOUND_EVENTS.getValues());
		private final int slotHeight = 12, listLength = soundEvents.size(), textOffset = 10;
		private final Map<SoundEvent, Component> soundEventKeys = new HashMap<>();
		private SoundInstance playingSound;
		private int selectedSoundIndex;

		public SoundScrollList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			int maxScroll = getContentHeight() - (height - border);

			this.selectedSoundIndex = Math.max(0, Iterables.indexOf(soundEvents, se -> se.getLocation().equals(selectedSoundEvent)));
			scrollDistance = selectedSoundIndex * slotHeight;

			if (scrollDistance > maxScroll)
				scrollDistance = maxScroll;
		}

		@Override
		protected int getContentHeight() {
			int height = listLength * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		protected boolean clickPanel(double mouseX, double mouseY, int button) {
			int slotIndex = (int) (mouseY + (border / 2)) / slotHeight;

			if (slotIndex >= 0 && slotIndex < listLength) {
				if (mouseX >= 0 && mouseX <= textOffset)
					playSound(soundEvents.get(slotIndex));
				else if (mouseX > textOffset && mouseX <= right - 6 && slotIndex != selectedSoundIndex) {
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

			if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
				Component soundEventKey = soundEventKeys.get(soundEvents.get(slotIndex));
				int length = font.width(soundEventKey);

				if (length >= width - 6 - textOffset)
					renderTooltip(pose, List.of(soundEventKey), Optional.empty(), left - 2, baseY + (slotHeight * slotIndex + slotHeight));
			}

			RenderSystem._setShaderTexture(0, new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm.png"));
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			Font font = Minecraft.getInstance().font;
			int baseY = top + border - (int) scrollDistance;
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;
			int min = left + textOffset - 2;

			//highlight hovered slot
			if (slotIndex != selectedSoundIndex && mouseX >= min && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				renderHighlightBox(entryRight, tesselator, baseY, slotBuffer, slotIndex, min);

			renderHighlightBox(entryRight, tesselator, baseY, slotBuffer, selectedSoundIndex, min);

			int i = 0;

			//draw entry strings and indicators whether the filter is enabled
			for (SoundEvent soundEvent : soundEvents) {
				Component name = soundEventKeys.computeIfAbsent(soundEvent, t -> Utils.localize(soundEvent.getLocation().toLanguageKey()));
				int yStart = relativeY + (slotHeight * i);

				font.draw(pose, name, left + textOffset, yStart, 0xC6C6C6);
				RenderSystem._setShaderTexture(0, GUI_TEXTURE);
				blit(pose, left, yStart - 1, getBlitOffset(), i == slotIndex && mouseX >= left && mouseX < min ? 9 : 0, 211, 10, 10, 256, 256);
				i++;
			}
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
			AlarmScreen.this.selectSound(soundEvents.get(slotIndex).getLocation());
		}

		public void playSound(SoundEvent soundEvent) {
			SoundManager soundManager = Minecraft.getInstance().getSoundManager();

			if (playingSound != null)
				soundManager.stop(playingSound);

			playingSound = SimpleSoundInstance.forUI(soundEvent, 1.0F, 1.0F);
			soundManager.play(playingSound);
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}
}
