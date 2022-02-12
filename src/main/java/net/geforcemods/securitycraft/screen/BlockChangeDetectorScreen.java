package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.inventory.GenericBEMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BlockChangeDetectorScreen extends AbstractContainerScreen<GenericBEMenu> {
	public BlockChangeDetectorScreen(GenericBEMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {}
}
