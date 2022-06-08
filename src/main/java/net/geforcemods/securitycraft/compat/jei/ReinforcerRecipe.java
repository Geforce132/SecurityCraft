package net.geforcemods.securitycraft.compat.jei;

import net.minecraft.world.level.block.Block;

public record ReinforcerRecipe(Block vanillaBlock, Block securityCraftBlock) {
	@Override
	public String toString() {
		return "Reinforcer Recipe - Vanilla Block: " + vanillaBlock.getRegistryName().toString() + " - Reinforced Block: " + securityCraftBlock.getRegistryName().toString();
	}
}
