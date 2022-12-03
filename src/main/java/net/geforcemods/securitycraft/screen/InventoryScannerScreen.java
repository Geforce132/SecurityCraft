package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InventoryScannerScreen extends AbstractContainerScreen<InventoryScannerMenu> {
	private static final ResourceLocation REGULAR_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation ENHANCED_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	public final InventoryScannerBlockEntity be;
	private boolean owns = false;
	private boolean hasRedstoneModule = false, hasStorageModule = false;
	private Component infoStringRedstone, infoStringStorage;
	private static final Style UNDERLINE = Style.EMPTY.applyFormat(ChatFormatting.UNDERLINE);
	private final Component redstoneModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.REDSTONE_MODULE.get().getDescriptionId()));
	private final Component storageModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.STORAGE_MODULE.get().getDescriptionId()));
	private final Component prohibitedItems = Utils.localize("gui.securitycraft:invScan.prohibitedItems");
	private final Component adminMode = Utils.localize("gui.securitycraft:invScan.mode.admin").setStyle(UNDERLINE);
	private final Component viewMode = Utils.localize("gui.securitycraft:invScan.mode.view").setStyle(UNDERLINE);
	private final Component storage = Utils.localize("gui.securitycraft:invScan.storage");

	public InventoryScannerScreen(InventoryScannerMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		be = menu.be;
		owns = be.isOwnedBy(inv.player);
		hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		hasStorageModule = be.isModuleEnabled(ModuleType.STORAGE);
		infoStringRedstone = Utils.localize("gui.securitycraft:invScan.emit_redstone", Utils.localize("gui.securitycraft:invScan." + (hasRedstoneModule ? "yes" : "no")));
		infoStringStorage = Utils.localize("gui.securitycraft:invScan.check_inv", Utils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no")));

		if (hasStorageModule)
			imageWidth = 246;
		else
			imageWidth = 190;

		imageHeight = 196;
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		font.drawWordWrap(infoStringRedstone, leftPos + 28, topPos + 45, 150, 4210752);
		font.drawWordWrap(infoStringStorage, leftPos + 28, topPos + 75, 150, 4210752);
		ClientUtils.renderModuleInfo(pose, ModuleType.REDSTONE, null, redstoneModuleNotInstalled, hasRedstoneModule, leftPos + 8, topPos + 45, width, height, mouseX, mouseY);
		ClientUtils.renderModuleInfo(pose, ModuleType.STORAGE, null, storageModuleNotInstalled, hasStorageModule, leftPos + 8, topPos + 75, width, height, mouseX, mouseY);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(pose, getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, prohibitedItems, 8, 6, 4210752);
		font.draw(pose, be.isOwnedBy(minecraft.player) ? adminMode : viewMode, 112, 6, 4210752);

		if (hasStorageModule && owns)
			font.draw(pose, storage, 188, 18, 4210752);

		font.draw(pose, Utils.INVENTORY_TEXT, 15, imageHeight - 93, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, hasStorageModule && owns ? ENHANCED_INVENTORY : REGULAR_INVENTORY);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight + 30);
	}
}
