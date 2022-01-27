package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.KeypadBlastFurnaceContainer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.recipebook.BlastFurnaceRecipeGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadBlastFurnaceScreen extends AbstractFurnaceScreen<KeypadBlastFurnaceContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/blast_furnace.png");

	public KeypadBlastFurnaceScreen(KeypadBlastFurnaceContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, new BlastFurnaceRecipeGui(), inv, container.te.hasCustomName() ? container.te.getCustomName() : Utils.localize(SCContent.KEYPAD_BLAST_FURNACE.get().getDescriptionId()), TEXTURE);
	}
}