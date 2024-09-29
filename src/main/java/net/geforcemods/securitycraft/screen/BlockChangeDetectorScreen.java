package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.CollapsibleTextList;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.IToggleableButton;
import net.geforcemods.securitycraft.screen.components.SmallButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlockChangeDetectorScreen extends AbstractContainerScreen<BlockChangeDetectorMenu> implements ContainerListener, IHasExtraAreas {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/block_change_detector.png");
	private BlockChangeDetectorBlockEntity be;
	private ChangeEntryList changeEntryList;
	private TextHoverChecker smartModuleHoverChecker;
	private CallbackCheckbox showAllCheckbox;
	private ColorChooser colorChooser;
	private final DetectionMode previousMode;
	private final boolean wasShowingHighlights;
	private final int previousColor;

	public BlockChangeDetectorScreen(BlockChangeDetectorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		menu.addSlotListener(this);
		be = (BlockChangeDetectorBlockEntity) menu.be;
		imageWidth = 200;
		imageHeight = 256;
		previousMode = be.getMode();
		wasShowingHighlights = be.isShowingHighlights();
		previousColor = be.getColor();
	}

	@Override
	protected void init() {
		super.init();

		Button clearButton = addRenderableWidget(SmallButton.createWithX(leftPos + 4, topPos + 4, b -> {
			changeEntryList.allEntries.forEach(this::removeWidget);
			changeEntryList.allEntries.clear();
			changeEntryList.filteredEntries.clear();
			be.getEntries().clear();
			be.setChanged();
			PacketDistributor.sendToServer(new ClearChangeDetectorServer(be.getBlockPos()));
		}));
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		boolean isOwner = be.isOwnedBy(minecraft.player);
		int settingsX = leftPos + 173;
		Button modeButton, colorChooserButton;
		CallbackCheckbox highlightInWorldCheckbox;

		modeButton = addRenderableWidget(new ModeButton(settingsX, topPos + 19, 20, 20, be.getMode().ordinal(), DetectionMode.values().length, b -> {
			be.setMode(DetectionMode.values()[((ModeButton) b).getCurrentIndex()]);
			b.setTooltip(Tooltip.create(Utils.localize(be.getMode().getDescriptionId())));
			changeEntryList.updateFilteredEntries();
			be.updateFilteredEntries();
		}));
		showAllCheckbox = addRenderableWidget(new CallbackCheckbox(settingsX, topPos + 65, 20, 20, Component.empty(), false, isSelected -> changeEntryList.updateFilteredEntries(), 0));
		highlightInWorldCheckbox = addRenderableWidget(new CallbackCheckbox(settingsX, topPos + 90, 20, 20, Component.empty(), be.isShowingHighlights(), be::showHighlights, 0));
		colorChooser = addRenderableWidget(new ColorChooser(Component.empty(), settingsX, topPos + 135, previousColor) {
			@Override
			public void onColorChange() {
				be.setColor(getRGBColor());
			}
		});
		colorChooser.init(minecraft, width, height);
		colorChooserButton = addRenderableWidget(new ColorChooserButton(settingsX, topPos + 115, 20, 20, colorChooser));

		clearButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:editModule.clear")));
		modeButton.setTooltip(Tooltip.create(Utils.localize(be.getMode().getDescriptionId())));
		showAllCheckbox.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:block_change_detector.show_all_checkbox")));
		highlightInWorldCheckbox.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:block_change_detector.highlight_in_world_checkbox")));
		colorChooserButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:choose_outline_color_tooltip")));
		smartModuleHoverChecker = isOwner ? new TextHoverChecker(topPos + 44, topPos + 60, settingsX + 1, leftPos + 191, Utils.localize("gui.securitycraft:block_change_detector.smart_module_hint")) : null;
		changeEntryList = addRenderableWidget(new ChangeEntryList(minecraft, 160, 150, topPos + 20, leftPos + 8));
		clearButton.active = modeButton.active = colorChooserButton.active = isOwner;

		for (ChangeEntry entry : be.getEntries()) {
			String stateString;

			if (!entry.state().getProperties().isEmpty())
				stateString = "[" + entry.state().toString().split("\\[")[1].replace(",", ", ");
			else
				stateString = "";

			//@formatter:off
			List<Component> list = List.of(
					entry.player(),
					entry.uuid(),
					entry.action(),
					Utils.getFormattedCoordinates(entry.pos()).getString(),
					stateString,
					dateFormat.format(new Date(entry.timestamp()))
			//@formatter:on
			).stream().map(Object::toString).filter(s -> !s.isEmpty()).map(Component::literal).collect(Collectors.toList());

			changeEntryList.addEntry(addWidget(new ContentSavingCollapsileTextList(0, 0, 154, Utils.localize(entry.state().getBlock().getDescriptionId()), list, b -> changeEntryList.setOpen((ContentSavingCollapsileTextList) b), changeEntryList::isHovered, entry)));
		}

		changeEntryList.updateFilteredEntries();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040, false);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		if (smartModuleHoverChecker != null && smartModuleHoverChecker.checkHover(mouseX, mouseY) && !be.isModuleEnabled(ModuleType.SMART))
			guiGraphics.renderComponentTooltip(font, smartModuleHoverChecker.getLines(), mouseX, mouseY);

		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void containerTick() {
		if (colorChooser != null)
			colorChooser.tick();

		if (changeEntryList != null)
			changeEntryList.tick();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (changeEntryList != null)
			changeEntryList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (changeEntryList != null)
			changeEntryList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (changeEntryList != null)
			changeEntryList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (changeEntryList != null)
			changeEntryList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		if (colorChooser != null)
			colorChooser.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (colorChooser != null) {
			colorChooser.keyPressed(keyCode, scanCode, modifiers);

			if (!colorChooser.getRgbHexBox().isFocused())
				return super.keyPressed(keyCode, scanCode, modifiers);
		}

		return true;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (colorChooser != null && colorChooser.charTyped(codePoint, modifiers))
			return true;

		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public void onClose() {
		super.onClose();

		DetectionMode currentMode = be.getMode();
		boolean isShowingHighlights = be.isShowingHighlights();
		int currentColor = be.getColor();

		if (previousMode != currentMode || wasShowingHighlights != isShowingHighlights || previousColor != currentColor)
			PacketDistributor.sendToServer(new SyncBlockChangeDetector(be.getBlockPos(), currentMode, isShowingHighlights, currentColor));

		be.updateFilteredEntries();
	}

	@Override
	public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack stack) {
		if (slotIndex == 0 && changeEntryList != null) {
			changeEntryList.updateFilteredEntries();
			be.updateFilteredEntries();
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu menu, int slotIndex, int value) {}

	@Override
	public List<Rect2i> getExtraAreas() {
		if (colorChooser != null)
			return colorChooser.getGuiExtraAreas();
		else
			return List.of();
	}

	class ChangeEntryList extends ScrollPanel {
		private static final int SLOT_HEIGHT = 12;
		private List<ContentSavingCollapsileTextList> allEntries = new ArrayList<>();
		private List<ContentSavingCollapsileTextList> filteredEntries = new ArrayList<>();
		private ContentSavingCollapsileTextList currentlyOpen = null;
		private int contentHeight = 0;

		public ChangeEntryList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left, 4, 6);
		}

		@Override
		protected int getContentHeight() {
			return contentHeight;
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			int height = 0;

			for (int i = 0; i < filteredEntries.size(); i++) {
				ContentSavingCollapsileTextList entry = filteredEntries.get(i);

				entry.setY(top + height - (int) scrollDistance);
				entry.visible = entry.getY() + entry.getHeight() > top && entry.getY() < bottom;
				height += entry.getHeight();
			}

			applyScrollLimits();
			super.render(guiGraphics, mouseX, mouseY, partialTicks);

			for (int i = 0; i < filteredEntries.size(); i++) {
				filteredEntries.get(i).renderLongMessageTooltip(guiGraphics, font);
			}
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			for (int i = 0; i < filteredEntries.size(); i++) {
				filteredEntries.get(i).render(guiGraphics, mouseX, mouseY, 0.0F);
			}
		}

		public void tick() {
			filteredEntries.forEach(CollapsibleTextList::tick);
		}

		public void addEntry(ContentSavingCollapsileTextList entry) {
			entry.setWidth(154);
			entry.setHeight(SLOT_HEIGHT);
			entry.setX(left);
			entry.setY(top + SLOT_HEIGHT * allEntries.size());
			allEntries.add(entry);
		}

		public void setOpen(ContentSavingCollapsileTextList newOpenedTextList) {
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

		private void applyScrollLimits() {
			int maxScroll = getContentHeight() - (height - border);

			if (maxScroll < 0)
				maxScroll /= 2;

			if (scrollDistance > maxScroll)
				scrollDistance = maxScroll;

			if (scrollDistance < 0.0F)
				scrollDistance = 0.0F;
		}

		public void updateFilteredEntries() {
			allEntries.forEach(e -> e.active = false);

			if (!showAllCheckbox.selected()) {
				//@formatter:off
				filteredEntries = new ArrayList<>(allEntries
						.stream()
						.filter(e -> be.isEntryShown(e.getChangeEntry()))
						.toList());
				//@formatter:on
			}
			else
				filteredEntries = new ArrayList<>(allEntries);

			filteredEntries.forEach(e -> e.active = true);
			recalculateContentHeight();
		}

		public void recalculateContentHeight() {
			int height = filteredEntries.stream().reduce(0, (accumulated, ctl) -> accumulated + ctl.getMaximumHeight(), (identity, accumulated) -> identity + accumulated);

			if (height < bottom - top - 8)
				height = bottom - top - 8;

			contentHeight = height;

			if (currentlyOpen != null)
				scrollDistance = SLOT_HEIGHT * filteredEntries.indexOf(currentlyOpen);

			applyScrollLimits();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (mouseY < top || mouseY > bottom)
				return false;

			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			if (getContentHeight() < height)
				return false;

			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
			if (getContentHeight() < height)
				return false;

			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}

	class ModeButton extends Button implements IToggleableButton {
		private final ItemStack ironPickaxe = new ItemStack(Items.IRON_PICKAXE);
		private final ItemStack grassBlock = new ItemStack(Blocks.GRASS_BLOCK);
		private final int toggleCount;
		private int currentIndex = 0;

		public ModeButton(int xPos, int yPos, int width, int height, int initialIndex, int toggleCount, OnPress onPress) {
			super(xPos, yPos, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
			this.toggleCount = toggleCount;
			currentIndex = initialIndex;
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

			if (currentIndex == DetectionMode.BREAK.ordinal())
				guiGraphics.renderItem(ironPickaxe, getX() + 2, getY() + 2);
			else if (currentIndex == DetectionMode.PLACE.ordinal())
				guiGraphics.renderItem(grassBlock, getX() + 2, getY() + 2);
			else if (currentIndex == DetectionMode.BOTH.ordinal()) {
				guiGraphics.renderItem(grassBlock, getX() + 2, getY() + 2, 0, -100);
				guiGraphics.renderItem(ironPickaxe, getX() + 2, getY() + 2);
			}
		}

		@Override
		public void onClick(double mouseX, double mouseY) {
			if (Screen.hasShiftDown())
				setCurrentIndex(currentIndex - 1);
			else
				setCurrentIndex(currentIndex + 1);

			super.onClick(mouseX, mouseY);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
			setCurrentIndex(currentIndex - (int) Math.signum(scrollY));
			onPress.onPress(this);
			return true;
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

	class ContentSavingCollapsileTextList extends CollapsibleTextList {
		private final ChangeEntry changeEntry;

		public ContentSavingCollapsileTextList(int xPos, int yPos, int width, Component displayString, List<? extends Component> textLines, OnPress onPress, BiPredicate<Integer, Integer> extraHoverCheck, ChangeEntry changeEntry) {
			super(xPos, yPos, width, displayString, textLines, onPress, extraHoverCheck);

			this.changeEntry = changeEntry;
		}

		public ChangeEntry getChangeEntry() {
			return changeEntry;
		}
	}
}
