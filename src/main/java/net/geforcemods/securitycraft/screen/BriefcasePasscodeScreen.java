package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BriefcasePasscodeScreen extends GuiContainer {
	public static final String UP_ARROW = "\u2191";
	public static final String RIGHT_ARROW = "\u2192";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final boolean isSetup;
	private final String title;
	private GuiButton[] keycodeTopButtons = new GuiButton[4];
	private GuiButton[] keycodeBottomButtons = new GuiButton[4];
	private GuiTextField[] keycodeTextboxes = new GuiTextField[4];
	private GuiButton continueButton;
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasscodeScreen(InventoryPlayer inventoryPlayer, boolean isSetup, String title) {
		super(new GenericMenu(inventoryPlayer, null));
		this.isSetup = isSetup;
		this.title = title;
	}

	@Override
	public void initGui() {
		super.initGui();

		for (int i = 0; i < keycodeTopButtons.length; i++) {
			keycodeTopButtons[i] = new GuiButton(i, width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, UP_ARROW);
			buttonList.add(keycodeTopButtons[i]);
		}

		for (int i = 0; i < keycodeBottomButtons.length; i++) {
			keycodeBottomButtons[i] = new GuiButton(4 + i, width / 2 - 40 + (i * 20), height / 2, 20, 20, DOWN_ARROW);
			buttonList.add(keycodeBottomButtons[i]);
		}

		continueButton = new GuiButton(8, (width / 2 + 42), height / 2 - 26, 20, 20, RIGHT_ARROW);
		buttonList.add(continueButton);

		for (int i = 0; i < keycodeTextboxes.length; i++) {
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

		for (GuiTextField textfield : keycodeTextboxes) {
			textfield.drawTextBox();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 8) {
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(mc.player, SCContent.briefcase);

			if (!briefcase.isEmpty()) {
				String passcode = digits[0] + "" + digits[1] + "" + digits[2] + "" + digits[3];

				if (isSetup) {
					SecurityCraft.network.sendToServer(new SetBriefcasePasscodeAndOwner(passcode));
					mc.player.openGui(SecurityCraft.instance, ScreenHandler.BRIEFCASE_INSERT_CODE_GUI_ID, Minecraft.getMinecraft().world, (int) Minecraft.getMinecraft().player.posX, (int) Minecraft.getMinecraft().player.posY, (int) Minecraft.getMinecraft().player.posZ);
				}
				else
					SecurityCraft.network.sendToServer(new CheckBriefcasePasscode(passcode));
			}
		}
		else {
			int index = button.id % 4;

			//java's modulo operator % does not handle negative numbers like it should for some reason, so floorMod needs to be used
			digits[index] = Math.floorMod((button.id > 3 ? --digits[index] : ++digits[index]), 10);
			keycodeTextboxes[index].setText(String.valueOf(digits[index]));
		}
	}
}
