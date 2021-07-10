package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
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
public class InventoryScannerScreen extends ContainerScreen<InventoryScannerContainer> {
	private static final ResourceLocation REGULAR_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation ENHANCED_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	private InventoryScannerTileEntity tileEntity;
	private boolean owns = false;
	private boolean hasRedstoneModule = false, hasStorageModule = false;
	private ITextComponent infoStringRedstone, infoStringStorage;
	private static final Style UNDERLINE = Style.EMPTY.applyFormatting(TextFormatting.UNDERLINE);
	private final ITextComponent redstoneModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.REDSTONE_MODULE.get().getTranslationKey()));
	private final ITextComponent storageModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.STORAGE_MODULE.get().getTranslationKey()));

	public InventoryScannerScreen(InventoryScannerContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		tileEntity = container.te;
		owns = tileEntity.getOwner().isOwner(inv.player);
		hasRedstoneModule = tileEntity.hasModule(ModuleType.REDSTONE);
		hasStorageModule = tileEntity.hasModule(ModuleType.STORAGE);
		infoStringRedstone = Utils.localize("gui.securitycraft:invScan.emit_redstone", Utils.localize("gui.securitycraft:invScan." + (hasRedstoneModule ? "yes" : "no")));
		infoStringStorage = Utils.localize("gui.securitycraft:invScan.check_inv", Utils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no")));

		if(hasStorageModule)
			xSize = 246;
		else
			xSize = 190;

		ySize = 196;
	}

	@Override
	public void init(){
		super.init();
		minecraft.keyboardListener.enableRepeatEvents(true);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		super.render(matrix, mouseX, mouseY, partialTicks);

		RenderSystem.disableLighting();
		font.func_238418_a_(infoStringRedstone, guiLeft + 28, guiTop + 45, 150, 4210752);
		font.func_238418_a_(infoStringStorage, guiLeft + 28, guiTop + 75, 150, 4210752);
		ClientUtils.renderModuleInfo(matrix, ModuleType.REDSTONE, null, redstoneModuleNotInstalled, hasRedstoneModule, guiLeft + 8, guiTop + 45, width, height, mouseX, mouseY);
		ClientUtils.renderModuleInfo(matrix, ModuleType.STORAGE, null, storageModuleNotInstalled, hasStorageModule, guiLeft + 8, guiTop + 75, width, height, mouseX, mouseY);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.drawText(matrix, Utils.localize("gui.securitycraft:invScan.prohibitedItems"), 8, 6, 4210752);
		font.drawText(matrix, Utils.localize("gui.securitycraft:invScan.mode." + (tileEntity.getOwner().isOwner(minecraft.player) ? "admin" : "view")).setStyle(UNDERLINE), 112, 6, 4210752);

		if(hasStorageModule && owns)
			font.drawText(matrix, Utils.localize("gui.securitycraft:invScan.storage"), 188, 18, 4210752);

		font.drawText(matrix, Utils.localize("container.inventory"), 15, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(hasStorageModule && owns ? ENHANCED_INVENTORY : REGULAR_INVENTORY);
		blit(matrix, (width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize + 30);
	}
}
