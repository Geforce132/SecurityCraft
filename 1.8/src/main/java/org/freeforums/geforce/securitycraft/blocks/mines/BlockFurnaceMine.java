package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockFurnaceMine extends BlockFurnace{

	public BlockFurnaceMine() {
		super(false);
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
   
   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
   	  if(par1World.isRemote){
   		  return true;
   	  }else{
   		  this.explode(par1World, pos);
   		  return true;
   	  }
   }	  

	private void explode(World par1World, BlockPos pos) {
		par1World.destroyBlock(pos, false);
		
		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			par1World.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, true);
		}else{
			par1World.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, true);
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
        return 1;
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityOwnable();
    }
    
}
