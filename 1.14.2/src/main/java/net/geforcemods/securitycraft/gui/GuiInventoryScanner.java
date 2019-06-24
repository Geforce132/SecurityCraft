package net.geforcemods.securitycraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.server.SetScanType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiInventoryScanner extends ContainerScreen<ContainerInventoryScanner> {
	private static final ResourceLocation regularInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation exhancedInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");

	private TileEntityInventoryScanner tileEntity;
	private boolean hasStorageModule = false;

	public GuiInventoryScanner(ContainerInventoryScanner container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		tileEntity = container.te;
		hasStorageModule = tileEntity.hasModule(EnumCustomModules.STORAGE);

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
			addButton(new GuiButtonClick(0, width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 63, 166, 20, tileEntity.getScanType().contains("check") ? ClientUtils.localize("gui.securitycraft:invScan.checkInv") : ClientUtils.localize("gui.securitycraft:invScan.emitRedstone"), this::actionPerformed));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

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
		else if(tileEntity.getScanType() != null && tileEntity.getScanType() != ""){
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

	protected void actionPerformed(GuiButtonClick button){
		switch(button.id){
			case 0:
				if(button.getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.checkInv")))
					button.setMessage(ClientUtils.localize("gui.securitycraft:invScan.emitRedstone"));
				else if(button.getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.emitRedstone")))
					button.setMessage(ClientUtils.localize("gui.securitycraft:invScan.checkInv"));

				saveType(button.getMessage().equals(ClientUtils.localize("gui.securitycraft:invScan.checkInv")) ? "check" : "redstone");

				break;
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

		font.drawString(ClientUtils.localize("container.inventory", new Object[0]), 8, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule)
			minecraft.getTextureManager().bindTexture(exhancedInventory);
		else
			minecraft.getTextureManager().bindTexture(regularInventory);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize + 30);
	}
}
