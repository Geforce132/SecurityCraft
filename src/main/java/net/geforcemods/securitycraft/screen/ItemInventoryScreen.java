package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class ItemInventoryScreen<T extends Container> extends ContainerScreen<T> {
	protected ResourceLocation texture;

	public ItemInventoryScreen(T menu, PlayerInventory inventory, ITextComponent title) {
		super(menu, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		minecraft.textureManager.bind(texture);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	public static class Briefcase extends ItemInventoryScreen<BriefcaseMenu> {
		public Briefcase(BriefcaseMenu menu, PlayerInventory inventory, ITextComponent title) {
			super(menu, inventory, title);
			texture = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/briefcase_inventory.png");
		}
	}

	public static class KeycardHolder extends ItemInventoryScreen<KeycardHolderMenu> {
		public KeycardHolder(KeycardHolderMenu menu, PlayerInventory inventory, ITextComponent title) {
			super(menu, inventory, title);
			texture = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/keycard_holder.png");
			imageHeight = 133;
		}
	}
}
