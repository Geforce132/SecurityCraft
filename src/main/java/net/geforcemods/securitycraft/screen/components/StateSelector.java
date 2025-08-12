package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.inventory.StateSelectorAccessMenu;
import net.geforcemods.securitycraft.misc.FullbrightBlockAndTintGetter;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class StateSelector extends Screen implements IGuiEventListener, IContainerListener {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/state_selector.png");
	private static final int PAGE_LENGTH = 5;
	private static final float ROTATION_SENSITIVITY = 0.1F;
	private static final Vector3f Y_DRAG_ROTATION_VECTOR = new Vector3f((float) (1.0D / Math.sqrt(2)), 0, (float) (1.0D / Math.sqrt(2)));
	private static final Quaternion DEFAULT_ROTATION = ClientUtils.fromXYZDegrees(15.0F, -135.0F, 0.0F);
	private static final EnumProperty<StandingOrWallType> STANDING_OR_WALL_TYPE_PROPERTY = EnumProperty.create("standing_or_wall", StandingOrWallType.class);
	private final StateSelectorAccessMenu menu;
	private final int xStart, yStart, slotToCheck;
	private final float previewXTranslation, previewYTranslation;
	private final HoverChecker dragHoverChecker;
	private final List<Rectangle2d> extraAreas = new ArrayList<>();
	private FullbrightBlockAndTintGetter fullbrightBlockAndTintGetter;
	private Item blockItem = Items.AIR;
	private BlockState state = Blocks.AIR.defaultBlockState();
	private List<Property<?>> properties = new ArrayList<>();
	private TileEntity be = null;
	@SuppressWarnings("rawtypes")
	private TileEntityRenderer beRenderer = null;
	private List<BlockStatePropertyButton<?>> propertyButtons = new ArrayList<>();
	private int page, amountOfPages;
	private Button previousPageButton, nextPageButton;
	private Matrix4f dragRotation = Util.make(new Matrix4f(), Matrix4f::setIdentity);
	private boolean clickedInDragRegion = false;
	private StandingOrWallType standingOrWallType = StandingOrWallType.NONE;

	public StateSelector(StateSelectorAccessMenu menu, ITextComponent title, int xStart, int yStart, int slotToCheck, int dragStartX, int dragStartY, float previewXTranslation, float previewYTranslation) {
		super(title);
		this.menu = menu;
		this.xStart = xStart;
		this.yStart = yStart;
		this.slotToCheck = slotToCheck;
		this.previewXTranslation = previewXTranslation;
		this.previewYTranslation = previewYTranslation;
		dragStartX += xStart;
		dragStartY += yStart;
		dragHoverChecker = new HoverChecker(dragStartY, dragStartY + 47, dragStartX, dragStartX + 47);
		copyStateFromMenu();
		menu.addSlotListener(this);
	}

	@Override
	protected void init() {
		previousPageButton = new ExtendedButton(xStart + 69, yStart + 125, 20, 20, new StringTextComponent("<"), button -> turnPage(-1));
		nextPageButton = new ExtendedButton(xStart + 126, yStart + 125, 20, 20, new StringTextComponent(">"), button -> turnPage(1));
		updateButtons(true, false);
		extraAreas.add(new Rectangle2d(xStart, 0, 193, minecraft.getWindow().getGuiScaledHeight()));
		fullbrightBlockAndTintGetter = new FullbrightBlockAndTintGetter(minecraft.level);
	}

	private void copyStateFromMenu() {
		if (menu.getStateStack().getItem() instanceof BlockItem) {
			BlockItem item = (BlockItem) menu.getStateStack().getItem();
			BlockState savedState = menu.getSavedState();
			Block blockToCheck = item.getBlock();

			standingOrWallType = menu.getStandingOrWallType();

			if (item instanceof WallOrFloorItem && standingOrWallType == StandingOrWallType.WALL)
				blockToCheck = ((WallOrFloorItem) item).wallBlock;

			if (blockToCheck == savedState.getBlock())
				state = savedState;
			else
				state = item.getBlock().defaultBlockState();

			blockItem = item;
		}
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
		IRenderTypeBuffer.Impl bufferSource = minecraft.renderBuffers().bufferSource();

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(TEXTURE);
		blit(pose, xStart, yStart, 0, 0, 193, 150);
		super.render(pose, mouseX, mouseY, partialTick);
		previousPageButton.render(pose, mouseX, mouseY, partialTick);
		nextPageButton.render(pose, mouseX, mouseY, partialTick);
		pose.pushPose();
		pose.translate(previewXTranslation, previewYTranslation, 0);
		pose.last().pose().multiply(1.5F);
		pose.translate(0.5F, 0.5F, 0.5F);
		pose.mulPose(DEFAULT_ROTATION);
		pose.last().pose().multiply(dragRotation);
		pose.translate(-0.5F, -0.5F, -0.5F);
		renderBlockModel(state, pose, bufferSource);

		if (beRenderer != null)
			beRenderer.render(be, partialTick, pose, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);

		pose.popPose();

		for (int i = 0; i < propertyButtons.size(); i++) {
			String propertyName = propertyButtons.get(i).getProperty().getName();

			font.draw(pose, propertyName, xStart + 91 - font.width(propertyName) - 2, yStart + i * 23 + 10, 0x404040);
		}

		font.draw(pose, page + "/" + amountOfPages, xStart + 100, yStart + 130, 0x404040);
	}

	public void updateButtons(boolean updateInfo, boolean slotChanged) {
		if (updateInfo) {
			properties = new ArrayList<>(state.getProperties());
			properties.removeIf(BlockStateProperties.WATERLOGGED::equals);

			if (blockItem instanceof WallOrFloorItem) {
				properties.add(0, STANDING_OR_WALL_TYPE_PROPERTY);

				if (slotChanged)
					standingOrWallType = StandingOrWallType.STANDING;
			}
			else if (slotChanged)
				standingOrWallType = StandingOrWallType.NONE;

			amountOfPages = (int) Math.ceil(properties.size() / (float) PAGE_LENGTH);
			page = amountOfPages == 0 ? 0 : 1;
			updateBlockEntityInfo(true);
			dragRotation.setIdentity();
		}

		int buttonY = 0;
		int pageStartIndex = (page - 1) * PAGE_LENGTH;
		int i = 0;

		buttons.removeIf(propertyButtons::contains);
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

			propertyButtons.add(addButton(new BlockStatePropertyButton<>(xStart + 91, yStart + buttonY + 5, 100, 20, defaultValueIndex, property)));
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

		if (state.hasTileEntity()) {
			if (be == null) {
				Minecraft mc = Minecraft.getInstance();

				be = state.createTileEntity(mc.level);
				be.level = mc.level;
				be.blockState = state; //Prevents crash from incorrectly set block state
				Utils.updateBlockEntityWithItemTag(be, menu.getStateStack());
				beRenderer = TileEntityRendererDispatcher.instance.getRenderer(be);
			}

			be.blockState = state;
		}
	}

	public void renderBlockModel(BlockState state, MatrixStack pose, IRenderTypeBuffer bufferSource) {
		if (state.getRenderShape() == BlockRenderType.MODEL) {
			BlockRendererDispatcher blockRenderer = minecraft.getBlockRenderer();
			IBakedModel blockModel = blockRenderer.getBlockModel(state);

			blockRenderer.getModelRenderer().tesselateWithoutAO(fullbrightBlockAndTintGetter, blockModel, state, BlockPos.ZERO, pose, bufferSource.getBuffer(RenderTypeLookup.getRenderType(state, false)), false, minecraft.level.random, 42L, OverlayTexture.NO_OVERLAY);
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0 && clickedInDragRegion) {
			dragRotation.transpose();
			dragRotation.multiply(Vector3f.YN.rotation((float) dragX * ROTATION_SENSITIVITY));
			dragRotation.multiply(Y_DRAG_ROTATION_VECTOR.rotation((float) dragY * ROTATION_SENSITIVITY));
			dragRotation.transpose();
		}

		return true;
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
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		for (int i = 0; i < propertyButtons.size(); i++) {
			if (propertyButtons.get(i).mouseScrolled(mouseX, mouseY, delta))
				break;
		}

		return false;
	}

	@Override
	public void slotChanged(Container menu, int slotIndex, ItemStack newStack) {
		if (slotIndex == slotToCheck) {
			if (newStack.getItem() instanceof BlockItem) {
				BlockItem newItem = (BlockItem) newStack.getItem();

				if (state == null || newItem.getBlock() != state.getBlock()) {
					state = newItem.getBlock().defaultBlockState();
					blockItem = newItem;
				}
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
	public boolean changeFocus(boolean focus) {
		return false;
	}

	@Override
	public List<? extends IGuiEventListener> children() {
		return new ArrayList<>();
	}

	@Override
	public void refreshContainer(Container containerToSend, NonNullList<ItemStack> itemsList) {}

	@Override
	public void setContainerData(Container container, int varToUpdate, int newValue) {}

	public BlockState getState() {
		return state;
	}

	public StandingOrWallType getStandingOrWallType() {
		return standingOrWallType;
	}

	public List<Rectangle2d> getGuiExtraAreas() {
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

				setMessage(new StringTextComponent(property.getName(value)));
			}
		}

		@Override
		public void onPress() {
			if (property == STANDING_OR_WALL_TYPE_PROPERTY) {
				state = standingOrWallType.getNewState((WallOrFloorItem) blockItem);
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