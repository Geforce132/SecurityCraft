package net.geforcemods.securitycraft.datagen;

import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.recipe.BlockReinforcingRecipe;
import net.geforcemods.securitycraft.recipe.BlockUnreinforcingRecipe;
import net.geforcemods.securitycraft.recipe.CopyPositionComponentItemRecipe;
import net.geforcemods.securitycraft.recipe.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	//TODO: Remove when the field in the super class is protected
	private final HolderGetter<Item> items;

	public RecipeGenerator(HolderLookup.Provider lookupProvider, RecipeOutput output) {
		super(lookupProvider, output);
		items = lookupProvider.lookupOrThrow(Registries.ITEM);
	}

	@Override
	protected final void buildRecipes() {
		//Combine block with universal block reinforcer to unreinforce/reinforce it (depending on the reinforcer's mode), reducing the reinforcer's durability
		SpecialRecipeBuilder.special(BlockReinforcingRecipe::new).save(output, scRecipeId("block_reinforcing"));
		SpecialRecipeBuilder.special(BlockUnreinforcingRecipe::new).save(output, scRecipeId("block_unreinforcing"));
		//tool copying
		SpecialRecipeBuilder.special(CopyPositionComponentItemRecipe::cameraMonitor).save(output, scRecipeId("copy_camera_monitor"));
		SpecialRecipeBuilder.special(CopyPositionComponentItemRecipe::mineRemoteAccessTool).save(output, scRecipeId("copy_mine_remote_access_tool"));
		SpecialRecipeBuilder.special(CopyPositionComponentItemRecipe::sentryRemoteAccessTool).save(output, scRecipeId("copy_sentry_remote_access_tool"));
		SpecialRecipeBuilder.special(CopyPositionComponentItemRecipe::sonicSecuritySystem).save(output, scRecipeId("copy_sonic_security_system"));
		//combine keycard with limited use keycard to get keycards with a configurable limited amount of uses
		SpecialRecipeBuilder.special(LimitedUseKeycardRecipe::new).save(output, scRecipeId("limited_use_keycards"));

		//@formatter:off
		//shaped recipes
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.ALARM)
		.pattern("GGG")
		.pattern("GNG")
		.pattern("GRG")
		.define('G', SCContent.REINFORCED_GLASS)
		.define('N', Blocks.NOTE_BLOCK)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.BLOCK_CHANGE_DETECTOR)
		.pattern("IRI")
		.pattern("ILI")
		.pattern("III")
		.define('R', Items.REDSTONE_TORCH)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('L', SCContent.USERNAME_LOGGER)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.BLOCK_POCKET_MANAGER)
		.pattern("CIC")
		.pattern("IRI")
		.pattern("CIC")
		.define('C', SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK)
		.define('I', SCContent.REINFORCED_IRON_BLOCK)
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK)
		.unlockedBy("has_redstone_block", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.BOUNCING_BETTY)
		.pattern(" P ")
		.pattern("IGI")
		.define('P', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GUNPOWDERS)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.BRIEFCASE)
		.pattern("SSS")
		.pattern("ICI")
		.pattern("III")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('C', SCContent.KEYPAD_CHEST)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.CAGE_TRAP)
		.pattern("BBB")
		.pattern("GRG")
		.pattern("III")
		.define('B', SCContent.REINFORCED_IRON_BARS)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('I', SCContent.REINFORCED_IRON_BLOCK)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.CAMERA_MONITOR)
		.pattern("III")
		.pattern("IGI")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCContent.REINFORCED_GLASS_PANE)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.CLAYMORE)
		.pattern("HSH")
		.pattern("SBS")
		.pattern("RGR")
		.define('H', Blocks.TRIPWIRE_HOOK)
		.define('S', Tags.Items.STRINGS)
		.define('B', SCContent.BOUNCING_BETTY)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('G', Tags.Items.GUNPOWDERS)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.CODEBREAKER)
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
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.CRYSTAL_QUARTZ_ITEM, 9)
		.pattern("CQC")
		.pattern("QCQ")
		.pattern("CQC")
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.define('C', Tags.Items.GEMS_PRISMARINE)
		.unlockedBy("has_prismarine_crystals", has(Tags.Items.GEMS_PRISMARINE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, SCContent.CRYSTAL_QUARTZ_BLOCK)
		.pattern("CC")
		.pattern("CC")
		.define('C', SCContent.CRYSTAL_QUARTZ_ITEM)
		.unlockedBy("has_crystal_quartz_item", has(SCContent.CRYSTAL_QUARTZ_ITEM))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, SCContent.DISPLAY_CASE)
		.pattern("III")
		.pattern("IFG")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', Items.ITEM_FRAME)
		.define('G', SCContent.REINFORCED_GLASS_PANE)
		.unlockedBy("has_item_frame", has(Items.ITEM_FRAME))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, SCContent.FLOOR_TRAP, 2)
		.pattern("ILI")
		.pattern("R R")
		.pattern("IPI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('L', SCTags.Items.REINFORCED_STONE_PRESSURE_PLATES)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('P', SCContent.REINFORCED_PISTON)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, SCContent.GLOW_DISPLAY_CASE)
		.pattern("III")
		.pattern("IFG")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', Items.GLOW_ITEM_FRAME)
		.define('G', SCContent.REINFORCED_GLASS_PANE)
		.unlockedBy("has_item_frame", has(Items.GLOW_ITEM_FRAME))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.REINFORCED_DOOR_ITEM)
		.pattern("III")
		.pattern("IDI")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('D', Items.IRON_DOOR)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.ELECTRIFIED_IRON_FENCE)
		.pattern(" I ")
		.pattern("IFI")
		.pattern(" I ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('F', SCTags.Items.REINFORCED_WOODEN_FENCES)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.IMS)
		.pattern("BPB")
		.pattern(" I ")
		.pattern("B B")
		.define('B', SCContent.BOUNCING_BETTY)
		.define('P', SCContent.PORTABLE_RADAR)
		.define('I', SCContent.REINFORCED_IRON_BLOCK)
		.unlockedBy("has_radar", has(SCContent.PORTABLE_RADAR))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.INCOGNITO_MASK)
		.pattern(" S ")
		.pattern("S S")
		.pattern(" L ")
		.define('S', Tags.Items.STRINGS)
		.define('L', Tags.Items.LEATHERS)
		.unlockedBy("has_leather", has(Tags.Items.LEATHERS))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.INVENTORY_SCANNER)
		.pattern("SSS")
		.pattern("SLS")
		.pattern("SCS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('L', SCContent.LASER_BLOCK)
		.define('C', Tags.Items.CHESTS_ENDER)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.KEYCARD_HOLDER)
		.pattern("IHI")
		.pattern("LIL")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('H', SCContent.REINFORCED_HOPPER)
		.define('L', Tags.Items.LEATHERS)
		.unlockedBy("has_stone", has(SCContent.KEYCARD_READER))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.KEYCARD_LOCK)
		.pattern("SS")
		.pattern("R_")
		.pattern("SS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('_', Ingredient.of(SCContent.REINFORCED_COBBLESTONE_SLAB, SCContent.REINFORCED_BLACKSTONE_SLAB, SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB))
		.unlockedBy("has_reader", has(SCContent.KEYCARD_READER))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.KEYCARD_READER)
		.pattern("SSS")
		.pattern("RH_")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('H', SCContent.REINFORCED_HOPPER)
		.define('_', Ingredient.of(SCContent.REINFORCED_COBBLESTONE_SLAB, SCContent.REINFORCED_BLACKSTONE_SLAB, SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB))
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.FRAME)
		.pattern("III")
		.pattern("IR ")
		.pattern("III")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.KEY_PANEL)
		.pattern("BBB")
		.pattern("BPB")
		.pattern("BBB")
		.define('B', SCContent.REINFORCED_STONE_BUTTON)
		.define('P', Items.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.unlockedBy("has_stone_button", has(Items.STONE_BUTTON))
		.save(output);
		//don't change these to reinforced, because the block reinforcer needs a laser block!!!
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.LASER_BLOCK)
		.pattern("SGS")
		.pattern("GRG")
		.pattern("SGS")
		.define('S', ItemTags.STONE_CRAFTING_MATERIALS)
		.define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
		.define('G', Tags.Items.GLASS_PANES_COLORLESS)
		.unlockedBy("has_stone", has(ItemTags.STONE_CRAFTING_MATERIALS))
		.save(output);
		//k you can change again :)
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.REINFORCED_LECTERN)
		.pattern("SSS")
		.pattern(" B ")
		.pattern(" S ")
		.define('S', SCTags.Items.REINFORCED_WOODEN_SLABS)
		.define('B', SCContent.REINFORCED_BOOKSHELF)
		.unlockedBy("has_bookshelf", has(Items.BOOKSHELF))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.LENS, 6)
		.group("securitycraft:lens")
		.pattern(" P")
		.pattern("P ")
		.pattern(" P")
		.define('P', SCContent.REINFORCED_GLASS_PANE)
		.unlockedBy("has_glass_pane", has(SCTags.Items.REINFORCED_GLASS_PANES))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.MINE, 3)
		.pattern(" I ")
		.pattern("IGI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GUNPOWDERS)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.MOTION_ACTIVATED_LIGHT)
		.pattern("L")
		.pattern("R")
		.pattern("S")
		.define('L', Blocks.REDSTONE_LAMP)
		.define('R', SCContent.PORTABLE_RADAR)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.PANIC_BUTTON)
		.pattern(" I ")
		.pattern("IBI")
		.pattern(" R ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('B', SCContent.REINFORCED_STONE_BUTTON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.PORTABLE_RADAR)
		.pattern("III")
		.pattern("ITI")
		.pattern("IRI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('T', Items.REDSTONE_TORCH)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.PORTABLE_TUNE_PLAYER)
		.pattern("IRN")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('N', Blocks.NOTE_BLOCK)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.PROJECTOR)
		.pattern("III")
		.pattern("BLP")
		.pattern("I I")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('B', SCContent.REINFORCED_IRON_BLOCK)
		.define('L', SCContent.REINFORCED_REDSTONE_LAMP)
		.define('P', SCContent.REINFORCED_GLASS_PANE)
		.unlockedBy("has_redstone_lamp", has(SCContent.REINFORCED_REDSTONE_LAMP))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.PROTECTO)
		.pattern("ODO")
		.pattern("OEO")
		.pattern("OOO")
		.define('O', SCContent.REINFORCED_OBSIDIAN)
		.define('D', Blocks.DAYLIGHT_DETECTOR)
		.define('E', Items.ENDER_EYE)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_BOOKSHELF)
		.pattern("PPP")
		.pattern("BBB")
		.pattern("PPP")
		.define('B', Items.BOOK)
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.unlockedBy("has_book", has(Items.BOOK))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_CHISELED_BOOKSHELF)
		.pattern("PPP")
		.pattern("SSS")
		.pattern("PPP")
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.define('S', SCTags.Items.REINFORCED_WOODEN_SLABS)
		.unlockedBy("has_book", has(Items.BOOK))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_COARSE_DIRT, 4)
		.pattern("DG")
		.pattern("GD")
		.define('D', SCContent.REINFORCED_DIRT)
		.define('G', SCContent.REINFORCED_GRAVEL)
		.unlockedBy("has_reinforced_gravel", has(SCContent.REINFORCED_GRAVEL))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_DIORITE, 2)
		.pattern("CQ")
		.pattern("QC")
		.define('C', SCContent.REINFORCED_COBBLESTONE)
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONES))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_DISPENSER)
		.pattern("CCC")
		.pattern("CBC")
		.pattern("CRC")
		.define('C', SCContent.REINFORCED_COBBLESTONE)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('B', Items.BOW)
		.unlockedBy("has_bow", has(Items.BOW))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_DROPPER)
		.pattern("CCC")
		.pattern("C C")
		.pattern("CRC")
		.define('C', SCContent.REINFORCED_COBBLESTONE)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.ELECTRIFIED_IRON_FENCE_GATE)
		.pattern(" I ")
		.pattern("IGI")
		.pattern(" I ")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCTags.Items.REINFORCED_WOODEN_FENCE_GATES)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_LEVER)
		.pattern("S")
		.pattern("C")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('C', SCTags.Items.REINFORCED_COBBLESTONE)
		.unlockedBy("has_cobble", has(SCContent.REINFORCED_COBBLESTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_OBSERVER)
		.pattern("CCC")
		.pattern("RRQ")
		.pattern("CCC")
		.define('C', SCContent.REINFORCED_COBBLESTONE)
		.define('Q', Tags.Items.GEMS_QUARTZ)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_quartz", has(Tags.Items.GEMS_QUARTZ))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_PISTON)
		.pattern("PPP")
		.pattern("CIC")
		.pattern("CRC")
		.define('P', SCTags.Items.REINFORCED_PLANKS)
		.define('C', SCContent.REINFORCED_COBBLESTONE)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_REDSTONE_LAMP)
		.pattern(" R ")
		.pattern("RGR")
		.pattern(" R ")
		.define('G', SCContent.REINFORCED_GLOWSTONE)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.REINFORCED_STICKY_PISTON)
		.pattern("S")
		.pattern("P")
		.define('P', SCContent.REINFORCED_PISTON)
		.define('S', Tags.Items.SLIME_BALLS)
		.unlockedBy("has_slime_ball", has(Tags.Items.SLIME_BALLS))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_TINTED_GLASS, 2)
		.pattern(" A ")
		.pattern("AGA")
		.pattern(" A ")
		.define('A', Items.AMETHYST_SHARD)
		.define('G', SCContent.REINFORCED_GLASS)
		.unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.MINE_REMOTE_ACCESS_TOOL)
		.pattern("T  ")
		.pattern("GDG")
		.pattern("III")
		.define('T', Items.REDSTONE_TORCH)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_mine", has(SCContent.MINE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.SENTRY_REMOTE_ACCESS_TOOL)
		.pattern("ITI")
		.pattern("IDI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('T', Items.REDSTONE_TORCH)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_sentry", has(SCContent.SENTRY))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.RETINAL_SCANNER)
		.pattern("SSS")
		.pattern("SES")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('E', Items.ENDER_EYE)
		.unlockedBy("has_stone", has(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.RIFT_STABILIZER_ITEM)
		.pattern("GEG")
		.pattern("CDC")
		.pattern("III")
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('E', Items.ENDER_EYE)
		.define('D', SCContent.REINFORCED_DIAMOND_BLOCK)
		.define('C', Items.CHORUS_FRUIT)
		.define('I', SCContent.REINFORCED_IRON_BLOCK)
		.unlockedBy("has_iron", has(SCContent.REINFORCED_IRON_BLOCK))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, SCContent.SECURE_REDSTONE_INTERFACE)
		.pattern("TRB")
		.pattern("SCS")
		.define('T', Items.REDSTONE_TORCH)
		.define('R', SCContent.PORTABLE_RADAR)
		.define('B', Items.BOWL)
		.define('S', SCContent.REINFORCED_SMOOTH_STONE)
		.define('C', Items.COMPARATOR)
		.unlockedBy("has_radar", has(SCContent.PORTABLE_RADAR))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.SECURITY_CAMERA)
		.pattern("III")
		.pattern("GRI")
		.pattern("IIS")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', SCContent.REINFORCED_GLASS)
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.SENTRY)
		.pattern("RDR")
		.pattern("IPI")
		.pattern("BBB")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('D', SCContent.REINFORCED_DISPENSER)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', SCContent.PORTABLE_RADAR)
		.define('B', SCContent.REINFORCED_IRON_BLOCK)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.TASER)
		.pattern("BGI")
		.pattern("RSG")
		.pattern("  S")
		.define('B', Items.BOW)
		.define('G', Tags.Items.INGOTS_GOLD)
		.define('I', Tags.Items.INGOTS_IRON)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, SCContent.TRACK_MINE, 6)
		.pattern("I I")
		.pattern("ISI")
		.pattern("IGI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('S', Tags.Items.RODS_WOODEN)
		.define('G', Tags.Items.GUNPOWDERS)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.TROPHY_SYSTEM)
		.pattern(" T ")
		.pattern(" B ")
		.pattern("S S")
		.define('T', SCContent.SENTRY)
		.define('B', SCContent.REINFORCED_IRON_BLOCK)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_MODIFIER)
		.pattern(" RE")
		.pattern(" IR")
		.pattern("I  ")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('E', Tags.Items.GEMS_EMERALD)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1)
		.group("securitycraft:universal_block_reinforcer")
		.pattern(" DG")
		.pattern("RLD")
		.pattern("SR ")
		.define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
		.define('D', Tags.Items.GEMS_DIAMOND)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2)
		.group("securitycraft:universal_block_reinforcer")
		.pattern(" DG")
		.pattern("RLD")
		.pattern("SR ")
		.define('G', SCContent.REINFORCED_BLACK_STAINED_GLASS)
		.define('D', SCContent.REINFORCED_DIAMOND_BLOCK)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3)
		.group("securitycraft:universal_block_reinforcer")
		.pattern(" EG")
		.pattern("RNE")
		.pattern("SR ")
		.define('G', SCContent.REINFORCED_PINK_STAINED_GLASS)
		.define('E', SCContent.REINFORCED_EMERALD_BLOCK)
		.define('R', SCContent.REINFORCED_REDSTONE_BLOCK)
		.define('N', Tags.Items.NETHER_STARS)
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_BLOCK_REMOVER)
		.pattern("SII")
		.define('S', Items.SHEARS)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_KEY_CHANGER)
		.pattern(" RL")
		.pattern(" IR")
		.pattern("I  ")
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.define('L', SCContent.LASER_BLOCK)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.USERNAME_LOGGER)
		.pattern("SPS")
		.pattern("SRS")
		.pattern("SSS")
		.define('S', SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS)
		.define('P', SCContent.PORTABLE_RADAR)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCContent.WIRE_CUTTERS)
		.pattern("SI ")
		.pattern("I I")
		.pattern(" I ")
		.define('S', Items.SHEARS)
		.define('I', Tags.Items.INGOTS_IRON)
		.unlockedBy("has_mine", has(SCContent.MINE))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, SCContent.REINFORCED_GLASS_PANE, 16)
		.pattern("GGG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS)
		.unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
		.save(output);
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, SCContent.SONIC_SECURITY_SYSTEM_ITEM)
		.pattern(" RD")
		.pattern(" S ")
		.pattern(" I ")
		.define('R', SCContent.PORTABLE_RADAR)
		.define('D', Items.BOWL)
		.define('S', Items.STICK)
		.define('I', Blocks.IRON_BLOCK)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(output);

		//shapeless recipes
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, SCContent.BLOCK_POCKET_WALL)
		.requires(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK)
		.unlockedBy("has_reinforced_crystal_quartz", has(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.REDSTONE, SCContent.KEYPAD_DOOR_ITEM)
		.requires(SCContent.REINFORCED_DOOR_ITEM)
		.requires(SCContent.KEYPAD)
		.unlockedBy("has_reinforced_door", has(SCContent.REINFORCED_DOOR_ITEM))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_PACKED_MUD)
		.requires(SCContent.REINFORCED_MUD)
		.requires(Items.WHEAT)
		.unlockedBy("has_wheat", has(Items.WHEAT))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_ANDESITE, 2)
		.requires(SCContent.REINFORCED_DIORITE)
		.requires(SCContent.REINFORCED_COBBLESTONE)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONES))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_ANDESITE, 2)
		.requires(Blocks.DIORITE)
		.requires(SCContent.REINFORCED_COBBLESTONE)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONES))
		.save(output, "securitycraft:reinforced_andesite_with_vanilla_diorite");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_ANDESITE, 2)
		.requires(SCContent.REINFORCED_DIORITE)
		.requires(Blocks.COBBLESTONE)
		.unlockedBy("has_cobblestone", has(Tags.Items.COBBLESTONES))
		.save(output, "securitycraft:reinforced_andesite_with_vanilla_cobblestone");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK)
		.requires(SCContent.BLOCK_POCKET_WALL)
		.unlockedBy("has_block_pocket_wall", has(SCContent.BLOCK_POCKET_WALL))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_GRANITE)
		.requires(SCContent.REINFORCED_DIORITE)
		.requires(Tags.Items.GEMS_QUARTZ)
		.unlockedBy("has_quartz", has(Tags.Items.GEMS_QUARTZ))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, SCContent.SC_MANUAL)
		.requires(Items.BOOK)
		.requires(Blocks.IRON_BARS)
		.unlockedBy("has_wood", has(ItemTags.LOGS)) //the thought behind this is that the recipe will be given right after the player chopped their first piece of wood
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, SCContent.SCANNER_DOOR_ITEM)
		.requires(SCContent.REINFORCED_DOOR_ITEM)
		.requires(SCContent.RETINAL_SCANNER)
		.unlockedBy("has_reinforced_door", has(SCContent.REINFORCED_DOOR_ITEM))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, SCContent.SCANNER_TRAPDOOR)
		.requires(SCContent.REINFORCED_IRON_TRAPDOOR)
		.requires(SCContent.RETINAL_SCANNER)
		.unlockedBy("has_reinforced_trapdoor", has(SCContent.REINFORCED_IRON_TRAPDOOR))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.TOOLS, SCContent.UNIVERSAL_OWNER_CHANGER)
		.requires(SCContent.UNIVERSAL_BLOCK_MODIFIER)
		.requires(Items.NAME_TAG)
		.unlockedBy("has_name_tag", has(Items.NAME_TAG))
		.save(output);

		//@formatter:on
		//template recipes
		addBarkRecipe(SCContent.REINFORCED_ACACIA_LOG, SCContent.REINFORCED_ACACIA_WOOD);
		addBarkRecipe(SCContent.REINFORCED_BIRCH_LOG, SCContent.REINFORCED_BIRCH_WOOD);
		addBarkRecipe(SCContent.REINFORCED_CHERRY_LOG, SCContent.REINFORCED_CHERRY_WOOD);
		addBarkRecipe(SCContent.REINFORCED_CRIMSON_STEM, SCContent.REINFORCED_CRIMSON_HYPHAE);
		addBarkRecipe(SCContent.REINFORCED_DARK_OAK_LOG, SCContent.REINFORCED_DARK_OAK_WOOD);
		addBarkRecipe(SCContent.REINFORCED_JUNGLE_LOG, SCContent.REINFORCED_JUNGLE_WOOD);
		addBarkRecipe(SCContent.REINFORCED_MANGROVE_LOG, SCContent.REINFORCED_MANGROVE_WOOD);
		addBarkRecipe(SCContent.REINFORCED_OAK_LOG, SCContent.REINFORCED_OAK_WOOD);
		addBarkRecipe(SCContent.REINFORCED_PALE_OAK_LOG, SCContent.REINFORCED_PALE_OAK_WOOD);
		addBarkRecipe(SCContent.REINFORCED_SPRUCE_LOG, SCContent.REINFORCED_SPRUCE_WOOD);
		addBarkRecipe(SCContent.REINFORCED_WARPED_STEM, SCContent.REINFORCED_WARPED_HYPHAE);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_ACACIA_LOG, SCContent.REINFORCED_STRIPPED_ACACIA_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_BIRCH_LOG, SCContent.REINFORCED_STRIPPED_BIRCH_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_CHERRY_LOG, SCContent.REINFORCED_STRIPPED_CHERRY_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_CRIMSON_STEM, SCContent.REINFORCED_STRIPPED_CRIMSON_HYPHAE);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG, SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_JUNGLE_LOG, SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_MANGROVE_LOG, SCContent.REINFORCED_STRIPPED_MANGROVE_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_SPRUCE_LOG, SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_OAK_LOG, SCContent.REINFORCED_STRIPPED_OAK_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_PALE_OAK_LOG, SCContent.REINFORCED_STRIPPED_PALE_OAK_WOOD);
		addBarkRecipe(SCContent.REINFORCED_STRIPPED_WARPED_STEM, SCContent.REINFORCED_STRIPPED_WARPED_HYPHAE);
		addBlockMineRecipe(Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE);
		addBlockMineRecipe(Blocks.CREAKING_HEART, SCContent.CREAKING_HEART_MINE);
		addBlockMineRecipe(Blocks.COAL_ORE, SCContent.COAL_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_COAL_ORE, SCContent.DEEPSLATE_COAL_ORE_MINE);
		addBlockMineRecipe(Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE);
		addBlockMineRecipe(Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_DIAMOND_ORE, SCContent.DEEPSLATE_DIAMOND_ORE_MINE);
		addBlockMineRecipe(Blocks.DIRT, SCContent.DIRT_MINE);
		addBlockMineRecipe(Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_EMERALD_ORE, SCContent.DEEPSLATE_EMERALD_ORE_MINE);
		addBlockMineRecipe(Blocks.FURNACE, SCContent.FURNACE_MINE);
		addBlockMineRecipe(Blocks.SMOKER, SCContent.SMOKER_MINE);
		addBlockMineRecipe(Blocks.BLAST_FURNACE, SCContent.BLAST_FURNACE_MINE);
		addBlockMineRecipe(Blocks.GRAVEL, SCContent.GRAVEL_MINE);
		addBlockMineRecipe(Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_GOLD_ORE, SCContent.DEEPSLATE_GOLD_ORE_MINE);
		addBlockMineRecipe(Blocks.GILDED_BLACKSTONE, SCContent.GILDED_BLACKSTONE_MINE);
		addBlockMineRecipe(Blocks.IRON_ORE, SCContent.IRON_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_IRON_ORE, SCContent.DEEPSLATE_IRON_ORE_MINE);
		addBlockMineRecipe(Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_LAPIS_ORE, SCContent.DEEPSLATE_LAPIS_ORE_MINE);
		addBlockMineRecipe(Blocks.NETHER_GOLD_ORE, SCContent.NETHER_GOLD_ORE_MINE);
		addBlockMineRecipe(Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE);
		addBlockMineRecipe(Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_REDSTONE_ORE, SCContent.DEEPSLATE_REDSTONE_ORE_MINE);
		addBlockMineRecipe(Blocks.COPPER_ORE, SCContent.COPPER_ORE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE_COPPER_ORE, SCContent.DEEPSLATE_COPPER_ORE_MINE);
		addBlockMineRecipe(Blocks.SAND, SCContent.SAND_MINE);
		addBlockMineRecipe(Blocks.STONE, SCContent.STONE_MINE);
		addBlockMineRecipe(Blocks.DEEPSLATE, SCContent.DEEPSLATE_MINE);
		addBlockMineRecipe(Blocks.COBBLED_DEEPSLATE, SCContent.COBBLED_DEEPSLATE_MINE);
		addBlockMineRecipe(Blocks.NETHERRACK, SCContent.NETHERRACK_MINE);
		addBlockMineRecipe(Blocks.END_STONE, SCContent.END_STONE_MINE);
		addBlockMineRecipe(Blocks.SUSPICIOUS_SAND, SCContent.SUSPICIOUS_SAND_MINE);
		addBlockMineRecipe(Blocks.SUSPICIOUS_GRAVEL, SCContent.SUSPICIOUS_GRAVEL_MINE);
		addButtonRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_BUTTON);
		addButtonRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON);
		addCarpetRecipe(SCContent.REINFORCED_MOSS_BLOCK, SCContent.REINFORCED_MOSS_CARPET, SCTags.Items.REINFORCED_MOSS);
		addCarpetRecipe(SCContent.REINFORCED_PALE_MOSS_BLOCK, SCContent.REINFORCED_PALE_MOSS_CARPET, SCTags.Items.REINFORCED_PALE_MOSS);
		addColoredCarpetRecipes(Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_WOOL, SCContent.REINFORCED_BLACK_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_WOOL, SCContent.REINFORCED_BLUE_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_WOOL, SCContent.REINFORCED_BROWN_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_WOOL, SCContent.REINFORCED_CYAN_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_WOOL, SCContent.REINFORCED_GRAY_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_WOOL, SCContent.REINFORCED_GREEN_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_WOOL, SCContent.REINFORCED_LIGHT_BLUE_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_WOOL, SCContent.REINFORCED_LIGHT_GRAY_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_WOOL, SCContent.REINFORCED_LIME_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_WOOL, SCContent.REINFORCED_MAGENTA_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_WOOL, SCContent.REINFORCED_ORANGE_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_WOOL, SCContent.REINFORCED_PINK_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_WOOL, SCContent.REINFORCED_PURPLE_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_RED, SCContent.REINFORCED_RED_WOOL, SCContent.REINFORCED_RED_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_WOOL, SCContent.REINFORCED_WHITE_CARPET);
		addColoredCarpetRecipes(Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_WOOL, SCContent.REINFORCED_YELLOW_CARPET);
		addChiselingRecipe(SCContent.CRYSTAL_QUARTZ_SLAB, SCContent.CHISELED_CRYSTAL_QUARTZ);
		addChiselingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ);
		addChiselingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB, SCContent.REINFORCED_CHISELED_DEEPSLATE);
		addChiselingRecipe(SCContent.REINFORCED_PURPUR_SLAB, SCContent.REINFORCED_PURPUR_PILLAR);
		addChiselingRecipe(SCContent.REINFORCED_NETHER_BRICK_SLAB, SCContent.REINFORCED_CHISELED_NETHER_BRICKS);
		addChiselingRecipe(SCContent.REINFORCED_QUARTZ_SLAB, SCContent.REINFORCED_CHISELED_QUARTZ);
		addChiselingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB, SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE);
		addChiselingRecipe(SCContent.REINFORCED_RED_SANDSTONE_SLAB, SCContent.REINFORCED_CHISELED_RED_SANDSTONE);
		addChiselingRecipe(SCContent.REINFORCED_SANDSTONE_SLAB, SCContent.REINFORCED_CHISELED_SANDSTONE);
		addChiselingRecipe(SCContent.REINFORCED_STONE_BRICK_SLAB, SCContent.REINFORCED_CHISELED_STONE_BRICKS);
		addChiselingRecipe(SCContent.REINFORCED_CUT_COPPER_SLAB, SCContent.REINFORCED_CHISELED_COPPER);
		addChiselingRecipe(SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB, SCContent.REINFORCED_EXPOSED_CHISELED_COPPER);
		addChiselingRecipe(SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB, SCContent.REINFORCED_WEATHERED_CHISELED_COPPER);
		addChiselingRecipe(SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB, SCContent.REINFORCED_OXIDIZED_CHISELED_COPPER);
		addChiselingRecipe(SCContent.REINFORCED_TUFF_BRICK_SLAB, SCContent.REINFORCED_CHISELED_TUFF_BRICKS);
		addChiselingRecipe(SCContent.REINFORCED_TUFF_SLAB, SCContent.REINFORCED_CHISELED_TUFF);
		addChiselingRecipe(SCContent.REINFORCED_RESIN_BRICK_SLAB, SCContent.REINFORCED_CHISELED_RESIN_BRICKS);
		addColoredLensRecipe(Items.BLACK_DYE, SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.BLUE_DYE, SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.BROWN_DYE, SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.CYAN_DYE, SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.GRAY_DYE, SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.GREEN_DYE, SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.LIGHT_BLUE_DYE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.LIGHT_GRAY_DYE, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.LIME_DYE, SCContent.REINFORCED_LIME_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.MAGENTA_DYE, SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.ORANGE_DYE, SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.PINK_DYE, SCContent.REINFORCED_PINK_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.PURPLE_DYE, SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.RED_DYE, SCContent.REINFORCED_RED_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.YELLOW_DYE, SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE);
		addColoredLensRecipe(Items.WHITE_DYE, SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE);
		addColoredWoolRecipe(Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_RED, SCContent.REINFORCED_RED_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_WOOL);
		addColoredWoolRecipe(Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_WOOL);
		addCompressingRecipe(SCContent.REINFORCED_ICE, SCContent.REINFORCED_PACKED_ICE);
		addCompressingRecipe(SCContent.REINFORCED_PACKED_ICE, SCContent.REINFORCED_BLUE_ICE);
		addCopperGrateRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_COPPER_GRATE);
		addCopperGrateRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_COPPER_GRATE);
		addCopperGrateRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_COPPER_GRATE);
		addCopperGrateRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_COPPER_GRATE);
		addFenceRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.REINFORCED_OAK_FENCE);
		addFenceRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.REINFORCED_SPRUCE_FENCE);
		addFenceRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.REINFORCED_BIRCH_FENCE);
		addFenceRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.REINFORCED_JUNGLE_FENCE);
		addFenceRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.REINFORCED_ACACIA_FENCE);
		addFenceRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.REINFORCED_DARK_OAK_FENCE);
		addFenceRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.REINFORCED_MANGROVE_FENCE);
		addFenceRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.REINFORCED_CHERRY_FENCE);
		addFenceRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.REINFORCED_PALE_OAK_FENCE);
		addFenceRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.REINFORCED_BAMBOO_FENCE);
		addFenceRecipe(SCContent.REINFORCED_CRIMSON_PLANKS, SCContent.REINFORCED_CRIMSON_FENCE);
		addFenceRecipe(SCContent.REINFORCED_WARPED_PLANKS, SCContent.REINFORCED_WARPED_FENCE);
		addFenceRecipe(SCContent.REINFORCED_NETHER_BRICKS, Tags.Items.BRICKS_NETHER, SCContent.REINFORCED_NETHER_BRICK_FENCE, 6, false);
		addFenceGateRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.REINFORCED_OAK_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.REINFORCED_SPRUCE_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.REINFORCED_BIRCH_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.REINFORCED_JUNGLE_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.REINFORCED_ACACIA_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.REINFORCED_DARK_OAK_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.REINFORCED_MANGROVE_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.REINFORCED_CHERRY_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.REINFORCED_PALE_OAK_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.REINFORCED_BAMBOO_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_CRIMSON_PLANKS, SCContent.REINFORCED_CRIMSON_FENCE_GATE);
		addFenceGateRecipe(SCContent.REINFORCED_WARPED_PLANKS, SCContent.REINFORCED_WARPED_FENCE_GATE);
		addKeycardRecipe(Tags.Items.INGOTS_GOLD, SCContent.KEYCARD_LVL_1);
		addKeycardRecipe(Tags.Items.BRICKS_NORMAL, SCContent.KEYCARD_LVL_2);
		addKeycardRecipe(Tags.Items.BRICKS_NETHER, SCContent.KEYCARD_LVL_3);
		addKeycardRecipe(Tags.Items.DYES_MAGENTA, SCContent.KEYCARD_LVL_4);
		addKeycardRecipe(Tags.Items.DYES_PURPLE, SCContent.KEYCARD_LVL_5);
		addKeycardRecipe(Tags.Items.GEMS_LAPIS, SCContent.LIMITED_USE_KEYCARD);
		//recipes to reset linked keycards
		addKeycardResetRecipe(SCContent.KEYCARD_LVL_1);
		addKeycardResetRecipe(SCContent.KEYCARD_LVL_2);
		addKeycardResetRecipe(SCContent.KEYCARD_LVL_3);
		addKeycardResetRecipe(SCContent.KEYCARD_LVL_4);
		addKeycardResetRecipe(SCContent.KEYCARD_LVL_5);
		addModuleRecipe(Items.INK_SAC, SCContent.DENYLIST_MODULE);
		addModuleRecipe(Items.PAINTING, SCContent.DISGUISE_MODULE);
		addModuleRecipe(ItemTags.ARROWS, SCContent.HARMING_MODULE);
		addModuleRecipe(Tags.Items.DUSTS_REDSTONE, SCContent.REDSTONE_MODULE);
		addModuleRecipe(Tags.Items.ENDER_PEARLS, SCContent.SMART_MODULE);
		addModuleRecipe(SCContent.KEYPAD_CHEST, SCContent.STORAGE_MODULE);
		addModuleRecipe(Items.PAPER, SCContent.ALLOWLIST_MODULE);
		addModuleRecipe(Items.SUGAR, SCContent.SPEED_MODULE);
		addPillarRecipe(SCContent.REINFORCED_BAMBOO_MOSAIC_SLAB, SCContent.REINFORCED_BAMBOO_MOSAIC, 1);
		addPillarRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_PILLAR);
		addPillarRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR);
		addPillarRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_PILLAR);
		addPlanksRecipe(SCTags.Items.REINFORCED_ACACIA_LOGS, SCContent.REINFORCED_ACACIA_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_BAMBOO_BLOCKS, SCContent.REINFORCED_BAMBOO_PLANKS, 2);
		addPlanksRecipe(SCTags.Items.REINFORCED_BIRCH_LOGS, SCContent.REINFORCED_BIRCH_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_CHERRY_LOGS, SCContent.REINFORCED_CHERRY_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_CRIMSON_STEMS, SCContent.REINFORCED_CRIMSON_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_DARK_OAK_LOGS, SCContent.REINFORCED_DARK_OAK_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_JUNGLE_LOGS, SCContent.REINFORCED_JUNGLE_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_MANGROVE_LOGS, SCContent.REINFORCED_MANGROVE_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_OAK_LOGS, SCContent.REINFORCED_OAK_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_PALE_OAK_LOGS, SCContent.REINFORCED_PALE_OAK_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_SPRUCE_LOGS, SCContent.REINFORCED_SPRUCE_PLANKS);
		addPlanksRecipe(SCTags.Items.REINFORCED_WARPED_STEMS, SCContent.REINFORCED_WARPED_PLANKS);
		addPressurePlateRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_PRESSURE_PLATE);
		addPressurePlateRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE);
		addSecretHangingSignRecipe(Items.ACACIA_HANGING_SIGN, SCContent.SECRET_ACACIA_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.BAMBOO_HANGING_SIGN, SCContent.SECRET_BAMBOO_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.BIRCH_HANGING_SIGN, SCContent.SECRET_BIRCH_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.CHERRY_HANGING_SIGN, SCContent.SECRET_CHERRY_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.CRIMSON_HANGING_SIGN, SCContent.SECRET_CRIMSON_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.DARK_OAK_HANGING_SIGN, SCContent.SECRET_DARK_OAK_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.JUNGLE_HANGING_SIGN, SCContent.SECRET_JUNGLE_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.MANGROVE_HANGING_SIGN, SCContent.SECRET_MANGROVE_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.OAK_HANGING_SIGN, SCContent.SECRET_OAK_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.PALE_OAK_HANGING_SIGN, SCContent.SECRET_PALE_OAK_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.SPRUCE_HANGING_SIGN, SCContent.SECRET_SPRUCE_HANGING_SIGN);
		addSecretHangingSignRecipe(Items.WARPED_HANGING_SIGN, SCContent.SECRET_WARPED_HANGING_SIGN);
		addSecretSignRecipe(Items.ACACIA_SIGN, SCContent.SECRET_ACACIA_SIGN);
		addSecretSignRecipe(Items.BAMBOO_SIGN, SCContent.SECRET_BAMBOO_SIGN);
		addSecretSignRecipe(Items.BIRCH_SIGN, SCContent.SECRET_BIRCH_SIGN);
		addSecretSignRecipe(Items.CHERRY_SIGN, SCContent.SECRET_CHERRY_SIGN);
		addSecretSignRecipe(Items.CRIMSON_SIGN, SCContent.SECRET_CRIMSON_SIGN);
		addSecretSignRecipe(Items.DARK_OAK_SIGN, SCContent.SECRET_DARK_OAK_SIGN);
		addSecretSignRecipe(Items.JUNGLE_SIGN, SCContent.SECRET_JUNGLE_SIGN);
		addSecretSignRecipe(Items.MANGROVE_SIGN, SCContent.SECRET_MANGROVE_SIGN);
		addSecretSignRecipe(Items.OAK_SIGN, SCContent.SECRET_OAK_SIGN);
		addSecretSignRecipe(Items.PALE_OAK_SIGN, SCContent.SECRET_PALE_OAK_SIGN);
		addSecretSignRecipe(Items.SPRUCE_SIGN, SCContent.SECRET_SPRUCE_SIGN);
		addSecretSignRecipe(Items.WARPED_SIGN, SCContent.SECRET_WARPED_SIGN);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.OAK_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.SPRUCE_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.BIRCH_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.JUNGLE_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.ACACIA_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.DARK_OAK_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.MANGROVE_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.CHERRY_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.PALE_OAK_SECURITY_SEA_BOAT);
		addSecuritySeaBoatRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.BAMBOO_SECURITY_SEA_RAFT);
		addSlabRecipe(Ingredient.of(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_PILLAR, SCContent.CHISELED_CRYSTAL_QUARTZ), SCContent.CRYSTAL_QUARTZ_SLAB);
		addSlabRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_ANDESITE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_BAMBOO_MOSAIC, SCContent.REINFORCED_BAMBOO_MOSAIC_SLAB);
		addSlabRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_BLACKSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_BRICKS, SCContent.REINFORCED_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_COBBLESTONE, SCContent.REINFORCED_COBBLESTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_CUT_COPPER, SCContent.REINFORCED_CUT_COPPER_SLAB);
		addSlabRecipe(Ingredient.of(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ), SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB);
		addSlabRecipe(SCContent.REINFORCED_CUT_SANDSTONE, SCContent.REINFORCED_CUT_SANDSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_CUT_RED_SANDSTONE, SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_DARK_PRISMARINE, SCContent.REINFORCED_DARK_PRISMARINE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_DEEPSLATE_TILES, SCContent.REINFORCED_DEEPSLATE_TILE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_DIORITE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_END_STONE_BRICKS, SCContent.REINFORCED_END_STONE_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_EXPOSED_CUT_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB);
		addSlabRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_GRANITE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_MOSSY_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_MOSSY_STONE_BRICKS, SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_MUD_BRICKS, SCContent.REINFORCED_MUD_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_NETHER_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_OXIDIZED_CUT_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB);
		addSlabRecipe(SCContent.REINFORCED_POLISHED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_POLISHED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_POLISHED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_PRISMARINE_BRICKS, SCContent.REINFORCED_PRISMARINE_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_PRISMARINE, SCContent.REINFORCED_PRISMARINE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_PURPUR_BLOCK, SCContent.REINFORCED_PURPUR_SLAB);
		addSlabRecipe(Ingredient.of(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_PILLAR, SCContent.REINFORCED_CHISELED_QUARTZ), SCContent.REINFORCED_QUARTZ_SLAB);
		addSlabRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_SANDSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_RED_NETHER_BRICKS, SCContent.REINFORCED_RED_NETHER_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_RED_SANDSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_RESIN_BRICKS, SCContent.REINFORCED_RESIN_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB);
		addSlabRecipe(SCContent.REINFORCED_SMOOTH_QUARTZ, SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB);
		addSlabRecipe(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_SMOOTH_SANDSTONE, SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_SMOOTH_STONE, SCContent.REINFORCED_SMOOTH_STONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_NORMAL_STONE_SLAB);
		addSlabRecipe(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_STONE_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_SLAB);
		addSlabRecipe(SCContent.REINFORCED_TUFF_BRICKS, SCContent.REINFORCED_TUFF_BRICK_SLAB);
		addSlabRecipe(SCContent.REINFORCED_WEATHERED_CUT_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB);
		addSlabRecipe(SCContent.SMOOTH_CRYSTAL_QUARTZ, SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB);
		addStainedGlassRecipe(Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_RED, SCContent.REINFORCED_RED_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_STAINED_GLASS);
		addStainedGlassRecipe(Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_STAINED_GLASS);
		addStainedGlassPaneRecipes(Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_STAINED_GLASS, SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_STAINED_GLASS, SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_STAINED_GLASS, SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_STAINED_GLASS, SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_STAINED_GLASS, SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_STAINED_GLASS, SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS, SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS, SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_STAINED_GLASS, SCContent.REINFORCED_LIME_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_STAINED_GLASS, SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_STAINED_GLASS, SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_STAINED_GLASS, SCContent.REINFORCED_PINK_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_STAINED_GLASS, SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_RED, SCContent.REINFORCED_RED_STAINED_GLASS, SCContent.REINFORCED_RED_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_STAINED_GLASS, SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE);
		addStainedGlassPaneRecipes(Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_STAINED_GLASS, SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE);
		addStainedTerracottaRecipe(Tags.Items.DYES_BLACK, SCContent.REINFORCED_BLACK_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_BLUE, SCContent.REINFORCED_BLUE_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_BROWN, SCContent.REINFORCED_BROWN_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_CYAN, SCContent.REINFORCED_CYAN_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_GRAY, SCContent.REINFORCED_GRAY_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_GREEN, SCContent.REINFORCED_GREEN_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_LIGHT_BLUE, SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_LIGHT_GRAY, SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_LIME, SCContent.REINFORCED_LIME_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_MAGENTA, SCContent.REINFORCED_MAGENTA_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_ORANGE, SCContent.REINFORCED_ORANGE_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_PINK, SCContent.REINFORCED_PINK_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_PURPLE, SCContent.REINFORCED_PURPLE_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_RED, SCContent.REINFORCED_RED_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_WHITE, SCContent.REINFORCED_WHITE_TERRACOTTA);
		addStainedTerracottaRecipe(Tags.Items.DYES_YELLOW, SCContent.REINFORCED_YELLOW_TERRACOTTA);
		addStairsRecipe(Ingredient.of(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_PILLAR, SCContent.CHISELED_CRYSTAL_QUARTZ), SCContent.CRYSTAL_QUARTZ_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_ANDESITE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_BAMBOO_MOSAIC, SCContent.REINFORCED_BAMBOO_MOSAIC_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_BLACKSTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_BRICKS, SCContent.REINFORCED_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_COBBLESTONE, SCContent.REINFORCED_COBBLESTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_COBBLED_DEEPSLATE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_CUT_COPPER, SCContent.REINFORCED_CUT_COPPER_STAIRS);
		addStairsRecipe(Ingredient.of(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ), SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_DARK_PRISMARINE, SCContent.REINFORCED_DARK_PRISMARINE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_DEEPSLATE_TILES, SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_DIORITE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_END_STONE_BRICKS, SCContent.REINFORCED_END_STONE_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_EXPOSED_CUT_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_GRANITE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_MOSSY_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_MOSSY_STONE_BRICKS, SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_MUD_BRICKS, SCContent.REINFORCED_MUD_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_NETHER_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_OXIDIZED_CUT_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_POLISHED_TUFF_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_PRISMARINE_BRICKS, SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_PRISMARINE, SCContent.REINFORCED_PRISMARINE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_PURPUR_BLOCK, SCContent.REINFORCED_PURPUR_STAIRS);
		addStairsRecipe(Ingredient.of(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_PILLAR, SCContent.REINFORCED_CHISELED_QUARTZ), SCContent.REINFORCED_QUARTZ_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_RED_NETHER_BRICKS, SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_RED_SANDSTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_RESIN_BRICKS, SCContent.REINFORCED_RESIN_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_SANDSTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_SMOOTH_QUARTZ, SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_SMOOTH_SANDSTONE, SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_STONE_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_TUFF_BRICKS, SCContent.REINFORCED_TUFF_BRICK_STAIRS);
		addStairsRecipe(SCContent.REINFORCED_WEATHERED_CUT_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER_STAIRS);
		addStairsRecipe(SCContent.SMOOTH_CRYSTAL_QUARTZ, SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS);
		addTwoByTwoRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE);
		addTwoByTwoRecipe(SCContent.REINFORCED_BASALT, SCContent.REINFORCED_POLISHED_BASALT);
		addTwoByTwoRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE);
		addTwoByTwoRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE);
		addTwoByTwoRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_CUT_COPPER);
		addTwoByTwoRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE);
		addTwoByTwoRecipe(SCContent.REINFORCED_END_STONE, SCContent.REINFORCED_END_STONE_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER);
		addTwoByTwoRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE);
		addTwoByTwoRecipe(SCContent.REINFORCED_PACKED_MUD, SCContent.REINFORCED_MUD_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER);
		addTwoByTwoRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILES);
		addTwoByTwoRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_RED_SAND, SCContent.REINFORCED_RED_SANDSTONE, 1);
		addTwoByTwoRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_CUT_RED_SANDSTONE);
		addTwoByTwoRecipe(SCContent.REINFORCED_SAND, SCContent.REINFORCED_SANDSTONE, 1);
		addTwoByTwoRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_CUT_SANDSTONE);
		addTwoByTwoRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_BRICKS);
		addTwoByTwoRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_POLISHED_TUFF);
		addTwoByTwoRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER);
		addWallRecipes(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_ANDESITE_WALL);
		addWallRecipes(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_BLACKSTONE_WALL);
		addWallRecipes(SCContent.REINFORCED_BRICKS, SCContent.REINFORCED_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_COBBLESTONE, SCContent.REINFORCED_COBBLESTONE_WALL);
		addWallRecipes(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_COBBLED_DEEPSLATE_WALL);
		addWallRecipes(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_DIORITE_WALL);
		addWallRecipes(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_DEEPSLATE_TILES, SCContent.REINFORCED_DEEPSLATE_TILE_WALL);
		addWallRecipes(SCContent.REINFORCED_END_STONE_BRICKS, SCContent.REINFORCED_END_STONE_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_GRANITE_WALL);
		addWallRecipes(SCContent.REINFORCED_MOSSY_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE_WALL);
		addWallRecipes(SCContent.REINFORCED_MOSSY_STONE_BRICKS, SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_MUD_BRICKS, SCContent.REINFORCED_MUD_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_NETHER_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL);
		addWallRecipes(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_WALL);
		addWallRecipes(SCContent.REINFORCED_PRISMARINE, SCContent.REINFORCED_PRISMARINE_WALL);
		addWallRecipes(SCContent.REINFORCED_RED_NETHER_BRICKS, SCContent.REINFORCED_RED_NETHER_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_RED_SANDSTONE_WALL);
		addWallRecipes(SCContent.REINFORCED_RESIN_BRICKS, SCContent.REINFORCED_RESIN_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_SANDSTONE_WALL);
		addWallRecipes(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_STONE_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_POLISHED_TUFF_WALL);
		addWallRecipes(SCContent.REINFORCED_TUFF_BRICKS, SCContent.REINFORCED_TUFF_BRICK_WALL);
		addWallRecipes(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_WALL);
		addWoodenButtonRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.REINFORCED_OAK_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.REINFORCED_SPRUCE_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.REINFORCED_BIRCH_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.REINFORCED_JUNGLE_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.REINFORCED_ACACIA_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.REINFORCED_DARK_OAK_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.REINFORCED_MANGROVE_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.REINFORCED_CHERRY_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.REINFORCED_PALE_OAK_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.REINFORCED_BAMBOO_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_CRIMSON_PLANKS, SCContent.REINFORCED_CRIMSON_BUTTON);
		addWoodenButtonRecipe(SCContent.REINFORCED_WARPED_PLANKS, SCContent.REINFORCED_WARPED_BUTTON);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.REINFORCED_OAK_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.REINFORCED_BIRCH_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.REINFORCED_ACACIA_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.REINFORCED_MANGROVE_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.REINFORCED_CHERRY_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.REINFORCED_PALE_OAK_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.REINFORCED_BAMBOO_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_CRIMSON_PLANKS, SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE);
		addWoodenPressurePlateRecipe(SCContent.REINFORCED_WARPED_PLANKS, SCContent.REINFORCED_WARPED_PRESSURE_PLATE);
		addWoodenSlabRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.REINFORCED_ACACIA_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.REINFORCED_BAMBOO_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.REINFORCED_BIRCH_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.REINFORCED_CHERRY_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_CRIMSON_PLANKS, SCContent.REINFORCED_CRIMSON_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.REINFORCED_DARK_OAK_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.REINFORCED_JUNGLE_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.REINFORCED_MANGROVE_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.REINFORCED_OAK_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.REINFORCED_PALE_OAK_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.REINFORCED_SPRUCE_SLAB);
		addWoodenSlabRecipe(SCContent.REINFORCED_WARPED_PLANKS, SCContent.REINFORCED_WARPED_SLAB);
		addWoodenStairsRecipe(SCContent.REINFORCED_ACACIA_PLANKS, SCContent.REINFORCED_ACACIA_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_BAMBOO_PLANKS, SCContent.REINFORCED_BAMBOO_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_BIRCH_PLANKS, SCContent.REINFORCED_BIRCH_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_CHERRY_PLANKS, SCContent.REINFORCED_CHERRY_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_CRIMSON_PLANKS, SCContent.REINFORCED_CRIMSON_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_DARK_OAK_PLANKS, SCContent.REINFORCED_DARK_OAK_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_JUNGLE_PLANKS, SCContent.REINFORCED_JUNGLE_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_MANGROVE_PLANKS, SCContent.REINFORCED_MANGROVE_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_OAK_PLANKS, SCContent.REINFORCED_OAK_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_PALE_OAK_PLANKS, SCContent.REINFORCED_PALE_OAK_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_SPRUCE_PLANKS, SCContent.REINFORCED_SPRUCE_STAIRS);
		addWoodenStairsRecipe(SCContent.REINFORCED_WARPED_PLANKS, SCContent.REINFORCED_WARPED_STAIRS);

		//furnace recipes
		addSimpleCookingRecipe(SCContent.REINFORCED_BASALT, SCContent.REINFORCED_SMOOTH_BASALT);
		addSimpleCookingRecipe(SCContent.REINFORCED_CLAY, SCContent.REINFORCED_TERRACOTTA, 0.35F, 200);
		addSimpleCookingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE);
		addSimpleCookingRecipe(SCContent.REINFORCED_COBBLESTONE, SCContent.REINFORCED_STONE);
		addSimpleCookingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_CRACKED_DEEPSLATE_BRICKS);
		addSimpleCookingRecipe(SCContent.REINFORCED_DEEPSLATE_TILES, SCContent.REINFORCED_CRACKED_DEEPSLATE_TILES);
		addSimpleCookingRecipe(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_CRACKED_NETHER_BRICKS);
		addSimpleCookingRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_SMOOTH_QUARTZ);
		addSimpleCookingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, SCContent.REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS);
		addSimpleCookingRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE);
		addSimpleCookingRecipe(SCTags.Items.REINFORCED_SAND, SCContent.REINFORCED_GLASS);
		addSimpleCookingRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_SMOOTH_SANDSTONE);
		addSimpleCookingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_SMOOTH_STONE);
		addSimpleCookingRecipe(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_CRACKED_STONE_BRICKS);
		addSimpleCookingRecipe(SCContent.REINFORCED_BLACK_TERRACOTTA, SCContent.REINFORCED_BLACK_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_BLUE_TERRACOTTA, SCContent.REINFORCED_BLUE_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_BROWN_TERRACOTTA, SCContent.REINFORCED_BROWN_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_CYAN_TERRACOTTA, SCContent.REINFORCED_CYAN_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_GRAY_TERRACOTTA, SCContent.REINFORCED_GRAY_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_GREEN_TERRACOTTA, SCContent.REINFORCED_GREEN_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA, SCContent.REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA, SCContent.REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_LIME_TERRACOTTA, SCContent.REINFORCED_LIME_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_MAGENTA_TERRACOTTA, SCContent.REINFORCED_MAGENTA_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_ORANGE_TERRACOTTA, SCContent.REINFORCED_ORANGE_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_PINK_TERRACOTTA, SCContent.REINFORCED_PINK_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_PURPLE_TERRACOTTA, SCContent.REINFORCED_PURPLE_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_RED_TERRACOTTA, SCContent.REINFORCED_RED_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_WHITE_TERRACOTTA, SCContent.REINFORCED_WHITE_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.REINFORCED_YELLOW_TERRACOTTA, SCContent.REINFORCED_YELLOW_GLAZED_TERRACOTTA);
		addSimpleCookingRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.SMOOTH_CRYSTAL_QUARTZ);
		addSimpleCookingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ);

		//stonecutter recipes (ordered alphabetically by the ingredient)
		addStonecuttingRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CHISELED_CRYSTAL_QUARTZ, 1);
		addStonecuttingRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_BRICKS, 1);
		addStonecuttingRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_PILLAR, 1);
		addStonecuttingRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_SLAB, 2);
		addStonecuttingRecipe(SCContent.CRYSTAL_QUARTZ_BLOCK, SCContent.CRYSTAL_QUARTZ_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_ANDESITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_ANDESITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BASALT, SCContent.REINFORCED_POLISHED_BASALT, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_BLACKSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_BLACKSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_BRICKS, SCContent.REINFORCED_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_BRICKS, SCContent.REINFORCED_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_COBBLED_DEEPSLATE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_COBBLED_DEEPSLATE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_CHISELED_DEEPSLATE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILES, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILE_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLESTONE, SCContent.REINFORCED_COBBLESTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_COBBLESTONE, SCContent.REINFORCED_COBBLESTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_CUT_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_CUT_COPPER_SLAB, 8);
		addStonecuttingRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_CUT_COPPER_STAIRS, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_CHISELED_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_COPPER_BLOCK, SCContent.REINFORCED_COPPER_GRATE, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK, SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_CUT_COPPER, SCContent.REINFORCED_CUT_COPPER_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_CUT_COPPER, SCContent.REINFORCED_CUT_COPPER_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_CUT_COPPER, SCContent.REINFORCED_CHISELED_COPPER, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_CUT_RED_SANDSTONE, SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_CUT_SANDSTONE, SCContent.REINFORCED_CUT_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DARK_PRISMARINE, SCContent.REINFORCED_DARK_PRISMARINE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DARK_PRISMARINE, SCContent.REINFORCED_DARK_PRISMARINE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_TILES, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_TILE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_BRICKS, SCContent.REINFORCED_DEEPSLATE_TILE_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_TILES, SCContent.REINFORCED_DEEPSLATE_TILE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DEEPSLATE_TILES, SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_DIORITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_DIORITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_END_STONE, SCContent.REINFORCED_END_STONE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_END_STONE, SCContent.REINFORCED_END_STONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_END_STONE, SCContent.REINFORCED_END_STONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_END_STONE, SCContent.REINFORCED_END_STONE_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_END_STONE_BRICKS, SCContent.REINFORCED_END_STONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_END_STONE_BRICKS, SCContent.REINFORCED_END_STONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB, 8);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER_STAIRS, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_CHISELED_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_COPPER, SCContent.REINFORCED_EXPOSED_COPPER_GRATE, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_CUT_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_CUT_COPPER, SCContent.REINFORCED_EXPOSED_CUT_COPPER_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_EXPOSED_CUT_COPPER, SCContent.REINFORCED_EXPOSED_CHISELED_COPPER, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_GRANITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_GRANITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_MOSSY_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_MOSSY_COBBLESTONE, SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_MOSSY_STONE_BRICKS, SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_MOSSY_STONE_BRICKS, SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_MUD_BRICKS, SCContent.REINFORCED_MUD_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_MUD_BRICKS, SCContent.REINFORCED_MUD_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_CHISELED_NETHER_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_NETHER_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_NETHER_BRICKS, SCContent.REINFORCED_NETHER_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB, 8);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER_STAIRS, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_CHISELED_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_COPPER, SCContent.REINFORCED_OXIDIZED_COPPER_GRATE, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_CUT_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_CUT_COPPER, SCContent.REINFORCED_OXIDIZED_CUT_COPPER_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_OXIDIZED_CUT_COPPER, SCContent.REINFORCED_OXIDIZED_CHISELED_COPPER, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_ANDESITE, SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE, SCContent.REINFORCED_POLISHED_BLACKSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS, SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILES, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_DEEPSLATE_TILE_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DEEPSLATE, SCContent.REINFORCED_POLISHED_DEEPSLATE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_DIORITE, SCContent.REINFORCED_POLISHED_DIORITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_GRANITE, SCContent.REINFORCED_POLISHED_GRANITE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_CHISELED_TUFF_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_POLISHED_TUFF, SCContent.REINFORCED_TUFF_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_PRISMARINE, SCContent.REINFORCED_PRISMARINE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_PRISMARINE, SCContent.REINFORCED_PRISMARINE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_PRISMARINE_BRICKS, SCContent.REINFORCED_PRISMARINE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_PRISMARINE_BRICKS, SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_PURPUR_BLOCK, SCContent.REINFORCED_PURPUR_PILLAR, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_PURPUR_BLOCK, SCContent.REINFORCED_PURPUR_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_PURPUR_BLOCK, SCContent.REINFORCED_PURPUR_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_CHISELED_QUARTZ, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_PILLAR, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_QUARTZ_BLOCK, SCContent.REINFORCED_QUARTZ_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_NETHER_BRICKS, SCContent.REINFORCED_RED_NETHER_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_NETHER_BRICKS, SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_CHISELED_RED_SANDSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_CUT_RED_SANDSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_RED_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_RED_SANDSTONE, SCContent.REINFORCED_RED_SANDSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_RESIN_BRICKS, SCContent.REINFORCED_CHISELED_RESIN_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_RESIN_BRICKS, SCContent.REINFORCED_RESIN_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_RESIN_BRICKS, SCContent.REINFORCED_RESIN_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_CHISELED_SANDSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_CUT_SANDSTONE, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_CUT_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_SANDSTONE, SCContent.REINFORCED_SANDSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ, SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_QUARTZ, SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_QUARTZ, SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE, SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_SANDSTONE, SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_SANDSTONE, SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_SMOOTH_STONE, SCContent.REINFORCED_SMOOTH_STONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_CHISELED_STONE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_NORMAL_STONE_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE, SCContent.REINFORCED_STONE_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_CHISELED_TUFF, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_POLISHED_TUFF, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_POLISHED_TUFF_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_POLISHED_TUFF_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_POLISHED_TUFF_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_CHISELED_TUFF_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_BRICK_WALL, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF, SCContent.REINFORCED_TUFF_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF_BRICKS, SCContent.REINFORCED_CHISELED_TUFF_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF_BRICKS, SCContent.REINFORCED_TUFF_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_TUFF_BRICKS, SCContent.REINFORCED_TUFF_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_CHISELED_STONE_BRICKS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_STONE_BRICK_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_STONE_BRICKS, SCContent.REINFORCED_STONE_BRICK_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB, 8);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER_STAIRS, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_CHISELED_COPPER, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_COPPER, SCContent.REINFORCED_WEATHERED_COPPER_GRATE, 4);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_CUT_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER_SLAB, 2);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_CUT_COPPER, SCContent.REINFORCED_WEATHERED_CUT_COPPER_STAIRS, 1);
		addStonecuttingRecipe(SCContent.REINFORCED_WEATHERED_CUT_COPPER, SCContent.REINFORCED_WEATHERED_CHISELED_COPPER, 1);
		addStonecuttingRecipe(SCContent.SMOOTH_CRYSTAL_QUARTZ, SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB, 2);
		addStonecuttingRecipe(SCContent.SMOOTH_CRYSTAL_QUARTZ, SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS, 1);

		//@formatter:off
		//mossy conversions
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE)
		.requires(SCContent.REINFORCED_COBBLESTONE)
		.requires(Items.VINE)
		.unlockedBy("has_vine", has(Items.VINE))
		.save(output, "securitycraft:reinforced_mossy_cobblestone_from_vine");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE)
		.requires(SCContent.REINFORCED_COBBLESTONE)
		.requires(Blocks.MOSS_BLOCK)
		.unlockedBy("has_moss", has(Blocks.MOSS_BLOCK))
		.save(output, "securitycraft:reinforced_mossy_cobblestone_from_vanilla_moss");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE)
		.requires(Blocks.COBBLESTONE)
		.requires(SCContent.REINFORCED_MOSS_BLOCK)
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK))
		.save(output, "securitycraft:reinforced_mossy_cobblestone_from_vanilla_cobblestone");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_COBBLESTONE)
		.requires(SCContent.REINFORCED_COBBLESTONE)
		.requires(SCContent.REINFORCED_MOSS_BLOCK)
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK))
		.save(output, "securitycraft:reinforced_mossy_cobblestone_from_reinforced_ingredients");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS)
		.requires(SCContent.REINFORCED_STONE_BRICKS)
		.requires(Items.VINE)
		.unlockedBy("has_vine", has(Items.VINE))
		.save(output, "securitycraft:reinforced_mossy_stone_bricks_from_vine");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS)
		.requires(SCContent.REINFORCED_STONE_BRICKS)
		.requires(Blocks.MOSS_BLOCK)
		.unlockedBy("has_moss", has(Blocks.MOSS_BLOCK))
		.save(output, "securitycraft:reinforced_mossy_stone_bricks_from_vanilla_moss");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS)
		.requires(Blocks.STONE_BRICKS)
		.requires(SCContent.REINFORCED_MOSS_BLOCK)
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK))
		.save(output, "securitycraft:reinforced_mossy_stone_bricks_from_vanilla_stone_bricks");
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, SCContent.REINFORCED_MOSSY_STONE_BRICKS)
		.requires(SCContent.REINFORCED_STONE_BRICKS)
		.requires(SCContent.REINFORCED_MOSS_BLOCK)
		.unlockedBy("has_reinforced_moss", has(SCContent.REINFORCED_MOSS_BLOCK))
		.save(output, "securitycraft:reinforced_mossy_stone_bricks_from_reinforced_ingredients");
		//@formatter:on
	}

	protected final void addBarkRecipe(ItemLike log, ItemLike result) { //woof
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 3)
		.group("securitycraft:bark")
		.pattern("LL")
		.pattern("LL")
		.define('L', log)
		.unlockedBy("has_log", has(log))
		.save(output);
		//@formatter:on
	}

	protected final void addBlockMineRecipe(ItemLike input, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, result)
		.group("securitycraft:block_mines")
		.requires(input)
		.requires(SCContent.MINE)
		.unlockedBy("has_mine", has(SCContent.MINE))
		.save(output);
		//@formatter:on
	}

	protected final void addButtonRecipe(ItemLike input, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.REDSTONE, result)
		.requires(input)
		.unlockedBy("has_block", has(input))
		.save(output);
		//@formatter:on
	}

	protected final void addCarpetRecipe(ItemLike carpetMaterial, ItemLike carpet, TagKey<Item> unlockedBy) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, carpet, 3)
		.group("securitycraft:reinforced_carpets")
		.pattern("MM")
		.define('M', carpetMaterial)
		.unlockedBy("has_item", has(unlockedBy))
		.save(output);
		//@formatter:on
	}

	protected final void addColoredCarpetRecipes(TagKey<Item> dye, ItemLike wool, ItemLike carpet) {
		addCarpetRecipe(wool, carpet, SCTags.Items.REINFORCED_WOOL);
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, carpet)
		.group("securitycraft:reinforced_carpets")
		.requires(dye)
		.requires(SCTags.Items.REINFORCED_WOOL_CARPETS)
		.unlockedBy("has_wool_carpet", has(SCTags.Items.REINFORCED_WOOL_CARPETS))
		.save(output, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(Utils.getRegistryName(carpet.asItem()).toString() + "_from_dye")));
		//@formatter:on
	}

	protected final void addColoredLensRecipe(Item dye, ItemLike pane) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, applyDyes(new ItemStack(SCContent.LENS.get()), (DyeItem) dye))
		.group("securitycraft:lens")
		.pattern(" P")
		.pattern("P ")
		.pattern(" P")
		.define('P', pane)
		.unlockedBy("has_lens", has(SCContent.LENS))
		.save(output, scRecipeId(((DyeItem) dye).getDyeColor().getName() + "_lens"));
		//@formatter:on
	}

	protected final void addChiselingRecipe(ItemLike slab, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result)
		.pattern("S")
		.pattern("S")
		.define('S', slab)
		.unlockedBy("has_slab", has(slab))
		.save(output);
		//@formatter:on
	}

	protected final void addColoredWoolRecipe(TagKey<Item> dye, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, result)
		.group("securitycraft:reinforced_wool")
		.requires(dye)
		.requires(SCTags.Items.REINFORCED_WOOL)
		.unlockedBy("has_wool", has(SCTags.Items.REINFORCED_WOOL))
		.save(output);
		//@formatter:on
	}

	protected final void addCompressingRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result)
		.pattern("BBB")
		.pattern("BBB")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addCopperGrateRecipe(ItemLike block, ItemLike grate) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, grate, 4)
		.pattern(" C ")
		.pattern("C C")
		.pattern(" C ")
		.define('C', block)
		.unlockedBy("has_copper_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addCopperBulbRecipe(ItemLike copperBlock, ItemLike bulb) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, bulb, 4)
		.pattern(" C ")
		.pattern("CBC")
		.pattern(" R ")
		.define('C', copperBlock)
		.define('B', Tags.Items.RODS_BLAZE)
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_copper_block", has(copperBlock))
		.save(output);
		//@formatter:on
	}

	protected final void addFenceRecipe(ItemLike material, ItemLike result) {
		addFenceRecipe(material, Tags.Items.RODS_WOODEN, result, 3, true);
	}

	protected final void addFenceRecipe(ItemLike material, TagKey<Item> stick, ItemLike result, int amount, boolean group) {
		//@formatter:off
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, result, amount)
		.pattern("MSM")
		.pattern("MSM")
		.define('M', material)
		.define('S', stick)
		.unlockedBy("has_stick", has(stick));
		//@formatter:on

		if (group)
			builder.group("securitycraft:reinforced_fences");

		builder.save(output);
	}

	protected final void addFenceGateRecipe(ItemLike material, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, result)
		.group("securitycraft:reinforced_fence_gates")
		.pattern("SMS")
		.pattern("SMS")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('M', material)
		.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
		.save(output);
		//@formatter:on
	}

	protected final void addKeycardRecipe(TagKey<Item> specialIngredient, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, result)
		.group("securitycraft:keycards")
		.pattern("III")
		.pattern("SSS")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		//@formatter:on
	}

	protected final void addKeycardResetRecipe(ItemLike keycard) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, keycard)
		.requires(keycard)
		.unlockedBy("has_keycard", has(keycard))
		.save(output, scRecipeId(Utils.getRegistryName(keycard.asItem()).getPath() + "_reset"));
		//@formatter:on
	}

	protected final void addModuleRecipe(ItemLike specialIngredient, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, result)
		.group("securitycraft:modules")
		.pattern("III")
		.pattern("IPI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', Items.PAPER)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		//@formatter:on
	}

	protected final void addModuleRecipe(TagKey<Item> specialIngredient, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, result)
		.group("securitycraft:modules")
		.pattern("III")
		.pattern("IPI")
		.pattern("ISI")
		.define('I', Tags.Items.INGOTS_IRON)
		.define('P', Items.PAPER)
		.define('S', specialIngredient)
		.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
		.save(output);
		//@formatter:on
	}

	protected final void addPillarRecipe(ItemLike block, ItemLike result) {
		addPillarRecipe(block, result, 2);
	}

	protected final void addPillarRecipe(ItemLike block, ItemLike result, int amount) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, amount)
		.pattern("B")
		.pattern("B")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addPlanksRecipe(TagKey<Item> log, ItemLike result) {
		addPlanksRecipe(log, result, 4);
	}

	protected final void addPlanksRecipe(TagKey<Item> log, ItemLike result, int amount) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, result, amount)
		.group("securitycraft:reinforced_planks")
		.requires(log)
		.unlockedBy("has_log", has(log))
		.save(output);
		//@formatter:on
	}

	protected final void addPressurePlateRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, result)
		.pattern("SS")
		.define('S', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addSecretHangingSignRecipe(ItemLike vanillaSign, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.DECORATIONS, result, 3)
		.group("securitycraft:secret_hanging_signs")
		.requires(vanillaSign, 3)
		.requires(SCContent.RETINAL_SCANNER)
		.unlockedBy("has_sign", has(ItemTags.SIGNS))
		.save(output);
		//@formatter:on
	}

	protected final void addSecretSignRecipe(ItemLike vanillaSign, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.DECORATIONS, result, 3)
		.group("securitycraft:secret_signs")
		.requires(vanillaSign, 3)
		.requires(SCContent.RETINAL_SCANNER)
		.unlockedBy("has_sign", has(ItemTags.SIGNS))
		.save(output);
		//@formatter:on
	}

	protected final void addSecuritySeaBoatRecipe(ItemLike planks, ItemLike boat) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, boat)
		.group("securitycraft:security_sea_boats")
		.pattern("PCP")
		.pattern("PPP")
		.define('P', planks)
		.define('C', SCContent.KEYPAD_CHEST)
		.unlockedBy("has_keypad_chest", has(SCContent.KEYPAD_CHEST))
		.save(output);
		//@formatter:on
	}

	protected final void addSimpleCookingRecipe(ItemLike input, ItemLike output) {
		addSimpleCookingRecipe(input, output, 0.1F, 200);
	}

	protected final void addSimpleCookingRecipe(TagKey<Item> input, ItemLike output) {
		addSimpleCookingRecipe(input, output, 0.1F, 200);
	}

	protected final void addSimpleCookingRecipe(ItemLike input, ItemLike output, float xp, int time) {
		//@formatter:off
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, output, xp, time)
		.unlockedBy("has_item", has(input))
		.save(this.output);
		//@formatter:on
	}

	protected final void addSimpleCookingRecipe(TagKey<Item> input, ItemLike output, float xp, int time) {
		//@formatter:off
		SimpleCookingRecipeBuilder.smelting(tag(input), RecipeCategory.BUILDING_BLOCKS, output, xp, time)
		.unlockedBy("has_item", has(input))
		.save(this.output);
		//@formatter:on
	}

	protected final void addSlabRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 6)
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addSlabRecipe(Ingredient block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 6)
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block.getValues().get(0).value()))
		.save(output);
		//@formatter:on
	}

	protected final void addStainedGlassRecipe(TagKey<Item> dye, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 8)
		.group("securitycraft:reinforced_glass")
		.pattern("GGG")
		.pattern("GDG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS)
		.define('D', dye)
		.unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
		.save(output);
		//@formatter:on
	}

	protected final void addStainedGlassPaneRecipes(TagKey<Item> dye, ItemLike stainedGlass, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, result, 8)
		.group("securitycraft:reinforced_glass_panes")
		.pattern("GGG")
		.pattern("GDG")
		.pattern("GGG")
		.define('G', SCContent.REINFORCED_GLASS_PANE)
		.define('D', dye)
		.unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
		.save(output, scRecipeId(Utils.getRegistryName(result.asItem()).getPath() + "_from_dye"));
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, result, 16)
		.group("securitycraft:reinforced_glass_panes")
		.pattern("GGG")
		.pattern("GGG")
		.define('G', stainedGlass)
		.unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
		.save(output);
		//@formatter:on
	}

	protected final void addStainedTerracottaRecipe(TagKey<Item> dye, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 8)
		.group("securitycraft:reinforced_terracotta")
		.pattern("TTT")
		.pattern("TDT")
		.pattern("TTT")
		.define('T', SCContent.REINFORCED_TERRACOTTA)
		.define('D', dye)
		.unlockedBy("has_reinforced_terracotta", has(SCContent.REINFORCED_TERRACOTTA))
		.save(output);
		//@formatter:on
	}

	protected final void addStairsRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 4)
		.pattern("B  ")
		.pattern("BB ")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addStairsRecipe(Ingredient block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 4)
		.pattern("B  ")
		.pattern("BB ")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block.getValues().get(0).value()))
		.save(output);
		//@formatter:on
	}

	protected final void addStonecuttingRecipe(ItemLike ingredient, ItemLike result, int count) {
		//@formatter:off
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, count)
		.unlockedBy("has_" + Utils.getRegistryName(ingredient.asItem()).getPath(), has(ingredient))
		.save(output, Utils.getRegistryName(result.asItem()) + "_from_" + Utils.getRegistryName(ingredient.asItem()).getPath() + "_stonecutting");
		//@formatter:on
	}

	protected final void addTwoByTwoRecipe(ItemLike block, ItemLike result) {
		addTwoByTwoRecipe(block, result, 4);
	}

	protected final void addTwoByTwoRecipe(ItemLike block, ItemLike result, int amount) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, amount)
		.pattern("BB")
		.pattern("BB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addWallRecipes(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, result, 6)
		.pattern("BBB")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		addStonecuttingRecipe(block, result, 1);
		//@formatter:on
	}

	protected final void addWoodenButtonRecipe(ItemLike input, ItemLike result) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.REDSTONE, result)
		.group("securitycraft:wooden_buttons")
		.requires(input)
		.unlockedBy("has_block", has(input))
		.save(output);
		//@formatter:on
	}

	protected final void addWoodenPressurePlateRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, result)
		.group("securitycraft:wooden_pressure_plates")
		.pattern("SS")
		.define('S', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addWoodenSlabRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 6)
		.group("securitycraft:wooden_slab")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	protected final void addWoodenStairsRecipe(ItemLike block, ItemLike result) {
		//@formatter:off
		ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result, 4)
		.group("securitycraft:wooden_stairs")
		.pattern("B  ")
		.pattern("BB ")
		.pattern("BBB")
		.define('B', block)
		.unlockedBy("has_block", has(block))
		.save(output);
		//@formatter:on
	}

	//copy of DyedItemColor#applyDyes to remove the tag check
	public static ItemStack applyDyes(ItemStack stack, DyeItem dye) {
		ItemStack copy = stack.copyWithCount(1);
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		DyedItemColor existingColor = copy.get(DataComponents.DYED_COLOR);

		if (existingColor != null) {
			int j1 = ARGB.red(existingColor.rgb());
			int k1 = ARGB.green(existingColor.rgb());
			int l1 = ARGB.blue(existingColor.rgb());

			l += Math.max(j1, Math.max(k1, l1));
			i += j1;
			j += k1;
			k += l1;
			i1++;
		}

		int j3 = dye.getDyeColor().getTextureDiffuseColor();
		int i2 = ARGB.red(j3);
		int j2 = ARGB.green(j3);
		int k2 = ARGB.blue(j3);

		l += Math.max(i2, Math.max(j2, k2));
		i += i2;
		j += j2;
		k += k2;
		i1++;

		int l2 = i / i1;
		int i3 = j / i1;
		int k3 = k / i1;
		float f = (float) l / (float) i1;
		float f1 = Math.max(l2, Math.max(i3, k3));

		l2 = (int) (l2 * f / f1);
		i3 = (int) (i3 * f / f1);
		k3 = (int) (k3 * f / f1);

		int l3 = ARGB.color(0, l2, i3, k3);

		copy.set(DataComponents.DYED_COLOR, new DyedItemColor(l3));
		return copy;
	}

	private static ResourceKey<Recipe<?>> scRecipeId(String path) {
		return ResourceKey.create(Registries.RECIPE, SecurityCraft.resLoc(path));
	}

	public static final class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
			return new RecipeGenerator(lookupProvider, output);
		}

		@Override
		public String getName() {
			return "SecurityCraft recipes";
		}
	}
}
