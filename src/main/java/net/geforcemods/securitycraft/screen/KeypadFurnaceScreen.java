package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.minecraft.client.gui.recipebook.FurnaceRecipeGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends AbstractFurnaceScreen<KeypadFurnaceMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

	public KeypadFurnaceScreen(KeypadFurnaceMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, new FurnaceRecipeGui(), inv, SecurityCraft.RANDOM.nextInt(100) < 5 ? new StringTextComponent("Keypad Gurnace") : (container.te.hasCustomName() ? container.te.getCustomName() : name), TEXTURE);
	}
}