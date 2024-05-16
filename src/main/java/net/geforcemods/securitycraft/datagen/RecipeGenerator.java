package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput output) {
		super(output);
	}

	@Override
	protected final void buildRecipes(RecipeOutput recipeOutput) {
		//combine keycard with limited use keycard to get keycards with a configurable limited amount of uses
		SpecialRecipeBuilder.special(LimitedUseKeycardRecipe::new).save(recipeOutput, "limited_use_keycards");

		//@formatter:off
		//shaped recipes
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.ALARM.get())
		.pattern("GGG")
		.pattern("GNG")
		.pattern("GRG")
		.define('G', SCContent.REINFORCED_GLASS.get())
		.define('N', Blocks.NOTE_BLOCK)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.BLOCK_CHANGE_DETECTOR.get())
		.pattern("IRI")
		.pattern("ILI")
		.pattern("III")
		.define('R', Items.REDSTONE_TORCH)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('L', SCContent.USERNAME_LOGGER.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.BLOCK_POCKET_MANAGER.get())
		.pattern("CIC")
		.pattern("IRI")
		.pattern("CIC")
		.define('C', SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get())
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.unlockedBy("has_redstone_block", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.BOUNCING_BETTY.get())
		.pattern(" P ")
		.pattern("IGI")
		.define('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.BRIEFCASE.get())
		.pattern("SSS")
		.pattern("ICI")
		.pattern("III")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('C', SCContent.KEYPAD_CHEST.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.CAGE_TRAP.get())
		.pattern("BBB")
		.pattern("GRG")
		.pattern("III")
		.define('B', SCContent.REINFORCED_IRON_BARS.get())
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.CAMERA_MONITOR.get())
		.pattern("III")
		.pattern("IGI")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.CLAYMORE.get())
		.pattern("HSH")
		.pattern("SBS")
		.pattern("RGR")
		.define('H', Blocks.TRIPWIRE_HOOK)
		.define('S', Tags.Items.STRING)
		.define('B', SCContent.BOUNCING_BETTY.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.CODEBREAKER.get())
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
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.CRYSTAL_QUARTZ_ITEM.get(), 9)
		.pattern("CQC")
		.pattern("QCQ")
		.pattern("CQC")
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.define('C', Tags.Items.GEMS_PRISMARINE)
		.unlockedBy("has_prismarine_crystals", has(Tags.Items.GEMS_PRISMARINE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.CRYSTAL_QUARTZ_BLOCK.get())
		.pattern("CC")
		.pattern("CC")
		.define('C', SCContent.CRYSTAL_QUARTZ_ITEM.get())
		.unlockedBy("has_crystal_quartz_item", has(SCContent.CRYSTAL_QUARTZ_ITEM.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SCContent.DISPLAY_CASE.get())
		.pattern("III")
		.pattern("IFG")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', Items.ITEM_FRAME)
		.define('G', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_item_frame", has(Items.ITEM_FRAME))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SCContent.FLOOR_TRAP.get(), 2)
		.pattern("ILI")
		.pattern("R R")
		.pattern("IPI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('L', SCTags.Items.REINFORCED_STONE_PRESSURE_PLATES)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('P', SCContent.REINFORCED_PISTON.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SCContent.GLOW_DISPLAY_CASE.get())
		.pattern("III")
		.pattern("IFG")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', Items.GLOW_ITEM_FRAME)
		.define('G', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_item_frame", has(Items.GLOW_ITEM_FRAME))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.REINFORCED_DOOR_ITEM.get())
		.pattern("III")
		.pattern("IDI")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('D', Items.IRON_DOOR)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.ELECTRIFIED_IRON_FENCE.get())
		.pattern(" I ")
		.pattern("IFI")
		.pattern(" I ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', SCTags.Items.REINFORCED_WOODEN_FENCES)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.IMS.get())
		.pattern("BPB")
		.pattern(" I ")
		.pattern("B B")
		.define('B', SCContent.BOUNCING_BETTY.get())
		.define('P', SCContent.PORTABLE_RADAR.get())
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_radar", has(SCContent.PORTABLE_RADAR.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.INVENTORY_SCANNER.get())
		.pattern("SSS")
		.pattern("SLS")
		.pattern("SCS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('L', SCContent.LASER_BLOCK.get())
		.define('C', Tags.Items.CHESTS_ENDER)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.KEYCARD_HOLDER.get())
		.pattern("IHI")
		.pattern("LIL")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('H', SCContent.REINFORCED_HOPPER.get())
		.define('L', Tags.Items.LEATHER)
		.unlockedBy("has_stone", has(SCContent.KEYCARD_READER.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.KEYCARD_LOCK.get())
		.pattern("SS")
		.pattern("R_")
		.pattern("SS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('_', Ingredient.of(SCContent.REINFORCED_COBBLESTONE_SLAB.get(), SCContent.REINFORCED_BLACKSTONE_SLAB.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB.get()))
		.unlockedBy("has_reader", has(SCContent.KEYCARD_READER.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.KEYCARD_READER.get())
		.pattern("SSS")
		.pattern("RH_")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('H', SCContent.REINFORCED_HOPPER.get())
		.define('_', Ingredient.of(SCContent.REINFORCED_COBBLESTONE_SLAB.get(), SCContent.REINFORCED_BLACKSTONE_SLAB.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB.get()))
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.FRAME.get())
		.pattern("III")
		.pattern("IR ")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.KEY_PANEL.get())
		.pattern("BBB")
		.pattern("BPB")
		.pattern("BBB")
		.define('B', SCContent.REINFORCED_STONE_BUTTON.get())
		.define('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.unlockedBy("has_stone_button", has(Items.STONE_BUTTON))
		.save(recipeOutput);
		//don't change these to reinforced, because the block reinforcer needs a laser block!!!
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.LASER_BLOCK.get())
		.pattern("SGS")
		.pattern("GRG")
		.pattern("SGS")
		.define('S', ItemTags.STONE_CRAFTING_MATERIALS)
		.define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.define('G', Tags.Items.GLASS_PANES_COLORLESS)
		.unlockedBy("has_stone", has(ItemTags.STONE_CRAFTING_MATERIALS))
		.save(recipeOutput);
		//k you can change again :)
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.REINFORCED_LECTERN.get())
		.pattern("SSS")
		.pattern(" B ")
		.pattern(" S ")
		.define('S', SCTags.Items.REINFORCED_WOODEN_SLABS)
		.define('B', SCContent.REINFORCED_BOOKSHELF.get())
		.unlockedBy("has_bookshelf", has(Items.BOOKSHELF))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.LENS.get(), 6)
		.group("securitycraft:lens")
		.pattern(" P")
		.pattern("P ")
		.pattern(" P")
		.define('P', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_glass_pane", has(SCTags.Items.REINFORCED_GLASS_PANES))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.MINE.get(), 3)
		.pattern(" I ")
		.pattern("IGI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.MOTION_ACTIVATED_LIGHT.get())
		.pattern("L")
		.pattern("R")
		.pattern("S")
		.define('L', Blocks.REDSTONE_LAMP)
		.define('R', SCContent.PORTABLE_RADAR.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.PANIC_BUTTON.get())
		.pattern(" I ")
		.pattern("IBI")
		.pattern(" R ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('B', SCContent.REINFORCED_STONE_BUTTON.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.PORTABLE_RADAR.get())
		.pattern("III")
		.pattern("ITI")
		.pattern("IRI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('T', Items.REDSTONE_TORCH)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.PORTABLE_TUNE_PLAYER.get())
		.pattern("IRN")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('N', Blocks.NOTE_BLOCK)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.PROJECTOR.get())
		.pattern("III")
		.pattern("BLP")
		.pattern("I I")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.define('L', SCContent.REINFORCED_REDSTONE_LAMP.get())
		.define('P', SCContent.REINFORCED_GLASS_PANE.get())
		.unlockedBy("has_redstone_lamp", has(SCContent.REINFORCED_REDSTONE_LAMP.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.PROTECTO.get())
		.pattern("ODO")
		.pattern("OEO")
		.pattern("OOO")
		.define('O', SCContent.REINFORCED_OBSIDIAN.get())
		.define('D', Blocks.DAYLIGHT_DETECTOR)
		.define('E', Items.ENDER_EYE)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_BOOKSHELF.get())
		.pattern("PPP")
		.pattern("BBB")
		.pattern("PPP")
		.define('B', Items.BOOK)
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.unlockedBy("has_book", has(Items.BOOK))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_CHISELED_BOOKSHELF.get())
		.pattern("PPP")
		.pattern("SSS")
		.pattern("PPP")
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.define('S', SCTags.Items.REINFORCED_WOODEN_SLABS)
		.unlockedBy("has_book", has(Items.BOOK))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_COARSE_DIRT.get(), 4)
		.pattern("DG")
		.pattern("GD")
		.define('D', SCContent.REINFORCED_DIRT.get())
		.define('G', SCContent.REINFORCED_GRAVEL.get())
		.unlockedBy("has_reinforced_gravel", has(SCContent.REINFORCED_GRAVEL.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_DIORITE.get(), 2)
		.pattern("CQ")
		.pattern("QC")
		.define('C', SCContent.REINFORCED_COBBLESTONE.get())
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_DISPENSER.get())
		.pattern("CCC")
		.pattern("CBC")
		.pattern("CRC")
		.define('C', SCContent.REINFORCED_COBBLESTONE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('B', Items.BOW)
		.unlockedBy("has_bow", has(Items.BOW))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_DROPPER.get())
		.pattern("CCC")
		.pattern("C C")
		.pattern("CRC")
		.define('C', SCContent.REINFORCED_COBBLESTONE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.ELECTRIFIED_IRON_FENCE_GATE.get())
		.pattern(" I ")
		.pattern("IGI")
		.pattern(" I ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCTags.Items.REINFORCED_WOODEN_FENCE_GATES)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_LEVER.get())
		.pattern("S")
		.pattern("C")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('C', SCTags.Items.REINFORCED_COBBLESTONE)
		.unlockedBy("has_cobble", has(SCContent.REINFORCED_COBBLESTONE.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_OBSERVER.get())
		.pattern("CCC")
		.pattern("RRQ")
		.pattern("CCC")
		.define('C', SCContent.REINFORCED_COBBLESTONE.get())
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_quartz", has(Tags.Items.GEMS_QUARTZ))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_PISTON.get())
		.pattern("PPP")
		.pattern("CIC")
		.pattern("CRC")
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.define('C', SCContent.REINFORCED_COBBLESTONE.get())
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_REDSTONE_LAMP.get())
		.pattern(" R ")
		.pattern("RGR")
		.pattern(" R ")
		.define('G', SCContent.REINFORCED_GLOWSTONE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SCContent.REINFORCED_STICKY_PISTON.get())
		.pattern("S")
		.pattern("P")
		.define('P', SCContent.REINFORCED_PISTON.get())
		.define('S', Tags.Items.SLIMEBALLS)
		.unlockedBy("has_slime_ball", has(Tags.Items.SLIMEBALLS))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_TINTED_GLASS.get(), 2)
		.pattern(" A ")
		.pattern("AGA")
		.pattern(" A ")
		.define('A', Items.AMETHYST_SHARD)
		.define('G', SCContent.REINFORCED_GLASS.get())
		.unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.MINE_REMOTE_ACCESS_TOOL.get())
		.pattern("T  ")
		.pattern("GDG")
		.pattern("III")
		.define('T', Items.REDSTONE_TORCH)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_mine", has(SCContent.MINE.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get())
		.pattern("ITI")
		.pattern("IDI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('T', Items.REDSTONE_TORCH)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_sentry", has(SCContent.SENTRY.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.RETINAL_SCANNER.get())
		.pattern("SSS")
		.pattern("SES")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('E', Items.ENDER_EYE)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.RIFT_STABILIZER_ITEM.get())
		.pattern("GEG")
		.pattern("CDC")
		.pattern("III")
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('E', Items.ENDER_EYE)
		.define('D', SCContent.REINFORCED_DIAMOND_BLOCK.get())
		.define('C', Items.CHORUS_FRUIT)
		.define('I', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_iron", has(SCContent.REINFORCED_IRON_BLOCK.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.SECURITY_CAMERA.get())
		.pattern("III")
		.pattern("GRI")
		.pattern("IIS")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCContent.REINFORCED_GLASS.get())
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.SENTRY.get())
		.pattern("RDR")
		.pattern("IPI")
		.pattern("BBB")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('D', SCContent.REINFORCED_DISPENSER.get())
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', SCContent.PORTABLE_RADAR.get())
		.define('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.TASER.get())
		.pattern("BGI")
		.pattern("RSG")
		.pattern("  S")
		.define('B', Items.BOW)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, SCContent.TRACK_MINE.get(), 6)
		.pattern("I I")
		.pattern("ISI")
		.pattern("IGI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('S', Tags.Items.RODS_WOODEN)
		.define('G', Tags.Items.GUNPOWDER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.TROPHY_SYSTEM.get())
		.pattern(" T ")
		.pattern(" B ")
		.pattern("S S")
		.define('T', SCContent.SENTRY.get())
		.define('B', SCContent.REINFORCED_IRON_BLOCK.get())
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
		.pattern(" RE")
		.pattern(" IR")
		.pattern("I  ")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('E', Tags.Items.GEMS_EMERALD)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get())
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
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get())
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
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get())
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
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REMOVER.get())
		.pattern("SII")
		.define('S', Items.SHEARS)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.UNIVERSAL_KEY_CHANGER.get())
		.pattern(" RL")
		.pattern(" IR")
		.pattern("I  ")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK.get())
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.USERNAME_LOGGER.get())
		.pattern("SPS")
		.pattern("SRS")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('P', SCContent.PORTABLE_RADAR.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCContent.WIRE_CUTTERS.get())
		.pattern("SI ")
		.pattern("I I")
		.pattern(" I ")
		.define('S', Items.SHEARS)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_mine", has(SCContent.MINE.get()))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_GLASS_PANE.get(), 16)
		.pattern("GGG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS.get())
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get())
		.pattern(" RD")
		.pattern(" S ")
		.pattern(" I ")
		.define('R', SCContent.PORTABLE_RADAR.get())
		.define('D', Items.BOWL)
		.define('S', Items.STICK)
		.define('I', Blocks.IRON_BLOCK)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(recipeOutput);

		//shapeless recipes
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SCContent.BLOCK_POCKET_WALL.get())
		.requires(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get())
		.unlockedBy("has_reinforced_crystal_quartz", has(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get()))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, SCContent.KEYPAD_DOOR_ITEM.get())
		.requires(SCContent.REINFORCED_DOOR_ITEM.get())
		.requires(SCContent.KEYPAD.get())
		.unlockedBy("has_reinforced_door", has(SCContent.REINFORCED_DOOR_ITEM.get()))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_PACKED_MUD.get())
		.requires(SCContent.REINFORCED_MUD.get())
		.requires(Items.WHEAT)
		.unlockedBy("has_wheat", has(Items.WHEAT))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_ANDESITE.get(), 2)
		.requires(SCContent.REINFORCED_DIORITE.get())
		.requires(SCContent.REINFORCED_COBBLESTONE.get())
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONE))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_ANDESITE.get(), 2)
		.requires(Blocks.DIORITE)
		.requires(SCContent.REINFORCED_COBBLESTONE.get())
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONE))
		.save(recipeOutput, "securitycraft:reinforced_andesite_with_vanilla_diorite");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_ANDESITE.get(), 2)
		.requires(SCContent.REINFORCED_DIORITE.get())
		.requires(Blocks.COBBLESTONE)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONE))
		.save(recipeOutput, "securitycraft:reinforced_andesite_with_vanilla_cobblestone");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get())
		.requires(SCContent.BLOCK_POCKET_WALL.get())
		.unlockedBy("has_block_pocket_wall", has(SCContent.BLOCK_POCKET_WALL.get()))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_GRANITE.get())
		.requires(SCContent.REINFORCED_DIORITE.get())
		.requires(Tags.Items.GEMS_QUARTZ)
		.unlockedBy("has_quartz", has(Tags.Items.GEMS_QUARTZ))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SCContent.SC_MANUAL.get())
		.requires(Items.BOOK)
		.requires(Blocks.IRON_BARS)
		.unlockedBy("has_wood", has(ItemTags.LOGS)) //the thought behind this is that the recipe will be given right after the player chopped their first piece of wood
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SCContent.SCANNER_DOOR_ITEM.get())
		.requires(SCContent.REINFORCED_DOOR_ITEM.get())
		.requires(SCContent.RETINAL_SCANNER.get())
		.unlockedBy("has_reinforced_door", has(SCContent.REINFORCED_DOOR_ITEM.get()))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SCContent.SCANNER_TRAPDOOR.get())
		.requires(SCContent.REINFORCED_IRON_TRAPDOOR.get())
		.requires(SCContent.RETINAL_SCANNER.get())
		.unlockedBy("has_reinforced_trapdoor", has(SCContent.REINFORCED_IRON_TRAPDOOR.get()))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SCContent.UNIVERSAL_OWNER_CHANGER.get())
		.requires(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())
		.requires(Items.NAME_TAG)
		.unlockedBy("has_name_tag", has(Items.NAME_TAG))
		.save(recipeOutput);

		//@formatter:on
		//template recipes
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_LOG.get(), SCContent.REINFORCED_ACACIA_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_LOG.get(), SCContent.REINFORCED_BIRCH_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_LOG.get(), SCContent.REINFORCED_CHERRY_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_STEM.get(), SCContent.REINFORCED_CRIMSON_HYPHAE.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_LOG.get(), SCContent.REINFORCED_DARK_OAK_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_LOG.get(), SCContent.REINFORCED_JUNGLE_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_LOG.get(), SCContent.REINFORCED_MANGROVE_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_OAK_LOG.get(), SCContent.REINFORCED_OAK_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_LOG.get(), SCContent.REINFORCED_SPRUCE_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_WARPED_STEM.get(), SCContent.REINFORCED_WARPED_HYPHAE.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(), SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(), SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_CHERRY_LOG.get(), SCContent.REINFORCED_STRIPPED_CHERRY_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_CRIMSON_STEM.get(), SCContent.REINFORCED_STRIPPED_CRIMSON_HYPHAE.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(), SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(), SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_MANGROVE_LOG.get(), SCContent.REINFORCED_STRIPPED_MANGROVE_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(), SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_OAK_LOG.get(), SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		addBarkRecipe(recipeOutput, SCContent.REINFORCED_STRIPPED_WARPED_STEM.get(), SCContent.REINFORCED_STRIPPED_WARPED_HYPHAE.get());
		addBlockMineRecipe(recipeOutput, Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_COAL_ORE, SCContent.DEEPSLATE_COAL_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_DIAMOND_ORE, SCContent.DEEPSLATE_DIAMOND_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DIRT, SCContent.DIRT_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_EMERALD_ORE, SCContent.DEEPSLATE_EMERALD_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.FURNACE, SCContent.FURNACE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.SMOKER, SCContent.SMOKER_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.BLAST_FURNACE, SCContent.BLAST_FURNACE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.GRAVEL, SCContent.GRAVEL_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_GOLD_ORE, SCContent.DEEPSLATE_GOLD_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.GILDED_BLACKSTONE, SCContent.GILDED_BLACKSTONE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.IRON_ORE, SCContent.IRON_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_IRON_ORE, SCContent.DEEPSLATE_IRON_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_LAPIS_ORE, SCContent.DEEPSLATE_LAPIS_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.NETHER_GOLD_ORE, SCContent.NETHER_GOLD_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_REDSTONE_ORE, SCContent.DEEPSLATE_REDSTONE_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.COPPER_ORE, SCContent.COPPER_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE_COPPER_ORE, SCContent.DEEPSLATE_COPPER_ORE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.SAND, SCContent.SAND_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.STONE, SCContent.STONE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.DEEPSLATE, SCContent.DEEPSLATE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.COBBLED_DEEPSLATE, SCContent.COBBLED_DEEPSLATE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.NETHERRACK, SCContent.NETHERRACK_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.END_STONE, SCContent.END_STONE_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.SUSPICIOUS_SAND, SCContent.SUSPICIOUS_SAND_MINE.get());
		addBlockMineRecipe(recipeOutput, Blocks.SUSPICIOUS_GRAVEL, SCContent.SUSPICIOUS_GRAVEL_MINE.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.REINFORCED_MANGROVE_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.REINFORCED_CHERRY_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.REINFORCED_BAMBOO_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_BUTTON.get());
		addButtonRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get());
		addCarpetRecipe(recipeOutput, SCContent.REINFORCED_MOSS_BLOCK.get(), SCContent.REINFORCED_MOSS_CARPET.get(), SCTags.Items.REINFORCED_MOSS);
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_WOOL.get(), SCContent.REINFORCED_BLACK_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_WOOL.get(), SCContent.REINFORCED_BLUE_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_WOOL.get(), SCContent.REINFORCED_BROWN_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_WOOL.get(), SCContent.REINFORCED_CYAN_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_WOOL.get(), SCContent.REINFORCED_GRAY_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_WOOL.get(), SCContent.REINFORCED_GREEN_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_WOOL.get(), SCContent.REINFORCED_LIGHT_BLUE_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_WOOL.get(), SCContent.REINFORCED_LIGHT_GRAY_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_WOOL.get(), SCContent.REINFORCED_LIME_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_WOOL.get(), SCContent.REINFORCED_MAGENTA_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_WOOL.get(), SCContent.REINFORCED_ORANGE_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_WOOL.get(), SCContent.REINFORCED_PINK_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_WOOL.get(), SCContent.REINFORCED_PURPLE_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_WOOL.get(), SCContent.REINFORCED_RED_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_WOOL.get(), SCContent.REINFORCED_WHITE_CARPET.get());
		addColoredCarpetRecipes(recipeOutput, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_WOOL.get(), SCContent.REINFORCED_YELLOW_CARPET.get());
		addChiselingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_SLAB.get(), SCContent.CHISELED_CRYSTAL_QUARTZ.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB.get(), SCContent.REINFORCED_CHISELED_DEEPSLATE.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_PURPUR_SLAB.get(), SCContent.REINFORCED_PURPUR_PILLAR.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICK_SLAB.get(), SCContent.REINFORCED_CHISELED_NETHER_BRICKS.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_SLAB.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get(), SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), SCContent.REINFORCED_CHISELED_RED_SANDSTONE.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE_SLAB.get(), SCContent.REINFORCED_CHISELED_SANDSTONE.get());
		addChiselingRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICK_SLAB.get(), SCContent.REINFORCED_CHISELED_STONE_BRICKS.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_WOOL.get());
		addColoredWoolRecipe(recipeOutput, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_WOOL.get());
		addCompressingRecipe(recipeOutput, SCContent.REINFORCED_ICE.get(), SCContent.REINFORCED_PACKED_ICE.get());
		addCompressingRecipe(recipeOutput, SCContent.REINFORCED_PACKED_ICE.get(), SCContent.REINFORCED_BLUE_ICE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.REINFORCED_MANGROVE_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.REINFORCED_CHERRY_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.REINFORCED_BAMBOO_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_FENCE.get());
		addFenceRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), Tags.Items.INGOTS_NETHER_BRICK, SCContent.REINFORCED_NETHER_BRICK_FENCE.get(), 6, false);
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.REINFORCED_MANGROVE_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.REINFORCED_CHERRY_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.REINFORCED_BAMBOO_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_FENCE_GATE.get());
		addFenceGateRecipe(recipeOutput, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_FENCE_GATE.get());
		addKeycardRecipe(recipeOutput, Tags.Items.INGOTS_GOLD, SCContent.KEYCARD_LVL_1.get());
		addKeycardRecipe(recipeOutput, Tags.Items.INGOTS_BRICK, SCContent.KEYCARD_LVL_2.get());
		addKeycardRecipe(recipeOutput, Tags.Items.INGOTS_NETHER_BRICK, SCContent.KEYCARD_LVL_3.get());
		addKeycardRecipe(recipeOutput, Tags.Items.DYES_MAGENTA, SCContent.KEYCARD_LVL_4.get());
		addKeycardRecipe(recipeOutput, Tags.Items.DYES_PURPLE, SCContent.KEYCARD_LVL_5.get());
		addKeycardRecipe(recipeOutput, Tags.Items.GEMS_LAPIS, SCContent.LIMITED_USE_KEYCARD.get());
		//recipes to reset linked keycards
		addKeycardResetRecipe(recipeOutput, SCContent.KEYCARD_LVL_1.get());
		addKeycardResetRecipe(recipeOutput, SCContent.KEYCARD_LVL_2.get());
		addKeycardResetRecipe(recipeOutput, SCContent.KEYCARD_LVL_3.get());
		addKeycardResetRecipe(recipeOutput, SCContent.KEYCARD_LVL_4.get());
		addKeycardResetRecipe(recipeOutput, SCContent.KEYCARD_LVL_5.get());
		addModuleRecipe(recipeOutput, Items.INK_SAC, SCContent.DENYLIST_MODULE.get());
		addModuleRecipe(recipeOutput, Items.PAINTING, SCContent.DISGUISE_MODULE.get());
		addModuleRecipe(recipeOutput, ItemTags.ARROWS, SCContent.HARMING_MODULE.get());
		addModuleRecipe(recipeOutput, Tags.Items.DUSTS_REDSTONE, SCContent.REDSTONE_MODULE.get());
		addModuleRecipe(recipeOutput, Tags.Items.ENDER_PEARLS, SCContent.SMART_MODULE.get());
		addModuleRecipe(recipeOutput, SCContent.KEYPAD_CHEST.get(), SCContent.STORAGE_MODULE.get());
		addModuleRecipe(recipeOutput, Items.PAPER, SCContent.ALLOWLIST_MODULE.get());
		addModuleRecipe(recipeOutput, Items.SUGAR, SCContent.SPEED_MODULE.get());
		addPillarRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_MOSAIC_SLAB.get(), SCContent.REINFORCED_BAMBOO_MOSAIC.get(), 1);
		addPillarRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get());
		addPillarRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
		addPillarRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_ACACIA_LOGS, SCContent.REINFORCED_ACACIA_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_BAMBOO_BLOCKS, SCContent.REINFORCED_BAMBOO_PLANKS.get(), 2);
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_BIRCH_LOGS, SCContent.REINFORCED_BIRCH_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_CHERRY_LOGS, SCContent.REINFORCED_CHERRY_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_CRIMSON_STEMS, SCContent.REINFORCED_CRIMSON_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_DARK_OAK_LOGS, SCContent.REINFORCED_DARK_OAK_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_JUNGLE_LOGS, SCContent.REINFORCED_JUNGLE_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_MANGROVE_LOGS, SCContent.REINFORCED_MANGROVE_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_OAK_LOGS, SCContent.REINFORCED_OAK_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_SPRUCE_LOGS, SCContent.REINFORCED_SPRUCE_PLANKS.get());
		addPlanksRecipe(recipeOutput, SCTags.Items.REINFORCED_WARPED_STEMS, SCContent.REINFORCED_WARPED_PLANKS.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.REINFORCED_MANGROVE_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.REINFORCED_CHERRY_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.REINFORCED_BAMBOO_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get());
		addPressurePlateRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get());
		addSecretSignRecipe(recipeOutput, Items.ACACIA_SIGN, SCContent.SECRET_ACACIA_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.BAMBOO_SIGN, SCContent.SECRET_BAMBOO_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.BIRCH_SIGN, SCContent.SECRET_BIRCH_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.CHERRY_SIGN, SCContent.SECRET_CHERRY_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.CRIMSON_SIGN, SCContent.SECRET_CRIMSON_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.DARK_OAK_SIGN, SCContent.SECRET_DARK_OAK_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.JUNGLE_SIGN, SCContent.SECRET_JUNGLE_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.MANGROVE_SIGN, SCContent.SECRET_MANGROVE_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.OAK_SIGN, SCContent.SECRET_OAK_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.SPRUCE_SIGN, SCContent.SECRET_SPRUCE_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.WARPED_SIGN, SCContent.SECRET_WARPED_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.ACACIA_HANGING_SIGN, SCContent.SECRET_ACACIA_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.BAMBOO_HANGING_SIGN, SCContent.SECRET_BAMBOO_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.BIRCH_HANGING_SIGN, SCContent.SECRET_BIRCH_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.CHERRY_HANGING_SIGN, SCContent.SECRET_CHERRY_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.CRIMSON_HANGING_SIGN, SCContent.SECRET_CRIMSON_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.DARK_OAK_HANGING_SIGN, SCContent.SECRET_DARK_OAK_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.JUNGLE_HANGING_SIGN, SCContent.SECRET_JUNGLE_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.MANGROVE_HANGING_SIGN, SCContent.SECRET_MANGROVE_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.OAK_HANGING_SIGN, SCContent.SECRET_OAK_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.SPRUCE_HANGING_SIGN, SCContent.SECRET_SPRUCE_HANGING_SIGN.get());
		addSecretSignRecipe(recipeOutput, Items.WARPED_HANGING_SIGN, SCContent.SECRET_WARPED_HANGING_SIGN.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.OAK_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.SPRUCE_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.BIRCH_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.JUNGLE_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.ACACIA_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.DARK_OAK_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.MANGROVE_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.CHERRY_SECURITY_SEA_BOAT.get());
		addSecuritySeaBoatRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.BAMBOO_SECURITY_SEA_RAFT.get());
		addSlabRecipe(recipeOutput, Ingredient.of(SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get(), SCContent.CHISELED_CRYSTAL_QUARTZ.get()), SCContent.CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.REINFORCED_BAMBOO_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_MOSAIC.get(), SCContent.REINFORCED_BAMBOO_MOSAIC_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.REINFORCED_CHERRY_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_CUT_COPPER.get(), SCContent.REINFORCED_CUT_COPPER_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_SLAB.get());
		addSlabRecipe(recipeOutput, Ingredient.of(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()), SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_CUT_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_TILES.get(), SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_CUT_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.REINFORCED_MANGROVE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_MUD_BRICKS.get(), SCContent.REINFORCED_MUD_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_CUT_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_SLAB.get());
		addSlabRecipe(recipeOutput, Ingredient.of(SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get()), SCContent.REINFORCED_QUARTZ_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_STONE.get(), SCContent.REINFORCED_SMOOTH_STONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_NORMAL_STONE_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_CUT_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB.get());
		addSlabRecipe(recipeOutput, SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_STAINED_GLASS.get());
		addStainedGlassRecipe(recipeOutput, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_STAINED_GLASS.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_STAINED_GLASS.get(), SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_STAINED_GLASS.get(), SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_STAINED_GLASS.get(), SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get());
		addStainedGlassPaneRecipes(recipeOutput, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_RED, SCContent.REINFORCED_RED_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_TERRACOTTA.get());
		addStainedTerracottaRecipe(recipeOutput, Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_TERRACOTTA.get());
		addStairsRecipe(recipeOutput, Ingredient.of(SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get(), SCContent.CHISELED_CRYSTAL_QUARTZ.get()), SCContent.CRYSTAL_QUARTZ_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_ACACIA_PLANKS.get(), SCContent.REINFORCED_ACACIA_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_PLANKS.get(), SCContent.REINFORCED_BAMBOO_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_BAMBOO_MOSAIC.get(), SCContent.REINFORCED_BAMBOO_MOSAIC_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_BIRCH_PLANKS.get(), SCContent.REINFORCED_BIRCH_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_CHERRY_PLANKS.get(), SCContent.REINFORCED_CHERRY_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_CUT_COPPER.get(), SCContent.REINFORCED_CUT_COPPER_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_CRIMSON_PLANKS.get(), SCContent.REINFORCED_CRIMSON_STAIRS.get());
		addStairsRecipe(recipeOutput, Ingredient.of(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()), SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_DARK_OAK_PLANKS.get(), SCContent.REINFORCED_DARK_OAK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_TILES.get(), SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_CUT_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_JUNGLE_PLANKS.get(), SCContent.REINFORCED_JUNGLE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_MANGROVE_PLANKS.get(), SCContent.REINFORCED_MANGROVE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_MUD_BRICKS.get(), SCContent.REINFORCED_MUD_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_OAK_PLANKS.get(), SCContent.REINFORCED_OAK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_CUT_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_STAIRS.get());
		addStairsRecipe(recipeOutput, Ingredient.of(SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get()), SCContent.REINFORCED_QUARTZ_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_SPRUCE_PLANKS.get(), SCContent.REINFORCED_SPRUCE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_WARPED_PLANKS.get(), SCContent.REINFORCED_WARPED_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_CUT_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER_STAIRS.get());
		addStairsRecipe(recipeOutput, SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_BASALT.get(), SCContent.REINFORCED_POLISHED_BASALT.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_COPPER_BLOCK.get(), SCContent.REINFORCED_CUT_COPPER.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_PACKED_MUD.get(), SCContent.REINFORCED_MUD_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILES.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_RED_SAND.get(), SCContent.REINFORCED_RED_SANDSTONE.get(), 1);
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_SAND.get(), SCContent.REINFORCED_SANDSTONE.get(), 1);
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICKS.get());
		addTwoByTwoRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_DEEPSLATE_TILES.get(), SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_MUD_BRICKS.get(), SCContent.REINFORCED_MUD_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_WALL.get());
		addWallRecipes(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_WALL.get());

		//furnace recipes
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_BASALT.get(), SCContent.REINFORCED_SMOOTH_BASALT.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_CLAY.get(), SCContent.REINFORCED_TERRACOTTA.get(), 0.35F, 200);
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_STONE.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_CRACKED_DEEPSLATE_BRICKS.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_TILES.get(), SCContent.REINFORCED_CRACKED_DEEPSLATE_TILES.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_CRACKED_NETHER_BRICKS.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_SMOOTH_QUARTZ.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get());
		addSimpleCookingRecipe(recipeOutput, SCTags.Items.REINFORCED_SAND, SCContent.REINFORCED_GLASS.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_SMOOTH_STONE.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_CRACKED_STONE_BRICKS.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_BLACK_TERRACOTTA.get(), SCContent.REINFORCED_BLACK_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_BLUE_TERRACOTTA.get(), SCContent.REINFORCED_BLUE_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_BROWN_TERRACOTTA.get(), SCContent.REINFORCED_BROWN_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_CYAN_TERRACOTTA.get(), SCContent.REINFORCED_CYAN_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_GRAY_TERRACOTTA.get(), SCContent.REINFORCED_GRAY_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_GREEN_TERRACOTTA.get(), SCContent.REINFORCED_GREEN_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA.get(), SCContent.REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA.get(), SCContent.REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_LIME_TERRACOTTA.get(), SCContent.REINFORCED_LIME_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_MAGENTA_TERRACOTTA.get(), SCContent.REINFORCED_MAGENTA_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_ORANGE_TERRACOTTA.get(), SCContent.REINFORCED_ORANGE_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_PINK_TERRACOTTA.get(), SCContent.REINFORCED_PINK_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_PURPLE_TERRACOTTA.get(), SCContent.REINFORCED_PURPLE_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_RED_TERRACOTTA.get(), SCContent.REINFORCED_RED_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_WHITE_TERRACOTTA.get(), SCContent.REINFORCED_WHITE_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_YELLOW_TERRACOTTA.get(), SCContent.REINFORCED_YELLOW_GLAZED_TERRACOTTA.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.SMOOTH_CRYSTAL_QUARTZ.get());
		addSimpleCookingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get());

		//stonecutter recipes (ordered alphabetically by the ingredient)
		addStonecuttingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CHISELED_CRYSTAL_QUARTZ.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_PILLAR.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_ANDESITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BASALT.get(), SCContent.REINFORCED_POLISHED_BASALT.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_BLACKSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_BRICKS.get(), SCContent.REINFORCED_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_COBBLED_DEEPSLATE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_CHISELED_DEEPSLATE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILES.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COBBLESTONE.get(), SCContent.REINFORCED_COBBLESTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COPPER_BLOCK.get(), SCContent.REINFORCED_CUT_COPPER.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COPPER_BLOCK.get(), SCContent.REINFORCED_CUT_COPPER_SLAB.get(), 8);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_COPPER_BLOCK.get(), SCContent.REINFORCED_CUT_COPPER_STAIRS.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CUT_COPPER.get(), SCContent.REINFORCED_CUT_COPPER_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CUT_COPPER.get(), SCContent.REINFORCED_CUT_COPPER_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_CUT_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DARK_PRISMARINE.get(), SCContent.REINFORCED_DARK_PRISMARINE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_TILES.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_TILES.get(), SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DEEPSLATE_TILES.get(), SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_DIORITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_END_STONE.get(), SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_END_STONE_BRICKS.get(), SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB.get(), 8);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER_STAIRS.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_CUT_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_EXPOSED_CUT_COPPER.get(), SCContent.REINFORCED_EXPOSED_CUT_COPPER_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_GRANITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_COBBLESTONE.get(), SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(), SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_MUD_BRICKS.get(), SCContent.REINFORCED_MUD_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_MUD_BRICKS.get(), SCContent.REINFORCED_MUD_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_CHISELED_NETHER_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_NETHER_BRICKS.get(), SCContent.REINFORCED_NETHER_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB.get(), 8);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER_STAIRS.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_CUT_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_OXIDIZED_CUT_COPPER.get(), SCContent.REINFORCED_OXIDIZED_CUT_COPPER_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_ANDESITE.get(), SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILES.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DEEPSLATE.get(), SCContent.REINFORCED_POLISHED_DEEPSLATE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_DIORITE.get(), SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_POLISHED_GRANITE.get(), SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE.get(), SCContent.REINFORCED_PRISMARINE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PRISMARINE_BRICKS.get(), SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_PILLAR.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_PURPUR_BLOCK.get(), SCContent.REINFORCED_PURPUR_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_CHISELED_QUARTZ.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_PILLAR.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_QUARTZ_BLOCK.get(), SCContent.REINFORCED_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_NETHER_BRICKS.get(), SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CHISELED_RED_SANDSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_RED_SANDSTONE.get(), SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CHISELED_SANDSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SANDSTONE.get(), SCContent.REINFORCED_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_QUARTZ.get(), SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_SANDSTONE.get(), SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_SMOOTH_STONE.get(), SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_CHISELED_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_NORMAL_STONE_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_BRICK_WALL.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE.get(), SCContent.REINFORCED_STONE_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_CHISELED_STONE_BRICKS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_STONE_BRICKS.get(), SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB.get(), 8);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER_STAIRS.get(), 4);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_CUT_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.REINFORCED_WEATHERED_CUT_COPPER.get(), SCContent.REINFORCED_WEATHERED_CUT_COPPER_STAIRS.get(), 1);
		addStonecuttingRecipe(recipeOutput, SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), 2);
		addStonecuttingRecipe(recipeOutput, SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), 1);

		//@formatter:off
		//mossy conversions
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE.get())
		.requires(SCContent.REINFORCED_COBBLESTONE.get())
		.requires(Items.VINE)
		.unlockedBy("has_vine", has(Items.VINE))
		.save(recipeOutput, "securitycraft:reinforced_mossy_cobblestone_from_vine");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE.get())
		.requires(SCContent.REINFORCED_COBBLESTONE.get())
		.requires(Blocks.MOSS_BLOCK)
		.unlockedBy("has_moss", has(Blocks.MOSS_BLOCK))
		.save(recipeOutput, "securitycraft:reinforced_mossy_cobblestone_from_vanilla_moss");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE.get())
		.requires(Blocks.COBBLESTONE)
		.requires(SCContent.REINFORCED_MOSS_BLOCK.get())
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK.get()))
		.save(recipeOutput, "securitycraft:reinforced_mossy_cobblestone_from_vanilla_cobblestone");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE.get())
		.requires(SCContent.REINFORCED_COBBLESTONE.get())
		.requires(SCContent.REINFORCED_MOSS_BLOCK.get())
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK.get()))
		.save(recipeOutput, "securitycraft:reinforced_mossy_cobblestone_from_reinforced_ingredients");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get())
		.requires(SCContent.REINFORCED_STONE_BRICKS.get())
		.requires(Items.VINE)
		.unlockedBy("has_vine", has(Items.VINE))
		.save(recipeOutput, "securitycraft:reinforced_mossy_stone_bricks_from_vine");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get())
		.requires(SCContent.REINFORCED_STONE_BRICKS.get())
		.requires(Blocks.MOSS_BLOCK)
		.unlockedBy("has_moss", has(Blocks.MOSS_BLOCK))
		.save(recipeOutput, "securitycraft:reinforced_mossy_stone_bricks_from_vanilla_moss");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get())
		.requires(Blocks.STONE_BRICKS)
		.requires(SCContent.REINFORCED_MOSS_BLOCK.get())
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK.get()))
		.save(recipeOutput, "securitycraft:reinforced_mossy_stone_bricks_from_vanilla_stone_bricks");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS.get())
		.requires(SCContent.REINFORCED_STONE_BRICKS.get())
		.requires(SCContent.REINFORCED_MOSS_BLOCK.get())
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK.get()))
		.save(recipeOutput, "securitycraft:reinforced_mossy_stone_bricks_from_reinforced_ingredients");
		//@formatter:on
	}

	protected final void addBarkRecipe(RecipeOutput recipeOutput, ItemLike log, ItemLike result) { //woof
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 3)
		.group("securitycraft:bark")
		.pattern("LL")
		.pattern("LL")
		.define('L', log)
		.unlockedBy("has_log", has(log))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addBlockMineRecipe(RecipeOutput recipeOutput, ItemLike input, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result)
		.group("securitycraft:block_mines")
		.requires(input)
		.requires(SCContent.MINE.get())
		.unlockedBy("has_mine", has(SCContent.MINE.get()))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addButtonRecipe(RecipeOutput recipeOutput, ItemLike input, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, result)
		.group("securitycraft:reinforced_buttons")
		.requires(input)
		.unlockedBy("has_block", has(input))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addCarpetRecipe(RecipeOutput recipeOutput, ItemLike carpetMaterial, ItemLike carpet, TagKey<Item> unlockedBy) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, carpet, 3)
		.group("securitycraft:reinforced_carpets")
		.pattern("MM")
		.define('M', carpetMaterial)
		.unlockedBy("has_item", has(unlockedBy))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addColoredCarpetRecipes(RecipeOutput recipeOutput, TagKey<Item> dye, ItemLike wool, ItemLike carpet) {
		addCarpetRecipe(recipeOutput, wool, carpet, SCTags.Items.REINFORCED_WOOL);
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, carpet)
		.group("securitycraft:reinforced_carpets")
		.requires(dye)
		.requires(SCTags.Items.REINFORCED_WOOL_CARPETS)
		.unlockedBy("has_wool_carpet", has(SCTags.Items.REINFORCED_WOOL_CARPETS))
		.save(recipeOutput, new ResourceLocation(Utils.getRegistryName(carpet.asItem()).toString() + "_from_dye"));
		//@formatter:on
	}

	protected final void addChiselingRecipe(RecipeOutput recipeOutput, ItemLike slab, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
		.pattern("S")
		.pattern("S")
		.define('S', slab)
		.unlockedBy("has_slab", has(slab))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addColoredWoolRecipe(RecipeOutput recipeOutput, TagKey<Item> dye, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result)
		.group("securitycraft:reinforced_wool")
		.requires(dye)
		.requires(SCTags.Items.REINFORCED_WOOL)
		.unlockedBy("has_wool", has(SCTags.Items.REINFORCED_WOOL))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addCompressingRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
		.pattern("BBB")
		.pattern("BBB")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addFenceRecipe(RecipeOutput recipeOutput, ItemLike material, ItemLike result) {
		addFenceRecipe(recipeOutput, material, Tags.Items.RODS_WOODEN, result, 3, true);
	}

	protected final void addFenceRecipe(RecipeOutput recipeOutput, ItemLike material, TagKey<Item> stick, ItemLike result, int amount, boolean group) {
		//@formatter:off
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, amount)
		.pattern("MSM")
		.pattern("MSM")
		.define('M', material)
		.define('S', stick)
		.unlockedBy("has_stick", has(stick));
		//@formatter:on

		if (group)
			builder.group("securitycraft:reinforced_fences");

		builder.save(recipeOutput);
	}

	protected final void addFenceGateRecipe(RecipeOutput recipeOutput, ItemLike material, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, result)
		.group("securitycraft:reinforced_fence_gates")
		.pattern("SMS")
		.pattern("SMS")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addKeycardRecipe(RecipeOutput recipeOutput, TagKey<Item> specialIngredient, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
		.group("securitycraft:keycards")
		.pattern("III")
		.pattern("SSS")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addKeycardResetRecipe(RecipeOutput recipeOutput, ItemLike keycard) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, keycard)
		.requires(keycard)
		.unlockedBy("has_keycard", has(keycard))
		.save(recipeOutput, new ResourceLocation(SecurityCraft.MODID, Utils.getRegistryName(keycard.asItem()).getPath() + "_reset"));
		//@formatter:on
	}

	protected final void addModuleRecipe(RecipeOutput recipeOutput, ItemLike specialIngredient, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
		.group("securitycraft:modules")
		.pattern("III")
		.pattern("IPI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', Items.PAPER)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addModuleRecipe(RecipeOutput recipeOutput, TagKey<Item> specialIngredient, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
		.group("securitycraft:modules")
		.pattern("III")
		.pattern("IPI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', Items.PAPER)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addPillarRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		addPillarRecipe(recipeOutput, block, result, 2);
	}

	protected final void addPillarRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result, int amount) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, amount)
		.pattern("B")
		.pattern("B")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addPlanksRecipe(RecipeOutput recipeOutput, TagKey<Item> log, ItemLike result) {
		addPlanksRecipe(recipeOutput, log, result, 4);
	}

	protected final void addPlanksRecipe(RecipeOutput recipeOutput, TagKey<Item> log, ItemLike result, int amount) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, amount)
		.group("securitycraft:reinforced_planks")
		.requires(log)
		.unlockedBy("has_log", has(log))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addPressurePlateRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, result)
		.group("securitycraft:reinforced_pressure_plates")
		.pattern("SS")
		.define('S', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addSecretSignRecipe(RecipeOutput recipeOutput, ItemLike vanillaSign, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 3)
		.group("securitycraft:secret_signs")
		.requires(vanillaSign, 3)
		.requires(SCContent.RETINAL_SCANNER.get())
		.unlockedBy("has_sign", has(ItemTags.SIGNS))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addSecuritySeaBoatRecipe(RecipeOutput recipeOutput, ItemLike planks, ItemLike boat) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, boat)
		.group("securitycraft:security_sea_boats")
		.pattern("PCP")
		.pattern("PPP")
		.define('P', planks)
		.define('C', SCContent.KEYPAD_CHEST.get())
		.unlockedBy("has_keypad_chest", has(SCContent.KEYPAD_CHEST.get()))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addSimpleCookingRecipe(RecipeOutput recipeOutput, ItemLike input, ItemLike output) {
		addSimpleCookingRecipe(recipeOutput, input, output, 0.1F, 200);
	}

	protected final void addSimpleCookingRecipe(RecipeOutput recipeOutput, TagKey<Item> input, ItemLike output) {
		addSimpleCookingRecipe(recipeOutput, input, output, 0.1F, 200);
	}

	protected final void addSimpleCookingRecipe(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float xp, int time) {
		//@formatter:off
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, output, xp, time)
		.unlockedBy("has_item", has(input))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addSimpleCookingRecipe(RecipeOutput recipeOutput, TagKey<Item> input, ItemLike output, float xp, int time) {
		//@formatter:off
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, output, xp, time)
		.unlockedBy("has_item", has(input))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addSlabRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 6)
		.group("securitycraft:slabs")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addSlabRecipe(RecipeOutput recipeOutput, Ingredient block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 6)
		.group("securitycraft:slabs")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block.getItems()[0].getItem()))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addStainedGlassRecipe(RecipeOutput recipeOutput, TagKey<Item> dye, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 8)
		.group("securitycraft:reinforced_glass")
		.pattern("GGG")
		.pattern("GDG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS.get())
		.define('D', dye)
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addStainedGlassPaneRecipes(RecipeOutput recipeOutput, TagKey<Item> dye, ItemLike stainedGlass, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 8)
		.group("securitycraft:reinforced_glass_panes")
		.pattern("GGG")
		.pattern("GDG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS_PANE.get())
		.define('D', dye)
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(recipeOutput, new ResourceLocation(SecurityCraft.MODID, Utils.getRegistryName(result.asItem()).getPath() + "_from_dye"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 16)
		.group("securitycraft:reinforced_glass_panes")
		.pattern("GGG")
		.pattern("GGG")
		.define('G', stainedGlass)
		.unlockedBy("has_glass", has(Tags.Items.GLASS))
		.save(recipeOutput, new ResourceLocation(SecurityCraft.MODID, Utils.getRegistryName(result.asItem()).getPath() + "_from_glass"));
		//@formatter:on
	}

	protected final void addStainedTerracottaRecipe(RecipeOutput recipeOutput, TagKey<Item> dye, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 8)
		.group("securitycraft:reinforced_terracotta")
		.pattern("TTT")
		.pattern("TDT")
		.pattern("TTT")
		.define('T', SCContent.REINFORCED_TERRACOTTA.get())
		.define('D', dye)
		.unlockedBy("has_reinforced_terracotta", has(SCContent.REINFORCED_TERRACOTTA.get()))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addStairsRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 4)
		.group("securitycraft:stairs")
		.pattern("B  ")
		.pattern("BB ")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addStairsRecipe(RecipeOutput recipeOutput, Ingredient block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 4)
		.group("securitycraft:stairs")
		.pattern("B  ")
		.pattern("BB ")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block.getItems()[0].getItem()))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addStonecuttingRecipe(RecipeOutput recipeOutput, ItemLike ingredient, ItemLike result, int count) {
		//@formatter:off
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, count)
		.unlockedBy("has_" + Utils.getRegistryName(ingredient.asItem()).getPath(), has(ingredient))
		.save(recipeOutput, Utils.getRegistryName(result.asItem()) + "_from_" + Utils.getRegistryName(ingredient.asItem()).getPath() + "_stonecutting");
		//@formatter:on
	}

	protected final void addTwoByTwoRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		addTwoByTwoRecipe(recipeOutput, block, result, 4);
	}

	protected final void addTwoByTwoRecipe(RecipeOutput recipeOutput, ItemLike block, ItemLike result, int amount) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, amount)
		.pattern("BB")
		.pattern("BB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		//@formatter:on
	}

	protected final void addWallRecipes(RecipeOutput recipeOutput, ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result, 6)
		.group("securitycraft:walls")
		.pattern("BBB")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(recipeOutput);
		addStonecuttingRecipe(recipeOutput, block, result, 1);
		//@formatter:on
	}
}
