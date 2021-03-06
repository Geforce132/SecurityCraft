package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSOpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBriefcase extends GuiContainer {

	public static final String UP_ARROW  = "\u2191";
	public static final String DOWN_ARROW  = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private GuiButton[] keycodeTopButtons = new GuiButton[4];
	private GuiButton[] keycodeBottomButtons = new GuiButton[4];
	private GuiTextField[] keycodeTextboxes = new GuiTextField[4];
	private GuiButton continueButton;
	private int[] digits = {0, 0, 0, 0};

	public GuiBriefcase(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
	}

	@Override
	public void initGui() {
		super.initGui();

		for(int i = 0; i < keycodeTopButtons.length; i++) {
			keycodeTopButtons[i] = new GuiButton(i, width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, UP_ARROW);
			buttonList.add(keycodeTopButtons[i]);
		}

		for(int i = 0; i < keycodeBottomButtons.length; i++) {
			keycodeBottomButtons[i] = new GuiButton(4 + i, width / 2 - 40 + (i * 20), height / 2, 20, 20, DOWN_ARROW);
			buttonList.add(keycodeBottomButtons[i]);
		}

		continueButton = new GuiButton(8, (width / 2 + 42), height / 2 - 26, 20, 20, ">");
		buttonList.add(continueButton);

		for(int i = 0; i < keycodeTextboxes.length; i++) {
			keycodeTextboxes[i] = new GuiTextField(9 + i, fontRenderer, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12);

			keycodeTextboxes[i].setTextColor(-1);
			keycodeTextboxes[i].setDisabledTextColour(-1);
			keycodeTextboxes[i].setEnableBackgroundDrawing(true);
			keycodeTextboxes[i].setMaxStringLength(1);
			keycodeTextboxes[i].setText("0");
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();

		for(GuiTextField textfield : keycodeTextboxes)
			textfield.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:briefcase.enterPasscode").getFormattedText(), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:briefcase.enterPasscode").getFormattedText()) / 2, 6, 4210752);
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
	protected void actionPerformed(GuiButton button) {
		if(button.id == 8)
		{
			if(PlayerUtils.isHoldingItem(Minecraft.getMinecraft().player, SCContent.briefcase, null)) {
				ItemStack briefcase = PlayerUtils.getSelectedItemStack(Minecraft.getMinecraft().player, SCContent.briefcase);
				NBTTagCompound nbt = briefcase.getTagCompound();
				String code = digits[0] + "" + digits[1] + "" +  digits[2] + "" + digits[3];

				if(nbt.getString("passcode").equals(code)) {
					if (!nbt.hasKey("owner")) {
						nbt.setString("owner", Minecraft.getMinecraft().player.getName());
						nbt.setString("ownerUUID", Minecraft.getMinecraft().player.getUniqueID().toString());
					}

					SecurityCraft.network.sendToServer(new PacketSOpenGui(GuiHandler.BRIEFCASE_GUI_ID, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ));
				}
			}
		}
		else
		{
			int index = button.id % 4;

			//java's modulo operator % does not handle negative numbers like it should for some reason, so floorMod needs to be used
			digits[index] = Math.floorMod((button.id > 3 ? --digits[index] : ++digits[index]), 10);
			keycodeTextboxes[index].setText(String.valueOf(digits[index]));
		}
	}
}
