package org.freeforums.geforce.securitycraft.network;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.freeforums.geforce.securitycraft.blocks.BlockActiveLaser;
import org.freeforums.geforce.securitycraft.blocks.BlockAlarm;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusLava;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusLavaBase;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusWater;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusWaterBase;
import org.freeforums.geforce.securitycraft.blocks.BlockCageTrap;
import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScanner;
import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScannerField;
import org.freeforums.geforce.securitycraft.blocks.BlockIronTrapDoor;
import org.freeforums.geforce.securitycraft.blocks.BlockKeycardReader;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypad;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypadChest;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypadFrame;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypadFurnace;
import org.freeforums.geforce.securitycraft.blocks.BlockLaser;
import org.freeforums.geforce.securitycraft.blocks.BlockLaserBlock;
import org.freeforums.geforce.securitycraft.blocks.BlockLogger;
import org.freeforums.geforce.securitycraft.blocks.BlockMonitor;
import org.freeforums.geforce.securitycraft.blocks.BlockOwnable;
import org.freeforums.geforce.securitycraft.blocks.BlockPanicButton;
import org.freeforums.geforce.securitycraft.blocks.BlockPortableRadar;
import org.freeforums.geforce.securitycraft.blocks.BlockReinforcedDoor;
import org.freeforums.geforce.securitycraft.blocks.BlockReinforcedFenceGate;
import org.freeforums.geforce.securitycraft.blocks.BlockReinforcedGlass;
import org.freeforums.geforce.securitycraft.blocks.BlockReinforcedWood;
import org.freeforums.geforce.securitycraft.blocks.BlockRetinalScanner;
import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.blocks.BlockUnbreakableBars;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockBouncingBetty;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockClaymore;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockFullMineBase;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockFurnaceMine;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockMine;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockTrackMine;
import org.freeforums.geforce.securitycraft.entity.EntityTaserBullet;
import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.items.ItemAdminTool;
import org.freeforums.geforce.securitycraft.items.ItemBlockReinforcedPlanks;
import org.freeforums.geforce.securitycraft.items.ItemCameraMonitor;
import org.freeforums.geforce.securitycraft.items.ItemCodebreaker;
import org.freeforums.geforce.securitycraft.items.ItemKeycardBase;
import org.freeforums.geforce.securitycraft.items.ItemModifiedBucket;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.items.ItemReinforcedDoor;
import org.freeforums.geforce.securitycraft.items.ItemRemoteAccess;
import org.freeforums.geforce.securitycraft.items.ItemSCManual;
import org.freeforums.geforce.securitycraft.items.ItemTaser;
import org.freeforums.geforce.securitycraft.items.ItemTestItem;
import org.freeforums.geforce.securitycraft.items.ItemUniversalBlockModifier;
import org.freeforums.geforce.securitycraft.items.ItemUniversalBlockRemover;
import org.freeforums.geforce.securitycraft.items.ItemWithInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.misc.SCManualPage;
import org.freeforums.geforce.securitycraft.network.packets.PacketCPlaySoundAtPos;
import org.freeforums.geforce.securitycraft.network.packets.PacketCRemoveLGView;
import org.freeforums.geforce.securitycraft.network.packets.PacketCSetCameraLocation;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateCooldown;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateNBTTag;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateOwner;
import org.freeforums.geforce.securitycraft.network.packets.PacketCheckKeypadCode;
import org.freeforums.geforce.securitycraft.network.packets.PacketCheckRetinalScanner;
import org.freeforums.geforce.securitycraft.network.packets.PacketGivePotionEffect;
import org.freeforums.geforce.securitycraft.network.packets.PacketSAddModules;
import org.freeforums.geforce.securitycraft.network.packets.PacketSSetOwner;
import org.freeforums.geforce.securitycraft.network.packets.PacketSUpdateNBTTag;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlock;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlockAndMetadata;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlockMetadata;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetExplosiveState;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetISType;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetKeycardLevel;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetKeypadCode;
import org.freeforums.geforce.securitycraft.network.packets.PacketUpdateLogger;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityAlarm;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityClaymore;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityEmpedWire;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFrame;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLaserBlock;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLogger;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMonitor;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRAM;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityReinforcedDoor;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRetinalScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ConfigurationHandler{
	private int[] harmingPotions = {8268, 8236, 16460, 16428};
	private int[] healingPotions = {8261, 8229, 16453, 16421};
	
	public boolean allowCodebreakerItem;
	public boolean allowDoorRemover;
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
	public boolean sayThanksMessage;
	public boolean isIrcBotEnabled;
	public boolean disconnectOnWorldClose;

	public static int fakePanelID = 1;
	public static int portableRadarSearchRadius;
	public static int usernameLoggerSearchRadius;	
    public static int laserBlockRange;
	public static int alarmTickDelay;
	public static double alarmSoundVolume;
	public static int cageTrapTextureIndex;
	public static int empRadius;
	public static int portableRadarDelay;
	public static int claymoreRange;
	public boolean useOldKeypadRecipe;

	public String currentHackIndex = "";
	public boolean ableToContinueHacking = true;
	public boolean hackingFailed = false;
		
	// RetinalScanner = new BlockRetinalScanner(1018, Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundMetalFootstep).setCreativeTab(mod_SecurityCraft.tabSecurityCraft).setBlockName("retinalScanner");

	public void setupAdditions(){
		this.setupTechnicalBlocks();
		this.setupMines();
		this.setupItems();
	}
	
	public void setupDebugAdditions() {
		this.setupDebuggingBlocks();
		this.setupDebuggingItems();
		
		this.registerDebuggingAdditions();
	}
	
	public void setupTechnicalBlocks(){
		mod_SecurityCraft.LaserBlock = new BlockLaserBlock(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("laserBlock").setBlockTextureName("securitycraft:dispenser_front_vertical");
		
		mod_SecurityCraft.Laser = new BlockLaser(Material.rock).setBlockUnbreakable().setResistance(1000F).setBlockName("laser");
		
		mod_SecurityCraft.Keypad = new BlockKeypad(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keypad");
				
		mod_SecurityCraft.LaserActive = new BlockActiveLaser(Material.iron).setBlockUnbreakable().setResistance(1000F).setBlockName("laserActive");
		
		mod_SecurityCraft.retinalScanner = new BlockRetinalScanner(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("retinalScanner");
	    
		mod_SecurityCraft.doorIndestructableIron = new BlockReinforcedDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setBlockName("ironDoorReinforced");
		
		mod_SecurityCraft.bogusLava = new BlockBogusLavaBase(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setBlockName("bogusLava").setBlockTextureName("lava_still");
		mod_SecurityCraft.bogusLavaFlowing = new BlockBogusLava(Material.lava).setHardness(0.0F).setLightLevel(1.0F).setBlockName("bogusLavaFlowing").setBlockTextureName("lava_flow");
		mod_SecurityCraft.bogusWater = new BlockBogusWaterBase(Material.water).setHardness(100.0F).setBlockName("bogusWater").setBlockTextureName("water_still");
		mod_SecurityCraft.bogusWaterFlowing = new BlockBogusWater(Material.water).setHardness(0.0F).setBlockName("bogusWaterFlowing").setBlockTextureName("water_flow");
		
		mod_SecurityCraft.keycardReader = new BlockKeycardReader(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keycardReader").setBlockTextureName("securitycraft:keycardReader");
	    
		mod_SecurityCraft.ironTrapdoor = new BlockIronTrapDoor(Material.iron).setHardness(5.0F).setResistance(200F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("ironTrapdoor").setBlockTextureName("securitycraft:ironTrapdoor");

		mod_SecurityCraft.inventoryScanner = new BlockInventoryScanner(Material.rock).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeStone).setBlockName("inventoryScanner");

		mod_SecurityCraft.inventoryScannerField = new BlockInventoryScannerField(Material.glass).setBlockUnbreakable().setResistance(1000F).setBlockName("inventoryScannerField");
				
	    mod_SecurityCraft.cageTrap = new BlockCageTrap(Material.rock, false, cageTrapTextureIndex).setHardness(5F).setResistance(100F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("cageTrap");
		
	    mod_SecurityCraft.deactivatedCageTrap = new BlockCageTrap(Material.rock, true, cageTrapTextureIndex).setBlockName("deactivatedCageTrap");

	    mod_SecurityCraft.portableRadar = new BlockPortableRadar(Material.circuits).setHardness(1F).setResistance(50F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("portableRadar");
	    
	    mod_SecurityCraft.unbreakableIronBars = new BlockUnbreakableBars("securitycraft:reinforcedIronBars", "securitycraft:reinforcedIronBars", Material.iron, true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockUnbreakable().setResistance(1000F).setBlockName("reinforcedIronBars");
	    
		mod_SecurityCraft.keypadChest = new BlockKeypadChest(0).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keypadChest");
	
	    mod_SecurityCraft.usernameLogger = new BlockLogger(Material.rock).setHardness(8F).setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("usernameLogger").setBlockTextureName("securitycraft:usernameLogger");
	
		mod_SecurityCraft.reinforcedGlass = new BlockReinforcedGlass("securitycraft:reinforcedGlass", "glass_pane_top", Material.iron, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("reinforcedGlass");
	
		mod_SecurityCraft.alarm = new BlockAlarm(Material.iron, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("alarm");
	
		mod_SecurityCraft.alarmLit = new BlockAlarm(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setBlockName("alarmLit");
	
		mod_SecurityCraft.reinforcedStone = new BlockOwnable(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("reinforcedStone").setBlockTextureName("securitycraft:reinforcedStone");
	
		mod_SecurityCraft.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("reinforcedFenceGate");
		
		mod_SecurityCraft.reinforcedWoodPlanks = new BlockReinforcedWood(Material.wood).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("reinforcedPlanks").setBlockTextureName("securitycraft:reinforcedPlanks");
	
		mod_SecurityCraft.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("panicButton");
	
		mod_SecurityCraft.keypadFrame = new BlockKeypadFrame(Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keypadFrame");
	
		mod_SecurityCraft.keypadFurnace = new BlockKeypadFurnace(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeMetal).setBlockName("keypadFurnace").setBlockTextureName("securitycraft:keypadUnactive");
	
	    mod_SecurityCraft.securityCamera = new BlockSecurityCamera(Material.iron, false).setHardness(1.0F).setResistance(10.F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("securityCamera").setBlockTextureName("securitycraft:cameraParticleTexture");
	    mod_SecurityCraft.securityCameraLit = new BlockSecurityCamera(Material.iron, true).setHardness(1.0F).setResistance(10.F).setBlockName("securityCameraLit").setBlockTextureName("securitycraft:cameraParticleTexture");
	
	    mod_SecurityCraft.monitor = new BlockMonitor(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("monitor");
	}
	
	public void setupMines(){
		mod_SecurityCraft.Mine = (BlockMine) new BlockMine(Material.circuits, false).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("mine");
		
		mod_SecurityCraft.MineCut = (BlockMine) new BlockMine(Material.circuits, true).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setBlockName("mineCut");
		
		mod_SecurityCraft.DirtMine = new BlockFullMineBase(Material.ground, "dirt").setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setBlockName("dirtMine").setBlockTextureName("dirt");
		
		mod_SecurityCraft.StoneMine = new BlockFullMineBase(Material.rock, "stone").setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.5F).setStepSound(Block.soundTypeStone).setBlockName("stoneMine").setBlockTextureName("stone");
		
		mod_SecurityCraft.CobblestoneMine = new BlockFullMineBase(Material.rock, "cobblestone").setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.75F).setStepSound(Block.soundTypeStone).setBlockName("cobblestoneMine").setBlockTextureName("cobblestone");
		
		mod_SecurityCraft.SandMine = new BlockFullMineBase(Material.sand, "sand").setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeSand).setBlockName("sandMine").setBlockTextureName("sand");
		
		mod_SecurityCraft.DiamondOreMine = new BlockFullMineBase(Material.rock, "diamond ore").setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setBlockName("diamondMine").setBlockTextureName("diamond_ore");
		
		mod_SecurityCraft.FurnaceMine = new BlockFurnaceMine(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setBlockName("furnaceMine");
				
	    mod_SecurityCraft.trackMine = new BlockTrackMine().setHardness(!ableToBreakMines ? -1F : 0.7F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("trackMine").setBlockTextureName("securitycraft:rail_mine");

		mod_SecurityCraft.bouncingBetty = new BlockBouncingBetty(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("bouncingBetty");
	
		mod_SecurityCraft.claymoreActive = new BlockClaymore(Material.circuits, true).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("claymoreActive").setBlockTextureName("securitycraft:claymore");
		
		mod_SecurityCraft.claymoreDefused = new BlockClaymore(Material.circuits, false).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setBlockName("claymoreDefused").setBlockTextureName("securitycraft:claymore");
	}
	
	public void setupItems(){
		mod_SecurityCraft.Codebreaker = new ItemCodebreaker().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("codebreaker").setTextureName("securitycraft:CodeBreaker1");
	    
		mod_SecurityCraft.keycards = new ItemKeycardBase();
	  
		mod_SecurityCraft.doorIndestructableIronItem = new ItemReinforcedDoor(Material.iron).setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(mod_SecurityCraft.tabSCTechnical).setTextureName("securitycraft:doorReinforcedIron");
	    
		mod_SecurityCraft.universalBlockRemover = new ItemUniversalBlockRemover().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockRemover").setTextureName("securitycraft:universalBlockRemover");
		
		mod_SecurityCraft.remoteAccessMine = new ItemRemoteAccess(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("remoteAccessMine").setTextureName("securitycraft:remoteAccessDoor").setMaxStackSize(1);
		
		mod_SecurityCraft.fWaterBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusWaterFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFWater").setTextureName("securitycraft:bucketFWater");
		
		mod_SecurityCraft.fLavaBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusLavaFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFLava").setTextureName("securitycraft:bucketFLava");

		mod_SecurityCraft.universalBlockModifier = new ItemUniversalBlockModifier().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockModifier").setTextureName("securitycraft:universalBlockModifier");
	
		mod_SecurityCraft.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setUnlocalizedName("redstoneModule").setTextureName("securitycraft:redstoneModule");
		
		mod_SecurityCraft.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setUnlocalizedName("whitelistModule").setTextureName("securitycraft:whitelistModule");
		
		mod_SecurityCraft.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setUnlocalizedName("blacklistModule").setTextureName("securitycraft:blacklistModule");
				
		mod_SecurityCraft.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setUnlocalizedName("harmingModule").setTextureName("securitycraft:harmingModule");
		
		mod_SecurityCraft.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setUnlocalizedName("smartModule").setTextureName("securitycraft:smartModule");
	
		mod_SecurityCraft.wireCutters = new ItemWithInfo(null, new String[]{"The wire cutters requires: 1 shears, 4 iron ingots", "XY ", "Y Y", " Y ", "X = shears, Y = iron ingot"}).setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("wireCutters").setTextureName("securitycraft:wireCutter");
	
		mod_SecurityCraft.keyPanel = new ItemWithInfo(null, new String[]{"The key panel requires: 1 weighted pressure plate (heavy), 8 stone button", "YYY", "YXY", "YYY", "X = weighted pressure plate (heavy), Y = stone button"}).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadItem").setTextureName("securitycraft:keypadItem");
		
		mod_SecurityCraft.adminTool = new ItemAdminTool("", new String[]{"", ""}).setMaxStackSize(1).setUnlocalizedName("adminTool");
	
		mod_SecurityCraft.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("cameraMonitor").setTextureName("securitycraft:cameraMonitor");
	
		mod_SecurityCraft.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("scManual").setTextureName("securitycraft:scManual");
	
		mod_SecurityCraft.taser = new ItemTaser().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("taser");
	}
	
	public void setupDebuggingBlocks(){
		
	}
	
	public void setupDebuggingItems(){
		mod_SecurityCraft.testItem = new ItemTestItem().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("Test");
	    //mod_SecurityCraft.testChestplate = new ItemModifiedArmor(3213, armorBase, 0, 1).setUnlocalizedName("testChestplate").setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
		
	public void setupConfiguration() {
		mod_SecurityCraft.configFile.load();

        allowCodebreakerItem = mod_SecurityCraft.configFile.get("options", "Is codebreaker allowed?", true).getBoolean(true);
        allowDoorRemover = mod_SecurityCraft.configFile.get("options", "Is door remover allowed?", true).getBoolean(true);
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
        
        portableRadarSearchRadius = mod_SecurityCraft.configFile.get("options", "Portable radar search radius:", 25).getInt(25);
        usernameLoggerSearchRadius = mod_SecurityCraft.configFile.get("options", "Username logger search radius:", 3).getInt(3);
        laserBlockRange = mod_SecurityCraft.configFile.get("options", "Laser range:", 5).getInt(5);
        alarmTickDelay = mod_SecurityCraft.configFile.get("options", "Delay between alarm sounds (seconds):", 2).getInt(2);
        alarmSoundVolume = mod_SecurityCraft.configFile.get("options", "Alarm sound volume:", 0.8D).getDouble(0.8D);
        portableRadarDelay = (mod_SecurityCraft.configFile.get("options", "Portable radar delay (seconds):", 4).getInt(4) * 20);
        claymoreRange = mod_SecurityCraft.configFile.get("options", "Claymore range:", 5).getInt(5);
        sayThanksMessage = mod_SecurityCraft.configFile.get("options", "Display a 'tip' message at spawn?", true).getBoolean(true);
        mod_SecurityCraft.debuggingMode = mod_SecurityCraft.configFile.get("options", "Is debug mode? (not recommended!)", false).getBoolean(false);
        isIrcBotEnabled = mod_SecurityCraft.configFile.get("options", "Disconnect IRC bot on world exited?", true).getBoolean(true);
        disconnectOnWorldClose = mod_SecurityCraft.configFile.get("options", "Is IRC bot enabled?", true).getBoolean(true);
        useOldKeypadRecipe = mod_SecurityCraft.configFile.get("options", "Use old keypad recipe (9 buttons)?", false).getBoolean(false);

        //cageTrapTextureIndex = config.get("Options", "cage-trap-block-texture (enter a block's id to use it's texture on the cage trap. Using the id '9999' will use the default cage trap texture)", 9999).getInt(9999);
        //empRadius = config.get("Options", "emp-radius(factors-of-ten-only)", 51).getInt(51);

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
		GameRegistry.registerBlock(mod_SecurityCraft.deactivatedCageTrap, mod_SecurityCraft.deactivatedCageTrap.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.unbreakableIronBars);
		registerBlock(mod_SecurityCraft.keypadChest);
		registerBlock(mod_SecurityCraft.usernameLogger);
		registerBlock(mod_SecurityCraft.reinforcedGlass);
		registerBlock(mod_SecurityCraft.alarm);
		GameRegistry.registerBlock(mod_SecurityCraft.alarmLit, mod_SecurityCraft.alarmLit.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStone);
		registerBlock(mod_SecurityCraft.reinforcedFencegate);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedWoodPlanks, ItemBlockReinforcedPlanks.class, mod_SecurityCraft.reinforcedWoodPlanks.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.panicButton);
		registerBlock(mod_SecurityCraft.keypadFrame);
		registerBlock(mod_SecurityCraft.claymoreActive);
		GameRegistry.registerBlock(mod_SecurityCraft.claymoreDefused, mod_SecurityCraft.claymoreDefused.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.keypadFurnace);
		registerBlock(mod_SecurityCraft.securityCamera);
		GameRegistry.registerBlock(mod_SecurityCraft.securityCameraLit, mod_SecurityCraft.securityCameraLit.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.monitor);

		registerItem(mod_SecurityCraft.Codebreaker);
	    registerItem(mod_SecurityCraft.doorIndestructableIronItem);
		registerItem(mod_SecurityCraft.universalBlockRemover);
		registerItem(mod_SecurityCraft.keycards);
		registerItem(mod_SecurityCraft.remoteAccessMine);
		registerItem(mod_SecurityCraft.fWaterBucket);
		registerItem(mod_SecurityCraft.fLavaBucket);
		registerItem(mod_SecurityCraft.universalBlockModifier);
		GameRegistry.registerItem(mod_SecurityCraft.redstoneModule, mod_SecurityCraft.redstoneModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.whitelistModule, mod_SecurityCraft.whitelistModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.blacklistModule, mod_SecurityCraft.blacklistModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.harmingModule, mod_SecurityCraft.harmingModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.smartModule, mod_SecurityCraft.smartModule.getUnlocalizedName().substring(5));
		registerItem(mod_SecurityCraft.wireCutters);
		registerItem(mod_SecurityCraft.adminTool);
		registerItem(mod_SecurityCraft.keyPanel);
		registerItem(mod_SecurityCraft.cameraMonitor);
		registerItem(mod_SecurityCraft.taser);
		registerItem(mod_SecurityCraft.scManual);

		GameRegistry.registerTileEntity(TileEntityOwnable.class, "abstractOwnable");
		GameRegistry.registerTileEntity(TileEntitySCTE.class, "abstractSC");
		GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, "laserBlock");
		GameRegistry.registerTileEntity(TileEntityReinforcedDoor.class, "reinforcedDoor");
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, "keycardReader");
		GameRegistry.registerTileEntity(TileEntityRAM.class, "remoteAccessDoor");
		GameRegistry.registerTileEntity(TileEntityMineLoc.class, "mineLoc");
		GameRegistry.registerTileEntity(TileEntityInventoryScanner.class, "inventoryScanner");
		GameRegistry.registerTileEntity(TileEntityPortableRadar.class, "portableRadar");
		GameRegistry.registerTileEntity(TileEntityEmpedWire.class, "empedWire");
		GameRegistry.registerTileEntity(TileEntitySecurityCamera.class, "securityCamera");
		GameRegistry.registerTileEntity(TileEntityLogger.class, "usernameLogger");
		GameRegistry.registerTileEntity(TileEntityRetinalScanner.class, "retinalScanner");
		GameRegistry.registerTileEntity(TileEntityKeypadChest.class, "keypadChest");
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "alarm");
		GameRegistry.registerTileEntity(TileEntityKeypadFrame.class, "keypadFrame");
		GameRegistry.registerTileEntity(TileEntityClaymore.class, "claymore");
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, "keypadFurnace");
		GameRegistry.registerTileEntity(TileEntityMonitor.class, "monitor");
		GameRegistry.registerTileEntity(CustomizableSCTE.class, "customizableSCTE");

		if(useOldKeypadRecipe){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.Keypad, 1), new Object[]{
				"III", "III", "III", 'I', Blocks.stone_button
			});
		}else{
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keyPanel, 1), new Object[]{
				"III", "IBI", "III", 'I', Blocks.stone_button, 'B', Blocks.heavy_weighted_pressure_plate
			});
			
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keypadFrame, 1), new Object[]{
				"III", "IBI", "I I", 'I', Blocks.stone, 'B', Items.redstone
			});
			
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.Keypad, 1), new Object[]{
				"III", "IBI", "IPI", 'I', Blocks.iron_block, 'B', mod_SecurityCraft.keypadFrame, 'P', mod_SecurityCraft.keyPanel
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
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycards, 1, 0), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.gold_ingot 
			});
		}
		
		if(ableToCraftKeycard2){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycards, 1, 1), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.brick
			});
		}
		
		if(ableToCraftKeycard3){ 
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycards, 1, 2), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.netherbrick
			});
		}
		
		if(ableToCraftKeycard4){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycards, 1, 4), new Object[]{
				"III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 13)
			});
		}
		
		if(ableToCraftKeycard5){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycards, 1, 5), new Object[]{
				"III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 5)
			});
		}
		
		if(ableToCraftLUKeycard){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycards, 1, 3), new Object[]{
				"III", "LLL", 'I', Items.iron_ingot, 'L', new ItemStack(Items.dye, 1, 4)
			});
		}
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.trackMine, 4), new Object[]{
			"X X", "X#X", "XGX", 'X', Items.iron_ingot, '#', Items.stick, 'G', Items.gunpowder
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.unbreakableIronBars, 4), new Object[]{
			" I ", "IBI", " I ", 'I', Items.iron_ingot, 'B', Blocks.iron_bars
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
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedGlass, 4), new Object[]{
			" G ", "GPG", " G ", 'G', Blocks.glass, 'P', Blocks.glass_pane
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.alarm, 1), new Object[]{
			"GGG", "GNG", "GRG", 'G', Blocks.glass, 'R', Items.redstone, 'N', Blocks.noteblock
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStone, 5), new Object[]{
			" C ", "CSC", " C ", 'C', Blocks.cobblestone, 'S', Blocks.stone
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedFencegate, 1), new Object[]{
			" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.fence_gate
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 5, 0), new Object[]{
			" I ", "IWI", " I ", 'I', Items.iron_ingot, 'W', new ItemStack(Blocks.planks, 1, 0)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 5, 1), new Object[]{
			" I ", "IWI", " I ", 'I', Items.iron_ingot, 'W', new ItemStack(Blocks.planks, 1, 1)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 5, 2), new Object[]{
			" I ", "IWI", " I ", 'I', Items.iron_ingot, 'W', new ItemStack(Blocks.planks, 1, 2)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 5, 3), new Object[]{
			" I ", "IWI", " I ", 'I', Items.iron_ingot, 'W', new ItemStack(Blocks.planks, 1, 3)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 5, 4), new Object[]{
			" I ", "IWI", " I ", 'I', Items.iron_ingot, 'W', new ItemStack(Blocks.planks, 1, 4)
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks, 5, 5), new Object[]{
			" I ", "IWI", " I ", 'I', Items.iron_ingot, 'W', new ItemStack(Blocks.planks, 1, 5)
		});
		
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
			"F", "R", "K", 'K', mod_SecurityCraft.keyPanel, 'R', Items.redstone, 'F', Blocks.furnace
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.claymoreActive, 1), new Object[]{
			"HSH", "SBS", "RGR", 'H', Blocks.tripwire_hook, 'S', Items.string, 'B', mod_SecurityCraft.bouncingBetty, 'R', Items.redstone, 'G', Items.gunpowder
		});
		
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.DirtMine, 1), new Object[] {Blocks.dirt, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.StoneMine, 1), new Object[] {Blocks.stone, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.CobblestoneMine, 1), new Object[] {Blocks.cobblestone, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.DiamondOreMine, 1), new Object[] {Blocks.diamond_ore, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.SandMine, 1), new Object[] {Blocks.sand, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.FurnaceMine, 1), new Object[] {Blocks.furnace, mod_SecurityCraft.Mine});
	}
	
	/**
	 * Registers the given block with GameRegistry.registerBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 */
	private void registerBlock(Block block){
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), StatCollector.translateToLocal(block.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + block.getUnlocalizedName().substring(5) + ".info")));
	}
	
	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item){
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, StatCollector.translateToLocal(item.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + item.getUnlocalizedName().substring(5) + ".info")));
	}
	
	public void registerDebuggingAdditions(){		
		GameRegistry.registerItem(mod_SecurityCraft.testItem, mod_SecurityCraft.testItem.getUnlocalizedName().substring(5));
	}
	
	public void setupOtherRegistrys(){}

	public void setupEntityRegistry() {
		EntityRegistry.registerModEntity(EntityTnTCompact.class, "TnTCompact", 0, mod_SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTaserBullet.class, "TazerBullet", 2, mod_SecurityCraft.instance, 256, 1, true);
	}

	public void setupHandlers(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(mod_SecurityCraft.eventHandler);
	} 

	public void setupPackets(SimpleNetworkWrapper network) {
		network.registerMessage(PacketCheckKeypadCode.Handler.class, PacketCheckKeypadCode.class, 0, Side.SERVER);
		network.registerMessage(PacketCheckRetinalScanner.Handler.class, PacketCheckRetinalScanner.class, 1, Side.SERVER);
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 2, Side.SERVER);
		network.registerMessage(PacketSetBlockMetadata.Handler.class, PacketSetBlockMetadata.class, 3, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 4, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 5, Side.SERVER);
		network.registerMessage(PacketSetKeypadCode.Handler.class, PacketSetKeypadCode.class, 6, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 7, Side.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 8, Side.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 9, Side.SERVER);
		network.registerMessage(PacketCUpdateCooldown.Handler.class, PacketCUpdateCooldown.class, 10, Side.CLIENT);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 11, Side.CLIENT);
		network.registerMessage(PacketCUpdateOwner.Handler.class, PacketCUpdateOwner.class, 12, Side.CLIENT);
		network.registerMessage(PacketSetExplosiveState.Handler.class, PacketSetExplosiveState.class, 13, Side.SERVER);
		network.registerMessage(PacketGivePotionEffect.Handler.class, PacketGivePotionEffect.class, 14, Side.SERVER);
		network.registerMessage(PacketSetBlockAndMetadata.Handler.class, PacketSetBlockAndMetadata.class, 15, Side.SERVER);
		network.registerMessage(PacketSSetOwner.Handler.class, PacketSSetOwner.class, 16, Side.SERVER);
		network.registerMessage(PacketSAddModules.Handler.class, PacketSAddModules.class, 17, Side.SERVER);
		network.registerMessage(PacketCSetCameraLocation.Handler.class, PacketCSetCameraLocation.class, 18, Side.CLIENT);
		network.registerMessage(PacketCRemoveLGView.Handler.class, PacketCRemoveLGView.class, 19, Side.CLIENT);
	}

}
