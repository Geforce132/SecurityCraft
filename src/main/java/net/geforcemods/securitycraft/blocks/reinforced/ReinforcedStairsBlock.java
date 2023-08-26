package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableStairsBlock;
import net.minecraft.block.Block;

public class ReinforcedStairsBlock extends OwnableStairsBlock implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedStairsBlock(Block baseBlock, int meta, Block vanillaBlock) {
		super(baseBlock, meta);

		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(vanillaBlock);
	}

	@Override
	public int getAmount() {
		return 1;
	}
}
