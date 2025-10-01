package net.geforcemods.securitycraft.datagen;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.misc.BlockEntityNBTCondition;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockLootTableGenerator extends BlockLootSubProvider {
	public BlockLootTableGenerator(HolderLookup.Provider lookupProvider) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
	}

	@Override
	public void generate() {
		for (DeferredHolder<Block, ? extends Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();

			if (block instanceof ReinforcedSlabBlock)
				add(block, this::createSlabItemTable);
			else if (block instanceof IExplosive)
				dropSelf(block);
			else if (block.asItem() != Items.AIR) {
				if (block instanceof EntityBlock entityBlock && entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()) instanceof Nameable)
					add(block, this::createNameableBlockEntityTable);
				else
					dropSelf(block);
			}
		}

		add(SCContent.CRYSTAL_QUARTZ_SLAB, this::createSlabItemTable);
		add(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB, this::createSlabItemTable);

		LootPoolSingletonContainer.Builder<?> imsLootEntryBuilder = LootItem.lootTableItem(SCContent.BOUNCING_BETTY);

		for (int i = 0; i <= 4; i++) {
			if (i == 1)
				continue;

			//@formatter:off
			imsLootEntryBuilder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(i))
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SCContent.IMS.get())
							.setProperties(StatePropertiesPredicate.Builder.properties()
									.hasProperty(IMSBlock.MINES, i))));
		}

		add(SCContent.IMS, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(imsLootEntryBuilder)));
		//@formatter:on
		add(SCContent.KEY_PANEL_BLOCK, this::createNameableBlockEntityTable);
		add(SCContent.KEYPAD_DOOR, this::createTwoHighBlockLootTable);
		add(SCContent.REINFORCED_DOOR, this::createTwoHighBlockLootTable);
		//@formatter:off
		add(SCContent.REINFORCED_IRON_BARS,
				LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(SCContent.REINFORCED_IRON_BARS)
								.when(BlockEntityNBTCondition.nbt("canDrop", true)))
						.when(ExplosionCondition.survivesExplosion())));
		//@formatter:on
		add(SCContent.REINFORCED_LAVA_CAULDRON, createNameableBlockEntityTable(SCContent.REINFORCED_CAULDRON.get()));
		add(SCContent.REINFORCED_PALE_MOSS_CARPET, createMossyCarpetBlockDrops(SCContent.REINFORCED_PALE_MOSS_CARPET.get()));
		add(SCContent.REINFORCED_POWDER_SNOW_CAULDRON, createNameableBlockEntityTable(SCContent.REINFORCED_CAULDRON.get()));
		add(SCContent.REINFORCED_WATER_CAULDRON, createNameableBlockEntityTable(SCContent.REINFORCED_CAULDRON.get()));
		add(SCContent.RIFT_STABILIZER, this::createTwoHighBlockLootTable);
		add(SCContent.SCANNER_DOOR, this::createTwoHighBlockLootTable);
		dropSelf(SCContent.SECRET_ACACIA_SIGN);
		dropSelf(SCContent.SECRET_ACACIA_WALL_SIGN);
		dropSelf(SCContent.SECRET_BAMBOO_SIGN);
		dropSelf(SCContent.SECRET_BAMBOO_WALL_SIGN);
		dropSelf(SCContent.SECRET_BIRCH_SIGN);
		dropSelf(SCContent.SECRET_BIRCH_WALL_SIGN);
		dropSelf(SCContent.SECRET_CRIMSON_SIGN);
		dropSelf(SCContent.SECRET_CRIMSON_WALL_SIGN);
		dropSelf(SCContent.SECRET_DARK_OAK_SIGN);
		dropSelf(SCContent.SECRET_DARK_OAK_WALL_SIGN);
		dropSelf(SCContent.SECRET_JUNGLE_SIGN);
		dropSelf(SCContent.SECRET_JUNGLE_WALL_SIGN);
		dropSelf(SCContent.SECRET_MANGROVE_SIGN);
		dropSelf(SCContent.SECRET_MANGROVE_WALL_SIGN);
		dropSelf(SCContent.SECRET_OAK_SIGN);
		dropSelf(SCContent.SECRET_OAK_WALL_SIGN);
		dropSelf(SCContent.SECRET_PALE_OAK_SIGN);
		dropSelf(SCContent.SECRET_PALE_OAK_WALL_SIGN);
		dropSelf(SCContent.SECRET_SPRUCE_SIGN);
		dropSelf(SCContent.SECRET_SPRUCE_WALL_SIGN);
		dropSelf(SCContent.SECRET_WARPED_SIGN);
		dropSelf(SCContent.SECRET_WARPED_WALL_SIGN);
		dropSelf(SCContent.CRYSTAL_QUARTZ_STAIRS);
		dropSelf(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS);
		//@formatter:off
		add(SCContent.SONIC_SECURITY_SYSTEM, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(SCContent.SONIC_SECURITY_SYSTEM_ITEM)
								.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
										.include(SCContent.SSS_LINKED_BLOCKS.get())
										.include(SCContent.NOTES.get())
										.include(DataComponents.CUSTOM_NAME)))));
		//@formatter:on
	}

	protected final LootTable.Builder createTwoHighBlockLootTable(Block twoHighBlock) {
		//@formatter:off
		return createSinglePropConditionTable(twoHighBlock, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
				.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
						.include(DataComponents.CUSTOM_NAME));
		//@formatter:on
	}

	protected void dropSelf(Supplier<? extends Block> block) {
		dropSelf(block.get());
	}

	protected void add(Supplier<? extends Block> block, Function<Block, Builder> factory) {
		add(block.get(), factory);
	}

	protected void add(Supplier<? extends Block> block, LootTable.Builder builder) {
		add(block.get(), builder);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return (Iterable<Block>) SCContent.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList();
	}
}
