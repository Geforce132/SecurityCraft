package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity.EnumIMSTargetingMode;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IMSScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String title;
	private final String target = Utils.localize("gui.securitycraft:ims.target").getFormattedText();
	private IMSBlockEntity tileEntity;
	private GuiButton targetButton;
	private EnumIMSTargetingMode targetMode;

	public IMSScreen(IMSBlockEntity te) {
		super(new GenericMenu(te));
		tileEntity = te;
		targetMode = tileEntity.getTargetingMode();
		title = te.getDisplayName().getFormattedText();
	}

	@Override
	public void initGui() {
		super.initGui();

		targetButton = addButton(new GuiButton(0, width / 2 - 75, height / 2 - 38, 150, 20, ""));
		updateButtonText();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
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
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			targetMode = EnumIMSTargetingMode.values()[(targetMode.ordinal() + 1) % EnumIMSTargetingMode.values().length]; //next enum value
			tileEntity.setTargetingMode(targetMode);
			ClientUtils.syncTileEntity(tileEntity);
			updateButtonText();
		}
	}

	private void updateButtonText() {
		targetButton.displayString = Utils.localize("gui.securitycraft:srat.targets" + (((targetMode.ordinal() + 2) % 3) + 1)).getFormattedText();
	}
}
