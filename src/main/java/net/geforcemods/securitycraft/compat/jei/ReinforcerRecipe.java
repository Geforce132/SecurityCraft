package net.geforcemods.securitycraft.compat.jei;

import net.minecraft.block.Block;

public class ReinforcerRecipe
{
	private final Block vanillaBlock;
	private final Block securityCraftBlock;

	public ReinforcerRecipe(Block vanillaBlock, Block securityCraftBlock)
	{
		this.vanillaBlock = vanillaBlock;
		this.securityCraftBlock = securityCraftBlock;
	}

	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	public Block getSecurityCraftBlock()
	{
		return securityCraftBlock;
	}
}
