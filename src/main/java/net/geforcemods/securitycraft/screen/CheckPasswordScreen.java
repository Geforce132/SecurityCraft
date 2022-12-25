package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
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
public class CheckPasswordScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final int MAX_CHARS = 20;
	private static final TextComponent COOLDOWN_TEXT_1 = new TranslationTextComponent("gui.securitycraft:password.cooldown1");
	private int cooldownText1XPos;
	private IPasswordProtected be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private TranslationTextComponent blockName;
	private TextFieldWidget keycodeTextbox;
	private String currentString = "";
	private boolean wasOnCooldownLastRenderTick = false;

	public CheckPasswordScreen(TileEntity te, ITextComponent title) {
		super(title);
		be = (IPasswordProtected) te;
		blockName = Utils.localize(te.getBlockState().getBlock().getDescriptionId());
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		cooldownText1XPos = width / 2 - font.width(COOLDOWN_TEXT_1) / 2;
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		addButton(new ExtendedButton(width / 2 - 33, height / 2 - 45, 20, 20, new StringTextComponent("1"), b -> addNumberToString(1)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 - 45, 20, 20, new StringTextComponent("2"), b -> addNumberToString(2)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 - 45, 20, 20, new StringTextComponent("3"), b -> addNumberToString(3)));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 - 20, 20, 20, new StringTextComponent("4"), b -> addNumberToString(4)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 - 20, 20, 20, new StringTextComponent("5"), b -> addNumberToString(5)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 - 20, 20, 20, new StringTextComponent("6"), b -> addNumberToString(6)));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 + 5, 20, 20, new StringTextComponent("7"), b -> addNumberToString(7)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 + 5, 20, 20, new StringTextComponent("8"), b -> addNumberToString(8)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 + 5, 20, 20, new StringTextComponent("9"), b -> addNumberToString(9)));
		addButton(new ExtendedButton(width / 2 - 33, height / 2 + 30, 20, 20, new StringTextComponent("←"), b -> removeLastCharacter()));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 + 30, 20, 20, new StringTextComponent("0"), b -> addNumberToString(0)));
		addButton(new ExtendedButton(width / 2 + 17, height / 2 + 30, 20, 20, new StringTextComponent("✔"), b -> checkCode(currentString)));

		addButton(keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 62, 77, 12, StringTextComponent.EMPTY) {
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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(matrix, mouseX, mouseY, partialTicks);
		font.draw(matrix, blockName, width / 2 - font.width(blockName) / 2, topPos + 6, 4210752);

		if (be.isOnCooldown()) {
			long cooldownEnd = be.getCooldownEnd();
			long secondsLeft = Math.max(cooldownEnd - System.currentTimeMillis(), 0) / 1000 + 1; //+1 so that the text doesn't say "0 seconds left" for a whole second
			TextComponent text = new TranslationTextComponent("gui.securitycraft:password.cooldown2", secondsLeft);

			font.draw(matrix, COOLDOWN_TEXT_1, cooldownText1XPos, height / 2 + 55, 4210752);
			font.draw(matrix, text, width / 2 - font.width(text) / 2, height / 2 + 65, 4210752);

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
			if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode)))
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

	private boolean isValidChar(char c) {
		for (int i = 0; i < allowedChars.length; i++) {
			if (c == allowedChars[i])
				return true;
		}

		return false;
	}

	protected void addNumberToString(int number) {
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

	private void setTextboxCensoredText(TextFieldWidget textField, String text) {
		String x = "";

		for (int i = 1; i <= text.length(); i++) {
			x += "*";
		}

		textField.setValue(x);
	}

	private void toggleChildrenActive(boolean setActive) {
		children().forEach(listener -> {
			if (listener instanceof Widget)
				((Widget) listener).active = setActive;
		});
		keycodeTextbox.setFocus(!setActive);
	}

	public void checkCode(String code) {
		BlockPos pos = ((TileEntity) be).getBlockPos();

		if (be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.SMART))
			toggleChildrenActive(false);

		currentString = "";
		keycodeTextbox.setValue("");
		SecurityCraft.channel.sendToServer(new CheckPassword(pos.getX(), pos.getY(), pos.getZ(), code));
	}
}
