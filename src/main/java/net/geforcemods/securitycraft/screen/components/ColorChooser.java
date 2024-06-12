package net.geforcemods.securitycraft.screen.components;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ColorChooser extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/color_chooser.png");
	private static final int COLOR_FIELD_SIZE = 75;
	protected boolean disabled = true;
	private final int xStart, yStart;
	private final List<Rectangle> extraAreas = new ArrayList<>();
	private boolean clickedInDragRegion = false;
	private float h, s, b;
	private int colorFieldTop, colorFieldBottom, colorFieldLeft, colorFieldRight;
	private final HoverChecker colorFieldHoverChecker;
	private float selectionX, selectionY;
	private final int rgbColor;
	private Block block;
	private FixedGuiTextField rBox, gBox, bBox, rgbHexBox;
	private HueSlider hueSlider;

	public ColorChooser(int xStart, int yStart, int rgbColor, Block block) {
		this.xStart = xStart;
		this.yStart = yStart;
		colorFieldLeft = xStart + 6;
		colorFieldTop = yStart + 6;
		colorFieldRight = colorFieldLeft + COLOR_FIELD_SIZE;
		colorFieldBottom = colorFieldTop + COLOR_FIELD_SIZE;
		this.rgbColor = rgbColor;
		this.block = block;
		colorFieldHoverChecker = new HoverChecker(colorFieldTop, colorFieldBottom, colorFieldLeft, colorFieldRight);
	}

	@Override
	public void initGui() {
		Predicate<String> boxFilter = string -> string.isEmpty() || StringUtils.isNumeric(string);
		Function<GuiTextField, GuiResponder> boxResponder = box -> new GuiResponder() {
			@Override
			public void setEntryValue(int id, boolean value) {}

			@Override
			public void setEntryValue(int id, float value) {}

			@Override
			public void setEntryValue(int id, String string) {
				if (!string.isEmpty()) {
					int number = Integer.parseInt(string);
					ToIntFunction<GuiTextField> parsingFunction = editBox -> {
						if (editBox.getText().isEmpty())
							return 0;
						else
							return Integer.parseInt(editBox.getText());
					};

					if (number < 0)
						box.setText("0");
					else if (number > 255)
						box.setText("255");

					updateHSBValues(parsingFunction.applyAsInt(rBox), parsingFunction.applyAsInt(gBox), parsingFunction.applyAsInt(bBox));
					updateTextFields(box);
					onColorChange();
				}
			}
		};
		int red = rgbColor >> 16 & 255;
		int green = rgbColor >> 8 & 255;
		int blue = rgbColor & 255;

		updateHSBValues(red, green, blue);
		extraAreas.add(new Rectangle(xStart, yStart, 144, 108));
		hueSlider = addButton(new HueSlider(Utils.getLanguageKeyDenotation(block), 0, colorFieldLeft - 2, yStart + 85, 82, 20, h * 360.0D, new net.geforcemods.securitycraft.screen.components.Slider.ISlider() {
			@Override
			public void onChangeSliderValue(Slider slider, String denotation, int id) {
				h = slider.getValueInt() / 360.0F;
				updateTextFields(null);
				onColorChange();
			}

			@Override
			public void onMouseRelease(int id) {}
		}));
		rBox = new FixedGuiTextField(1, fontRenderer, colorFieldRight + 13, colorFieldTop, 26, 10);
		gBox = new FixedGuiTextField(2, fontRenderer, colorFieldRight + 13, colorFieldTop + 15, 26, 10);
		bBox = new FixedGuiTextField(3, fontRenderer, colorFieldRight + 13, colorFieldTop + 30, 26, 10);
		rgbHexBox = new FixedGuiTextField(4, fontRenderer, colorFieldRight + 13, colorFieldTop + 45, 46, 10);
		rBox.setText("" + red);
		gBox.setText("" + green);
		bBox.setText("" + blue);
		getRgbHexBox().setText(Integer.toHexString(rgbColor).substring(2));
		rBox.setMaxStringLength(3);
		gBox.setMaxStringLength(3);
		bBox.setMaxStringLength(3);
		getRgbHexBox().setMaxStringLength(6);
		rBox.setValidator(boxFilter::test);
		gBox.setValidator(boxFilter::test);
		bBox.setValidator(boxFilter::test);
		getRgbHexBox().setValidator(string -> string.matches("[0-9a-fA-F]*"));
		rBox.setGuiResponder(boxResponder.apply(rBox));
		gBox.setGuiResponder(boxResponder.apply(gBox));
		bBox.setGuiResponder(boxResponder.apply(bBox));
		getRgbHexBox().setGuiResponder(new GuiResponder() {
			@Override
			public void setEntryValue(int id, boolean value) {}

			@Override
			public void setEntryValue(int id, float value) {}

			@Override
			public void setEntryValue(int id, String string) {
				if (!string.isEmpty()) {
					int hexColor = Integer.parseInt(string, 16);

					updateHSBValues(hexColor >> 16 & 255, hexColor >> 8 & 255, hexColor & 255);
					updateTextFields(getRgbHexBox());
					onColorChange();
				}
			}
		});
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		if (!disabled) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(TEXTURE);
			drawTexturedModalRect(xStart, yStart, 0, 0, 145, 109);
			ClientUtils.fillHorizontalGradient(0, colorFieldLeft, colorFieldTop, colorFieldRight + 1, colorFieldBottom + 1, 0xFFFFFFFF, ClientUtils.HSBtoRGB(h, 1.0F, 1.0F));
			drawGradientRect(colorFieldLeft, colorFieldTop, colorFieldRight + 1, colorFieldBottom + 1, 0x00000000, 0xFF000000);
			drawTexturedModalRect((int) selectionX - 1, (int) selectionY - 1, colorFieldHoverChecker.checkHover(mouseX, mouseY) ? 148 : 145, 20, 3, 3); //color field indicator
			super.drawScreen(mouseX, mouseY, partialTick);
			rBox.drawTextBox();
			gBox.drawTextBox();
			bBox.drawTextBox();
			getRgbHexBox().drawTextBox();
			fontRenderer.drawString("R", colorFieldRight + 5, colorFieldTop + 1, 0x404040);
			fontRenderer.drawString("G", colorFieldRight + 5, colorFieldTop + 16, 0x404040);
			fontRenderer.drawString("B", colorFieldRight + 5, colorFieldTop + 31, 0x404040);
			fontRenderer.drawString("#", colorFieldRight + 5, colorFieldTop + 46, 0x404040);
		}
	}

	@Override
	public void updateScreen() {
		if (!disabled) {
			//this is validated here and not in the edit box' responder in order to allow for an empty box while typing
			validateNotEmpty(rBox);
			validateNotEmpty(gBox);
			validateNotEmpty(bBox);

			if (getRgbHexBox() != null && !getRgbHexBox().isFocused() && getRgbHexBox().getText().isEmpty())
				getRgbHexBox().setText("000000");

			if (Mouse.isButtonDown(0) && clickedInDragRegion) {
				int mouseX = Mouse.getEventX() * width / mc.displayWidth;
				int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

				setSelection(mouseX, mouseY);
			}
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode != Keyboard.KEY_ESCAPE && (tryTypeKey(rBox, typedChar, keyCode) || tryTypeKey(gBox, typedChar, keyCode) || tryTypeKey(bBox, typedChar, keyCode) || tryTypeKey(getRgbHexBox(), typedChar, keyCode)))
			return;

		super.keyTyped(typedChar, keyCode);
	}

	private boolean tryTypeKey(GuiTextField editBox, char typedChar, int keyCode) {
		if (editBox.isFocused()) {
			editBox.textboxKeyTyped(typedChar, keyCode);
			return true;
		}

		return false;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (!disabled) {
			super.mouseClicked(mouseX, mouseY, button);
			rBox.mouseClicked(mouseX, mouseY, button);
			gBox.mouseClicked(mouseX, mouseY, button);
			bBox.mouseClicked(mouseX, mouseY, button);
			getRgbHexBox().mouseClicked(mouseX, mouseY, button);
			clickedInDragRegion = colorFieldHoverChecker.checkHover(mouseX, mouseY);

			if (clickedInDragRegion)
				setSelection(mouseX, mouseY);

			if (button == 0 && hueSlider.isMouseOver())
				hueSlider.setDragging(true);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		if (!disabled) {
			super.mouseReleased(mouseX, mouseY, button);
			clickedInDragRegion = false;

			if (button == 0 && hueSlider.isDragging())
				hueSlider.mouseReleased(Mouse.getX(), Mouse.getY());
		}
	}

	public List<Rectangle> getGuiExtraAreas() {
		return disabled ? new ArrayList<>() : extraAreas;
	}

	public int getRGBColor() {
		return ClientUtils.HSBtoRGB(h, s, b);
	}

	private void setSelection(double mouseX, double mouseY) {
		selectionX = (int) MathHelper.clamp(mouseX, colorFieldLeft, colorFieldRight);
		selectionY = (int) MathHelper.clamp(mouseY, colorFieldTop, colorFieldBottom);
		s = ((selectionX - colorFieldLeft) / COLOR_FIELD_SIZE);
		b = 1.0F - ((selectionY - colorFieldTop) / COLOR_FIELD_SIZE);
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

	private void updateTextFields(GuiTextField excluded) {
		int currentRGBColor = getRGBColor();
		int red = currentRGBColor >> 16 & 255;
		int green = currentRGBColor >> 8 & 255;
		int blue = currentRGBColor & 255;

		//setting the value directly to prevent a stack overflow due to setValue calling the responder, which in turn calls this
		trySetText(excluded, rBox, "" + red);
		trySetText(excluded, gBox, "" + green);
		trySetText(excluded, bBox, "" + blue);
		trySetText(excluded, getRgbHexBox(), Integer.toHexString(currentRGBColor).substring(2));
	}

	private void trySetText(GuiTextField excluded, FixedGuiTextField editBox, String value) {
		if (excluded != editBox) {
			editBox.text = value;
			editBox.updateCursor();
		}
	}

	private void updateSelection() {
		selectionX = s * COLOR_FIELD_SIZE + colorFieldLeft;
		selectionY = -b * COLOR_FIELD_SIZE + COLOR_FIELD_SIZE + colorFieldTop;

		if (hueSlider != null)
			hueSlider.setValue(h * 360.0D);
	}

	public void onColorChange() {}

	private void validateNotEmpty(GuiTextField box) {
		if (box != null && !box.isFocused() && box.getText().isEmpty())
			box.setText("0");
	}

	public FixedGuiTextField getRgbHexBox() {
		return rgbHexBox;
	}

	public class HueSlider extends Slider {
		public HueSlider(String denotation, int id, int x, int y, int width, int height, double d, net.geforcemods.securitycraft.screen.components.Slider.ISlider iSlider) {
			super("", denotation, id, x, y, width, height, "", 0.0D, 360.0D, d, false, iSlider);
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTick) {
			if (visible && isDragging()) {
				setSliderValue((mouseX - (x + 4)) / (float) (width - 8));
				updateSlider();
			}

			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			mc.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x + (int) (getSliderValue() * (width - 8)), y, hovered ? 151 : 145, 0, 6, height);
		}
	}

	//fixes focus when selecting boxes from last added to first added, as well as the highlight position sometimes being wrong when setting the value
	public class FixedGuiTextField extends GuiTextField {
		public FixedGuiTextField(int id, FontRenderer font, int x, int y, int width, int height) {
			super(id, font, x, y, width, height);
		}

		@Override
		public void setFocused(boolean focused) {
			if (focused) {
				rBox.setFocused(false);
				gBox.setFocused(false);
				bBox.setFocused(false);
				getRgbHexBox().setFocused(false);
			}

			super.setFocused(focused);
		}

		public void updateCursor() {
			setCursorPosition(text.length()); //calling moveCursorToEnd also calls onValueChange, causing this method to be called again
			setSelectionPos(getCursorPosition());
		}
	}
}