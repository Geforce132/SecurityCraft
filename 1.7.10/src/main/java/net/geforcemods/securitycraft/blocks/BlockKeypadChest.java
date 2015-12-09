package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockKeypadChest extends BlockChest {

	public BlockKeypadChest(int par1){
		super(par1);
	}
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote) {
            if(par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityKeypadChest) {
            	((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).openPasswordGUI(par5EntityPlayer);
            }  

            return true;
        }
        
        return true;
	}

    public static void activate(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
    	IInventory iinventory = ((BlockKeypadChest) par1World.getBlock(par2, par3, par4)).func_149951_m(par1World, par2, par3, par4);

    	if(iinventory != null){ 
    		par5EntityPlayer.displayGUIChest(iinventory);
    	}
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
        
        ((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
        
        if(par1World.getTileEntity(par2 + 1, par3, par4) != null && par1World.getTileEntity(par2 + 1, par3, par4) instanceof TileEntityKeypadChest){
        	((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2 + 1, par3, par4)).getPassword());
		}else if(par1World.getTileEntity(par2 - 1, par3, par4) != null && par1World.getTileEntity(par2 - 1, par3, par4) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2 - 1, par3, par4)).getPassword());
		}else if(par1World.getTileEntity(par2, par3, par4 + 1) != null && par1World.getTileEntity(par2, par3, par4 + 1) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4 + 1)).getPassword());
		}else if(par1World.getTileEntity(par2, par3, par4 - 1) != null && par1World.getTileEntity(par2, par3, par4 - 1) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4 - 1)).getPassword());
		}
    }
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block){
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5Block);
        TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)par1World.getTileEntity(par2, par3, par4);

        if (tileentitychest != null){
            tileentitychest.updateContainingBlockInfo();
        }
      
    }
	
    public TileEntity createNewTileEntity(World par1World, int par2){
        return new TileEntityKeypadChest();
    }

}
