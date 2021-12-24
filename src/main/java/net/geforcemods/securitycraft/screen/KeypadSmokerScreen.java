package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeypadSmokerScreen extends AbstractFurnaceScreen<KeypadSmokerMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

	public KeypadSmokerScreen(KeypadSmokerMenu menu, Inventory inv, Component title) {
		super(menu, new SmokingRecipeBookComponent(), inv, menu.be.hasCustomName() ? menu.be.getCustomName() : title, TEXTURE);
	}
}