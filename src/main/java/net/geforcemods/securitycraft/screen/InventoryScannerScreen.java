package net.geforcemods.securitycraft.screen;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InventoryScannerScreen extends GuiContainer {
	private static final ResourceLocation regularInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation ENHANCED_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	public InventoryScannerBlockEntity tileEntity;
	private EntityPlayer playerObj;
	private boolean owns = false;
	private boolean hasRedstoneModule = false, hasStorageModule = false;
	private String infoStringRedstone, infoStringStorage;
	private String redstoneModuleTooltip = null;
	private String storageModuleTooltip = null;

	public InventoryScannerScreen(InventoryPlayer inventory, InventoryScannerBlockEntity te, EntityPlayer player) {
		super(new InventoryScannerMenu(inventory, te));
		tileEntity = te;
		playerObj = player;
		owns = tileEntity.isOwnedBy(playerObj);
		hasRedstoneModule = tileEntity.isModuleEnabled(ModuleType.REDSTONE);
		hasStorageModule = tileEntity.isModuleEnabled(ModuleType.STORAGE);
		infoStringRedstone = Utils.localize("gui.securitycraft:invScan.emit_redstone", Utils.localize("gui.securitycraft:invScan." + (hasRedstoneModule ? "yes" : "no"))).getFormattedText();
		infoStringStorage = Utils.localize("gui.securitycraft:invScan.check_inv", Utils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no"))).getFormattedText();

		if (hasRedstoneModule)
			redstoneModuleTooltip = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.redstoneModule.getTranslationKey() + ".name")).getFormattedText();

		if (hasStorageModule)
			xSize = 246;
		else {
			xSize = 190;
			storageModuleTooltip = Utils.localize("gui.securitycraft:invScan.notInstalled", Utils.localize(SCContent.storageModule.getTranslationKey() + ".name")).getFormattedText();
		}

		ySize = 196;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.disableLighting();
		fontRenderer.drawSplitString(infoStringRedstone, guiLeft + 28, guiTop + 45, 150, 4210752);
		fontRenderer.drawSplitString(infoStringStorage, guiLeft + 28, guiTop + 75, 150, 4210752);
		GuiUtils.renderModuleInfo(ModuleType.REDSTONE, redstoneModuleTooltip, hasRedstoneModule, guiLeft + 8, guiTop + 45, width, height, mouseX, mouseY);
		GuiUtils.renderModuleInfo(ModuleType.STORAGE, storageModuleTooltip, hasStorageModule, guiLeft + 8, guiTop + 75, width, height, mouseX, mouseY);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(Utils.localize("gui.securitycraft:invScan.prohibitedItems").getFormattedText(), 8, 6, 4210752);
		fontRenderer.drawString(tileEntity.isOwnedBy(playerObj) ? (TextFormatting.UNDERLINE + Utils.localize("gui.securitycraft:invScan.mode.admin").getFormattedText()) : (TextFormatting.UNDERLINE + Utils.localize("gui.securitycraft:invScan.mode.view").getFormattedText()), 112, 6, 4210752);

		if (hasStorageModule && owns)
			fontRenderer.drawString(Utils.localize("gui.securitycraft:invScan.storage").getFormattedText(), 188, 18, 4210752);

		fontRenderer.drawString(Utils.localize("container.inventory").getFormattedText(), 15, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(hasStorageModule && owns ? ENHANCED_INVENTORY : regularInventory);
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize + 30);
	}
}
