package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BaseFullMineBlock extends ExplosiveBlock implements IIntersectable, IOverlayDisplay, IBlockMine {

	private final Block blockDisguisedAs;
	private final SoundType soundType;

	public BaseFullMineBlock(Block.Properties properties, Block disguisedBlock) {
		super(properties);
		blockDisguisedAs = disguisedBlock;
		this.soundType = SoundType.STONE;
	}

	public BaseFullMineBlock(Material material, SoundType soundType, Block disguisedBlock, float baseHardness) {
		super(soundType, material, baseHardness);
		blockDisguisedAs = disguisedBlock;
		this.soundType = soundType;
	}

	@Override
	public SoundType getSoundType(BlockState state)
	{
		return soundType;
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
		}

		return VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity){
		if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof LivingEntity && !PlayerUtils.isPlayerMountedOnCamera((LivingEntity)entity) && !EntityUtils.doesEntityOwn(entity, world, pos))
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
			if(player != null && player.isCreative() && !ConfigHandler.CONFIG.mineExplodesWhenInCreative.get())
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {}

	@Override
	public void defuseMine(World world, BlockPos pos) {}

	@Override
	public void explode(World world, BlockPos pos) {
		world.destroyBlock(pos, false);

		if(ConfigHandler.CONFIG.smallerMineExplosion.get())
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 2.5F, ConfigHandler.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
		else
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 5.0F, ConfigHandler.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
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
		return new SecurityCraftTileEntity().intersectsEntities();
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return false;
	}

}