package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.StackHoverChecker;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.network.PacketDistributor;

public class BlockPocketManagerScreen extends ContainerScreen<BlockPocketManagerMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	private static final ResourceLocation TEXTURE_STORAGE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager_storage.png");
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.BLOCK_POCKET_WALL.get());
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
	private static final int CHISELED_NEEDED_OVERALL = 8;
	private final TranslationTextComponent youNeed = Utils.localize("gui.securitycraft:blockPocketManager.youNeed");
	private final boolean hasStorageModule;
	private final boolean isOwner;
	private final int[] materialCounts = new int[3];
	public final BlockPocketManagerBlockEntity be;
	private int size = 5;
	private Button assembleButton;
	private Button outlineButton;
	private Slider offsetSlider;
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

	public BlockPocketManagerScreen(BlockPocketManagerMenu menu, PlayerInventory inv, ITextComponent title) {
		super(menu, inv, title);

		be = menu.be;
		size = be.getSize();
		isOwner = menu.isOwner;
		hasStorageModule = menu.hasStorageModule;

		if (hasStorageModule)
			imageWidth = 256;

		imageHeight = !hasStorageModule ? 194 : 240;
		previousColor = be.getColor();
	}

	@Override
	public void init() {
		super.init();

		int guiWidth = hasStorageModule ? 123 : imageWidth;
		int widgetWidth = hasStorageModule ? 110 : 120;
		int widgetOffset = widgetWidth / 2;
		//@formatter:off
		int[] yOffset = hasStorageModule ? new int[] {-76, -100, -52, -28, -4} : new int[] {-40, -70, 23, 47, 71};
		//@formatter:on
		int outlineY = topPos + imageHeight / 2 + yOffset[2];
		Button colorChooserButton;
		int colorChooserButtonX = leftPos + guiWidth / 2 - widgetOffset + (hasStorageModule ? 0 : widgetWidth + 3);
		int outlineButtonX = colorChooserButtonX + (hasStorageModule ? 23 : -widgetWidth - 3);
		int outlineButtonWidth = widgetWidth - (hasStorageModule ? 23 : 0);
		int colorChooserX = colorChooserButtonX + (hasStorageModule ? -145 : 20);
		Button toggleButton, sizeButton;

		toggleButton = addButton(new ExtendedButton(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[0], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager." + (!be.isEnabled() ? "activate" : "deactivate")), this::toggleButtonClicked));
		sizeButton = addButton(new ExtendedButton(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[1], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.size", size, size, size), this::sizeButtonClicked));
		outlineButton = addButton(new ExtendedButton(outlineButtonX, outlineY, outlineButtonWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.outline." + (!be.showsOutline() ? "show" : "hide")), this::outlineButtonClicked));
		assembleButton = addButton(new ExtendedButton(leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[3], widgetWidth, 20, Utils.localize("gui.securitycraft:blockPocketManager.assemble"), this::assembleButtonClicked));
		offsetSlider = addButton(new NamedSlider(Utils.localize("gui.securitycraft:projector.offset", be.getAutoBuildOffset()), BlockUtils.getLanguageKeyDenotation(SCContent.BLOCK_POCKET_MANAGER.get()), leftPos + guiWidth / 2 - widgetOffset, topPos + imageHeight / 2 + yOffset[4], widgetWidth, 20, Utils.localize("gui.securitycraft:projector.offset", ""), "", (-size + 2) / 2, (size - 2) / 2, be.getAutoBuildOffset(), false, true, null, this::offsetSliderReleased));
		colorChooser = addWidget(new ColorChooser(StringTextComponent.EMPTY, colorChooserX, outlineY, previousColor) {
			@Override
			public void onColorChange() {
				be.setColor(getRGBColor());
			}
		});
		colorChooser.init(minecraft, this.width, height);
		colorChooserButton = addButton(new ColorChooserButton(colorChooserButtonX, outlineY, 20, 20, colorChooser));

		if (!be.isOwnedBy(Minecraft.getInstance().player))
			sizeButton.active = toggleButton.active = assembleButton.active = outlineButton.active = offsetSlider.active = colorChooserButton.active = false;
		else {
			updateMaterialInformation(true);
			sizeButton.active = offsetSlider.active = !be.isEnabled();
		}

		if (!hasStorageModule) {
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
	protected void renderLabels(MatrixStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, (hasStorageModule ? 123 : imageWidth) / 2 - font.width(title) / 2, 6, 4210752);

		if (hasStorageModule)
			font.draw(pose, inventory.getDisplayName(), 8, imageHeight - 94, 4210752);

		if (!be.isEnabled() && isOwner) {
			if (!hasStorageModule) {
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
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		if (hasStorageModule)
			renderTooltip(pose, mouseX, mouseY);

		if (!be.isEnabled() && isOwner) {
			if (colorChooser != null)
				colorChooser.render(pose, mouseX, mouseY, partialTicks);

			for (StackHoverChecker shc : hoverCheckers) {
				if (shc.checkHover(mouseX, mouseY)) {
					renderTooltip(pose, shc.getStack(), mouseX, mouseY);
					break;
				}
			}

			if (!assembleButton.active && assembleHoverChecker.checkHover(mouseX, mouseY)) {
				if (!hasStorageModule)
					GuiUtils.drawHoveringText(pose, assembleHoverChecker.getLines().subList(0, 1), mouseX, mouseY, width, height, -1, font);
				else
					GuiUtils.drawHoveringText(pose, assembleHoverChecker.getLines().subList(1, 2), mouseX, mouseY, width, height, -1, font);
			}

			if (colorChooserButtonHoverChecker.checkHover(mouseX, mouseY))
				renderTooltip(pose, colorChooserButtonHoverChecker.getName(), mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(hasStorageModule ? TEXTURE_STORAGE : TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void tick() {
		if (colorChooser != null)
			colorChooser.tick();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (colorChooser != null)
			colorChooser.mouseDragged(mouseX, mouseY, button, dragX, dragY);

		return (getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY)) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (offsetSlider.dragging)
			offsetSlider.mouseReleased(mouseX, mouseY, button);

		if (colorChooser != null && colorChooser.getHueSlider().dragging)
			colorChooser.getHueSlider().mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
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
	public List<Rectangle2d> getExtraAreas() {
		if (colorChooser != null)
			return colorChooser.getGuiExtraAreas();
		else
			return new ArrayList<>();
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

					if (stack.getItem() instanceof BlockItem) {
						Block block = ((BlockItem) stack.getItem()).getBlock();

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
		assembleButton.active = isOwner && (minecraft.player.isCreative() || (!be.isEnabled() && hasStorageModule && wallsStillNeeded <= 0 && pillarsStillNeeded <= 0 && chiseledStillNeeded <= 0));
	}

	public void toggleButtonClicked(Button button) {
		be.setSize(size);
		be.setEnabled(!be.isEnabled());
		SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new ToggleBlockPocketManager(be, be.isEnabled()));
		Minecraft.getInstance().player.closeContainer();
	}

	public void sizeButtonClicked(Button button) {
		int newOffset;
		int newMin;
		int newMax;

		size += 4;

		if (size > 25)
			size = 5;

		newMin = (-size + 2) / 2;
		newMax = (size - 2) / 2;

		if (be.getAutoBuildOffset() > 0)
			newOffset = Math.min(be.getAutoBuildOffset(), newMax);
		else
			newOffset = Math.max(be.getAutoBuildOffset(), newMin);

		updateMaterialInformation(false);
		be.setSize(size);
		offsetSlider.minValue = newMin;
		offsetSlider.maxValue = newMax;
		be.setAutoBuildOffset(newOffset);
		offsetSlider.setValue(newOffset);
		offsetSlider.updateSlider();
		button.setMessage(Utils.localize("gui.securitycraft:blockPocketManager.size", size, size, size));
		sync();
	}

	public void assembleButtonClicked(Button button) {
		be.setSize(size);
		SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new AssembleBlockPocket(be));
		Minecraft.getInstance().player.closeContainer();
	}

	public void outlineButtonClicked(Button button) {
		be.toggleOutline();
		outlineButton.setMessage(Utils.localize("gui.securitycraft:blockPocketManager.outline." + (!be.showsOutline() ? "show" : "hide")));
		sync();
	}

	public void offsetSliderReleased(Slider slider) {
		be.setAutoBuildOffset(slider.getValueInt());
		sync();
	}

	private void sync() {
		SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new SyncBlockPocketManager(be.getBlockPos(), be.getSize(), be.showsOutline(), be.getAutoBuildOffset(), be.getColor()));
	}
}
