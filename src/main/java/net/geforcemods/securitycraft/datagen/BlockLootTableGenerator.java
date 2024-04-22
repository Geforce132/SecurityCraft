package net.geforcemods.securitycraft.datagen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.misc.BlockEntityNBTCondition;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockLootTableGenerator implements LootTableSubProvider {
	protected final Map<Supplier<? extends Block>, LootTable.Builder> lootTables = new HashMap<>();

	@Override
	public void generate(HolderLookup.Provider lookupProvider, BiConsumer<ResourceKey<LootTable>, Builder> consumer) {
		for (DeferredHolder<Block, ? extends Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();

			if (block instanceof ReinforcedSlabBlock)
				putSlabLootTable(obj);
			else if (block instanceof IExplosive)
				putStandardBlockLootTable(obj);
			else if (block.asItem() != Items.AIR) {
				if (block instanceof EntityBlock entityBlock && entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()) instanceof Nameable) {
					//@formatter:off
					lootTables.put(obj, LootTable.lootTable()
							.withPool(LootPool.lootPool()
									.setRolls(ConstantValue.exactly(1))
									.add(LootItem.lootTableItem(obj.get())
											.apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)))
									.when(ExplosionCondition.survivesExplosion())));
					//@formatter:on
				}
				else
					putStandardBlockLootTable(obj);
			}
		}

		lootTables.remove(SCContent.REINFORCED_PISTON_HEAD);
		putStandardBlockLootTable(SCContent.ANCIENT_DEBRIS_MINE);
		putSlabLootTable(SCContent.CRYSTAL_QUARTZ_SLAB);
		putSlabLootTable(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB);

		LootPoolSingletonContainer.Builder<?> imsLootEntryBuilder = LootItem.lootTableItem(SCContent.BOUNCING_BETTY.get());

		for (int i = 0; i <= 4; i++) {
			if (i == 1)
				continue;

			//@formatter:off
			imsLootEntryBuilder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(i))
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SCContent.IMS.get())
							.setProperties(StatePropertiesPredicate.Builder.properties()
									.hasProperty(IMSBlock.MINES, i))));
		}

		lootTables.put(SCContent.IMS, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(imsLootEntryBuilder)));
		lootTables.put(SCContent.KEY_PANEL_BLOCK, createStandardBlockLootTable(SCContent.KEY_PANEL.get()).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)));
		putTwoHighBlockLootTable(SCContent.KEYPAD_DOOR, SCContent.KEYPAD_DOOR_ITEM);
		putTwoHighBlockLootTable(SCContent.REINFORCED_DOOR, SCContent.REINFORCED_DOOR_ITEM);
		lootTables.put(SCContent.REINFORCED_IRON_BARS,
				LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(SCContent.REINFORCED_IRON_BARS.get())
								.when(BlockEntityNBTCondition.nbt("canDrop", true)))
						.when(ExplosionCondition.survivesExplosion())));
		lootTables.put(SCContent.REINFORCED_LAVA_CAULDRON, createStandardBlockLootTable(SCContent.REINFORCED_CAULDRON.get()).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)));
		lootTables.put(SCContent.REINFORCED_POWDER_SNOW_CAULDRON, createStandardBlockLootTable(SCContent.REINFORCED_CAULDRON.get()).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)));
		lootTables.put(SCContent.REINFORCED_WATER_CAULDRON, createStandardBlockLootTable(SCContent.REINFORCED_CAULDRON.get()).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)));
		putTwoHighBlockLootTable(SCContent.RIFT_STABILIZER, SCContent.RIFT_STABILIZER_ITEM);
		putTwoHighBlockLootTable(SCContent.SCANNER_DOOR, SCContent.SCANNER_DOOR_ITEM);
		putStandardBlockLootTable(SCContent.SECRET_ACACIA_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_ACACIA_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BAMBOO_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BAMBOO_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BIRCH_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BIRCH_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_CRIMSON_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_CRIMSON_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_DARK_OAK_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_DARK_OAK_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_JUNGLE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_JUNGLE_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_MANGROVE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_MANGROVE_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_OAK_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_OAK_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_SPRUCE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_SPRUCE_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_WARPED_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_WARPED_WALL_SIGN);
		putStandardBlockLootTable(SCContent.CRYSTAL_QUARTZ_STAIRS);
		putStandardBlockLootTable(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS);
		lootTables.put(SCContent.SONIC_SECURITY_SYSTEM, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get())
								.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
										.include(DataComponents.CUSTOM_DATA)) //TODO: Check if this works
								.apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)))));
		//@formatter:on

		lootTables.forEach((block, lootTable) -> consumer.accept(block.get().getLootTable(), lootTable.setParamSet(LootContextParamSets.BLOCK)));
	}

	protected final LootTable.Builder createStandardBlockLootTable(Supplier<? extends Block> drop) {
		return createStandardBlockLootTable(drop.get());
	}

	protected final LootTable.Builder createStandardBlockLootTable(ItemLike drop) {
		//@formatter:off
		return LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(drop))
						.when(ExplosionCondition.survivesExplosion()));
		//@formatter:on
	}

	protected final void putTwoHighBlockLootTable(Supplier<? extends Block> door, Supplier<? extends Item> doorItem) {
		lootTables.put(door, createTwoHighBlockLootTable(door, doorItem));
	}

	protected final Builder createTwoHighBlockLootTable(Supplier<? extends Block> door, Supplier<? extends Item> doorItem) {
		//@formatter:off
		return LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(doorItem.get())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(door.get())
										.setProperties(StatePropertiesPredicate.Builder.properties()
												.hasProperty(ReinforcedDoorBlock.HALF, DoubleBlockHalf.LOWER)))
								.when(ExplosionCondition.survivesExplosion())
								.apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY))));
		//@formatter:on
	}

	protected final void putStandardBlockLootTable(Supplier<? extends Block> block) {
		putStandardBlockLootTable(block, block.get());
	}

	protected final void putStandardBlockLootTable(Supplier<? extends Block> block, ItemLike drop) {
		lootTables.put(block, createStandardBlockLootTable(drop));
	}

	protected final void putSlabLootTable(Supplier<? extends Block> slab) {
		//@formatter:off
		lootTables.put(slab, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(slab.get())
								.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))
										.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(slab.get())
												.setProperties(StatePropertiesPredicate.Builder.properties()
														.hasProperty(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE))))
								.apply(ApplyExplosionDecay.explosionDecay()))));
		//@formatter:on
	}
}
