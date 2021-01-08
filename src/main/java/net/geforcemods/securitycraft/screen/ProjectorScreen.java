package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorScreen extends ContainerScreen<ProjectorContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private ProjectorTileEntity tileEntity;
	private String blockName;

	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[5];

	private NamedSlider projectionWidthSlider;
	private NamedSlider projectionHeightSlider;
	private NamedSlider projectionRangeSlider;
	private NamedSlider projectionOffsetSlider;
	private TogglePictureButton toggleButton;

	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = ClientUtils.localize(tileEntity.getBlockState().getBlock().getTranslationKey());
		ySize = 225;
	}

	@Override
	public void init()
	{
		super.init();
		int id = 0;
		int left = guiLeft + ((xSize - sliderWidth) / 2);

		projectionWidthSlider = addButton(new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.width", tileEntity.getProjectionWidth()), blockName, id, left, guiTop + 47, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.width", ""), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionWidth(), false, true, null, this::sliderReleased));
		projectionWidthSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionWidthSlider, ClientUtils.localize("gui.securitycraft:projector.width.description"));

		projectionHeightSlider = addButton(new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.height", tileEntity.getProjectionHeight()), blockName, id, left, guiTop + 68, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.height", ""), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionHeight(), false, true, null, this::sliderReleased));
		projectionHeightSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionHeightSlider, ClientUtils.localize("gui.securitycraft:projector.height.description"));

		projectionRangeSlider = addButton(new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.range", tileEntity.getProjectionRange()), blockName, id, left, guiTop + 89, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.range", ""), "", ProjectorTileEntity.MIN_RANGE, ProjectorTileEntity.MAX_RANGE, tileEntity.getProjectionRange(), false, true, slider -> {
			//show a different number so it makes sense within the world
			if(tileEntity.isHorizontal())
				slider.setMessage(slider.dispString + Integer.toString((int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue) - 16));
		}, this::sliderReleased));
		projectionRangeSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionRangeSlider, ClientUtils.localize("gui.securitycraft:projector.range.description"));

		projectionOffsetSlider = addButton(new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.offset", tileEntity.getProjectionOffset()), blockName, id, left, guiTop + 110, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.offset", ""), "", ProjectorTileEntity.MIN_OFFSET, ProjectorTileEntity.MAX_OFFSET, tileEntity.getProjectionOffset(), false, true, null, this::sliderReleased));
		projectionOffsetSlider.setFGColor(14737632);
		hoverCheckers[id++] = new StringHoverChecker(projectionOffsetSlider, ClientUtils.localize("gui.securitycraft:projector.offset.description"));

		toggleButton = addButton(new TogglePictureButton(id, left, guiTop + 26, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, b -> {
			tileEntity.setHorizontal(!tileEntity.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getPos(), tileEntity.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(tileEntity.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new StringHoverChecker(toggleButton, Arrays.asList(ClientUtils.localize("gui.securitycraft:projector.vertical"), ClientUtils.localize("gui.securitycraft:projector.horizontal")));
		projectionRangeSlider.updateSlider();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);

		renderHoveredToolTip(mouseX, mouseY);

		for(StringHoverChecker thc : hoverCheckers)
		{
			if(thc.checkHover(mouseX, mouseY))
				renderTooltip(thc.getName(), mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		font.drawString(blockName, xSize / 2 - font.getStringWidth(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
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

		SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getPos(), data, dataType));
	}
}
