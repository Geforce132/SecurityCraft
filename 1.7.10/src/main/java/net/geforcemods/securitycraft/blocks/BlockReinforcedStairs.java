package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockReinforcedStairs extends BlockStairs implements ITileEntityProvider {
    
	public BlockReinforcedStairs(Block par1, int par2) {
		super(par1, par2);
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);

		((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        
        if(par1World.getTileEntity(par2, par3, par4) != null){
        	par1World.removeTileEntity(par2, par3, par4);
        }
    }
	
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityOwnable();
	}

}
