package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseFullMineBlock extends ExplosiveBlock implements IOverlayDisplay, IBlockMine {
	private final Block blockDisguisedAs;

	public BaseFullMineBlock(BlockBehaviour.Properties properties, Block disguisedBlock) {
		super(properties);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		if (collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity() != null) {
			Entity entity = ctx.getEntity();

			if ((entity instanceof ItemEntity) || level.getBlockEntity(pos) instanceof IOwnable ownable && ((entity instanceof Player player && (ownable.isOwnedBy(player) || player.isCreative())) || (entity instanceof OwnableEntity ownableEntity && ownable.allowsOwnableEntity(ownableEntity))))
				return Shapes.block();

			return Shapes.empty();
		}

		return Shapes.block();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.getBlockEntity(pos) instanceof IOwnable ownable && !ownable.isOwnedBy(entity))
			explode(level, pos);
	}

	@Override
	public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
		if (!level.isClientSide) {
			if (pos.equals(BlockPos.containing(explosion.getPosition())))
				return;

			explode(level, pos);
		}
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!level.isClientSide) {
			if (player != null && player.isCreative() && !ConfigHandler.SERVER.mineExplodesWhenInCreative.get())
				return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
			else if (!(level.getBlockEntity(pos) instanceof IOwnable ownable && ownable.isOwnedBy(player))) {
				explode(level, pos);
				return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
			}
		}

		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public boolean activateMine(Level level, BlockPos pos) {
		return false;
	}

	@Override
	public boolean defuseMine(Level level, BlockPos pos) {
		return false;
	}

	@Override
	public void explode(Level level, BlockPos pos) {
		if (!level.isClientSide) {
			level.destroyBlock(pos, false);
			level.explode(null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 2.5F : 5.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());
		}
	}

	@Override
	public boolean dropFromExplosion(Explosion explosion) {
		return false;
	}

	@Override
	public boolean isActive(Level level, BlockPos pos) {
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		if (IDisguisable.shouldPickBlockDisguise(level, pos, player))
			return new ItemStack(blockDisguisedAs);

		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return getBlockDisguisedAs().defaultBlockState();
	}

	public Block getBlockDisguisedAs() {
		return blockDisguisedAs;
	}
}