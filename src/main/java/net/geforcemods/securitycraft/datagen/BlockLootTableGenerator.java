package net.geforcemods.securitycraft.datagen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.misc.conditions.TileEntityNBTCondition;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.fmllegacy.RegistryObject;
import sun.jvm.hotspot.code.ConstantIntValue;

public class BlockLootTableGenerator implements DataProvider
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	protected final Map<Supplier<Block>,LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	public BlockLootTableGenerator(DataGenerator generator)
	{
		this.generator = generator;
	}

	private void addTables()
	{
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
				{
					RegistryObject<Block> obj = ((RegistryObject<Block>)field.get(null));

					if(obj.get() instanceof ReinforcedSlabBlock)
						putSlabLootTable(obj);
					else
						putStandardBlockLootTable(obj);
				}
				else if(field.isAnnotationPresent(RegisterItemBlock.class))
				{
					RegistryObject<Block> obj = ((RegistryObject<Block>)field.get(null));

					if(obj.get() instanceof IExplosive)
						putMineLootTable(obj);
					else
						putStandardBlockLootTable(obj);
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		putMineLootTable(SCContent.ANCIENT_DEBRIS_MINE);
		putSlabLootTable(SCContent.CRYSTAL_QUARTZ_SLAB);

		LootPoolSingletonContainer.Builder<?> imsLootEntryBuilder = LootItem.lootTableItem(SCContent.BOUNCING_BETTY.get());

		for(int i = 0; i <= 4; i++)
		{
			if(i == 1) //default
				continue;

			imsLootEntryBuilder.apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(i))
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(SCContent.IMS.get())
							.setProperties(StatePropertiesPredicate.Builder.properties()
									.hasProperty(IMSBlock.MINES, i))));
		}

		lootTables.put(SCContent.IMS, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantIntValue.exactly(1))
						.add(imsLootEntryBuilder)));
		putStandardBlockLootTable(SCContent.KEYPAD_CHEST);
		putDoorLootTable(SCContent.KEYPAD_DOOR, SCContent.KEYPAD_DOOR_ITEM);
		putDoorLootTable(SCContent.REINFORCED_DOOR, SCContent.REINFORCED_DOOR_ITEM);
		lootTables.put(SCContent.REINFORCED_IRON_BARS,
				LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantIntValue.exactly(1))
						.add(LootItem.lootTableItem(SCContent.REINFORCED_IRON_BARS.get())
								.when(TileEntityNBTCondition.builder().equals("canDrop", true)))
						.when(ExplosionCondition.survivesExplosion())));
		putDoorLootTable(SCContent.SCANNER_DOOR, SCContent.SCANNER_DOOR_ITEM);
		putStandardBlockLootTable(SCContent.SECRET_ACACIA_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_ACACIA_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BIRCH_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BIRCH_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_CRIMSON_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_CRIMSON_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_DARK_OAK_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_DARK_OAK_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_JUNGLE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_JUNGLE_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_OAK_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_OAK_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_SPRUCE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_SPRUCE_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_WARPED_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_WARPED_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECURITY_CAMERA);
		putStandardBlockLootTable(SCContent.STAIRS_CRYSTAL_QUARTZ);
	}

	protected final LootTable.Builder createStandardBlockLootTable(Supplier<Block> block)
	{
		return LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantIntValue.exactly(1))
						.add(LootItem.lootTableItem(block.get()))
						.when(ExplosionCondition.survivesExplosion()));
	}

	protected final void putDoorLootTable(Supplier<Block> door, Supplier<Item> doorItem)
	{
		lootTables.put(door, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantIntValue.exactly(1))
						.add(LootItem.lootTableItem(doorItem.get())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(door.get())
										.setProperties(StatePropertiesPredicate.Builder.properties()
												.hasProperty(ReinforcedDoorBlock.HALF, DoubleBlockHalf.LOWER)))
								.when(ExplosionCondition.survivesExplosion()))));
	}

	protected final void putStandardBlockLootTable(Supplier<Block> block)
	{
		lootTables.put(block, createStandardBlockLootTable(block));
	}

	protected final void putMineLootTable(Supplier<Block> mine)
	{
		lootTables.put(mine, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantIntValue.exactly(1))
						.add(LootItem.lootTableItem(mine.get()))
						.when(ExplosionCondition.survivesExplosion())
						.when(InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.entityPresent(EntityTarget.THIS)))));
	}

	protected final void putSlabLootTable(Supplier<Block> slab)
	{
		lootTables.put(slab, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ConstantIntValue.exactly(1))
						.add(LootItem.lootTableItem(slab.get())
								.apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2))
										.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(slab.get())
												.setProperties(StatePropertiesPredicate.Builder.properties()
														.hasProperty(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE))))
								.apply(ApplyExplosionDecay.explosionDecay()))));
	}

	@Override
	public void run(HashCache cache) throws IOException
	{
		Map<ResourceLocation,LootTable> tables = new HashMap<>();

		addTables();

		for(Map.Entry<Supplier<Block>,LootTable.Builder> entry : lootTables.entrySet())
		{
			tables.put(entry.getKey().get().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
		}

		tables.forEach((key, lootTable) -> {
			try
			{
				DataProvider.save(GSON, cache, LootTables.serialize(lootTable), generator.getOutputFolder().resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		});
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block Loot Tables";
	}
}
