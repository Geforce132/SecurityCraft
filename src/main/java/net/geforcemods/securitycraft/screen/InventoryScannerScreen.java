package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SetScanType;
import net.geforcemods.securitycraft.screen.components.ClickButton;
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
	private static final ResourceLocation regularInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation exhancedInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	private InventoryScannerTileEntity tileEntity;
	private boolean hasStorageModule = false;

	public InventoryScannerScreen(InventoryScannerContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		tileEntity = container.te;
		hasStorageModule = tileEntity.getOwner().isOwner(inv.player) && tileEntity.hasModule(ModuleType.STORAGE);

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

		if(tileEntity.getOwner().isOwner(minecraft.player))
			addButton(new ClickButton(0, width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 63, 166, 20, tileEntity.getScanType().contains("check") ? ClientUtils.localize("gui.securitycraft:invScan.checkInv") : ClientUtils.localize("gui.securitycraft:invScan.emitRedstone"), this::actionPerformed));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();

		if(!buttons.isEmpty()){
			font.drawString(ClientUtils.localize("gui.securitycraft:invScan.explanation.1"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 38, 4210752);
			font.drawString(ClientUtils.localize("gui.securitycraft:invScan.explanation.2"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 28, 4210752);

			if(buttons.get(0).getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.checkInv"))){
				font.drawString(ClientUtils.localize("gui.securitycraft:invScan.explanation.checkInv.3"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 18, 4210752);
				font.drawString(ClientUtils.localize("gui.securitycraft:invScan.explanation.checkInv.4"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 8, 4210752);
			}else{
				font.drawString(ClientUtils.localize("gui.securitycraft:invScan.explanation.emitRedstone.3"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 18, 4210752);
				font.drawString(ClientUtils.localize("gui.securitycraft:invScan.explanation.emitRedstone.4"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 8, 4210752);
			}
		}
		else if(tileEntity.getScanType() != null && !tileEntity.getScanType().equals("")){
			font.drawString(ClientUtils.localize("gui.securitycraft:invScan.setTo"), width / 2 - 83, height / 2 - 61, 4210752);
			font.drawString((tileEntity.getScanType().equals("check") ? ClientUtils.localize("gui.securitycraft:invScan.checkInv") : ClientUtils.localize("gui.securitycraft:invScan.emitRedstone")), width / 2 - 83, height / 2 - 51, 4210752);

		}

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	protected void actionPerformed(ClickButton button){
		if(button.id == 0){
			if(button.getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.checkInv")))
				button.setMessage(ClientUtils.localize("gui.securitycraft:invScan.emitRedstone"));
			else if(button.getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.emitRedstone")))
				button.setMessage(ClientUtils.localize("gui.securitycraft:invScan.checkInv"));

			saveType(button.getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.checkInv")) ? "check" : "redstone");
		}

	}

	private void saveType(String type){
		tileEntity.setScanType(type);
		SecurityCraft.channel.sendToServer(new SetScanType(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), type));

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		font.drawString("Prohibited Items", 8, 6, 4210752);
		font.drawString(tileEntity.getOwner().isOwner(minecraft.player) ? (TextFormatting.UNDERLINE + ClientUtils.localize("gui.securitycraft:invScan.mode.admin")) : (TextFormatting.UNDERLINE + ClientUtils.localize("gui.securitycraft:invScan.mode.view")), 112, 6, 4210752);

		if(hasStorageModule && tileEntity.getOwner().isOwner(minecraft.player))
			font.drawString("Storage", 183, 6, 4210752);

		font.drawString(ClientUtils.localize("container.inventory"), 8, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule)
			minecraft.getTextureManager().bindTexture(exhancedInventory);
		else
			minecraft.getTextureManager().bindTexture(regularInventory);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize + 30);
	}
}
