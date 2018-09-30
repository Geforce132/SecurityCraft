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
		SCContent.laserBlock = new BlockLaserBlock(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("laser_block").setUnlocalizedName("securitycraft:laserBlock");
		SCContent.laserField = new BlockLaserField(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setRegistryName("laser");

		SCContent.keypad = new BlockKeypad(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad").setUnlocalizedName("securitycraft:keypad");

		SCContent.retinalScanner = new BlockRetinalScanner(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("retinal_scanner").setUnlocalizedName("securitycraft:retinalScanner");

		SCContent.reinforcedDoor = new BlockReinforcedDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("iron_door_reinforced").setUnlocalizedName("securitycraft:ironDoorReinforced");

		SCContent.bogusLava = (BlockStaticLiquid) new BlockFakeLavaBase(Material.LAVA).setHardness(100.0F).setLightLevel(1.0F).setRegistryName("bogus_lava").setUnlocalizedName("securitycraft:bogusLava");
		SCContent.bogusLavaFlowing = new BlockFakeLava(Material.LAVA).setHardness(0.0F).setLightLevel(1.0F).setRegistryName("bogus_lava_flowing").setUnlocalizedName("securitycraft:bogusLavaFlowing");
		SCContent.bogusWater = (BlockStaticLiquid) new BlockFakeWaterBase(Material.WATER).setHardness(100.0F).setRegistryName("bogus_water").setUnlocalizedName("securitycraft:bogusWater");
		SCContent.bogusWaterFlowing = new BlockFakeWater(Material.WATER).setHardness(0.0F).setRegistryName("bogus_water_flowing").setUnlocalizedName("securitycraft:bogusWaterFlowing");

		SCContent.keycardReader = new BlockKeycardReader(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keycard_reader").setUnlocalizedName("securitycraft:keycardReader");

		SCContent.ironTrapdoor = new BlockIronTrapDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_iron_trapdoor").setUnlocalizedName("securitycraft:reinforcedIronTrapdoor");

		SCContent.inventoryScanner = new BlockInventoryScanner(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("inventory_scanner").setUnlocalizedName("securitycraft:inventoryScanner");
		SCContent.inventoryScannerField = new BlockInventoryScannerField(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("inventory_scanner_field").setUnlocalizedName("securitycraft:inventoryScannerField");

		SCContent.cageTrap = new BlockCageTrap(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("cage_trap").setUnlocalizedName("securitycraft:cageTrap");

		SCContent.portableRadar = new BlockPortableRadar(Material.CIRCUITS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("portable_radar").setUnlocalizedName("securitycraft:portableRadar");

		SCContent.reinforcedIronBars = new BlockReinforcedIronBars(Material.IRON, true).setCreativeTab(SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setRegistryName("reinforced_iron_bars").setUnlocalizedName("securitycraft:reinforcedIronBars");

		SCContent.keypadChest = new BlockKeypadChest().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_chest").setUnlocalizedName("securitycraft:keypadChest");

		SCContent.usernameLogger = new BlockLogger(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("username_logger").setUnlocalizedName("securitycraft:usernameLogger");

		SCContent.alarm = new BlockAlarm(Material.IRON, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("alarm").setUnlocalizedName("securitycraft:alarm");
		SCContent.alarmLit = new BlockAlarm(Material.IRON, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setRegistryName("alarm_lit").setUnlocalizedName("securitycraft:alarmLit");

		SCContent.reinforcedStone = new BlockReinforcedStone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone").setUnlocalizedName("securitycraft:reinforcedStone");

		SCContent.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_fence_gate").setUnlocalizedName("securitycraft:reinforcedFenceGate");

		SCContent.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_planks").setUnlocalizedName("securitycraft:reinforcedPlanks");

		SCContent.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("panic_button").setUnlocalizedName("securitycraft:panicButton");

		SCContent.frame = new BlockFrame(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_frame").setUnlocalizedName("securitycraft:keypadFrame");

		SCContent.keypadFurnace = new BlockKeypadFurnace(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_furnace").setUnlocalizedName("securitycraft:keypadFurnace");

		SCContent.securityCamera = new BlockSecurityCamera(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("security_camera").setUnlocalizedName("securitycraft:securityCamera");

		SCContent.reinforcedStairsOak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_oak").setUnlocalizedName("securitycraft:reinforcedStairsOak");
		SCContent.reinforcedStairsSpruce = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_spruce").setUnlocalizedName("securitycraft:reinforcedStairsSpruce");
		SCContent.reinforcedStairsBirch = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_birch").setUnlocalizedName("securitycraft:reinforcedStairsBirch");
		SCContent.reinforcedStairsJungle = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_jungle").setUnlocalizedName("securitycraft:reinforcedStairsJungle");
		SCContent.reinforcedStairsAcacia = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_acacia").setUnlocalizedName("securitycraft:reinforcedStairsAcacia");
		SCContent.reinforcedStairsDarkoak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_darkoak").setUnlocalizedName("securitycraft:reinforcedStairsDarkoak");
		SCContent.reinforcedStairsStone = new BlockReinforcedStairs(SCContent.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone").setUnlocalizedName("securitycraft:reinforcedStairsStone");

		SCContent.ironFence = new BlockIronFence(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("electrified_iron_fence").setUnlocalizedName("securitycraft:electrifiedIronFence");

		SCContent.reinforcedGlass = new BlockReinforcedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_block").setUnlocalizedName("securitycraft:reinforcedGlassBlock");
		SCContent.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_glass").setUnlocalizedName("securitycraft:reinforcedStainedGlass");

		SCContent.reinforcedDirt = new BlockReinforcedBase(Material.GROUND, 1, Blocks.DIRT).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_dirt").setUnlocalizedName("securitycraft:reinforcedDirt");

		SCContent.reinforcedCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_cobblestone").setUnlocalizedName("securitycraft:reinforcedCobblestone");
		SCContent.reinforcedStairsCobblestone = new BlockReinforcedStairs(SCContent.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_cobblestone").setUnlocalizedName("securitycraft:reinforcedStairsCobblestone");

		SCContent.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sandstone").setUnlocalizedName("securitycraft:reinforcedSandstone");
		SCContent.reinforcedStairsSandstone = new BlockReinforcedStairs(SCContent.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_sandstone").setUnlocalizedName("securitycraft:reinforcedStairsSandstone");

		SCContent.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wood_slabs").setUnlocalizedName("securitycraft:reinforcedWoodSlabs");
		SCContent.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_wood_slabs").setUnlocalizedName("securitycraft:reinforcedDoubleWoodSlabs");
		SCContent.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs").setUnlocalizedName("securitycraft:reinforcedStoneSlabs");
		SCContent.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs").setUnlocalizedName("securitycraft:reinforcedDoubleStoneSlabs");

		SCContent.protecto = new BlockProtecto(Material.IRON).setBlockUnbreakable().setResistance(1000F).setLightLevel(0.5F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("protecto").setUnlocalizedName("securitycraft:protecto");

		SCContent.scannerDoor = new BlockScannerDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("scanner_door").setUnlocalizedName("securitycraft:scannerDoor");

		SCContent.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_brick").setUnlocalizedName("securitycraft:reinforcedStoneBrick");
		SCContent.reinforcedStairsStoneBrick= new BlockReinforcedStairs(SCContent.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone_brick").setUnlocalizedName("securitycraft:reinforcedStairsStoneBrick");

		SCContent.reinforcedMossyCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.MOSSY_COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_mossy_cobblestone").setUnlocalizedName("securitycraft:reinforcedMossyCobblestone");

		SCContent.reinforcedBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.BRICK_BLOCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_brick").setUnlocalizedName("securitycraft:reinforcedBrick");
		SCContent.reinforcedStairsBrick= new BlockReinforcedStairs(SCContent.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_brick").setUnlocalizedName("securitycraft:reinforcedStairsBrick");

		SCContent.reinforcedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_nether_brick").setUnlocalizedName("securitycraft:reinforcedNetherBrick");
		SCContent.reinforcedStairsNetherBrick= new BlockReinforcedStairs(SCContent.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_nether_brick").setUnlocalizedName("securitycraft:reinforcedStairsNetherBrick");

		SCContent.reinforcedHardenedClay = new BlockReinforcedBase(Material.ROCK, 1, Blocks.HARDENED_CLAY).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_hardened_clay").setUnlocalizedName("securitycraft:reinforcedHardenedClay");
		SCContent.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_hardened_clay").setUnlocalizedName("securitycraft:reinforcedStainedHardenedClay");

		SCContent.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs").setUnlocalizedName("securitycraft:reinforcedLogs");
		SCContent.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs2").setUnlocalizedName("securitycraft:reinforcedLogs2");

		SCContent.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_metals").setUnlocalizedName("securitycraft:reinforcedMetals");
		SCContent.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_compressed_blocks").setUnlocalizedName("securitycraft:reinforcedCompressedBlocks");

		SCContent.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wool").setUnlocalizedName("securitycraft:reinforcedWool");

		SCContent.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_quartz").setUnlocalizedName("securitycraft:reinforcedQuartz");
		SCContent.reinforcedStairsQuartz = new BlockReinforcedStairs(SCContent.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_quartz").setUnlocalizedName("securitycraft:reinforcedStairsQuartz");

		SCContent.reinforcedPrismarine = new BlockReinforcedPrismarine().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_prismarine").setUnlocalizedName("securitycraft:reinforcedPrismarine");

		SCContent.reinforcedRedSandstone = new BlockReinforcedRedSandstone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_sandstone").setUnlocalizedName("securitycraft:reinforcedRedSandstone");
		SCContent.reinforcedStairsRedSandstone = new BlockReinforcedStairs(SCContent.reinforcedRedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_red_sandstone").setUnlocalizedName("securitycraft:reinforcedStairsRedSandstone");

		SCContent.reinforcedStoneSlabs2 = new BlockReinforcedSlabs2(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs2").setUnlocalizedName("securitycraft:reinforcedStoneSlabs2");
		SCContent.reinforcedDoubleStoneSlabs2 = new BlockReinforcedSlabs2(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs2").setUnlocalizedName("securitycraft:reinforcedDoubleStoneSlabs2");

		SCContent.reinforcedEndStoneBricks = new BlockReinforcedBase(Material.ROCK, 1, Blocks.END_BRICKS).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone_bricks").setUnlocalizedName("securitycraft:reinforcedEndStoneBricks");

		SCContent.reinforcedRedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.RED_NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_nether_brick").setUnlocalizedName("securitycraft:reinforcedRedNetherBrick");

		SCContent.reinforcedPurpur = new BlockReinforcedPurpur().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_purpur").setUnlocalizedName("securitycraft:reinforcedPurpur");
		SCContent.reinforcedStairsPurpur = new BlockReinforcedStairs(SCContent.reinforcedPurpur, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_purpur").setUnlocalizedName("securitycraft:reinforcedStairsPurpur");

		SCContent.secretSignStanding = new BlockSecretSignStanding().setBlockUnbreakable().setResistance(1000F).setRegistryName("secret_sign_standing").setUnlocalizedName("securitycraft:secretSign");
		SCContent.secretSignWall = new BlockSecretSignWall().setBlockUnbreakable().setResistance(1000F).setRegistryName("secret_sign_wall").setUnlocalizedName("securitycraft:secretSign");

		SCContent.motionActivatedLight = new BlockMotionActivatedLight(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("motion_activated_light").setUnlocalizedName("securitycraft:motionActivatedLight").setCreativeTab(SecurityCraft.tabSCTechnical);

		SCContent.reinforcedObsidian = new BlockReinforcedBase(Material.ROCK, 1, Blocks.OBSIDIAN).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_obsidian").setUnlocalizedName("securitycraft:reinforcedObsidian");

		SCContent.reinforcedNetherrack = new BlockReinforcedBase(Material.ROCK, 1, Blocks.NETHERRACK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_netherrack").setUnlocalizedName("securitycraft:reinforcedNetherrack");

		SCContent.reinforcedEndStone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.END_STONE).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone").setUnlocalizedName("securitycraft:reinforcedEndStone");

		SCContent.reinforcedSeaLantern = new BlockReinforcedBase(Material.GLASS, 1, Blocks.SEA_LANTERN).setBlockUnbreakable().setResistance(1000).setLightLevel(1.0F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sea_lantern").setUnlocalizedName("securitycraft:reinforcedSeaLantern");

		SCContent.reinforcedBoneBlock = new BlockReinforcedBoneBlock(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_bone_block").setUnlocalizedName("securitycraft:reinforcedBoneBlock");

		SCContent.reinforcedGlassPane = new BlockReinforcedGlassPane().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_pane").setUnlocalizedName("securitycraft:reinforcedGlassPane");
		SCContent.reinforcedStainedGlassPanes = new BlockReinforcedStainedGlassPanes().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_panes").setUnlocalizedName("securitycraft:reinforcedStainedGlassPanes");

		SCContent.reinforcedCarpet = new BlockReinforcedCarpet().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_carpet").setUnlocalizedName("securitycraft:reinforcedCarpet");

		SCContent.reinforcedGlowstone = new BlockReinforcedBase(Material.GLASS, 1, Blocks.GLOWSTONE).setBlockUnbreakable().setResistance(1000).setLightLevel(1.0F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glowstone").setUnlocalizedName("securitycraft:reinforcedGlowstone");
	}

	public static void setupMines()
	{
		SCContent.mine = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("mine").setUnlocalizedName("securitycraft:mine");
		SCContent.mineCut = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(1000F).setRegistryName("mine_cut").setUnlocalizedName("securitycraft:mineCut");

		SCContent.dirtMine = new BlockFullMineBase(Material.GROUND, Blocks.DIRT).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1.25F).setRegistryName("dirt_mine").setUnlocalizedName("securitycraft:dirtMine");
		SCContent.stoneMine = new BlockFullMineBase(Material.ROCK, Blocks.STONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 2.5F).setRegistryName("stone_mine").setUnlocalizedName("securitycraft:stoneMine");
		SCContent.cobblestoneMine = new BlockFullMineBase(Material.ROCK, Blocks.COBBLESTONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 2.75F).setRegistryName("cobblestone_mine").setUnlocalizedName("securitycraft:cobblestoneMine");
		SCContent.sandMine = new BlockFullMineFalling(Material.SAND, Blocks.SAND).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1.25F).setRegistryName("sand_mine").setUnlocalizedName("securitycraft:sandMine");
		SCContent.diamondOreMine = new BlockFullMineBase(Material.ROCK, Blocks.DIAMOND_ORE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 3.75F).setRegistryName("diamond_mine").setUnlocalizedName("securitycraft:diamondMine");
		SCContent.furnaceMine = new BlockFurnaceMine(Material.ROCK).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 3.75F).setRegistryName("furnace_mine").setUnlocalizedName("securitycraft:furnaceMine");
		SCContent.gravelMine = new BlockFullMineFalling(Material.GROUND, Blocks.GRAVEL).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1.25F).setRegistryName("gravel_mine").setUnlocalizedName("securitycraft:gravelMine");

		SCContent.trackMine = new BlockTrackMine().setHardness(!ConfigHandler.ableToBreakMines ? -1F : 0.7F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("track_mine").setUnlocalizedName("securitycraft:trackMine");

		SCContent.bouncingBetty = new BlockBouncingBetty(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("bouncing_betty").setUnlocalizedName("securitycraft:bouncingBetty");

		SCContent.claymore = new BlockClaymore(Material.CIRCUITS).setHardness(!ConfigHandler.ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("claymore").setUnlocalizedName("securitycraft:claymore");

		SCContent.ims = new BlockIMS(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("ims").setUnlocalizedName("securitycraft:ims");
	}

	public static void setupItems()
	{
		SCContent.codebreaker = new ItemCodebreaker().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("codebreaker").setUnlocalizedName("securitycraft:codebreaker");

		SCContent.keycardLvl1 = new ItemKeycardBase(0).setRegistryName("keycard_lv1").setUnlocalizedName("securitycraft:keycardLV1");
		SCContent.keycardLvl2 = new ItemKeycardBase(1).setRegistryName("keycard_lv2").setUnlocalizedName("securitycraft:keycardLV2");
		SCContent.keycardLvl3 = new ItemKeycardBase(2).setRegistryName("keycard_lv3").setUnlocalizedName("securitycraft:keycardLV3");
		SCContent.keycardLvl4 = new ItemKeycardBase(4).setRegistryName("keycard_lv4").setUnlocalizedName("securitycraft:keycardLV4");
		SCContent.keycardLvl5 = new ItemKeycardBase(5).setRegistryName("keycard_lv5").setUnlocalizedName("securitycraft:keycardLV5");
		SCContent.limitedUseKeycard = new ItemKeycardBase(3).setRegistryName("limited_use_keycard").setUnlocalizedName("securitycraft:limitedUseKeycard");

		SCContent.reinforcedDoorItem = new ItemReinforcedDoor().setRegistryName("door_indestructible_iron_item").setUnlocalizedName("securitycraft:doorIndestructibleIronItem").setCreativeTab(SecurityCraft.tabSCDecoration);

		SCContent.universalBlockRemover = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_remover").setUnlocalizedName("securitycraft:universalBlockRemover");

		SCContent.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("remote_access_mine").setUnlocalizedName("securitycraft:remoteAccessMine");

		SCContent.fWaterBucket = new ItemModifiedBucket(SCContent.bogusWaterFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_water").setUnlocalizedName("securitycraft:bucketFWater");

		SCContent.fLavaBucket = new ItemModifiedBucket(SCContent.bogusLavaFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_lava").setUnlocalizedName("securitycraft:bucketFLava");

		SCContent.universalBlockModifier = new Item().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_modifier").setUnlocalizedName("securitycraft:universalBlockModifier");

		SCContent.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setRegistryName("redstone_module").setUnlocalizedName("securitycraft:redstoneModule");
		SCContent.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setRegistryName("whitelist_module").setUnlocalizedName("securitycraft:whitelistModule");
		SCContent.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setRegistryName("blacklist_module").setUnlocalizedName("securitycraft:blacklistModule");
		SCContent.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setRegistryName("harming_module").setUnlocalizedName("securitycraft:harmingModule");
		SCContent.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setRegistryName("smart_module").setUnlocalizedName("securitycraft:smartModule");
		SCContent.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setRegistryName("storage_module").setUnlocalizedName("securitycraft:storageModule");
		SCContent.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setRegistryName("disguise_module").setUnlocalizedName("securitycraft:disguiseModule");

		SCContent.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("wire_cutters").setUnlocalizedName("securitycraft:wireCutters");

		SCContent.keyPanel = new ItemKeyPanel().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_item").setUnlocalizedName("securitycraft:keypadItem");

		SCContent.adminTool = new ItemAdminTool().setMaxStackSize(1).setRegistryName("admin_tool").setUnlocalizedName("securitycraft:adminTool");

		SCContent.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("camera_monitor").setUnlocalizedName("securitycraft:cameraMonitor");

		SCContent.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("sc_manual").setUnlocalizedName("securitycraft:scManual");

		SCContent.taser = new ItemTaser(false).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("taser").setUnlocalizedName("securitycraft:taser");
		SCContent.taserPowered = new ItemTaser(true).setMaxStackSize(1).setRegistryName("taser_powered").setUnlocalizedName("securitycraft:taser");

		SCContent.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_owner_changer").setUnlocalizedName("securitycraft:universalOwnerChanger");

		SCContent.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl1").setUnlocalizedName("securitycraft:universalBlockReinforcerLvL1");
		SCContent.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl2").setUnlocalizedName("securitycraft:universalBlockReinforcerLvL2");
		SCContent.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl3").setUnlocalizedName("securitycraft:universalBlockReinforcerLvL3");

		SCContent.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("briefcase").setUnlocalizedName("securitycraft:briefcase");

		SCContent.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_key_changer").setUnlocalizedName("securitycraft:universalKeyChanger");

		SCContent.scannerDoorItem = new ItemScannerDoor().setRegistryName("scanner_door_item").setUnlocalizedName("securitycraft:scannerDoorItem").setCreativeTab(SecurityCraft.tabSCDecoration);

		SCContent.secretSignItem = new ItemSecretSign().setRegistryName("secret_sign_item").setUnlocalizedName("securitycraft:secretSignItem").setCreativeTab(SecurityCraft.tabSCDecoration);
	}
}
