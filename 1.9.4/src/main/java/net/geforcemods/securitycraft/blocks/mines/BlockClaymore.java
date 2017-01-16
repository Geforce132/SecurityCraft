package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockClaymore extends BlockContainer implements IExplosive {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

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
        return worldIn.getBlockState(pos.down()).getBlock().isSideSolid(worldIn.getBlockState(pos.down()), worldIn, pos.down(), EnumFacing.UP);
    }
    
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
		if(!par1World.isRemote){
			if(par5EntityPlayer.inventory.getCurrentItem() != null && par5EntityPlayer.inventory.getCurrentItem().getItem() == mod_SecurityCraft.wireCutters){
				par1World.setBlockState(pos, mod_SecurityCraft.claymore.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(DEACTIVATED, true));
				return true;
			}else if(par5EntityPlayer.inventory.getCurrentItem() != null && par5EntityPlayer.inventory.getCurrentItem().getItem() == Items.FLINT_AND_STEEL){
				par1World.setBlockState(pos, mod_SecurityCraft.claymore.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(DEACTIVATED, false));
				return true;
			}
		}
		
		return false;
	}
	
    @Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
    	if (!world.isRemote && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED).booleanValue())
        {
            BlockUtils.destroyBlock(world, pos, false);
            world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
        }
    	
    	return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        if (!worldIn.isRemote && BlockUtils.hasBlockProperty(worldIn, pos, BlockClaymore.DEACTIVATED) && !worldIn.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED).booleanValue())
        {
            BlockUtils.destroyBlock(worldIn, pos, false);
            worldIn.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
        }
    }
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(DEACTIVATED, false);
    }
	
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
		}
	}

	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
		}
	}
	
	public void explode(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.5F, true);
		}
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (source.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH)
    		return new AxisAlignedBB(0.225F, 0.000F, 0.175F, 0.775F, 0.325F, 0.450F);
        else if (source.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
        	return new AxisAlignedBB(0.225F, 0.000F, 0.550F, 0.775F, 0.325F, 0.825F);
        else if (source.getBlockState(pos).getValue(FACING) == EnumFacing.EAST)
        	return new AxisAlignedBB(0.550F, 0.0F, 0.225F, 0.825F, 0.335F, 0.775F);
        else
        	return new AxisAlignedBB(0.175F, 0.0F, 0.225F, 0.450F, 0.335F, 0.775F);
	}
	
	@SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
    	if(meta <= 5){
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(DEACTIVATED, true);
        }else{
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(DEACTIVATED, false);
        }
    }

    public int getMetaFromState(IBlockState state)
    {
    	if(state.getValue(DEACTIVATED).booleanValue()){
    		return (state.getValue(FACING).getIndex() + 6);
    	}else{
    		return state.getValue(FACING).getIndex();
    	}
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, DEACTIVATED});
    }
    
    public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED).booleanValue();
	}
    
    public boolean isDefusable() {
		return true;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityClaymore();
	}
}
