package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends AbstractContainerScreen<ProjectorMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final Component SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block");
	private ProjectorBlockEntity be;
	private TextHoverChecker slotHoverChecker;
	private CallbackSlider projectionWidthSlider;
	private CallbackSlider projectionHeightSlider;
	private CallbackSlider projectionRangeSlider;
	private CallbackSlider projectionOffsetSlider;
	private TogglePictureButton toggleButton;
	private StateSelector stateSelector;
	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		this.be = menu.be;
		imageHeight = 235;
	}

	@Override
	public void init() {
		super.init();
		leftPos += 90;

		int left = leftPos + ((imageWidth - sliderWidth) / 2);

		projectionWidthSlider = addRenderableWidget(new CallbackSlider(left, topPos + 57, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", ""), Component.empty(), ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionWidth(), true, this::applySliderValue));
		projectionWidthSlider.setFGColor(14737632);
		projectionWidthSlider.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:projector.width.description")));

		projectionHeightSlider = addRenderableWidget(new CallbackSlider(left, topPos + 78, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", ""), Component.empty(), ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionHeight(), true, this::applySliderValue));
		projectionHeightSlider.setFGColor(14737632);
		projectionHeightSlider.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:projector.height.description")));

		projectionRangeSlider = addRenderableWidget(new CallbackSlider(left, topPos + 99, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", ""), Component.empty(), ProjectorBlockEntity.MIN_RANGE - (be.isHorizontal() ? 16 : 0), ProjectorBlockEntity.MAX_RANGE - (be.isHorizontal() ? 16 : 0), be.getProjectionRange() - (be.isHorizontal() ? 16 : 0), true, this::applySliderValue));
		projectionRangeSlider.setFGColor(0xE0E0E0);
		projectionRangeSlider.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:projector.range.description")));

		projectionOffsetSlider = addRenderableWidget(new CallbackSlider(left, topPos + 120, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), Component.empty(), ProjectorBlockEntity.MIN_OFFSET, ProjectorBlockEntity.MAX_OFFSET, be.getProjectionOffset(), true, this::applySliderValue));
		projectionOffsetSlider.setFGColor(14737632);
		projectionOffsetSlider.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:projector.offset.description")));
		//@formatter:off
		toggleButton = addRenderableWidget(new TogglePictureButton(left + sliderWidth - 20, topPos + 36, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, 2, button -> {
			//@formatter:on
			boolean horizontal = !be.isHorizontal();

			be.setHorizontal(horizontal);
			updateToggleButtonTooltip();
			projectionRangeSlider.setMinValue(projectionRangeSlider.getMinValue() - (horizontal ? 16 : -16));
			projectionRangeSlider.setMaxValue(projectionRangeSlider.getMaxValue() - (horizontal ? 16 : -16));
			projectionRangeSlider.setValue(projectionRangeSlider.getValue() - (horizontal ? 16 : -16));
			applySliderValue(projectionRangeSlider);
			SecurityCraft.CHANNEL.sendToServer(new SyncProjector(be.getBlockPos(), be.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(be.isHorizontal() ? 1 : 0);
		updateToggleButtonTooltip();

		slotHoverChecker = new TextHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);

		stateSelector = addRenderableWidget(new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 197, 0, -2.85F, -0.45F));
		stateSelector.init(minecraft, width, height);
	}

	private void updateToggleButtonTooltip() {
		toggleButton.setTooltip(Tooltip.create(be.isHorizontal() ? Utils.localize("gui.securitycraft:projector.horizontal") : Utils.localize("gui.securitycraft:projector.vertical")));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		renderTooltip(guiGraphics, mouseX, mouseY);

		if (slotHoverChecker.checkHover(mouseX, mouseY) && menu.be.isEmpty())
			guiGraphics.renderTooltip(font, slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040, false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		renderBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (stateSelector != null && stateSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;

		return (getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY)) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getGuiExtraAreas();
		else
			return List.of();
	}

	@Override
	public void onClose() {
		super.onClose();

		if (stateSelector.getState() != null) {
			be.setProjectedState(stateSelector.getState());
			SecurityCraft.CHANNEL.sendToServer(new SyncProjector(be.getBlockPos(), stateSelector.getState()));
		}
	}

	public void applySliderValue(CallbackSlider slider) {
		int data = 0;
		DataType dataType = DataType.INVALID;

		if (slider == projectionWidthSlider) {
			data = slider.getValueInt();
			be.setProjectionWidth(data);
			dataType = DataType.WIDTH;
		}
		else if (slider == projectionHeightSlider) {
			data = slider.getValueInt();
			be.setProjectionHeight(data);
			dataType = DataType.HEIGHT;
		}
		else if (slider == projectionRangeSlider) {
			data = slider.getValueInt();

			if (be.isHorizontal())
				data += 16;

			be.setProjectionRange(data);
			dataType = DataType.RANGE;
		}
		else if (slider == projectionOffsetSlider) {
			data = slider.getValueInt();
			be.setProjectionOffset(data);
			dataType = DataType.OFFSET;
		}

		SecurityCraft.CHANNEL.sendToServer(new SyncProjector(be.getBlockPos(), data, dataType));
	}
}
