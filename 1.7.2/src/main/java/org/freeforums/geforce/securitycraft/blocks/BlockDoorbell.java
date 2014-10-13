package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockButton;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockDoorbell extends BlockButton{

	public BlockDoorbell(Material par1Material) {
		super(false);
	}
	
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
    	if(par1World.isRemote){
    		return true;
    	}else{
    		par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.anvil_land", 5F, 0.6F);
     	   par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
     	   return true;
    	}
    }

	
//	 /**
//     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
//     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
//     */
//    public boolean isOpaqueCube()
//    {
//        return false;
//    }
//
//    /**
//     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
//     */
//    public boolean renderAsNormalBlock()
//    {
//        return false;
//    }
//	
//	@SideOnly(Side.CLIENT)
//
//    /**
//     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
//     */
//    public Icon getIcon(int par1, int par2)
//    {
//        return Block.stone.getBlockTextureFromSide(1);
//    }
//	
//    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
//           if(par1World.isRemote){
//        	   return true;
//           }else{
//        	   par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.anvil_land", 5F, 0.6F);
//        	   par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
//        	   return true;
//           }
//        
//    }
//    
//   
//    
//    
//    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
//    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 2){
//   		this.setBlockBounds(0.0F, 0.3F, 0.4F, 0.1F, 0.7F, -0.5F);
//    	}
//    }
//    
//    /**
//     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
//     */
//    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
//    {
//        int j1 = par1World.getBlockMetadata(par2, par3, par4);
//        int k1 = j1 & 8;
//        j1 &= 7;
//
//
//        ForgeDirection dir = ForgeDirection.getOrientation(par5);
//
//        if (dir == NORTH && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH))
//        {
//            j1 = 4;
//        }
//        else if (dir == SOUTH && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH))
//        {
//            j1 = 3;
//        }
//        else if (dir == WEST && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST))
//        {
//            j1 = 2;
//        }
//        else if (dir == EAST && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST))
//        {
//            j1 = 1;
//        }
//        else
//        {
//            j1 = this.getOrientation(par1World, par2, par3, par4);
//        }
//
//        return j1 + k1;
//    }
//
//    /**
//     * Get side which this button is facing.
//     */
//    private int getOrientation(World par1World, int par2, int par3, int par4)
//    {
//        if (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, EAST)) return 1;
//        if (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, WEST)) return 2;
//        if (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, SOUTH)) return 3;
//        if (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, NORTH)) return 4;
//        return 1;
//    }
//    
//    /**
//     * Updates the blocks bounds based on its current state. Args: world, x, y, z
//     */
//    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
//    {
//        int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
//        this.func_82534_e(l);
//    }
//
//    private void func_82534_e(int par1)
//    {
//        int j = par1 & 7;
//        boolean flag = (par1 & 8) > 0;
//        float f = 0.375F;
//        float f1 = 0.625F;
//        float f2 = 0.1875F;
//        float f3 = 0.125F;
//
//        if (flag)
//        {
//            f3 = 0.0625F;
//        }
//
//        if (j == 1)
//        {
//            this.setBlockBounds(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
//        }
//        else if (j == 2)
//        {
//            this.setBlockBounds(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
//        }
//        else if (j == 3)
//        {
//            this.setBlockBounds(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
//        }
//        else if (j == 4)
//        {
//            this.setBlockBounds(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
//        }
//    }
//
//
//    /**
//     * Called when the block is placed in the world.
//     */
//    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
//    {
//        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
//
//        if (l == 0)
//        {
//            par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
//            System.out.println(par1World.getBlockMetadata(par2, par3, par4) + " | Portable radar id");
//
//        }
//
//        if (l == 1)
//        {
//            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
//            System.out.println(par1World.getBlockMetadata(par2, par3, par4) + " | Portable radar id");
//
//
//
//        }
//
//        if (l == 2)
//        {
//            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
//            System.out.println(par1World.getBlockMetadata(par2, par3, par4) + " | Portable radar id");
//
//
//
//        }
//
//        if (l == 3)
//        {
//            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
//            System.out.println(par1World.getBlockMetadata(par2, par3, par4) + " | Portable radar id");
//
//    	}else{
//    		return;
//    	}
//
//       
//    }
    

}
