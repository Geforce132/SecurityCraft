package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.minecraft.client.gui.recipebook.BlastFurnaceRecipeGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadBlastFurnaceScreen extends AbstractFurnaceScreen<KeypadBlastFurnaceMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/blast_furnace.png");

	public KeypadBlastFurnaceScreen(KeypadBlastFurnaceMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, new BlastFurnaceRecipeGui(), inv, container.te.hasCustomName() ? container.te.getCustomName() : name, TEXTURE);
	}
}