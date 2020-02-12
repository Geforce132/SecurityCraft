package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.blocks.mines.MineBlock;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.TaserBulletEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.tileentity.AlarmTileEntity;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.tileentity.ClaymoreTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.LaserBlockTileEntity;
import net.geforcemods.securitycraft.tileentity.MotionActivatedLightTileEntity;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.tileentity.ProtectoTileEntity;
import net.geforcemods.securitycraft.tileentity.ReinforcedPressurePlateTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.ScannerDoorTileEntity;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.tileentity.TrackMineTileEntity;
import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.util.OwnableTE;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class SCContent
{
	//Blocks
	@RegisterItemBlock public static Block alarm;
	@RegisterItemBlock public static Block blockPocketManager;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block blockPocketWall;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block bouncingBetty;
	@RegisterItemBlock public static Block cageTrap;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block chiseledCrystalQuartz;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block crystalQuartz;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block crystalQuartzPillar;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block crystalQuartzSlab;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block claymore;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block cobblestoneMine;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block diamondOreMine;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block dirtMine;
	@OwnableTE @RegisterItemBlock public static Block frame;
	@OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block furnaceMine;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block gravelMine;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block ims;
	@RegisterItemBlock public static Block inventoryScanner;
	public static Block inventoryScannerField;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block ironFence;
	@RegisterItemBlock public static Block keycardReader;
	@RegisterItemBlock public static Block keypad;
	public static Block keypadChest;
	@RegisterItemBlock public static Block keypadFurnace;
	@RegisterItemBlock public static Block laserBlock;
	public static Block laserField;
	@RegisterItemBlock public static Block motionActivatedLight;
	@OwnableTE @RegisterItemBlock public static Block panicButton;
	@RegisterItemBlock public static Block portableRadar;
	@RegisterItemBlock public static Block protecto;
	@OwnableTE public static Block reinforcedDoor;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block reinforcedFencegate;
	@RegisterItemBlock public static Block retinalScanner;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block sandMine;
	public static Block scannerDoor;
	public static Block secretOakSign;
	public static Block secretOakWallSign;
	public static Block secretSpruceSign;
	public static Block secretSpruceWallSign;
	public static Block secretBirchSign;
	public static Block secretBirchWallSign;
	public static Block secretJungleSign;
	public static Block secretJungleWallSign;
	public static Block secretAcaciaSign;
	public static Block secretAcaciaWallSign;
	public static Block secretDarkOakSign;
	public static Block secretDarkOakWallSign;
	@RegisterItemBlock public static Block securityCamera;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block stairsCrystalQuartz;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block stoneMine;
	@OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block trackMine;
	@OwnableTE @RegisterItemBlock(SCItemGroup.TECHNICAL) public static Block trophySystem;
	@RegisterItemBlock public static Block usernameLogger;
	@OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static MineBlock mine;
	public static FlowingFluidBlock fakeWaterBlock;
	public static FlowingFluidBlock fakeLavaBlock;

	//Reinforced Blocks (ordered by vanilla building blocks creative tab order)
	@OwnableTE @Reinforced(hasPage=true) public static Block reinforcedStone;
	@OwnableTE @Reinforced public static Block reinforcedGranite;
	@OwnableTE @Reinforced public static Block reinforcedPolishedGranite;
	@OwnableTE @Reinforced public static Block reinforcedDiorite;
	@OwnableTE @Reinforced public static Block reinforcedPolishedDiorite;
	@OwnableTE @Reinforced public static Block reinforcedAndesite;
	@OwnableTE @Reinforced public static Block reinforcedPolishedAndesite;
	@OwnableTE @Reinforced public static Block reinforcedDirt;
	@OwnableTE @Reinforced public static Block reinforcedCobblestone;
	@OwnableTE @Reinforced public static Block reinforcedOakPlanks;
	@OwnableTE @Reinforced public static Block reinforcedSprucePlanks;
	@OwnableTE @Reinforced public static Block reinforcedBirchPlanks;
	@OwnableTE @Reinforced public static Block reinforcedJunglePlanks;
	@OwnableTE @Reinforced public static Block reinforcedAcaciaPlanks;
	@OwnableTE @Reinforced public static Block reinforcedDarkOakPlanks;
	@OwnableTE @Reinforced public static Block reinforcedSand;
	@OwnableTE @Reinforced public static Block reinforcedRedSand;
	@OwnableTE @Reinforced public static Block reinforcedGravel;
	@OwnableTE @Reinforced public static Block reinforcedOakLog;
	@OwnableTE @Reinforced public static Block reinforcedSpruceLog;
	@OwnableTE @Reinforced public static Block reinforcedBirchLog;
	@OwnableTE @Reinforced public static Block reinforcedJungleLog;
	@OwnableTE @Reinforced public static Block reinforcedAcaciaLog;
	@OwnableTE @Reinforced public static Block reinforcedDarkOakLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedOakLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedSpruceLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedBirchLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedJungleLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedAcaciaLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedDarkOakLog;
	@OwnableTE @Reinforced public static Block reinforcedStrippedOakWood;
	@OwnableTE @Reinforced public static Block reinforcedStrippedSpruceWood;
	@OwnableTE @Reinforced public static Block reinforcedStrippedBirchWood;
	@OwnableTE @Reinforced public static Block reinforcedStrippedJungleWood;
	@OwnableTE @Reinforced public static Block reinforcedStrippedAcaciaWood;
	@OwnableTE @Reinforced public static Block reinforcedStrippedDarkOakWood;
	@OwnableTE @Reinforced public static Block reinforcedOakWood;
	@OwnableTE @Reinforced public static Block reinforcedSpruceWood;
	@OwnableTE @Reinforced public static Block reinforcedBirchWood;
	@OwnableTE @Reinforced public static Block reinforcedJungleWood;
	@OwnableTE @Reinforced public static Block reinforcedAcaciaWood;
	@OwnableTE @Reinforced public static Block reinforcedDarkOakWood;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedGlass;
	@OwnableTE @Reinforced public static Block reinforcedLapisBlock;
	@OwnableTE @Reinforced public static Block reinforcedSandstone;
	@OwnableTE @Reinforced public static Block reinforcedChiseledSandstone;
	@OwnableTE @Reinforced public static Block reinforcedCutSandstone;
	@OwnableTE @Reinforced public static Block reinforcedWhiteWool;
	@OwnableTE @Reinforced public static Block reinforcedOrangeWool;
	@OwnableTE @Reinforced public static Block reinforcedMagentaWool;
	@OwnableTE @Reinforced public static Block reinforcedLightBlueWool;
	@OwnableTE @Reinforced public static Block reinforcedYellowWool;
	@OwnableTE @Reinforced public static Block reinforcedLimeWool;
	@OwnableTE @Reinforced public static Block reinforcedPinkWool;
	@OwnableTE @Reinforced public static Block reinforcedGrayWool;
	@OwnableTE @Reinforced public static Block reinforcedLightGrayWool;
	@OwnableTE @Reinforced public static Block reinforcedCyanWool;
	@OwnableTE @Reinforced public static Block reinforcedPurpleWool;
	@OwnableTE @Reinforced public static Block reinforcedBlueWool;
	@OwnableTE @Reinforced public static Block reinforcedBrownWool;
	@OwnableTE @Reinforced public static Block reinforcedGreenWool;
	@OwnableTE @Reinforced public static Block reinforcedRedWool;
	@OwnableTE @Reinforced public static Block reinforcedBlackWool;
	@OwnableTE @Reinforced public static Block reinforcedGoldBlock;
	@OwnableTE @Reinforced public static Block reinforcedIronBlock;
	@OwnableTE @Reinforced public static Block reinforcedOakSlab;
	@OwnableTE @Reinforced public static Block reinforcedSpruceSlab;
	@OwnableTE @Reinforced public static Block reinforcedBirchSlab;
	@OwnableTE @Reinforced public static Block reinforcedJungleSlab;
	@OwnableTE @Reinforced public static Block reinforcedAcaciaSlab;
	@OwnableTE @Reinforced public static Block reinforcedDarkOakSlab;
	@OwnableTE @Reinforced public static Block reinforcedNormalStoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedSmoothStoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedSandstoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedCutSandstoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedCobblestoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedStoneBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedNetherBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedQuartzSlab;
	@OwnableTE @Reinforced public static Block reinforcedRedSandstoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedCutRedSandstoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedPurpurSlab;
	@OwnableTE @Reinforced public static Block reinforcedPrismarineSlab;
	@OwnableTE @Reinforced public static Block reinforcedPrismarineBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedDarkPrismarineSlab;
	@OwnableTE @Reinforced public static Block reinforcedSmoothQuartz;
	@OwnableTE @Reinforced public static Block reinforcedSmoothRedSandstone;
	@OwnableTE @Reinforced public static Block reinforcedSmoothSandstone;
	@OwnableTE @Reinforced public static Block reinforcedSmoothStone;
	@OwnableTE @Reinforced public static Block reinforcedBricks;
	@OwnableTE @Reinforced public static Block reinforcedBookshelf;
	@OwnableTE @Reinforced public static Block reinforcedMossyCobblestone;
	@OwnableTE @Reinforced public static Block reinforcedObsidian;
	@OwnableTE @Reinforced public static Block reinforcedPurpurBlock;
	@OwnableTE @Reinforced public static Block reinforcedPurpurPillar;
	@OwnableTE @Reinforced public static Block reinforcedPurpurStairs;
	@OwnableTE @Reinforced public static Block reinforcedOakStairs;
	@OwnableTE @Reinforced public static Block reinforcedDiamondBlock;
	@OwnableTE @Reinforced public static Block reinforcedCobblestoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedNetherrack;
	@OwnableTE @Reinforced public static Block reinforcedGlowstone;
	@OwnableTE @Reinforced public static Block reinforcedStoneBricks;
	@OwnableTE @Reinforced public static Block reinforcedMossyStoneBricks;
	@OwnableTE @Reinforced public static Block reinforcedCrackedStoneBricks;
	@OwnableTE @Reinforced public static Block reinforcedChiseledStoneBricks;
	@OwnableTE @Reinforced public static Block reinforcedBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedStoneBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedNetherBricks;
	@OwnableTE @Reinforced public static Block reinforcedNetherBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedEndStone;
	@OwnableTE @Reinforced public static Block reinforcedEndStoneBricks;
	@OwnableTE @Reinforced public static Block reinforcedSandstoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedEmeraldBlock;
	@OwnableTE @Reinforced public static Block reinforcedSpruceStairs;
	@OwnableTE @Reinforced public static Block reinforcedBirchStairs;
	@OwnableTE @Reinforced public static Block reinforcedJungleStairs;
	@OwnableTE @Reinforced public static Block reinforcedChiseledQuartz;
	@OwnableTE @Reinforced public static Block reinforcedQuartz;
	@OwnableTE @Reinforced public static Block reinforcedQuartzPillar;
	@OwnableTE @Reinforced public static Block reinforcedQuartzStairs;
	@OwnableTE @Reinforced public static Block reinforcedWhiteTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedOrangeTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedMagentaTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedLightBlueTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedYellowTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedLimeTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedPinkTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedGrayTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedLightGrayTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedCyanTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedPurpleTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedBlueTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedBrownTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedGreenTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedRedTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedBlackTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedTerracotta;
	@OwnableTE @Reinforced public static Block reinforcedCoalBlock;
	@OwnableTE @Reinforced public static Block reinforcedAcaciaStairs;
	@OwnableTE @Reinforced public static Block reinforcedDarkOakStairs;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedWhiteStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedOrangeStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedMagentaStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedLightBlueStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedYellowStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedLimeStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedPinkStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedGrayStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedLightGrayStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedCyanStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedPurpleStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedBlueStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedBrownStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedGreenStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedRedStainedGlass;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedBlackStainedGlass;
	@OwnableTE @Reinforced public static Block reinforcedPrismarine;
	@OwnableTE @Reinforced public static Block reinforcedPrismarineBricks;
	@OwnableTE @Reinforced public static Block reinforcedDarkPrismarine;
	@OwnableTE @Reinforced public static Block reinforcedPrismarineStairs;
	@OwnableTE @Reinforced public static Block reinforcedPrismarineBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedDarkPrismarineStairs;
	@OwnableTE @Reinforced public static Block reinforcedSeaLantern;
	@OwnableTE @Reinforced public static Block reinforcedRedSandstone;
	@OwnableTE @Reinforced public static Block reinforcedChiseledRedSandstone;
	@OwnableTE @Reinforced public static Block reinforcedCutRedSandstone;
	@OwnableTE @Reinforced public static Block reinforcedRedSandstoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedRedNetherBricks;
	@OwnableTE @Reinforced public static Block reinforcedBoneBlock;
	@OwnableTE @Reinforced public static Block reinforcedWhiteConcrete;
	@OwnableTE @Reinforced public static Block reinforcedOrangeConcrete;
	@OwnableTE @Reinforced public static Block reinforcedMagentaConcrete;
	@OwnableTE @Reinforced public static Block reinforcedLightBlueConcrete;
	@OwnableTE @Reinforced public static Block reinforcedYellowConcrete;
	@OwnableTE @Reinforced public static Block reinforcedLimeConcrete;
	@OwnableTE @Reinforced public static Block reinforcedPinkConcrete;
	@OwnableTE @Reinforced public static Block reinforcedGrayConcrete;
	@OwnableTE @Reinforced public static Block reinforcedLightGrayConcrete;
	@OwnableTE @Reinforced public static Block reinforcedCyanConcrete;
	@OwnableTE @Reinforced public static Block reinforcedPurpleConcrete;
	@OwnableTE @Reinforced public static Block reinforcedBlueConcrete;
	@OwnableTE @Reinforced public static Block reinforcedBrownConcrete;
	@OwnableTE @Reinforced public static Block reinforcedGreenConcrete;
	@OwnableTE @Reinforced public static Block reinforcedRedConcrete;
	@OwnableTE @Reinforced public static Block reinforcedBlackConcrete;
	@OwnableTE @Reinforced public static Block reinforcedPolishedGraniteStairs;
	@OwnableTE @Reinforced public static Block reinforcedSmoothRedSandstoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedMossyStoneBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedPolishedDioriteStairs;
	@OwnableTE @Reinforced public static Block reinforcedMossyCobblestoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedEndStoneBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedStoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedSmoothSandstoneStairs;
	@OwnableTE @Reinforced public static Block reinforcedSmoothQuartzStairs;
	@OwnableTE @Reinforced public static Block reinforcedGraniteStairs;
	@OwnableTE @Reinforced public static Block reinforcedAndesiteStairs;
	@OwnableTE @Reinforced public static Block reinforcedRedNetherBrickStairs;
	@OwnableTE @Reinforced public static Block reinforcedPolishedAndesiteStairs;
	@OwnableTE @Reinforced public static Block reinforcedDioriteStairs;
	@OwnableTE @Reinforced public static Block reinforcedPolishedGraniteSlab;
	@OwnableTE @Reinforced public static Block reinforcedSmoothRedSandstoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedMossyStoneBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedPolishedDioriteSlab;
	@OwnableTE @Reinforced public static Block reinforcedMossyCobblestoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedEndStoneBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedSmoothSandstoneSlab;
	@OwnableTE @Reinforced public static Block reinforcedSmoothQuartzSlab;
	@OwnableTE @Reinforced public static Block reinforcedGraniteSlab;
	@OwnableTE @Reinforced public static Block reinforcedAndesiteSlab;
	@OwnableTE @Reinforced public static Block reinforcedRedNetherBrickSlab;
	@OwnableTE @Reinforced public static Block reinforcedPolishedAndesiteSlab;
	@OwnableTE @Reinforced public static Block reinforcedDioriteSlab;
	//ordered by vanilla decoration blocks creative tab order
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedIronBars;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedGlassPane;
	@OwnableTE @Reinforced public static Block reinforcedWhiteCarpet;
	@OwnableTE @Reinforced public static Block reinforcedOrangeCarpet;
	@OwnableTE @Reinforced public static Block reinforcedMagentaCarpet;
	@OwnableTE @Reinforced public static Block reinforcedLightBlueCarpet;
	@OwnableTE @Reinforced public static Block reinforcedYellowCarpet;
	@OwnableTE @Reinforced public static Block reinforcedLimeCarpet;
	@OwnableTE @Reinforced public static Block reinforcedPinkCarpet;
	@OwnableTE @Reinforced public static Block reinforcedGrayCarpet;
	@OwnableTE @Reinforced public static Block reinforcedLightGrayCarpet;
	@OwnableTE @Reinforced public static Block reinforcedCyanCarpet;
	@OwnableTE @Reinforced public static Block reinforcedPurpleCarpet;
	@OwnableTE @Reinforced public static Block reinforcedBlueCarpet;
	@OwnableTE @Reinforced public static Block reinforcedBrownCarpet;
	@OwnableTE @Reinforced public static Block reinforcedGreenCarpet;
	@OwnableTE @Reinforced public static Block reinforcedRedCarpet;
	@OwnableTE @Reinforced public static Block reinforcedBlackCarpet;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedWhiteStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedOrangeStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedMagentaStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedLightBlueStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedYellowStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedLimeStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedPinkStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedGrayStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedLightGrayStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedCyanStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedPurpleStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedBlueStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedBrownStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedGreenStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedRedStainedGlassPane;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedBlackStainedGlassPane;
	//misc
	@Reinforced(tint=0x0E7063) public static Block reinforcedChiseledCrystalQuartz;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartz;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartzPillar;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartzSlab;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartzStairs;
	@OwnableTE @Reinforced(hasTint=false) public static Block reinforcedIronTrapdoor;
	@Reinforced(hasPage=true) public static Block reinforcedStonePressurePlate;
	@Reinforced public static Block reinforcedOakPressurePlate;
	@Reinforced public static Block reinforcedSprucePressurePlate;
	@Reinforced public static Block reinforcedBirchPressurePlate;
	@Reinforced public static Block reinforcedJunglePressurePlate;
	@Reinforced public static Block reinforcedAcaciaPressurePlate;
	@Reinforced public static Block reinforcedDarkOakPressurePlate;
	@OwnableTE @Reinforced public static Block reinforcedRedstoneBlock;
	@OwnableTE public static Block horizontalReinforcedIronBars;

	//Fluids
	public static FlowingFluid flowingFakeWater;
	public static FlowingFluid fakeWater;
	public static FlowingFluid flowingFakeLava;
	public static FlowingFluid fakeLava;

	//Items
	public static Item adminTool;
	public static Item briefcase;
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
	public static Item keyPanel;
	public static Item limitedUseKeycard;
	public static Item reinforcedDoorItem;
	public static Item remoteAccessMine;
	public static Item remoteAccessSentry;
	public static Item scannerDoorItem;
	public static Item scManual;
	public static Item secretOakSignItem;
	public static Item secretSpruceSignItem;
	public static Item secretBirchSignItem;
	public static Item secretJungleSignItem;
	public static Item secretAcaciaSignItem;
	public static Item secretDarkOakSignItem;
	public static Item sentry;
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

	//Modules
	public static ModuleItem blacklistModule;
	public static ModuleItem disguiseModule;
	public static ModuleItem harmingModule;
	public static ModuleItem redstoneModule;
	public static ModuleItem smartModule;
	public static ModuleItem storageModule;
	public static ModuleItem whitelistModule;

	//Tile entity typses
	@ObjectHolder(SecurityCraft.MODID + ":ownable")
	public static TileEntityType<OwnableTileEntity> teTypeOwnable;
	@ObjectHolder(SecurityCraft.MODID + ":abstract")
	public static TileEntityType<SecurityCraftTileEntity> teTypeAbstract;
	@ObjectHolder(SecurityCraft.MODID + ":keypad")
	public static TileEntityType<KeypadTileEntity> teTypeKeypad;
	@ObjectHolder(SecurityCraft.MODID + ":laser_block")
	public static TileEntityType<LaserBlockTileEntity> teTypeLaserBlock;
	@ObjectHolder(SecurityCraft.MODID + ":cage_trap")
	public static TileEntityType<CageTrapTileEntity> teTypeCageTrap;
	@ObjectHolder(SecurityCraft.MODID + ":keycard_reader")
	public static TileEntityType<KeycardReaderTileEntity> teTypeKeycardReader;
	@ObjectHolder(SecurityCraft.MODID + ":inventory_scanner")
	public static TileEntityType<InventoryScannerTileEntity> teTypeInventoryScanner;
	@ObjectHolder(SecurityCraft.MODID + ":portable_radar")
	public static TileEntityType<PortableRadarTileEntity> teTypePortableRadar;
	@ObjectHolder(SecurityCraft.MODID + ":security_camera")
	public static TileEntityType<SecurityCameraTileEntity> teTypeSecurityCamera;
	@ObjectHolder(SecurityCraft.MODID + ":username_logger")
	public static TileEntityType<UsernameLoggerTileEntity> teTypeUsernameLogger;
	@ObjectHolder(SecurityCraft.MODID + ":retinal_scanner")
	public static TileEntityType<RetinalScannerTileEntity> teTypeRetinalScanner;
	@ObjectHolder(SecurityCraft.MODID + ":keypad_chest")
	public static TileEntityType<KeypadChestTileEntity> teTypeKeypadChest;
	@ObjectHolder(SecurityCraft.MODID + ":alarm")
	public static TileEntityType<AlarmTileEntity> teTypeAlarm;
	@ObjectHolder(SecurityCraft.MODID + ":claymore")
	public static TileEntityType<ClaymoreTileEntity> teTypeClaymore;
	@ObjectHolder(SecurityCraft.MODID + ":keypad_furnace")
	public static TileEntityType<KeypadFurnaceTileEntity> teTypeKeypadFurnace;
	@ObjectHolder(SecurityCraft.MODID + ":ims")
	public static TileEntityType<IMSTileEntity> teTypeIms;
	@ObjectHolder(SecurityCraft.MODID + ":protecto")
	public static TileEntityType<ProtectoTileEntity> teTypeProtecto;
	@ObjectHolder(SecurityCraft.MODID + ":scanner_door")
	public static TileEntityType<ScannerDoorTileEntity> teTypeScannerDoor;
	@ObjectHolder(SecurityCraft.MODID + ":secret_sign")
	public static TileEntityType<SecretSignTileEntity> teTypeSecretSign;
	@ObjectHolder(SecurityCraft.MODID + ":motion_light")
	public static TileEntityType<MotionActivatedLightTileEntity> teTypeMotionLight;
	@ObjectHolder(SecurityCraft.MODID + ":track_mine")
	public static TileEntityType<TrackMineTileEntity> teTypeTrackMine;
	@ObjectHolder(SecurityCraft.MODID + ":trophy_system")
	public static TileEntityType<TrophySystemTileEntity> teTypeTrophySystem;
	@ObjectHolder(SecurityCraft.MODID + ":block_pocket_manager")
	public static TileEntityType<BlockPocketManagerTileEntity> teTypeBlockPocketManager;
	@ObjectHolder(SecurityCraft.MODID + ":block_pocket")
	public static TileEntityType<BlockPocketTileEntity> teTypeBlockPocket;
	@ObjectHolder(SecurityCraft.MODID + ":reinforced_pressure_plate")
	public static TileEntityType<ReinforcedPressurePlateTileEntity> teTypeReinforcedPressurePlate;

	//Entity types
	@ObjectHolder(SecurityCraft.MODID + ":bouncingbetty")
	public static EntityType<BouncingBettyEntity> eTypeBouncingBetty;
	@ObjectHolder(SecurityCraft.MODID + ":taserbullet")
	public static EntityType<TaserBulletEntity> eTypeTaserBullet;
	@ObjectHolder(SecurityCraft.MODID + ":imsbomb")
	public static EntityType<IMSBombEntity> eTypeImsBomb;
	@ObjectHolder(SecurityCraft.MODID + ":securitycamera")
	public static EntityType<SecurityCameraEntity> eTypeSecurityCamera;
	@ObjectHolder(SecurityCraft.MODID + ":sentry")
	public static EntityType<SentryEntity> eTypeSentry;
	@ObjectHolder(SecurityCraft.MODID + ":bullet")
	public static EntityType<BulletEntity> eTypeBullet;

	//Container types
	@ObjectHolder(SecurityCraft.MODID + ":block_reinforcer")
	public static ContainerType<BlockReinforcerContainer> cTypeBlockReinforcer;
	@ObjectHolder(SecurityCraft.MODID + ":briefcase")
	public static ContainerType<GenericContainer> cTypeBriefcase;
	@ObjectHolder(SecurityCraft.MODID + ":briefcase_inventory")
	public static ContainerType<BriefcaseContainer> cTypeBriefcaseInventory;
	@ObjectHolder(SecurityCraft.MODID + ":briefcase_setup")
	public static ContainerType<GenericContainer> cTypeBriefcaseSetup;
	@ObjectHolder(SecurityCraft.MODID + ":customize_block")
	public static ContainerType<CustomizeBlockContainer> cTypeCustomizeBlock;
	@ObjectHolder(SecurityCraft.MODID + ":disguise_module")
	public static ContainerType<DisguiseModuleContainer> cTypeDisguiseModule;
	@ObjectHolder(SecurityCraft.MODID + ":inventory_scanner")
	public static ContainerType<InventoryScannerContainer> cTypeInventoryScanner;
	@ObjectHolder(SecurityCraft.MODID + ":keypad_furnace")
	public static ContainerType<KeypadFurnaceContainer> cTypeKeypadFurnace;
	@ObjectHolder(SecurityCraft.MODID + ":check_password")
	public static ContainerType<GenericTEContainer> cTypeCheckPassword;
	@ObjectHolder(SecurityCraft.MODID + ":set_password")
	public static ContainerType<GenericTEContainer> cTypeSetPassword;
	@ObjectHolder(SecurityCraft.MODID + ":username_logger")
	public static ContainerType<GenericTEContainer> cTypeUsernameLogger;
	@ObjectHolder(SecurityCraft.MODID + ":ims")
	public static ContainerType<GenericTEContainer> cTypeIMS;
	@ObjectHolder(SecurityCraft.MODID + ":keycard_setup")
	public static ContainerType<GenericTEContainer> cTypeKeycardSetup;
	@ObjectHolder(SecurityCraft.MODID + ":key_changer")
	public static ContainerType<GenericTEContainer> cTypeKeyChanger;
	@ObjectHolder(SecurityCraft.MODID + ":block_pocket_manager")
	public static ContainerType<GenericTEContainer> cTypeBlockPocketManager;
}
