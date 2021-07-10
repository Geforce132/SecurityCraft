package net.geforcemods.securitycraft.screen;

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
	private String infoStringRedstone, infoStringStorage;
	private final String redstoneModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.REDSTONE_MODULE.get().getTranslationKey())).getFormattedText();
	private final String storageModuleNotInstalled = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.STORAGE_MODULE.get().getTranslationKey())).getFormattedText();

	public InventoryScannerScreen(InventoryScannerContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		tileEntity = container.te;
		owns = tileEntity.getOwner().isOwner(inv.player);
		hasRedstoneModule = tileEntity.hasModule(ModuleType.REDSTONE);
		hasStorageModule = tileEntity.hasModule(ModuleType.STORAGE);
		infoStringRedstone = Utils.localize("gui.securitycraft:invScan.emit_redstone", Utils.localize("gui.securitycraft:invScan." + (hasRedstoneModule ? "yes" : "no"))).getFormattedText();
		infoStringStorage = Utils.localize("gui.securitycraft:invScan.check_inv", Utils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no"))).getFormattedText();

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
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);

		RenderSystem.disableLighting();
		font.drawSplitString(infoStringRedstone, guiLeft + 28, guiTop + 45, 150, 4210752);
		font.drawSplitString(infoStringStorage, guiLeft + 28, guiTop + 75, 150, 4210752);
		ClientUtils.renderModuleInfo(ModuleType.REDSTONE, null, redstoneModuleNotInstalled, hasRedstoneModule, guiLeft + 8, guiTop + 45, width, height, mouseX, mouseY);
		ClientUtils.renderModuleInfo(ModuleType.STORAGE, null, storageModuleNotInstalled, hasStorageModule, guiLeft + 8, guiTop + 75, width, height, mouseX, mouseY);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		font.drawString(Utils.localize("gui.securitycraft:invScan.prohibitedItems").getFormattedText(), 8, 6, 4210752);
		font.drawString(tileEntity.getOwner().isOwner(minecraft.player) ? (TextFormatting.UNDERLINE + Utils.localize("gui.securitycraft:invScan.mode.admin").getFormattedText()) : (TextFormatting.UNDERLINE + Utils.localize("gui.securitycraft:invScan.mode.view").getFormattedText()), 112, 6, 4210752);

		if(hasStorageModule && owns)
			font.drawString(Utils.localize("gui.securitycraft:invScan.storage").getFormattedText(), 188, 18, 4210752);

		font.drawString(Utils.localize("container.inventory").getFormattedText(), 15, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(hasStorageModule && owns ? ENHANCED_INVENTORY : REGULAR_INVENTORY);
		blit((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize + 30);
	}
}
