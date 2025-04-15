package net.geforcemods.securitycraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.NamedBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.FloorTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.IronFenceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ValidationOwnableBlockEntity;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
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
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.DyeItemRecipe;
import net.geforcemods.securitycraft.misc.IngredientBrewingRecipe;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.PartialNBTIngredient;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.network.client.InteractWithFrame;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.SpawnInterfaceHighlightParticle;
import net.geforcemods.securitycraft.network.client.UpdateLaserColors;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.client.UpdateTeamPrecedence;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RemoveMineFromMRAT;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.network.server.SyncFrame;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.recipe.CopyPositionComponentItemRecipe;
import net.geforcemods.securitycraft.recipe.LimitedUseKeycardRecipe;
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
import net.minecraftforge.common.crafting.CompoundIngredient;
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
	private static Map<PageGroup, List<Block>> pageTypeBlocks = new EnumMap<>(PageGroup.class);
	private static Map<PageGroup, List<ItemStack>> pageTypeStacks = new EnumMap<>(PageGroup.class);
	private static Map<Block, String> blocksDesignedBy = new HashMap<>();
	private static Map<Block, Supplier<Boolean>> blockConfigValues = new HashMap<>();
	private static final Supplier<Boolean> ABLE_TO_CRAFT_MINES = () -> ConfigHandler.ableToCraftMines;

	private RegistrationHandler() {}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		registerBlock(event, SCContent.laserBlock);
		event.getRegistry().register(SCContent.laserField);
		registerBlock(event, SCContent.keypad);
		registerBlock(event, SCContent.mine, ABLE_TO_CRAFT_MINES);
		event.getRegistry().register(SCContent.mineCut);
		registerBlock(event, SCContent.retinalScanner);
		event.getRegistry().register(SCContent.reinforcedDoor);
		registerBlock(event, SCContent.fakeLava, PageGroup.NO_PAGE);
		registerBlock(event, SCContent.bogusLavaFlowing, PageGroup.NO_PAGE);
		registerBlock(event, SCContent.fakeWater, PageGroup.NO_PAGE);
		registerBlock(event, SCContent.bogusWaterFlowing, PageGroup.NO_PAGE);
		registerBlock(event, SCContent.keycardReader);
		registerBlock(event, SCContent.reinforcedIronTrapdoor);
		registerBlock(event, SCContent.bouncingBetty, ABLE_TO_CRAFT_MINES);
		registerBlock(event, SCContent.inventoryScanner);
		event.getRegistry().register(SCContent.inventoryScannerField);
		registerBlock(event, SCContent.trackMine, ABLE_TO_CRAFT_MINES);
		registerBlock(event, SCContent.cageTrap);
		event.getRegistry().register(SCContent.horizontalReinforcedIronBars);
		registerBlock(event, SCContent.portableRadar);
		registerBlock(event, SCContent.reinforcedIronBars, PageGroup.REINFORCED);
		registerBlock(event, SCContent.keypadChest);
		registerBlock(event, SCContent.usernameLogger);
		registerBlock(event, SCContent.alarm);
		event.getRegistry().register(SCContent.alarmLit);
		registerBlock(event, SCContent.reinforcedStone, new ItemBlockReinforcedStone(SCContent.reinforcedStone), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedSandstone), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedDirt, new ItemBlockReinforcedDirt(SCContent.reinforcedDirt), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedCobblestone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.electrifiedIronFenceGate);
		registerBlock(event, SCContent.reinforcedWoodPlanks, new ItemBlockReinforcedPlanks(SCContent.reinforcedWoodPlanks), PageGroup.REINFORCED);
		registerBlock(event, SCContent.panicButton);
		registerBlock(event, SCContent.frame);
		registerBlock(event, SCContent.claymore, ABLE_TO_CRAFT_MINES);
		registerBlock(event, SCContent.keypadFurnace);
		registerBlock(event, SCContent.securityCamera);
		registerBlock(event, SCContent.reinforcedStairsOak, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsSpruce, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsCobblestone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsSandstone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsBirch, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsJungle, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsAcacia, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsDarkoak, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsStone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.electrifiedIronFence);
		registerBlock(event, SCContent.ims, ABLE_TO_CRAFT_MINES);
		registerBlock(event, SCContent.reinforcedGlass, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStainedGlass, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlass), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedWoodSlabs, new ItemBlockReinforcedWoodSlabs(SCContent.reinforcedWoodSlabs), PageGroup.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleWoodSlabs);
		registerBlock(event, SCContent.reinforcedStoneSlabs, new ItemBlockReinforcedSlabs(SCContent.reinforcedStoneSlabs), PageGroup.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs);
		registerBlock(event, SCContent.protecto);
		event.getRegistry().register(SCContent.scannerDoor);
		registerBlock(event, SCContent.reinforcedStoneBrick, new ItemBlockReinforcedStoneBrick(SCContent.reinforcedStoneBrick), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsStoneBrick, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedMossyCobblestone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedBrick, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsBrick, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherBrick, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsNetherBrick, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedHardenedClay, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStainedHardenedClay, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedHardenedClay), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedOldLogs, new ItemBlockReinforcedLog(SCContent.reinforcedOldLogs), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedNewLogs, new ItemBlockReinforcedLog(SCContent.reinforcedNewLogs), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedMetals, new ItemBlockReinforcedMetals(SCContent.reinforcedMetals), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedLapisAndCoalBlocks, new ItemBlockReinforcedCompressedBlocks(SCContent.reinforcedLapisAndCoalBlocks), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedWool, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedWool), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedQuartz, new ItemBlockCustomQuartz(SCContent.reinforcedQuartz), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsQuartz, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPrismarine, new ItemBlockReinforcedPrismarine(SCContent.reinforcedPrismarine), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedRedSandstone), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsRedSandstone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStoneSlabs2, new ItemBlockReinforcedSlabs2(SCContent.reinforcedStoneSlabs2), PageGroup.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs2);
		registerBlock(event, SCContent.reinforcedEndStoneBricks, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedNetherBrick, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPurpur, new ItemBlockReinforcedPurpur(SCContent.reinforcedPurpur), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStairsPurpur, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedConcrete, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedConcrete), PageGroup.REINFORCED);
		event.getRegistry().register(SCContent.secretSignWall);
		event.getRegistry().register(SCContent.secretSignStanding);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.reinforcedObsidian, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherrack, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedEndStone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedSeaLantern, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedBoneBlock, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGlassPane, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStainedGlassPanes, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlassPanes), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedCarpet, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedCarpet), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGlowstone, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedSand, new ItemBlockReinforcedSand(SCContent.reinforcedSand), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGravel, PageGroup.REINFORCED);
		registerBlock(event, SCContent.trophySystem);
		registerBlock(event, SCContent.crystalQuartz, new ItemBlockCustomQuartz(SCContent.crystalQuartz), PageGroup.SINGLE_ITEM);
		registerBlock(event, SCContent.reinforcedCrystalQuartz, new ItemBlockCustomQuartz(SCContent.reinforcedCrystalQuartz), PageGroup.REINFORCED);
		registerBlock(event, SCContent.crystalQuartzSlab, new ItemBlockCrystalQuartzSlab(SCContent.crystalQuartzSlab), PageGroup.NO_PAGE);
		event.getRegistry().register(SCContent.doubleCrystalQuartzSlab);
		registerBlock(event, SCContent.reinforcedCrystalQuartzSlab, new ItemBlockReinforcedCrystalQuartzSlab(SCContent.reinforcedCrystalQuartzSlab), PageGroup.REINFORCED);
		event.getRegistry().register(SCContent.reinforcedDoubleCrystalQuartzSlab);
		registerBlock(event, SCContent.stairsCrystalQuartz, PageGroup.NO_PAGE);
		registerBlock(event, SCContent.reinforcedStairsCrystalQuartz, PageGroup.REINFORCED);
		registerBlock(event, SCContent.blockPocketWall);
		registerBlock(event, SCContent.blockPocketManager, "Henzoid");
		registerBlock(event, SCContent.reinforcedStonePressurePlate, PageGroup.PRESSURE_PLATES);
		registerBlock(event, SCContent.reinforcedWoodenPressurePlate, PageGroup.PRESSURE_PLATES);
		registerBlock(event, SCContent.reinforcedBookshelf, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedWalls, new ItemBlockReinforcedWalls(SCContent.reinforcedWalls), PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStickyPiston, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPiston, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPistonHead, null, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedMovingPiston, null, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedObserver, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedstoneLamp, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedCobweb, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGrass, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedSnowBlock, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedIce, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPackedIce, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedMycelium, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedClay, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherWartBlock, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGrassPath, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedStoneButton, PageGroup.BUTTONS);
		registerBlock(event, SCContent.reinforcedWoodenButton, PageGroup.BUTTONS);
		registerBlock(event, SCContent.reinforcedLever);
		registerBlock(event, SCContent.reinforcedHopper);
		registerBlock(event, SCContent.projector);
		event.getRegistry().register(SCContent.keypadDoor);
		registerBlock(event, SCContent.reinforcedCauldron, PageGroup.REINFORCED);
		event.getRegistry().register(SCContent.keyPanelFloorCeilingBlock);
		event.getRegistry().register(SCContent.keyPanelWallBlock);
		registerBlock(event, SCContent.sonicSecuritySystem, (ItemBlock) SCContent.sonicSecuritySystemItem, PageGroup.SINGLE_ITEM);
		event.getRegistry().register(SCContent.blockChangeDetectorFloorCeiling);
		event.getRegistry().register(SCContent.blockChangeDetectorWall);
		event.getRegistry().register(SCContent.sentryDisguise);
		registerBlock(event, SCContent.reinforcedEndRod, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedWhiteGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedOrangeGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedMagentaGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedLightBlueGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedYellowGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedLimeGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPinkGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGrayGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedSilverGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedCyanGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedPurpleGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedBlueGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedBrownGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedGreenGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedRedGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedBlackGlazedTerracotta, PageGroup.REINFORCED);
		registerBlock(event, SCContent.riftStabilizer);
		registerBlock(event, SCContent.displayCase);
		registerBlock(event, SCContent.keypadTrapdoor);
		registerBlock(event, SCContent.reinforcedLadder, PageGroup.REINFORCED);
		registerBlock(event, SCContent.floorTrap);
		event.getRegistry().register(SCContent.keycardLockFloorCeilingBlock);
		event.getRegistry().register(SCContent.keycardLockWallBlock);
		registerBlock(event, SCContent.scannerTrapdoor);
		registerBlock(event, SCContent.reinforcedDispenser, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedDropper, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedOakFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedNetherBrickFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedSpruceFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedBirchFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedJungleFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedDarkOakFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedAcaciaFence, PageGroup.REINFORCED);
		registerBlock(event, SCContent.reinforcedOakFenceGate, PageGroup.FENCE_GATES);
		registerBlock(event, SCContent.reinforcedSpruceFenceGate, PageGroup.FENCE_GATES);
		registerBlock(event, SCContent.reinforcedBirchFenceGate, PageGroup.FENCE_GATES);
		registerBlock(event, SCContent.reinforcedJungleFenceGate, PageGroup.FENCE_GATES);
		registerBlock(event, SCContent.reinforcedDarkOakFenceGate, PageGroup.FENCE_GATES);
		registerBlock(event, SCContent.reinforcedAcaciaFenceGate, PageGroup.FENCE_GATES);
		registerBlock(event, SCContent.secureRedstoneInterface);
		registerBlock(event, SCContent.reinforcedMagmaBlock);
		registerBlock(event, SCContent.reinforcedSoulSand);

		//block mines
		registerBlockMine(event, SCContent.stoneMine);
		registerBlockMine(event, SCContent.dirtMine);
		registerBlockMine(event, SCContent.cobblestoneMine);
		registerBlockMine(event, SCContent.sandMine);
		registerBlockMine(event, SCContent.gravelMine);
		registerBlockMine(event, SCContent.netherrackMine);
		registerBlockMine(event, SCContent.endStoneMine);
		registerBlockMine(event, SCContent.goldOreMine);
		registerBlockMine(event, SCContent.ironOreMine);
		registerBlockMine(event, SCContent.coalOreMine);
		registerBlockMine(event, SCContent.lapisOreMine);
		registerBlockMine(event, SCContent.diamondOreMine);
		registerBlockMine(event, SCContent.redstoneOreMine);
		registerBlockMine(event, SCContent.emeraldOreMine);
		registerBlockMine(event, SCContent.quartzOreMine);
		registerBlock(event, SCContent.furnaceMine, ABLE_TO_CRAFT_MINES);
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
			SCManualItem.PAGES.add(new SCManualPage(
					Item.getItemFromBlock(block),
					PageGroup.SINGLE_ITEM,
					Utils.localize(block),
					Utils.localize("help." + block.getTranslationKey().substring(5) + ".info"),
					blocksDesignedBy.getOrDefault(block, ""),
					false,
					blockConfigValues.getOrDefault(block, () -> true)));
			//@formatter:on
		}

		registerItem(event, SCContent.codebreaker);
		registerItem(event, SCContent.reinforcedDoorItem);
		registerItem(event, SCContent.scannerDoorItem);
		registerItem(event, SCContent.universalBlockRemover);
		registerItem(event, SCContent.keycardLvl1, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard1);
		registerItem(event, SCContent.keycardLvl2, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard2);
		registerItem(event, SCContent.keycardLvl3, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard3);
		registerItem(event, SCContent.keycardLvl4, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard4);
		registerItem(event, SCContent.keycardLvl5, PageGroup.KEYCARDS, () -> ConfigHandler.ableToCraftKeycard5);
		registerItem(event, SCContent.limitedUseKeycard, PageGroup.SINGLE_ITEM, () -> ConfigHandler.ableToCraftLUKeycard);
		registerItem(event, SCContent.mineRemoteAccessTool);
		registerItem(event, SCContent.sentryRemoteAccessTool);
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
		registerItem(event, SCContent.universalBlockReinforcerLvL1, PageGroup.BLOCK_REINFORCERS);
		registerItem(event, SCContent.universalBlockReinforcerLvL2, PageGroup.BLOCK_REINFORCERS);
		registerItem(event, SCContent.universalBlockReinforcerLvL3, PageGroup.BLOCK_REINFORCERS);
		registerItem(event, SCContent.briefcase);
		registerItem(event, SCContent.universalKeyChanger);
		event.getRegistry().register(SCContent.taserPowered); //won't show up in the manual
		registerItem(event, SCContent.secretSignItem);
		registerItem(event, SCContent.sentry, PageGroup.SINGLE_ITEM, () -> true, "Henzoid");
		registerItem(event, SCContent.crystalQuartzItem);
		registerItem(event, SCContent.keypadDoorItem);
		registerItem(event, SCContent.portableTunePlayer);
		registerItem(event, SCContent.keycardHolder);
		registerItem(event, SCContent.lens);
		registerItem(event, SCContent.keycardLock);
		registerItem(event, SCContent.blockChangeDetectorItem);

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
		pageTypeStacks.remove(PageGroup.SINGLE_ITEM);
		pageTypeStacks.forEach((pageType, list) -> {
			pageType.setItems(Ingredient.fromStacks(list.toArray(new ItemStack[list.size()])));
			SCManualItem.PAGES.add(new SCManualPage(list.get(0).getItem(), pageType, Utils.localize(pageType.getTitle()), Utils.localize(pageType.getSpecialInfoKey()), "", !pageType.hasRecipeGrid()));
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
		GameRegistry.registerTileEntity(OwnableBlockEntity.class, new ResourceLocation("securitycraft:ownable"));
		GameRegistry.registerTileEntity(NamedBlockEntity.class, new ResourceLocation("securitycraft:abstract"));
		GameRegistry.registerTileEntity(KeypadBlockEntity.class, new ResourceLocation("securitycraft:keypad"));
		GameRegistry.registerTileEntity(LaserBlockBlockEntity.class, new ResourceLocation("securitycraft:laser_block"));
		GameRegistry.registerTileEntity(CageTrapBlockEntity.class, new ResourceLocation("securitycraft:cage_trap"));
		GameRegistry.registerTileEntity(KeycardReaderBlockEntity.class, new ResourceLocation("securitycraft:keycard_reader"));
		GameRegistry.registerTileEntity(InventoryScannerBlockEntity.class, new ResourceLocation("securitycraft:inventory_scanner"));
		GameRegistry.registerTileEntity(PortableRadarBlockEntity.class, new ResourceLocation("securitycraft:portable_radar"));
		GameRegistry.registerTileEntity(SecurityCameraBlockEntity.class, new ResourceLocation("securitycraft:security_camera"));
		GameRegistry.registerTileEntity(UsernameLoggerBlockEntity.class, new ResourceLocation("securitycraft:username_logger"));
		GameRegistry.registerTileEntity(RetinalScannerBlockEntity.class, new ResourceLocation("securitycraft:retinal_scanner"));
		GameRegistry.registerTileEntity(KeypadChestBlockEntity.class, new ResourceLocation("securitycraft:keypad_chest"));
		GameRegistry.registerTileEntity(AlarmBlockEntity.class, new ResourceLocation("securitycraft:alarm"));
		GameRegistry.registerTileEntity(ClaymoreBlockEntity.class, new ResourceLocation("securitycraft:claymore"));
		GameRegistry.registerTileEntity(KeypadFurnaceBlockEntity.class, new ResourceLocation("securitycraft:keypad_furnace"));
		GameRegistry.registerTileEntity(IMSBlockEntity.class, new ResourceLocation("securitycraft:ims"));
		GameRegistry.registerTileEntity(ProtectoBlockEntity.class, new ResourceLocation("securitycraft:protecto"));
		GameRegistry.registerTileEntity(CustomizableBlockEntity.class, new ResourceLocation("securitycraft:customizable"));
		GameRegistry.registerTileEntity(ScannerDoorBlockEntity.class, new ResourceLocation("securitycraft:scanner_door"));
		GameRegistry.registerTileEntity(SecretSignBlockEntity.class, new ResourceLocation("securitycraft:secret_sign"));
		GameRegistry.registerTileEntity(MotionActivatedLightBlockEntity.class, new ResourceLocation("securitycraft:motion_light"));
		GameRegistry.registerTileEntity(TrackMineBlockEntity.class, new ResourceLocation("securitycraft:track_mine"));
		GameRegistry.registerTileEntity(TrophySystemBlockEntity.class, new ResourceLocation("securitycraft:trophy_system"));
		GameRegistry.registerTileEntity(BlockPocketManagerBlockEntity.class, new ResourceLocation("securitycraft:block_pocket_manager"));
		GameRegistry.registerTileEntity(BlockPocketBlockEntity.class, new ResourceLocation("securitycraft:block_pocket"));
		GameRegistry.registerTileEntity(AllowlistOnlyBlockEntity.class, new ResourceLocation("securitycraft:reinforced_pressure_plate"));
		GameRegistry.registerTileEntity(ReinforcedHopperBlockEntity.class, new ResourceLocation("securitycraft:reinforced_hopper"));
		GameRegistry.registerTileEntity(ProjectorBlockEntity.class, new ResourceLocation("securitycraft:projector"));
		GameRegistry.registerTileEntity(IronFenceBlockEntity.class, new ResourceLocation("securitycraft:iron_fence"));
		GameRegistry.registerTileEntity(KeypadDoorBlockEntity.class, new ResourceLocation("securitycraft:keypad_door"));
		GameRegistry.registerTileEntity(ReinforcedIronBarsBlockEntity.class, new ResourceLocation("securitycraft:reinforced_iron_bars"));
		GameRegistry.registerTileEntity(ReinforcedCauldronBlockEntity.class, new ResourceLocation("securitycraft:reinforced_cauldron"));
		GameRegistry.registerTileEntity(ReinforcedPistonBlockEntity.class, new ResourceLocation("securitycraft:reinforced_piston"));
		GameRegistry.registerTileEntity(ValidationOwnableBlockEntity.class, new ResourceLocation("securitycraft:validation_ownable"));
		GameRegistry.registerTileEntity(KeyPanelBlockEntity.class, new ResourceLocation("securitycraft:key_panel"));
		GameRegistry.registerTileEntity(SonicSecuritySystemBlockEntity.class, new ResourceLocation("securitycraft:sonic_security_system"));
		GameRegistry.registerTileEntity(ReinforcedDoorBlockEntity.class, new ResourceLocation("securitycraft:reinforced_door"));
		GameRegistry.registerTileEntity(BlockChangeDetectorBlockEntity.class, new ResourceLocation("securitycraft:block_change_detector"));
		GameRegistry.registerTileEntity(RiftStabilizerBlockEntity.class, new ResourceLocation("securitycraft:rift_stabilizer"));
		GameRegistry.registerTileEntity(DisguisableBlockEntity.class, new ResourceLocation("securitycraft:disguisable"));
		GameRegistry.registerTileEntity(DisplayCaseBlockEntity.class, new ResourceLocation("securitycraft:display_case"));
		GameRegistry.registerTileEntity(KeypadTrapdoorBlockEntity.class, new ResourceLocation("securitycraft:keypad_trapdoor"));
		GameRegistry.registerTileEntity(FloorTrapBlockEntity.class, new ResourceLocation("securitycraft:floor_trap"));
		GameRegistry.registerTileEntity(KeycardLockBlockEntity.class, new ResourceLocation("securitycraft:keycard_lock"));
		GameRegistry.registerTileEntity(ScannerTrapdoorBlockEntity.class, new ResourceLocation("securitycraft:scanner_trapdoor"));
		GameRegistry.registerTileEntity(ReinforcedDispenserBlockEntity.class, new ResourceLocation("securitycraft:reinforced_dispenser"));
		GameRegistry.registerTileEntity(ReinforcedDropperBlockEntity.class, new ResourceLocation("securitycraft:reinforced_dropper"));
		GameRegistry.registerTileEntity(SecureRedstoneInterfaceBlockEntity.class, new ResourceLocation("securitycraft:secure_redstone_interface"));
		GameRegistry.registerTileEntity(FrameBlockEntity.class, new ResourceLocation("securitycraft:frame"));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		//@formatter:off
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "bouncingbetty"), 0)
				.entity(BouncingBetty.class)
				.name("BBetty")
				.tracker(128, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "imsbomb"), 3)
				.entity(IMSBomb.class)
				.name("IMSBomb")
				.tracker(256, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "securitycamera"), 4)
				.entity(SecurityCamera.class)
				.name("SecurityCamera")
				.tracker(256, 20, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "sentry"), 5)
				.entity(Sentry.class)
				.name("Sentry")
				.tracker(256, 1, true).build());
		event.getRegistry().register(EntityEntryBuilder.create()
				.id(new ResourceLocation(SecurityCraft.MODID, "bullet"), 6)
				.entity(Bullet.class)
				.name("SentryBullet")
				.tracker(256, 1, true).build());
		//@formatter:on
	}

	public static void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(SetCameraPowered.Handler.class, SetCameraPowered.class, 1, Side.SERVER);
		network.registerMessage(SyncKeycardSettings.Handler.class, SyncKeycardSettings.class, 3, Side.SERVER);
		network.registerMessage(UpdateLogger.Handler.class, UpdateLogger.class, 4, Side.CLIENT);
		network.registerMessage(UpdateNBTTagOnClient.Handler.class, UpdateNBTTagOnClient.class, 5, Side.CLIENT);
		network.registerMessage(RemoteControlMine.Handler.class, RemoteControlMine.class, 8, Side.SERVER);
		network.registerMessage(ToggleNightVision.Handler.class, ToggleNightVision.class, 9, Side.SERVER);
		network.registerMessage(SetPasscode.Handler.class, SetPasscode.class, 12, Side.SERVER);
		network.registerMessage(CheckPasscode.Handler.class, CheckPasscode.class, 13, Side.SERVER);
		network.registerMessage(SyncTENBTTag.Handler.class, SyncTENBTTag.class, 14, Side.SERVER);
		network.registerMessage(MountCamera.Handler.class, MountCamera.class, 15, Side.SERVER);
		network.registerMessage(CheckBriefcasePasscode.Handler.class, CheckBriefcasePasscode.class, 18, Side.SERVER);
		network.registerMessage(ToggleOption.Handler.class, ToggleOption.class, 19, Side.SERVER);
		network.registerMessage(UpdateSliderValue.Handler.class, UpdateSliderValue.class, 22, Side.SERVER);
		network.registerMessage(RemoveCameraTag.Handler.class, RemoveCameraTag.class, 23, Side.SERVER);
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
		network.registerMessage(SetGhostSlot.Handler.class, SetGhostSlot.class, 42, Side.SERVER);
		network.registerMessage(RemoveMineFromMRAT.Handler.class, RemoveMineFromMRAT.class, 43, Side.SERVER);
		network.registerMessage(RemovePositionFromSSS.Handler.class, RemovePositionFromSSS.class, 44, Side.SERVER);
		network.registerMessage(RemoveSentryFromSRAT.Handler.class, RemoveSentryFromSRAT.class, 45, Side.SERVER);
		network.registerMessage(SetBriefcasePasscodeAndOwner.Handler.class, SetBriefcasePasscodeAndOwner.class, 46, Side.SERVER);
		network.registerMessage(SetListModuleData.Handler.class, SetListModuleData.class, 47, Side.SERVER);
		network.registerMessage(SetStateOnDisguiseModule.Handler.class, SetStateOnDisguiseModule.class, 48, Side.SERVER);
		network.registerMessage(SyncRiftStabilizer.Handler.class, SyncRiftStabilizer.class, 49, Side.SERVER);
		network.registerMessage(UpdateLaserColors.Handler.class, UpdateLaserColors.class, 50, Side.CLIENT);
		network.registerMessage(SyncLaserSideConfig.Handler.class, SyncLaserSideConfig.class, 51, Side.SERVER);
		network.registerMessage(PlayAlarmSound.Handler.class, PlayAlarmSound.class, 52, Side.CLIENT);
		network.registerMessage(SyncAlarmSettings.Handler.class, SyncAlarmSettings.class, 53, Side.SERVER);
		network.registerMessage(SyncBlockReinforcer.Handler.class, SyncBlockReinforcer.class, 54, Side.SERVER);
		network.registerMessage(OpenScreen.Handler.class, OpenScreen.class, 55, Side.CLIENT);
		network.registerMessage(BlockPocketManagerFailedActivation.Handler.class, BlockPocketManagerFailedActivation.class, 56, Side.SERVER);
		network.registerMessage(SetDefaultCameraViewingDirection.Handler.class, SetDefaultCameraViewingDirection.class, 57, Side.SERVER);
		network.registerMessage(SyncSecureRedstoneInterface.Handler.class, SyncSecureRedstoneInterface.class, 58, Side.SERVER);
		network.registerMessage(SpawnInterfaceHighlightParticle.Handler.class, SpawnInterfaceHighlightParticle.class, 59, Side.CLIENT);
		network.registerMessage(InteractWithFrame.Handler.class, InteractWithFrame.class, 60, Side.CLIENT);
		network.registerMessage(SyncFrame.Handler.class, SyncFrame.class, 61, Side.SERVER);
		network.registerMessage(UpdateTeamPrecedence.Handler.class, UpdateTeamPrecedence.class, 62, Side.CLIENT);
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
		event.getRegistry().register(new DyeItemRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "dye_briefcase")));
		event.getRegistry().register(CopyPositionComponentItemRecipe.cameraMonitor().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "copy_camera_monitor")));
		event.getRegistry().register(CopyPositionComponentItemRecipe.mineRemoteAccessTool().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "copy_mine_remote_access_tool")));
		event.getRegistry().register(CopyPositionComponentItemRecipe.sentryRemoteAccessTool().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "copy_sentry_remote_access_tool")));
		event.getRegistry().register(CopyPositionComponentItemRecipe.sonicSecuritySystem().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "copy_sonic_security_system")));
		event.getRegistry().register(new LimitedUseKeycardRecipe().setRegistryName(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_conversion")));
		registerFakeLiquidRecipes(new ItemStack(Items.WATER_BUCKET), PotionTypes.HARMING, PotionTypes.STRONG_HARMING, new ItemStack(SCContent.fWaterBucket));
		registerFakeLiquidRecipes(new ItemStack(Items.LAVA_BUCKET), PotionTypes.HEALING, PotionTypes.STRONG_HEALING, new ItemStack(SCContent.fLavaBucket));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerResourceLocations(ModelRegistryEvent event) {
		//blocks
		registerInventoryModel(SCContent.keypad, 0, "keypad");
		registerInventoryModel(SCContent.frame, 0, "keypad_frame");
		registerInventoryModel(SCContent.reinforcedStone, 0, "reinforced_stone_default");
		registerInventoryModel(SCContent.reinforcedStone, 1, "reinforced_stone_granite");
		registerInventoryModel(SCContent.reinforcedStone, 2, "reinforced_stone_smooth_granite");
		registerInventoryModel(SCContent.reinforcedStone, 3, "reinforced_stone_diorite");
		registerInventoryModel(SCContent.reinforcedStone, 4, "reinforced_stone_smooth_diorite");
		registerInventoryModel(SCContent.reinforcedStone, 5, "reinforced_stone_andesite");
		registerInventoryModel(SCContent.reinforcedStone, 6, "reinforced_stone_smooth_andesite");
		registerInventoryModel(SCContent.laserBlock, 0, "laser_block");
		registerInventoryModel(SCContent.laserField, 0, "laser");
		registerInventoryModel(SCContent.keypadChest, 0, "keypad_chest");
		registerInventoryModel(SCContent.reinforcedDoor, 0, "reinforced_iron_door");
		registerInventoryModel(SCContent.reinforcedIronTrapdoor, 0, "reinforced_iron_trapdoor");
		registerInventoryModel(SCContent.keycardReader, 0, "keycard_reader");
		registerInventoryModel(SCContent.inventoryScanner, 0, "inventory_scanner");
		registerInventoryModel(SCContent.cageTrap, 0, "cage_trap");
		registerInventoryModel(SCContent.inventoryScannerField, 0, "inventory_scanner_field");
		registerInventoryModel(SCContent.retinalScanner, 0, "retinal_scanner");
		registerInventoryModel(SCContent.reinforcedIronBars, 0, "reinforced_iron_bars");
		registerInventoryModel(SCContent.portableRadar, 0, "portable_radar");
		registerInventoryModel(SCContent.alarm, 0, "alarm");
		registerInventoryModel(SCContent.alarmLit, 0, "alarm_lit");
		registerInventoryModel(SCContent.usernameLogger, 0, "username_logger");
		registerInventoryModel(SCContent.electrifiedIronFenceGate, 0, "reinforced_fence_gate");
		registerInventoryModel(SCContent.electrifiedIronFence, 0, "electrified_iron_fence");
		registerInventoryModel(SCContent.reinforcedWoodPlanks, 0, "reinforced_planks_oak");
		registerInventoryModel(SCContent.reinforcedWoodPlanks, 1, "reinforced_planks_spruce");
		registerInventoryModel(SCContent.reinforcedWoodPlanks, 2, "reinforced_planks_birch");
		registerInventoryModel(SCContent.reinforcedWoodPlanks, 3, "reinforced_planks_jungle");
		registerInventoryModel(SCContent.reinforcedWoodPlanks, 4, "reinforced_planks_acacia");
		registerInventoryModel(SCContent.reinforcedWoodPlanks, 5, "reinforced_planks_dark_oak");
		registerInventoryModel(SCContent.reinforcedStairsStone, 0, "reinforced_stairs_stone");
		registerInventoryModel(SCContent.reinforcedStairsCobblestone, 0, "reinforced_stairs_cobblestone");
		registerInventoryModel(SCContent.reinforcedStairsOak, 0, "reinforced_stairs_oak");
		registerInventoryModel(SCContent.reinforcedStairsSpruce, 0, "reinforced_stairs_spruce");
		registerInventoryModel(SCContent.reinforcedStairsBirch, 0, "reinforced_stairs_birch");
		registerInventoryModel(SCContent.reinforcedStairsJungle, 0, "reinforced_stairs_jungle");
		registerInventoryModel(SCContent.reinforcedStairsAcacia, 0, "reinforced_stairs_acacia");
		registerInventoryModel(SCContent.reinforcedStairsDarkoak, 0, "reinforced_stairs_darkoak");
		registerInventoryModel(SCContent.reinforcedGlass, 0, "reinforced_glass_block");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 0, "reinforced_stained_glass_white");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 1, "reinforced_stained_glass_orange");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 2, "reinforced_stained_glass_magenta");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 3, "reinforced_stained_glass_light_blue");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 4, "reinforced_stained_glass_yellow");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 5, "reinforced_stained_glass_lime");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 6, "reinforced_stained_glass_pink");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 7, "reinforced_stained_glass_gray");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 8, "reinforced_stained_glass_silver");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 9, "reinforced_stained_glass_cyan");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 10, "reinforced_stained_glass_purple");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 11, "reinforced_stained_glass_blue");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 12, "reinforced_stained_glass_brown");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 13, "reinforced_stained_glass_green");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 14, "reinforced_stained_glass_red");
		registerInventoryModel(SCContent.reinforcedStainedGlass, 15, "reinforced_stained_glass_black");
		registerInventoryModel(SCContent.keypadChest, 0, "keypad_chest");
		registerInventoryModel(SCContent.keypadFurnace, 0, "keypad_furnace");
		registerInventoryModel(SCContent.panicButton, 0, "panic_button");
		registerInventoryModel(SCContent.securityCamera, 0, "security_camera");
		registerInventoryModel(SCContent.reinforcedDirt, 0, "reinforced_dirt");
		registerInventoryModel(SCContent.reinforcedDirt, 1, "reinforced_coarse_dirt");
		registerInventoryModel(SCContent.reinforcedDirt, 2, "reinforced_podzol");
		registerInventoryModel(SCContent.reinforcedCobblestone, 0, "reinforced_cobblestone");
		registerInventoryModel(SCContent.reinforcedSandstone, 0, "reinforced_sandstone_normal");
		registerInventoryModel(SCContent.reinforcedSandstone, 1, "reinforced_sandstone_chiseled");
		registerInventoryModel(SCContent.reinforcedSandstone, 2, "reinforced_sandstone_smooth");
		registerInventoryModel(SCContent.reinforcedWoodSlabs, 0, "reinforced_wood_slabs_oak");
		registerInventoryModel(SCContent.reinforcedWoodSlabs, 1, "reinforced_wood_slabs_spruce");
		registerInventoryModel(SCContent.reinforcedWoodSlabs, 2, "reinforced_wood_slabs_birch");
		registerInventoryModel(SCContent.reinforcedWoodSlabs, 3, "reinforced_wood_slabs_jungle");
		registerInventoryModel(SCContent.reinforcedWoodSlabs, 4, "reinforced_wood_slabs_acacia");
		registerInventoryModel(SCContent.reinforcedWoodSlabs, 5, "reinforced_wood_slabs_dark_oak");
		registerInventoryModel(SCContent.reinforcedStairsCobblestone, 0, "reinforced_stairs_cobblestone");
		registerInventoryModel(SCContent.reinforcedStairsSandstone, 0, "reinforced_stairs_sandstone");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 0, "reinforced_stone_slabs_stone");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 1, "reinforced_stone_slabs_cobblestone");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 2, "reinforced_stone_slabs_sandstone");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 3, "reinforced_stone_slabs_stonebrick");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 4, "reinforced_stone_slabs_brick");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 5, "reinforced_stone_slabs_netherbrick");
		registerInventoryModel(SCContent.reinforcedStoneSlabs, 6, "reinforced_stone_slabs_quartz");
		registerInventoryModel(SCContent.reinforcedStoneSlabs2, 0, "reinforced_stone_slabs2_red_sandstone");
		registerInventoryModel(SCContent.reinforcedStoneSlabs2, 1, "reinforced_stone_slabs2_purpur");
		registerInventoryModel(SCContent.protecto, 0, "protecto");
		registerInventoryModel(SCContent.scannerDoor, 0, "scanner_door");
		registerInventoryModel(SCContent.reinforcedStoneBrick, 0, "reinforced_stone_brick_default");
		registerInventoryModel(SCContent.reinforcedStoneBrick, 1, "reinforced_stone_brick_mossy");
		registerInventoryModel(SCContent.reinforcedStoneBrick, 2, "reinforced_stone_brick_cracked");
		registerInventoryModel(SCContent.reinforcedStoneBrick, 3, "reinforced_stone_brick_chiseled");
		registerInventoryModel(SCContent.reinforcedStairsStoneBrick, 0, "reinforced_stairs_stone_brick");
		registerInventoryModel(SCContent.reinforcedMossyCobblestone, 0, "reinforced_mossy_cobblestone");
		registerInventoryModel(SCContent.reinforcedBrick, 0, "reinforced_brick");
		registerInventoryModel(SCContent.reinforcedStairsBrick, 0, "reinforced_stairs_brick");
		registerInventoryModel(SCContent.reinforcedNetherBrick, 0, "reinforced_nether_brick");
		registerInventoryModel(SCContent.reinforcedStairsNetherBrick, 0, "reinforced_stairs_nether_brick");
		registerInventoryModel(SCContent.reinforcedHardenedClay, 0, "reinforced_hardened_clay");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 0, "reinforced_stained_hardened_clay_white");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 1, "reinforced_stained_hardened_clay_orange");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 2, "reinforced_stained_hardened_clay_magenta");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 3, "reinforced_stained_hardened_clay_light_blue");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 4, "reinforced_stained_hardened_clay_yellow");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 5, "reinforced_stained_hardened_clay_lime");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 6, "reinforced_stained_hardened_clay_pink");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 7, "reinforced_stained_hardened_clay_gray");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 8, "reinforced_stained_hardened_clay_silver");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 9, "reinforced_stained_hardened_clay_cyan");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 10, "reinforced_stained_hardened_clay_purple");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 11, "reinforced_stained_hardened_clay_blue");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 12, "reinforced_stained_hardened_clay_brown");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 13, "reinforced_stained_hardened_clay_green");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 14, "reinforced_stained_hardened_clay_red");
		registerInventoryModel(SCContent.reinforcedStainedHardenedClay, 15, "reinforced_stained_hardened_clay_black");
		registerInventoryModel(SCContent.reinforcedOldLogs, 0, "reinforced_logs_oak");
		registerInventoryModel(SCContent.reinforcedOldLogs, 1, "reinforced_logs_spruce");
		registerInventoryModel(SCContent.reinforcedOldLogs, 2, "reinforced_logs_birch");
		registerInventoryModel(SCContent.reinforcedOldLogs, 3, "reinforced_logs_jungle");
		registerInventoryModel(SCContent.reinforcedNewLogs, 0, "reinforced_logs2_acacia");
		registerInventoryModel(SCContent.reinforcedNewLogs, 1, "reinforced_logs2_big_oak");
		registerInventoryModel(SCContent.reinforcedMetals, 0, "reinforced_metals_gold");
		registerInventoryModel(SCContent.reinforcedMetals, 1, "reinforced_metals_iron");
		registerInventoryModel(SCContent.reinforcedMetals, 2, "reinforced_metals_diamond");
		registerInventoryModel(SCContent.reinforcedMetals, 3, "reinforced_metals_emerald");
		registerInventoryModel(SCContent.reinforcedMetals, 4, "reinforced_metals_redstone");
		registerInventoryModel(SCContent.reinforcedLapisAndCoalBlocks, 0, "reinforced_compressed_blocks_lapis");
		registerInventoryModel(SCContent.reinforcedLapisAndCoalBlocks, 1, "reinforced_compressed_blocks_coal");
		registerInventoryModel(SCContent.reinforcedWool, 0, "reinforced_wool_white");
		registerInventoryModel(SCContent.reinforcedWool, 1, "reinforced_wool_orange");
		registerInventoryModel(SCContent.reinforcedWool, 2, "reinforced_wool_magenta");
		registerInventoryModel(SCContent.reinforcedWool, 3, "reinforced_wool_light_blue");
		registerInventoryModel(SCContent.reinforcedWool, 4, "reinforced_wool_yellow");
		registerInventoryModel(SCContent.reinforcedWool, 5, "reinforced_wool_lime");
		registerInventoryModel(SCContent.reinforcedWool, 6, "reinforced_wool_pink");
		registerInventoryModel(SCContent.reinforcedWool, 7, "reinforced_wool_gray");
		registerInventoryModel(SCContent.reinforcedWool, 8, "reinforced_wool_silver");
		registerInventoryModel(SCContent.reinforcedWool, 9, "reinforced_wool_cyan");
		registerInventoryModel(SCContent.reinforcedWool, 10, "reinforced_wool_purple");
		registerInventoryModel(SCContent.reinforcedWool, 11, "reinforced_wool_blue");
		registerInventoryModel(SCContent.reinforcedWool, 12, "reinforced_wool_brown");
		registerInventoryModel(SCContent.reinforcedWool, 13, "reinforced_wool_green");
		registerInventoryModel(SCContent.reinforcedWool, 14, "reinforced_wool_red");
		registerInventoryModel(SCContent.reinforcedWool, 15, "reinforced_wool_black");
		registerInventoryModel(SCContent.reinforcedQuartz, 0, "reinforced_quartz_default");
		registerInventoryModel(SCContent.reinforcedQuartz, 1, "reinforced_quartz_chiseled");
		registerInventoryModel(SCContent.reinforcedQuartz, 2, "reinforced_quartz_pillar");
		registerInventoryModel(SCContent.reinforcedStairsQuartz, 0, "reinforced_stairs_quartz");
		registerInventoryModel(SCContent.reinforcedPrismarine, 0, "reinforced_prismarine_default");
		registerInventoryModel(SCContent.reinforcedPrismarine, 1, "reinforced_prismarine_bricks");
		registerInventoryModel(SCContent.reinforcedPrismarine, 2, "reinforced_prismarine_dark");
		registerInventoryModel(SCContent.reinforcedRedSandstone, 0, "reinforced_red_sandstone_default");
		registerInventoryModel(SCContent.reinforcedRedSandstone, 1, "reinforced_red_sandstone_chiseled");
		registerInventoryModel(SCContent.reinforcedRedSandstone, 2, "reinforced_red_sandstone_smooth");
		registerInventoryModel(SCContent.reinforcedStairsRedSandstone, 0, "reinforced_stairs_red_sandstone");
		registerInventoryModel(SCContent.reinforcedEndStoneBricks, 0, "reinforced_end_stone_bricks");
		registerInventoryModel(SCContent.reinforcedRedNetherBrick, 0, "reinforced_red_nether_brick");
		registerInventoryModel(SCContent.reinforcedPurpur, 0, "reinforced_purpur_default");
		registerInventoryModel(SCContent.reinforcedPurpur, 1, "reinforced_purpur_pillar");
		registerInventoryModel(SCContent.reinforcedStairsPurpur, 0, "reinforced_stairs_purpur");
		registerInventoryModel(SCContent.reinforcedConcrete, 0, "reinforced_concrete_white");
		registerInventoryModel(SCContent.reinforcedConcrete, 1, "reinforced_concrete_orange");
		registerInventoryModel(SCContent.reinforcedConcrete, 2, "reinforced_concrete_magenta");
		registerInventoryModel(SCContent.reinforcedConcrete, 3, "reinforced_concrete_light_blue");
		registerInventoryModel(SCContent.reinforcedConcrete, 4, "reinforced_concrete_yellow");
		registerInventoryModel(SCContent.reinforcedConcrete, 5, "reinforced_concrete_lime");
		registerInventoryModel(SCContent.reinforcedConcrete, 6, "reinforced_concrete_pink");
		registerInventoryModel(SCContent.reinforcedConcrete, 7, "reinforced_concrete_gray");
		registerInventoryModel(SCContent.reinforcedConcrete, 8, "reinforced_concrete_silver");
		registerInventoryModel(SCContent.reinforcedConcrete, 9, "reinforced_concrete_cyan");
		registerInventoryModel(SCContent.reinforcedConcrete, 10, "reinforced_concrete_purple");
		registerInventoryModel(SCContent.reinforcedConcrete, 11, "reinforced_concrete_blue");
		registerInventoryModel(SCContent.reinforcedConcrete, 12, "reinforced_concrete_brown");
		registerInventoryModel(SCContent.reinforcedConcrete, 13, "reinforced_concrete_green");
		registerInventoryModel(SCContent.reinforcedConcrete, 14, "reinforced_concrete_red");
		registerInventoryModel(SCContent.reinforcedConcrete, 15, "reinforced_concrete_black");
		registerInventoryModel(SCContent.motionActivatedLight, 0, "motion_activated_light");
		registerInventoryModel(SCContent.reinforcedObsidian, 0, "reinforced_obsidian");
		registerInventoryModel(SCContent.reinforcedNetherrack, 0, "reinforced_netherrack");
		registerInventoryModel(SCContent.reinforcedEndStone, 0, "reinforced_end_stone");
		registerInventoryModel(SCContent.reinforcedSeaLantern, 0, "reinforced_sea_lantern");
		registerInventoryModel(SCContent.reinforcedBoneBlock, 0, "reinforced_bone_block");
		registerInventoryModel(SCContent.reinforcedGlassPane, 0, "reinforced_glass_pane");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 0, "reinforced_stained_glass_panes_white");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 1, "reinforced_stained_glass_panes_orange");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 2, "reinforced_stained_glass_panes_magenta");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 3, "reinforced_stained_glass_panes_light_blue");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 4, "reinforced_stained_glass_panes_yellow");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 5, "reinforced_stained_glass_panes_lime");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 6, "reinforced_stained_glass_panes_pink");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 7, "reinforced_stained_glass_panes_gray");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 8, "reinforced_stained_glass_panes_silver");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 9, "reinforced_stained_glass_panes_cyan");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 10, "reinforced_stained_glass_panes_purple");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 11, "reinforced_stained_glass_panes_blue");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 12, "reinforced_stained_glass_panes_brown");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 13, "reinforced_stained_glass_panes_green");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 14, "reinforced_stained_glass_panes_red");
		registerInventoryModel(SCContent.reinforcedStainedGlassPanes, 15, "reinforced_stained_glass_panes_black");
		registerInventoryModel(SCContent.reinforcedCarpet, 0, "reinforced_carpet_white");
		registerInventoryModel(SCContent.reinforcedCarpet, 1, "reinforced_carpet_orange");
		registerInventoryModel(SCContent.reinforcedCarpet, 2, "reinforced_carpet_magenta");
		registerInventoryModel(SCContent.reinforcedCarpet, 3, "reinforced_carpet_light_blue");
		registerInventoryModel(SCContent.reinforcedCarpet, 4, "reinforced_carpet_yellow");
		registerInventoryModel(SCContent.reinforcedCarpet, 5, "reinforced_carpet_lime");
		registerInventoryModel(SCContent.reinforcedCarpet, 6, "reinforced_carpet_pink");
		registerInventoryModel(SCContent.reinforcedCarpet, 7, "reinforced_carpet_gray");
		registerInventoryModel(SCContent.reinforcedCarpet, 8, "reinforced_carpet_silver");
		registerInventoryModel(SCContent.reinforcedCarpet, 9, "reinforced_carpet_cyan");
		registerInventoryModel(SCContent.reinforcedCarpet, 10, "reinforced_carpet_purple");
		registerInventoryModel(SCContent.reinforcedCarpet, 11, "reinforced_carpet_blue");
		registerInventoryModel(SCContent.reinforcedCarpet, 12, "reinforced_carpet_brown");
		registerInventoryModel(SCContent.reinforcedCarpet, 13, "reinforced_carpet_green");
		registerInventoryModel(SCContent.reinforcedCarpet, 14, "reinforced_carpet_red");
		registerInventoryModel(SCContent.reinforcedCarpet, 15, "reinforced_carpet_black");
		registerInventoryModel(SCContent.reinforcedGlowstone, 0, "reinforced_glowstone");
		registerInventoryModel(SCContent.reinforcedSand, 0, "reinforced_sand");
		registerInventoryModel(SCContent.reinforcedSand, 1, "reinforced_red_sand");
		registerInventoryModel(SCContent.reinforcedGravel, 0, "reinforced_gravel");
		registerInventoryModel(SCContent.trophySystem, 0, "trophy_system");
		registerInventoryModel(SCContent.crystalQuartz, 0, "crystal_quartz_default");
		registerInventoryModel(SCContent.crystalQuartz, 1, "crystal_quartz_chiseled");
		registerInventoryModel(SCContent.crystalQuartz, 2, "crystal_quartz_pillar");
		registerInventoryModel(SCContent.reinforcedCrystalQuartz, 0, "reinforced_crystal_quartz_default");
		registerInventoryModel(SCContent.reinforcedCrystalQuartz, 1, "reinforced_crystal_quartz_chiseled");
		registerInventoryModel(SCContent.reinforcedCrystalQuartz, 2, "reinforced_crystal_quartz_pillar");
		registerInventoryModel(SCContent.crystalQuartzSlab, 0, "crystal_quartz_slab");
		registerInventoryModel(SCContent.reinforcedCrystalQuartzSlab, 0, "reinforced_crystal_quartz_slab");
		registerInventoryModel(SCContent.stairsCrystalQuartz, 0, "stairs_crystal_quartz");
		registerInventoryModel(SCContent.reinforcedStairsCrystalQuartz, 0, "reinforced_stairs_crystal_quartz");
		registerInventoryModel(SCContent.blockPocketWall, 0, "block_pocket_wall");
		registerInventoryModel(SCContent.blockPocketManager, 0, "block_pocket_manager");
		registerInventoryModel(SCContent.reinforcedStonePressurePlate, 0, "reinforced_stone_pressure_plate");
		registerInventoryModel(SCContent.reinforcedWoodenPressurePlate, 0, "reinforced_wooden_pressure_plate");
		registerInventoryModel(SCContent.reinforcedBookshelf, 0, "reinforced_bookshelf");
		registerInventoryModel(SCContent.reinforcedWalls, 0, "reinforced_cobblestone_wall");
		registerInventoryModel(SCContent.reinforcedWalls, 1, "reinforced_mossy_cobblestone_wall");
		registerInventoryModel(SCContent.reinforcedStickyPiston, 0, "reinforced_sticky_piston");
		registerInventoryModel(SCContent.reinforcedPiston, 0, "reinforced_piston");
		registerInventoryModel(SCContent.reinforcedObserver, 0, "reinforced_observer");
		registerInventoryModel(SCContent.reinforcedRedstoneLamp, 0, "reinforced_redstone_lamp");
		registerInventoryModel(SCContent.reinforcedCobweb, 0, "reinforced_cobweb");
		registerInventoryModel(SCContent.reinforcedGrass, 0, "reinforced_grass_block");
		registerInventoryModel(SCContent.reinforcedSnowBlock, 0, "reinforced_snow_block");
		registerInventoryModel(SCContent.reinforcedIce, 0, "reinforced_ice");
		registerInventoryModel(SCContent.reinforcedPackedIce, 0, "reinforced_packed_ice");
		registerInventoryModel(SCContent.reinforcedMycelium, 0, "reinforced_mycelium");
		registerInventoryModel(SCContent.reinforcedClay, 0, "reinforced_clay");
		registerInventoryModel(SCContent.reinforcedNetherWartBlock, 0, "reinforced_nether_wart_block");
		registerInventoryModel(SCContent.reinforcedGrassPath, 0, "reinforced_grass_path");
		registerInventoryModel(SCContent.reinforcedStoneButton, 0, "reinforced_stone_button");
		registerInventoryModel(SCContent.reinforcedWoodenButton, 0, "reinforced_wooden_button");
		registerInventoryModel(SCContent.reinforcedLever, 0, "reinforced_lever");
		registerInventoryModel(SCContent.reinforcedHopper, 0, "reinforced_hopper");
		registerInventoryModel(SCContent.projector, 0, "projector");
		registerInventoryModel(SCContent.keypadDoor, 0, "keypad_door");
		registerInventoryModel(SCContent.reinforcedCauldron, 0, "reinforced_cauldron");
		registerInventoryModel(SCContent.reinforcedEndRod, 0, "reinforced_end_rod");
		registerInventoryModel(SCContent.reinforcedWhiteGlazedTerracotta, 0, "reinforced_white_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedOrangeGlazedTerracotta, 0, "reinforced_orange_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedMagentaGlazedTerracotta, 0, "reinforced_magenta_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedLightBlueGlazedTerracotta, 0, "reinforced_light_blue_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedYellowGlazedTerracotta, 0, "reinforced_yellow_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedLimeGlazedTerracotta, 0, "reinforced_lime_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedPinkGlazedTerracotta, 0, "reinforced_pink_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedGrayGlazedTerracotta, 0, "reinforced_gray_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedSilverGlazedTerracotta, 0, "reinforced_silver_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedCyanGlazedTerracotta, 0, "reinforced_cyan_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedPurpleGlazedTerracotta, 0, "reinforced_purple_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedBlueGlazedTerracotta, 0, "reinforced_blue_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedBrownGlazedTerracotta, 0, "reinforced_brown_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedGreenGlazedTerracotta, 0, "reinforced_green_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedRedGlazedTerracotta, 0, "reinforced_red_glazed_terracotta");
		registerInventoryModel(SCContent.reinforcedBlackGlazedTerracotta, 0, "reinforced_black_glazed_terracotta");
		registerInventoryModel(SCContent.riftStabilizer, 0, "rift_stabilizer");
		registerInventoryModel(SCContent.displayCase, 0, "display_case");
		registerInventoryModel(SCContent.keypadTrapdoor, 0, "keypad_trapdoor");
		registerInventoryModel(SCContent.reinforcedLadder, 0, "reinforced_ladder");
		registerInventoryModel(SCContent.floorTrap, 0, "floor_trap");
		registerInventoryModel(SCContent.scannerTrapdoor, 0, "scanner_trapdoor");
		registerInventoryModel(SCContent.reinforcedDispenser, 0, "reinforced_dispenser");
		registerInventoryModel(SCContent.reinforcedDropper, 0, "reinforced_dropper");
		registerInventoryModel(SCContent.reinforcedOakFence, 0, "reinforced_oak_fence");
		registerInventoryModel(SCContent.reinforcedNetherBrickFence, 0, "reinforced_nether_brick_fence");
		registerInventoryModel(SCContent.reinforcedSpruceFence, 0, "reinforced_spruce_fence");
		registerInventoryModel(SCContent.reinforcedBirchFence, 0, "reinforced_birch_fence");
		registerInventoryModel(SCContent.reinforcedJungleFence, 0, "reinforced_jungle_fence");
		registerInventoryModel(SCContent.reinforcedDarkOakFence, 0, "reinforced_dark_oak_fence");
		registerInventoryModel(SCContent.reinforcedAcaciaFence, 0, "reinforced_acacia_fence");
		registerInventoryModel(SCContent.reinforcedOakFenceGate, 0, "reinforced_oak_fence_gate");
		registerInventoryModel(SCContent.reinforcedSpruceFenceGate, 0, "reinforced_spruce_fence_gate");
		registerInventoryModel(SCContent.reinforcedBirchFenceGate, 0, "reinforced_birch_fence_gate");
		registerInventoryModel(SCContent.reinforcedJungleFenceGate, 0, "reinforced_jungle_fence_gate");
		registerInventoryModel(SCContent.reinforcedDarkOakFenceGate, 0, "reinforced_dark_oak_fence_gate");
		registerInventoryModel(SCContent.reinforcedAcaciaFenceGate, 0, "reinforced_acacia_fence_gate");
		registerInventoryModel(SCContent.secureRedstoneInterface, 0, "secure_redstone_interface");
		registerInventoryModel(SCContent.reinforcedMagmaBlock, 0, "reinforced_magma_block");
		registerInventoryModel(SCContent.reinforcedSoulSand, 0, "reinforced_soul_sand");

		//items
		registerInventoryModel(SCContent.codebreaker, 0, "codebreaker");
		registerInventoryModel(SCContent.mineRemoteAccessTool, 0, "remote_access_mine");
		registerInventoryModel(SCContent.sentryRemoteAccessTool, 0, "remote_access_sentry");
		registerInventoryModel(SCContent.reinforcedDoorItem, 0, "door_indestructible_iron_item");
		registerInventoryModel(SCContent.fWaterBucket, 0, "bucket_f_water");
		registerInventoryModel(SCContent.fLavaBucket, 0, "bucket_f_lava");
		registerInventoryModel(SCContent.keycardLvl1, 0, "keycard_lv1");
		registerInventoryModel(SCContent.keycardLvl2, 0, "keycard_lv2");
		registerInventoryModel(SCContent.keycardLvl3, 0, "keycard_lv3");
		registerInventoryModel(SCContent.keycardLvl4, 0, "keycard_lv4");
		registerInventoryModel(SCContent.keycardLvl5, 0, "keycard_lv5");
		registerInventoryModel(SCContent.limitedUseKeycard, 0, "limited_use_keycard");
		registerInventoryModel(SCContent.universalBlockRemover, 0, "universal_block_remover");
		registerInventoryModel(SCContent.universalBlockModifier, 0, "universal_block_modifier");
		registerInventoryModel(SCContent.allowlistModule, 0, "whitelist_module");
		registerInventoryModel(SCContent.denylistModule, 0, "blacklist_module");
		registerInventoryModel(SCContent.redstoneModule, 0, "redstone_module");
		registerInventoryModel(SCContent.harmingModule, 0, "harming_module");
		registerInventoryModel(SCContent.storageModule, 0, "storage_module");
		registerInventoryModel(SCContent.smartModule, 0, "smart_module");
		registerInventoryModel(SCContent.disguiseModule, 0, "disguise_module");
		registerInventoryModel(SCContent.speedModule, 0, "speed_module");
		registerInventoryModel(SCContent.wireCutters, 0, "wire_cutters");
		registerInventoryModel(SCContent.keyPanel, 0, "keypad_item");
		registerInventoryModel(SCContent.adminTool, 0, "admin_tool");
		registerInventoryModel(SCContent.cameraMonitor, 0, "camera_monitor");
		registerInventoryModel(SCContent.scManual, 0, "sc_manual");
		registerInventoryModel(SCContent.taser, 0, "taser");
		registerInventoryModel(SCContent.taserPowered, 0, "taser_powered");
		registerInventoryModel(SCContent.universalOwnerChanger, 0, "universal_owner_changer");
		registerInventoryModel(SCContent.universalBlockReinforcerLvL1, 0, "universal_block_reinforcer_lvl1");
		registerInventoryModel(SCContent.universalBlockReinforcerLvL2, 0, "universal_block_reinforcer_lvl2");
		registerInventoryModel(SCContent.universalBlockReinforcerLvL3, 0, "universal_block_reinforcer_lvl3");
		registerInventoryModel(SCContent.briefcase, 0, "briefcase");
		registerInventoryModel(SCContent.universalKeyChanger, 0, "universal_key_changer");
		registerInventoryModel(SCContent.scannerDoorItem, 0, "scanner_door_item");
		registerInventoryModel(SCContent.secretSignItem, 0, "secret_sign_item");
		registerInventoryModel(SCContent.sentry, 0, "sentry");
		registerInventoryModel(SCContent.crystalQuartzItem, 0, "crystal_quartz_item");
		registerInventoryModel(SCContent.keypadDoorItem, 0, "keypad_door_item");
		registerInventoryModel(SCContent.sonicSecuritySystemItem, 0, "sonic_security_system");
		registerInventoryModel(SCContent.portableTunePlayer, 0, "portable_tune_player");
		registerInventoryModel(SCContent.keycardHolder, 0, "keycard_holder");
		registerInventoryModel(SCContent.lens, 0, "lens");
		registerInventoryModel(SCContent.keycardLock, 0, "keycard_lock");
		registerInventoryModel(SCContent.blockChangeDetectorItem, 0, "block_change_detector");

		//mines
		registerInventoryModel(SCContent.mine, 0, "mine");
		registerInventoryModel(SCContent.dirtMine, 0, "dirt_mine");
		registerInventoryModel(SCContent.stoneMine, 0, "stone_mine");
		registerInventoryModel(SCContent.cobblestoneMine, 0, "cobblestone_mine");
		registerInventoryModel(SCContent.sandMine, 0, "sand_mine");
		registerInventoryModel(SCContent.diamondOreMine, 0, "diamond_mine");
		registerInventoryModel(SCContent.furnaceMine, 0, "furnace_mine");
		registerInventoryModel(SCContent.trackMine, 0, "track_mine");
		registerInventoryModel(SCContent.bouncingBetty, 0, "bouncing_betty");
		registerInventoryModel(SCContent.claymore, 0, "claymore");
		registerInventoryModel(SCContent.ims, 0, "ims");
		registerInventoryModel(SCContent.gravelMine, 0, "gravel_mine");
		registerInventoryModel(SCContent.coalOreMine, 0, "coal_ore_mine");
		registerInventoryModel(SCContent.emeraldOreMine, 0, "emerald_ore_mine");
		registerInventoryModel(SCContent.goldOreMine, 0, "gold_ore_mine");
		registerInventoryModel(SCContent.ironOreMine, 0, "iron_ore_mine");
		registerInventoryModel(SCContent.lapisOreMine, 0, "lapis_ore_mine");
		registerInventoryModel(SCContent.quartzOreMine, 0, "quartz_ore_mine");
		registerInventoryModel(SCContent.redstoneOreMine, 0, "redstone_ore_mine");
		registerInventoryModel(SCContent.netherrackMine, 0, "netherrack_mine");
		registerInventoryModel(SCContent.endStoneMine, 0, "end_stone_mine");
	}

	private static void registerInventoryModel(Block block, int metadata, String name) {
		registerInventoryModel(Item.getItemFromBlock(block), metadata, name);
	}

	private static void registerInventoryModel(Item item, int metadata, String name) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation("securitycraft:" + name, "inventory"));
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
		registerBlock(event, block, new ItemBlock(block), PageGroup.SINGLE_ITEM);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 *
	 * @param block The block to register
	 */
	private static void registerBlockMine(RegistryEvent.Register<Block> event, Block block) {
		registerBlock(event, block, new ItemBlock(block), PageGroup.BLOCK_MINES);
		blockConfigValues.put(block, ABLE_TO_CRAFT_MINES);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item. Additionally, a
	 * configuration value can be set to have this block's recipe show as disabled in the manual.
	 *
	 * @param block The block to register
	 * @param configValue The config value
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, Supplier<Boolean> configValue) {
		registerBlock(event, block, new ItemBlock(block), PageGroup.SINGLE_ITEM);
		blockConfigValues.put(block, configValue);
	}

	/**
	 * Registers a block and its ItemBlock
	 *
	 * @param block The Block to register
	 * @param pageType The type of the manual page from this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, PageGroup pageGroup) {
		registerBlock(event, block, new ItemBlock(block), pageGroup);
	}

	/**
	 * Registers a block with a custom ItemBlock
	 *
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param pageType The type of the manual page from this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ItemBlock itemBlock, PageGroup pageType) {
		event.getRegistry().register(block);

		if (itemBlock != null)
			itemBlocks.add(itemBlock.setRegistryName(block.getRegistryName().toString()));

		if (pageType == PageGroup.SINGLE_ITEM)
			blockPages.add(block);
		else if (pageType != PageGroup.NO_PAGE) {
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
		registerItem(event, item, PageGroup.SINGLE_ITEM, () -> true, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item. Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageGroup pageType, Supplier<Boolean> configValue) {
		registerItem(event, item, pageType, configValue, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageGroup pageType) {
		registerItem(event, item, pageType, () -> true, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft
	 * manual item. Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, PageGroup pageType, Supplier<Boolean> configValue, String designedBy) {
		event.getRegistry().register(item);

		if (pageType == PageGroup.NO_PAGE)
			return;

		if (pageType != PageGroup.SINGLE_ITEM) {
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

			SCManualItem.PAGES.add(new SCManualPage(item, pageType, title, helpInfo, designedBy, false, configValue));
		}
	}

	private static void registerFakeLiquidRecipes(ItemStack input, PotionType normalPotion, PotionType strongPotion, ItemStack output) {
		NBTTagCompound normalNBT = new NBTTagCompound();
		NBTTagCompound strongNBT = new NBTTagCompound();
		PartialNBTIngredient normalPotions;
		PartialNBTIngredient strongPotions;

		normalNBT.setString("Potion", normalPotion.getRegistryName().toString());
		strongNBT.setString("Potion", strongPotion.getRegistryName().toString());
		normalPotions = PartialNBTIngredient.of(normalNBT, Items.POTIONITEM, Items.SPLASH_POTION, Items.LINGERING_POTION);
		strongPotions = PartialNBTIngredient.of(strongNBT, Items.POTIONITEM, Items.SPLASH_POTION, Items.LINGERING_POTION);
		BrewingRecipeRegistry.addRecipe(new IngredientBrewingRecipe(input, PublicCompoundIngredient.of(normalPotions, strongPotions), output));
	}

	public static class PublicCompoundIngredient extends CompoundIngredient { //Constructor of CompoundIngredient is protected, so this surrogate class is needed
		public PublicCompoundIngredient(List<Ingredient> children) {
			super(children);
		}

		public static PublicCompoundIngredient of(Ingredient... ingredients) {
			return new PublicCompoundIngredient(Arrays.asList(ingredients));
		}
	}
}
