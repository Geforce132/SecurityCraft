package org.freeforums.geforce.securitycraft.blocks.mines;

import java.util.Random;

import org.freeforums.geforce.securitycraft.api.IOwnable;
import org.freeforums.geforce.securitycraft.blocks.BlockOwnable;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityIMS;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIMS extends BlockOwnable {

	public BlockIMS(Material par1) {
		super(par1);
		this.setBlockBounds(0F, 0F, 0F, 1F, 0.45F, 1F);
	}
	
	public boolean isOpaqueCube(){
        return false;
    }
    
    public boolean isNormalCube(){
        return false;
    } 
    
    public int getRenderType(){
    	return -1;
    }

	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(!par1World.isRemote){
			if(BlockUtils.isOwnerOfBlock(((IOwnable) par1World.getTileEntity(pos)), par5EntityPlayer)){
				par5EntityPlayer.openGui(mod_SecurityCraft.instance, 19, par1World, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		
		return false;
	}
	
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
		if(!par1World.isRemote){
			BlockUtils.destroyBlock(par1World, pos, false);
		}                      
	}
	
	/**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){      
    	if(par1World.getTileEntity(pos) != null && ((TileEntityIMS) par1World.getTileEntity(pos)).getBombsRemaining() == 0){
    		double d0 = (double)((float)pos.getX() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d1 = (double)((float)pos.getY() + 0.4F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d3 = 0.2199999988079071D;
    		double d4 = 0.27000001072883606D;

    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    		
    		par1World.spawnParticle(EnumParticleTypes.FLAME, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    	}
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityIMS();
	}

}
