package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.blocks.KeypadTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.ScannerTrapDoorBlock;
import net.geforcemods.securitycraft.items.LensItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Tinted;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.item.Item;

public class SCContent {
	//Blocks
	public static Block alarm;
	/**
	 * @deprecated Use {@link alarm} and its LIT property
	 * @see {@link AlarmBlock}
	 */
	@Deprecated
	public static Block alarmLit;
	public static Block blockChangeDetectorFloorCeiling;
	public static Block blockChangeDetectorWall;
	@Tinted(customTint = 0x15b3a2)
	public static Block blockPocketManager;
	@Tinted(customTint = 0x15b3a2)
	public static Block blockPocketWall;
	public static Block bogusLavaFlowing;
	public static Block bogusWaterFlowing;
	public static Block bouncingBetty;
	public static Block cageTrap;
	public static Block claymore;
	public static Block coalOreMine;
	public static Block cobblestoneMine;
	@Tinted(customTint = 0x15b3a2, hasReinforcedTint = false)
	public static Block crystalQuartz;
	@Tinted(customTint = 0x15b3a2, hasReinforcedTint = false)
	public static Block crystalQuartzSlab;
	public static Block diamondOreMine;
	public static Block dirtMine;
	public static Block displayCase;
	@Tinted(customTint = 0x15b3a2, hasReinforcedTint = false)
	public static Block doubleCrystalQuartzSlab;
	public static Block electrifiedIronFence;
	public static Block electrifiedIronFenceGate;
	public static Block emeraldOreMine;
	public static Block endStoneMine;
	public static BlockStaticLiquid fakeLava;
	public static BlockStaticLiquid fakeWater;
	public static Block floorTrap;
	public static Block frame;
	public static Block furnaceMine;
	public static Block goldOreMine;
	public static Block gravelMine;
	public static Block ims;
	public static Block inventoryScanner;
	public static Block inventoryScannerField;
	public static Block ironOreMine;
	public static Block keycardLockFloorCeilingBlock;
	public static Block keycardLockWallBlock;
	public static Block keycardReader;
	public static Block keyPanelFloorCeilingBlock;
	public static Block keyPanelWallBlock;
	public static Block keypad;
	public static Block keypadChest;
	public static Block keypadDoor;
	public static Block keypadFurnace;
	public static KeypadTrapDoorBlock keypadTrapdoor;
	public static Block lapisOreMine;
	public static Block laserBlock;
	public static Block laserField;
	public static Block motionActivatedLight;
	public static Block netherrackMine;
	public static Block panicButton;
	public static Block portableRadar;
	public static Block projector;
	public static Block protecto;
	public static Block quartzOreMine;
	public static Block redstoneOreMine;
	@Reinforced
	@Tinted
	public static Block reinforcedBoneBlock;
	@Reinforced
	@Tinted
	public static Block reinforcedBookshelf;
	@Reinforced
	@Tinted
	public static Block reinforcedBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedCarpet;
	@Reinforced
	@Tinted
	public static Block reinforcedCauldron;
	@Reinforced
	@Tinted
	public static Block reinforcedClay;
	@Reinforced
	@Tinted
	public static Block reinforcedCobblestone;
	@Reinforced
	@Tinted
	public static Block reinforcedLapisAndCoalBlocks;
	@Reinforced
	@Tinted
	public static Block reinforcedConcrete;
	@Reinforced
	@Tinted(customTint = 0x15b3a2)
	public static Block reinforcedCrystalQuartz;
	@Reinforced
	@Tinted
	public static Block reinforcedDirt;
	public static Block reinforcedDoor;
	@Reinforced
	@Tinted
	public static Block reinforcedCobweb;
	@Reinforced
	@Tinted(customTint = 0x15b3a2)
	public static Block reinforcedCrystalQuartzSlab;
	@Reinforced
	@Tinted
	public static Block reinforcedDispenser;
	@Reinforced
	@Tinted(customTint = 0x15b3a2)
	public static Block reinforcedDoubleCrystalQuartzSlab;
	@Reinforced
	@Tinted
	public static Block reinforcedDoubleStoneSlabs;
	@Reinforced
	@Tinted
	public static Block reinforcedDoubleStoneSlabs2;
	@Reinforced
	@Tinted
	public static Block reinforcedDoubleWoodSlabs;
	@Reinforced
	@Tinted
	public static Block reinforcedDropper;
	@Reinforced
	@Tinted
	public static Block reinforcedEndRod;
	@Reinforced
	@Tinted
	public static Block reinforcedEndStone;
	@Reinforced
	@Tinted
	public static Block reinforcedEndStoneBricks;
	@Reinforced
	@Tinted
	public static Block reinforcedOakFence;
	@Reinforced
	@Tinted
	public static Block reinforcedNetherBrickFence;
	@Reinforced
	@Tinted
	public static Block reinforcedSpruceFence;
	@Reinforced
	@Tinted
	public static Block reinforcedBirchFence;
	@Reinforced
	@Tinted
	public static Block reinforcedJungleFence;
	@Reinforced
	@Tinted
	public static Block reinforcedDarkOakFence;
	@Reinforced
	@Tinted
	public static Block reinforcedAcaciaFence;
	@Reinforced
	@Tinted
	public static Block reinforcedOakFenceGate;
	@Reinforced
	@Tinted
	public static Block reinforcedSpruceFenceGate;
	@Reinforced
	@Tinted
	public static Block reinforcedBirchFenceGate;
	@Reinforced
	@Tinted
	public static Block reinforcedJungleFenceGate;
	@Reinforced
	@Tinted
	public static Block reinforcedDarkOakFenceGate;
	@Reinforced
	@Tinted
	public static Block reinforcedAcaciaFenceGate;
	@Reinforced
	public static Block reinforcedGlass;
	@Reinforced
	public static Block reinforcedGlassPane;
	@Reinforced
	@Tinted
	public static Block reinforcedWhiteGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedOrangeGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedMagentaGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedLightBlueGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedYellowGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedLimeGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedPinkGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedGrayGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedSilverGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedCyanGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedPurpleGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedBlueGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedBrownGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedGreenGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedRedGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedBlackGlazedTerracotta;
	@Reinforced
	@Tinted
	public static Block reinforcedGrassPath;
	@Reinforced
	@Tinted
	public static Block reinforcedGlowstone;
	@Reinforced
	@Tinted
	public static Block reinforcedGrass;
	@Reinforced
	@Tinted
	public static Block reinforcedGravel;
	@Reinforced
	@Tinted
	public static Block reinforcedHardenedClay;
	@Reinforced
	@Tinted
	public static Block reinforcedHopper;
	@Reinforced
	@Tinted
	public static Block reinforcedIce;
	public static Block horizontalReinforcedIronBars;
	@Reinforced
	public static Block reinforcedIronBars;
	@Reinforced
	public static Block reinforcedIronTrapdoor;
	@Reinforced
	@Tinted
	public static Block reinforcedLadder;
	@Reinforced
	@Tinted
	public static Block reinforcedLever;
	@Reinforced
	@Tinted
	public static Block reinforcedMagmaBlock;
	@Reinforced
	@Tinted
	public static Block reinforcedMetals;
	@Reinforced
	@Tinted
	public static Block reinforcedMossyCobblestone;
	@Reinforced
	@Tinted
	public static Block reinforcedMycelium;
	@Reinforced
	@Tinted
	public static Block reinforcedNetherBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedNetherrack;
	@Reinforced
	@Tinted
	public static Block reinforcedNetherWartBlock;
	@Reinforced
	@Tinted
	public static Block reinforcedNewLogs;
	@Reinforced
	@Tinted
	public static Block reinforcedObserver;
	@Reinforced
	@Tinted
	public static Block reinforcedObsidian;
	@Reinforced
	@Tinted
	public static Block reinforcedOldLogs;
	@Reinforced
	@Tinted
	public static Block reinforcedPackedIce;
	@Reinforced
	@Tinted
	public static Block reinforcedPiston;
	@Reinforced
	@Tinted
	public static Block reinforcedPistonHead;
	public static Block reinforcedMovingPiston;
	@Reinforced
	@Tinted
	public static Block reinforcedPrismarine;
	@Reinforced
	@Tinted
	public static Block reinforcedPurpur;
	@Reinforced
	@Tinted
	public static Block reinforcedQuartz;
	@Reinforced
	@Tinted
	public static Block reinforcedRedNetherBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedRedSandstone;
	@Reinforced
	@Tinted
	public static Block reinforcedRedstoneLamp;
	@Reinforced
	@Tinted
	public static Block reinforcedSand;
	@Reinforced
	@Tinted
	public static Block reinforcedSandstone;
	@Reinforced
	@Tinted
	public static Block reinforcedSeaLantern;
	@Reinforced
	@Tinted
	public static Block reinforcedSnowBlock;
	@Reinforced
	@Tinted
	public static Block reinforcedSoulSand;
	@Reinforced
	public static Block reinforcedStainedGlass;
	@Reinforced
	public static Block reinforcedStainedGlassPanes;
	@Reinforced
	@Tinted
	public static Block reinforcedStainedHardenedClay;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsAcacia;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsBirch;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsCobblestone;
	@Reinforced
	@Tinted(customTint = 0x15b3a2)
	public static Block reinforcedStairsCrystalQuartz;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsDarkoak;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsJungle;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsNetherBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsOak;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsPurpur;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsQuartz;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsRedSandstone;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsSandstone;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsSpruce;
	@Tinted
	public static Block reinforcedStairsStone;
	@Reinforced
	@Tinted
	public static Block reinforcedStairsStoneBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedStickyPiston;
	@Reinforced
	@Tinted
	public static Block reinforcedStone;
	@Reinforced
	@Tinted
	public static Block reinforcedStoneBrick;
	@Reinforced
	@Tinted
	public static Block reinforcedStoneButton;
	@Reinforced
	@Tinted
	public static Block reinforcedStonePressurePlate;
	@Reinforced
	@Tinted
	public static Block reinforcedStoneSlabs;
	@Reinforced
	@Tinted
	public static Block reinforcedStoneSlabs2;
	@Reinforced
	@Tinted
	public static Block reinforcedWalls;
	@Reinforced
	@Tinted
	public static Block reinforcedWoodenButton;
	@Reinforced
	@Tinted
	public static Block reinforcedWoodenPressurePlate;
	@Reinforced
	@Tinted
	public static Block reinforcedWoodPlanks;
	@Reinforced
	@Tinted
	public static Block reinforcedWoodSlabs;
	@Reinforced
	@Tinted
	public static Block reinforcedWool;
	public static Block retinalScanner;
	public static Block riftStabilizer;
	public static Block sandMine;
	public static Block scannerDoor;
	public static ScannerTrapDoorBlock scannerTrapdoor;
	public static Block secretSignStanding;
	public static Block secretSignWall;
	public static Block secureRedstoneInterface;
	public static Block securityCamera;
	public static Block sentryDisguise;
	public static Block sonicSecuritySystem;
	@Tinted(customTint = 0x15b3a2, hasReinforcedTint = false)
	public static Block stairsCrystalQuartz;
	public static Block stoneMine;
	public static Block trackMine;
	public static Block trophySystem;
	public static Block usernameLogger;

	//Items
	public static Item adminTool;
	public static Item briefcase;
	public static Item blockChangeDetectorItem;
	public static Item cameraMonitor;
	public static Item codebreaker;
	public static Item crystalQuartzItem;
	public static Item fLavaBucket;
	public static Item fWaterBucket;
	public static Item keycardLvl1;
	public static Item keycardLvl2;
	public static Item keycardLvl3;
	public static Item keycardLvl4;
	public static Item keycardLvl5;
	public static Item keypadDoorItem;
	public static Item keyPanel;
	public static Item limitedUseKeycard;
	public static Item portableTunePlayer;
	public static Item reinforcedDoorItem;
	public static Item mineRemoteAccessTool;
	public static Item sentryRemoteAccessTool;
	public static Item scannerDoorItem;
	public static Item scManual;
	public static Item secretSignItem;
	public static Item sentry;
	public static Item sonicSecuritySystemItem;
	public static Item taser;
	public static Item taserPowered;
	public static Item universalBlockModifier;
	public static Item universalBlockReinforcerLvL1;
	public static Item universalBlockReinforcerLvL2;
	public static Item universalBlockReinforcerLvL3;
	public static Item universalBlockRemover;
	public static Item universalKeyChanger;
	public static Item universalOwnerChanger;
	public static Item wireCutters;
	public static Item keycardHolder;
	public static LensItem lens;
	public static Item keycardLock;

	//Modules
	public static ModuleItem denylistModule;
	public static ModuleItem disguiseModule;
	public static ModuleItem harmingModule;
	public static ModuleItem redstoneModule;
	public static ModuleItem smartModule;
	public static ModuleItem storageModule;
	public static ModuleItem allowlistModule;
	public static ModuleItem speedModule;

	private SCContent() {}
}
