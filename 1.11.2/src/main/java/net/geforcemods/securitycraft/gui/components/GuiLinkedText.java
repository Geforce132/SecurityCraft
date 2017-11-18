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
		super(id, xPos, yPos, Minecraft.getMinecraft().fontRendererObj.getStringWidth(link), 14, link);
		url = link;
	}

	public GuiLinkedText(int id, int xPos, int yPos, String link, String displayString) {
		super(id, xPos, yPos, Minecraft.getMinecraft().fontRendererObj.getStringWidth(displayString), 14, displayString);
		url = link;
	}

	public GuiLinkedText(int id, int xPos, int yPos, String link, String displayString, int color) {
		super(id, xPos, yPos, Minecraft.getMinecraft().fontRendererObj.getStringWidth(displayString), 14, displayString);
		url = link;
		textColor = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			FontRenderer fontrenderer = mc.fontRendererObj;
			mouseDragged(mc, mouseX, mouseY);

			drawCenteredString(fontrenderer, TextFormatting.UNDERLINE + displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height) {
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
