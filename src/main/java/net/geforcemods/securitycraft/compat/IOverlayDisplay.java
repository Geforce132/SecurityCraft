package net.geforcemods.securitycraft.compat;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IOverlayDisplay {

	public ItemStack getDisplayStack(Level world, BlockState state, BlockPos pos);

	public boolean shouldShowSCInfo(Level world, BlockState state, BlockPos pos);
}
