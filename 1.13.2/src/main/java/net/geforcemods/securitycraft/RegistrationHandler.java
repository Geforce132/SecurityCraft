package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs2;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.items.ItemReinforcedBlock;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.network.client.SetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTag;
import net.geforcemods.securitycraft.network.client.UpdateTEOwnable;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.network.server.GivePotionEffect;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.network.server.SetBlock;
import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.network.server.SetExplosiveState;
import net.geforcemods.securitycraft.network.server.SetKeycardLevel;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.network.server.SetScanType;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
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
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

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
		registerBlock(event, SCContent.reinforcedFencegate);
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
		registerBlock(event, SCContent.reinforcedStairsStoneBrick);
		registerBlock(event, SCContent.reinforcedStairsBrick);
		registerBlock(event, SCContent.reinforcedStairsNetherBrick);
		registerBlock(event, SCContent.reinforcedOldLogs, new ItemBlockReinforcedLog(SCContent.reinforcedOldLogs), false);
		registerBlock(event, SCContent.reinforcedNewLogs, new ItemBlockReinforcedLog(SCContent.reinforcedNewLogs), false);
		registerBlock(event, SCContent.reinforcedStairsQuartz);
		registerBlock(event, SCContent.reinforcedStairsRedSandstone);
		registerBlock(event, SCContent.reinforcedStoneSlabs2, new ItemBlockReinforcedSlabs2(SCContent.reinforcedStoneSlabs2), false);
		event.getRegistry().register(SCContent.reinforcedDoubleStoneSlabs2);
		registerBlock(event, SCContent.reinforcedStairsPurpur);
		event.getRegistry().register(SCContent.secretSignWall);
		event.getRegistry().register(SCContent.secretSignStanding);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.reinforcedObsidian, false);
		registerBlock(event, SCContent.reinforcedSeaLantern, false);
		registerBlock(event, SCContent.reinforcedBoneBlock, false);
		registerBlock(event, SCContent.reinforcedGlassPane, false);
		registerBlock(event, SCContent.reinforcedStainedGlassPanes, new ItemBlockReinforcedStainedBlock(SCContent.reinforcedStainedGlassPanes), true);
		registerBlock(event, SCContent.reinforcedGlowstone, false);
		registerBlock(event, SCContent.gravelMine, false);
		registerBlock(event, SCContent.reinforcedSand, false);
		registerBlock(event, SCContent.reinforcedGravel, false);

		//register reinforced blocks
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					registerBlock(event, (Block)field.get(null), field.getAnnotation(Reinforced.class).value());
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		//register itemblocks for the reinforced blocks
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					event.getRegistry().register(new ItemReinforcedBlock((Block)field.get(null)));
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		//init block sc manual pages
		for(Block block : blockPages)
		{
			if(block == SCContent.reinforcedStone)
				SecurityCraft.instance.manualPages.add(new SCManualPage(block.asItem(), "help.securitycraft:reinforced.info"));
			else
				SecurityCraft.instance.manualPages.add(new SCManualPage(block.asItem(), "help." + block.getTranslationKey().substring(5) + ".info"));
		}

		registerItem(event, SCContent.codebreaker);
		registerItem(event, SCContent.reinforcedDoorItem);
		registerItem(event, SCContent.scannerDoorItem);
		registerItem(event, SCContent.universalBlockRemover);
		registerItem(event, SCContent.keycardLvl1, ServerConfig.CONFIG.ableToCraftKeycard1.get());
		registerItem(event, SCContent.keycardLvl2, ServerConfig.CONFIG.ableToCraftKeycard2.get());
		registerItem(event, SCContent.keycardLvl3, ServerConfig.CONFIG.ableToCraftKeycard3.get());
		registerItem(event, SCContent.keycardLvl4, ServerConfig.CONFIG.ableToCraftKeycard4.get());
		registerItem(event, SCContent.keycardLvl5, ServerConfig.CONFIG.ableToCraftKeycard5.get());
		registerItem(event, SCContent.limitedUseKeycard, ServerConfig.CONFIG.ableToCraftLUKeycard.get());
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

		SecurityCraft.proxy.registerVariants();
		//clear unused memory
		blockPages.clear();
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		SCContent.teTypeOwnable = TileEntityType.register(SecurityCraft.MODID + "ownable", TileEntityType.Builder.create(TileEntityOwnable::new));
		SCContent.teTypeAbstract = TileEntityType.register(SecurityCraft.MODID + "abstract", TileEntityType.Builder.create(TileEntitySCTE::new));
		SCContent.teTypeKeypad = TileEntityType.register(SecurityCraft.MODID + "keypad", TileEntityType.Builder.create(TileEntityKeypad::new));
		SCContent.teTypeLaserBlock = TileEntityType.register(SecurityCraft.MODID + "laser_block", TileEntityType.Builder.create(TileEntityLaserBlock::new));
		SCContent.teTypeCageTrap = TileEntityType.register(SecurityCraft.MODID + "cage_trap", TileEntityType.Builder.create(TileEntityCageTrap::new));
		SCContent.teTypeKeycardReader = TileEntityType.register(SecurityCraft.MODID + "keycard_reader", TileEntityType.Builder.create(TileEntityKeycardReader::new));
		SCContent.teTypeInventoryScanner = TileEntityType.register(SecurityCraft.MODID + "inventory_scanner", TileEntityType.Builder.create(TileEntityInventoryScanner::new));
		SCContent.teTypePortableRadar = TileEntityType.register(SecurityCraft.MODID + "portable_radar", TileEntityType.Builder.create(TileEntityPortableRadar::new));
		SCContent.teTypeSecurityCamera = TileEntityType.register(SecurityCraft.MODID + "security_camera", TileEntityType.Builder.create(TileEntitySecurityCamera::new));
		SCContent.teTypeUsernameLogger = TileEntityType.register(SecurityCraft.MODID + "username_logger", TileEntityType.Builder.create(TileEntityLogger::new));
		SCContent.teTypeRetinalScanner = TileEntityType.register(SecurityCraft.MODID + "retinal_scanner", TileEntityType.Builder.create(TileEntityRetinalScanner::new));
		SCContent.teTypeKeypadChest = TileEntityType.register(SecurityCraft.MODID + "keypad_chest", TileEntityType.Builder.create(TileEntityKeypadChest::new));
		SCContent.teTypeAlarm = TileEntityType.register(SecurityCraft.MODID + "alarm", TileEntityType.Builder.create(TileEntityAlarm::new));
		SCContent.teTypeClaymore = TileEntityType.register(SecurityCraft.MODID + "claymore", TileEntityType.Builder.create(TileEntityClaymore::new));
		SCContent.teTypeKeypadFurnace = TileEntityType.register(SecurityCraft.MODID + "keypad_furnace", TileEntityType.Builder.create(TileEntityKeypadFurnace::new));
		SCContent.teTypeIms = TileEntityType.register(SecurityCraft.MODID + "ims", TileEntityType.Builder.create(TileEntityIMS::new));
		SCContent.teTypeProtecto = TileEntityType.register(SecurityCraft.MODID + "protecto", TileEntityType.Builder.create(TileEntityProtecto::new));
		SCContent.teTypeScannerDoor = TileEntityType.register(SecurityCraft.MODID + "scanner_door", TileEntityType.Builder.create(TileEntityScannerDoor::new));
		SCContent.teTypeSecretSign = TileEntityType.register(SecurityCraft.MODID + "secret_sign", TileEntityType.Builder.create(TileEntitySecretSign::new));
		SCContent.teTypeMotionLight = TileEntityType.register(SecurityCraft.MODID + "motion_light", TileEntityType.Builder.create(TileEntityMotionLight::new));
		SCContent.teTypeTrackMine = TileEntityType.register(SecurityCraft.MODID + "track_mine", TileEntityType.Builder.create(TileEntityTrackMine::new));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event)
	{
		SCContent.eTypeBouncingBetty = EntityType.register(SecurityCraft.MODID + "bouncingbetty", EntityType.Builder.create(EntityBouncingBetty.class, EntityBouncingBetty::new).tracker(128, 1, true));
		SCContent.eTypeTaserBullet = EntityType.register(SecurityCraft.MODID + "taserbullet", EntityType.Builder.create(EntityTaserBullet.class, EntityTaserBullet::new).tracker(256, 1, true));
		SCContent.eTypeImsBomb = EntityType.register(SecurityCraft.MODID + "imsbomb", EntityType.Builder.create(EntityIMSBomb.class, EntityIMSBomb::new).tracker(256, 1, true));
		SCContent.eTypeSecurityCamera = EntityType.register(SecurityCraft.MODID + "securitycamera", EntityType.Builder.create(EntitySecurityCamera.class, EntitySecurityCamera::new).tracker(256, 20, true));
		SCContent.eTypeSentry = EntityType.register(SecurityCraft.MODID + "sentry", EntityType.Builder.create(EntitySentry.class, EntitySentry::new).tracker(256, 1, true));
		SCContent.eTypeBullet = EntityType.register(SecurityCraft.MODID + "bullet", EntityType.Builder.create(EntityBullet.class, EntityBullet::new).tracker(256, 1, true));
	}

	public static void registerPackets()
	{
		int index = 0;

		SecurityCraft.channel.registerMessage(index++, SetBlock.class, SetBlock::encode, SetBlock::decode, SetBlock::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetScanType.class, SetScanType::encode, SetScanType::decode, SetScanType::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetKeycardLevel.class, SetKeycardLevel::encode, SetKeycardLevel::decode, SetKeycardLevel::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::decode, UpdateLogger::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTag.class, UpdateNBTTag::encode, UpdateNBTTag::decode, UpdateNBTTag::onMessage);
		SecurityCraft.channel.registerMessage(index++, PlaySoundAtPos.class, PlaySoundAtPos::encode, PlaySoundAtPos::decode, PlaySoundAtPos::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetExplosiveState.class, SetExplosiveState::encode, SetExplosiveState::decode, SetExplosiveState::onMessage);
		SecurityCraft.channel.registerMessage(index++, GivePotionEffect.class, GivePotionEffect::encode, GivePotionEffect::decode, GivePotionEffect::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetPassword.class, SetPassword::encode, SetPassword::decode, SetPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, CheckPassword.class, CheckPassword::encode, CheckPassword::decode, CheckPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, MountCamera.class, MountCamera::encode, MountCamera::decode, MountCamera::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetCameraRotation.class, SetCameraRotation::encode, SetCameraRotation::decode, SetCameraRotation::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetPlayerPositionAndRotation.class, SetPlayerPositionAndRotation::encode, SetPlayerPositionAndRotation::decode, SetPlayerPositionAndRotation::onMessage);
		SecurityCraft.channel.registerMessage(index++, OpenGui.class, OpenGui::encode, OpenGui::decode, OpenGui::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleOption.class, ToggleOption::encode, ToggleOption::decode, ToggleOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, RequestTEOwnableUpdate.class, RequestTEOwnableUpdate::encode, RequestTEOwnableUpdate::decode, RequestTEOwnableUpdate::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateTEOwnable.class, UpdateTEOwnable::encode, UpdateTEOwnable::decode, UpdateTEOwnable::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::decode, UpdateSliderValue::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemoveCameraTag.class, RemoveCameraTag::encode, RemoveCameraTag::decode, RemoveCameraTag::onMessage);
		SecurityCraft.channel.registerMessage(index++, InitSentryAnimation.class, InitSentryAnimation::encode, InitSentryAnimation::decode, InitSentryAnimation::onMessage);
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
		registerBlock(event, block, true);
	}

	/**
	 * Registers a block and its ItemBlock
	 * @param block The Block to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, boolean initPage)
	{
		event.getRegistry().register(block);

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
