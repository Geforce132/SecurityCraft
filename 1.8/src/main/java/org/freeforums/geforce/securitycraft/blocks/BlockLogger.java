package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLogger;

public class BlockLogger extends BlockContainer{

	public BlockLogger(Material par1Material) {
		super(par1Material);
	}
	
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
    	if(par1World.isRemote){
    		return true;
    	}else{
    		par5EntityPlayer.openGui(mod_SecurityCraft.instance, 11, par1World, pos.getX(), pos.getY(), pos.getZ());
    		return true;
    	}
    }
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block p_149695_5_)
    {
    	if (!par1World.isRemote){              	       
        	if(par1World.isBlockPowered(pos))
            {
            	((TileEntityLogger)par1World.getTileEntity(pos)).logPlayers();
            }
        }
    }


	public TileEntity createNewTileEntity(World world, int par1) {
		return new TileEntityLogger();
	}

}
