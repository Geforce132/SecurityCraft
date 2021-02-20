package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAlarm extends BlockOwnable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockAlarm(Material material, boolean isLit) {
		super(material);

		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

		if(isLit)
			setLightLevel(1.0F);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		return side == EnumFacing.UP && world.isSideSolid(pos.down(), EnumFacing.UP) ? true : world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!canPlaceBlockOnSide(world, pos, state.getValue(FACING))) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return world.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if(world.isRemote)
			return;
		else
			world.scheduleUpdate(pos, state.getBlock(), 5);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random){
		if(!world.isRemote){
			playSoundAndUpdate(world, pos);

			world.scheduleUpdate(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos neighbor){
		World world = ((World)w);

		if(world.isRemote)
			return;

		playSoundAndUpdate((world), pos);

		EnumFacing facing = world.getBlockState(pos).getValue(FACING);

		if (!world.isSideSolid(pos.offset(facing.getOpposite()), facing, true))
		{
			dropBlockAsItem((world), pos, world.getBlockState(pos), 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		float threePx = 0.1875F;
		float ySideMin = 0.5F - threePx; //bottom of the alarm when placed on a block side
		float ySideMax = 0.5F + threePx; //top of the alarm when placed on a block side
		float hSideMin = 0.5F - threePx; //the left start for s/w and right start for n/e
		float hSideMax = 0.5F + threePx; //the left start for n/e and right start for s/w
		float px = 1.0F / 16.0F; //one sixteenth of a block
		EnumFacing facing = state.getValue(FACING);

		switch(BlockAlarm.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()]){
			case 1: //east
				return new AxisAlignedBB(0.0F, ySideMin - px, hSideMin - px, 0.5F, ySideMax + px, hSideMax + px);
			case 2: //west
				return new AxisAlignedBB(0.5F, ySideMin - px, hSideMin - px, 1.0F, ySideMax + px, hSideMax + px);
			case 3: //north
				return new AxisAlignedBB(hSideMin - px, ySideMin - px, 0.0F, hSideMax + px, ySideMax + px, 0.5F);
			case 4: //south
				return new AxisAlignedBB(hSideMin - px, ySideMin - px, 0.5F, hSideMax + px, ySideMax + px, 1.0F);
			case 5: //up
				return new AxisAlignedBB(0.5F - threePx - px, 0F, 0.5F - threePx - px, 0.5F + threePx + px, 0.5F, 0.5F + threePx + px);
			case 6: //down
				return new AxisAlignedBB(0.5F - threePx - px, 0.5F, 0.5F - threePx - px, 0.5F + threePx + px, 1.0F, 0.5F + threePx + px);
		}

		return state.getBoundingBox(source, pos);
	}

	private void playSoundAndUpdate(World world, BlockPos pos){
		if(!(world.getBlockState(pos).getBlock() instanceof BlockAlarm) || !(world.getTileEntity(pos) instanceof TileEntityAlarm)) return;

		TileEntityAlarm te = (TileEntityAlarm)world.getTileEntity(pos);

		if(world.getRedstonePowerFromNeighbors(pos) > 0){
			boolean isPowered = te.isPowered();

			if(!isPowered){
				Owner owner = te.getOwner();
				EnumFacing dir = BlockUtils.getBlockProperty(world, pos, FACING);
				OptionInt range = te.range;
				world.setBlockState(pos, SCContent.alarmLit.getDefaultState());
				BlockUtils.setFacingProperty(world, pos, FACING, dir);
				te = (TileEntityAlarm)world.getTileEntity(pos);
				te.getOwner().set(owner);
				te.setPowered(true);
				te.range.copy(range);
			}

		}else{
			boolean isPowered = te.isPowered();

			if(isPowered){
				Owner owner = te.getOwner();
				EnumFacing dir = BlockUtils.getBlockProperty(world, pos, FACING);
				OptionInt range = te.range;
				world.setBlockState(pos, SCContent.alarm.getDefaultState());
				BlockUtils.setFacingProperty(world, pos, FACING, dir);
				te = (TileEntityAlarm)world.getTileEntity(pos);
				te.getOwner().set(owner);
				te.setPowered(false);
				te.range.copy(range);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state){
		return new ItemStack(Item.getItemFromBlock(SCContent.alarm));
	}

	@Override
	public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_){
		return Item.getItemFromBlock(SCContent.alarm);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing facing;

		switch (meta & 7){
			case 0:
				facing = EnumFacing.DOWN;
				break;
			case 1:
				facing = EnumFacing.EAST;
				break;
			case 2:
				facing = EnumFacing.WEST;
				break;
			case 3:
				facing = EnumFacing.SOUTH;
				break;
			case 4:
				facing = EnumFacing.NORTH;
				break;
			case 5:
			default:
				facing = EnumFacing.UP;
		}

		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		int meta;

		switch(BlockAlarm.SwitchEnumFacing.FACING_LOOKUP[state.getValue(FACING).ordinal()]){
			case 1:
				meta = 1;
				break;
			case 2:
				meta = 2;
				break;
			case 3:
				meta = 3;
				break;
			case 4:
				meta = 4;
				break;
			case 5:
			default:
				meta = 5;
				break;
			case 6:
				meta = 0;
		}

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		EnumFacing facing = state.getValue(FACING);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if(facing.getAxis() == Axis.Z)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if(facing.getAxis() == Axis.X)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case NONE: break;
		}

		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityAlarm();
	}

	static final class SwitchEnumFacing{
		static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

		static{
			try{
				FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
			}catch (NoSuchFieldError e){

			}

			try{
				FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
			}catch (NoSuchFieldError e){

			}

			try{
				FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
			}catch (NoSuchFieldError e){

			}

			try{
				FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
			}catch (NoSuchFieldError e){

			}

			try{
				FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
			}catch (NoSuchFieldError e){

			}

			try{
				FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
			}catch (NoSuchFieldError e){

			}
		}
	}
}
