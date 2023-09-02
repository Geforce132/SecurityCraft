package net.geforcemods.securitycraft.screen;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends AbstractContainerScreen<ProjectorMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final TranslatableComponent SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block");
	private ProjectorBlockEntity be;
	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[6];
	private TextHoverChecker slotHoverChecker;
	private CallbackSlider projectionWidthSlider;
	private CallbackSlider projectionHeightSlider;
	private CallbackSlider projectionRangeSlider;
	private CallbackSlider projectionOffsetSlider;
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

		int id = 0;
		int left = leftPos + ((imageWidth - sliderWidth) / 2);
		TogglePictureButton toggleButton;
		CallbackCheckbox overrideCheckbox;

		projectionWidthSlider = addRenderableWidget(new CallbackSlider(left, topPos + 57, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", ""), TextComponent.EMPTY, ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionWidth(), true, this::applySliderValue));
		projectionWidthSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description"));

		projectionHeightSlider = addRenderableWidget(new CallbackSlider(left, topPos + 78, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", ""), TextComponent.EMPTY, ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionHeight(), true, this::applySliderValue));
		projectionHeightSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description"));

		projectionRangeSlider = addRenderableWidget(new CallbackSlider(left, topPos + 99, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", ""), TextComponent.EMPTY, ProjectorBlockEntity.MIN_RANGE - (be.isHorizontal() ? 16 : 0), ProjectorBlockEntity.MAX_RANGE - (be.isHorizontal() ? 16 : 0), be.getProjectionRange() - (be.isHorizontal() ? 16 : 0), true, this::applySliderValue));
		projectionRangeSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new TextHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description"));

		projectionOffsetSlider = addRenderableWidget(new CallbackSlider(left, topPos + 120, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), TextComponent.EMPTY, ProjectorBlockEntity.MIN_OFFSET, ProjectorBlockEntity.MAX_OFFSET, be.getProjectionOffset(), true, this::applySliderValue));
		projectionOffsetSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description"));
		//@formatter:off
		toggleButton = addRenderableWidget(new TogglePictureButton(left + sliderWidth - 20, topPos + 36, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, 2, slider -> {
			//@formatter:on
			boolean horizontal = !be.isHorizontal();

			be.setHorizontal(horizontal);
			projectionRangeSlider.setMinValue(projectionRangeSlider.getMinValue() - (horizontal ? 16 : -16));
			projectionRangeSlider.setMaxValue(projectionRangeSlider.getMaxValue() - (horizontal ? 16 : -16));
			projectionRangeSlider.setValue(projectionRangeSlider.getValue() - (horizontal ? 16 : -16));
			applySliderValue(projectionRangeSlider);
			SecurityCraft.CHANNEL.sendToServer(new SyncProjector(be.getBlockPos(), be.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(be.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new TextHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical"), Utils.localize("gui.securitycraft:projector.horizontal")));

		overrideCheckbox = addRenderableWidget(new CallbackCheckbox(left, topPos + 36, 20, 20, TextComponent.EMPTY, be.isOverridingBlocks(), newValue -> {
			be.setOverridingBlocks(newValue);
			SecurityCraft.CHANNEL.sendToServer(new SyncProjector(be.getBlockPos(), be.isOverridingBlocks() ? 1 : 0, DataType.OVERRIDING_BLOCKS));
		}, 0));
		hoverCheckers[id++] = new TextHoverChecker(overrideCheckbox, Arrays.asList(Utils.localize("gui.securitycraft:projector.isOverridingBlocks.yes"), Utils.localize("gui.securitycraft:projector.isOverridingBlocks.no")));

		slotHoverChecker = new TextHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);

		stateSelector = addRenderableWidget(new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 197, 0, -2.85F, -0.45F));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		renderTooltip(pose, mouseX, mouseY);

		for (TextHoverChecker thc : hoverCheckers) {
			//last check hides the tooltip when a slider is being dragged
			if (thc.checkHover(mouseX, mouseY) && (!(thc.getWidget() instanceof CallbackSlider) || !isDragging())) {
				renderTooltip(pose, thc.getName(), mouseX, mouseY);
				break;
			}
		}

		if (slotHoverChecker.checkHover(mouseX, mouseY) && menu.be.isEmpty())
			renderTooltip(pose, slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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
