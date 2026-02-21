package net.geforcemods.securitycraft.blocks.reinforced;

import com.mojang.serialization.MapCodec;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.ReinforcedCauldronInteractions;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.cauldron.CauldronInteraction.Dispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedCauldronBlock extends AbstractCauldronBlock implements IReinforcedBlock, EntityBlock {
	public static final MapCodec<ReinforcedCauldronBlock> CODEC = simpleCodec(ReinforcedCauldronBlock::new);
	private final float destroyTimeForOwner;

	public ReinforcedCauldronBlock(BlockBehaviour.Properties properties) {
		this(properties, ReinforcedCauldronInteractions.EMPTY);
	}

	public ReinforcedCauldronBlock(BlockBehaviour.Properties properties, Dispatcher dispatcher) {
		super(OwnableBlock.withReinforcedDestroyTime(properties), dispatcher);
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	protected MapCodec<? extends AbstractCauldronBlock> codec() {
		return CODEC;
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		if (collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity() != null) {
			Entity entity = ctx.getEntity();

			if (entity instanceof Player player) {
				if (level.getBlockEntity(pos) instanceof ReinforcedCauldronBlockEntity be && be.isAllowedToInteract(player))
					return SHAPE;
				else
					return Shapes.block();
			}
		}

		return SHAPE;
	}

	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ReinforcedCauldronBlockEntity be && be.isAllowedToInteract(player))
			return super.useItemOn(stack, state, level, pos, player, hand, hit);

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public boolean isFull(BlockState state) {
		return false;
	}

	protected static boolean shouldHandlePrecipitation(Level level, Precipitation precipitation) {
		return switch (precipitation) {
			case RAIN -> level.getRandom().nextFloat() < 0.05F;
			case SNOW -> level.getRandom().nextFloat() < 0.1F;
			default -> false;
		};
	}

	@Override
	public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Precipitation precipitation) {
		if (shouldHandlePrecipitation(level, precipitation)) {
			BlockState newCauldronState = null;

			if (precipitation == Precipitation.RAIN)
				newCauldronState = SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState();
			else if (precipitation == Precipitation.SNOW)
				newCauldronState = SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get().defaultBlockState();

			if (newCauldronState != null) {
				updateBlockState(level, pos, newCauldronState);
				level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			}
		}
	}

	@Override
	protected boolean canReceiveStalactiteDrip(Fluid fluid) {
		return true;
	}

	@Override
	protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
		BlockState newCauldronState = null;
		int levelEvent = 0;

		if (fluid == Fluids.WATER) {
			newCauldronState = SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState();
			levelEvent = LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON;
		}
		else if (fluid == Fluids.LAVA) {
			newCauldronState = SCContent.REINFORCED_LAVA_CAULDRON.get().defaultBlockState();
			levelEvent = LevelEvent.SOUND_DRIP_LAVA_INTO_CAULDRON;
		}

		if (newCauldronState != null) {
			updateBlockState(level, pos, newCauldronState);
			level.levelEvent(levelEvent, pos, 0);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newCauldronState));
		}
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.CAULDRON;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedCauldronBlockEntity(pos, state);
	}

	public static void updateBlockState(Level level, BlockPos pos, BlockState newState) {
		updateBlockState(level, pos, newState, level.getBlockEntity(pos));
	}

	public static void updateBlockState(Level level, BlockPos pos, BlockState newState, BlockEntity be) {
		CompoundTag tag = null;
		HolderLookup.Provider lookupProvider = level.registryAccess();

		if (be != null)
			tag = be.saveCustomOnly(lookupProvider);

		level.setBlockAndUpdate(pos, newState);

		if (tag != null) {
			be = level.getBlockEntity(pos);

			try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(be.problemPath(), SecurityCraft.LOGGER)) {
				be.loadCustomOnly(TagValueInput.create(problemReporter, level.registryAccess(), tag));
			}
		}
	}
}
