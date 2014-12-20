package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.timers.ReverseLaserBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockActiveLaser extends Block{

	public BlockActiveLaser(Material par1Material) {
		super(par1Material);
	}
	

	/**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }
    

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return 15;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
       return 15;
    }
    
    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5)
    {
    	if(!par1World.isRemote){
    		for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2 + i, par3, par4);
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2 + j, par3, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2 - i, par3, par4);
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2 - j, par3, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2, par3, par4 + i);
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3, par4 + j, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2 , par3, par4 - i);
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3, par4 - j, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2, par3 + i, par4);
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.func_147480_a(par2, par3 + j, par4, false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlock(par2, par3 - i, par4);
    			if(id == mod_SecurityCraft.LaserActive){
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
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
    	super.onBlockAdded(par1World, par2, par3, par4);
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this);
        
        new ReverseLaserBlock(3, par1World, par2, par3, par4);
    }
    
   
    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {        
            double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)par3 + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;

            
                par1World.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
                par1World.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
            
        
    }
	 
    
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("dispenser_front_vertical");
     
    }

}
