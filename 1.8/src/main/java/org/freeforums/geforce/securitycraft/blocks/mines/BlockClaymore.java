package org.freeforums.geforce.securitycraft.blocks.mines;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityClaymore;

public class BlockClaymore extends BlockContainer{
	
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockClaymore(Material materialIn) {
		super(materialIn);
	}
	
	public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    } 
    
    public int getRenderType(){
    	return 3;
    }
    
    public boolean isFullCube()
    {
        return false;
    }
    
    public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state)
    {
        return null;
    }
    
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }  
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getBlock().isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }
    
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
    	if (!worldIn.isRemote)
        {
            Utils.destroyBlock(worldIn, pos, false);
            worldIn.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
        }
    }
    
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        if (!worldIn.isRemote)
        {
            Utils.destroyBlock(worldIn, pos, false);
            worldIn.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
        }
    }
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }
	
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos)
    {
        if ((EnumFacing) par1IBlockAccess.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH)
        {
    		this.setBlockBounds(0.225F, 0.000F, 0.175F, 0.775F, 0.325F, 0.450F);
        }
        else if ((EnumFacing) par1IBlockAccess.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
        {
    		this.setBlockBounds(0.225F, 0.000F, 0.550F, 0.775F, 0.325F, 0.825F);
        }
        else if ((EnumFacing) par1IBlockAccess.getBlockState(pos).getValue(FACING) == EnumFacing.EAST)
        {
    		this.setBlockBounds(0.550F, 0.0F, 0.225F, 0.825F, 0.335F, 0.775F);
        }
        else
        {
    		this.setBlockBounds(0.175F, 0.0F, 0.225F, 0.450F, 0.335F, 0.775F);
        }
        
    } 
	
	@SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityClaymore();
	}

}
