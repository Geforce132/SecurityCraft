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
		SCContent.laserBlock = new BlockLaserBlock(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("laser_block").setUnlocalizedName("laserBlock");
		SCContent.laserField = new BlockLaserField(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setRegistryName("laser");

		SCContent.keypad = new BlockKeypad(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad").setUnlocalizedName("keypad");

		SCContent.retinalScanner = new BlockRetinalScanner(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("retinal_scanner").setUnlocalizedName("retinalScanner");

		SCContent.reinforcedDoor = new BlockReinforcedDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("iron_door_reinforced").setUnlocalizedName("ironDoorReinforced");

		SCContent.bogusLava = (BlockStaticLiquid) new BlockFakeLavaBase(Material.LAVA).setHardness(100.0F).setLightLevel(1.0F).setRegistryName("bogus_lava").setUnlocalizedName("bogusLava");
		SCContent.bogusLavaFlowing = new BlockFakeLava(Material.LAVA).setHardness(0.0F).setLightLevel(1.0F).setRegistryName("bogus_lava_flowing").setUnlocalizedName("bogusLavaFlowing");
		SCContent.bogusWater = (BlockStaticLiquid) new BlockFakeWaterBase(Material.WATER).setHardness(100.0F).setRegistryName("bogus_water").setUnlocalizedName("bogusWater");
		SCContent.bogusWaterFlowing = new BlockFakeWater(Material.WATER).setHardness(0.0F).setRegistryName("bogus_water_flowing").setUnlocalizedName("bogusWaterFlowing");

		SCContent.keycardReader = new BlockKeycardReader(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keycard_reader").setUnlocalizedName("keycardReader");

		SCContent.ironTrapdoor = new BlockIronTrapDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_iron_trapdoor").setUnlocalizedName("reinforcedIronTrapdoor");

		SCContent.inventoryScanner = new BlockInventoryScanner(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("inventory_scanner").setUnlocalizedName("inventoryScanner");
		SCContent.inventoryScannerField = new BlockInventoryScannerField(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("inventory_scanner_field").setUnlocalizedName("inventoryScannerField");

		SCContent.cageTrap = new BlockCageTrap(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("cage_trap").setUnlocalizedName("cageTrap");

		SCContent.portableRadar = new BlockPortableRadar(Material.CIRCUITS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("portable_radar").setUnlocalizedName("portableRadar");

		SCContent.unbreakableIronBars = new BlockReinforcedIronBars(Material.IRON, true).setCreativeTab(SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setRegistryName("reinforced_iron_bars").setUnlocalizedName("reinforcedIronBars");

		SCContent.keypadChest = new BlockKeypadChest().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_chest").setUnlocalizedName("keypadChest");

		SCContent.usernameLogger = new BlockLogger(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("username_logger").setUnlocalizedName("usernameLogger");

		SCContent.alarm = new BlockAlarm(Material.IRON, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("alarm").setUnlocalizedName("alarm");
		SCContent.alarmLit = new BlockAlarm(Material.IRON, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setRegistryName("alarm_lit").setUnlocalizedName("alarmLit");

		SCContent.reinforcedStone = new BlockReinforcedStone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone").setUnlocalizedName("reinforcedStone");

		SCContent.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_fence_gate").setUnlocalizedName("reinforcedFenceGate");

		SCContent.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_planks").setUnlocalizedName("reinforcedPlanks");

		SCContent.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("panic_button").setUnlocalizedName("panicButton");

		SCContent.frame = new BlockFrame(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_frame").setUnlocalizedName("keypadFrame");

		SCContent.keypadFurnace = new BlockKeypadFurnace(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_furnace").setUnlocalizedName("keypadFurnace");

		SCContent.securityCamera = new BlockSecurityCamera(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("security_camera").setUnlocalizedName("securityCamera");

		SCContent.reinforcedStairsOak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_oak").setUnlocalizedName("reinforcedStairsOak");
		SCContent.reinforcedStairsSpruce = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_spruce").setUnlocalizedName("reinforcedStairsSpruce");
		SCContent.reinforcedStairsBirch = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_birch").setUnlocalizedName("reinforcedStairsBirch");
		SCContent.reinforcedStairsJungle = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_jungle").setUnlocalizedName("reinforcedStairsJungle");
		SCContent.reinforcedStairsAcacia = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_acacia").setUnlocalizedName("reinforcedStairsAcacia");
		SCContent.reinforcedStairsDarkoak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_darkoak").setUnlocalizedName("reinforcedStairsDarkoak");
		SCContent.reinforcedStairsStone = new BlockReinforcedStairs(SCContent.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone").setUnlocalizedName("reinforcedStairsStone");

		SCContent.ironFence = new BlockIronFence(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("electrified_iron_fence").setUnlocalizedName("electrifiedIronFence");

		SCContent.reinforcedGlass = new BlockReinforcedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_block").setUnlocalizedName("reinforcedGlassBlock");
		SCContent.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_glass").setUnlocalizedName("reinforcedStainedGlass");

		SCContent.reinforcedDirt = new BlockReinforcedBase(Material.GROUND, 1, Blocks.DIRT).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_dirt").setUnlocalizedName("reinforcedDirt");

		SCContent.reinforcedCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_cobblestone").setUnlocalizedName("reinforcedCobblestone");
		SCContent.reinforcedStairsCobblestone = new BlockReinforcedStairs(SCContent.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_cobblestone").setUnlocalizedName("reinforcedStairsCobblestone");

		SCContent.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sandstone").setUnlocalizedName("reinforcedSandstone");
		SCContent.reinforcedStairsSandstone = new BlockReinforcedStairs(SCContent.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_sandstone").setUnlocalizedName("reinforcedStairsSandstone");

		SCContent.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wood_slabs").setUnlocalizedName("reinforcedWoodSlabs");
		SCContent.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_wood_slabs").setUnlocalizedName("reinforcedDoubleWoodSlabs");
		SCContent.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs").setUnlocalizedName("reinforcedStoneSlabs");
		SCContent.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs").setUnlocalizedName("reinforcedDoubleStoneSlabs");


		SCContent.protecto = new BlockProtecto(Material.IRON).setBlockUnbreakable().setResistance(1000F).setLightLevel(0.5F).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("protecto").setUnlocalizedName("protecto");

		SCContent.scannerDoor = new BlockScannerDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("scanner_door").setUnlocalizedName("scannerDoor");

		SCContent.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_brick").setUnlocalizedName("reinforcedStoneBrick");
		SCContent.reinforcedStairsStoneBrick= new BlockReinforcedStairs(SCContent.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone_brick").setUnlocalizedName("reinforcedStairsStoneBrick");

		SCContent.reinforcedMossyCobblestone = new BlockReinforcedBase(Material.ROCK, 1, Blocks.MOSSY_COBBLESTONE).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_mossy_cobblestone").setUnlocalizedName("reinforcedMossyCobblestone");

		SCContent.reinforcedBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.BRICK_BLOCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_brick").setUnlocalizedName("reinforcedBrick");
		SCContent.reinforcedStairsBrick= new BlockReinforcedStairs(SCContent.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_brick").setUnlocalizedName("reinforcedStairsBrick");

		SCContent.reinforcedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_nether_brick").setUnlocalizedName("reinforcedNetherBrick");
		SCContent.reinforcedStairsNetherBrick= new BlockReinforcedStairs(SCContent.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_nether_brick").setUnlocalizedName("reinforcedStairsNetherBrick");

		SCContent.reinforcedHardenedClay = new BlockReinforcedBase(Material.ROCK, 1, Blocks.HARDENED_CLAY).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_hardened_clay").setUnlocalizedName("reinforcedHardenedClay");
		SCContent.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_hardened_clay").setUnlocalizedName("reinforcedStainedHardenedClay");

		SCContent.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs").setUnlocalizedName("reinforcedLogs");
		SCContent.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs2").setUnlocalizedName("reinforcedLogs2");

		SCContent.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_metals").setUnlocalizedName("reinforcedMetals");
		SCContent.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_compressed_blocks").setUnlocalizedName("reinforcedCompressedBlocks");

		SCContent.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wool").setUnlocalizedName("reinforcedWool");

		SCContent.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_quartz").setUnlocalizedName("reinforcedQuartz");
		SCContent.reinforcedStairsQuartz = new BlockReinforcedStairs(SCContent.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_quartz").setUnlocalizedName("reinforcedStairsQuartz");

		SCContent.reinforcedPrismarine = new BlockReinforcedPrismarine().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_prismarine").setUnlocalizedName("reinforcedPrismarine");

		SCContent.reinforcedRedSandstone = new BlockReinforcedRedSandstone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_sandstone").setUnlocalizedName("reinforcedRedSandstone");
		SCContent.reinforcedStairsRedSandstone = new BlockReinforcedStairs(SCContent.reinforcedRedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_red_sandstone").setUnlocalizedName("reinforcedStairsRedSandstone");

		SCContent.reinforcedStoneSlabs2 = new BlockReinforcedSlabs2(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs2").setUnlocalizedName("reinforcedStoneSlabs2");
		SCContent.reinforcedDoubleStoneSlabs2 = new BlockReinforcedSlabs2(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs2").setUnlocalizedName("reinforcedDoubleStoneSlabs2");

		SCContent.reinforcedEndStoneBricks = new BlockReinforcedBase(Material.ROCK, 1, Blocks.END_BRICKS).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone_bricks").setUnlocalizedName("reinforcedEndStoneBricks");

		SCContent.reinforcedRedNetherBrick = new BlockReinforcedBase(Material.ROCK, 1, Blocks.RED_NETHER_BRICK).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_nether_brick").setUnlocalizedName("reinforcedRedNetherBrick");

		SCContent.reinforcedPurpur = new BlockReinforcedPurpur().setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_purpur").setUnlocalizedName("reinforcedPurpur");
		SCContent.reinforcedStairsPurpur = new BlockReinforcedStairs(SCContent.reinforcedPurpur, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_purpur").setUnlocalizedName("reinforcedStairsPurpur");
	}

	public static void setupMines()
	{
		SCContent.mine = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("mine").setUnlocalizedName("mine");
		SCContent.mineCut = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setRegistryName("mine_cut").setUnlocalizedName("mineCut");

		SCContent.dirtMine = new BlockFullMineBase(Material.GROUND, Blocks.DIRT).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setRegistryName("dirt_mine").setUnlocalizedName("dirtMine");
		SCContent.stoneMine = new BlockFullMineBase(Material.ROCK, Blocks.STONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 2.5F).setRegistryName("stone_mine").setUnlocalizedName("stoneMine");
		SCContent.cobblestoneMine = new BlockFullMineBase(Material.ROCK, Blocks.COBBLESTONE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 2.75F).setRegistryName("cobblestone_mine").setUnlocalizedName("cobblestoneMine");
		SCContent.sandMine = new BlockFullMineBase(Material.SAND, Blocks.SAND).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setRegistryName("sand_mine").setUnlocalizedName("sandMine");
		SCContent.diamondOreMine = new BlockFullMineBase(Material.ROCK, Blocks.DIAMOND_ORE).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 3.75F).setRegistryName("diamond_mine").setUnlocalizedName("diamondMine");
		SCContent.furnaceMine = new BlockFurnaceMine(Material.ROCK).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 3.75F).setRegistryName("furnace_mine").setUnlocalizedName("furnaceMine");

		SCContent.trackMine = new BlockTrackMine().setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 0.7F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("track_mine").setUnlocalizedName("trackMine");

		SCContent.bouncingBetty = new BlockBouncingBetty(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("bouncing_betty").setUnlocalizedName("bouncingBetty");

		SCContent.claymore = new BlockClaymore(Material.CIRCUITS).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("claymore").setUnlocalizedName("claymore");

		SCContent.ims = new BlockIMS(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setRegistryName("ims").setUnlocalizedName("ims");
	}

	public static void setupItems()
	{
		SCContent.codebreaker = new ItemCodebreaker().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("codebreaker").setUnlocalizedName("codebreaker");

		SCContent.keycardLV1 = new ItemKeycardBase(0).setRegistryName("keycard_lv1").setUnlocalizedName("keycardLV1");
		SCContent.keycardLV2 = new ItemKeycardBase(1).setRegistryName("keycard_lv2").setUnlocalizedName("keycardLV2");
		SCContent.keycardLV3 = new ItemKeycardBase(2).setRegistryName("keycard_lv3").setUnlocalizedName("keycardLV3");
		SCContent.keycardLV4 = new ItemKeycardBase(4).setRegistryName("keycard_lv4").setUnlocalizedName("keycardLV4");
		SCContent.keycardLV5 = new ItemKeycardBase(5).setRegistryName("keycard_lv5").setUnlocalizedName("keycardLV5");
		SCContent.limitedUseKeycard = new ItemKeycardBase(3).setRegistryName("limited_use_keycard").setUnlocalizedName("limitedUseKeycard");

		SCContent.reinforcedDoorItem = new ItemReinforcedDoor().setRegistryName("door_indestructible_iron_item").setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(SecurityCraft.tabSCDecoration);

		SCContent.universalBlockRemover = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_remover").setUnlocalizedName("universalBlockRemover");

		SCContent.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("remote_access_mine").setUnlocalizedName("remoteAccessMine");

		SCContent.fWaterBucket = new ItemModifiedBucket(SCContent.bogusWaterFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_water").setUnlocalizedName("bucketFWater");

		SCContent.fLavaBucket = new ItemModifiedBucket(SCContent.bogusLavaFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_lava").setUnlocalizedName("bucketFLava");

		SCContent.universalBlockModifier = new Item().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_modifier").setUnlocalizedName("universalBlockModifier");

		SCContent.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setRegistryName("redstone_module").setUnlocalizedName("redstoneModule");
		SCContent.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setRegistryName("whitelist_module").setUnlocalizedName("whitelistModule");
		SCContent.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setRegistryName("blacklist_module").setUnlocalizedName("blacklistModule");
		SCContent.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setRegistryName("harming_module").setUnlocalizedName("harmingModule");
		SCContent.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setRegistryName("smart_module").setUnlocalizedName("smartModule");
		SCContent.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setRegistryName("storage_module").setUnlocalizedName("storageModule");
		SCContent.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setRegistryName("disguise_module").setUnlocalizedName("disguiseModule");

		SCContent.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("wire_cutters").setUnlocalizedName("wireCutters");

		SCContent.keyPanel = new ItemKeyPanel().setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("keypad_item").setUnlocalizedName("keypadItem");

		SCContent.adminTool = new ItemAdminTool().setMaxStackSize(1).setRegistryName("admin_tool").setUnlocalizedName("adminTool");

		SCContent.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("camera_monitor").setUnlocalizedName("cameraMonitor");

		SCContent.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("sc_manual").setUnlocalizedName("scManual");

		SCContent.taser = new ItemTaser(false).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("taser").setUnlocalizedName("taser");
		SCContent.taserPowered = new ItemTaser(true).setMaxStackSize(1).setRegistryName("taser_powered").setUnlocalizedName("taser");

		SCContent.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_owner_changer").setUnlocalizedName("universalOwnerChanger");

		SCContent.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl1").setUnlocalizedName("universalBlockReinforcerLvL1");
		SCContent.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl2").setUnlocalizedName("universalBlockReinforcerLvL2");
		SCContent.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl3").setUnlocalizedName("universalBlockReinforcerLvL3");

		SCContent.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("briefcase").setUnlocalizedName("briefcase");

		SCContent.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setRegistryName("universal_key_changer").setUnlocalizedName("universalKeyChanger");

		SCContent.scannerDoorItem = new ItemScannerDoor().setRegistryName("scanner_door_item").setUnlocalizedName("scannerDoorItem").setCreativeTab(SecurityCraft.tabSCDecoration);
	}
}
