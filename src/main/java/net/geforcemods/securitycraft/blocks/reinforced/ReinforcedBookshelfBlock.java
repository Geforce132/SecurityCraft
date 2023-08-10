package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ReinforcedBookshelfBlock extends BaseReinforcedBlock {
	public ReinforcedBookshelfBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
		return 1.0F;
	}
}
