package net.breakinbad.securitycraft.blocks;

import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedStairs extends BlockStairs implements ITileEntityProvider {

	public BlockReinforcedStairs(Block baseBlock, int meta) {
		super(meta != 0 ? baseBlock.getStateFromMeta(meta) : baseBlock.getDefaultState());
		this.useNeighborBrightness = true;
	}
	
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	if(!par1World.isRemote){
    		if(par5EntityLivingBase instanceof EntityPlayer){
    			((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());
    		}
    	}
    }
	
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

}
