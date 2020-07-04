package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
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
	private boolean hasStorageModule = false;
	private String storageString, redstoneString;

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
	public void func_231160_c_(){
		super.func_231160_c_();
		field_230706_i_.keyboardListener.enableRepeatEvents(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();

		field_230712_o_.drawString(redstoneString, guiLeft + 5, guiTop + 40, 4210752);
		field_230712_o_.drawString(storageString, guiLeft + 5, guiTop + 50, 4210752);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	public void func_231175_as__(){
		super.func_231175_as__();
		field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		field_230712_o_.drawString("Prohibited Items", 8, 6, 4210752);
		field_230712_o_.drawString(tileEntity.getOwner().isOwner(field_230706_i_.player) ? (TextFormatting.UNDERLINE + ClientUtils.localize("gui.securitycraft:invScan.mode.admin")) : (TextFormatting.UNDERLINE + ClientUtils.localize("gui.securitycraft:invScan.mode.view")), 112, 6, 4210752);

		if(hasStorageModule && owns)
			field_230712_o_.drawString("Storage", 183, 6, 4210752);

		field_230712_o_.drawString(ClientUtils.localize("container.inventory"), 8, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		func_230446_a_();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule && owns)
			field_230706_i_.getTextureManager().bindTexture(ENHANCED_INVENTORY);
		else
			field_230706_i_.getTextureManager().bindTexture(REGULAR_INVENTORY);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize + 30);
	}
}
