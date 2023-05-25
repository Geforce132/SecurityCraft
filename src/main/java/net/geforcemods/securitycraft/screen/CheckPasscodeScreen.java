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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CheckPasscodeScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final Component COOLDOWN_TEXT_1 = Component.translatable("gui.securitycraft:passcode.cooldown1");
	private int cooldownText1XPos;
	private IPasscodeProtected be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private EditBox keycodeTextbox;
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

		addRenderableWidget(new Button(width / 2 - 33, height / 2 - 45, 20, 20, Component.literal("1"), b -> addNumberToString(1), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 8, height / 2 - 45, 20, 20, Component.literal("2"), b -> addNumberToString(2), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 + 17, height / 2 - 45, 20, 20, Component.literal("3"), b -> addNumberToString(3), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 33, height / 2 - 20, 20, 20, Component.literal("4"), b -> addNumberToString(4), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 8, height / 2 - 20, 20, 20, Component.literal("5"), b -> addNumberToString(5), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 + 17, height / 2 - 20, 20, 20, Component.literal("6"), b -> addNumberToString(6), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 33, height / 2 + 5, 20, 20, Component.literal("7"), b -> addNumberToString(7), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 8, height / 2 + 5, 20, 20, Component.literal("8"), b -> addNumberToString(8), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 + 17, height / 2 + 5, 20, 20, Component.literal("9"), b -> addNumberToString(9), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 33, height / 2 + 30, 20, 20, Component.literal("←"), b -> removeLastCharacter(), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 - 8, height / 2 + 30, 20, 20, Component.literal("0"), b -> addNumberToString(0), Button.DEFAULT_NARRATION));
		addRenderableWidget(new Button(width / 2 + 17, height / 2 + 30, 20, 20, Component.literal("✔"), b -> checkCode(keycodeTextbox.getValue()), Button.DEFAULT_NARRATION));

		addRenderableWidget(keycodeTextbox = new CensoringEditBox(font, width / 2 - 37, height / 2 - 62, 77, 12, Component.empty()) {
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				return active && super.mouseClicked(mouseX, mouseY, button);
			}

			@Override
			public boolean canConsumeInput() {
				return active && super.canConsumeInput();
			}
		});
		keycodeTextbox.setMaxLength(Integer.MAX_VALUE);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*\\**")); //allow any amount of numbers and any amount of asterisks

		if (be.isOnCooldown())
			toggleChildrenActive(false);
		else
			setInitialFocus(keycodeTextbox);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);

		if (be.isOnCooldown()) {
			long cooldownEnd = be.getCooldownEnd();
			long secondsLeft = Math.max(cooldownEnd - System.currentTimeMillis(), 0) / 1000 + 1; //+1 so that the text doesn't say "0 seconds left" for a whole second
			Component text = Component.translatable("gui.securitycraft:passcode.cooldown2", secondsLeft);

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
		if (keyCode == GLFW.GLFW_KEY_BACKSPACE && keycodeTextbox.getValue().length() > 0)
			minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.0F);

		if (!super.keyPressed(keyCode, scanCode, modifiers)) {
			if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode)))
				onClose();

			if (!be.isOnCooldown()) {
				if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
					minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.0F);
					checkCode(keycodeTextbox.getValue());
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
		if (!be.isOnCooldown() && isValidChar(typedChar)) {
			super.charTyped(typedChar, keyCode);
			minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.0F);
		}

		return true;
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
		if (keycodeTextbox.getValue().length() > 0)
			keycodeTextbox.deleteChars(-1);
	}

	private void toggleChildrenActive(boolean setActive) {
		children().forEach(listener -> {
			if (listener instanceof AbstractWidget widget)
				widget.active = setActive;
		});
		keycodeTextbox.setFocused(setActive);
	}

	public void checkCode(String code) {
		BlockPos pos = ((BlockEntity) be).getBlockPos();

		if (be instanceof IModuleInventory moduleInv && moduleInv.isModuleEnabled(ModuleType.SMART))
			toggleChildrenActive(false);

		keycodeTextbox.setValue("");
		SecurityCraft.channel.sendToServer(new CheckPasscode(pos.getX(), pos.getY(), pos.getZ(), code));
	}

	public static class CensoringEditBox extends EditBox {
		private String renderedText = "";

		public CensoringEditBox(Font font, int x, int y, int width, int height, Component message) {
			super(font, x, y, width, height, message);
			setResponder(s -> renderedText = censorText(s));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			String originalValue = value;

			value = renderedText;

			boolean success = super.mouseClicked(mouseX, mouseY, button);

			value = originalValue;
			return success;
		}

		@Override
		public void renderWidget(PoseStack pose, int mouseX, int mouseY, float partialTick) {
			String originalValue = value;

			value = renderedText;
			super.renderWidget(pose, mouseX, mouseY, partialTick);
			value = originalValue;
		}

		@Override
		public void setHighlightPos(int position) {
			String originalValue = value;

			renderedText = censorText(originalValue);
			value = renderedText;
			super.setHighlightPos(position);
			value = originalValue;
		}

		private String censorText(String original) {
			String x = "";

			for (int i = 1; i <= original.length(); i++) {
				x += "*";
			}

			return x;
		}
	}
}
