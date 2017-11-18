package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSetKeycardLevel;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiKeycardSetup extends GuiContainer{

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntityKeycardReader keypadInventory;
	private GuiButton lvlOfSecurityButton;
	private GuiButton requiresExactCardButton;
	private boolean requiresExactCard = false;
	private int lvlOfSecurity = 0;

	public GuiKeycardSetup(InventoryPlayer inventory, TileEntityKeycardReader tile_entity) {
		super(new ContainerGeneric(inventory, tile_entity));
		keypadInventory = tile_entity;
	}

	@Override
	public void initGui(){
		super.initGui();

		buttonList.add(lvlOfSecurityButton = new GuiButton(0, width / 2 - (48 * 2 - 23), height / 2 + 20, 150, 20, ""));
		buttonList.add(requiresExactCardButton = new GuiButton(1, width / 2 - (48 * 2 - 11), height / 2 - 28, 125, 20, requiresExactCard ? ClientUtils.localize("gui.keycardSetup.equal") : ClientUtils.localize("gui.keycardSetup.equalOrHigher")));
		buttonList.add(new GuiButton(2, width / 2 - 48, height / 2 + 30 + 20, 100, 20, ClientUtils.localize("gui.keycardSetup.save")));

		updateButtonText();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.1"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.keycardSetup.explanation.1")) / 2, 6, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.2"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.keycardSetup.explanation.2")) / 2 - 2, 30 - 10, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.3"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.keycardSetup.explanation.3")) / 2 - 11, 42 - 10, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.4"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.keycardSetup.explanation.4")) / 2 - 10, 54 - 10, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.5"), xSize / 2 + 45, 66 - 5, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.6"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.keycardSetup.explanation.6")) / 2 - 6, 78 - 1, 4210752);
		fontRenderer.drawString(ClientUtils.localize("gui.keycardSetup.explanation.7"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.keycardSetup.explanation.7")) / 2 - 20, 90 - 1, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	private void updateButtonText(){
		lvlOfSecurity++;
		if(lvlOfSecurity <= 5)
			lvlOfSecurityButton.displayString = ClientUtils.localize("gui.keycardSetup.lvlNeeded") + " " + lvlOfSecurity;
		else{
			lvlOfSecurity = 1;
			lvlOfSecurityButton.displayString = ClientUtils.localize("gui.keycardSetup.lvlNeeded") + " " + lvlOfSecurity;

		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
			case 0:
				updateButtonText();
				break;

			case 1:
				requiresExactCard = !requiresExactCard;
				requiresExactCardButton.displayString = requiresExactCard ? ClientUtils.localize("gui.keycardSetup.equal") : ClientUtils.localize("gui.keycardSetup.equalOrHigher");
				break;

			case 2:
				saveLVs();
				break;
		}
	}

	private void saveLVs() {
		keypadInventory.setPassword(String.valueOf(lvlOfSecurity));
		keypadInventory.setRequiresExactKeycard(requiresExactCard);

		mod_SecurityCraft.network.sendToServer(new PacketSetKeycardLevel(keypadInventory.getPos().getX(), keypadInventory.getPos().getY(), keypadInventory.getPos().getZ(), lvlOfSecurity, requiresExactCard));

		Minecraft.getMinecraft().player.closeScreen();
	}

}
