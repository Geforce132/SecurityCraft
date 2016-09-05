package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.containers.ContainerModuleCustomization;
import net.geforcemods.securitycraft.containers.ModuleInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiModuleInventory extends GuiContainer {
	
	private int numberOfSlots = 0;

	public GuiModuleInventory(EntityPlayer player, InventoryPlayer inventory) {
		super(new ContainerModuleCustomization(player, inventory, new ModuleInventory(player.getCurrentEquippedItem())));
		numberOfSlots = new ModuleInventory(player.getCurrentEquippedItem()).SIZE;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString(StatCollector.translateToLocal("item.disguiseModule.name"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("item.disguiseModule.name")) / 2, 6, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("securitycraft:textures/gui/container/customize" + numberOfSlots + ".png"));
		int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

}
