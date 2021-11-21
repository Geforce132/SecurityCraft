package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.NamedTileEntity;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BaseFullMineBlock extends ExplosiveBlock implements IOverlayDisplay, IBlockMine {

	private final Block blockDisguisedAs;

	public BaseFullMineBlock(Block.Properties properties, Block disguisedBlock) {
		super(properties);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		if(ctx instanceof EntitySelectionContext)
		{
			Entity entity = ((EntitySelectionContext)ctx).getEntity();

			if(entity instanceof ItemEntity)
				return VoxelShapes.fullCube();
			else if(entity instanceof PlayerEntity)
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof OwnableTileEntity)
				{
					OwnableTileEntity ownableTe = (OwnableTileEntity) te;

					if(ownableTe.getOwner().isOwner((PlayerEntity)entity))
						return VoxelShapes.fullCube();
				}
			}

			return ctx == ISelectionContext.dummy() ? VoxelShapes.fullCube() : VoxelShapes.empty();
		}

		return VoxelShapes.fullCube();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
		if(entity instanceof LivingEntity && !PlayerUtils.isPlayerMountedOnCamera((LivingEntity)entity) && !EntityUtils.doesEntityOwn(entity, world, pos))
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
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		if(!world.isRemote)
			if(player != null && player.isCreative() && !ConfigHandler.SERVER.mineExplodesWhenInCreative.get())
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
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
		if(!world.isRemote) {
			world.destroyBlock(pos, false);
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 2.5F : 5.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new NamedTileEntity();
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		if (world.getTileEntity(pos) instanceof OwnableTileEntity) {
			OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(pos);

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