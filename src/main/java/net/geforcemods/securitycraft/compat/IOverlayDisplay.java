package net.geforcemods.securitycraft.compat;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOverlayDisplay {
	public ItemStack getDisplayStack(World level, BlockState state, BlockPos pos);

	public boolean shouldShowSCInfo(World level, BlockState state, BlockPos pos);
}
