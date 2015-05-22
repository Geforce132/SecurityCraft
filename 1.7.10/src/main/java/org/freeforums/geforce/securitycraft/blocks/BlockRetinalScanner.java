package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRetinalScanner;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRetinalScanner extends BlockContainer implements IHelpInfo {
	
	@SideOnly(Side.CLIENT)
	private IIcon rtIconTop;
	
	@SideOnly(Side.CLIENT)
	private IIcon rtIconFront;
	
	@SideOnly(Side.CLIENT)
	private IIcon rtIconFrontActive;

	public BlockRetinalScanner(Material par1) {
		super(par1);
	}

	/**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

        if (l == 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);          
        }

        if (l == 1)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);            
        }

        if (l == 2)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);    
        }

        if (l == 3)
        {
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);   
    	}else{
    		return;
    	}
      
    }
    
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public IIcon getIcon(int par1, int par2)
    {
        if(par1 == 3 && par2 == 0){
    		return this.rtIconFront;
    	}
        
    	if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10){
    		return par1 == 1 ? this.rtIconTop : (par1 == 0 ? this.rtIconTop : (par1 != (par2 - 5) ? this.blockIcon : this.rtIconFrontActive));
    	}else{
    		return par1 == 1 ? this.rtIconTop : (par1 == 0 ? this.rtIconTop : (par1 != par2 ? this.blockIcon : this.rtIconFront));
    	}
    }
    
    
    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) >= 7 && par1World.getBlockMetadata(par2, par3, par4) <= 10){
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) - 5, 3);
        }                       
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
    	this.blockIcon = par1IconRegister.registerIcon("furnace_side");
        this.rtIconTop = par1IconRegister.registerIcon("furnace_top");
        this.rtIconFront = par1IconRegister.registerIcon("securitycraft:retinalScannerFront");
        this.rtIconFrontActive = par1IconRegister.registerIcon("securitycraft:retinalScannerFront");
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityRetinalScanner();
	}

	public String[] getRecipe() {
		return new String[]{"The retinal scanner requires: 8 stone, 1 eye of ender", "XXX", "XYX", "XXX", "X = stone, Y = eye of ender"};
	}

}
