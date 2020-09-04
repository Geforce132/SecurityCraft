package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedNetherrack extends BlockReinforcedBase
{
	public BlockReinforcedNetherrack(Material mat, int a, Block... vB)
	{
		super(mat, a, vB);
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.UP;
	}

}
