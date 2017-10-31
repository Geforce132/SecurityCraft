package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockReinforcedStainedGlassPanes extends BlockStainedGlassPane implements ITileEntityProvider {

	private static final IIcon[] paneTextures = new IIcon[16];
    private static final IIcon[] topPaneTextures = new IIcon[16];
    
	public BlockReinforcedStainedGlassPanes() {
		super();
		ObfuscationReflectionHelper.setPrivateValue(BlockPane.class, this, "glass_reinforced", 2);
		ObfuscationReflectionHelper.setPrivateValue(BlockPane.class, this, "glass_reinforced_pane_top", 0);
	}
	
    public int damageDropped(int par1){
        return par1;
    }
    
    @Override
    public int quantityDropped(Random random)
    {
    	return 1;
    }
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        par1World.removeTileEntity(par2, par3, par4);
	}

	@SideOnly(Side.CLIENT)
	public IIcon func_149735_b(int par1, int par2){
		return paneTextures[par2 % paneTextures.length];
	}

	@SideOnly(Side.CLIENT)
	public IIcon func_150104_b(int par1){
		return topPaneTextures[~par1 & 15];
	}
	
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
        for (int i = 0; i < paneTextures.length; i++){
            par3List.add(new ItemStack(par1Item, 1, i));
        }
    }

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IIconRegister){
        for(int i = 0; i < paneTextures.length; ++i){
        	paneTextures[i] = par1IIconRegister.registerIcon(this.getTextureName() + "_" + ItemDye.field_150921_b[func_150103_c(i)]);
        	topPaneTextures[i] = par1IIconRegister.registerIcon(this.getTextureName() + "_pane_top_" + ItemDye.field_150921_b[func_150103_c(i)]);
        }
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
    	return Item.getItemFromBlock(this);
    }
}
