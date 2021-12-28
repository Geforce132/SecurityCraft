package net.geforcemods.securitycraft.screen;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.recipebook.FurnaceRecipeGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends AbstractFurnaceScreen<KeypadFurnaceContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

	public KeypadFurnaceScreen(KeypadFurnaceContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, new FurnaceRecipeGui(), inv, new Random().nextInt(100) < 5 ? new StringTextComponent("Keypad Gurnace") : (container.te.hasCustomName() ? container.te.getCustomName() : Utils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey())), TEXTURE);
	}
}