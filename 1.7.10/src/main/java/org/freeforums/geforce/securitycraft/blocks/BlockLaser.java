package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLaser extends Block{

	public BlockLaser() {
		super(Material.circuits);
		this.setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);

	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }
	
	public boolean isOpaqueCube(){
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
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        if(!par1World.isRemote && par5Entity instanceof EntityLivingBase && !HelpfulMethods.doesMobHavePotionEffect((EntityLivingBase) par5Entity, Potion.invisibility)){	
			for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = par1World.getBlock(par2 + i, par3, par4);
				if(id == mod_SecurityCraft.LaserBlock){
					par1World.setBlock(par2 + i, par3, par4, mod_SecurityCraft.LaserBlock, 2, 3);
					par1World.notifyBlocksOfNeighborChange(par2 + i, par3, par4, mod_SecurityCraft.LaserBlock);
					par1World.scheduleBlockUpdate(par2 + i, par3, par4, mod_SecurityCraft.LaserBlock, 50);
					par1World.notifyBlocksOfNeighborChange(par2 + i, par3, par4, mod_SecurityCraft.LaserBlock);

				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = par1World.getBlock(par2 - i, par3, par4);
				if(id == mod_SecurityCraft.LaserBlock){
					par1World.setBlock(par2 - i, par3, par4, mod_SecurityCraft.LaserBlock, 2, 3);
					par1World.notifyBlocksOfNeighborChange(par2 - i, par3, par4, mod_SecurityCraft.LaserBlock);
					par1World.scheduleBlockUpdate(par2 - i, par3, par4, mod_SecurityCraft.LaserBlock, 50);
					par1World.notifyBlocksOfNeighborChange(par2 - i, par3, par4, mod_SecurityCraft.LaserBlock);

				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = par1World.getBlock(par2, par3, par4 + i);
				if(id == mod_SecurityCraft.LaserBlock){
					par1World.setBlock(par2, par3, par4 + i, mod_SecurityCraft.LaserBlock, 2, 3);
					par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + i, mod_SecurityCraft.LaserBlock);
					par1World.scheduleBlockUpdate(par2, par3, par4 + i, mod_SecurityCraft.LaserBlock, 50);
					par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + i, mod_SecurityCraft.LaserBlock);

				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = par1World.getBlock(par2, par3, par4 - i);
				if(id == mod_SecurityCraft.LaserBlock){
					par1World.setBlock(par2, par3, par4 - i, mod_SecurityCraft.LaserBlock, 2, 3);
					par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - i, mod_SecurityCraft.LaserBlock);
					par1World.scheduleBlockUpdate(par2, par3, par4 - i, mod_SecurityCraft.LaserBlock, 50);
					par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - i, mod_SecurityCraft.LaserBlock);

				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = par1World.getBlock(par2, par3 + i, par4);
				if(id == mod_SecurityCraft.LaserBlock){
					par1World.setBlock(par2, par3 + i, par4, mod_SecurityCraft.LaserBlock, 2, 3);
					par1World.notifyBlocksOfNeighborChange(par2, par3 + i, par4, mod_SecurityCraft.LaserBlock);
					par1World.scheduleBlockUpdate(par2, par3 + i, par4, mod_SecurityCraft.LaserBlock, 50);
					par1World.notifyBlocksOfNeighborChange(par2, par3 + i, par4, mod_SecurityCraft.LaserBlock);

				}else{
					continue;
				}
			}
			
			for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
				Block id = par1World.getBlock(par2, par3 - i, par4);
				if(id == mod_SecurityCraft.LaserBlock){
					par1World.setBlock(par2, par3 - i, par4, mod_SecurityCraft.LaserBlock, 2, 3);
					par1World.notifyBlocksOfNeighborChange(par2, par3 - i, par4, mod_SecurityCraft.LaserBlock);
					par1World.scheduleBlockUpdate(par2, par3 - i, par4, mod_SecurityCraft.LaserBlock, 50);
					par1World.notifyBlocksOfNeighborChange(par2, par3 - i, par4, mod_SecurityCraft.LaserBlock);


				}else{
					continue;
				}
			}
        }
    }
    
    
    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5)
    {
    	if(!par1World.isRemote){
    		for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2 + i, par3, par4);
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2 + j, par3, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2 - i, par3, par4);
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2 - j, par3, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2, par3, par4 + i);
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3, par4 + j, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2 , par3, par4 - i);
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3, par4 - j, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2, par3 + i, par4);
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3 + j, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2, par3 - i, par4);
    			if(id == mod_SecurityCraft.LaserBlock || id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3 - j, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    	}
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
            
        }

        if (l == 1)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
           

        }

        if (l == 2)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
           


        }

        if (l == 3)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
           
            

        }
    	
       
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1)
        {
    		this.setBlockBounds(0.250F, 0.000F, 0.300F, 0.750F, 1.000F, 0.700F);
        }
        else if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 2)
        {

    		this.setBlockBounds(0.250F, 0.300F, 0.000F, 0.750F, 0.700F, 1.000F);
        }
        else if (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 3)
        {

    		this.setBlockBounds(0.000F, 0.300F, 0.300F, 1.000F, 0.700F, 0.700F);
        }
        else
        {
    		this.setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);
        }
    }


    
 
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("securitycraft:aniLaser");
     
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item getItem(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

}
