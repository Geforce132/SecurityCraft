package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public interface IReinforcedBlock
{
	public static final List<Block> BLOCKS = new ArrayList<>();

	public Block getVanillaBlock();

	public IBlockState getConvertedState(IBlockState vanillaState);
}
