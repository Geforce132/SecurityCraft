package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockProtecto extends BlockOwnable {

	public BlockProtecto(Material par1) {
		super(par1);
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public int getRenderType(){
		return -1;
	}
	
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4){
        return par1World.isSideSolid(par2, par3 - 1, par4, ForgeDirection.UP);
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityProtecto();
	}
	
}
