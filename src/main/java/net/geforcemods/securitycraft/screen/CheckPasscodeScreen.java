package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.minecraft.client.gui.Font;
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

public class CheckPasscodeScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/check_passcode.png");
	private static final Component COOLDOWN_TEXT_1 = new TranslatableComponent("gui.securitycraft:passcode.cooldown1");
	private int cooldownText1XPos;
	private IPasscodeProtected be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private int imageWidth = 176;
	private int imageHeight = 186;
	private int leftPos;
	private int topPos;
	private CensoringEditBox keycodeTextbox;
	private boolean wasOnCooldownLastRenderTick = false;

	public CheckPasscodeScreen(BlockEntity be, Component title) {
		super(title);
		this.be = (IPasscodeProtected) be;
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		cooldownText1XPos = width / 2 - font.width(COOLDOWN_TEXT_1) / 2;
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		addRenderableWidget(new CallbackCheckbox(width / 2 - 37, height / 2 - 55, 12, 12, new TranslatableComponent("gui.securitycraft:passcode.showPasscode"), false, newState -> keycodeTextbox.setCensoring(!newState), 0x404040));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 - 35, 20, 20, new TextComponent("1"), b -> addNumberToString(1)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 - 35, 20, 20, new TextComponent("2"), b -> addNumberToString(2)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 - 35, 20, 20, new TextComponent("3"), b -> addNumberToString(3)));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 - 10, 20, 20, new TextComponent("4"), b -> addNumberToString(4)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 - 10, 20, 20, new TextComponent("5"), b -> addNumberToString(5)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 - 10, 20, 20, new TextComponent("6"), b -> addNumberToString(6)));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 + 15, 20, 20, new TextComponent("7"), b -> addNumberToString(7)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 + 15, 20, 20, new TextComponent("8"), b -> addNumberToString(8)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 + 15, 20, 20, new TextComponent("9"), b -> addNumberToString(9)));
		addRenderableWidget(new ExtendedButton(width / 2 - 33, height / 2 + 40, 20, 20, new TextComponent("←"), b -> removeLastCharacter()));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 + 40, 20, 20, new TextComponent("0"), b -> addNumberToString(0)));
		addRenderableWidget(new ExtendedButton(width / 2 + 17, height / 2 + 40, 20, 20, new TextComponent("✔"), b -> checkCode(keycodeTextbox.getValue())));

		keycodeTextbox = addRenderableWidget(new CensoringEditBox(font, width / 2 - 37, height / 2 - 72, 77, 12, TextComponent.EMPTY) {
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				return active && super.mouseClicked(mouseX, mouseY, button);
			}

			@Override
			public boolean canConsumeInput() {
				return active && isVisible();
			}
		});
		keycodeTextbox.setMaxLength(Integer.MAX_VALUE);
		keycodeTextbox.setFilter(s -> s.matches("\\d*\\**")); //allow any amount of digits and any amount of asterisks

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
			Component text = new TranslatableComponent("gui.securitycraft:passcode.cooldown2", secondsLeft);

			font.draw(pose, COOLDOWN_TEXT_1, cooldownText1XPos, height / 2 + 65, 4210752);
			font.draw(pose, text, width / 2 - font.width(text) / 2, height / 2 + 75, 4210752);

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
		if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !keycodeTextbox.getValue().isEmpty())
			minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);

		if (!super.keyPressed(keyCode, scanCode, modifiers) && !keycodeTextbox.keyPressed(keyCode, scanCode, modifiers)) {
			if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode)))
				onClose();

			if (!be.isOnCooldown() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
				minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
				checkCode(keycodeTextbox.getValue());
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
		if (!be.isOnCooldown() && isValidChar(typedChar)) {
			keycodeTextbox.charTyped(typedChar, keyCode);
			minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
		}

		return true;
	}

	@Override
	public void setFocused(GuiEventListener listener) {
		if (!(listener instanceof AbstractWidget widget) || widget.isFocused())
			super.setFocused(listener);
	}

	private boolean isValidChar(char c) {
		for (char allowedChar : allowedChars) {
			if (c == allowedChar)
				return true;
		}

		return false;
	}

	private void addNumberToString(int number) {
		keycodeTextbox.insertText("" + number);
	}

	private void removeLastCharacter() {
		if (!keycodeTextbox.getValue().isEmpty())
			keycodeTextbox.deleteChars(-1);
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

		keycodeTextbox.setValue("");
		SecurityCraft.CHANNEL.sendToServer(new CheckPasscode(pos, code));
	}

	public static class CensoringEditBox extends EditBox {
		private String renderedText = "";
		private boolean shouldCensor = true;

		public CensoringEditBox(Font font, int x, int y, int width, int height, Component message) {
			super(font, x, y, width, height, message);
			setResponder(this::updateRenderedText);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			String originalValue = value;
			boolean success;

			value = renderedText;
			success = super.mouseClicked(mouseX, mouseY, button);
			value = originalValue;
			return success;
		}

		@Override
		public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick) {
			String originalValue = value;

			value = renderedText;
			super.renderButton(pose, mouseX, mouseY, partialTick);
			value = originalValue;
		}

		@Override
		public void setHighlightPos(int position) {
			String originalValue = value;

			updateRenderedText(originalValue);
			value = renderedText;
			super.setHighlightPos(position);
			value = originalValue;
		}

		public void setCensoring(boolean shouldCensor) {
			this.shouldCensor = shouldCensor;
			updateRenderedText(value);
		}

		private void updateRenderedText(String original) {
			if (shouldCensor) {
				String x = "";

				for (int i = 1; i <= original.length(); i++) {
					x += "*";
				}

				renderedText = x;
			}
			else
				renderedText = original;
		}
	}
}
