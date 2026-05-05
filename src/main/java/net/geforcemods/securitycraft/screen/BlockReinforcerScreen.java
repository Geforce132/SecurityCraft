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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlockReinforcerScreen extends AbstractContainerScreen<BlockReinforcerMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/universal_block_reinforcer.png");
	private static final ResourceLocation SAVE_SPRITE = SecurityCraft.resLoc("textures/gui/save.png");
	private static final ResourceLocation SAVE_INACTIVE_SPRITE = SecurityCraft.resLoc("textures/gui/save_inactive.png");
	private final Component output = Utils.localize("gui.securitycraft:blockReinforcer.output");
	private final Component mode = Utils.localize("gui.securitycraft:blockReinforcer.mode");
	private final Component tint = Utils.localize("gui.securitycraft:blockReinforcer.tint");
	private ToggleComponentButton reinforcingModeButton;
	private ToggleComponentButton tintModeButton;
	private ColorChooser tintColorChooser;
	private ActiveBasedTextureButton saveToConfigButton;
	private int oldTintColor;
	private TintMode oldTintMode;

	public BlockReinforcerScreen(BlockReinforcerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		imageHeight = 186;
	}

	@Override
	protected void init() {
		super.init();

		int tintRowY = topPos + 67;
		Button colorChooserButton;

		oldTintColor = TintMode.color();
		oldTintMode = TintMode.mode();
		reinforcingModeButton = addRenderableWidget(new ToggleComponentButton(leftPos + 89, topPos + 42, 80, 20, this::updateReinforcingModeButtonText, menu.isReinforcing ? 0 : 1, 2, this::reinforcingModeButtonClicked));
		tintModeButton = addRenderableWidget(new ToggleComponentButton(leftPos + 44, tintRowY, 75, 20, i -> TintMode.values()[i].translate(), oldTintMode.ordinal(), 4, this::tintModeButtonClicked));
		tintColorChooser = new ColorChooser(Component.empty(), leftPos + 124, tintRowY, oldTintColor, this::colorChanged);
		colorChooserButton = addRenderableWidget(new ColorChooserButton(leftPos + 124, tintRowY, 20, 20, tintColorChooser));
		saveToConfigButton = addRenderableWidget(new ActiveBasedTextureButton(leftPos + 149, tintRowY, 20, 20, SAVE_SPRITE, SAVE_INACTIVE_SPRITE, 16, 16, 2, 2, 16, 16, 16, 16, this::saveButtonClicked));

		updateReinforcingTooltip(menu.isReinforcing);
		updateTintTooltip(TintMode.mode());
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
		if (TintMode.mode() != ConfigHandler.CLIENT.reinforcedBlockTintMode.get() || TintMode.color() != ConfigHandler.CLIENT.reinforcedBlockTintColor.get()) {
			saveToConfigButton.active = true;
			saveToConfigButton.setTooltip(Tooltip.create(Component.translatable("gui.securitycraft:blockReinforcer.saveToConfigTooltip")));
		}
		else {
			saveToConfigButton.active = false;
			saveToConfigButton.setTooltip(null);
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 5, 0x404040, false);
		guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 0x404040, false);

		if (!menu.getResult().isEmpty())
			guiGraphics.drawString(font, output, 128 - font.width(output), 25, 0x404040, false);

		guiGraphics.drawString(font, mode, 8, 48, 0x404040, false);
		guiGraphics.drawString(font, tint, 8, 73, 0x404040, false);
	}

	@Override
	public void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		renderBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	private Component updateReinforcingModeButtonText(int index) {
		return index == 1 ? Utils.localize("gui.securitycraft:blockReinforcer.unreinforcing") : Utils.localize("gui.securitycraft:blockReinforcer.reinforcing");
	}

	private void reinforcingModeButtonClicked(Button button) {
		if (button instanceof ToggleComponentButton toggleButton) {
			boolean isReinforcing = toggleButton.getCurrentIndex() == 0;

			updateReinforcingTooltip(isReinforcing);
			SecurityCraft.CHANNEL.sendToServer(new SyncBlockReinforcer(isReinforcing));
		}
	}

	private void tintModeButtonClicked(Button button) {
		if (button instanceof ToggleComponentButton toggleButton) {
			TintMode newMode = TintMode.values()[toggleButton.getCurrentIndex()];

			TintMode.setMode(newMode);
			updateTintTooltip(newMode);
			updateSaveButton();
		}
	}

	private void colorChanged(int newColor) {
		TintMode.setColor(newColor);
		updateSaveButton();
	}

	private void saveButtonClicked(Button button) {
		ConfigHandler.CLIENT.reinforcedBlockTintColor.set(TintMode.color());
		ConfigHandler.CLIENT.reinforcedBlockTintMode.set(TintMode.mode());
		ConfigHandler.CLIENT.reinforcedBlockTintColor.save();
		ConfigHandler.CLIENT.reinforcedBlockTintMode.save();
		updateSaveButton();
	}

	@Override
	public void onClose() {
		super.onClose();

		if (TintMode.mode() != oldTintMode || TintMode.color() != oldTintColor)
			ClientUtils.recompileAllChunksInRange();
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return tintColorChooser != null ? tintColorChooser.getGuiExtraAreas() : List.of();
	}
}
