package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.containers.BriefcaseInventory;
import net.geforcemods.securitycraft.containers.ContainerBriefcase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiBriefcaseInventory extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/briefcaseInventory.png");

	public GuiBriefcaseInventory(EntityPlayer player, InventoryPlayer inventory) {
		super(new ContainerBriefcase(player, inventory, new BriefcaseInventory(player.getCurrentEquippedItem())));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(StatCollector.translateToLocal("item.securitycraft:briefcase.name"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("item.securitycraft:briefcase.name")) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

}
