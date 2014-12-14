package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockFullMineBase extends Block{
	
	

	public BlockFullMineBase(Material par2Material) {
		super(par2Material);

	}
	
	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state)
    {
        return null;
    }

    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
   public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity){
    	if(par1World.isRemote){
    		return;
    	}else{
    	
    		if(par5Entity instanceof EntityCreeper || par5Entity instanceof EntityOcelot || par5Entity instanceof EntityEnderman || par5Entity instanceof EntityItem){
    			return;
    		}else{
    			this.explode(par1World, pos);
    		}
    		
    	}
    
    	
    }
   
   /**
    * Called upon the block being destroyed by an explosion
    */
   public void onBlockDestroyedByExplosion(World par1World, BlockPos pos, Explosion par5Explosion)
   {
       if (!par1World.isRemote)
       {
           this.explode(par1World, pos);
       }
   }
   
   public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state){
	   if (!par1World.isRemote)
	   {
		   this.explode(par1World, pos);
	   }
   }	
    
    

	private void explode(World par1World, BlockPos pos) {
		par1World.destroyBlock(pos, false);
		par1World.createExplosion((Entity)null, pos.getX(), (double) pos.getY() + 0.5D, pos.getZ(), 5.0F, true);

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
    
    public Item getItemDropped(IBlockState state, Random par2Random, int par3){
    	return null;
    }

	
}