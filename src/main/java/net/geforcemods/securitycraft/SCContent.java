package net.geforcemods.securitycraft;

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
import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.tileentity.ClaymoreTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.LaserBlockTileEntity;
import net.geforcemods.securitycraft.tileentity.UsernameLoggerTileEntity;
import net.geforcemods.securitycraft.tileentity.MotionActivatedLightTileEntity;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.tileentity.ProtectoTileEntity;
import net.geforcemods.securitycraft.tileentity.ReinforcedPressurePlateTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.ScannerDoorTileEntity;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.tileentity.TrackMineTileEntity;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.Ownable;
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
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block bouncingBetty;
	@RegisterItemBlock public static Block cageTrap;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block chiseledCrystalQuartz;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block crystalQuartz;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block crystalQuartzPillar;
	@RegisterItemBlock(SCItemGroup.DECORATION) public static Block crystalQuartzSlab;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block claymore;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block cobblestoneMine;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block diamondOreMine;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block dirtMine;
	@Ownable @RegisterItemBlock public static Block frame;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block furnaceMine;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block gravelMine;
	@RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block ims;
	@RegisterItemBlock public static Block inventoryScanner;
	public static Block inventoryScannerField;
	@Ownable @RegisterItemBlock(SCItemGroup.DECORATION) public static Block ironFence;
	@RegisterItemBlock public static Block keycardReader;
	@RegisterItemBlock public static Block keypad;
	public static Block keypadChest;
	@RegisterItemBlock public static Block keypadFurnace;
	@RegisterItemBlock public static Block laserBlock;
	public static Block laserField;
	@RegisterItemBlock public static Block motionActivatedLight;
	@Ownable @RegisterItemBlock public static Block panicButton;
	@RegisterItemBlock public static Block portableRadar;
	@RegisterItemBlock public static Block protecto;
	@Ownable public static Block reinforcedDoor;
	@Ownable @RegisterItemBlock(SCItemGroup.DECORATION) public static Block reinforcedFencegate;
	@RegisterItemBlock public static Block retinalScanner;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block sandMine;
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
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block stoneMine;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block trackMine;
	@Ownable @RegisterItemBlock(SCItemGroup.TECHNICAL) public static Block trophySystem;
	@RegisterItemBlock public static Block usernameLogger;
	@Ownable @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static MineBlock mine;
	public static FlowingFluidBlock fakeWaterBlock;
	public static FlowingFluidBlock fakeLavaBlock;

	//Reinforced Blocks (ordered by vanilla building blocks creative tab order)
	@Ownable @Reinforced(hasPage=true) public static Block reinforcedStone;
	@Ownable @Reinforced public static Block reinforcedGranite;
	@Ownable @Reinforced public static Block reinforcedPolishedGranite;
	@Ownable @Reinforced public static Block reinforcedDiorite;
	@Ownable @Reinforced public static Block reinforcedPolishedDiorite;
	@Ownable @Reinforced public static Block reinforcedAndesite;
	@Ownable @Reinforced public static Block reinforcedPolishedAndesite;
	@Ownable @Reinforced public static Block reinforcedDirt;
	@Ownable @Reinforced public static Block reinforcedCobblestone;
	@Ownable @Reinforced public static Block reinforcedOakPlanks;
	@Ownable @Reinforced public static Block reinforcedSprucePlanks;
	@Ownable @Reinforced public static Block reinforcedBirchPlanks;
	@Ownable @Reinforced public static Block reinforcedJunglePlanks;
	@Ownable @Reinforced public static Block reinforcedAcaciaPlanks;
	@Ownable @Reinforced public static Block reinforcedDarkOakPlanks;
	@Ownable @Reinforced public static Block reinforcedSand;
	@Ownable @Reinforced public static Block reinforcedRedSand;
	@Ownable @Reinforced public static Block reinforcedGravel;
	@Ownable @Reinforced public static Block reinforcedOakLog;
	@Ownable @Reinforced public static Block reinforcedSpruceLog;
	@Ownable @Reinforced public static Block reinforcedBirchLog;
	@Ownable @Reinforced public static Block reinforcedJungleLog;
	@Ownable @Reinforced public static Block reinforcedAcaciaLog;
	@Ownable @Reinforced public static Block reinforcedDarkOakLog;
	@Ownable @Reinforced public static Block reinforcedStrippedOakLog;
	@Ownable @Reinforced public static Block reinforcedStrippedSpruceLog;
	@Ownable @Reinforced public static Block reinforcedStrippedBirchLog;
	@Ownable @Reinforced public static Block reinforcedStrippedJungleLog;
	@Ownable @Reinforced public static Block reinforcedStrippedAcaciaLog;
	@Ownable @Reinforced public static Block reinforcedStrippedDarkOakLog;
	@Ownable @Reinforced public static Block reinforcedStrippedOakWood;
	@Ownable @Reinforced public static Block reinforcedStrippedSpruceWood;
	@Ownable @Reinforced public static Block reinforcedStrippedBirchWood;
	@Ownable @Reinforced public static Block reinforcedStrippedJungleWood;
	@Ownable @Reinforced public static Block reinforcedStrippedAcaciaWood;
	@Ownable @Reinforced public static Block reinforcedStrippedDarkOakWood;
	@Ownable @Reinforced public static Block reinforcedOakWood;
	@Ownable @Reinforced public static Block reinforcedSpruceWood;
	@Ownable @Reinforced public static Block reinforcedBirchWood;
	@Ownable @Reinforced public static Block reinforcedJungleWood;
	@Ownable @Reinforced public static Block reinforcedAcaciaWood;
	@Ownable @Reinforced public static Block reinforcedDarkOakWood;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedGlass;
	@Ownable @Reinforced public static Block reinforcedLapisBlock;
	@Ownable @Reinforced public static Block reinforcedSandstone;
	@Ownable @Reinforced public static Block reinforcedChiseledSandstone;
	@Ownable @Reinforced public static Block reinforcedCutSandstone;
	@Ownable @Reinforced public static Block reinforcedWhiteWool;
	@Ownable @Reinforced public static Block reinforcedOrangeWool;
	@Ownable @Reinforced public static Block reinforcedMagentaWool;
	@Ownable @Reinforced public static Block reinforcedLightBlueWool;
	@Ownable @Reinforced public static Block reinforcedYellowWool;
	@Ownable @Reinforced public static Block reinforcedLimeWool;
	@Ownable @Reinforced public static Block reinforcedPinkWool;
	@Ownable @Reinforced public static Block reinforcedGrayWool;
	@Ownable @Reinforced public static Block reinforcedLightGrayWool;
	@Ownable @Reinforced public static Block reinforcedCyanWool;
	@Ownable @Reinforced public static Block reinforcedPurpleWool;
	@Ownable @Reinforced public static Block reinforcedBlueWool;
	@Ownable @Reinforced public static Block reinforcedBrownWool;
	@Ownable @Reinforced public static Block reinforcedGreenWool;
	@Ownable @Reinforced public static Block reinforcedRedWool;
	@Ownable @Reinforced public static Block reinforcedBlackWool;
	@Ownable @Reinforced public static Block reinforcedGoldBlock;
	@Ownable @Reinforced public static Block reinforcedIronBlock;
	@Ownable @Reinforced public static Block reinforcedOakSlab;
	@Ownable @Reinforced public static Block reinforcedSpruceSlab;
	@Ownable @Reinforced public static Block reinforcedBirchSlab;
	@Ownable @Reinforced public static Block reinforcedJungleSlab;
	@Ownable @Reinforced public static Block reinforcedAcaciaSlab;
	@Ownable @Reinforced public static Block reinforcedDarkOakSlab;
	@Ownable @Reinforced public static Block reinforcedNormalStoneSlab;
	@Ownable @Reinforced public static Block reinforcedSmoothStoneSlab;
	@Ownable @Reinforced public static Block reinforcedSandstoneSlab;
	@Ownable @Reinforced public static Block reinforcedCobblestoneSlab;
	@Ownable @Reinforced public static Block reinforcedBrickSlab;
	@Ownable @Reinforced public static Block reinforcedStoneBrickSlab;
	@Ownable @Reinforced public static Block reinforcedNetherBrickSlab;
	@Ownable @Reinforced public static Block reinforcedQuartzSlab;
	@Ownable @Reinforced public static Block reinforcedRedSandstoneSlab;
	@Ownable @Reinforced public static Block reinforcedPurpurSlab;
	@Ownable @Reinforced public static Block reinforcedPrismarineSlab;
	@Ownable @Reinforced public static Block reinforcedPrismarineBrickSlab;
	@Ownable @Reinforced public static Block reinforcedDarkPrismarineSlab;
	@Ownable @Reinforced public static Block reinforcedSmoothQuartz;
	@Ownable @Reinforced public static Block reinforcedSmoothRedSandstone;
	@Ownable @Reinforced public static Block reinforcedSmoothSandstone;
	@Ownable @Reinforced public static Block reinforcedSmoothStone;
	@Ownable @Reinforced public static Block reinforcedBricks;
	@Ownable @Reinforced public static Block reinforcedMossyCobblestone;
	@Ownable @Reinforced public static Block reinforcedObsidian;
	@Ownable @Reinforced public static Block reinforcedPurpurBlock;
	@Ownable @Reinforced public static Block reinforcedPurpurPillar;
	@Ownable @Reinforced public static Block reinforcedPurpurStairs;
	@Ownable @Reinforced public static Block reinforcedOakStairs;
	@Ownable @Reinforced public static Block reinforcedDiamondBlock;
	@Ownable @Reinforced public static Block reinforcedCobblestoneStairs;
	@Ownable @Reinforced public static Block reinforcedNetherrack;
	@Ownable @Reinforced public static Block reinforcedGlowstone;
	@Ownable @Reinforced public static Block reinforcedStoneBricks;
	@Ownable @Reinforced public static Block reinforcedMossyStoneBricks;
	@Ownable @Reinforced public static Block reinforcedCrackedStoneBricks;
	@Ownable @Reinforced public static Block reinforcedChiseledStoneBricks;
	@Ownable @Reinforced public static Block reinforcedBrickStairs;
	@Ownable @Reinforced public static Block reinforcedStoneBrickStairs;
	@Ownable @Reinforced public static Block reinforcedNetherBricks;
	@Ownable @Reinforced public static Block reinforcedNetherBrickStairs;
	@Ownable @Reinforced public static Block reinforcedEndStone;
	@Ownable @Reinforced public static Block reinforcedEndStoneBricks;
	@Ownable @Reinforced public static Block reinforcedSandstoneStairs;
	@Ownable @Reinforced public static Block reinforcedEmeraldBlock;
	@Ownable @Reinforced public static Block reinforcedSpruceStairs;
	@Ownable @Reinforced public static Block reinforcedBirchStairs;
	@Ownable @Reinforced public static Block reinforcedJungleStairs;
	@Ownable @Reinforced public static Block reinforcedChiseledQuartz;
	@Ownable @Reinforced public static Block reinforcedQuartz;
	@Ownable @Reinforced public static Block reinforcedQuartzPillar;
	@Ownable @Reinforced public static Block reinforcedQuartzStairs;
	@Ownable @Reinforced public static Block reinforcedWhiteTerracotta;
	@Ownable @Reinforced public static Block reinforcedOrangeTerracotta;
	@Ownable @Reinforced public static Block reinforcedMagentaTerracotta;
	@Ownable @Reinforced public static Block reinforcedLightBlueTerracotta;
	@Ownable @Reinforced public static Block reinforcedYellowTerracotta;
	@Ownable @Reinforced public static Block reinforcedLimeTerracotta;
	@Ownable @Reinforced public static Block reinforcedPinkTerracotta;
	@Ownable @Reinforced public static Block reinforcedGrayTerracotta;
	@Ownable @Reinforced public static Block reinforcedLightGrayTerracotta;
	@Ownable @Reinforced public static Block reinforcedCyanTerracotta;
	@Ownable @Reinforced public static Block reinforcedPurpleTerracotta;
	@Ownable @Reinforced public static Block reinforcedBlueTerracotta;
	@Ownable @Reinforced public static Block reinforcedBrownTerracotta;
	@Ownable @Reinforced public static Block reinforcedGreenTerracotta;
	@Ownable @Reinforced public static Block reinforcedRedTerracotta;
	@Ownable @Reinforced public static Block reinforcedBlackTerracotta;
	@Ownable @Reinforced public static Block reinforcedTerracotta;
	@Ownable @Reinforced public static Block reinforcedCoalBlock;
	@Ownable @Reinforced public static Block reinforcedAcaciaStairs;
	@Ownable @Reinforced public static Block reinforcedDarkOakStairs;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedWhiteStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedOrangeStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedMagentaStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedLightBlueStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedYellowStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedLimeStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedPinkStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedGrayStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedLightGrayStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedCyanStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedPurpleStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedBlueStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedBrownStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedGreenStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedRedStainedGlass;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedBlackStainedGlass;
	@Ownable @Reinforced public static Block reinforcedPrismarine;
	@Ownable @Reinforced public static Block reinforcedPrismarineBricks;
	@Ownable @Reinforced public static Block reinforcedDarkPrismarine;
	@Ownable @Reinforced public static Block reinforcedPrismarineStairs;
	@Ownable @Reinforced public static Block reinforcedPrismarineBrickStairs;
	@Ownable @Reinforced public static Block reinforcedDarkPrismarineStairs;
	@Ownable @Reinforced public static Block reinforcedSeaLantern;
	@Ownable @Reinforced public static Block reinforcedRedSandstone;
	@Ownable @Reinforced public static Block reinforcedChiseledRedSandstone;
	@Ownable @Reinforced public static Block reinforcedCutRedSandstone;
	@Ownable @Reinforced public static Block reinforcedRedSandstoneStairs;
	@Ownable @Reinforced public static Block reinforcedRedNetherBricks;
	@Ownable @Reinforced public static Block reinforcedBoneBlock;
	@Ownable @Reinforced public static Block reinforcedWhiteConcrete;
	@Ownable @Reinforced public static Block reinforcedOrangeConcrete;
	@Ownable @Reinforced public static Block reinforcedMagentaConcrete;
	@Ownable @Reinforced public static Block reinforcedLightBlueConcrete;
	@Ownable @Reinforced public static Block reinforcedYellowConcrete;
	@Ownable @Reinforced public static Block reinforcedLimeConcrete;
	@Ownable @Reinforced public static Block reinforcedPinkConcrete;
	@Ownable @Reinforced public static Block reinforcedGrayConcrete;
	@Ownable @Reinforced public static Block reinforcedLightGrayConcrete;
	@Ownable @Reinforced public static Block reinforcedCyanConcrete;
	@Ownable @Reinforced public static Block reinforcedPurpleConcrete;
	@Ownable @Reinforced public static Block reinforcedBlueConcrete;
	@Ownable @Reinforced public static Block reinforcedBrownConcrete;
	@Ownable @Reinforced public static Block reinforcedGreenConcrete;
	@Ownable @Reinforced public static Block reinforcedRedConcrete;
	@Ownable @Reinforced public static Block reinforcedBlackConcrete;
	@Ownable @Reinforced public static Block reinforcedPolishedGraniteStairs;
	@Ownable @Reinforced public static Block reinforcedSmoothRedSandstoneStairs;
	@Ownable @Reinforced public static Block reinforcedMossyStoneBrickStairs;
	@Ownable @Reinforced public static Block reinforcedPolishedDioriteStairs;
	@Ownable @Reinforced public static Block reinforcedMossyCobblestoneStairs;
	@Ownable @Reinforced public static Block reinforcedEndStoneBrickStairs;
	@Ownable @Reinforced public static Block reinforcedStoneStairs;
	@Ownable @Reinforced public static Block reinforcedSmoothSandstoneStairs;
	@Ownable @Reinforced public static Block reinforcedSmoothQuartzStairs;
	@Ownable @Reinforced public static Block reinforcedGraniteStairs;
	@Ownable @Reinforced public static Block reinforcedAndesiteStairs;
	@Ownable @Reinforced public static Block reinforcedRedNetherBrickStairs;
	@Ownable @Reinforced public static Block reinforcedPolishedAndesiteStairs;
	@Ownable @Reinforced public static Block reinforcedDioriteStairs;
	@Ownable @Reinforced public static Block reinforcedPolishedGraniteSlab;
	@Ownable @Reinforced public static Block reinforcedSmoothRedSandstoneSlab;
	@Ownable @Reinforced public static Block reinforcedMossyStoneBrickSlab;
	@Ownable @Reinforced public static Block reinforcedPolishedDioriteSlab;
	@Ownable @Reinforced public static Block reinforcedMossyCobblestoneSlab;
	@Ownable @Reinforced public static Block reinforcedEndStoneBrickSlab;
	@Ownable @Reinforced public static Block reinforcedSmoothSandstoneSlab;
	@Ownable @Reinforced public static Block reinforcedSmoothQuartzSlab;
	@Ownable @Reinforced public static Block reinforcedGraniteSlab;
	@Ownable @Reinforced public static Block reinforcedAndesiteSlab;
	@Ownable @Reinforced public static Block reinforcedRedNetherBrickSlab;
	@Ownable @Reinforced public static Block reinforcedPolishedAndesiteSlab;
	@Ownable @Reinforced public static Block reinforcedDioriteSlab;
	//ordered by vanilla decoration blocks creative tab order
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedIronBars;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedGlassPane;
	@Ownable @Reinforced public static Block reinforcedWhiteCarpet;
	@Ownable @Reinforced public static Block reinforcedOrangeCarpet;
	@Ownable @Reinforced public static Block reinforcedMagentaCarpet;
	@Ownable @Reinforced public static Block reinforcedLightBlueCarpet;
	@Ownable @Reinforced public static Block reinforcedYellowCarpet;
	@Ownable @Reinforced public static Block reinforcedLimeCarpet;
	@Ownable @Reinforced public static Block reinforcedPinkCarpet;
	@Ownable @Reinforced public static Block reinforcedGrayCarpet;
	@Ownable @Reinforced public static Block reinforcedLightGrayCarpet;
	@Ownable @Reinforced public static Block reinforcedCyanCarpet;
	@Ownable @Reinforced public static Block reinforcedPurpleCarpet;
	@Ownable @Reinforced public static Block reinforcedBlueCarpet;
	@Ownable @Reinforced public static Block reinforcedBrownCarpet;
	@Ownable @Reinforced public static Block reinforcedGreenCarpet;
	@Ownable @Reinforced public static Block reinforcedRedCarpet;
	@Ownable @Reinforced public static Block reinforcedBlackCarpet;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedWhiteStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedOrangeStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedMagentaStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedLightBlueStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedYellowStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedLimeStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedPinkStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedGrayStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedLightGrayStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedCyanStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedPurpleStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedBlueStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedBrownStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedGreenStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedRedStainedGlassPane;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedBlackStainedGlassPane;
	//misc
	@Reinforced(tint=0x0E7063) public static Block reinforcedChiseledCrystalQuartz;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartz;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartzPillar;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartzSlab;
	@Reinforced(tint=0x0E7063) public static Block reinforcedCrystalQuartzStairs;
	@Ownable @Reinforced(hasTint=false) public static Block reinforcedIronTrapdoor;
	@Reinforced(hasPage=true) public static Block reinforcedStonePressurePlate;
	@Reinforced public static Block reinforcedOakPressurePlate;
	@Reinforced public static Block reinforcedSprucePressurePlate;
	@Reinforced public static Block reinforcedBirchPressurePlate;
	@Reinforced public static Block reinforcedJunglePressurePlate;
	@Reinforced public static Block reinforcedAcaciaPressurePlate;
	@Reinforced public static Block reinforcedDarkOakPressurePlate;

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
	public static TileEntityType<TileEntityTrophySystem> teTypeTrophySystem;
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
