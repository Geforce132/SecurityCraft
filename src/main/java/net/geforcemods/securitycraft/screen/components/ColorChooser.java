package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
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
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/color_chooser.png");
	public boolean disabled = false;
	private final int xStart, yStart;
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private boolean clickedInDragRegion = false;
	private float h, s, b;
	private int colorFieldTop, colorFieldBottom, colorFieldLeft, colorFieldRight;
	private final int colorFieldSize = 75;
	private HoverChecker colorFieldHoverChecker;
	private float selectionX, selectionY;

	public ColorChooser(Component title, int xStart, int yStart) {
		super(title);
		this.xStart = xStart;
		this.yStart = yStart;
		colorFieldLeft = xStart + 6;
		colorFieldTop = yStart + 6;
		colorFieldRight = colorFieldLeft + colorFieldSize;
		colorFieldBottom = colorFieldTop + colorFieldSize;
	}

	@Override
	protected void init() {
		extraAreas.add(new Rect2i(xStart, 0, 193, minecraft.getWindow().getGuiScaledHeight())); //TODO: set proper extra areas
		addRenderableWidget(new HueSlider(colorFieldLeft - 2, yStart + 85, 81, 19, h) {
			@Override
			protected void applyValue() {
				h = getValueInt() / 360.0F;
				onColorChange();
			}
		});
		colorFieldHoverChecker = new HoverChecker(colorFieldTop, colorFieldBottom, colorFieldLeft, colorFieldRight);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		if (!disabled) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem._setShaderTexture(0, TEXTURE);
			blit(pose, xStart, yStart, 0, 0, 193, 150);
			super.render(pose, mouseX, mouseY, partialTick);
			ClientUtils.fillHorizontalGradient(pose, 0, colorFieldLeft, colorFieldTop, colorFieldRight, colorFieldBottom, 0xFFFFFFFF, ClientUtils.HSBtoRGB(h, 1.0F, 1.0F));
			fillGradient(pose, colorFieldLeft, colorFieldTop, colorFieldRight, colorFieldBottom, 0x00000000, 0xFF000000, getBlitOffset());
			blit(pose, (int) selectionX - 1, (int) selectionY - 1, colorFieldHoverChecker.checkHover(mouseX, mouseY) ? 253 : 250, 38, 3, 3);
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		super.mouseDragged(mouseX, mouseY, button, dragX, dragY);

		if (!disabled && button == 0 && clickedInDragRegion) {
			setSelection(mouseX, mouseY);
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);

		if (!disabled) {
			clickedInDragRegion = colorFieldHoverChecker.checkHover(mouseX, mouseY);

			if (clickedInDragRegion)
				setSelection(mouseX, mouseY);
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);

		if (!disabled)
			clickedInDragRegion = false;

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

	public int getColor() {
		return ClientUtils.HSBtoRGB(h, s, b);
	}

	private void setSelection(double mouseX, double mouseY) {
		selectionX = (int) Mth.clamp(mouseX, colorFieldLeft, colorFieldRight);
		selectionY = (int) Mth.clamp(mouseY, colorFieldTop, colorFieldBottom);
		s = ((selectionX - colorFieldLeft) / colorFieldSize);
		b = 1.0F - ((selectionY - colorFieldTop) / colorFieldSize);
		onColorChange();
	}

	public void onColorChange() {}

	class HueSlider extends ForgeSlider {
		public HueSlider(int x, int y, int width, int height, double currentValue) {
			super(x, y, width, height, Component.empty(), Component.empty(), 0.0D, 360.0D, currentValue, 1.0D, 0, false);
		}

		@Override
		protected void renderBg(PoseStack pose, Minecraft minecraft, int mouseX, int mouseY) {}

		@Override
		public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick) {
			RenderSystem._setShaderTexture(0, TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			blit(pose, x + (int) (value * (width - 8)), y, 250, isHoveredOrFocused() ? height : 0, 6, height);
		}
	}
}