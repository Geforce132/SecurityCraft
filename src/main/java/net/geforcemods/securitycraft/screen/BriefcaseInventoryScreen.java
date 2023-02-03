package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BriefcaseInventoryScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/briefcase_inventory.png");
	private String briefcaseName;

	public BriefcaseInventoryScreen(InventoryPlayer inventory, ItemStack briefcase) {
		super(new BriefcaseMenu(inventory, new BriefcaseContainer(briefcase)));

		this.briefcaseName = briefcase.getDisplayName();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(briefcaseName, xSize / 2 - fontRenderer.getStringWidth(briefcaseName) / 2, 6, 4210752);
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
}
