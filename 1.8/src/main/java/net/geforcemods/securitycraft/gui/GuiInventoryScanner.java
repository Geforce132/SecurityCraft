package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryScanner extends GuiContainer {
	private static final ResourceLocation regularInventory = new ResourceLocation("securitycraft:textures/gui/container/inventoryScannerGUI.png");
	private static final ResourceLocation exhancedInventory = new ResourceLocation("securitycraft:textures/gui/container/inventoryScannerEnhancedGUI.png");

	private TileEntityInventoryScanner tileEntity;
	private EntityPlayer playerObj;
	private boolean hasStorageModule = false;

	public GuiInventoryScanner(IInventory inventory, TileEntityInventoryScanner te, EntityPlayer player){
		super(new ContainerInventoryScanner(inventory, te));
		tileEntity = te;
		playerObj = player;
		hasStorageModule = ((CustomizableSCTE) te).hasModule(EnumCustomModules.STORAGE);

		if(hasStorageModule)
			xSize = 236;
		else
			xSize = 176;

		ySize = 196;
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);

		if(tileEntity.getOwner().isOwner(playerObj))
			buttonList.add(new GuiButton(0, width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 63, 166, 20, tileEntity.getType().contains("check") ? StatCollector.translateToLocal("gui.securitycraft:invScan.checkInv") : StatCollector.translateToLocal("gui.securitycraft:invScan.emitRedstone")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

		if(!buttonList.isEmpty()){
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.explanation.1"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 38, 4210752);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.explanation.2"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 28, 4210752);

			if(((GuiButton)buttonList.get(0)).displayString.equals(StatCollector.translateToLocal("gui.securitycraft:invScan.checkInv"))){
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.explanation.checkInv.3"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 18, 4210752);
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.explanation.checkInv.4"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 8, 4210752);
			}else{
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.explanation.emitRedstone.3"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 18, 4210752);
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.explanation.emitRedstone.4"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 8, 4210752);
			}
		}
		else if(tileEntity.getType() != null && tileEntity.getType() != ""){
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:invScan.setTo"), width / 2 - 83, height / 2 - 61, 4210752);
			fontRendererObj.drawString((tileEntity.getType().equals("check") ? StatCollector.translateToLocal("gui.securitycraft:invScan.checkInv") : StatCollector.translateToLocal("gui.securitycraft:invScan.emitRedstone")), width / 2 - 83, height / 2 - 51, 4210752);

		}

	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);

	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0:
				if(button.displayString.equals(StatCollector.translateToLocal("gui.securitycraft:invScan.checkInv")))
					button.displayString = StatCollector.translateToLocal("gui.securitycraft:invScan.emitRedstone");
				else if(button.displayString.equals(StatCollector.translateToLocal("gui.securitycraft:invScan.emitRedstone")))
					button.displayString = StatCollector.translateToLocal("gui.securitycraft:invScan.checkInv");

				saveType(button.displayString.equals(StatCollector.translateToLocal("gui.securitycraft:invScan.checkInv")) ? "check" : "redstone");

				break;
		}

	}

	private void saveType(String type){
		tileEntity.setType(type);
		SecurityCraft.network.sendToServer(new PacketSetISType(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), type));

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString("Prohibited Items", 8, 6, 4210752);
		fontRendererObj.drawString(tileEntity.getOwner().isOwner(playerObj) ? (EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("gui.securitycraft:invScan.mode.admin")) : (EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("gui.securitycraft:invScan.mode.view")), 112, 6, 4210752);

		if(hasStorageModule && tileEntity.getOwner().isOwner(playerObj))
			fontRendererObj.drawString("Storage", 183, 6, 4210752);

		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule)
			mc.getTextureManager().bindTexture(exhancedInventory);
		else
			mc.getTextureManager().bindTexture(regularInventory);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize + 30);
	}
}
