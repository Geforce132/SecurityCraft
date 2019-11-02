package net.geforcemods.securitycraft.gui.components;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.text.TextFormatting;

/**
 * Invisible "button" which allows you to add clickable links to your GUIs.
 * Opens any link whenever you click on the button's text.
 *
 * @author Geforce
 */
public class GuiLinkedText extends GuiButton implements GuiYesNoCallback {

	private final String url;
	private int textColor = 16777120;

	public GuiLinkedText(int id, int xPos, int yPos, String link) {
		super(id, xPos, yPos, Minecraft.getMinecraft().fontRenderer.getStringWidth(link), 14, link);
		url = link;
	}

	public GuiLinkedText(int id, int xPos, int yPos, String link, String displayString) {
		super(id, xPos, yPos, Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString), 14, displayString);
		url = link;
	}

	public GuiLinkedText(int id, int xPos, int yPos, String link, String displayString, int color) {
		super(id, xPos, yPos, Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString), 14, displayString);
		url = link;
		textColor = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float f) {
		if (visible) {
			FontRenderer fontRenderer = mc.fontRenderer;
			mouseDragged(mc, mouseX, mouseY);

			drawCenteredString(fontRenderer, TextFormatting.UNDERLINE + displayString, x + width / 2, y + (height - 8) / 2, textColor);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
			if (mc.gameSettings.chatLinksPrompt)
				mc.displayGuiScreen(new GuiConfirmOpenLink(this, url, 0, false));

			return true;
		}

		return false;
	}

	@Override
	public void confirmClicked(boolean choseYes, int buttonID) {
		if(buttonID == 0)
			if(choseYes)
				ClientUtils.openURL(url);

		ClientUtils.closePlayerScreen();
	}

	@Override
	public void playPressSound(SoundHandler soundHandler) {}

}
