package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.GuiUtils;

public class CollapsibleTextList extends Button {
	private static final TextComponent PLUS = new TextComponent("+ ");
	private static final TextComponent MINUS = new TextComponent("- ");
	private final int threeDotsWidth = Minecraft.getInstance().font.width("...");
	private final int maximumHeight;
	private int currentHeight, previousHeight;
	private final Component originalDisplayString;
	private final List<List<FormattedCharSequence>> textLines = new ArrayList<>();
	private final BiPredicate<Integer, Integer> extraHoverCheck;
	private boolean open = true;
	private boolean isMessageTooLong = false;
	private int initialY = -1;

	public CollapsibleTextList(int xPos, int yPos, int width, Component displayString, List<? extends Component> textLines, OnPress onPress, BiPredicate<Integer, Integer> extraHoverCheck) {
		super(xPos, yPos, width, 12, displayString, onPress);
		originalDisplayString = displayString;
		switchOpenStatus(); //properly sets the message as well

		Font font = Minecraft.getInstance().font;
		int amountOfLines = 0;

		for (Component line : textLines) {
			List<FormattedCharSequence> splitLines = font.split(line, width - 5);

			amountOfLines += splitLines.size();
			this.textLines.add(splitLines);
		}

		maximumHeight = height + amountOfLines * font.lineHeight + textLines.size() * 3;
		currentHeight = previousHeight = height;
		this.extraHoverCheck = extraHoverCheck;
	}

	public void tick() {
		previousHeight = currentHeight;

		if (open) {
			if (currentHeight < maximumHeight)
				currentHeight = Math.min(currentHeight + 40, maximumHeight);
		}
		else if (currentHeight > height)
			currentHeight = Math.max(height, currentHeight - 40);
	}

	@Override
	public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		isHovered &= extraHoverCheck.test(mouseX, mouseY);

		Font font = Minecraft.getInstance().font;
		int v = getYImage(isHoveredOrFocused());
		int heightOffset = (height - 8) / 2;
		Matrix4f m4f = pose.last().pose();
		int renderedLines = 0;
		int interpolatedHeight = (int) Mth.lerp(partialTick, previousHeight, currentHeight);

		GuiUtils.drawContinuousTexturedBox(pose, WIDGETS_LOCATION, x, y, 0, 46 + v * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
		drawCenteredString(pose, font, getMessage(), x + font.width(getMessage()) / 2 + 3, y + heightOffset, getFGColor());
		GuiUtils.drawGradientRect(m4f, 0, x, y + height, x + width, y + interpolatedHeight, 0xC0101010, 0xD0101010);

		for (int i = 0; i < textLines.size(); i++) {
			int textY = y + 2 + height + renderedLines * font.lineHeight + (i * 12);
			List<FormattedCharSequence> linesToDraw = textLines.get(i);

			if (i > 0)
				GuiUtils.drawGradientRect(m4f, getBlitOffset(), x + 1, textY - 3, x + width - 2, textY - 2, 0xAAA0A0A0, 0xAAA0A0A0);

			for (int lineIndex = 0; lineIndex < linesToDraw.size(); lineIndex++) {
				int lineY = textY + lineIndex * font.lineHeight;

				//don't render text that would render outside of the current height of the text list
				if (lineY + font.lineHeight > y + interpolatedHeight)
					return;

				font.draw(pose, linesToDraw.get(lineIndex), x + 2, lineY, getFGColor());
			}

			renderedLines += linesToDraw.size() - 1;
		}
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
		return currentHeight;
	}

	public int getMaximumHeight() {
		return open ? maximumHeight : height;
	}

	@Override
	public void onPress() {
		switchOpenStatus();
		super.onPress();
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return extraHoverCheck.test((int) mouseX, (int) mouseY) && super.isMouseOver(mouseX, mouseY);
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
