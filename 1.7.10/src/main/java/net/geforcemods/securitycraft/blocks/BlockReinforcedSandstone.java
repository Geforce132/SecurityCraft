package net.geforcemods.securitycraft.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockReinforcedSandstone extends BlockOwnable {
	
    private static final String[] sideIconSuffixes = new String[] {"normal", "carved", "smooth"};
    
    @SideOnly(Side.CLIENT)
    private IIcon[] sideIcons;
    
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
   
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockReinforcedSandstone(){
        super(Material.rock);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
    	par3List.add(new ItemStack(par1Item, 1, 0));
    	par3List.add(new ItemStack(par1Item, 1, 1));
    	par3List.add(new ItemStack(par1Item, 1, 2));
    }
    
    public int damageDropped(int par1){
        return par1;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
		return Blocks.sandstone.getIcon(par1, par2);
    }

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

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IIconRegister){
        this.sideIcons = new IIcon[sideIconSuffixes.length];

        for(int i = 0; i < this.sideIcons.length; i++){
            this.sideIcons[i] = par1IIconRegister.registerIcon(this.getTextureName() + "_" + sideIconSuffixes[i]);
        }

        this.topIcon = par1IIconRegister.registerIcon(this.getTextureName() + "_top");
        this.bottomIcon = par1IIconRegister.registerIcon(this.getTextureName() + "_bottom");
    }

}
