package net.geforcemods.securitycraft.compat.jei;

import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.level.block.Block;

public record ReinforcerRecipe(Block vanillaBlock, Block securityCraftBlock) {
	@Override
	public String toString() {
		return "Reinforcer Recipe - Vanilla Block: " + Utils.getRegistryName(vanillaBlock).toString() + " - Reinforced Block: " + Utils.getRegistryName(securityCraftBlock).toString();
	}
}
