package net.geforcemods.securitycraft.screen.components;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;

public class CollapsibleTextList extends Button {
	private static final TextComponent PLUS = new TextComponent("+ ");
	private static final TextComponent MINUS = new TextComponent("- ");
	private final int threeDotsWidth = Minecraft.getInstance().font.width("...");
	private final int heightOpen;
	private final int textCutoff;
	private final Component originalDisplayString;
	private final List<? extends Component> textLines;
	private final List<Long> splitTextLineCount;
	private final boolean shouldRenderLongMessageTooltip;
	private boolean open = true;
	private boolean isMessageTooLong = false;
	private int initialY = -1;

	public CollapsibleTextList(int xPos, int yPos, int width, Component displayString, List<? extends Component> textLines, OnPress onPress, boolean shouldRenderLongMessageTooltip) {
		super(xPos, yPos, width, 12, displayString, onPress);
		this.shouldRenderLongMessageTooltip = shouldRenderLongMessageTooltip;
		originalDisplayString = displayString;
		switchOpenStatus(); //properly sets the message as well
		textCutoff = width - 5;

		ImmutableList.Builder<Long> splitTextLineCountBuilder = new ImmutableList.Builder<>();
		Font font = Minecraft.getInstance().font;
		int amountOfLines = 0;

		for (Component line : textLines) {
			//@formatter:off
			long count = font.getSplitter().splitLines(line, textCutoff, line.getStyle()).stream()
			.map(FormattedText::getString)
			.map(TextComponent::new).count();
			//@formatter:on

			amountOfLines += count;
			splitTextLineCountBuilder.add(count);
		}

		this.textLines = textLines;
		this.splitTextLineCount = splitTextLineCountBuilder.build();
		heightOpen = height + amountOfLines * font.lineHeight + textLines.size() * 3;
	}

	@Override
	public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		Font font = Minecraft.getInstance().font;
		int v = getYImage(isHoveredOrFocused());
		int heightOffset = (height - 8) / 2;

		GuiUtils.drawContinuousTexturedBox(pose, WIDGETS_LOCATION, x, y, 0, 46 + v * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
		drawCenteredString(pose, font, getMessage(), x + font.width(getMessage()) / 2 + 3, y + heightOffset, getFGColor());

		if (open) {
			int renderedLines = 0;

			GuiUtils.drawGradientRect(pose.last().pose(), 0, x, y + height, x + width, y + heightOpen, 0xC0101010, 0xD0101010);

			for (int i = 0; i < textLines.size(); i++) {
				font.drawWordWrap(textLines.get(i), x + 2, y + 2 + height + renderedLines * font.lineHeight + (i * 12), textCutoff, getFGColor());
				renderedLines += splitTextLineCount.get(i) - 1;
			}
		}

		if (shouldRenderLongMessageTooltip)
			renderLongMessageTooltip(pose);
	}

	public void renderLongMessageTooltip(PoseStack pose) {
		if (isMessageTooLong && isHoveredOrFocused()) {
			Screen currentScreen = Minecraft.getInstance().screen;

			if (currentScreen != null)
				currentScreen.renderTooltip(pose, originalDisplayString, x + 1, y + height + 2);
		}
	}

	@Override
	public void setMessage(Component message) {
		Font font = Minecraft.getInstance().font;
		int stringWidth = font.width(message);
		int cutoff = width - 6;

		if (stringWidth > cutoff && stringWidth > threeDotsWidth) {
			isMessageTooLong = true;
			message = new TextComponent(font.substrByWidth(message, cutoff - threeDotsWidth).getString() + "...");
		}

		super.setMessage(message);
	}

	@Override
	public int getHeight() {
		return open ? heightOpen : height;
	}

	@Override
	public void onPress() {
		switchOpenStatus();
		super.onPress();
	}

	public void setY(int y) {
		if (initialY == -1)
			initialY = y;

		this.y = y;
	}

	public void switchOpenStatus() {
		open = !open;
		setMessage((open ? MINUS : PLUS).copy().append(originalDisplayString));
	}

	public boolean isOpen() {
		return open;
	}

	public int getInitialY() {
		return initialY;
	}
}
