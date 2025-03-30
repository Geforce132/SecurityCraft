package net.geforcemods.securitycraft.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CheckPasscodeScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/check_passcode.png");
	private final String cooldownText1 = new TextComponentTranslation("gui.securitycraft:passcode.cooldown1").getFormattedText();
	private int cooldownText1XPos;
	private IPasscodeProtected be;
	private char[] allowedChars = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0008', '\u001B'
	}; //0-9, backspace and escape
	private String title;
	private CensoringEditBox keycodeTextbox;
	private boolean wasOnCooldownLastRenderTick = false;

	public CheckPasscodeScreen(TileEntity tileEntity) {
		super(new GenericMenu(tileEntity));
		be = (IPasscodeProtected) tileEntity;
		title = tileEntity.getDisplayName().getFormattedText();
		ySize = 186;
	}

	@Override
	public void initGui() {
		super.initGui();
		cooldownText1XPos = xSize / 2 - fontRenderer.getStringWidth(cooldownText1) / 2;
		Keyboard.enableRepeatEvents(true);

		buttonList.add(new CallbackCheckbox(0, width / 2 - 37, height / 2 - 55, 12, 12, Utils.localize("gui.securitycraft:passcode.showPasscode").getFormattedText(), false, newState -> keycodeTextbox.setCensoring(!newState), 0x404040));
		buttonList.add(new ClickButton(1, width / 2 - 33, height / 2 - 35, 20, 20, "1", b -> addNumberToString(1)));
		buttonList.add(new ClickButton(2, width / 2 - 8, height / 2 - 35, 20, 20, "2", b -> addNumberToString(2)));
		buttonList.add(new ClickButton(3, width / 2 + 17, height / 2 - 35, 20, 20, "3", b -> addNumberToString(3)));
		buttonList.add(new ClickButton(4, width / 2 - 33, height / 2 - 10, 20, 20, "4", b -> addNumberToString(4)));
		buttonList.add(new ClickButton(5, width / 2 - 8, height / 2 - 10, 20, 20, "5", b -> addNumberToString(5)));
		buttonList.add(new ClickButton(6, width / 2 + 17, height / 2 - 10, 20, 20, "6", b -> addNumberToString(6)));
		buttonList.add(new ClickButton(7, width / 2 - 33, height / 2 + 15, 20, 20, "7", b -> addNumberToString(7)));
		buttonList.add(new ClickButton(8, width / 2 - 8, height / 2 + 15, 20, 20, "8", b -> addNumberToString(8)));
		buttonList.add(new ClickButton(9, width / 2 + 17, height / 2 + 15, 20, 20, "9", b -> addNumberToString(9)));
		buttonList.add(new ClickButton(10, width / 2 - 33, height / 2 + 40, 20, 20, "←", b -> removeLastCharacter()));
		buttonList.add(new ClickButton(11, width / 2 - 8, height / 2 + 40, 20, 20, "0", b -> addNumberToString(0)));
		buttonList.add(new ClickButton(12, width / 2 + 17, height / 2 + 40, 20, 20, "✔", b -> checkCode(keycodeTextbox.getText())));

		keycodeTextbox = new CensoringEditBox(13, fontRenderer, width / 2 - 37, height / 2 - 72, 77, 12);

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(Integer.MAX_VALUE);

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
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);

		if (be.isOnCooldown()) {
			long cooldownEnd = be.getCooldownEnd();
			long secondsLeft = Math.max(cooldownEnd - System.currentTimeMillis(), 0) / 1000 + 1; //+1 so that the text doesn't say "0 seconds left" for a whole second
			String text = new TextComponentTranslation("gui.securitycraft:passcode.cooldown2", secondsLeft).getFormattedText();

			fontRenderer.drawString(cooldownText1, cooldownText1XPos, ySize / 2 + 65, 4210752);
			fontRenderer.drawString(text, xSize / 2 - fontRenderer.getStringWidth(text) / 2, ySize / 2 + 75, 4210752);

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
			if (keyCode == Keyboard.KEY_BACK && !keycodeTextbox.getText().isEmpty())
				Minecraft.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);

			if (isValidChar(typedChar) && keycodeTextbox.textboxKeyTyped(typedChar, keyCode)) {
				Minecraft.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
				return;
			}
			else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
				Minecraft.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
				checkCode(keycodeTextbox.getText());
			}
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		keycodeTextbox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private boolean isValidChar(char c) {
		for (char allowedChar : allowedChars) {
			if (c == allowedChar)
				return true;
		}

		return c == 0;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	private void addNumberToString(int number) {
		keycodeTextbox.writeText("" + number);
	}

	private void removeLastCharacter() {
		if (!keycodeTextbox.getText().isEmpty())
			keycodeTextbox.deleteFromCursor(-1);
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

		keycodeTextbox.setText("");
		SecurityCraft.network.sendToServer(new CheckPasscode(pos.getX(), pos.getY(), pos.getZ(), code));
	}

	public static class CensoringEditBox extends GuiTextField implements GuiPageButtonList.GuiResponder {
		private String renderedText = "";
		private boolean shouldCensor = true;

		public CensoringEditBox(int id, FontRenderer font, int x, int y, int width, int height) {
			super(id, font, x, y, width, height);
			setGuiResponder(this);
		}

		@Override
		public boolean textboxKeyTyped(char typedChar, int keyCode) {
			boolean success = false;

			if (isEnabled) {
				boolean originalFocused = isFocused();
				setFocused(true);
				success = super.textboxKeyTyped(typedChar, keyCode);
				setFocused(originalFocused);
			}

			return success;
		}

		@Override
		public boolean mouseClicked(int mouseX, int mouseY, int button) {
			boolean success = false;

			if (isEnabled) {
				String originalValue = text;
				text = renderedText;
				success = super.mouseClicked(mouseX, mouseY, button);
				text = originalValue;
			}

			return success;
		}

		@Override
		public void drawTextBox() {
			String originalText = text;

			text = renderedText;
			super.drawTextBox();
			text = originalText;
		}

		@Override
		public void setSelectionPos(int position) {
			String originalText = text;

			updateRenderedText(originalText);
			text = renderedText;
			super.setSelectionPos(position);
			text = originalText;
		}

		public void setCensoring(boolean shouldCensor) {
			this.shouldCensor = shouldCensor;
			updateRenderedText(text);
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

		@Override
		public void setEntryValue(int id, String text) {
			updateRenderedText(text);
		}

		@Override
		public void setEntryValue(int id, boolean value) {}

		@Override
		public void setEntryValue(int id, float value) {}
	}
}
