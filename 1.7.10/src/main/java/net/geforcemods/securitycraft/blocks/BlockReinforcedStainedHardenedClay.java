package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockReinforcedStainedHardenedClay extends BlockOwnable
{
	private static final IIcon[] iicons = new IIcon[16];

	public BlockReinforcedStainedHardenedClay()
	{
		super(Material.rock);
	}
	
	@SideOnly(Side.CLIENT)
	public static int func_149997_b(int par1){
		return ~par1 & 15;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
		for (int i = 0; i < iicons.length; ++i){
			par3List.add(new ItemStack(par1Item, 1, i));
		}
	}

	public int damageDropped(int par1){
		return par1;
	}

	public int quantityDropped(Random par1Random){
		return 1;
	}

	protected boolean canSilkHarvest(){
		return true;
	}

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
		return Blocks.stained_hardened_clay.getIcon(par1, par2);
    }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		return Blocks.stained_hardened_clay.getIcon(side, access.getBlockMetadata(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IIconRegister){
		for(int i = 0; i < iicons.length; ++i){
			iicons[i] = par1IIconRegister.registerIcon(this.getTextureName() + "_" + ItemDye.field_150921_b[func_149997_b(i)]);
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return 0x999999;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor(int p_149741_1_)
	{
		return 0x999999;
	}

	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x999999;
	}
}
