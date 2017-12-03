package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
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

	public GuiInventoryScanner(IInventory par1IInventory, TileEntityInventoryScanner par2TileEntity, EntityPlayer par3EntityPlayer){
		super(new ContainerInventoryScanner(par1IInventory, par2TileEntity));
		tileEntity = par2TileEntity;
		playerObj = par3EntityPlayer;
		hasStorageModule = ((CustomizableSCTE) par2TileEntity).hasModule(EnumCustomModules.STORAGE);

		if(hasStorageModule)
			xSize = 234;
		else
			xSize = 176;

		ySize = 196;
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);

		if(tileEntity.getOwner().isOwner(playerObj))
			buttonList.add(new GuiButton(0, width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 63, 166, 20, tileEntity.getType().contains("check") ? StatCollector.translateToLocal("gui.invScan.checkInv") : StatCollector.translateToLocal("gui.invScan.emitRedstone")));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GL11.glDisable(GL11.GL_LIGHTING);

		if(!buttonList.isEmpty()){
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.explanation.1"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 38, 4210752);
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.explanation.2"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 28, 4210752);

			if(buttonList.get(0).displayString.matches(StatCollector.translateToLocal("gui.invScan.checkInv"))){
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.explanation.checkInv.3"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 18, 4210752);
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.explanation.checkInv.4"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 8, 4210752);
			}else{
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.explanation.emitRedstone.3"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 18, 4210752);
				fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.explanation.emitRedstone.4"), width / 2 - 83 - (hasStorageModule ? 28 : 0), height / 2 - 8, 4210752);
			}
		}
		else if(tileEntity.getType() != null && tileEntity.getType() != ""){
			fontRendererObj.drawString(StatCollector.translateToLocal("gui.invScan.setTo"), width / 2 - 83, height / 2 - 61, 4210752);
			fontRendererObj.drawString((tileEntity.getType().matches("check") ? StatCollector.translateToLocal("gui.invScan.checkInv") : StatCollector.translateToLocal("gui.invScan.emitRedstone")), width / 2 - 83, height / 2 - 51, 4210752);

		}

	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException{
		super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException{
		super.mouseClicked(par1, par2, par3);

	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				if(guibutton.displayString.matches(StatCollector.translateToLocal("gui.invScan.checkInv")))
					guibutton.displayString = StatCollector.translateToLocal("gui.invScan.emitRedstone");
				else if(guibutton.displayString.matches(StatCollector.translateToLocal("gui.invScan.emitRedstone")))
					guibutton.displayString = StatCollector.translateToLocal("gui.invScan.checkInv");

				saveType(guibutton.displayString.matches(StatCollector.translateToLocal("gui.invScan.checkInv")) ? "check" : "redstone");

				break;
		}

	}

	private void saveType(String type){
		tileEntity.setType(type);
		mod_SecurityCraft.network.sendToServer(new PacketSetISType(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), type));

	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRendererObj.drawString("Prohibited Items", 8, 6, 4210752);
		fontRendererObj.drawString(tileEntity.getOwner().isOwner(playerObj) ? (EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("gui.invScan.mode.admin")) : (EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("gui.invScan.mode.view")), 112, 6, 4210752);

		if(hasStorageModule && tileEntity.getOwner().isOwner(playerObj))
			fontRendererObj.drawString("Storage", 183, 6, 4210752);

		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule)
			mc.getTextureManager().bindTexture(exhancedInventory);
		else
			mc.getTextureManager().bindTexture(regularInventory);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize + 30);
	}
}
