package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiIMS extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private TileEntityIMS tileEntity;
	private GuiButton targetButton;
	private int targetingOption = 0;

	public GuiIMS(InventoryPlayer par1InventoryPlayer, TileEntityIMS par2TileEntity) {
		super(new ContainerGeneric(par1InventoryPlayer, par2TileEntity));
		tileEntity = par2TileEntity;
		targetingOption = tileEntity.getTargetingOption();
	}

	@Override
	public void initGui(){
		super.initGui();

		buttonList.add(targetButton = new GuiButton(0, width / 2 - 38, height / 2 - 58, 120, 20, tileEntity.getTargetingOption() == 1 ? StatCollector.translateToLocal("gui.ims.hostileAndPlayers") : StatCollector.translateToLocal("tooltip.module.players")));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){
		fontRendererObj.drawString(StatCollector.translateToLocal("tile.ims.name"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("tile.ims.name")) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.ims.target"), xSize / 2 - 78, 30, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				targetingOption++;

				if(targetingOption > 1)
					targetingOption = 0;

				tileEntity.setTargetingOption(targetingOption);

				ClientUtils.syncTileEntity(tileEntity);

				updateButtonText();
		}
	}

	private void updateButtonText() {
		if(targetingOption == 0)
			targetButton.displayString = StatCollector.translateToLocal("tooltip.module.playerCustomization.players");
		else if(targetingOption == 1)
			targetButton.displayString = StatCollector.translateToLocal("gui.ims.hostileAndPlayers");
	}

}
