package net.breakinbad.securitycraft.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.blocks.BlockAlarm;
import net.breakinbad.securitycraft.blocks.BlockCageTrap;
import net.breakinbad.securitycraft.blocks.BlockFakeLava;
import net.breakinbad.securitycraft.blocks.BlockFakeLavaBase;
import net.breakinbad.securitycraft.blocks.BlockFakeWater;
import net.breakinbad.securitycraft.blocks.BlockFakeWaterBase;
import net.breakinbad.securitycraft.blocks.BlockFrame;
import net.breakinbad.securitycraft.blocks.BlockInventoryScanner;
import net.breakinbad.securitycraft.blocks.BlockInventoryScannerField;
import net.breakinbad.securitycraft.blocks.BlockIronFence;
import net.breakinbad.securitycraft.blocks.BlockIronTrapDoor;
import net.breakinbad.securitycraft.blocks.BlockKeycardReader;
import net.breakinbad.securitycraft.blocks.BlockKeypad;
import net.breakinbad.securitycraft.blocks.BlockKeypadChest;
import net.breakinbad.securitycraft.blocks.BlockKeypadFurnace;
import net.breakinbad.securitycraft.blocks.BlockLaserBlock;
import net.breakinbad.securitycraft.blocks.BlockLaserField;
import net.breakinbad.securitycraft.blocks.BlockLogger;
import net.breakinbad.securitycraft.blocks.BlockOwnable;
import net.breakinbad.securitycraft.blocks.BlockPanicButton;
import net.breakinbad.securitycraft.blocks.BlockPortableRadar;
import net.breakinbad.securitycraft.blocks.BlockReinforcedDoor;
import net.breakinbad.securitycraft.blocks.BlockReinforcedFenceGate;
import net.breakinbad.securitycraft.blocks.BlockReinforcedGlass;
import net.breakinbad.securitycraft.blocks.BlockReinforcedGlassPane;
import net.breakinbad.securitycraft.blocks.BlockReinforcedIronBars;
import net.breakinbad.securitycraft.blocks.BlockReinforcedStainedGlass;
import net.breakinbad.securitycraft.blocks.BlockReinforcedStainedGlassPanes;
import net.breakinbad.securitycraft.blocks.BlockReinforcedStairs;
import net.breakinbad.securitycraft.blocks.BlockReinforcedWood;
import net.breakinbad.securitycraft.blocks.BlockRetinalScanner;
import net.breakinbad.securitycraft.blocks.BlockSecurityCamera;
import net.breakinbad.securitycraft.blocks.mines.BlockBouncingBetty;
import net.breakinbad.securitycraft.blocks.mines.BlockClaymore;
import net.breakinbad.securitycraft.blocks.mines.BlockFullMineBase;
import net.breakinbad.securitycraft.blocks.mines.BlockFurnaceMine;
import net.breakinbad.securitycraft.blocks.mines.BlockIMS;
import net.breakinbad.securitycraft.blocks.mines.BlockMine;
import net.breakinbad.securitycraft.blocks.mines.BlockTrackMine;
import net.breakinbad.securitycraft.entity.EntityIMSBomb;
import net.breakinbad.securitycraft.entity.EntityTaserBullet;
import net.breakinbad.securitycraft.entity.EntityTnTCompact;
import net.breakinbad.securitycraft.items.ItemAdminTool;
import net.breakinbad.securitycraft.items.ItemBlockReinforcedPlanks;
import net.breakinbad.securitycraft.items.ItemBlockReinforcedStainedGlass;
import net.breakinbad.securitycraft.items.ItemBlockReinforcedStainedGlassPanes;
import net.breakinbad.securitycraft.items.ItemCameraMonitor;
import net.breakinbad.securitycraft.items.ItemCodebreaker;
import net.breakinbad.securitycraft.items.ItemKeyPanel;
import net.breakinbad.securitycraft.items.ItemKeycardBase;
import net.breakinbad.securitycraft.items.ItemMineRemoteAccessTool;
import net.breakinbad.securitycraft.items.ItemModifiedBucket;
import net.breakinbad.securitycraft.items.ItemModule;
import net.breakinbad.securitycraft.items.ItemReinforcedDoor;
import net.breakinbad.securitycraft.items.ItemSCManual;
import net.breakinbad.securitycraft.items.ItemTaser;
import net.breakinbad.securitycraft.items.ItemTestItem;
import net.breakinbad.securitycraft.items.ItemUniversalBlockModifier;
import net.breakinbad.securitycraft.items.ItemUniversalBlockRemover;
import net.breakinbad.securitycraft.items.ItemUniversalOwnerChanger;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.EnumCustomModules;
import net.breakinbad.securitycraft.misc.SCManualPage;
import net.breakinbad.securitycraft.network.packets.PacketCCreateLGView;
import net.breakinbad.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.breakinbad.securitycraft.network.packets.PacketCRemoveLGView;
import net.breakinbad.securitycraft.network.packets.PacketCSetCameraLocation;
import net.breakinbad.securitycraft.network.packets.PacketCUpdateCooldown;
import net.breakinbad.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.breakinbad.securitycraft.network.packets.PacketCheckRetinalScanner;
import net.breakinbad.securitycraft.network.packets.PacketGivePotionEffect;
import net.breakinbad.securitycraft.network.packets.PacketSAddModules;
import net.breakinbad.securitycraft.network.packets.PacketSCheckPassword;
import net.breakinbad.securitycraft.network.packets.PacketSSetOwner;
import net.breakinbad.securitycraft.network.packets.PacketSSetPassword;
import net.breakinbad.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.breakinbad.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.breakinbad.securitycraft.network.packets.PacketSetBlock;
import net.breakinbad.securitycraft.network.packets.PacketSetBlockAndMetadata;
import net.breakinbad.securitycraft.network.packets.PacketSetBlockMetadata;
import net.breakinbad.securitycraft.network.packets.PacketSetExplosiveState;
import net.breakinbad.securitycraft.network.packets.PacketSetISType;
import net.breakinbad.securitycraft.network.packets.PacketSetKeycardLevel;
import net.breakinbad.securitycraft.network.packets.PacketUpdateLogger;
import net.breakinbad.securitycraft.tileentity.TileEntityAlarm;
import net.breakinbad.securitycraft.tileentity.TileEntityClaymore;
import net.breakinbad.securitycraft.tileentity.TileEntityEmpedWire;
import net.breakinbad.securitycraft.tileentity.TileEntityFrame;
import net.breakinbad.securitycraft.tileentity.TileEntityIMS;
import net.breakinbad.securitycraft.tileentity.TileEntityInventoryScanner;
import net.breakinbad.securitycraft.tileentity.TileEntityKeycardReader;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypad;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadChest;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.breakinbad.securitycraft.tileentity.TileEntityLaserBlock;
import net.breakinbad.securitycraft.tileentity.TileEntityLogger;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.breakinbad.securitycraft.tileentity.TileEntityPortableRadar;
import net.breakinbad.securitycraft.tileentity.TileEntityRAM;
import net.breakinbad.securitycraft.tileentity.TileEntityReinforcedDoor;
import net.breakinbad.securitycraft.tileentity.TileEntityRetinalScanner;
import net.breakinbad.securitycraft.tileentity.TileEntitySCTE;
import net.breakinbad.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ConfigurationHandler{
	private int[]  harmingPotions = {8268, 8236, 16460, 16428};
	private int[]  healingPotions = {8261, 8229, 16453, 16421};
	
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
	public static int imsRange;
	public boolean useOldKeypadRecipe;

	public String currentHackIndex = "";
	public boolean ableToContinueHacking = true;
	public boolean hackingFailed = false;
		
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
		
		mod_SecurityCraft.Laser = new BlockLaserField(Material.rock).setBlockUnbreakable().setResistance(1000F).setBlockName("laser");
		
		mod_SecurityCraft.Keypad = new BlockKeypad(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keypad");
						
		mod_SecurityCraft.retinalScanner = new BlockRetinalScanner(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("retinalScanner");
	    
		mod_SecurityCraft.doorIndestructableIron = new BlockReinforcedDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setBlockName("ironDoorReinforced");
		
		mod_SecurityCraft.bogusLava = new BlockFakeLavaBase(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setBlockName("bogusLava").setBlockTextureName("lava_still");
		mod_SecurityCraft.bogusLavaFlowing = new BlockFakeLava(Material.lava).setHardness(0.0F).setLightLevel(1.0F).setBlockName("bogusLavaFlowing").setBlockTextureName("lava_flow");
		mod_SecurityCraft.bogusWater = new BlockFakeWaterBase(Material.water).setHardness(100.0F).setBlockName("bogusWater").setBlockTextureName("water_still");
		mod_SecurityCraft.bogusWaterFlowing = new BlockFakeWater(Material.water).setHardness(0.0F).setBlockName("bogusWaterFlowing").setBlockTextureName("water_flow");
		
		mod_SecurityCraft.keycardReader = new BlockKeycardReader(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keycardReader").setBlockTextureName("securitycraft:keycardReader");
	    
		mod_SecurityCraft.ironTrapdoor = new BlockIronTrapDoor(Material.iron).setHardness(5.0F).setResistance(200F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("ironTrapdoor").setBlockTextureName("securitycraft:ironTrapdoor");

		mod_SecurityCraft.inventoryScanner = new BlockInventoryScanner(Material.rock).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeStone).setBlockName("inventoryScanner");

		mod_SecurityCraft.inventoryScannerField = new BlockInventoryScannerField(Material.glass).setBlockUnbreakable().setResistance(1000F).setBlockName("inventoryScannerField");
				
	    mod_SecurityCraft.cageTrap = new BlockCageTrap(Material.rock, false, cageTrapTextureIndex).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("cageTrap").setBlockTextureName("securitycraft:reinforcedIronBars");
		
	    mod_SecurityCraft.deactivatedCageTrap = new BlockCageTrap(Material.rock, true, cageTrapTextureIndex).setBlockUnbreakable().setResistance(1000F).setBlockName("deactivatedCageTrap").setBlockTextureName("iron_bars");

	    mod_SecurityCraft.portableRadar = new BlockPortableRadar(Material.circuits).setHardness(1F).setResistance(50F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("portableRadar");
	    
	    mod_SecurityCraft.unbreakableIronBars = new BlockReinforcedIronBars("securitycraft:reinforcedIronBars", "securitycraft:reinforcedIronBars", Material.iron, true).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setBlockName("reinforcedIronBars");
	    
		mod_SecurityCraft.keypadChest = new BlockKeypadChest(0).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keypadChest");
	
	    mod_SecurityCraft.usernameLogger = new BlockLogger(Material.rock).setHardness(8F).setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("usernameLogger").setBlockTextureName("securitycraft:usernameLogger");
	
		mod_SecurityCraft.reinforcedGlassPane = new BlockReinforcedGlassPane("securitycraft:glass_reinforced", "glass_pane_top", Material.iron, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedGlass");
	
		mod_SecurityCraft.alarm = new BlockAlarm(Material.iron, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("alarm").setBlockTextureName("securitycraft:alarmParticleTexture");
	
		mod_SecurityCraft.alarmLit = new BlockAlarm(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setBlockName("alarmLit");
	
		mod_SecurityCraft.reinforcedStone = new BlockOwnable(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStone").setBlockTextureName("securitycraft:reinforcedStone");
	
		mod_SecurityCraft.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedFenceGate");
		
		mod_SecurityCraft.reinforcedWoodPlanks = new BlockReinforcedWood(Material.wood).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedPlanks").setBlockTextureName("securitycraft:reinforcedPlanks");
	
		mod_SecurityCraft.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("panicButton");
	
		mod_SecurityCraft.frame = new BlockFrame(Material.rock).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("keypadFrame").setBlockTextureName("iron_block");
	
		mod_SecurityCraft.keypadFurnace = new BlockKeypadFurnace(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeMetal).setBlockName("keypadFurnace").setBlockTextureName("securitycraft:keypadUnactive");
	
	    mod_SecurityCraft.securityCamera = new BlockSecurityCamera(Material.iron, false).setHardness(1.0F).setResistance(10.F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockName("securityCamera").setBlockTextureName("securitycraft:cameraParticleTexture");
	
	    mod_SecurityCraft.reinforcedStairsOak = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsOak");
	    mod_SecurityCraft.reinforcedStairsSpruce = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsSpruce");
	    mod_SecurityCraft.reinforcedStairsBirch = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsBirch");
	    mod_SecurityCraft.reinforcedStairsJungle = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsJungle");
	    mod_SecurityCraft.reinforcedStairsAcacia = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsAcacia");
	    mod_SecurityCraft.reinforcedStairsDarkoak = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsDarkoak");
	    mod_SecurityCraft.reinforcedStairsStone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStairsStone");
	    
	    mod_SecurityCraft.ironFence = new BlockIronFence("securitycraft:reinforcedDoorLower", Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("scIronFence");
	
	    mod_SecurityCraft.reinforcedGlass = new BlockReinforcedGlass(Material.glass).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedGlassBlock");
	    mod_SecurityCraft.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.glass).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStainedGlass").setBlockTextureName("securitycraft:glass_reinforced");
	    mod_SecurityCraft.reinforcedStainedGlassPanes = new BlockReinforcedStainedGlassPanes().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockName("reinforcedStainedGlassPanes").setBlockTextureName("securitycraft:glass_reinforced");
	}
	
	public void setupMines(){
		mod_SecurityCraft.Mine = (BlockMine) new BlockMine(Material.circuits, false).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("mine");
		
		mod_SecurityCraft.MineCut = (BlockMine) new BlockMine(Material.circuits, true).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setBlockName("mineCut");
		
		mod_SecurityCraft.DirtMine = new BlockFullMineBase(Material.ground).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setBlockName("dirtMine").setBlockTextureName("dirt");
		
		mod_SecurityCraft.StoneMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.5F).setStepSound(Block.soundTypeStone).setBlockName("stoneMine").setBlockTextureName("stone");
		
		mod_SecurityCraft.CobblestoneMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.75F).setStepSound(Block.soundTypeStone).setBlockName("cobblestoneMine").setBlockTextureName("cobblestone");
		
		mod_SecurityCraft.SandMine = new BlockFullMineBase(Material.sand).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeSand).setBlockName("sandMine").setBlockTextureName("sand");
		
		mod_SecurityCraft.DiamondOreMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setBlockName("diamondMine").setBlockTextureName("diamond_ore");
		
		mod_SecurityCraft.FurnaceMine = new BlockFurnaceMine(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setBlockName("furnaceMine");
				
	    mod_SecurityCraft.trackMine = new BlockTrackMine().setHardness(!ableToBreakMines ? -1F : 0.7F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("trackMine").setBlockTextureName("securitycraft:rail_mine");

		mod_SecurityCraft.bouncingBetty = new BlockBouncingBetty(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("bouncingBetty");
	
		mod_SecurityCraft.claymoreActive = new BlockClaymore(Material.circuits, true).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("claymoreActive").setBlockTextureName("securitycraft:claymore");
		
		mod_SecurityCraft.claymoreDefused = new BlockClaymore(Material.circuits, false).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setBlockName("claymoreDefused").setBlockTextureName("securitycraft:claymore");
	
		mod_SecurityCraft.ims = new BlockIMS(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setBlockName("ims").setBlockTextureName("securitycraft:ims");
	}
	
	public void setupItems(){
		mod_SecurityCraft.Codebreaker = new ItemCodebreaker().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("codebreaker").setTextureName("securitycraft:CodeBreaker1");
	    
		mod_SecurityCraft.keycards = new ItemKeycardBase().setUnlocalizedName("keycards");
	  
		mod_SecurityCraft.doorIndestructableIronItem = new ItemReinforcedDoor(Material.iron).setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(mod_SecurityCraft.tabSCDecoration).setTextureName("securitycraft:doorReinforcedIron");
		
		mod_SecurityCraft.universalBlockRemover = new ItemUniversalBlockRemover().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockRemover").setTextureName("securitycraft:universalBlockRemover");
		
		mod_SecurityCraft.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("remoteAccessMine").setTextureName("securitycraft:remoteAccessDoor").setMaxStackSize(1);
		
		mod_SecurityCraft.fWaterBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusWaterFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFWater").setTextureName("securitycraft:bucketFWater");
		
		mod_SecurityCraft.fLavaBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusLavaFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFLava").setTextureName("securitycraft:bucketFLava");

		mod_SecurityCraft.universalBlockModifier = new ItemUniversalBlockModifier().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockModifier").setTextureName("securitycraft:universalBlockModifier");
	
		mod_SecurityCraft.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setUnlocalizedName("redstoneModule").setTextureName("securitycraft:redstoneModule");
		
		mod_SecurityCraft.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setUnlocalizedName("whitelistModule").setTextureName("securitycraft:whitelistModule");
		
		mod_SecurityCraft.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setUnlocalizedName("blacklistModule").setTextureName("securitycraft:blacklistModule");
				
		mod_SecurityCraft.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setUnlocalizedName("harmingModule").setTextureName("securitycraft:harmingModule");
		
		mod_SecurityCraft.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setUnlocalizedName("smartModule").setTextureName("securitycraft:smartModule");
	
		mod_SecurityCraft.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setUnlocalizedName("storageModule").setTextureName("securitycraft:storageModule");

		mod_SecurityCraft.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("wireCutters").setTextureName("securitycraft:wireCutter");
	
		mod_SecurityCraft.keyPanel = new ItemKeyPanel().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadItem").setTextureName("securitycraft:keypadItem");
		
		mod_SecurityCraft.adminTool = new ItemAdminTool().setMaxStackSize(1).setUnlocalizedName("adminTool").setTextureName("securitycraft:adminTool");
	
		mod_SecurityCraft.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("cameraMonitor").setTextureName("securitycraft:cameraMonitor");
	
		mod_SecurityCraft.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("scManual").setTextureName("securitycraft:scManual");
	
		mod_SecurityCraft.taser = new ItemTaser().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("taser");
	
		mod_SecurityCraft.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalOwnerChanger").setTextureName("securitycraft:universalOwnerChanger");
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
		registerBlock(mod_SecurityCraft.reinforcedGlassPane);
		registerBlock(mod_SecurityCraft.alarm);
		GameRegistry.registerBlock(mod_SecurityCraft.alarmLit, mod_SecurityCraft.alarmLit.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.reinforcedStone);
		registerBlock(mod_SecurityCraft.reinforcedFencegate);
		registerBlock(mod_SecurityCraft.reinforcedWoodPlanks, ItemBlockReinforcedPlanks.class);
		registerBlock(mod_SecurityCraft.panicButton);
		registerBlock(mod_SecurityCraft.frame);
		registerBlock(mod_SecurityCraft.claymoreActive);
		GameRegistry.registerBlock(mod_SecurityCraft.claymoreDefused, mod_SecurityCraft.claymoreDefused.getUnlocalizedName().substring(5));
		registerBlock(mod_SecurityCraft.keypadFurnace);
		registerBlock(mod_SecurityCraft.securityCamera);
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsOak, mod_SecurityCraft.reinforcedStairsOak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStairsSpruce, mod_SecurityCraft.reinforcedStairsSpruce.getUnlocalizedName().substring(5));
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
		
		registerItem(mod_SecurityCraft.Codebreaker);
	    registerItem(mod_SecurityCraft.doorIndestructableIronItem, mod_SecurityCraft.doorIndestructableIronItem.getUnlocalizedName().substring(5));
		registerItem(mod_SecurityCraft.universalBlockRemover);
		registerItem(mod_SecurityCraft.keycards);
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
		
		GameRegistry.registerTileEntity(TileEntityOwnable.class, "abstractOwnable");
		GameRegistry.registerTileEntity(TileEntitySCTE.class, "abstractSC");
		GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, "laserBlock");
		GameRegistry.registerTileEntity(TileEntityReinforcedDoor.class, "reinforcedDoor");
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, "keycardReader");
		GameRegistry.registerTileEntity(TileEntityRAM.class, "remoteAccessDoor");
		GameRegistry.registerTileEntity(TileEntityInventoryScanner.class, "inventoryScanner");
		GameRegistry.registerTileEntity(TileEntityPortableRadar.class, "portableRadar");
		GameRegistry.registerTileEntity(TileEntityEmpedWire.class, "empedWire");
		GameRegistry.registerTileEntity(TileEntitySecurityCamera.class, "securityCamera");
		GameRegistry.registerTileEntity(TileEntityLogger.class, "usernameLogger");
		GameRegistry.registerTileEntity(TileEntityRetinalScanner.class, "retinalScanner");
		GameRegistry.registerTileEntity(TileEntityKeypadChest.class, "keypadChest");
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "alarm");
		GameRegistry.registerTileEntity(TileEntityFrame.class, "keypadFrame");
		GameRegistry.registerTileEntity(TileEntityClaymore.class, "claymore");
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, "keypadFurnace");
		GameRegistry.registerTileEntity(TileEntityIMS.class, "ims");
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
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedGlassPane, 4), new Object[]{
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
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.claymoreActive, 1), new Object[]{
			"HSH", "SBS", "RGR", 'H', Blocks.tripwire_hook, 'S', Items.string, 'B', mod_SecurityCraft.bouncingBetty, 'R', Items.redstone, 'G', Items.gunpowder
		});
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.ironFence, 1), new Object[]{
			" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.fence
	    });
		
		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStairsStone, 4), new Object[]{
			"S  ", "SS ", "SSS", 'S', mod_SecurityCraft.reinforcedStone
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
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedGlass, 4), new Object[]{
    		" G ", "GGG", " G ", 'G', Blocks.glass
    	});
        
        GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.securityCamera, 1), new Object[]{
        	"III", "GRI", "IIS", 'I', Items.iron_ingot, 'G', mod_SecurityCraft.reinforcedGlassPane, 'R', Blocks.redstone_block, 'S', Items.stick
        });

        for(int i = 0; i < 16; i++){
        	GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 8, BlockColored.func_150031_c(i)), new Object[]{
        		"###", "#X#", "###", '#', new ItemStack(mod_SecurityCraft.reinforcedGlass), 'X', new ItemStack(Items.dye, 1, i)
        	});
        	
        	GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.reinforcedStainedGlassPanes, 16, i), new Object[]{
        		"###", "###", '#', new ItemStack(mod_SecurityCraft.reinforcedStainedGlass, 1, i)
        	});
        }
	
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
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, StatCollector.translateToLocal(item.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + item.getUnlocalizedName().substring(5) + ".info")));
	}
	
	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item, String customName){
		GameRegistry.registerItem(item, customName);
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, StatCollector.translateToLocal(item.getUnlocalizedName() + ".name"), StatCollector.translateToLocal("help." + item.getUnlocalizedName().substring(5) + ".info")));
	}
	
	public void registerDebuggingAdditions(){		
		GameRegistry.registerItem(mod_SecurityCraft.testItem, mod_SecurityCraft.testItem.getUnlocalizedName().substring(5));
	}
	
	public void setupOtherRegistrys(){}

	public void setupEntityRegistry() {
		EntityRegistry.registerModEntity(EntityTnTCompact.class, "TnTCompact", 0, mod_SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTaserBullet.class, "TazerBullet", 2, mod_SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntityIMSBomb.class, "IMSBomb", 3, mod_SecurityCraft.instance, 256, 1, true);
	}

	public void setupHandlers(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(mod_SecurityCraft.eventHandler);
	} 

	public void setupPackets(SimpleNetworkWrapper network) {
		network.registerMessage(PacketCheckRetinalScanner.Handler.class, PacketCheckRetinalScanner.class, 1, Side.SERVER);
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 2, Side.SERVER);
		network.registerMessage(PacketSetBlockMetadata.Handler.class, PacketSetBlockMetadata.class, 3, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 4, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 5, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 6, Side.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 7, Side.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 8, Side.SERVER);
		network.registerMessage(PacketCUpdateCooldown.Handler.class, PacketCUpdateCooldown.class, 9, Side.CLIENT);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 10, Side.CLIENT);
		network.registerMessage(PacketSetExplosiveState.Handler.class, PacketSetExplosiveState.class, 11, Side.SERVER);
		network.registerMessage(PacketGivePotionEffect.Handler.class, PacketGivePotionEffect.class, 12, Side.SERVER);
		network.registerMessage(PacketSetBlockAndMetadata.Handler.class, PacketSetBlockAndMetadata.class, 13, Side.SERVER);
		network.registerMessage(PacketSSetOwner.Handler.class, PacketSSetOwner.class, 14, Side.SERVER);
		network.registerMessage(PacketSAddModules.Handler.class, PacketSAddModules.class, 15, Side.SERVER);
		network.registerMessage(PacketCSetCameraLocation.Handler.class, PacketCSetCameraLocation.class, 16, Side.CLIENT);
		network.registerMessage(PacketCRemoveLGView.Handler.class, PacketCRemoveLGView.class, 17, Side.CLIENT);
		network.registerMessage(PacketCCreateLGView.Handler.class, PacketCCreateLGView.class, 18, Side.CLIENT);
		network.registerMessage(PacketSSetPassword.Handler.class, PacketSSetPassword.class, 19, Side.SERVER);
		network.registerMessage(PacketSCheckPassword.Handler.class, PacketSCheckPassword.class, 20, Side.SERVER);
		network.registerMessage(PacketSSyncTENBTTag.Handler.class, PacketSSyncTENBTTag.class, 21, Side.SERVER);
	}

}
