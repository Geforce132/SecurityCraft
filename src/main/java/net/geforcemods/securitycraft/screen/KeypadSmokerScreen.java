package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeypadSmokerScreen extends AbstractFurnaceScreen<KeypadSmokerMenu> {
	private static final ResourceLocation LIT_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/smoker/lit_progress");
	private static final ResourceLocation BURN_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/smoker/burn_progress");
	private static final ResourceLocation TEXTURE = SecurityCraft.mcResLoc("textures/gui/container/smoker.png");

	public KeypadSmokerScreen(KeypadSmokerMenu menu, Inventory inv, Component title) {
		super(menu, new SmokingRecipeBookComponent(), inv, menu.be.hasCustomName() ? menu.be.getCustomName() : title, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
	}
}