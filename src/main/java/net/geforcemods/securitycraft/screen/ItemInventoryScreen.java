package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ItemInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
	protected ResourceLocation texture;

	protected ItemInventoryScreen(T menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderType::guiTextured, texture, leftPos, topPos, 0.0F, 0.0F, 0, 0, imageWidth, imageHeight);
	}

	public static class Briefcase extends ItemInventoryScreen<BriefcaseMenu> {
		public Briefcase(BriefcaseMenu menu, Inventory inventory, Component title) {
			super(menu, inventory, title);
			texture = SecurityCraft.resLoc("textures/gui/container/briefcase_inventory.png");
		}
	}

	public static class KeycardHolder extends ItemInventoryScreen<KeycardHolderMenu> {
		public KeycardHolder(KeycardHolderMenu menu, Inventory inventory, Component title) {
			super(menu, inventory, title);
			texture = SecurityCraft.resLoc("textures/gui/container/keycard_holder.png");
			imageHeight = 133;
		}
	}
}
