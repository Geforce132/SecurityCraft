package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorScreen extends ContainerScreen<ProjectorContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private ProjectorTileEntity tileEntity;
	private TranslationTextComponent blockName;

	private HoverChecker[] hoverCheckers = new HoverChecker[3];

	private NamedSlider projectionWidth;
	private NamedSlider projectionRange;
	private NamedSlider projectionOffset;

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

		projectionWidth = new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.width", tileEntity.getProjectionWidth()), blockName, 0, guiLeft + ((xSize - sliderWidth) / 2), guiTop + 50, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.width", ""), "", ProjectorTileEntity.MIN_WIDTH, ProjectorTileEntity.MAX_WIDTH, tileEntity.getProjectionWidth(), false, true, null, this::sliderReleased);
		projectionWidth.setFGColor(14737632);

		projectionRange = new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.range", tileEntity.getProjectionRange()), blockName, 1, guiLeft + ((xSize - sliderWidth) / 2), guiTop + 80, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.range", ""), "", ProjectorTileEntity.MIN_RANGE, ProjectorTileEntity.MAX_RANGE, tileEntity.getProjectionRange(), false, true, null, this::sliderReleased);
		projectionRange.setFGColor(14737632);

		projectionOffset = new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.offset", tileEntity.getProjectionOffset()), blockName, 2, guiLeft + ((xSize - sliderWidth) / 2), guiTop + 110, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.offset", ""), "", ProjectorTileEntity.MIN_OFFSET, ProjectorTileEntity.MAX_OFFSET, tileEntity.getProjectionOffset(), false, true, null, this::sliderReleased);
		projectionOffset.setFGColor(14737632);

		addButton(projectionWidth);
		addButton(projectionRange);
		addButton(projectionOffset);

		hoverCheckers[0] = new HoverChecker(projectionWidth);
		hoverCheckers[1] = new HoverChecker(projectionRange);
		hoverCheckers[2] = new HoverChecker(projectionOffset);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		func_230459_a_(matrix, mouseX, mouseY);

		if(hoverCheckers[0] != null && hoverCheckers[0].checkHover(mouseX, mouseY))
			renderTooltip(matrix, minecraft.fontRenderer.func_238425_b_(ClientUtils.localize("gui.securitycraft:projector.width.description"), 150), mouseX, mouseY);

		if(hoverCheckers[1] != null && hoverCheckers[1].checkHover(mouseX, mouseY))
			renderTooltip(matrix, minecraft.fontRenderer.func_238425_b_(ClientUtils.localize("gui.securitycraft:projector.range.description"), 150), mouseX, mouseY);

		if(hoverCheckers[2] != null && hoverCheckers[2].checkHover(mouseX, mouseY))
			renderTooltip(matrix, minecraft.fontRenderer.func_238425_b_(ClientUtils.localize("gui.securitycraft:projector.offset.description"), 150), mouseX, mouseY);
	}

	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.func_238422_b_(matrix, blockName, xSize / 2 - font.func_238414_a_(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
	}

	public void sliderReleased(NamedSlider slider)
	{
		if(slider.id == 0)
			tileEntity.setProjectionWidth(slider.getValueInt());
		else if(slider.id == 1)
			tileEntity.setProjectionRange(slider.getValueInt());
		else if(slider.id == 2)
			tileEntity.setProjectionOffset(slider.getValueInt());

		SecurityCraft.channel.sendToServer(new SyncProjector(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), projectionWidth.getValueInt(), projectionRange.getValueInt(), projectionOffset.getValueInt()));
	}

}
