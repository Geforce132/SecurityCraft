package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedTintedGlassBlock extends ReinforcedGlassBlock {
	public ReinforcedTintedGlassBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return false;
	}

	@Override
	public int getLightBlock(BlockState state) {
		return 15;
	}
}
