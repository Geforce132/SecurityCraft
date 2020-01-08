package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ReinforcedObsidianBlock extends BaseReinforcedBlock
{
	public ReinforcedObsidianBlock()
	{
		super(Material.ROCK, Blocks.OBSIDIAN, "reinforced_obsidian");
	}

	@Override
	public boolean isPortalFrame(BlockState state, IWorldReader world, BlockPos pos)
	{
		return true;
	}
}
