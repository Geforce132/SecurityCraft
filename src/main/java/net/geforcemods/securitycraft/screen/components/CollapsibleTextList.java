package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ScreenUtils;

public class CollapsibleTextList extends Button {
	private static final Component PLUS = Component.literal("+ ");
	private static final Component MINUS = Component.literal("- ");
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
		super(xPos, yPos, width, 12, displayString, onPress, s -> Component.empty());
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
	public void renderWidget(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		isHovered &= extraHoverCheck.test(mouseX, mouseY);

		Font font = Minecraft.getInstance().font;
		int v = !active ? 0 : (isHoveredOrFocused() ? 2 : 1);
		int heightOffset = (height - 8) / 2;

		ScreenUtils.blitWithBorder(pose, WIDGETS_LOCATION, getX(), getY(), 0, 46 + v * 20, width, height, 200, 20, 2, 3, 2, 2, 0);
		drawCenteredString(pose, font, getMessage(), getX() + font.width(getMessage()) / 2 + 3, getY() + heightOffset, getFGColor());

		int renderedLines = 0;
		int interpolatedHeight = (int) Mth.lerp(partialTick, previousHeight, currentHeight);

		GuiComponent.fillGradient(pose, getX(), getY() + height, getX() + width, getY() + interpolatedHeight, 0xC0101010, 0xD0101010, 0);

		for (int i = 0; i < textLines.size(); i++) {
			int textY = getY() + 2 + height + renderedLines * font.lineHeight + (i * 12);
			List<FormattedCharSequence> linesToDraw = textLines.get(i);

			if (i > 0)
				GuiComponent.fillGradient(pose, getX() + 1, textY - 3, getX() + width - 2, textY - 2, 0xAAA0A0A0, 0xAAA0A0A0, 0);

			for (int lineIndex = 0; lineIndex < linesToDraw.size(); lineIndex++) {
				int lineY = textY + lineIndex * font.lineHeight;

				//don't render text that would render outside of the current height of the text list
				if (lineY + font.lineHeight > getY() + interpolatedHeight)
					return;

				font.draw(pose, linesToDraw.get(lineIndex), getX() + 2, lineY, getFGColor());
			}

			renderedLines += linesToDraw.size() - 1;
		}
	}

	public void renderLongMessageTooltip(PoseStack pose) {
		if (isMessageTooLong && isHoveredOrFocused()) {
			Screen currentScreen = Minecraft.getInstance().screen;

			if (currentScreen != null)
				currentScreen.renderTooltip(pose, originalDisplayString, getX() + 1, getY() + height + 2);
		}
	}

	@Override
	public void setMessage(Component message) {
		Font font = Minecraft.getInstance().font;
		int stringWidth = font.width(message);
		int cutoff = width - 6;

		if (stringWidth > cutoff && stringWidth > threeDotsWidth) {
			isMessageTooLong = true;
			message = Component.literal(font.substrByWidth(message, cutoff - threeDotsWidth).getString() + "...");
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

	@Override
	public void setY(int y) {
		if (initialY == -1)
			initialY = y;

		super.setY(y);
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
