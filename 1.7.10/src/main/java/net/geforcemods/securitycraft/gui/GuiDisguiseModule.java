package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerDisguiseModule;
import net.geforcemods.securitycraft.containers.ModuleInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiDisguiseModule extends GuiContainer {
	private static final ResourceLocation TEXTURE  = new ResourceLocation("securitycraft:textures/gui/container/customize1.png");

	public GuiDisguiseModule(EntityPlayer player, InventoryPlayer inventory) {
		super(new ContainerDisguiseModule(player, inventory, new ModuleInventory(player.getCurrentEquippedItem())));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(StatCollector.translateToLocal("item.securitycraft:disguiseModule.name"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("item.securitycraft:disguiseModule.name")) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

}
