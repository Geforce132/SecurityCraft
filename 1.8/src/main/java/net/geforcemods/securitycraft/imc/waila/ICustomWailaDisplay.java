package net.geforcemods.securitycraft.imc.waila;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ICustomWailaDisplay {
	
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos);

	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos);
}
