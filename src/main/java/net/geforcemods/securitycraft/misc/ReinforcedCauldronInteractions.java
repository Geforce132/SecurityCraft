package net.geforcemods.securitycraft.misc;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLayeredCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ReinforcedCauldronInteractions {
	public static final CauldronInteraction.Dispatcher EMPTY = CauldronInteractions.newDispatcher("reinforced_empty");
	public static final CauldronInteraction.Dispatcher WATER = CauldronInteractions.newDispatcher("reinforced_water");
	public static final CauldronInteraction.Dispatcher LAVA = CauldronInteractions.newDispatcher("reinforced_lava");
	public static final CauldronInteraction.Dispatcher POWDER_SNOW = CauldronInteractions.newDispatcher("reinforced_powder_snow");

	public static void bootstrap() {
		addDefaultInteractions(EMPTY);
		EMPTY.put(Items.POTION, (_, level, pos, player, hand, stack) -> {
			PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);

			if (potionContents != null && potionContents.is(Potions.WATER)) {
				if (!level.isClientSide()) {
					Item item = stack.getItem();

					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					ReinforcedCauldronBlock.updateBlockState(level, pos, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState());
					level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
				}

				return InteractionResult.SUCCESS;
			}
			else
				return InteractionResult.TRY_WITH_EMPTY_HAND;
		});
		addDefaultInteractions(WATER);
		WATER.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> fillBucket(state, level, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
		WATER.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) {
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
		WATER.put(Items.POTION, (state, level, pos, player, hand, stack) -> {
			if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) {
				PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);

				if (potionContents != null && potionContents.is(Potions.WATER)) {
					if (!level.isClientSide()) {
						player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.USE_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
						ReinforcedCauldronBlock.updateBlockState(level, pos, state.cycle(LayeredCauldronBlock.LEVEL), null);
						level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
						level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
					}

					return InteractionResult.SUCCESS;
				}
			}

			return InteractionResult.TRY_WITH_EMPTY_HAND;
		});
		WATER.put(ItemTags.CAULDRON_CAN_REMOVE_DYE, ReinforcedCauldronInteractions::dyedItemInteraction);
		WATER.put(Items.WHITE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.GRAY_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.BLACK_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.BLUE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.BROWN_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.CYAN_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.GREEN_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.LIGHT_BLUE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.LIGHT_GRAY_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.LIME_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.MAGENTA_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.ORANGE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.PINK_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.PURPLE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.RED_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.YELLOW_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		WATER.put(Items.WHITE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.GRAY_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.BLACK_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.BLUE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.BROWN_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.CYAN_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.GREEN_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.LIME_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.MAGENTA_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.ORANGE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.PINK_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.PURPLE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.RED_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		WATER.put(Items.YELLOW_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		LAVA.put(Items.BUCKET, (state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.LAVA_BUCKET), _ -> true, SoundEvents.BUCKET_FILL_LAVA));
		addDefaultInteractions(LAVA);
		POWDER_SNOW.put(Items.BUCKET, (state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.POWDER_SNOW_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
		addDefaultInteractions(POWDER_SNOW);
	}

	static void addDefaultInteractions(CauldronInteraction.Dispatcher dispatcher) {
		dispatcher.put(Items.LAVA_BUCKET, ReinforcedCauldronInteractions::fillLavaInteraction);
		dispatcher.put(Items.WATER_BUCKET, ReinforcedCauldronInteractions::fillWaterInteraction);
		dispatcher.put(Items.POWDER_SNOW_BUCKET, ReinforcedCauldronInteractions::fillPowderSnowInteraction);
	}

	static InteractionResult fillBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack bucket, Predicate<BlockState> fillPredicate, SoundEvent sound) {
		if (!fillPredicate.test(state))
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		else {
			if (!level.isClientSide()) {
				Item item = stack.getItem();

				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, bucket));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				ReinforcedCauldronBlock.updateBlockState(level, pos, SCContent.REINFORCED_CAULDRON.get().defaultBlockState());
				level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
			}

			return InteractionResult.SUCCESS;
		}
	}

	static InteractionResult emptyBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent sound) {
		if (!level.isClientSide()) {
			Item item = stack.getItem();

			player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
			player.awardStat(Stats.FILL_CAULDRON);
			player.awardStat(Stats.ITEM_USED.get(item));
			ReinforcedCauldronBlock.updateBlockState(level, pos, state);
			level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
		}

		return InteractionResult.SUCCESS;
	}

	private static InteractionResult fillWaterInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		return emptyBucket(level, pos, player, hand, stack, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
	}

	private static InteractionResult fillLavaInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		if (CauldronInteractions.isUnderWater(level, pos))
			return InteractionResult.CONSUME;
		else
			return emptyBucket(level, pos, player, hand, stack, SCContent.REINFORCED_LAVA_CAULDRON.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
	}

	private static InteractionResult fillPowderSnowInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		if (CauldronInteractions.isUnderWater(level, pos))
			return InteractionResult.CONSUME;
		else
			return emptyBucket(level, pos, player, hand, stack, SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
	}

	private static InteractionResult shulkerBoxInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		Block block = Block.byItem(stack.getItem());

		if (!(block instanceof ShulkerBoxBlock))
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		else {
			if (!level.isClientSide()) {
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, stack.transmuteCopy(Blocks.SHULKER_BOX, 1), false));
				player.awardStat(Stats.CLEAN_SHULKER_BOX);
				ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}

			return InteractionResult.SUCCESS;
		}
	}

	private static InteractionResult bannerInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
		BannerPatternLayers layers = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);

		if (layers.layers().isEmpty())
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		else {
			if (!level.isClientSide()) {
				ItemStack stackCopy = stack.copyWithCount(1);

				stackCopy.set(DataComponents.BANNER_PATTERNS, layers.removeLast());
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, stackCopy, false));
				player.awardStat(Stats.CLEAN_BANNER);
				ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}

			return InteractionResult.SUCCESS;
		}
	}

	public static InteractionResult dyedItemInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack itemInHand) {
		if (!itemInHand.has(DataComponents.DYED_COLOR))
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		else {
			if (!level.isClientSide()) {
				itemInHand.remove(DataComponents.DYED_COLOR);
				player.awardStat(Stats.CLEAN_ARMOR);
				ReinforcedLayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}

			return InteractionResult.SUCCESS;
		}
	}
}
