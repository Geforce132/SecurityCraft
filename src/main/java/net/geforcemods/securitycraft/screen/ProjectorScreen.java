package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorScreen extends ContainerScreen<ProjectorContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final String SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block").getColoredString();
	private ProjectorTileEntity tileEntity;
	private String blockName;
	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[5];
	private StringHoverChecker slotHoverChecker;
	private NamedSlider projectionWidthSlider;
	private NamedSlider projectionHeightSlider;
	private NamedSlider projectionRangeSlider;
	private NamedSlider projectionOffsetSlider;
	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = Utils.localize(tileEntity.getBlockState().getBlock().getDescriptionId()).getColoredString();
		imageHeight = 225;
	}

	@Override
	public void init() {
		super.init();

		int id = 0;
		int left = leftPos + ((imageWidth - sliderWidth) / 2);
		TogglePictureButton toggleButton;

		projectionWidthSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.width", tileEntity.getProjectionWidth()).getColoredString(), blockName, left, topPos + 47, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", "").getColoredString(), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionWidth(), false, true, null, this::sliderReleased));
		projectionWidthSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description").getColoredString());

		projectionHeightSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.height", tileEntity.getProjectionHeight()).getColoredString(), blockName, left, topPos + 68, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", "").getColoredString(), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionHeight(), false, true, null, this::sliderReleased));
		projectionHeightSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description").getColoredString());

		projectionRangeSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.range", tileEntity.getProjectionRange()).getColoredString(), blockName, left, topPos + 89, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", "").getColoredString(), "", ProjectorTileEntity.MIN_RANGE, ProjectorTileEntity.MAX_RANGE, tileEntity.getProjectionRange(), false, true, slider -> {
			//show a different number so it makes sense within the world
			if (tileEntity.isHorizontal())
				slider.setMessage(slider.dispString + Integer.toString((int) Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue) - 16));
		}, this::sliderReleased));
		projectionRangeSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description").getColoredString());

		projectionOffsetSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.offset", tileEntity.getProjectionOffset()).getColoredString(), blockName, left, topPos + 110, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", "").getColoredString(), "", ProjectorTileEntity.MIN_OFFSET, ProjectorTileEntity.MAX_OFFSET, tileEntity.getProjectionOffset(), false, true, null, this::sliderReleased));
		projectionOffsetSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description").getColoredString());

		//@formatter:off
		toggleButton = addButton(new TogglePictureButton(left, topPos + 26, 20, 20, TEXTURE, new int[] {176, 192}, new int[] {0, 0}, 2, 2, b -> {
			//@formatter:on
			tileEntity.setHorizontal(!tileEntity.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getBlockPos(), tileEntity.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(tileEntity.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new StringHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical").getColoredString(), Utils.localize("gui.securitycraft:projector.horizontal").getColoredString()));
		projectionRangeSlider.updateSlider();

		slotHoverChecker = new StringHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		renderTooltip(mouseX, mouseY);

		for (StringHoverChecker thc : hoverCheckers) {
			if (thc.checkHover(mouseX, mouseY))
				renderTooltip(thc.getName(), mouseX, mouseY);
		}

		if (slotHoverChecker.checkHover(mouseX, mouseY) && menu.te.isEmpty())
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

	public void sliderReleased(NamedSlider slider) {
		int data = 0;
		DataType dataType = DataType.INVALID;

		if (slider == projectionWidthSlider) {
			tileEntity.setProjectionWidth(data = slider.getValueInt());
			dataType = DataType.WIDTH;
		}
		else if (slider == projectionHeightSlider) {
			tileEntity.setProjectionHeight(data = slider.getValueInt());
			dataType = DataType.HEIGHT;
		}
		else if (slider == projectionRangeSlider) {
			tileEntity.setProjectionRange(data = slider.getValueInt());
			dataType = DataType.RANGE;
		}
		else if (slider == projectionOffsetSlider) {
			tileEntity.setProjectionOffset(data = slider.getValueInt());
			dataType = DataType.OFFSET;
		}

		SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getBlockPos(), data, dataType));
	}
}
