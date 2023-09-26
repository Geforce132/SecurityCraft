package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.minecraft.client.gui.recipebook.SmokerRecipeGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadSmokerScreen extends AbstractFurnaceScreen<KeypadSmokerMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

	public KeypadSmokerScreen(KeypadSmokerMenu menu, PlayerInventory inv, ITextComponent title) {
		super(menu, new SmokerRecipeGui(), inv, menu.be.hasCustomName() ? menu.be.getCustomName() : title, TEXTURE);
	}
}