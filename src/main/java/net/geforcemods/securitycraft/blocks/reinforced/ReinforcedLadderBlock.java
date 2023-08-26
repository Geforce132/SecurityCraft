package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ReinforcedLadderBlock extends LadderBlock implements IReinforcedBlock {
	public ReinforcedLadderBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.LADDER;
	}
}
