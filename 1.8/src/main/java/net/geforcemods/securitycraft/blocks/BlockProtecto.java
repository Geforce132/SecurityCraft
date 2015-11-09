package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockProtecto extends BlockOwnable {
	
	public static final PropertyBool ACTIVATED = PropertyBool.create("activated");

	public BlockProtecto(Material par1) {
		super(par1);
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
    public boolean canPlaceBlockAt(World par1World, BlockPos pos){
        return par1World.isSideSolid(pos.down(), EnumFacing.UP);
    }
    
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(ACTIVATED, false);
    }
    
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(ACTIVATED, meta == 1 ? true : false);
    }

    public int getMetaFromState(IBlockState state)
    {
    	return ((Boolean) state.getValue(ACTIVATED)).booleanValue() == true ? 1 : 0;
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {ACTIVATED});
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityProtecto().attacks(EntityLivingBase.class, 10, 200);
	}
	
}
