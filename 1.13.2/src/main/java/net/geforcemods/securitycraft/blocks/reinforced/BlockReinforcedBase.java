package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private Block vanillaBlock;

	public BlockReinforcedBase(Material mat, Block vB, String registryPath)
	{
		this(SoundType.STONE, mat, vB, registryPath);
	}

	public BlockReinforcedBase(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, Block.Properties.create(mat).hardnessAndResistance(-1.0F, 6000000.0F));

		vanillaBlock = vB;
		setRegistryName(new ResourceLocation(SecurityCraft.MODID, registryPath));
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}
}
