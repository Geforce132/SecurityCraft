package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.blocks.BlockCageTrap;
import net.geforcemods.securitycraft.blocks.BlockFakeLava;
import net.geforcemods.securitycraft.blocks.BlockFakeLavaBase;
import net.geforcemods.securitycraft.blocks.BlockFakeWater;
import net.geforcemods.securitycraft.blocks.BlockFakeWaterBase;
import net.geforcemods.securitycraft.blocks.BlockFrame;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.blocks.BlockInventoryScannerField;
import net.geforcemods.securitycraft.blocks.BlockIronFence;
import net.geforcemods.securitycraft.blocks.BlockIronTrapDoor;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockLaserField;
import net.geforcemods.securitycraft.blocks.BlockLogger;
import net.geforcemods.securitycraft.blocks.BlockPanicButton;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.mines.BlockBouncingBetty;
import net.geforcemods.securitycraft.blocks.mines.BlockClaymore;
import net.geforcemods.securitycraft.blocks.mines.BlockFullMineBase;
import net.geforcemods.securitycraft.blocks.mines.BlockFurnaceMine;
import net.geforcemods.securitycraft.blocks.mines.BlockIMS;
import net.geforcemods.securitycraft.blocks.mines.BlockMine;
import net.geforcemods.securitycraft.blocks.mines.BlockTrackMine;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedBase;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedConcrete;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedDoor;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedFenceGate;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedGlass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedIronBars;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedMetals;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedNewLog;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedOldLog;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedPrismarine;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedPurpur;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedQuartz;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedRedSandstone;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSandstone;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs2;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedHardenedClay;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStairs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStone;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWood;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWool;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemAdminTool;
import net.geforcemods.securitycraft.items.ItemBriefcase;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.items.ItemCodebreaker;
import net.geforcemods.securitycraft.items.ItemKeyPanel;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.items.ItemMineRemoteAccessTool;
import net.geforcemods.securitycraft.items.ItemModifiedBucket;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.items.ItemReinforcedDoor;
import net.geforcemods.securitycraft.items.ItemSCManual;
import net.geforcemods.securitycraft.items.ItemScannerDoor;
import net.geforcemods.securitycraft.items.ItemTaser;
import net.geforcemods.securitycraft.items.ItemUniversalBlockModifier;
import net.geforcemods.securitycraft.items.ItemUniversalBlockReinforcer;
import net.geforcemods.securitycraft.items.ItemUniversalBlockRemover;
import net.geforcemods.securitycraft.items.ItemUniversalKeyChanger;
import net.geforcemods.securitycraft.items.ItemUniversalOwnerChanger;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class SetupHandler
{
	public static void setupBlocks()
	{
		SCContent.laserBlock = new BlockLaserBlock(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("laser_block").setTranslationKey("laserBlock");
		SCContent.laserField = new BlockLaserField(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setRegistryName("laser");

		SCContent.keypad = new BlockKeypad(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad").setTranslationKey("keypad");

		SCContent.retinalScanner = new BlockRetinalScanner(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("retinal_scanner").setTranslationKey("retinalScanner");

		SCContent.reinforcedDoor = new BlockReinforcedDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("iron_door_reinforced").setTranslationKey("ironDoorReinforced");

		SCContent.bogusLava = (BlockStaticLiquid) new BlockFakeLavaBase(Material.LAVA).setHardness(100.0F).setLightLevel(1.0F).setRegistryName("bogus_lava").setTranslationKey("bogusLava");
		SCContent.bogusLavaFlowing = new BlockFakeLava(Material.LAVA).setHardness(0.0F).setLightLevel(1.0F).setRegistryName("bogus_lava_flowing").setTranslationKey("bogusLavaFlowing");
		SCContent.bogusWater = (BlockStaticLiquid) new BlockFakeWaterBase(Material.WATER).setHardness(100.0F).setRegistryName("bogus_water").setTranslationKey("bogusWater");
		SCContent.bogusWaterFlowing = new BlockFakeWater(Material.WATER).setHardness(0.0F).setRegistryName("bogus_water_flowing").setTranslationKey("bogusWaterFlowing");

		SCContent.keycardReader = new BlockKeycardReader(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keycard_reader").setTranslationKey("keycardReader");

		SCContent.ironTrapdoor = new BlockIronTrapDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_iron_trapdoor").setTranslationKey("reinforcedIronTrapdoor");

		SCContent.inventoryScanner = new BlockInventoryScanner(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("inventory_scanner").setTranslationKey("inventoryScanner");
		SCContent.inventoryScannerField = new BlockInventoryScannerField(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("inventory_scanner_field").setTranslationKey("inventoryScannerField");

		SCContent.cageTrap = new BlockCageTrap(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("cage_trap").setTranslationKey("cageTrap");

		SCContent.portableRadar = new BlockPortableRadar(Material.CIRCUITS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("portable_radar").setTranslationKey("portableRadar");

		SCContent.unbreakableIronBars = new BlockReinforcedIronBars(Material.IRON, true).setCreativeTab(SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setRegistryName("reinforced_iron_bars").setTranslationKey("reinforcedIronBars");

		SCContent.keypadChest = new BlockKeypadChest().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_chest").setTranslationKey("keypadChest");

		SCContent.usernameLogger = new BlockLogger(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("username_logger").setTranslationKey("usernameLogger");

		SCContent.alarm = new BlockAlarm(Material.IRON, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("alarm").setTranslationKey("alarm");
		SCContent.alarmLit = new BlockAlarm(Material.IRON, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setRegistryName("alarm_lit").setTranslationKey("alarmLit");

		SCContent.reinforcedStone = new BlockReinforcedStone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone").setTranslationKey("reinforcedStone");

		SCContent.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_fence_gate").setTranslationKey("reinforcedFenceGate");

		SCContent.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_planks").setTranslationKey("reinforcedPlanks");

		SCContent.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("panic_button").setTranslationKey("panicButton");

		SCContent.frame = new BlockFrame(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_frame").setTranslationKey("keypadFrame");

		SCContent.keypadFurnace = new BlockKeypadFurnace(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_furnace").setTranslationKey("keypadFurnace");

		SCContent.securityCamera = new BlockSecurityCamera(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("security_camera").setTranslationKey("securityCamera");

		SCContent.reinforcedStairsOak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_oak").setTranslationKey("reinforcedStairsOak");
		SCContent.reinforcedStairsSpruce = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_spruce").setTranslationKey("reinforcedStairsSpruce");
		SCContent.reinforcedStairsBirch = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_birch").setTranslationKey("reinforcedStairsBirch");
		SCContent.reinforcedStairsJungle = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_jungle").setTranslationKey("reinforcedStairsJungle");
		SCContent.reinforcedStairsAcacia = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_acacia").setTranslationKey("reinforcedStairsAcacia");
		SCContent.reinforcedStairsDarkoak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_darkoak").setTranslationKey("reinforcedStairsDarkoak");
		SCContent.reinforcedStairsStone = new BlockReinforcedStairs(SCContent.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone").setTranslationKey("reinforcedStairsStone");

		SCContent.ironFence = new BlockIronFence(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("electrified_iron_fence").setTranslationKey("electrifiedIronFence");

		SCContent.reinforcedGlass = new BlockReinforcedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_block").setTranslationKey("reinforcedGlassBlock");
		SCContent.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_glass").setTranslationKey("reinforcedStainedGlass");

		SCContent.reinforcedDirt = new BlockReinforcedBase(Material.GROUND, 1, Blocks.DIRT).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_dirt").setTranslationKey("reinforcedDirt");

		SCContent.reinforcedCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_cobblestone").setTranslationKey("reinforcedCobblestone");
		SCContent.reinforcedStairsCobblestone = new BlockReinforcedStairs(SCContent.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_cobblestone").setTranslationKey("reinforcedStairsCobblestone");

		SCContent.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sandstone").setTranslationKey("reinforcedSandstone");
		SCContent.reinforcedStairsSandstone = new BlockReinforcedStairs(SCContent.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_sandstone").setTranslationKey("reinforcedStairsSandstone");

		SCContent.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wood_slabs").setTranslationKey("reinforcedWoodSlabs");
		SCContent.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_wood_slabs").setTranslationKey("reinforcedDoubleWoodSlabs");
		SCContent.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs").setTranslationKey("reinforcedStoneSlabs");
		SCContent.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs").setTranslationKey("reinforcedDoubleStoneSlabs");


		SCContent.protecto = new BlockProtecto(Material.IRON).setBlockUnbreakable().setResistance(1000F).setLightLevel(0.5F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("protecto").setTranslationKey("protecto");

		SCContent.scannerDoor = new BlockScannerDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("scanner_door").setTranslationKey("scannerDoor");

		SCContent.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_brick").setTranslationKey("reinforcedStoneBrick");
		SCContent.reinforcedStairsStoneBrick= new BlockReinforcedStairs(SCContent.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone_brick").setTranslationKey("reinforcedStairsStoneBrick");

		SCContent.reinforcedMossyCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.MOSSY_COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_mossy_cobblestone").setTranslationKey("reinforcedMossyCobblestone");

		SCContent.reinforcedBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.BRICK_BLOCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_brick").setTranslationKey("reinforcedBrick");
		SCContent.reinforcedStairsBrick= new BlockReinforcedStairs(SCContent.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_brick").setTranslationKey("reinforcedStairsBrick");

		SCContent.reinforcedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_nether_brick").setTranslationKey("reinforcedNetherBrick");
		SCContent.reinforcedStairsNetherBrick= new BlockReinforcedStairs(SCContent.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_nether_brick").setTranslationKey("reinforcedStairsNetherBrick");

		SCContent.reinforcedHardenedClay = new BlockReinforcedBase(Material.ROCK, 1, Blocks.HARDENED_CLAY).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_hardened_clay").setTranslationKey("reinforcedHardenedClay");
		SCContent.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_hardened_clay").setTranslationKey("reinforcedStainedHardenedClay");

		SCContent.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs").setTranslationKey("reinforcedLogs");
		SCContent.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs2").setTranslationKey("reinforcedLogs2");

		SCContent.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_metals").setTranslationKey("reinforcedMetals");
		SCContent.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_compressed_blocks").setTranslationKey("reinforcedCompressedBlocks");

		SCContent.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wool").setTranslationKey("reinforcedWool");

		SCContent.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_quartz").setTranslationKey("reinforcedQuartz");
		SCContent.reinforcedStairsQuartz = new BlockReinforcedStairs(SCContent.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_quartz").setTranslationKey("reinforcedStairsQuartz");

		SCContent.reinforcedPrismarine = new BlockReinforcedPrismarine().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_prismarine").setTranslationKey("reinforcedPrismarine");

		SCContent.reinforcedRedSandstone = new BlockReinforcedRedSandstone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_sandstone").setTranslationKey("reinforcedRedSandstone");
		SCContent.reinforcedStairsRedSandstone = new BlockReinforcedStairs(SCContent.reinforcedRedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_red_sandstone").setTranslationKey("reinforcedStairsRedSandstone");

		SCContent.reinforcedStoneSlabs2 = new BlockReinforcedSlabs2(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs2").setTranslationKey("reinforcedStoneSlabs2");
		SCContent.reinforcedDoubleStoneSlabs2 = new BlockReinforcedSlabs2(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs2").setTranslationKey("reinforcedDoubleStoneSlabs2");

		SCContent.reinforcedEndStoneBricks = new BlockReinforcedBase(Material.ROCK, 1, Blocks.END_BRICKS).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone_bricks").setTranslationKey("reinforcedEndStoneBricks");

		SCContent.reinforcedRedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.RED_NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_nether_brick").setTranslationKey("reinforcedRedNetherBrick");

		SCContent.reinforcedPurpur = new BlockReinforcedPurpur().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_purpur").setTranslationKey("reinforcedPurpur");
		SCContent.reinforcedStairsPurpur = new BlockReinforcedStairs(SCContent.reinforcedPurpur, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_purpur").setTranslationKey("reinforcedStairsPurpur");

		SCContent.reinforcedConcrete = new BlockReinforcedConcrete().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_concrete").setTranslationKey("reinforcedConcrete");
	}

	public static void setupMines()
	{
		SCContent.mine = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("mine").setTranslationKey("mine");
		SCContent.mineCut = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setRegistryName("mine_cut").setTranslationKey("mineCut");

		SCContent.dirtMine = new BlockFullMineBase(Material.GROUND, Blocks.DIRT).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setRegistryName("dirt_mine").setTranslationKey("dirtMine");
		SCContent.stoneMine = new BlockFullMineBase(Material.ROCK, Blocks.STONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 2.5F).setRegistryName("stone_mine").setTranslationKey("stoneMine");
		SCContent.cobblestoneMine = new BlockFullMineBase(Material.ROCK, Blocks.COBBLESTONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 2.75F).setRegistryName("cobblestone_mine").setTranslationKey("cobblestoneMine");
		SCContent.sandMine = new BlockFullMineBase(Material.SAND, Blocks.SAND).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setRegistryName("sand_mine").setTranslationKey("sandMine");
		SCContent.diamondOreMine = new BlockFullMineBase(Material.ROCK, Blocks.DIAMOND_ORE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 3.75F).setRegistryName("diamond_mine").setTranslationKey("diamondMine");
		SCContent.furnaceMine = new BlockFurnaceMine(Material.ROCK).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 3.75F).setRegistryName("furnace_mine").setTranslationKey("furnaceMine");

		SCContent.trackMine = new BlockTrackMine().setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 0.7F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("track_mine").setTranslationKey("trackMine");

		SCContent.bouncingBetty = new BlockBouncingBetty(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("bouncing_betty").setTranslationKey("bouncingBetty");

		SCContent.claymore = new BlockClaymore(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("claymore").setTranslationKey("claymore");

		SCContent.ims = new BlockIMS(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("ims").setTranslationKey("ims");
	}

	public static void setupItems()
	{
		SCContent.codebreaker = new ItemCodebreaker().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("codebreaker").setTranslationKey("codebreaker");

		SCContent.keycardLV1 = new ItemKeycardBase(0).setRegistryName("keycard_lv1").setTranslationKey("keycardLV1");
		SCContent.keycardLV2 = new ItemKeycardBase(1).setRegistryName("keycard_lv2").setTranslationKey("keycardLV2");
		SCContent.keycardLV3 = new ItemKeycardBase(2).setRegistryName("keycard_lv3").setTranslationKey("keycardLV3");
		SCContent.keycardLV4 = new ItemKeycardBase(4).setRegistryName("keycard_lv4").setTranslationKey("keycardLV4");
		SCContent.keycardLV5 = new ItemKeycardBase(5).setRegistryName("keycard_lv5").setTranslationKey("keycardLV5");
		SCContent.limitedUseKeycard = new ItemKeycardBase(3).setRegistryName("limited_use_keycard").setTranslationKey("limitedUseKeycard");

		SCContent.reinforcedDoorItem = new ItemReinforcedDoor().setRegistryName("door_indestructible_iron_item").setTranslationKey("doorIndestructibleIronItem").setCreativeTab(SecurityCraft.tabSCDecoration);

		SCContent.universalBlockRemover = new ItemUniversalBlockRemover().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_remover").setTranslationKey("universalBlockRemover");

		SCContent.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("remote_access_mine").setTranslationKey("remoteAccessMine");

		SCContent.fWaterBucket = new ItemModifiedBucket(SCContent.bogusWaterFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_water").setTranslationKey("bucketFWater");

		SCContent.fLavaBucket = new ItemModifiedBucket(SCContent.bogusLavaFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_lava").setTranslationKey("bucketFLava");

		SCContent.universalBlockModifier = new ItemUniversalBlockModifier().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_modifier").setTranslationKey("universalBlockModifier");

		SCContent.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setRegistryName("redstone_module").setTranslationKey("redstoneModule");
		SCContent.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setRegistryName("whitelist_module").setTranslationKey("whitelistModule");
		SCContent.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setRegistryName("blacklist_module").setTranslationKey("blacklistModule");
		SCContent.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setRegistryName("harming_module").setTranslationKey("harmingModule");
		SCContent.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setRegistryName("smart_module").setTranslationKey("smartModule");
		SCContent.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setRegistryName("storage_module").setTranslationKey("storageModule");
		SCContent.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setRegistryName("disguise_module").setTranslationKey("disguiseModule");

		SCContent.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("wire_cutters").setTranslationKey("wireCutters");

		SCContent.keyPanel = new ItemKeyPanel().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_item").setTranslationKey("keypadItem");

		SCContent.adminTool = new ItemAdminTool().setMaxStackSize(1).setRegistryName("admin_tool").setTranslationKey("adminTool");

		SCContent.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("camera_monitor").setTranslationKey("cameraMonitor");

		SCContent.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("sc_manual").setTranslationKey("scManual");

		SCContent.taser = new ItemTaser().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("taser").setTranslationKey("taser");

		SCContent.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_owner_changer").setTranslationKey("universalOwnerChanger");


		SCContent.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl1").setTranslationKey("universalBlockReinforcerLvL1");
		SCContent.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl2").setTranslationKey("universalBlockReinforcerLvL2");
		SCContent.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl3").setTranslationKey("universalBlockReinforcerLvL3");

		SCContent.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("briefcase").setTranslationKey("briefcase");

		SCContent.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_key_changer").setTranslationKey("universalKeyChanger");

		SCContent.scannerDoorItem = new ItemScannerDoor().setRegistryName("scanner_door_item").setTranslationKey("scannerDoorItem").setCreativeTab(SecurityCraft.tabSCDecoration);
	}
}
