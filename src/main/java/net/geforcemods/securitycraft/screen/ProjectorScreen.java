package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncProjector.DataType;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorScreen extends ContainerScreen<ProjectorMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/projector.png");
	private static final TranslationTextComponent SLOT_TOOLTIP = Utils.localize("gui.securitycraft:projector.block");
	private ProjectorBlockEntity be;
	private Block block;
	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[6];
	private TextHoverChecker slotHoverChecker;
	private NamedSlider projectionWidthSlider;
	private NamedSlider projectionHeightSlider;
	private NamedSlider projectionRangeSlider;
	private NamedSlider projectionOffsetSlider;
	private StateSelector stateSelector;
	private int sliderWidth = 120;

	public ProjectorScreen(ProjectorMenu menu, PlayerInventory inv, ITextComponent title) {
		super(menu, inv, title);
		this.be = menu.be;
		block = be.getBlockState().getBlock();
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
		String denotation = BlockUtils.getLanguageKeyDenotation(block);

		projectionWidthSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.width", be.getProjectionWidth()), denotation, left, topPos + 57, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.width", ""), "", ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionWidth(), false, true, null, this::applySliderValue));
		projectionWidthSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionWidthSlider, Utils.localize("gui.securitycraft:projector.width.description"));

		projectionHeightSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.height", be.getProjectionHeight()), denotation, left, topPos + 78, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.height", ""), "", ProjectorBlockEntity.MIN_WIDTH, ProjectorBlockEntity.MAX_WIDTH, be.getProjectionHeight(), false, true, null, this::applySliderValue));
		projectionHeightSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionHeightSlider, Utils.localize("gui.securitycraft:projector.height.description"));

		projectionRangeSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.range", be.getProjectionRange()), denotation, left, topPos + 99, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.range", ""), "", ProjectorBlockEntity.MIN_RANGE, ProjectorBlockEntity.MAX_RANGE, be.getProjectionRange(), false, true, slider -> {
			//show a different number so it makes sense within the world
			if (be.isHorizontal())
				slider.setMessage(new StringTextComponent("").append(slider.dispString).append(Integer.toString((int) Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue) - 16)));
		}, this::applySliderValue));
		projectionRangeSlider.setFGColor(0xE0E0E0);
		hoverCheckers[id++] = new TextHoverChecker(projectionRangeSlider, Utils.localize("gui.securitycraft:projector.range.description"));

		projectionOffsetSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.offset", be.getProjectionOffset()), denotation, left, topPos + 120, sliderWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), "", ProjectorBlockEntity.MIN_OFFSET, ProjectorBlockEntity.MAX_OFFSET, be.getProjectionOffset(), false, true, null, this::applySliderValue));
		projectionOffsetSlider.setFGColor(14737632);
		hoverCheckers[id++] = new TextHoverChecker(projectionOffsetSlider, Utils.localize("gui.securitycraft:projector.offset.description"));

		//@formatter:off
		toggleButton = addButton(new TogglePictureButton(left + sliderWidth - 20, topPos + 36, 20, 20, TEXTURE, new int[]{176, 192}, new int[]{0, 0}, 2, 2, slider -> {
			//@formatter:on
			be.setHorizontal(!be.isHorizontal());
			projectionRangeSlider.updateSlider();
			SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), be.isHorizontal() ? 1 : 0, DataType.HORIZONTAL));
		}));
		toggleButton.setCurrentIndex(be.isHorizontal() ? 1 : 0);
		hoverCheckers[id++] = new TextHoverChecker(toggleButton, Arrays.asList(Utils.localize("gui.securitycraft:projector.vertical"), Utils.localize("gui.securitycraft:projector.horizontal")));
		projectionRangeSlider.updateSlider();

		overrideCheckbox = addButton(new CallbackCheckbox(left + sliderWidth - 41, topPos + 36, 20, 20, StringTextComponent.EMPTY, be.isOverridingBlocks(), newValue -> {
			be.setOverridingBlocks(newValue);
			SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), be.isOverridingBlocks() ? 1 : 0, DataType.OVERRIDING_BLOCKS));
		}, 0));
		hoverCheckers[id++] = new TextHoverChecker(overrideCheckbox, Arrays.asList(Utils.localize("gui.securitycraft:projector.isOverridingBlocks.yes"), Utils.localize("gui.securitycraft:projector.isOverridingBlocks.no")));

		slotHoverChecker = new TextHoverChecker(topPos + 22, topPos + 39, leftPos + 78, leftPos + 95, SLOT_TOOLTIP);

		stateSelector = addWidget(new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 197, 0, -2.85F, -0.45F));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		renderTooltip(pose, mouseX, mouseY);

		for (TextHoverChecker thc : hoverCheckers) {
			if (thc.checkHover(mouseX, mouseY)) {
				renderTooltip(pose, thc.getName(), mouseX, mouseY);
				break;
			}
		}

		if (slotHoverChecker.checkHover(mouseX, mouseY) && menu.be.isEmpty())
			renderTooltip(pose, slotHoverChecker.getName(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		if (stateSelector != null)
			stateSelector.render(pose, mouseX, mouseY, partialTicks);
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

	public void applySliderValue(NamedSlider slider) {
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
			be.setProjectionRange(data);
			dataType = DataType.RANGE;
		}
		else if (slider == projectionOffsetSlider) {
			data = slider.getValueInt();
			be.setProjectionOffset(data);
			dataType = DataType.OFFSET;
		}

		SecurityCraft.channel.sendToServer(new SyncProjector(be.getBlockPos(), data, dataType));
	}
}
