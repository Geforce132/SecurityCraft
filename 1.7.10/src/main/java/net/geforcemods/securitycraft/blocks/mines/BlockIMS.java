package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIMS extends BlockOwnable {

	public BlockIMS(Material par1) {
		super(par1);
		this.setBlockBounds(0F, 0F, 0F, 1F, 0.45F, 1F);
	}
	
	public boolean isOpaqueCube(){
        return false;
    }
    
    public boolean isNormalCube(){
        return false;
    } 
    
    public int getRenderType(){
    	return -1;
    }

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote){
			if(((IOwnable) par1World.getTileEntity(par2, par3, par4)).getOwner().isOwner(par5EntityPlayer)){
				par5EntityPlayer.openGui(mod_SecurityCraft.instance, GuiHandler.IMS_GUI_ID, par1World, par2, par3, par4);
				return true;
			}
		}
		
		return false;
	}
	
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
		if(!par1World.isRemote){
			BlockUtils.destroyBlock(par1World, par2, par3, par4, false);
		}                      
	}
	
	/**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random){
    	if(par1World.getTileEntity(par2, par3, par4) != null && ((TileEntityIMS) par1World.getTileEntity(par2, par3, par4)).getBombsRemaining() == 0){
    		double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d1 = (double)((float)par3 + 0.4F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
    		double d3 = 0.2199999988079071D;
    		double d4 = 0.27000001072883606D;

    		par1World.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    		par1World.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    		
    		par1World.spawnParticle("flame", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    		par1World.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
    	}
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityIMS();
	}

}
