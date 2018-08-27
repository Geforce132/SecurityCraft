package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedMetals;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPrismarine;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPurpur;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedQuartz;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs2;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.network.packets.PacketCRequestTEOwnableUpdate;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
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
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntityTrackMine;
import net.geforcemods.securitycraft.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

public class RegistrationHandler
{
	private static ItemStack[] harmingPotions = {PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.STRONG_HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HARMING)};
	private static ItemStack[] healingPotions = {PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.STRONG_HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HEALING)};

	public static void registerContent()
	{
		registerBlock(SCContent.laserBlock);
		GameData.register_impl(SCContent.laserField);
		registerBlock(SCContent.keypad);
		registerBlock(SCContent.mine);
		GameData.register_impl(SCContent.mineCut);
		registerBlock(SCContent.dirtMine);
		registerBlock(SCContent.stoneMine, false);
		registerBlock(SCContent.cobblestoneMine, false);
		registerBlock(SCContent.diamondOreMine, false);
		registerBlock(SCContent.sandMine, false);
		registerBlock(SCContent.furnaceMine);
		registerBlock(SCContent.retinalScanner);
		GameData.register_impl(SCContent.reinforcedDoor);
		registerBlock(SCContent.bogusLava, false);
		registerBlock(SCContent.bogusLavaFlowing, false);
		registerBlock(SCContent.bogusWater, false);
		registerBlock(SCContent.bogusWaterFlowing, false);
		registerBlock(SCContent.keycardReader);
		registerBlock(SCContent.ironTrapdoor);
		registerBlock(SCContent.bouncingBetty);
		registerBlock(SCContent.inventoryScanner);
		GameData.register_impl(SCContent.inventoryScannerField);
		registerBlock(SCContent.trackMine);
		registerBlock(SCContent.cageTrap);
		registerBlock(SCContent.portableRadar);
		registerBlock(SCContent.reinforcedIronBars, false);
		registerBlock(SCContent.keypadChest);
		registerBlock(SCContent.usernameLogger);
		registerBlock(SCContent.alarm);
		GameData.register_impl(SCContent.alarmLit);
		registerBlock(SCContent.reinforcedStone, new ItemBlockReinforcedStone(SCContent.reinforcedStone), true);
		registerBlock(SCContent.reinforcedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedSandstone), false);
		registerBlock(SCContent.reinforcedDirt, false);
		registerBlock(SCContent.reinforcedCobblestone, false);
		registerBlock(SCContent.reinforcedFencegate);
		registerBlock(SCContent.reinforcedWoodPlanks, new ItemBlockReinforcedPlanks(SCContent.reinforcedWoodPlanks), false);
		registerBlock(SCContent.panicButton);
		registerBlock(SCContent.frame);
		registerBlock(SCContent.claymore);
		registerBlock(SCContent.keypadFurnace);
		registerBlock(SCContent.securityCamera);
		registerBlock(SCContent.reinforcedStairsOak, false);
		registerBlock(SCContent.reinforcedStairsSpruce, false);
		registerBlock(SCContent.reinforcedStairsCobblestone, false);
		registerBlock(SCContent.reinforcedStairsSandstone, false);
		registerBlock(SCContent.reinforcedStairsBirch, false);
		registerBlock(SCContent.reinforcedStairsJungle, false);
		registerBlock(SCContent.reinforcedStairsAcacia, false);
		registerBlock(SCContent.reinforcedStairsDarkoak, false);
		registerBlock(SCContent.reinforcedStairsStone);
		registerBlock(SCContent.ironFence);
		registerBlock(SCContent.ims);
		registerBlock(SCContent.reinforcedGlass, false);
		registerBlock(SCContent.reinforcedStainedGlass, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlass), true);
		registerBlock(SCContent.reinforcedWoodSlabs, new ItemBlockReinforcedWoodSlabs(SCContent.reinforcedWoodSlabs), true);
		GameData.register_impl(SCContent.reinforcedDoubleWoodSlabs);
		registerBlock(SCContent.reinforcedStoneSlabs, new ItemBlockReinforcedSlabs(SCContent.reinforcedStoneSlabs), true);
		GameData.register_impl(SCContent.reinforcedDoubleStoneSlabs);
		registerBlock(SCContent.protecto);
		GameData.register_impl(SCContent.scannerDoor);
		registerBlock(SCContent.reinforcedStoneBrick, new ItemBlockReinforcedStoneBrick(SCContent.reinforcedStoneBrick), false);
		registerBlock(SCContent.reinforcedStairsStoneBrick);
		registerBlock(SCContent.reinforcedMossyCobblestone, false);
		registerBlock(SCContent.reinforcedBrick, false);
		registerBlock(SCContent.reinforcedStairsBrick);
		registerBlock(SCContent.reinforcedNetherBrick, false);
		registerBlock(SCContent.reinforcedStairsNetherBrick);
		registerBlock(SCContent.reinforcedHardenedClay, false);
		registerBlock(SCContent.reinforcedStainedHardenedClay, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedHardenedClay), false);
		registerBlock(SCContent.reinforcedOldLogs, new ItemBlockReinforcedLog(SCContent.reinforcedOldLogs), false);
		registerBlock(SCContent.reinforcedNewLogs, new ItemBlockReinforcedLog(SCContent.reinforcedNewLogs), false);
		registerBlock(SCContent.reinforcedMetals, new ItemBlockReinforcedMetals(SCContent.reinforcedMetals), false);
		registerBlock(SCContent.reinforcedCompressedBlocks, new ItemBlockReinforcedCompressedBlocks(SCContent.reinforcedCompressedBlocks), false);
		registerBlock(SCContent.reinforcedWool, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedWool), false);
		registerBlock(SCContent.reinforcedQuartz, new ItemBlockReinforcedQuartz(SCContent.reinforcedQuartz), false);
		registerBlock(SCContent.reinforcedStairsQuartz);
		registerBlock(SCContent.reinforcedPrismarine, new ItemBlockReinforcedPrismarine(SCContent.reinforcedPrismarine), false);
		registerBlock(SCContent.reinforcedRedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedRedSandstone), false);
		registerBlock(SCContent.reinforcedStairsRedSandstone);
		registerBlock(SCContent.reinforcedStoneSlabs2, new ItemBlockReinforcedSlabs2(SCContent.reinforcedStoneSlabs2), false); //technically not a reinforced block, but doesn't need a page
		GameData.register_impl(SCContent.reinforcedDoubleStoneSlabs2);
		registerBlock(SCContent.reinforcedEndStoneBricks, false);
		registerBlock(SCContent.reinforcedRedNetherBrick, false);
		registerBlock(SCContent.reinforcedPurpur, new ItemBlockReinforcedPurpur(SCContent.reinforcedPurpur), false);
		registerBlock(SCContent.reinforcedStairsPurpur);
		registerBlock(SCContent.reinforcedConcrete, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedConcrete), false);
		GameData.register_impl(SCContent.secretSignWall);
		GameData.register_impl(SCContent.secretSignStanding);
		registerBlock(SCContent.motionActivatedLight);
		registerBlock(SCContent.reinforcedObsidian, false);
		registerBlock(SCContent.reinforcedNetherrack, false);
		registerBlock(SCContent.reinforcedEndStone, false);
		registerBlock(SCContent.reinforcedSeaLantern, false);
		registerBlock(SCContent.reinforcedBoneBlock, false);

		registerItem(SCContent.codebreaker);
		registerItem(SCContent.reinforcedDoorItem);
		registerItem(SCContent.scannerDoorItem);
		registerItem(SCContent.universalBlockRemover);
		registerItem(SCContent.keycardLV1, SecurityCraft.config.ableToCraftKeycard1);
		registerItem(SCContent.keycardLV2, SecurityCraft.config.ableToCraftKeycard2);
		registerItem(SCContent.keycardLV3, SecurityCraft.config.ableToCraftKeycard3);
		registerItem(SCContent.keycardLV4, SecurityCraft.config.ableToCraftKeycard4);
		registerItem(SCContent.keycardLV5, SecurityCraft.config.ableToCraftKeycard5);
		registerItem(SCContent.limitedUseKeycard, SecurityCraft.config.ableToCraftLUKeycard);
		registerItem(SCContent.remoteAccessMine);
		registerItemWithCustomRecipe(SCContent.fWaterBucket, new ItemStack[]{ ItemStack.EMPTY, harmingPotions[0], ItemStack.EMPTY, ItemStack.EMPTY, ItemUtils.toItemStack(Items.WATER_BUCKET), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
		registerItemWithCustomRecipe(SCContent.fLavaBucket, new ItemStack[]{ ItemStack.EMPTY, healingPotions[0], ItemStack.EMPTY, ItemStack.EMPTY, ItemUtils.toItemStack(Items.LAVA_BUCKET), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
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
		GameData.register_impl(SCContent.taserPowered); //won't show up in the manual
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

	public static void registerEntities()
	{
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "bouncingbetty"), EntityBouncingBetty.class, "BBetty", 0, SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "taserbullet"), EntityTaserBullet.class, "TazerBullet", 2, SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "imsbomb"), EntityIMSBomb.class, "IMSBomb", 3, SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("securitycraft", "securitycamera"), EntitySecurityCamera.class, "SecurityCamera", 4, SecurityCraft.instance, 256, 20, false);
	}

	public static void registerPackets(SimpleNetworkWrapper network)
	{
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
		network.registerMessage(PacketSRemoveCameraTag.Handler.class, PacketSRemoveCameraTag.class, 23, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	public static void registerResourceLocations()
	{
		//Blocks
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
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.ironTrapdoor), 0, new ModelResourceLocation("securitycraft:reinforced_iron_trapdoor", "inventory"));
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
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedNewLogs), 1, new ModelResourceLocation("securitycraft:reinforced_logs2_big_oak", "inventory"));;
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 0, new ModelResourceLocation("securitycraft:reinforced_metals_gold", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 1, new ModelResourceLocation("securitycraft:reinforced_metals_iron", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 2, new ModelResourceLocation("securitycraft:reinforced_metals_diamond", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCContent.reinforcedMetals), 3, new ModelResourceLocation("securitycraft:reinforced_metals_emerald", "inventory"));
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

		//Items
		ModelLoader.setCustomModelResourceLocation(SCContent.codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remote_access_mine", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.reinforcedDoorItem, 0, new ModelResourceLocation("securitycraft:door_indestructible_iron_item", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.fWaterBucket, 0, new ModelResourceLocation("securitycraft:bucket_f_water", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.fLavaBucket, 0, new ModelResourceLocation("securitycraft:bucket_f_lava", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLV1, 0, new ModelResourceLocation("securitycraft:keycard_lv1", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLV2, 0, new ModelResourceLocation("securitycraft:keycard_lv2", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLV3, 0, new ModelResourceLocation("securitycraft:keycard_lv3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLV4, 0, new ModelResourceLocation("securitycraft:keycard_lv4", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.keycardLV5, 0, new ModelResourceLocation("securitycraft:keycard_lv5", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.limitedUseKeycard, 0, new ModelResourceLocation("securitycraft:limited_use_keycard", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockRemover, 0, new ModelResourceLocation("securitycraft:universal_block_remover", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.universalBlockModifier, 0, new ModelResourceLocation("securitycraft:universal_block_modifier", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.whitelistModule, 0, new ModelResourceLocation("securitycraft:whitelist_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.blacklistModule, 0, new ModelResourceLocation("securitycraft:blacklist_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.redstoneModule, 0, new ModelResourceLocation("securitycraft:redstone_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.harmingModule, 0, new ModelResourceLocation("securitycraft:harming_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.storageModule, 0, new ModelResourceLocation("securitycraft:storage_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.smartModule, 0, new ModelResourceLocation("securitycraft:smart_module", "inventory"));
		ModelLoader.setCustomModelResourceLocation(SCContent.disguiseModule, 0, new ModelResourceLocation("securitycraft:disguise_module", "inventory"));
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

		//Mines
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
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 * @param block The block to register
	 */
	private static void registerBlock(Block block)
	{
		registerBlock(block, new ItemBlock(block), true);
	}

	/**
	 * Registers a block and its ItemBlock
	 * @param block The Block to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private static void registerBlock(Block block, boolean initPage)
	{
		registerBlock(block, new ItemBlock(block), initPage);
	}

	/**
	 * Registers a block with a custom ItemBlock
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private static void registerBlock(Block block, ItemBlock itemBlock, boolean initPage)
	{
		GameData.register_impl(block);
		GameData.register_impl(itemBlock.setRegistryName(block.getRegistryName().toString()));

		if(initPage)
			if(initPage)
				if(block == SCContent.reinforcedStone)
					SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.securitycraft:reinforced.info"));
				else
					SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getTranslationKey().substring(5) + ".info"));
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(Item item)
	{
		GameData.register_impl(item);
		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info"));
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(Item item, boolean configValue)
	{
		GameData.register_impl(item);
		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info", configValue));
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Also overrides the default recipe that would've been drawn in the manual with a new recipe.
	 */
	private static void registerItemWithCustomRecipe(Item item, ItemStack... customRecipe)
	{
		GameData.register_impl(item);

		NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(customRecipe.length, Ingredient.EMPTY);

		for(int i = 0; i < recipeItems.size(); i++)
			recipeItems.set(i, Ingredient.fromStacks(customRecipe[i]));

		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info", recipeItems));
	}
}
