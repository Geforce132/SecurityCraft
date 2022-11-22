package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.screen.components.CollapsibleTextList;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.IToggleableButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
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
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ScrollPanel;

public class BlockChangeDetectorScreen extends AbstractContainerScreen<BlockChangeDetectorMenu> implements ContainerListener, IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_change_detector.png");
	private static final Component CLEAR = Utils.localize("gui.securitycraft:editModule.clear");
	private static final Component BLOCK_NAME = Utils.localize(SCContent.BLOCK_CHANGE_DETECTOR.get().getDescriptionId());
	private BlockChangeDetectorBlockEntity be;
	private ChangeEntryList changeEntryList;
	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[5];
	private TextHoverChecker smartModuleHoverChecker;
	private ModeButton modeButton;
	private Checkbox showAllCheckbox;
	private Checkbox highlightInWorldCheckbox;
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

		Button clearButton = addRenderableWidget(new ExtendedButton(leftPos + 4, topPos + 4, 8, 8, Component.literal("x"), b -> {
			changeEntryList.allEntries.forEach(this::removeWidget);
			changeEntryList.allEntries.clear();
			changeEntryList.filteredEntries.clear();
			be.getEntries().clear();
			be.setChanged();
			SecurityCraft.channel.sendToServer(new ClearChangeDetectorServer(be.getBlockPos()));
		}));
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		boolean isOwner = be.getOwner().isOwner(minecraft.player);
		int settingsX = leftPos + 173;
		Button colorChooserButton;

		addRenderableWidget(modeButton = new ModeButton(settingsX, topPos + 19, 20, 20, be.getMode().ordinal(), DetectionMode.values().length, b -> {
			be.setMode(DetectionMode.values()[((ModeButton) b).getCurrentIndex()]);
			changeEntryList.updateFilteredEntries();
			be.updateFilteredEntries();
		}));
		addRenderableWidget(showAllCheckbox = new Checkbox(settingsX, topPos + 65, 20, 20, Component.empty(), false, false) {
			@Override
			public void onPress() {
				super.onPress();
				changeEntryList.updateFilteredEntries();
			}
		});
		addRenderableWidget(highlightInWorldCheckbox = new Checkbox(settingsX, topPos + 90, 20, 20, Component.empty(), be.isShowingHighlights(), false) {
			@Override
			public void onPress() {
				super.onPress();
				be.showHighlights(selected());
			}
		});
		addRenderableWidget(colorChooser = new ColorChooser(Component.empty(), settingsX, topPos + 135, previousColor) {
			@Override
			public void onColorChange() {
				be.setColor(getRGBColor());
			}
		});
		colorChooser.init(minecraft, width, height);
		addRenderableWidget(colorChooserButton = new ColorChooserButton(settingsX, topPos + 115, 20, 20, colorChooser));

		hoverCheckers[0] = new TextHoverChecker(clearButton, CLEAR);
		hoverCheckers[1] = new TextHoverChecker(modeButton, Arrays.stream(DetectionMode.values()).map(e -> Utils.localize(e.getDescriptionId())).toList());
		hoverCheckers[2] = new TextHoverChecker(showAllCheckbox, Utils.localize("gui.securitycraft:block_change_detector.show_all_checkbox"));
		hoverCheckers[3] = new TextHoverChecker(highlightInWorldCheckbox, Utils.localize("gui.securitycraft:block_change_detector.highlight_in_world_checkbox"));
		hoverCheckers[4] = new TextHoverChecker(colorChooserButton, Utils.localize("gui.securitycraft:choose_outline_color_tooltip"));
		smartModuleHoverChecker = isOwner ? new TextHoverChecker(topPos + 44, topPos + 60, settingsX + 1, leftPos + 191, Utils.localize("gui.securitycraft:block_change_detector.smart_module_hint")) : null;
		addRenderableWidget(changeEntryList = new ChangeEntryList(minecraft, 160, 150, topPos + 20, leftPos + 8));
		clearButton.active = modeButton.active = colorChooserButton.active = isOwner;

		for (ChangeEntry entry : be.getEntries()) {
			String stateString;

			if (entry.state().getProperties().size() > 0)
				stateString = "[" + entry.state().toString().split("\\[")[1].replace(",", ", ");
			else
				stateString = "";

			List<Component> list = List.of(
			//@formatter:off
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
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, BLOCK_NAME, imageWidth / 2 - font.width(BLOCK_NAME) / 2, 6, 0x404040);
		renderTooltip(pose, mouseX - leftPos, mouseY - topPos);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		super.render(pose, mouseX, mouseY, partialTick);

		for (TextHoverChecker hoverChecker : hoverCheckers) {
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY)) {
				renderTooltip(pose, hoverChecker.getName(), mouseX, mouseY);
				break;
			}
		}

		if (smartModuleHoverChecker != null && smartModuleHoverChecker.checkHover(mouseX, mouseY) && !be.isModuleEnabled(ModuleType.SMART))
			renderComponentTooltip(pose, smartModuleHoverChecker.getLines(), mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void containerTick() {
		if (colorChooser != null)
			colorChooser.tick();
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
		if (colorChooser != null)
			colorChooser.keyPressed(keyCode, scanCode, modifiers);

		if (!colorChooser.rgbHexBox.isFocused())
			return super.keyPressed(keyCode, scanCode, modifiers);

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
			SecurityCraft.channel.sendToServer(new SyncBlockChangeDetector(be.getBlockPos(), currentMode, isShowingHighlights, currentColor));

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
		private final int slotHeight = 12;
		private List<ContentSavingCollapsileTextList> allEntries = new ArrayList<>();
		private List<ContentSavingCollapsileTextList> filteredEntries = new ArrayList<>();
		private ContentSavingCollapsileTextList currentlyOpen = null;
		private int contentHeight = 0;

		public ChangeEntryList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left, 4, 6, 0x00000000, 0x00000000);
		}

		@Override
		protected int getContentHeight() {
			return contentHeight;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			int height = 0;

			for (int i = 0; i < filteredEntries.size(); i++) {
				ContentSavingCollapsileTextList entry = filteredEntries.get(i);

				entry.y = top + height - (int) scrollDistance;
				entry.visible = entry.y + entry.getHeight() > top && entry.y < bottom;
				height += entry.getHeight();
			}

			applyScrollLimits();
			super.render(pose, mouseX, mouseY, partialTicks);

			for (int i = 0; i < filteredEntries.size(); i++) {
				filteredEntries.get(i).renderLongMessageTooltip(pose);
			}
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			for (int i = 0; i < filteredEntries.size(); i++) {
				filteredEntries.get(i).render(pose, mouseX, mouseY, 0.0F);
			}
		}

		public void addEntry(ContentSavingCollapsileTextList entry) {
			entry.setWidth(154);
			entry.setHeight(slotHeight);
			entry.x = left;
			entry.setY(top + slotHeight * allEntries.size());
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
			int height = filteredEntries.stream().reduce(0, (accumulated, ctl) -> accumulated + ctl.getHeight(), (identity, accumulated) -> identity + accumulated);

			if (height < bottom - top - 8)
				height = bottom - top - 8;

			contentHeight = height;

			if (currentlyOpen != null)
				scrollDistance = slotHeight * filteredEntries.indexOf(currentlyOpen);

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
		public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
			if (getContentHeight() < height)
				return false;

			return super.mouseScrolled(mouseX, mouseY, scroll);
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}

	class ModeButton extends ExtendedButton implements IToggleableButton {
		private final ItemStack ironPickaxe = new ItemStack(Items.IRON_PICKAXE);
		private final ItemStack grassBlock = new ItemStack(Blocks.GRASS_BLOCK);
		private final int toggleCount;
		private int currentIndex = 0;

		public ModeButton(int xPos, int yPos, int width, int height, int initialIndex, int toggleCount, OnPress onPress) {
			super(xPos, yPos, width, height, Component.empty(), onPress);
			this.toggleCount = toggleCount;
			currentIndex = initialIndex;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
			super.render(pose, mouseX, mouseY, partialTick);

			if (currentIndex == DetectionMode.BREAK.ordinal())
				minecraft.getItemRenderer().renderAndDecorateItem(ironPickaxe, x + 2, y + 2);
			else if (currentIndex == DetectionMode.PLACE.ordinal())
				minecraft.getItemRenderer().renderAndDecorateItem(grassBlock, x + 2, y + 2);
			else if (currentIndex == DetectionMode.BOTH.ordinal()) {
				minecraft.getItemRenderer().renderAndDecorateItem(grassBlock, x + 2, y + 2, 0, -100);
				minecraft.getItemRenderer().renderAndDecorateItem(ironPickaxe, x + 2, y + 2);
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
		public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
			setCurrentIndex(currentIndex - (int) Math.signum(delta));
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
