package net.geforcemods.securitycraft.misc;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLayeredCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCauldronInteractionEvent;
import net.neoforged.neoforge.event.RegisterCauldronInteractionEvent.Dispatcher;

@EventBusSubscriber
public class ReinforcedCauldronInteractions {
	public static final IdentifiableDispatcher EMPTY = new IdentifiableDispatcher("reinforced_empty");
	public static final IdentifiableDispatcher WATER = new IdentifiableDispatcher("reinforced_water");
	public static final IdentifiableDispatcher LAVA = new IdentifiableDispatcher("reinforced_lava");
	public static final IdentifiableDispatcher POWDER_SNOW = new IdentifiableDispatcher("reinforced_powder_snow");

	@SubscribeEvent
	public static void onRegisterCauldronInteractionDispatchers(RegisterCauldronInteractionEvent.Dispatcher event) {
		register(event, EMPTY);
		register(event, WATER);
		register(event, LAVA);
		register(event, POWDER_SNOW);
	}

	private static void register(Dispatcher event, IdentifiableDispatcher dispatcher) {
		event.register(dispatcher.id(), dispatcher);
	}

	@SubscribeEvent
	public static void onRegisterCauldronInteractionInteractions(RegisterCauldronInteractionEvent.Interaction event) {
		Identifier empty = ReinforcedCauldronInteractions.EMPTY.id();
		Identifier water = ReinforcedCauldronInteractions.WATER.id();
		Identifier lava = ReinforcedCauldronInteractions.LAVA.id();
		Identifier powderSnow = ReinforcedCauldronInteractions.POWDER_SNOW.id();

		addDefaultInteractions(event, empty);
		event.register(empty, Items.POTION, (_, level, pos, player, hand, stack) -> {
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
		addDefaultInteractions(event, water);
		event.register(water, Items.BUCKET, (state, level, pos, player, hand, stack) -> fillBucket(state, level, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
		event.register(water, Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
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
		event.register(water, Items.POTION, (state, level, pos, player, hand, stack) -> {
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
		event.register(water, ItemTags.CAULDRON_CAN_REMOVE_DYE, ReinforcedCauldronInteractions::dyedItemInteraction);
		event.register(water, Items.WHITE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.GRAY_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.BLACK_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.BLUE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.BROWN_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.CYAN_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.GREEN_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.LIGHT_BLUE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.LIGHT_GRAY_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.LIME_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.MAGENTA_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.ORANGE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.PINK_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.PURPLE_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.RED_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.YELLOW_BANNER, ReinforcedCauldronInteractions::bannerInteraction);
		event.register(water, Items.WHITE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.GRAY_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.BLACK_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.BLUE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.BROWN_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.CYAN_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.GREEN_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.LIGHT_BLUE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.LIGHT_GRAY_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.LIME_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.MAGENTA_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.ORANGE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.PINK_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.PURPLE_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.RED_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(water, Items.YELLOW_SHULKER_BOX, ReinforcedCauldronInteractions::shulkerBoxInteraction);
		event.register(lava, Items.BUCKET, (state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.LAVA_BUCKET), _ -> true, SoundEvents.BUCKET_FILL_LAVA));
		addDefaultInteractions(event, lava);
		event.register(powderSnow, Items.BUCKET, (state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.POWDER_SNOW_BUCKET), s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
		addDefaultInteractions(event, powderSnow);
	}

	static void addDefaultInteractions(RegisterCauldronInteractionEvent.Interaction event, Identifier dispatcher) {
		event.register(dispatcher, Items.LAVA_BUCKET, ReinforcedCauldronInteractions::fillLavaInteraction);
		event.register(dispatcher, Items.WATER_BUCKET, ReinforcedCauldronInteractions::fillWaterInteraction);
		event.register(dispatcher, Items.POWDER_SNOW_BUCKET, ReinforcedCauldronInteractions::fillPowderSnowInteraction);
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

	public static class IdentifiableDispatcher extends CauldronInteraction.Dispatcher {
		private final Identifier id;

		public IdentifiableDispatcher(String path) {
			super();
			this.id = SecurityCraft.resLoc(path);
		}

		public Identifier id() {
			return id;
		}
	}
}
