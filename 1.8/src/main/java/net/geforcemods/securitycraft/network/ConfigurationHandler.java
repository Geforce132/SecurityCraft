package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
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
import net.geforcemods.securitycraft.blocks.BlockOwnable;
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
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedQuartz;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedRedSandstone;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSandstone;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs2;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedGlassPanes;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStainedHardenedClay;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStairs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWood;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWool;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedMetals;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPrismarine;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedQuartz;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs2;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedGlassPanes;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockTinted;
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
import net.geforcemods.securitycraft.items.ItemTestItem;
import net.geforcemods.securitycraft.items.ItemUniversalBlockModifier;
import net.geforcemods.securitycraft.items.ItemUniversalBlockReinforcer;
import net.geforcemods.securitycraft.items.ItemUniversalBlockRemover;
import net.geforcemods.securitycraft.items.ItemUniversalKeyChanger;
import net.geforcemods.securitycraft.items.ItemUniversalOwnerChanger;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketGivePotionEffect;
import net.geforcemods.securitycraft.network.packets.PacketSAddModules;
import net.geforcemods.securitycraft.network.packets.PacketSCheckPassword;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.geforcemods.securitycraft.network.packets.PacketSOpenGui;
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSSetOwner;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateSliderValue;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.network.packets.PacketSetKeycardLevel;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigurationHandler{
	private int[]  harmingPotions = {8268, 8236, 16460, 16428};
	private int[]  healingPotions = {8261, 8229, 16453, 16421};
	
	//******************configuration options
	public boolean allowCodebreakerItem;
	public boolean allowAdminTool;
	public boolean shouldSpawnFire;
	public boolean ableToBreakMines;
	public boolean ableToCraftKeycard1;
	public boolean ableToCraftKeycard2;
	public boolean ableToCraftKeycard3;
	public boolean ableToCraftKeycard4;
	public boolean ableToCraftKeycard5;
	public boolean ableToCraftLUKeycard;
	public boolean smallerMineExplosion;
	public boolean mineExplodesWhenInCreative;
	public boolean sayThanksMessage;
	public boolean isIrcBotEnabled;
	public boolean disconnectOnWorldClose;
	public boolean useOldKeypadRecipe;
	public boolean checkForUpdates;
	public double portableRadarSearchRadius;
	public int usernameLoggerSearchRadius;	
    public int laserBlockRange;
	public int alarmTickDelay;
	public double alarmSoundVolume;
	public int cageTrapTextureIndex;
	public int empRadius;
	public int portableRadarDelay;
	public int claymoreRange;
	public int imsRange;
	public float cameraSpeed;
	//***************************************

	public void setupAdditions(){
		this.setupTechnicalBlocks();
		this.setupMines();
		this.setupItems();
	}
	
	public void setupDebugAdditions(){
		this.setupDebuggingBlocks();
		this.setupDebuggingItems();
		
		this.registerDebuggingAdditions();
	}

	public void setupTechnicalBlocks(){
		mod_SecurityCraft.laserBlock = new BlockLaserBlock(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("laserBlock");
		mod_SecurityCraft.laser = new BlockLaserField(Material.rock).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("laser");
		
		mod_SecurityCraft.keypad = new BlockKeypad(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypad");
						
		mod_SecurityCraft.retinalScanner = new BlockRetinalScanner(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("retinalScanner");
	    
		mod_SecurityCraft.reinforcedDoor = new BlockReinforcedDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("ironDoorReinforced");
		
		mod_SecurityCraft.bogusLava = (BlockStaticLiquid) new BlockFakeLavaBase(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("bogusLava");
		mod_SecurityCraft.bogusLavaFlowing = new BlockFakeLava(Material.lava).setHardness(0.0F).setLightLevel(1.0F).setUnlocalizedName("bogusLavaFlowing");
		mod_SecurityCraft.bogusWater = (BlockStaticLiquid) new BlockFakeWaterBase(Material.water).setHardness(100.0F).setUnlocalizedName("bogusWater");
		mod_SecurityCraft.bogusWaterFlowing = new BlockFakeWater(Material.water).setHardness(0.0F).setUnlocalizedName("bogusWaterFlowing");
		
		mod_SecurityCraft.keycardReader = new BlockKeycardReader(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keycardReader");
	    
		mod_SecurityCraft.ironTrapdoor = new BlockIronTrapDoor(Material.iron).setHardness(5.0F).setResistance(200F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedIronTrapdoor");

		mod_SecurityCraft.inventoryScanner = new BlockInventoryScanner(Material.rock).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeStone).setUnlocalizedName("inventoryScanner");
		mod_SecurityCraft.inventoryScannerField = new BlockInventoryScannerField(Material.glass).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("inventoryScannerField");
				
	    mod_SecurityCraft.cageTrap = new BlockCageTrap(Material.rock).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("cageTrap");
		
	    mod_SecurityCraft.portableRadar = new BlockPortableRadar(Material.circuits).setHardness(1F).setResistance(50F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("portableRadar");
	    
	    mod_SecurityCraft.unbreakableIronBars = new BlockReinforcedIronBars(Material.iron, true).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("reinforcedIronBars");
	    
		mod_SecurityCraft.keypadChest = new BlockKeypadChest(0).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadChest");
	
	    mod_SecurityCraft.usernameLogger = new BlockLogger(Material.rock).setHardness(8F).setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("usernameLogger");
	
		mod_SecurityCraft.reinforcedGlassPane = new BlockReinforcedGlassPane(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedGlass");
	
		mod_SecurityCraft.alarm = new BlockAlarm(Material.iron, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("alarm");
		mod_SecurityCraft.alarmLit = new BlockAlarm(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setUnlocalizedName("alarmLit");

		mod_SecurityCraft.reinforcedStone = new BlockOwnable(Material.rock, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStone");
	
		mod_SecurityCraft.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedFenceGate");
		
		mod_SecurityCraft.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedPlanks");
	
		mod_SecurityCraft.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("panicButton");
	
		mod_SecurityCraft.frame = new BlockFrame(Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadFrame");
	
		mod_SecurityCraft.keypadFurnace = new BlockKeypadFurnace(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeMetal).setUnlocalizedName("keypadFurnace");
	
	    mod_SecurityCraft.securityCamera = new BlockSecurityCamera(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("securityCamera");
	
	    mod_SecurityCraft.reinforcedStairsOak = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsOak");
	    mod_SecurityCraft.reinforcedStairsSpruce = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsSpruce");
	    mod_SecurityCraft.reinforcedStairsBirch = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsBirch");
	    mod_SecurityCraft.reinforcedStairsJungle = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsJungle");
	    mod_SecurityCraft.reinforcedStairsAcacia = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsAcacia");
	    mod_SecurityCraft.reinforcedStairsDarkoak = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsDarkoak");
	    mod_SecurityCraft.reinforcedStairsStone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsStone");

	    mod_SecurityCraft.ironFence = new BlockIronFence(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("electrifiedIronFence");
	
	    mod_SecurityCraft.reinforcedGlass = new BlockReinforcedGlass(Material.glass).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedGlassBlock");
	    mod_SecurityCraft.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.glass).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStainedGlass");
	    mod_SecurityCraft.reinforcedStainedGlassPanes = new BlockReinforcedStainedGlassPanes().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStainedGlassPanes");
	
	    mod_SecurityCraft.reinforcedDirt = new BlockOwnable(Material.ground, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGravel).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedDirt");
		
		mod_SecurityCraft.reinforcedCobblestone = new BlockOwnable(Material.rock, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedCobblestone");
	    mod_SecurityCraft.reinforcedStairsCobblestone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsCobblestone");

	    mod_SecurityCraft.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedSandstone");
	    mod_SecurityCraft.reinforcedStairsSandstone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsSandstone");

	    mod_SecurityCraft.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedWoodSlabs");
	    mod_SecurityCraft.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setUnlocalizedName("reinforcedDoubleWoodSlabs");
	    mod_SecurityCraft.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStoneSlabs");
	    mod_SecurityCraft.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setUnlocalizedName("reinforcedDoubleStoneSlabs");
	    mod_SecurityCraft.reinforcedDirtSlab = new BlockReinforcedSlabs(false, Material.ground).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeGravel).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedDirtSlab");
	    mod_SecurityCraft.reinforcedDoubleDirtSlab = new BlockReinforcedSlabs(true, Material.ground).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeGravel).setUnlocalizedName("reinforcedDoubleDirtSlab");
	
		mod_SecurityCraft.protecto = new BlockProtecto(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setLightLevel(0.5F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("protecto");
	
		mod_SecurityCraft.scannerDoor = new BlockScannerDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("scannerDoor");

		mod_SecurityCraft.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStoneBrick");
	    mod_SecurityCraft.reinforcedStairsStoneBrick = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsStoneBrick");
	
	    mod_SecurityCraft.reinforcedMossyCobblestone = new BlockOwnable(Material.rock, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedMossyCobblestone");
	    
	    mod_SecurityCraft.reinforcedBrick = new BlockOwnable(Material.rock, true).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedBrick");
	    mod_SecurityCraft.reinforcedStairsBrick = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsBrick");
	    
	    mod_SecurityCraft.reinforcedNetherBrick = new BlockOwnable(Material.rock, true).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedNetherBrick");
	    mod_SecurityCraft.reinforcedStairsNetherBrick = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsNetherBrick");
	
	    mod_SecurityCraft.reinforcedHardenedClay = new BlockOwnable(Material.rock, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedHardenedClay");
		mod_SecurityCraft.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStainedHardenedClay");
	
		mod_SecurityCraft.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedLogs");
		mod_SecurityCraft.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedLogs2");
	
		mod_SecurityCraft.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedMetals");
		mod_SecurityCraft.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedCompressedBlocks");
		
		mod_SecurityCraft.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeCloth).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedWool");
	
		mod_SecurityCraft.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedQuartz");
	    mod_SecurityCraft.reinforcedStairsQuartz = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypePiston).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsQuartz");
	
		mod_SecurityCraft.reinforcedPrismarine = new BlockReinforcedPrismarine().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypePiston).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedPrismarine");
	
		mod_SecurityCraft.reinforcedRedSandstone = new BlockReinforcedRedSandstone().setBlockUnbreakable().setStepSound(Block.soundTypePiston).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedRedSandstone");
		mod_SecurityCraft.reinforcedStairsRedSandstone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedRedSandstone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypePiston).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStairsRedSandstone");
	
	    mod_SecurityCraft.reinforcedStoneSlabs2 = new BlockReinforcedSlabs2(false, Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStoneSlabs2");
	    mod_SecurityCraft.reinforcedDoubleStoneSlabs2 = new BlockReinforcedSlabs2(true, Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setUnlocalizedName("reinforcedDoubleStoneSlabs2");
	}
	
	public void setupMines(){
		mod_SecurityCraft.mine = (BlockMine) new BlockMine(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("mine");
		mod_SecurityCraft.mineCut = (BlockMine) new BlockMine(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setUnlocalizedName("mineCut");
		
		mod_SecurityCraft.dirtMine = new BlockFullMineBase(Material.ground, Blocks.dirt).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirtMine");
		mod_SecurityCraft.stoneMine = new BlockFullMineBase(Material.rock, Blocks.stone).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stoneMine");
		mod_SecurityCraft.cobblestoneMine = new BlockFullMineBase(Material.rock, Blocks.cobblestone).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("cobblestoneMine");
		mod_SecurityCraft.sandMine = new BlockFullMineBase(Material.sand, Blocks.sand).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeSand).setUnlocalizedName("sandMine");
		mod_SecurityCraft.diamondOreMine = new BlockFullMineBase(Material.rock, Blocks.diamond_ore).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("diamondMine");
		mod_SecurityCraft.furnaceMine = new BlockFurnaceMine(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("furnaceMine");
				
	    mod_SecurityCraft.trackMine = new BlockTrackMine().setHardness(!ableToBreakMines ? -1F : 0.7F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("trackMine");

		mod_SecurityCraft.bouncingBetty = new BlockBouncingBetty(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("bouncingBetty");
	
		mod_SecurityCraft.claymore = new BlockClaymore(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("claymore");
	
		mod_SecurityCraft.ims = new BlockIMS(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("ims");
	}
	
	public void setupItems(){
		mod_SecurityCraft.codebreaker = new ItemCodebreaker().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("codebreaker");
	    
		mod_SecurityCraft.keycardLV1 = new ItemKeycardBase(0).setUnlocalizedName("keycardLV1");
		mod_SecurityCraft.keycardLV2 = new ItemKeycardBase(1).setUnlocalizedName("keycardLV2");
		mod_SecurityCraft.keycardLV3 = new ItemKeycardBase(2).setUnlocalizedName("keycardLV3");
		mod_SecurityCraft.keycardLV4 = new ItemKeycardBase(4).setUnlocalizedName("keycardLV4");
		mod_SecurityCraft.keycardLV5 = new ItemKeycardBase(5).setUnlocalizedName("keycardLV5");
		mod_SecurityCraft.limitedUseKeycard = new ItemKeycardBase(3).setUnlocalizedName("limitedUseKeycard");

		mod_SecurityCraft.reinforcedDoorItem = new ItemReinforcedDoor().setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(mod_SecurityCraft.tabSCDecoration);
		
		mod_SecurityCraft.universalBlockRemover = new ItemUniversalBlockRemover().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockRemover");
		
		mod_SecurityCraft.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("remoteAccessMine");
		
		mod_SecurityCraft.fWaterBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusWaterFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFWater");
		
		mod_SecurityCraft.fLavaBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusLavaFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFLava");

		mod_SecurityCraft.universalBlockModifier = new ItemUniversalBlockModifier().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockModifier");
	
		mod_SecurityCraft.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setUnlocalizedName("redstoneModule");
		mod_SecurityCraft.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setUnlocalizedName("whitelistModule");
		mod_SecurityCraft.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setUnlocalizedName("blacklistModule");
		mod_SecurityCraft.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setUnlocalizedName("harmingModule");
		mod_SecurityCraft.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setUnlocalizedName("smartModule");
		mod_SecurityCraft.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setUnlocalizedName("storageModule");
		mod_SecurityCraft.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setUnlocalizedName("disguiseModule");

		mod_SecurityCraft.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("wireCutters");
	
		mod_SecurityCraft.keyPanel = new ItemKeyPanel().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadItem");
		
		mod_SecurityCraft.adminTool = new ItemAdminTool().setMaxStackSize(1).setUnlocalizedName("adminTool");
	
		mod_SecurityCraft.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("cameraMonitor");
	
		mod_SecurityCraft.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("scManual");
	
		mod_SecurityCraft.taser = new ItemTaser().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("taser");
	
		mod_SecurityCraft.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalOwnerChanger");
		
		mod_SecurityCraft.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockReinforcerLvL1");
		mod_SecurityCraft.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockReinforcerLvL2");
		mod_SecurityCraft.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockReinforcerLvL3");
	
	    mod_SecurityCraft.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("briefcase");
	
	    mod_SecurityCraft.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalKeyChanger");
	    
	    mod_SecurityCraft.scannerDoorItem = new ItemScannerDoor().setUnlocalizedName("scannerDoorItem").setCreativeTab(mod_SecurityCraft.tabSCDecoration);
	}
	
	public void setupDebuggingBlocks() {}
	
	public void setupDebuggingItems(){
		mod_SecurityCraft.testItem = new ItemTestItem().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("Test");
	    //mod_SecurityCraft.testChestplate = new ItemModifiedArmor(3213, armorBase, 0, 1).setUnlocalizedName("testChestplate").setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
		
	public void setupConfiguration() {
		mod_SecurityCraft.configFile.load();

		Property dummyProp;

		dummyProp = mod_SecurityCraft.configFile.get("options", "Is codebreaker allowed?", true);
		dummyProp.setLanguageKey("config.isCodebreakerAllowed");
		allowCodebreakerItem = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Is admin tool allowed?", false);
		dummyProp.setLanguageKey("config.allowAdminTool");
		allowAdminTool = dummyProp.getBoolean(false);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Mine(s) spawn fire when detonated?", true);
		dummyProp.setLanguageKey("config.shouldSpawnFire");
		shouldSpawnFire = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Are mines unbreakable?", true);
		dummyProp.setLanguageKey("config.ableToBreakMines");
		ableToBreakMines = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Craftable level 1 keycard?", true);
		dummyProp.setLanguageKey("config.ableToCraftKeycard1");
		ableToCraftKeycard1 = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Craftable level 2 keycard?", true);
		dummyProp.setLanguageKey("config.ableToCraftKeycard2");
		ableToCraftKeycard2 = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Craftable level 3 keycard?", true);
		dummyProp.setLanguageKey("config.ableToCraftKeycard3");
		ableToCraftKeycard3 = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Craftable level 4 keycard?", true);
		dummyProp.setLanguageKey("config.ableToCraftKeycard4");
		ableToCraftKeycard4 = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Craftable level 5 keycard?", true);
		dummyProp.setLanguageKey("config.ableToCraftKeycard5");
		ableToCraftKeycard5 = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Craftable Limited Use keycard?", true);
		dummyProp.setLanguageKey("config.ableToCraftLUKeycard");
		ableToCraftLUKeycard = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Mines use a smaller explosion?", false);
		dummyProp.setLanguageKey("config.smallerMineExplosion");
		smallerMineExplosion = dummyProp.getBoolean(false);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Mines explode when broken in Creative?", true);
		dummyProp.setLanguageKey("config.mineExplodesWhenInCreative");
		mineExplodesWhenInCreative = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Portable radar search radius:", 25);
		dummyProp.setLanguageKey("config.portableRadarSearchRadius");
		portableRadarSearchRadius = dummyProp.getDouble(25);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Username logger search radius:", 3);
		dummyProp.setLanguageKey("config.usernameLoggerSearchRadius");
		usernameLoggerSearchRadius = dummyProp.getInt(3);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Laser range:", 5);
		dummyProp.setLanguageKey("config.laserBlockRange");
		laserBlockRange = dummyProp.getInt(5);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Delay between alarm sounds (seconds):", 2);
		dummyProp.setLanguageKey("config.alarmTickDelay");
		alarmTickDelay = dummyProp.getInt(2);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Alarm sound volume:", 0.8D);
		dummyProp.setLanguageKey("config.alarmSoundVolume");
		alarmSoundVolume = dummyProp.getDouble(0.8D);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Portable radar delay (seconds):", 4);
		dummyProp.setLanguageKey("config.portableRadarDelay");
		portableRadarDelay = dummyProp.getInt(4);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Claymore range:", 5);
		dummyProp.setLanguageKey("config.claymoreRange");
		claymoreRange = dummyProp.getInt(5);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "IMS range:", 12);
		dummyProp.setLanguageKey("config.imsRange");
		imsRange = dummyProp.getInt(12);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Display a 'tip' message at spawn?", true);
		dummyProp.setLanguageKey("config.sayThanksMessage");
		sayThanksMessage = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Is debug mode? (not recommended!)", false);
		dummyProp.setLanguageKey("config.debuggingMode");
		mod_SecurityCraft.debuggingMode = dummyProp.getBoolean(false);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Is IRC bot enabled?", true);
		dummyProp.setLanguageKey("config.isIrcBotEnabled");
		isIrcBotEnabled = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Disconnect IRC bot on world exited?", true);
		dummyProp.setLanguageKey("config.disconnectOnWorldClose");
		disconnectOnWorldClose = dummyProp.getBoolean(true);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Use old keypad recipe (9 buttons)?", false);
		dummyProp.setLanguageKey("config.useOldKeypadRecipe");
		useOldKeypadRecipe = dummyProp.getBoolean(false);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Camera Speed when not using LookingGlass:", 2);
		dummyProp.setLanguageKey("config.cameraSpeed");
		cameraSpeed = dummyProp.getInt(2);
		
		dummyProp = mod_SecurityCraft.configFile.get("options", "Should check for updates on Github?", true);
		dummyProp.setLanguageKey("config.checkForUpdates");
		checkForUpdates = dummyProp.getBoolean(true);

        if(mod_SecurityCraft.configFile.hasChanged()){
        	mod_SecurityCraft.configFile.save();
        }
	}
	
	public void setupGameRegistry(){
		registerBlock(mod_SecurityCraft.laserBlock);
		GameRegistry.registerBlock(mod_SecurityCraft.laser, mod_SecurityCraft.laser.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.keypad);
		registerBlock(mod_SecurityCraft.mine);
		GameRegistry.registerBlock(mod_SecurityCraft.mineCut,mod_SecurityCraft.mineCut.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.dirtMine);
		GameRegistry.registerBlock(mod_SecurityCraft.stoneMine, mod_SecurityCraft.stoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.cobblestoneMine, mod_SecurityCraft.cobblestoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.diamondOreMine, mod_SecurityCraft.diamondOreMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.sandMine, mod_SecurityCraft.sandMine.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.furnaceMine);
		registerBlock(mod_SecurityCraft.retinalScanner);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoor, mod_SecurityCraft.reinforcedDoor.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusLava, mod_SecurityCraft.bogusLava.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusLavaFlowing, mod_SecurityCraft.bogusLavaFlowing.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusWater, mod_SecurityCraft.bogusWater.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusWaterFlowing, mod_SecurityCraft.bogusWaterFlowing.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.keycardReader);
		registerBlock(mod_SecurityCraft.ironTrapdoor);
		registerBlock(mod_SecurityCraft.bouncingBetty);
		registerBlock(mod_SecurityCraft.inventoryScanner);
		GameRegistry.registerBlock(mod_SecurityCraft.inventoryScannerField, mod_SecurityCraft.inventoryScannerField.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.trackMine);
		registerBlock(mod_SecurityCraft.cageTrap);
		registerBlock(mod_SecurityCraft.portableRadar);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedStone, ItemBlockTinted.class);
		registerBlockWithCustomRecipe(mod_SecurityCraft.keypadChest, new ItemStack[]{ null, ItemUtils.toItemStack(mod_SecurityCraft.keyPanel), null, null, ItemUtils.toItemStack(Items.redstone), null, null, ItemUtils.toItemStack(Item.getItemFromBlock(Blocks.chest)), null});
		registerBlock(mod_SecurityCraft.usernameLogger);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedGlassPane);
		registerBlock(mod_SecurityCraft.alarm);
		GameRegistry.registerBlock(mod_SecurityCraft.alarmLit, mod_SecurityCraft.alarmLit.getUnlocalizedName().substring(5));
		registerReinforcedBlock(mod_SecurityCraft.unbreakableIronBars);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedSandstone, ItemBlockReinforcedSandstone.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedDirt, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedCobblestone, ItemBlockTinted.class);
		registerBlock(mod_SecurityCraft.reinforcedFencegate);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedWoodPlanks, ItemBlockReinforcedPlanks.class);
		registerBlock(mod_SecurityCraft.panicButton);
		registerBlock(mod_SecurityCraft.frame);
		registerBlock(mod_SecurityCraft.claymore);
		registerBlock(mod_SecurityCraft.keypadFurnace);
		registerBlock(mod_SecurityCraft.securityCamera);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsOak, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsOak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsSpruce, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsSpruce.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsCobblestone, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsCobblestone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsSandstone, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsSandstone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsBirch, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsBirch.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsJungle, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsJungle.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsAcacia, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsAcacia.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsDarkoak, ItemBlockTinted.class, mod_SecurityCraft.reinforcedStairsDarkoak.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStairsStone, ItemBlockTinted.class);
		registerBlock(mod_SecurityCraft.ironFence);
		registerBlock(mod_SecurityCraft.ims);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedGlass);
		registerBlock(mod_SecurityCraft.reinforcedStainedGlass, ItemBlockReinforcedStainedGlass.class);
		registerBlock(mod_SecurityCraft.reinforcedStainedGlassPanes, ItemBlockReinforcedStainedGlassPanes.class);
		registerBlock(mod_SecurityCraft.reinforcedWoodSlabs, ItemBlockReinforcedWoodSlabs.class);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleWoodSlabs, ItemBlockTinted.class, mod_SecurityCraft.reinforcedDoubleWoodSlabs.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStoneSlabs, ItemBlockReinforcedSlabs.class);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleStoneSlabs, ItemBlockTinted.class, mod_SecurityCraft.reinforcedDoubleStoneSlabs.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDirtSlab, ItemBlockReinforcedSlabs.class, mod_SecurityCraft.reinforcedDirtSlab.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleDirtSlab, ItemBlockTinted.class, mod_SecurityCraft.reinforcedDoubleDirtSlab.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.protecto);
		GameRegistry.registerBlock(mod_SecurityCraft.scannerDoor, mod_SecurityCraft.scannerDoor.getUnlocalizedName().substring(5));
		registerReinforcedBlock(mod_SecurityCraft.reinforcedStoneBrick, ItemBlockReinforcedStoneBrick.class);
		registerBlock(mod_SecurityCraft.reinforcedStairsStoneBrick, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedMossyCobblestone, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedBrick, ItemBlockTinted.class);
		registerBlock(mod_SecurityCraft.reinforcedStairsBrick, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedNetherBrick, ItemBlockTinted.class);
		registerBlock(mod_SecurityCraft.reinforcedStairsNetherBrick, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedHardenedClay, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedStainedHardenedClay, ItemBlockReinforcedStainedBlock.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedOldLogs, ItemBlockReinforcedLog.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedNewLogs, ItemBlockReinforcedLog.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedMetals, ItemBlockReinforcedMetals.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedCompressedBlocks, ItemBlockReinforcedCompressedBlocks.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedWool, ItemBlockReinforcedStainedBlock.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedQuartz, ItemBlockReinforcedQuartz.class);
		registerBlock(mod_SecurityCraft.reinforcedStairsQuartz, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedPrismarine, ItemBlockReinforcedPrismarine.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedRedSandstone, ItemBlockReinforcedSandstone.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedStairsRedSandstone, ItemBlockTinted.class);
		registerReinforcedBlock(mod_SecurityCraft.reinforcedStoneSlabs2, ItemBlockReinforcedSlabs2.class); //technically not a reinforced block, but doesn't need a page
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleStoneSlabs2, ItemBlockTinted.class, mod_SecurityCraft.reinforcedDoubleStoneSlabs2.getUnlocalizedName().substring(5));
		
		registerItem(mod_SecurityCraft.codebreaker);
	    registerItem(mod_SecurityCraft.reinforcedDoorItem, mod_SecurityCraft.reinforcedDoorItem.getUnlocalizedName().substring(5));
	    registerItem(mod_SecurityCraft.scannerDoorItem, mod_SecurityCraft.scannerDoorItem.getUnlocalizedName().substring(5));
		registerItem(mod_SecurityCraft.universalBlockRemover);
		registerItem(mod_SecurityCraft.keycardLV1, ableToCraftKeycard1);
		registerItem(mod_SecurityCraft.keycardLV2, ableToCraftKeycard2);
		registerItem(mod_SecurityCraft.keycardLV3, ableToCraftKeycard3);
		registerItem(mod_SecurityCraft.keycardLV4, ableToCraftKeycard4);
		registerItem(mod_SecurityCraft.keycardLV5, ableToCraftKeycard5);
		registerItem(mod_SecurityCraft.limitedUseKeycard, ableToCraftLUKeycard);
		registerItem(mod_SecurityCraft.remoteAccessMine);
		registerItem(mod_SecurityCraft.fWaterBucket);
		registerItem(mod_SecurityCraft.fLavaBucket);
		registerItem(mod_SecurityCraft.universalBlockModifier);
		registerItem(mod_SecurityCraft.redstoneModule);
		registerItem(mod_SecurityCraft.whitelistModule);
		registerItem(mod_SecurityCraft.blacklistModule);
		registerItem(mod_SecurityCraft.harmingModule);
		registerItem(mod_SecurityCraft.smartModule);
		registerItem(mod_SecurityCraft.storageModule);
		registerItem(mod_SecurityCraft.disguiseModule);
		registerItem(mod_SecurityCraft.wireCutters);
		registerItem(mod_SecurityCraft.adminTool);
		registerItem(mod_SecurityCraft.keyPanel);
		registerItem(mod_SecurityCraft.cameraMonitor);
		registerItem(mod_SecurityCraft.taser);
		registerItem(mod_SecurityCraft.scManual);
		registerItem(mod_SecurityCraft.universalOwnerChanger);
		registerItem(mod_SecurityCraft.universalBlockReinforcerLvL1);
		registerItem(mod_SecurityCraft.universalBlockReinforcerLvL2);
		registerItem(mod_SecurityCraft.universalBlockReinforcerLvL3);
		registerItem(mod_SecurityCraft.briefcase);
		registerItem(mod_SecurityCraft.universalKeyChanger);

		GameRegistry.registerTileEntity(TileEntityOwnable.class, "abstractOwnable");
		GameRegistry.registerTileEntity(TileEntitySCTE.class, "abstractSC");
		GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, "laserBlock");
		GameRegistry.registerTileEntity(TileEntityCageTrap.class, "cageTrap");
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, "keycardReader");
		GameRegistry.registerTileEntity(TileEntityInventoryScanner.class, "inventoryScanner");
		GameRegistry.registerTileEntity(TileEntityPortableRadar.class, "portableRadar");
		GameRegistry.registerTileEntity(TileEntitySecurityCamera.class, "securityCamera");
		GameRegistry.registerTileEntity(TileEntityLogger.class, "usernameLogger");
		GameRegistry.registerTileEntity(TileEntityRetinalScanner.class, "retinalScanner");
		GameRegistry.registerTileEntity(TileEntityKeypadChest.class, "keypadChest");
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "alarm");
		GameRegistry.registerTileEntity(TileEntityClaymore.class, "claymore");
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, "keypadFurnace");
		GameRegistry.registerTileEntity(TileEntityIMS.class, "ims");
		GameRegistry.registerTileEntity(TileEntityProtecto.class, "protecto");
		GameRegistry.registerTileEntity(CustomizableSCTE.class, "customizableSCTE");
		GameRegistry.registerTileEntity(TileEntityScannerDoor.class, "scannerDoor");

		if(useOldKeypadRecipe){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keypad, 1), new Object[]{
				"III", "III", "III", 'I', Blocks.stone_button
			});
		}else{
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keyPanel, 1), new Object[]{
				"III", "IBI", "III", 'I', Blocks.stone_button, 'B', Blocks.heavy_weighted_pressure_plate
			});
			
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.frame, 1), new Object[]{
				"III", "IBI", "I I", 'I', Items.iron_ingot, 'B', Items.redstone
			});
		}
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.laserBlock, 1), new Object[]{
			"III", "IBI", "IPI", 'I', Blocks.stone, 'B', Blocks.redstone_block, 'P', Blocks.glass_pane
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.mine, 3), new Object[]{
			" I ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedDoorItem, 1), new Object[]{
			"III", "IDI", "III", 'I', Items.iron_ingot, 'D', Items.iron_door
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockRemover, 1), new Object[]{
			"SII",'I', Items.iron_ingot, 'S', Items.shears
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.ironTrapdoor, 1), new Object[]{
			"###", "#P#", "###", '#', Items.iron_ingot, 'P', Blocks.trapdoor
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardReader, 1), new Object[]{
			"SSS", "SHS", "SSS", 'S', Blocks.stone, 'H', Blocks.hopper
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.bouncingBetty, 1), new Object[]{
			" P ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder, 'P', Blocks.heavy_weighted_pressure_plate
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.codebreaker, 1), new Object[]{
			"DTD", "GSG", "RER", 'D', Items.diamond, 'T', Blocks.redstone_torch, 'G', Items.gold_ingot, 'S', Items.nether_star, 'R', Items.redstone, 'E', Items.emerald
		});
		
		if(ableToCraftKeycard1){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV1, 1), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.gold_ingot 
			});
		}
		
		if(ableToCraftKeycard2){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV2, 1), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.brick
			});
		}
		
		if(ableToCraftKeycard3){ 
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV3, 1), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.netherbrick
			});
		}
		
		if(ableToCraftKeycard4){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV4, 1), new Object[]{
				"III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 13)
			});
		}
		
		if(ableToCraftKeycard5){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV5, 1), new Object[]{
				"III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 5)
			});
		}
		
		if(ableToCraftLUKeycard){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.limitedUseKeycard, 1), new Object[]{
				"III", "LLL", 'I', Items.iron_ingot, 'L', new ItemStack(Items.dye, 1, 4)
			});
		}
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.trackMine, 4), new Object[]{
			"X X", "X#X", "XGX", 'X', Items.iron_ingot, '#', Items.stick, 'G', Items.gunpowder
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.portableRadar, 1), new Object[]{
			"III", "ITI", "IRI", 'I', Items.iron_ingot, 'T', Blocks.redstone_torch, 'R', Items.redstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keypadChest, 1), new Object[]{
			"K", "R", "C", 'K', mod_SecurityCraft.keyPanel, 'R', Items.redstone, 'C', Blocks.chest
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.remoteAccessMine, 1), new Object[]{
			" R ", " DG", "S  ", 'R', Blocks.redstone_torch, 'D', Items.diamond, 'G', Items.gold_ingot, 'S', Items.stick
		});
		
		for(int i = 0; i < 4; i++){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.fWaterBucket, 1), new Object[]{
				"P", "B", 'P', new ItemStack(Items.potionitem, 1, harmingPotions[i]), 'B', Items.water_bucket
			});
		}
		
		for(int i = 0; i < 4; i++){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.fLavaBucket, 1), new Object[]{
				"P", "B", 'P', new ItemStack(Items.potionitem, 1, healingPotions[i]), 'B', Items.lava_bucket
			});
		}
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.retinalScanner, 1), new Object[]{
			"SSS", "SES", "SSS", 'S', Blocks.stone, 'E', Items.ender_eye
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.inventoryScanner, 1), new Object[]{
			"SSS", "SLS", "SCS", 'S', Blocks.stone, 'L', mod_SecurityCraft.laserBlock, 'C', Blocks.ender_chest
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.cageTrap, 1), new Object[]{
			"BBB", "GRG", "III", 'B', mod_SecurityCraft.unbreakableIronBars, 'G', Items.gold_ingot, 'R', Items.redstone, 'I', Blocks.iron_block
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.alarm, 1), new Object[]{
			"GGG", "GNG", "GRG", 'G', Blocks.glass, 'R', Items.redstone, 'N', Blocks.noteblock
		});
		
//		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedFencegate, 1), new Object[]{
//			" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.fence_gate
//		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.wireCutters, 1), new Object[]{
			"SI ", "I I", " I ", 'I', Items.iron_ingot, 'S', Items.shears
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.panicButton, 1), new Object[]{
			" I ", "IBI", " R ", 'I', Items.iron_ingot, 'B', Blocks.stone_button, 'R', Items.redstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.whitelistModule, 1), new Object[]{
			"III", "IPI", "IPI", 'I', Items.iron_ingot, 'P', Items.paper
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.blacklistModule, 1), new Object[]{
			"III", "IPI", "IDI", 'I', Items.iron_ingot, 'P', Items.paper, 'D', new ItemStack(Items.dye, 1, 0)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.redstoneModule, 1), new Object[]{
			"III", "IPI", "IRI", 'I', Items.iron_ingot, 'P', Items.paper, 'R', Items.redstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.harmingModule, 1), new Object[]{
			"III", "IPI", "IAI", 'I', Items.iron_ingot, 'P', Items.paper, 'A', Items.arrow
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.smartModule, 1), new Object[]{
			"III", "IPI", "IEI", 'I', Items.iron_ingot, 'P', Items.paper, 'E', Items.ender_pearl
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.storageModule, 1), new Object[]{
			"III", "IPI", "ICI", 'I', Items.iron_ingot, 'P', Items.paper, 'C', mod_SecurityCraft.keypadChest
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.disguiseModule, 1), new Object[]{
				"III", "IPI", "IAI", 'I', Items.iron_ingot, 'P', Items.paper, 'A', Items.painting
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockModifier, 1), new Object[]{
			"ER ", "RI ", "  I", 'E', Items.emerald, 'R', Items.redstone, 'I', Items.iron_ingot
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockModifier, 1), new Object[]{
			" RE", " IR", "I  ", 'E', Items.emerald, 'R', Items.redstone, 'I', Items.iron_ingot
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.usernameLogger, 1), new Object[]{
			"SPS", "SRS", "SSS", 'S', Blocks.stone, 'P', mod_SecurityCraft.portableRadar, 'R', Items.redstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keypadFurnace, 1), new Object[]{
			"K", "F", "P", 'K', mod_SecurityCraft.frame, 'F', Blocks.furnace, 'P', mod_SecurityCraft.keyPanel
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.claymore, 1), new Object[]{
			"HSH", "SBS", "RGR", 'H', Blocks.tripwire_hook, 'S', Items.string, 'B', mod_SecurityCraft.bouncingBetty, 'R', Items.redstone, 'G', Items.gunpowder
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.ironFence, 1), new Object[]{
			" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.oak_fence
	    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsStone, 4), new Object[]{
			"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedStone
	    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsCobblestone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedCobblestone
		    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsSandstone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedSandstone
		    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsOak, 4), new Object[]{
		    "W  ", "WW ", "WWW", 'W', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, 0)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsSpruce, 4), new Object[]{
			"W  ", "WW ", "WWW", 'W', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, 1)
	    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsBirch, 4), new Object[]{
		    "W  ", "WW ", "WWW", 'W', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, 2)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsJungle, 4), new Object[]{
			"W  ", "WW ", "WWW", 'W', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, 3)
	    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsAcacia, 4), new Object[]{
		    "W  ", "WW ", "WWW", 'W', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, 4)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsDarkoak, 4), new Object[]{
			"W  ", "WW ", "WWW", 'W', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, 5)
	    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsStoneBrick, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedStoneBrick
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsBrick, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedBrick
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsNetherBrick, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedNetherBrick
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsQuartz, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedQuartz
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsRedSandstone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedRedSandstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.ims, 1), new Object[]{
		    "BPB", " I ", "B B", 'B', mod_SecurityCraft.bouncingBetty, 'P', mod_SecurityCraft.portableRadar, 'I', Blocks.iron_block
		});
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.cameraMonitor, 1), new Object[]{
		    "III", "IGI", "III", 'I', Items.iron_ingot, 'G', Blocks.glass_pane
		});
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.taser, 1), new Object[]{
    		"BGI", "RSG", "  S", 'B', Items.bow, 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'R', Items.redstone, 'S', Items.stick
        });
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.securityCamera, 1), new Object[]{
        	"III", "GRI", "IIS", 'I', Items.iron_ingot, 'G', mod_SecurityCraft.reinforcedGlassPane, 'R', Blocks.redstone_block, 'S', Items.stick
        });

        for(int i = 0; i < 16; i++){
        	GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 8, 15 - i), new Object[]{
        		"###", "#X#", "###", '#', new ItemStack(mod_SecurityCraft.reinforcedGlass), 'X', new ItemStack(Items.dye, 1, i)
        	});
        	
        	GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStainedGlassPanes, 16, i - 1), new Object[]{
        		"###", "###", '#', new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 1, i)
        	});

        	GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStainedHardenedClay, 8, ~i & 15), new Object[]{
					"###", "#X#", "###", '#', new ItemStack(mod_SecurityCraft.reinforcedHardenedClay), 'X', new ItemStack(Items.dye, 1, i)
			});
        }
	
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockReinforcerLvL1, 1), new Object[]{
        		" DG", "RLD", "SR ", 'G', Blocks .glass, 'D', Items.diamond, 'L', mod_SecurityCraft.laserBlock, 'R', Items.redstone, 'S', Items.stick
        });
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockReinforcerLvL2, 1), new Object[]{
        		" DG", "RLD", "SR ", 'G', new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 1, 15), 'D', Blocks.diamond_block, 'L', mod_SecurityCraft.laserBlock, 'R', Items.redstone, 'S', Items.stick
        });
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockReinforcerLvL3, 1), new Object[]{
        		" EG", "RNE", "SR ", 'G', new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 1, 6), 'E', Blocks.emerald_block, 'N', Items.nether_star, 'R', Blocks.redstone_block, 'S', Items.stick
        });
        
		for(int i = 0; i < 6; i++)
		{
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodSlabs, 6, i), new Object[]{
					"MMM", 'M', new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 1, i)
			});
		}

		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 0), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedStone
		});

		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 1), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedCobblestone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 2), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedSandstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedDirtSlab, 6, 3), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedDirt
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 4), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedStoneBrick
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 5), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedBrick
		});

		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 6), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedNetherBrick
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs, 6, 7), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedQuartz
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStoneSlabs2, 6, 0), new Object[]{
				"MMM", 'M', mod_SecurityCraft.reinforcedRedSandstone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.protecto, 1), new Object[]{
				"ODO", "OEO", "OOO", 'O', Blocks.obsidian, 'D', Blocks.daylight_detector, 'E', Items.ender_eye
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.briefcase, 1), new Object[]{
				"SSS", "ICI", "III", 'S', Items.stick, 'I', Items.iron_ingot, 'C', Blocks.chest
		});

		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalKeyChanger, 1), new Object[]{
				" RL", " IR", "I  ", 'R', Items.redstone, 'L', mod_SecurityCraft.laserBlock, 'I', Items.iron_ingot
		});
		
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.dirtMine, 1), new Object[] {Blocks.dirt, mod_SecurityCraft.mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.stoneMine, 1), new Object[] {Blocks.stone, mod_SecurityCraft.mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.cobblestoneMine, 1), new Object[] {Blocks.cobblestone, mod_SecurityCraft.mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.diamondOreMine, 1), new Object[] {Blocks.diamond_ore, mod_SecurityCraft.mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.sandMine, 1), new Object[] {Blocks.sand, mod_SecurityCraft.mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.furnaceMine, 1), new Object[] {Blocks.furnace, mod_SecurityCraft.mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.universalOwnerChanger, 1), new Object[] {mod_SecurityCraft.universalBlockModifier, Items.name_tag});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.scannerDoorItem), new Object[]{mod_SecurityCraft.reinforcedDoorItem, mod_SecurityCraft.retinalScanner});
	}

	/**
	 * Registers the given block with GameRegistry.registerBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 */
	private void registerBlock(Block block){
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}
	
	private void registerBlock(Block block, Class<? extends ItemBlock> itemClass){
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}
	
	boolean hasReinforcedPage = false;
	
	private void registerReinforcedBlock(Block block){
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
		
		if(!hasReinforcedPage)
		{
			mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.reinforced.info"));
			hasReinforcedPage = true;
		}
	}
	
	private void registerReinforcedBlock(Block block, Class<? extends ItemBlock> itemClass){
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));
		
		if(!hasReinforcedPage)
		{
			mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.reinforced.info"));
			hasReinforcedPage = true;
		}
	}
	
	/**
	 * Registers the given block with GameRegistry.registerBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 * Also overrides the default recipe that would've been drawn in the manual with a new recipe.
	 * 
	 */
	private void registerBlockWithCustomRecipe(Block block, ItemStack... customRecipe){ 
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));

		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info", customRecipe));
	}
	
	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item){
		registerItem(item, item.getUnlocalizedName().substring(5));
	}
	
	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private void registerItem(Item item, boolean configValue){
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info", configValue));
	}
	
	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item, String customName){
		GameRegistry.registerItem(item, customName);
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info"));
	}
	
	public void registerDebuggingAdditions(){		
		//GameRegistry.registerItem(mod_SecurityCraft.testItem, mod_SecurityCraft.testItem.getUnlocalizedName().substring(5));
	}
	
	public void setupOtherRegistries(){
		EnumCustomModules.refresh();
	}

	public void setupEntityRegistry() {
		EntityRegistry.registerModEntity(EntityBouncingBetty.class, "BBetty", 0, mod_SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTaserBullet.class, "TazerBullet", 2, mod_SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntityIMSBomb.class, "IMSBomb", 3, mod_SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntitySecurityCamera.class, "SecurityCamera", 4, mod_SecurityCraft.instance, 256, 20, false);
	}

	public void setupHandlers(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(mod_SecurityCraft.eventHandler);
	} 

	public void setupPackets(SimpleNetworkWrapper network) {
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 1, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 2, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 3, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 4, Side.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 5, Side.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 6, Side.SERVER);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 7, Side.CLIENT);
		network.registerMessage(PacketSetExplosiveState.Handler.class, PacketSetExplosiveState.class, 8, Side.SERVER);
		network.registerMessage(PacketGivePotionEffect.Handler.class, PacketGivePotionEffect.class, 9, Side.SERVER);
		network.registerMessage(PacketSSetOwner.Handler.class, PacketSSetOwner.class, 10, Side.SERVER);
		network.registerMessage(PacketSAddModules.Handler.class, PacketSAddModules.class, 11, Side.SERVER);
		network.registerMessage(PacketSSetPassword.Handler.class, PacketSSetPassword.class, 12, Side.SERVER);
		network.registerMessage(PacketSCheckPassword.Handler.class, PacketSCheckPassword.class, 13, Side.SERVER);
		network.registerMessage(PacketSSyncTENBTTag.Handler.class, PacketSSyncTENBTTag.class, 14, Side.SERVER);
		network.registerMessage(PacketSMountCamera.Handler.class, PacketSMountCamera.class, 15, Side.SERVER);
		network.registerMessage(PacketSSetCameraRotation.Handler.class, PacketSSetCameraRotation.class, 16, Side.SERVER);
		network.registerMessage(PacketCSetPlayerPositionAndRotation.Handler.class, PacketCSetPlayerPositionAndRotation.class, 17, Side.CLIENT);
		network.registerMessage(PacketSOpenGui.Handler.class, PacketSOpenGui.class, 18, Side.SERVER);
		network.registerMessage(PacketSToggleOption.Handler.class, PacketSToggleOption.class, 19, Side.SERVER);
		network.registerMessage(PacketSUpdateSliderValue.Handler.class, PacketSUpdateSliderValue.class, 20, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	public void setupTextureRegistry() {
		//Blocks 
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.keypad), 0, new ModelResourceLocation("securitycraft:keypad", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.frame), 0, new ModelResourceLocation("securitycraft:keypadFrame", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStone), 0, new ModelResourceLocation("securitycraft:reinforcedStone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.laserBlock), 0, new ModelResourceLocation("securitycraft:laserBlock", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.laser), 0, new ModelResourceLocation("securitycraft:laser", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), 0, new ModelResourceLocation("securitycraft:keypadChest", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedDoor), 0, new ModelResourceLocation("securitycraft:reinforcedIronDoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.ironTrapdoor), 0, new ModelResourceLocation("securitycraft:reinforcedIronTrapdoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.keycardReader), 0, new ModelResourceLocation("securitycraft:keycardReader", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.inventoryScanner), 0, new ModelResourceLocation("securitycraft:inventoryScanner", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.cageTrap), 0, new ModelResourceLocation("securitycraft:cageTrap", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.inventoryScannerField), 0, new ModelResourceLocation("securitycraft:inventoryScannerField", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.retinalScanner), 0, new ModelResourceLocation("securitycraft:retinalScanner", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedGlassPane), 0, new ModelResourceLocation("securitycraft:reinforcedGlass", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.unbreakableIronBars), 0, new ModelResourceLocation("securitycraft:reinforcedIronBars", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.portableRadar), 0, new ModelResourceLocation("securitycraft:portableRadar", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.alarm), 0, new ModelResourceLocation("securitycraft:alarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.alarmLit), 0, new ModelResourceLocation("securitycraft:alarmLit", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.usernameLogger), 0, new ModelResourceLocation("securitycraft:usernameLogger", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedFencegate), 0, new ModelResourceLocation("securitycraft:reinforcedFenceGate", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.ironFence), 0, new ModelResourceLocation("securitycraft:electrifiedIronFence", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Oak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 1, new ModelResourceLocation("securitycraft:reinforcedPlanks_Spruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 2, new ModelResourceLocation("securitycraft:reinforcedPlanks_Birch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 3, new ModelResourceLocation("securitycraft:reinforcedPlanks_Jungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 4, new ModelResourceLocation("securitycraft:reinforcedPlanks_Acacia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 5, new ModelResourceLocation("securitycraft:reinforcedPlanks_DarkOak", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsStone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsStone", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsCobblestone", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsOak), 0, new ModelResourceLocation("securitycraft:reinforcedStairsOak", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsSpruce), 0, new ModelResourceLocation("securitycraft:reinforcedStairsSpruce", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsBirch), 0, new ModelResourceLocation("securitycraft:reinforcedStairsBirch", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsJungle), 0, new ModelResourceLocation("securitycraft:reinforcedStairsJungle", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsAcacia), 0, new ModelResourceLocation("securitycraft:reinforcedStairsAcacia", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsDarkoak), 0, new ModelResourceLocation("securitycraft:reinforcedStairsDarkoak", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedGlass), 0, new ModelResourceLocation("securitycraft:reinforcedGlassBlock", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 0, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_white", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 1, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_orange", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 2, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_magenta", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 3, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_light_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 4, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_yellow", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 5, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_lime", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 6, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_pink", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 7, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_gray", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 8, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_silver", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 9, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_cyan", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 10, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_purple", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 11, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 12, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_brown", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 13, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_green", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 14, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_red", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 15, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_black", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 0, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_white", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 1, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_orange", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 2, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_magenta", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 3, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_light_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 4, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_yellow", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 5, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_lime", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 6, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_pink", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 7, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_gray", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 8, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_silver", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 9, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_cyan", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 10, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_purple", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 11, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 12, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_brown", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 13, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_green", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 14, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_red", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlassPanes), 15, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_black", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), 0, new ModelResourceLocation("securitycraft:keypadChest", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.keypadFurnace), 0, new ModelResourceLocation("securitycraft:keypadFurnace", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.panicButton), 0, new ModelResourceLocation("securitycraft:panicButton", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.securityCamera), 0, new ModelResourceLocation("securitycraft:securityCamera", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirt), 0, new ModelResourceLocation("securitycraft:reinforcedDirt", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedSandstone_normal", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedSandstone), 1, new ModelResourceLocation("securitycraft:reinforcedSandstone_chiseled", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedSandstone), 2, new ModelResourceLocation("securitycraft:reinforcedSandstone_smooth", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 0, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_oak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 1, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_spruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 2, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_birch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 3, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_jungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 4, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_acacia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 5, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_darkoak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsSandstone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 0, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_stone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 1, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_cobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 2, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_sandstone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirtSlab), 3, new ModelResourceLocation("securitycraft:reinforcedDirtSlab", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 4, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_stonebrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 5, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_brick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 6, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_netherbrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 7, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_quartz", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs2), 0, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs2_red_sandstone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.protecto), 0, new ModelResourceLocation("securitycraft:protecto", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.scannerDoor), 0, new ModelResourceLocation("securitycraft:scannerDoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_default", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 1, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_mossy", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 2, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_cracked", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 3, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_chiseled", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStairsStoneBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMossyCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedMossyCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedBrick), 0, new ModelResourceLocation("securitycraft:reinforcedBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStairsBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforcedNetherBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStairsNetherBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforcedHardenedClay", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_white", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 1, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_orange", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 2, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_magenta", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 3, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_light_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 4, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_yellow", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 5, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_lime", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 6, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_pink", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 7, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_gray", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 8, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_silver", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 9, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_cyan", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 10, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_purple", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 11, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 12, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_brown", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 13, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_green", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 14, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_red", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 15, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_black", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 0, new ModelResourceLocation("securitycraft:reinforcedLogs_oak", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 1, new ModelResourceLocation("securitycraft:reinforcedLogs_spruce", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 2, new ModelResourceLocation("securitycraft:reinforcedLogs_birch", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 3, new ModelResourceLocation("securitycraft:reinforcedLogs_jungle", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedNewLogs), 0, new ModelResourceLocation("securitycraft:reinforcedLogs2_acacia", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedNewLogs), 1, new ModelResourceLocation("securitycraft:reinforcedLogs2_big_oak", "inventory"));;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 0, new ModelResourceLocation("securitycraft:reinforcedMetals_gold", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 1, new ModelResourceLocation("securitycraft:reinforcedMetals_iron", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 2, new ModelResourceLocation("securitycraft:reinforcedMetals_diamond", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 3, new ModelResourceLocation("securitycraft:reinforcedMetals_emerald", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedCompressedBlocks), 0, new ModelResourceLocation("securitycraft:reinforcedCompressedBlocks_lapis", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedCompressedBlocks), 1, new ModelResourceLocation("securitycraft:reinforcedCompressedBlocks_coal", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 0, new ModelResourceLocation("securitycraft:reinforcedWool_white", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 1, new ModelResourceLocation("securitycraft:reinforcedWool_orange", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 2, new ModelResourceLocation("securitycraft:reinforcedWool_magenta", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 3, new ModelResourceLocation("securitycraft:reinforcedWool_light_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 4, new ModelResourceLocation("securitycraft:reinforcedWool_yellow", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 5, new ModelResourceLocation("securitycraft:reinforcedWool_lime", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 6, new ModelResourceLocation("securitycraft:reinforcedWool_pink", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 7, new ModelResourceLocation("securitycraft:reinforcedWool_gray", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 8, new ModelResourceLocation("securitycraft:reinforcedWool_silver", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 9, new ModelResourceLocation("securitycraft:reinforcedWool_cyan", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 10, new ModelResourceLocation("securitycraft:reinforcedWool_purple", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 11, new ModelResourceLocation("securitycraft:reinforcedWool_blue", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 12, new ModelResourceLocation("securitycraft:reinforcedWool_brown", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 13, new ModelResourceLocation("securitycraft:reinforcedWool_green", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 14, new ModelResourceLocation("securitycraft:reinforcedWool_red", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 15, new ModelResourceLocation("securitycraft:reinforcedWool_black", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedQuartz), 0, new ModelResourceLocation("securitycraft:reinforcedQuartz_default", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedQuartz), 1, new ModelResourceLocation("securitycraft:reinforcedQuartz_chiseled", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedQuartz), 2, new ModelResourceLocation("securitycraft:reinforcedQuartz_pillar", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsQuartz), 0, new ModelResourceLocation("securitycraft:reinforcedStairsQuartz", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPrismarine), 0, new ModelResourceLocation("securitycraft:reinforcedPrismarine_default", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPrismarine), 1, new ModelResourceLocation("securitycraft:reinforcedPrismarine_bricks", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPrismarine), 2, new ModelResourceLocation("securitycraft:reinforcedPrismarine_dark", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedRedSandstone_default", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedSandstone), 1, new ModelResourceLocation("securitycraft:reinforcedRedSandstone_chiseled", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedSandstone), 2, new ModelResourceLocation("securitycraft:reinforcedRedSandstone_smooth", "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsRedSandstone", "inventory"));

		//Items
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remoteAccessMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.reinforcedDoorItem, 0, new ModelResourceLocation("securitycraft:doorIndestructibleIronItem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.fWaterBucket, 0, new ModelResourceLocation("securitycraft:bucketFWater", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.fLavaBucket, 0, new ModelResourceLocation("securitycraft:bucketFLava", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV1, 0, new ModelResourceLocation("securitycraft:keycardLV1", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV2, 0, new ModelResourceLocation("securitycraft:keycardLV2", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV3, 0, new ModelResourceLocation("securitycraft:keycardLV3", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV4, 0, new ModelResourceLocation("securitycraft:keycardLV4", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV5, 0, new ModelResourceLocation("securitycraft:keycardLV5", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.limitedUseKeycard, 0, new ModelResourceLocation("securitycraft:limitedUseKeycard", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockRemover, 0, new ModelResourceLocation("securitycraft:universalBlockRemover", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockModifier, 0, new ModelResourceLocation("securitycraft:universalBlockModifier", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.whitelistModule, 0, new ModelResourceLocation("securitycraft:whitelistModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.blacklistModule, 0, new ModelResourceLocation("securitycraft:blacklistModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.redstoneModule, 0, new ModelResourceLocation("securitycraft:redstoneModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.harmingModule, 0, new ModelResourceLocation("securitycraft:harmingModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.storageModule, 0, new ModelResourceLocation("securitycraft:storageModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.smartModule, 0, new ModelResourceLocation("securitycraft:smartModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.disguiseModule, 0, new ModelResourceLocation("securitycraft:disguiseModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.wireCutters, 0, new ModelResourceLocation("securitycraft:wireCutters", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keyPanel, 0, new ModelResourceLocation("securitycraft:keypadItem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.adminTool, 0, new ModelResourceLocation("securitycraft:adminTool", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.cameraMonitor, 0, new ModelResourceLocation("securitycraft:cameraMonitor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.scManual, 0, new ModelResourceLocation("securitycraft:scManual", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.taser, 0, new ModelResourceLocation("securitycraft:taser", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalOwnerChanger, 0, new ModelResourceLocation("securitycraft:universalOwnerChanger", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockReinforcerLvL1, 0, new ModelResourceLocation("securitycraft:universalBlockReinforcerLvL1", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockReinforcerLvL2, 0, new ModelResourceLocation("securitycraft:universalBlockReinforcerLvL2", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockReinforcerLvL3, 0, new ModelResourceLocation("securitycraft:universalBlockReinforcerLvL3", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.briefcase, 0, new ModelResourceLocation("securitycraft:briefcase", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalKeyChanger, 0, new ModelResourceLocation("securitycraft:universalKeyChanger", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.scannerDoorItem, 0, new ModelResourceLocation("securitycraft:scannerDoorItem", "inventory"));
		
		//Mines
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.mine), 0, new ModelResourceLocation("securitycraft:mine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.dirtMine), 0, new ModelResourceLocation("securitycraft:dirtMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.stoneMine), 0, new ModelResourceLocation("securitycraft:stoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.cobblestoneMine), 0, new ModelResourceLocation("securitycraft:cobblestoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.sandMine), 0, new ModelResourceLocation("securitycraft:sandMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.diamondOreMine), 0, new ModelResourceLocation("securitycraft:diamondMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.furnaceMine), 0, new ModelResourceLocation("securitycraft:furnaceMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.trackMine), 0, new ModelResourceLocation("securitycraft:trackMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.bouncingBetty), 0, new ModelResourceLocation("securitycraft:bouncingBetty", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.claymore), 0, new ModelResourceLocation("securitycraft:claymore", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.ims), 0, new ModelResourceLocation("securitycraft:ims", "inventory"));
	}
}
