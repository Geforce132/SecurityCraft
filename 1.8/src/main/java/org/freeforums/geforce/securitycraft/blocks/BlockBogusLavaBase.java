package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBogusLavaBase extends BlockStaticLiquid{

    public BlockBogusLavaBase(Material p_i45429_1_){
    	super(p_i45429_1_);
    }
   
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
    	if(!par1World.isRemote){
    		
    		if(par5Entity instanceof EntityPlayer){
    			((EntityPlayer) par5Entity).heal(4);
    			((EntityPlayer) par5Entity).extinguish();
    		}
    	}
   }
   
   @SideOnly(Side.CLIENT)
   public Item getItem(World par1World, BlockPos pos){
	   return null;
   }
}