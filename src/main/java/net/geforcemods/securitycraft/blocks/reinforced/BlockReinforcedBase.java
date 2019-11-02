package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private List<Block> vanillaBlocks;
	private int amount;

	public BlockReinforcedBase(Material mat, int a, Block... vB)
	{
		super(mat);

		vanillaBlocks = Arrays.asList(vB);
		amount = a;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return vanillaBlocks;
	}

	@Override
	public int getAmount()
	{
		return amount;
	}
}
