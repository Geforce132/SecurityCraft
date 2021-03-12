package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockClaymore extends BlockExplosive {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockClaymore(Material material) {
		super(material);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else
			world.destroyBlock(pos, true);
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
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		if (!player.capabilities.isCreativeMode && !world.isRemote && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED))
		{
			world.destroyBlock(pos, false);

			if(!EntityUtils.doesPlayerOwn(player, world, pos))
				explode(world, pos);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote && BlockUtils.hasBlockProperty(world, pos, BlockClaymore.DEACTIVATED) && !world.getBlockState(pos).getValue(BlockClaymore.DEACTIVATED))
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(DEACTIVATED, false);
	}

	@Override
	public boolean activateMine(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		if(state.getValue(DEACTIVATED))
		{
			world.setBlockState(pos, state.withProperty(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		if(!state.getValue(DEACTIVATED))
		{
			world.setBlockState(pos, state.withProperty(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(!world.isRemote){
			world.destroyBlock(pos, false);
			world.newExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.smallerMineExplosion ? 1.5F : 3.5F, ConfigHandler.shouldSpawnFire, true);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (state.getValue(FACING) == EnumFacing.NORTH)
			return new AxisAlignedBB(0.225F, 0.000F, 0.175F, 0.775F, 0.325F, 0.450F);
		else if (state.getValue(FACING) == EnumFacing.SOUTH)
			return new AxisAlignedBB(0.225F, 0.000F, 0.550F, 0.775F, 0.325F, 0.825F);
		else if (state.getValue(FACING) == EnumFacing.EAST)
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
		if(state.getValue(DEACTIVATED))
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityClaymore();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}
}
