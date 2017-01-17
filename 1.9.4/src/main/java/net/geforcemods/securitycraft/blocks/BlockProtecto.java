package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockProtecto extends BlockOwnable {
	
	public static final PropertyBool ACTIVATED = PropertyBool.create("activated");

	public BlockProtecto(Material par1) {
		super(par1);
		setSoundType(SoundType.METAL);
	}
	
	public boolean isOpaqueCube(IBlockState state){
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
    	return state.getValue(ACTIVATED).booleanValue() == true ? 1 : 0;
    }
    
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {ACTIVATED});
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityProtecto().attacks(EntityLivingBase.class, 10, 200);
	}
	
}
