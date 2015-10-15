package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
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
	
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos){
		EnumFacing dir = BlockUtils.getBlockPropertyAsEnum((World) world, pos, FACING);
        
    	if(dir == EnumFacing.SOUTH){
    		this.setBlockBounds(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
    	}else if(dir == EnumFacing.NORTH){
    		this.setBlockBounds(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
        }else if(dir == EnumFacing.WEST){
    		this.setBlockBounds(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
        }else{
    		this.setBlockBounds(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
        }
	}

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	Block block = par1World.getBlockState(pos.north()).getBlock();
        Block block1 = par1World.getBlockState(pos.south()).getBlock();
        Block block2 = par1World.getBlockState(pos.west()).getBlock();
        Block block3 = par1World.getBlockState(pos.east()).getBlock();
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
        {
            enumfacing = EnumFacing.SOUTH;
        }
        else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
        {
            enumfacing = EnumFacing.NORTH;
        }
        else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
        {
            enumfacing = EnumFacing.EAST;
        }
        else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
        {
            enumfacing = EnumFacing.WEST;
        }

        par1World.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
       
        if(par5EntityLivingBase instanceof EntityPlayer){
    		((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), ((EntityPlayer) par5EntityLivingBase).getName());
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
    	if(player.ridingEntity == null && !world.isRemote) {
    		PlayerUtils.sendMessageToPlayer(player, "Security Camera", "You are now mounted to a camera. Use the WASD keys to move the camera's view, and the +/- buttons to zoom in and out.", EnumChatFormatting.GREEN);
    	}

    	EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5);
    	world.spawnEntityInWorld(dummyEntity);
    	player.mountEntity(dummyEntity);
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
    }
    
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
        return worldIn.isSideSolid(pos.west(), EnumFacing.EAST, true) ||
               worldIn.isSideSolid(pos.east(), EnumFacing.WEST, true) ||
               worldIn.isSideSolid(pos.north(), EnumFacing.SOUTH, true) ||
               worldIn.isSideSolid(pos.south(), EnumFacing.NORTH, true);
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
        	return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
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

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, POWERED});
    }
    
    public TileEntity createNewTileEntity(World world, int par2){
    	return new TileEntitySecurityCamera();
    }

}
