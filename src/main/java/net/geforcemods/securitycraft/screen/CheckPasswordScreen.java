package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CheckPasswordScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private BlockEntity be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private Component blockName;
	private EditBox keycodeTextbox;
	private String currentString = "";
	private static final int MAX_CHARS = 20;

	public CheckPasswordScreen(BlockEntity be, Component title) {
		super(title);
		this.be = be;
		blockName = Utils.localize(be.getBlockState().getBlock().getDescriptionId());
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		addRenderableWidget(new ExtendedButton(width / 2 - 38, height / 2 - 40, 20, 20, Component.literal("1"), b -> addNumberToString(1)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 - 40, 20, 20, Component.literal("2"), b -> addNumberToString(2)));
		addRenderableWidget(new ExtendedButton(width / 2 + 22, height / 2 - 40, 20, 20, Component.literal("3"), b -> addNumberToString(3)));
		addRenderableWidget(new ExtendedButton(width / 2 - 38, height / 2 - 10, 20, 20, Component.literal("4"), b -> addNumberToString(4)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 - 10, 20, 20, Component.literal("5"), b -> addNumberToString(5)));
		addRenderableWidget(new ExtendedButton(width / 2 + 22, height / 2 - 10, 20, 20, Component.literal("6"), b -> addNumberToString(6)));
		addRenderableWidget(new ExtendedButton(width / 2 - 38, height / 2 + 20, 20, 20, Component.literal("7"), b -> addNumberToString(7)));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 + 20, 20, 20, Component.literal("8"), b -> addNumberToString(8)));
		addRenderableWidget(new ExtendedButton(width / 2 + 22, height / 2 + 20, 20, 20, Component.literal("9"), b -> addNumberToString(9)));
		addRenderableWidget(new ExtendedButton(width / 2 - 38, height / 2 + 50, 20, 20, Component.literal("←"), b -> removeLastCharacter()));
		addRenderableWidget(new ExtendedButton(width / 2 - 8, height / 2 + 50, 20, 20, Component.literal("0"), b -> addNumberToString(0)));
		addRenderableWidget(new ExtendedButton(width / 2 + 22, height / 2 + 50, 20, 20, Component.literal("✔"), b -> checkCode(currentString)));

		addRenderableWidget(keycodeTextbox = new EditBox(font, width / 2 - 37, height / 2 - 62, 77, 12, Component.empty()));
		keycodeTextbox.setMaxLength(MAX_CHARS);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*\\**")); //allow any amount of numbers and any amount of asterisks
		setInitialFocus(keycodeTextbox);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, blockName, width / 2 - font.width(blockName) / 2, topPos + 6, 4210752);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!super.keyPressed(keyCode, scanCode, modifiers)) {
			if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode)))
				onClose();
			else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && currentString.length() > 0) {
				Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.0F);
				removeLastCharacter();
			}
			else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
				Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.0F);
				checkCode(currentString);
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
		if (isValidChar(typedChar) && currentString.length() < MAX_CHARS) {
			Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.15F, 1.0F);
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

	public void checkCode(String code) {
		SecurityCraft.channel.sendToServer(new CheckPassword(be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ(), code));
	}
}
