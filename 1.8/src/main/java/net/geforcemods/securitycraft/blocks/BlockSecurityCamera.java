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
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSecurityCamera extends BlockContainer{

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockSecurityCamera(Material par2Material) {
		super(par2Material);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state){
		return null;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isFullCube(){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		super.breakBlock(world, pos, state);

		world.notifyNeighborsOfStateChange(pos.north(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.south(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.east(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.west(), world.getBlockState(pos).getBlock());
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos){
		if(!(world instanceof World))
			return;

		EnumFacing dir = BlockUtils.getBlockPropertyAsEnum((World) world, pos, FACING);
		float px = 1.0F/16.0F; //one sixteenth of a block

		if(dir == EnumFacing.SOUTH)
			setBlockBounds(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
		else if(dir == EnumFacing.NORTH)
			setBlockBounds(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
		else if(dir == EnumFacing.WEST)
			setBlockBounds(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
		else if(dir == EnumFacing.DOWN)
			setBlockBounds(px * 5, 1.0F - px * 2, px * 5, px * 11, 1.0F, px * 11);
		else
			setBlockBounds(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		IBlockState iblockstate = getDefaultState().withProperty(POWERED, Boolean.valueOf(false));

		if(worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing))
			return iblockstate.withProperty(FACING, facing).withProperty(POWERED, false);
		else{
			Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();
			EnumFacing enumfacing1;

			do{
				if(!iterator.hasNext())
					return iblockstate;

				enumfacing1 = (EnumFacing)iterator.next();
			}while (!worldIn.isSideSolid(pos.offset(enumfacing1.getOpposite()), enumfacing1));

			return iblockstate.withProperty(FACING, facing).withProperty(POWERED, false);
		}
	}

	@Override
	public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block){
		if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.NORTH)
		{
			if(!par1World.isSideSolid(pos.south(), EnumFacing.NORTH))
				BlockUtils.destroyBlock(par1World, pos, true);
		}
		else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.SOUTH)
		{
			if(!par1World.isSideSolid(pos.north(), EnumFacing.SOUTH))
				BlockUtils.destroyBlock(par1World, pos, true);
		}
		else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.EAST)
		{
			if(!par1World.isSideSolid(pos.west(), EnumFacing.EAST))
				BlockUtils.destroyBlock(par1World, pos, true);
		}
		else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.WEST)
		{
			if(!par1World.isSideSolid(pos.east(), EnumFacing.WEST))
				BlockUtils.destroyBlock(par1World, pos, true);
		}
		else if(BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING) == EnumFacing.DOWN)
		{
			if(!par1World.isSideSolid(pos.up(), EnumFacing.DOWN))
				BlockUtils.destroyBlock(par1World, pos, true);
		}
	}

	public void mountCamera(World world, int par2, int par3, int par4, int par5, EntityPlayer player){
		if(!world.isRemote && player.ridingEntity == null)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securityCamera.name"), StatCollector.translateToLocal("messages.securityCamera.mounted"), EnumChatFormatting.GREEN);

		if(player.ridingEntity != null && player.ridingEntity instanceof EntitySecurityCamera){
			EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, (EntitySecurityCamera) player.ridingEntity);
			world.spawnEntityInWorld(dummyEntity);
			player.mountEntity(dummyEntity);
			return;
		}

		EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, player);
		world.spawnEntityInWorld(dummyEntity);
		player.mountEntity(dummyEntity);

		for(Object e : world.loadedEntityList)
			if(e instanceof EntityLiving)
				if(((EntityLiving)e).getAttackTarget() == player)
					((EntityLiving)e).setAttackTarget(null);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side){
		return (side != EnumFacing.UP) ? (side == EnumFacing.DOWN ? true : super.canPlaceBlockOnSide(worldIn, pos, side)) : false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos){
		return !world.getBlockState(pos).getBlock().isReplaceable(world, pos) ^ //exclusive or
				(world.isSideSolid(pos.west(), EnumFacing.EAST, true) ||
						world.isSideSolid(pos.east(), EnumFacing.WEST, true) ||
						world.isSideSolid(pos.north(), EnumFacing.SOUTH, true) ||
						world.isSideSolid(pos.south(), EnumFacing.NORTH, true) ||
						world.isSideSolid(pos.south(), EnumFacing.DOWN, true));
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
		if(((Boolean) state.getValue(POWERED)).booleanValue() && ((CustomizableSCTE) par1IBlockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, BlockPos pos, IBlockState state, EnumFacing side){
		if(((Boolean) state.getValue(POWERED)).booleanValue() && ((CustomizableSCTE) par1IBlockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta <= 5)
			return getDefaultState().withProperty(FACING, (EnumFacing.values()[meta] == EnumFacing.UP) ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(((Boolean) state.getValue(POWERED)).booleanValue())
			return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
		else
			return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2){
		return new TileEntitySecurityCamera().nameable();
	}

}
