package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSecurityCamera extends BlockContainer{

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockSecurityCamera(Material par2Material) {
		super(par2Material);
	}
	
	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state){
        return null;
    }
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}

	public boolean isFullCube(){
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		EnumFacing dir = BlockUtils.getBlockPropertyAsEnum((World) source, pos, FACING);
        
    	if(dir == EnumFacing.SOUTH)
    		return new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
    	else if(dir == EnumFacing.NORTH)
    		return new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
        else if(dir == EnumFacing.WEST)
        	return new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
        else
        	return new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
	}
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        IBlockState iblockstate = this.getDefaultState().withProperty(POWERED, Boolean.valueOf(false));

        if(worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing)){
            return iblockstate.withProperty(FACING, facing).withProperty(POWERED, false);
        }else{
            Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();
            EnumFacing enumfacing1;

            do{
                if(!iterator.hasNext()){               
                    return iblockstate;
                }

                enumfacing1 = (EnumFacing)iterator.next();
            }while (!worldIn.isSideSolid(pos.offset(enumfacing1.getOpposite()), enumfacing1));

            return iblockstate.withProperty(FACING, facing).withProperty(POWERED, false);
        }
    }
    
    public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block){    			
		if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.NORTH){
			if(!par1World.isSideSolid(pos.south(), EnumFacing.NORTH)){
				BlockUtils.destroyBlock(par1World, pos, true);
			}
		}else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.SOUTH){
			if(!par1World.isSideSolid(pos.north(), EnumFacing.SOUTH)){
				BlockUtils.destroyBlock(par1World, pos, true);
			}
		}else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.EAST){
			if(!par1World.isSideSolid(pos.west(), EnumFacing.EAST)){
				BlockUtils.destroyBlock(par1World, pos, true);
			}
		}else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.WEST){
			if(!par1World.isSideSolid(pos.east(), EnumFacing.WEST)){
				BlockUtils.destroyBlock(par1World, pos, true);
			}
		}
	}
    
    public void mountCamera(World world, int par2, int par3, int par4, int par5, EntityPlayer player){
    	if(!world.isRemote && player.getRidingEntity() == null) {
    		PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("tile.securityCamera.name"), I18n.translateToLocal("messages.securityCamera.mounted"), TextFormatting.GREEN);
    	}
    	
    	if(player.getRidingEntity() != null && player.getRidingEntity() instanceof EntitySecurityCamera){
			EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, (EntitySecurityCamera) player.getRidingEntity());
			world.spawnEntityInWorld(dummyEntity);
			player.startRiding(dummyEntity);
			return;
		}

    	EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, player);
    	world.spawnEntityInWorld(dummyEntity);
    	player.startRiding(dummyEntity);
		
		for(Object e : world.loadedEntityList)
		{
			if(e instanceof EntityLiving)
			{
				if(((EntityLiving)e).getAttackTarget() == player)
					((EntityLiving)e).setAttackTarget(null);
			}
		}
    }
    
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side){
        return (side != EnumFacing.UP && side != EnumFacing.DOWN) ? super.canPlaceBlockOnSide(worldIn, pos, side) : false;
    }
    
    public boolean canPlaceBlockAt(World world, BlockPos pos){
        return !world.getBlockState(pos).getBlock().isReplaceable(world, pos) ^ //exclusive or
        	   (world.isSideSolid(pos.west(), EnumFacing.EAST, true) ||
               world.isSideSolid(pos.east(), EnumFacing.WEST, true) ||
               world.isSideSolid(pos.north(), EnumFacing.SOUTH, true) ||
               world.isSideSolid(pos.south(), EnumFacing.NORTH, true));
    }
    
    public boolean canProvidePower(){
        return true;
    }
    
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
    	if(((Boolean) state.getValue(POWERED)).booleanValue() && ((CustomizableSCTE) par1IBlockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){  	
    	if(((Boolean) state.getValue(POWERED)).booleanValue() && ((CustomizableSCTE) par1IBlockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE)){
    		return 15;
    	}else{
    		return 0;
    	}
    }
  
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        if(meta <= 5){
        	return this.getDefaultState().withProperty(FACING, (EnumFacing.values()[meta] == EnumFacing.UP || EnumFacing.values()[meta] == EnumFacing.DOWN) ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
        }else{
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
        }
    }

    public int getMetaFromState(IBlockState state)
    {
    	if(((Boolean) state.getValue(POWERED)).booleanValue()){
    		return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
    	}else{
    		return ((EnumFacing) state.getValue(FACING)).getIndex();
    	}
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
    }
    
    public TileEntity createNewTileEntity(World world, int par2){
    	return new TileEntitySecurityCamera().nameable();
    }

}
