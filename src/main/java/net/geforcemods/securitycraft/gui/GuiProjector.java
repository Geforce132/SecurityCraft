package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerProjector;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.gui.components.GuiSlider.ISlider;
import net.geforcemods.securitycraft.gui.components.HoverChecker;
import net.geforcemods.securitycraft.network.packets.PacketSSyncProjector;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProjector extends GuiContainer implements ISlider {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private TileEntityProjector te;
	private String blockName;

	private HoverChecker[] hoverCheckers = new HoverChecker[3];

	private GuiSlider projectionWidth;
	private GuiSlider projectionRange;
	private GuiSlider projectionOffset;

	private int sliderWidth = 120;

	public GuiProjector(InventoryPlayer inv, TileEntityProjector te)
	{
		super(new ContainerProjector(inv, te));
		this.te = te;
		blockName = ClientUtils.localize(te.getWorld().getBlockState(te.getPos()).getBlock().getTranslationKey() + ".name");
		ySize = 225;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		projectionWidth = new GuiSlider((ClientUtils.localize("gui.securitycraft:projector.width").replace("#", te.getProjectionWidth() + "")), blockName, 0, guiLeft + ((xSize - sliderWidth) / 2), guiTop + 50, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.width").replace("#", ""), TileEntityProjector.MIN_WIDTH, TileEntityProjector.MAX_WIDTH, te.getProjectionWidth(), false, true, this);
		projectionWidth.packedFGColour = 14737632;

		projectionRange = new GuiSlider((ClientUtils.localize("gui.securitycraft:projector.range").replace("#", te.getProjectionRange() + "")), blockName, 1, guiLeft + ((xSize - sliderWidth) / 2), guiTop + 80, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.range").replace("#", ""), TileEntityProjector.MIN_RANGE, TileEntityProjector.MAX_RANGE, te.getProjectionRange(), false, true, this);
		projectionWidth.packedFGColour = 14737632;

		projectionOffset = new GuiSlider((ClientUtils.localize("gui.securitycraft:projector.offset").replace("#", te.getProjectionOffset() + "")), blockName, 2, guiLeft + ((xSize - sliderWidth) / 2), guiTop + 110, sliderWidth, 20, ClientUtils.localize("gui.securitycraft:projector.offset").replace("#", ""), TileEntityProjector.MIN_OFFSET, TileEntityProjector.MAX_OFFSET, te.getProjectionOffset(), false, true, this);
		projectionWidth.packedFGColour = 14737632;

		addButton(projectionWidth);
		addButton(projectionRange);
		addButton(projectionOffset);

		hoverCheckers[0] = new HoverChecker(projectionWidth);
		hoverCheckers[1] = new HoverChecker(projectionRange);
		hoverCheckers[2] = new HoverChecker(projectionOffset);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		renderHoveredToolTip(mouseX, mouseY);

		if(hoverCheckers[0] != null && hoverCheckers[0].checkHover(mouseX, mouseY))
			drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:projector.width.description"), 150), mouseX, mouseY, fontRenderer);

		if(hoverCheckers[1] != null && hoverCheckers[1].checkHover(mouseX, mouseY))
			drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:projector.range.description"), 150), mouseX, mouseY, fontRenderer);

		if(hoverCheckers[2] != null && hoverCheckers[2].checkHover(mouseX, mouseY))
			drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:projector.offset.description"), 150), mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(blockName, xSize / 2 - fontRenderer.getStringWidth(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public void onMouseRelease(int id)
	{
		if(id == projectionWidth.id)
			te.setProjectionWidth(projectionWidth.getValueInt());
		else if(id == projectionRange.id)
			te.setProjectionRange(projectionRange.getValueInt());
		else if(id == projectionOffset.id)
			te.setProjectionOffset(projectionOffset.getValueInt());

		SecurityCraft.network.sendToServer(new PacketSSyncProjector(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), projectionWidth.getValueInt(), projectionRange.getValueInt(), projectionOffset.getValueInt()));
	}

	@Override
	public void onChangeSliderValue(GuiSlider slider, String blockName, int id)
	{
		slider.displayString = slider.prefix + slider.getValueInt();
	}
}
