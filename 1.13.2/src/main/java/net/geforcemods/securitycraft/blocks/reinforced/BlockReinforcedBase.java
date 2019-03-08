package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private Block vanillaBlock;

	public BlockReinforcedBase(Material mat, Block vB)
	{
		this(SoundType.STONE, mat, vB);
	}

	public BlockReinforcedBase(SoundType soundType, Material mat, Block vB)
	{
		super(soundType, Block.Properties.create(mat).hardnessAndResistance(-1.0F, 6000000.0F));

		vanillaBlock = vB;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}
}
