package net.geforcemods.securitycraft.datagen;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
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
	protected final void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
	{
		ItemStack healingStack = new ItemStack(Items.POTION);
		ItemStack strongHealingStack = new ItemStack(Items.POTION);
		ItemStack harmingStack = new ItemStack(Items.POTION);
		ItemStack strongHarmingStack = new ItemStack(Items.POTION);
		CompoundTag healingNBT = new CompoundTag();
		CompoundTag strongHealingNBT = new CompoundTag();
		CompoundTag harmingNBT = new CompoundTag();
		CompoundTag strongHarmingNBT = new CompoundTag();

		healingNBT.putString("Potion", Potions.HEALING.getRegistryName().toString());
		strongHealingNBT.putString("Potion", Potions.STRONG_HEALING.getRegistryName().toString());
		harmingNBT.putString("Potion", Potions.HARMING.getRegistryName().toString());
		strongHarmingNBT.putString("Potion", Potions.STRONG_HARMING.getRegistryName().toString());
		healingStack.setTag(healingNBT);
		strongHealingStack.setTag(strongHealingNBT);
		harmingStack.setTag(harmingNBT);
		strongHarmingStack.setTag(strongHarmingNBT);

		//combine keycard with limited use keycard to get keycards with the a configurable limited amount of uses
		SpecialRecipeBuilder.special(LimitedUseKeycardRecipe.serializer).save(consumer, "limited_use_keycards");

		//shaped recipes
		ShapedRecipeBuilder.shaped(SCContent.ALARM.get())
		.pattern("GGG")
		.pattern("GNG")
		.pattern("GRG")
		.define('G', SCContent.REINFORCED_GLASS.get())
		.define('N', Blocks.NOTE_BLOCK)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.BLOCK_POCKET_MANAGER.get())
		.pattern("CIC")
		.pattern("IRI")
		.pattern("CIC")
		.define('C', SCContent.REINFORCED_CRYSTAL_QUARTZ.get())
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.unlockedBy("has_redstone_block", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.BOUNCING_BETTY.get())
		.pattern(" P ")
		.pattern("IGI")
		.define('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.BRIEFCASE.get())
		.pattern("SSS")
		.pattern("ICI")
		.pattern("III")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('C', SCContent.KEYPAD_CHEST.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.FAKE_LAVA_BUCKET.get())
		.group("securitycraft:fake_liquids")
		.pattern("P")
		.pattern("B")
		.define('P', new CustomNBTIngredient(healingStack))
		.define('B', Items.LAVA_BUCKET)
		.unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_lava_normal"));
		ShapedRecipeBuilder.shaped(SCContent.FAKE_LAVA_BUCKET.get())
		.group("securitycraft:fake_liquids")
		.pattern("P")
		.pattern("B")
		.define('P', new CustomNBTIngredient(strongHealingStack))
		.define('B', Items.LAVA_BUCKET)
		.unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_lava_strong"));
		ShapedRecipeBuilder.shaped(SCContent.FAKE_WATER_BUCKET.get())
		.group("securitycraft:fake_liquids")
		.pattern("P")
		.pattern("B")
		.define('P', new CustomNBTIngredient(harmingStack))
		.define('B', Items.WATER_BUCKET)
		.unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_water_normal"));
		ShapedRecipeBuilder.shaped(SCContent.FAKE_WATER_BUCKET.get())
		.group("securitycraft:fake_liquids")
		.pattern("P")
		.pattern("B")
		.define('P', new CustomNBTIngredient(strongHarmingStack))
		.define('B', Items.WATER_BUCKET)
		.unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, "bucket_f_water_strong"));
		ShapedRecipeBuilder.shaped(SCContent.CAGE_TRAP.get())
		.pattern("BBB")
		.pattern("GRG")
		.pattern("III")
		.define('B', SCContent.REINFORCED_IRON_BARS.get())
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.CAMERA_MONITOR.get())
		.pattern("III")
		.pattern("IGI")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.CLAYMORE.get())
		.pattern("HSH")
		.pattern("SBS")
		.pattern("RGR")
		.define('H', Blocks.TRIPWIRE_HOOK)
		.define('S', Tags.Items.STRING)
		.define('B', SCContent.BOUNCING_BETTY.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.CODEBREAKER.get())
		.pattern("DTD")
		.pattern("GSG")
		.pattern("RER")
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('T', Items.REDSTONE_TORCH)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('S', Tags.Items.NETHER_STARS)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('E', Tags.Items.GEMS_EMERALD)
		.unlockedBy("has_nether_star", has(Tags.Items.NETHER_STARS))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.CRYSTAL_QUARTZ_ITEM.get(), 9)
		.pattern("CQC")
		.pattern("QCQ")
		.pattern("CQC")
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.define('C', Tags.Items.GEMS_PRISMARINE)
		.unlockedBy("has_prismarine_crystals", has(Tags.Items.GEMS_PRISMARINE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.CRYSTAL_QUARTZ.get())
		.pattern("CC")
		.pattern("CC")
		.define('C', SCContent.CRYSTAL_QUARTZ_ITEM.get())
		.unlockedBy("has_crystal_quartz_item", has(SCContent.CRYSTAL_QUARTZ_ITEM.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_DOOR_ITEM.get())
		.pattern("III")
		.pattern("IDI")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('D', Items.IRON_DOOR)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.IRON_FENCE.get())
		.pattern(" I ")
		.pattern("IFI")
		.pattern(" I ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', ItemTags.WOODEN_FENCES)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.IMS.get())
		.pattern("BPB")
		.pattern(" I ")
		.pattern("B B")
		.define('B', SCContent.BOUNCING_BETTY.get())
		.define('P', SCContent.PORTABLE_RADAR.get())
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_radar", has(SCContent.PORTABLE_RADAR.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.INVENTORY_SCANNER.get())
		.pattern("SSS")
		.pattern("SLS")
		.pattern("SCS")
		.define('S', SCTags.Items.REINFORCED_STONE)
		.define('L', SCContent.LASER_BLOCK.get())
		.define('C', Tags.Items.CHESTS_ENDER)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.KEYCARD_READER.get())
		.pattern("SSS")
		.pattern("SHS")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE)
		.define('H', Items.HOPPER)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.FRAME.get())
		.pattern("III")
		.pattern("IR ")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.KEY_PANEL.get())
		.pattern("BBB")
		.pattern("BPB")
		.pattern("BBB")
		.define('B', Items.STONE_BUTTON)
		.define('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.unlockedBy("has_stone_button", has(Items.STONE_BUTTON))
		.save(consumer);
		//don't change these to reinforced, because the block reinforcer needs a laser block!!!
		ShapedRecipeBuilder.shaped(SCContent.LASER_BLOCK.get())
		.pattern("SSS")
		.pattern("SRS")
		.pattern("SGS")
		.define('S', Tags.Items.STONE)
		.define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.define('G', Tags.Items.GLASS_PANES_COLORLESS)
		.unlockedBy("has_stone", has(Tags.Items.STONE))
		.save(consumer);
		//k you can change again :)
		ShapedRecipeBuilder.shaped(SCContent.MINE.get(), 3)
		.pattern(" I ")
		.pattern("IGI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.MOTION_ACTIVATED_LIGHT.get())
		.pattern("L")
		.pattern("R")
		.pattern("S")
		.define('L', Blocks.REDSTONE_LAMP)
		.define('R', SCContent.PORTABLE_RADAR.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.PANIC_BUTTON.get())
		.pattern(" I ")
		.pattern("IBI")
		.pattern(" R ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('B', Items.STONE_BUTTON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.PORTABLE_RADAR.get())
		.pattern("III")
		.pattern("ITI")
		.pattern("IRI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('T', Items.REDSTONE_TORCH)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.PROJECTOR.get())
		.pattern("III")
		.pattern("BLP")
		.pattern("I I")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.define('L', SCContent.REINFORCED_REDSTONE_LAMP.get())
		.define('P', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_redstone_lamp", has(SCContent.REINFORCED_REDSTONE_LAMP.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.PROTECTO.get())
		.pattern("ODO")
		.pattern("OEO")
		.pattern("OOO")
		.define('O', SCContent.REINFORCED_OBSIDIAN.get())
		.define('D', Blocks.DAYLIGHT_DETECTOR)
		.define('E', Items.ENDER_EYE)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_BOOKSHELF.get())
		.pattern("PPP")
		.pattern("BBB")
		.pattern("PPP")
		.define('B', Items.BOOK)
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.unlockedBy("has_book", has(Items.BOOK))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_COARSE_DIRT.get(), 4)
		.pattern("DG")
		.pattern("GD")
		.define('D', SCContent.REINFORCED_DIRT.get())
		.define('G', SCContent.REINFORCED_GRAVEL.get())
		.unlockedBy("has_reinforced_gravel", has(SCContent.REINFORCED_GRAVEL.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_DIORITE.get(), 2)
		.pattern("CQ")
		.pattern("QC")
		.define('C', SCContent.REINFORCED_COBBLESTONE.get())
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_FENCEGATE.get())
		.pattern(" I ")
		.pattern("IGI")
		.pattern(" I ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.FENCE_GATES_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_LEVER.get())
		.pattern("S")
		.pattern("C")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('C', SCTags.Items.REINFORCED_COBBLESTONE)
		.unlockedBy("has_cobble", has(SCContent.REINFORCED_COBBLESTONE.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_OBSERVER.get())
		.pattern("CCC")
		.pattern("RRQ")
		.pattern("CCC")
		.define('C', SCTags.Items.REINFORCED_COBBLESTONE)
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_quartz", has(Tags.Items.GEMS_QUARTZ))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_REDSTONE_LAMP.get())
		.pattern(" R ")
		.pattern("RGR")
		.pattern(" R ")
		.define('G', SCContent.REINFORCED_GLOWSTONE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get());
		addPressurePlateRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get());
		ShapedRecipeBuilder.shaped(SCContent.REMOTE_ACCESS_MINE.get())
		.pattern(" T ")
		.pattern(" DG")
		.pattern("S  ")
		.define('T', Items.REDSTONE_TORCH)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_mine", has(SCContent.MINE.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REMOTE_ACCESS_SENTRY.get())
		.pattern("ITI")
		.pattern("IDI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('T', Items.REDSTONE_TORCH)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_sentry", has(SCContent.SENTRY.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.RETINAL_SCANNER.get())
		.pattern("SSS")
		.pattern("SES")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE)
		.define('E', Items.ENDER_EYE)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.SECURITY_CAMERA.get())
		.pattern("III")
		.pattern("GRI")
		.pattern("IIS")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCContent.REINFORCED_GLASS.get())
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.SENTRY.get())
		.pattern("RDR")
		.pattern("IPI")
		.pattern("BBB")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('D', Blocks.DISPENSER)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', SCContent.PORTABLE_RADAR.get())
		.define('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.TASER.get())
		.pattern("BGI")
		.pattern("RSG")
		.pattern("  S")
		.define('B', Items.BOW)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.TRACK_MINE.get(), 4)
		.pattern("I I")
		.pattern("ISI")
		.pattern("IGI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('S', Tags.Items.RODS_WOODEN)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.TROPHY_SYSTEM.get())
		.pattern(" T ")
		.pattern(" B ")
		.pattern("S S")
		.define('T', SCContent.SENTRY.get())
		.define('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
		.pattern(" RE")
		.pattern(" IR")
		.pattern("I  ")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('E', Tags.Items.GEMS_EMERALD)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get())
		.group("securitycraft:universal_block_reinforcer")
		.pattern(" DG")
		.pattern("RLD")
		.pattern("SR ")
		.define('G', Tags.Items.GLASS_COLORLESS)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get())
		.group("securitycraft:universal_block_reinforcer")
		.pattern(" DG")
		.pattern("RLD")
		.pattern("SR ")
		.define('G', SCContent.REINFORCED_BLACK_STAINED_GLASS.get())
		.define('D', SCContent.REINFORCED_DIAMOND_BLOCK.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
		.group("securitycraft:universal_block_reinforcer")
		.pattern(" EG")
		.pattern("RNE")
		.pattern("SR ")
		.define('G', SCContent.REINFORCED_PINK_STAINED_GLASS.get())
		.define('E', SCContent.REINFORCED_EMERALD_BLOCK.get())
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.define('N', Tags.Items.NETHER_STARS)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.UNIVERSAL_BLOCK_REMOVER.get())
		.pattern("SII")
		.define('S', Items.SHEARS)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.UNIVERSAL_KEY_CHANGER.get())
		.pattern(" RL")
		.pattern(" IR")
		.pattern("I  ")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK.get())
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.USERNAME_LOGGER.get())
		.pattern("SPS")
		.pattern("SRS")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE)
		.define('P', SCContent.PORTABLE_RADAR.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.WIRE_CUTTERS.get())
		.pattern("SI ")
		.pattern("I I")
		.pattern(" I ")
		.define('S', Items.SHEARS)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_mine", has(SCContent.MINE.get()))
		.save(consumer);
		ShapedRecipeBuilder.shaped(SCContent.REINFORCED_GLASS_PANE.get())
		.pattern("GGG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS.get())
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(consumer);

		//shapeless recipes
		ShapelessRecipeBuilder.shapeless(SCContent.BLOCK_POCKET_WALL.get())
		.requires(SCContent.REINFORCED_CRYSTAL_QUARTZ.get())
		.unlockedBy("has_reinforced_crystal_quartz", has(SCContent.REINFORCED_CRYSTAL_QUARTZ.get()))
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.KEYPAD_DOOR_ITEM.get())
		.requires(SCContent.REINFORCED_DOOR_ITEM.get())
		.requires(SCContent.KEYPAD.get())
		.unlockedBy("has_reinforced_door", has(SCContent.REINFORCED_DOOR_ITEM.get()))
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.REINFORCED_ANDESITE.get(), 2)
		.requires(SCContent.REINFORCED_DIORITE.get())
		.requires(SCTags.Items.REINFORCED_COBBLESTONE)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONE))
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.REINFORCED_CRYSTAL_QUARTZ.get())
		.requires(SCContent.BLOCK_POCKET_WALL.get())
		.unlockedBy("has_block_pocket_wall", has(SCContent.BLOCK_POCKET_WALL.get()))
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.REINFORCED_GRANITE.get())
		.requires(SCContent.REINFORCED_DIORITE.get())
		.requires(Tags.Items.GEMS_QUARTZ)
		.unlockedBy("has_quartz", has(Tags.Items.GEMS_QUARTZ))
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.SC_MANUAL.get())
		.requires(Items.BOOK)
		.requires(Blocks.IRON_BARS)
		.unlockedBy("has_wood", has(ItemTags.LOGS)) //the thought behind this is that the recipe will be given right after the player chopped their first piece of wood
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.SCANNER_DOOR_ITEM.get())
		.requires(SCContent.REINFORCED_DOOR_ITEM.get())
		.requires(SCContent.RETINAL_SCANNER.get())
		.unlockedBy("has_reinforced_door", has(SCContent.REINFORCED_DOOR_ITEM.get()))
		.save(consumer);
		ShapelessRecipeBuilder.shapeless(SCContent.UNIVERSAL_OWNER_CHANGER.get())
		.requires(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
		.requires(Items.NAME_TAG)
		.unlockedBy("has_name_tag", has(Items.NAME_TAG))
		.save(consumer);

		//template recipes
		addBarkRecipe(consumer, SCContent.REINFORCED_ACACIA_LOG.get(), SCContent.REINFORCED_ACACIA_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_BIRCH_LOG.get(), SCContent.REINFORCED_BIRCH_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_CRIMSON_STEM.get(), SCContent.REINFORCED_CRIMSON_HYPHAE.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_DARK_OAK_LOG.get(), SCContent.REINFORCED_DARK_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_JUNGLE_LOG.get(), SCContent.REINFORCED_JUNGLE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_OAK_LOG.get(), SCContent.REINFORCED_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_SPRUCE_LOG.get(), SCContent.REINFORCED_SPRUCE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_WARPED_STEM.get(), SCContent.REINFORCED_WARPED_HYPHAE.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(), SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(), SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_CRIMSON_STEM.get(), SCContent.REINFORCED_STRIPPED_CRIMSON_HYPHAE.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(), SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(), SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(), SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_OAK_LOG.get(), SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		addBarkRecipe(consumer, SCContent.REINFORCED_STRIPPED_WARPED_STEM.get(), SCContent.REINFORCED_STRIPPED_WARPED_HYPHAE.get());
		addBlockMineRecipe(consumer, Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE.get());
		addBlockMineRecipe(consumer, Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_COAL_ORE, SCContent.DEEPSLATE_COAL_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_DIAMOND_ORE, SCContent.DEEPSLATE_DIAMOND_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DIRT, SCContent.DIRT_MINE.get());
		addBlockMineRecipe(consumer, Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_EMERALD_ORE, SCContent.DEEPSLATE_EMERALD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.FURNACE, SCContent.FURNACE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.GRAVEL, SCContent.GRAVEL_MINE.get());
		addBlockMineRecipe(consumer, Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_GOLD_ORE, SCContent.DEEPSLATE_GOLD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.GILDED_BLACKSTONE, SCContent.GILDED_BLACKSTONE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.IRON_ORE, SCContent.IRON_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_IRON_ORE, SCContent.DEEPSLATE_IRON_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_LAPIS_ORE, SCContent.DEEPSLATE_LAPIS_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.NETHER_GOLD_ORE, SCContent.NETHER_GOLD_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_REDSTONE_ORE, SCContent.DEEPSLATE_REDSTONE_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.COPPER_ORE, SCContent.COPPER_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.DEEPSLATE_COPPER_ORE, SCContent.DEEPSLATE_COPPER_ORE_MINE.get());
		addBlockMineRecipe(consumer, Blocks.SAND, SCContent.SAND_MINE.get());
		addBlockMineRecipe(consumer, Blocks.STONE, SCContent.STONE_MINE.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_BUTTON.get());
		addButtonRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get());
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
		addChiselingRecipe(consumer, SCContent.REINFORCED_NETHER_BRICK_SLAB.get(), SCContent.REINFORCED_CHISELED_NETHER_BRICKS.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_QUARTZ_SLAB.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get());
		addChiselingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get(), SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get());
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
		addCompressingRecipe(consumer, SCContent.REINFORCED_ICE.get(), SCContent.REINFORCED_PACKED_ICE.get());
		addCompressingRecipe(consumer, SCContent.REINFORCED_PACKED_ICE.get(), SCContent.REINFORCED_BLUE_ICE.get());
		addKeycardRecipe(consumer, Tags.Items.INGOTS_GOLD, SCContent.KEYCARD_LVL_1.get());
		addKeycardRecipe(consumer, Tags.Items.INGOTS_BRICK, SCContent.KEYCARD_LVL_2.get());
		addKeycardRecipe(consumer, Tags.Items.INGOTS_NETHER_BRICK, SCContent.KEYCARD_LVL_3.get());
		addKeycardRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.KEYCARD_LVL_4.get());
		addKeycardRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.KEYCARD_LVL_5.get());
		addKeycardRecipe(consumer, Tags.Items.GEMS_LAPIS, SCContent.LIMITED_USE_KEYCARD.get());
		//recipes to reset linked keycards
		addKeycardResetRecipe(consumer, SCContent.KEYCARD_LVL_1.get());
		addKeycardResetRecipe(consumer, SCContent.KEYCARD_LVL_2.get());
		addKeycardResetRecipe(consumer, SCContent.KEYCARD_LVL_3.get());
		addKeycardResetRecipe(consumer, SCContent.KEYCARD_LVL_4.get());
		addKeycardResetRecipe(consumer, SCContent.KEYCARD_LVL_5.get());
		addModuleRecipe(consumer, Items.INK_SAC, SCContent.DENYLIST_MODULE.get());
		addModuleRecipe(consumer, Items.PAINTING, SCContent.DISGUISE_MODULE.get());
		addModuleRecipe(consumer, ItemTags.ARROWS, SCContent.HARMING_MODULE.get());
		addModuleRecipe(consumer, Tags.Items.DUSTS_REDSTONE, SCContent.REDSTONE_MODULE.get());
		addModuleRecipe(consumer, Tags.Items.ENDER_PEARLS, SCContent.SMART_MODULE.get());
		addModuleRecipe(consumer, SCContent.KEYPAD_CHEST.get(), SCContent.STORAGE_MODULE.get());
		addModuleRecipe(consumer, Items.PAPER, SCContent.ALLOWLIST_MODULE.get());
		addModuleRecipe(consumer, Items.SUGAR, SCContent.SPEED_MODULE.get());
		addMossyRecipe(consumer, SCTags.Items.REINFORCED_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE.get());
		addMossyRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICKS.get());
		addPillarRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get());
		addPillarRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
		addPillarRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_ACACIA_LOGS, SCContent.REINFORCED_ACACIA_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_BIRCH_LOGS, SCContent.REINFORCED_BIRCH_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_CRIMSON_STEMS, SCContent.REINFORCED_CRIMSON_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_DARK_OAK_LOGS, SCContent.REINFORCED_DARK_OAK_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_JUNGLE_LOGS, SCContent.REINFORCED_JUNGLE_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_OAK_LOGS, SCContent.REINFORCED_OAK_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_SPRUCE_LOGS, SCContent.REINFORCED_SPRUCE_PLANKS.get());
		addPlanksRecipe(consumer, SCTags.Items.REINFORCED_WARPED_STEMS, SCContent.REINFORCED_WARPED_PLANKS.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_BASALT.get(), SCContent.REINFORCED_POLISHED_BASALT.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICKS.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_BRICKS.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_RED_SAND.get(), SCContent.REINFORCED_RED_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_SAND.get(), SCContent.REINFORCED_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE.get());
		addPolishingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICKS.get());
		addSecretSignRecipe(consumer, Items.ACACIA_SIGN, SCContent.SECRET_ACACIA_SIGN.get());
		addSecretSignRecipe(consumer, Items.BIRCH_SIGN, SCContent.SECRET_BIRCH_SIGN.get());
		addSecretSignRecipe(consumer, Items.CRIMSON_SIGN, SCContent.SECRET_CRIMSON_SIGN.get());
		addSecretSignRecipe(consumer, Items.DARK_OAK_SIGN, SCContent.SECRET_DARK_OAK_SIGN.get());
		addSecretSignRecipe(consumer, Items.JUNGLE_SIGN, SCContent.SECRET_JUNGLE_SIGN.get());
		addSecretSignRecipe(consumer, Items.OAK_SIGN, SCContent.SECRET_OAK_SIGN.get());
		addSecretSignRecipe(consumer, Items.SPRUCE_SIGN, SCContent.SECRET_SPRUCE_SIGN.get());
		addSecretSignRecipe(consumer, Items.WARPED_SIGN, SCContent.SECRET_WARPED_SIGN.get());
		addSlabRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CUT_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SMOOTH_STONE.get(), SCContent.REINFORCED_SMOOTH_STONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_NORMAL_STONE_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get());
		addSlabRecipe(consumer, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_SLAB.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get());
		addStainedGlassRecipe(consumer, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get());
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
		addStainedGlassPaneRecipes(consumer, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get());
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
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_TERRACOTTA.get());
		addStainedTerracottaRecipe(consumer, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_TERRACOTTA.get());
		addStairsRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.STAIRS_CRYSTAL_QUARTZ.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get());
		addStairsRecipe(consumer, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_STAIRS.get());
		addWallRecipes(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_WALL.get());
		addWallRecipes(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_WALL.get());

		//furnace recipes
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_CLAY.get()), SCContent.REINFORCED_TERRACOTTA.get(), 0.35F, 200)
		.unlockedBy("has_reinforced_clay", has(SCContent.REINFORCED_CLAY.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_COBBLESTONE.get()), SCContent.REINFORCED_STONE.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_cobblestone", has(SCContent.REINFORCED_COBBLESTONE.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_NETHER_BRICKS.get()), SCContent.REINFORCED_CRACKED_NETHER_BRICKS.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_nether_bricks", has(SCContent.REINFORCED_NETHER_BRICKS.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_QUARTZ.get()), SCContent.REINFORCED_SMOOTH_QUARTZ.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_quartz", has(SCContent.REINFORCED_QUARTZ.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get()), SCContent.REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_blackstone_bricks", has(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_RED_SANDSTONE.get()), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_red_sandstone", has(SCContent.REINFORCED_RED_SANDSTONE.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCTags.Items.REINFORCED_SAND), SCContent.REINFORCED_GLASS.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_sand", has(SCContent.REINFORCED_SAND.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_SANDSTONE.get()), SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_sandstone", has(SCContent.REINFORCED_SANDSTONE.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_STONE.get()), SCContent.REINFORCED_SMOOTH_STONE.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_stone", has(SCContent.REINFORCED_STONE.get()))
		.save(consumer);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(SCContent.REINFORCED_STONE_BRICKS.get()), SCContent.REINFORCED_CRACKED_STONE_BRICKS.get(), 0.1F, 200)
		.unlockedBy("has_reinforced_stone_bricks", has(SCContent.REINFORCED_STONE_BRICKS.get()))
		.save(consumer);

		//stonecutter recipes (ordered by the ingredient's sort in creative building tab)
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_CHISELED_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_NORMAL_STONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_STONE.get(), SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BASALT.get(), SCContent.REINFORCED_POLISHED_BASALT.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_CHISELED_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CHISELED_SANDSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_CUT_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CHISELED_RED_SANDSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_QUARTZ.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_PILLAR.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_CHISELED_NETHER_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.STAIRS_CRYSTAL_QUARTZ.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CHISELED_CRYSTAL_QUARTZ.get(), 1);
		addStonecuttingRecipe(consumer, SCContent.CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get(), 1);
	}

	protected final void addBarkRecipe(Consumer<FinishedRecipe> consumer, ItemLike log, ItemLike result) //woof
	{
		ShapedRecipeBuilder.shaped(result, 3)
		.group("securitycraft:bark")
		.pattern("LL")
		.pattern("LL")
		.define('L', log)
		.unlockedBy("has_log", has(log))
		.save(consumer);
	}

	protected final void addBlockMineRecipe(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result)
		.group("securitycraft:block_mines")
		.requires(input)
		.requires(SCContent.MINE.get())
		.unlockedBy("has_mine", has(SCContent.MINE.get()))
		.save(consumer);
	}

	protected final void addButtonRecipe(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result)
		.group("securitycraft:reinforced_buttons")
		.requires(input)
		.unlockedBy("has_block", has(input))
		.save(consumer);
	}

	protected final void addCarpetRecipe(Consumer<FinishedRecipe> consumer, ItemLike wool, ItemLike carpet)
	{
		ShapedRecipeBuilder.shaped(carpet, 3)
		.group("securitycraft:reinforced_carpets")
		.pattern("WW")
		.define('W', wool)
		.unlockedBy("has_wool", has(SCTags.Items.REINFORCED_WOOL))
		.save(consumer);
	}

	protected final void addChiselingRecipe(Consumer<FinishedRecipe> consumer, ItemLike slab, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result)
		.pattern("S")
		.pattern("S")
		.define('S', slab)
		.unlockedBy("has_slab", has(slab))
		.save(consumer);
	}

	protected final void addColoredWoolRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> dye, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result)
		.group("securitycraft:reinforced_wool")
		.requires(dye)
		.requires(SCContent.REINFORCED_WHITE_WOOL.get())
		.unlockedBy("has_wool", has(SCContent.REINFORCED_WHITE_WOOL.get()))
		.save(consumer);
	}

	protected final void addCompressingRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result)
		.pattern("BBB")
		.pattern("BBB")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addKeycardRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> specialIngredient, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result)
		.group("securitycraft:keycards")
		.pattern("III")
		.pattern("SSS")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
	}

	protected final void addKeycardResetRecipe(Consumer<FinishedRecipe> consumer, ItemLike keycard)
	{
		ShapelessRecipeBuilder.shapeless(keycard)
		.requires(keycard)
		.unlockedBy("has_keycard", has(keycard))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, keycard.asItem().getRegistryName().getPath() + "_reset"));
	}

	protected final void addModuleRecipe(Consumer<FinishedRecipe> consumer, ItemLike specialIngredient, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result)
		.group("securitycraft:modules")
		.pattern("III")
		.pattern("IPI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', Items.PAPER)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
	}

	protected final void addModuleRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> specialIngredient, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result)
		.group("securitycraft:modules")
		.pattern("III")
		.pattern("IPI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', Items.PAPER)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(consumer);
	}

	protected final void addMossyRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> block, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result)
		.requires(block)
		.requires(Items.VINE)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addMossyRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result)
		.requires(block)
		.requires(Items.VINE)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addPillarRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 2)
		.pattern("B")
		.pattern("B")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addPlanksRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> log, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result, 4)
		.group("securitycraft:reinforced_planks")
		.requires(log)
		.unlockedBy("has_log", has(log))
		.save(consumer);
	}

	protected final void addPolishingRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 4)
		.pattern("BB")
		.pattern("BB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addPressurePlateRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result)
		.group("securitycraft:reinforced_pressure_plates")
		.pattern("SS")
		.define('S', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addSecretSignRecipe(Consumer<FinishedRecipe> consumer, ItemLike vanillaSign, ItemLike result)
	{
		ShapelessRecipeBuilder.shapeless(result, 3)
		.group("securitycraft:secret_signs")
		.requires(vanillaSign, 3)
		.requires(SCContent.RETINAL_SCANNER.get())
		.unlockedBy("has_sign", has(ItemTags.SIGNS))
		.save(consumer);
	}

	protected final void addShapelessConditionalRecipe(Consumer<FinishedRecipe> consumer, ItemLike result, int amount, String group, List<ItemLike> ingredients, AbstractCriterionTriggerInstance criterion, ICondition condition)
	{
		ShapelessRecipeBuilder.Result recipe;
		Item resultItem = result.asItem();
		ResourceLocation id = resultItem.getRegistryName();

		recipe = new ShapelessRecipeBuilder.Result(id,
				resultItem, amount, group, ingredients.stream().map(item -> Ingredient.of(item)).collect(Collectors.toList()),
				Advancement.Builder.advancement().addCriterion("has_item", criterion),
				new ResourceLocation(id.getNamespace(), "recipes/" + resultItem.getItemCategory().getRecipeFolderName() + "/" + id.getPath()));
		ConditionalRecipe.builder().addCondition(condition).addRecipe(recipe).build(consumer, id);
	}

	protected final void addSlabRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 6)
		.group("securitycraft:slabs")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addStainedGlassRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> dye, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 8)
		.group("securitycraft:reinforced_glass")
		.pattern("GGG")
		.pattern("GDG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS.get())
		.define('D', dye)
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(consumer);
	}

	protected final void addStainedGlassPaneRecipes(Consumer<FinishedRecipe> consumer, Tag<Item> dye, ItemLike stainedGlass, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 8)
		.group("securitycraft:reinforced_glass_panes")
		.pattern("GGG")
		.pattern("GDG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS_PANE.get())
		.define('D', dye)
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, result.asItem().getRegistryName().getPath() + "_from_dye"));
		ShapedRecipeBuilder.shaped(result, 16)
		.group("securitycraft:reinforced_glass_panes")
		.pattern("GGG")
		.pattern("GGG")
		.define('G', stainedGlass)
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(consumer, new ResourceLocation(SecurityCraft.MODID, result.asItem().getRegistryName().getPath() + "_from_glass"));
	}

	protected final void addStainedTerracottaRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> dye, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 8)
		.group("securitycraft:reinforced_terracotta")
		.pattern("TTT")
		.pattern("TDT")
		.pattern("TTT")
		.define('T', SCContent.REINFORCED_TERRACOTTA.get())
		.define('D', dye)
		.unlockedBy("has_reinforced_terracotta", has(SCContent.REINFORCED_TERRACOTTA.get()))
		.save(consumer);
	}

	protected final void addStairsRecipe(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 4)
		.group("securitycraft:stairs")
		.pattern("B  ")
		.pattern("BB ")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
	}

	protected final void addStonecuttingRecipe(Consumer<FinishedRecipe> consumer, ItemLike ingredient, ItemLike result, int count)
	{
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), result, count)
		.unlockedBy("has_" + ingredient.asItem().getRegistryName().getPath(), has(ingredient))
		.save(consumer, result.asItem().getRegistryName() + "_from_" + ingredient.asItem().getRegistryName().getPath() + "_stonecutting");
	}

	protected final void addWallRecipes(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike result)
	{
		ShapedRecipeBuilder.shaped(result, 6)
		.group("securitycraft:walls")
		.pattern("BBB")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(consumer);
		addStonecuttingRecipe(consumer, block, result, 1);
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
