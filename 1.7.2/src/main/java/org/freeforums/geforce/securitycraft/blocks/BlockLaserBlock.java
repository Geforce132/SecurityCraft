package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLaserBlock extends BlockContainer{


	    
	public BlockLaserBlock(Material par2Material) {
		super(par2Material);
	}
	
	/**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
    	((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(par5EntityLivingBase.getCommandSenderName());

    	if(!par1World.isRemote){
        	this.setLaser(par1World, par2, par3, par4);
        }

    }

    private void setLaser(World par1World, int par2, int par3, int par4) {
		for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2 + i, par3, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2 + j, par3, par4) == Blocks.air){
						par1World.setBlock(par2 + j, par3, par4, mod_SecurityCraft.Laser, 3, 3);
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2 - i, par3, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2 - j, par3, par4) == Blocks.air){
						par1World.setBlock(par2 - j, par3, par4, mod_SecurityCraft.Laser, 3, 3);
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2, par3, par4 + i);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3, par4 + j) == Blocks.air){
						par1World.setBlock(par2, par3, par4 + j, mod_SecurityCraft.Laser, 2, 3);
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2 , par3, par4 - i);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3, par4 - j) == Blocks.air){
						par1World.setBlock(par2, par3, par4 - j, mod_SecurityCraft.Laser, 2, 3);
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2, par3 + i, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3 + j, par4) == Blocks.air){
						par1World.setBlock(par2, par3 + j, par4, mod_SecurityCraft.Laser, 1, 3);
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2, par3 - i, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3 - j, par4) == Blocks.air){
						par1World.setBlock(par2, par3 - j, par4, mod_SecurityCraft.Laser, 1, 3);
					}
				}
			}else{
				continue;
			}
		}
	}
    
    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {
    	if(!par1World.isRemote){
    		destroyAdjecentLasers(par1World, par2, par3, par4);
    	}
    }
    
    public static void destroyAdjecentLasers(World par1World, int par2, int par3, int par4){
    	for(int i = 1; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2 + i, par3, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2 + j, par3, par4) == mod_SecurityCraft.Laser){
						par1World.func_147480_a(par2 + j, par3, par4, false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2 - i, par3, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2 - j, par3, par4) == mod_SecurityCraft.Laser){
						par1World.func_147480_a(par2 - j, par3, par4, false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2, par3, par4 + i);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3, par4 + j) == mod_SecurityCraft.Laser){
						par1World.func_147480_a(par2, par3, par4 + j, false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2 , par3, par4 - i);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3, par4 - j) == mod_SecurityCraft.Laser){
						par1World.func_147480_a(par2, par3, par4 - j, false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2, par3 + i, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3 + j, par4) == mod_SecurityCraft.Laser){
						par1World.func_147480_a(par2, par3 + j, par4, false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
		
		for(int i = 0; i <= mod_SecurityCraft.configHandler.laserBlockRange; i++){
			Block id = par1World.getBlock(par2, par3 - i, par4);
			if(id == mod_SecurityCraft.LaserBlock){
				for(int j = 1; j < i; j++){
					if(par1World.getBlock(par2, par3 - j, par4) == mod_SecurityCraft.Laser){
						par1World.func_147480_a(par2, par3 - j, par4, false);
					}else{
						return;
					}
				}
			}else{
				continue;
			}
		}
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
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 2){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 2){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) == 2){
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 3);
        }
        
            
        
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        
            if(par1World.getBlockMetadata(par2, par3, par4) == 2){
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
        
    }

	public TileEntity createNewTileEntity(World par1World, int par2) {
		return new TileEntityOwnable();
	}

 
//    @SideOnly(Side.CLIENT)
//
//    /**
//     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
//     * is the only chance you get to register icons.
//     */
//    public void registerBlockIcons(IIconRegister par1IconRegister)
//    {
//        this.blockIcon = par1IconRegister.registerIcon("dispenser_front_vertical");
//      
//    }
    
    

}
