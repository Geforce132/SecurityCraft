package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.timers.ReverseLaserBlock;

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
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return 15;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
       return 15;
    }
    
    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state)
    {
    	if(!par1World.isRemote){
    		for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlockState(pos.east(i)).getBlock();
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.east(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlockState(pos.west(i)).getBlock();
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.west(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlockState(pos.south(i)).getBlock();
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.south(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlockState(pos.north(i)).getBlock();
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.north(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlockState(pos.up(i)).getBlock();
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.up(j), false);
    				}
    			}else{
    				continue;
    			}
    		}
    		
    		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
    			Block id = par1World.getBlockState(pos.down(i)).getBlock();
    			if(id == mod_SecurityCraft.LaserActive){
    				for(int j = 1; j < i; j++){
    					par1World.destroyBlock(pos.down(j), false);
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
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
    {
    	super.onBlockAdded(par1World, pos, state);
        par1World.notifyNeighborsOfStateChange(pos, state.getBlock());
        
        new ReverseLaserBlock(3, par1World, pos);
    }
    
   
    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random)
    {        
    	double d0 = (double)((float)pos.getX() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    	double d1 = (double)((float)pos.getY() + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    	double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    	double d3 = 0.2199999988079071D;
    	double d4 = 0.27000001072883606D;


    	par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    	par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    	par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
    	par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
    	par1World.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);


    }
	 
}
