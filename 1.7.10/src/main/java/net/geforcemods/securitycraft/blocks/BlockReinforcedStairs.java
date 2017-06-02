package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReinforcedStairs extends BlockStairs implements ITileEntityProvider {
    
	public BlockReinforcedStairs(Block par1, int par2) {
		super(par1, par2);
		this.useNeighborBrightness = true;
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
        super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
        
        if(par1World.getTileEntity(par2, par3, par4) != null){
        	par1World.removeTileEntity(par2, par3, par4);
        }
    }
	
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityOwnable();
	}

	@Override
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return 0x999999;
	}
    
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int p_149741_1_)
    {
        return 0x999999;
    }
	
    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
    	return 0x999999;
    }
	
}
