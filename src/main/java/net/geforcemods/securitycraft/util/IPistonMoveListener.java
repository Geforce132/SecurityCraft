package net.geforcemods.securitycraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IPistonMoveListener {
	void prePistonPushSideEffects(BlockPos pos, BlockState state);
}
