package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlockReinforcerScreen extends AbstractContainerScreen<BlockReinforcerMenu> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/universal_block_reinforcer.png");
	private static final ResourceLocation TEXTURE_LVL1 = SecurityCraft.resLoc("textures/gui/container/universal_block_reinforcer_lvl1.png");
	private final Component output = Utils.localize("gui.securitycraft:blockReinforcer.output");
	private CallbackCheckbox unreinforceCheckbox;

	public BlockReinforcerScreen(BlockReinforcerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		imageHeight = 186;
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
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		NonNullList<ItemStack> inv = menu.getItems();

		guiGraphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 5, 4210752, false);
		guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 4210752, false);

		if (!inv.get(36).isEmpty()) {
			guiGraphics.drawString(font, output, 50, 25, 4210752, false);
			guiGraphics.renderItem(menu.reinforcingSlot.getOutput(), 116, 20);
			guiGraphics.renderItemDecorations(minecraft.font, menu.reinforcingSlot.getOutput(), 116, 20, null);

			if (mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 17 && mouseY < topPos + 39)
				guiGraphics.renderTooltip(font, menu.reinforcingSlot.getOutput(), mouseX - leftPos, mouseY - topPos);
		}

		if (!menu.isLvl1 && !inv.get(37).isEmpty()) {
			guiGraphics.drawString(font, output, 50, 50, 4210752, false);
			guiGraphics.renderItem(menu.unreinforcingSlot.getOutput(), 116, 46);
			guiGraphics.renderItemDecorations(minecraft.font, menu.unreinforcingSlot.getOutput(), 116, 46, null);

			if (mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 43 && mouseY < topPos + 64)
				guiGraphics.renderTooltip(font, menu.unreinforcingSlot.getOutput(), mouseX - leftPos, mouseY - topPos);
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, menu.isLvl1 ? TEXTURE_LVL1 : TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public void onClose() {
		super.onClose();
		PacketDistributor.sendToServer(new SyncBlockReinforcer(!unreinforceCheckbox.selected()));
	}
}
