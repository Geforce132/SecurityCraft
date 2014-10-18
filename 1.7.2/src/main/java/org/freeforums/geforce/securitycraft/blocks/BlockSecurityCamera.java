package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSecurityCamera extends BlockContainer{
	
	@SideOnly(Side.CLIENT)
    private IIcon keypadIconTop;
    @SideOnly(Side.CLIENT)
    private IIcon keypadIconFront;

	public BlockSecurityCamera(Material par2Material) {
		super(par2Material);
	}
	
	
	

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2)
    {
    	return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != par2 ? this.blockIcon : this.keypadIconFront)); 	
    }


    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("cobblestone");
        this.keypadIconFront = par1IconRegister.registerIcon("stone");
        this.keypadIconTop = par1IconRegister.registerIcon("cobblestone");
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
	        HelpfulMethods.checkBlocksMetadata(par1World, par2, par3, par4);
        }

        if (l == 1){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
	        HelpfulMethods.checkBlocksMetadata(par1World, par2, par3, par4);
        }

        if (l == 2){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
	        HelpfulMethods.checkBlocksMetadata(par1World, par2, par3, par4);
        }
        
        if (l == 3){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
	        HelpfulMethods.checkBlocksMetadata(par1World, par2, par3, par4);
    	}else{
    		return;
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
        return ((TileEntitySecurityCamera) par1IBlockAccess.getTileEntity(par2, par3, par4)).isDetectingPlayer() ? 15 : 0;
    	//return 15;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return ((TileEntitySecurityCamera) par1IBlockAccess.getTileEntity(par2, par3, par4)).isDetectingPlayer() ? 15 : 0;
    	//return 15;
    }

	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntitySecurityCamera();
	}

}
