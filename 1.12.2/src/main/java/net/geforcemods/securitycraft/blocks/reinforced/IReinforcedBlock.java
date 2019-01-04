package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

public interface IReinforcedBlock
{
	public static final List<Block> BLOCKS = new ArrayList<>();

	public List<Block> getVanillaBlocks();

	public int getAmount();
}
