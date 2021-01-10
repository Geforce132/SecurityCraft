package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS.EnumIMSTargetingMode;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiIMS extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityIMS tileEntity;
	private GuiButton targetButton;
	private EnumIMSTargetingMode targetMode;

	public GuiIMS(InventoryPlayer inventory, TileEntityIMS te) {
		super(new ContainerGeneric(inventory, te));
		tileEntity = te;
		targetMode = tileEntity.getTargetingOption();
	}

	@Override
	public void initGui(){
		super.initGui();

		buttonList.add(targetButton = new GuiButton(0, width / 2 - 38, height / 2 - 58, 120, 20, ""));
		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(ClientUtils.localize("tile.securitycraft:ims.name").getFormattedText(), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("tile.securitycraft:ims.name").getFormattedText()) / 2, 6, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:ims.target").getFormattedText(), xSize / 2 - 78, 30, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button.id == 0){
			targetMode = EnumIMSTargetingMode.values()[(targetMode.ordinal() + 1) % EnumIMSTargetingMode.values().length]; //next enum value
			tileEntity.setTargetingOption(targetMode);
			ClientUtils.syncTileEntity(tileEntity);
			updateButtonText();
		}
	}

	private void updateButtonText() {
		if(targetMode == EnumIMSTargetingMode.PLAYERS)
			targetButton.displayString = ClientUtils.localize("tooltip.securitycraft:module.playerCustomization.players").getFormattedText();
		else if(targetMode == EnumIMSTargetingMode.PLAYERS_AND_MOBS)
			targetButton.displayString = ClientUtils.localize("gui.securitycraft:ims.hostileAndPlayers").getFormattedText();
		else if(targetMode == EnumIMSTargetingMode.MOBS)
			targetButton.displayString = ClientUtils.localize("gui.securitycraft:ims.hostile").getFormattedText();
	}

}
