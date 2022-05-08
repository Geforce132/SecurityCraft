package net.geforcemods.securitycraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntityNamed;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.geforcemods.securitycraft.itemblocks.ItemBlockCrystalQuartzSlab;
import net.geforcemods.securitycraft.itemblocks.ItemBlockCustomQuartz;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCrystalQuartzSlab;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedDirt;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedMetals;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPrismarine;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPurpur;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSand;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs2;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWalls;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.items.ItemSCManual;
import net.geforcemods.securitycraft.misc.DyeBriefcaseRecipe;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.PageType;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.tileentity.TileEntityAllowlistOnly;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocket;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityIronFence;
import net.geforcemods.securitycraft.tileentity.TileEntityKeyPanel;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedCauldron;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedHopper;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedIronBars;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPiston;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.geforcemods.securitycraft.tileentity.TileEntityTrackMine;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.tileentity.TileEntityValidationOwnable;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;

@EventBusSubscriber
public class RegistrationHandler {
	private static List<Item> itemBlocks = new ArrayList<>();
	private static List<Block> blockPages = new ArrayList<>();
	private static Map<PageType, List<Block>> pageTypeBlocks = new EnumMap<>(PageType.class);
	private static Map<PageType, List<ItemStack>> pageTypeStacks = new EnumMap<>(PageType.class);
	private static Map<Block, String> blocksDesignedBy = new HashMap<>();
	private static Map<Block, Boolean> blockConfigValues = new HashMap<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		registerBlock(event, SCContent.laserBlock);
		event.getRegistry().register(SCContent.laserField);
		registerBlock(event, SCContent.keypad);
		registerBlock(event, SCContent.mine, ConfigHandler.ableToCraftMines);
		event.getRegistry().register(SCContent.mineCut);
		registerBlock(event, SCContent.retinalScanner);
		event.getRegistry().register(SCContent.reinforcedDoor);
		registerBlock(event, SCContent.fakeLava, PageType.NO_PAGE);
		registerBlock(event, SCContent.bogusLavaFlowing, PageType.NO_PAGE);
		registerBlock(event, SCContent.fakeWater, PageType.NO_PAGE);
		registerBlock(event, SCContent.bogusWaterFlowing, PageType.NO_PAGE);
		registerBlock(event, SCContent.keycardReader);
		registerBlock(event, SCContent.reinforcedIronTrapdoor);
		registerBlock(event, SCContent.bouncingBetty, ConfigHandler.ableToCraftMines);
		registerBlock(event, SCContent.inventoryScanner);
		event.getRegistry().register(SCContent.inventoryScannerField);
		registerBlock(event, SCContent.trackMine, ConfigHandler.ableToCraftMines);
		registerBlock(event, SCContent.cageTrap);
		event.getRegistry().register(SCContent.horizontalReinforcedIronBars);
		registerBlock(event, SCContent.portableRadar);
		registerBlock(event, SCContent.reinforcedIronBars, PageType.REINFORCED);
		registerBlock(event, SCContent.keypadChest);
		registerBlock(event, SCContent.usernameLogger);
		registerBlock(event, SCContent.alarm);
		event.getRegistry().register(SCContent.alarmLit);
		registerBlock(event, SCContent.reinforcedStone, new ItemBlockReinforcedStone(SCContent.reinforcedStone), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedSandstone), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedDirt, new ItemBlockReinforcedDirt(SCContent.reinforcedDirt), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedCobblestone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedFencegate);
		registerBlock(event, SCContent.reinforcedWoodPlanks, new ItemBlockReinforcedPlanks(SCContent.reinforcedWoodPlanks), PageType.REINFORCED);
		registerBlock(event, SCContent.panicButton);
		registerBlock(event, SCContent.frame);
		registerBlock(event, SCContent.claymore, ConfigHandler.ableToCraftMines);
		registerBlock(event, SCContent.keypadFurnace);
		registerBlock(event, SCContent.securityCamera);
		registerBlock(event, SCContent.reinforcedStairsOak, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsSpruce, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsCobblestone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsSandstone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsBirch, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsJungle, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsAcacia, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsDarkoak, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsStone, PageType.REINFORCED);
		registerBlock(event, SCContent.ironFence);
		registerBlock(event, SCContent.ims, ConfigHandler.ableToCraftMines);
		registerBlock(event, SCContent.reinforcedGlass, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStainedGlass, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlass), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedWoodSlabs, new ItemBlockReinforcedWoodSlabs(SCContent.reinforcedWoodSlabs), PageType.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleWoodSlabs);
		registerBlock(event, SCContent.reinforcedStoneSlabs, new ItemBlockReinforcedSlabs(SCContent.reinforcedStoneSlabs), PageType.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs);
		registerBlock(event, SCContent.protecto);
		event.getRegistry().register(SCContent.scannerDoor);
		registerBlock(event, SCContent.reinforcedStoneBrick, new ItemBlockReinforcedStoneBrick(SCContent.reinforcedStoneBrick), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsStoneBrick, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedMossyCobblestone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedBrick, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsBrick, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherBrick, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsNetherBrick, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedHardenedClay, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStainedHardenedClay, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedHardenedClay), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedOldLogs, new ItemBlockReinforcedLog(SCContent.reinforcedOldLogs), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedNewLogs, new ItemBlockReinforcedLog(SCContent.reinforcedNewLogs), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedMetals, new ItemBlockReinforcedMetals(SCContent.reinforcedMetals), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedCompressedBlocks, new ItemBlockReinforcedCompressedBlocks(SCContent.reinforcedCompressedBlocks), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedWool, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedWool), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedQuartz, new ItemBlockCustomQuartz(SCContent.reinforcedQuartz), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsQuartz, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedPrismarine, new ItemBlockReinforcedPrismarine(SCContent.reinforcedPrismarine), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedRedSandstone), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsRedSandstone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStoneSlabs2, new ItemBlockReinforcedSlabs2(SCContent.reinforcedStoneSlabs2), PageType.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs2);
		registerBlock(event, SCContent.reinforcedEndStoneBricks, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedNetherBrick, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedPurpur, new ItemBlockReinforcedPurpur(SCContent.reinforcedPurpur), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsPurpur, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedConcrete, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedConcrete), PageType.REINFORCED);
		event.getRegistry().register(SCContent.secretSignWall);
		event.getRegistry().register(SCContent.secretSignStanding);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.reinforcedObsidian, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherrack, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedEndStone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedSeaLantern, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedBoneBlock, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedGlassPane, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStainedGlassPanes, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlassPanes), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedCarpet, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedCarpet), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedGlowstone, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedSand, new ItemBlockReinforcedSand(SCContent.reinforcedSand), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedGravel, PageType.REINFORCED);
		registerBlock(event, SCContent.trophySystem);
		registerBlock(event, SCContent.crystalQuartz, new ItemBlockCustomQuartz(SCContent.crystalQuartz), PageType.SINGLE_ITEM);
		registerBlock(event, SCContent.reinforcedCrystalQuartz, new ItemBlockCustomQuartz(SCContent.reinforcedCrystalQuartz), PageType.REINFORCED);
		registerBlock(event, SCContent.crystalQuartzSlab, new ItemBlockCrystalQuartzSlab(SCContent.crystalQuartzSlab), PageType.NO_PAGE);
		event.getRegistry().register(SCContent.doubleCrystalQuartzSlab);
		registerBlock(event, SCContent.reinforcedCrystalQuartzSlab, new ItemBlockReinforcedCrystalQuartzSlab(SCContent.reinforcedCrystalQuartzSlab), PageType.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleCrystalQuartzSlab);
		registerBlock(event, SCContent.stairsCrystalQuartz, PageType.NO_PAGE);
		registerBlock(event, SCContent.reinforcedStairsCrystalQuartz, PageType.REINFORCED);
		registerBlock(event, SCContent.blockPocketWall);
		registerBlock(event, SCContent.blockPocketManager, "Henzoid");
		registerBlock(event, SCContent.reinforcedStonePressurePlate, PageType.PRESSURE_PLATES);
		registerBlock(event, SCContent.reinforcedWoodenPressurePlate, PageType.PRESSURE_PLATES);
		registerBlock(event, SCContent.reinforcedBookshelf, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedWalls, new ItemBlockReinforcedWalls(SCContent.reinforcedWalls), PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStickyPiston, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedPiston, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedPistonHead, null, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedPistonExtension, null, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedObserver, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedstoneLamp, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedCobweb, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedGrass, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedSnowBlock, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedIce, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedPackedIce, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedMycelium, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedClay, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherWartBlock, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedGrassPath, PageType.REINFORCED);
		registerBlock(event, SCContent.reinforcedStoneButton, PageType.BUTTONS);
		registerBlock(event, SCContent.reinforcedWoodenButton, PageType.BUTTONS);
		registerBlock(event, SCContent.reinforcedLever);
		registerBlock(event, SCContent.reinforcedHopper);
		registerBlock(event, SCContent.projector);
		event.getRegistry().register(SCContent.keypadDoor);
		registerBlock(event, SCContent.reinforcedCauldron, PageType.REINFORCED);
		event.getRegistry().register(SCContent.keyPanelFloorCeilingBlock);
		event.getRegistry().register(SCContent.keyPanelWallBlock);
		registerBlock(event, SCContent.sonicSecuritySystem, (ItemBlock) SCContent.sonicSecuritySystemItem, PageType.SINGLE_ITEM);
		registerBlock(event, SCContent.blockChangeDetector);
		event.getRegistry().register(SCContent.sentryDisguise);

		//block mines
		registerBlockMine(event, SCContent.stoneMine);
		registerBlockMine(event, SCContent.dirtMine);
		registerBlockMine(event, SCContent.cobblestoneMine);
		registerBlockMine(event, SCContent.sandMine);
		registerBlockMine(event, SCContent.gravelMine);
		registerBlockMine(event, SCContent.goldOreMine);
		registerBlockMine(event, SCContent.ironOreMine);
		registerBlockMine(event, SCContent.coalOreMine);
		registerBlockMine(event, SCContent.lapisOreMine);
		registerBlockMine(event, SCContent.diamondOreMine);
		registerBlockMine(event, SCContent.redstoneOreMine);
		registerBlockMine(event, SCContent.emeraldOreMine);
		registerBlockMine(event, SCContent.quartzOreMine);
		registerBlock(event, SCContent.furnaceMine, ConfigHandler.ableToCraftMines);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		//register item blocks
		for (Item item : itemBlocks) {
			event.getRegistry().register(item);
		}

		//init block sc manual pages
		for (Block block : blockPages) {
			//@formatter:off
			ItemSCManual.PAGES.add(new SCManualPage(
					Item.getItemFromBlock(block),
					PageType.SINGLE_ITEM,
					Utils.localize(block),
					Utils.localize("help." + block.getTranslationKey().substring(5) + ".info"),
					blocksDesignedBy.getOrDefault(block, ""),
					false,
					blockConfigValues.getOrDefault(block, true)));
			//@formatter:on
		}

		registerItem(event, SCContent.codebreaker);
		registerItem(event, SCContent.reinforcedDoorItem);
		registerItem(event, SCContent.scannerDoorItem);
		registerItem(event, SCContent.universalBlockRemover);
		registerItem(event, SCContent.keycardLvl1, PageType.KEYCARDS, ConfigHandler.ableToCraftKeycard1);
		registerItem(event, SCContent.keycardLvl2, PageType.KEYCARDS, ConfigHandler.ableToCraftKeycard2);
		registerItem(event, SCContent.keycardLvl3, PageType.KEYCARDS, ConfigHandler.ableToCraftKeycard3);
		registerItem(event, SCContent.keycardLvl4, PageType.KEYCARDS, ConfigHandler.ableToCraftKeycard4);
		registerItem(event, SCContent.keycardLvl5, PageType.KEYCARDS, ConfigHandler.ableToCraftKeycard5);
		registerItem(event, SCContent.limitedUseKeycard, PageType.SINGLE_ITEM, ConfigHandler.ableToCraftLUKeycard);
		registerItem(event, SCContent.remoteAccessMine);
		registerItem(event, SCContent.remoteAccessSentry);
		registerItem(event, SCContent.fWaterBucket);
		registerItem(event, SCContent.fLavaBucket);
		registerItem(event, SCContent.universalBlockModifier);
		registerItem(event, SCContent.redstoneModule);
		registerItem(event, SCContent.allowlistModule);
		registerItem(event, SCContent.denylistModule);
		registerItem(event, SCContent.harmingModule);
		registerItem(event, SCContent.smartModule);
		registerItem(event, SCContent.storageModule);
		registerItem(event, SCContent.disguiseModule);
		registerItem(event, SCContent.speedModule);
		registerItem(event, SCContent.wireCutters);
		registerItem(event, SCContent.adminTool);
		registerItem(event, SCContent.keyPanel);
		registerItem(event, SCContent.cameraMonitor);
		registerItem(event, SCContent.taser);
		registerItem(event, SCContent.scManual);
		registerItem(event, SCContent.universalOwnerChanger);
		registerItem(event, SCContent.universalBlockReinforcerLvL1, PageType.BLOCK_REINFORCERS);
		registerItem(event, SCContent.universalBlockReinforcerLvL2, PageType.BLOCK_REINFORCERS);
		registerItem(event, SCContent.universalBlockReinforcerLvL3, PageType.BLOCK_REINFORCERS);
		registerItem(event, SCContent.briefcase);
		registerItem(event, SCContent.universalKeyChanger);
		event.getRegistry().register(SCContent.taserPowered); //won't show up in the manual
		registerItem(event, SCContent.secretSignItem);
		registerItem(event, SCContent.sentry, PageType.SINGLE_ITEM, true, "Henzoid");
		registerItem(event, SCContent.crystalQuartzItem);
		registerItem(event, SCContent.keypadDoorItem);
		registerItem(event, SCContent.portableTunePlayer);

		SecurityCraft.proxy.registerVariants();
		pageTypeBlocks.forEach((pageType, list) -> {
			if (!pageTypeStacks.containsKey(pageType))
				pageTypeStacks.put(pageType, new ArrayList<>());

			list.stream().map(Item::getItemFromBlock).forEach(item -> {
				if (item != null && item.getHasSubtypes()) {
					NonNullList<ItemStack> subStacks = NonNullList.create();

					item.getSubItems(item.getCreativeTab(), subStacks);
					pageTypeStacks.get(pageType).addAll(subStacks);
				}
				else
					pageTypeStacks.get(pageType).add(new ItemStack(item));
			});
		});
		pageTypeStacks.remove(PageType.SINGLE_ITEM);
		pageTypeStacks.forEach((pageType, list) -> {
			pageType.setItems(Ingredient.fromStacks(list.toArray(new ItemStack[list.size()])));
			ItemSCManual.PAGES.add(new SCManualPage(list.get(0).getItem(), pageType, Utils.localize(pageType.getTitle()), Utils.localize(pageType.getSpecialInfoKey()), "", !pageType.hasRecipeGrid()));
		});
		//clear unused memory
		itemBlocks = null;
		blockPages = null;
		pageTypeBlocks = null;
		pageTypeStacks = null;
		blocksDesignedBy = null;
		blockConfigValues = null;
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<Block> event) {
		GameRegistry.registerTileEntity(TileEntityOwnable.class, new ResourceLocation("securitycraft:ownable"));
		GameRegistry.registerTileEntity(TileEntityNamed.class, new ResourceLocation("securitycraft:abstract"));
		GameRegistry.registerTileEntity(TileEntityKeypad.class, new ResourceLocation("securitycraft:keypad"));
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, new ResourceLocation("securitycraft:laser_block"));
		GameRegistry.registerTileEntity(TileEntityCageTrap.class, new ResourceLocation("securitycraft:cage_trap"));
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, new ResourceLocation("securitycraft:keycard_reader"));
		GameRegistry.registerTileEntity(TileEntityInventoryScanner.class, new ResourceLocation("securitycraft:inventory_scanner"));
		GameRegistry.registerTileEntity(TileEntityPortableRadar.class, new ResourceLocation("securitycraft:portable_radar"));
		GameRegistry.registerTileEntity(TileEntitySecurityCamera.class, new ResourceLocation("securitycraft:security_camera"));
		GameRegistry.registerTileEntity(TileEntityLogger.class, new ResourceLocation("securitycraft:username_logger"));
		GameRegistry.registerTileEntity(TileEntityRetinalScanner.class, new ResourceLocation("securitycraft:retinal_scanner"));
		GameRegistry.registerTileEntity(TileEntityKeypadChest.class, new ResourceLocation("securitycraft:keypad_chest"));
		GameRegistry.registerTileEntity(TileEntityAlarm.class, new ResourceLocation("securitycraft:alarm"));
		GameRegistry.registerTileEntity(TileEntityClaymore.class, new ResourceLocation("securitycraft:claymore"));
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, new ResourceLocation("securitycraft:keypad_furnace"));
		GameRegistry.registerTileEntity(TileEntityIMS.class, new ResourceLocation("securitycraft:ims"));
		GameRegistry.registerTileEntity(TileEntityProtecto.class, new ResourceLocation("securitycraft:protecto"));
		GameRegistry.registerTileEntity(CustomizableSCTE.class, new ResourceLocation("securitycraft:customizable"));
		GameRegistry.registerTileEntity(TileEntityScannerDoor.class, new ResourceLocation("securitycraft:scanner_door"));
		GameRegistry.registerTileEntity(TileEntitySecretSign.class, new ResourceLocation("securitycraft:secret_sign"));
		GameRegistry.registerTileEntity(TileEntityMotionLight.class, new ResourceLocation("securitycraft:motion_light"));
		GameRegistry.registerTileEntity(TileEntityTrackMine.class, new ResourceLocation("securitycraft:track_mine"));
		GameRegistry.registerTileEntity(TileEntityTrophySystem.class, new ResourceLocation("securitycraft:trophy_system"));
		GameRegistry.registerTileEntity(TileEntityBlockPocketManager.class, new ResourceLocation("securitycraft:block_pocket_manager"));
		GameRegistry.registerTileEntity(TileEntityBlockPocket.class, new ResourceLocation("securitycraft:block_pocket"));
		GameRegistry.registerTileEntity(TileEntityAllowlistOnly.class, new ResourceLocation("securitycraft:reinforced_pressure_plate"));
		GameRegistry.registerTileEntity(TileEntityReinforcedHopper.class, new ResourceLocation("securitycraft:reinforced_hopper"));
		GameRegistry.registerTileEntity(TileEntityProjector.class, new ResourceLocation("securitycraft:projector"));
		GameRegistry.registerTileEntity(TileEntityIronFence.class, new ResourceLocation("securitycraft:iron_fence"));
		GameRegistry.registerTileEntity(TileEntityKeypadDoor.class, new ResourceLocation("securitycraft:keypad_door"));
		GameRegistry.registerTileEntity(TileEntityReinforcedIronBars.class, new ResourceLocation("securitycraft:reinforced_iron_bars"));
		GameRegistry.registerTileEntity(TileEntityReinforcedCauldron.class, new ResourceLocation("securitycraft:reinforced_cauldron"));
		GameRegistry.registerTileEntity(TileEntityReinforcedPiston.class, new ResourceLocation("securitycraft:reinforced_piston"));
		GameRegistry.registerTileEntity(TileEntityValidationOwnable.class, new ResourceLocation("securitycraft:validation_ownable"));
		GameRegistry.registerTileEntity(TileEntityKeyPanel.class, new ResourceLocation("securitycraft:key_panel"));
		GameRegistry.registerTileEntity(TileEntitySonicSecuritySystem.class, new ResourceLocation("securitycraft:sonic_security_system"));
		GameRegistry.registerTileEntity(TileEntityReinforcedDoor.class, new ResourceLocation("securitycraft:reinforced_door"));
		GameRegistry.registerTileEntity(TileEntityBlockChangeDetector.class, new ResourceLocation("securitycraft:block_change_detector"));
		GameRegistry.registerTileEntity(TileEntityDisguisable.class, new ResourceLocation("securitycraft:disguisable"));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		//@formatter:off
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "bouncingbetty"), 0)
				.entity(EntityBouncingBetty.class)
				.name("BBetty")
				.tracker(128, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "imsbomb"), 3)
				.entity(EntityIMSBomb.class)
				.name("IMSBomb")
				.tracker(256, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "securitycamera"), 4)
				.entity(EntitySecurityCamera.class)
				.name("SecurityCamera")
				.tracker(256, 20, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "sentry"), 5)
				.entity(EntitySentry.class)
				.name("Sentry")
				.tracker(256, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "bullet"), 6)
				.entity(EntityBullet.class)
				.name("SentryBullet")
				.tracker(256, 1, true).build());
		//@formatter:on
	}

	public static void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(SetCameraPowered.Handler.class, SetCameraPowered.class, 1, Side.SERVER);
		network.registerMessage(SyncKeycardSettings.Handler.class, SyncKeycardSettings.class, 3, Side.SERVER);
		network.registerMessage(UpdateLogger.Handler.class, UpdateLogger.class, 4, Side.CLIENT);
		network.registerMessage(UpdateNBTTagOnClient.Handler.class, UpdateNBTTagOnClient.class, 5, Side.CLIENT);
		network.registerMessage(UpdateNBTTagOnServer.Handler.class, UpdateNBTTagOnServer.class, 6, Side.SERVER);
		network.registerMessage(RemoteControlMine.Handler.class, RemoteControlMine.class, 8, Side.SERVER);
		network.registerMessage(GiveNightVision.Handler.class, GiveNightVision.class, 9, Side.SERVER);
		network.registerMessage(SetPassword.Handler.class, SetPassword.class, 12, Side.SERVER);
		network.registerMessage(CheckPassword.Handler.class, CheckPassword.class, 13, Side.SERVER);
		network.registerMessage(SyncTENBTTag.Handler.class, SyncTENBTTag.class, 14, Side.SERVER);
		network.registerMessage(MountCamera.Handler.class, MountCamera.class, 15, Side.SERVER);
		network.registerMessage(OpenBriefcaseGui.Handler.class, OpenBriefcaseGui.class, 18, Side.SERVER);
		network.registerMessage(ToggleOption.Handler.class, ToggleOption.class, 19, Side.SERVER);
		network.registerMessage(UpdateSliderValue.Handler.class, UpdateSliderValue.class, 22, Side.SERVER);
		network.registerMessage(RemoveCameraTag.Handler.class, RemoveCameraTag.class, 23, Side.SERVER);
		network.registerMessage(InitSentryAnimation.Handler.class, InitSentryAnimation.class, 24, Side.CLIENT);
		network.registerMessage(ToggleBlockPocketManager.Handler.class, ToggleBlockPocketManager.class, 25, Side.SERVER);
		network.registerMessage(ClearLoggerServer.Handler.class, ClearLoggerServer.class, 27, Side.SERVER);
		network.registerMessage(RefreshDiguisedModel.Handler.class, RefreshDiguisedModel.class, 28, Side.CLIENT);
		network.registerMessage(SetSentryMode.Handler.class, SetSentryMode.class, 29, Side.SERVER);
		network.registerMessage(AssembleBlockPocket.Handler.class, AssembleBlockPocket.class, 30, Side.SERVER);
		network.registerMessage(SyncProjector.Handler.class, SyncProjector.class, 31, Side.SERVER);
		network.registerMessage(SyncBlockPocketManager.Handler.class, SyncBlockPocketManager.class, 32, Side.SERVER);
		network.registerMessage(SyncTrophySystem.Handler.class, SyncTrophySystem.class, 33, Side.SERVER);
		network.registerMessage(SetTrophySystemTarget.Handler.class, SetTrophySystemTarget.class, 34, Side.CLIENT);
		network.registerMessage(SetKeycardUses.Handler.class, SetKeycardUses.class, 35, Side.SERVER);
		network.registerMessage(SetCameraView.Handler.class, SetCameraView.class, 36, Side.CLIENT);
		network.registerMessage(DismountCamera.Handler.class, DismountCamera.class, 37, Side.SERVER);
		network.registerMessage(SyncSSSSettingsOnServer.Handler.class, SyncSSSSettingsOnServer.class, 38, Side.SERVER);
		network.registerMessage(ClearChangeDetectorServer.Handler.class, ClearChangeDetectorServer.class, 39, Side.SERVER);
		network.registerMessage(SyncBlockChangeDetector.Handler.class, SyncBlockChangeDetector.class, 40, Side.SERVER);
		network.registerMessage(ToggleModule.Handler.class, ToggleModule.class, 41, Side.SERVER);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		for (int i = 0; i < SCSounds.values().length; i++) {
			event.getRegistry().register(SCSounds.values()[i].event);
		}
	}

	@SubscribeEvent
	public static void registerDataSerializerEntries(RegistryEvent.Register<DataSerializerEntry> event) {
		event.getRegistry().register(new DataSerializerEntry(new DataSerializer<Owner>() {
			@Override
			public void write(PacketBuffer buf, Owner value) {
				ByteBufUtils.writeUTF8String(buf, value.getName());
				ByteBufUtils.writeUTF8String(buf, value.getUUID());
			}

			@Override
			public Owner read(PacketBuffer buf) throws IOException {
				String name = ByteBufUtils.readUTF8String(buf);
				String uuid = ByteBufUtils.readUTF8String(buf);

				return new Owner(name, uuid);
			}

			@Override
			public DataParameter<Owner> createKey(int id) {
				return new DataParameter<>(id, this);
			}

			@Override
			public Owner copyValue(Owner value) {
				return new Owner(value.getName(), value.getUUID());
			}
		}).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "owner")));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().register(new DyeBriefcaseRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "dye_briefcase")));
		event.getRegistry().register(new LimitedUseKeycardRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_conversion")));
		registerFakeLiquidRecipes(new ItemStack(Items.WATER_BUCKET), PotionTypes.HARMING, PotionTypes.STRONG_HARMING, new ItemStack(SCContent.fWaterBucket));
		registerFakeLiquidRecipes(new ItemStack(Items.LAVA_BUCKET), PotionTypes.HEALING, PotionTypes.STRONG_HEALING, new ItemStack(SCContent.fLavaBucket));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerResourceLocations(ModelRegistryEvent event) {
		//blocks
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.keypad), 0, new ModelResourceLocation("securitycraft:keypad", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.frame), 0, new ModelResourceLocation("securitycraft:keypad_frame", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 0, new ModelResourceLocation("securitycraft:reinforced_stone_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 1, new ModelResourceLocation("securitycraft:reinforced_stone_granite", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 2, new ModelResourceLocation("securitycraft:reinforced_stone_smooth_granite", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 3, new ModelResourceLocation("securitycraft:reinforced_stone_diorite", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 4, new ModelResourceLocation("securitycraft:reinforced_stone_smooth_diorite", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 5, new ModelResourceLocation("securitycraft:reinforced_stone_andesite", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStone), 6, new ModelResourceLocation("securitycraft:reinforced_stone_smooth_andesite", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.laserBlock), 0, new ModelResourceLocation("securitycraft:laser_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.laserField), 0, new ModelResourceLocation("securitycraft:laser", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.keypadChest), 0, new ModelResourceLocation("securitycraft:keypad_chest", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedDoor), 0, new ModelResourceLocation("securitycraft:reinforced_iron_door", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedIronTrapdoor), 0, new ModelResourceLocation("securitycraft:reinforced_iron_trapdoor", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.keycardReader), 0, new ModelResourceLocation("securitycraft:keycard_reader", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.inventoryScanner), 0, new ModelResourceLocation("securitycraft:inventory_scanner", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.cageTrap), 0, new ModelResourceLocation("securitycraft:cage_trap", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.inventoryScannerField), 0, new ModelResourceLocation("securitycraft:inventory_scanner_field", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.retinalScanner), 0, new ModelResourceLocation("securitycraft:retinal_scanner", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedIronBars), 0, new ModelResourceLocation("securitycraft:reinforced_iron_bars", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.portableRadar), 0, new ModelResourceLocation("securitycraft:portable_radar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.alarm), 0, new ModelResourceLocation("securitycraft:alarm", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.alarmLit), 0, new ModelResourceLocation("securitycraft:alarm_lit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.usernameLogger), 0, new ModelResourceLocation("securitycraft:username_logger", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedFencegate), 0, new ModelResourceLocation("securitycraft:reinforced_fence_gate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.ironFence), 0, new ModelResourceLocation("securitycraft:electrified_iron_fence", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 0, new ModelResourceLocation("securitycraft:reinforced_planks_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 1, new ModelResourceLocation("securitycraft:reinforced_planks_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 2, new ModelResourceLocation("securitycraft:reinforced_planks_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 3, new ModelResourceLocation("securitycraft:reinforced_planks_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 4, new ModelResourceLocation("securitycraft:reinforced_planks_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 5, new ModelResourceLocation("securitycraft:reinforced_planks_dark_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsStone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_stone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsOak), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsSpruce), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsBirch), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsJungle), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsAcacia), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsDarkoak), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_darkoak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedGlass), 0, new ModelResourceLocation("securitycraft:reinforced_glass_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 0, new ModelResourceLocation("securitycraft:reinforced_stained_glass_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 1, new ModelResourceLocation("securitycraft:reinforced_stained_glass_orange", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 2, new ModelResourceLocation("securitycraft:reinforced_stained_glass_magenta", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 3, new ModelResourceLocation("securitycraft:reinforced_stained_glass_light_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 4, new ModelResourceLocation("securitycraft:reinforced_stained_glass_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 5, new ModelResourceLocation("securitycraft:reinforced_stained_glass_lime", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 6, new ModelResourceLocation("securitycraft:reinforced_stained_glass_pink", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 7, new ModelResourceLocation("securitycraft:reinforced_stained_glass_gray", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 8, new ModelResourceLocation("securitycraft:reinforced_stained_glass_silver", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 9, new ModelResourceLocation("securitycraft:reinforced_stained_glass_cyan", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 10, new ModelResourceLocation("securitycraft:reinforced_stained_glass_purple", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 11, new ModelResourceLocation("securitycraft:reinforced_stained_glass_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 12, new ModelResourceLocation("securitycraft:reinforced_stained_glass_brown", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 13, new ModelResourceLocation("securitycraft:reinforced_stained_glass_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 14, new ModelResourceLocation("securitycraft:reinforced_stained_glass_red", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 15, new ModelResourceLocation("securitycraft:reinforced_stained_glass_black", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.keypadChest), 0, new ModelResourceLocation("securitycraft:keypad_chest", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.keypadFurnace), 0, new ModelResourceLocation("securitycraft:keypad_furnace", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.panicButton), 0, new ModelResourceLocation("securitycraft:panic_button", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.securityCamera), 0, new ModelResourceLocation("securitycraft:security_camera", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedDirt), 0, new ModelResourceLocation("securitycraft:reinforced_dirt", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedDirt), 1, new ModelResourceLocation("securitycraft:reinforced_coarse_dirt", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedDirt), 2, new ModelResourceLocation("securitycraft:reinforced_podzol", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_sandstone_normal", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSandstone), 1, new ModelResourceLocation("securitycraft:reinforced_sandstone_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSandstone), 2, new ModelResourceLocation("securitycraft:reinforced_sandstone_smooth", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 0, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 1, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 2, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 3, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 4, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 5, new ModelResourceLocation("securitycraft:reinforced_wood_slabs_dark_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 0, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_stone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 1, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 2, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 3, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_stonebrick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 4, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 5, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_netherbrick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 6, new ModelResourceLocation("securitycraft:reinforced_stone_slabs_quartz", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs2), 0, new ModelResourceLocation("securitycraft:reinforced_stone_slabs2_red_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs2), 1, new ModelResourceLocation("securitycraft:reinforced_stone_slabs2_purpur", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.protecto), 0, new ModelResourceLocation("securitycraft:protecto", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.scannerDoor), 0, new ModelResourceLocation("securitycraft:scanner_door", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stone_brick_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 1, new ModelResourceLocation("securitycraft:reinforced_stone_brick_mossy", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 2, new ModelResourceLocation("securitycraft:reinforced_stone_brick_cracked", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 3, new ModelResourceLocation("securitycraft:reinforced_stone_brick_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_stone_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMossyCobblestone), 0, new ModelResourceLocation("securitycraft:reinforced_mossy_cobblestone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedBrick), 0, new ModelResourceLocation("securitycraft:reinforced_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforced_nether_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_nether_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforced_hardened_clay", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 1, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_orange", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 2, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_magenta", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 3, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_light_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 4, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 5, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_lime", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 6, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_pink", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 7, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_gray", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 8, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_silver", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 9, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_cyan", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 10, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_purple", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 11, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 12, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_brown", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 13, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 14, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_red", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 15, new ModelResourceLocation("securitycraft:reinforced_stained_hardened_clay_black", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 0, new ModelResourceLocation("securitycraft:reinforced_logs_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 1, new ModelResourceLocation("securitycraft:reinforced_logs_spruce", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 2, new ModelResourceLocation("securitycraft:reinforced_logs_birch", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 3, new ModelResourceLocation("securitycraft:reinforced_logs_jungle", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedNewLogs), 0, new ModelResourceLocation("securitycraft:reinforced_logs2_acacia", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedNewLogs), 1, new ModelResourceLocation("securitycraft:reinforced_logs2_big_oak", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 0, new ModelResourceLocation("securitycraft:reinforced_metals_gold", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 1, new ModelResourceLocation("securitycraft:reinforced_metals_iron", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 2, new ModelResourceLocation("securitycraft:reinforced_metals_diamond", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 3, new ModelResourceLocation("securitycraft:reinforced_metals_emerald", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 4, new ModelResourceLocation("securitycraft:reinforced_metals_redstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCompressedBlocks), 0, new ModelResourceLocation("securitycraft:reinforced_compressed_blocks_lapis", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCompressedBlocks), 1, new ModelResourceLocation("securitycraft:reinforced_compressed_blocks_coal", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 0, new ModelResourceLocation("securitycraft:reinforced_wool_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 1, new ModelResourceLocation("securitycraft:reinforced_wool_orange", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 2, new ModelResourceLocation("securitycraft:reinforced_wool_magenta", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 3, new ModelResourceLocation("securitycraft:reinforced_wool_light_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 4, new ModelResourceLocation("securitycraft:reinforced_wool_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 5, new ModelResourceLocation("securitycraft:reinforced_wool_lime", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 6, new ModelResourceLocation("securitycraft:reinforced_wool_pink", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 7, new ModelResourceLocation("securitycraft:reinforced_wool_gray", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 8, new ModelResourceLocation("securitycraft:reinforced_wool_silver", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 9, new ModelResourceLocation("securitycraft:reinforced_wool_cyan", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 10, new ModelResourceLocation("securitycraft:reinforced_wool_purple", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 11, new ModelResourceLocation("securitycraft:reinforced_wool_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 12, new ModelResourceLocation("securitycraft:reinforced_wool_brown", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 13, new ModelResourceLocation("securitycraft:reinforced_wool_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 14, new ModelResourceLocation("securitycraft:reinforced_wool_red", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWool), 15, new ModelResourceLocation("securitycraft:reinforced_wool_black", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedQuartz), 0, new ModelResourceLocation("securitycraft:reinforced_quartz_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedQuartz), 1, new ModelResourceLocation("securitycraft:reinforced_quartz_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedQuartz), 2, new ModelResourceLocation("securitycraft:reinforced_quartz_pillar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsQuartz), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_quartz", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPrismarine), 0, new ModelResourceLocation("securitycraft:reinforced_prismarine_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPrismarine), 1, new ModelResourceLocation("securitycraft:reinforced_prismarine_bricks", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPrismarine), 2, new ModelResourceLocation("securitycraft:reinforced_prismarine_dark", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_red_sandstone_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedRedSandstone), 1, new ModelResourceLocation("securitycraft:reinforced_red_sandstone_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedRedSandstone), 2, new ModelResourceLocation("securitycraft:reinforced_red_sandstone_smooth", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_red_sandstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedEndStoneBricks), 0, new ModelResourceLocation("securitycraft:reinforced_end_stone_bricks", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedRedNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforced_red_nether_brick", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPurpur), 0, new ModelResourceLocation("securitycraft:reinforced_purpur_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPurpur), 1, new ModelResourceLocation("securitycraft:reinforced_purpur_pillar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsPurpur), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_purpur", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 0, new ModelResourceLocation("securitycraft:reinforced_concrete_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 1, new ModelResourceLocation("securitycraft:reinforced_concrete_orange", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 2, new ModelResourceLocation("securitycraft:reinforced_concrete_magenta", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 3, new ModelResourceLocation("securitycraft:reinforced_concrete_light_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 4, new ModelResourceLocation("securitycraft:reinforced_concrete_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 5, new ModelResourceLocation("securitycraft:reinforced_concrete_lime", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 6, new ModelResourceLocation("securitycraft:reinforced_concrete_pink", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 7, new ModelResourceLocation("securitycraft:reinforced_concrete_gray", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 8, new ModelResourceLocation("securitycraft:reinforced_concrete_silver", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 9, new ModelResourceLocation("securitycraft:reinforced_concrete_cyan", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 10, new ModelResourceLocation("securitycraft:reinforced_concrete_purple", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 11, new ModelResourceLocation("securitycraft:reinforced_concrete_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 12, new ModelResourceLocation("securitycraft:reinforced_concrete_brown", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 13, new ModelResourceLocation("securitycraft:reinforced_concrete_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 14, new ModelResourceLocation("securitycraft:reinforced_concrete_red", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedConcrete), 15, new ModelResourceLocation("securitycraft:reinforced_concrete_black", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.motionActivatedLight), 0, new ModelResourceLocation("securitycraft:motion_activated_light", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedObsidian), 0, new ModelResourceLocation("securitycraft:reinforced_obsidian", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedNetherrack), 0, new ModelResourceLocation("securitycraft:reinforced_netherrack", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedEndStone), 0, new ModelResourceLocation("securitycraft:reinforced_end_stone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSeaLantern), 0, new ModelResourceLocation("securitycraft:reinforced_sea_lantern", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedBoneBlock), 0, new ModelResourceLocation("securitycraft:reinforced_bone_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedGlassPane), 0, new ModelResourceLocation("securitycraft:reinforced_glass_pane", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 0, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 1, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_orange", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 2, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_magenta", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 3, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_light_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 4, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 5, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_lime", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 6, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_pink", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 7, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_gray", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 8, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_silver", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 9, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_cyan", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 10, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_purple", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 11, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 12, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_brown", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 13, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 14, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_red", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 15, new ModelResourceLocation("securitycraft:reinforced_stained_glass_panes_black", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 0, new ModelResourceLocation("securitycraft:reinforced_carpet_white", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 1, new ModelResourceLocation("securitycraft:reinforced_carpet_orange", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 2, new ModelResourceLocation("securitycraft:reinforced_carpet_magenta", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 3, new ModelResourceLocation("securitycraft:reinforced_carpet_light_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 4, new ModelResourceLocation("securitycraft:reinforced_carpet_yellow", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 5, new ModelResourceLocation("securitycraft:reinforced_carpet_lime", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 6, new ModelResourceLocation("securitycraft:reinforced_carpet_pink", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 7, new ModelResourceLocation("securitycraft:reinforced_carpet_gray", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 8, new ModelResourceLocation("securitycraft:reinforced_carpet_silver", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 9, new ModelResourceLocation("securitycraft:reinforced_carpet_cyan", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 10, new ModelResourceLocation("securitycraft:reinforced_carpet_purple", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 11, new ModelResourceLocation("securitycraft:reinforced_carpet_blue", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 12, new ModelResourceLocation("securitycraft:reinforced_carpet_brown", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 13, new ModelResourceLocation("securitycraft:reinforced_carpet_green", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 14, new ModelResourceLocation("securitycraft:reinforced_carpet_red", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCarpet), 15, new ModelResourceLocation("securitycraft:reinforced_carpet_black", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedGlowstone), 0, new ModelResourceLocation("securitycraft:reinforced_glowstone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSand), 0, new ModelResourceLocation("securitycraft:reinforced_sand", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSand), 1, new ModelResourceLocation("securitycraft:reinforced_red_sand", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedGravel), 0, new ModelResourceLocation("securitycraft:reinforced_gravel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.trophySystem), 0, new ModelResourceLocation("securitycraft:trophy_system", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.crystalQuartz), 0, new ModelResourceLocation("securitycraft:crystal_quartz_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.crystalQuartz), 1, new ModelResourceLocation("securitycraft:crystal_quartz_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.crystalQuartz), 2, new ModelResourceLocation("securitycraft:crystal_quartz_pillar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCrystalQuartz), 0, new ModelResourceLocation("securitycraft:reinforced_crystal_quartz_default", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCrystalQuartz), 1, new ModelResourceLocation("securitycraft:reinforced_crystal_quartz_chiseled", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCrystalQuartz), 2, new ModelResourceLocation("securitycraft:reinforced_crystal_quartz_pillar", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.crystalQuartzSlab), 0, new ModelResourceLocation("securitycraft:crystal_quartz_slab", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCrystalQuartzSlab), 0, new ModelResourceLocation("securitycraft:reinforced_crystal_quartz_slab", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.stairsCrystalQuartz), 0, new ModelResourceLocation("securitycraft:stairs_crystal_quartz", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStairsCrystalQuartz), 0, new ModelResourceLocation("securitycraft:reinforced_stairs_crystal_quartz", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.blockPocketWall), 0, new ModelResourceLocation("securitycraft:block_pocket_wall", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.blockPocketManager), 0, new ModelResourceLocation("securitycraft:block_pocket_manager", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStonePressurePlate), 0, new ModelResourceLocation("securitycraft:reinforced_stone_pressure_plate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodenPressurePlate), 0, new ModelResourceLocation("securitycraft:reinforced_wooden_pressure_plate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedBookshelf), 0, new ModelResourceLocation("securitycraft:reinforced_bookshelf", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWalls), 0, new ModelResourceLocation("securitycraft:reinforced_cobblestone_wall", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWalls), 1, new ModelResourceLocation("securitycraft:reinforced_mossy_cobblestone_wall", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStickyPiston), 0, new ModelResourceLocation("securitycraft:reinforced_sticky_piston", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPiston), 0, new ModelResourceLocation("securitycraft:reinforced_piston", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedObserver), 0, new ModelResourceLocation("securitycraft:reinforced_observer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedRedstoneLamp), 0, new ModelResourceLocation("securitycraft:reinforced_redstone_lamp", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCobweb), 0, new ModelResourceLocation("securitycraft:reinforced_cobweb", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedGrass), 0, new ModelResourceLocation("securitycraft:reinforced_grass_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedSnowBlock), 0, new ModelResourceLocation("securitycraft:reinforced_snow_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedIce), 0, new ModelResourceLocation("securitycraft:reinforced_ice", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedPackedIce), 0, new ModelResourceLocation("securitycraft:reinforced_packed_ice", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMycelium), 0, new ModelResourceLocation("securitycraft:reinforced_mycelium", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedClay), 0, new ModelResourceLocation("securitycraft:reinforced_clay", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedNetherWartBlock), 0, new ModelResourceLocation("securitycraft:reinforced_nether_wart_block", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedGrassPath), 0, new ModelResourceLocation("securitycraft:reinforced_grass_path", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedStoneButton), 0, new ModelResourceLocation("securitycraft:reinforced_stone_button", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedWoodenButton), 0, new ModelResourceLocation("securitycraft:reinforced_wooden_button", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedLever), 0, new ModelResourceLocation("securitycraft:reinforced_lever", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedHopper), 0, new ModelResourceLocation("securitycraft:reinforced_hopper", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.projector), 0, new ModelResourceLocation("securitycraft:projector", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.keypadDoor), 0, new ModelResourceLocation("securitycraft:keypad_door", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedCauldron), 0, new ModelResourceLocation("securitycraft:reinforced_cauldron", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.blockChangeDetector), 0, new ModelResourceLocation("securitycraft:block_change_detector", "inventory"));

		//items
		ModelLoader.setCustomModelResourceLocation(SCContent.codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remote_access_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.remoteAccessSentry, 0, new ModelResourceLocation("securitycraft:remote_access_sentry", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.reinforcedDoorItem, 0, new ModelResourceLocation("securitycraft:door_indestructible_iron_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.fWaterBucket, 0, new ModelResourceLocation("securitycraft:bucket_f_water", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.fLavaBucket, 0, new ModelResourceLocation("securitycraft:bucket_f_lava", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLvl1, 0, new ModelResourceLocation("securitycraft:keycard_lv1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLvl2, 0, new ModelResourceLocation("securitycraft:keycard_lv2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLvl3, 0, new ModelResourceLocation("securitycraft:keycard_lv3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLvl4, 0, new ModelResourceLocation("securitycraft:keycard_lv4", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLvl5, 0, new ModelResourceLocation("securitycraft:keycard_lv5", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.limitedUseKeycard, 0, new ModelResourceLocation("securitycraft:limited_use_keycard", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockRemover, 0, new ModelResourceLocation("securitycraft:universal_block_remover", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockModifier, 0, new ModelResourceLocation("securitycraft:universal_block_modifier", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.allowlistModule, 0, new ModelResourceLocation("securitycraft:whitelist_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.denylistModule, 0, new ModelResourceLocation("securitycraft:blacklist_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.redstoneModule, 0, new ModelResourceLocation("securitycraft:redstone_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.harmingModule, 0, new ModelResourceLocation("securitycraft:harming_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.storageModule, 0, new ModelResourceLocation("securitycraft:storage_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.smartModule, 0, new ModelResourceLocation("securitycraft:smart_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.disguiseModule, 0, new ModelResourceLocation("securitycraft:disguise_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.speedModule, 0, new ModelResourceLocation("securitycraft:speed_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.wireCutters, 0, new ModelResourceLocation("securitycraft:wire_cutters", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keyPanel, 0, new ModelResourceLocation("securitycraft:keypad_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.adminTool, 0, new ModelResourceLocation("securitycraft:admin_tool", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.cameraMonitor, 0, new ModelResourceLocation("securitycraft:camera_monitor", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.scManual, 0, new ModelResourceLocation("securitycraft:sc_manual", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.taser, 0, new ModelResourceLocation("securitycraft:taser", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.taserPowered, 0, new ModelResourceLocation("securitycraft:taser_powered", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalOwnerChanger, 0, new ModelResourceLocation("securitycraft:universal_owner_changer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockReinforcerLvL1, 0, new ModelResourceLocation("securitycraft:universal_block_reinforcer_lvl1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockReinforcerLvL2, 0, new ModelResourceLocation("securitycraft:universal_block_reinforcer_lvl2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockReinforcerLvL3, 0, new ModelResourceLocation("securitycraft:universal_block_reinforcer_lvl3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.briefcase, 0, new ModelResourceLocation("securitycraft:briefcase", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalKeyChanger, 0, new ModelResourceLocation("securitycraft:universal_key_changer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.scannerDoorItem, 0, new ModelResourceLocation("securitycraft:scanner_door_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.secretSignItem, 0, new ModelResourceLocation("securitycraft:secret_sign_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.sentry, 0, new ModelResourceLocation("securitycraft:sentry", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.crystalQuartzItem, 0, new ModelResourceLocation("securitycraft:crystal_quartz_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keypadDoorItem, 0, new ModelResourceLocation("securitycraft:keypad_door_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.sonicSecuritySystemItem, 0, new ModelResourceLocation("securitycraft:sonic_security_system", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.portableTunePlayer, 0, new ModelResourceLocation("securitycraft:portable_tune_player", "inventory"));

		//mines
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.mine), 0, new ModelResourceLocation("securitycraft:mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.dirtMine), 0, new ModelResourceLocation("securitycraft:dirt_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.stoneMine), 0, new ModelResourceLocation("securitycraft:stone_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.cobblestoneMine), 0, new ModelResourceLocation("securitycraft:cobblestone_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.sandMine), 0, new ModelResourceLocation("securitycraft:sand_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.diamondOreMine), 0, new ModelResourceLocation("securitycraft:diamond_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.furnaceMine), 0, new ModelResourceLocation("securitycraft:furnace_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.trackMine), 0, new ModelResourceLocation("securitycraft:track_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.bouncingBetty), 0, new ModelResourceLocation("securitycraft:bouncing_betty", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.claymore), 0, new ModelResourceLocation("securitycraft:claymore", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.ims), 0, new ModelResourceLocation("securitycraft:ims", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.gravelMine), 0, new ModelResourceLocation("securitycraft:gravel_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.coalOreMine), 0, new ModelResourceLocation("securitycraft:coal_ore_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.emeraldOreMine), 0, new ModelResourceLocation("securitycraft:emerald_ore_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.goldOreMine), 0, new ModelResourceLocation("securitycraft:gold_ore_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.ironOreMine), 0, new ModelResourceLocation("securitycraft:iron_ore_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.lapisOreMine), 0, new ModelResourceLocation("securitycraft:lapis_ore_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.quartzOreMine), 0, new ModelResourceLocation("securitycraft:quartz_ore_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.redstoneOreMine), 0, new ModelResourceLocation("securitycraft:redstone_ore_mine", "inventory"));
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 *
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, String designedBy) {
		registerBlock(event, block, new ItemBlock(block), designedBy);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 *
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block) {
		registerBlock(event, block, new ItemBlock(block), PageType.SINGLE_ITEM);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 *
	 * @param block The block to register
	 */
	private static void registerBlockMine(RegistryEvent.Register<Block> event, Block block) {
		registerBlock(event, block, new ItemBlock(block), PageType.BLOCK_MINES);
		blockConfigValues.put(block, ConfigHandler.ableToBreakMines);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this block's recipe show as disabled in the manual.
	 *
	 * @param block The block to register
	 * @param configValue The config value
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, boolean configValue) {
		registerBlock(event, block, new ItemBlock(block), PageType.SINGLE_ITEM);
		blockConfigValues.put(block, configValue);
	}

	/**
	 * Registers a block and its ItemBlock
	 *
	 * @param block The Block to register
	 * @param pageType The type of the manual page from this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, PageType pageGroup) {
		registerBlock(event, block, new ItemBlock(block), pageGroup);
	}

	/**
	 * Registers a block with a custom ItemBlock
	 *
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param pageType The type of the manual page from this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ItemBlock itemBlock, PageType pageType) {
		event.getRegistry().register(block);

		if (itemBlock != null)
			itemBlocks.add(itemBlock.setRegistryName(block.getRegistryName().toString()));

		if (pageType == PageType.SINGLE_ITEM)
			blockPages.add(block);
		else if (pageType != PageType.NO_PAGE) {
			if (!pageTypeBlocks.containsKey(pageType))
				pageTypeBlocks.put(pageType, new ArrayList<>());

			pageTypeBlocks.get(pageType).add(block);
		}
	}

	/**
	 * Registers a block with a custom ItemBlock
	 *
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param designedBy The name of the person who designed this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ItemBlock itemBlock, String designedBy) {
		event.getRegistry().register(block);

		if (itemBlock != null)
			itemBlocks.add(itemBlock.setRegistryName(block.getRegistryName().toString()));

		blockPages.add(block);

		if (designedBy != null)
			blocksDesignedBy.put(block, designedBy);
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item) {
		registerItem(event, item, PageType.SINGLE_ITEM, true, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item. Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageType pageType, boolean configValue) {
		registerItem(event, item, pageType, configValue, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageType pageType) {
		registerItem(event, item, pageType, true, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item. Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageType pageType, boolean configValue, String designedBy) {
		event.getRegistry().register(item);

		if (pageType == PageType.NO_PAGE)
			return;

		if (pageType != PageType.SINGLE_ITEM) {
			if (!pageTypeStacks.containsKey(pageType))
				pageTypeStacks.put(pageType, new ArrayList<>());

			if (item != null && item.getHasSubtypes()) {
				NonNullList<ItemStack> subStacks = NonNullList.create();

				item.getSubItems(item.getCreativeTab(), subStacks);
				pageTypeStacks.get(pageType).addAll(subStacks);
			}
			else
				pageTypeStacks.get(pageType).add(new ItemStack(item));
		}
		else {
			TextComponentTranslation title = Utils.localize(item);
			TextComponentTranslation helpInfo = Utils.localize("help." + item.getTranslationKey().substring(5) + ".info");

			ItemSCManual.PAGES.add(new SCManualPage(item, pageType, title, helpInfo, designedBy, false, configValue));
		}
	}

	private static void registerFakeLiquidRecipes(ItemStack input, PotionType normalPotion, PotionType strongPotion, ItemStack output) {
		ItemStack normalPotionStack = new ItemStack(Items.POTIONITEM);
		ItemStack strongPotionStack = new ItemStack(Items.POTIONITEM);
		ItemStack normalSplashPotionStack = new ItemStack(Items.SPLASH_POTION);
		ItemStack strongSplashPotionStack = new ItemStack(Items.SPLASH_POTION);
		ItemStack normalLingeringPotionStack = new ItemStack(Items.LINGERING_POTION);
		ItemStack strongLingeringPotionStack = new ItemStack(Items.LINGERING_POTION);
		NBTTagCompound normalNBT = new NBTTagCompound();
		NBTTagCompound strongNBT = new NBTTagCompound();
		normalNBT.setString("Potion", normalPotion.getRegistryName().toString());
		strongNBT.setString("Potion", strongPotion.getRegistryName().toString());
		normalPotionStack.setTagCompound(normalNBT.copy());
		strongPotionStack.setTagCompound(strongNBT.copy());
		normalSplashPotionStack.setTagCompound(normalNBT.copy());
		strongSplashPotionStack.setTagCompound(strongNBT.copy());
		normalLingeringPotionStack.setTagCompound(normalNBT.copy());
		strongLingeringPotionStack.setTagCompound(strongNBT.copy());

		BrewingRecipeRegistry.addRecipe(input, normalPotionStack, output);
		BrewingRecipeRegistry.addRecipe(input, strongPotionStack, output);
		BrewingRecipeRegistry.addRecipe(input, normalSplashPotionStack, output);
		BrewingRecipeRegistry.addRecipe(input, strongSplashPotionStack, output);
		BrewingRecipeRegistry.addRecipe(input, normalLingeringPotionStack, output);
		BrewingRecipeRegistry.addRecipe(input, strongLingeringPotionStack, output);
	}
}
