package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.containers.BriefcaseInventory;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.containers.ModuleInventory;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.TaserBulletEntity;
import net.geforcemods.securitycraft.items.ReinforcedBlockItem;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard1Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard2Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard3Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard4Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleKeycard5Condition;
import net.geforcemods.securitycraft.misc.conditions.ToggleLimitedUseKeycardCondition;
import net.geforcemods.securitycraft.network.client.ClearLoggerClient;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.network.client.RefreshKeypadModel;
import net.geforcemods.securitycraft.network.client.SetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.client.UpdateTEOwnable;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.CloseFurnace;
import net.geforcemods.securitycraft.network.server.GivePotionEffect;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.network.server.SetExplosiveState;
import net.geforcemods.securitycraft.network.server.SetKeycardLevel;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.network.server.SetScanType;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
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
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
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
import net.geforcemods.securitycraft.util.Ownable;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class RegistrationHandler
{
	private static ArrayList<Block> blockPages = new ArrayList<Block>();
	private static Map<Block,String> blocksDesignedBy = new HashMap<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		//fluids first so the fluid blocks can be registered correctly
		SetupHandler.setupFluids();
		SetupHandler.setupBlocks();
		SetupHandler.setupReinforcedBlocks();
		SetupHandler.setupMines();

		ForgeRegistries.FLUIDS.register(SCContent.flowingFakeWater.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "flowing_fake_water")));
		ForgeRegistries.FLUIDS.register(SCContent.fakeWater.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "fake_water")));
		ForgeRegistries.FLUIDS.register(SCContent.flowingFakeLava.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "flowing_fake_lava")));
		ForgeRegistries.FLUIDS.register(SCContent.fakeLava.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "fake_lava")));

		registerBlock(event, SCContent.laserBlock);
		event.getRegistry().register(SCContent.laserField);
		registerBlock(event, SCContent.keypad);
		registerBlock(event, SCContent.mine);
		registerBlock(event, SCContent.dirtMine);
		registerBlock(event, SCContent.stoneMine, false);
		registerBlock(event, SCContent.cobblestoneMine, false);
		registerBlock(event, SCContent.diamondOreMine, false);
		registerBlock(event, SCContent.sandMine, false);
		registerBlock(event, SCContent.furnaceMine);
		registerBlock(event, SCContent.retinalScanner);
		event.getRegistry().register(SCContent.reinforcedDoor);
		registerBlock(event, SCContent.keycardReader);
		registerBlock(event, SCContent.reinforcedIronTrapdoor);
		registerBlock(event, SCContent.bouncingBetty);
		registerBlock(event, SCContent.inventoryScanner);
		event.getRegistry().register(SCContent.inventoryScannerField);
		registerBlock(event, SCContent.trackMine);
		registerBlock(event, SCContent.cageTrap);
		registerBlock(event, SCContent.portableRadar);
		registerBlock(event, SCContent.keypadChest);
		registerBlock(event, SCContent.usernameLogger);
		registerBlock(event, SCContent.alarm);
		registerBlock(event, SCContent.reinforcedFencegate);
		registerBlock(event, SCContent.panicButton);
		registerBlock(event, SCContent.frame);
		registerBlock(event, SCContent.claymore);
		registerBlock(event, SCContent.keypadFurnace);
		registerBlock(event, SCContent.securityCamera);
		registerBlock(event, SCContent.ironFence);
		registerBlock(event, SCContent.ims);
		registerBlock(event, SCContent.protecto);
		event.getRegistry().register(SCContent.scannerDoor);
		event.getRegistry().register(SCContent.secretOakWallSign);
		event.getRegistry().register(SCContent.secretOakSign);
		event.getRegistry().register(SCContent.secretSpruceWallSign);
		event.getRegistry().register(SCContent.secretSpruceSign);
		event.getRegistry().register(SCContent.secretBirchWallSign);
		event.getRegistry().register(SCContent.secretBirchSign);
		event.getRegistry().register(SCContent.secretJungleWallSign);
		event.getRegistry().register(SCContent.secretJungleSign);
		event.getRegistry().register(SCContent.secretAcaciaWallSign);
		event.getRegistry().register(SCContent.secretAcaciaSign);
		event.getRegistry().register(SCContent.secretDarkOakWallSign);
		event.getRegistry().register(SCContent.secretDarkOakSign);
		registerBlock(event, SCContent.motionActivatedLight);
		registerBlock(event, SCContent.gravelMine, false);
		registerBlock(event, SCContent.fakeLavaBlock, false);
		registerBlock(event, SCContent.fakeWaterBlock, false);
		registerBlock(event, SCContent.trophySystem);
		registerBlock(event, SCContent.crystalQuartz, true);
		registerBlock(event, SCContent.chiseledCrystalQuartz, false);
		registerBlock(event, SCContent.crystalQuartzPillar, false);
		registerBlock(event, SCContent.crystalQuartzSlab, false);
		registerBlock(event, SCContent.stairsCrystalQuartz, false);
		registerBlock(event, SCContent.blockPocketWall);
		registerBlock(event, SCContent.blockPocketManager, "Henzoid");

		//register reinforced blocks
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					registerBlock(event, (Block)field.get(null), field.getAnnotation(Reinforced.class).hasPage());
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
		SetupHandler.setupItems();

		//register item blocks from annotated fields
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					event.getRegistry().register(new ReinforcedBlockItem((Block)field.get(null)));
				else if(field.isAnnotationPresent(RegisterItemBlock.class))
				{
					int tab = field.getAnnotation(RegisterItemBlock.class).value().ordinal();
					Block block = (Block)field.get(null);

					event.getRegistry().register(new BlockItem(block, new Item.Properties().group(tab == 0 ? SecurityCraft.groupSCTechnical : (tab == 1 ? SecurityCraft.groupSCMine : SecurityCraft.groupSCDecoration))).setRegistryName(block.getRegistryName()));
				}

			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		SecurityCraft.proxy.registerKeypadChestItem(event);

		//init block sc manual pages
		for(Block block : blockPages)
		{
			if(block == SCContent.reinforcedStone)
				SecurityCraft.instance.manualPages.add(new SCManualPage(block.asItem(), "help.securitycraft:reinforced.info"));
			else
			{
				SCManualPage page = new SCManualPage(block.asItem(), "help" + block.getTranslationKey().substring(5) + ".info");

				if(blocksDesignedBy.containsKey(block))
					page.setDesignedBy(blocksDesignedBy.get(block));

				SecurityCraft.instance.manualPages.add(page);
			}
		}

		//items
		registerItem(event, SCContent.codebreaker);
		registerItem(event, SCContent.reinforcedDoorItem);
		registerItem(event, SCContent.scannerDoorItem);
		registerItem(event, SCContent.universalBlockRemover);
		registerItem(event, SCContent.keycardLvl1, () -> CommonConfig.CONFIG.ableToCraftKeycard1.get());
		registerItem(event, SCContent.keycardLvl2, () -> CommonConfig.CONFIG.ableToCraftKeycard2.get());
		registerItem(event, SCContent.keycardLvl3, () -> CommonConfig.CONFIG.ableToCraftKeycard3.get());
		registerItem(event, SCContent.keycardLvl4, () -> CommonConfig.CONFIG.ableToCraftKeycard4.get());
		registerItem(event, SCContent.keycardLvl5, () -> CommonConfig.CONFIG.ableToCraftKeycard5.get());
		registerItem(event, SCContent.limitedUseKeycard, () -> CommonConfig.CONFIG.ableToCraftLUKeycard.get());
		registerItem(event, SCContent.remoteAccessMine);
		registerItem(event, SCContent.remoteAccessSentry);
		registerItem(event, SCContent.fWaterBucket);
		registerItem(event, SCContent.fLavaBucket);
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
		registerItem(event, SCContent.secretOakSignItem);
		event.getRegistry().register(SCContent.secretSpruceSignItem);
		event.getRegistry().register(SCContent.secretBirchSignItem);
		event.getRegistry().register(SCContent.secretJungleSignItem);
		event.getRegistry().register(SCContent.secretAcaciaSignItem);
		event.getRegistry().register(SCContent.secretDarkOakSignItem);
		registerItem(event, SCContent.sentry, "Henzoid");
		registerItem(event, SCContent.crystalQuartzItem);

		//clear unused memory
		blockPages = null;
		blocksDesignedBy = null;
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		List<Block> teOwnableBlocks = new ArrayList<>();

		//find all blocks whose tile entity is TileEntityOwnable
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Ownable.class))
					teOwnableBlocks.add((Block)field.get(null));

			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		event.getRegistry().register(TileEntityType.Builder.create(OwnableTileEntity::new, teOwnableBlocks.toArray(new Block[teOwnableBlocks.size()])).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ownable")));
		event.getRegistry().register(TileEntityType.Builder.create(SecurityCraftTileEntity::new, SCContent.laserField, SCContent.inventoryScannerField).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "abstract")));
		event.getRegistry().register(TileEntityType.Builder.create(KeypadTileEntity::new, SCContent.keypad).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad")));
		event.getRegistry().register(TileEntityType.Builder.create(LaserBlockTileEntity::new, SCContent.laserBlock).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "laser_block")));
		event.getRegistry().register(TileEntityType.Builder.create(CageTrapTileEntity::new, SCContent.cageTrap).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "cage_trap")));
		event.getRegistry().register(TileEntityType.Builder.create(KeycardReaderTileEntity::new, SCContent.keycardReader).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keycard_reader")));
		event.getRegistry().register(TileEntityType.Builder.create(InventoryScannerTileEntity::new, SCContent.inventoryScanner).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "inventory_scanner")));
		event.getRegistry().register(TileEntityType.Builder.create(PortableRadarTileEntity::new, SCContent.portableRadar).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "portable_radar")));
		event.getRegistry().register(TileEntityType.Builder.create(SecurityCameraTileEntity::new, SCContent.securityCamera).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "security_camera")));
		event.getRegistry().register(TileEntityType.Builder.create(UsernameLoggerTileEntity::new, SCContent.usernameLogger).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "username_logger")));
		event.getRegistry().register(TileEntityType.Builder.create(RetinalScannerTileEntity::new, SCContent.retinalScanner).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "retinal_scanner")));
		event.getRegistry().register(TileEntityType.Builder.create(KeypadChestTileEntity::new, SCContent.keypadChest).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_chest")));
		event.getRegistry().register(TileEntityType.Builder.create(AlarmTileEntity::new, SCContent.alarm).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "alarm")));
		event.getRegistry().register(TileEntityType.Builder.create(ClaymoreTileEntity::new, SCContent.claymore).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "claymore")));
		event.getRegistry().register(TileEntityType.Builder.create(KeypadFurnaceTileEntity::new, SCContent.keypadFurnace).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_furnace")));
		event.getRegistry().register(TileEntityType.Builder.create(IMSTileEntity::new, SCContent.ims).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ims")));
		event.getRegistry().register(TileEntityType.Builder.create(ProtectoTileEntity::new, SCContent.protecto).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "protecto")));
		event.getRegistry().register(TileEntityType.Builder.create(ScannerDoorTileEntity::new, SCContent.scannerDoor).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "scanner_door")));
		event.getRegistry().register(TileEntityType.Builder.create(SecretSignTileEntity::new, SCContent.secretOakSign, SCContent.secretOakWallSign, SCContent.secretSpruceSign, SCContent.secretSpruceWallSign, SCContent.secretBirchSign, SCContent.secretBirchWallSign, SCContent.secretJungleSign, SCContent.secretJungleWallSign, SCContent.secretAcaciaSign, SCContent.secretAcaciaWallSign, SCContent.secretDarkOakSign, SCContent.secretDarkOakWallSign).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "secret_sign")));
		event.getRegistry().register(TileEntityType.Builder.create(MotionActivatedLightTileEntity::new, SCContent.motionActivatedLight).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "motion_light")));
		event.getRegistry().register(TileEntityType.Builder.create(TrackMineTileEntity::new, SCContent.trackMine).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "track_mine")));
		event.getRegistry().register(TileEntityType.Builder.create(TrophySystemTileEntity::new, SCContent.trophySystem).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "trophy_system")));
		event.getRegistry().register(TileEntityType.Builder.create(BlockPocketManagerTileEntity::new, SCContent.blockPocketManager).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket_manager")));
		event.getRegistry().register(TileEntityType.Builder.create(BlockPocketTileEntity::new, SCContent.blockPocketWall, SCContent.reinforcedCrystalQuartz, SCContent.reinforcedChiseledCrystalQuartz, SCContent.reinforcedCrystalQuartzPillar).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket")));
		event.getRegistry().register(TileEntityType.Builder.create(ReinforcedPressurePlateTileEntity::new, SCContent.reinforcedStonePressurePlate, SCContent.reinforcedAcaciaPressurePlate, SCContent.reinforcedBirchPressurePlate, SCContent.reinforcedDarkOakPressurePlate, SCContent.reinforcedJunglePressurePlate, SCContent.reinforcedOakPressurePlate, SCContent.reinforcedSprucePressurePlate).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_pressure_plate")));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event)
	{
		event.getRegistry().register(EntityType.Builder.<BouncingBettyEntity>create(BouncingBettyEntity::new, EntityClassification.MISC)
				.size(0.5F, 0.2F)
				.setTrackingRange(128)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new BouncingBettyEntity(SCContent.eTypeBouncingBetty, world))
				.build(SecurityCraft.MODID + ":bouncingbetty")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "bouncingbetty")));
		event.getRegistry().register(EntityType.Builder.<TaserBulletEntity>create(TaserBulletEntity::new, EntityClassification.MISC)
				.size(0.01F, 0.01F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new TaserBulletEntity(SCContent.eTypeTaserBullet, world))
				.build(SecurityCraft.MODID + ":taserbullet")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "taserbullet")));
		event.getRegistry().register(EntityType.Builder.<IMSBombEntity>create(IMSBombEntity::new, EntityClassification.MISC)
				.size(0.25F, 0.3F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new IMSBombEntity(SCContent.eTypeImsBomb, world))
				.build(SecurityCraft.MODID + ":imsbomb")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "imsbomb")));
		event.getRegistry().register(EntityType.Builder.<SecurityCameraEntity>create(SecurityCameraEntity::new, EntityClassification.MISC)
				.size(0.0001F, 0.0001F)
				.setTrackingRange(256)
				.setUpdateInterval(20)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new SecurityCameraEntity(SCContent.eTypeSecurityCamera, world))
				.build(SecurityCraft.MODID + ":securitycamera")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "securitycamera")));
		event.getRegistry().register(EntityType.Builder.<SentryEntity>create(SentryEntity::new, EntityClassification.MISC)
				.size(1.0F, 2.0F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new SentryEntity(SCContent.eTypeSentry, world))
				.build(SecurityCraft.MODID + ":sentry")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "sentry")));
		event.getRegistry().register(EntityType.Builder.<BulletEntity>create(BulletEntity::new, EntityClassification.MISC)
				.size(0.15F, 0.1F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.setCustomClientFactory((spawnEntity, world) -> new BulletEntity(SCContent.eTypeBullet, world))
				.build(SecurityCraft.MODID + ":bullet")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "bullet")));
	}

	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event)
	{
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new BlockReinforcerContainer(windowId, inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_reinforcer")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericContainer(SCContent.cTypeBriefcase, windowId)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "briefcase")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new BriefcaseContainer(windowId, inv, new BriefcaseInventory(inv.getCurrentItem()))).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "briefcase_inventory")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericContainer(SCContent.cTypeBriefcaseSetup, windowId)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "briefcase_setup")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new CustomizeBlockContainer(windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "customize_block")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new DisguiseModuleContainer(windowId, inv, new ModuleInventory(inv.getCurrentItem()))).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "disguise_module")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new InventoryScannerContainer(windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "inventory_scanner")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new KeypadFurnaceContainer(windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_furnace")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "check_password")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeSetPassword, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "set_password")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeUsernameLogger, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "username_logger")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeIMS, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ims")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeKeycardSetup, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keycard_setup")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeKeyChanger, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "key_changer")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeBlockPocketManager, windowId, SecurityCraft.proxy.getClientWorld(), data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket_manager")));
	}

	public static void registerPackets()
	{
		int index = 0;

		SecurityCraft.channel.registerMessage(index++, SetScanType.class, SetScanType::encode, SetScanType::decode, SetScanType::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetKeycardLevel.class, SetKeycardLevel::encode, SetKeycardLevel::decode, SetKeycardLevel::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::decode, UpdateLogger::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnClient.class, UpdateNBTTagOnClient::encode, UpdateNBTTagOnClient::decode, UpdateNBTTagOnClient::onMessage);
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
		SecurityCraft.channel.registerMessage(index++, SetCameraPowered.class, SetCameraPowered::encode, SetCameraPowered::decode, SetCameraPowered::onMessage);
		SecurityCraft.channel.registerMessage(index++, CloseFurnace.class, CloseFurnace::encode, CloseFurnace::decode, CloseFurnace::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnServer.class, UpdateNBTTagOnServer::encode, UpdateNBTTagOnServer::decode, UpdateNBTTagOnServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncTENBTTag.class, SyncTENBTTag::encode, SyncTENBTTag::decode, SyncTENBTTag::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::decode, ToggleBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, ClearLoggerServer.class, ClearLoggerServer::encode, ClearLoggerServer::decode, ClearLoggerServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, ClearLoggerClient.class, ClearLoggerClient::encode, ClearLoggerClient::decode, ClearLoggerClient::onMessage);
		SecurityCraft.channel.registerMessage(index++, RefreshKeypadModel.class, RefreshKeypadModel::encode, RefreshKeypadModel::decode, RefreshKeypadModel::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetSentryMode.class, SetSentryMode::encode, SetSentryMode::decode, SetSentryMode::onMessage);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		for(int i = 0; i < SCSounds.values().length; i++)
		{
			event.getRegistry().register(SCSounds.values()[i].event);
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		CraftingHelper.register(ToggleKeycard1Condition.Serializer.INSTANCE);
		CraftingHelper.register(ToggleKeycard2Condition.Serializer.INSTANCE);
		CraftingHelper.register(ToggleKeycard3Condition.Serializer.INSTANCE);
		CraftingHelper.register(ToggleKeycard4Condition.Serializer.INSTANCE);
		CraftingHelper.register(ToggleKeycard5Condition.Serializer.INSTANCE);
		CraftingHelper.register(ToggleLimitedUseKeycardCondition.Serializer.INSTANCE);
	}

	/**
	 * Registers a block and its ItemBlock and adds the help info for the block to the SecurityCraft manual item
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, String designedBy)
	{
		registerBlock(event, block, true, designedBy);
	}

	/**
	 * Registers a block and its BlockItem and adds the help info for the block to the SecurityCraft manual item
	 * @param block The block to register
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block)
	{
		registerBlock(event, block, true, null);
	}

	/**
	 * Registers a block and its BlockItem
	 * @param block The Block to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, boolean initPage)
	{
		registerBlock(event, block, initPage, null);
	}

	/**
	 * Registers a block and its ItemBlock
	 * @param block The Block to register
	 * @param initPage Wether a SecurityCraft Manual page should be added for the block
	 * @param designedBy The name of the person who designed this block
	 */
	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, boolean initPage, String designedBy)
	{
		event.getRegistry().register(block);

		if(initPage)
			blockPages.add(block);

		if(designedBy != null)
			blocksDesignedBy.put(block, designedBy);
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
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, BooleanSupplier configValue)
	{
		registerItem(event, item, configValue, "");
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, String designedBy)
	{
		event.getRegistry().register(item); //need this call first before accessing the translation key

		SCManualPage page = new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info");

		page.setDesignedBy(designedBy);
		SecurityCraft.instance.manualPages.add(page);
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(RegistryEvent.Register<Item> event, Item item, BooleanSupplier configValue, String designedBy)
	{
		event.getRegistry().register(item); //need this call first before accessing the translation key

		SCManualPage page = new SCManualPage(item, "help." + item.getTranslationKey().substring(5) + ".info", configValue);

		page.setDesignedBy(designedBy);
		SecurityCraft.instance.manualPages.add(page);
	}
}
