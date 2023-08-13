package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public abstract class ItemInventoryScreen<T extends Container> extends GuiContainer {
	protected ResourceLocation texture;
	private final String title, inventoryName;

	protected ItemInventoryScreen(T menu, InventoryPlayer inventory, String title) {
		super(menu);
		this.title = title;
		inventoryName = inventory.getDisplayName().getUnformattedText();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
		fontRenderer.drawString(inventoryName, 8, ySize - 94, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	public static class Briefcase extends ItemInventoryScreen<BriefcaseMenu> {
		public Briefcase(BriefcaseMenu menu, InventoryPlayer inventory, String title) {
			super(menu, inventory, title);
			texture = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/briefcase_inventory.png");
		}
	}

	public static class KeycardHolder extends ItemInventoryScreen<KeycardHolderMenu> {
		public KeycardHolder(KeycardHolderMenu menu, InventoryPlayer inventory, String title) {
			super(menu, inventory, title);
			texture = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/keycard_holder.png");
			ySize = 133;
		}
	}
}
