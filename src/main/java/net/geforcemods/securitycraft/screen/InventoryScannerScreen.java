package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryScannerScreen extends ContainerScreen<InventoryScannerMenu> {
	private static final ResourceLocation REGULAR_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation ENHANCED_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	private InventoryScannerBlockEntity tileEntity;
	private boolean owns = false;
	private boolean hasRedstoneModule = false, hasStorageModule = false;
	private ITextComponent infoStringRedstone, infoStringStorage;
	private static final Style UNDERLINE = Style.EMPTY.applyFormat(TextFormatting.UNDERLINE);
	private final ITextComponent redstoneModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.REDSTONE_MODULE.get().getDescriptionId()));
	private final ITextComponent storageModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.STORAGE_MODULE.get().getDescriptionId()));
	private final ITextComponent prohibitedItems = Utils.localize("gui.securitycraft:invScan.prohibitedItems");
	private final ITextComponent adminMode = Utils.localize("gui.securitycraft:invScan.mode.admin").setStyle(UNDERLINE);
	private final ITextComponent viewMode = Utils.localize("gui.securitycraft:invScan.mode.view").setStyle(UNDERLINE);
	private final ITextComponent storage = Utils.localize("gui.securitycraft:invScan.storage");

	public InventoryScannerScreen(InventoryScannerMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = container.te;
		owns = tileEntity.getOwner().isOwner(inv.player);
		hasRedstoneModule = tileEntity.hasModule(ModuleType.REDSTONE);
		hasStorageModule = tileEntity.hasModule(ModuleType.STORAGE);
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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);

		RenderSystem.disableLighting();
		font.drawWordWrap(infoStringRedstone, leftPos + 28, topPos + 45, 150, 4210752);
		font.drawWordWrap(infoStringStorage, leftPos + 28, topPos + 75, 150, 4210752);
		ClientUtils.renderModuleInfo(matrix, ModuleType.REDSTONE, null, redstoneModuleNotInstalled, hasRedstoneModule, leftPos + 8, topPos + 45, width, height, mouseX, mouseY);
		ClientUtils.renderModuleInfo(matrix, ModuleType.STORAGE, null, storageModuleNotInstalled, hasStorageModule, leftPos + 8, topPos + 75, width, height, mouseX, mouseY);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY) {
		font.draw(matrix, prohibitedItems, 8, 6, 4210752);
		font.draw(matrix, tileEntity.getOwner().isOwner(minecraft.player) ? adminMode : viewMode, 112, 6, 4210752);

		if (hasStorageModule && owns)
			font.draw(matrix, storage, 188, 18, 4210752);

		font.draw(matrix, Utils.INVENTORY_TEXT, 15, imageHeight - 93, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(hasStorageModule && owns ? ENHANCED_INVENTORY : REGULAR_INVENTORY);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight + 30);
	}
}
