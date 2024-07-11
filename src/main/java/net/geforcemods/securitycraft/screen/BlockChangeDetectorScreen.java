package net.geforcemods.securitycraft.screen;

import java.awt.Rectangle;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.EnumDetectionMode;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.CollapsibleTextList;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.ColorableScrollPanel;
import net.geforcemods.securitycraft.screen.components.IToggleableButton;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

public class BlockChangeDetectorScreen extends GuiContainer implements IContainerListener, IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_change_detector.png");
	private final String title;
	private BlockChangeDetectorBlockEntity be;
	private ChangeEntryList changeEntryList;
	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[5];
	private StringHoverChecker smartModuleHoverChecker;
	private CallbackCheckbox showAllCheckbox;
	private ColorChooser colorChooser;
	private final EnumDetectionMode previousMode;
	private final boolean wasShowingHighlights;
	private final int previousColor;

	public BlockChangeDetectorScreen(InventoryPlayer inv, BlockChangeDetectorBlockEntity be) {
		super(new BlockChangeDetectorMenu(inv, be));
		this.be = be;
		inventorySlots.addListener(this);
		xSize = 200;
		ySize = 256;
		previousMode = be.getMode();
		wasShowingHighlights = be.isShowingHighlights();
		previousColor = be.getColor();
		title = be.getDisplayName().getFormattedText();
	}

	@Override
	public void initGui() {
		super.initGui();

		GuiButton clearButton = addButton(new ClickButton(0, guiLeft + 4, guiTop + 4, 8, 8, "x", b -> {
			changeEntryList.allEntries.clear();
			changeEntryList.filteredEntries.clear();
			be.getEntries().clear();
			be.markDirty();
			SecurityCraft.network.sendToServer(new ClearChangeDetectorServer(be.getPos()));
		}));
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		boolean isOwner = be.isOwnedBy(mc.player);
		int settingsX = guiLeft + 173;
		GuiButton colorChooserButton;
		ModeButton modeButton;
		CallbackCheckbox highlightInWorldCheckbox;

		modeButton = addButton(new ModeButton(1, settingsX, guiTop + 19, 20, 20, previousMode.ordinal(), EnumDetectionMode.values().length, b -> {
			be.setMode(EnumDetectionMode.values()[((ModeButton) b).getCurrentIndex()]);
			changeEntryList.updateFilteredEntries();
			be.updateFilteredEntries();
		}));
		showAllCheckbox = addButton(new CallbackCheckbox(2, settingsX, guiTop + 65, 20, 20, "", false, isSelected -> changeEntryList.updateFilteredEntries(), 0x404040));
		highlightInWorldCheckbox = addButton(new CallbackCheckbox(3, settingsX, guiTop + 90, 20, 20, "", be.isShowingHighlights(), isSelected -> be.showHighlights(isSelected), 0x404040));
		colorChooser = new ColorChooser(settingsX, guiTop + 135, previousColor, SCContent.blockChangeDetectorFloorCeiling) {
			@Override
			public void onColorChange() {
				be.setColor(getRGBColor());
			}
		};
		colorChooser.setWorldAndResolution(mc, width, height);
		colorChooserButton = addButton(new ColorChooserButton(4, settingsX, guiTop + 115, 20, 20, colorChooser));

		hoverCheckers[0] = new StringHoverChecker(clearButton, Utils.localize("gui.securitycraft:editModule.clear").getFormattedText());
		hoverCheckers[1] = new StringHoverChecker(modeButton, Arrays.stream(EnumDetectionMode.values()).map(e -> Utils.localize(e.getDescriptionId()).getFormattedText()).collect(Collectors.toList()));
		hoverCheckers[2] = new StringHoverChecker(showAllCheckbox, Utils.localize("gui.securitycraft:block_change_detector.show_all_checkbox").getFormattedText());
		hoverCheckers[3] = new StringHoverChecker(highlightInWorldCheckbox, Utils.localize("gui.securitycraft:block_change_detector.highlight_in_world_checkbox").getFormattedText());
		hoverCheckers[4] = new StringHoverChecker(colorChooserButton, Utils.localize("gui.securitycraft:choose_outline_color_tooltip").getFormattedText());
		smartModuleHoverChecker = isOwner ? new StringHoverChecker(guiTop + 44, guiTop + 60, settingsX + 1, guiLeft + 191, Utils.localize("gui.securitycraft:block_change_detector.smart_module_hint").getFormattedText()) : null;
		changeEntryList = new ChangeEntryList(mc, 160, 150, guiTop + 20, guiLeft + 8);
		clearButton.enabled = modeButton.enabled = colorChooserButton.enabled = isOwner;

		for (ChangeEntry entry : be.getEntries()) {
			String stateString;

			if (entry.state.getProperties().size() > 0)
				stateString = "[" + entry.state.toString().split("\\[")[1].replace(",", ", ");
			else
				stateString = "";

			//@formatter:off
			List<ITextComponent> list = Arrays.asList(
				entry.player,
				entry.uuid,
				entry.action,
				Utils.getFormattedCoordinates(entry.pos).getFormattedText(),
				stateString,
				dateFormat.format(new Date(entry.timestamp))
			//@formatter:on
			).stream().map(Object::toString).filter(s -> !s.isEmpty()).map(TextComponentString::new).collect(Collectors.toList());
			Block block = entry.state.getBlock();
			int meta = block.getMetaFromState(entry.state);
			ItemStack stack = new ItemStack(block, 1, meta);
			String blockName;

			if (stack.isEmpty())
				blockName = Utils.localize(block).getFormattedText();
			else
				blockName = Utils.localize(stack.getTranslationKey() + ".name").getFormattedText();

			changeEntryList.addEntry(new ContentSavingCollapsibleTextList(-1, 0, 0, 154, blockName, list, b -> changeEntryList.setOpen((ContentSavingCollapsibleTextList) b), changeEntryList::isHovered, entry));
		}

		changeEntryList.updateFilteredEntries();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for (StringHoverChecker hoverChecker : hoverCheckers) {
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY))
				drawHoveringText(hoverChecker.getName(), mouseX, mouseY);
		}

		if (smartModuleHoverChecker != null && smartModuleHoverChecker.checkHover(mouseX, mouseY) && !be.isModuleEnabled(ModuleType.SMART))
			drawHoveringText(smartModuleHoverChecker.getLines(), mouseX, mouseY);

		if (changeEntryList != null)
			changeEntryList.renderLongMessageTooltips();

		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (changeEntryList != null)
			changeEntryList.drawScreen(mouseX, mouseY);

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

		if (changeEntryList != null)
			changeEntryList.tick();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (changeEntryList != null && changeEntryList.mouseClicked(mouseX, mouseY, button))
			return;

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
	public void onGuiClosed() {
		super.onGuiClosed();

		EnumDetectionMode currentMode = be.getMode();
		boolean isShowingHighlights = be.isShowingHighlights();
		int currentColor = be.getColor();

		if (previousMode != currentMode || wasShowingHighlights != isShowingHighlights || previousColor != currentColor)
			SecurityCraft.network.sendToServer(new SyncBlockChangeDetector(be.getPos(), currentMode, isShowingHighlights, currentColor));

		be.updateFilteredEntries();
	}

	@Override
	public void sendSlotContents(Container container, int slotIndex, ItemStack stack) {
		if (slotIndex == 0 && changeEntryList != null) {
			changeEntryList.updateFilteredEntries();
			be.updateFilteredEntries();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		if (changeEntryList != null)
			changeEntryList.handleMouseInput(Mouse.getEventX() * width / mc.displayWidth, height - Mouse.getEventY() * height / mc.displayHeight - 1);
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> stacks) {}

	@Override
	public void sendWindowProperty(Container container, int varToUpdate, int newValue) {}

	@Override
	public void sendAllWindowProperties(Container container, IInventory inventory) {}

	@Override
	public List<Rectangle> getGuiExtraAreas() {
		if (colorChooser != null)
			return colorChooser.getGuiExtraAreas();
		else
			return new ArrayList<>();
	}

	class ChangeEntryList extends ColorableScrollPanel {
		private List<ContentSavingCollapsibleTextList> allEntries = new ArrayList<>();
		private List<ContentSavingCollapsibleTextList> filteredEntries = new ArrayList<>();
		private ContentSavingCollapsibleTextList currentlyOpen = null;
		private int contentHeight = 0;

		public ChangeEntryList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, top + height, left, 12, new Color(0x00, 0x00, 0x00, 0x00), new Color(0x00, 0x00, 0x00, 0x00));
		}

		@Override
		public int getContentHeight() {
			return contentHeight;
		}

		@Override
		public void drawScreen(int mouseX, int mouseY) {
			int height = 0;

			for (int i = 0; i < filteredEntries.size(); i++) {
				ContentSavingCollapsibleTextList entry = filteredEntries.get(i);

				entry.y = top + height - (int) scrollDistance;
				entry.visible = entry.y + entry.getHeight() > top && entry.y < bottom;
				height += entry.getHeight();
			}

			applyScrollLimits();
			super.drawScreen(mouseX, mouseY);
		}

		@Override
		public void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY) {
			for (int i = 0; i < filteredEntries.size(); i++) {
				filteredEntries.get(i).drawButton(mc, mouseX, mouseY, 0.0F);
			}
		}

		public void renderLongMessageTooltips() {
			for (int i = 0; i < filteredEntries.size(); i++) {
				filteredEntries.get(i).renderLongMessageTooltip();
			}
		}

		public void tick() {
			filteredEntries.forEach(CollapsibleTextList::tick);
		}

		public void addEntry(ContentSavingCollapsibleTextList entry) {
			entry.setWidth(154);
			entry.setHeight(slotHeight);
			entry.x = left;
			entry.setY(top + slotHeight * allEntries.size());
			allEntries.add(entry);
		}

		public void setOpen(ContentSavingCollapsibleTextList newOpenedTextList) {
			if (currentlyOpen == null)
				currentlyOpen = newOpenedTextList;
			else {
				if (currentlyOpen == newOpenedTextList)
					currentlyOpen = null;
				else {
					currentlyOpen.switchOpenStatus();
					currentlyOpen = newOpenedTextList;
				}
			}

			recalculateContentHeight();
		}

		public boolean isHovered(int mouseX, int mouseY) {
			return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
		}

		public void updateFilteredEntries() {
			allEntries.forEach(e -> e.enabled = false);

			if (!showAllCheckbox.selected())
				filteredEntries = new ArrayList<>(allEntries.stream().filter(e -> be.isEntryShown(e.getChangeEntry())).collect(Collectors.toList()));
			else
				filteredEntries = new ArrayList<>(allEntries);

			filteredEntries.forEach(e -> e.enabled = true);
			recalculateContentHeight();
		}

		public void recalculateContentHeight() {
			int height = filteredEntries.stream().reduce(0, (accumulated, ctl) -> accumulated + ctl.getMaximumHeight(), (identity, accumulated) -> identity + accumulated);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			contentHeight = height;

			if (currentlyOpen != null)
				scrollDistance = slotHeight * filteredEntries.indexOf(currentlyOpen);

			applyScrollLimits();
		}

		public boolean mouseClicked(int mouseX, int mouseY, int button) {
			if (button == 0) {
				for (GuiButton entry : filteredEntries) {
					if (entry.mousePressed(mc, mouseX, mouseY)) {
						GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(BlockChangeDetectorScreen.this, entry, buttonList);

						if (MinecraftForge.EVENT_BUS.post(event))
							break;

						entry = event.getButton();
						entry.playPressSound(mc.getSoundHandler());
						actionPerformed(entry);

						if (BlockChangeDetectorScreen.this.equals(mc.currentScreen))
							MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(BlockChangeDetectorScreen.this, entry, buttonList));
					}
				}
			}

			return !(mouseY < top || mouseY > bottom || mouseX < right - SCROLL_BAR_WIDTH || mouseX > right);
		}

		@Override
		public int getSize() {
			return 0;
		}
	}

	class ModeButton extends ClickButton implements IToggleableButton {
		private final ItemStack ironPickaxe = new ItemStack(Items.IRON_PICKAXE);
		private final ItemStack grassBlock = new ItemStack(Blocks.GRASS);
		private final int toggleCount;
		private int currentIndex = 0;

		public ModeButton(int id, int xPos, int yPos, int width, int height, int initialIndex, int toggleCount, Consumer<ClickButton> onPress) {
			super(id, xPos, yPos, width, height, "", onPress);
			this.toggleCount = toggleCount;
			currentIndex = initialIndex;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
			super.drawButton(mc, mouseX, mouseY, partial);

			if (currentIndex == EnumDetectionMode.BREAK.ordinal())
				GuiUtils.drawItemStackToGui(ironPickaxe, x + 2, y + 2, false);
			else if (currentIndex == EnumDetectionMode.PLACE.ordinal())
				GuiUtils.drawItemStackToGui(grassBlock, x + 2, y + 2, true);
			else if (currentIndex == EnumDetectionMode.BOTH.ordinal()) {
				//changing zLevel so the grass block is rendered completely behind the pickaxe
				float blitOffset = mc.getRenderItem().zLevel;

				mc.getRenderItem().zLevel = -50.0F;
				GuiUtils.drawItemStackToGui(grassBlock, x + 2, y + 2, true);
				mc.getRenderItem().zLevel = blitOffset;
				GuiUtils.drawItemStackToGui(ironPickaxe, x + 2, y + 2, false);
			}
		}

		@Override
		public void onClick() {
			setCurrentIndex(currentIndex + 1);
			super.onClick();
		}

		@Override
		public int getCurrentIndex() {
			return currentIndex;
		}

		@Override
		public void setCurrentIndex(int newIndex) {
			currentIndex = Math.floorMod(newIndex, toggleCount);
		}
	}

	class ContentSavingCollapsibleTextList extends CollapsibleTextList {
		private final ChangeEntry changeEntry;

		public ContentSavingCollapsibleTextList(int id, int xPos, int yPos, int width, String displayString, List<? extends ITextComponent> textLines, Consumer<CollapsibleTextList> onPress, BiPredicate<Integer, Integer> extraHoverCheck, ChangeEntry changeEntry) {
			super(id, xPos, yPos, width, displayString, textLines, onPress, extraHoverCheck);

			this.changeEntry = changeEntry;
		}

		public ChangeEntry getChangeEntry() {
			return changeEntry;
		}
	}
}
