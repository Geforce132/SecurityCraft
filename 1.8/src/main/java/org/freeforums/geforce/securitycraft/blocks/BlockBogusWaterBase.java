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

public class BlockBogusWaterBase extends BlockStaticLiquid{
	
	public BlockBogusWaterBase(Material par2Material)
    {
        super(par2Material);    
    } 
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
   {
    	if(!par1World.isRemote){
    		
    		if(par5Entity instanceof EntityPlayer && !((EntityPlayer) par5Entity).capabilities.isCreativeMode){
    			float f = ((EntityPlayer) par5Entity).getHealth();
    			((EntityPlayer) par5Entity).setHealth(f - 0.5F);
    			par1World.playSoundAtEntity(par5Entity, "random.fizz", 1.0F, 1.0F);
    		}
    	}
   }

   /**
    * Gets an item for the block being called on. Args: world, x, y, z
    */
   @SideOnly(Side.CLIENT)
   public Item getItem(World p_149694_1_, BlockPos pos)
   {
	   return null;
   }
}
