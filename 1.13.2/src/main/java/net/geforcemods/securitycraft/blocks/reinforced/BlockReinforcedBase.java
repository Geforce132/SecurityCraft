package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private List<Block> vanillaBlocks;
	private int amount;

	public BlockReinforcedBase(Material mat, int a, Block... vB)
	{
		this(SoundType.STONE, mat, a, vB);
	}

	public BlockReinforcedBase(SoundType soundType, Material mat, int a, Block... vB)
	{
		super(soundType, Block.Properties.create(mat).hardnessAndResistance(-1.0F, 6000000.0F));

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
