package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.containers.ContainerDisguiseModule;
import net.geforcemods.securitycraft.containers.ModuleInventory;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDisguiseModule extends GuiContainer {
	private static final ResourceLocation TEXTURE  = new ResourceLocation("securitycraft:textures/gui/container/customize1.png");

	public GuiDisguiseModule(EntityPlayer player, InventoryPlayer inventory) {
		super(new ContainerDisguiseModule(player, inventory, new ModuleInventory(player.inventory.getCurrentItem())));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(ClientUtils.localize("item.securitycraft:disguiseModule.name"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("item.securitycraft:disguiseModule.name")) / 2, 6, 4210752);
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
