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
import net.geforcemods.securitycraft.blocks.BlockMotionActivatedLight;
import net.geforcemods.securitycraft.blocks.BlockPanicButton;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.blocks.BlockProtecto;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.blocks.BlockSecretSignStanding;
import net.geforcemods.securitycraft.blocks.BlockSecretSignWall;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.mines.BlockBouncingBetty;
import net.geforcemods.securitycraft.blocks.mines.BlockClaymore;
import net.geforcemods.securitycraft.blocks.mines.BlockFullMineBase;
import net.geforcemods.securitycraft.blocks.mines.BlockFullMineFalling;
import net.geforcemods.securitycraft.blocks.mines.BlockFurnaceMine;
import net.geforcemods.securitycraft.blocks.mines.BlockIMS;
import net.geforcemods.securitycraft.blocks.mines.BlockMine;
import net.geforcemods.securitycraft.blocks.mines.BlockTrackMine;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedBase;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedBoneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedCarpet;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedConcrete;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedDoor;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedFenceGate;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedGlass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedGlassPane;
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
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedGlassPanes;
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
import net.geforcemods.securitycraft.items.ItemSecretSign;
import net.geforcemods.securitycraft.items.ItemTaser;
import net.geforcemods.securitycraft.items.ItemUniversalBlockReinforcer;
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
		SCContent.laserBlock = new BlockLaserBlock(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("laser_block").setTranslationKey("securitycraft:laserBlock");
		SCContent.laserField = new BlockLaserField(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setRegistryName("laser");

		SCContent.keypad = new BlockKeypad(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad").setTranslationKey("securitycraft:keypad");

		SCContent.retinalScanner = new BlockRetinalScanner(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("retinal_scanner").setTranslationKey("securitycraft:retinalScanner");

		SCContent.reinforcedDoor = new BlockReinforcedDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("iron_door_reinforced").setTranslationKey("securitycraft:ironDoorReinforced");

		SCContent.bogusLava = (BlockStaticLiquid) new BlockFakeLavaBase(Material.LAVA).setHardness(100.0F).setLightLevel(1.0F).setRegistryName("bogus_lava").setTranslationKey("securitycraft:bogusLava");
		SCContent.bogusLavaFlowing = new BlockFakeLava(Material.LAVA).setHardness(0.0F).setLightLevel(1.0F).setRegistryName("bogus_lava_flowing").setTranslationKey("securitycraft:bogusLavaFlowing");
		SCContent.bogusWater = (BlockStaticLiquid) new BlockFakeWaterBase(Material.WATER).setHardness(100.0F).setRegistryName("bogus_water").setTranslationKey("securitycraft:bogusWater");
		SCContent.bogusWaterFlowing = new BlockFakeWater(Material.WATER).setHardness(0.0F).setRegistryName("bogus_water_flowing").setTranslationKey("securitycraft:bogusWaterFlowing");

		SCContent.keycardReader = new BlockKeycardReader(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keycard_reader").setTranslationKey("securitycraft:keycardReader");

		SCContent.ironTrapdoor = new BlockIronTrapDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_iron_trapdoor").setTranslationKey("securitycraft:reinforcedIronTrapdoor");

		SCContent.inventoryScanner = new BlockInventoryScanner(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("inventory_scanner").setTranslationKey("securitycraft:inventoryScanner");
		SCContent.inventoryScannerField = new BlockInventoryScannerField(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("inventory_scanner_field").setTranslationKey("securitycraft:inventoryScannerField");

		SCContent.cageTrap = new BlockCageTrap(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("cage_trap").setTranslationKey("securitycraft:cageTrap");

		SCContent.portableRadar = new BlockPortableRadar(Material.CIRCUITS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("portable_radar").setTranslationKey("securitycraft:portableRadar");

		SCContent.reinforcedIronBars = new BlockReinforcedIronBars(Material.IRON, true).setCreativeTab(SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setRegistryName("reinforced_iron_bars").setTranslationKey("securitycraft:reinforcedIronBars");

		SCContent.keypadChest = new BlockKeypadChest().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_chest").setTranslationKey("securitycraft:keypadChest");

		SCContent.usernameLogger = new BlockLogger(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("username_logger").setTranslationKey("securitycraft:usernameLogger");

		SCContent.alarm = new BlockAlarm(Material.IRON, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("alarm").setTranslationKey("securitycraft:alarm");
		SCContent.alarmLit = new BlockAlarm(Material.IRON, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setRegistryName("alarm_lit").setTranslationKey("securitycraft:alarmLit");

		SCContent.reinforcedStone = new BlockReinforcedStone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone").setTranslationKey("securitycraft:reinforcedStone");

		SCContent.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_fence_gate").setTranslationKey("securitycraft:reinforcedFenceGate");

		SCContent.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_planks").setTranslationKey("securitycraft:reinforcedPlanks");

		SCContent.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("panic_button").setTranslationKey("securitycraft:panicButton");

		SCContent.frame = new BlockFrame(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_frame").setTranslationKey("securitycraft:keypadFrame");

		SCContent.keypadFurnace = new BlockKeypadFurnace(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_furnace").setTranslationKey("securitycraft:keypadFurnace");

		SCContent.securityCamera = new BlockSecurityCamera(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("security_camera").setTranslationKey("securitycraft:securityCamera");

		SCContent.reinforcedStairsOak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_oak").setTranslationKey("securitycraft:reinforcedStairsOak");
		SCContent.reinforcedStairsSpruce = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_spruce").setTranslationKey("securitycraft:reinforcedStairsSpruce");
		SCContent.reinforcedStairsBirch = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_birch").setTranslationKey("securitycraft:reinforcedStairsBirch");
		SCContent.reinforcedStairsJungle = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_jungle").setTranslationKey("securitycraft:reinforcedStairsJungle");
		SCContent.reinforcedStairsAcacia = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_acacia").setTranslationKey("securitycraft:reinforcedStairsAcacia");
		SCContent.reinforcedStairsDarkoak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_darkoak").setTranslationKey("securitycraft:reinforcedStairsDarkoak");
		SCContent.reinforcedStairsStone = new BlockReinforcedStairs(SCContent.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone").setTranslationKey("securitycraft:reinforcedStairsStone");

		SCContent.ironFence = new BlockIronFence(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("electrified_iron_fence").setTranslationKey("securitycraft:electrifiedIronFence");

		SCContent.reinforcedGlass = new BlockReinforcedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_block").setTranslationKey("securitycraft:reinforcedGlassBlock");
		SCContent.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_glass").setTranslationKey("securitycraft:reinforcedStainedGlass");

		SCContent.reinforcedDirt = new BlockReinforcedBase(Material.GROUND, 1, Blocks.DIRT).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_dirt").setTranslationKey("securitycraft:reinforcedDirt");

		SCContent.reinforcedCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_cobblestone").setTranslationKey("securitycraft:reinforcedCobblestone");
		SCContent.reinforcedStairsCobblestone = new BlockReinforcedStairs(SCContent.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_cobblestone").setTranslationKey("securitycraft:reinforcedStairsCobblestone");

		SCContent.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sandstone").setTranslationKey("securitycraft:reinforcedSandstone");
		SCContent.reinforcedStairsSandstone = new BlockReinforcedStairs(SCContent.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_sandstone").setTranslationKey("securitycraft:reinforcedStairsSandstone");

		SCContent.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wood_slabs").setTranslationKey("securitycraft:reinforcedWoodSlabs");
		SCContent.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_wood_slabs").setTranslationKey("securitycraft:reinforcedDoubleWoodSlabs");
		SCContent.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs").setTranslationKey("securitycraft:reinforcedStoneSlabs");
		SCContent.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs").setTranslationKey("securitycraft:reinforcedDoubleStoneSlabs");

		SCContent.protecto = new BlockProtecto(Material.IRON).setBlockUnbreakable().setResistance(1000F).setLightLevel(0.5F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("protecto").setTranslationKey("securitycraft:protecto");

		SCContent.scannerDoor = new BlockScannerDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("scanner_door").setTranslationKey("securitycraft:scannerDoor");

		SCContent.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_brick").setTranslationKey("securitycraft:reinforcedStoneBrick");
		SCContent.reinforcedStairsStoneBrick= new BlockReinforcedStairs(SCContent.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone_brick").setTranslationKey("securitycraft:reinforcedStairsStoneBrick");

		SCContent.reinforcedMossyCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.MOSSY_COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_mossy_cobblestone").setTranslationKey("securitycraft:reinforcedMossyCobblestone");

		SCContent.reinforcedBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.BRICK_BLOCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_brick").setTranslationKey("securitycraft:reinforcedBrick");
		SCContent.reinforcedStairsBrick= new BlockReinforcedStairs(SCContent.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_brick").setTranslationKey("securitycraft:reinforcedStairsBrick");

		SCContent.reinforcedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_nether_brick").setTranslationKey("securitycraft:reinforcedNetherBrick");
		SCContent.reinforcedStairsNetherBrick= new BlockReinforcedStairs(SCContent.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_nether_brick").setTranslationKey("securitycraft:reinforcedStairsNetherBrick");

		SCContent.reinforcedHardenedClay = new BlockReinforcedBase(Material.ROCK, 1, Blocks.HARDENED_CLAY).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_hardened_clay").setTranslationKey("securitycraft:reinforcedHardenedClay");
		SCContent.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_hardened_clay").setTranslationKey("securitycraft:reinforcedStainedHardenedClay");

		SCContent.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs").setTranslationKey("securitycraft:reinforcedLogs");
		SCContent.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs2").setTranslationKey("securitycraft:reinforcedLogs2");

		SCContent.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_metals").setTranslationKey("securitycraft:reinforcedMetals");
		SCContent.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_compressed_blocks").setTranslationKey("securitycraft:reinforcedCompressedBlocks");

		SCContent.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wool").setTranslationKey("securitycraft:reinforcedWool");

		SCContent.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_quartz").setTranslationKey("securitycraft:reinforcedQuartz");
		SCContent.reinforcedStairsQuartz = new BlockReinforcedStairs(SCContent.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_quartz").setTranslationKey("securitycraft:reinforcedStairsQuartz");

		SCContent.reinforcedPrismarine = new BlockReinforcedPrismarine().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_prismarine").setTranslationKey("securitycraft:reinforcedPrismarine");

		SCContent.reinforcedRedSandstone = new BlockReinforcedRedSandstone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_sandstone").setTranslationKey("securitycraft:reinforcedRedSandstone");
		SCContent.reinforcedStairsRedSandstone = new BlockReinforcedStairs(SCContent.reinforcedRedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_red_sandstone").setTranslationKey("securitycraft:reinforcedStairsRedSandstone");

		SCContent.reinforcedStoneSlabs2 = new BlockReinforcedSlabs2(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs2").setTranslationKey("securitycraft:reinforcedStoneSlabs2");
		SCContent.reinforcedDoubleStoneSlabs2 = new BlockReinforcedSlabs2(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs2").setTranslationKey("securitycraft:reinforcedDoubleStoneSlabs2");

		SCContent.reinforcedEndStoneBricks = new BlockReinforcedBase(Material.ROCK, 1, Blocks.END_BRICKS).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone_bricks").setTranslationKey("securitycraft:reinforcedEndStoneBricks");

		SCContent.reinforcedRedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.RED_NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_nether_brick").setTranslationKey("securitycraft:reinforcedRedNetherBrick");

		SCContent.reinforcedPurpur = new BlockReinforcedPurpur().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_purpur").setTranslationKey("securitycraft:reinforcedPurpur");
		SCContent.reinforcedStairsPurpur = new BlockReinforcedStairs(SCContent.reinforcedPurpur, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_purpur").setTranslationKey("securitycraft:reinforcedStairsPurpur");

		SCContent.reinforcedConcrete = new BlockReinforcedConcrete().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_concrete").setTranslationKey("securitycraft:reinforcedConcrete");

		SCContent.secretSignStanding = new BlockSecretSignStanding().setBlockUnbreakable().setResistance(1000F).setRegistryName("secret_sign_standing").setTranslationKey("securitycraft:secretSign");
		SCContent.secretSignWall = new BlockSecretSignWall().setBlockUnbreakable().setResistance(1000F).setRegistryName("secret_sign_wall").setTranslationKey("securitycraft:secretSign");

		SCContent.motionActivatedLight = new BlockMotionActivatedLight(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("motion_activated_light").setTranslationKey("securitycraft:motionActivatedLight").setCreativeTab(SecurityCraft.tabSCTechnical);

		SCContent.reinforcedObsidian = new BlockReinforcedBase(Material.ROCK, 1, Blocks.OBSIDIAN).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_obsidian").setTranslationKey("securitycraft:reinforcedObsidian");

		SCContent.reinforcedNetherrack = new BlockReinforcedBase(Material.ROCK, 1, Blocks.NETHERRACK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_netherrack").setTranslationKey("securitycraft:reinforcedNetherrack");

		SCContent.reinforcedEndStone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.END_STONE).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone").setTranslationKey("securitycraft:reinforcedEndStone");

		SCContent.reinforcedSeaLantern = new BlockReinforcedBase(Material.GLASS, 1, Blocks.SEA_LANTERN).setBlockUnbreakable().setResistance(1000).setLightLevel(1.0F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sea_lantern").setTranslationKey("securitycraft:reinforcedSeaLantern");

		SCContent.reinforcedBoneBlock = new BlockReinforcedBoneBlock(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_bone_block").setTranslationKey("securitycraft:reinforcedBoneBlock");

		SCContent.reinforcedGlassPane = new BlockReinforcedGlassPane().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_pane").setTranslationKey("securitycraft:reinforcedGlassPane");
		SCContent.reinforcedStainedGlassPanes = new BlockReinforcedStainedGlassPanes().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_panes").setTranslationKey("securitycraft:reinforcedStainedGlassPanes");

		SCContent.reinforcedCarpet = new BlockReinforcedCarpet().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_carpet").setTranslationKey("securitycraft:reinforcedCarpet");

		SCContent.reinforcedGlowstone = new BlockReinforcedBase(Material.GLASS, 1, Blocks.GLOWSTONE).setBlockUnbreakable().setResistance(1000).setLightLevel(1.0F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glowstone").setTranslationKey("securitycraft:reinforcedGlowstone");
	}

	public static void setupMines()
	{
		SCContent.mine = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("mine").setTranslationKey("securitycraft:mine");
		SCContent.mineCut = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(1000F).setRegistryName("mine_cut").setTranslationKey("securitycraft:mineCut");

		SCContent.dirtMine = new BlockFullMineBase(Material.GROUND, Blocks.DIRT).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1.25F).setRegistryName("dirt_mine").setTranslationKey("securitycraft:dirtMine");
		SCContent.stoneMine = new BlockFullMineBase(Material.ROCK, Blocks.STONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 2.5F).setRegistryName("stone_mine").setTranslationKey("securitycraft:stoneMine");
		SCContent.cobblestoneMine = new BlockFullMineBase(Material.ROCK, Blocks.COBBLESTONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 2.75F).setRegistryName("cobblestone_mine").setTranslationKey("securitycraft:cobblestoneMine");
		SCContent.sandMine = new BlockFullMineFalling(Material.SAND, Blocks.SAND).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1.25F).setRegistryName("sand_mine").setTranslationKey("securitycraft:sandMine");
		SCContent.diamondOreMine = new BlockFullMineBase(Material.ROCK, Blocks.DIAMOND_ORE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 3.75F).setRegistryName("diamond_mine").setTranslationKey("securitycraft:diamondMine");
		SCContent.furnaceMine = new BlockFurnaceMine(Material.ROCK).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 3.75F).setRegistryName("furnace_mine").setTranslationKey("securitycraft:furnaceMine");
		SCContent.gravelMine = new BlockFullMineFalling(Material.GROUND, Blocks.GRAVEL).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1.25F).setRegistryName("gravel_mine").setTranslationKey("securitycraft:gravelMine");

		SCContent.trackMine = new BlockTrackMine().setHardness(!ConfigHandler.ableToBreakMines ? -1F : 0.7F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("track_mine").setTranslationKey("securitycraft:trackMine");

		SCContent.bouncingBetty = new BlockBouncingBetty(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("bouncing_betty").setTranslationKey("securitycraft:bouncingBetty");

		SCContent.claymore = new BlockClaymore(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("claymore").setTranslationKey("securitycraft:claymore");

		SCContent.ims = new BlockIMS(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("ims").setTranslationKey("securitycraft:ims");
	}

	public static void setupItems()
	{
		SCContent.codebreaker = new ItemCodebreaker().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("codebreaker").setTranslationKey("securitycraft:codebreaker");

		SCContent.keycardLvl1 = new ItemKeycardBase(0).setRegistryName("keycard_lv1").setTranslationKey("securitycraft:keycardLV1");
		SCContent.keycardLvl2 = new ItemKeycardBase(1).setRegistryName("keycard_lv2").setTranslationKey("securitycraft:keycardLV2");
		SCContent.keycardLvl3 = new ItemKeycardBase(2).setRegistryName("keycard_lv3").setTranslationKey("securitycraft:keycardLV3");
		SCContent.keycardLvl4 = new ItemKeycardBase(4).setRegistryName("keycard_lv4").setTranslationKey("securitycraft:keycardLV4");
		SCContent.keycardLvl5 = new ItemKeycardBase(5).setRegistryName("keycard_lv5").setTranslationKey("securitycraft:keycardLV5");
		SCContent.limitedUseKeycard = new ItemKeycardBase(3).setRegistryName("limited_use_keycard").setTranslationKey("securitycraft:limitedUseKeycard");

		SCContent.reinforcedDoorItem = new ItemReinforcedDoor().setRegistryName("door_indestructible_iron_item").setTranslationKey("securitycraft:doorIndestructibleIronItem").setCreativeTab(SecurityCraft.tabSCDecoration);

		SCContent.universalBlockRemover = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_remover").setTranslationKey("securitycraft:universalBlockRemover");

		SCContent.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("remote_access_mine").setTranslationKey("securitycraft:remoteAccessMine");

		SCContent.fWaterBucket = new ItemModifiedBucket(SCContent.bogusWaterFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_water").setTranslationKey("securitycraft:bucketFWater");

		SCContent.fLavaBucket = new ItemModifiedBucket(SCContent.bogusLavaFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_lava").setTranslationKey("securitycraft:bucketFLava");

		SCContent.universalBlockModifier = new Item().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_modifier").setTranslationKey("securitycraft:universalBlockModifier");

		SCContent.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setRegistryName("redstone_module").setTranslationKey("securitycraft:redstoneModule");
		SCContent.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true, true, GuiHandler.MODULES).setRegistryName("whitelist_module").setTranslationKey("securitycraft:whitelistModule");
		SCContent.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true, true, GuiHandler.MODULES).setRegistryName("blacklist_module").setTranslationKey("securitycraft:blacklistModule");
		SCContent.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setRegistryName("harming_module").setTranslationKey("securitycraft:harmingModule");
		SCContent.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setRegistryName("smart_module").setTranslationKey("securitycraft:smartModule");
		SCContent.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setRegistryName("storage_module").setTranslationKey("securitycraft:storageModule");
		SCContent.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setRegistryName("disguise_module").setTranslationKey("securitycraft:disguiseModule");

		SCContent.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("wire_cutters").setTranslationKey("securitycraft:wireCutters");

		SCContent.keyPanel = new ItemKeyPanel().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_item").setTranslationKey("securitycraft:keypadItem");

		SCContent.adminTool = new ItemAdminTool().setMaxStackSize(1).setRegistryName("admin_tool").setTranslationKey("securitycraft:adminTool");

		SCContent.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("camera_monitor").setTranslationKey("securitycraft:cameraMonitor");

		SCContent.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("sc_manual").setTranslationKey("securitycraft:scManual");

		SCContent.taser = new ItemTaser(false).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("taser").setTranslationKey("securitycraft:taser");
		SCContent.taserPowered = new ItemTaser(true).setMaxStackSize(1).setRegistryName("taser_powered").setTranslationKey("securitycraft:taser");

		SCContent.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_owner_changer").setTranslationKey("securitycraft:universalOwnerChanger");

		SCContent.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl1").setTranslationKey("securitycraft:universalBlockReinforcerLvL1");
		SCContent.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl2").setTranslationKey("securitycraft:universalBlockReinforcerLvL2");
		SCContent.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl3").setTranslationKey("securitycraft:universalBlockReinforcerLvL3");

		SCContent.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("briefcase").setTranslationKey("securitycraft:briefcase");

		SCContent.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_key_changer").setTranslationKey("securitycraft:universalKeyChanger");

		SCContent.scannerDoorItem = new ItemScannerDoor().setRegistryName("scanner_door_item").setTranslationKey("securitycraft:scannerDoorItem").setCreativeTab(SecurityCraft.tabSCDecoration);

		SCContent.secretSignItem = new ItemSecretSign().setRegistryName("secret_sign_item").setTranslationKey("securitycraft:secretSignItem").setCreativeTab(SecurityCraft.tabSCDecoration);
	}
}
