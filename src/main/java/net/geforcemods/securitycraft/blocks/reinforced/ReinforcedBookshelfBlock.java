package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedBookshelfBlock extends BaseReinforcedBlock {
	public ReinforcedBookshelfBlock(Block... vB) {
		super(vB);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos) {
		return 1.0F;
	}
}
