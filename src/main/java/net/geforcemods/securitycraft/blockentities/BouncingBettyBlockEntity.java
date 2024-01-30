package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BouncingBettyBlockEntity extends MineBlockEntity {
	public BouncingBettyBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}
}
