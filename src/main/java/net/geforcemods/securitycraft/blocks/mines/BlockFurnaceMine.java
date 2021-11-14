package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockFurnaceMine extends BlockExplosive implements IOverlayDisplay, IBlockMine {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockFurnaceMine(Material material) {
		super(material);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
		if (!world.isRemote)
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		if(!world.isRemote)
			if(player != null && player.capabilities.isCreativeMode && !ConfigHandler.mineExplodesWhenInCreative)
				return super.removedByPlayer(state, world, pos, player, willHarvest);
			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem(hand).getItem() != SCContent.remoteAccessMine && !EntityUtils.doesPlayerOwn(player, world, pos)){
			explode(world, pos);
			return true;
		}
		else
			return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public boolean activateMine(World world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos)
	{
		return false;
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if (!world.isRemote) {
			world.destroyBlock(pos, false);
			world.newExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.smallerMineExplosion ? 2.5F : 5.0F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
		}
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		if (world.getTileEntity(pos) instanceof TileEntityOwnable) {
			TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(pos);

			if (player.isCreative() || te.getOwner().isOwner(player))
				return super.getPickBlock(state, target, world, pos, player);
		}

		return new ItemStack(Blocks.FURNACE);
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
