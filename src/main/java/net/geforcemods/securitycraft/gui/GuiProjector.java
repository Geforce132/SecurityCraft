package net.geforcemods.securitycraft.gui;

import java.util.Arrays;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerProjector;
import net.geforcemods.securitycraft.gui.components.ClickButton;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.gui.components.GuiSlider.ISlider;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.gui.components.TogglePictureButton;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProjector extends GuiContainer implements ISlider {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final String SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block").getFormattedText();
	private TileEntityProjector te;
	private String blockName;

	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[5];
	private StringHoverChecker slotHoverChecker;

	private GuiSlider projectionWidthSlider;
	private GuiSlider projectionHeightSlider;
	private GuiSlider projectionRangeSlider;
	private GuiSlider projectionOffsetSlider;
	private TogglePictureButton toggleButton;

	private int sliderWidth = 120;

	public GuiProjector(InventoryPlayer inv, TileEntityProjector te)
	{
		super(new ContainerProjector(inv, te));
		this.te = te;
		blockName = Utils.localize(te.getWorld().getBlockState(te.getPos()).getBlock().getTranslationKey() + ".name").getFormattedText();
		ySize = 225;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int id = 0;
		int left = guiLeft + ((xSize - sliderWidth) / 2);

		projectionWidthSlider = addButton(new GuiSlider(Utils.localize("gui.securitycraft:projector.width", te.getProjectionWidth()).getFormattedText(), blockName, id, left, guiTop + 47, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", "").getFormattedText(), TileEntityProjector.MIN_WIDTH, TileEntityProjector.MAX_WIDTH, te.getProjectionWidth(), false, true, this));
		projectionWidthSlider.packedFGColour = 14737632;
		hoverCheckers[id++] = new StringHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description").getFormattedText());

		projectionHeightSlider = addButton(new GuiSlider(Utils.localize("gui.securitycraft:projector.height", te.getProjectionHeight()).getFormattedText(), blockName, id, left, guiTop + 68, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", "").getFormattedText(), TileEntityProjector.MIN_WIDTH, TileEntityProjector.MAX_WIDTH, te.getProjectionHeight(), false, true, this));
		projectionHeightSlider.packedFGColour = 14737632;
		hoverCheckers[id++] = new StringHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description").getFormattedText());

		projectionRangeSlider = addButton(new GuiSlider(Utils.localize("gui.securitycraft:projector.range", te.getProjectionRange()).getFormattedText(), blockName, id, left, guiTop + 89, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", "").getFormattedText(), TileEntityProjector.MIN_RANGE, TileEntityProjector.MAX_RANGE, te.getProjectionRange(), false, true, this));
		projectionWidthSlider.packedFGColour = 14737632;
		hoverCheckers[id++] = new StringHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description").getFormattedText());

		projectionOffsetSlider = addButton(new GuiSlider(Utils.localize("gui.securitycraft:projector.offset", te.getProjectionOffset()).getFormattedText(), blockName, id, left, guiTop + 110, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", "").getFormattedText(), TileEntityProjector.MIN_OFFSET, TileEntityProjector.MAX_OFFSET, te.getProjectionOffset(), false, true, this));
		projectionWidthSlider.packedFGColour = 14737632;
		hoverCheckers[id++] = new StringHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description").getFormattedText());

		toggleButton = addButton(new TogglePictureButton(id, left, guiTop + 26, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, b -> {
			te.setHorizontal(!te.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.network.sendToServer(new SyncProjector(te.getPos(), te.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(te.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new StringHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical").getFormattedText(), Utils.localize("gui.securitycraft:projector.horizontal").getFormattedText()));
		projectionRangeSlider.updateSlider();

		slotHoverChecker = new StringHoverChecker(guiTop + 22, guiTop + 39, guiLeft + 78, guiLeft + 95, SLOT_TOOLTIP);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		renderHoveredToolTip(mouseX, mouseY);

		for(StringHoverChecker thc : hoverCheckers)
		{
			if(thc.checkHover(mouseX, mouseY))
				drawHoveringText(thc.getName(), mouseX, mouseY);
		}

		if(slotHoverChecker.checkHover(mouseX, mouseY) && te.isEmpty())
			drawHoveringText(slotHoverChecker.getName(), mouseX, mouseY);
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
	protected void actionPerformed(GuiButton button)
	{
		if(button instanceof ClickButton)
			((ClickButton)button).onClick();
	}

	@Override
	public void onMouseRelease(int id)
	{
		int data = 0;
		DataType dataType = DataType.INVALID;

		if(id == projectionWidthSlider.id)
		{
			te.setProjectionWidth(data = projectionWidthSlider.getValueInt());
			dataType = DataType.WIDTH;
		}
		if(id == projectionHeightSlider.id)
		{
			te.setProjectionHeight(data = projectionHeightSlider.getValueInt());
			dataType = DataType.HEIGHT;
		}
		else if(id == projectionRangeSlider.id)
		{
			te.setProjectionRange(data = projectionRangeSlider.getValueInt());
			dataType = DataType.RANGE;
		}
		else if(id == projectionOffsetSlider.id)
		{
			te.setProjectionOffset(data = projectionOffsetSlider.getValueInt());
			dataType = DataType.OFFSET;
		}

		SecurityCraft.network.sendToServer(new SyncProjector(te.getPos(), data, dataType));
	}

	@Override
	public void onChangeSliderValue(GuiSlider slider, String blockName, int id)
	{
		if(te.isHorizontal() && slider.id == projectionRangeSlider.id)
			slider.displayString = slider.prefix + (slider.getValueInt() - 16);
		else
			slider.displayString = slider.prefix + slider.getValueInt();
	}
}
