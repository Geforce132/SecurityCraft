package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedBookshelf extends BlockReinforcedBase
{
	public BlockReinforcedBookshelf(Material mat, int a, Block... vB)
	{
		super(mat, a, vB);

		setSoundType(SoundType.WOOD);
	}

	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos)
	{
		return 1.0F;
	}
}
