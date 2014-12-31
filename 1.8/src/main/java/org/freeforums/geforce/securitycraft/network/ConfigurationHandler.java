package org.freeforums.geforce.securitycraft.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.blocks.BlockActiveLaser;
import org.freeforums.geforce.securitycraft.blocks.BlockAlarm;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusLava;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusLavaBase;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusWater;
import org.freeforums.geforce.securitycraft.blocks.BlockBogusWaterBase;
import org.freeforums.geforce.securitycraft.blocks.BlockCageTrap;
import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScanner;
import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScannerBlock;
import org.freeforums.geforce.securitycraft.blocks.BlockIronTrapDoor;
import org.freeforums.geforce.securitycraft.blocks.BlockKeycardReader;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypad;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypadChest;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypadFurnace;
import org.freeforums.geforce.securitycraft.blocks.BlockLaser;
import org.freeforums.geforce.securitycraft.blocks.BlockLaserBlock;
import org.freeforums.geforce.securitycraft.blocks.BlockLogger;
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
import org.freeforums.geforce.securitycraft.blocks.mines.BlockFullMineBase;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockFurnaceMine;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockMine;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockTrackMine;
import org.freeforums.geforce.securitycraft.blocks.mines.TileEntityFullMine;
import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.items.ItemCodebreaker;
import org.freeforums.geforce.securitycraft.items.ItemKeycardBase;
import org.freeforums.geforce.securitycraft.items.ItemModifiedBucket;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.items.ItemReinforcedDoor;
import org.freeforums.geforce.securitycraft.items.ItemRemoteAccess;
import org.freeforums.geforce.securitycraft.items.ItemTestItem;
import org.freeforums.geforce.securitycraft.items.ItemUniversalBlockModifier;
import org.freeforums.geforce.securitycraft.items.ItemUniversalBlockRemover;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.network.packets.PacketCPlaySoundAtPos;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateCooldown;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateNBTTag;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateOwner;
import org.freeforums.geforce.securitycraft.network.packets.PacketCheckKeypadCode;
import org.freeforums.geforce.securitycraft.network.packets.PacketCheckRetinalScanner;
import org.freeforums.geforce.securitycraft.network.packets.PacketCreateExplosion;
import org.freeforums.geforce.securitycraft.network.packets.PacketSDebugField;
import org.freeforums.geforce.securitycraft.network.packets.PacketSUpdateNBTTag;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetBlock;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetISType;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetKeycardLevel;
import org.freeforums.geforce.securitycraft.network.packets.PacketSetKeypadCode;
import org.freeforums.geforce.securitycraft.network.packets.PacketUpdateClient;
import org.freeforums.geforce.securitycraft.network.packets.PacketUpdateLogger;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityAlarm;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityEmpedWire;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityIntersectable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScannerBlock;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLaser;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLaserBlock;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityLogger;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRAM;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityReinforcedDoor;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRetinalScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

public class ConfigurationHandler{
	private int[] harmingPotions = {8268, 8236, 16460, 16428};
	private int[] healingPotions = {8261, 8229, 16453, 16421};
	
	public boolean allowCodebreakerItem;
	public boolean allowDoorRemover;
	public boolean shouldSpawnFire;
	public boolean ableToBreakMines;
	public boolean ableToCraftKeycard1;
	public boolean ableToCraftKeycard2;
	public boolean ableToCraftKeycard3;
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
	
	public String currentHackIndex = "";
	public boolean ableToContinueHacking = true;
	public boolean hackingFailed = false;
		
	// RetinalScanner = new BlockRetinalScanner(1018, Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundMetalFootstep).setCreativeTab(mod_SecurityCraft.tabSecurityCraft).setUnlocalizedName("retinalScanner");

	public void setupAdditions(){
		this.setupCCTVMod();
		this.setupTechnicalBlocks();
		this.setupMines();
		this.setupItems();
	}
	
	private void setupCCTVMod() {
		//mod_SecurityCraft.instance.cctvPlugin.load();
        //GameRegistry.registerBlock(mod_SecurityCraft.instance.cctvPlugin.cctvCamera, mod_SecurityCraft.instance.cctvPlugin.cctvCamera.getUnlocalizedName().substring(5));
        //GameRegistry.registerItem(mod_SecurityCraft.instance.cctvPlugin.cctv, mod_SecurityCraft.instance.cctvPlugin.cctv.getUnlocalizedName().substring(5));
		
	}

	public void setupDebugAdditions() {
		this.setupDebuggingBlocks();
		this.setupDebuggingItems();
		
		this.registerDebuggingAdditions();
	}
	
	public void setupTechnicalBlocks(){
		mod_SecurityCraft.LaserBlock = new BlockLaserBlock(Material.iron).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("laserBlock");
		
		mod_SecurityCraft.Laser = new BlockLaser().setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("laser");
		
		mod_SecurityCraft.Keypad = new BlockKeypad(Material.circuits).setBlockUnbreakable().setResistance(1000).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypad");
				
		mod_SecurityCraft.LaserActive = new BlockActiveLaser(Material.iron).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("laserActive");
		
		mod_SecurityCraft.retinalScanner = new BlockRetinalScanner(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("retinalScanner");
	    
		mod_SecurityCraft.doorIndestructableIron = new BlockReinforcedDoor(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("reinforcedIronDoor");
		
		mod_SecurityCraft.bogusLava = (BlockStaticLiquid) new BlockBogusLavaBase(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("bogusLava");
		mod_SecurityCraft.bogusLavaFlowing = new BlockBogusLava(Material.lava).setHardness(0.0F).setLightLevel(1.0F).setUnlocalizedName("bogusLavaFlowing");
		mod_SecurityCraft.bogusWater = (BlockStaticLiquid) new BlockBogusWaterBase(Material.water).setHardness(100.0F).setUnlocalizedName("bogusWater");
		mod_SecurityCraft.bogusWaterFlowing = new BlockBogusWater(Material.water).setHardness(0.0F).setUnlocalizedName("bogusWaterFlowing");
		
		mod_SecurityCraft.keycardReader = new BlockKeycardReader(Material.iron).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keycardReader");
	    
		mod_SecurityCraft.ironTrapdoor = new BlockIronTrapDoor(Material.iron).setHardness(5.0F).setResistance(200F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("reinforcedIronTrapdoor");

		//mod_SecurityCraft.doorbell = new BlockDoorbell(2077, Material.rock).setHardness(2F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setTextureName("button").setUnlocalizedName("doorbell");

		mod_SecurityCraft.inventoryScanner = new BlockInventoryScanner(Material.rock).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeStone).setUnlocalizedName("inventoryScanner");

		mod_SecurityCraft.inventoryScannerField = new BlockInventoryScannerBlock(Material.glass).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("inventoryScannerField");
				
	    mod_SecurityCraft.cageTrap = new BlockCageTrap(Material.rock, false, cageTrapTextureIndex).setHardness(5F).setResistance(100F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("cageTrap");
		
	    mod_SecurityCraft.deactivatedCageTrap = new BlockCageTrap(Material.rock, true, cageTrapTextureIndex).setUnlocalizedName("deactivatedCageTrap");

	    mod_SecurityCraft.portableRadar = new BlockPortableRadar(Material.circuits).setHardness(1F).setResistance(50F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("portableRadar");
	    
	    mod_SecurityCraft.unbreakableIronBars = new BlockUnbreakableBars(Material.iron, true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setBlockUnbreakable().setResistance(1000F).setUnlocalizedName("reinforcedIronBars");
	    
		mod_SecurityCraft.keypadChest = new BlockKeypadChest(0).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeWood).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("keypadChest");
	
	    mod_SecurityCraft.usernameLogger = new BlockLogger(Material.rock).setHardness(8F).setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("usernameLogger");
	
		mod_SecurityCraft.reinforcedGlass = new BlockReinforcedGlass(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeGlass).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("reinforcedGlass");
	
		mod_SecurityCraft.alarm = new BlockAlarm(Material.iron, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("alarm");
	
		mod_SecurityCraft.alarmLit = new BlockAlarm(Material.iron, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setUnlocalizedName("alarmLit");
	
		mod_SecurityCraft.reinforcedStone = new BlockOwnable(Material.rock).setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeStone).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("reinforcedStone");
	
		mod_SecurityCraft.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("reinforcedFenceGate");
		
		mod_SecurityCraft.reinforcedPlanks_Oak = new BlockReinforcedWood().setUnlocalizedName("reinforcedPlanks_Oak");
		
		mod_SecurityCraft.reinforcedPlanks_Spruce = new BlockReinforcedWood().setUnlocalizedName("reinforcedPlanks_Spruce");

		mod_SecurityCraft.reinforcedPlanks_Birch = new BlockReinforcedWood().setUnlocalizedName("reinforcedPlanks_Birch");

		mod_SecurityCraft.reinforcedPlanks_Jungle = new BlockReinforcedWood().setUnlocalizedName("reinforcedPlanks_Jungle");

		mod_SecurityCraft.reinforcedPlanks_Acadia = new BlockReinforcedWood().setUnlocalizedName("reinforcedPlanks_Acadia");

		mod_SecurityCraft.reinforcedPlanks_DarkOak = new BlockReinforcedWood().setUnlocalizedName("reinforcedPlanks_DarkOak");

		mod_SecurityCraft.keypadFurnace = new BlockKeypadFurnace(Material.iron).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setStepSound(Block.soundTypeMetal).setUnlocalizedName("keypadFurnace");
	
		mod_SecurityCraft.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("panicButton");
	}
	
	public void setupMines(){
		mod_SecurityCraft.Mine = new BlockMine(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("mine");
				
		mod_SecurityCraft.DirtMine = new BlockFullMineBase(Material.ground).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirtMine");
		
		mod_SecurityCraft.StoneMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stoneMine");
		
		mod_SecurityCraft.CobblestoneMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("cobblestoneMine");
		
		mod_SecurityCraft.SandMine = new BlockFullMineBase(Material.sand).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setStepSound(Block.soundTypeSand).setUnlocalizedName("sandMine");
		
		mod_SecurityCraft.DiamondOreMine = new BlockFullMineBase(Material.rock).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("diamondMine");
		
		mod_SecurityCraft.FurnaceMine = new BlockFurnaceMine().setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setStepSound(Block.soundTypeStone).setUnlocalizedName("furnaceMine");
				
	    mod_SecurityCraft.trackMine = new BlockTrackMine().setHardness(!ableToBreakMines ? -1F : 0.7F).setStepSound(Block.soundTypeMetal).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("trackMine");

		mod_SecurityCraft.bouncingBetty = new BlockBouncingBetty(Material.circuits).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setUnlocalizedName("bouncingBetty");
	}
	
	public void setupItems(){
		mod_SecurityCraft.Codebreaker = new ItemCodebreaker().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("codebreaker");
	    
		mod_SecurityCraft.keycardLV1 = new ItemKeycardBase(1).setUnlocalizedName("keycardLV1");
		
		mod_SecurityCraft.keycardLV2 = new ItemKeycardBase(2).setUnlocalizedName("keycardLV2");

		mod_SecurityCraft.keycardLV3 = new ItemKeycardBase(3).setUnlocalizedName("keycardLV3");
	  
		mod_SecurityCraft.limitedUseKeycard = new ItemKeycardBase(4).setUnlocalizedName("limitedUseKeycard");

		mod_SecurityCraft.doorIndestructableIronItem = new ItemReinforcedDoor(Material.iron).setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	    
		mod_SecurityCraft.universalBlockRemover = new ItemUniversalBlockRemover().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockRemover");
		
		mod_SecurityCraft.remoteAccessMine = new ItemRemoteAccess(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("remoteAccessMine").setMaxStackSize(1);
		
		mod_SecurityCraft.fWaterBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusWater).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFWater");
		
		mod_SecurityCraft.fLavaBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusLava).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("bucketFLava");

		mod_SecurityCraft.universalBlockModifier = new ItemUniversalBlockModifier().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("universalBlockModifier");
	
		mod_SecurityCraft.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setUnlocalizedName("redstoneModule");
		
		mod_SecurityCraft.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setUnlocalizedName("whitelistModule");
		
		mod_SecurityCraft.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setUnlocalizedName("blacklistModule");
				
		mod_SecurityCraft.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setUnlocalizedName("harmingModule");
		
		mod_SecurityCraft.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setUnlocalizedName("smartModule");
		
		mod_SecurityCraft.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("wireCutters");
	}
	
	public void setupDebuggingBlocks(){
	    mod_SecurityCraft.securityCamera = new BlockSecurityCamera(Material.iron).setHardness(1.0F).setResistance(10.F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("securityCamera");		
	}
	
	public void setupDebuggingItems(){
		mod_SecurityCraft.testItem = new ItemTestItem().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setUnlocalizedName("Test");
	    
	    //mod_SecurityCraft.testChestplate = new ItemModifiedArmor(3213, armorBase, 0, 1).setUnlocalizedName("testChestplate").setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
		
	public void setupConfiguration() {
		mod_SecurityCraft.configFile.load();

        allowCodebreakerItem = mod_SecurityCraft.configFile.get("options", "Is codebreaker allowed?", true).getBoolean(true);
        allowDoorRemover = mod_SecurityCraft.configFile.get("options", "Is door remover allowed?", true).getBoolean(true);
        shouldSpawnFire = mod_SecurityCraft.configFile.get("options", "Mine(s) spawn fire when detonated?", true).getBoolean(true);
        ableToBreakMines = mod_SecurityCraft.configFile.get("options", "Are mines unbreakable?", true).getBoolean(true);
        ableToCraftKeycard1 = mod_SecurityCraft.configFile.get("options", "Craftable level 1 keycard?", true).getBoolean(true);
        ableToCraftKeycard2 = mod_SecurityCraft.configFile.get("options", "Craftable level 2 keycard?", true).getBoolean(true);
        ableToCraftKeycard3 = mod_SecurityCraft.configFile.get("options", "Craftable level 3 keycard?", true).getBoolean(true);
        smallerMineExplosion = mod_SecurityCraft.configFile.get("options", "Mines use a smaller explosion?", false).getBoolean(false);
        
        portableRadarSearchRadius = mod_SecurityCraft.configFile.get("options", "Portable radar search radius:", 25).getInt(25);
        usernameLoggerSearchRadius = mod_SecurityCraft.configFile.get("options", "Username logger search radius:", 3).getInt(3);
        laserBlockRange = mod_SecurityCraft.configFile.get("options", "Laser range:", 5).getInt(5);
        alarmTickDelay = mod_SecurityCraft.configFile.get("options", "Delay between alarm sounds (seconds):", 2).getInt(2);
        alarmSoundVolume = mod_SecurityCraft.configFile.get("options", "Alarm sound volume:", 1.0D).getDouble(1.0D);
        portableRadarDelay = (mod_SecurityCraft.configFile.get("options", "Portable radar delay (seconds):", 4).getInt(4) * 20);
        sayThanksMessage = mod_SecurityCraft.configFile.get("options", "Display a 'tip' message at spawn?", true).getBoolean(true);
        mod_SecurityCraft.debuggingMode = mod_SecurityCraft.configFile.get("options", "Is debug mode? (not recommended!)", false).getBoolean(false);
        isIrcBotEnabled = mod_SecurityCraft.configFile.get("options", "Disconnect IRC bot on world exited?", true).getBoolean(true);
        disconnectOnWorldClose = mod_SecurityCraft.configFile.get("options", "Is IRC bot enabled?", true).getBoolean(true);
        
        //cageTrapTextureIndex = config.get("Options", "cage-trap-block-texture (enter a block's id to use it's texture on the cage trap. Using the id '9999' will use the default cage trap texture)", 9999).getInt(9999);
        //empRadius = config.get("Options", "emp-radius(factors-of-ten-only)", 51).getInt(51);

        if(mod_SecurityCraft.configFile.hasChanged()){
        	mod_SecurityCraft.configFile.save();
        }
        

	}
	
	public void setupGameRegistry(){
		GameRegistry.registerBlock(mod_SecurityCraft.LaserBlock, mod_SecurityCraft.LaserBlock.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.Laser, mod_SecurityCraft.Laser.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.Keypad, mod_SecurityCraft.Keypad.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.Mine, mod_SecurityCraft.Mine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.DirtMine, mod_SecurityCraft.DirtMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.StoneMine, mod_SecurityCraft.StoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.CobblestoneMine, mod_SecurityCraft.CobblestoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.DiamondOreMine, mod_SecurityCraft.DiamondOreMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.SandMine, mod_SecurityCraft.SandMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.FurnaceMine, mod_SecurityCraft.FurnaceMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.retinalScanner, mod_SecurityCraft.retinalScanner.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.doorIndestructableIron, mod_SecurityCraft.doorIndestructableIron.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusLava, mod_SecurityCraft.bogusLava.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusLavaFlowing, mod_SecurityCraft.bogusLavaFlowing.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusWater, mod_SecurityCraft.bogusWater.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bogusWaterFlowing, mod_SecurityCraft.bogusWaterFlowing.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.keycardReader, mod_SecurityCraft.keycardReader.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.ironTrapdoor, mod_SecurityCraft.ironTrapdoor.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.bouncingBetty, mod_SecurityCraft.bouncingBetty.getUnlocalizedName().substring(5));
		//GameRegistry.registerBlock(mod_SecurityCraft.doorbell, "doorbell");
		GameRegistry.registerBlock(mod_SecurityCraft.inventoryScanner, mod_SecurityCraft.inventoryScanner.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.inventoryScannerField, mod_SecurityCraft.inventoryScannerField.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.trackMine, mod_SecurityCraft.trackMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.cageTrap, mod_SecurityCraft.cageTrap.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.portableRadar, mod_SecurityCraft.portableRadar.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.deactivatedCageTrap, mod_SecurityCraft.deactivatedCageTrap.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.unbreakableIronBars, mod_SecurityCraft.unbreakableIronBars.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.keypadChest, mod_SecurityCraft.keypadChest.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.usernameLogger, mod_SecurityCraft.usernameLogger.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedGlass, mod_SecurityCraft.reinforcedGlass.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.alarm, mod_SecurityCraft.alarm.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.alarmLit, mod_SecurityCraft.alarmLit.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedStone, mod_SecurityCraft.reinforcedStone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedFencegate, mod_SecurityCraft.reinforcedFencegate.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedPlanks_Oak, mod_SecurityCraft.reinforcedPlanks_Oak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedPlanks_Spruce, mod_SecurityCraft.reinforcedPlanks_Spruce.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedPlanks_Birch, mod_SecurityCraft.reinforcedPlanks_Birch.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedPlanks_Jungle, mod_SecurityCraft.reinforcedPlanks_Jungle.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedPlanks_Acadia, mod_SecurityCraft.reinforcedPlanks_Acadia.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.reinforcedPlanks_DarkOak, mod_SecurityCraft.reinforcedPlanks_DarkOak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.keypadFurnace, mod_SecurityCraft.keypadFurnace.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mod_SecurityCraft.panicButton, mod_SecurityCraft.panicButton.getUnlocalizedName().substring(5));
		
		GameRegistry.registerItem(mod_SecurityCraft.Codebreaker, mod_SecurityCraft.Codebreaker.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.doorIndestructableIronItem, mod_SecurityCraft.doorIndestructableIronItem.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.universalBlockRemover, mod_SecurityCraft.universalBlockRemover.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.keycardLV1, mod_SecurityCraft.keycardLV1.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.keycardLV2, mod_SecurityCraft.keycardLV2.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.keycardLV3, mod_SecurityCraft.keycardLV3.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.limitedUseKeycard, mod_SecurityCraft.limitedUseKeycard.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.remoteAccessMine, mod_SecurityCraft.remoteAccessMine.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.fWaterBucket, mod_SecurityCraft.fWaterBucket.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.fLavaBucket, mod_SecurityCraft.fLavaBucket.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.universalBlockModifier, mod_SecurityCraft.universalBlockModifier.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.redstoneModule, mod_SecurityCraft.redstoneModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.whitelistModule, mod_SecurityCraft.whitelistModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.blacklistModule, mod_SecurityCraft.blacklistModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.harmingModule, mod_SecurityCraft.harmingModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.smartModule, mod_SecurityCraft.smartModule.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(mod_SecurityCraft.wireCutters, mod_SecurityCraft.wireCutters.getUnlocalizedName().substring(5));
		
		GameRegistry.registerTileEntity(TileEntityOwnable.class, "abstractOwnable");
		GameRegistry.registerTileEntity(TileEntitySCTE.class, "abstractSC");
		GameRegistry.registerTileEntity(TileEntityIntersectable.class, "abstractIntersectable");
		GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, "laserBlock");
		GameRegistry.registerTileEntity(TileEntityLaser.class, "laser");
		GameRegistry.registerTileEntity(TileEntityReinforcedDoor.class, "reinforcedDoor");
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, "keycardReader");
		GameRegistry.registerTileEntity(TileEntityRAM.class, "remoteAccessDoor");
		GameRegistry.registerTileEntity(TileEntityInventoryScanner.class, "inventoryScanner");
		GameRegistry.registerTileEntity(TileEntityMineLoc.class, "mineLoc");
		GameRegistry.registerTileEntity(TileEntityFullMine.class, "blockMine");
		GameRegistry.registerTileEntity(TileEntityInventoryScannerBlock.class, "inventoryScannerField");
		GameRegistry.registerTileEntity(TileEntityPortableRadar.class, "portableRadar");
		GameRegistry.registerTileEntity(TileEntityEmpedWire.class, "empedWire");
		GameRegistry.registerTileEntity(TileEntitySecurityCamera.class, "securityCamera");
		GameRegistry.registerTileEntity(TileEntityLogger.class, "usernameLogger");
		GameRegistry.registerTileEntity(TileEntityRetinalScanner.class, "retinalScanner");
		GameRegistry.registerTileEntity(TileEntityKeypadChest.class, "keypadChest");
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "alarm");
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, "keypadFurnace");
		GameRegistry.registerTileEntity(CustomizableSCTE.class, "customizableSCTE");

		GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.Keypad, 1), new Object[]{
			"III", "III", "III", 'I', Blocks.stone_button
		});
		
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
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV1), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.gold_ingot 
			});
		}
		
		if(ableToCraftKeycard2){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV2), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.brick
			});
		}
		
		if(ableToCraftKeycard3){
			GameRegistry.addRecipe(new ItemStack(mod_SecurityCraft.keycardLV3), new Object[]{
				"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.netherbrick
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
			"IKI", "ICI", "III", 'I', Items.iron_ingot, 'K', mod_SecurityCraft.Keypad, 'C', Blocks.chest
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
			" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.oak_fence_gate
		});
		
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.DirtMine, 1), new Object[] {Blocks.dirt, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.StoneMine, 1), new Object[] {Blocks.stone, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.CobblestoneMine, 1), new Object[] {Blocks.cobblestone, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.DiamondOreMine, 1), new Object[] {Blocks.diamond_ore, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.SandMine, 1), new Object[] {Blocks.sand, mod_SecurityCraft.Mine});
        GameRegistry.addShapelessRecipe(new ItemStack(mod_SecurityCraft.FurnaceMine, 1), new Object[] {Blocks.furnace, mod_SecurityCraft.Mine});
	}
	
	public void registerDebuggingAdditions(){
		GameRegistry.registerBlock(mod_SecurityCraft.securityCamera, mod_SecurityCraft.securityCamera.getUnlocalizedName().substring(5));
		
		GameRegistry.registerItem(mod_SecurityCraft.testItem, mod_SecurityCraft.testItem.getUnlocalizedName().substring(5));
		
	}
	
	public void setupOtherRegistrys(){}


	public void setupEntityRegistry() {
		EntityRegistry.registerGlobalEntityID(EntityTnTCompact.class, "TnTCompact", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityTnTCompact.class, "TnTCompact", 0, mod_SecurityCraft.instance, 128, 1, true);
		//EntityRegistry.registerGlobalEntityID(EntityCCTV.class, "CCTV", EntityRegistry.findGlobalUniqueEntityId());
		//EntityRegistry.registerModEntity(EntityCCTV.class, "CCTV", 3, mod_SecurityCraft.instance, 225, 1, true);
	}

	public void setupHandlers(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(mod_SecurityCraft.eventHandler);

	} 

	public void setupPackets(SimpleNetworkWrapper network) {
		network.registerMessage(PacketCheckKeypadCode.Handler.class, PacketCheckKeypadCode.class, 0, Side.SERVER);
		network.registerMessage(PacketCheckRetinalScanner.Handler.class, PacketCheckRetinalScanner.class, 1, Side.SERVER);
		network.registerMessage(PacketCreateExplosion.Handler.class, PacketCreateExplosion.class, 2, Side.SERVER);
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 3, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 5, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 6, Side.SERVER);
		network.registerMessage(PacketSetKeypadCode.Handler.class, PacketSetKeypadCode.class, 7, Side.SERVER);
		network.registerMessage(PacketUpdateClient.Handler.class, PacketUpdateClient.class, 8, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 9, Side.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 10, Side.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 11, Side.SERVER);
		network.registerMessage(PacketCUpdateCooldown.Handler.class, PacketCUpdateCooldown.class, 12, Side.CLIENT);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 13, Side.CLIENT);
		network.registerMessage(PacketSDebugField.Handler.class, PacketSDebugField.class, 14, Side.SERVER);
		network.registerMessage(PacketCUpdateOwner.Handler.class, PacketCUpdateOwner.class, 15, Side.CLIENT);
	}

	@SideOnly(Side.CLIENT)
	public void setupTextureRegistry() {
		//Blocks 
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Keypad), 0, new ModelResourceLocation("securitycraft:keypad", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedStone), 0, new ModelResourceLocation("securitycraft:reinforcedStone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.LaserBlock), 0, new ModelResourceLocation("securitycraft:laserBlock", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Laser), 0, new ModelResourceLocation("securitycraft:laser", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.keypadChest), 0, new ModelResourceLocation("securitycraft:keypadChest", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.doorIndestructableIron), 0, new ModelResourceLocation("securitycraft:reinforcedIronDoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.ironTrapdoor), 0, new ModelResourceLocation("securitycraft:reinforcedIronTrapdoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.keycardReader), 0, new ModelResourceLocation("securitycraft:keycardReader", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.inventoryScanner), 0, new ModelResourceLocation("securitycraft:inventoryScanner", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.retinalScanner), 0, new ModelResourceLocation("securitycraft:retinalScanner", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedGlass), 0, new ModelResourceLocation("securitycraft:reinforcedGlass", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.unbreakableIronBars), 0, new ModelResourceLocation("securitycraft:reinforcedIronBars", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.portableRadar), 0, new ModelResourceLocation("securitycraft:portableRadar", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.alarm), 0, new ModelResourceLocation("securitycraft:alarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedFencegate), 0, new ModelResourceLocation("securitycraft:reinforcedFenceGate", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedPlanks_Oak), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Oak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedPlanks_Spruce), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Spruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedPlanks_Birch), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Birch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedPlanks_Jungle), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Jungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedPlanks_Acadia), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Acadia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.reinforcedPlanks_DarkOak), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_DarkOak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.keypadFurnace), 0, new ModelResourceLocation("securitycraft:keypadFurnace", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.panicButton), 0, new ModelResourceLocation("securitycraft:panicButton", "inventory"));

		//Items
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.Codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remoteAccessMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.doorIndestructableIronItem, 0, new ModelResourceLocation("securitycraft:doorIndestructibleIronItem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.fWaterBucket, 0, new ModelResourceLocation("securitycraft:bucketFWater", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.fLavaBucket, 0, new ModelResourceLocation("securitycraft:bucketFLava", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV1, 0, new ModelResourceLocation("securitycraft:keycardLV1", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV2, 0, new ModelResourceLocation("securitycraft:keycardLV2", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.keycardLV3, 0, new ModelResourceLocation("securitycraft:keycardLV3", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.limitedUseKeycard, 0, new ModelResourceLocation("securitycraft:limitedUseKeycard", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockRemover, 0, new ModelResourceLocation("securitycraft:universalBlockRemover", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.universalBlockModifier, 0, new ModelResourceLocation("securitycraft:universalBlockModifier", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.whitelistModule, 0, new ModelResourceLocation("securitycraft:whitelistModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.blacklistModule, 0, new ModelResourceLocation("securitycraft:blacklistModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.redstoneModule, 0, new ModelResourceLocation("securitycraft:redstoneModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.harmingModule, 0, new ModelResourceLocation("securitycraft:harmingModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.smartModule, 0, new ModelResourceLocation("securitycraft:smartModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(mod_SecurityCraft.wireCutters, 0, new ModelResourceLocation("securitycraft:wireCutters", "inventory"));
		//Mines
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.Mine), 0, new ModelResourceLocation("securitycraft:mine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.DirtMine), 0, new ModelResourceLocation("securitycraft:dirtMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.StoneMine), 0, new ModelResourceLocation("securitycraft:stoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.CobblestoneMine), 0, new ModelResourceLocation("securitycraft:cobblestoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.SandMine), 0, new ModelResourceLocation("securitycraft:sandMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.DiamondOreMine), 0, new ModelResourceLocation("securitycraft:diamondMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.FurnaceMine), 0, new ModelResourceLocation("securitycraft:furnaceMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.trackMine), 0, new ModelResourceLocation("securitycraft:trackMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HelpfulMethods.getItemFromBlock(mod_SecurityCraft.bouncingBetty), 0, new ModelResourceLocation("securitycraft:bouncingBetty", "inventory"));

	}

	

}
