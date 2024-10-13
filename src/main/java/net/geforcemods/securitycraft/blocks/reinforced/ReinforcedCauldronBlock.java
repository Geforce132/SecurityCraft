package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;
import java.util.function.Predicate;

import com.mojang.serialization.MapCodec;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedCauldronBlock extends AbstractCauldronBlock implements IReinforcedBlock, EntityBlock {
	public static final MapCodec<ReinforcedCauldronBlock> CODEC = simpleCodec(ReinforcedCauldronBlock::new);

	public ReinforcedCauldronBlock(BlockBehaviour.Properties properties) {
		this(properties, IReinforcedCauldronInteraction.EMPTY);
	}

	public ReinforcedCauldronBlock(BlockBehaviour.Properties properties, CauldronInteraction.InteractionMap interactions) {
		super(properties, interactions);
	}

	@Override
	protected MapCodec<? extends AbstractCauldronBlock> codec() {
		return CODEC;
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

		if (tag != null)
			level.getBlockEntity(pos).loadCustomOnly(tag, lookupProvider);
	}

	public interface IReinforcedCauldronInteraction extends CauldronInteraction {
		InteractionMap EMPTY = CauldronInteraction.newInteractionMap("reinforced_empty");
		InteractionMap WATER = CauldronInteraction.newInteractionMap("reinforced_water");
		InteractionMap LAVA = CauldronInteraction.newInteractionMap("reinforced_lava");
		InteractionMap POWDER_SNOW = CauldronInteraction.newInteractionMap("reinforced_powder_snow");
		CauldronInteraction FILL_WATER = (state, level, pos, player, hand, stack) -> emptyBucket(level, pos, player, hand, stack, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
		CauldronInteraction FILL_LAVA = (state, level, pos, player, hand, stack) -> emptyBucket(level, pos, player, hand, stack, SCContent.REINFORCED_LAVA_CAULDRON.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
		CauldronInteraction FILL_POWDER_SNOW = (state, level, pos, player, hand, stack) -> emptyBucket(level, pos, player, hand, stack, SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
		CauldronInteraction SHULKER_BOX = (state, level, pos, player, hand, stack) -> {
			Block block = Block.byItem(stack.getItem());

			if (!(block instanceof ShulkerBoxBlock))
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			else {
				if (!level.isClientSide) {
					player.setItemInHand(hand, stack.transmuteCopy(Blocks.SHULKER_BOX, 1));
					player.awardStat(Stats.CLEAN_SHULKER_BOX);
					ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
				}

				return InteractionResult.SUCCESS;
			}
		};
		CauldronInteraction BANNER = (state, level, pos, player, hand, stack) -> {
			BannerPatternLayers layers = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);

			if (layers.layers().isEmpty())
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			else {
				if (!level.isClientSide) {
					ItemStack stackCopy = stack.copyWithCount(1);

					stackCopy.set(DataComponents.BANNER_PATTERNS, layers.removeLast());
					stack.consume(1, player);

					if (stack.isEmpty())
						player.setItemInHand(hand, stackCopy);
					else if (player.getInventory().add(stackCopy))
						player.inventoryMenu.sendAllDataToRemote();
					else
						player.drop(stackCopy, false);

					player.awardStat(Stats.CLEAN_BANNER);
					ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
				}

				return InteractionResult.SUCCESS;
			}
		};
		CauldronInteraction DYED_ITEM = (state, level, pos, player, hand, stack) -> {
			if (!stack.is(ItemTags.DYEABLE))
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			else {
				if (!stack.has(DataComponents.DYED_COLOR))
					return InteractionResult.TRY_WITH_EMPTY_HAND;
				else {
					if (!level.isClientSide) {
						stack.remove(DataComponents.DYED_COLOR);
						player.awardStat(Stats.CLEAN_ARMOR);
						ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
					}

					return InteractionResult.SUCCESS;
				}
			}
		};

		static void bootStrap() {
			Map<Item, CauldronInteraction> emptyMap = EMPTY.map();
			Map<Item, CauldronInteraction> waterMap = WATER.map();
			Map<Item, CauldronInteraction> lavaMap = LAVA.map();
			Map<Item, CauldronInteraction> powderSnowMap = POWDER_SNOW.map();
			Map<Item, CauldronInteraction> vanillaWaterMap = CauldronInteraction.WATER.map();

			addDefaultInteractions(emptyMap);
			emptyMap.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
				PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);

				if (potionContents != null && potionContents.is(Potions.WATER)) {
					if (!level.isClientSide) {
						Item item = stack.getItem();

						player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.USE_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item));
						updateBlockState(level, pos, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState());
						level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
						level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
					}

					return InteractionResult.SUCCESS;
				}

				return InteractionResult.TRY_WITH_EMPTY_HAND;
			});
			addDefaultInteractions(waterMap);
			waterMap.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> fillBucket(state, level, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
			waterMap.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
				if (!level.isClientSide) {
					Item item = stack.getItem();

					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
					level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
				}

				return InteractionResult.SUCCESS;
			});
			waterMap.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
				if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) {
					PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);

					if (potionContents != null && potionContents.is(Potions.WATER)) {
						if (!level.isClientSide) {
							player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
							player.awardStat(Stats.USE_CAULDRON);
							player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
							updateBlockState(level, pos, state.cycle(LayeredCauldronBlock.LEVEL), null);
							level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
							level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
						}

						return InteractionResult.SUCCESS;
					}
				}

				return InteractionResult.TRY_WITH_EMPTY_HAND;
			});

			waterMap.put(Items.LEATHER_BOOTS, DYED_ITEM);
			waterMap.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
			waterMap.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
			waterMap.put(Items.LEATHER_HELMET, DYED_ITEM);
			waterMap.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
			waterMap.put(Items.WOLF_ARMOR, DYED_ITEM);
			waterMap.put(Items.WHITE_BANNER, BANNER);
			waterMap.put(Items.GRAY_BANNER, BANNER);
			waterMap.put(Items.BLACK_BANNER, BANNER);
			waterMap.put(Items.BLUE_BANNER, BANNER);
			waterMap.put(Items.BROWN_BANNER, BANNER);
			waterMap.put(Items.CYAN_BANNER, BANNER);
			waterMap.put(Items.GREEN_BANNER, BANNER);
			waterMap.put(Items.LIGHT_BLUE_BANNER, BANNER);
			waterMap.put(Items.LIGHT_GRAY_BANNER, BANNER);
			waterMap.put(Items.LIME_BANNER, BANNER);
			waterMap.put(Items.MAGENTA_BANNER, BANNER);
			waterMap.put(Items.ORANGE_BANNER, BANNER);
			waterMap.put(Items.PINK_BANNER, BANNER);
			waterMap.put(Items.PURPLE_BANNER, BANNER);
			waterMap.put(Items.RED_BANNER, BANNER);
			waterMap.put(Items.YELLOW_BANNER, BANNER);
			waterMap.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
			waterMap.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
			lavaMap.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> fillBucket(state, level, pos, player, hand, stack, new ItemStack(Items.LAVA_BUCKET), s -> true, SoundEvents.BUCKET_FILL_LAVA));
			addDefaultInteractions(lavaMap);
			powderSnowMap.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> fillBucket(state, level, pos, player, hand, stack, new ItemStack(Items.POWDER_SNOW_BUCKET), l -> l.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
			addDefaultInteractions(powderSnowMap);

			//add dyeable item interactions
			//TODO: AT
			vanillaWaterMap.put(SCContent.BRIEFCASE.get(), CauldronInteraction::dyedItemIteration);
			waterMap.put(SCContent.BRIEFCASE.get(), DYED_ITEM);
			vanillaWaterMap.put(SCContent.LENS.get(), CauldronInteraction::dyedItemIteration);
			waterMap.put(SCContent.LENS.get(), DYED_ITEM);
		}

		static void addDefaultInteractions(Map<Item, CauldronInteraction> interactions) {
			interactions.put(Items.LAVA_BUCKET, FILL_LAVA);
			interactions.put(Items.WATER_BUCKET, FILL_WATER);
			interactions.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
		}

		static InteractionResult fillBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack bucket, Predicate<BlockState> fillPredicate, SoundEvent sound) {
			if (!fillPredicate.test(state))
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			else {
				if (!level.isClientSide) {
					Item item = stack.getItem();

					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, bucket));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					updateBlockState(level, pos, SCContent.REINFORCED_CAULDRON.get().defaultBlockState());
					level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
				}

				return InteractionResult.SUCCESS;
			}
		}

		static InteractionResult emptyBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent sound) {
			if (!level.isClientSide) {
				Item item = stack.getItem();

				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				updateBlockState(level, pos, state);
				level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}

			return InteractionResult.SUCCESS;
		}
	}
}
