package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private final Block vanillaBlock;

	public BlockReinforcedBase(Material mat, Block vB, String registryPath)
	{
		this(mat, vB, registryPath, 0);
	}

	public BlockReinforcedBase(Material mat, Block vB, String registryPath, int lightValue)
	{
		this(SoundType.STONE, mat, vB, registryPath, lightValue);
	}

	public BlockReinforcedBase(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		this(soundType, mat, vB, registryPath, 0);
	}

	public BlockReinforcedBase(SoundType soundType, Material mat, Block vB, String registryPath, int lightValue)
	{
		super(soundType, Block.Properties.create(mat).hardnessAndResistance(-1.0F, 6000000.0F).lightValue(lightValue));

		vanillaBlock = vB;
		setRegistryName(new ResourceLocation(SecurityCraft.MODID, registryPath));
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}
}
