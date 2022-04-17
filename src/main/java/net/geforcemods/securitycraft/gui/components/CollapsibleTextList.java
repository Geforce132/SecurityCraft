package net.geforcemods.securitycraft.gui.components;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.config.GuiUtils;

public class CollapsibleTextList extends ClickButton {
	private static final TextComponentString PLUS = new TextComponentString("+ ");
	private static final TextComponentString MINUS = new TextComponentString("- ");
	private final int threeDotsWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth("...");
	private final int heightOpen;
	private final int textCutoff;
	private final String originalDisplayString;
	private final List<String> textLines;
	private final List<Long> splitTextLineCount;
	private final BiPredicate<Integer, Integer> extraHoverCheck;
	private boolean open = true;
	private boolean isMessageTooLong = false;
	private int initialY = -1;

	public CollapsibleTextList(int id, int x, int y, int width, String buttonText, List<? extends ITextComponent> textLines, Consumer<CollapsibleTextList> onPress, BiPredicate<Integer, Integer> extraHoverCheck) {
		super(id, x, y, width, 12, buttonText, clickButton -> onPress.accept((CollapsibleTextList) clickButton));
		originalDisplayString = buttonText;
		switchOpenStatus(); //properly sets the message as well
		textCutoff = width - 5;

		ImmutableList.Builder<Long> splitTextLineCountBuilder = new ImmutableList.Builder<>();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		int amountOfLines = 0;

		for (ITextComponent line : textLines) {
			long count = font.listFormattedStringToWidth(line.getFormattedText(), textCutoff).size();

			amountOfLines += count;
			splitTextLineCountBuilder.add(count);
		}

		this.textLines = textLines.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
		splitTextLineCount = splitTextLineCountBuilder.build();
		heightOpen = height + amountOfLines * font.FONT_HEIGHT + textLines.size() * 3;
		this.extraHoverCheck = extraHoverCheck;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		hovered &= extraHoverCheck.test(mouseX, mouseY);

		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		int v = getHoverState(isMouseOver());
		int heightOffset = (height - 8) / 2;

		GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, x, y, 0, 46 + v * 20, width, height, 200, 20, 2, 3, 2, 2, zLevel);
		drawCenteredString(font, displayString, x + font.getStringWidth(displayString) / 2 + 3, y + heightOffset, packedFGColour);

		if (open) {
			int renderedLines = 0;

			GuiUtils.drawGradientRect(0, x, y + height, x + width, y + heightOpen, 0xC0101010, 0xD0101010);

			for (int i = 0; i < textLines.size(); i++) {
				int textY = y + 2 + height + renderedLines * font.FONT_HEIGHT + (i * 12);

				if (i > 0)
					GuiUtils.drawGradientRect((int) zLevel, x + 1, textY - 3, x + width - 2, textY - 2, 0xAAA0A0A0, 0xAAA0A0A0);

				font.drawSplitString(textLines.get(i), x + 2, textY, textCutoff, packedFGColour);
				renderedLines += splitTextLineCount.get(i) - 1;
			}
		}
	}

	public void renderLongMessageTooltip() {
		if (isMessageTooLong && isMouseOver()) {
			GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;

			if (currentScreen != null)
				currentScreen.drawHoveringText(originalDisplayString, x + 1, y + height + 2);
		}
	}

	public void setMessage(ITextComponent message) {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		String newDisplayString = message.getFormattedText();
		int stringWidth = font.getStringWidth(newDisplayString);
		int cutoff = width - 6;

		if (stringWidth > cutoff && stringWidth > threeDotsWidth) {
			isMessageTooLong = true;
			newDisplayString = font.trimStringToWidth(newDisplayString, cutoff - threeDotsWidth) + "...";
		}

		displayString = newDisplayString;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return open ? heightOpen : height;
	}

	@Override
	public void onClick() {
		switchOpenStatus();
		super.onClick();
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return extraHoverCheck.test(mouseX, mouseY) && super.mousePressed(mc, mouseX, mouseY);
	}

	public void setY(int y) {
		if (initialY == -1)
			initialY = y;

		this.y = y;
	}

	public void switchOpenStatus() {
		open = !open;
		setMessage((open ? MINUS : PLUS).createCopy().appendSibling(new TextComponentString(originalDisplayString)));
	}

	public boolean isOpen() {
		return open;
	}

	public int getInitialY() {
		return initialY;
	}
}
