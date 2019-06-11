package net.geforcemods.securitycraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.network.server.SetKeycardLevel;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiKeycardSetup extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityKeycardReader keypadInventory;
	private GuiButton lvlOfSecurityButton;
	private GuiButton requiresExactCardButton;
	private boolean requiresExactCard = false;
	private int lvlOfSecurity = 0;

	public GuiKeycardSetup(TileEntityKeycardReader tile_entity) {
		super(new ContainerGeneric());
		keypadInventory = tile_entity;
	}

	@Override
	public void initGui(){
		super.initGui();

		addButton(lvlOfSecurityButton = new GuiButtonClick(0, width / 2 - (48 * 2 - 23), height / 2 + 20, 150, 20, "", this::actionPerformed));
		addButton(requiresExactCardButton = new GuiButtonClick(1, width / 2 - (48 * 2 - 11), height / 2 - 28, 125, 20, requiresExactCard ? ClientUtils.localize("gui.securitycraft:keycardSetup.equal") : ClientUtils.localize("gui.securitycraft:keycardSetup.equalOrHigher"), this::actionPerformed));
		addButton(new GuiButtonClick(2, width / 2 - 48, height / 2 + 30 + 20, 100, 20, ClientUtils.localize("gui.securitycraft:keycardSetup.save"), this::actionPerformed));

		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.1"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.1")) / 2, 6, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.2"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.2")) / 2 - 2, 30 - 10, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.3"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.3")) / 2 - 11, 42 - 10, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.4"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.4")) / 2 - 10, 54 - 10, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.5"), xSize / 2 + 45, 66 - 5, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.6"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.6")) / 2 - 6, 78 - 1, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.7"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:keycardSetup.explanation.7")) / 2 - 20, 90 - 1, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	private void updateButtonText(){
		lvlOfSecurity++;
		if(lvlOfSecurity <= 5)
			lvlOfSecurityButton.displayString = ClientUtils.localize("gui.securitycraft:keycardSetup.lvlNeeded") + " " + lvlOfSecurity;
		else{
			lvlOfSecurity = 1;
			lvlOfSecurityButton.displayString = ClientUtils.localize("gui.securitycraft:keycardSetup.lvlNeeded") + " " + lvlOfSecurity;

		}
	}

	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0:
				updateButtonText();
				break;

			case 1:
				requiresExactCard = !requiresExactCard;
				requiresExactCardButton.displayString = requiresExactCard ? ClientUtils.localize("gui.securitycraft:keycardSetup.equal") : ClientUtils.localize("gui.securitycraft:keycardSetup.equalOrHigher");
				break;

			case 2:
				saveLvls();
				break;
		}
	}

	private void saveLvls() {
		keypadInventory.setPassword(String.valueOf(lvlOfSecurity));
		keypadInventory.setRequiresExactKeycard(requiresExactCard);

		SecurityCraft.channel.sendToServer(new SetKeycardLevel(keypadInventory.getPos().getX(), keypadInventory.getPos().getY(), keypadInventory.getPos().getZ(), lvlOfSecurity, requiresExactCard));

		Minecraft.getInstance().player.closeScreen();
	}

}
