package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.StateSelectorAccessMenu;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

public class StateSelector extends Screen implements GuiEventListener, NarratableEntry, ContainerListener {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/state_selector.png");
	private static final int PAGE_LENGTH = 5;
	private static final float ROTATION_SENSITIVITY = 0.1F;
	private static final Vector3f Y_DRAG_ROTATION_VECTOR = new Vector3f((float) (1.0D / Math.sqrt(2)), 0, (float) (1.0D / Math.sqrt(2)));
	private static final Quaternionf DEFAULT_ROTATION = ClientUtils.fromXYZDegrees(15.0F, -135.0F, 0.0F).rotateAxis(30.0F * ((float) Math.PI / 180.0F), Y_DRAG_ROTATION_VECTOR).mul(Axis.YP.rotationDegrees(-180.0F));
	private static final EnumProperty<StandingOrWallType> STANDING_OR_WALL_TYPE_PROPERTY = EnumProperty.create("standing_or_wall", StandingOrWallType.class);
	private static final Vector3f Y_AXIS = new Vector3f(0.0F, 1.0F, 0.0F);
	private final StateSelectorAccessMenu menu;
	private final int xStart, yStart, slotToCheck;
	private final int previewXTranslation, previewYTranslation;
	private final HoverChecker dragHoverChecker;
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private Item blockItem = Items.AIR;
	private BlockState state = Blocks.AIR.defaultBlockState();
	private List<Property<?>> properties = List.of();
	private BlockEntity be = null;
	@SuppressWarnings("rawtypes")
	private BlockEntityRenderer beRenderer = null;
	private List<BlockStatePropertyButton<?>> propertyButtons = new ArrayList<>();
	private int page, amountOfPages;
	private Button previousPageButton, nextPageButton;
	private Quaternionf dragRotation = new Quaternionf();
	private Quaternionf rotation = new Quaternionf();
	private Vector3f xRotationVector = new Vector3f();
	private Vector3f yRotationVector = new Vector3f();
	private boolean clickedInDragRegion = false;
	private StandingOrWallType standingOrWallType = StandingOrWallType.NONE;

	public StateSelector(StateSelectorAccessMenu menu, Component title, int xStart, int yStart, int slotToCheck, int dragStartX, int dragStartY, int previewXTranslation, int previewYTranslation) {
		super(title);
		menu.addSlotListener(this);
		this.menu = menu;
		this.xStart = xStart;
		this.yStart = yStart;
		this.slotToCheck = slotToCheck;
		this.previewXTranslation = previewXTranslation;
		this.previewYTranslation = previewYTranslation;
		dragStartX += xStart;
		dragStartY += yStart;
		dragHoverChecker = new HoverChecker(dragStartY, dragStartY + 47, dragStartX, dragStartX + 47);
	}

	@Override
	protected void init() {
		if (menu.getStateStack().getItem() instanceof BlockItem item) {
			BlockState savedState = menu.getSavedState();
			Block blockToCheck = item.getBlock();

			standingOrWallType = menu.getStandingOrWallType();

			if (item instanceof StandingAndWallBlockItem sawbi && standingOrWallType == StandingOrWallType.WALL)
				blockToCheck = sawbi.wallBlock;

			if (blockToCheck == savedState.getBlock())
				state = savedState;
			else
				state = item.getBlock().defaultBlockState();

			blockItem = item;
		}

		previousPageButton = Button.builder(Component.literal("<"), button -> turnPage(-1)).bounds(xStart + 69, yStart + 125, 20, 20).build();
		nextPageButton = Button.builder(Component.literal(">"), button -> turnPage(1)).bounds(xStart + 126, yStart + 125, 20, 20).build();
		updateButtons(true, false);
		extraAreas.add(new Rect2i(xStart, 0, 193, minecraft.getWindow().getGuiScaledHeight()));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		PoseStack pose = guiGraphics.pose();

		DEFAULT_ROTATION.mul(dragRotation, rotation);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		previousPageButton.render(guiGraphics, mouseX, mouseY, partialTick);
		nextPageButton.render(guiGraphics, mouseX, mouseY, partialTick);
		pose.pushPose();
		pose.translate(xStart + previewXTranslation, yStart + previewYTranslation, 100);
		pose.scale(-24.0F, -24.0F, -24.0F);
		pose.translate(0.5F, 0.5F, 0.5F);
		pose.mulPose(rotation.rotateY((float) Math.toRadians(-90.0D)));
		pose.translate(-0.5F, -0.5F, -0.5F);
		Lighting.setupForEntityInInventory(rotation);
		renderBlockModel(state, pose, bufferSource);

		if (beRenderer != null)
			beRenderer.render(be, partialTick, pose, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

		pose.popPose();
		guiGraphics.flush();
		Lighting.setupFor3DItems();

		for (int i = 0; i < propertyButtons.size(); i++) {
			String propertyName = propertyButtons.get(i).getProperty().getName();

			guiGraphics.drawString(font, propertyName, xStart + 91 - font.width(propertyName) - 2, yStart + i * 23 + 10, 0x404040, false);
		}

		guiGraphics.drawString(font, page + "/" + amountOfPages, xStart + 100, yStart + 130, 0x404040, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.blit(RenderType::guiTextured, TEXTURE, xStart, yStart, 0.0F, 0.0F, 193, 150, 256, 256);
	}

	public void updateButtons(boolean updateInfo, boolean slotChanged) {
		if (updateInfo) {
			properties = new ArrayList<>(state.getProperties());
			properties.removeIf(BlockStateProperties.WATERLOGGED::equals);

			if (blockItem instanceof StandingAndWallBlockItem) {
				properties.add(0, STANDING_OR_WALL_TYPE_PROPERTY);

				if (slotChanged)
					standingOrWallType = StandingOrWallType.STANDING;
			}
			else if (slotChanged)
				standingOrWallType = StandingOrWallType.NONE;

			amountOfPages = (int) Math.ceil(properties.size() / (float) PAGE_LENGTH);

			page = switch (amountOfPages) {
				case 0 -> 0;
				default -> 1;
			};

			updateBlockEntityInfo(true);
			dragRotation.identity();
		}

		int buttonY = 0;
		int pageStartIndex = (page - 1) * PAGE_LENGTH;
		int i = 0;

		propertyButtons.forEach(this::removeWidget);
		propertyButtons.clear();

		for (Property<?> property : properties) {
			if (i < pageStartIndex) {
				i++;
				continue;
			}
			else if (i >= pageStartIndex + PAGE_LENGTH)
				break;

			Collection<?> values = property.getPossibleValues();
			Object defaultValue = property == STANDING_OR_WALL_TYPE_PROPERTY ? standingOrWallType : state.getValue(property);
			int defaultValueIndex = 0;

			for (Object o : values) {
				if (o == defaultValue)
					break;

				defaultValueIndex++;
			}

			propertyButtons.add(addRenderableWidget(new BlockStatePropertyButton<>(xStart + 91, yStart + buttonY + 5, 100, 20, defaultValueIndex, property)));
			buttonY += 23;
			i++;
		}

		if (previousPageButton != null)
			previousPageButton.active = page > 1;

		if (nextPageButton != null)
			nextPageButton.active = page != amountOfPages;
	}

	public void turnPage(int direction) {
		page += Math.signum(direction);

		if (page > amountOfPages)
			page = 1;
		else if (page < 1)
			page = amountOfPages;

		updateButtons(false, false);
	}

	private void updateBlockEntityInfo(boolean reset) {
		if (reset) {
			be = null;
			beRenderer = null;
		}

		if (state.hasBlockEntity()) {
			if (be == null) {
				Minecraft mc = Minecraft.getInstance();

				be = ((EntityBlock) state.getBlock()).newBlockEntity(BlockPos.ZERO, state);

				if (be != null) {
					be.setLevel(mc.level);
					beRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(be);
				}
				else
					beRenderer = null;
			}
			else
				be.setBlockState(state);
		}
	}

	public void renderBlockModel(BlockState state, PoseStack pose, MultiBufferSource bufferSource) {
		if (state.getRenderShape() == RenderShape.MODEL) {
			BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
			BakedModel blockModel = blockRenderer.getBlockModel(state);
			int color = minecraft.getBlockColors().getColor(state, null, null, 0);
			float r = (color >> 16 & 255) / 255.0F;
			float g = (color >> 8 & 255) / 255.0F;
			float b = (color & 255) / 255.0F;

			for (RenderType renderType : blockModel.getRenderTypes(state, minecraft.level.random, ModelData.EMPTY)) {
				blockRenderer.getModelRenderer().renderModel(pose.last(), bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(renderType)), state, blockModel, r, g, b, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
			}
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0 && clickedInDragRegion) {
			Quaternionf inverted = new Quaternionf(dragRotation).invert();

			dragRotation.mul(new Quaternionf().fromAxisAngleRad(Y_AXIS.rotate(inverted, xRotationVector), (float) dragX * ROTATION_SENSITIVITY));
			dragRotation.mul(new Quaternionf().fromAxisAngleRad(Y_DRAG_ROTATION_VECTOR.rotate(inverted, yRotationVector), (float) -dragY * ROTATION_SENSITIVITY));
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (int i = 0; i < propertyButtons.size(); i++) {
			if (propertyButtons.get(i).mouseClicked(mouseX, mouseY, button))
				break;
		}

		previousPageButton.mouseClicked(mouseX, mouseY, button);
		nextPageButton.mouseClicked(mouseX, mouseY, button);
		clickedInDragRegion = dragHoverChecker.checkHover(mouseX, mouseY);
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		clickedInDragRegion = false;
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		for (int i = 0; i < propertyButtons.size(); i++) {
			if (propertyButtons.get(i).mouseScrolled(mouseX, mouseY, scrollX, scrollY))
				break;
		}

		return false;
	}

	@Override
	public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack newStack) {
		if (slotIndex == slotToCheck) {
			if (newStack.getItem() instanceof BlockItem item && (state == null || item.getBlock() != state.getBlock())) {
				state = item.getBlock().defaultBlockState();
				blockItem = item;
			}
			else {
				state = Blocks.AIR.defaultBlockState();
				blockItem = Items.AIR;
			}

			updateButtons(true, true);

			if (this.menu != null)
				this.menu.onStateChange(state);
		}
	}

	@Override
	public void changeFocus(ComponentPath path) {}

	@Override
	public List<? extends GuiEventListener> children() {
		return List.of();
	}

	@Override
	public void dataChanged(AbstractContainerMenu menu, int slotIndex, int value) {}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	public BlockState getState() {
		return state;
	}

	public StandingOrWallType getStandingOrWallType() {
		return standingOrWallType;
	}

	public List<Rect2i> getGuiExtraAreas() {
		return extraAreas;
	}

	public class BlockStatePropertyButton<T extends Comparable<T>> extends ToggleComponentButton {
		private final Property<T> property;
		private T value;

		public BlockStatePropertyButton(int xPos, int yPos, int width, int height, int initialValue, Property<T> property) {
			super(xPos, yPos, width, height, null, initialValue, property.getPossibleValues().size(), b -> {});
			this.property = property;
			onValueChange();
		}

		@Override
		public void onValueChange() {
			if (property != null) {
				Collection<T> values = property.getPossibleValues();
				int i = 0;

				for (T t : values) {
					if (i++ == getCurrentIndex()) {
						value = t;
						break;
					}
				}

				setMessage(Component.literal(property.getName(value)));
			}
		}

		@Override
		public void onPress() {
			if (property == STANDING_OR_WALL_TYPE_PROPERTY) {
				state = standingOrWallType.getNewState((StandingAndWallBlockItem) blockItem);
				standingOrWallType = standingOrWallType == StandingOrWallType.STANDING ? StandingOrWallType.WALL : StandingOrWallType.STANDING;
				updateButtons(true, false);
				updateBlockEntityInfo(true);
			}
			else {
				state = state.setValue(property, value);
				updateBlockEntityInfo(false);
			}

			menu.onStateChange(state);
		}

		public Property<T> getProperty() {
			return property;
		}

		public T getValue() {
			return value;
		}
	}
}