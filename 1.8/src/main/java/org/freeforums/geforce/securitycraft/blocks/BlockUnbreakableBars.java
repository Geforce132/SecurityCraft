package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockUnbreakableBars extends BlockPane implements ITileEntityProvider, IHelpInfo {
	
	public BlockUnbreakableBars(Material par1Material, boolean par2) {
		super(par1Material, par2);
		
	}
	
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random) {	
    	Utils.setBlock(par1World, pos, Blocks.iron_bars);
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	if(par1World.isRemote){
    		return;
    	}else{
    		if(par5EntityLivingBase instanceof EntityPlayer){
    			((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());
    		}
    	}
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

    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, BlockPos pos)
    {
        return HelpfulMethods.getItemFromBlock(this);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item getItemDropped(IBlockState state, Random par2Random, int par3)
    {
        return HelpfulMethods.getItemFromBlock(this);
    }

	public TileEntity createNewTileEntity(World par1, int par2) {
		return new TileEntityOwnable();
	}

	public String getHelpInfo() {
		return "Reinforced iron bars act the same as vanilla iron bars, except they are unbreakable.";
	}

	public String[] getRecipe() {
		return new String[]{"Reinforced iron bars requires: 4 iron ingots, 1 iron bars", " X ", "XYX", " X ", "X = iron ingot, Y = iron bars"};
	}

}
