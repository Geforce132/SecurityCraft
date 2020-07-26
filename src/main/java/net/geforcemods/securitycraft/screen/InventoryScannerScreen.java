package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InventoryScannerScreen extends ContainerScreen<InventoryScannerContainer> {
	private static final ResourceLocation REGULAR_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation ENHANCED_INVENTORY = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	private InventoryScannerTileEntity tileEntity;
	private boolean owns = false;
	private boolean hasStorageModule = false;
	private TranslationTextComponent storageString, redstoneString;
	private static final Style UNDERLINE = Style.EMPTY.applyFormatting(TextFormatting.UNDERLINE);

	public InventoryScannerScreen(InventoryScannerContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		tileEntity = container.te;
		owns = tileEntity.getOwner().isOwner(inv.player);
		hasStorageModule = tileEntity.hasModule(ModuleType.STORAGE);
		storageString = ClientUtils.localize("gui.securitycraft:invScan.check_inv", ClientUtils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no")));
		redstoneString = ClientUtils.localize("gui.securitycraft:invScan.emit_redstone", ClientUtils.localize("gui.securitycraft:invScan." + (tileEntity.hasModule(ModuleType.REDSTONE) ? "yes" : "no")));

		if(hasStorageModule)
			xSize = 236;
		else
			xSize = 176;

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

		font.func_238422_b_(matrix, redstoneString, guiLeft + 5, guiTop + 40, 4210752);
		font.func_238422_b_(matrix, storageString, guiLeft + 5, guiTop + 50, 4210752);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		//TODO: translate everything untranslated in here, also other mc versions
		font.func_238422_b_(matrix, new StringTextComponent("Prohibited Items"), 8, 6, 4210752);
		font.func_238422_b_(matrix, ClientUtils.localize("gui.securitycraft:invScan.mode." + (tileEntity.getOwner().isOwner(minecraft.player) ? "admin" : "view")).setStyle(UNDERLINE), 112, 6, 4210752);

		if(hasStorageModule && owns)
			font.func_238422_b_(matrix, new StringTextComponent("Storage"), 183, 6, 4210752);

		font.func_238422_b_(matrix, ClientUtils.localize("container.inventory"), 8, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule && owns)
			minecraft.getTextureManager().bindTexture(ENHANCED_INVENTORY);
		else
			minecraft.getTextureManager().bindTexture(REGULAR_INVENTORY);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize + 30);
	}
}
