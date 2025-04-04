package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IBlockMine;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.CreakingHeartMineBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;

public class CreakingHeartMineBlock extends CreakingHeartBlock implements IBlockMine, IExplosive, IOverlayDisplay {
	private final Block blockDisguisedAs;

	public CreakingHeartMineBlock(BlockBehaviour.Properties properties, Block disguisedBlock) {
		super(properties);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return exUseItemOn(heldItem, state, level, pos, player, hand, hit);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return exUseWithoutItem(state, level, pos, player, hit);
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
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
		if (level.getBlockEntity(pos) instanceof IOwnable ownable && !ownable.isOwnedBy(entity))
			explode(level, pos);
	}

	@Override
	public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
		if (pos.equals(BlockPos.containing(explosion.center())))
			return;

		explode(level, pos);
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
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide && state.getValue(STATE) != CreakingHeartState.UPROOTED ? createTickerHelper(type, SCContent.CREAKING_HEART_MINE_BLOCK_ENTITY.get(), CreakingHeartBlockEntity::serverTick) : null;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CreakingHeartMineBlockEntity(pos, state);
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
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		if (player.isCreative() || (level.getBlockEntity(pos) instanceof IOwnable be && be.isOwnedBy(player)))
			return super.getCloneItemStack(level, pos, state, includeData, player);

		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return getBlockDisguisedAs().defaultBlockState();
	}

	@Override
	public Block getBlockDisguisedAs() {
		return blockDisguisedAs;
	}
}
