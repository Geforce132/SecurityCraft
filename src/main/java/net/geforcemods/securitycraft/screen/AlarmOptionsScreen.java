package net.geforcemods.securitycraft.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.Slider;
import net.geforcemods.securitycraft.screen.components.Slider.ISlider;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;

public class AlarmOptionsScreen extends GuiScreen implements ISlider {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/alarm_options.png");
	private static final ResourceLocation RESET_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset.png");
	private static final ResourceLocation RESET_INACTIVE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/reset_inactive.png");
	private final String soundLengthText = new TextComponentTranslation("gui.securitycraft:alarm.sound_length").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private final String title = Utils.localize("options.title").getFormattedText();
	private final AlarmScreen alarmScreen;
	private int imageWidth = 226, imageHeight = 112, leftPos, topPos;
	private GuiButton minusMinute, minusTenSeconds, minusSecond, reset, plusSecond, plusTenSeconds, plusMinute;
	private SoundLengthEditBox soundLengthEditBox;
	private Slider pitchSlider;
	private int soundLengthTextXPosition;

	public AlarmOptionsScreen(AlarmScreen alarmScreen) {
		this.alarmScreen = alarmScreen;
	}

	@Override
	public void initGui() {
		super.initGui();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		updateAlarmScreenSizes();

		int buttonHeight = 13;
		int buttonsX = leftPos + 5;
		int buttonY = topPos + 40;
		int timeEditBoxWidth = 34;
		int soundLengthTextWidthPlusBuffer = fontRenderer.getStringWidth(soundLengthText) + 5;
		int combinedTextAndBoxWidth = soundLengthTextWidthPlusBuffer + timeEditBoxWidth;
		int id = 0;

		buttonList.add(new ClickButton(id++, leftPos + imageWidth - 12, topPos + 4, 8, 8, "x", b -> mc.displayGuiScreen(alarmScreen)));
		soundLengthTextXPosition = width / 2 - combinedTextAndBoxWidth / 2;
		soundLengthEditBox = new SoundLengthEditBox(id++, fontRenderer, soundLengthTextXPosition + soundLengthTextWidthPlusBuffer, buttonY - 15, timeEditBoxWidth, 12);
		soundLengthEditBox.setValidator(string -> string.matches("[0-9:]*"));
		minusMinute = addButton(new ClickButton(id++, buttonsX, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.minus_one_minute").getFormattedText(), b -> changeSoundLength(-60)));
		minusTenSeconds = addButton(new ClickButton(id++, buttonsX + 34, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.minus_ten_seconds").getFormattedText(), b -> changeSoundLength(-10)));
		minusSecond = addButton(new ClickButton(id++, buttonsX + 68, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.minus_one_second").getFormattedText(), b -> changeSoundLength(-1)));
		reset = addButton(new PictureButton(id++, buttonsX + 102, buttonY, 12, buttonHeight, RESET_INACTIVE_TEXTURE, 10, 10, 1, 2, 10, 10, 10, 10, b -> changeSoundLength(0)) {
			@Override
			public ResourceLocation getTextureLocation() {
				return enabled ? RESET_TEXTURE : RESET_INACTIVE_TEXTURE;
			}
		});
		plusSecond = addButton(new ClickButton(id++, buttonsX + 116, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.plus_one_second").getFormattedText(), b -> changeSoundLength(1)));
		plusTenSeconds = addButton(new ClickButton(id++, buttonsX + 150, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.plus_ten_seconds").getFormattedText(), b -> changeSoundLength(10)));
		plusMinute = addButton(new ClickButton(id++, buttonsX + 184, buttonY, 32, buttonHeight, new TextComponentTranslation("gui.securitycraft:alarm.plus_one_minute").getFormattedText(), b -> changeSoundLength(60)));
		setSoundLength(alarmScreen.soundLength);
		pitchSlider = addButton(new Slider(Utils.localize("gui.securitycraft:alarm.pitch", "" + alarmScreen.pitch).getFormattedText(), Utils.getLanguageKeyDenotation(SCContent.alarm), id++, leftPos + imageWidth / 2 - 50, buttonY + 25, 100, 20, Utils.localize("gui.securitycraft:alarm.pitch", "").getFormattedText(), 0.5D, 2.0D, alarmScreen.pitch, true, this));
	}

	private void updateAlarmScreenSizes() {
		int oldLeftPos = alarmScreen.leftPos, leftDifference;
		int oldTopPos = alarmScreen.topPos, topDifference;

		alarmScreen.width = width;
		alarmScreen.height = height;
		alarmScreen.leftPos = (alarmScreen.width - alarmScreen.imageWidth) / 2;
		alarmScreen.topPos = (alarmScreen.height - alarmScreen.imageHeight) / 2;
		leftDifference = alarmScreen.leftPos - oldLeftPos;
		topDifference = alarmScreen.topPos - oldTopPos;
		alarmScreen.soundList.updateSize(leftDifference, topDifference);
		alarmScreen.searchBar.x += leftDifference;
		alarmScreen.searchBar.y += topDifference;
		alarmScreen.optionsButton.x += leftDifference;
		alarmScreen.optionsButton.y += topDifference;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		alarmScreen.drawScreen(Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.drawScreen(mouseX, mouseY, partialTicks);
		soundLengthEditBox.drawTextBox();
		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, topPos + 6, 0x404040);
		fontRenderer.drawString(soundLengthText, soundLengthTextXPosition, topPos + 27, 0x404040);

		if (alarmScreen.be.isPowered() && !alarmScreen.be.isDisabled()) {
			int cooldownSeconds = (alarmScreen.be.getCooldown() - 1) / 20;
			String nextSoundText = Utils.localize("gui.securitycraft:alarm.nextSound", String.format("%02d:%02d", cooldownSeconds / 60, cooldownSeconds % 60 + 1)).getFormattedText();

			fontRenderer.drawString(nextSoundText, width / 2 - fontRenderer.getStringWidth(nextSoundText) / 2, topPos + 95, 0x404040);
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
			soundLengthEditBox.setText(String.format("%02d:%02d", soundLength / 60, soundLength % 60));

		enablePlusButtons = soundLength < AlarmBlockEntity.MAXIMUM_ALARM_SOUND_LENGTH;
		enableMinusButtons = soundLength > 1;
		minusMinute.enabled = enableMinusButtons;
		minusTenSeconds.enabled = enableMinusButtons;
		minusSecond.enabled = enableMinusButtons;
		reset.enabled = soundLength != alarmScreen.previousSoundLength;
		plusSecond.enabled = enablePlusButtons;
		plusTenSeconds.enabled = enablePlusButtons;
		plusMinute.enabled = enablePlusButtons;
		alarmScreen.soundLength = soundLength;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		soundLengthEditBox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		double mouseX = Mouse.getEventX() * width / mc.displayWidth;
		double mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		if (soundLengthEditBox.isHovered(mouseX, mouseY) && Mouse.getEventDWheel() != 0)
			soundLengthEditBox.mouseScrolled(Mouse.getEventDWheel());
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		soundLengthEditBox.textboxKeyTyped(typedChar, keyCode);

		if (keyCode == 1 || (mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)))
			mc.displayGuiScreen(alarmScreen);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return alarmScreen.doesGuiPauseGame();
	}

	@Override
	public void onGuiClosed() {
		soundLengthEditBox.checkAndProcessInput();
		super.onGuiClosed();
	}

	public class SoundLengthEditBox extends GuiTextField {
		public SoundLengthEditBox(int id, FontRenderer font, int x, int y, int width, int height) {
			super(id, font, x, y, width, height);
		}

		public void mouseScrolled(double delta) {
			changeSoundLength((int) Math.signum(delta));
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

		public void checkAndProcessInput() {
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

			setSoundLength(minutes * 60 + seconds, true);
			setCursorPositionEnd();
		}

		public boolean isHovered(double mouseX, double mouseY) {
			return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		}
	}

	@Override
	public void onChangeSliderValue(Slider slider, String denotation, int id) {
		slider.displayString = slider.getPrefix() + getTruncatedSliderValue(slider);
	}

	@Override
	public void onMouseRelease(int id) {
		alarmScreen.pitch = getTruncatedSliderValue(pitchSlider);
	}

	private float getTruncatedSliderValue(Slider slider) {
		return (float) Math.floor(slider.getValue() * 100.0F) / 100.0F;
	}
}
