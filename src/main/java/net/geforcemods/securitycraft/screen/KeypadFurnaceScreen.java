package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeypadFurnaceScreen extends AbstractFurnaceScreen<KeypadFurnaceMenu> {
	private static final ResourceLocation LIT_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/furnace/lit_progress");
	private static final ResourceLocation BURN_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/furnace/burn_progress");
	private static final ResourceLocation TEXTURE = SecurityCraft.mcResLoc("textures/gui/container/furnace.png");

	public KeypadFurnaceScreen(KeypadFurnaceMenu menu, Inventory inv, Component title) {
		super(menu, new SmeltingRecipeBookComponent(), inv, SecurityCraft.RANDOM.nextInt(100) < 5 ? Component.literal("Keypad Gurnace") : (menu.be.hasCustomName() ? menu.be.getCustomName() : title), TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
	}
}