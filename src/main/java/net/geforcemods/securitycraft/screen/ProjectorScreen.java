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
import net.geforcemods.securitycraft.screen.components.NamedSlider;
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
	private TranslatableComponent blockName;
	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[5];
	private TextHoverChecker slotHoverChecker;
	private NamedSlider projectionWidthSlider;
	private NamedSlider projectionHeightSlider;
	private NamedSlider projectionRangeSlider;
	private NamedSlider projectionOffsetSlider;
	private TogglePictureButton toggleButton;
	private StateSelector stateSelector;
	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorMenu menu, Inventory inv, Component text) {
		super(menu, inv, text);
		this.be = menu.be;
		blockName = Utils.localize(be.getBlockState().getBlock().getDescriptionId());
		imageHeight = 235;
	}

	@Override
	public void init() {
		super.init();
		leftPos += 90;

		int id = 0;
		int left = leftPos + ((imageWidth - sliderWidth) / 2);

		projectionWidthSlider = addRenderableWidget(new NamedSlider(Utils.localize("gui.securitycraft:projector.width", be.getProjectionWidth()), blockName, id, left, topPos + 57, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", ""), "", ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionWidth(), false, true, null, this::sliderReleased));
		projectionWidthSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new TextHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description"));

		projectionHeightSlider = addRenderableWidget(new NamedSlider(Utils.localize("gui.securitycraft:projector.height", be.getProjectionHeight()), blockName, id, left, topPos + 78, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", ""), "", ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionHeight(), false, true, null, this::sliderReleased));
		projectionHeightSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new TextHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description"));

		projectionRangeSlider = addRenderableWidget(new NamedSlider(Utils.localize("gui.securitycraft:projector.range", be.getProjectionRange()), blockName, id, left, topPos + 99, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", ""), "", ProjectorBlockEntity.MIN_RANGE, ProjectorBlockEntity.MAX_RANGE, be.getProjectionRange(), false, true, slider -> {
			//show a different number so it makes sense within the world
			if (be.isHorizontal())
				slider.setMessage(new TextComponent("").append(slider.dispString).append(Integer.toString((int) Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue) - 16)));
		}, this::sliderReleased));
		projectionRangeSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new TextHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description"));

		projectionOffsetSlider = addRenderableWidget(new NamedSlider(Utils.localize("gui.securitycraft:projector.offset", be.getProjectionOffset()), blockName, id, left, topPos + 120, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), "", ProjectorBlockEntity.MIN_OFFSET, ProjectorBlockEntity.MAX_OFFSET, be.getProjectionOffset(), false, true, null, this::sliderReleased));
		projectionOffsetSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new TextHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description"));
		//@formatter:off
		toggleButton = addRenderableWidget(new TogglePictureButton(id, left + sliderWidth - 20, topPos + 36, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, 2, b -> {
			//@formatter:on
			be.setHorizontal(!be.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), be.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(be.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new TextHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical"), Utils.localize("gui.securitycraft:projector.horizontal")));
		projectionRangeSlider.updateSlider();

		slotHoverChecker = new TextHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);

		stateSelector = addRenderableWidget(new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 197, 0, -2.85F, -0.45F));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		renderTooltip(pose, mouseX, mouseY);

		for (TextHoverChecker thc : hoverCheckers) {
			if (thc.checkHover(mouseX, mouseY))
				renderTooltip(pose, thc.getName(), mouseX, mouseY);
		}

		if (slotHoverChecker.checkHover(mouseX, mouseY) && menu.be.isEmpty())
			renderTooltip(pose, slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, blockName, imageWidth / 2 - font.width(blockName) / 2, 6, 0x404040);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (projectionWidthSlider.dragging)
			projectionWidthSlider.mouseReleased(mouseX, mouseY, button);

		if (projectionHeightSlider.dragging)
			projectionWidthSlider.mouseReleased(mouseX, mouseY, button);

		if (projectionRangeSlider.dragging)
			projectionRangeSlider.mouseReleased(mouseX, mouseY, button);

		if (projectionOffsetSlider.dragging)
			projectionOffsetSlider.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (stateSelector != null && stateSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
			SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), stateSelector.getState()));
		}
	}

	public void sliderReleased(NamedSlider slider) {
		int data = 0;
		DataType dataType = DataType.INVALID;

		if (slider.id == projectionWidthSlider.id) {
			be.setProjectionWidth(data = slider.getValueInt());
			dataType = DataType.WIDTH;
		}
		else if (slider.id == projectionHeightSlider.id) {
			be.setProjectionHeight(data = slider.getValueInt());
			dataType = DataType.HEIGHT;
		}
		else if (slider.id == projectionRangeSlider.id) {
			be.setProjectionRange(data = slider.getValueInt());
			dataType = DataType.RANGE;
		}
		else if (slider.id == projectionOffsetSlider.id) {
			be.setProjectionOffset(data = slider.getValueInt());
			dataType = DataType.OFFSET;
		}

		SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), data, dataType));
	}
}
