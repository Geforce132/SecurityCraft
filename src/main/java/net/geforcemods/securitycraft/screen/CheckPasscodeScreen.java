package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class CheckPasscodeScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/check_passcode.png");
	private static final TextComponent COOLDOWN_TEXT_1 = new TranslationTextComponent("gui.securitycraft:passcode.cooldown1");
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

	public CheckPasscodeScreen(TileEntity be, ITextComponent title) {
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

		addButton(new CallbackCheckbox(width / 2 - 37, height / 2 - 55, 12, 12, new TranslationTextComponent("gui.securitycraft:passcode.showPasscode"), false, newState -> keycodeTextbox.setCensoring(!newState), 0x404040));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 - 35, 20, 20, new StringTextComponent("1"), b -> addNumberToString(1)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 - 35, 20, 20, new StringTextComponent("2"), b -> addNumberToString(2)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 - 35, 20, 20, new StringTextComponent("3"), b -> addNumberToString(3)));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 - 10, 20, 20, new StringTextComponent("4"), b -> addNumberToString(4)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 - 10, 20, 20, new StringTextComponent("5"), b -> addNumberToString(5)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 - 10, 20, 20, new StringTextComponent("6"), b -> addNumberToString(6)));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 + 15, 20, 20, new StringTextComponent("7"), b -> addNumberToString(7)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 + 15, 20, 20, new StringTextComponent("8"), b -> addNumberToString(8)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 + 15, 20, 20, new StringTextComponent("9"), b -> addNumberToString(9)));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 + 40, 20, 20, new StringTextComponent("←"), b -> removeLastCharacter()));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 + 40, 20, 20, new StringTextComponent("0"), b -> addNumberToString(0)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 + 40, 20, 20, new StringTextComponent("✔"), b -> checkCode(keycodeTextbox.getValue())));

		keycodeTextbox = addButton(new CensoringEditBox(font, width / 2 - 37, height / 2 - 72, 77, 12, StringTextComponent.EMPTY) {
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
		keycodeTextbox.setFilter(s -> s.matches("\\d*\\**")); //allow any amount of numbers and any amount of asterisks

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
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);

		if (be.isOnCooldown()) {
			long cooldownEnd = be.getCooldownEnd();
			long secondsLeft = Math.max(cooldownEnd - System.currentTimeMillis(), 0) / 1000 + 1; //+1 so that the text doesn't say "0 seconds left" for a whole second
			TextComponent text = new TranslationTextComponent("gui.securitycraft:passcode.cooldown2", secondsLeft);

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
			if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode)))
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
	public void setFocused(IGuiEventListener listener) {
		if (!(listener instanceof Widget) || ((Widget) listener).isFocused())
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
			if (listener instanceof Widget)
				((Widget) listener).active = setActive;
		});
		keycodeTextbox.setFocus(setActive);
	}

	public void checkCode(String code) {
		BlockPos pos = ((TileEntity) be).getBlockPos();

		if (be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.SMART))
			toggleChildrenActive(false);

		keycodeTextbox.setValue("");
		SecurityCraft.channel.sendToServer(new CheckPasscode(pos.getX(), pos.getY(), pos.getZ(), code));
	}

	public static class CensoringEditBox extends TextFieldWidget {
		private String renderedText = "";
		private boolean shouldCensor = true;

		public CensoringEditBox(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
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
		public void renderButton(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
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
