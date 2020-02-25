package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
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
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
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
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
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
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DataSerializerEntry;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class RegistrationHandler
{
	private static ArrayList<Block> blockPages = new ArrayList<>();
	private static Map<Block,String> blocksDesignedBy = new HashMap<>();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		//		registerBlock(event, SCContent.LASER_BLOCK);
		//		event.getRegistry().register(SCContent.LASER_FIELD);
		//		registerBlock(event, SCContent.KEYPAD);
		//		registerBlock(event, SCContent.MINE);
		//		registerBlock(event, SCContent.DIRT_MINE);
		//		registerBlock(event, SCContent.STONE_MINE, false);
		//		registerBlock(event, SCContent.COBBLESTONE_MINE, false);
		//		registerBlock(event, SCContent.DIAMOND_ORE_MINE, false);
		//		registerBlock(event, SCContent.SAND_MINE, false);
		//		registerBlock(event, SCContent.FURNACE_MINE);
		//		registerBlock(event, SCContent.RETINAL_SCANNER);
		//		event.getRegistry().register(SCContent.REINFORCED_DOOR);
		//		registerBlock(event, SCContent.KEYCARD_READER);
		//		registerBlock(event, SCContent.REINFORCED_IRON_TRAPDOOR);
		//		registerBlock(event, SCContent.BOUNCING_BETTY);
		//		registerBlock(event, SCContent.INVENTORY_SCANNER);
		//		event.getRegistry().register(SCContent.INVENTORY_SCANNER_FIELD);
		//		registerBlock(event, SCContent.TRACK_MINE);
		//		registerBlock(event, SCContent.CAGE_TRAP);
		//		event.getRegistry().register(SCContent.HORIZONTAL_REINFORCED_IRON_BARS);
		//		registerBlock(event, SCContent.PORTABLE_RADAR);
		//		registerBlock(event, SCContent.KEYPAD_CHEST);
		//		registerBlock(event, SCContent.USERNAME_LOGGER);
		//		registerBlock(event, SCContent.ALARM);
		//		registerBlock(event, SCContent.REINFORCED_FENCEGATE);
		//		registerBlock(event, SCContent.PANIC_BUTTON);
		//		registerBlock(event, SCContent.FRAME);
		//		registerBlock(event, SCContent.CLAYMORE);
		//		registerBlock(event, SCContent.KEYPAD_FURNACE);
		//		registerBlock(event, SCContent.SECURITY_CAMERA);
		//		registerBlock(event, SCContent.IRON_FENCE);
		//		registerBlock(event, SCContent.IMS);
		//		registerBlock(event, SCContent.PROTECTO);
		//		event.getRegistry().register(SCContent.SCANNER_DOOR);
		//		event.getRegistry().register(SCContent.SECRET_OAK_WALL_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_OAK_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_SPRUCE_WALL_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_SPRUCE_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_BIRCH_WALL_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_BIRCH_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_JUNGLE_WALL_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_JUNGLE_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_ACACIA_WALL_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_ACACIA_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_DARK_OAK_WALL_SIGN);
		//		event.getRegistry().register(SCContent.SECRET_DARK_OAK_SIGN);
		//		registerBlock(event, SCContent.MOTION_ACTIVATED_LIGHT);
		//		registerBlock(event, SCContent.GRAVEL_MINE, false);
		//		registerBlock(event, SCContent.FAKE_LAVA_BLOCK, false);
		//		registerBlock(event, SCContent.FAKE_WATER_BLOCK, false);
		//		registerBlock(event, SCContent.TROPHY_SYSTEM);
		//		registerBlock(event, SCContent.CRYSTAL_QUARTZ, true);
		//		registerBlock(event, SCContent.CHISELED_CRYSTAL_QUARTZ, false);
		//		registerBlock(event, SCContent.CRYSTAL_QUARTZ_PILLAR, false);
		//		registerBlock(event, SCContent.CRYSTAL_QUARTZ_SLAB, false);
		//		registerBlock(event, SCContent.STAIRS_CRYSTAL_QUARTZ, false);
		//		registerBlock(event, SCContent.BLOCK_POCKET_WALL);
		//		registerBlock(event, SCContent.BLOCK_POCKET_MANAGER, "Henzoid");
		//		registerBlock(event, SCContent.PROJECTOR);

		//register reinforced blocks
		//		for(Field field : SCContent.class.getFields())
		//		{
		//			try
		//			{
		//				if(field.isAnnotationPresent(Reinforced.class))
		//					registerBlock(event, (Block)field.get(null), field.getAnnotation(Reinforced.class).hasPage());
		//			}
		//			catch(IllegalArgumentException | IllegalAccessException e)
		//			{
		//				e.printStackTrace();
		//			}
		//		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		//register item blocks from annotated fields
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					event.getRegistry().register(new ReinforcedBlockItem(((RegistryObject<Block>)field.get(null)).get()));
				else if(field.isAnnotationPresent(RegisterItemBlock.class))
				{
					int tab = field.getAnnotation(RegisterItemBlock.class).value().ordinal();
					RegistryObject<Block> block = (RegistryObject<Block>)field.get(null);

					event.getRegistry().register(new BlockItem(block.get(), new Item.Properties().group(tab == 0 ? SecurityCraft.groupSCTechnical : (tab == 1 ? SecurityCraft.groupSCMine : SecurityCraft.groupSCDecoration))).setRegistryName(block.get().getRegistryName()));
				}

			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		//		SecurityCraft.proxy.registerKeypadChestItem(event);

		//init block sc manual pages
		for(Block block : blockPages)
		{
			if(block == SCContent.REINFORCED_STONE.get())
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
		//		registerItem(event, SCContent.CODEBREAKER);
		//		registerItem(event, SCContent.REINFORCED_DOOR_ITEM);
		//		registerItem(event, SCContent.SCANNER_DOOR_ITEM);
		//		registerItem(event, SCContent.UNIVERSAL_BLOCK_REMOVER);
		//		registerItem(event, SCContent.KEYCARD_LVL_1, () -> ConfigHandler.CONFIG.ableToCraftKeycard1.get());
		//		registerItem(event, SCContent.KEYCARD_LVL_2, () -> ConfigHandler.CONFIG.ableToCraftKeycard2.get());
		//		registerItem(event, SCContent.KEYCARD_LVL_3, () -> ConfigHandler.CONFIG.ableToCraftKeycard3.get());
		//		registerItem(event, SCContent.KEYCARD_LVL_4, () -> ConfigHandler.CONFIG.ableToCraftKeycard4.get());
		//		registerItem(event, SCContent.KEYCARD_LVL_5, () -> ConfigHandler.CONFIG.ableToCraftKeycard5.get());
		//		registerItem(event, SCContent.LIMITED_USE_KEYCARD, () -> ConfigHandler.CONFIG.ableToCraftLUKeycard.get());
		//		registerItem(event, SCContent.REMOTE_ACCESS_MINE);
		//		registerItem(event, SCContent.REMOVE_ACCESS_SENTRY);
		//		registerItem(event, SCContent.FAKE_WATER_BUCKET);
		//		registerItem(event, SCContent.FAKE_LAVA_BUCKET);
		//		registerItem(event, SCContent.UNIVERSAL_BLOCK_MODIFIER);
		//		registerItem(event, SCContent.redstoneModule);
		//		registerItem(event, SCContent.whitelistModule);
		//		registerItem(event, SCContent.blacklistModule);
		//		registerItem(event, SCContent.harmingModule);
		//		registerItem(event, SCContent.smartModule);
		//		registerItem(event, SCContent.storageModule);
		//		registerItem(event, SCContent.disguiseModule);
		//		registerItem(event, SCContent.WIRE_CUTTERS);
		//		registerItem(event, SCContent.ADMIN_TOOL);
		//		registerItem(event, SCContent.KEY_PANEL);
		//		registerItem(event, SCContent.CAMERA_MONITOR);
		//		registerItem(event, SCContent.TASER);
		//		registerItem(event, SCContent.SC_MANUAL);
		//		registerItem(event, SCContent.UNIVERSAL_OWNER_CHANGER);
		//		registerItem(event, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1);
		//		registerItem(event, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2);
		//		registerItem(event, SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3);
		//		registerItem(event, SCContent.BRIEFCASE);
		//		registerItem(event, SCContent.UNIVERSAL_KEY_CHANGER);
		//		event.getRegistry().register(SCContent.TASER_POWERED); //won't show up in the manual
		//		registerItem(event, SCContent.SECRET_OAK_SIGN_ITEM);
		//		event.getRegistry().register(SCContent.SECRET_SPRUCE_SIGN_ITEM);
		//		event.getRegistry().register(SCContent.SECRET_BIRCH_SIGN_ITEM);
		//		event.getRegistry().register(SCContent.SECRET_JUNGLE_SIGN_ITEM);
		//		event.getRegistry().register(SCContent.SECRET_ACACIA_SIGN_ITEM);
		//		event.getRegistry().register(SCContent.SECRET_DARK_OAK_SIGN_ITEM);
		//		registerItem(event, SCContent.SENTRY, "Henzoid");
		//		registerItem(event, SCContent.CRYSTAL_QUARTZ_ITEM);

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
				if(field.isAnnotationPresent(OwnableTE.class))
					teOwnableBlocks.add(((RegistryObject<Block>)field.get(null)).get());

			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		event.getRegistry().register(TileEntityType.Builder.create(OwnableTileEntity::new, teOwnableBlocks.toArray(new Block[teOwnableBlocks.size()])).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ownable")));
		event.getRegistry().register(TileEntityType.Builder.create(SecurityCraftTileEntity::new, SCContent.LASER_FIELD.get(), SCContent.INVENTORY_SCANNER_FIELD.get(), SCContent.IRON_FENCE.get(), SCContent.COBBLESTONE_MINE.get(), SCContent.DIAMOND_ORE_MINE.get(), SCContent.DIRT_MINE.get(), SCContent.FURNACE_MINE.get(), SCContent.GRAVEL_MINE.get(), SCContent.SAND_MINE.get(), SCContent.STONE_MINE.get(), SCContent.BOUNCING_BETTY.get(), SCContent.REINFORCED_FENCEGATE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "abstract")));
		event.getRegistry().register(TileEntityType.Builder.create(KeypadTileEntity::new, SCContent.KEYPAD.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad")));
		event.getRegistry().register(TileEntityType.Builder.create(LaserBlockTileEntity::new, SCContent.LASER_BLOCK.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "laser_block")));
		event.getRegistry().register(TileEntityType.Builder.create(CageTrapTileEntity::new, SCContent.CAGE_TRAP.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "cage_trap")));
		event.getRegistry().register(TileEntityType.Builder.create(KeycardReaderTileEntity::new, SCContent.KEYCARD_READER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keycard_reader")));
		event.getRegistry().register(TileEntityType.Builder.create(InventoryScannerTileEntity::new, SCContent.INVENTORY_SCANNER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "inventory_scanner")));
		event.getRegistry().register(TileEntityType.Builder.create(PortableRadarTileEntity::new, SCContent.PORTABLE_RADAR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "portable_radar")));
		event.getRegistry().register(TileEntityType.Builder.create(SecurityCameraTileEntity::new, SCContent.SECURITY_CAMERA.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "security_camera")));
		event.getRegistry().register(TileEntityType.Builder.create(UsernameLoggerTileEntity::new, SCContent.USERNAME_LOGGER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "username_logger")));
		event.getRegistry().register(TileEntityType.Builder.create(RetinalScannerTileEntity::new, SCContent.RETINAL_SCANNER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "retinal_scanner")));
		event.getRegistry().register(TileEntityType.Builder.create(KeypadChestTileEntity::new, SCContent.KEYPAD_CHEST.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_chest")));
		event.getRegistry().register(TileEntityType.Builder.create(AlarmTileEntity::new, SCContent.ALARM.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "alarm")));
		event.getRegistry().register(TileEntityType.Builder.create(ClaymoreTileEntity::new, SCContent.CLAYMORE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "claymore")));
		event.getRegistry().register(TileEntityType.Builder.create(KeypadFurnaceTileEntity::new, SCContent.KEYPAD_FURNACE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_furnace")));
		event.getRegistry().register(TileEntityType.Builder.create(IMSTileEntity::new, SCContent.IMS.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ims")));
		event.getRegistry().register(TileEntityType.Builder.create(ProtectoTileEntity::new, SCContent.PROTECTO.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "protecto")));
		event.getRegistry().register(TileEntityType.Builder.create(ScannerDoorTileEntity::new, SCContent.SCANNER_DOOR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "scanner_door")));
		event.getRegistry().register(TileEntityType.Builder.create(SecretSignTileEntity::new, SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get(), SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get(), SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get(), SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get(), SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get(), SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "secret_sign")));
		event.getRegistry().register(TileEntityType.Builder.create(MotionActivatedLightTileEntity::new, SCContent.MOTION_ACTIVATED_LIGHT.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "motion_light")));
		event.getRegistry().register(TileEntityType.Builder.create(TrackMineTileEntity::new, SCContent.TRACK_MINE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "track_mine")));
		event.getRegistry().register(TileEntityType.Builder.create(TrophySystemTileEntity::new, SCContent.TROPHY_SYSTEM.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "trophy_system")));
		event.getRegistry().register(TileEntityType.Builder.create(BlockPocketManagerTileEntity::new, SCContent.BLOCK_POCKET_MANAGER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket_manager")));
		event.getRegistry().register(TileEntityType.Builder.create(BlockPocketTileEntity::new, SCContent.BLOCK_POCKET_WALL.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket")));
		event.getRegistry().register(TileEntityType.Builder.create(ReinforcedPressurePlateTileEntity::new, SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(), SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(), SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(), SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(), SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(), SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(), SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_pressure_plate")));
		event.getRegistry().register(TileEntityType.Builder.create(ProjectorTileEntity::new, SCContent.PROJECTOR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "projector")));
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
		SecurityCraft.channel.registerMessage(index++, RefreshDisguisableModel.class, RefreshDisguisableModel::encode, RefreshDisguisableModel::decode, RefreshDisguisableModel::onMessage);
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

	@SubscribeEvent
	public static void registerDataSerializerEntries(RegistryEvent.Register<DataSerializerEntry> event)
	{
		event.getRegistry().register(new DataSerializerEntry(new IDataSerializer<Owner>() {
			@Override
			public void write(PacketBuffer buf, Owner value)
			{
				buf.writeString(value.getName());
				buf.writeString(value.getUUID());
			}

			@Override
			public Owner read(PacketBuffer buf)
			{
				String name = buf.readString(Integer.MAX_VALUE / 4);
				String uuid = buf.readString(Integer.MAX_VALUE / 4);

				return new Owner(name, uuid);
			}

			@Override
			public DataParameter<Owner> createKey(int id)
			{
				return new DataParameter<>(id, this);
			}

			@Override
			public Owner copyValue(Owner value)
			{
				return new Owner(value.getName(), value.getUUID());
			}
		}).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "owner")));
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
