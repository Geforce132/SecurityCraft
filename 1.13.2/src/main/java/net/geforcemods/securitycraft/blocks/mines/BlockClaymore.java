package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;

public class BlockClaymore extends BlockContainer implements IExplosive {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockClaymore(Material material) {
		super(material);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess access, BlockPos pos)
	{
		return null;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock().isSideSolid(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == SCContent.wireCutters){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(DEACTIVATED, true));
				return true;
			}else if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == Items.FLINT_AND_STEEL){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().withProperty(FACING, state.getValue(FACING)).withProperty(DEACTIVATED, false));
				return true;
			}

		return false;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		if (!player.capabilities.isCreativeMode && !world.isRemote && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED).booleanValue())
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote && BlockUtils.hasBlockProperty(world, pos, BlockClaymore.DEACTIVATED) && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED).booleanValue())
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(DEACTIVATED, false);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
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

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(DEACTIVATED, true);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(DEACTIVATED, false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getValue(DEACTIVATED).booleanValue())
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING, DEACTIVATED});
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED).booleanValue();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityClaymore();
	}
}
