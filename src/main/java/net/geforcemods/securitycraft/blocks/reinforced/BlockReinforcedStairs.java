package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.blocks.BlockOwnableStairs;
import net.minecraft.block.Block;

public class BlockReinforcedStairs extends BlockOwnableStairs implements IReinforcedBlock
{
	private final Block vanillaBlock;

	public BlockReinforcedStairs(Block baseBlock, int meta, Block vanillaBlock) {
		super(baseBlock, meta);

		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(vanillaBlock);
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}
