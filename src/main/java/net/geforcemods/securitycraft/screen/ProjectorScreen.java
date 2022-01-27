package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorScreen extends ContainerScreen<ProjectorMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final String SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block").getColoredString();
	private ProjectorBlockEntity be;
	private String blockName;
	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[5];
	private StringHoverChecker slotHoverChecker;
	private NamedSlider projectionWidthSlider;
	private NamedSlider projectionHeightSlider;
	private NamedSlider projectionRangeSlider;
	private NamedSlider projectionOffsetSlider;
	private StateSelector stateSelector;
	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorMenu menu, PlayerInventory inv, ITextComponent text) {
		super(menu, inv, text);
		this.be = menu.be;
		blockName = Utils.localize(be.getBlockState().getBlock().getDescriptionId()).getColoredString();
		imageHeight = 235;
	}

	@Override
	public void init() {
		super.init();
		leftPos += 90;

		int id = 0;
		int left = leftPos + ((imageWidth - sliderWidth) / 2);
		TogglePictureButton toggleButton;

		projectionWidthSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.width", be.getProjectionWidth()).getColoredString(), blockName, left, topPos + 57, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", "").getColoredString(), "", ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionWidth(), false, true, null, this::sliderReleased));
		projectionWidthSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description").getColoredString());

		projectionHeightSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.height", be.getProjectionHeight()).getColoredString(), blockName, left, topPos + 78, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", "").getColoredString(), "", ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionHeight(), false, true, null, this::sliderReleased));
		projectionHeightSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description").getColoredString());

		projectionRangeSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.range", be.getProjectionRange()).getColoredString(), blockName, left, topPos + 99, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", "").getColoredString(), "", ProjectorBlockEntity.MIN_RANGE, ProjectorBlockEntity.MAX_RANGE, be.getProjectionRange(), false, true, slider -> {
			//show a different number so it makes sense within the world
			if (be.isHorizontal())
				slider.setMessage(slider.dispString + Integer.toString((int) Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue) - 16));
		}, this::sliderReleased));
		projectionRangeSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new StringHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description").getColoredString());

		projectionOffsetSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.offset", be.getProjectionOffset()).getColoredString(), blockName, left, topPos + 120, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", "").getColoredString(), "", ProjectorBlockEntity.MIN_OFFSET, ProjectorBlockEntity.MAX_OFFSET, be.getProjectionOffset(), false, true, null, this::sliderReleased));
		projectionOffsetSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new StringHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description").getColoredString());

		//@formatter:off
		toggleButton = addButton(new TogglePictureButton(left + sliderWidth - 20, topPos + 36, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, 2, b -> {
			//@formatter:on
			be.setHorizontal(!be.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), be.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(be.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new StringHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical").getColoredString(), Utils.localize("gui.securitycraft:projector.horizontal").getColoredString()));
		projectionRangeSlider.updateSlider();

		slotHoverChecker = new StringHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);

		children.add(stateSelector = new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 197, 0, -2.85F, -0.45F));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		renderTooltip(mouseX, mouseY);

		for (StringHoverChecker thc : hoverCheckers) {
			if (thc.checkHover(mouseX, mouseY))
				renderTooltip(thc.getName(), mouseX, mouseY);
		}

		if (slotHoverChecker.checkHover(mouseX, mouseY) && menu.be.isEmpty())
			renderTooltip(slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		font.draw(blockName, imageWidth / 2 - font.width(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(leftPos, topPos, 0, 0, imageWidth, imageHeight);

		if (stateSelector != null)
			stateSelector.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (projectionWidthSlider.dragging)
			projectionWidthSlider.mouseReleased(mouseX, mouseY, button);

		if (projectionHeightSlider.dragging)
			projectionHeightSlider.mouseReleased(mouseX, mouseY, button);

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
	public List<Rectangle2d> getExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getGuiExtraAreas();
		else
			return new ArrayList<>();
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

		if (slider == projectionWidthSlider) {
			be.setProjectionWidth(data = slider.getValueInt());
			dataType = DataType.WIDTH;
		}
		else if (slider == projectionHeightSlider) {
			be.setProjectionHeight(data = slider.getValueInt());
			dataType = DataType.HEIGHT;
		}
		else if (slider == projectionRangeSlider) {
			be.setProjectionRange(data = slider.getValueInt());
			dataType = DataType.RANGE;
		}
		else if (slider == projectionOffsetSlider) {
			be.setProjectionOffset(data = slider.getValueInt());
			dataType = DataType.OFFSET;
		}

		SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), data, dataType));
	}
}
