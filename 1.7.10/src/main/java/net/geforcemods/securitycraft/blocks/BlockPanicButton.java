package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPanicButton extends BlockButton implements ITileEntityProvider {

	@SideOnly(Side.CLIENT)
	private IIcon buttonPowered;
	
	public BlockPanicButton() {
		super(false);
	}
	
	public boolean onBlockActivated(World worldIn, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
		if(worldIn.isRemote){
			return true;
		}else{
	        if(worldIn.getBlockMetadata(par2, par3, par4) > 4 && worldIn.getBlockMetadata(par2, par3, par4) < 10){
	        	worldIn.setBlockMetadataWithNotify(par2, par3, par4, worldIn.getBlockMetadata(par2, par3, par4) - 5, 3);
	        	worldIn.markBlockForUpdate(par2, par3, par4);
	            worldIn.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.4D, "random.click", 0.3F, 0.5F);
	            worldIn.scheduleBlockUpdate(par2, par3, par4, this, 1);
	            this.notifyNeighbors(worldIn, par2, par3, par4);
	            return true;
	        }else{
	        	worldIn.setBlockMetadataWithNotify(par2, par3, par4, worldIn.getBlockMetadata(par2, par3, par4) + 5, 3);
	            worldIn.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
	            worldIn.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "random.click", 0.3F, 0.6F);
	            worldIn.scheduleBlockUpdate(par2, par3, par4, this, 1);
	            this.notifyNeighbors(worldIn, par2, par3, par4);
	            return true;
	        }
        }
    }
	
    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_){}
    
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6) {
        if(par1World.isRemote){
        	return;
        }else{
        	this.notifyNeighbors(par1World, par2, par3, par4);
        }
        
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
    }

    public boolean onBlockEventReceived(World worldIn, int par2, int par3, int par4, int par5, int par6){
        super.onBlockEventReceived(worldIn, par2, par3, par4, par5, par6);
        TileEntity tileentity = worldIn.getTileEntity(par2, par3, par4);
        return tileentity == null ? false : tileentity.receiveClientEvent(par5, par6);
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
    	this.updateBlockBounds(l);
    }

	private void updateBlockBounds(int par1) {
		int j = par1 & 15;
		boolean flag = (par1 == 6 || par1 == 7 || par1 == 8 || par1 == 9);
        float f2 = (float)(flag ? 1 : 2) / 16.0F;
                
        if(j == 0){
        	this.setBlockBounds(0.1800F, 0.300F, 0.95F, 0.8150F, 0.700F, 1.0F);
        }else if (j == 1){
        	this.setBlockBounds(0.0F, 0.30F, 0.18F, f2, 0.70F, 0.82F);
        }else if (j == 2){
        	this.setBlockBounds(1.0F - f2, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
        }else if (j == 3){
        	this.setBlockBounds(0.1800F, 0.300F, 0.0F, 0.8150F, 0.700F, f2);
        }else if (j == 4){
        	this.setBlockBounds(0.1800F, 0.300F, 1.0F - f2, 0.8150F, 0.700F, 1.0F);
        }else if (j == 5){
        	this.setBlockBounds(1.0F - f2, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
        }else if (j == 6){
        	this.setBlockBounds(0.0F, 0.30F, 0.18F, f2, 0.70F, 0.82F);
        }else if (j == 7){
        	this.setBlockBounds(1.0F - f2, 0.30F, 0.18F, 1.0F, 0.70F, 0.82F);
        }else if (j == 8){
        	this.setBlockBounds(0.1800F, 0.300F, 0.0F, 0.8150F, 0.700F, f2);
        }else if (j == 9){
        	this.setBlockBounds(0.1800F, 0.300F, 1.0F - f2, 0.8150F, 0.700F, 1.0F);
        }
	}
	
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
		return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) > 4 && par1IBlockAccess.getBlockMetadata(par2, par3, par4) < 10) ? 15 : 0;
    }
	
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
		return (par1IBlockAccess.getBlockMetadata(par2, par3, par4) > 4 && par1IBlockAccess.getBlockMetadata(par2, par3, par4) < 10) ? 15 : 0;
    }
	
    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_){}

	private void notifyNeighbors(World par1World, int par2, int par3, int par4)
    {
		int meta = par1World.getBlockMetadata(par2, par3, par4);
		
		if(meta == 1 || meta == 6){
			par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this);
		}else if(meta == 2 || meta == 7){
	        par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this);
		}else if(meta == 3 || meta == 8){
	        par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this);
		}else if(meta == 4 || meta == 9){
	        par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this);
		}else{
			par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this);
			par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this);
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this);
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this);
		}
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
		if(par2 > 4 && par2 < 10){
			return this.buttonPowered;
		}else{
			return this.blockIcon;	
		}
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IIconRegister) {
		this.blockIcon = par1IIconRegister.registerIcon("securitycraft:panicButton");
		this.buttonPowered = par1IIconRegister.registerIcon("securitycraft:panicButtonPowered");
	}

	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityOwnable();
	}

}
