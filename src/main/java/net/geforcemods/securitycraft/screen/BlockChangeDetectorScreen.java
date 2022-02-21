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
import net.geforcemods.securitycraft.inventory.GenericBEMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.screen.components.CollapsibleTextList;
import net.geforcemods.securitycraft.screen.components.IToggleableButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class BlockChangeDetectorScreen extends AbstractContainerScreen<GenericBEMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_change_detector.png");
	private static final TranslatableComponent CLEAR = Utils.localize("gui.securitycraft:editModule.clear");
	private static final TranslatableComponent BLOCK_NAME = Utils.localize(SCContent.BLOCK_CHANGE_DETECTOR.get().getDescriptionId());
	private BlockChangeDetectorBlockEntity be;
	private ChangeEntryList changeEntryList;
	private TextHoverChecker[] hoverCheckers = new TextHoverChecker[2];
	private TextHoverChecker smartModuleHoverChecker;
	private ModeButton modeButton;
	private DetectionMode currentMode;

	public BlockChangeDetectorScreen(GenericBEMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		be = (BlockChangeDetectorBlockEntity) menu.be;
		imageWidth = 200;
		imageHeight = 256;
	}

	@Override
	protected void init() {
		super.init();

		Button clearButton = addRenderableWidget(new ExtendedButton(leftPos + 4, topPos + 4, 8, 8, new TextComponent("x"), b -> {
			changeEntryList.allEntries.forEach(this::removeWidget);
			changeEntryList.allEntries.clear();
			changeEntryList.modeFilteredEntries.clear();
			be.getEntries().clear();
			be.setChanged();
			SecurityCraft.channel.sendToServer(new ClearChangeDetectorServer(be.getBlockPos()));
		}));
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());

		clearButton.active = be.getOwner().isOwner(minecraft.player);
		addRenderableWidget(changeEntryList = new ChangeEntryList(minecraft, 160, 150, topPos + 20, leftPos + 8));
		currentMode = be.getMode();
		addRenderableWidget(modeButton = new ModeButton(leftPos + 173, topPos + 19, 20, 20, currentMode.ordinal(), DetectionMode.values().length, b -> {
			currentMode = DetectionMode.values()[((ModeButton) b).getCurrentIndex()];
			changeEntryList.updateFilteredEntries();
		}));
		hoverCheckers[0] = new TextHoverChecker(clearButton, CLEAR);
		hoverCheckers[1] = new TextHoverChecker(modeButton, Arrays.stream(DetectionMode.values()).map(e -> (Component) Utils.localize(e.getDescriptionId())).toList());
		smartModuleHoverChecker = new TextHoverChecker(topPos + 44, topPos + 60, leftPos + 174, leftPos + 191, Utils.localize("gui.securitycraft:block_change_detector.smart_module_hint"));

		for (ChangeEntry entry : be.getEntries()) {
			String stateString;

			if (entry.state().getProperties().size() > 0)
				stateString = "[" + entry.state().toString().split("\\[")[1].replace(",", ", ");
			else
				stateString = "[]";

			List<TextComponent> list = List.of(
			//@formatter:off
				entry.player(),
				entry.uuid(),
				dateFormat.format(new Date(entry.timestamp())),
				entry.action(),
				Utils.getFormattedCoordinates(entry.pos()).getString(),
				stateString
			//@formatter:on
			).stream().map(Object::toString).map(TextComponent::new).collect(Collectors.toList());

			changeEntryList.addEntry(addWidget(new ModeSavingCollapsileTextList(0, 0, 154, Utils.localize(entry.state().getBlock().getDescriptionId()), list, b -> changeEntryList.setOpen((ModeSavingCollapsileTextList) b), false, changeEntryList::isHovered, entry.action())));
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, BLOCK_NAME, imageWidth / 2 - font.width(BLOCK_NAME) / 2, 6, 0x404040);

		for (TextHoverChecker hoverChecker : hoverCheckers) {
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY))
				renderTooltip(pose, hoverChecker.getName(), mouseX - leftPos, mouseY - topPos);
		}

		if (smartModuleHoverChecker != null && smartModuleHoverChecker.checkHover(mouseX, mouseY) && !be.hasModule(ModuleType.SMART))
			renderComponentTooltip(pose, smartModuleHoverChecker.getLines(), mouseX - leftPos, mouseY - topPos);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public void onClose() {
		super.onClose();
		sendModeChangeToServer();
	}

	private void sendModeChangeToServer() {
		DetectionMode mode = DetectionMode.values()[modeButton.getCurrentIndex()];

		if (mode != be.getMode()) {
			be.setMode(mode);
			SecurityCraft.channel.sendToServer(new SyncBlockChangeDetector(be.getBlockPos(), mode));
		}
	}

	class ChangeEntryList extends ScrollPanel {
		private final int slotHeight = 12;
		private List<ModeSavingCollapsileTextList> allEntries = new ArrayList<>();
		private List<ModeSavingCollapsileTextList> modeFilteredEntries = new ArrayList<>();
		private ModeSavingCollapsileTextList currentlyOpen = null;
		private boolean recalculateContentHeight = false;
		private int contentHeight = 0;

		public ChangeEntryList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left, 4, 6, 0x00000000, 0x00000000);
		}

		@Override
		protected int getContentHeight() {
			if (recalculateContentHeight) {
				int height = modeFilteredEntries.stream().reduce(0, (accumulated, ctl) -> accumulated + ctl.getHeight(), (identity, accumulated) -> identity + accumulated);

				if (height < bottom - top - 8)
					height = bottom - top - 8;

				contentHeight = height;
				recalculateContentHeight = false;
			}

			return contentHeight;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			super.render(pose, mouseX, mouseY, partialTicks);

			int height = 0;

			for (int i = 0; i < modeFilteredEntries.size(); i++) {
				ModeSavingCollapsileTextList entry = modeFilteredEntries.get(i);

				entry.renderLongMessageTooltip(pose);
				entry.y = top + height - (int) scrollDistance;
				entry.visible = entry.y + entry.getHeight() > top && entry.y < bottom;
				height += entry.getHeight();
			}

			if (getContentHeight() > this.height)
				applyScrollLimits();
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			for (int i = 0; i < modeFilteredEntries.size(); i++) {
				modeFilteredEntries.get(i).render(pose, mouseX, mouseY, 0.0F);
			}
		}

		public void addEntry(ModeSavingCollapsileTextList entry) {
			entry.setWidth(154);
			entry.setHeight(slotHeight);
			entry.x = left;
			entry.setY(top + slotHeight * allEntries.size());
			allEntries.add(entry);

			if (currentMode == DetectionMode.BOTH || currentMode == entry.getMode()) {
				modeFilteredEntries.add(entry);
				recalculateContentHeight();
			}
		}

		public void setOpen(ModeSavingCollapsileTextList newOpenedTextList) {
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

			if (currentlyOpen != null)
				scrollDistance = slotHeight * modeFilteredEntries.indexOf(currentlyOpen);

			recalculateContentHeight();
		}

		public boolean isHovered(int mouseX, int mouseY) {
			return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
		}

		private void applyScrollLimits() {
			int maxScroll = getContentHeight() - (height - border);

			if (maxScroll < 0)
				maxScroll /= 2;

			if (scrollDistance < 0.0F)
				scrollDistance = 0.0F;

			if (scrollDistance > maxScroll)
				scrollDistance = maxScroll;
		}

		public void updateFilteredEntries() {
			modeFilteredEntries = new ArrayList<>();
			modeFilteredEntries.addAll(allEntries.stream().filter(e -> currentMode == DetectionMode.BOTH || currentMode == e.getMode()).toList());
			recalculateContentHeight();
		}

		public void recalculateContentHeight() {
			recalculateContentHeight = true;
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
			super(xPos, yPos, width, height, TextComponent.EMPTY, onPress);
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

	class ModeSavingCollapsileTextList extends CollapsibleTextList {
		private final DetectionMode mode;

		public ModeSavingCollapsileTextList(int xPos, int yPos, int width, Component displayString, List<? extends Component> textLines, OnPress onPress, boolean shouldRenderLongMessageTooltip, BiPredicate<Integer, Integer> extraHoverCheck, DetectionMode mode) {
			super(xPos, yPos, width, displayString, textLines, onPress, shouldRenderLongMessageTooltip, extraHoverCheck);

			this.mode = mode;
		}

		public DetectionMode getMode() {
			return mode;
		}
	}
}
