package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.items.ItemRemoteAccess;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;

public class BlockMine extends BlockContainer{
	
	public boolean cut;
	
	public BlockMine(Material par1Material, boolean cut) {
		super(par1Material);
		 float f = 0.2F;
		 float g = 0.1F;
		 this.cut = cut;
		 this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (g * 2.0F) / 2 + 0.1F, 0.5F + f);
	}
	
	 /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    } 
 
    /**
    * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
    * their own) Args: x, y, z, neighbor blockID
    */
   public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5)
   {
       if (par1World.getBlockState(pos.down()).getBlock().getMaterial() != Material.air){
    	   return;  	   
       }else{    	   
    	   par1World.destroyBlock(pos, true);      
       }
   }
   
   /**
    * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
    */
   public boolean canPlaceBlockAt(World par1World, BlockPos pos)
   {
	   if(Utils.getBlockMaterial(par1World, pos.down()) == Material.glass || Utils.getBlockMaterial(par1World, pos.down()) == Material.cactus || Utils.getBlockMaterial(par1World, pos.down()) == Material.air || Utils.getBlockMaterial(par1World, pos.down()) == Material.cake || Utils.getBlockMaterial(par1World, pos.down()) == Material.plants){
		   return false;
	   }else{
		   return true;
	   }
   }
   
   public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state){
	   this.explode(par1World, pos);
   }
   
   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
	   if(par1World.isRemote){
		   return true;
	   }else{
		   if(par5EntityPlayer.getCurrentEquippedItem() == null || !isInteractibleItem(par5EntityPlayer.getCurrentEquippedItem().getItem())){
			   this.explode(par1World, pos);
			   return false;
		   }else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.shears){
			   Utils.setBlock(par1World, pos, mod_SecurityCraft.MineCut);
			   return true;
		   }else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
			   Utils.setBlock(par1World, pos, mod_SecurityCraft.Mine);
			   return true;
		   }else{
			   return false;	   		
		   }
	   }
   }
   
   private boolean isInteractibleItem(Item item){
	   if(item == Items.shears || item == mod_SecurityCraft.remoteAccessMine || item == Items.flint_and_steel){
		   return true;
	   }else{
		   return false;
	   }
	   
   }  

   private void setPosition(ItemRemoteAccess par1Item, BlockPos pos, TileEntityMineLoc TEML, EntityPlayer par6EntityPlayer) {	   
	   if(this.isFullArray(par1Item)){
		   HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, "No avaliable slots to bind the mine to!", EnumChatFormatting.RED);
		   return;
	   }
	   
	   for(int x = 1; x <= par1Item.tEList.length; x++){
		   if(par1Item.tEList[x - 1] != null && par1Item.tEList[x - 1].getPos() == pos){
			   HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, par6EntityPlayer.getName() + " unbound a mine at " + Utils.getFormattedCoordinates(pos) + ".", null);
			   par1Item.tEList[x - 1] = null;
			   break;
		   }else if(par1Item.tEList[x - 1] == null){
			   par1Item.tEList[x - 1] = TEML;
			   HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, par6EntityPlayer.getName() + " bound a mine at " + Utils.getFormattedCoordinates(pos) + ".", null);
			   break;
		   }
	   }

   }

  private boolean isFullArray(ItemRemoteAccess par1Item) {
	  
	   for(int x = 1; x <= par1Item.tEList.length; x++){
		   if(par1Item.tEList[x - 1] == null){
			   return false;
		   }else{
			   continue;
		   }
	   }
	   
	   return true;
		   

	  
  }
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
   public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity)
   {
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
    
	private void explode(World par1World, BlockPos pos) {
		if(!cut){
			par1World.destroyBlock(pos, false);
			if(mod_SecurityCraft.configHandler.smallerMineExplosion){
				par1World.createExplosion((Entity) null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), 1.0F, true);
				//this.newExplosion((Entity)null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(),  1.0F, true, true, par1World);
			}else{
				//this.newExplosion((Entity)null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(),  3.0F, true, true, par1World);
				par1World.createExplosion((Entity) null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), 3.0F, true);
			}
		}
	}
	
	///**
	// * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
	// */
	//public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10, World par11World){
		//Explosion explosion = new Explosion(par11World, par1Entity, par2, par4, par6, par8);
		//if(mod_SecurityCraft.configHandler.shouldSpawnFire){
		//	explosion.isFlaming = true;
		//}else{
		//	explosion.isFlaming = false;
		//}
		//explosion.isSmoking = par10;
		//explosion.doExplosionA();


		//explosion.doExplosionB(true);
		//return explosion;
	//}
	
	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(IBlockState state, Random par2Random, int par3){
		return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	}
	 
	/**
	  * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	  */
	public Item getItem(World par1World, BlockPos pos){
		return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityMineLoc();
	}      

}
