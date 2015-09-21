package net.breakinbad.securitycraft.blocks;

import java.util.Random;

import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedWoodSlabs extends BlockWoodSlab implements ITileEntityProvider {

	public BlockReinforcedWoodSlabs(){
		if(!this.isDouble()){
			this.useNeighborBrightness = true;
		}
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
	
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
        return Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs);
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos){
        return Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs);
    }

    public boolean isDouble(){
		return false;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

}
