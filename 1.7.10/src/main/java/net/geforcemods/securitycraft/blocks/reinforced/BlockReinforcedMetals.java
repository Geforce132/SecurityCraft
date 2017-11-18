package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedMetals extends BlockOwnable implements ICustomWailaDisplay
{
	public BlockReinforcedMetals()
	{
		super(Material.iron);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
	{
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 2));
		p_149666_3_.add(new ItemStack(p_149666_1_, 1, 3));
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		switch(meta)
		{
			case 0: return Blocks.gold_block.getIcon(side, meta);
			case 1: return Blocks.iron_block.getIcon(side, meta);
			case 2: return Blocks.diamond_block.getIcon(side, meta);
			case 3: return Blocks.emerald_block.getIcon(side, meta);
			default: return null; //won't happen
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return getIcon(side, access.getBlockMetadata(x, y, z));
	}

	@Override
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int p_149741_1_)
	{
		return 0x999999;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x999999;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		return new ItemStack(Item.getItemFromBlock(this), 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z)
	{
		return new ItemStack(Item.getItemFromBlock(world.getBlock(x, y, z)), 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z)
	{
		return true;
	}
}
