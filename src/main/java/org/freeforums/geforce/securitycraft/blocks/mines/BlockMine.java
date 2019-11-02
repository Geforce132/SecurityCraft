package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.items.ItemRemoteAccess;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
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
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    

    
    /**
    * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
    * their own) Args: x, y, z, neighbor blockID
    */
   public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
   {
       if (par1World.getBlock(par2, par3 - 1, par4).getMaterial() != Material.air){
    	   return;
    	   
       }else{
    	   
    	   HelpfulMethods.destroyBlock(par1World, par2, par3, par4, true);
       
       }
   }
   
   /**
    * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
    */
   public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
   {
	   if(par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.glass || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.cactus || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.air || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.cake || par1World.getBlock(par2, par3 - 1, par4).getMaterial() == Material.plants){
		   return false;
	   }else{
		   return true;
	   }
   }
   
   public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5){
	   this.explode(par1World, par2, par3, par4);
   }
   

   
   public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
	   if(par1World.isRemote){
		   return true;
	   }else{
		   if(par5EntityPlayer.getCurrentEquippedItem() == null || !isInteractibleItem(par5EntityPlayer.getCurrentEquippedItem().getItem())){
			   this.explode(par1World, par2, par3, par4);
			   return false;
		   }else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.wireCutters){
			   par1World.setBlock(par2, par3, par4, mod_SecurityCraft.MineCut);
			   return true;
		   }else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
			   par1World.setBlock(par2, par3, par4, mod_SecurityCraft.Mine);
			   return true;
		   }else{
			   return false;	   		
		   }
	   }
   }
   
   private boolean isInteractibleItem(Item item){
	   if(item == mod_SecurityCraft.wireCutters || item == mod_SecurityCraft.remoteAccessMine || item == Items.flint_and_steel){
		   return true;
	   }else{
		   return false;
	   }
	   
   }

   

   private void setPosition(ItemRemoteAccess par1Item, int par2, int par3, int par4, TileEntityMineLoc TEML, EntityPlayer par6EntityPlayer) {
	   
	   if(this.isFullArray(par1Item)){
		   HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, "No avaliable slots to bind the mine to!", EnumChatFormatting.RED);
		   return;
	   }
	   
	   for(int x = 1; x <= par1Item.tEList.length; x++){
		   if(par1Item.tEList[x - 1] != null && par1Item.tEList[x - 1].xCoord == par2 && par1Item.tEList[x - 1].yCoord == par3 && par1Item.tEList[x - 1].zCoord == par4){
			   HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, par6EntityPlayer.getCommandSenderName() + " unbound a mine at X:" + par2 + " Y:" + par3 + " Z:" + par4 + ".", null);
			   par1Item.tEList[x - 1] = null;
			   break;
		   }else if(par1Item.tEList[x - 1] == null){
			   par1Item.tEList[x - 1] = TEML;
			   HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, par6EntityPlayer.getCommandSenderName() + " bound a mine at X:" + par2 + " Y:" + par3 + " Z:" + par4 + " to a remote access tool.", null);
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
   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
   {
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
    
    

	private void explode(World par1World, int par2, int par3, int par4) {
		if(!cut){
			par1World.func_147480_a(par2, par3, par4, false);
			if(mod_SecurityCraft.configHandler.smallerMineExplosion){
				this.newExplosion((Entity)null, (double) par2, (double) par3, (double) par4,  1.0F, true, true, par1World);
			}else{
				this.newExplosion((Entity)null, (double) par2, (double) par3, (double) par4,  3.0F, true, true, par1World);
			}
		}
	}
	
	/**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10, World par11World)
    {
        Explosion explosion = new Explosion(par11World, par1Entity, par2, par4, par6, par8);
        if(mod_SecurityCraft.configHandler.shouldSpawnFire){
            explosion.isFlaming = true;
        }else{
            explosion.isFlaming = false;
        }
        explosion.isSmoking = par10;
        explosion.doExplosionA();
        
        
        explosion.doExplosionB(true);
        return explosion;
    }
	
	 public void registerBlockIcons(IIconRegister par1IconRegister){
		 if(cut){
		    this.blockIcon = par1IconRegister.registerIcon("securitycraft:mineCut");
		 }else{
			this.blockIcon = par1IconRegister.registerIcon("securitycraft:mine");
		 }
		 
	 }
	 
	 /**
	   * Returns the ID of the items to drop on destruction.
	   */
	 public Item getItemDropped(int par1, Random par2Random, int par3){
		 return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	 }
	 
	 /**
	   * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	   */
	 public Item getItem(World par1World, int par2, int par3, int par4){
		 return HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine);
	 }

	public TileEntity createNewTileEntity(World var1, int var2) {
		TileEntityMineLoc TEML = new TileEntityMineLoc();
		return TEML;
	}
	 
	 
	 

	
	      

}
