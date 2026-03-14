package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class BlockReinforcerScreen extends AbstractContainerScreen<BlockReinforcerMenu> {
	private static final Identifier TEXTURE = SecurityCraft.resLoc("textures/gui/container/universal_block_reinforcer.png");
	private static final Identifier TEXTURE_LVL1 = SecurityCraft.resLoc("textures/gui/container/universal_block_reinforcer_lvl1.png");
	private final Component output = Utils.localize("gui.securitycraft:blockReinforcer.output");
	private CallbackCheckbox unreinforceCheckbox;

	public BlockReinforcerScreen(BlockReinforcerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, 176, 186);
	}

	@Override
	protected void init() {
		super.init();
		unreinforceCheckbox = addRenderableWidget(new CallbackCheckbox(leftPos + 24, topPos + 69, 20, 20, Component.empty(), !menu.isReinforcing, this::updateTooltip, 0));
		updateTooltip(unreinforceCheckbox.selected());

		if (menu.isLvl1)
			unreinforceCheckbox.visible = false;
	}

	private void updateTooltip(boolean isSelected) {
		if (isSelected)
			unreinforceCheckbox.setTooltip(Tooltip.create(Component.translatable("gui.securitycraft:blockReinforcer.unreinforceCheckbox.checked")));
		else
			unreinforceCheckbox.setTooltip(Tooltip.create(Component.translatable("gui.securitycraft:blockReinforcer.unreinforceCheckbox.not_checked")));
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

		if (!inv.get(36).isEmpty()) {
			guiGraphics.text(font, output, 50, 25, CommonColors.DARK_GRAY, false);
			guiGraphics.item(menu.reinforcingSlot.getOutput(), 116, 20);
			guiGraphics.itemDecorations(minecraft.font, menu.reinforcingSlot.getOutput(), 116, 20, null);

			if (mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 17 && mouseY < topPos + 39)
				guiGraphics.setTooltipForNextFrame(font, menu.reinforcingSlot.getOutput(), mouseX, mouseY);
		}

		if (!menu.isLvl1 && !inv.get(37).isEmpty()) {
			guiGraphics.text(font, output, 50, 50, CommonColors.DARK_GRAY, false);
			guiGraphics.item(menu.unreinforcingSlot.getOutput(), 116, 46);
			guiGraphics.itemDecorations(minecraft.font, menu.unreinforcingSlot.getOutput(), 116, 46, null);

			if (mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 43 && mouseY < topPos + 64)
				guiGraphics.setTooltipForNextFrame(font, menu.unreinforcingSlot.getOutput(), mouseX, mouseY);
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, menu.isLvl1 ? TEXTURE_LVL1 : TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public void onClose() {
		super.onClose();
		ClientPacketDistributor.sendToServer(new SyncBlockReinforcer(!unreinforceCheckbox.selected()));
	}
}
