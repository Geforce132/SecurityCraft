package net.geforcemods.securitycraft.blocks.mines;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.TileEntityNamed;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFullMineBase extends BlockExplosive implements IOverlayDisplay, IBlockMine {

	private final Block blockDisguisedAs;

	public BlockFullMineBase(Material material, Block disguisedBlock) {
		super(material);
		blockDisguisedAs = disguisedBlock;

		if(material == Material.SAND)
			setSoundType(SoundType.SAND);
		else if(material == Material.GROUND)
			setSoundType(SoundType.GROUND);
		else
			setSoundType(SoundType.STONE);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess access, BlockPos pos){
		return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState)
	{
		if(entity instanceof EntityItem)
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
			return;
		}
		else if(entity instanceof EntityPlayer)
		{
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof TileEntityOwnable)
			{
				TileEntityOwnable ownableTe = (TileEntityOwnable) te;

				if(ownableTe.getOwner().isOwner((EntityPlayer)entity))
				{
					addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
					return;
				}
			}
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity){
		if(!EntityUtils.doesEntityOwn(entity, world, pos))
			explode(world, pos);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion){
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
			world.newExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), ConfigHandler.smallerMineExplosion ? 2.5F : 5.0F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
		}
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean canDropFromExplosion(Explosion explosion){
		return false;
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityNamed();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
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

		return new ItemStack(blockDisguisedAs);
	}

	public Block getBlockDisguisedAs()
	{
		return blockDisguisedAs;
	}
}