package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.containers.KeypadSmokerContainer;
import net.minecraft.client.gui.recipebook.SmokerRecipeGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadSmokerScreen extends AbstractFurnaceScreen<KeypadSmokerContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

	public KeypadSmokerScreen(KeypadSmokerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, new SmokerRecipeGui(), inv, container.te.hasCustomName() ? container.te.getCustomName() : name, TEXTURE);
	}
}