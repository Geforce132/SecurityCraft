package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSecurityCamera extends BlockContainer{

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockSecurityCamera(Material par2Material) {
		super(par2Material);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos){
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		if(state.getValue(FACING) == EnumFacing.DOWN)
			return EnumBlockRenderType.MODEL;
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		EnumFacing dir = BlockUtils.getBlockPropertyAsEnum(source, pos, FACING);
		float px = 1.0F/16.0F; //one sixteenth of a block

		if(dir == EnumFacing.SOUTH)
			return new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
		else if(dir == EnumFacing.NORTH)
			return new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
		else if(dir == EnumFacing.WEST)
			return new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
		else if(dir == EnumFacing.DOWN)
			return new AxisAlignedBB(px * 5, 1.0F - px * 2, px * 5, px * 11, 1.0F, px * 11);
		else
			return new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
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

	public void mountCamera(World world, int par2, int par3, int par4, int par5, EntityPlayer player){
		if(!world.isRemote && player.getRidingEntity() == null)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securityCamera.name"), ClientUtils.localize("messages.securityCamera.mounted"), TextFormatting.GREEN);

		if(player.getRidingEntity() != null && player.getRidingEntity() instanceof EntitySecurityCamera){
			EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, (EntitySecurityCamera) player.getRidingEntity());
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(dummyEntity));
			player.startRiding(dummyEntity);
			return;
		}

		EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, player);
		WorldUtils.addScheduledTask(world, () -> world.spawnEntity(dummyEntity));
		player.startRiding(dummyEntity);

		for(Object e : world.loadedEntityList)
			if(e instanceof EntityLiving)
				if(((EntityLiving)e).getAttackTarget() == player)
					((EntityLiving)e).setAttackTarget(null);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side){
		if(side == EnumFacing.UP)
			return false;
		else
			return worldIn.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public boolean canProvidePower(IBlockState state){
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue() && ((CustomizableSCTE) blockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue() && ((CustomizableSCTE) blockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (!canPlaceBlockAt(worldIn, pos) && !canPlaceBlockOnSide(worldIn, pos, state.getValue(FACING))) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
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
		if(state.getValue(POWERED).booleanValue())
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2){
		return new TileEntitySecurityCamera().nameable();
	}

}
