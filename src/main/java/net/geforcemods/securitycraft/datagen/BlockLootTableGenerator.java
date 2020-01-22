package net.geforcemods.securitycraft.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;

public class BlockLootTableGenerator implements IDataProvider
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	protected final Map<Block,LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	public BlockLootTableGenerator(DataGenerator generator)
	{
		this.generator = generator;
	}

	private void addTables()
	{
		putStandardBlockLootTable(SCContent.alarm);
		putStandardBlockLootTable(SCContent.bouncingBetty);
		putStandardBlockLootTable(SCContent.cageTrap);
		putStandardBlockLootTable(SCContent.claymore);
		putStandardBlockLootTable(SCContent.cobblestoneMine);
		putStandardBlockLootTable(SCContent.diamondOreMine);
		putStandardBlockLootTable(SCContent.dirtMine);
		putStandardBlockLootTable(SCContent.ironFence);
		putStandardBlockLootTable(SCContent.furnaceMine);
		putStandardBlockLootTable(SCContent.gravelMine);
		putStandardBlockLootTable(SCContent.inventoryScanner);
		putDoorLootTable(SCContent.reinforcedDoor, SCContent.reinforcedDoorItem);
		putStandardBlockLootTable(SCContent.keycardReader);
		putStandardBlockLootTable(SCContent.keypadChest);
		putStandardBlockLootTable(SCContent.frame);
		putStandardBlockLootTable(SCContent.keypadFurnace);
		putStandardBlockLootTable(SCContent.keypad);
		putStandardBlockLootTable(SCContent.laserBlock);
		putStandardBlockLootTable(SCContent.mine);
		putStandardBlockLootTable(SCContent.motionActivatedLight);
		putStandardBlockLootTable(SCContent.panicButton);
		putStandardBlockLootTable(SCContent.portableRadar);
		putStandardBlockLootTable(SCContent.protecto);
		putStandardBlockLootTable(SCContent.reinforcedFencegate);
		putStandardBlockLootTable(SCContent.reinforcedIronTrapdoor);
		putStandardBlockLootTable(SCContent.retinalScanner);
		putStandardBlockLootTable(SCContent.sandMine);
		putDoorLootTable(SCContent.scannerDoor, SCContent.scannerDoorItem);
		putStandardBlockLootTable(SCContent.secretAcaciaSign);
		putStandardBlockLootTable(SCContent.secretAcaciaWallSign);
		putStandardBlockLootTable(SCContent.secretBirchSign);
		putStandardBlockLootTable(SCContent.secretBirchWallSign);
		putStandardBlockLootTable(SCContent.secretDarkOakSign);
		putStandardBlockLootTable(SCContent.secretDarkOakWallSign);
		putStandardBlockLootTable(SCContent.secretJungleSign);
		putStandardBlockLootTable(SCContent.secretJungleWallSign);
		putStandardBlockLootTable(SCContent.secretOakSign);
		putStandardBlockLootTable(SCContent.secretOakWallSign);
		putStandardBlockLootTable(SCContent.secretSpruceSign);
		putStandardBlockLootTable(SCContent.secretSpruceWallSign);
		putStandardBlockLootTable(SCContent.securityCamera);
		putStandardBlockLootTable(SCContent.stoneMine);
		putStandardBlockLootTable(SCContent.trackMine);
		putStandardBlockLootTable(SCContent.trophySystem);
		putStandardBlockLootTable(SCContent.usernameLogger);
	}

	protected final LootTable.Builder createStandardBlockLootTable(Block block)
	{
		return LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(block))
						.acceptCondition(SurvivesExplosion.builder()));
	}

	protected final void putDoorLootTable(Block door, Item doorItem)
	{
		lootTables.put(door, LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(doorItem)
								.acceptCondition(BlockStateProperty.builder(door)
										.fromProperties(StatePropertiesPredicate.Builder.newBuilder()
												.withProp(ReinforcedDoorBlock.HALF, DoubleBlockHalf.LOWER)))
								.acceptCondition(SurvivesExplosion.builder()))));
	}

	protected final void putStandardBlockLootTable(Block block)
	{
		lootTables.put(block, createStandardBlockLootTable(block));
	}

	@Override
	public void act(DirectoryCache cache) throws IOException
	{
		Map<ResourceLocation,LootTable> tables = new HashMap<>();

		addTables();

		for(Map.Entry<Block,LootTable.Builder> entry : lootTables.entrySet())
		{
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
		}

		tables.forEach((key, lootTable) -> {
			try
			{
				IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), generator.getOutputFolder().resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json"));
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
