package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CheckPasswordScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final int MAX_CHARS = 20;
	private static final Component COOLDOWN_TEXT_1 = new TranslatableComponent("gui.securitycraft:password.cooldown1");
	private int cooldownText1XPos;
	private IPasswordProtected be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private EditBox keycodeTextbox;
	private String currentString = "";
	private boolean wasOnCooldownLastRenderTick = false;

	public CheckPasswordScreen(BlockEntity be, Component title) {
		super(title);
		this.be = (IPasswordProtected) be;
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		cooldownText1XPos = width / 2 - font.width(COOLDOWN_TEXT_1) / 2;
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 - 45, 20, 20, new TextComponent("1"), b -> addNumberToString(1)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 - 45, 20, 20, new TextComponent("2"), b -> addNumberToString(2)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 - 45, 20, 20, new TextComponent("3"), b -> addNumberToString(3)));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 - 20, 20, 20, new TextComponent("4"), b -> addNumberToString(4)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 - 20, 20, 20, new TextComponent("5"), b -> addNumberToString(5)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 - 20, 20, 20, new TextComponent("6"), b -> addNumberToString(6)));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 + 5, 20, 20, new TextComponent("7"), b -> addNumberToString(7)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 + 5, 20, 20, new TextComponent("8"), b -> addNumberToString(8)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 + 5, 20, 20, new TextComponent("9"), b -> addNumberToString(9)));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 + 30, 20, 20, new TextComponent("←"), b -> removeLastCharacter()));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 + 30, 20, 20, new TextComponent("0"), b -> addNumberToString(0)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 + 30, 20, 20, new TextComponent("✔"), b -> checkCode(currentString)));

		addRenderableWidget(keycodeTextbox = new EditBox(font, width / 2 - 37, height / 2 - 62, 77, 12, TextComponent.EMPTY) {
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				return active && super.mouseClicked(mouseX, mouseY, button);
			}

			@Override
			public boolean canConsumeInput() {
				return active && super.canConsumeInput();
			}
		});
		keycodeTextbox.setMaxLength(MAX_CHARS);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*\\**")); //allow any amount of numbers and any amount of asterisks

		if (be.isOnCooldown())
			toggleChildrenActive(false);
		else
			setInitialFocus(keycodeTextbox);
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);

		if (be.isOnCooldown()) {
			long cooldownEnd = be.getCooldownEnd();
			long secondsLeft = Math.max(cooldownEnd - System.currentTimeMillis(), 0) / 1000 + 1; //+1 so that the text doesn't say "0 seconds left" for a whole second
			Component text = new TranslatableComponent("gui.securitycraft:password.cooldown2", secondsLeft);

			font.draw(pose, COOLDOWN_TEXT_1, cooldownText1XPos, height / 2 + 55, 4210752);
			font.draw(pose, text, width / 2 - font.width(text) / 2, height / 2 + 65, 4210752);

			if (!wasOnCooldownLastRenderTick)
				wasOnCooldownLastRenderTick = true;
		}
		else if (wasOnCooldownLastRenderTick) {
			wasOnCooldownLastRenderTick = false;
			toggleChildrenActive(true);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean isBackspace = keyCode == GLFW.GLFW_KEY_BACKSPACE;

		if (isBackspace || !super.keyPressed(keyCode, scanCode, modifiers)) {
			if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode)))
				onClose();

			if (!be.isOnCooldown()) {
				if (isBackspace && currentString.length() > 0) {
					Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
					removeLastCharacter();
				}
				else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
					Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
					checkCode(currentString);
				}
			}
		}

		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if (!be.isOnCooldown() && isValidChar(typedChar) && currentString.length() < MAX_CHARS) {
			Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
			currentString += typedChar;
			setTextboxCensoredText(keycodeTextbox, currentString);
		}
		else
			return super.charTyped(typedChar, keyCode);

		return true;
	}

	@Override
	public void setFocused(GuiEventListener listener) {
		if (!(listener instanceof AbstractWidget widget) || widget.isFocused())
			super.setFocused(listener);
	}

	private boolean isValidChar(char c) {
		for (int i = 0; i < allowedChars.length; i++) {
			if (c == allowedChars[i])
				return true;
		}

		return false;
	}

	private void addNumberToString(int number) {
		if (currentString.length() < MAX_CHARS) {
			currentString += "" + number;
			setTextboxCensoredText(keycodeTextbox, currentString);
		}
	}

	private void removeLastCharacter() {
		if (currentString.length() > 0) {
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
		}
	}

	private void setTextboxCensoredText(EditBox textField, String text) {
		String x = "";

		for (int i = 1; i <= text.length(); i++) {
			x += "*";
		}

		textField.setValue(x);
	}

	private void toggleChildrenActive(boolean setActive) {
		children().forEach(listener -> {
			if (listener instanceof AbstractWidget widget)
				widget.active = setActive;
		});
		keycodeTextbox.setFocus(setActive);
	}

	public void checkCode(String code) {
		BlockPos pos = ((BlockEntity) be).getBlockPos();

		if (be instanceof IModuleInventory moduleInv && moduleInv.isModuleEnabled(ModuleType.SMART))
			toggleChildrenActive(false);

		currentString = "";
		keycodeTextbox.setValue("");
		SecurityCraft.channel.sendToServer(new CheckPassword(pos.getX(), pos.getY(), pos.getZ(), code));
	}
}
