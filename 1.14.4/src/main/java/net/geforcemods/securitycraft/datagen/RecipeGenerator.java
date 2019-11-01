package net.geforcemods.securitycraft.datagen;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.IngredientNBT;

//TODO: keycard 1-5 recipes once conditions can be added via the builder
//TODO: limited use keycard recipe once conditions can be added via the builder
public class RecipeGenerator extends RecipeProvider
{
	public RecipeGenerator(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
	{
		ItemStack healingStack = new ItemStack(Items.POTION);
		ItemStack strongHealingStack = new ItemStack(Items.POTION);
		ItemStack harmingStack = new ItemStack(Items.POTION);
		ItemStack strongHarmingStack = new ItemStack(Items.POTION);
		CompoundNBT healingNBT = new CompoundNBT();
		CompoundNBT strongHealingNBT = new CompoundNBT();
		CompoundNBT harmingNBT = new CompoundNBT();
		CompoundNBT strongHarmingNBT = new CompoundNBT();

		healingNBT.putString("Potion", Potions.HEALING.getRegistryName().toString());
		strongHealingNBT.putString("Potion", Potions.STRONG_HEALING.getRegistryName().toString());
		harmingNBT.putString("Potion", Potions.HARMING.getRegistryName().toString());
		strongHarmingNBT.putString("Potion", Potions.STRONG_HARMING.getRegistryName().toString());
		healingStack.setTag(healingNBT);
		strongHealingStack.setTag(strongHealingNBT);
		harmingStack.setTag(harmingNBT);
		strongHarmingStack.setTag(strongHarmingNBT);

		//shaped recipes
		ShapedRecipeBuilder.shapedRecipe(SCContent.alarm)
		.patternLine("GGG")
		.patternLine("GNG")
		.patternLine("GRG")
		.key('G', SCContent.reinforcedGlass)
		.key('N', Blocks.NOTE_BLOCK)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.blockPocketManager)
		.patternLine("CIC")
		.patternLine("IRI")
		.patternLine("CIC")
		.key('C', SCContent.reinforcedCrystalQuartz)
		.key('I', SCContent.reinforcedIronBlock)
		.key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.addCriterion("has_redstone_block", hasItem(Tags.Items.STORAGE_BLOCKS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.bouncingBetty)
		.patternLine(" P ")
		.patternLine("IGI")
		.key('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.briefcase)
		.patternLine("SSS")
		.patternLine("ICI")
		.patternLine("III")
		.key('S', Tags.Items.RODS_WOODEN)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('C', SCContent.keypadChest)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.fLavaBucket)
		.patternLine("P")
		.patternLine("B")
		.key('P', new NBTIngredient(healingStack))
		.key('B', Items.LAVA_BUCKET)
		.addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_lava_normal"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.fLavaBucket)
		.patternLine("P")
		.patternLine("B")
		.key('P', new NBTIngredient(strongHealingStack))
		.key('B', Items.LAVA_BUCKET)
		.addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_lava_strong"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.fWaterBucket)
		.patternLine("P")
		.patternLine("B")
		.key('P', new NBTIngredient(harmingStack))
		.key('B', Items.WATER_BUCKET)
		.addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_water_normal"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.fWaterBucket)
		.patternLine("P")
		.patternLine("B")
		.key('P', new NBTIngredient(strongHarmingStack))
		.key('B', Items.WATER_BUCKET)
		.addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_water_strong"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.cageTrap)
		.patternLine("BBB")
		.patternLine("GRG")
		.patternLine("III")
		.key('B', SCContent.reinforcedIronBars)
		.key('G', Tags.Items.INGOTS_GOLD)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('I', Tags.Items.STORAGE_BLOCKS_IRON)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.cameraMonitor)
		.patternLine("III")
		.patternLine("IGI")
		.patternLine("III")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.GLASS_PANES_COLORLESS)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.chiseledCrystalQuartz)
		.patternLine("B")
		.patternLine("B")
		.key('B', SCContent.crystalQuartzSlab)
		.addCriterion("has_crystal_quartz_slab", hasItem(SCContent.crystalQuartzSlab))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.claymore)
		.patternLine("HSH")
		.patternLine("SBS")
		.patternLine("RGR")
		.key('H', Blocks.TRIPWIRE_HOOK)
		.key('S', Tags.Items.STRING)
		.key('B', SCContent.bouncingBetty)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.codebreaker)
		.patternLine("DTD")
		.patternLine("GSG")
		.patternLine("RER")
		.key('D', Tags.Items.GEMS_DIAMOND)
		.key('T', Items.REDSTONE_TORCH)
		.key('G', Tags.Items.INGOTS_GOLD)
		.key('S', Tags.Items.NETHER_STARS)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('E', Tags.Items.GEMS_EMERALD)
		.addCriterion("has_nether_star", hasItem(Tags.Items.NETHER_STARS))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.crystalQuartzItem, 9)
		.patternLine("CQC")
		.patternLine("QCQ")
		.patternLine("CQC")
		.key('Q', Tags.Items.GEMS_QUARTZ)
		.key('C', Tags.Items.GEMS_PRISMARINE)
		.addCriterion("has_prismarine_crystals", hasItem(Tags.Items.GEMS_PRISMARINE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.crystalQuartz)
		.patternLine("CC")
		.patternLine("CC")
		.key('C', SCContent.crystalQuartzItem)
		.addCriterion("has_crystal_quartz_item", hasItem(SCContent.crystalQuartzItem))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.crystalQuartzPillar, 2)
		.patternLine("B")
		.patternLine("B")
		.key('B', SCContent.crystalQuartz)
		.addCriterion("has_crystal_quartz", hasItem(SCContent.crystalQuartz))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.reinforcedDoorItem)
		.patternLine("III")
		.patternLine("IDI")
		.patternLine("III")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('D', Items.IRON_DOOR)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.ironFence)
		.patternLine(" I ")
		.patternLine("IFI")
		.patternLine(" I ")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('F', ItemTags.WOODEN_FENCES)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.ims)
		.patternLine("BPB")
		.patternLine(" I ")
		.patternLine("B B")
		.key('B', SCContent.bouncingBetty)
		.key('P', SCContent.portableRadar)
		.key('I', Tags.Items.STORAGE_BLOCKS_IRON)
		.addCriterion("has_portable_radar", hasItem(SCContent.portableRadar))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.inventoryScanner)
		.patternLine("SSS")
		.patternLine("SLS")
		.patternLine("SCS")
		.key('S', Tags.Items.STONE)
		.key('L', SCContent.laserBlock)
		.key('C', Tags.Items.CHESTS_ENDER)
		.addCriterion("has_stone", hasItem(Tags.Items.STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.keycardReader)
		.patternLine("SSS")
		.patternLine("SHS")
		.patternLine("SSS")
		.key('S', Tags.Items.STONE)
		.key('H', Items.HOPPER)
		.addCriterion("has_stone", hasItem(Tags.Items.STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.frame)
		.patternLine("III")
		.patternLine("IR ")
		.patternLine("III")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.keyPanel)
		.patternLine("BBB")
		.patternLine("BPB")
		.patternLine("BBB")
		.key('B', Items.STONE_BUTTON)
		.key('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.addCriterion("has_stone_button", hasItem(Items.STONE_BUTTON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.laserBlock)
		.patternLine("SSS")
		.patternLine("SRS")
		.patternLine("SGS")
		.key('S', Tags.Items.STONE)
		.key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.key('G', Tags.Items.GLASS_PANES_COLORLESS)
		.addCriterion("has_stone", hasItem(Tags.Items.STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.mine, 3)
		.patternLine(" I ")
		.patternLine("IGI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_item", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.motionActivatedLight)
		.patternLine("L")
		.patternLine("R")
		.patternLine("S")
		.key('L', Blocks.REDSTONE_LAMP)
		.key('R', SCContent.portableRadar)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_stick", hasItem(Tags.Items.RODS_WOODEN))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.panicButton)
		.patternLine(" I ")
		.patternLine("IBI")
		.patternLine(" R ")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('B', Items.STONE_BUTTON)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.portableRadar)
		.patternLine("III")
		.patternLine("ITI")
		.patternLine("IRI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('T', Items.REDSTONE_TORCH)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.protecto)
		.patternLine("ODO")
		.patternLine("OEO")
		.patternLine("OOO")
		.key('O', Tags.Items.OBSIDIAN)
		.key('D', Blocks.DAYLIGHT_DETECTOR)
		.key('E', Items.ENDER_EYE)
		.addCriterion("has_obsidian", hasItem(Tags.Items.OBSIDIAN))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.reinforcedDiorite, 2)
		.patternLine("CQ")
		.patternLine("QC")
		.key('C', SCContent.reinforcedCobblestone)
		.key('Q', Tags.Items.GEMS_QUARTZ)
		.addCriterion("has_cobblestone", hasItem(Tags.Items.COBBLESTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.reinforcedFencegate)
		.patternLine(" I ")
		.patternLine("IGI")
		.patternLine(" I ")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.FENCE_GATES_WOODEN)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.reinforcedStonePressurePlate)
		.patternLine("SS")
		.key('S', SCContent.reinforcedStone)
		.addCriterion("has_reinforced_stone", hasItem(SCContent.reinforcedStone))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.remoteAccessMine)
		.patternLine(" T ")
		.patternLine(" DG")
		.patternLine("S  ")
		.key('T', Items.REDSTONE_TORCH)
		.key('D', Tags.Items.GEMS_DIAMOND)
		.key('G', Tags.Items.INGOTS_GOLD)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_mine", hasItem(SCContent.mine))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.retinalScanner)
		.patternLine("SSS")
		.patternLine("SES")
		.patternLine("SSS")
		.key('S', Tags.Items.STONE)
		.key('E', Items.ENDER_EYE)
		.addCriterion("has_stone", hasItem(Tags.Items.STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.securityCamera)
		.patternLine("III")
		.patternLine("GRI")
		.patternLine("IIS")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', SCContent.reinforcedGlass)
		.key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.sentry)
		.patternLine("RDR")
		.patternLine("IPI")
		.patternLine("BBB")
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('D', Blocks.DISPENSER)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('P', SCContent.portableRadar)
		.key('B', SCContent.reinforcedIronBlock)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.taser)
		.patternLine("BGI")
		.patternLine("RSG")
		.patternLine("  S")
		.key('B', Items.BOW)
		.key('G', Tags.Items.INGOTS_GOLD)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.trackMine, 4)
		.patternLine("I I")
		.patternLine("ISI")
		.patternLine("IGI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('S', Tags.Items.RODS_WOODEN)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.trophySystem)
		.patternLine(" T ")
		.patternLine(" B ")
		.patternLine("S S")
		.key('T', SCContent.sentry)
		.key('B', SCContent.reinforcedIronBlock)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.universalBlockModifier)
		.patternLine(" RE")
		.patternLine(" IR")
		.patternLine("I  ")
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('E', Tags.Items.GEMS_EMERALD)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.universalBlockReinforcerLvL1)
		.setGroup("securitycraft:universal_block_reinforcer")
		.patternLine(" DG")
		.patternLine("RLD")
		.patternLine("SR ")
		.key('G', Tags.Items.GLASS_COLORLESS)
		.key('D', Tags.Items.GEMS_DIAMOND)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('L', SCContent.laserBlock)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.universalBlockReinforcerLvL2)
		.setGroup("securitycraft:universal_block_reinforcer")
		.patternLine(" DG")
		.patternLine("RLD")
		.patternLine("SR ")
		.key('G', SCContent.reinforcedBlackStainedGlass)
		.key('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('L', SCContent.laserBlock)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.universalBlockReinforcerLvL3)
		.setGroup("securitycraft:universal_block_reinforcer")
		.patternLine(" EG")
		.patternLine("RNE")
		.patternLine("SR ")
		.key('G', SCContent.reinforcedPinkStainedGlass)
		.key('E', Tags.Items.STORAGE_BLOCKS_EMERALD)
		.key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.key('N', Tags.Items.NETHER_STARS)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.universalBlockRemover)
		.patternLine("SII")
		.key('S', Items.SHEARS)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.universalKeyChanger)
		.patternLine(" RL")
		.patternLine(" IR")
		.patternLine("I  ")
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('L', SCContent.laserBlock)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.usernameLogger)
		.patternLine("SPS")
		.patternLine("SRS")
		.patternLine("SSS")
		.key('S', Tags.Items.STONE)
		.key('P', SCContent.portableRadar)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.wireCutters)
		.patternLine("SI ")
		.patternLine("I I")
		.patternLine(" I ")
		.key('S', Items.SHEARS)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_mine", hasItem(SCContent.mine))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.reinforcedGlassPane)
		.patternLine("GGG")
		.patternLine("GGG")
		.key('G', SCContent.reinforcedGlass)
		.addCriterion("has_glass", hasItem(Tags.Items.GLASS))
		.build(consumer);

		//shapeless recipes
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.blockPocketWall)
		.addIngredient(SCContent.reinforcedCrystalQuartz)
		.addCriterion("has_reinforced_crystal_quartz", hasItem(SCContent.reinforcedCrystalQuartz))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.universalKeyChanger)
		.addIngredient(SCContent.universalKeyChanger)
		.addIngredient(SCContent.briefcase)
		.addCriterion("has_briefcase", hasItem(SCContent.briefcase))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "briefcase_reset"));
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.reinforcedAndesite, 2)
		.addIngredient(SCContent.reinforcedDiorite)
		.addIngredient(SCContent.reinforcedCobblestone)
		.addCriterion("has_cobblestone", hasItem(Tags.Items.COBBLESTONE))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.reinforcedCrystalQuartz)
		.addIngredient(SCContent.blockPocketWall)
		.addCriterion("has_block_pocket_wall", hasItem(SCContent.blockPocketWall))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.reinforcedGranite)
		.addIngredient(SCContent.reinforcedDiorite)
		.addIngredient(Tags.Items.GEMS_QUARTZ)
		.addCriterion("has_quartz", hasItem(Tags.Items.GEMS_QUARTZ))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.reinforcedMossyCobblestone)
		.addIngredient(Items.VINE)
		.addIngredient(SCContent.reinforcedCobblestone)
		.addCriterion("has_reinforced_cobblestone", hasItem(SCContent.reinforcedCobblestone))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.scManual)
		.addIngredient(Items.BOOK)
		.addIngredient(Blocks.IRON_BARS)
		.addCriterion("has_wood", hasItem(ItemTags.LOGS)) //the thought behind this is that the recipe will be given right after the player chopped their first piece of wood
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.scannerDoorItem)
		.addIngredient(SCContent.reinforcedDoorItem)
		.addIngredient(SCContent.retinalScanner)
		.addCriterion("has_reinforced_door", hasItem(SCContent.reinforcedDoorItem))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.universalOwnerChanger)
		.addIngredient(SCContent.universalBlockModifier)
		.addIngredient(Items.NAME_TAG)
		.addCriterion("has_name_tag", hasItem(Items.NAME_TAG))
		.build(consumer);

		//template recipes
		addBarkRecipe(consumer, SCContent.reinforcedAcaciaLog, SCContent.reinforcedAcaciaWood);
		addBarkRecipe(consumer, SCContent.reinforcedBirchLog, SCContent.reinforcedBirchWood);
		addBarkRecipe(consumer, SCContent.reinforcedDarkOakLog, SCContent.reinforcedDarkOakWood);
		addBarkRecipe(consumer, SCContent.reinforcedJungleLog, SCContent.reinforcedJungleWood);
		addBarkRecipe(consumer, SCContent.reinforcedSpruceLog, SCContent.reinforcedSpruceWood);
		addBarkRecipe(consumer, SCContent.reinforcedOakLog, SCContent.reinforcedOakWood);
		addBlockMineRecipe(consumer, Blocks.COBBLESTONE, SCContent.cobblestoneMine);
		addBlockMineRecipe(consumer, Blocks.DIAMOND_ORE, SCContent.diamondOreMine);
		addBlockMineRecipe(consumer, Blocks.DIRT, SCContent.dirtMine);
		addBlockMineRecipe(consumer, Blocks.FURNACE, SCContent.furnaceMine);
		addBlockMineRecipe(consumer, Blocks.GRAVEL, SCContent.gravelMine);
		addBlockMineRecipe(consumer, Blocks.SAND, SCContent.sandMine);
		addBlockMineRecipe(consumer, Blocks.STONE, SCContent.stoneMine);
		addCarpetRecipe(consumer, SCContent.reinforcedBlackWool, SCContent.reinforcedBlackCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedBlueWool, SCContent.reinforcedBlueCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedBrownWool, SCContent.reinforcedBrownCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedCyanWool, SCContent.reinforcedCyanCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedGrayWool, SCContent.reinforcedGrayCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedGreenWool, SCContent.reinforcedGreenCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedLightBlueWool, SCContent.reinforcedLightBlueCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedLightGrayWool, SCContent.reinforcedLightGrayCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedLimeWool, SCContent.reinforcedLimeCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedMagentaWool, SCContent.reinforcedMagentaCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedOrangeWool, SCContent.reinforcedOrangeCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedPinkWool, SCContent.reinforcedPinkCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedPurpleWool, SCContent.reinforcedPurpleCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedRedWool, SCContent.reinforcedRedCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedWhiteWool, SCContent.reinforcedWhiteCarpet);
		addCarpetRecipe(consumer, SCContent.reinforcedYellowWool, SCContent.reinforcedYellowCarpet);
		addModuleRecipe(consumer, Items.INK_SAC, SCContent.blacklistModule);
		addModuleRecipe(consumer, Items.PAINTING, SCContent.disguiseModule);
		addModuleRecipe(consumer, Tags.Items.ARROWS, SCContent.harmingModule);
		addModuleRecipe(consumer, Tags.Items.DUSTS_REDSTONE, SCContent.redstoneModule);
		addModuleRecipe(consumer, Tags.Items.ENDER_PEARLS, SCContent.smartModule);
		addModuleRecipe(consumer, SCContent.keypadChest, SCContent.storageModule);
		addModuleRecipe(consumer, Items.PAPER, SCContent.whitelistModule);
		addPolishingRecipe(consumer, SCContent.reinforcedAndesite, SCContent.reinforcedPolishedAndesite);
		addPolishingRecipe(consumer, SCContent.reinforcedDiorite, SCContent.reinforcedPolishedDiorite);
		addPolishingRecipe(consumer, SCContent.reinforcedGranite, SCContent.reinforcedPolishedGranite);
		addSecretSignRecipe(consumer, Items.ACACIA_SIGN, SCContent.secretAcaciaSign);
		addSecretSignRecipe(consumer, Items.BIRCH_SIGN, SCContent.secretBirchSign);
		addSecretSignRecipe(consumer, Items.DARK_OAK_SIGN, SCContent.secretDarkOakSign);
		addSecretSignRecipe(consumer, Items.JUNGLE_SIGN, SCContent.secretJungleSign);
		addSecretSignRecipe(consumer, Items.OAK_SIGN, SCContent.secretOakSign);
		addSecretSignRecipe(consumer, Items.SPRUCE_SIGN, SCContent.secretSpruceSign);
		addSlabRecipe(consumer, SCContent.crystalQuartz, SCContent.crystalQuartzSlab);
		addSlabRecipe(consumer, SCContent.reinforcedAndesite, SCContent.reinforcedAndesiteSlab);
		addSlabRecipe(consumer, SCContent.reinforcedCrystalQuartz, SCContent.reinforcedCrystalQuartzSlab);
		addSlabRecipe(consumer, SCContent.reinforcedDarkPrismarine, SCContent.reinforcedDarkPrismarineSlab);
		addSlabRecipe(consumer, SCContent.reinforcedDiorite, SCContent.reinforcedDioriteSlab);
		addSlabRecipe(consumer, SCContent.reinforcedEndStoneBricks, SCContent.reinforcedEndStoneBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedGranite, SCContent.reinforcedGraniteSlab);
		addSlabRecipe(consumer, SCContent.reinforcedMossyCobblestone, SCContent.reinforcedMossyCobblestoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedStone, SCContent.reinforcedNormalStoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedPolishedAndesite, SCContent.reinforcedPolishedAndesiteSlab);
		addSlabRecipe(consumer, SCContent.reinforcedPolishedDiorite, SCContent.reinforcedPolishedDioriteSlab);
		addSlabRecipe(consumer, SCContent.reinforcedPolishedGranite, SCContent.reinforcedPolishedGraniteSlab);
		addSlabRecipe(consumer, SCContent.reinforcedPrismarineBricks, SCContent.reinforcedPrismarineBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedPrismarine, SCContent.reinforcedPrismarineSlab);
		addSlabRecipe(consumer, SCContent.reinforcedRedNetherBricks, SCContent.reinforcedRedNetherBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedSmoothQuartz, SCContent.reinforcedSmoothQuartzSlab);
		addSlabRecipe(consumer, SCContent.reinforcedSmoothRedSandstone, SCContent.reinforcedSmoothRedSandstoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedSmoothSandstone, SCContent.reinforcedSmoothSandstoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedSmoothStone, SCContent.reinforcedSmoothStoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedNetherBricks, SCContent.reinforcedNetherBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedBricks, SCContent.reinforcedBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedCobblestone, SCContent.reinforcedCobblestoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedQuartz, SCContent.reinforcedQuartzSlab);
		addSlabRecipe(consumer, SCContent.reinforcedSandstone, SCContent.reinforcedSandstoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedStoneBricks, SCContent.reinforcedStoneBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedMossyStoneBricks, SCContent.reinforcedMossyStoneBrickSlab);
		addSlabRecipe(consumer, SCContent.reinforcedRedSandstone, SCContent.reinforcedRedSandstoneSlab);
		addSlabRecipe(consumer, SCContent.reinforcedPurpurBlock, SCContent.reinforcedPurpurSlab);
		addSlabRecipe(consumer, SCContent.reinforcedAcaciaPlanks, SCContent.reinforcedAcaciaSlab);
		addSlabRecipe(consumer, SCContent.reinforcedBirchPlanks, SCContent.reinforcedBirchSlab);
		addSlabRecipe(consumer, SCContent.reinforcedDarkOakPlanks, SCContent.reinforcedDarkOakSlab);
		addSlabRecipe(consumer, SCContent.reinforcedJunglePlanks, SCContent.reinforcedJungleSlab);
		addSlabRecipe(consumer, SCContent.reinforcedOakPlanks, SCContent.reinforcedOakSlab);
		addSlabRecipe(consumer, SCContent.reinforcedSprucePlanks, SCContent.reinforcedSpruceSlab);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BLACK, SCContent.reinforcedBlackStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BLUE, SCContent.reinforcedBlueStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BROWN, SCContent.reinforcedBrownStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_CYAN, SCContent.reinforcedCyanStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.reinforcedGrayStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GREEN, SCContent.reinforcedGreenStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.reinforcedLightBlueStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.reinforcedLightGrayStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_LIME, SCContent.reinforcedLimeStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.reinforcedMagentaStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_ORANGE, SCContent.reinforcedOrangeStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_PINK, SCContent.reinforcedPinkStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.reinforcedPurpleStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_RED, SCContent.reinforcedRedStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_WHITE, SCContent.reinforcedWhiteStainedGlass);
		addStainedGlassRecipe(consumer, Tags.Items.DYES_YELLOW, SCContent.reinforcedYellowStainedGlass);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_BLACK, SCContent.reinforcedBlackStainedGlass, SCContent.reinforcedBlackStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_BLUE, SCContent.reinforcedBlueStainedGlass, SCContent.reinforcedBlueStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_BROWN, SCContent.reinforcedBrownStainedGlass, SCContent.reinforcedBrownStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_CYAN, SCContent.reinforcedCyanStainedGlass, SCContent.reinforcedCyanStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_GRAY, SCContent.reinforcedGrayStainedGlass, SCContent.reinforcedGrayStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_GREEN, SCContent.reinforcedGreenStainedGlass, SCContent.reinforcedGreenStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.reinforcedLightBlueStainedGlass, SCContent.reinforcedLightBlueStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_GRAY, SCContent.reinforcedLightGrayStainedGlass, SCContent.reinforcedLightGrayStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_LIME, SCContent.reinforcedLimeStainedGlass, SCContent.reinforcedLimeStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_MAGENTA, SCContent.reinforcedMagentaStainedGlass, SCContent.reinforcedMagentaStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_ORANGE, SCContent.reinforcedOrangeStainedGlass, SCContent.reinforcedOrangeStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_PINK, SCContent.reinforcedPinkStainedGlass, SCContent.reinforcedPinkStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_PURPLE, SCContent.reinforcedPurpleStainedGlass, SCContent.reinforcedPurpleStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_RED, SCContent.reinforcedRedStainedGlass, SCContent.reinforcedRedStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_WHITE, SCContent.reinforcedWhiteStainedGlass, SCContent.reinforcedWhiteStainedGlassPane);
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_YELLOW, SCContent.reinforcedYellowStainedGlass, SCContent.reinforcedYellowStainedGlassPane);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_BLACK, SCContent.reinforcedBlackTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_BLUE, SCContent.reinforcedBlueTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_BROWN, SCContent.reinforcedBrownTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_CYAN, SCContent.reinforcedCyanTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.reinforcedGrayTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_GREEN, SCContent.reinforcedGreenTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.reinforcedLightBlueTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.reinforcedLightGrayTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_LIME, SCContent.reinforcedLimeTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.reinforcedMagentaTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_ORANGE, SCContent.reinforcedOrangeTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_PINK, SCContent.reinforcedPinkTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.reinforcedPurpleTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_RED, SCContent.reinforcedRedTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_WHITE, SCContent.reinforcedWhiteTerracotta);
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_YELLOW, SCContent.reinforcedYellowTerracotta);
		addStairsRecipe(consumer, SCContent.reinforcedAndesite, SCContent.reinforcedAndesiteStairs);
		addStairsRecipe(consumer, SCContent.reinforcedDarkPrismarine, SCContent.reinforcedDarkPrismarineStairs);
		addStairsRecipe(consumer, SCContent.reinforcedDiorite, SCContent.reinforcedDioriteStairs);
		addStairsRecipe(consumer, SCContent.reinforcedEndStoneBricks, SCContent.reinforcedEndStoneBrickStairs);
		addStairsRecipe(consumer, SCContent.reinforcedGranite, SCContent.reinforcedGraniteStairs);
		addStairsRecipe(consumer, SCContent.reinforcedMossyCobblestone, SCContent.reinforcedMossyCobblestoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedPolishedAndesite, SCContent.reinforcedPolishedAndesiteStairs);
		addStairsRecipe(consumer, SCContent.reinforcedPolishedDiorite, SCContent.reinforcedPolishedDioriteStairs);
		addStairsRecipe(consumer, SCContent.reinforcedPolishedGranite, SCContent.reinforcedPolishedGraniteStairs);
		addStairsRecipe(consumer, SCContent.reinforcedPrismarineBricks, SCContent.reinforcedPrismarineBrickStairs);
		addStairsRecipe(consumer, SCContent.reinforcedPrismarine, SCContent.reinforcedPrismarineStairs);
		addStairsRecipe(consumer, SCContent.reinforcedRedNetherBricks, SCContent.reinforcedRedNetherBrickStairs);
		addStairsRecipe(consumer, SCContent.reinforcedSmoothQuartz, SCContent.reinforcedSmoothQuartzStairs);
		addStairsRecipe(consumer, SCContent.reinforcedSmoothRedSandstone, SCContent.reinforcedSmoothRedSandstoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedSmoothSandstone, SCContent.reinforcedSmoothSandstoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedAcaciaPlanks, SCContent.reinforcedAcaciaStairs);
		addStairsRecipe(consumer, SCContent.reinforcedBirchPlanks, SCContent.reinforcedBirchStairs);
		addStairsRecipe(consumer, SCContent.reinforcedBricks, SCContent.reinforcedBrickStairs);
		addStairsRecipe(consumer, SCContent.reinforcedCobblestone, SCContent.reinforcedCobblestoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedCrystalQuartz, SCContent.reinforcedCrystalQuartzStairs);
		addStairsRecipe(consumer, SCContent.reinforcedDarkOakPlanks, SCContent.reinforcedDarkOakStairs);
		addStairsRecipe(consumer, SCContent.reinforcedJunglePlanks, SCContent.reinforcedJungleStairs);
		addStairsRecipe(consumer, SCContent.reinforcedNetherBricks, SCContent.reinforcedNetherBrickStairs);
		addStairsRecipe(consumer, SCContent.reinforcedOakPlanks, SCContent.reinforcedOakStairs);
		addStairsRecipe(consumer, SCContent.reinforcedPurpurBlock, SCContent.reinforcedPurpurStairs);
		addStairsRecipe(consumer, SCContent.reinforcedQuartz, SCContent.reinforcedQuartzStairs);
		addStairsRecipe(consumer, SCContent.reinforcedRedSandstone, SCContent.reinforcedRedSandstoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedSandstone, SCContent.reinforcedSandstoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedSprucePlanks, SCContent.reinforcedSpruceStairs);
		addStairsRecipe(consumer, SCContent.reinforcedStone, SCContent.reinforcedStoneStairs);
		addStairsRecipe(consumer, SCContent.reinforcedStoneBricks, SCContent.reinforcedStoneBrickStairs);
		addStairsRecipe(consumer, SCContent.reinforcedMossyStoneBricks, SCContent.reinforcedMossyStoneBrickStairs);
		addStairsRecipe(consumer, SCContent.crystalQuartz, SCContent.stairsCrystalQuartz);

		//furnace recipes
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.reinforcedQuartz), SCContent.reinforcedSmoothQuartz, 0.1F, 200)
		.addCriterion("has_reinforced_quartz", hasItem(SCContent.reinforcedQuartz))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.reinforcedRedSandstone), SCContent.reinforcedSmoothRedSandstone, 0.1F, 200)
		.addCriterion("has_reinforced_red_sandstone", hasItem(SCContent.reinforcedRedSandstone))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.reinforcedSandstone), SCContent.reinforcedSmoothSandstone, 0.1F, 200)
		.addCriterion("has_reinforced_sandstone", hasItem(SCContent.reinforcedSandstone))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.reinforcedStone), SCContent.reinforcedSmoothStone, 0.1F, 200)
		.addCriterion("has_reinforced_stone", hasItem(SCContent.reinforcedStone))
		.build(consumer);
	}

	protected void addBarkRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider log, IItemProvider result) //woof 🐶
	{
		ShapedRecipeBuilder.shapedRecipe(result, 3)
		.setGroup("securitycraft:bark")
		.patternLine("LL")
		.patternLine("LL")
		.key('L', log)
		.addCriterion("has_log", hasItem(log))
		.build(consumer);
	}

	protected void addBlockMineRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.setGroup("securitycraft:block_mines")
		.addIngredient(input)
		.addIngredient(SCContent.mine)
		.addCriterion("has_mine", hasItem(SCContent.mine))
		.build(consumer);
	}

	protected void addBlockMineRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> input, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.setGroup("securitycraft:block_mines")
		.addIngredient(input)
		.addIngredient(SCContent.mine)
		.addCriterion("has_mine", hasItem(SCContent.mine))
		.build(consumer);
	}

	protected void addCarpetRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider wool, IItemProvider carpet)
	{
		ShapedRecipeBuilder.shapedRecipe(carpet, 3)
		.setGroup("securitycraft:reinforced_carpets")
		.patternLine("WW")
		.key('W', wool)
		.addCriterion("has_wool", hasItem(SCTags.Items.REINFORCED_WOOL))
		.build(consumer);
	}

	protected void addModuleRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider specialIngredient, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result)
		.setGroup("securitycraft:modules")
		.patternLine("III")
		.patternLine("IPI")
		.patternLine("ISI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('P', Items.PAPER)
		.key('S', specialIngredient)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
	}

	protected void addModuleRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> specialIngredient, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result)
		.setGroup("securitycraft:modules")
		.patternLine("III")
		.patternLine("IPI")
		.patternLine("ISI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('P', Items.PAPER)
		.key('S', specialIngredient)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
	}

	protected void addPolishingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 4)
		.patternLine("BB")
		.patternLine("BB")
		.key('B', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected void addSecretSignRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider vanillaSign, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result, 3)
		.setGroup("securitycraft:secret_signs")
		.addIngredient(vanillaSign, 3)
		.addIngredient(SCContent.retinalScanner)
		.addCriterion("has_sign", hasItem(ItemTags.SIGNS))
		.build(consumer);
	}

	protected void addSlabRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 6)
		.setGroup("securitycraft:slabs")
		.patternLine("BBB")
		.key('B', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected void addStainedGlassRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 8)
		.setGroup("securitycraft:reinforced_glass")
		.patternLine("GGG")
		.patternLine("GDG")
		.patternLine("GGG")
		.key('G', SCContent.reinforcedGlass)
		.key('D', dye)
		.addCriterion("has_glass", hasItem(Tags.Items.GLASS))
		.build(consumer);
	}

	protected void addStainedGlassPaneRecipes(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider stainedGlass, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 8)
		.setGroup("securitycraft:reinforced_glass_panes")
		.patternLine("GGG")
		.patternLine("GDG")
		.patternLine("GGG")
		.key('G', SCContent.reinforcedGlassPane)
		.key('D', dye)
		.addCriterion("has_glass", hasItem(Tags.Items.GLASS))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, result.asItem().getRegistryName().getPath() + "_from_dye"));
		ShapedRecipeBuilder.shapedRecipe(result, 16)
		.setGroup("securitycraft:reinforced_glass_panes")
		.patternLine("GGG")
		.patternLine("GGG")
		.key('G', stainedGlass)
		.addCriterion("has_glass", hasItem(Tags.Items.GLASS))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, result.asItem().getRegistryName().getPath() + "_from_glass"));
	}

	protected void addStainedTerracottaRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 8)
		.setGroup("securitycraft:reinforced_terracotta")
		.patternLine("TTT")
		.patternLine("TDT")
		.patternLine("TTT")
		.key('T', SCContent.reinforcedTerracotta)
		.key('D', dye)
		.addCriterion("has_reinforced_terracotta", hasItem(SCContent.reinforcedTerracotta))
		.build(consumer);
	}

	protected void addStairsRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 4)
		.setGroup("securitycraft:stairs")
		.patternLine("B  ")
		.patternLine("BB ")
		.patternLine("BBB")
		.key('B', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Recipes";
	}

	//helper because IngredientNBT's constructor is protected
	private static class NBTIngredient extends IngredientNBT
	{
		public NBTIngredient(ItemStack stack)
		{
			super(stack);
		}
	}
}
