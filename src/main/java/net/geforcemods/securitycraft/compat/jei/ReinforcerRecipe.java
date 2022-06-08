package net.geforcemods.securitycraft.compat.jei;

import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.level.block.Block;

public class ReinforcerRecipe {
	private final Block vanillaBlock;
	private final Block securityCraftBlock;

	public ReinforcerRecipe(Block vanillaBlock, Block securityCraftBlock) {
		this.vanillaBlock = vanillaBlock;
		this.securityCraftBlock = securityCraftBlock;
	}

	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	public Block getSecurityCraftBlock() {
		return securityCraftBlock;
	}

	@Override
	public String toString() {
		return "Reinforcer Recipe - Vanilla Block: " + Utils.getRegistryName(getVanillaBlock()).toString() + " - Reinforced Block: " + Utils.getRegistryName(getSecurityCraftBlock()).toString();
	}
}
