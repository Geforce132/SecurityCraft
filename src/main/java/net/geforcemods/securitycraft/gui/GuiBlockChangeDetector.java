package net.geforcemods.securitycraft.gui;

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

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerBlockChangeDetector;
import net.geforcemods.securitycraft.gui.components.ClickButton;
import net.geforcemods.securitycraft.gui.components.CollapsibleTextList;
import net.geforcemods.securitycraft.gui.components.ColorableScrollPanel;
import net.geforcemods.securitycraft.gui.components.IToggleableButton;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector.ChangeEntry;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector.EnumDetectionMode;
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
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiBlockChangeDetector extends GuiContainer implements IContainerListener {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_change_detector.png");
	private final String blockName = Utils.localize(SCContent.blockChangeDetector.getTranslationKey()).getFormattedText();
	private TileEntityBlockChangeDetector be;
	private ChangeEntryList changeEntryList;
	private StringHoverChecker[] hoverCheckers = new StringHoverChecker[3];
	private StringHoverChecker smartModuleHoverChecker;
	private ModeButton modeButton;
	private GuiCheckBox showAllCheckbox;
	private EnumDetectionMode currentMode;

	public GuiBlockChangeDetector(InventoryPlayer inv, TileEntityBlockChangeDetector te) {
		super(new ContainerBlockChangeDetector(inv, te));
		this.be = te;
		inventorySlots.addListener(this);
		xSize = 200;
		ySize = 256;
	}

	@Override
	public void initGui() {
		super.initGui();

		GuiButton clearButton = addButton(new ClickButton(0, guiLeft + 4, guiTop + 4, 8, 8, "x", b -> {
			changeEntryList.allEntries.forEach(e -> buttonList.removeIf(e::equals));
			changeEntryList.allEntries.clear();
			changeEntryList.filteredEntries.clear();
			be.getEntries().clear();
			be.markDirty();
			SecurityCraft.network.sendToServer(new ClearChangeDetectorServer(be.getPos()));
		}));
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		boolean isOwner = be.getOwner().isOwner(mc.player);

		currentMode = be.getMode();
		addButton(modeButton = new ModeButton(1, guiLeft + 173, guiTop + 19, 20, 20, currentMode.ordinal(), EnumDetectionMode.values().length, b -> {
			currentMode = EnumDetectionMode.values()[((ModeButton) b).getCurrentIndex()];
			changeEntryList.updateFilteredEntries();
		}));
		addButton(showAllCheckbox = new GuiCheckBox(2, guiLeft + 173, guiTop + 65, "", false) {
			@Override
			public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
				boolean returnValue = super.mousePressed(mc, mouseX, mouseY);

				if (returnValue)
					changeEntryList.updateFilteredEntries();

				return returnValue;
			}
		});
		hoverCheckers[0] = new StringHoverChecker(clearButton, Utils.localize("gui.securitycraft:editModule.clear").getFormattedText());
		hoverCheckers[1] = new StringHoverChecker(modeButton, Arrays.stream(EnumDetectionMode.values()).map(e -> Utils.localize(e.getDescriptionId()).getFormattedText()).collect(Collectors.toList()));
		hoverCheckers[2] = new StringHoverChecker(showAllCheckbox, Utils.localize("gui.securitycraft:block_change_detector.show_all_checkbox").getFormattedText());
		smartModuleHoverChecker = isOwner ? new StringHoverChecker(guiTop + 44, guiTop + 60, guiLeft + 174, guiLeft + 191, Utils.localize("gui.securitycraft:block_change_detector.smart_module_hint").getFormattedText()) : null;
		changeEntryList = new ChangeEntryList(mc, 160, 150, guiTop + 20, guiLeft + 8, width, height);
		clearButton.enabled = modeButton.enabled = isOwner;

		for (ChangeEntry entry : be.getEntries()) {
			String stateString;

			if (entry.state.getProperties().size() > 0)
				stateString = "[" + entry.state.toString().split("\\[")[1].replace(",", ", ");
			else
				stateString = "";

			List<ITextComponent> list = Arrays.asList(
			//@formatter:off
					entry.player,
					entry.uuid,
					entry.action,
					Utils.getFormattedCoordinates(entry.pos).getFormattedText(),
					stateString,
					dateFormat.format(new Date(entry.timestamp))
					//@formatter:on
			).stream().map(Object::toString).filter(s -> !s.isEmpty()).map(TextComponentString::new).collect(Collectors.toList());

			changeEntryList.addEntry(new ContentSavingCollapsileTextList(-1, 0, 0, 154, Utils.localize(entry.state.getBlock().getTranslationKey()).getFormattedText(), list, b -> changeEntryList.setOpen((ContentSavingCollapsileTextList) b), changeEntryList::isHovered, entry.action, entry.state.getBlock()));
		}

		ItemStack filteredStack = inventorySlots.getSlot(0).getStack();

		changeEntryList.filteredBlock = filteredStack.isEmpty() ? Blocks.AIR : ((ItemBlock) filteredStack.getItem()).getBlock();
		changeEntryList.updateFilteredEntries();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(blockName, ySize / 2 - fontRenderer.getStringWidth(blockName) / 2, 6, 0x404040);
		renderHoveredToolTip(mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for (StringHoverChecker hoverChecker : hoverCheckers) {
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY))
				drawHoveringText(hoverChecker.getName(), mouseX, mouseY);
		}

		if (smartModuleHoverChecker != null && smartModuleHoverChecker.checkHover(mouseX, mouseY) && !be.hasModule(EnumModuleType.SMART))
			drawHoveringText(smartModuleHoverChecker.getLines(), mouseX, mouseY);

		if (changeEntryList != null)
			changeEntryList.renderLongMessageTooltips();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (changeEntryList != null)
			changeEntryList.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if (changeEntryList != null && !changeEntryList.mouseClicked(mouseX, mouseY, button))
			return;

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		sendModeChangeToServer();
	}

	private void sendModeChangeToServer() {
		EnumDetectionMode mode = EnumDetectionMode.values()[modeButton.getCurrentIndex()];

		if (mode != be.getMode()) {
			be.setMode(mode);
			SecurityCraft.network.sendToServer(new SyncBlockChangeDetector(be.getPos(), mode));
		}
	}

	@Override
	public void sendSlotContents(Container container, int slotIndex, ItemStack stack) {
		if (slotIndex == 0 && changeEntryList != null) {
			if (stack.isEmpty())
				changeEntryList.filteredBlock = Blocks.AIR;
			else
				changeEntryList.filteredBlock = ((ItemBlock) stack.getItem()).getBlock();

			changeEntryList.updateFilteredEntries();
		}
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> stacks) {}

	@Override
	public void sendWindowProperty(Container container, int varToUpdate, int newValue) {}

	@Override
	public void sendAllWindowProperties(Container container, IInventory inventory) {}

	class ChangeEntryList extends ColorableScrollPanel {
		private List<ContentSavingCollapsileTextList> allEntries = new ArrayList<>();
		private List<ContentSavingCollapsileTextList> filteredEntries = new ArrayList<>();
		private ContentSavingCollapsileTextList currentlyOpen = null;
		private int contentHeight = 0;
		private Block filteredBlock = Blocks.AIR;

		public ChangeEntryList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight) {
			super(client, width, height, top, top + height, left, 12, screenWidth, screenHeight, new Color(0x00, 0x00, 0x00, 0x00), new Color(0x00, 0x00, 0x00, 0x00));
		}

		@Override
		public int getContentHeight() {
			return contentHeight;
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			int height = 0;

			for (int i = 0; i < filteredEntries.size(); i++) {
				ContentSavingCollapsileTextList entry = filteredEntries.get(i);

				entry.y = top + height - (int) scrollDistance;
				entry.visible = entry.y + entry.getHeight() > top && entry.y < bottom;
				height += entry.getHeight();
			}

			applyScrollLimits();
			super.drawScreen(mouseX, mouseY, partialTicks);
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

		@Override
		public void applyScrollLimits() {
			int maxScroll = getContentHeight() - (height - 4);

			if (maxScroll < 0)
				maxScroll /= 2;

			if (scrollDistance > maxScroll)
				scrollDistance = maxScroll;

			if (scrollDistance < 0.0F)
				scrollDistance = 0.0F;
		}

		public void updateFilteredEntries() {
			allEntries.forEach(e -> e.enabled = false);

			if (!showAllCheckbox.isChecked()) {
				//@formatter:off
				filteredEntries = new ArrayList<>(allEntries
						.stream()
						.filter(e -> currentMode == EnumDetectionMode.BOTH || currentMode == e.getMode())
						.filter(e -> filteredBlock == Blocks.AIR || filteredBlock == e.getBlock())
						.collect(Collectors.toList()));
				//@formatter:on
			}
			else
				filteredEntries = new ArrayList<>(allEntries);

			filteredEntries.forEach(e -> e.enabled = true);
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

		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (mouseY < top || mouseY > bottom)
				return false;

			return true;
		}

		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			if (getContentHeight() < height)
				return false;

			return true;
		}

		public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
			if (getContentHeight() < height)
				return false;

			return true;
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
				mc.getRenderItem().renderItemAndEffectIntoGUI(ironPickaxe, x + 2, y + 2);
			else if (currentIndex == EnumDetectionMode.PLACE.ordinal())
				mc.getRenderItem().renderItemAndEffectIntoGUI(grassBlock, x + 2, y + 2);
			else if (currentIndex == EnumDetectionMode.BOTH.ordinal()) {
				//changing zLevel so the grass block is rendered completely behind the pickaxe
				float blitOffset = mc.getRenderItem().zLevel;

				mc.getRenderItem().zLevel = -50.0F;
				mc.getRenderItem().renderItemAndEffectIntoGUI(grassBlock, x + 2, y + 2);
				mc.getRenderItem().zLevel = blitOffset;
				mc.getRenderItem().renderItemAndEffectIntoGUI(ironPickaxe, x + 2, y + 2);
			}
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
		private final EnumDetectionMode mode;
		private final Block block;

		public ContentSavingCollapsileTextList(int id, int xPos, int yPos, int width, String displayString, List<? extends ITextComponent> textLines, Consumer<CollapsibleTextList> onPress, BiPredicate<Integer, Integer> extraHoverCheck, EnumDetectionMode mode, Block block) {
			super(id, xPos, yPos, width, displayString, textLines, onPress, extraHoverCheck);

			this.mode = mode;
			this.block = block;
		}

		public EnumDetectionMode getMode() {
			return mode;
		}

		public Block getBlock() {
			return block;
		}
	}
}
