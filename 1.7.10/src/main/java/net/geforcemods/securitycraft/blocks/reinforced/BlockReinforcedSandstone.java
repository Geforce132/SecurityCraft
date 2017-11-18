package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockReinforcedSandstone extends BlockOwnable {
	public BlockReinforcedSandstone(){
		super(Material.rock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
		par3List.add(new ItemStack(par1Item, 1, 0));
		par3List.add(new ItemStack(par1Item, 1, 1));
		par3List.add(new ItemStack(par1Item, 1, 2));
	}

	@Override
	public int damageDropped(int par1){
		return par1;
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2){
		return Blocks.sandstone.getIcon(par1, par2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return Blocks.sandstone.getIcon(side, access.getBlockMetadata(x, y, z));
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
}
