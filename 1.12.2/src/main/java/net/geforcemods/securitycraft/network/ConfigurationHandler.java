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
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWood;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWool;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.itemblocks.ItemBlockPurpur;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedMetals;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPrismarine;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedQuartz;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs2;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWoodSlabs;
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
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.network.packets.PacketCRequestTEOwnableUpdate;
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
import net.geforcemods.securitycraft.network.packets.PacketSUpdateTEOwnable;
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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

public class ConfigurationHandler{
	private ItemStack[] harmingPotions = {PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HARMING), 
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.STRONG_HARMING), 
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HARMING)};
	
	private ItemStack[] healingPotions = {PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING), 
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.STRONG_HEALING), 
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HEALING)};
	
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
	}

	public void setupTechnicalBlocks(){
		mod_SecurityCraft.laserBlock = new BlockLaserBlock(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("laser_block").setUnlocalizedName("laserBlock");
		mod_SecurityCraft.laser = new BlockLaserField(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setRegistryName("laser");
		
		mod_SecurityCraft.keypad = new BlockKeypad(Material.IRON).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("keypad").setUnlocalizedName("keypad");
						
		mod_SecurityCraft.retinalScanner = new BlockRetinalScanner(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("retinal_scanner").setUnlocalizedName("retinalScanner");
	    
		mod_SecurityCraft.reinforcedDoor = new BlockReinforcedDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("iron_door_reinforced").setUnlocalizedName("ironDoorReinforced");
		
		mod_SecurityCraft.bogusLava = (BlockStaticLiquid) new BlockFakeLavaBase(Material.LAVA).setHardness(100.0F).setLightLevel(1.0F).setRegistryName("bogus_lava").setUnlocalizedName("bogusLava");
		mod_SecurityCraft.bogusLavaFlowing = new BlockFakeLava(Material.LAVA).setHardness(0.0F).setLightLevel(1.0F).setRegistryName("bogus_lava_flowing").setUnlocalizedName("bogusLavaFlowing");
		mod_SecurityCraft.bogusWater = (BlockStaticLiquid) new BlockFakeWaterBase(Material.WATER).setHardness(100.0F).setRegistryName("bogus_water").setUnlocalizedName("bogusWater");
		mod_SecurityCraft.bogusWaterFlowing = new BlockFakeWater(Material.WATER).setHardness(0.0F).setRegistryName("bogus_water_flowing").setUnlocalizedName("bogusWaterFlowing");
		
		mod_SecurityCraft.keycardReader = new BlockKeycardReader(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("keycard_reader").setUnlocalizedName("keycardReader");
	    
		mod_SecurityCraft.ironTrapdoor = new BlockIronTrapDoor(Material.IRON).setHardness(5.0F).setResistance(200F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_iron_trapdoor").setUnlocalizedName("reinforcedIronTrapdoor");

		mod_SecurityCraft.inventoryScanner = new BlockInventoryScanner(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("inventory_scanner").setUnlocalizedName("inventoryScanner");
		mod_SecurityCraft.inventoryScannerField = new BlockInventoryScannerField(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setRegistryName("inventory_scanner_field").setUnlocalizedName("inventoryScannerField");
				
	    mod_SecurityCraft.cageTrap = new BlockCageTrap(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("cage_trap").setUnlocalizedName("cageTrap");
		
	    mod_SecurityCraft.portableRadar = new BlockPortableRadar(Material.CIRCUITS).setHardness(1F).setResistance(50F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("portable_radar").setUnlocalizedName("portableRadar");
	    
	    mod_SecurityCraft.unbreakableIronBars = new BlockReinforcedIronBars(Material.IRON, true).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setBlockUnbreakable().setResistance(1000F).setRegistryName("reinforced_iron_bars").setUnlocalizedName("reinforcedIronBars");
	    
		mod_SecurityCraft.keypadChest = new BlockKeypadChest().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("keypad_chest").setUnlocalizedName("keypadChest");
	
	    mod_SecurityCraft.usernameLogger = new BlockLogger(Material.ROCK).setHardness(8F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("username_logger").setUnlocalizedName("usernameLogger");
	
		mod_SecurityCraft.alarm = new BlockAlarm(Material.IRON, false).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("alarm").setUnlocalizedName("alarm");
		mod_SecurityCraft.alarmLit = new BlockAlarm(Material.IRON, true).setBlockUnbreakable().setResistance(1000F).setTickRandomly(true).setRegistryName("alarm_lit").setUnlocalizedName("alarmLit");

		mod_SecurityCraft.reinforcedStone = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone").setUnlocalizedName("reinforcedStone");
	
		mod_SecurityCraft.reinforcedFencegate = new BlockReinforcedFenceGate().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_fence_gate").setUnlocalizedName("reinforcedFenceGate");
		
		mod_SecurityCraft.reinforcedWoodPlanks = new BlockReinforcedWood().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_planks").setUnlocalizedName("reinforcedPlanks");
	
		mod_SecurityCraft.panicButton = new BlockPanicButton().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("panic_button").setUnlocalizedName("panicButton");
	
		mod_SecurityCraft.frame = new BlockFrame(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("keypad_frame").setUnlocalizedName("keypadFrame");
	
		mod_SecurityCraft.keypadFurnace = new BlockKeypadFurnace(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("keypad_furnace").setUnlocalizedName("keypadFurnace");
	
	    mod_SecurityCraft.securityCamera = new BlockSecurityCamera(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("security_camera").setUnlocalizedName("securityCamera");
	
	    mod_SecurityCraft.reinforcedStairsOak = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_oak").setUnlocalizedName("reinforcedStairsOak");
	    mod_SecurityCraft.reinforcedStairsSpruce = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 1).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_spruce").setUnlocalizedName("reinforcedStairsSpruce");
	    mod_SecurityCraft.reinforcedStairsBirch = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 2).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_birch").setUnlocalizedName("reinforcedStairsBirch");
	    mod_SecurityCraft.reinforcedStairsJungle = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 3).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_jungle").setUnlocalizedName("reinforcedStairsJungle");
	    mod_SecurityCraft.reinforcedStairsAcacia = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 4).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_acacia").setUnlocalizedName("reinforcedStairsAcacia");
	    mod_SecurityCraft.reinforcedStairsDarkoak = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedWoodPlanks, 5).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_darkoak").setUnlocalizedName("reinforcedStairsDarkoak");
	    mod_SecurityCraft.reinforcedStairsStone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedStone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone").setUnlocalizedName("reinforcedStairsStone");

	    mod_SecurityCraft.ironFence = new BlockIronFence(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("electrified_iron_fence").setUnlocalizedName("electrifiedIronFence");
	
	    mod_SecurityCraft.reinforcedGlass = new BlockReinforcedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_glass_block").setUnlocalizedName("reinforcedGlassBlock");
	    mod_SecurityCraft.reinforcedStainedGlass = new BlockReinforcedStainedGlass(Material.GLASS).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_glass").setUnlocalizedName("reinforcedStainedGlass");
	    
	    mod_SecurityCraft.reinforcedDirt = new BlockOwnable(Material.GROUND).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_dirt").setUnlocalizedName("reinforcedDirt");
		
		mod_SecurityCraft.reinforcedCobblestone = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_cobblestone").setUnlocalizedName("reinforcedCobblestone");
	    mod_SecurityCraft.reinforcedStairsCobblestone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedCobblestone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_cobblestone").setUnlocalizedName("reinforcedStairsCobblestone");

	    mod_SecurityCraft.reinforcedSandstone = new BlockReinforcedSandstone().setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_sandstone").setUnlocalizedName("reinforcedSandstone");
	    mod_SecurityCraft.reinforcedStairsSandstone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_sandstone").setUnlocalizedName("reinforcedStairsSandstone");

	    mod_SecurityCraft.reinforcedWoodSlabs = new BlockReinforcedWoodSlabs(false).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wood_slabs").setUnlocalizedName("reinforcedWoodSlabs");
	    mod_SecurityCraft.reinforcedDoubleWoodSlabs = new BlockReinforcedWoodSlabs(true).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_wood_slabs").setUnlocalizedName("reinforcedDoubleWoodSlabs");
	    mod_SecurityCraft.reinforcedStoneSlabs = new BlockReinforcedSlabs(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs").setUnlocalizedName("reinforcedStoneSlabs");
	    mod_SecurityCraft.reinforcedDoubleStoneSlabs = new BlockReinforcedSlabs(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs").setUnlocalizedName("reinforcedDoubleStoneSlabs");
	  
	
		mod_SecurityCraft.protecto = new BlockProtecto(Material.IRON).setBlockUnbreakable().setResistance(1000F).setLightLevel(0.5F).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("protecto").setUnlocalizedName("protecto");
	
		mod_SecurityCraft.scannerDoor = new BlockScannerDoor(Material.IRON).setBlockUnbreakable().setResistance(1000F).setRegistryName("scanner_door").setUnlocalizedName("scannerDoor");

		mod_SecurityCraft.reinforcedStoneBrick = new BlockReinforcedStoneBrick().setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_brick").setUnlocalizedName("reinforcedStoneBrick");
	    mod_SecurityCraft.reinforcedStairsStoneBrick= new BlockReinforcedStairs(mod_SecurityCraft.reinforcedStoneBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_stone_brick").setUnlocalizedName("reinforcedStairsStoneBrick");
	
	    mod_SecurityCraft.reinforcedMossyCobblestone = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_mossy_cobblestone").setUnlocalizedName("reinforcedMossyCobblestone");
	    
	    mod_SecurityCraft.reinforcedBrick = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_brick").setUnlocalizedName("reinforcedBrick");
	    mod_SecurityCraft.reinforcedStairsBrick= new BlockReinforcedStairs(mod_SecurityCraft.reinforcedBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_brick").setUnlocalizedName("reinforcedStairsBrick");
	    
	    mod_SecurityCraft.reinforcedNetherBrick = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_nether_brick").setUnlocalizedName("reinforcedNetherBrick");
	    mod_SecurityCraft.reinforcedStairsNetherBrick= new BlockReinforcedStairs(mod_SecurityCraft.reinforcedNetherBrick, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_nether_brick").setUnlocalizedName("reinforcedStairsNetherBrick");
	
	    mod_SecurityCraft.reinforcedHardenedClay = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_hardened_clay").setUnlocalizedName("reinforcedHardenedClay");
		mod_SecurityCraft.reinforcedStainedHardenedClay = new BlockReinforcedStainedHardenedClay().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stained_hardened_clay").setUnlocalizedName("reinforcedStainedHardenedClay");
		
		mod_SecurityCraft.reinforcedOldLogs = new BlockReinforcedOldLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs").setUnlocalizedName("reinforcedLogs");
		mod_SecurityCraft.reinforcedNewLogs = new BlockReinforcedNewLog().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_logs2").setUnlocalizedName("reinforcedLogs2");
	
		mod_SecurityCraft.reinforcedMetals = new BlockReinforcedMetals().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_metals").setUnlocalizedName("reinforcedMetals");
		mod_SecurityCraft.reinforcedCompressedBlocks = new BlockReinforcedCompressedBlocks().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_compressed_blocks").setUnlocalizedName("reinforcedCompressedBlocks");
		
		mod_SecurityCraft.reinforcedWool = new BlockReinforcedWool().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_wool").setUnlocalizedName("reinforcedWool");
	
		mod_SecurityCraft.reinforcedQuartz = new BlockReinforcedQuartz().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_quartz").setUnlocalizedName("reinforcedQuartz");
	    mod_SecurityCraft.reinforcedStairsQuartz = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedQuartz, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_quartz").setUnlocalizedName("reinforcedStairsQuartz");
	
		mod_SecurityCraft.reinforcedPrismarine = new BlockReinforcedPrismarine().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_prismarine").setUnlocalizedName("reinforcedPrismarine");
	
		mod_SecurityCraft.reinforcedRedSandstone = new BlockReinforcedRedSandstone().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_sandstone").setUnlocalizedName("reinforcedRedSandstone");
		mod_SecurityCraft.reinforcedStairsRedSandstone = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedRedSandstone, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_red_sandstone").setUnlocalizedName("reinforcedStairsRedSandstone");
	
	    mod_SecurityCraft.reinforcedStoneSlabs2 = new BlockReinforcedSlabs2(false, Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stone_slabs2").setUnlocalizedName("reinforcedStoneSlabs2");
	    mod_SecurityCraft.reinforcedDoubleStoneSlabs2 = new BlockReinforcedSlabs2(true, Material.ROCK).setBlockUnbreakable().setResistance(1000).setRegistryName("reinforced_double_stone_slabs2").setUnlocalizedName("reinforcedDoubleStoneSlabs2");
	
	    mod_SecurityCraft.reinforcedEndStoneBricks = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_end_stone_bricks").setUnlocalizedName("reinforcedEndStoneBricks");
	    
	    mod_SecurityCraft.reinforcedRedNetherBrick = new BlockOwnable(Material.ROCK).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_red_nether_brick").setUnlocalizedName("reinforcedRedNetherBrick");
	
	    mod_SecurityCraft.reinforcedPurpur = new BlockReinforcedPurpur().setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_purpur").setUnlocalizedName("reinforcedPurpur");
	    mod_SecurityCraft.reinforcedStairsPurpur = new BlockReinforcedStairs(mod_SecurityCraft.reinforcedPurpur, 0).setBlockUnbreakable().setResistance(1000).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_stairs_purpur").setUnlocalizedName("reinforcedStairsPurpur");
	    
	    mod_SecurityCraft.reinforcedConcrete = new BlockReinforcedConcrete().setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCDecoration).setRegistryName("reinforced_concrete").setUnlocalizedName("reinforcedConcrete");
	}
	
	public void setupMines(){
		mod_SecurityCraft.mine = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setRegistryName("mine").setUnlocalizedName("mine");
		mod_SecurityCraft.mineCut = (BlockMine) new BlockMine(Material.CIRCUITS).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setRegistryName("mine_cut").setUnlocalizedName("mineCut");
		
		mod_SecurityCraft.dirtMine = new BlockFullMineBase(Material.GROUND, Blocks.DIRT).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setRegistryName("dirt_mine").setUnlocalizedName("dirtMine");
		mod_SecurityCraft.stoneMine = new BlockFullMineBase(Material.ROCK, Blocks.STONE).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.5F).setRegistryName("stone_mine").setUnlocalizedName("stoneMine");
		mod_SecurityCraft.cobblestoneMine = new BlockFullMineBase(Material.ROCK, Blocks.COBBLESTONE).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 2.75F).setRegistryName("cobblestone_mine").setUnlocalizedName("cobblestoneMine");
		mod_SecurityCraft.sandMine = new BlockFullMineBase(Material.SAND, Blocks.SAND).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 1.25F).setRegistryName("sand_mine").setUnlocalizedName("sandMine");
		mod_SecurityCraft.diamondOreMine = new BlockFullMineBase(Material.ROCK, Blocks.DIAMOND_ORE).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setRegistryName("diamond_mine").setUnlocalizedName("diamondMine");
		mod_SecurityCraft.furnaceMine = new BlockFurnaceMine(Material.ROCK).setCreativeTab(mod_SecurityCraft.tabSCMine).setHardness(!ableToBreakMines ? -1F : 3.75F).setRegistryName("furnace_mine").setUnlocalizedName("furnaceMine");
				
	    mod_SecurityCraft.trackMine = new BlockTrackMine().setHardness(!ableToBreakMines ? -1F : 0.7F).setCreativeTab(mod_SecurityCraft.tabSCMine).setRegistryName("track_mine").setUnlocalizedName("trackMine");

		mod_SecurityCraft.bouncingBetty = new BlockBouncingBetty(Material.CIRCUITS).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setRegistryName("bouncing_betty").setUnlocalizedName("bouncingBetty");
	
		mod_SecurityCraft.claymore = new BlockClaymore(Material.CIRCUITS).setHardness(!ableToBreakMines ? -1F : 1F).setResistance(3F).setCreativeTab(mod_SecurityCraft.tabSCMine).setRegistryName("claymore").setUnlocalizedName("claymore");
	
		mod_SecurityCraft.ims = new BlockIMS(Material.IRON).setBlockUnbreakable().setResistance(1000F).setCreativeTab(mod_SecurityCraft.tabSCMine).setRegistryName("ims").setUnlocalizedName("ims");
	}
	
	public void setupItems(){
		mod_SecurityCraft.codebreaker = new ItemCodebreaker().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("codebreaker").setUnlocalizedName("codebreaker");
	    
		mod_SecurityCraft.keycardLV1 = new ItemKeycardBase(0).setRegistryName("keycard_lv1").setUnlocalizedName("keycardLV1");
		mod_SecurityCraft.keycardLV2 = new ItemKeycardBase(1).setRegistryName("keycard_lv2").setUnlocalizedName("keycardLV2");
		mod_SecurityCraft.keycardLV3 = new ItemKeycardBase(2).setRegistryName("keycard_lv3").setUnlocalizedName("keycardLV3");
		mod_SecurityCraft.keycardLV4 = new ItemKeycardBase(4).setRegistryName("keycard_lv4").setUnlocalizedName("keycardLV4");
		mod_SecurityCraft.keycardLV5 = new ItemKeycardBase(5).setRegistryName("keycard_lv5").setUnlocalizedName("keycardLV5");
		mod_SecurityCraft.limitedUseKeycard = new ItemKeycardBase(3).setRegistryName("limited_use_keycard").setUnlocalizedName("limitedUseKeycard");

		mod_SecurityCraft.reinforcedDoorItem = new ItemReinforcedDoor().setRegistryName("door_indestructible_iron_item").setUnlocalizedName("doorIndestructibleIronItem").setCreativeTab(mod_SecurityCraft.tabSCDecoration);
		
		mod_SecurityCraft.universalBlockRemover = new ItemUniversalBlockRemover().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_block_remover").setUnlocalizedName("universalBlockRemover");
		
		mod_SecurityCraft.remoteAccessMine = new ItemMineRemoteAccessTool().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("remote_access_mine").setUnlocalizedName("remoteAccessMine");
		
		mod_SecurityCraft.fWaterBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusWaterFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_water").setUnlocalizedName("bucketFWater");
		
		mod_SecurityCraft.fLavaBucket = new ItemModifiedBucket(mod_SecurityCraft.bogusLavaFlowing).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("bucket_f_lava").setUnlocalizedName("bucketFLava");

		mod_SecurityCraft.universalBlockModifier = new ItemUniversalBlockModifier().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_block_modifier").setUnlocalizedName("universalBlockModifier");
	
		mod_SecurityCraft.redstoneModule = (ItemModule) new ItemModule(EnumCustomModules.REDSTONE, false).setRegistryName("redstone_module").setUnlocalizedName("redstoneModule");
		mod_SecurityCraft.whitelistModule = (ItemModule) new ItemModule(EnumCustomModules.WHITELIST, true).setRegistryName("whitelist_module").setUnlocalizedName("whitelistModule");
		mod_SecurityCraft.blacklistModule = (ItemModule) new ItemModule(EnumCustomModules.BLACKLIST, true).setRegistryName("blacklist_module").setUnlocalizedName("blacklistModule");
		mod_SecurityCraft.harmingModule = (ItemModule) new ItemModule(EnumCustomModules.HARMING, false).setRegistryName("harming_module").setUnlocalizedName("harmingModule");
		mod_SecurityCraft.smartModule = (ItemModule) new ItemModule(EnumCustomModules.SMART, false).setRegistryName("smart_module").setUnlocalizedName("smartModule");
		mod_SecurityCraft.storageModule = (ItemModule) new ItemModule(EnumCustomModules.STORAGE, false).setRegistryName("storage_module").setUnlocalizedName("storageModule");
		mod_SecurityCraft.disguiseModule = (ItemModule) new ItemModule(EnumCustomModules.DISGUISE, false, true, GuiHandler.DISGUISE_MODULE, 0, 1).setRegistryName("disguise_module").setUnlocalizedName("disguiseModule");

		mod_SecurityCraft.wireCutters = new Item().setMaxStackSize(1).setMaxDamage(476).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("wire_cutters").setUnlocalizedName("wireCutters");
	
		mod_SecurityCraft.keyPanel = new ItemKeyPanel().setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("keypad_item").setUnlocalizedName("keypadItem");
		
		mod_SecurityCraft.adminTool = new ItemAdminTool().setMaxStackSize(1).setRegistryName("admin_tool").setUnlocalizedName("adminTool");
	
		mod_SecurityCraft.cameraMonitor = new ItemCameraMonitor().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("camera_monitor").setUnlocalizedName("cameraMonitor");
	
		mod_SecurityCraft.scManual = new ItemSCManual().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("sc_manual").setUnlocalizedName("scManual");
	
		mod_SecurityCraft.taser = new ItemTaser().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("taser").setUnlocalizedName("taser");
	
		mod_SecurityCraft.universalOwnerChanger = new ItemUniversalOwnerChanger().setMaxStackSize(1).setMaxDamage(48).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_owner_changer").setUnlocalizedName("universalOwnerChanger");

		
		mod_SecurityCraft.universalBlockReinforcerLvL1 = new ItemUniversalBlockReinforcer(300).setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl1").setUnlocalizedName("universalBlockReinforcerLvL1");
		mod_SecurityCraft.universalBlockReinforcerLvL2 = new ItemUniversalBlockReinforcer(2700).setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl2").setUnlocalizedName("universalBlockReinforcerLvL2");
		mod_SecurityCraft.universalBlockReinforcerLvL3 = new ItemUniversalBlockReinforcer(0).setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_block_reinforcer_lvl3").setUnlocalizedName("universalBlockReinforcerLvL3");
	
	    mod_SecurityCraft.briefcase = new ItemBriefcase().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("briefcase").setUnlocalizedName("briefcase");
	
	    mod_SecurityCraft.universalKeyChanger = new ItemUniversalKeyChanger().setMaxStackSize(1).setCreativeTab(mod_SecurityCraft.tabSCTechnical).setRegistryName("universal_key_changer").setUnlocalizedName("universalKeyChanger");
	    
	    mod_SecurityCraft.scannerDoorItem = new ItemScannerDoor().setRegistryName("scanner_door_item").setUnlocalizedName("scannerDoorItem").setCreativeTab(mod_SecurityCraft.tabSCDecoration);
	}
	
	public void setupDebuggingBlocks() {}
	
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
		GameData.register_impl(mod_SecurityCraft.laser);
		registerBlock(mod_SecurityCraft.keypad);
		registerBlock(mod_SecurityCraft.mine);
		GameData.register_impl(mod_SecurityCraft.mineCut);
		registerBlock(mod_SecurityCraft.dirtMine);
		registerBlock(mod_SecurityCraft.stoneMine, false);
		registerBlock(mod_SecurityCraft.cobblestoneMine, false);
		registerBlock(mod_SecurityCraft.diamondOreMine, false);
		registerBlock(mod_SecurityCraft.sandMine, false);
		registerBlock(mod_SecurityCraft.furnaceMine);
		registerBlock(mod_SecurityCraft.retinalScanner);
		GameData.register_impl(mod_SecurityCraft.reinforcedDoor);
		registerBlock(mod_SecurityCraft.bogusLava, false);
		registerBlock(mod_SecurityCraft.bogusLavaFlowing, false);
		registerBlock(mod_SecurityCraft.bogusWater, false);
		registerBlock(mod_SecurityCraft.bogusWaterFlowing, false);
		registerBlock(mod_SecurityCraft.keycardReader);
		registerBlock(mod_SecurityCraft.ironTrapdoor);
		registerBlock(mod_SecurityCraft.bouncingBetty);
		registerBlock(mod_SecurityCraft.inventoryScanner);
		GameData.register_impl(mod_SecurityCraft.inventoryScannerField);
		registerBlock(mod_SecurityCraft.trackMine);
		registerBlock(mod_SecurityCraft.cageTrap);
		registerBlock(mod_SecurityCraft.portableRadar);
		registerBlock(mod_SecurityCraft.unbreakableIronBars, false);
		registerBlockWithCustomRecipe(mod_SecurityCraft.keypadChest, new ItemStack[]{ ItemStack.EMPTY, ItemUtils.toItemStack(mod_SecurityCraft.keyPanel), ItemStack.EMPTY, ItemStack.EMPTY, ItemUtils.toItemStack(Items.REDSTONE), ItemStack.EMPTY, ItemStack.EMPTY, ItemUtils.toItemStack(Item.getItemFromBlock(Blocks.CHEST)), ItemStack.EMPTY});
		registerBlock(mod_SecurityCraft.usernameLogger);
		registerBlock(mod_SecurityCraft.alarm);
		GameData.register_impl(mod_SecurityCraft.alarmLit);
		registerBlock(mod_SecurityCraft.reinforcedStone);
		registerBlock(mod_SecurityCraft.reinforcedSandstone, new ItemBlockReinforcedSandstone(mod_SecurityCraft.reinforcedSandstone), false);
		registerBlock(mod_SecurityCraft.reinforcedDirt, false);
		registerBlock(mod_SecurityCraft.reinforcedCobblestone, false);
		registerBlock(mod_SecurityCraft.reinforcedFencegate);
		registerBlock(mod_SecurityCraft.reinforcedWoodPlanks, new ItemBlockReinforcedPlanks(mod_SecurityCraft.reinforcedWoodPlanks), false);
		registerBlock(mod_SecurityCraft.panicButton);
		registerBlock(mod_SecurityCraft.frame);
		registerBlock(mod_SecurityCraft.claymore);
		registerBlock(mod_SecurityCraft.keypadFurnace);
		registerBlock(mod_SecurityCraft.securityCamera);
		registerBlock(mod_SecurityCraft.reinforcedStairsOak, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsSpruce, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsCobblestone, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsSandstone, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsBirch, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsJungle, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsAcacia, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsDarkoak, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsStone);
		registerBlock(mod_SecurityCraft.ironFence);
		registerBlock(mod_SecurityCraft.ims);
		registerBlock(mod_SecurityCraft.reinforcedGlass, false);
		registerBlock(mod_SecurityCraft.reinforcedStainedGlass, new ItemBlockReinforcedStainedBlock(mod_SecurityCraft.reinforcedStainedGlass), true);
		registerBlock(mod_SecurityCraft.reinforcedWoodSlabs, new ItemBlockReinforcedWoodSlabs(mod_SecurityCraft.reinforcedWoodSlabs), true);
		GameData.register_impl(mod_SecurityCraft.reinforcedDoubleWoodSlabs);
		registerBlock(mod_SecurityCraft.reinforcedStoneSlabs, new ItemBlockReinforcedSlabs(mod_SecurityCraft.reinforcedStoneSlabs), true);
		GameData.register_impl(mod_SecurityCraft.reinforcedDoubleStoneSlabs);
		registerBlock(mod_SecurityCraft.protecto);
		GameData.register_impl(mod_SecurityCraft.scannerDoor);
		registerBlock(mod_SecurityCraft.reinforcedStoneBrick, new ItemBlockReinforcedStoneBrick(mod_SecurityCraft.reinforcedStoneBrick), false);
		registerBlock(mod_SecurityCraft.reinforcedStairsStoneBrick);
		registerBlock(mod_SecurityCraft.reinforcedMossyCobblestone, false);
		registerBlock(mod_SecurityCraft.reinforcedBrick, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsBrick);
		registerBlock(mod_SecurityCraft.reinforcedNetherBrick, false);
		registerBlock(mod_SecurityCraft.reinforcedStairsNetherBrick);
		registerBlock(mod_SecurityCraft.reinforcedHardenedClay, false);
		registerBlock(mod_SecurityCraft.reinforcedStainedHardenedClay, new ItemBlockReinforcedStainedBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), false);
		registerBlock(mod_SecurityCraft.reinforcedOldLogs, new ItemBlockReinforcedLog(mod_SecurityCraft.reinforcedOldLogs), false);
		registerBlock(mod_SecurityCraft.reinforcedNewLogs, new ItemBlockReinforcedLog(mod_SecurityCraft.reinforcedNewLogs), false);
		registerBlock(mod_SecurityCraft.reinforcedMetals, new ItemBlockReinforcedMetals(mod_SecurityCraft.reinforcedMetals), false);
		registerBlock(mod_SecurityCraft.reinforcedCompressedBlocks, new ItemBlockReinforcedCompressedBlocks(mod_SecurityCraft.reinforcedCompressedBlocks), false);
		registerBlock(mod_SecurityCraft.reinforcedWool, new ItemBlockReinforcedStainedBlock(mod_SecurityCraft.reinforcedWool), false);
		registerBlock(mod_SecurityCraft.reinforcedQuartz, new ItemBlockReinforcedQuartz(mod_SecurityCraft.reinforcedQuartz), false);
		registerBlock(mod_SecurityCraft.reinforcedStairsQuartz);
		registerBlock(mod_SecurityCraft.reinforcedPrismarine, new ItemBlockReinforcedPrismarine(mod_SecurityCraft.reinforcedPrismarine), false);
		registerBlock(mod_SecurityCraft.reinforcedRedSandstone, new ItemBlockReinforcedSandstone(mod_SecurityCraft.reinforcedRedSandstone), false);
		registerBlock(mod_SecurityCraft.reinforcedStairsRedSandstone);
		registerBlock(mod_SecurityCraft.reinforcedStoneSlabs2, new ItemBlockReinforcedSlabs2(mod_SecurityCraft.reinforcedStoneSlabs2), false); //technically not a reinforced block, but doesn't need a page
		GameData.register_impl(mod_SecurityCraft.reinforcedDoubleStoneSlabs2);
		registerBlock(mod_SecurityCraft.reinforcedEndStoneBricks, false);
		registerBlock(mod_SecurityCraft.reinforcedRedNetherBrick, false);
		registerBlock(mod_SecurityCraft.reinforcedPurpur, new ItemBlockPurpur(mod_SecurityCraft.reinforcedPurpur), false);
		registerBlock(mod_SecurityCraft.reinforcedStairsPurpur);
		registerBlock(mod_SecurityCraft.reinforcedConcrete, new ItemBlockReinforcedStainedBlock(mod_SecurityCraft.reinforcedConcrete), false);
		
		registerItem(mod_SecurityCraft.codebreaker);
	    registerItem(mod_SecurityCraft.reinforcedDoorItem);
	    registerItem(mod_SecurityCraft.scannerDoorItem);
		registerItem(mod_SecurityCraft.universalBlockRemover);
		registerItem(mod_SecurityCraft.keycardLV1, ableToCraftKeycard1);
		registerItem(mod_SecurityCraft.keycardLV2, ableToCraftKeycard2);
		registerItem(mod_SecurityCraft.keycardLV3, ableToCraftKeycard3);
		registerItem(mod_SecurityCraft.keycardLV4, ableToCraftKeycard4);
		registerItem(mod_SecurityCraft.keycardLV5, ableToCraftKeycard5);
		registerItem(mod_SecurityCraft.limitedUseKeycard, ableToCraftLUKeycard);
		registerItem(mod_SecurityCraft.remoteAccessMine);
		registerItemWithCustomRecipe(mod_SecurityCraft.fWaterBucket, new ItemStack[]{ ItemStack.EMPTY, harmingPotions[0], ItemStack.EMPTY, ItemStack.EMPTY, ItemUtils.toItemStack(Items.WATER_BUCKET), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
		registerItemWithCustomRecipe(mod_SecurityCraft.fLavaBucket, new ItemStack[]{ ItemStack.EMPTY, healingPotions[0], ItemStack.EMPTY, ItemStack.EMPTY, ItemUtils.toItemStack(Items.LAVA_BUCKET), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
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

		for(int i = 0; i < SCSounds.values().length; i++)
		{
			SCSounds.values()[i].event.setRegistryName(SCSounds.values()[i].path);
			GameData.register_impl(SCSounds.values()[i].event);
		}
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 * @param block The block to register
	 */
	private void registerBlock(Block block)
	{
		registerBlock(block, new ItemBlock(block), true);
	}
	
	/**
	 * Registers a block and its ItemBlock
	 * @param block The Block to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private void registerBlock(Block block, boolean initPage)
	{
		registerBlock(block, new ItemBlock(block), initPage);
	}
	
	/**
	 * Registers a block with a custom ItemBlock
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private void registerBlock(Block block, ItemBlock itemBlock, boolean initPage){
		GameData.register_impl(block);
		GameData.register_impl(itemBlock.setRegistryName(block.getRegistryName().toString()));
		
		if(initPage)
		{
			if(initPage)
			{
				if(block == mod_SecurityCraft.reinforcedStone)
					mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.reinforced.info"));
				else
					mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
			}
		}
	}
	
	/**
	 * Registers the given block with GameData.register_implBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 * Also overrides the default recipe that would've been drawn in the manual with a new recipe.
	 * 
	 */
	private void registerBlockWithCustomRecipe(Block block, ItemStack... customRecipe){ 
		GameData.register_impl(block);
		GameData.register_impl(new ItemBlock(block).setRegistryName(block.getRegistryName().toString()));

		NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(customRecipe.length, Ingredient.EMPTY);
		
		for(int i = 0; i < recipeItems.size(); i++)
		{
			recipeItems.set(i, Ingredient.fromStacks(customRecipe[i]));
		}
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info", recipeItems));
	}
	
	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private void registerItem(Item item){
		GameData.register_impl(item);
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info"));
	}
	
	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private void registerItem(Item item, boolean configValue){
		GameData.register_impl(item);
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info", configValue));
	}
	
	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Also overrides the default recipe that would've been drawn in the manual with a new recipe. 
	 */
	private void registerItemWithCustomRecipe(Item item, ItemStack... customRecipe){ 
		GameData.register_impl(item);

		NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(customRecipe.length, Ingredient.EMPTY);
		
		for(int i = 0; i < recipeItems.size(); i++)
		{
			recipeItems.set(i, Ingredient.fromStacks(customRecipe[i]));
		}
		
		mod_SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info", recipeItems));
	}
	
	public void setupOtherRegistries(){
		EnumCustomModules.refresh();
	}

	public void setupEntityRegistry() {
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "bouncingbetty"), EntityBouncingBetty.class, "BBetty", 0, mod_SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "taserbullet"), EntityTaserBullet.class, "TazerBullet", 2, mod_SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "imsbomb"), EntityIMSBomb.class, "IMSBomb", 3, mod_SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "securitycamera"), EntitySecurityCamera.class, "SecurityCamera", 4, mod_SecurityCraft.instance, 256, 20, false);
	}

	public void setupHandlers(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(mod_SecurityCraft.eventHandler);
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
		network.registerMessage(PacketCRequestTEOwnableUpdate.Handler.class, PacketCRequestTEOwnableUpdate.class, 20, Side.SERVER);
		network.registerMessage(PacketSUpdateTEOwnable.Handler.class, PacketSUpdateTEOwnable.class, 21, Side.CLIENT);
		network.registerMessage(PacketSUpdateSliderValue.Handler.class, PacketSUpdateSliderValue.class, 22, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	public void setupTextureRegistry() {
		//Blocks 
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.keypad), 0, new ModelResourceLocation("securitycraft:keypad", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.frame), 0, new ModelResourceLocation("securitycraft:keypad_frame", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStone), 0, new ModelResourceLocation("securitycraft:reinforced_stone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.laserBlock), 0, new ModelResourceLocation("securitycraft:laser_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.laser), 0, new ModelResourceLocation("securitycraft:laser", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), 0, new ModelResourceLocation("securitycraft:keypad_chest", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedDoor), 0, new ModelResourceLocation("securitycraft:reinforced_iron_door", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.ironTrapdoor), 0, new ModelResourceLocation("securitycraft:reinforced_iron_trapdoor", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.keycardReader), 0, new ModelResourceLocation("securitycraft:keycard_reader", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.inventoryScanner), 0, new ModelResourceLocation("securitycraft:inventory_scanner", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.cageTrap), 0, new ModelResourceLocation("securitycraft:cage_trap", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.inventoryScannerField), 0, new ModelResourceLocation("securitycraft:inventory_scanner_field", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.retinalScanner), 0, new ModelResourceLocation("securitycraft:retinal_scanner", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.unbreakableIronBars), 0, new ModelResourceLocation("securitycraft:reinforced_iron_bars", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.portableRadar), 0, new ModelResourceLocation("securitycraft:portable_radar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.alarm), 0, new ModelResourceLocation("securitycraft:alarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.alarmLit), 0, new ModelResourceLocation("securitycraft:alarm_lit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.usernameLogger), 0, new ModelResourceLocation("securitycraft:username_logger", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedFencegate), 0, new ModelResourceLocation("securitycraft:reinforced_fence_gate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.ironFence), 0, new ModelResourceLocation("securitycraft:electrified_iron_fence", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 0, new ModelResourceLocation("securitycraft:reinforced_planks_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 1, new ModelResourceLocation("securitycraft:reinforced_planks_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 2, new ModelResourceLocation("securitycraft:reinforced_planks_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 3, new ModelResourceLocation("securitycraft:reinforced_planks_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 4, new ModelResourceLocation("securitycraft:reinforced_planks_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 5, new ModelResourceLocation("securitycraft:reinforced_planks_dark_oak", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsStone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_stone", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_cobblestone", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsOak), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_oak", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsSpruce), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_spruce", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsBirch), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_birch", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsJungle), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_jungle", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsAcacia), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_acacia", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsDarkoak), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_darkoak", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedGlass), 0, new ModelResourceLocation("securitycraft:reinforced_glass_block", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 0, new ModelResourceLocation("securitycraft:reinforced_stained_glass_white", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 1, new ModelResourceLocation("securitycraft:reinforced_stained_glass_orange", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 2, new ModelResourceLocation("securitycraft:reinforced_stained_glass_magenta", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 3, new ModelResourceLocation("securitycraft:reinforced_stained_glass_light_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 4, new ModelResourceLocation("securitycraft:reinforced_stained_glass_yellow", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 5, new ModelResourceLocation("securitycraft:reinforced_stained_glass_lime", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 6, new ModelResourceLocation("securitycraft:reinforced_stained_glass_pink", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 7, new ModelResourceLocation("securitycraft:reinforced_stained_glass_gray", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 8, new ModelResourceLocation("securitycraft:reinforced_stained_glass_silver", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 9, new ModelResourceLocation("securitycraft:reinforced_stained_glass_cyan", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 10, new ModelResourceLocation("securitycraft:reinforced_stained_glass_purple", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 11, new ModelResourceLocation("securitycraft:reinforced_stained_glass_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 12, new ModelResourceLocation("securitycraft:reinforced_stained_glass_brown", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 13, new ModelResourceLocation("securitycraft:reinforced_stained_glass_green", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 14, new ModelResourceLocation("securitycraft:reinforced_stained_glass_red", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedGlass), 15, new ModelResourceLocation("securitycraft:reinforced_stained_glass_black", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), 0, new ModelResourceLocation("securitycraft:keypad_chest", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.keypadFurnace), 0, new ModelResourceLocation("securitycraft:keypad_furnace", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.panicButton), 0, new ModelResourceLocation("securitycraft:panic_button", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.securityCamera), 0, new ModelResourceLocation("securitycraft:security_camera", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedDirt), 0, new ModelResourceLocation("securitycraft:reinforced_dirt", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_sandstone_normal", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedSandstone), 1, new ModelResourceLocation("securitycraft:reinforced_sandstone_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedSandstone), 2, new ModelResourceLocation("securitycraft:reinforced_sandstone_smooth", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 0, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 1, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 2, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 3, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 4, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodSlabs), 5, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_dark_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 0, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_stone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 1, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 2, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 3, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_stonebrick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 4, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 5, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_netherbrick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs), 6, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_quartz", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs2), 0, new ModelResourceLocation("securitycraft:reinforced_stone_slabs2_red_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneSlabs2), 1, new ModelResourceLocation("securitycraft:reinforced_stone_slabs2_purpur", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.protecto), 0, new ModelResourceLocation("securitycraft:protecto", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.scannerDoor), 0, new ModelResourceLocation("securitycraft:scanner_door", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stone_brick_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 1, new ModelResourceLocation("securitycraft:reinforced_stone_brick_mossy", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 2, new ModelResourceLocation("securitycraft:reinforced_stone_brick_cracked", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStoneBrick), 3, new ModelResourceLocation("securitycraft:reinforced_stone_brick_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_stone_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMossyCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_mossy_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedBrick), 0, new ModelResourceLocation("securitycraft:reinforced_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforced_nether_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_nether_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforced_hardened_clay", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_white", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 1, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_orange", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 2, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_magenta", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 3, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_light_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 4, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_yellow", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 5, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_lime", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 6, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_pink", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 7, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_gray", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 8, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_silver", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 9, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_cyan", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 10, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_purple", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 11, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 12, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_brown", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 13, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_green", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 14, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_red", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStainedHardenedClay), 15, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_black", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 0, new ModelResourceLocation("securitycraft:reinforced_logs_oak", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 1, new ModelResourceLocation("securitycraft:reinforced_logs_spruce", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 2, new ModelResourceLocation("securitycraft:reinforced_logs_birch", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedOldLogs), 3, new ModelResourceLocation("securitycraft:reinforced_logs_jungle", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedNewLogs), 0, new ModelResourceLocation("securitycraft:reinforced_logs2_acacia", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedNewLogs), 1, new ModelResourceLocation("securitycraft:reinforced_logs2_big_oak", "inventory"));;
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 0, new ModelResourceLocation("securitycraft:reinforced_metals_gold", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 1, new ModelResourceLocation("securitycraft:reinforced_metals_iron", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 2, new ModelResourceLocation("securitycraft:reinforced_metals_diamond", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedMetals), 3, new ModelResourceLocation("securitycraft:reinforced_metals_emerald", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedCompressedBlocks), 0, new ModelResourceLocation("securitycraft:reinforced_compressed_blocks_lapis", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedCompressedBlocks), 1, new ModelResourceLocation("securitycraft:reinforced_compressed_blocks_coal", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 0, new ModelResourceLocation("securitycraft:reinforced_wool_white", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 1, new ModelResourceLocation("securitycraft:reinforced_wool_orange", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 2, new ModelResourceLocation("securitycraft:reinforced_wool_magenta", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 3, new ModelResourceLocation("securitycraft:reinforced_wool_light_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 4, new ModelResourceLocation("securitycraft:reinforced_wool_yellow", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 5, new ModelResourceLocation("securitycraft:reinforced_wool_lime", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 6, new ModelResourceLocation("securitycraft:reinforced_wool_pink", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 7, new ModelResourceLocation("securitycraft:reinforced_wool_gray", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 8, new ModelResourceLocation("securitycraft:reinforced_wool_silver", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 9, new ModelResourceLocation("securitycraft:reinforced_wool_cyan", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 10, new ModelResourceLocation("securitycraft:reinforced_wool_purple", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 11, new ModelResourceLocation("securitycraft:reinforced_wool_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 12, new ModelResourceLocation("securitycraft:reinforced_wool_brown", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 13, new ModelResourceLocation("securitycraft:reinforced_wool_green", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 14, new ModelResourceLocation("securitycraft:reinforced_wool_red", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWool), 15, new ModelResourceLocation("securitycraft:reinforced_wool_black", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedQuartz), 0, new ModelResourceLocation("securitycraft:reinforced_quartz_default", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedQuartz), 1, new ModelResourceLocation("securitycraft:reinforced_quartz_chiseled", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedQuartz), 2, new ModelResourceLocation("securitycraft:reinforced_quartz_pillar", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsQuartz), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_quartz", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPrismarine), 0, new ModelResourceLocation("securitycraft:reinforced_prismarine_default", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPrismarine), 1, new ModelResourceLocation("securitycraft:reinforced_prismarine_bricks", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPrismarine), 2, new ModelResourceLocation("securitycraft:reinforced_prismarine_dark", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_red_sandstone_default", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedSandstone), 1, new ModelResourceLocation("securitycraft:reinforced_red_sandstone_chiseled", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedSandstone), 2, new ModelResourceLocation("securitycraft:reinforced_red_sandstone_smooth", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_red_sandstone", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedEndStoneBricks), 0, new ModelResourceLocation("securitycraft:reinforced_end_stone_bricks", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedRedNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforced_red_nether_brick", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPurpur), 0, new ModelResourceLocation("securitycraft:reinforced_purpur_default", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedPurpur), 1, new ModelResourceLocation("securitycraft:reinforced_purpur_pillar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedStairsPurpur), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_purpur", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 0, new ModelResourceLocation("securitycraft:reinforced_concrete_white", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 1, new ModelResourceLocation("securitycraft:reinforced_concrete_orange", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 2, new ModelResourceLocation("securitycraft:reinforced_concrete_magenta", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 3, new ModelResourceLocation("securitycraft:reinforced_concrete_light_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 4, new ModelResourceLocation("securitycraft:reinforced_concrete_yellow", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 5, new ModelResourceLocation("securitycraft:reinforced_concrete_lime", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 6, new ModelResourceLocation("securitycraft:reinforced_concrete_pink", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 7, new ModelResourceLocation("securitycraft:reinforced_concrete_gray", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 8, new ModelResourceLocation("securitycraft:reinforced_concrete_silver", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 9, new ModelResourceLocation("securitycraft:reinforced_concrete_cyan", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 10, new ModelResourceLocation("securitycraft:reinforced_concrete_purple", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 11, new ModelResourceLocation("securitycraft:reinforced_concrete_blue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 12, new ModelResourceLocation("securitycraft:reinforced_concrete_brown", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 13, new ModelResourceLocation("securitycraft:reinforced_concrete_green", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 14, new ModelResourceLocation("securitycraft:reinforced_concrete_red", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.reinforcedConcrete), 15, new ModelResourceLocation("securitycraft:reinforced_concrete_black", "inventory"));
        
		//Items
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remote_access_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.reinforcedDoorItem, 0, new ModelResourceLocation("securitycraft:door_indestructible_iron_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.fWaterBucket, 0, new ModelResourceLocation("securitycraft:bucket_f_water", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.fLavaBucket, 0, new ModelResourceLocation("securitycraft:bucket_f_lava", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.keycardLV1, 0, new ModelResourceLocation("securitycraft:keycard_lv1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.keycardLV2, 0, new ModelResourceLocation("securitycraft:keycard_lv2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.keycardLV3, 0, new ModelResourceLocation("securitycraft:keycard_lv3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.keycardLV4, 0, new ModelResourceLocation("securitycraft:keycard_lv4", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.keycardLV5, 0, new ModelResourceLocation("securitycraft:keycard_lv5", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.limitedUseKeycard, 0, new ModelResourceLocation("securitycraft:limited_use_keycard", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalBlockRemover, 0, new ModelResourceLocation("securitycraft:universal_block_remover", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalBlockModifier, 0, new ModelResourceLocation("securitycraft:universal_block_modifier", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.whitelistModule, 0, new ModelResourceLocation("securitycraft:whitelist_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.blacklistModule, 0, new ModelResourceLocation("securitycraft:blacklist_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.redstoneModule, 0, new ModelResourceLocation("securitycraft:redstone_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.harmingModule, 0, new ModelResourceLocation("securitycraft:harming_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.storageModule, 0, new ModelResourceLocation("securitycraft:storage_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.smartModule, 0, new ModelResourceLocation("securitycraft:smart_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.disguiseModule, 0, new ModelResourceLocation("securitycraft:disguise_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.wireCutters, 0, new ModelResourceLocation("securitycraft:wire_cutters", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.keyPanel, 0, new ModelResourceLocation("securitycraft:keypad_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.adminTool, 0, new ModelResourceLocation("securitycraft:admin_tool", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.cameraMonitor, 0, new ModelResourceLocation("securitycraft:camera_monitor", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.scManual, 0, new ModelResourceLocation("securitycraft:sc_manual", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.taser, 0, new ModelResourceLocation("securitycraft:taser", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalOwnerChanger, 0, new ModelResourceLocation("securitycraft:universal_owner_changer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalBlockReinforcerLvL1, 0, new ModelResourceLocation("securitycraft:universal_block_reinforcer_lvl1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalBlockReinforcerLvL2, 0, new ModelResourceLocation("securitycraft:universal_block_reinforcer_lvl2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalBlockReinforcerLvL3, 0, new ModelResourceLocation("securitycraft:universal_block_reinforcer_lvl3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.briefcase, 0, new ModelResourceLocation("securitycraft:briefcase", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.universalKeyChanger, 0, new ModelResourceLocation("securitycraft:universal_key_changer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(mod_SecurityCraft.scannerDoorItem, 0, new ModelResourceLocation("securitycraft:scanner_door_item", "inventory"));
		
		//Mines
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.mine), 0, new ModelResourceLocation("securitycraft:mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.dirtMine), 0, new ModelResourceLocation("securitycraft:dirt_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.stoneMine), 0, new ModelResourceLocation("securitycraft:stone_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.cobblestoneMine), 0, new ModelResourceLocation("securitycraft:cobblestone_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.sandMine), 0, new ModelResourceLocation("securitycraft:sand_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.diamondOreMine), 0, new ModelResourceLocation("securitycraft:diamond_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.furnaceMine), 0, new ModelResourceLocation("securitycraft:furnace_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.trackMine), 0, new ModelResourceLocation("securitycraft:track_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.bouncingBetty), 0, new ModelResourceLocation("securitycraft:bouncing_betty", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.claymore), 0, new ModelResourceLocation("securitycraft:claymore", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mod_SecurityCraft.ims), 0, new ModelResourceLocation("securitycraft:ims", "inventory"));
	}
}
