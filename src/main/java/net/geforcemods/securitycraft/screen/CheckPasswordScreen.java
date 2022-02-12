package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.GenericBEMenu;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class CheckPasswordScreen extends ContainerScreen<GenericBEMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private String blockName;
	private TextFieldWidget keycodeTextbox;
	private String currentString = "";
	private static final int MAX_CHARS = 20;

	public CheckPasswordScreen(GenericBEMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = Utils.localize(tileEntity.getBlockState().getBlock().getDescriptionId()).getColoredString();
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		addButton(new ExtendedButton(width / 2 - 38, height / 2 + 30 + 10, 80, 20, "0", b -> addNumberToString(0)));
		addButton(new ExtendedButton(width / 2 - 38, height / 2 - 60 + 10, 20, 20, "1", b -> addNumberToString(1)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 - 60 + 10, 20, 20, "2", b -> addNumberToString(2)));
		addButton(new ExtendedButton(width / 2 + 22, height / 2 - 60 + 10, 20, 20, "3", b -> addNumberToString(3)));
		addButton(new ExtendedButton(width / 2 - 38, height / 2 - 30 + 10, 20, 20, "4", b -> addNumberToString(4)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 - 30 + 10, 20, 20, "5", b -> addNumberToString(5)));
		addButton(new ExtendedButton(width / 2 + 22, height / 2 - 30 + 10, 20, 20, "6", b -> addNumberToString(6)));
		addButton(new ExtendedButton(width / 2 - 38, height / 2 + 10, 20, 20, "7", b -> addNumberToString(7)));
		addButton(new ExtendedButton(width / 2 - 8, height / 2 + 10, 20, 20, "8", b -> addNumberToString(8)));
		addButton(new ExtendedButton(width / 2 + 22, height / 2 + 10, 20, 20, "9", b -> addNumberToString(9)));
		addButton(new ExtendedButton(width / 2 + 48, height / 2 + 30 + 10, 25, 20, "<-", b -> removeLastCharacter()));

		addButton(keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 67, 77, 12, ""));
		keycodeTextbox.setMaxLength(MAX_CHARS);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*\\**")); //allow any amount of numbers and any amount of asterisks
		setInitialFocus(keycodeTextbox);
	}

	@Override
	public void onClose() {
		super.onClose();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		font.draw(blockName, imageWidth / 2 - font.width(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_BACKSPACE && currentString.length() > 0) {
			Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if (isValidChar(typedChar) && currentString.length() < MAX_CHARS) {
			Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
			currentString += typedChar;
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
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
			checkCode(currentString);
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

	public void checkCode(String code) {
		SecurityCraft.channel.sendToServer(new CheckPassword(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ(), code));
	}
}
