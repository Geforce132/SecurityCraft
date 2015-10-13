package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockOwnable extends BlockContainer {

	public BlockOwnable(Material par1) {
		super(par1);
	}
	
	public int getRenderType()
    {
        return 3;
    }
	
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	if(par5EntityLivingBase instanceof EntityPlayer){
    		((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), ((EntityPlayer) par5EntityLivingBase).getName());
    	}
    }

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
