package net.geforcemods.securitycraft.imc.waila;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICustomWailaDisplay {
	
	public ItemStack getDisplayStack(World world, int x, int y, int z);

	public boolean shouldShowSCInfo(World world, int x, int y, int z);
}
