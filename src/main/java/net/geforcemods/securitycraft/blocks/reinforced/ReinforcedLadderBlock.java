package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;

public class ReinforcedLadderBlock extends BlockLadder implements IReinforcedBlock {
	public ReinforcedLadderBlock() {
		setSoundType(SoundType.WOOD);
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.LADDER);
	}

	@Override
	public int getAmount() {
		return 1;
	}
}
