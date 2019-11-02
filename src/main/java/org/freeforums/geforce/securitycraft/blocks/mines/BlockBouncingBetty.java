package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.items.ItemRemoteAccess;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;

public class BlockBouncingBetty extends BlockMine{

	public BlockBouncingBetty(Material par2Material) {
		super(par2Material, false);
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
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
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
		this.setBlockBounds(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F);
    }
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
  public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
   {
	   if(par5Entity instanceof EntityLivingBase){
		   par1World.setBlockToAir(par2, par3, par4);
		   EntityTnTCompact entitytntprimed = new EntityTnTCompact(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (EntityLivingBase) par5Entity);
		   entitytntprimed.fuse = 15;
	   	   entitytntprimed.motionY = 0.50D;
	   	   par1World.spawnEntityInWorld(entitytntprimed);
	   	   par1World.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
	   }else{
		   return;
	   }
    	
   }
   
   /**
    * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
    */
   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
   {
	   if(par5EntityPlayer instanceof EntityLivingBase){
		   par1World.setBlockToAir(par2, par3, par4);
		   EntityTnTCompact entitytntprimed = new EntityTnTCompact(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (EntityLivingBase) par5EntityPlayer);
		   entitytntprimed.fuse = 15;
	   	   entitytntprimed.motionY = 0.50D;
	   	   par1World.spawnEntityInWorld(entitytntprimed);
	   	   par1World.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
	   }else{
		   return;
	   }
   }
   
   public void explode(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
	   par1World.setBlockToAir(par2, par3, par4);
	   EntityTnTCompact entitytntprimed = new EntityTnTCompact(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), (EntityLivingBase) par5EntityPlayer);
	   entitytntprimed.fuse = 15;
   	   entitytntprimed.motionY = 0.50D;
   	   par1World.spawnEntityInWorld(entitytntprimed);
   	   par1World.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
   }
   
   public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
	   if(par1World.isRemote){
		   if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.remoteAccessMine){
			   //this.setPosition((ItemRemoteAccess) par5EntityPlayer.getCurrentEquippedItem().getItem(), par2, par3, par4, (TileEntityMineLoc) par1World.getBlockTileEntity(par2, par3, par4));
		   }
		   
		   return true;
	   }else{
	   
		   if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.remoteAccessMine){
			   this.explode(par1World, par2, par3, par4, par5EntityPlayer);
			   return false;
		   }else{
			   this.setPosition((ItemRemoteAccess) par5EntityPlayer.getCurrentEquippedItem().getItem(), par2, par3, par4, (TileEntityMineLoc) par1World.getTileEntity(par2, par3, par4));
			   return false;
		   }
	   }
   }
   
   /**
    * Returns the ID of the items to drop on destruction.
    */
   public Item getItemDropped(int par1, Random par2Random, int par3)
   {
       return HelpfulMethods.getItemFromBlock(this);
   }
   
   
  /**
   * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
   */
   public Item getItem(World par1World, int par2, int par3, int par4){
	   return HelpfulMethods.getItemFromBlock(this);
   }
   
   private void setPosition(ItemRemoteAccess par1Item, int par2, int par3, int par4, TileEntityMineLoc TEML) {
	   
	   for(int x = 1; x <= par1Item.tEList.length; x++){
		   if(par1Item.tEList[x - 1] != null && par1Item.tEList[x - 1].xCoord == par2 && par1Item.tEList[x - 1].yCoord == par3 && par1Item.tEList[x - 1].zCoord == par4){
			   break;
		   }else if(par1Item.tEList[x - 1] == null){
			   par1Item.tEList[x - 1] = TEML;
			   break;
		   }
	   }

   }
   
   public void registerBlockIcons(IIconRegister par1IconRegister){
	   this.blockIcon = par1IconRegister.registerIcon("securitycraft:bouncingBetty");
   }

   public TileEntity createNewTileEntity(World world) {
		TileEntityMineLoc TEML = new TileEntityMineLoc();
		return TEML;
	}
		 

}
