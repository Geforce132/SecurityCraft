package net.geforcemods.securitycraft.gui;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryScanner extends GuiContainer {
	private static final ResourceLocation regularInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_gui.png");
	private static final ResourceLocation exhancedInventory = new ResourceLocation("securitycraft:textures/gui/container/inventory_scanner_enhanced_gui.png");
	private TileEntityInventoryScanner tileEntity;
	private EntityPlayer playerObj;
	private boolean owns = false;
	private boolean hasStorageModule = false;
	private String infoString;

	public GuiInventoryScanner(InventoryPlayer inventory, TileEntityInventoryScanner te, EntityPlayer player){
		super(new ContainerInventoryScanner(inventory, te));
		tileEntity = te;
		playerObj = player;
		owns = tileEntity.getOwner().isOwner(playerObj);
		hasStorageModule = tileEntity.hasModule(EnumModuleType.STORAGE);
		infoString = ClientUtils.localize("gui.securitycraft:invScan.emit_redstone", ClientUtils.localize("gui.securitycraft:invScan." + (tileEntity.hasModule(EnumModuleType.REDSTONE) ? "yes" : "no")))
				.appendText("\n\n")
				.appendSibling(ClientUtils.localize("gui.securitycraft:invScan.check_inv", ClientUtils.localize("gui.securitycraft:invScan." + (hasStorageModule ? "yes" : "no")))).getFormattedText();

		if(hasStorageModule)
			xSize = 246;
		else
			xSize = 190;

		ySize = 196;
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

		fontRenderer.drawSplitString(infoString, guiLeft + 8, guiTop + 40, 170, 4210752);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:invScan.prohibitedItems").getFormattedText(), 8, 6, 4210752);
		fontRenderer.drawString(tileEntity.getOwner().isOwner(playerObj) ? (TextFormatting.UNDERLINE + ClientUtils.localize("gui.securitycraft:invScan.mode.admin").getFormattedText()) : (TextFormatting.UNDERLINE + ClientUtils.localize("gui.securitycraft:invScan.mode.view").getFormattedText()), 112, 6, 4210752);

		if(hasStorageModule && owns)
			fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:invScan.storage").getFormattedText(), 188, 18, 4210752);

		fontRenderer.drawString(ClientUtils.localize("container.inventory").getFormattedText(), 15, ySize - 93, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if(hasStorageModule && owns)
			mc.getTextureManager().bindTexture(exhancedInventory);
		else
			mc.getTextureManager().bindTexture(regularInventory);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize + 30);
	}
}
