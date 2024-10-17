package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InventoryScannerScreen extends AbstractContainerScreen<InventoryScannerMenu> {
	private static final ResourceLocation REGULAR_INVENTORY = SecurityCraft.resLoc("textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation ENHANCED_INVENTORY = SecurityCraft.resLoc("textures/gui/container/inventory_scanner_enhanced_gui.png");
	public final InventoryScannerBlockEntity be;
	private boolean owns = false;
	private boolean hasRedstoneModule = false, hasStorageModule = false;
	private Component infoStringRedstone, infoStringStorage;
	private static final Style UNDERLINE = Style.EMPTY.applyFormat(ChatFormatting.UNDERLINE);
	private final Component prohibitedItems = Utils.localize("gui.securitycraft:invScan.prohibitedItems");
	private final Component adminMode = Utils.localize("gui.securitycraft:invScan.mode.admin").setStyle(UNDERLINE);
	private final Component viewMode = Utils.localize("gui.securitycraft:invScan.mode.view").setStyle(UNDERLINE);
	private final Component storage = Utils.localize("gui.securitycraft:invScan.storage");
	private Component redstoneModuleTooltip = null;
	private Component storageModuleTooltip = null;

	public InventoryScannerScreen(InventoryScannerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		be = menu.be;
		owns = be.isOwnedBy(inv.player);
		hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		hasStorageModule = be.isModuleEnabled(ModuleType.STORAGE);
		infoStringRedstone = Utils.localize("gui.securitycraft:invScan.emit_redstone", Utils.localize("gui.securitycraft:invScan." + (hasRedstoneModule ? "yes" : "no")));
		infoStringStorage = Utils.localize("gui.securitycraft:invScan.check_inv", Utils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no")));

		if (!hasRedstoneModule)
			redstoneModuleTooltip = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.REDSTONE_MODULE.get().getDescriptionId()));

		if (hasStorageModule)
			imageWidth = 246;
		else {
			imageWidth = 190;
			storageModuleTooltip = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.STORAGE_MODULE.get().getDescriptionId()));
		}

		imageHeight = 196;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		guiGraphics.drawWordWrap(font, infoStringRedstone, leftPos + 28, topPos + 45, 150, 4210752);
		guiGraphics.drawWordWrap(font, infoStringStorage, leftPos + 28, topPos + 75, 150, 4210752);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.REDSTONE, redstoneModuleTooltip, hasRedstoneModule, leftPos + 8, topPos + 45, mouseX, mouseY);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.STORAGE, storageModuleTooltip, hasStorageModule, leftPos + 8, topPos + 75, mouseX, mouseY);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, prohibitedItems, 8, 6, 4210752, false);
		guiGraphics.drawString(font, be.isOwnedBy(minecraft.player) ? adminMode : viewMode, 112, 6, 4210752, false);

		if (hasStorageModule && owns)
			guiGraphics.drawString(font, storage, 188, 18, 4210752, false);

		guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 15, imageHeight - 93, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderType::guiTextured, hasStorageModule && owns ? ENHANCED_INVENTORY : REGULAR_INVENTORY, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight + 30, 256, 256);
	}
}
