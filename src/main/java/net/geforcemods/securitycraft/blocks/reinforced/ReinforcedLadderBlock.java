package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;

public class ReinforcedLadderBlock extends LadderBlock implements IReinforcedBlock {
	public ReinforcedLadderBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.LADDER;
	}
}
