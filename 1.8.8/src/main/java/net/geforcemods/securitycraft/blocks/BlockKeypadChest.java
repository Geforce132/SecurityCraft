package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockKeypadChest extends BlockChest {

	public BlockKeypadChest(int par1){
		super(par1);	
	}
	
	/**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
        if(par1World.isRemote){
            return true;
        }else{
        	ILockableContainer ilockablecontainer = this.getLockableContainer(par1World, pos);

            if (ilockablecontainer != null){ 
            	if(par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.Codebreaker){
                    if(mod_SecurityCraft.configHandler.allowCodebreakerItem){
                        par5EntityPlayer.displayGUIChest(ilockablecontainer);
                        return true;
                    }else{	
                        PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypadChest.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);  	
                        return true;
                    }                    
				}
            	
            	if(par1World.getTileEntity(pos) != null && par1World.getTileEntity(pos) instanceof TileEntityKeypadChest){
            		if(((TileEntityKeypadChest) par1World.getTileEntity(pos)).getPassword() != null && !((TileEntityKeypadChest) par1World.getTileEntity(pos)).getPassword().isEmpty()){
            			par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, par1World, pos.getX(), pos.getY(), pos.getZ());
            		}else{
            			par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, par1World, pos.getX(), pos.getY(), pos.getZ());

            		}
            	}
            	
            }

            return true;
        }
    }
    
    public static void activate(World par1World, BlockPos pos, EntityPlayer player){
    	player.displayGUIChest(((BlockChest) BlockUtils.getBlock(par1World, pos)).getLockableContainer(par1World, pos));
	}
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);
        
        ((TileEntityKeypadChest) par1World.getTileEntity(pos)).getOwner().set(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
        
        if(par1World.getTileEntity(pos.east()) != null && par1World.getTileEntity(pos.east()) instanceof TileEntityKeypadChest){
        	((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.east())).getPassword());
		}else if(par1World.getTileEntity(pos.west()) != null && par1World.getTileEntity(pos.west()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.west())).getPassword());
		}else if(par1World.getTileEntity(pos.south()) != null && par1World.getTileEntity(pos.south()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.south())).getPassword());
		}else if(par1World.getTileEntity(pos.north()) != null && par1World.getTileEntity(pos.north()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)(par1World.getTileEntity(pos))).setPassword(((TileEntityKeypadChest) par1World.getTileEntity(pos.north())).getPassword());
		}
    }
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block){
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

}
