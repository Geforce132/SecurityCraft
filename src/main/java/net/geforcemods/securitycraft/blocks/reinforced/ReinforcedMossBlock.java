package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ReinforcedMossBlock extends BaseReinforcedBlock {
	public ReinforcedMossBlock(BlockBehaviour.Properties properties, Block vanillaBlock) {
		super(properties, vanillaBlock);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	}
}
