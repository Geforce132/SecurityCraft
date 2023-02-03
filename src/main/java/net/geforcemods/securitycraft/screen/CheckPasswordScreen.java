package net.geforcemods.securitycraft.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CheckPasswordScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private static final int MAX_CHARS = 20;
	private final String cooldownText1 = new TextComponentTranslation("gui.securitycraft:password.cooldown1").getFormattedText();
	private int cooldownText1XPos;
	private IPasswordProtected be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private String blockName;
	private GuiTextField keycodeTextbox;
	private String currentString = "";
	private boolean wasOnCooldownLastRenderTick = false;

	public CheckPasswordScreen(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
		super(new GenericMenu(inventoryPlayer, tileEntity));
		be = (IPasswordProtected) tileEntity;
		blockName = Utils.localize(tileEntity.getBlockType().getTranslationKey() + ".name").getFormattedText();
	}

	@Override
	public void initGui() {
		super.initGui();
		cooldownText1XPos = xSize / 2 - fontRenderer.getStringWidth(cooldownText1) / 2;
		Keyboard.enableRepeatEvents(true);

		buttonList.add(new ClickButton(0, width / 2 - 33, height / 2 - 45, 20, 20, "1", b -> addNumberToString(1)));
		buttonList.add(new ClickButton(1, width / 2 - 8, height / 2 - 45, 20, 20, "2", b -> addNumberToString(2)));
		buttonList.add(new ClickButton(2, width / 2 + 17, height / 2 - 45, 20, 20, "3", b -> addNumberToString(3)));
		buttonList.add(new ClickButton(3, width / 2 - 33, height / 2 - 20, 20, 20, "4", b -> addNumberToString(4)));
		buttonList.add(new ClickButton(4, width / 2 - 8, height / 2 - 20, 20, 20, "5", b -> addNumberToString(5)));
		buttonList.add(new ClickButton(5, width / 2 + 17, height / 2 - 20, 20, 20, "6", b -> addNumberToString(6)));
		buttonList.add(new ClickButton(6, width / 2 - 33, height / 2 + 5, 20, 20, "7", b -> addNumberToString(7)));
		buttonList.add(new ClickButton(7, width / 2 - 8, height / 2 + 5, 20, 20, "8", b -> addNumberToString(8)));
		buttonList.add(new ClickButton(8, width / 2 + 17, height / 2 + 5, 20, 20, "9", b -> addNumberToString(9)));
		buttonList.add(new ClickButton(9, width / 2 - 33, height / 2 + 30, 20, 20, "←", b -> removeLastCharacter()));
		buttonList.add(new ClickButton(10, width / 2 - 8, height / 2 + 30, 20, 20, "0", b -> addNumberToString(0)));
		buttonList.add(new ClickButton(11, width / 2 + 17, height / 2 + 30, 20, 20, "✔", b -> checkCode(currentString)));

		keycodeTextbox = new GuiTextField(11, fontRenderer, width / 2 - 37, height / 2 - 62, 77, 12) {
			@Override
			public boolean textboxKeyTyped(char typedChar, int keyCode) {
				boolean returnValue = isEnabled && super.textboxKeyTyped(typedChar, keyCode);

				if (returnValue)
					setTextboxCensoredText(this, getText());

				return returnValue;
			}
		};

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(MAX_CHARS);

		if (be.isOnCooldown())
			toggleChildrenActive(false);
		else
			keycodeTextbox.setFocused(true);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		keycodeTextbox.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(blockName, xSize / 2 - fontRenderer.getStringWidth(blockName) / 2, 6, 4210752);

		if (be.isOnCooldown()) {
			long cooldownEnd = be.getCooldownEnd();
			long secondsLeft = Math.max(cooldownEnd - System.currentTimeMillis(), 0) / 1000 + 1; //+1 so that the text doesn't say "0 seconds left" for a whole second
			String text = new TextComponentTranslation("gui.securitycraft:password.cooldown2", secondsLeft).getFormattedText();

			fontRenderer.drawString(cooldownText1, cooldownText1XPos, ySize / 2 + 55, 4210752);
			fontRenderer.drawString(text, xSize / 2 - fontRenderer.getStringWidth(text) / 2, ySize / 2 + 65, 4210752);

			if (!wasOnCooldownLastRenderTick)
				wasOnCooldownLastRenderTick = true;
		}
		else if (wasOnCooldownLastRenderTick) {
			wasOnCooldownLastRenderTick = false;
			toggleChildrenActive(true);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!be.isOnCooldown()) {
			if (keyCode == Keyboard.KEY_BACK && currentString.length() > 0) {
				Minecraft.getMinecraft().player.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("random.click")), 0.15F, 1.0F);
				removeLastCharacter();
			}
			else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
				Minecraft.getMinecraft().player.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("random.click")), 0.15F, 1.0F);
				checkCode(currentString);
			}
			else if (isValidChar(typedChar) && currentString.length() < MAX_CHARS) {
				Minecraft.getMinecraft().player.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("random.click")), 0.15F, 1.0F);
				currentString += typedChar;
				setTextboxCensoredText(keycodeTextbox, currentString);
			}

			if (keyCode != Keyboard.KEY_ESCAPE)
				return;
		}

		super.keyTyped(typedChar, keyCode);
	}

	private boolean isValidChar(char c) {
		for (int i = 0; i < allowedChars.length; i++) {
			if (c == allowedChars[i])
				return true;
		}

		return false;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
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

	private void setTextboxCensoredText(GuiTextField textField, String text) {
		String x = "";

		for (int i = 1; i <= text.length(); i++) {
			x += "*";
		}

		textField.setText(x);
	}

	private void toggleChildrenActive(boolean setActive) {
		buttonList.forEach(button -> button.enabled = setActive);
		keycodeTextbox.isEnabled = setActive;
		keycodeTextbox.setFocused(setActive);
	}

	public void checkCode(String code) {
		BlockPos pos = ((TileEntity) be).getPos();

		if (be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.SMART))
			toggleChildrenActive(false);

		currentString = "";
		keycodeTextbox.setText("");
		SecurityCraft.network.sendToServer(new CheckPassword(pos.getX(), pos.getY(), pos.getZ(), code));
	}
}
