package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class ReinforcedGlassBlock extends BaseReinforcedBlock
{
	public ReinforcedGlassBlock(Block vB, String registryPath)
	{
		super(SoundType.GLASS, Material.GLASS, vB, registryPath);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}
}
