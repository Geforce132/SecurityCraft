package net.geforcemods.securitycraft;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedColoredBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedMetals;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedQuartz;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.packets.PacketCChangeStackSize;
import net.geforcemods.securitycraft.network.packets.PacketCCreateLGView;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.network.packets.PacketCRemoveLGView;
import net.geforcemods.securitycraft.network.packets.PacketCSetCameraLocation;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.packets.PacketCSpawnLightning;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketGivePotionEffect;
import net.geforcemods.securitycraft.network.packets.PacketSAddModules;
import net.geforcemods.securitycraft.network.packets.PacketSCheckPassword;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.geforcemods.securitycraft.network.packets.PacketSOpenGui;
import net.geforcemods.securitycraft.network.packets.PacketSRemoveCameraTag;
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSSetOwner;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateSliderValue;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.network.packets.PacketSetBlockAndMetadata;
import net.geforcemods.securitycraft.network.packets.PacketSetBlockMetadata;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.network.packets.PacketSetKeycardLevel;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntityTrackMine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class RegistrationHandler
{
	private static final int[] HARMING_POTIONS = {8268, 8236, 16460, 16428};
	private static final int[] HEALING_POTIONS = {8261, 8229, 16453, 16421};

	public static void registerContent()
	{
		registerBlock(SCContent.laserBlock);
		GameRegistry.registerBlock(SCContent.laserField, SCContent.laserField.getUnlocalizedName().substring(5));
		registerBlock(SCContent.keypad);
		registerBlock(SCContent.mine);
		GameRegistry.registerBlock(SCContent.mineCut,SCContent.mineCut.getUnlocalizedName().substring(5));
		registerBlock(SCContent.dirtMine);
		GameRegistry.registerBlock(SCContent.stoneMine, SCContent.stoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.cobblestoneMine, SCContent.cobblestoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.diamondOreMine, SCContent.diamondOreMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.sandMine, SCContent.sandMine.getUnlocalizedName().substring(5));
		registerBlock(SCContent.furnaceMine);
		registerBlock(SCContent.retinalScanner);
		GameRegistry.registerBlock(SCContent.reinforcedDoor, SCContent.reinforcedDoor.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusLava, SCContent.bogusLava.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusLavaFlowing, SCContent.bogusLavaFlowing.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusWater, SCContent.bogusWater.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusWaterFlowing, SCContent.bogusWaterFlowing.getUnlocalizedName().substring(5));
		registerBlock(SCContent.keycardReader);
		registerBlock(SCContent.ironTrapdoor);
		registerBlock(SCContent.bouncingBetty);
		registerBlock(SCContent.inventoryScanner);
		GameRegistry.registerBlock(SCContent.inventoryScannerField, SCContent.inventoryScannerField.getUnlocalizedName().substring(5));
		registerBlock(SCContent.trackMine);
		registerBlock(SCContent.cageTrap);
		registerBlock(SCContent.portableRadar);
		GameRegistry.registerBlock(SCContent.deactivatedCageTrap, SCContent.deactivatedCageTrap.getUnlocalizedName().substring(5));
		registerReinforcedBlock(SCContent.reinforcedStone);
		registerBlock(SCContent.keypadChest);
		registerBlock(SCContent.usernameLogger);
		registerReinforcedBlock(SCContent.reinforcedGlassPane);
		registerBlock(SCContent.alarm);
		GameRegistry.registerBlock(SCContent.alarmLit, SCContent.alarmLit.getUnlocalizedName().substring(5));
		registerReinforcedBlock(SCContent.reinforcedIronBars);
		registerReinforcedBlock(SCContent.reinforcedSandstone, ItemBlockReinforcedSandstone.class);
		registerReinforcedBlock(SCContent.reinforcedDirt);
		registerReinforcedBlock(SCContent.reinforcedCobblestone);
		registerBlock(SCContent.reinforcedFencegate);
		registerReinforcedBlock(SCContent.reinforcedWoodPlanks, ItemBlockReinforcedPlanks.class);
		registerBlock(SCContent.panicButton);
		registerBlock(SCContent.frame);
		registerBlock(SCContent.claymoreActive);
		GameRegistry.registerBlock(SCContent.claymoreDefused, SCContent.claymoreDefused.getUnlocalizedName().substring(5));
		registerBlock(SCContent.keypadFurnace);
		registerBlock(SCContent.securityCamera);
		GameRegistry.registerBlock(SCContent.reinforcedStairsOak, SCContent.reinforcedStairsOak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsSpruce, SCContent.reinforcedStairsSpruce.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsBirch, SCContent.reinforcedStairsBirch.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsJungle, SCContent.reinforcedStairsJungle.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsAcacia, SCContent.reinforcedStairsAcacia.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsDarkoak, SCContent.reinforcedStairsDarkoak.getUnlocalizedName().substring(5));
		registerBlock(SCContent.reinforcedStairsStone);
		registerBlock(SCContent.reinforcedStairsCobblestone);
		registerBlock(SCContent.reinforcedStairsSandstone);
		registerBlock(SCContent.ironFence);
		registerBlock(SCContent.ims);
		registerReinforcedBlock(SCContent.reinforcedGlass);
		registerBlock(SCContent.reinforcedStainedGlass, ItemBlockReinforcedColoredBlock.class);
		registerBlock(SCContent.reinforcedStainedGlassPanes, ItemBlockReinforcedColoredBlock.class);
		registerBlock(SCContent.reinforcedWoodSlabs, ItemBlockReinforcedSlabs.class, SCContent.reinforcedWoodSlabs, false, ItemBlockReinforcedSlabs.ReinforcedSlabType.WOOD);
		registerBlock(SCContent.reinforcedStoneSlabs, ItemBlockReinforcedSlabs.class, SCContent.reinforcedStoneSlabs, false, ItemBlockReinforcedSlabs.ReinforcedSlabType.OTHER);
		GameRegistry.registerBlock(SCContent.reinforcedDirtSlab, ItemBlockReinforcedSlabs.class, SCContent.reinforcedDirtSlab.getUnlocalizedName().substring(5), SCContent.reinforcedDirtSlab, false, ItemBlockReinforcedSlabs.ReinforcedSlabType.OTHER);
		GameRegistry.registerBlock(SCContent.reinforcedDoubleWoodSlabs, SCContent.reinforcedDoubleWoodSlabs.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedDoubleStoneSlabs, SCContent.reinforcedDoubleStoneSlabs.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedDoubleDirtSlab, SCContent.reinforcedDoubleDirtSlab.getUnlocalizedName().substring(5));
		registerBlock(SCContent.protecto);
		GameRegistry.registerBlock(SCContent.scannerDoor, SCContent.scannerDoor.getUnlocalizedName().substring(5));
		registerReinforcedBlock(SCContent.reinforcedStoneBrick, ItemBlockReinforcedStoneBrick.class);
		registerBlock(SCContent.reinforcedStairsStoneBrick);
		registerBlock(SCContent.reinforcedStairsQuartz);
		registerReinforcedBlock(SCContent.reinforcedMossyCobblestone);
		registerReinforcedBlock(SCContent.reinforcedBrick);
		registerBlock(SCContent.reinforcedStairsBrick);
		registerReinforcedBlock(SCContent.reinforcedNetherBrick);
		registerBlock(SCContent.reinforcedStairsNetherBrick);
		registerReinforcedBlock(SCContent.reinforcedHardenedClay);
		registerReinforcedBlock(SCContent.reinforcedStainedHardenedClay, ItemBlockReinforcedColoredBlock.class);
		registerReinforcedBlock(SCContent.reinforcedOldLogs, ItemBlockReinforcedLog.class);
		registerReinforcedBlock(SCContent.reinforcedNewLogs, ItemBlockReinforcedLog.class);
		registerReinforcedBlock(SCContent.reinforcedMetals, ItemBlockReinforcedMetals.class);
		registerReinforcedBlock(SCContent.reinforcedCompressedBlocks, ItemBlockReinforcedCompressedBlocks.class);
		registerReinforcedBlock(SCContent.reinforcedWool, ItemBlockReinforcedColoredBlock.class);
		registerReinforcedBlock(SCContent.reinforcedQuartz, ItemBlockReinforcedQuartz.class);
		GameRegistry.registerBlock(SCContent.secretSignWall, "secretSignWall");
		GameRegistry.registerBlock(SCContent.secretSignStanding, "secretSignStanding");
		registerBlock(SCContent.motionActivatedLightOff);
		GameRegistry.registerBlock(SCContent.motionActivatedLightOn, "motionActivatedLightOn");
		registerReinforcedBlock(SCContent.reinforcedObsidian);
		registerReinforcedBlock(SCContent.reinforcedNetherrack);
		registerReinforcedBlock(SCContent.reinforcedEndStone);
		registerReinforcedBlock(SCContent.reinforcedCarpet, ItemBlockReinforcedColoredBlock.class);
		registerReinforcedBlock(SCContent.reinforcedGlowstone);
		GameRegistry.registerBlock(SCContent.gravelMine, SCContent.gravelMine.getUnlocalizedName().substring(5));

		registerItem(SCContent.codebreaker);
		registerItem(SCContent.reinforcedDoorItem, SCContent.reinforcedDoorItem.getUnlocalizedName().substring(5));
		registerItem(SCContent.scannerDoorItem, SCContent.scannerDoorItem.getUnlocalizedName().substring(5));
		registerItem(SCContent.universalBlockRemover);
		registerItem(SCContent.keycards);
		registerItem(SCContent.remoteAccessMine);
		registerItem(SCContent.fWaterBucket);
		registerItem(SCContent.fLavaBucket);
		registerItem(SCContent.universalBlockModifier);
		registerItem(SCContent.redstoneModule);
		registerItem(SCContent.whitelistModule);
		registerItem(SCContent.blacklistModule);
		registerItem(SCContent.harmingModule);
		registerItem(SCContent.smartModule);
		registerItem(SCContent.storageModule);
		registerItem(SCContent.disguiseModule);
		registerItem(SCContent.wireCutters);
		registerItem(SCContent.adminTool);
		registerItem(SCContent.keyPanel);
		registerItem(SCContent.cameraMonitor);
		registerItem(SCContent.taser);
		registerItem(SCContent.scManual);
		registerItem(SCContent.universalOwnerChanger);
		registerItem(SCContent.universalBlockReinforcerLvL1);
		registerItem(SCContent.universalBlockReinforcerLvL2);
		registerItem(SCContent.universalBlockReinforcerLvL3);
		registerItem(SCContent.briefcase);
		registerItem(SCContent.universalKeyChanger);
		GameRegistry.registerItem(SCContent.taserPowered, "taserPowered");
		registerItem(SCContent.secretSignItem);
	}

	public static void registerTileEntities()
	{
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
		GameRegistry.registerTileEntity(TileEntityFrame.class, "keypadFrame");
		GameRegistry.registerTileEntity(TileEntityClaymore.class, "claymore");
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, "keypadFurnace");
		GameRegistry.registerTileEntity(TileEntityIMS.class, "ims");
		GameRegistry.registerTileEntity(TileEntityProtecto.class, "protecto");
		GameRegistry.registerTileEntity(CustomizableSCTE.class, "customizableSCTE");
		GameRegistry.registerTileEntity(TileEntityScannerDoor.class, "scannerDoor");
		GameRegistry.registerTileEntity(TileEntitySecretSign.class, "secretSign");
		GameRegistry.registerTileEntity(TileEntityMotionLight.class, "motionLight");
		GameRegistry.registerTileEntity(TileEntityTrackMine.class, "trackMineSC");
	}

	public static void registerRecipes()
	{
		GameRegistry.addRecipe(new ItemStack(SCContent.keyPanel, 1), "III", "IBI", "III", 'I', Blocks.stone_button, 'B', Blocks.heavy_weighted_pressure_plate);
		GameRegistry.addRecipe(new ItemStack(SCContent.frame, 1), "III", "IBI", "I I", 'I', Items.iron_ingot, 'B', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(SCContent.laserBlock, 1), "III", "IBI", "IPI", 'I', Blocks.stone, 'B', Blocks.redstone_block, 'P', Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(SCContent.mine, 3), " I ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedDoorItem, 1), "III", "IDI", "III", 'I', Items.iron_ingot, 'D', Items.iron_door);
		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockRemover, 1), "SII",'I', Items.iron_ingot, 'S', Items.shears);
		GameRegistry.addRecipe(new ItemStack(SCContent.ironTrapdoor, 1), "###", "#P#", "###", '#', Items.iron_ingot, 'P', Blocks.trapdoor);
		GameRegistry.addRecipe(new ItemStack(SCContent.keycardReader, 1), "SSS", "SHS", "SSS", 'S', Blocks.stone, 'H', Blocks.hopper);
		GameRegistry.addRecipe(new ItemStack(SCContent.bouncingBetty, 1), " P ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder, 'P', Blocks.heavy_weighted_pressure_plate);
		GameRegistry.addRecipe(new ItemStack(SCContent.codebreaker, 1), "DTD", "GSG", "RER", 'D', Items.diamond, 'T', Blocks.redstone_torch, 'G', Items.gold_ingot, 'S', Items.nether_star, 'R', Items.redstone, 'E', Items.emerald);

		if(SecurityCraft.config.ableToCraftKeycard1)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycards, 1, 0), "III", "YYY", 'I', Items.iron_ingot, 'Y', Items.gold_ingot);

		if(SecurityCraft.config.ableToCraftKeycard2)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycards, 1, 1), "III", "YYY", 'I', Items.iron_ingot, 'Y', Items.brick);

		if(SecurityCraft.config.ableToCraftKeycard3)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycards, 1, 2), "III", "YYY", 'I', Items.iron_ingot, 'Y', Items.netherbrick);

		if(SecurityCraft.config.ableToCraftKeycard4)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycards, 1, 4), "III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 13));

		if(SecurityCraft.config.ableToCraftKeycard5)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycards, 1, 5), "III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 5));

		if(SecurityCraft.config.ableToCraftLUKeycard)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycards, 1, 3), "III", "LLL", 'I', Items.iron_ingot, 'L', new ItemStack(Items.dye, 1, 4));

		GameRegistry.addRecipe(new ItemStack(SCContent.trackMine, 4), "X X", "X#X", "XGX", 'X', Items.iron_ingot, '#', Items.stick, 'G', Items.gunpowder);
		GameRegistry.addRecipe(new ItemStack(SCContent.portableRadar, 1), "III", "ITI", "IRI", 'I', Items.iron_ingot, 'T', Blocks.redstone_torch, 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(SCContent.remoteAccessMine, 1), " R ", " DG", "S  ", 'R', Blocks.redstone_torch, 'D', Items.diamond, 'G', Items.gold_ingot, 'S', Items.stick);

		for(int i = 0; i < 4; i++)
		{
			GameRegistry.addRecipe(new ItemStack(SCContent.fWaterBucket, 1), "P", "B", 'P', new ItemStack(Items.potionitem, 1, HARMING_POTIONS[i]), 'B', Items.water_bucket);
			GameRegistry.addRecipe(new ItemStack(SCContent.fLavaBucket, 1), "P", "B", 'P', new ItemStack(Items.potionitem, 1, HEALING_POTIONS[i]), 'B', Items.lava_bucket);
		}

		GameRegistry.addRecipe(new ItemStack(SCContent.retinalScanner, 1), "SSS", "SES", "SSS", 'S', Blocks.stone, 'E', Items.ender_eye);
		GameRegistry.addRecipe(new ItemStack(SCContent.inventoryScanner, 1), "SSS", "SLS", "SCS", 'S', Blocks.stone, 'L', SCContent.laserBlock, 'C', Blocks.ender_chest);
		GameRegistry.addRecipe(new ItemStack(SCContent.cageTrap, 1), "BBB", "GRG", "III", 'B', SCContent.reinforcedIronBars, 'G', Items.gold_ingot, 'R', Items.redstone, 'I', Blocks.iron_block);
		GameRegistry.addRecipe(new ItemStack(SCContent.alarm, 1), "GGG", "GNG", "GRG", 'G', SCContent.reinforcedGlass, 'R', Items.redstone, 'N', Blocks.noteblock);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), " I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.fence_gate);
		GameRegistry.addRecipe(new ItemStack(SCContent.wireCutters, 1), "SI ", "I I", " I ", 'I', Items.iron_ingot, 'S', Items.shears);
		GameRegistry.addRecipe(new ItemStack(SCContent.panicButton, 1), " I ", "IBI", " R ", 'I', Items.iron_ingot, 'B', Blocks.stone_button, 'R', Items.redstone);

		GameRegistry.addRecipe(new ItemStack(SCContent.whitelistModule, 1), "III", "IPI", "IPI", 'I', Items.iron_ingot, 'P', Items.paper);
		GameRegistry.addRecipe(new ItemStack(SCContent.blacklistModule, 1), "III", "IPI", "IDI", 'I', Items.iron_ingot, 'P', Items.paper, 'D', new ItemStack(Items.dye, 1, 0));
		GameRegistry.addRecipe(new ItemStack(SCContent.redstoneModule, 1), "III", "IPI", "IRI", 'I', Items.iron_ingot, 'P', Items.paper, 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(SCContent.harmingModule, 1), "III", "IPI", "IAI", 'I', Items.iron_ingot, 'P', Items.paper, 'A', Items.arrow);
		GameRegistry.addRecipe(new ItemStack(SCContent.smartModule, 1), "III", "IPI", "IEI", 'I', Items.iron_ingot, 'P', Items.paper, 'E', Items.ender_pearl);
		GameRegistry.addRecipe(new ItemStack(SCContent.storageModule, 1), "III", "IPI", "ICI", 'I', Items.iron_ingot, 'P', Items.paper, 'C', SCContent.keypadChest);
		GameRegistry.addRecipe(new ItemStack(SCContent.disguiseModule, 1), "III", "IPI", "IAI", 'I', Items.iron_ingot, 'P', Items.paper, 'A', Items.painting);

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockModifier, 1), "ER ", "RI ", "  I", 'E', Items.emerald, 'R', Items.redstone, 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockModifier, 1), " RE", " IR", "I  ", 'E', Items.emerald, 'R', Items.redstone, 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(SCContent.usernameLogger, 1), "SPS", "SRS", "SSS", 'S', Blocks.stone, 'P', SCContent.portableRadar, 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(SCContent.claymoreActive, 1), "HSH", "SBS", "RGR", 'H', Blocks.tripwire_hook, 'S', Items.string, 'B', SCContent.bouncingBetty, 'R', Items.redstone, 'G', Items.gunpowder);
		GameRegistry.addRecipe(new ItemStack(SCContent.ironFence, 1), " I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.fence);

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsStone, 4), "S  ", "SS ", "SSS", 'S', SCContent.reinforcedStone);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsCobblestone, 4), "S  ", "SS ", "SSS", 'S', SCContent.reinforcedCobblestone);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsSandstone, 4), "S  ", "SS ", "SSS", 'S', new ItemStack(SCContent.reinforcedSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsOak, 4), "W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 0));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsSpruce, 4), "W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 1));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsBirch, 4), "W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 2));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsJungle, 4), "W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 3));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsAcacia, 4), "W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 4));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsDarkoak, 4), "W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 5));
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsStoneBrick, 4), "S  ", "SS ", "SSS", 'S', SCContent.reinforcedStoneBrick);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsBrick, 4), "S  ", "SS ", "SSS", 'S', SCContent.reinforcedBrick);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsNetherBrick, 4), "S  ", "SS ", "SSS", 'S', SCContent.reinforcedNetherBrick);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsQuartz, 4), "S  ", "SS ", "SSS", 'S', new ItemStack(SCContent.reinforcedQuartz, 1, 0));

		GameRegistry.addRecipe(new ItemStack(SCContent.ims, 1), "BPB", " I ", "B B", 'B', SCContent.bouncingBetty, 'P', SCContent.portableRadar, 'I', Blocks.iron_block);
		GameRegistry.addRecipe(new ItemStack(SCContent.cameraMonitor, 1), "III", "IGI", "III", 'I', Items.iron_ingot, 'G', Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(SCContent.taser, 1), "BGI", "RSG", "  S", 'B', Items.bow, 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'R', Items.redstone, 'S', Items.stick);
		GameRegistry.addRecipe(new ItemStack(SCContent.securityCamera, 1), "III", "GRI", "IIS", 'I', Items.iron_ingot, 'G', SCContent.reinforcedGlassPane, 'R', Blocks.redstone_block, 'S', Items.stick);

		for(int i = 0; i < 16; i++)
		{
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStainedGlass, 8, BlockColored.func_150031_c(i)), "###", "#X#", "###", '#', new ItemStack(SCContent.reinforcedGlass), 'X', new ItemStack(Items.dye, 1, i));
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStainedGlassPanes, 16, i), "###", "###", '#', new ItemStack(SCContent.reinforcedStainedGlass, 1, i));
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStainedHardenedClay, 8, BlockColored.func_150031_c(i)), "###", "#X#", "###", '#', new ItemStack(SCContent.reinforcedHardenedClay), 'X', new ItemStack(Items.dye, 1, i));
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedCarpet, 3, i), "##", '#', new ItemStack(SCContent.reinforcedWool, 1, i));
		}

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockReinforcerLvL1, 1), " DG", "RLD", "SR ", 'G', Blocks .glass, 'D', Items.diamond, 'L', SCContent.laserBlock, 'R', Items.redstone, 'S', Items.stick);
		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockReinforcerLvL2, 1), " DG", "RLD", "SR ", 'G', new ItemStack(SCContent.reinforcedStainedGlass, 1, 15), 'D', Blocks.diamond_block, 'L', SCContent.laserBlock, 'R', Items.redstone, 'S', Items.stick);
		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockReinforcerLvL3, 1), " EG", "RNE", "SR ", 'G', new ItemStack(SCContent.reinforcedStainedGlass, 1, 6), 'E', Blocks.emerald_block, 'N', Items.nether_star, 'R', Blocks.redstone_block, 'S', Items.stick);

		for(int i = 0; i < 6; i++)
		{
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedWoodSlabs, 6, i), "MMM", 'M', new ItemStack(SCContent.reinforcedWoodPlanks, 1, i));
		}

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 0), "MMM", 'M', SCContent.reinforcedStone);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 1), "MMM", 'M', SCContent.reinforcedCobblestone);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 2), "MMM", 'M', SCContent.reinforcedSandstone);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedDirtSlab, 6, 3), "MMM", 'M', SCContent.reinforcedDirt);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 4), "MMM", 'M', SCContent.reinforcedStoneBrick);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 5), "MMM", 'M', SCContent.reinforcedBrick);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 6), "MMM", 'M', SCContent.reinforcedNetherBrick);
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 7, 6), "MMM", 'M', SCContent.reinforcedQuartz);

		GameRegistry.addRecipe(new ItemStack(SCContent.protecto, 1), "ODO", "OEO", "OOO", 'O', Blocks.obsidian, 'D', Blocks.daylight_detector, 'E', Items.ender_eye);
		GameRegistry.addRecipe(new ItemStack(SCContent.briefcase, 1), "SSS", "ICI", "III", 'S', Items.stick, 'I', Items.iron_ingot, 'C', SCContent.keypadChest);
		GameRegistry.addRecipe(new ItemStack(SCContent.universalKeyChanger, 1), " RL", " IR", "I  ", 'R', Items.redstone, 'L', SCContent.laserBlock, 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(SCContent.motionActivatedLightOff, 1), "L", "R", "S", 'L', Blocks.redstone_lamp, 'R', SCContent.portableRadar, 'S', Items.stick);

		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.dirtMine, 1), new Object[] {Blocks.dirt, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.stoneMine, 1), new Object[] {Blocks.stone, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.cobblestoneMine, 1), new Object[] {Blocks.cobblestone, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.diamondOreMine, 1), new Object[] {Blocks.diamond_ore, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.sandMine, 1), new Object[] {Blocks.sand, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.furnaceMine, 1), new Object[] {Blocks.furnace, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.gravelMine, 1), new Object[] {Blocks.gravel, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.universalOwnerChanger, 1), new Object[] {SCContent.universalBlockModifier, Items.name_tag});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.scannerDoorItem, 1), new Object[]{SCContent.reinforcedDoorItem, SCContent.retinalScanner});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.secretSignItem, 3), new Object[]{Items.sign, Items.sign, Items.sign, SCContent.retinalScanner});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.universalKeyChanger), new Object[]{SCContent.briefcase, SCContent.universalKeyChanger});
	}

	public static void registerEntities()
	{
		EntityRegistry.registerModEntity(EntityBouncingBetty.class, "BBetty", 0, SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTaserBullet.class, "TazerBullet", 2, SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntityIMSBomb.class, "IMSBomb", 3, SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntitySecurityCamera.class, "SecurityCamera", 4, SecurityCraft.instance, 256, 20, false);
	}

	public static void registerPackets(SimpleNetworkWrapper network)
	{
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 1, Side.SERVER);
		network.registerMessage(PacketSetBlockMetadata.Handler.class, PacketSetBlockMetadata.class, 2, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 3, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 4, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 5, Side.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 6, Side.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 7, Side.SERVER);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 8, Side.CLIENT);
		network.registerMessage(PacketSetExplosiveState.Handler.class, PacketSetExplosiveState.class, 9, Side.SERVER);
		network.registerMessage(PacketGivePotionEffect.Handler.class, PacketGivePotionEffect.class, 10, Side.SERVER);
		network.registerMessage(PacketSetBlockAndMetadata.Handler.class, PacketSetBlockAndMetadata.class, 11, Side.SERVER);
		network.registerMessage(PacketSSetOwner.Handler.class, PacketSSetOwner.class, 12, Side.SERVER);
		network.registerMessage(PacketSAddModules.Handler.class, PacketSAddModules.class, 13, Side.SERVER);
		network.registerMessage(PacketCSetCameraLocation.Handler.class, PacketCSetCameraLocation.class, 14, Side.CLIENT);
		network.registerMessage(PacketCRemoveLGView.Handler.class, PacketCRemoveLGView.class, 15, Side.CLIENT);
		network.registerMessage(PacketCCreateLGView.Handler.class, PacketCCreateLGView.class, 16, Side.CLIENT);
		network.registerMessage(PacketSSetPassword.Handler.class, PacketSSetPassword.class, 17, Side.SERVER);
		network.registerMessage(PacketSCheckPassword.Handler.class, PacketSCheckPassword.class, 18, Side.SERVER);
		network.registerMessage(PacketSSyncTENBTTag.Handler.class, PacketSSyncTENBTTag.class, 19, Side.SERVER);
		network.registerMessage(PacketSMountCamera.Handler.class, PacketSMountCamera.class, 20, Side.SERVER);
		network.registerMessage(PacketSSetCameraRotation.Handler.class, PacketSSetCameraRotation.class, 21, Side.SERVER);
		network.registerMessage(PacketCSetPlayerPositionAndRotation.Handler.class, PacketCSetPlayerPositionAndRotation.class, 22, Side.CLIENT);
		network.registerMessage(PacketCSpawnLightning.Handler.class, PacketCSpawnLightning.class, 23, Side.CLIENT);
		network.registerMessage(PacketSOpenGui.Handler.class, PacketSOpenGui.class, 24, Side.SERVER);
		network.registerMessage(PacketSToggleOption.Handler.class, PacketSToggleOption.class, 25, Side.SERVER);
		network.registerMessage(PacketSUpdateSliderValue.Handler.class, PacketSUpdateSliderValue.class, 26, Side.SERVER);
		network.registerMessage(PacketSRemoveCameraTag.Handler.class, PacketSRemoveCameraTag.class, 27, Side.SERVER);
		network.registerMessage(PacketCChangeStackSize.Handler.class, PacketCChangeStackSize.class, 28, Side.CLIENT);
	}

	/**
	 * Registers the given block with GameRegistry.registerBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 */
	private static void registerBlock(Block block)
	{
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));

		SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}

	static boolean hasReinforcedPage = false;

	private static void registerReinforcedBlock(Block block)
	{
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));

		if(!hasReinforcedPage)
		{
			SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.securitycraft:reinforced.info"));
			hasReinforcedPage = true;
		}
	}

	private static void registerReinforcedBlock(Block block, Class<? extends ItemBlock> itemClass)
	{
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));

		if(!hasReinforcedPage)
		{
			SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.securitycraft:reinforced.info"));
			hasReinforcedPage = true;
		}
	}

	private static void registerBlock(Block block, Class<? extends ItemBlock> itemClass)
	{
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));

		SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}

	private static void registerBlock(Block block, Class<? extends ItemBlock> itemClass, Object... constructorParams)
	{
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5), constructorParams);

		SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}

	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(Item item)
	{
		registerItem(item, item.getUnlocalizedName().substring(5));
	}

	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(Item item, String customName)
	{
		GameRegistry.registerItem(item, customName);

		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info"));
	}
}
