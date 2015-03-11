package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;

public class BlockKeypadChest extends BlockChest implements IHelpInfo {

	public BlockKeypadChest(int par1){
		super(par1);	
	}
	
	/**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9)
    {
        if (par1World.isRemote){
            return true;
        }else{
        	ILockableContainer ilockablecontainer = this.getLockableContainer(par1World, pos);

            if (ilockablecontainer != null){ 
            	if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.Codebreaker){
            		par5EntityPlayer.displayGUIChest(ilockablecontainer);
            		return true;
            	}
            	
            	if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityKeypadChest){
            		if(((TileEntityKeypadChest) par1World.getTileEntity(pos)).getKeypadCode() != null && !((TileEntityKeypadChest) par1World.getTileEntity(pos)).getKeypadCode().isEmpty()){
            			par5EntityPlayer.openGui(mod_SecurityCraft.instance, 13, par1World, pos.getX(), pos.getY(), pos.getZ());
            		}else{
            			par5EntityPlayer.openGui(mod_SecurityCraft.instance, 12, par1World, pos.getX(), pos.getY(), pos.getZ());

            		}
            	}
            	
            }

            return true;
        }
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);
        
        ((TileEntityKeypadChest) par1World.getTileEntity(pos)).setOwner(par5EntityLivingBase.getName());
        
        if(par1World.getTileEntity(pos.east()) != null && par1World.getTileEntity(pos.east()) instanceof TileEntityKeypadChest){
        	((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setKeypadCode(((TileEntityKeypadChest) par1World.getTileEntity(pos.east())).getKeypadCode());
		}else if(par1World.getTileEntity(pos.west()) != null && par1World.getTileEntity(pos.west()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setKeypadCode(((TileEntityKeypadChest) par1World.getTileEntity(pos.west())).getKeypadCode());
		}else if(par1World.getTileEntity(pos.south()) != null && par1World.getTileEntity(pos.south()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setKeypadCode(((TileEntityKeypadChest) par1World.getTileEntity(pos.south())).getKeypadCode());
		}else if(par1World.getTileEntity(pos.north()) != null && par1World.getTileEntity(pos.north()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setKeypadCode(((TileEntityKeypadChest) par1World.getTileEntity(pos.north())).getKeypadCode());
		}
    }
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block)
    {
        super.onNeighborBlockChange(par1World, pos, state, par5Block);
        TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)par1World.getTileEntity(pos);

        if (tileentitychest != null)
        {
            tileentitychest.updateContainingBlockInfo();
        }
      
    }
	
	/**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World par1World, int par2)
    {
        return new TileEntityKeypadChest();
    }

	public String getHelpInfo() {
		return "The password-protected chest is equipped with a password locking system. Whenever the password is entered correctly, the chest's inventory will open.";
	}

	public String[] getRecipe() {
		return new String[]{"The password-protected chest requires: 7 iron ingot, 1 keypad, 1 chest", "XYX", "XZX", "XXX", "X = iron ingot, Y = keypad, Z = chest"};
	}

}
