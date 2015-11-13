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
import net.geforcemods.securitycraft.blocks.BlockReinforcedDoor;
import net.geforcemods.securitycraft.blocks.BlockReinforcedFenceGate;
import net.geforcemods.securitycraft.blocks.BlockReinforcedGlass;
import net.geforcemods.securitycraft.blocks.BlockReinforcedGlassPane;
import net.geforcemods.securitycraft.blocks.BlockReinforcedIronBars;
import net.geforcemods.securitycraft.blocks.BlockReinforcedSandstone;
import net.geforcemods.securitycraft.blocks.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.blocks.BlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.blocks.BlockReinforcedStainedGlassPanes;
import net.geforcemods.securitycraft.blocks.BlockReinforcedStairs;
import net.geforcemods.securitycraft.blocks.BlockReinforcedWood;
import net.geforcemods.securitycraft.blocks.BlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.blocks.mines.BlockBouncingBetty;
import net.geforcemods.securitycraft.blocks.mines.BlockClaymore;
import net.geforcemods.securitycraft.blocks.mines.BlockFullMineBase;
import net.geforcemods.securitycraft.blocks.mines.BlockFurnaceMine;
import net.geforcemods.securitycraft.blocks.mines.BlockIMS;
import net.geforcemods.securitycraft.blocks.mines.BlockMine;
import net.geforcemods.securitycraft.blocks.mines.BlockTrackMine;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.entity.EntityTnTCompact;
import net.geforcemods.securitycraft.items.ItemAdminTool;
import net.geforcemods.securitycraft.items.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.items.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.items.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.items.ItemBlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.items.ItemBlockReinforcedStainedGlassPanes;
import net.geforcemods.securitycraft.items.ItemBlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.items.ItemCodebreaker;
import net.geforcemods.securitycraft.items.ItemKeyPanel;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.items.ItemMineRemoteAccessTool;
import net.geforcemods.securitycraft.items.ItemModifiedBucket;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.items.ItemReinforcedDoor;
import net.geforcemods.securitycraft.items.ItemSCManual;
import net.geforcemods.securitycraft.items.ItemTaser;
import net.geforcemods.securitycraft.items.ItemTestItem;
import net.geforcemods.securitycraft.items.ItemUniversalBlockModifier;
import net.geforcemods.securitycraft.items.ItemUniversalBlockReinforcer;
import net.geforcemods.securitycraft.items.ItemUniversalBlockRemover;
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
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSSetOwner;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.network.packets.PacketSetKeycardLevel;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
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
import net.geforcemods.securitycraft.tileentity.TileEntityRAM;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
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
import net.minecraft.util.StatCollector;
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
	public boolean fiveMinAutoShutoff;
	public boolean useOldKeypadRecipe;
	public int portableRadarSearchRadius;
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
		mod_SecurityCraft.LaserBlock = new BlockLaserBlock(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("laserBlock");
		mod_SecurityCraft.Laser = new BlockLaserField(Material.rock).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("laser");
		
		mod_SecurityCraft.Keypad = new BlockKeypad(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypad");
						
		mod_SecurityCraft.retinalScanner = new BlockRetinalScanner(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("retinalScanner");
	    
		mod_SecurityCraft.doorIndestructableIron = new BlockReinforcedDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("ironDoorReinforced");
		
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

		mod_SecurityCraft.reinforcedStone = new BlockOwnable(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedStone");
	
		mod_SecurityCraft.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedFenceGate");
		
		mod_SecurityCraft.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedPlanks");
	
		mod_SecurityCraft.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("panicButton");
	
		mod_SecurityCraft.frame = new BlockFrame(Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadFrame");
	
		mod_SecurityCraft.keypadFurnace = new BlockKeypadFurnace(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeMetal).setUnlocalizedName("keypadFurnace");
	
	    mod_SecurityCraft.securityCamera = new BlockSecurityCamera(Material.iron).setHardness(1.0F).setResistance(10.F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("securityCamera");
	
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
	
	    mod_SecurityCraft.reinforcedDirt = new BlockOwnable(Material.ground).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGravel).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedDirt");
		
		mod_SecurityCraft.reinforcedCobblestone = new BlockOwnable(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setUnlocalizedName("reinforcedCobblestone");
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
	}
	
	public void setupMines(){
		mod_SecurityCraft.Mine = (BlockMine) new BlockMine(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("mine");
		mod_SecurityCraft.MineCut = (BlockMine) new BlockMine(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setUnlocalizedName("mineCut");
		
		mod_SecurityCraft.DirtMine = new BlockFullMineBase(Material.ground).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirtMine");
		mod_SecurityCraft.StoneMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stoneMine");
		mod_SecurityCraft.CobblestoneMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("cobblestoneMine");
		mod_SecurityCraft.SandMine = new BlockFullMineBase(Material.sand).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeSand).setUnlocalizedName("sandMine");
		mod_SecurityCraft.DiamondOreMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("diamondMine");
		mod_SecurityCraft.FurnaceMine = new BlockFurnaceMine(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("furnaceMine");
				
	    mod_SecurityCraft.trackMine = new BlockTrackMine().setHardness(!ableToBreakMines ? -1F : 0.7F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("trackMine");

		mod_SecurityCraft.bouncingBetty = new BlockBouncingBetty(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("bouncingBetty");
	
		mod_SecurityCraft.claymore = new BlockClaymore(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("claymore");
	
		mod_SecurityCraft.ims = new BlockIMS(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("ims");
	}
	
	public void setupItems(){
		mod_SecurityCraft.Codebreaker = new ItemCodebreaker().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("codebreaker");
	    
		mod_SecurityCraft.keycardLV1 = new ItemKeycardBase(0).setUnlocalizedName("keycardLV1");
		mod_SecurityCraft.keycardLV2 = new ItemKeycardBase(1).setUnlocalizedName("keycardLV2");
		mod_SecurityCraft.keycardLV3 = new ItemKeycardBase(2).setUnlocalizedName("keycardLV3");
		mod_SecurityCraft.keycardLV4 = new ItemKeycardBase(4).setUnlocalizedName("keycardLV4");
		mod_SecurityCraft.keycardLV5 = new ItemKeycardBase(5).setUnlocalizedName("keycardLV5");
		mod_SecurityCraft.limitedUseKeycard = new ItemKeycardBase(3).setUnlocalizedName("limitedUseKeycard");

		mod_SecurityCraft.doorIndestructableIronItem = new ItemReinforcedDoor(Material.iron).setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(mod_SecurityCraft.tabSCDecoration);
		
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
	}
	
	public void setupDebuggingBlocks() {}
	
	public void setupDebuggingItems(){
		mod_SecurityCraft.testItem = new ItemTestItem().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("Test");
	    //mod_SecurityCraft.testChestplate = new ItemModifiedArmor(3213, armorBase, 0, 1).setUnlocalizedName("testChestplate").setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
		
	public void setupConfiguration() {
		mod_SecurityCraft.configFile.load();

        allowCodebreakerItem = mod_SecurityCraft.configFile.get("options", "Is codebreaker allowed?", true).getBoolean(true);
        allowAdminTool = mod_SecurityCraft.configFile.get("options", "Is admin tool allowed?", false).getBoolean(false);
        shouldSpawnFire = mod_SecurityCraft.configFile.get("options", "Mine(s) spawn fire when detonated?", true).getBoolean(true);
        ableToBreakMines = mod_SecurityCraft.configFile.get("options", "Are mines unbreakable?", true).getBoolean(true);
        ableToCraftKeycard1 = mod_SecurityCraft.configFile.get("options", "Craftable level 1 keycard?", true).getBoolean(true);
        ableToCraftKeycard2 = mod_SecurityCraft.configFile.get("options", "Craftable level 2 keycard?", true).getBoolean(true);
        ableToCraftKeycard3 = mod_SecurityCraft.configFile.get("options", "Craftable level 3 keycard?", true).getBoolean(true);
        ableToCraftKeycard4 = mod_SecurityCraft.configFile.get("options", "Craftable level 4 keycard?", true).getBoolean(true);
        ableToCraftKeycard5 = mod_SecurityCraft.configFile.get("options", "Craftable level 5 keycard?", true).getBoolean(true);
        ableToCraftLUKeycard = mod_SecurityCraft.configFile.get("options", "Craftable Limited Use keycard?", true).getBoolean(true);
        smallerMineExplosion = mod_SecurityCraft.configFile.get("options", "Mines use a smaller explosion?", false).getBoolean(false);
        mineExplodesWhenInCreative = mod_SecurityCraft.configFile.get("options", "Mines explode when broken in Creative?", true).getBoolean(true);
        fiveMinAutoShutoff = mod_SecurityCraft.configFile.get("options", "Monitors shutoff after 5 minutes?", true).getBoolean(true);

        portableRadarSearchRadius = mod_SecurityCraft.configFile.get("options", "Portable radar search radius:", 25).getInt(25);
        usernameLoggerSearchRadius = mod_SecurityCraft.configFile.get("options", "Username logger search radius:", 3).getInt(3);
        laserBlockRange = mod_SecurityCraft.configFile.get("options", "Laser range:", 5).getInt(5);
        alarmTickDelay = mod_SecurityCraft.configFile.get("options", "Delay between alarm sounds (seconds):", 2).getInt(2);
        alarmSoundVolume = mod_SecurityCraft.configFile.get("options", "Alarm sound volume:", 0.8D).getDouble(0.8D);
        portableRadarDelay = (mod_SecurityCraft.configFile.get("options", "Portable radar delay (seconds):", 4).getInt(4) * 20);
        claymoreRange = mod_SecurityCraft.configFile.get("options", "Claymore range:", 5).getInt(5);
        imsRange = mod_SecurityCraft.configFile.get("options", "IMS range:", 12).getInt(12);
        sayThanksMessage = mod_SecurityCraft.configFile.get("options", "Display a 'tip' message at spawn?", true).getBoolean(true);
        mod_SecurityCraft.debuggingMode = mod_SecurityCraft.configFile.get("options", "Is debug mode? (not recommended!)", false).getBoolean(false);
        isIrcBotEnabled = mod_SecurityCraft.configFile.get("options", "Disconnect IRC bot on world exited?", true).getBoolean(true);
        disconnectOnWorldClose = mod_SecurityCraft.configFile.get("options", "Is IRC bot enabled?", true).getBoolean(true);
        useOldKeypadRecipe = mod_SecurityCraft.configFile.get("options", "Use old keypad recipe (9 buttons)?", false).getBoolean(false);
		cameraSpeed = mod_SecurityCraft.configFile.get("options", "Camera Speed when not using LookingGlass (Default: 2)", 2).getInt(2);

        if(mod_SecurityCraft.configFile.hasChanged()){
        	mod_SecurityCraft.configFile.save();
        }
        
	}
	
	public void setupGameRegistry(){
		registerBlock(mod_SecurityCraft.LaserBlock);
		GameRegistry.registerBlock(mod_SecurityCraft.Laser, mod_SecurityCraft.Laser.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.Keypad);
		registerBlock(mod_SecurityCraft.Mine);
		GameRegistry.registerBlock(mod_SecurityCraft.MineCut,mod_SecurityCraft.MineCut.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.DirtMine);
		GameRegistry.registerBlock(mod_SecurityCraft.StoneMine, mod_SecurityCraft.StoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.CobblestoneMine, mod_SecurityCraft.CobblestoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.DiamondOreMine, mod_SecurityCraft.DiamondOreMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.SandMine, mod_SecurityCraft.SandMine.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.FurnaceMine);
		registerBlock(mod_SecurityCraft.retinalScanner);
		GameRegistry.registerBlock(mod_SecurityCraft.doorIndestructableIron, mod_SecurityCraft.doorIndestructableIron.getUnlocalizedName().substring(5));
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
		registerBlock(mod_SecurityCraft.unbreakableIronBars);
		registerBlock(mod_SecurityCraft.keypadChest);
		registerBlock(mod_SecurityCraft.usernameLogger);
		registerBlock(mod_SecurityCraft.reinforcedGlassPane);
		registerBlock(mod_SecurityCraft.alarm);
		GameRegistry.registerBlock(mod_SecurityCraft.alarmLit, mod_SecurityCraft.alarmLit.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStone);
		registerBlock(mod_SecurityCraft.reinforcedSandstone, ItemBlockReinforcedSandstone.class);
		registerBlock(mod_SecurityCraft.reinforcedDirt);
		registerBlock(mod_SecurityCraft.reinforcedCobblestone);
		registerBlock(mod_SecurityCraft.reinforcedFencegate);
		registerBlock(mod_SecurityCraft.reinforcedWoodPlanks, ItemBlockReinforcedPlanks.class);
		registerBlock(mod_SecurityCraft.panicButton);
		registerBlock(mod_SecurityCraft.frame);
		registerBlock(mod_SecurityCraft.claymore);
		registerBlock(mod_SecurityCraft.keypadFurnace);
		registerBlock(mod_SecurityCraft.securityCamera);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsOak, mod_SecurityCraft.reinforcedStairsOak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsSpruce, mod_SecurityCraft.reinforcedStairsSpruce.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsCobblestone, mod_SecurityCraft.reinforcedStairsCobblestone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsSandstone, mod_SecurityCraft.reinforcedStairsSandstone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsBirch, mod_SecurityCraft.reinforcedStairsBirch.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsJungle, mod_SecurityCraft.reinforcedStairsJungle.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsAcacia, mod_SecurityCraft.reinforcedStairsAcacia.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsDarkoak, mod_SecurityCraft.reinforcedStairsDarkoak.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStairsStone);
		registerBlock(mod_SecurityCraft.ironFence);
		registerBlock(mod_SecurityCraft.ims);
		registerBlock(mod_SecurityCraft.reinforcedGlass);
		registerBlock(mod_SecurityCraft.reinforcedStainedGlass, ItemBlockReinforcedStainedGlass.class);
		registerBlock(mod_SecurityCraft.reinforcedStainedGlassPanes, ItemBlockReinforcedStainedGlassPanes.class);
		registerBlock(mod_SecurityCraft.reinforcedWoodSlabs, ItemBlockReinforcedWoodSlabs.class);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleWoodSlabs, mod_SecurityCraft.reinforcedDoubleWoodSlabs.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStoneSlabs, ItemBlockReinforcedSlabs.class);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleStoneSlabs, mod_SecurityCraft.reinforcedDoubleStoneSlabs.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDirtSlab, ItemBlockReinforcedSlabs.class, mod_SecurityCraft.reinforcedDirtSlab.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedDoubleDirtSlab, mod_SecurityCraft.reinforcedDoubleDirtSlab.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.protecto);

		registerItem(mod_SecurityCraft.Codebreaker);
	    registerItem(mod_SecurityCraft.doorIndestructableIronItem, mod_SecurityCraft.doorIndestructableIronItem.getUnlocalizedName().substring(5));
		registerItem(mod_SecurityCraft.universalBlockRemover);
		registerItem(mod_SecurityCraft.keycardLV1);
		registerItem(mod_SecurityCraft.keycardLV2);
		registerItem(mod_SecurityCraft.keycardLV3);
		registerItem(mod_SecurityCraft.keycardLV4);
		registerItem(mod_SecurityCraft.keycardLV5);
		registerItem(mod_SecurityCraft.limitedUseKeycard);
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

		GameRegistry.registerTileEntity(TileEntityOwnable.class, "abstractOwnable");
		GameRegistry.registerTileEntity(TileEntitySCTE.class, "abstractSC");
		GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, "laserBlock");
		GameRegistry.registerTileEntity(TileEntityReinforcedDoor.class, "reinforcedDoor");
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, "keycardReader");
		GameRegistry.registerTileEntity(TileEntityRAM.class, "remoteAccessDoor");
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

		if(useOldKeypadRecipe){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.Keypad, 1), new Object[]{
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
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.LaserBlock, 1), new Object[]{
			"III", "IBI", "IPI", 'I', Blocks.stone, 'B', Blocks.redstone_block, 'P', Blocks.glass_pane
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.Mine, 3), new Object[]{
			" I ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.doorIndestructableIronItem, 1), new Object[]{
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
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.Codebreaker, 1), new Object[]{
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
			"SSS", "SLS", "SCS", 'S', Blocks.stone, 'L', mod_SecurityCraft.LaserBlock, 'C', Blocks.ender_chest
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
        }
	
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockReinforcerLvL1, 1), new Object[]{
        		" DG", "RLD", "SR ", 'G', Blocks .glass, 'D', Items.diamond, 'L', mod_SecurityCraft.LaserBlock, 'R', Items.redstone, 'S', Items.stick
        });
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.universalBlockReinforcerLvL2, 1), new Object[]{
        		" DG", "RLD", "SR ", 'G', new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 1, 15), 'D', Blocks.diamond_block, 'L', mod_SecurityCraft.LaserBlock, 'R', Items.redstone, 'S', Items.stick
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
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.protecto, 1), new Object[]{
				"ODO", "OEO", "OOO", 'O', Blocks.obsidian, 'D', Blocks.daylight_detector, 'E', Items.ender_eye
		});
        
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.DirtMine, 1), new Object[] {Blocks.dirt, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.StoneMine, 1), new Object[] {Blocks.stone, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.CobblestoneMine, 1), new Object[] {Blocks.cobblestone, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.DiamondOreMine, 1), new Object[] {Blocks.diamond_ore, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.SandMine, 1), new Object[] {Blocks.sand, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.FurnaceMine, 1), new Object[] {Blocks.furnace, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.universalOwnerChanger, 1), new Object[] {mod_SecurityCraft.universalBlockModifier, Items.name_tag});
	}

	/**
	 * Registers the given block with GameRegistry.registerBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 */
	private void registerBlock(Block block){
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), StatCollector.translateToLocal(block.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + block.getUnlocalizedName().substring(5) + ".info")));
	}
	
	private void registerBlock(Block block, Class<? extends ItemBlock> itemClass){
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), StatCollector.translateToLocal(block.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + block.getUnlocalizedName().substring(5) + ".info")));
	}
	
	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item){
		registerItem(item, item.getUnlocalizedName().substring(5));
	}
	
	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item, String customName){
		GameRegistry.registerItem(item, customName);
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, StatCollector.translateToLocal(item.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + item.getUnlocalizedName().substring(5) + ".info")));
	}
	
	public void registerDebuggingAdditions(){		
		//GameRegistry.registerItem(mod_SecurityCraft.testItem, mod_SecurityCraft.testItem.getUnlocalizedName().substring(5));
	}
	
	public void setupOtherRegistrys(){}

	public void setupEntityRegistry() {
		EntityRegistry.registerModEntity(EntityTnTCompact.class, "TnTCompact", 0, mod_SecurityCraft.instance, 128, 1, true);
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
	}

	@SideOnly(Side.CLIENT)
	public void setupTextureRegistry() {
		//Blocks 
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.Keypad), 0, new ModelResourceLocation("securitycraft:keypad", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.frame), 0, new ModelResourceLocation("securitycraft:keypadFrame", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStone), 0, new ModelResourceLocation("securitycraft:reinforcedStone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.LaserBlock), 0, new ModelResourceLocation("securitycraft:laserBlock", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.Laser), 0, new ModelResourceLocation("securitycraft:laser", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), 0, new ModelResourceLocation("securitycraft:keypadChest", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.doorIndestructableIron), 0, new ModelResourceLocation("securitycraft:reinforcedIronDoor", "inventory"));
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
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.protecto), 0, new ModelResourceLocation("securitycraft:protecto", "inventory"));

		//Items
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.Codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remoteAccessMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.doorIndestructableIronItem, 0, new ModelResourceLocation("securitycraft:doorIndestructibleIronItem", "inventory"));
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

		//Mines
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.Mine), 0, new ModelResourceLocation("securitycraft:mine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.DirtMine), 0, new ModelResourceLocation("securitycraft:dirtMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.StoneMine), 0, new ModelResourceLocation("securitycraft:stoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.CobblestoneMine), 0, new ModelResourceLocation("securitycraft:cobblestoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.SandMine), 0, new ModelResourceLocation("securitycraft:sandMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.DiamondOreMine), 0, new ModelResourceLocation("securitycraft:diamondMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.FurnaceMine), 0, new ModelResourceLocation("securitycraft:furnaceMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.trackMine), 0, new ModelResourceLocation("securitycraft:trackMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.bouncingBetty), 0, new ModelResourceLocation("securitycraft:bouncingBetty", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.claymore), 0, new ModelResourceLocation("securitycraft:claymore", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(mod_SecurityCraft.ims), 0, new ModelResourceLocation("securitycraft:ims", "inventory"));
	}
}
