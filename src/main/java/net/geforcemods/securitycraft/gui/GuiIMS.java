package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SCContent;
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
	private final String imsName = ClientUtils.localize(SCContent.ims.getTranslationKey() + ".name").getFormattedText();
	private final String target = ClientUtils.localize("gui.securitycraft:ims.target").getFormattedText();
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

		buttonList.add(targetButton = new GuiButton(0, width / 2 - 75, height / 2 - 38, 150, 20, ""));
		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawString(imsName, xSize / 2 - fontRenderer.getStringWidth(imsName) / 2, 6, 4210752);
		fontRenderer.drawString(target, xSize / 2 - fontRenderer.getStringWidth(target) / 2, 30, 4210752);
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
		targetButton.displayString = ClientUtils.localize("gui.securitycraft:srat.targets" + (((targetMode.ordinal() + 2) % 3) + 1)).getFormattedText();
	}

}
