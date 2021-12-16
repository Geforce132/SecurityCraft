package net.geforcemods.securitycraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickingBlockEntity
{
	default void tick(Level level, BlockPos pos, BlockState state) {}
}
