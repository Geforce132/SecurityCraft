package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortableRadar extends BlockOwnable {

	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3F, 0.0F, 0.3F, 0.7F, 0.45F, 0.7F);
	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3F, 0.55F, 0.3F, 0.7F, 1.0F, 0.7F);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0F, 0.3F, 0.3F, 0.45F, 0.7F, 0.7F);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.55F, 0.3F, 0.3F, 1.0F, 0.7F, 0.7F);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3F, 0.3F, 0.55F, 0.7F, 0.7F, 1.0F);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3F, 0.3F, 0.0F, 0.7F, 0.7F, 0.45F);

	public BlockPortableRadar(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(POWERED, false).withProperty(FACING, EnumFacing.UP));
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		switch(state.getValue(FACING))
		{
			case EAST: return EAST_AABB;
			case WEST: return WEST_AABB;
			case SOUTH: return SOUTH_AABB;
			case NORTH: return NORTH_AABB;
			case UP: return UP_AABB;
			case DOWN: return DOWN_AABB;
		}

		return FULL_BLOCK_AABB;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if(!canPlaceBlockOnSide(world, pos, state.getValue(FACING)))
			world.destroyBlock(pos, true);
	}

	public static void togglePowerOutput(World world, BlockPos pos, boolean par5) {
		IBlockState state = world.getBlockState(pos);
		boolean powered = state.getValue(POWERED);

		if(par5 && !powered)
			world.setBlockState(pos, state.withProperty(POWERED, true));
		else if(!par5 && powered)
			world.setBlockState(pos, state.withProperty(POWERED, false));
	}

	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
		if(state.getValue(POWERED) && ((IModuleInventory) world.getTileEntity(pos)).hasModule(EnumModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		boolean powered = meta % 2 == 1;
		EnumFacing facing = EnumFacing.UP;

		if(powered)
			meta--;

		switch(meta)
		{
			case 2: facing = EnumFacing.DOWN; break;
			case 4: facing = EnumFacing.NORTH; break;
			case 6: facing = EnumFacing.SOUTH; break;
			case 8: facing = EnumFacing.WEST; break;
			case 10: facing = EnumFacing.EAST; break;
		}

		return getDefaultState().withProperty(POWERED, powered).withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int meta = 0;

		switch(state.getValue(FACING))
		{
			case UP: meta = 0; break;
			case DOWN: meta = 2; break;
			case NORTH: meta = 4; break;
			case SOUTH: meta = 6; break;
			case WEST: meta = 8; break;
			case EAST: meta = 10; break;
		}

		return meta + (state.getValue(POWERED) ? 1 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, POWERED, FACING);
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPortableRadar();
	}

}
