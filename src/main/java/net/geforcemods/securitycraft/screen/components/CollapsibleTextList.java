package net.geforcemods.securitycraft.screen.components;

import java.util.List;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class CollapsibleTextList extends Button implements IGuiEventListener {
	private static final TextComponent PLUS = new StringTextComponent("+ ");
	private static final TextComponent MINUS = new StringTextComponent("- ");
	private final int threeDotsWidth = Minecraft.getInstance().font.width("...");
	private final int heightOpen;
	private final int textCutoff;
	private final ITextComponent originalDisplayString;
	private final List<? extends ITextComponent> textLines;
	private final List<Long> splitTextLineCount;
	private final BiPredicate<Integer, Integer> extraHoverCheck;
	private boolean open = true;
	private boolean isMessageTooLong = false;
	private int initialY = -1;

	public CollapsibleTextList(int xPos, int yPos, int width, ITextComponent displayString, List<? extends ITextComponent> textLines, IPressable onPress, BiPredicate<Integer, Integer> extraHoverCheck) {
		super(xPos, yPos, width, 12, displayString, onPress);
		originalDisplayString = displayString;
		switchOpenStatus(); //properly sets the message as well
		textCutoff = width - 5;

		ImmutableList.Builder<Long> splitTextLineCountBuilder = new ImmutableList.Builder<>();
		FontRenderer font = Minecraft.getInstance().font;
		int amountOfLines = 0;

		for (ITextComponent line : textLines) {
			long count = font.getSplitter().splitLines(line, textCutoff, line.getStyle()).size();

			amountOfLines += count;
			splitTextLineCountBuilder.add(count);
		}

		this.textLines = textLines;
		splitTextLineCount = splitTextLineCountBuilder.build();
		heightOpen = height + amountOfLines * font.lineHeight + textLines.size() * 3;
		this.extraHoverCheck = extraHoverCheck;
	}

	@Override
	public void renderButton(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
		isHovered &= extraHoverCheck.test(mouseX, mouseY);

		FontRenderer font = Minecraft.getInstance().font;
		int v = getYImage(isHovered());
		int heightOffset = (height - 8) / 2;

		GuiUtils.drawContinuousTexturedBox(pose, WIDGETS_LOCATION, x, y, 0, 46 + v * 20, width, height, 200, 20, 2, 3, 2, 2, getBlitOffset());
		drawCenteredString(pose, font, getMessage(), x + font.width(getMessage()) / 2 + 3, y + heightOffset, getFGColor());

		if (open) {
			int renderedLines = 0;
			Matrix4f m4f = pose.last().pose();

			GuiUtils.drawGradientRect(m4f, 0, x, y + height, x + width, y + heightOpen, 0xC0101010, 0xD0101010);

			for (int i = 0; i < textLines.size(); i++) {
				int textY = y + 2 + height + renderedLines * font.lineHeight + (i * 12);

				if (i > 0)
					GuiUtils.drawGradientRect(m4f, getBlitOffset(), x + 1, textY - 3, x + width - 2, textY - 2, 0xAAA0A0A0, 0xAAA0A0A0);

				font.drawWordWrap(textLines.get(i), x + 2, textY, textCutoff, getFGColor());
				renderedLines += splitTextLineCount.get(i) - 1;
			}
		}
	}

	public void renderLongMessageTooltip(MatrixStack pose) {
		if (isMessageTooLong && isHovered()) {
			Screen currentScreen = Minecraft.getInstance().screen;

			if (currentScreen != null)
				currentScreen.renderTooltip(pose, originalDisplayString, x + 1, y + height + 2);
		}
	}

	@Override
	public void setMessage(ITextComponent message) {
		FontRenderer font = Minecraft.getInstance().font;
		int stringWidth = font.width(message);
		int cutoff = width - 6;

		if (stringWidth > cutoff && stringWidth > threeDotsWidth) {
			isMessageTooLong = true;
			message = new StringTextComponent(font.substrByWidth(message, cutoff - threeDotsWidth).getString() + "...");
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
