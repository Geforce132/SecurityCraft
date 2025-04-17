package net.geforcemods.securitycraft.screen.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.inventory.StateSelectorAccessMenu;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class StateSelector extends GuiScreen implements IContainerListener {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/state_selector.png");
	private static final int PAGE_LENGTH = 5;
	private final StateSelectorAccessMenu menu;
	private final int xStart, yStart, slotToCheck;
	private final float previewXTranslation, previewYTranslation;
	private final HoverChecker dragHoverChecker;
	private final List<Rectangle> extraAreas = new ArrayList<>();
	private final RenderAccess renderAccess = new RenderAccess();
	private IBlockState state = Blocks.AIR.getDefaultState();
	private List<IProperty<?>> properties = new ArrayList<>();
	private TileEntity te = null;
	@SuppressWarnings("rawtypes")
	private TileEntitySpecialRenderer teRenderer = null;
	private List<BlockStatePropertyButton<?>> propertyButtons = new ArrayList<>();
	private int page, amountOfPages;
	private GuiButton previousPageButton, nextPageButton;
	private boolean clickedInDragRegion = false;
	private float dragX = -15.0F;
	private float dragY = -135.0F;

	public StateSelector(StateSelectorAccessMenu menu, int xStart, int yStart, int slotToCheck, int dragStartX, int dragStartY) {
		this.menu = menu;
		this.xStart = xStart;
		this.yStart = yStart;
		this.slotToCheck = slotToCheck;
		dragStartX += xStart;
		dragStartY += yStart;
		this.previewXTranslation = dragStartX + 34;
		this.previewYTranslation = dragStartY + 36;
		dragHoverChecker = new HoverChecker(dragStartY, dragStartY + 47, dragStartX, dragStartX + 47);
		menu.addListener(this);
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
		updateButtons(true);
		extraAreas.add(new Rectangle(xStart, 0, 193, new ScaledResolution(mc).getScaledHeight()));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		if (Mouse.isButtonDown(0) && clickedInDragRegion) {
			float dy = Mouse.getDY();
			float dx = Mouse.getDX();

			if (dy != 0)
				dragX += dy;

			if (dx != 0)
				dragY += dx;
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(xStart, yStart, 0, 0, 193, 150);
		super.drawScreen(mouseX, mouseY, partialTick);
		previousPageButton.drawButton(mc, mouseX, mouseY, partialTick);
		nextPageButton.drawButton(mc, mouseX, mouseY, partialTick);
		GlStateManager.pushMatrix();
		GlStateManager.translate(previewXTranslation, previewYTranslation, 512);
		GlStateManager.translate(-12.0F, -12.0F, -12.0F);
		GlStateManager.rotate(dragX, 1, 0, 0);
		GlStateManager.rotate(dragY, 0, 1, 0);
		GlStateManager.translate(12.0F, 12.0F, 12.0F);
		GlStateManager.scale(-26.0F, -26.0F, -26.0F);
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		renderBlockModel(state);

		if (teRenderer != null)
			teRenderer.render(te, 0.0D, 0.0D, 0.0D, partialTick, -1, 1.0F);

		GlStateManager.popMatrix();

		for (int i = 0; i < propertyButtons.size(); i++) {
			String propertyName = propertyButtons.get(i).getProperty().getName();

			fontRenderer.drawString(propertyName, xStart + 91 - fontRenderer.getStringWidth(propertyName) - 2, yStart + i * 23 + 10, 0x404040);
		}

		fontRenderer.drawString(page + "/" + amountOfPages, xStart + 100, yStart + 130, 0x404040);
	}

	public void updateButtons(boolean updateInfo) {
		if (updateInfo) {
			properties = new ArrayList<>(state.getPropertyKeys());
			amountOfPages = (int) Math.ceil(properties.size() / (float) PAGE_LENGTH);
			page = amountOfPages == 0 ? 0 : 1;
			updateBlockEntityInfo(true);
			dragX = -15.0F;
			dragY = -135.0F;
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

		updateButtons(false);
	}

	private void updateBlockEntityInfo(boolean reset) {
		if (reset) {
			te = null;
			teRenderer = null;
		}

		if (state.getBlock().hasTileEntity(state)) {
			if (te == null) {
				Minecraft mc = Minecraft.getMinecraft();

				te = state.getBlock().createTileEntity(mc.world, state);
				te.blockType = state.getBlock();
				te.blockMetadata = state.getBlock().getMetaFromState(state);
				te.setWorld(mc.world);

				if (te instanceof TileEntityBanner)
					((TileEntityBanner) te).setItemValues(menu.getStateStack(), false);
				else
					Utils.updateBlockEntityWithItemTag(te, menu.getStateStack());

				teRenderer = TileEntityRendererDispatcher.instance.getRenderer(te);
			}

			te.blockMetadata = state.getBlock().getMetaFromState(state);
		}
	}

	public void renderBlockModel(IBlockState state) {
		if (state.getRenderType() == EnumBlockRenderType.MODEL) {
			BlockRendererDispatcher blockRenderer = mc.getBlockRendererDispatcher();
			IBakedModel blockModel = blockRenderer.getModelForState(state);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			blockRenderer.getBlockModelRenderer().renderModel(renderAccess, blockModel, state, BlockPos.ORIGIN, buffer, false);
			tessellator.draw();
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			for (int i = 0; i < propertyButtons.size(); i++) {
				BlockStatePropertyButton<?> bspb = propertyButtons.get(i);

				if (bspb.onPress()) {
					bspb.playPressSound(mc.getSoundHandler());
					break;
				}
			}

			if (previousPageButton.mousePressed(mc, mouseX, mouseY)) {
				((ClickButton) previousPageButton).onClick();
				previousPageButton.playPressSound(mc.getSoundHandler());
			}
			else if (nextPageButton.mousePressed(mc, mouseX, mouseY)) {
				((ClickButton) nextPageButton).onClick();
				nextPageButton.playPressSound(mc.getSoundHandler());
			}

			clickedInDragRegion = dragHoverChecker.checkHover(mouseX, mouseY);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		clickedInDragRegion = false;
	}

	@Override
	public void sendSlotContents(Container container, int slotIndex, ItemStack newStack) {
		if (slotIndex == slotToCheck) {
			if (newStack.getItem() instanceof ItemBlock && (state == null || ((ItemBlock) newStack.getItem()).getBlock() != state.getBlock()))
				state = ((ItemBlock) newStack.getItem()).getBlock().getStateFromMeta(newStack.getMetadata());
			else
				state = Blocks.AIR.getDefaultState();

			updateButtons(true);

			if (menu != null)
				menu.onStateChange(state);
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

	public class RenderAccess implements IBlockAccess {
		@Override
		public TileEntity getTileEntity(BlockPos pos) {
			return pos == BlockPos.ORIGIN ? te : null;
		}

		@Override
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return 0;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos) {
			return pos == BlockPos.ORIGIN ? state : Blocks.AIR.getDefaultState();
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return pos != BlockPos.ORIGIN || (state != null && state.getBlock() == Blocks.AIR);
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return Biomes.PLAINS;
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {
			return Minecraft.getMinecraft().world.getWorldType();
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) {
			return pos == BlockPos.ORIGIN ? state.isSideSolid(this, pos, side) : defaultValue;
		}
	}
}