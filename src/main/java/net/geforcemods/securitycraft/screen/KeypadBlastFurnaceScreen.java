package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.BlastingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeypadBlastFurnaceScreen extends AbstractFurnaceScreen<KeypadBlastFurnaceMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/blast_furnace.png");

	public KeypadBlastFurnaceScreen(KeypadBlastFurnaceMenu menu, Inventory inv, Component title) {
		super(menu, new BlastingRecipeBookComponent(), inv, menu.be.hasCustomName() ? menu.be.getCustomName() : title, TEXTURE);
	}
}