package net.geforcemods.securitycraft.datagen;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Maps;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard1Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard2Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard3Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard4Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard5Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleLimitedUseKeycardCondition;
import net.minecraft.advancements.Advancement;
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
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class RecipeGenerator extends RecipeProvider
{
	public RecipeGenerator(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	protected final void registerRecipes(Consumer<IFinishedRecipe> consumer)
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
		ShapedRecipeBuilder.shapedRecipe(SCContent.ALARM.get())
		.patternLine("GGG")
		.patternLine("GNG")
		.patternLine("GRG")
		.key('G', SCContent.REINFORCED_GLASS.get())
		.key('N', Blocks.NOTE_BLOCK)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.BLOCK_POCKET_MANAGER.get())
		.patternLine("CIC")
		.patternLine("IRI")
		.patternLine("CIC")
		.key('C', SCContent.REINFORCED_CRYSTAL_QUARTZ.get())
		.key('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.key('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.addCriterion("has_redstone_block", hasItem(Tags.Items.STORAGE_BLOCKS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.BOUNCING_BETTY.get())
		.patternLine(" P ")
		.patternLine("IGI")
		.key('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.BRIEFCASE.get())
		.patternLine("SSS")
		.patternLine("ICI")
		.patternLine("III")
		.key('S', Tags.Items.RODS_WOODEN)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('C', SCContent.KEYPAD_CHEST.get())
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.FAKE_LAVA_BUCKET.get())
		.setGroup("securitycraft:fake_liquids")
		.patternLine("P")
		.patternLine("B")
		.key('P', new CustomNBTIngredient(healingStack))
		.key('B', Items.LAVA_BUCKET)
		.addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_lava_normal"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.FAKE_LAVA_BUCKET.get())
		.setGroup("securitycraft:fake_liquids")
		.patternLine("P")
		.patternLine("B")
		.key('P', new CustomNBTIngredient(strongHealingStack))
		.key('B', Items.LAVA_BUCKET)
		.addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_lava_strong"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.FAKE_WATER_BUCKET.get())
		.setGroup("securitycraft:fake_liquids")
		.patternLine("P")
		.patternLine("B")
		.key('P', new CustomNBTIngredient(harmingStack))
		.key('B', Items.WATER_BUCKET)
		.addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_water_normal"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.FAKE_WATER_BUCKET.get())
		.setGroup("securitycraft:fake_liquids")
		.patternLine("P")
		.patternLine("B")
		.key('P', new CustomNBTIngredient(strongHarmingStack))
		.key('B', Items.WATER_BUCKET)
		.addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_water_strong"));
		ShapedRecipeBuilder.shapedRecipe(SCContent.CAGE_TRAP.get())
		.patternLine("BBB")
		.patternLine("GRG")
		.patternLine("III")
		.key('B', SCContent.REINFORCED_IRON_BARS.get())
		.key('G', Tags.Items.INGOTS_GOLD)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.CAMERA_MONITOR.get())
		.patternLine("III")
		.patternLine("IGI")
		.patternLine("III")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', SCContent.REINFORCED_GLASS_PANE.get())
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.CLAYMORE.get())
		.patternLine("HSH")
		.patternLine("SBS")
		.patternLine("RGR")
		.key('H', Blocks.TRIPWIRE_HOOK)
		.key('S', Tags.Items.STRING)
		.key('B', SCContent.BOUNCING_BETTY.get())
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.CODEBREAKER.get())
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
		ShapedRecipeBuilder.shapedRecipe(SCContent.CRYSTAL_QUARTZ_ITEM.get(), 9)
		.patternLine("CQC")
		.patternLine("QCQ")
		.patternLine("CQC")
		.key('Q', Tags.Items.GEMS_QUARTZ)
		.key('C', Tags.Items.GEMS_PRISMARINE)
		.addCriterion("has_prismarine_crystals", hasItem(Tags.Items.GEMS_PRISMARINE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.CRYSTAL_QUARTZ.get())
		.patternLine("CC")
		.patternLine("CC")
		.key('C', SCContent.CRYSTAL_QUARTZ_ITEM.get())
		.addCriterion("has_crystal_quartz_item", hasItem(SCContent.CRYSTAL_QUARTZ_ITEM.get()))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REINFORCED_DOOR_ITEM.get())
		.patternLine("III")
		.patternLine("IDI")
		.patternLine("III")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('D', Items.IRON_DOOR)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.IRON_FENCE.get())
		.patternLine(" I ")
		.patternLine("IFI")
		.patternLine(" I ")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('F', ItemTags.WOODEN_FENCES)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.IMS.get())
		.patternLine("BPB")
		.patternLine(" I ")
		.patternLine("B B")
		.key('B', SCContent.BOUNCING_BETTY.get())
		.key('P', SCContent.PORTABLE_RADAR.get())
		.key('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.addCriterion("has_portable_radar", hasItem(SCContent.PORTABLE_RADAR.get()))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.INVENTORY_SCANNER.get())
		.patternLine("SSS")
		.patternLine("SLS")
		.patternLine("SCS")
		.key('S', SCTags.Items.REINFORCED_STONE)
		.key('L', SCContent.LASER_BLOCK.get())
		.key('C', Tags.Items.CHESTS_ENDER)
		.addCriterion("has_stone", hasItem(SCTags.Items.REINFORCED_STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.KEYCARD_READER.get())
		.patternLine("SSS")
		.patternLine("SHS")
		.patternLine("SSS")
		.key('S', SCTags.Items.REINFORCED_STONE)
		.key('H', Items.HOPPER)
		.addCriterion("has_stone", hasItem(SCTags.Items.REINFORCED_STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.FRAME.get())
		.patternLine("III")
		.patternLine("IR ")
		.patternLine("III")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.KEY_PANEL.get())
		.patternLine("BBB")
		.patternLine("BPB")
		.patternLine("BBB")
		.key('B', Items.STONE_BUTTON)
		.key('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.addCriterion("has_stone_button", hasItem(Items.STONE_BUTTON))
		.build(consumer);
		//don't change these to reinforced, because the block reinforcer needs a laser block!!!
		ShapedRecipeBuilder.shapedRecipe(SCContent.LASER_BLOCK.get())
		.patternLine("SSS")
		.patternLine("SRS")
		.patternLine("SGS")
		.key('S', Tags.Items.STONE)
		.key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.key('G', Tags.Items.GLASS_PANES_COLORLESS)
		.addCriterion("has_stone", hasItem(Tags.Items.STONE))
		.build(consumer);
		//k you can change again :)
		ShapedRecipeBuilder.shapedRecipe(SCContent.MINE.get(), 3)
		.patternLine(" I ")
		.patternLine("IGI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_item", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.MOTION_ACTIVATED_LIGHT.get())
		.patternLine("L")
		.patternLine("R")
		.patternLine("S")
		.key('L', Blocks.REDSTONE_LAMP)
		.key('R', SCContent.PORTABLE_RADAR.get())
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_stick", hasItem(Tags.Items.RODS_WOODEN))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.PANIC_BUTTON.get())
		.patternLine(" I ")
		.patternLine("IBI")
		.patternLine(" R ")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('B', Items.STONE_BUTTON)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.PORTABLE_RADAR.get())
		.patternLine("III")
		.patternLine("ITI")
		.patternLine("IRI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('T', Items.REDSTONE_TORCH)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.PROTECTO.get())
		.patternLine("ODO")
		.patternLine("OEO")
		.patternLine("OOO")
		.key('O', SCContent.REINFORCED_OBSIDIAN.get())
		.key('D', Blocks.DAYLIGHT_DETECTOR)
		.key('E', Items.ENDER_EYE)
		.addCriterion("has_ender_eye", hasItem(Items.ENDER_EYE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REINFORCED_BOOKSHELF.get())
		.patternLine("PPP")
		.patternLine("BBB")
		.patternLine("PPP")
		.key('B', Items.BOOK)
		.key('P', SCTags.Items.REINFORCED_PLANKS)
		.addCriterion("has_book", hasItem(Items.BOOK))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REINFORCED_DIORITE.get(), 2)
		.patternLine("CQ")
		.patternLine("QC")
		.key('C', SCContent.REINFORCED_COBBLESTONE.get())
		.key('Q', Tags.Items.GEMS_QUARTZ)
		.addCriterion("has_cobblestone", hasItem(Tags.Items.COBBLESTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REINFORCED_FENCEGATE.get())
		.patternLine(" I ")
		.patternLine("IGI")
		.patternLine(" I ")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', Tags.Items.FENCE_GATES_WOODEN)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REINFORCED_OBSERVER.get())
		.patternLine("CCC")
		.patternLine("RRQ")
		.patternLine("CCC")
		.key('C', SCTags.Items.REINFORCED_COBBLESTONE)
		.key('Q', Tags.Items.GEMS_QUARTZ)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_quartz", hasItem(Tags.Items.GEMS_QUARTZ))
		.build(consumer);
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get());
		ShapedRecipeBuilder.shapedRecipe(SCContent.REMOTE_ACCESS_MINE.get())
		.patternLine(" T ")
		.patternLine(" DG")
		.patternLine("S  ")
		.key('T', Items.REDSTONE_TORCH)
		.key('D', Tags.Items.GEMS_DIAMOND)
		.key('G', Tags.Items.INGOTS_GOLD)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_mine", hasItem(SCContent.MINE.get()))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REMOTE_ACCESS_SENTRY.get())
		.patternLine("ITI")
		.patternLine("IDI")
		.patternLine("ISI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('T', Items.REDSTONE_TORCH)
		.key('D', Tags.Items.GEMS_DIAMOND)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_sentry", hasItem(SCContent.SENTRY.get()))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.RETINAL_SCANNER.get())
		.patternLine("SSS")
		.patternLine("SES")
		.patternLine("SSS")
		.key('S', SCTags.Items.REINFORCED_STONE)
		.key('E', Items.ENDER_EYE)
		.addCriterion("has_stone", hasItem(SCTags.Items.REINFORCED_STONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.SECURITY_CAMERA.get())
		.patternLine("III")
		.patternLine("GRI")
		.patternLine("IIS")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('G', SCContent.REINFORCED_GLASS.get())
		.key('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.SENTRY.get())
		.patternLine("RDR")
		.patternLine("IPI")
		.patternLine("BBB")
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('D', Blocks.DISPENSER)
		.key('I', Tags.Items.INGOTS_IRON)
		.key('P', SCContent.PORTABLE_RADAR.get())
		.key('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.TASER.get())
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
		ShapedRecipeBuilder.shapedRecipe(SCContent.TRACK_MINE.get(), 4)
		.patternLine("I I")
		.patternLine("ISI")
		.patternLine("IGI")
		.key('I', Tags.Items.INGOTS_IRON)
		.key('S', Tags.Items.RODS_WOODEN)
		.key('G', Tags.Items.GUNPOWDER)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.TROPHY_SYSTEM.get())
		.patternLine(" T ")
		.patternLine(" B ")
		.patternLine("S S")
		.key('T', SCContent.SENTRY.get())
		.key('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
		.patternLine(" RE")
		.patternLine(" IR")
		.patternLine("I  ")
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('E', Tags.Items.GEMS_EMERALD)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get())
		.setGroup("securitycraft:universal_block_reinforcer")
		.patternLine(" DG")
		.patternLine("RLD")
		.patternLine("SR ")
		.key('G', Tags.Items.GLASS_COLORLESS)
		.key('D', Tags.Items.GEMS_DIAMOND)
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('L', SCContent.LASER_BLOCK.get())
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get())
		.setGroup("securitycraft:universal_block_reinforcer")
		.patternLine(" DG")
		.patternLine("RLD")
		.patternLine("SR ")
		.key('G', SCContent.REINFORCED_BLACK_STAINED_GLASS.get())
		.key('D', SCContent.REINFORCED_DIAMOND_BLOCK.get())
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('L', SCContent.LASER_BLOCK.get())
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
		.setGroup("securitycraft:universal_block_reinforcer")
		.patternLine(" EG")
		.patternLine("RNE")
		.patternLine("SR ")
		.key('G', SCContent.REINFORCED_PINK_STAINED_GLASS.get())
		.key('E', SCContent.REINFORCED_EMERALD_BLOCK.get())
		.key('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.key('N', Tags.Items.NETHER_STARS)
		.key('S', Tags.Items.RODS_WOODEN)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.UNIVERSAL_BLOCK_REMOVER.get())
		.patternLine("SII")
		.key('S', Items.SHEARS)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.UNIVERSAL_KEY_CHANGER.get())
		.patternLine(" RL")
		.patternLine(" IR")
		.patternLine("I  ")
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.key('L', SCContent.LASER_BLOCK.get())
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.USERNAME_LOGGER.get())
		.patternLine("SPS")
		.patternLine("SRS")
		.patternLine("SSS")
		.key('S', SCTags.Items.REINFORCED_STONE)
		.key('P', SCContent.PORTABLE_RADAR.get())
		.key('R', Tags.Items.DUSTS_REDSTONE)
		.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.WIRE_CUTTERS.get())
		.patternLine("SI ")
		.patternLine("I I")
		.patternLine(" I ")
		.key('S', Items.SHEARS)
		.key('I', Tags.Items.INGOTS_IRON)
		.addCriterion("has_mine", hasItem(SCContent.MINE.get()))
		.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(SCContent.REINFORCED_GLASS_PANE.get())
		.patternLine("GGG")
		.patternLine("GGG")
		.key('G', SCContent.REINFORCED_GLASS.get())
		.addCriterion("has_glass", hasItem(Tags.Items.GLASS))
		.build(consumer);

		//shapeless recipes
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.BLOCK_POCKET_WALL.get())
		.addIngredient(SCContent.REINFORCED_CRYSTAL_QUARTZ.get())
		.addCriterion("has_reinforced_crystal_quartz", hasItem(SCContent.REINFORCED_CRYSTAL_QUARTZ.get()))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.UNIVERSAL_KEY_CHANGER.get())
		.addIngredient(SCContent.UNIVERSAL_KEY_CHANGER.get())
		.addIngredient(SCContent.BRIEFCASE.get())
		.addCriterion("has_briefcase", hasItem(SCContent.BRIEFCASE.get()))
		.build(consumer, new ResourceLocation(SecurityCraft.MODID, "briefcase_reset"));
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.REINFORCED_ANDESITE.get(), 2)
		.addIngredient(SCContent.REINFORCED_DIORITE.get())
		.addIngredient(SCTags.Items.REINFORCED_COBBLESTONE)
		.addCriterion("has_cobblestone", hasItem(Tags.Items.COBBLESTONE))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ.get())
		.addIngredient(SCContent.BLOCK_POCKET_WALL.get())
		.addCriterion("has_block_pocket_wall", hasItem(SCContent.BLOCK_POCKET_WALL.get()))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.REINFORCED_GRANITE.get())
		.addIngredient(SCContent.REINFORCED_DIORITE.get())
		.addIngredient(Tags.Items.GEMS_QUARTZ)
		.addCriterion("has_quartz", hasItem(Tags.Items.GEMS_QUARTZ))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.SC_MANUAL.get())
		.addIngredient(Items.BOOK)
		.addIngredient(Blocks.IRON_BARS)
		.addCriterion("has_wood", hasItem(ItemTags.LOGS)) //the thought behind this is that the recipe will be given right after the player chopped their first piece of wood
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.SCANNER_DOOR_ITEM.get())
		.addIngredient(SCContent.REINFORCED_DOOR_ITEM.get())
		.addIngredient(SCContent.RETINAL_SCANNER.get())
		.addCriterion("has_reinforced_door", hasItem(SCContent.REINFORCED_DOOR_ITEM.get()))
		.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(SCContent.UNIVERSAL_OWNER_CHANGER.get())
		.addIngredient(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
		.addIngredient(Items.NAME_TAG)
		.addCriterion("has_name_tag", hasItem(Items.NAME_TAG))
		.build(consumer);

		//template recipes
		addBarkRecipe(consumer, SCContent.REINFORCED_ACACIA_LOG.get(), SCContent.REINFORCED_ACACIA_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_BIRCH_LOG.get(), SCContent.REINFORCED_BIRCH_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_DARK_OAK_LOG.get(), SCContent.REINFORCED_DARK_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_JUNGLE_LOG.get(), SCContent.REINFORCED_JUNGLE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_OAK_LOG.get(), SCContent.REINFORCED_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_SPRUCE_LOG.get(), SCContent.REINFORCED_SPRUCE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(), SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(), SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(), SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(), SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(), SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_OAK_LOG.get(), SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		addBlockMineRecipe(consumer, Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DIRT, SCContent.DIRT_MINE.get());
		addBlockMineRecipe(consumer, Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.FURNACE, SCContent.FURNACE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.GRAVEL, SCContent.GRAVEL_MINE.get());
		addBlockMineRecipe(consumer, Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.IRON_ORE, SCContent.IRON_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.SAND, SCContent.SAND_MINE.get());
		addBlockMineRecipe(consumer, Blocks.STONE, SCContent.STONE_MINE.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_BLACK_WOOL.get(), SCContent.REINFORCED_BLACK_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_BLUE_WOOL.get(), SCContent.REINFORCED_BLUE_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_BROWN_WOOL.get(), SCContent.REINFORCED_BROWN_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_CYAN_WOOL.get(), SCContent.REINFORCED_CYAN_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_GRAY_WOOL.get(), SCContent.REINFORCED_GRAY_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_GREEN_WOOL.get(), SCContent.REINFORCED_GREEN_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_LIGHT_BLUE_WOOL.get(), SCContent.REINFORCED_LIGHT_BLUE_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_LIGHT_GRAY_WOOL.get(), SCContent.REINFORCED_LIGHT_GRAY_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_LIME_WOOL.get(), SCContent.REINFORCED_LIME_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_MAGENTA_WOOL.get(), SCContent.REINFORCED_MAGENTA_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_ORANGE_WOOL.get(), SCContent.REINFORCED_ORANGE_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_PINK_WOOL.get(), SCContent.REINFORCED_PINK_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_PURPLE_WOOL.get(), SCContent.REINFORCED_PURPLE_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_RED_WOOL.get(), SCContent.REINFORCED_RED_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_WHITE_WOOL.get(), SCContent.REINFORCED_WHITE_CARPET.get());
		addCarpetRecipe(consumer, SCContent.REINFORCED_YELLOW_WOOL.get(), SCContent.REINFORCED_YELLOW_CARPET.get());
		addChiselingRecipe(consumer, SCContent.CRYSTAL_QUARTZ_SLAB.get(), SCContent.CHISELED_CRYSTAL_QUARTZ.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_PURPUR_SLAB.get(), SCContent.REINFORCED_PURPUR_PILLAR.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_QUARTZ_SLAB.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), SCContent.REINFORCED_CHISELED_RED_SANDSTONE.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_SANDSTONE_SLAB.get(), SCContent.REINFORCED_CHISELED_SANDSTONE.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_STONE_BRICK_SLAB.get(), SCContent.REINFORCED_CHISELED_STONE_BRICKS.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_WOOL.get());
		addColoredWoolRecipe(consumer, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_WOOL.get());
		addKeycardRecipe(consumer, Tags.Items.INGOTS_GOLD, SCContent.KEYCARD_LVL_1.get(), ToggleKeycard1Condition.INSTANCE);
		addKeycardRecipe(consumer, Tags.Items.INGOTS_BRICK, SCContent.KEYCARD_LVL_2.get(), ToggleKeycard2Condition.INSTANCE);
		addKeycardRecipe(consumer, Tags.Items.INGOTS_NETHER_BRICK, SCContent.KEYCARD_LVL_3.get(), ToggleKeycard3Condition.INSTANCE);
		addKeycardRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.KEYCARD_LVL_4.get(), ToggleKeycard4Condition.INSTANCE);
		addKeycardRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.KEYCARD_LVL_5.get(), ToggleKeycard5Condition.INSTANCE);
		addKeycardRecipe(consumer, Tags.Items.GEMS_LAPIS, SCContent.LIMITED_USE_KEYCARD.get(), ToggleLimitedUseKeycardCondition.INSTANCE);
		addModuleRecipe(consumer, Items.INK_SAC, SCContent.BLACKLIST_MODULE.get());
		addModuleRecipe(consumer, Items.PAINTING, SCContent.DISGUISE_MODULE.get());
		addModuleRecipe(consumer, Tags.Items.ARROWS, SCContent.HARMING_MODULE.get());
		addModuleRecipe(consumer, Tags.Items.DUSTS_REDSTONE, SCContent.REDSTONE_MODULE.get());
		addModuleRecipe(consumer, Tags.Items.ENDER_PEARLS, SCContent.SMART_MODULE.get());
		addModuleRecipe(consumer, SCContent.KEYPAD_CHEST.get(), SCContent.STORAGE_MODULE.get());
		addModuleRecipe(consumer, Items.PAPER, SCContent.WHITELIST_MODULE.get());
		addMossyRecipe(consumer, SCTags.Items.REINFORCED_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE.get());
		addMossyRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICKS.get());
		addPillarRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get());
		addPillarRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
		addPillarRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_ACACIA_LOGS, SCContent.REINFORCED_ACACIA_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_BIRCH_LOGS, SCContent.REINFORCED_BIRCH_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_DARK_OAK_LOGS, SCContent.REINFORCED_DARK_OAK_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_JUNGLE_LOGS, SCContent.REINFORCED_JUNGLE_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_OAK_LOGS, SCContent.REINFORCED_OAK_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_SPRUCE_LOGS, SCContent.REINFORCED_SPRUCE_PLANKS.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICKS.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_RED_SAND.get(), SCContent.REINFORCED_RED_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_SAND.get(), SCContent.REINFORCED_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICKS.get());
		addSecretSignRecipe(consumer, Items.ACACIA_SIGN, SCContent.SECRET_ACACIA_SIGN.get());
		addSecretSignRecipe(consumer, Items.BIRCH_SIGN, SCContent.SECRET_BIRCH_SIGN.get());
		addSecretSignRecipe(consumer, Items.DARK_OAK_SIGN, SCContent.SECRET_DARK_OAK_SIGN.get());
		addSecretSignRecipe(consumer, Items.JUNGLE_SIGN, SCContent.SECRET_JUNGLE_SIGN.get());
		addSecretSignRecipe(consumer, Items.OAK_SIGN, SCContent.SECRET_OAK_SIGN.get());
		addSecretSignRecipe(consumer, Items.SPRUCE_SIGN, SCContent.SECRET_SPRUCE_SIGN.get());
		addSlabRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CUT_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_NORMAL_STONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_STONE.get(), SCContent.REINFORCED_SMOOTH_STONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_SLAB.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_STAINED_GLASS.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_STAINED_GLASS.get(), SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_STAINED_GLASS.get(), SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_STAINED_GLASS.get(), SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_TERRACOTTA.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.STAIRS_CRYSTAL_QUARTZ.get());

		//furnace recipes
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_COBBLESTONE.get()), SCContent.REINFORCED_STONE.get(), 0.1F, 200)
		.addCriterion("has_reinforced_cobblestone", hasItem(SCContent.REINFORCED_COBBLESTONE.get()))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_QUARTZ.get()), SCContent.REINFORCED_SMOOTH_QUARTZ.get(), 0.1F, 200)
		.addCriterion("has_reinforced_quartz", hasItem(SCContent.REINFORCED_QUARTZ.get()))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_RED_SANDSTONE.get()), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), 0.1F, 200)
		.addCriterion("has_reinforced_red_sandstone", hasItem(SCContent.REINFORCED_RED_SANDSTONE.get()))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_SAND.get()), SCContent.REINFORCED_GLASS.get(), 0.1F, 200)
		.addCriterion("has_reinforced_sand", hasItem(SCContent.REINFORCED_SAND.get()))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_SANDSTONE.get()), SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), 0.1F, 200)
		.addCriterion("has_reinforced_sandstone", hasItem(SCContent.REINFORCED_SANDSTONE.get()))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_STONE.get()), SCContent.REINFORCED_SMOOTH_STONE.get(), 0.1F, 200)
		.addCriterion("has_reinforced_stone", hasItem(SCContent.REINFORCED_STONE.get()))
		.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SCContent.REINFORCED_STONE_BRICKS.get()), SCContent.REINFORCED_CRACKED_STONE_BRICKS.get(), 0.1F, 200)
		.addCriterion("has_reinforced_stone_bricks", hasItem(SCContent.REINFORCED_STONE_BRICKS.get()))
		.build(consumer);
	}

	protected final void addBarkRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider log, IItemProvider result) //woof
	{
		ShapedRecipeBuilder.shapedRecipe(result, 3)
		.setGroup("securitycraft:bark")
		.patternLine("LL")
		.patternLine("LL")
		.key('L', log)
		.addCriterion("has_log", hasItem(log))
		.build(consumer);
	}

	protected final void addBlockMineRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.setGroup("securitycraft:block_mines")
		.addIngredient(input)
		.addIngredient(SCContent.MINE.get())
		.addCriterion("has_mine", hasItem(SCContent.MINE.get()))
		.build(consumer);
	}

	protected final void addBlockMineRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> input, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.setGroup("securitycraft:block_mines")
		.addIngredient(input)
		.addIngredient(SCContent.MINE.get())
		.addCriterion("has_mine", hasItem(SCContent.MINE.get()))
		.build(consumer);
	}

	protected final void addCarpetRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider wool, IItemProvider carpet)
	{
		ShapedRecipeBuilder.shapedRecipe(carpet, 3)
		.setGroup("securitycraft:reinforced_carpets")
		.patternLine("WW")
		.key('W', wool)
		.addCriterion("has_wool", hasItem(SCTags.Items.REINFORCED_WOOL))
		.build(consumer);
	}

	protected final void addChiselingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider slab, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result)
		.patternLine("S")
		.patternLine("S")
		.key('S', slab)
		.addCriterion("has_slab", hasItem(slab))
		.build(consumer);
	}

	protected final void addColoredWoolRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.addIngredient(dye)
		.addIngredient(SCContent.REINFORCED_WHITE_WOOL.get())
		.addCriterion("has_wool", hasItem(SCContent.REINFORCED_WHITE_WOOL.get()))
		.build(consumer);
	}

	protected final void addKeycardRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> specialIngredient, IItemProvider result, ICondition condition)
	{
		ShapedRecipeBuilder.Result recipe;
		Map<Character, Ingredient> key = Maps.newLinkedHashMap();
		Item resultItem = result.asItem();
		ResourceLocation id = resultItem.getRegistryName();

		key.put('I', Ingredient.fromTag(Tags.Items.INGOTS_IRON));
		key.put('S', Ingredient.fromTag(specialIngredient));
		recipe = new ShapedRecipeBuilder(Items.AIR, 0).new Result(id,
				resultItem, 1, "securitycraft:keycards",
				Arrays.asList(new String[] {"III", "SSS"}), key,
				Advancement.Builder.builder().withCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)),
				new ResourceLocation(id.getNamespace(), "recipes/" + resultItem.getGroup().getPath() + "/" + id.getPath()));
		ConditionalRecipe.builder().addCondition(condition).addRecipe(recipe).build(consumer, id);
	}

	protected final void addModuleRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider specialIngredient, IItemProvider result)
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

	protected final void addModuleRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> specialIngredient, IItemProvider result)
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

	protected final void addMossyRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> block, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.addIngredient(block)
		.addIngredient(Items.VINE)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected final void addMossyRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result)
		.addIngredient(block)
		.addIngredient(Items.VINE)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected final void addPillarRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 2)
		.patternLine("B")
		.patternLine("B")
		.key('B', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected final void addPlanksRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> log, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result, 4)
		.addIngredient(log)
		.addCriterion("has_log", hasItem(log))
		.build(consumer);
	}

	protected final void addPolishingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 4)
		.patternLine("BB")
		.patternLine("BB")
		.key('B', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected final void addPressurePlateRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result)
		.setGroup("securitycraft:reinforced_pressure_plates")
		.patternLine("SS")
		.key('S', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected final void addSecretSignRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider vanillaSign, IItemProvider result)
	{
		ShapelessRecipeBuilder.shapelessRecipe(result, 3)
		.setGroup("securitycraft:secret_signs")
		.addIngredient(vanillaSign, 3)
		.addIngredient(SCContent.RETINAL_SCANNER.get())
		.addCriterion("has_sign", hasItem(ItemTags.SIGNS))
		.build(consumer);
	}

	protected final void addSlabRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 6)
		.setGroup("securitycraft:slabs")
		.patternLine("BBB")
		.key('B', block)
		.addCriterion("has_block", hasItem(block))
		.build(consumer);
	}

	protected final void addStainedGlassRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 8)
		.setGroup("securitycraft:reinforced_glass")
		.patternLine("GGG")
		.patternLine("GDG")
		.patternLine("GGG")
		.key('G', SCContent.REINFORCED_GLASS.get())
		.key('D', dye)
		.addCriterion("has_glass", hasItem(Tags.Items.GLASS))
		.build(consumer);
	}

	protected final void addStainedGlassPaneRecipes(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider stainedGlass, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 8)
		.setGroup("securitycraft:reinforced_glass_panes")
		.patternLine("GGG")
		.patternLine("GDG")
		.patternLine("GGG")
		.key('G', SCContent.REINFORCED_GLASS_PANE.get())
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

	protected final void addStainedTerracottaRecipe(Consumer<IFinishedRecipe> consumer, Tag<Item> dye, IItemProvider result)
	{
		ShapedRecipeBuilder.shapedRecipe(result, 8)
		.setGroup("securitycraft:reinforced_terracotta")
		.patternLine("TTT")
		.patternLine("TDT")
		.patternLine("TTT")
		.key('T', SCContent.REINFORCED_TERRACOTTA.get())
		.key('D', dye)
		.addCriterion("has_reinforced_terracotta", hasItem(SCContent.REINFORCED_TERRACOTTA.get()))
		.build(consumer);
	}

	protected final void addStairsRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider result)
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
	private static class CustomNBTIngredient extends NBTIngredient
	{
		public CustomNBTIngredient(ItemStack stack)
		{
			super(stack);
		}
	}
}
