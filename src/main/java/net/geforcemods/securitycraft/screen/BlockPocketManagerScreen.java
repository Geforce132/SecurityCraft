package net.geforcemods.securitycraft.screen;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.Slider;
import net.geforcemods.securitycraft.screen.components.Slider.ISlider;
import net.geforcemods.securitycraft.screen.components.StackHoverChecker;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

public class BlockPocketManagerScreen extends GuiContainer implements ISlider, IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	private static final ResourceLocation TEXTURE_STORAGE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager_storage.png");
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.blockPocketWall);
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 1);
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 2);
	private static final int CHISELED_NEEDED_OVERALL = 8;
	private final String blockPocketManager = Utils.localize(SCContent.blockPocketManager).getFormattedText();
	private final String youNeed = Utils.localize("gui.securitycraft:blockPocketManager.youNeed").getFormattedText();
	private final boolean hasStorageModule;
	private final boolean isOwner;
	private final int[] materialCounts = new int[3];
	private final InventoryPlayer playerInventory;
	private BlockPocketManagerBlockEntity te;
	private int size = 5;
	private GuiButton toggleButton;
	private GuiButton sizeButton;
	private GuiButton assembleButton;
	private GuiButton outlineButton;
	private Slider offsetSlider;
	private StackHoverChecker[] hoverCheckers = new StackHoverChecker[3];
	private StringHoverChecker assembleHoverChecker;
	private StringHoverChecker colorChooserButtonHoverChecker;
	private ColorChooser colorChooser;
	private int wallsNeededOverall = (size - 2) * (size - 2) * 6;
	private int pillarsNeededOverall = (size - 2) * 12 - 1;
	private int wallsStillNeeded;
	private int pillarsStillNeeded;
	private int chiseledStillNeeded;
	private final int previousColor;

	public BlockPocketManagerScreen(InventoryPlayer inventory, BlockPocketManagerBlockEntity te) {
		super(new BlockPocketManagerMenu(inventory, te));

		this.te = te;
		playerInventory = inventory;
		size = te.getSize();
		isOwner = te.isOwnedBy(inventory.player);
		hasStorageModule = te.isModuleEnabled(ModuleType.STORAGE) && isOwner;

		if (hasStorageModule)
			xSize = 256;

		ySize = !hasStorageModule ? 194 : 240;
		previousColor = te.getColor();
	}

	@Override
	public void initGui() {
		super.initGui();

		int guiWidth = hasStorageModule ? 123 : xSize;
		int widgetWidth = hasStorageModule ? 110 : 120;
		int widgetOffset = widgetWidth / 2;
		//@formatter:off
		int[] yOffset = hasStorageModule ? new int[] {-76, -100, -52, -28, -4} : new int[] {-40, -70, 23, 47, 71};
		//@formatter:on
		int outlineY = guiTop + ySize / 2 + yOffset[2];
		GuiButton colorChooserButton;
		int colorChooserButtonX = guiLeft + guiWidth / 2 - widgetOffset + (hasStorageModule ? 0 : widgetWidth + 3);
		int outlineButtonX = colorChooserButtonX + (hasStorageModule ? 23 : -widgetWidth - 3);
		int outlineButtonWidth = widgetWidth - (hasStorageModule ? 23 : 0);
		int colorChooserX = colorChooserButtonX + (hasStorageModule ? -145 : 20);

		toggleButton = addButton(new GuiButton(0, guiLeft + guiWidth / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[0], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager." + (!te.isEnabled() ? "activate" : "deactivate")).getFormattedText()));
		sizeButton = addButton(new GuiButton(1, guiLeft + guiWidth / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[1], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText()));
		outlineButton = addButton(new GuiButton(2, outlineButtonX, outlineY, outlineButtonWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showsOutline() ? "show" : "hide")).getFormattedText()));
		assembleButton = addButton(new GuiButton(3, guiLeft + guiWidth / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[3], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.assemble").getFormattedText()));
		offsetSlider = addButton(new Slider(Utils.localize("gui.securitycraft:projector.offset", te.getAutoBuildOffset()).getFormattedText(), Utils.getLanguageKeyDenotation(SCContent.projector), 4, guiLeft + guiWidth / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[4], widgetWidth, 20, Utils.localize("gui.securitycraft:projector.offset", "").getFormattedText(), (-size + 2) / 2, (size - 2) / 2, te.getAutoBuildOffset(), true, this));
		colorChooser = new ColorChooser(colorChooserX, outlineY, previousColor, SCContent.blockPocketManager) {
			@Override
			public void onColorChange() {
				te.setColor(getRGBColor());
			}
		};
		colorChooser.setWorldAndResolution(mc, this.width, height);
		colorChooserButton = addButton(new ColorChooserButton(5, colorChooserButtonX, outlineY, 20, 20, colorChooser));

		if (!te.isOwnedBy(Minecraft.getMinecraft().player))
			sizeButton.enabled = toggleButton.enabled = assembleButton.enabled = outlineButton.enabled = offsetSlider.enabled = colorChooserButton.enabled = false;
		else {
			updateMaterialInformation(true);
			sizeButton.enabled = offsetSlider.enabled = !te.isEnabled();
		}

		if (!hasStorageModule) {
			hoverCheckers[0] = new StackHoverChecker(BLOCK_POCKET_WALL, guiTop + 93, guiTop + 113, guiLeft + 23, guiLeft + 43);
			hoverCheckers[1] = new StackHoverChecker(REINFORCED_CRYSTAL_QUARTZ_PILLAR, guiTop + 93, guiTop + 113, guiLeft + 75, guiLeft + 95);
			hoverCheckers[2] = new StackHoverChecker(REINFORCED_CHISELED_CRYSTAL_QUARTZ, guiTop + 93, guiTop + 113, guiLeft + 128, guiLeft + 148);
		}
		else {
			hoverCheckers[0] = new StackHoverChecker(BLOCK_POCKET_WALL, guiTop + ySize - 73, guiTop + ySize - 54, guiLeft + 174, guiLeft + 191);
			hoverCheckers[1] = new StackHoverChecker(REINFORCED_CRYSTAL_QUARTZ_PILLAR, guiTop + ySize - 50, guiTop + ySize - 31, guiLeft + 174, guiLeft + 191);
			hoverCheckers[2] = new StackHoverChecker(REINFORCED_CHISELED_CRYSTAL_QUARTZ, guiTop + ySize - 27, guiTop + ySize - 9, guiLeft + 174, guiLeft + 191);
		}

		assembleHoverChecker = new StringHoverChecker(assembleButton, Arrays.asList(Utils.localize("gui.securitycraft:blockPocketManager.needStorageModule").getFormattedText(), Utils.localize("messages.securitycraft:blockpocket.notEnoughItems").getFormattedText()));
		colorChooserButtonHoverChecker = new StringHoverChecker(colorChooserButton, Utils.localize("gui.securitycraft:choose_outline_color_tooltip").getFormattedText());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(blockPocketManager, (hasStorageModule ? 123 : xSize) / 2 - fontRenderer.getStringWidth(blockPocketManager) / 2, 6, 4210752);

		if (!te.isEnabled() && isOwner) {
			if (!hasStorageModule) {
				fontRenderer.drawString(youNeed, xSize / 2 - fontRenderer.getStringWidth(youNeed) / 2, 83, 4210752);

				fontRenderer.drawString(wallsNeededOverall + "", 42, 100, 4210752);
				GuiUtils.drawItemStackToGui(BLOCK_POCKET_WALL, 25, 96, false);

				fontRenderer.drawString(pillarsNeededOverall + "", 94, 100, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96, false);

				fontRenderer.drawString(CHISELED_NEEDED_OVERALL + "", 147, 100, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96, false);
			}
			else {
				fontRenderer.drawString(youNeed, 169 + 87 / 2 - fontRenderer.getStringWidth(youNeed) / 2, ySize - 83, 4210752);

				fontRenderer.drawString(Math.max(0, wallsStillNeeded) + "", 192, ySize - 66, 4210752);
				GuiUtils.drawItemStackToGui(BLOCK_POCKET_WALL, 175, ySize - 70, false);

				fontRenderer.drawString(Math.max(0, pillarsStillNeeded) + "", 192, ySize - 44, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 175, ySize - 48, false);

				fontRenderer.drawString(Math.max(0, chiseledStillNeeded) + "", 192, ySize - 22, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 175, ySize - 26, false);
			}
		}

		if (hasStorageModule)
			fontRenderer.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 94, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (hasStorageModule)
			renderHoveredToolTip(mouseX, mouseY);

		if (!te.isEnabled() && isOwner) {
			for (StackHoverChecker shc : hoverCheckers) {
				if (shc.checkHover(mouseX, mouseY)) {
					renderToolTip(shc.getStack(), mouseX, mouseY);
					return;
				}
			}
		}

		if (!assembleButton.enabled && assembleHoverChecker.checkHover(mouseX, mouseY)) {
			if (!hasStorageModule)
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(assembleHoverChecker.getLines().subList(0, 1), mouseX, mouseY, width, height, -1, fontRenderer);
			else
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(assembleHoverChecker.getLines().subList(1, 2), mouseX, mouseY, width, height, -1, fontRenderer);

			if (colorChooserButtonHoverChecker.checkHover(mouseX, mouseY))
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(colorChooserButtonHoverChecker.getLines(), mouseX, mouseY, width, height, -1, fontRenderer);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(hasStorageModule ? TEXTURE_STORAGE : TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (colorChooser != null)
			colorChooser.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Slot hoveredSlot = getSlotUnderMouse();

		//code copied from super implementation to prevent pressing the inventory key from closing the screen when the rgb hex text field is focused
		if (keyCode == Keyboard.KEY_ESCAPE || (!colorChooser.getRgbHexBox().isFocused() && mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)))
			mc.player.closeScreen();

		checkHotbarKeys(keyCode);

		if (hoveredSlot != null && hoveredSlot.getHasStack()) {
			if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode))
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
			else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode))
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
		}

		if (colorChooser != null)
			colorChooser.keyTyped(typedChar, keyCode);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (colorChooser != null)
			colorChooser.updateScreen();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (colorChooser != null)
			colorChooser.mouseClicked(mouseX, mouseY, button);

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (colorChooser != null)
			colorChooser.mouseReleased(mouseX, mouseY, state);

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
		//the super call needs to be before calculating the stored materials, as it is responsible for putting the stack inside the slot
		super.handleMouseClick(slot, slotId, mouseButton, type);
		//every time items are added/removed, the mouse is clicking a slot and these values are recomputed
		//not the best place, as this code will run when an empty slot is clicked while not holding any item, but it's good enough
		updateMaterialInformation(true);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == toggleButton.id) {
			te.setSize(size);
			te.setEnabled(!te.isEnabled());
			SecurityCraft.network.sendToServer(new ToggleBlockPocketManager(te, te.isEnabled()));
			Minecraft.getMinecraft().player.closeScreen();
		}
		else if (button.id == sizeButton.id) {
			int newOffset;
			int newMin;
			int newMax;

			size += 4;

			if (size > 25)
				size = 5;

			newMin = (-size + 2) / 2;
			newMax = (size - 2) / 2;

			if (te.getAutoBuildOffset() > 0)
				newOffset = Math.min(te.getAutoBuildOffset(), newMax);
			else
				newOffset = Math.max(te.getAutoBuildOffset(), newMin);

			updateMaterialInformation(false);
			te.setSize(size);
			offsetSlider.setMinValue(newMin);
			offsetSlider.setMaxValue(newMax);
			te.setAutoBuildOffset(newOffset);
			offsetSlider.setValue(newOffset);
			offsetSlider.updateSlider();
			button.displayString = Utils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText();
			sync();
		}
		else if (button.id == assembleButton.id) {
			te.setSize(size);
			SecurityCraft.network.sendToServer(new AssembleBlockPocket(te));
			Minecraft.getMinecraft().player.closeScreen();
		}
		else if (button.id == outlineButton.id) {
			te.toggleOutline();
			outlineButton.displayString = Utils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showsOutline() ? "show" : "hide")).getFormattedText();
			sync();
		}
		else if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	private void sync() {
		SecurityCraft.network.sendToServer(new SyncBlockPocketManager(te.getPos(), te.getSize(), te.showsOutline(), te.getAutoBuildOffset(), te.getColor()));
	}

	@Override
	public void onMouseRelease(int id) {
		if (id == offsetSlider.id) {
			te.setAutoBuildOffset(offsetSlider.getValueInt());
			sync();
		}
	}

	private void updateMaterialInformation(boolean recalculateStoredStacks) {
		if (recalculateStoredStacks) {
			materialCounts[0] = materialCounts[1] = materialCounts[2] = 0;

			IItemHandler handler = te.getStorageHandler();

			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);

				if (stack.getItem() instanceof ItemBlock) {
					Block block = ((ItemBlock) stack.getItem()).getBlock();

					if (block == SCContent.blockPocketWall)
						materialCounts[0] += stack.getCount();
					else if (block == SCContent.reinforcedCrystalQuartz && stack.getMetadata() >= 2)
						materialCounts[1] += stack.getCount();
					else if (block == SCContent.reinforcedCrystalQuartz && stack.getMetadata() == 1)
						materialCounts[2] += stack.getCount();
				}
			}
		}

		wallsNeededOverall = (size - 2) * (size - 2) * 6;
		pillarsNeededOverall = (size - 2) * 12 - 1;
		wallsStillNeeded = wallsNeededOverall - materialCounts[0];
		pillarsStillNeeded = pillarsNeededOverall - materialCounts[1];
		chiseledStillNeeded = CHISELED_NEEDED_OVERALL - materialCounts[2];
		//the assemble button should always be active when the player is in creative mode
		assembleButton.enabled = isOwner && (mc.player.isCreative() || (!te.isEnabled() && hasStorageModule && wallsStillNeeded <= 0 && pillarsStillNeeded <= 0 && chiseledStillNeeded <= 0));
	}

	@Override
	public void onChangeSliderValue(Slider slider, String denotation, int id) {
		if (slider.id == offsetSlider.id)
			slider.displayString = slider.getPrefix() + slider.getValueInt();
	}

	@Override
	public List<Rectangle> getGuiExtraAreas() {
		if (colorChooser != null)
			return colorChooser.getGuiExtraAreas();
		else
			return new ArrayList<>();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (previousColor != te.getColor())
			sync();
	}
}
