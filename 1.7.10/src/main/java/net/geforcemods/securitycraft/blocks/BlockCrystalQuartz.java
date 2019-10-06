package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockQuartz;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

public class BlockCrystalQuartz extends BlockQuartz
{
	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x15b3a2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x15b3a2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x15b3a2;
	}
}
