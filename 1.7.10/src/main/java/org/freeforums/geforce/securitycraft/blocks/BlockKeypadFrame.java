package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFrame;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockKeypadFrame extends BlockOwnable implements IHelpInfo {

	public BlockKeypadFrame(Material par1) {
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

        ((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

        if(l == 0){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);    
        }

        if(l == 1){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);         
        }

        if(l == 2){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);           
        }

        if(l == 3){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);                   
    	}else{
    		return;
    	}   
        
    }
	
	public void registerBlockIcons(IIconRegister icon){
		this.blockIcon = icon.registerIcon("stone");
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityKeypadFrame();
	}

	public String getHelpInfo() {
		return "The keypad frame is used in the keypad's crafting recipe.";
	}

	public String[] getRecipe() {
		return new String[]{"The keypad frame requires: 8 stone, 1 redstone", "XXX", "XYX", "X X", "X = stone, Y = redstone"};
	}

}
