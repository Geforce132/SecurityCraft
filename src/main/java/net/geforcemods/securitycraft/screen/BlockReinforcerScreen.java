package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.misc.TintMode;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.screen.components.ActiveBasedTextureButton;
import net.geforcemods.securitycraft.screen.components.ColorChooser;
import net.geforcemods.securitycraft.screen.components.ColorChooserButton;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class BlockReinforcerScreen extends AbstractContainerScreen<BlockReinforcerMenu> implements IHasExtraAreas {
	private static final Identifier TEXTURE = SecurityCraft.resLoc("textures/gui/container/universal_block_reinforcer.png");
	private static final Identifier SAVE_SPRITE = SecurityCraft.resLoc("widget/save");
	private static final Identifier SAVE_INACTIVE_SPRITE = SecurityCraft.resLoc("widget/save_inactive");
	private final Component output = Utils.localize("gui.securitycraft:blockReinforcer.output");
	private final Component mode = Utils.localize("gui.securitycraft:blockReinforcer.mode");
	private final Component tint = Utils.localize("gui.securitycraft:blockReinforcer.tint");
	private ToggleComponentButton reinforcingModeButton;
	private ToggleComponentButton tintModeButton;
	private ColorChooser tintColorChooser;
	private ActiveBasedTextureButton saveToConfigButton;

	public BlockReinforcerScreen(BlockReinforcerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, 176, 186);
	}

	@Override
	protected void init() {
		super.init();

		int tintRowY = topPos + 67;
		Button colorChooserButton;

		reinforcingModeButton = addRenderableWidget(new ToggleComponentButton(leftPos + 44, topPos + 42, 100, 20, this::updateReinforcingModeButtonText, menu.isReinforcing ? 0 : 1, 2, this::reinforcingModeButtonClicked));
		tintModeButton = addRenderableWidget(new ToggleComponentButton(leftPos + 44, tintRowY, 75, 20, i -> TintMode.values()[i].translate(), TintMode.getTintMode().ordinal(), 4, this::tintModeButtonClicked));
		tintColorChooser = new ColorChooser(Component.empty(), leftPos + 124, tintRowY, TintMode.getTintColor(), color -> updateSaveButton());
		colorChooserButton = addRenderableWidget(new ColorChooserButton(leftPos + 124, tintRowY, 20, 20, tintColorChooser));
		saveToConfigButton = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 149, tintRowY, 20, 20, SAVE_SPRITE, SAVE_INACTIVE_SPRITE, 2, 2, 16, 16, this::saveButtonClicked));

		updateReinforcingTooltip(menu.isReinforcing);
		updateTintTooltip(TintMode.getTintMode());
		colorChooserButton.setTooltip(Tooltip.create(Component.translatable("gui.securitycraft:blockReinforcer.chooseTintColorTooltip")));
		updateSaveButton();

		if (menu.isLvl1)
			reinforcingModeButton.active = false;
	}

	private void updateReinforcingTooltip(boolean isReinforcing) {
		reinforcingModeButton.setTooltip(Tooltip.create(Component.translatable(isReinforcing ? "gui.securitycraft:blockReinforcer.reinforcing.tooltip" : "gui.securitycraft:blockReinforcer.unreinforcing.tooltip")));
	}

	private void updateTintTooltip(TintMode tintMode) {
		tintModeButton.setTooltip(Tooltip.create(tintMode.tooltip()));
	}

	private void updateSaveButton() {
		TintMode newTintMode = TintMode.values()[tintModeButton.getCurrentIndex()];
		int newTintColor = tintColorChooser.getRGBColor();

		if (newTintMode != ConfigHandler.CLIENT.reinforcedBlockTintMode.get() || newTintColor != ConfigHandler.CLIENT.reinforcedBlockTintColor.getAsInt()) {
			saveToConfigButton.active = true;
			saveToConfigButton.setTooltip(Tooltip.create(Component.translatable("gui.securitycraft:blockReinforcer.saveToConfigTooltip")));
		}
		else {
			saveToConfigButton.active = false;
			saveToConfigButton.setTooltip(null);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
		extractTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		NonNullList<ItemStack> inv = menu.getItems();

		guiGraphics.text(font, title, (imageWidth - font.width(title)) / 2, 5, CommonColors.DARK_GRAY, false);
		guiGraphics.text(font, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, CommonColors.DARK_GRAY, false);

		if (!inv.get(36).isEmpty()) { //Rendering the preview of the output item
			guiGraphics.text(font, output, 50, 25, CommonColors.DARK_GRAY, false);
			guiGraphics.item(menu.blockReinforcerSlot.getOutput(), 116, 20);
			guiGraphics.itemDecorations(minecraft.font, menu.blockReinforcerSlot.getOutput(), 116, 20, null);

			if (mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 17 && mouseY < topPos + 39)
				guiGraphics.setTooltipForNextFrame(font, menu.blockReinforcerSlot.getOutput(), mouseX, mouseY);
		}

		guiGraphics.text(font, mode, 8, 48, CommonColors.DARK_GRAY, false);
		guiGraphics.text(font, tint, 8, 73, CommonColors.DARK_GRAY, false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
		super.extractBackground(guiGraphics, mouseX, mouseY, a);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	private Component updateReinforcingModeButtonText(int index) {
		return index == 1 ? Utils.localize("gui.securitycraft:blockReinforcer.unreinforcing") : Utils.localize("gui.securitycraft:blockReinforcer.reinforcing");
	}

	private void reinforcingModeButtonClicked(Button button) {
		if (button instanceof ToggleComponentButton toggleButton) {
			boolean isReinforcing = toggleButton.getCurrentIndex() == 0;

			updateReinforcingTooltip(isReinforcing);
			ClientPacketDistributor.sendToServer(new SyncBlockReinforcer(isReinforcing));
		}
	}

	private void tintModeButtonClicked(Button button) {
		if (button instanceof ToggleComponentButton toggleButton)
			updateTintTooltip(TintMode.values()[toggleButton.getCurrentIndex()]);

		updateSaveButton();
	}

	private void saveButtonClicked(Button button) {
		ConfigHandler.CLIENT.reinforcedBlockTintColor.set(tintColorChooser.getRGBColor());
		ConfigHandler.CLIENT.reinforcedBlockTintColor.save();
		ConfigHandler.CLIENT.reinforcedBlockTintMode.set(TintMode.values()[tintModeButton.getCurrentIndex()]);
		ConfigHandler.CLIENT.reinforcedBlockTintMode.save();
		ClientUtils.recompileAllChunksInRange(); //The saved value will automatically be updated through the changing config, so the tints need to be refreshed
		updateSaveButton();
	}

	@Override
	public void onClose() {
		super.onClose();

		TintMode newTintMode = TintMode.values()[tintModeButton.getCurrentIndex()];
		int newTintColor = tintColorChooser.getRGBColor();

		if (newTintMode != TintMode.getTintMode() || newTintColor != TintMode.getTintColor()) {
			TintMode.setTintSettings(newTintColor, newTintMode);
			ClientUtils.recompileAllChunksInRange();
		}
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return tintColorChooser != null ? tintColorChooser.getGuiExtraAreas() : List.of();
	}
}
