package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedCauldronTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedCauldronBlock extends AbstractCauldronBlock implements IReinforcedBlock, EntityBlock
{
	public ReinforcedCauldronBlock(Properties properties, Map<Item, CauldronInteraction> interactions)
	{
		super(properties, interactions);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext collisionContext)
	{
		if(collisionContext instanceof EntityCollisionContext ctx)
		{
			if(ctx.getEntity().isPresent())
			{
				Entity entity = ctx.getEntity().get();

				if(entity instanceof Player player)
				{
					BlockEntity tile = world.getBlockEntity(pos);

					if(tile instanceof ReinforcedCauldronTileEntity te && te.isAllowedToInteract(player))
						return SHAPE;
					else
						return Shapes.block();
				}
			}
		}

		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof ReinforcedCauldronTileEntity te && te.isAllowedToInteract(player))
			return super.use(state, world, pos, player, hand, hit);

		return InteractionResult.PASS;
	}

	@Override
	public boolean isFull(BlockState state) {
		return false;
	}

	protected static boolean shouldHandlePrecipitation(Level world, Precipitation precipitation) {
		if (precipitation == Precipitation.RAIN) {
			return world.getRandom().nextFloat() < 0.05F;
		} else if (precipitation == Precipitation.SNOW) {
			return world.getRandom().nextFloat() < 0.1F;
		} else {
			return false;
		}
	}

	@Override
	public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Precipitation precipitation) {
		if (shouldHandlePrecipitation(world, precipitation)) {
			BlockEntity te = world.getBlockEntity(pos);

			if (precipitation == Precipitation.RAIN) {
				world.setBlockAndUpdate(pos, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState());
				world.setBlockEntity(te);
				world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
			} else if (precipitation == Precipitation.SNOW) {
				world.setBlockAndUpdate(pos, SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get().defaultBlockState());
				world.setBlockEntity(te);
				world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
			}

		}
	}

	@Override
	protected boolean canReceiveStalactiteDrip(Fluid fluid) {
		return true;
	}

	@Override
	protected void receiveStalactiteDrip(BlockState state, Level world, BlockPos pos, Fluid fluid) {
		BlockEntity te = world.getBlockEntity(pos);

		if (fluid == Fluids.WATER) {
			world.setBlockAndUpdate(pos, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState());
			world.setBlockEntity(te);
			world.levelEvent(1047, pos, 0);
			world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
		} else if (fluid == Fluids.LAVA) {
			world.setBlockAndUpdate(pos, SCContent.REINFORCED_LAVA_CAULDRON.get().defaultBlockState());
			world.setBlockEntity(te);
			world.levelEvent(1046, pos, 0);
			world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
		}

	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.CAULDRON;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ReinforcedCauldronTileEntity(pos, state);
	}

	public interface IReinforcedCauldronInteraction extends CauldronInteraction {
		Map<Item, CauldronInteraction> EMPTY = CauldronInteraction.newInteractionMap();
		Map<Item, CauldronInteraction> WATER = CauldronInteraction.newInteractionMap();
		Map<Item, CauldronInteraction> LAVA = CauldronInteraction.newInteractionMap();
		Map<Item, CauldronInteraction> POWDER_SNOW = CauldronInteraction.newInteractionMap();
		CauldronInteraction FILL_WATER = (state, world, pos, player, hand, stack) -> emptyBucket(world, pos, player, hand, stack, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
		CauldronInteraction FILL_LAVA = (state, world, pos, player, hand, stack) -> emptyBucket(world, pos, player, hand, stack, SCContent.REINFORCED_LAVA_CAULDRON.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
		CauldronInteraction FILL_POWDER_SNOW = (state, world, pos, player, hand, stack) -> emptyBucket(world, pos, player, hand, stack, SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
		CauldronInteraction SHULKER_BOX = (state, world, pos, player, hand, stack) -> {
			Block block = Block.byItem(stack.getItem());
			if (!(block instanceof ShulkerBoxBlock)) {
				return InteractionResult.PASS;
			} else {
				if (!world.isClientSide) {
					ItemStack shulker = new ItemStack(Blocks.SHULKER_BOX);
					if (stack.hasTag()) {
						shulker.setTag(stack.getTag().copy());
					}

					player.setItemInHand(hand, shulker);
					player.awardStat(Stats.CLEAN_SHULKER_BOX);
					ReinforcedLayeredCauldronBlock.lowerFillLevel(state, world, pos);
				}

				return InteractionResult.sidedSuccess(world.isClientSide);
			}
		};
		CauldronInteraction BANNER = (state, world, pos, player, hand, stack) -> {
			if (BannerBlockEntity.getPatternCount(stack) <= 0) {
				return InteractionResult.PASS;
			} else {
				if (!world.isClientSide) {
					ItemStack banner = stack.copy();
					banner.setCount(1);
					BannerBlockEntity.removeLastPattern(banner);
					if (!player.getAbilities().instabuild) {
						stack.shrink(1);
					}

					if (stack.isEmpty()) {
						player.setItemInHand(hand, banner);
					} else if (player.getInventory().add(banner)) {
						player.inventoryMenu.sendAllDataToRemote();
					} else {
						player.drop(banner, false);
					}

					player.awardStat(Stats.CLEAN_BANNER);
					ReinforcedLayeredCauldronBlock.lowerFillLevel(state, world, pos);
				}

				return InteractionResult.sidedSuccess(world.isClientSide);
			}
		};
		CauldronInteraction DYED_ITEM = (state, world, pos, player, hand, stack) -> {
			Item item = stack.getItem();
			if (!(item instanceof DyeableLeatherItem leatherItem)) {
				return InteractionResult.PASS;
			} else {
				if (!leatherItem.hasCustomColor(stack)) {
					return InteractionResult.PASS;
				} else {
					if (!world.isClientSide) {
						leatherItem.clearColor(stack);
						player.awardStat(Stats.CLEAN_ARMOR);
						ReinforcedLayeredCauldronBlock.lowerFillLevel(state, world, pos);
					}

					return InteractionResult.sidedSuccess(world.isClientSide);
				}
			}
		};

		static void bootStrap() {
			addDefaultInteractions(EMPTY);
			EMPTY.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
				if (PotionUtils.getPotion(stack) != Potions.WATER) {
					return InteractionResult.PASS;
				} else {
					if (!world.isClientSide) {
						Item item = stack.getItem();
						BlockEntity te = world.getBlockEntity(pos);

						player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.USE_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item));
						world.setBlockAndUpdate(pos, SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState());
						world.setBlockEntity(te);
						world.playSound((Player)null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
						world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
					}

					return InteractionResult.sidedSuccess(world.isClientSide);
				}
			});
			addDefaultInteractions(WATER);
			WATER.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> fillBucket(state, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), (s) -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
			WATER.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
				if (!world.isClientSide) {
					Item item = stack.getItem();

					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					ReinforcedLayeredCauldronBlock.lowerFillLevel(state, world, pos);
					world.playSound((Player)null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
					world.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
				}

				return InteractionResult.sidedSuccess(world.isClientSide);
			});
			WATER.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
				if (state.getValue(LayeredCauldronBlock.LEVEL) != 3 && PotionUtils.getPotion(stack) == Potions.WATER) {
					if (!world.isClientSide) {
						player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.USE_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
						world.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
						world.playSound((Player)null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
						world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
					}

					return InteractionResult.sidedSuccess(world.isClientSide);
				} else {
					return InteractionResult.PASS;
				}
			});
			WATER.put(Items.LEATHER_BOOTS, DYED_ITEM);
			WATER.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
			WATER.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
			WATER.put(Items.LEATHER_HELMET, DYED_ITEM);
			WATER.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
			WATER.put(Items.WHITE_BANNER, BANNER);
			WATER.put(Items.GRAY_BANNER, BANNER);
			WATER.put(Items.BLACK_BANNER, BANNER);
			WATER.put(Items.BLUE_BANNER, BANNER);
			WATER.put(Items.BROWN_BANNER, BANNER);
			WATER.put(Items.CYAN_BANNER, BANNER);
			WATER.put(Items.GREEN_BANNER, BANNER);
			WATER.put(Items.LIGHT_BLUE_BANNER, BANNER);
			WATER.put(Items.LIGHT_GRAY_BANNER, BANNER);
			WATER.put(Items.LIME_BANNER, BANNER);
			WATER.put(Items.MAGENTA_BANNER, BANNER);
			WATER.put(Items.ORANGE_BANNER, BANNER);
			WATER.put(Items.PINK_BANNER, BANNER);
			WATER.put(Items.PURPLE_BANNER, BANNER);
			WATER.put(Items.RED_BANNER, BANNER);
			WATER.put(Items.YELLOW_BANNER, BANNER);
			WATER.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
			WATER.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
			LAVA.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> fillBucket(state, world, pos, player, hand, stack, new ItemStack(Items.LAVA_BUCKET), s -> true, SoundEvents.BUCKET_FILL_LAVA));
			addDefaultInteractions(LAVA);
			POWDER_SNOW.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> fillBucket(state, world, pos, player, hand, stack, new ItemStack(Items.POWDER_SNOW_BUCKET), (level) -> level.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
			addDefaultInteractions(POWDER_SNOW);

			//add briefcase interactions
			CauldronInteraction.WATER.put(SCContent.BRIEFCASE.get(), CauldronInteraction.DYED_ITEM);
			WATER.put(SCContent.BRIEFCASE.get(), DYED_ITEM);
		}

		static void addDefaultInteractions(Map<Item, CauldronInteraction> interactions) {
			interactions.put(Items.LAVA_BUCKET, FILL_LAVA);
			interactions.put(Items.WATER_BUCKET, FILL_WATER);
			interactions.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
		}

		static InteractionResult fillBucket(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, ItemStack bucket, Predicate<BlockState> fillPredicate, SoundEvent sound) {
			if (!fillPredicate.test(state)) {
				return InteractionResult.PASS;
			} else {
				if (!world.isClientSide) {
					Item item = stack.getItem();
					BlockEntity te = world.getBlockEntity(pos);

					player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, bucket));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					world.setBlockAndUpdate(pos, SCContent.REINFORCED_CAULDRON.get().defaultBlockState());
					world.setBlockEntity(te);
					world.playSound((Player)null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
					world.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
				}

				return InteractionResult.sidedSuccess(world.isClientSide);
			}
		}

		static InteractionResult emptyBucket(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent sound) {
			if (!world.isClientSide) {
				Item item = stack.getItem();
				BlockEntity te = world.getBlockEntity(pos);

				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				world.setBlockAndUpdate(pos, state);
				world.setBlockEntity(te);
				world.playSound((Player)null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
				world.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
			}

			return InteractionResult.sidedSuccess(world.isClientSide);
		}
	}
}
