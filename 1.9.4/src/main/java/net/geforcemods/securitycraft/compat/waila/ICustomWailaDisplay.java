package net.geforcemods.securitycraft.compat.waila;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICustomWailaDisplay {

	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos);

	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos);
}
