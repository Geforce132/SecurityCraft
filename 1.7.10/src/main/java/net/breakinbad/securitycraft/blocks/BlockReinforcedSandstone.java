package net.breakinbad.securitycraft.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockReinforcedSandstone extends Block {
	
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

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        if(par1 != 1 && (par1 != 0 || par2 != 1 && par2 != 2)){
            if(par1 == 0){
                return this.bottomIcon;
            }else{
                if(par2 < 0 || par2 >= this.sideIcons.length){
                    par2 = 0;
                }

                return this.sideIcons[par2];
            }
        }else{
            return this.topIcon;
        }
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
