package net.geforcemods.securitycraft.screen;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.inventory.StateSelectorAccessMenu;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.Slider;
import net.geforcemods.securitycraft.screen.components.Slider.ISlider;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProjectorScreen extends GuiContainer implements ISlider, IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final String SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block").getFormattedText();
	private ProjectorBlockEntity te;
	private Block block;
	private String title;
	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[5];
	private StringHoverChecker slotHoverChecker;
	private Slider projectionWidthSlider;
	private Slider projectionHeightSlider;
	private Slider projectionRangeSlider;
	private Slider projectionOffsetSlider;
	private TogglePictureButton toggleButton;
	private StateSelector stateSelector;
	private int sliderWidth = 120;

	public ProjectorScreen(InventoryPlayer inv, ProjectorBlockEntity te) {
		super(new ProjectorMenu(inv, te));
		this.te = te;
		block = te.getWorld().getBlockState(te.getPos()).getBlock();
		title = te.getDisplayName().getFormattedText();
		ySize = 235;
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft += 90;

		int id = 0;
		int left = guiLeft + ((xSize - sliderWidth) / 2);

		projectionWidthSlider = addButton(new Slider(Utils.localize("gui.securitycraft:projector.width", te.getProjectionWidth()).getFormattedText(), block, id, left, guiTop + 57, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", "").getFormattedText(), ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, te.getProjectionWidth(), false, true, this));
		hoverCheckers[id++] = new StringHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description").getFormattedText());

		projectionHeightSlider = addButton(new Slider(Utils.localize("gui.securitycraft:projector.height", te.getProjectionHeight()).getFormattedText(), block, id, left, guiTop + 78, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", "").getFormattedText(), ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, te.getProjectionHeight(), false, true, this));
		hoverCheckers[id++] = new StringHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description").getFormattedText());

		projectionRangeSlider = addButton(new Slider(Utils.localize("gui.securitycraft:projector.range", te.getProjectionRange()).getFormattedText(), block, id, left, guiTop + 99, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", "").getFormattedText(), ProjectorBlockEntity.MIN_RANGE, ProjectorBlockEntity.MAX_RANGE, te.getProjectionRange(), false, true, this));
		hoverCheckers[id++] = new StringHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description").getFormattedText());

		projectionOffsetSlider = addButton(new Slider(Utils.localize("gui.securitycraft:projector.offset", te.getProjectionOffset()).getFormattedText(), block, id, left, guiTop + 120, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", "").getFormattedText(), ProjectorBlockEntity.MIN_OFFSET, ProjectorBlockEntity.MAX_OFFSET, te.getProjectionOffset(), false, true, this));
		hoverCheckers[id++] = new StringHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description").getFormattedText());

		//@formatter:off
		toggleButton = addButton(new TogglePictureButton(id, left + sliderWidth - 20, guiTop + 36, 20, 20, TEXTURE, new int[] {176, 192}, new int[] {0, 0}, 2, 2, b -> {
			//@formatter:on
			te.setHorizontal(!te.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.network.sendToServer(new SyncProjector(te.getPos(), te.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(te.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new StringHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical").getFormattedText(), Utils.localize("gui.securitycraft:projector.horizontal").getFormattedText()));
		projectionRangeSlider.updateSlider();

		slotHoverChecker = new StringHoverChecker(guiTop + 22, guiTop + 39, guiLeft + 78, guiLeft + 95, SLOT_TOOLTIP);

		stateSelector = new StateSelector((StateSelectorAccessMenu) inventorySlots, guiLeft - 190, guiTop + 7, 0, 197, 0);
		stateSelector.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		renderHoveredToolTip(mouseX, mouseY);

		for (StringHoverChecker thc : hoverCheckers) {
			if (thc.checkHover(mouseX, mouseY))
				drawHoveringText(thc.getName(), mouseX, mouseY);
		}

		if (slotHoverChecker.checkHover(mouseX, mouseY) && te.isEmpty())
			drawHoveringText(slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (stateSelector != null)
			stateSelector.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (stateSelector != null)
			stateSelector.mouseClicked(mouseX, mouseY, button);

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (stateSelector != null)
			stateSelector.mouseReleased(mouseX, mouseY, state);

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public List<Rectangle> getGuiExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getExtraAreas();
		else
			return new ArrayList<>();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (stateSelector.getState() != null) {
			te.setProjectedState(stateSelector.getState());
			SecurityCraft.network.sendToServer(new SyncProjector(te.getPos(), stateSelector.getState()));
		}
	}

	@Override
	public void onMouseRelease(int id) {
		int data = 0;
		DataType dataType = DataType.INVALID;

		if (id == projectionWidthSlider.id) {
			te.setProjectionWidth(data = projectionWidthSlider.getValueInt());
			dataType = DataType.WIDTH;
		}
		else if (id == projectionHeightSlider.id) {
			te.setProjectionHeight(data = projectionHeightSlider.getValueInt());
			dataType = DataType.HEIGHT;
		}
		else if (id == projectionRangeSlider.id) {
			te.setProjectionRange(data = projectionRangeSlider.getValueInt());
			dataType = DataType.RANGE;
		}
		else if (id == projectionOffsetSlider.id) {
			te.setProjectionOffset(data = projectionOffsetSlider.getValueInt());
			dataType = DataType.OFFSET;
		}

		SecurityCraft.network.sendToServer(new SyncProjector(te.getPos(), data, dataType));
	}

	@Override
	public void onChangeSliderValue(Slider slider, Block block, int id) {
		if (te.isHorizontal() && slider.id == projectionRangeSlider.id)
			slider.displayString = slider.prefix + (slider.getValueInt() - 16);
		else
			slider.displayString = slider.prefix + slider.getValueInt();
	}
}
