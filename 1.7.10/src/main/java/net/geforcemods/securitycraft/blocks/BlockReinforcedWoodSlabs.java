package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockReinforcedWoodSlabs extends BlockSlab implements ITileEntityProvider {

    public static final String[] variants = new String[] {"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};

	public BlockReinforcedWoodSlabs(boolean isDouble) {
		super(isDouble, Material.wood);		
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
		
		if(!par1World.isRemote){
    		if(par5EntityLivingBase instanceof EntityPlayer){
    			((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
    		}
    	}
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(mod_SecurityCraft.reinforcedWoodSlabs, 2, par1 & 7);
    }

    public String func_150002_b(int par1){
        if(par1 < 0 || par1 >= variants.length){
        	par1 = 0;
        }

        return super.getUnlocalizedName() + "." + variants[par1];
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
        if(par1Item != Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks)){
            for(int i = 0; i < variants.length; i++){
            	par3List.add(new ItemStack(par1Item, 1, i));
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItem(World par1World, int par2, int par3, int par4){
        return Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs);
    }
    
    public Item getItemDropped(int par1, Random par2Random, int par3){
        return Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs);
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        return mod_SecurityCraft.reinforcedWoodPlanks.getIcon(par1, par2 & 7);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {}
    
    public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
