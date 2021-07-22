package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorScreen extends AbstractContainerScreen<ProjectorContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final TranslatableComponent SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block");
	private ProjectorTileEntity tileEntity;
	private TranslatableComponent blockName;

	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[5];
	private TextHoverChecker slotHoverChecker;

	private NamedSlider projectionWidthSlider;
	private NamedSlider projectionHeightSlider;
	private NamedSlider projectionRangeSlider;
	private NamedSlider projectionOffsetSlider;
	private TogglePictureButton toggleButton;

	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorContainer container, Inventory inv, Component name)
	{
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = Utils.localize(tileEntity.getBlockState().getBlock().getDescriptionId());
		imageHeight = 225;
	}

	@Override
	public void init()
	{
		super.init();
		int id = 0;
		int left = leftPos + ((imageWidth - sliderWidth) / 2);

		projectionWidthSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.width", tileEntity.getProjectionWidth()), blockName, id, left, topPos + 47, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", ""), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionWidth(), false, true, null, this::sliderReleased));
		projectionWidthSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description"));

		projectionHeightSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.height", tileEntity.getProjectionHeight()), blockName, id, left, topPos + 68, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", ""), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionHeight(), false, true, null, this::sliderReleased));
		projectionHeightSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description"));

		projectionRangeSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.range", tileEntity.getProjectionRange()), blockName, id, left, topPos + 89, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", ""), "", ProjectorTileEntity.MIN_RANGE, ProjectorTileEntity.MAX_RANGE, tileEntity.getProjectionRange(), false, true, slider -> {
			//show a different number so it makes sense within the world
			if(tileEntity.isHorizontal())
				slider.setMessage(new TextComponent("").append(slider.dispString).append(Integer.toString((int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue) - 16)));
		}, this::sliderReleased));
		projectionRangeSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description"));

		projectionOffsetSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.offset", tileEntity.getProjectionOffset()), blockName, id, left, topPos + 110, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), "", ProjectorTileEntity.MIN_OFFSET, ProjectorTileEntity.MAX_OFFSET, tileEntity.getProjectionOffset(), false, true, null, this::sliderReleased));
		projectionOffsetSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description"));

		toggleButton = addButton(new TogglePictureButton(id, left, topPos + 26, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, 2, b -> {
			tileEntity.setHorizontal(!tileEntity.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getBlockPos(), tileEntity.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(tileEntity.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new TextHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical"), Utils.localize("gui.securitycraft:projector.horizontal")));
		projectionRangeSlider.updateSlider();

		slotHoverChecker = new TextHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		renderTooltip(matrix, mouseX, mouseY);

		for(TextHoverChecker thc : hoverCheckers)
		{
			if(thc.checkHover(mouseX, mouseY))
				renderTooltip(matrix, thc.getName(), mouseX, mouseY);
		}

		if(slotHoverChecker.checkHover(mouseX, mouseY) && menu.te.isEmpty())
			renderTooltip(matrix, slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY)
	{
		font.draw(matrix, blockName, imageWidth / 2 - font.width(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(projectionWidthSlider.dragging)
			projectionWidthSlider.mouseReleased(mouseX, mouseY, button);

		if(projectionHeightSlider.dragging)
			projectionWidthSlider.mouseReleased(mouseX, mouseY, button);

		if(projectionRangeSlider.dragging)
			projectionRangeSlider.mouseReleased(mouseX, mouseY, button);

		if(projectionOffsetSlider.dragging)
			projectionOffsetSlider.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	public void sliderReleased(NamedSlider slider)
	{
		int data = 0;
		DataType dataType = DataType.INVALID;

		if(slider.id == projectionWidthSlider.id)
		{
			tileEntity.setProjectionWidth(data = slider.getValueInt());
			dataType = DataType.WIDTH;
		}
		else if(slider.id == projectionHeightSlider.id)
		{
			tileEntity.setProjectionHeight(data = slider.getValueInt());
			dataType = DataType.HEIGHT;
		}
		else if(slider.id == projectionRangeSlider.id)
		{
			tileEntity.setProjectionRange(data = slider.getValueInt());
			dataType = DataType.RANGE;
		}
		else if(slider.id == projectionOffsetSlider.id)
		{
			tileEntity.setProjectionOffset(data = slider.getValueInt());
			dataType = DataType.OFFSET;
		}

		SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getBlockPos(), data, dataType));
	}
}
