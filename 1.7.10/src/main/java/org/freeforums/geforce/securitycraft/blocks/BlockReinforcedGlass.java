package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReinforcedGlass extends BlockPane implements ITileEntityProvider{

	public BlockReinforcedGlass(String par2Str, String par3Str, Material par4Material, boolean par5) {
		super(par2Str, par3Str, par4Material, par5);
		ReflectionHelper.setPrivateValue(Block.class, this, true, 25);
	}
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);       
    }
    
    public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
    	if(p_149689_1_.isRemote){
    		return;
    	}else{
    		((TileEntityOwnable) p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).setOwner(((EntityPlayer) p_149689_5_).getGameProfile().getId().toString(), p_149689_5_.getCommandSenderName());
    	}
    }


    public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
    {
        super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
        p_149749_1_.removeTileEntity(p_149749_2_, p_149749_3_, p_149749_4_);
    }

    public boolean onBlockEventReceived(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_)
    {
        super.onBlockEventReceived(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_5_, p_149696_6_);
        TileEntity tileentity = p_149696_1_.getTileEntity(p_149696_2_, p_149696_3_, p_149696_4_);
        return tileentity != null ? tileentity.receiveClientEvent(p_149696_5_, p_149696_6_) : false;
    }
    
	
	@SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return HelpfulMethods.getItemFromBlock(this);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return HelpfulMethods.getItemFromBlock(this);
    }

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
