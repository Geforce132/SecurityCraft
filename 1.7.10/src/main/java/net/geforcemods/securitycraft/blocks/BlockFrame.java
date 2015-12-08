package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockFrame extends BlockOwnable {

	public BlockFrame(Material par1) {
		super(par1);
	}

	public boolean renderAsNormalBlock(){
		return false;
	}

	public boolean isNormalCube(){
		return false;
	}

	public boolean isOpaqueCube(){
		return false;
	}

	public int getRenderType(){
		return -1;
	}
	
	/**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if(l == 0){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);    
        }

        if(l == 1){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);         
        }

        if(l == 2){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);           
        }

        if(l == 3){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);                   
    	}         
    }
    
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.isRemote){
			if(mod_SecurityCraft.configHandler.fiveMinAutoShutoff && ((TileEntityFrame) par1World.getTileEntity(par2, par3, par4)).hasCameraLocation()){
				((TileEntityFrame) par1World.getTileEntity(par2, par3, par4)).enableView();
				return true;
			}
		}else{
			if(!((TileEntityFrame) par1World.getTileEntity(par2, par3, par4)).hasCameraLocation() && (par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.cameraMonitor)){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, StatCollector.translateToLocal("tile.keypadFrame.name"), StatCollector.translateToLocal("messages.frame.rightclick"), EnumChatFormatting.RED);
				return false;
			}
			
			if(PlayerUtils.isHoldingItem(par5EntityPlayer, mod_SecurityCraft.keyPanel))
				return false;
		}
		
		return false;
	}
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityFrame();
	}

}
