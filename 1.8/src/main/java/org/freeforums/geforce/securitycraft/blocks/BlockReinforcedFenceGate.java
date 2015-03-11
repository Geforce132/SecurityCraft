package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockReinforcedFenceGate extends BlockFenceGate implements ITileEntityProvider, IHelpInfo{
	
	public BlockReinforcedFenceGate(){
		super();
	}
	
	/**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World p_149727_1_, BlockPos pos, IBlockState state, EntityPlayer p_149727_5_, EnumFacing facing, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        return false;
    }
    
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);
    	
    	((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());
    }
    
    public void breakBlock(World par1World, BlockPos pos, IBlockState state)
    {
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }

    public boolean onBlockEventReceived(World par1World, BlockPos pos, IBlockState state, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, pos, state, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(pos);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

	public String getHelpInfo() {
		return "The reinforced iron fence gate acts the same as a vanilla fence gate, except it is unbreakable, and can only be opened with redstone power.";
	}

	public String[] getRecipe() {
		return new String[]{"The reinforced iron fencegate requires: 4 iron ingots, 1 fencegate.", " X ", "XYX", " X ", "X = iron ingot, Y = fencegate"};
	}

}
