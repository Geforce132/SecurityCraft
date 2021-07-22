package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedBookshelfBlock extends BaseReinforcedBlock
{
	public ReinforcedBookshelfBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos)
	{
		return 1.0F;
	}
}
