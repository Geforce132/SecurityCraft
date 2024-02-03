package net.geforcemods.securitycraft.screen;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.StackHoverChecker;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.network.PacketDistributor;

public class BlockPocketManagerScreen extends AbstractContainerScreen<BlockPocketManagerMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	private static final ResourceLocation TEXTURE_STORAGE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager_storage.png");
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.BLOCK_POCKET_WALL.get());
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
	private static final int CHISELED_NEEDED_OVERALL = 8;
	private final TranslatableComponent youNeed = Utils.localize("gui.securitycraft:blockPocketManager.youNeed");
	private final boolean storage;
	private final boolean isOwner;
	private final int[] materialCounts = new int[3];
	public final BlockPocketManagerBlockEntity be;
	private int size = 5;
	private final int[] allowedSizes = {
			5, 9, 13, 17, 21, 25
	};
	private Button assembleButton;
	private Button outlineButton;
	private CallbackSlider offsetSlider;
	private StackHoverChecker[] hoverCheckers = new StackHoverChecker[3];
	private TextHoverChecker assembleHoverChecker;
	private TextHoverChecker colorChooserButtonHoverChecker;
	private ColorChooser colorChooser;
	private int wallsNeededOverall = (size - 2) * (size - 2) * 6;
	private int pillarsNeededOverall = (size - 2) * 12 - 1;
	private int wallsStillNeeded;
	private int pillarsStillNeeded;
	private int chiseledStillNeeded;
	private final int previousColor;

	public BlockPocketManagerScreen(BlockPocketManagerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);

		be = menu.be;
		size = be.getSize();
		isOwner = menu.isOwner;
		storage = menu.storage;

		if (storage)
			imageWidth = 256;

		imageHeight = !storage ? 194 : 240;
		previousColor = be.getColor();
	}

	@Override
	public void init() {
		super.init();

		int guiWidth = storage ? 123 : imageWidth;
		int widgetWidth = storage ? 110 : 120;
		int widgetOffset = widgetWidth / 2;
		//@formatter:off
		int[] yOffset = storage ? new int[] {-76, -100, -52, -28, -4} : new int[] {-40, -70, 23, 47, 71};
		//@formatter:on
		int outlineY = topPos + imageHeight / 2 + yOffset[2];
		Button colorChooserButton;
		int colorChooserButtonX = leftPos + guiWidth / 2 - widgetOffset + (storage ? 0 : widgetWidth + 3);
		int outlineButtonX = colorChooserButtonX + (storage ? 23 : -widgetWidth - 3);
		int outlineButtonWidth = widgetWidth - (storage ? 23 : 0);
		int colorChooserX = colorChooserButtonX + (storage ? -145 : 20);
		Button toggleButton, sizeButton;

		toggleButton = addRenderableWidget(new ExtendedButton(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[0], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager." + (!be.isEnabled() ? "activate" : "deactivate")), this::toggleButtonClicked));
		sizeButton = addRenderableWidget(new ToggleComponentButton(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[1], widgetWidth, 20, this::updateSizeButtonText, ArrayUtils.indexOf(allowedSizes, size), allowedSizes.length, this::sizeButtonClicked));
		outlineButton = addRenderableWidget(new ExtendedButton(outlineButtonX, outlineY, outlineButtonWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.outline." + (!be.showsOutline() ? "show" : "hide")), this::outlineButtonClicked));
		assembleButton = addRenderableWidget(new ExtendedButton(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[3], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.assemble"), this::assembleButtonClicked));
		offsetSlider = addRenderableWidget(new CallbackSlider(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[4], widgetWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), TextComponent.EMPTY, (-size + 2) / 2, (size - 2) / 2, be.getAutoBuildOffset(), true, this::offsetSliderReleased));
		colorChooser = addRenderableWidget(new ColorChooser(TextComponent.EMPTY, colorChooserX, outlineY, be.getColor()) {
			@Override
			public void onColorChange() {
				be.setColor(getRGBColor());
			}
		});
		colorChooser.init(minecraft, width, height);
		colorChooserButton = addRenderableWidget(new ColorChooserButton(colorChooserButtonX, outlineY, 20, 20, colorChooser));

		if (!be.isOwnedBy(Minecraft.getInstance().player))
			sizeButton.active = toggleButton.active = assembleButton.active = outlineButton.active = offsetSlider.active = colorChooserButton.active = false;
		else {
			updateMaterialInformation(true);
			sizeButton.active = assembleButton.active = offsetSlider.active = !be.isEnabled();
		}

		if (!storage) {
			hoverCheckers[0] = new StackHoverChecker(BLOCK_POCKET_WALL, topPos + 93, topPos + 113, leftPos + 23, leftPos + 43);
			hoverCheckers[1] = new StackHoverChecker(REINFORCED_CRYSTAL_QUARTZ_PILLAR, topPos + 93, topPos + 113, leftPos + 75, leftPos + 95);
			hoverCheckers[2] = new StackHoverChecker(REINFORCED_CHISELED_CRYSTAL_QUARTZ, topPos + 93, topPos + 113, leftPos + 128, leftPos + 148);
		}
		else {
			hoverCheckers[0] = new StackHoverChecker(BLOCK_POCKET_WALL, topPos + imageHeight - 73, topPos + imageHeight - 54, leftPos + 174, leftPos + 191);
			hoverCheckers[1] = new StackHoverChecker(REINFORCED_CRYSTAL_QUARTZ_PILLAR, topPos + imageHeight - 50, topPos + imageHeight - 31, leftPos + 174, leftPos + 191);
			hoverCheckers[2] = new StackHoverChecker(REINFORCED_CHISELED_CRYSTAL_QUARTZ, topPos + imageHeight - 27, topPos + imageHeight - 9, leftPos + 174, leftPos + 191);
		}

		assembleHoverChecker = new TextHoverChecker(assembleButton, Arrays.asList(Utils.localize("gui.securitycraft:blockPocketManager.needStorageModule"), Utils.localize("messages.securitycraft:blockpocket.notEnoughItems")));
		colorChooserButtonHoverChecker = new TextHoverChecker(colorChooserButton, Utils.localize("gui.securitycraft:choose_outline_color_tooltip"));
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, (storage ? 123 : imageWidth) / 2 - font.width(title) / 2, 6, 4210752);

		if (storage)
			font.draw(pose, playerInventoryTitle, 8, imageHeight - 94, 4210752);

		if (!be.isEnabled() && isOwner) {
			if (!storage) {
				font.draw(pose, youNeed, imageWidth / 2 - font.width(youNeed) / 2, 83, 4210752);

				font.draw(pose, wallsNeededOverall + "", 42, 100, 4210752);
				minecraft.getItemRenderer().renderAndDecorateItem(BLOCK_POCKET_WALL, 25, 96);

				font.draw(pose, pillarsNeededOverall + "", 94, 100, 4210752);
				minecraft.getItemRenderer().renderAndDecorateItem(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96);

				font.draw(pose, CHISELED_NEEDED_OVERALL + "", 147, 100, 4210752);
				minecraft.getItemRenderer().renderAndDecorateItem(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96);
			}
			else {
				font.draw(pose, youNeed, 169 + 87 / 2 - font.width(youNeed) / 2, imageHeight - 83, 4210752);

				font.draw(pose, Math.max(0, wallsStillNeeded) + "", 192, imageHeight - 66, 4210752);
				minecraft.getItemRenderer().renderAndDecorateItem(BLOCK_POCKET_WALL, 175, imageHeight - 70);

				font.draw(pose, Math.max(0, pillarsStillNeeded) + "", 192, imageHeight - 44, 4210752);
				minecraft.getItemRenderer().renderAndDecorateItem(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 175, imageHeight - 48);

				font.draw(pose, Math.max(0, chiseledStillNeeded) + "", 192, imageHeight - 22, 4210752);
				minecraft.getItemRenderer().renderAndDecorateItem(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 175, imageHeight - 26);
			}
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		if (storage)
			renderTooltip(pose, mouseX, mouseY);

		if (!be.isEnabled() && isOwner) {
			for (StackHoverChecker shc : hoverCheckers) {
				if (shc.checkHover(mouseX, mouseY)) {
					renderTooltip(pose, shc.getStack(), mouseX, mouseY);
					break;
				}
			}

			if (!assembleButton.active && assembleHoverChecker.checkHover(mouseX, mouseY)) {
				if (!storage)
					renderComponentTooltip(pose, assembleHoverChecker.getLines().subList(0, 1), mouseX, mouseY);
				else
					renderComponentTooltip(pose, assembleHoverChecker.getLines().subList(1, 2), mouseX, mouseY);
			}

			if (colorChooserButtonHoverChecker.checkHover(mouseX, mouseY))
				renderTooltip(pose, colorChooserButtonHoverChecker.getName(), mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, storage ? TEXTURE_STORAGE : TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	protected void containerTick() {
		if (colorChooser != null)
			colorChooser.tick();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (colorChooser != null)
			colorChooser.mouseDragged(mouseX, mouseY, button, dragX, dragY);

		return getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
		//the super call needs to be before calculating the stored materials, as it is responsible for putting the stack inside the slot
		super.slotClicked(slot, slotId, mouseButton, type);
		//every time items are added/removed, the mouse is clicking a slot and these values are recomputed
		//not the best place, as this code will run when an empty slot is clicked while not holding any item, but it's good enough
		updateMaterialInformation(true);
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		if (colorChooser != null)
			return colorChooser.getGuiExtraAreas();
		else
			return List.of();
	}

	@Override
	public void onClose() {
		super.onClose();

		if (previousColor != be.getColor())
			sync();
	}

	private void updateMaterialInformation(boolean recalculateStoredStacks) {
		if (recalculateStoredStacks) {
			materialCounts[0] = materialCounts[1] = materialCounts[2] = 0;
			be.getStorageHandler().ifPresent(handler -> {
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack stack = handler.getStackInSlot(i);

					if (stack.getItem() instanceof BlockItem blockItem) {
						Block block = blockItem.getBlock();

						if (block == SCContent.BLOCK_POCKET_WALL.get())
							materialCounts[0] += stack.getCount();
						else if (block == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
							materialCounts[1] += stack.getCount();
						else if (block == SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
							materialCounts[2] += stack.getCount();
					}
				}
			});
		}

		wallsNeededOverall = (size - 2) * (size - 2) * 6;
		pillarsNeededOverall = (size - 2) * 12 - 1;
		wallsStillNeeded = wallsNeededOverall - materialCounts[0];
		pillarsStillNeeded = pillarsNeededOverall - materialCounts[1];
		chiseledStillNeeded = CHISELED_NEEDED_OVERALL - materialCounts[2];
		//the assemble button should always be active when the player is in creative mode
		assembleButton.active = isOwner && (minecraft.player.isCreative() || (!be.isEnabled() && storage && wallsStillNeeded <= 0 && pillarsStillNeeded <= 0 && chiseledStillNeeded <= 0));
	}

	public void toggleButtonClicked(Button button) {
		be.setSize(size);
		be.setEnabled(!be.isEnabled());
		SecurityCraft.CHANNEL.send(PacketDistributor.SERVER.noArg(), new ToggleBlockPocketManager(be, be.isEnabled()));
		Minecraft.getInstance().player.closeContainer();
	}

	public void sizeButtonClicked(Button button) {
		int newOffset;
		int newMin;
		int newMax;

		size = allowedSizes[((ToggleComponentButton) button).getCurrentIndex()];
		newMin = (-size + 2) / 2;
		newMax = (size - 2) / 2;

		if (be.getAutoBuildOffset() > 0)
			newOffset = Math.min(be.getAutoBuildOffset(), newMax);
		else
			newOffset = Math.max(be.getAutoBuildOffset(), newMin);

		updateMaterialInformation(false);
		be.setSize(size);
		offsetSlider.setMinValue(newMin);
		offsetSlider.setMaxValue(newMax);
		be.setAutoBuildOffset(newOffset);
		offsetSlider.setValue(newOffset);
		sync();
		((ToggleComponentButton) button).onValueChange();
	}

	public Component updateSizeButtonText(int index) {
		return Utils.localize("gui.securitycraft:blockPocketManager.size", size, size, size);
	}

	public void assembleButtonClicked(Button button) {
		be.setSize(size);
		SecurityCraft.CHANNEL.send(PacketDistributor.SERVER.noArg(), new AssembleBlockPocket(be));
		Minecraft.getInstance().player.closeContainer();
	}

	public void outlineButtonClicked(Button button) {
		be.toggleOutline();
		outlineButton.setMessage(Utils.localize("gui.securitycraft:blockPocketManager.outline." + (!be.showsOutline() ? "show" : "hide")));
		sync();
	}

	public void offsetSliderReleased(CallbackSlider slider) {
		be.setAutoBuildOffset(slider.getValueInt());
		sync();
	}

	private void sync() {
		SecurityCraft.CHANNEL.send(PacketDistributor.SERVER.noArg(), new SyncBlockPocketManager(be.getBlockPos(), be.getSize(), be.showsOutline(), be.getAutoBuildOffset(), be.getColor()));
	}
}
