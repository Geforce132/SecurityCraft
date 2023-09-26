package net.geforcemods.securitycraft.compat.jei;

import net.minecraft.block.Block;

public class ReinforcerRecipe {
	private final Block vanillaBlock;
	private final Block securityCraftBlock;

	public ReinforcerRecipe(Block vanillaBlock, Block securityCraftBlock) {
		this.vanillaBlock = vanillaBlock;
		this.securityCraftBlock = securityCraftBlock;
	}

	public Block vanillaBlock() {
		return vanillaBlock;
	}

	public Block securityCraftBlock() {
		return securityCraftBlock;
	}

	@Override
	public String toString() {
		return "Reinforcer Recipe - Vanilla Block: " + vanillaBlock().getRegistryName().toString() + " - Reinforced Block: " + securityCraftBlock().getRegistryName().toString();
	}
}
