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

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private TileEntityIMS tileEntity;
	private GuiButton targetButton;
	private int targetingOption = 0;

	public GuiIMS(InventoryPlayer playerInv, TileEntityIMS te) {
		super(new ContainerGeneric(playerInv, te));
		tileEntity = te;
		targetingOption = tileEntity.getTargetingOption();
	}

	@Override
	public void initGui(){
		super.initGui();

		buttonList.add(targetButton = new GuiButton(0, width / 2 - 38, height / 2 - 58, 120, 20, tileEntity.getTargetingOption() == 1 ? StatCollector.translateToLocal("gui.securitycraft:ims.hostileAndPlayers") : StatCollector.translateToLocal("tooltip.securitycraft:module.players")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(StatCollector.translateToLocal("tile.securitycraft:ims.name"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("tile.securitycraft:ims.name")) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:ims.target"), xSize / 2 - 78, 30, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
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
			targetButton.displayString = StatCollector.translateToLocal("tooltip.securitycraft:module.playerCustomization.players");
		else if(targetingOption == 1)
			targetButton.displayString = StatCollector.translateToLocal("gui.securitycraft:ims.hostileAndPlayers");
	}

}
