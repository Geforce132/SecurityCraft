package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAlarm extends BlockOwnable {

	public final boolean isLit;
	public static final PropertyEnum FACING = PropertyDirection.create("facing");

	public BlockAlarm(Material par1Material, boolean isLit) {
		super(par1Material);

		this.isLit = isLit;
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

		if(isLit)
			setLightLevel(1.0F);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isFullCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side){
		return side == EnumFacing.UP && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) ? true : worldIn.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		return worldIn.isSideSolid(pos.west(),  EnumFacing.EAST ) ||
				worldIn.isSideSolid(pos.east(),  EnumFacing.WEST ) ||
				worldIn.isSideSolid(pos.north(), EnumFacing.SOUTH) ||
				worldIn.isSideSolid(pos.south(), EnumFacing.NORTH) ||
				worldIn.isSideSolid(pos.down(),  EnumFacing.UP   ) ||
				worldIn.isSideSolid(pos.up(),    EnumFacing.DOWN );
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {
		if(par1World.isRemote)
			return;
		else
			par1World.scheduleUpdate(pos, state.getBlock(), 1);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
		if(!par1World.isRemote){
			playSoundAndUpdate(par1World, pos);

			par1World.scheduleUpdate(pos, state.getBlock(), 5);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block){
		if(par1World.isRemote)
			return;
		else
			playSoundAndUpdate(par1World, pos);

		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

		if (!par1World.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true))
		{
			dropBlockAsItem(par1World, pos, state, 0);
			par1World.setBlockToAir(pos);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos){
		float f = 0.1875F;
		float ySideMin = 0.5F - f; //bottom of the alarm when placed on a block side
		float ySideMax = 0.5F + f; //top of the alarm when placed on a block side
		float hSideMin = 0.5F - f; //the left start for s/w and right start for n/e
		float hSideMax = 0.5F + f; //the left start for n/e and right start for s/w
		float px = 1.0F / 16.0F; //one sixteenth of a block

		EnumFacing enumfacing = (EnumFacing) worldIn.getBlockState(pos).getValue(FACING);
		switch(BlockAlarm.SwitchEnumFacing.FACING_LOOKUP[enumfacing.ordinal()]){
			case 1: //east
				setBlockBounds(0.0F, ySideMin - px, hSideMin - px, 0.5F, ySideMax + px, hSideMax + px);
				break;
			case 2: //west
				setBlockBounds(0.5F, ySideMin - px, hSideMin - px, 1.0F, ySideMax + px, hSideMax + px);
				break;
			case 3: //north
				setBlockBounds(hSideMin - px, ySideMin - px, 0.0F, hSideMax + px, ySideMax + px, 0.5F);
				break;
			case 4: //south
				setBlockBounds(hSideMin - px, ySideMin - px, 0.5F, hSideMax + px, ySideMax + px, 1.0F);
				break;
			case 5: //up
				setBlockBounds(0.5F - f - px, 0F, 0.5F - f - px, 0.5F + f + px, 0.5F, 0.5F + f + px);
				break;
			case 6: //down
				setBlockBounds(0.5F - f - px, 0.5F, 0.5F - f - px, 0.5F + f + px, 1.0F, 0.5F + f + px);
				break;
		}
	}

	private void playSoundAndUpdate(World par1World, BlockPos pos){
		if(!(par1World.getTileEntity(pos) instanceof TileEntityAlarm)) return;

		if(par1World.isBlockIndirectlyGettingPowered(pos) > 0){
			boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(pos)).isPowered();

			if(!isPowered){
				Owner owner = ((TileEntityAlarm) par1World.getTileEntity(pos)).getOwner();
				EnumFacing dir = BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING);
				BlockUtils.setBlock(par1World, pos, SCContent.alarmLit);
				BlockUtils.setBlockProperty(par1World, pos, FACING, dir);
				((TileEntityAlarm) par1World.getTileEntity(pos)).getOwner().set(owner);
				((TileEntityAlarm) par1World.getTileEntity(pos)).setPowered(true);
			}

		}else{
			boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(pos)).isPowered();

			if(isPowered){
				Owner owner = ((TileEntityAlarm) par1World.getTileEntity(pos)).getOwner();
				EnumFacing dir = BlockUtils.getBlockPropertyAsEnum(par1World, pos, FACING);
				BlockUtils.setBlock(par1World, pos, SCContent.alarm);
				BlockUtils.setBlockProperty(par1World, pos, FACING, dir);
				((TileEntityAlarm) par1World.getTileEntity(pos)).getOwner().set(owner);
				((TileEntityAlarm) par1World.getTileEntity(pos)).setPowered(false);
			}
		}
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, BlockPos pos){
		return Item.getItemFromBlock(SCContent.alarm);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_){
		return Item.getItemFromBlock(SCContent.alarm);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state){
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing enumfacing;

		switch (meta & 7){
			case 0:
				enumfacing = EnumFacing.DOWN;
				break;
			case 1:
				enumfacing = EnumFacing.EAST;
				break;
			case 2:
				enumfacing = EnumFacing.WEST;
				break;
			case 3:
				enumfacing = EnumFacing.SOUTH;
				break;
			case 4:
				enumfacing = EnumFacing.NORTH;
				break;
			case 5:
			default:
				enumfacing = EnumFacing.UP;
		}

		return getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		int i;

		switch(BlockAlarm.SwitchEnumFacing.FACING_LOOKUP[((EnumFacing)state.getValue(FACING)).ordinal()]){
			case 1:
				i = 1;
				break;
			case 2:
				i = 2;
				break;
			case 3:
				i = 3;
				break;
			case 4:
				i = 4;
				break;
			case 5:
			default:
				i = 5;
				break;
			case 6:
				i = 0;
		}

		return i;
	}

	@Override
	protected BlockState createBlockState(){
		return new BlockState(this, new IProperty[] {FACING});
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

	static final class SwitchEnumFacing{
		static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

		static{
			try{
				FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
			}catch (NoSuchFieldError var6){
				;
			}

			try{
				FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
			}catch (NoSuchFieldError var5){
				;
			}

			try{
				FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
			}catch (NoSuchFieldError var4){
				;
			}

			try{
				FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
			}catch (NoSuchFieldError var3){
				;
			}

			try{
				FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
			}catch (NoSuchFieldError var2){
				;
			}

			try{
				FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
			}catch (NoSuchFieldError var1){
				;
			}
		}
	}
}
