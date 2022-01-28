package net.geforcemods.securitycraft.gui.components;

import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import net.geforcemods.securitycraft.containers.ContainerStateSelectorAccess;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class StateSelector extends GuiScreen implements IContainerListener {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/state_selector.png");
	private static final int PAGE_LENGTH = 5;
	private static final float ROTATION_SENSITIVITY = 0.1F;
	private static final Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
	private static final Vector3f Y_DRAG_ROTATION_VECTOR = new Vector3f((float) (1.0D / Math.sqrt(2)), 0, (float) (1.0D / Math.sqrt(2)));
	private static final Quaternion DEFAULT_ROTATION = ClientUtils.fromXYZDegrees(new Vector3f(15.0F, -135.0F, 0.0F));
	private static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer(16);
	private final ContainerStateSelectorAccess menu;
	private final int xStart, yStart, slotToCheck;
	private final float previewXTranslation, previewYTranslation;
	private final HoverChecker dragHoverChecker;
	private final List<Rectangle> extraAreas = new ArrayList<>();
	private IBlockState state = Blocks.AIR.getDefaultState();
	private List<IProperty<?>> properties = new ArrayList<>();
	private TileEntity te = null;
	@SuppressWarnings("rawtypes")
	private TileEntitySpecialRenderer beRenderer = null;
	private List<BlockStatePropertyButton<?>> propertyButtons = new ArrayList<>();
	private int page, amountOfPages;
	private GuiButton previousPageButton, nextPageButton;
	private Matrix4f dragRotation = Utils.make(new Matrix4f(), matrix -> matrix.setIdentity());
	private boolean clickedInDragRegion = false;

	public StateSelector(ContainerStateSelectorAccess menu, int xStart, int yStart, int slotToCheck, int dragStartX, int dragStartY, float previewXTranslation, float previewYTranslation) {
		menu.addListener(this);
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
	public void initGui() {
		mc = Minecraft.getMinecraft();
		fontRenderer = mc.fontRenderer;

		if (menu.getStateStack().getItem() instanceof ItemBlock) {
			ItemBlock blockItem = (ItemBlock) menu.getStateStack().getItem();
			IBlockState savedState = menu.getSavedState();
			Block blockToCheck = blockItem.getBlock();

			if (blockToCheck == savedState.getBlock())
				state = savedState;
			else
				state = blockItem.getBlock().getDefaultState();
		}

		previousPageButton = new ClickButton(0, xStart + 69, yStart + 125, 20, 20, "<", button -> turnPage(-1));
		nextPageButton = new ClickButton(1, xStart + 126, yStart + 125, 20, 20, ">", button -> turnPage(1));
		updateButtons(true, false);
		extraAreas.add(new Rectangle(xStart, 0, 193, new ScaledResolution(mc).getScaledHeight()));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(xStart, yStart, 0, 0, 193, 150);
		super.drawScreen(mouseX, mouseY, partialTick);
		previousPageButton.drawButton(mc, mouseX, mouseY, partialTick);
		nextPageButton.drawButton(mc, mouseX, mouseY, partialTick);
		GlStateManager.pushMatrix();
		GlStateManager.translate(previewXTranslation, previewYTranslation, 0);
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(DEFAULT_ROTATION);
		BUF_FLOAT_16.clear();
		dragRotation.store(BUF_FLOAT_16);
		BUF_FLOAT_16.rewind();
		GlStateManager.multMatrix(BUF_FLOAT_16);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		renderBlockModel(state);

		if (beRenderer != null)
			beRenderer.render(te, 0.0D, 0.0D, 0.0D, partialTick, -1, 0.0F);

		GlStateManager.popMatrix();

		for (int i = 0; i < propertyButtons.size(); i++) {
			String propertyName = propertyButtons.get(i).getProperty().getName();

			fontRenderer.drawString(propertyName, xStart + 91 - fontRenderer.getStringWidth(propertyName) - 2, yStart + i * 23 + 10, 0x404040);
		}

		fontRenderer.drawString(page + "/" + amountOfPages, xStart + 100, yStart + 130, 0x404040);
	}

	public void updateButtons(boolean updateInfo, boolean slotChanged) {
		if (updateInfo) {
			properties = new ArrayList<>(state.getPropertyKeys());
			amountOfPages = (int) Math.ceil(properties.size() / (float) PAGE_LENGTH);
			page = amountOfPages == 0 ? 0 : 1;
			updateBlockEntityInfo(true);
			dragRotation.setIdentity();
		}

		int buttonY = 0;
		int pageStartIndex = (page - 1) * PAGE_LENGTH;
		int i = 0;
		int buttonId = 2;

		buttonList.removeIf(propertyButtons::contains);
		propertyButtons.clear();

		for (IProperty<?> property : properties) {
			if (i < pageStartIndex) {
				i++;
				continue;
			}
			else if (i >= pageStartIndex + PAGE_LENGTH)
				break;

			Collection<?> values = property.getAllowedValues();
			Object defaultValue = state.getValue(property);
			int defaultValueIndex = 0;

			for (Object o : values) {
				if (o == defaultValue)
					break;

				defaultValueIndex++;
			}

			propertyButtons.add(addButton(new BlockStatePropertyButton<>(buttonId++, xStart + 91, yStart + buttonY + 5, 100, 20, defaultValueIndex, property)));
			buttonY += 23;
			i++;
		}

		if (previousPageButton != null)
			previousPageButton.enabled = page > 1;

		if (nextPageButton != null)
			nextPageButton.enabled = page != amountOfPages;
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
			te = null;
			beRenderer = null;
		}

		if (state.getBlock().hasTileEntity(state)) {
			if (te == null) {
				Minecraft mc = Minecraft.getMinecraft();

				te = state.getBlock().createTileEntity(mc.world, state);
				te.blockType = state.getBlock();
				te.setWorld(mc.world);
				beRenderer = TileEntityRendererDispatcher.instance.getRenderer(te);
			}

			te.blockMetadata = state.getBlock().getMetaFromState(state);
		}
	}

	public void renderBlockModel(IBlockState state) {
		if (state.getRenderType() == EnumBlockRenderType.MODEL) {
			BlockRendererDispatcher blockRenderer = mc.getBlockRendererDispatcher();
			IBakedModel blockModel = blockRenderer.getModelForState(state);
			//			int color = mc.getBlockColors().getColor(state, null, null);
			//			float r = (color >> 16 & 255) / 255.0F;
			//			float g = (color >> 8 & 255) / 255.0F;
			//			float b = (color & 255) / 255.0F;

			blockRenderer.getBlockModelRenderer().renderModel(mc.world, blockModel, state, BlockPos.ORIGIN, Tessellator.getInstance().getBuffer(), false);
		}
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
		if (button == 0 && clickedInDragRegion) {
			double dragX = Mouse.getDX();
			double dragY = Mouse.getDY();

			dragRotation.transpose();
			dragRotation.rotate((float) dragX * ROTATION_SENSITIVITY, YN);
			dragRotation.rotate((float) dragY * ROTATION_SENSITIVITY, Y_DRAG_ROTATION_VECTOR);
			dragRotation.transpose();
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		for (int i = 0; i < propertyButtons.size(); i++) {
			if (propertyButtons.get(i).onPress())
				break;
		}

		previousPageButton.mousePressed(mc, mouseX, mouseY);
		nextPageButton.mousePressed(mc, mouseX, mouseY);
		clickedInDragRegion = dragHoverChecker.checkHover(mouseX, mouseY);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		clickedInDragRegion = false;
	}

	@Override
	public void sendSlotContents(Container container, int slotIndex, ItemStack newStack) {
		if (slotIndex == slotToCheck) {
			if (newStack.getItem() instanceof ItemBlock && (state == null || ((ItemBlock) newStack.getItem()).getBlock() != state.getBlock()))
				state = ((ItemBlock) newStack.getItem()).getBlock().getDefaultState();
			else
				state = Blocks.AIR.getDefaultState();

			updateButtons(true, true);

			if (this.menu != null)
				this.menu.onStateChange(state);
		}
	}

	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {}

	@Override
	public void sendWindowProperty(Container container, int varToUpdate, int newValue) {}

	@Override
	public void sendAllWindowProperties(Container container, IInventory inventory) {}

	public IBlockState getState() {
		return state;
	}

	public List<Rectangle> getExtraAreas() {
		return extraAreas;
	}

	public class BlockStatePropertyButton<T extends Comparable<T>> extends GuiButtonExt {
		private int currentIndex = 0;
		private final int toggleCount;
		private final IProperty<T> property;
		private T value;

		public BlockStatePropertyButton(int id, int xPos, int yPos, int width, int height, int initialValue, IProperty<T> property) {
			super(id, xPos, yPos, width, height, "");
			this.property = property;
			currentIndex = initialValue;
			toggleCount = property.getAllowedValues().size();
			onValueChange();
		}

		public void onValueChange() {
			if (property != null) {
				Collection<T> values = property.getAllowedValues();
				int i = 0;

				for (T t : values) {
					if (i++ == currentIndex) {
						value = t;
						break;
					}
				}

				displayString = property.getName(value);
			}
		}

		public boolean onPress() {
			if (hovered) {
				currentIndex = Math.floorMod(currentIndex + 1, toggleCount);
				onValueChange();
				state = state.withProperty(property, value);
				updateBlockEntityInfo(false);
				menu.onStateChange(state);
			}

			return hovered;
		}

		public IProperty<T> getProperty() {
			return property;
		}

		public T getValue() {
			return value;
		}
	}
}