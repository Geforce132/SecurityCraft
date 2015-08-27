package net.breakinbad.securitycraft.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockReinforcedWood extends BlockOwnable {
	
	public static final String[] icons = new String[] {"oak", "spruce", "birch", "jungle", "acacia", "bigoak"};
    @SideOnly(Side.CLIENT)
    private IIcon[] icon;

	public BlockReinforcedWood(Material par1) {
		super(par1);
	}

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return par1;
    }
    
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        if (par2 < 0 || par2 >= this.icon.length)
        {
            par2 = 0;
        }
        
        return this.icon[par2];
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_){
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 0));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 1));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 2));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 3));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 4));
        p_149666_3_.add(new ItemStack(p_149666_1_, 1, 5));      
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_){
        this.icon = new IIcon[icons.length];

        for (int i = 0; i < this.icon.length; ++i)
        {
            this.icon[i] = p_149651_1_.registerIcon(this.getTextureName() + "_" + icons[i]);
        }
    }
	
}
