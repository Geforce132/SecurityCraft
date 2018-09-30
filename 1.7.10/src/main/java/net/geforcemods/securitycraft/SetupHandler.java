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
import net.geforcemods.securitycraft.blocks.BlockSecretSign;
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
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedQuartz;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSandstone;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedGlassPanes;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedHardenedClay;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStairs;
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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class SetupHandler
{
	public static void setupBlocks()
	{
		SCContent.laserBlock = new BlockLaserBlock(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:laserBlock").setTextureName("securitycraft:laserBlock");
		SCContent.laserField = new BlockLaserField(Material.rock).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:laser");

		SCContent.keypad = new BlockKeypad(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:keypad");

		SCContent.retinalScanner = new BlockRetinalScanner(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:retinalScanner");

		SCContent.reinforcedDoor = new BlockReinforcedDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("securitycraft:ironDoorReinforced");

		SCContent.bogusLava = new BlockFakeLavaBase(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("securitycraft:bogusLava").setTextureName("lava_still");
		SCContent.bogusLavaFlowing = new BlockFakeLava(Material.lava).setHardness(0.0F).setLightLevel(1.0F).setUnlocalizedName("securitycraft:bogusLavaFlowing").setTextureName("lava_flow");
		SCContent.bogusWater = new BlockFakeWaterBase(Material.water).setHardness(100.0F).setUnlocalizedName("securitycraft:bogusWater").setTextureName("water_still");
		SCContent.bogusWaterFlowing = new BlockFakeWater(Material.water).setHardness(0.0F).setUnlocalizedName("securitycraft:bogusWaterFlowing").setTextureName("water_flow");

		SCContent.keycardReader = new BlockKeycardReader(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:keycardReader").setTextureName("securitycraft:keycardReader");

		SCContent.ironTrapdoor = new BlockIronTrapDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:ironTrapdoor").setTextureName("securitycraft:ironTrapdoor");

		SCContent.inventoryScanner = new BlockInventoryScanner(Material.rock).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeStone).setUnlocalizedName("securitycraft:inventoryScanner");
		SCContent.inventoryScannerField = new BlockInventoryScannerField(Material.glass).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:inventoryScannerField");

		SCContent.cageTrap = new BlockCageTrap(Material.rock, false).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:cageTrap").setTextureName("securitycraft:reinforcedIronBars");
		SCContent.deactivatedCageTrap = new BlockCageTrap(Material.rock, true).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:deactivatedCageTrap").setTextureName("iron_bars");

		SCContent.portableRadar = new BlockPortableRadar(Material.circuits).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:portableRadar");

		SCContent.reinforcedIronBars = new BlockReinforcedIronBars("securitycraft:reinforcedIronBars", "securitycraft:reinforcedIronBars", Material.iron, true).setCreativeTab(SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:reinforcedIronBars");

		SCContent.keypadChest = new BlockKeypadChest(0).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:keypadChest");

		SCContent.usernameLogger = new BlockLogger(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:usernameLogger").setTextureName("securitycraft:usernameLogger");

		SCContent.reinforcedGlassPane = new BlockReinforcedGlassPane("securitycraft:glass_reinforced", "glass_pane_top", Material.iron, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedGlass");

		SCContent.alarm = new BlockAlarm(Material.iron, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:alarm").setTextureName("securitycraft:alarmParticleTexture");
		SCContent.alarmLit = new BlockAlarm(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setUnlocalizedName("securitycraft:alarmLit");

		SCContent.reinforcedStone = new BlockReinforcedBase(Material.rock, 1, Blocks.stone).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStone").setTextureName("securitycraft:reinforcedStone");

		SCContent.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedFenceGate");

		SCContent.reinforcedWoodPlanks = new BlockReinforcedWood(Material.wood).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedPlanks").setTextureName("securitycraft:reinforcedPlanks");

		SCContent.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:panicButton");

		SCContent.frame = new BlockFrame(Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:keypadFrame").setTextureName("iron_block");

		SCContent.keypadFurnace = new BlockKeypadFurnace(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeMetal).setUnlocalizedName("securitycraft:keypadFurnace").setTextureName("securitycraft:keypadUnactive");

		SCContent.securityCamera = new BlockSecurityCamera(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:securityCamera").setTextureName("securitycraft:securityCameraParticleTexture");

		SCContent.reinforcedStairsOak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsOak");
		SCContent.reinforcedStairsSpruce = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsSpruce");
		SCContent.reinforcedStairsBirch = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsBirch");
		SCContent.reinforcedStairsJungle = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsJungle");
		SCContent.reinforcedStairsAcacia = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsAcacia");
		SCContent.reinforcedStairsDarkoak = new BlockReinforcedStairs(SCContent.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsDarkoak");
		SCContent.reinforcedStairsStone = new BlockReinforcedStairs(SCContent.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsStone");

		SCContent.ironFence = new BlockIronFence("securitycraft:reinforcedDoorLower", Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:scIronFence");

		SCContent.reinforcedGlass = new BlockReinforcedGlass(Material.glass).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedGlassBlock");
		SCContent.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.glass).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStainedGlass").setTextureName("securitycraft:glass_reinforced");
		SCContent.reinforcedStainedGlassPanes = new BlockReinforcedStainedGlassPanes().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStainedGlassPanes").setTextureName("securitycraft:glass_reinforced");

		SCContent.reinforcedDirt = new BlockReinforcedBase(Material.ground, 1, Blocks.dirt).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGravel).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedDirt").setTextureName("securitycraft:reinforcedDirt");

		SCContent.reinforcedCobblestone = new BlockReinforcedBase(Material.rock, 1, Blocks.cobblestone).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedCobblestone").setTextureName("securitycraft:reinforcedCobblestone");
		SCContent.reinforcedStairsCobblestone = new BlockReinforcedStairs(SCContent.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsCobblestone");

		SCContent.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedSandstone").setTextureName("securitycraft:reinforcedSandstone");
		SCContent.reinforcedStairsSandstone = new BlockReinforcedStairs(SCContent.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsSandstone");

		SCContent.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedWoodSlabs");
		SCContent.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setUnlocalizedName("securitycraft:reinforcedDoubleWoodSlabs");
		SCContent.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStoneSlabs");
		SCContent.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setUnlocalizedName("securitycraft:reinforcedDoubleStoneSlabs");
		SCContent.reinforcedDirtSlab = new BlockReinforcedSlabs(false, Material.ground).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeGravel).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedDirtSlab");
		SCContent.reinforcedDoubleDirtSlab = new BlockReinforcedSlabs(true, Material.ground).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeGravel).setUnlocalizedName("securitycraft:reinforcedDoubleDirtSlab");

		SCContent.protecto = new BlockProtecto(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setLightLevel(0.5F).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:protecto").setTextureName("securitycraft:protectoParticleTexture");

		SCContent.scannerDoor = new BlockScannerDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("securitycraft:scannerDoor");

		SCContent.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStoneBrick").setTextureName("securitycraft:reinforcedStoneBrick");
		SCContent.reinforcedStairsStoneBrick = new BlockReinforcedStairs(SCContent.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsStoneBrick");

		SCContent.reinforcedMossyCobblestone = new BlockReinforcedBase(Material.rock, 1, Blocks.mossy_cobblestone).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedMossyCobblestone").setTextureName("securitycraft:reinforcedMossyCobblestone");

		SCContent.reinforcedBrick = new BlockReinforcedBase(Material.rock, 1, Blocks.brick_block).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedBrick").setTextureName("securitycraft:reinforcedBrick");
		SCContent.reinforcedStairsBrick = new BlockReinforcedStairs(SCContent.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsBrick");

		SCContent.reinforcedNetherBrick = new BlockReinforcedBase(Material.rock, 1, Blocks.nether_brick).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedNetherBrick").setTextureName("securitycraft:reinforcedNetherBrick");
		SCContent.reinforcedStairsNetherBrick = new BlockReinforcedStairs(SCContent.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsNetherBrick");

		SCContent.reinforcedHardenedClay = new BlockReinforcedBase(Material.rock, 1, Blocks.hardened_clay).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedHardenedClay").setTextureName("securitycraft:reinforcedHardenedClay");
		SCContent.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStainedHardenedClay").setTextureName("securitycraft:reinforcedStainedHardenedClay");

		SCContent.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedLogs").setTextureName("securitycraft:reinforcedLogs");
		SCContent.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedLogs2").setTextureName("securitycraft:reinforcedLogs2");

		SCContent.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedMetals").setTextureName("securitycraft:reinforcedMetals");
		SCContent.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedCompressedBlocks").setTextureName("securitycraft:reinforcedCompressedBlocks");

		SCContent.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeCloth).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedWool").setTextureName("securitycraft:reinforcedWool");

		SCContent.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston/*?!?!?!?*/).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedQuartz").setTextureName("securitycraft:reinforcedQuartz");
		SCContent.reinforcedStairsQuartz = new BlockReinforcedStairs(SCContent.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypePiston).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedStairsQuartz");

		SCContent.secretSignStanding = new BlockSecretSign(true).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:secretSign");
		SCContent.secretSignWall = new BlockSecretSign(false).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:secretSign");

		SCContent.motionActivatedLightOff = new BlockMotionActivatedLight(Material.glass).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("securitycraft:motionActivatedLightOff").setStepSound(Block.soundTypeGlass).setCreativeTab(SecurityCraft.tabSCTechnical).setTextureName("securitycraft:motion_activated_light_particles");
		SCContent.motionActivatedLightOn = new BlockMotionActivatedLight(Material.glass).setBlockUnbreakable().setResistance(1000F).setLightLevel(1.0F).setUnlocalizedName("securitycraft:motionActivatedLightOn").setStepSound(Block.soundTypeGlass).setTextureName("securitycraft:motion_activated_light_particles");

		SCContent.reinforcedObsidian = new BlockReinforcedBase(Material.rock, 1, Blocks.obsidian).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedObsidian");

		SCContent.reinforcedNetherrack = new BlockReinforcedBase(Material.rock, 1, Blocks.netherrack).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedNetherrack");

		SCContent.reinforcedEndStone = new BlockReinforcedBase(Material.rock, 1, Blocks.end_stone).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedEndStone");

		SCContent.reinforcedCarpet = new BlockReinforcedCarpet().setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeCloth).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedCarpet");

		SCContent.reinforcedGlowstone = new BlockReinforcedBase(Material.glass, 1, Blocks.glowstone).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeGlass).setLightLevel(1.0F).setCreativeTab(SecurityCraft.tabSCDecoration).setUnlocalizedName("securitycraft:reinforcedGlowstone");
	}

	public static void setupMines()
	{
		SCContent.mine = (BlockMine) new BlockMine(Material.circuits, false).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setUnlocalizedName("securitycraft:mine");
		SCContent.mineCut = (BlockMine) new BlockMine(Material.circuits, true).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setUnlocalizedName("securitycraft:mineCut");

		SCContent.dirtMine = new BlockFullMineBase(Material.ground, Blocks.dirt).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("securitycraft:dirtMine").setTextureName("dirt");
		SCContent.stoneMine = new BlockFullMineBase(Material.rock, Blocks.stone).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 2.5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("securitycraft:stoneMine").setTextureName("stone");
		SCContent.cobblestoneMine = new BlockFullMineBase(Material.rock, Blocks.cobblestone).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 2.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("securitycraft:cobblestoneMine").setTextureName("cobblestone");
		SCContent.sandMine = new BlockFullMineFalling(Material.sand, Blocks.sand).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeSand).setUnlocalizedName("securitycraft:sandMine").setTextureName("sand");
		SCContent.diamondOreMine = new BlockFullMineBase(Material.rock, Blocks.diamond_ore).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("securitycraft:diamondMine").setTextureName("diamond_ore");
		SCContent.furnaceMine = new BlockFurnaceMine(Material.rock).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("securitycraft:furnaceMine");
		SCContent.gravelMine = new BlockFullMineFalling(Material.ground, Blocks.gravel).setCreativeTab(SecurityCraft.tabSCMine).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("securitycraft:gravelMine").setTextureName("gravel");

		SCContent.trackMine = new BlockTrackMine().setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 0.7F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCMine).setUnlocalizedName("securitycraft:trackMine").setTextureName("securitycraft:rail_mine");

		SCContent.bouncingBetty = new BlockBouncingBetty(Material.circuits).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(SecurityCraft.tabSCMine).setUnlocalizedName("securitycraft:bouncingBetty");

		SCContent.claymoreActive = new BlockClaymore(Material.circuits, true).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(SecurityCraft.tabSCMine).setUnlocalizedName("securitycraft:claymoreActive").setTextureName("securitycraft:claymore");
		SCContent.claymoreDefused = new BlockClaymore(Material.circuits, false).setHardness(!SecurityCraft.config.ableToBreakMines ? -1F : 1F).setResistance(3F).setUnlocalizedName("securitycraft:claymoreDefused").setTextureName("securitycraft:claymore");

		SCContent.ims = new BlockIMS(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(SecurityCraft.tabSCMine).setUnlocalizedName("securitycraft:ims").setTextureName("securitycraft:ims");
	}

	public static void setupItems()
	{
		SCContent.codebreaker = new ItemCodebreaker().setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:codebreaker").setTextureName("securitycraft:CodeBreaker1");

		SCContent.keycards = new ItemKeycardBase().setUnlocalizedName("securitycraft:keycards");

		SCContent.reinforcedDoorItem = new ItemReinforcedDoor().setUnlocalizedName("securitycraft:doorIndestructibleIronItem").setCreativeTab(SecurityCraft.tabSCDecoration).setTextureName("securitycraft:doorReinforcedIron");

		SCContent.universalBlockRemover = new Item().setMaxStackSize(1).setMaxDurability(476).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalBlockRemover").setTextureName("securitycraft:universalBlockRemover");

		SCContent.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:remoteAccessMine").setTextureName("securitycraft:remoteAccessDoor").setMaxStackSize(1);

		SCContent.fWaterBucket = new ItemModifiedBucket(SCContent.bogusWaterFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:bucketFWater").setTextureName("securitycraft:bucketFWater");
		SCContent.fLavaBucket = new ItemModifiedBucket(SCContent.bogusLavaFlowing).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:bucketFLava").setTextureName("securitycraft:bucketFLava");

		SCContent.universalBlockModifier = new Item().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalBlockModifier").setTextureName("securitycraft:universalBlockModifier");

		SCContent.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setUnlocalizedName("securitycraft:redstoneModule").setTextureName("securitycraft:redstoneModule");
		SCContent.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setUnlocalizedName("securitycraft:whitelistModule").setTextureName("securitycraft:whitelistModule");
		SCContent.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setUnlocalizedName("securitycraft:blacklistModule").setTextureName("securitycraft:blacklistModule");
		SCContent.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setUnlocalizedName("securitycraft:harmingModule").setTextureName("securitycraft:harmingModule");
		SCContent.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setUnlocalizedName("securitycraft:smartModule").setTextureName("securitycraft:smartModule");
		SCContent.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setUnlocalizedName("securitycraft:storageModule").setTextureName("securitycraft:storageModule");
		SCContent.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setUnlocalizedName("securitycraft:disguiseModule").setTextureName("securitycraft:disguiseModule");

		SCContent.wireCutters = new Item().setMaxStackSize(1).setMaxDurability(476).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:wireCutters").setTextureName("securitycraft:wireCutter");

		SCContent.keyPanel = new ItemKeyPanel().setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:keypadItem").setTextureName("securitycraft:keypadItem");

		SCContent.adminTool = new ItemAdminTool().setMaxStackSize(1).setUnlocalizedName("securitycraft:adminTool").setTextureName("securitycraft:adminTool");

		SCContent.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:cameraMonitor").setTextureName("securitycraft:cameraMonitor");

		SCContent.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:scManual").setTextureName("securitycraft:scManual");

		SCContent.taser = new ItemTaser(false).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:taser");
		SCContent.taserPowered = new ItemTaser(true).setMaxStackSize(1).setUnlocalizedName("securitycraft:taser");

		SCContent.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDurability(48).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalOwnerChanger").setTextureName("securitycraft:universalOwnerChanger");

		SCContent.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalBlockReinforcerLvL1").setTextureName("securitycraft:universalBlockReinforcerLvL1");
		SCContent.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalBlockReinforcerLvL2").setTextureName("securitycraft:universalBlockReinforcerLvL2");
		SCContent.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalBlockReinforcerLvL3").setTextureName("securitycraft:universalBlockReinforcerLvL3");

		SCContent.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:briefcase");

		SCContent.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(SecurityCraft.tabSCTechnical).setUnlocalizedName("securitycraft:universalKeyChanger").setTextureName("securitycraft:universalKeyChanger");

		SCContent.scannerDoorItem = new ItemScannerDoor().setUnlocalizedName("securitycraft:scannerDoorItem").setCreativeTab(SecurityCraft.tabSCDecoration).setTextureName("securitycraft:scannerDoor");

		SCContent.secretSignItem = new ItemSecretSign().setUnlocalizedName("securitycraft:secretSignItem").setCreativeTab(SecurityCraft.tabSCDecoration).setTextureName("minecraft:sign");
	}
}
