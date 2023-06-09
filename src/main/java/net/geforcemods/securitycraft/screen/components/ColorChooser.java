package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class ColorChooser extends Screen implements GuiEventListener, NarratableEntry {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/color_chooser.png");
	public boolean disabled = true;
	private final int xStart, yStart;
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private final Component rText = Component.literal("R");
	private final Component gText = Component.literal("G");
	private final Component bText = Component.literal("B");
	private final Component rgbHexText = Component.literal("#");
	private boolean clickedInDragRegion = false;
	private float h, s, b;
	private int colorFieldTop, colorFieldBottom, colorFieldLeft, colorFieldRight;
	private final int colorFieldSize = 75;
	private final HoverChecker colorFieldHoverChecker;
	private float selectionX, selectionY;
	private final int rgbColor;
	public ColorEditBox rBox, gBox, bBox, rgbHexBox;
	private HueSlider hueSlider;

	public ColorChooser(Component title, int xStart, int yStart, int rgbColor) {
		super(title);
		this.xStart = xStart;
		this.yStart = yStart;
		colorFieldLeft = xStart + 6;
		colorFieldTop = yStart + 6;
		colorFieldRight = colorFieldLeft + colorFieldSize;
		colorFieldBottom = colorFieldTop + colorFieldSize;
		this.rgbColor = rgbColor;
		colorFieldHoverChecker = new HoverChecker(colorFieldTop, colorFieldBottom, colorFieldLeft, colorFieldRight);
	}

	@Override
	protected void init() {
		Predicate<String> boxFilter = string -> string.isEmpty() || StringUtils.isNumeric(string);
		Function<EditBox, Consumer<String>> boxResponder = box -> string -> {
			if (!string.isEmpty()) {
				int number = Integer.parseInt(string);
				Function<EditBox, Integer> parsingFunction = editBox -> {
					if (editBox.getValue().isEmpty())
						return 0;
					else
						return Integer.parseInt(editBox.getValue());
				};

				if (number < 0)
					box.setValue("0");
				else if (number > 255)
					box.setValue("255");

				updateHSBValues(parsingFunction.apply(rBox), parsingFunction.apply(gBox), parsingFunction.apply(bBox));
				updateTextFields(box);
				onColorChange();
			}
		};
		int red = rgbColor >> 16 & 255;
		int green = rgbColor >> 8 & 255;
		int blue = rgbColor & 255;

		updateHSBValues(red, green, blue);
		extraAreas.add(new Rect2i(xStart, yStart, 144, 108));
		addRenderableWidget(hueSlider = new HueSlider(colorFieldLeft - 2, yStart + 85, 82, 20, h * 360.0D) {
			@Override
			protected void applyValue() {
				h = getValueInt() / 360.0F;
				updateTextFields(null);
				onColorChange();
			}
		});
		addRenderableWidget(rBox = new ColorEditBox(font, colorFieldRight + 13, colorFieldTop, 26, 10, rText));
		addRenderableWidget(gBox = new ColorEditBox(font, colorFieldRight + 13, colorFieldTop + 15, 26, 10, gText));
		addRenderableWidget(bBox = new ColorEditBox(font, colorFieldRight + 13, colorFieldTop + 30, 26, 10, bText));
		addRenderableWidget(rgbHexBox = new ColorEditBox(font, colorFieldRight + 13, colorFieldTop + 45, 46, 10, rgbHexText));
		rBox.setValue("" + red);
		gBox.setValue("" + green);
		bBox.setValue("" + blue);
		rgbHexBox.setValue(Integer.toHexString(rgbColor).substring(2));
		rBox.setMaxLength(3);
		gBox.setMaxLength(3);
		bBox.setMaxLength(3);
		rgbHexBox.setMaxLength(6);
		rBox.setFilter(boxFilter);
		gBox.setFilter(boxFilter);
		bBox.setFilter(boxFilter);
		rgbHexBox.setFilter(string -> string.matches("[0-9a-fA-F]*"));
		rBox.setResponder(boxResponder.apply(rBox));
		gBox.setResponder(boxResponder.apply(gBox));
		bBox.setResponder(boxResponder.apply(bBox));
		rgbHexBox.setResponder(string -> {
			if (!string.isEmpty()) {
				int hexColor = Integer.parseInt(string, 16);

				updateHSBValues(hexColor >> 16 & 255, hexColor >> 8 & 255, hexColor & 255);
				updateTextFields(rgbHexBox);
				onColorChange();
			}
		});
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (!disabled) {
			guiGraphics.blit(TEXTURE, xStart, yStart, 0, 0, 145, 109);
			ClientUtils.fillHorizontalGradient(guiGraphics, 0, colorFieldLeft, colorFieldTop, colorFieldRight + 1, colorFieldBottom + 1, 0xFFFFFFFF, ClientUtils.HSBtoRGB(h, 1.0F, 1.0F));
			guiGraphics.fillGradient(colorFieldLeft, colorFieldTop, colorFieldRight + 1, colorFieldBottom + 1, 0x00000000, 0xFF000000, 0);
			guiGraphics.blit(TEXTURE, (int) selectionX - 1, (int) selectionY - 1, colorFieldHoverChecker.checkHover(mouseX, mouseY) ? 148 : 145, 20, 3, 3); //color field indicator
			super.render(guiGraphics, mouseX, mouseY, partialTick);
			guiGraphics.drawString(font, rText, colorFieldRight + 5, colorFieldTop + 1, 0x404040, false);
			guiGraphics.drawString(font, gText, colorFieldRight + 5, colorFieldTop + 16, 0x404040, false);
			guiGraphics.drawString(font, bText, colorFieldRight + 5, colorFieldTop + 31, 0x404040, false);
			guiGraphics.drawString(font, rgbHexText, colorFieldRight + 5, colorFieldTop + 46, 0x404040, false);
		}
	}

	@Override
	public void tick() {
		if (!disabled) {
			//this is validated here and not in the edit box' responder in order to allow for an empty box while typing
			validateNotEmpty(rBox);
			validateNotEmpty(gBox);
			validateNotEmpty(bBox);

			if (rgbHexBox != null && !rgbHexBox.isFocused() && rgbHexBox.getValue().isEmpty())
				rgbHexBox.setValue("000000");
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (!disabled) {
			super.mouseDragged(mouseX, mouseY, button, dragX, dragY);

			if (button == 0 && clickedInDragRegion) {
				setSelection(mouseX, mouseY);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!disabled) {
			super.mouseClicked(mouseX, mouseY, button);
			clickedInDragRegion = colorFieldHoverChecker.checkHover(mouseX, mouseY);

			if (clickedInDragRegion)
				setSelection(mouseX, mouseY);
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (!disabled) {
			super.mouseReleased(mouseX, mouseY, button);
			clickedInDragRegion = false;
		}

		return false;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	public List<Rect2i> getGuiExtraAreas() {
		return disabled ? List.of() : extraAreas;
	}

	public int getRGBColor() {
		return ClientUtils.HSBtoRGB(h, s, b);
	}

	private void setSelection(double mouseX, double mouseY) {
		selectionX = (int) Mth.clamp(mouseX, colorFieldLeft, colorFieldRight);
		selectionY = (int) Mth.clamp(mouseY, colorFieldTop, colorFieldBottom);
		s = ((selectionX - colorFieldLeft) / colorFieldSize);
		b = 1.0F - ((selectionY - colorFieldTop) / colorFieldSize);
		updateTextFields(null);
		onColorChange();
	}

	private void updateHSBValues(int red, int green, int blue) {
		float[] hsbColor = ClientUtils.RGBtoHSB(red, green, blue);

		h = hsbColor[0];
		s = hsbColor[1];
		b = hsbColor[2];
		updateSelection();
	}

	private void updateTextFields(EditBox excluded) {
		int rgbColor = getRGBColor();
		int red = rgbColor >> 16 & 255;
		int green = rgbColor >> 8 & 255;
		int blue = rgbColor & 255;

		//setting the value directly to prevent a stack overflow due to setValue calling the responder, which in turn calls this
		trySetText(excluded, rBox, "" + red);
		trySetText(excluded, gBox, "" + green);
		trySetText(excluded, bBox, "" + blue);
		trySetText(excluded, rgbHexBox, Integer.toHexString(rgbColor).substring(2));
	}

	private void trySetText(EditBox excluded, ColorEditBox editBox, String value) {
		if (excluded != editBox) {
			editBox.value = value;
			editBox.updateCursor();
		}
	}

	private void updateSelection() {
		selectionX = s * colorFieldSize + colorFieldLeft;
		selectionY = -b * colorFieldSize + colorFieldSize + colorFieldTop;

		if (hueSlider != null)
			hueSlider.setValue(h * 360.0D);
	}

	public void onColorChange() {}

	private void validateNotEmpty(EditBox box) {
		if (box != null && !box.isFocused() && box.getValue().isEmpty())
			box.setValue("0");
	}

	class HueSlider extends ForgeSlider {
		public HueSlider(int x, int y, int width, int height, double currentValue) {
			super(x, y, width, height, Component.empty(), Component.empty(), 0.0D, 360.0D, currentValue, 1.0D, 0, false);
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			guiGraphics.blit(TEXTURE, getX() + (int) (value * (width - 8)), getY(), isHoveredOrFocused() ? 151 : 145, 0, 6, height);
		}
	}

	public class ColorEditBox extends EditBox {
		public ColorEditBox(Font font, int x, int y, int width, int height, Component message) {
			super(font, x, y, width, height, message);
		}

		public void updateCursor() {
			setCursorPosition(value.length()); //calling moveCursorToEnd also calls onValueChange, causing this method to be called again
			setHighlightPos(getCursorPosition());
		}
	}
}