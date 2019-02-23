package net.geforcemods.securitycraft;

import java.util.ArrayList;

import javafx.geometry.Side;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySentry;
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
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCInitSentryAnimation;
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
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class RegistrationHandler
{
	private static ItemStack[] harmingPotions = {PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.STRONG_HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HARMING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HARMING)};
	private static ItemStack[] healingPotions = {PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.STRONG_HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HEALING),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.STRONG_HEALING)};
	private static ArrayList<Item> itemBlocks = new ArrayList<Item>();
	private static ArrayList<Block> blockPages = new ArrayList<Block>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		registerBlock(event, SCContent.laserBlock);
		event.getRegistry().register(SCContent.laserField);
		registerBlock(event, SCContent.keypad);
		registerBlock(event, SCContent.mine);
		event.getRegistry().register(SCContent.mineCut);
		registerBlock(event, SCContent.dirtMine);
		registerBlock(event, SCContent.stoneMine, false);
		registerBlock(event, SCContent.cobblestoneMine, false);
		registerBlock(event, SCContent.diamondOreMine, false);
		registerBlock(event, SCContent.sandMine, false);
		registerBlock(event, SCContent.furnaceMine);
		registerBlock(event, SCContent.retinalScanner);
		event.getRegistry().register(SCContent.reinforcedDoor);
		registerBlock(event, SCContent.bogusLava, false);
		registerBlock(event, SCContent.bogusLavaFlowing, false);
		registerBlock(event, SCContent.bogusWater, false);
		registerBlock(event, SCContent.bogusWaterFlowing, false);
		registerBlock(event, SCContent.keycardReader);
		registerBlock(event, SCContent.ironTrapdoor);
		registerBlock(event, SCContent.bouncingBetty);
		registerBlock(event, SCContent.inventoryScanner);
		event.getRegistry().register(SCContent.inventoryScannerField);
		registerBlock(event, SCContent.trackMine);
		registerBlock(event, SCContent.cageTrap);
		registerBlock(event, SCContent.portableRadar);
		registerBlock(event, SCContent.reinforcedIronBars, false);
		registerBlock(event, SCContent.keypadChest);
		registerBlock(event, SCContent.usernameLogger);
		registerBlock(event, SCContent.alarm);
		event.getRegistry().register(SCContent.alarmLit);
		registerBlock(event, SCContent.reinforcedStone, new ItemBlockReinforcedStone(SCContent.reinforcedStone), true);
		registerBlock(event, SCContent.reinforcedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedSandstone), false);
		registerBlock(event, SCContent.reinforcedDirt, false);
		registerBlock(event, SCContent.reinforcedCobblestone, false);
		registerBlock(event, SCContent.reinforcedFencegate);
		registerBlock(event, SCContent.reinforcedWoodPlanks, new ItemBlockReinforcedPlanks(SCContent.reinforcedWoodPlanks), false);
		registerBlock(event, SCContent.panicButton);
		registerBlock(event, SCContent.frame);
		registerBlock(event, SCContent.claymore);
		registerBlock(event, SCContent.keypadFurnace);
		registerBlock(event, SCContent.securityCamera);
		registerBlock(event, SCContent.reinforcedStairsOak, false);
		registerBlock(event, SCContent.reinforcedStairsSpruce, false);
		registerBlock(event, SCContent.reinforcedStairsCobblestone, false);
		registerBlock(event, SCContent.reinforcedStairsSandstone, false);
		registerBlock(event, SCContent.reinforcedStairsBirch, false);
		registerBlock(event, SCContent.reinforcedStairsJungle, false);
		registerBlock(event, SCContent.reinforcedStairsAcacia, false);
		registerBlock(event, SCContent.reinforcedStairsDarkoak, false);
		registerBlock(event, SCContent.reinforcedStairsStone);
		registerBlock(event, SCContent.ironFence);
		registerBlock(event, SCContent.ims);
		registerBlock(event, SCContent.reinforcedGlass, false);
		registerBlock(event, SCContent.reinforcedStainedGlass, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlass), true);
		registerBlock(event, SCContent.reinforcedWoodSlabs, new ItemBlockReinforcedWoodSlabs(SCContent.reinforcedWoodSlabs), true);
		event.getRegistry().register(SCContent.reinforcedDoubleWoodSlabs);
		registerBlock(event, SCContent.reinforcedStoneSlabs, new ItemBlockReinforcedSlabs(SCContent.reinforcedStoneSlabs), true);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs);
		registerBlock(event, SCContent.protecto);
		event.getRegistry().register(SCContent.scannerDoor);
		registerBlock(event, SCContent.reinforcedStoneBrick, new ItemBlockReinforcedStoneBrick(SCContent.reinforcedStoneBrick), false);
		registerBlock(event, SCContent.reinforcedStairsStoneBrick);
		registerBlock(event, SCContent.reinforcedMossyCobblestone, false);
		registerBlock(event, SCContent.reinforcedBrick, false);
		registerBlock(event, SCContent.reinforcedStairsBrick);
		registerBlock(event, SCContent.reinforcedNetherBrick, false);
		registerBlock(event, SCContent.reinforcedStairsNetherBrick);
		registerBlock(event, SCContent.reinforcedHardenedClay, false);
		registerBlock(event, SCContent.reinforcedStainedHardenedClay, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedHardenedClay), false);
		registerBlock(event, SCContent.reinforcedOldLogs, new ItemBlockReinforcedLog(SCContent.reinforcedOldLogs), false);
		registerBlock(event, SCContent.reinforcedNewLogs, new ItemBlockReinforcedLog(SCContent.reinforcedNewLogs), false);
		registerBlock(event, SCContent.reinforcedMetals, new ItemBlockReinforcedMetals(SCContent.reinforcedMetals), false);
		registerBlock(event, SCContent.reinforcedCompressedBlocks, new ItemBlockReinforcedCompressedBlocks(SCContent.reinforcedCompressedBlocks), false);
		registerBlock(event, SCContent.reinforcedWool, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedWool), false);
		registerBlock(event, SCContent.reinforcedQuartz, new ItemBlockReinforcedQuartz(SCContent.reinforcedQuartz), false);
		registerBlock(event, SCContent.reinforcedStairsQuartz);
		registerBlock(event, SCContent.reinforcedPrismarine, new ItemBlockReinforcedPrismarine(SCContent.reinforcedPrismarine), false);
		registerBlock(event, SCContent.reinforcedRedSandstone, new ItemBlockReinforcedSandstone(SCContent.reinforcedRedSandstone), false);
		registerBlock(event, SCContent.reinforcedStairsRedSandstone);
		registerBlock(event, SCContent.reinforcedStoneSlabs2, new ItemBlockReinforcedSlabs2(SCContent.reinforcedStoneSlabs2), false);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs2);
		registerBlock(event, SCContent.reinforcedEndStoneBricks, false);
		registerBlock(event, SCContent.reinforcedRedNetherBrick, false);
		registerBlock(event, SCContent.reinforcedPurpur, new ItemBlockReinforcedPurpur(SCContent.reinforcedPurpur), false);
		registerBlock(event, SCContent.reinforcedStairsPurpur);
		registerBlock(event, SCContent.reinforcedConcrete, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedConcrete), false);
		event.getRegistry().register(SCContent.secretSignWall);
		event.getRegistry().register(SCContent.secretSignStanding);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.reinforcedObsidian, false);
		registerBlock(event, SCContent.reinforcedNetherrack, false);
		registerBlock(event, SCContent.reinforcedEndStone, false);
		registerBlock(event, SCContent.reinforcedSeaLantern, false);
		registerBlock(event, SCContent.reinforcedBoneBlock, false);
		registerBlock(event, SCContent.reinforcedGlassPane, false);
		registerBlock(event, SCContent.reinforcedStainedGlassPanes, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlassPanes), true);
		registerBlock(event, SCContent.reinforcedCarpet, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedCarpet), false);
		registerBlock(event, SCContent.reinforcedGlowstone, false);
		registerBlock(event, SCContent.gravelMine, false);
		registerBlock(event, SCContent.reinforcedSand, false);
		registerBlock(event, SCContent.reinforcedGravel, false);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		//register item blocks
		for(Item item : itemBlocks)
		{
			event.getRegistry().register(item);
		}

		//init block sc manual pages
		for(Block block : blockPages)
		{
			if(block == SCContent.reinforcedStone)
				SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.securitycraft:reinforced.info"));
			else
				SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getTranslationKey().substring(5) + ".info"));
		}

		registerItem(event, SCContent.codebreaker);
		registerItem(event, SCContent.reinforcedDoorItem);
		registerItem(event, SCContent.scannerDoorItem);
		registerItem(event, SCContent.universalBlockRemover);
		registerItem(event, SCContent.keycardLvl1, ConfigHandler.ableToCraftKeycard1);
		registerItem(event, SCContent.keycardLvl2, ConfigHandler.ableToCraftKeycard2);
		registerItem(event, SCContent.keycardLvl3, ConfigHandler.ableToCraftKeycard3);
		registerItem(event, SCContent.keycardLvl4, ConfigHandler.ableToCraftKeycard4);
		registerItem(event, SCContent.keycardLvl5, ConfigHandler.ableToCraftKeycard5);
		registerItem(event, SCContent.limitedUseKeycard, ConfigHandler.ableToCraftLUKeycard);
		registerItem(event, SCContent.remoteAccessMine);
		registerItemWithCustomRecipe(event, SCContent.fWaterBucket, new ItemStack[]{ ItemStack.EMPTY, harmingPotions[0], ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.WATER_BUCKET, 1), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
		registerItemWithCustomRecipe(event, SCContent.fLavaBucket, new ItemStack[]{ ItemStack.EMPTY, healingPotions[0], ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.LAVA_BUCKET, 1), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY});
		registerItem(event, SCContent.universalBlockModifier);
		registerItem(event, SCContent.redstoneModule);
		registerItem(event, SCContent.whitelistModule);
		registerItem(event, SCContent.blacklistModule);
		registerItem(event, SCContent.harmingModule);
		registerItem(event, SCContent.smartModule);
		registerItem(event, SCContent.storageModule);
		registerItem(event, SCContent.disguiseModule);
		registerItem(event, SCContent.wireCutters);
		registerItem(event, SCContent.adminTool);
		registerItem(event, SCContent.keyPanel);
		registerItem(event, SCContent.cameraMonitor);
		registerItem(event, SCContent.taser);
		registerItem(event, SCContent.scManual);
		registerItem(event, SCContent.universalOwnerChanger);
		registerItem(event, SCContent.universalBlockReinforcerLvL1);
		registerItem(event, SCContent.universalBlockReinforcerLvL2);
		registerItem(event, SCContent.universalBlockReinforcerLvL3);
		registerItem(event, SCContent.briefcase);
		registerItem(event, SCContent.universalKeyChanger);
		event.getRegistry().register(SCContent.taserPowered); //won't show up in the manual
		registerItem(event, SCContent.secretSignItem);
		registerItem(event, SCContent.sentry, "Henzoid");

		SecurityCraft.serverProxy.registerVariants();
		//clear unused memory
		itemBlocks.clear();
		blockPages.clear();
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityOwnable::new).build(null).setRegistryName(new ResourceLocation("securitycraft:ownable")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntitySCTE::new).build(null).setRegistryName(new ResourceLocation("securitycraft:abstract")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityKeypad::new).build(null).setRegistryName(new ResourceLocation("securitycraft:keypad")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityLaserBlock::new).build(null).setRegistryName(new ResourceLocation("securitycraft:laser_block")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityCageTrap::new).build(null).setRegistryName(new ResourceLocation("securitycraft:cage_trap")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityKeycardReader::new).build(null).setRegistryName(new ResourceLocation("securitycraft:keycard_reader")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityInventoryScanner::new).build(null).setRegistryName(new ResourceLocation("securitycraft:inventory_scanner")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityPortableRadar::new).build(null).setRegistryName(new ResourceLocation("securitycraft:portable_radar")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntitySecurityCamera::new).build(null).setRegistryName(new ResourceLocation("securitycraft:security_camera")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityLogger::new).build(null).setRegistryName(new ResourceLocation("securitycraft:username_logger")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityRetinalScanner::new).build(null).setRegistryName(new ResourceLocation("securitycraft:retinal_scanner")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityKeypadChest::new).build(null).setRegistryName(new ResourceLocation("securitycraft:keypad_chest")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityAlarm::new).build(null).setRegistryName(new ResourceLocation("securitycraft:alarm")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityClaymore::new).build(null).setRegistryName(new ResourceLocation("securitycraft:claymore")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityKeypadFurnace::new).build(null).setRegistryName(new ResourceLocation("securitycraft:keypad_furnace")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityIMS::new).build(null).setRegistryName(new ResourceLocation("securitycraft:ims")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityProtecto::new).build(null).setRegistryName(new ResourceLocation("securitycraft:protecto")));
		event.getRegistry().register(TileEntityType.Builder.create(CustomizableSCTE::new).build(null).setRegistryName(new ResourceLocation("securitycraft:customizable")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityScannerDoor::new).build(null).setRegistryName(new ResourceLocation("securitycraft:scanner_door")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntitySecretSign::new).build(null).setRegistryName(new ResourceLocation("securitycraft:secret_sign")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityMotionLight::new).build(null).setRegistryName(new ResourceLocation("securitycraft:motion_light")));
		event.getRegistry().register(TileEntityType.Builder.create(TileEntityTrackMine::new).build(null).setRegistryName(new ResourceLocation("securitycraft:track_mine")));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event)
	{
		event.getRegistry().register(EntityType.Builder.create(EntityBouncingBetty.class, EntityBouncingBetty::new).tracker(128, 1, true).build(SecurityCraft.MODID + ":bouncingbetty"));
		event.getRegistry().register(EntityType.Builder.create(EntityTaserBullet.class, EntityTaserBullet::new).tracker(256, 1, true).build(SecurityCraft.MODID + ":taserbullet"));
		event.getRegistry().register(EntityType.Builder.create(EntityIMSBomb.class, EntityIMSBomb::new).tracker(256, 1, true).build(SecurityCraft.MODID + ":imsbomb"));
		event.getRegistry().register(EntityType.Builder.create(EntitySecurityCamera.class, EntitySecurityCamera::new).tracker(256, 20, true).build(SecurityCraft.MODID + ":securitycamera"));
		event.getRegistry().register(EntityType.Builder.create(EntitySentry.class, EntitySentry::new).tracker(256, 1, true).build(SecurityCraft.MODID + ":sentry"));
		event.getRegistry().register(EntityType.Builder.create(EntityBullet.class, EntityBullet::new).tracker(256, 1, true).build(SecurityCraft.MODID + ":bullet"));
	}

	public static void registerPackets(SimpleNetworkWrapper network)
	{
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 1, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 2, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 3, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 4, Dist.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 5, Dist.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 6, Side.SERVER);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 7, Dist.CLIENT);
		network.registerMessage(PacketSetExplosiveState.Handler.class, PacketSetExplosiveState.class, 8, Side.SERVER);
		network.registerMessage(PacketGivePotionEffect.Handler.class, PacketGivePotionEffect.class, 9, Side.SERVER);
		network.registerMessage(PacketSSetOwner.Handler.class, PacketSSetOwner.class, 10, Side.SERVER);
		network.registerMessage(PacketSAddModules.Handler.class, PacketSAddModules.class, 11, Side.SERVER);
		network.registerMessage(PacketSSetPassword.Handler.class, PacketSSetPassword.class, 12, Side.SERVER);
		network.registerMessage(PacketSCheckPassword.Handler.class, PacketSCheckPassword.class, 13, Side.SERVER);
		network.registerMessage(PacketSSyncTENBTTag.Handler.class, PacketSSyncTENBTTag.class, 14, Side.SERVER);
		network.registerMessage(PacketSMountCamera.Handler.class, PacketSMountCamera.class, 15, Side.SERVER);
		network.registerMessage(PacketSSetCameraRotation.Handler.class, PacketSSetCameraRotation.class, 16, Side.SERVER);
		network.registerMessage(PacketCSetPlayerPositionAndRotation.Handler.class, PacketCSetPlayerPositionAndRotation.class, 17, Dist.CLIENT);
		network.registerMessage(PacketSOpenGui.Handler.class, PacketSOpenGui.class, 18, Side.SERVER);
		network.registerMessage(PacketSToggleOption.Handler.class, PacketSToggleOption.class, 19, Side.SERVER);
		network.registerMessage(PacketCRequestTEOwnableUpdate.Handler.class, PacketCRequestTEOwnableUpdate.class, 20, Side.SERVER);
		network.registerMessage(PacketSUpdateTEOwnable.Handler.class, PacketSUpdateTEOwnable.class, 21, Dist.CLIENT);
		network.registerMessage(PacketSUpdateSliderValue.Handler.class, PacketSUpdateSliderValue.class, 22, Side.SERVER);
		network.registerMessage(PacketSRemoveCameraTag.Handler.class, PacketSRemoveCameraTag.class, 23, Side.SERVER);
		network.registerMessage(PacketCInitSentryAnimation.Handler.class, PacketCInitSentryAnimation.class, 24, Dist.CLIENT);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		for(int i = 0; i < SCSounds.values().length; i++)
		{
			event.getRegistry().register(SCSounds.values()[i].event);
		}
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block)
	{
		registerBlock(event, block, new ItemBlock(block, new Item.Properties()), true);
	}

	/**
	 * Registers a block and its ItemBlock
	 * @param block The Block to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, boolean initPage)
	{
		registerBlock(event, block, new ItemBlock(block, new Item.Properties()), initPage);
	}

	/**
	 * Registers a block with a custom ItemBlock
	 * @param block The Block to register
	 * @param itemBlock The ItemBlock to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ItemBlock itemBlock, boolean initPage)
	{
		event.getRegistry().register(block);

		if(itemBlock != null)
			itemBlocks.add(itemBlock.setRegistryName(block.getRegistryName().toString()));

		if(initPage)
			blockPages.add(block);
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item)
	{
		registerItem(event, item, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, boolean configValue)
	{
		registerItem(event, item, configValue, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, String designedBy)
	{
		SCManualPage page = new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info");

		event.getRegistry().register(item);
		page.designedBy(designedBy);
		SecurityCraft.instance.manualPages.add(page);
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, boolean configValue, String designedBy)
	{
		SCManualPage page = new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info", configValue);

		event.getRegistry().register(item);
		page.designedBy(designedBy);
		SecurityCraft.instance.manualPages.add(page);
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Also overrides the default recipe that would've been drawn in the manual with a new recipe.
	 */
	private static void registerItemWithCustomRecipe(RegistryEvent.Register<Item> event, Item item, ItemStack... customRecipe)
	{
		event.getRegistry().register(item);

		NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(customRecipe.length, Ingredient.EMPTY);

		for(int i = 0; i < recipeItems.size(); i++)
			recipeItems.set(i, Ingredient.fromStacks(customRecipe[i]));

		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info", recipeItems));
	}
}
