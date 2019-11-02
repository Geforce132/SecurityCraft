package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockFullMineBase extends Block{
	
	

	public BlockFullMineBase(Material par2Material) {
		super(par2Material);

	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
    	if(par1World.isRemote){
    		return;
    	}else{
    	
    		if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem){
    			return;
    		}else{
    			this.explode(par1World, par2, par3, par4);
    		}
    		
    	}
    
    	
    }
   
   /**
    * Called upon the block being destroyed by an explosion
    */
   public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion)
   {
       if (!par1World.isRemote)
       {
           this.explode(par1World, par2, par3, par4);
       }
   }
   
   public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
	   if (!par1World.isRemote)
	   {
		   this.explode(par1World, par2, par3, par4);
	   }
   }	
    
    

	private void explode(World par1World, int par2, int par3, int par4) {
		par1World.func_147480_a(par2, par3, par4, false);
		
        if(mod_SecurityCraft.configHandler.smallerMineExplosion){
            par1World.createExplosion((Entity)null, par2, (double)par3 + 0.5D, par4, 2.5F, true);
        }else{
            par1World.createExplosion((Entity)null, par2, (double)par3 + 0.5D, par4, 5.0F, true);
        }
	}
	
	/**
     * Return whether this block can drop from an explosion.
     */
    public boolean canDropFromExplosion(Explosion par1Explosion)
    {
        return false;
    }
    
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
    
    public Item getItemDropped(int par1, Random par2Random, int par3){
    	return null;
    }

	
}