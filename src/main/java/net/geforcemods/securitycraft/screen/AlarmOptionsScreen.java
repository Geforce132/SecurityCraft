package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.geforcemods.securitycraft.screen.components.SmallButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AlarmOptionsScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm_options.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private final Component soundLengthText = Component.translatable("gui.securitycraft:alarm.sound_length").withStyle(ChatFormatting.UNDERLINE);
	private final AlarmScreen alarmScreen;
	private int imageWidth = 226, imageHeight = 112, leftPos, topPos;
	private Button minusMinute, minusTenSeconds, minusSecond, reset, plusSecond, plusTenSeconds, plusMinute;
	private SoundLengthEditBox soundLengthEditBox;
	private int soundLengthTextXPosition;

	public AlarmOptionsScreen(AlarmScreen alarmScreen) {
		super(Utils.localize("options.title"));
		this.alarmScreen = alarmScreen;
	}

	@Override
	protected void init() {
		super.init();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		int buttonHeight = 13;
		int buttonsX = leftPos + 5;
		int buttonY = topPos + 40;
		int timeEditBoxWidth = 34;
		int soundLengthTextWidthPlusBuffer = font.width(soundLengthText) + 5;
		int combinedTextAndBoxWidth = soundLengthTextWidthPlusBuffer + timeEditBoxWidth;

		addRenderableWidget(SmallButton.createWithX(leftPos + imageWidth - 12, topPos + 4, b -> Minecraft.getInstance().popGuiLayer()));
		soundLengthTextXPosition = width / 2 - combinedTextAndBoxWidth / 2;
		soundLengthEditBox = addRenderableWidget(new SoundLengthEditBox(font, soundLengthTextXPosition + soundLengthTextWidthPlusBuffer, buttonY - 15, timeEditBoxWidth, 12, Component.empty()));
		soundLengthEditBox.setFilter(string -> string.matches("[0-9:]*"));
		minusMinute = addRenderableWidget(new Button(buttonsX, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.minus_one_minute"), b -> changeSoundLength(-60), Button.DEFAULT_NARRATION));
		minusTenSeconds = addRenderableWidget(new Button(buttonsX + 34, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.minus_ten_seconds"), b -> changeSoundLength(-10), Button.DEFAULT_NARRATION));
		minusSecond = addRenderableWidget(new Button(buttonsX + 68, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.minus_one_second"), b -> changeSoundLength(-1), Button.DEFAULT_NARRATION));
		reset = addRenderableWidget(new ActiveBasedTextureButton(buttonsX + 102, buttonY, 12, buttonHeight, RESET_TEXTURE, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSoundLength(0)));
		plusSecond = addRenderableWidget(new Button(buttonsX + 116, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.plus_one_second"), b -> changeSoundLength(1), Button.DEFAULT_NARRATION));
		plusTenSeconds = addRenderableWidget(new Button(buttonsX + 150, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.plus_ten_seconds"), b -> changeSoundLength(10), Button.DEFAULT_NARRATION));
		plusMinute = addRenderableWidget(new Button(buttonsX + 184, buttonY, 32, buttonHeight, Component.translatable("gui.securitycraft:alarm.plus_one_minute"), b -> changeSoundLength(60), Button.DEFAULT_NARRATION));
		setSoundLength(alarmScreen.soundLength);
		addRenderableWidget(new CallbackSlider(leftPos + imageWidth / 2 - 50, buttonY + 25, 100, 20, Utils.localize("gui.securitycraft:alarm.pitch", ""), Component.empty(), 0.5D, 2.0D, alarmScreen.pitch, 0.05D, 0, true, slider -> alarmScreen.pitch = (float) slider.getValue())).setFGColor(0xE0E0E0);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem._setShaderTexture(0, GUI_TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 0x404040);
		font.draw(pose, soundLengthText, soundLengthTextXPosition, topPos + 27, 0x404040);

		if (alarmScreen.be.isPowered() && !alarmScreen.be.isDisabled()) {
			int cooldownSeconds = (alarmScreen.be.getCooldown() - 1) / 20;
			Component nextSoundText = Utils.localize("gui.securitycraft:alarm.nextSound", String.format("%02d:%02d", cooldownSeconds / 60, cooldownSeconds % 60 + 1));

			font.draw(pose, nextSoundText, width / 2 - font.width(nextSoundText) / 2, topPos + 95, 0x404040);
		}
	}

	public void setSoundLength(int newSoundLength) {
		setSoundLength(newSoundLength, true);
	}

	public void changeSoundLength(int soundLengthChange) {
		if (soundLengthChange == 0)
			setSoundLength(alarmScreen.previousSoundLength, true);
		else
			setSoundLength(alarmScreen.soundLength + soundLengthChange, true);
	}

	public void setSoundLength(int newSoundLength, boolean updateTimeEditBox) {
		boolean enablePlusButtons;
		boolean enableMinusButtons;
		int soundLength = Math.max(1, Math.min(newSoundLength, AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH));

		if (updateTimeEditBox)
			soundLengthEditBox.setValue(String.format("%02d:%02d", soundLength / 60, soundLength % 60));

		enablePlusButtons = soundLength < AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH;
		enableMinusButtons = soundLength > 1;
		minusMinute.active = enableMinusButtons;
		minusTenSeconds.active = enableMinusButtons;
		minusSecond.active = enableMinusButtons;
		reset.active = soundLength != alarmScreen.previousSoundLength;
		plusSecond.active = enablePlusButtons;
		plusTenSeconds.active = enablePlusButtons;
		plusMinute.active = enablePlusButtons;
		alarmScreen.soundLength = soundLength;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onClose() {
		soundLengthEditBox.checkAndProcessInput();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return alarmScreen.isPauseScreen();
	}

	public class SoundLengthEditBox extends EditBox {
		public SoundLengthEditBox(Font font, int x, int y, int width, int height, Component message) {
			super(font, x, y, width, height, message);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
			changeSoundLength((int) Math.signum(delta));
			return super.mouseScrolled(mouseX, mouseY, delta);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
				checkAndProcessInput();

			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void setFocused(boolean focused) {
			if (isFocused() && !focused)
				checkAndProcessInput();

			super.setFocused(focused);
		}

		public void checkAndProcessInput() {
			int minutes = 0;
			int seconds = 2;

			if (value != null && !value.isEmpty() && !value.isBlank()) {
				if (!value.contains(":")) {
					int numeric = Integer.parseInt(value);

					minutes = numeric / 60;
					seconds = numeric % 60;
				}
				else {
					String[] split = value.split(":");

					if (split.length == 2) {
						if (!split[0].isEmpty() && !split[0].isBlank()) {
							try {
								minutes = Integer.parseInt(split[0]);
							}
							catch (NumberFormatException e) { //usually happens when the entered number is too big
								minutes = 60;
							}
						}

						if (!split[1].isEmpty() && !split[1].isBlank()) {
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

			setSoundLength(minutes * 60 + seconds, true);
			moveCursorToEnd();
		}
	}
}
