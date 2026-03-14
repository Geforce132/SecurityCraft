package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class ItemInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
	protected Identifier texture;

	protected ItemInventoryScreen(T menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	protected ItemInventoryScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
		super(menu, inventory, title, imageWidth, imageHeight);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
		extractTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	public static class Briefcase extends ItemInventoryScreen<BriefcaseMenu> {
		public Briefcase(BriefcaseMenu menu, Inventory inventory, Component title) {
			super(menu, inventory, title);
			texture = SecurityCraft.resLoc("textures/gui/container/briefcase_inventory.png");
		}
	}

	public static class KeycardHolder extends ItemInventoryScreen<KeycardHolderMenu> {
		public KeycardHolder(KeycardHolderMenu menu, Inventory inventory, Component title) {
			super(menu, inventory, title, 176, 133);
			texture = SecurityCraft.resLoc("textures/gui/container/keycard_holder.png");
		}
	}
}
