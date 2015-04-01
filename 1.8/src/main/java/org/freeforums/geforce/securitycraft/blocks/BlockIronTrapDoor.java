package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
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

public class BlockIronTrapDoor extends BlockTrapDoor implements ITileEntityProvider, IHelpInfo {

	public BlockIronTrapDoor(Material materialIn) {
		super(materialIn);
	}
	
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
    	((TileEntityOwnable) worldIn.getTileEntity(pos)).setOwner(((EntityPlayer) placer).getGameProfile().getId().toString(), placer.getName());
    }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ){
    	return false;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
    
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

	public String getHelpInfo() {
		return "The reinforced iron trapdoor is the same as a vanilla trapdoor, except it can only be opened using a redstone signal.";
	}

	public String[] getRecipe() {
		return new String[]{"The reinforced iron trapdoor requires: 8 iron ingots, 1 trapdoor", "XXX", "XYX", "XXX", "X = iron ingot, Y = trapdoor"};
	}
    
}