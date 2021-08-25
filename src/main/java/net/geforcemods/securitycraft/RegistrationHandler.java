package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftBlockEntity;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.geforcemods.securitycraft.blockentities.PortableRadarBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.Bullet;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.SecurityCamera;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.ClearLoggerClient;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.network.client.OpenSRATGui;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.OwnableTE;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class RegistrationHandler
{
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		//register item blocks from annotated fields
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).registerBlockItem())
				{
					Block block = ((RegistryObject<Block>)field.get(null)).get();

					event.getRegistry().register(new BlockItem(block, new Item.Properties().tab(SecurityCraft.groupSCDecoration).fireResistant()).setRegistryName(block.getRegistryName()));
				}
				else if(field.isAnnotationPresent(RegisterItemBlock.class))
				{
					int tab = field.getAnnotation(RegisterItemBlock.class).value().ordinal();
					RegistryObject<Block> block = (RegistryObject<Block>)field.get(null);

					event.getRegistry().register(new BlockItem(block.get(), new Item.Properties().tab(tab == 0 ? SecurityCraft.groupSCTechnical : (tab == 1 ? SecurityCraft.groupSCMine : SecurityCraft.groupSCDecoration))).setRegistryName(block.get().getRegistryName()));
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event)
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

		event.getRegistry().register(BlockEntityType.Builder.of(OwnableBlockEntity::new, teOwnableBlocks.toArray(new Block[teOwnableBlocks.size()])).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ownable")));
		event.getRegistry().register(BlockEntityType.Builder.of(SecurityCraftBlockEntity::new, SCContent.LASER_FIELD.get(), SCContent.INVENTORY_SCANNER_FIELD.get(), SCContent.IRON_FENCE.get(), SCContent.COBBLESTONE_MINE.get(), SCContent.DIAMOND_ORE_MINE.get(), SCContent.DIRT_MINE.get(), SCContent.FURNACE_MINE.get(), SCContent.GRAVEL_MINE.get(), SCContent.SAND_MINE.get(), SCContent.STONE_MINE.get(), SCContent.BOUNCING_BETTY.get(), SCContent.REINFORCED_FENCEGATE.get(), SCContent.ANCIENT_DEBRIS_MINE.get(), SCContent.COAL_ORE_MINE.get(), SCContent.EMERALD_ORE_MINE.get(), SCContent.GOLD_ORE_MINE.get(), SCContent.GILDED_BLACKSTONE_MINE.get(), SCContent.IRON_ORE_MINE.get(), SCContent.LAPIS_ORE_MINE.get(), SCContent.NETHER_GOLD_ORE_MINE.get(), SCContent.QUARTZ_ORE_MINE.get(), SCContent.REDSTONE_ORE_MINE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "abstract")));
		event.getRegistry().register(BlockEntityType.Builder.of(KeypadBlockEntity::new, SCContent.KEYPAD.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad")));
		event.getRegistry().register(BlockEntityType.Builder.of(LaserBlockBlockEntity::new, SCContent.LASER_BLOCK.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "laser_block")));
		event.getRegistry().register(BlockEntityType.Builder.of(CageTrapBlockEntity::new, SCContent.CAGE_TRAP.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "cage_trap")));
		event.getRegistry().register(BlockEntityType.Builder.of(KeycardReaderBlockEntity::new, SCContent.KEYCARD_READER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keycard_reader")));
		event.getRegistry().register(BlockEntityType.Builder.of(InventoryScannerBlockEntity::new, SCContent.INVENTORY_SCANNER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "inventory_scanner")));
		event.getRegistry().register(BlockEntityType.Builder.of(PortableRadarBlockEntity::new, SCContent.PORTABLE_RADAR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "portable_radar")));
		event.getRegistry().register(BlockEntityType.Builder.of(SecurityCameraBlockEntity::new, SCContent.SECURITY_CAMERA.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "security_camera")));
		event.getRegistry().register(BlockEntityType.Builder.of(UsernameLoggerBlockEntity::new, SCContent.USERNAME_LOGGER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "username_logger")));
		event.getRegistry().register(BlockEntityType.Builder.of(RetinalScannerBlockEntity::new, SCContent.RETINAL_SCANNER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "retinal_scanner")));
		event.getRegistry().register(BlockEntityType.Builder.of(KeypadChestBlockEntity::new, SCContent.KEYPAD_CHEST.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_chest")));
		event.getRegistry().register(BlockEntityType.Builder.of(AlarmBlockEntity::new, SCContent.ALARM.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "alarm")));
		event.getRegistry().register(BlockEntityType.Builder.of(ClaymoreBlockEntity::new, SCContent.CLAYMORE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "claymore")));
		event.getRegistry().register(BlockEntityType.Builder.of(KeypadFurnaceBlockEntity::new, SCContent.KEYPAD_FURNACE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_furnace")));
		event.getRegistry().register(BlockEntityType.Builder.of(IMSBlockEntity::new, SCContent.IMS.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ims")));
		event.getRegistry().register(BlockEntityType.Builder.of(ProtectoBlockEntity::new, SCContent.PROTECTO.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "protecto")));
		event.getRegistry().register(BlockEntityType.Builder.of(ScannerDoorBlockEntity::new, SCContent.SCANNER_DOOR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "scanner_door")));
		event.getRegistry().register(BlockEntityType.Builder.of(SecretSignBlockEntity::new, SCContent.SECRET_OAK_SIGN.get(), SCContent.SECRET_OAK_WALL_SIGN.get(), SCContent.SECRET_SPRUCE_SIGN.get(), SCContent.SECRET_SPRUCE_WALL_SIGN.get(), SCContent.SECRET_BIRCH_SIGN.get(), SCContent.SECRET_BIRCH_WALL_SIGN.get(), SCContent.SECRET_JUNGLE_SIGN.get(), SCContent.SECRET_JUNGLE_WALL_SIGN.get(), SCContent.SECRET_ACACIA_SIGN.get(), SCContent.SECRET_ACACIA_WALL_SIGN.get(), SCContent.SECRET_DARK_OAK_SIGN.get(), SCContent.SECRET_DARK_OAK_WALL_SIGN.get(), SCContent.SECRET_CRIMSON_SIGN.get(), SCContent.SECRET_CRIMSON_WALL_SIGN.get(), SCContent.SECRET_WARPED_SIGN.get(), SCContent.SECRET_WARPED_WALL_SIGN.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "secret_sign")));
		event.getRegistry().register(BlockEntityType.Builder.of(MotionActivatedLightBlockEntity::new, SCContent.MOTION_ACTIVATED_LIGHT.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "motion_light")));
		event.getRegistry().register(BlockEntityType.Builder.of(TrackMineBlockEntity::new, SCContent.TRACK_MINE.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "track_mine")));
		event.getRegistry().register(BlockEntityType.Builder.of(TrophySystemBlockEntity::new, SCContent.TROPHY_SYSTEM.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "trophy_system")));
		event.getRegistry().register(BlockEntityType.Builder.of(BlockPocketManagerBlockEntity::new, SCContent.BLOCK_POCKET_MANAGER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket_manager")));
		event.getRegistry().register(BlockEntityType.Builder.of(BlockPocketBlockEntity::new, SCContent.BLOCK_POCKET_WALL.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(), SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket")));
		event.getRegistry().register(BlockEntityType.Builder.of(AllowlistOnlyBlockEntity::new, SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(), SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(), SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(), SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(), SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(), SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(), SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(), SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(), SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get(), SCContent.REINFORCED_STONE_BUTTON.get(), SCContent.REINFORCED_ACACIA_BUTTON.get(), SCContent.REINFORCED_BIRCH_BUTTON.get(), SCContent.REINFORCED_CRIMSON_BUTTON.get(), SCContent.REINFORCED_DARK_OAK_BUTTON.get(), SCContent.REINFORCED_JUNGLE_BUTTON.get(), SCContent.REINFORCED_OAK_BUTTON.get(), SCContent.REINFORCED_SPRUCE_BUTTON.get(), SCContent.REINFORCED_WARPED_BUTTON.get(), SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(), SCContent.REINFORCED_LEVER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_pressure_plate")));
		event.getRegistry().register(BlockEntityType.Builder.of(ReinforcedHopperBlockEntity::new, SCContent.REINFORCED_HOPPER.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_hopper")));
		event.getRegistry().register(BlockEntityType.Builder.of(ProjectorBlockEntity::new, SCContent.PROJECTOR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "projector")));
		event.getRegistry().register(BlockEntityType.Builder.of(KeypadDoorBlockEntity::new, SCContent.KEYPAD_DOOR.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_door")));
		event.getRegistry().register(BlockEntityType.Builder.of(ReinforcedIronBarsBlockEntity::new, SCContent.REINFORCED_IRON_BARS.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_iron_bars")));
		event.getRegistry().register(BlockEntityType.Builder.of(ReinforcedCauldronBlockEntity::new, SCContent.REINFORCED_CAULDRON.get(), SCContent.REINFORCED_WATER_CAULDRON.get(), SCContent.REINFORCED_LAVA_CAULDRON.get(), SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_cauldron")));
		event.getRegistry().register(BlockEntityType.Builder.of(ReinforcedPistonMovingBlockEntity::new, SCContent.REINFORCED_MOVING_PISTON.get()).build(null).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "reinforced_piston")));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event)
	{
		EntityType<Sentry> sentry = (EntityType<Sentry>)EntityType.Builder.<Sentry>of(Sentry::new, MobCategory.MISC)
				.sized(1.0F, 2.0F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.build(SecurityCraft.MODID + ":sentry")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "sentry"));

		event.getRegistry().register(EntityType.Builder.<BouncingBetty>of(BouncingBetty::new, MobCategory.MISC)
				.sized(0.5F, 0.2F)
				.setTrackingRange(128)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.build(SecurityCraft.MODID + ":bouncingbetty")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "bouncingbetty")));
		event.getRegistry().register(EntityType.Builder.<IMSBomb>of(IMSBomb::new, MobCategory.MISC)
				.sized(0.25F, 0.3F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.build(SecurityCraft.MODID + ":imsbomb")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "imsbomb")));
		event.getRegistry().register(EntityType.Builder.<SecurityCamera>of(SecurityCamera::new, MobCategory.MISC)
				.sized(0.0001F, 0.0001F)
				.setTrackingRange(256)
				.setUpdateInterval(20)
				.setShouldReceiveVelocityUpdates(true)
				.build(SecurityCraft.MODID + ":securitycamera")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "securitycamera")));
		event.getRegistry().register(sentry);
		event.getRegistry().register(EntityType.Builder.<Bullet>of(Bullet::new, MobCategory.MISC)
				.sized(0.15F, 0.1F)
				.setTrackingRange(256)
				.setUpdateInterval(1)
				.setShouldReceiveVelocityUpdates(true)
				.build(SecurityCraft.MODID + ":bullet")
				.setRegistryName(new ResourceLocation(SecurityCraft.MODID, "bullet")));
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
	{
		event.put(SCContent.eTypeSentry, Mob.createMobAttributes().build());
	}

	@SubscribeEvent
	public static void registerMenus(RegistryEvent.Register<MenuType<?>> event)
	{
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new BlockReinforcerMenu(windowId, inv, data.readBoolean())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_reinforcer")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericMenu(SCContent.mTypeBriefcase, windowId)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "briefcase")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(inv, SCContent.BRIEFCASE.get())))).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "briefcase_inventory")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericMenu(SCContent.mTypeBriefcaseSetup, windowId)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "briefcase_setup")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new CustomizeBlockMenu(windowId, inv.player.level, data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "customize_block")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(PlayerUtils.getSelectedItemStack(inv, SCContent.DISGUISE_MODULE.get())))).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "disguise_module")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new InventoryScannerMenu(windowId, inv.player.level, data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "inventory_scanner")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new KeypadFurnaceMenu(windowId, inv.player.level, data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keypad_furnace")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new ProjectorMenu(windowId, inv.player.level, data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "projector")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEMenu(SCContent.mTypeCheckPassword, windowId, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "check_password")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEMenu(SCContent.mTypeSetPassword, windowId, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "set_password")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEMenu(SCContent.mTypeUsernameLogger, windowId, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "username_logger")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEMenu(SCContent.mTypeIMS, windowId, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "ims")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new KeycardReaderMenu(windowId, inv, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "keycard_setup")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEMenu(SCContent.mTypeKeyChanger, windowId, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "key_changer")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEMenu(SCContent.mTypeTrophySystem, windowId, inv.player.level, data.readBlockPos())).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "trophy_system")));
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new BlockPocketManagerMenu(windowId, inv.player.level, data.readBlockPos(), inv)).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "block_pocket_manager")));
	}

	public static void registerPackets()
	{
		int index = 0;

		//client
		SecurityCraft.channel.registerMessage(index++, ClearLoggerClient.class, ClearLoggerClient::encode, ClearLoggerClient::decode, ClearLoggerClient::onMessage);
		SecurityCraft.channel.registerMessage(index++, InitSentryAnimation.class, InitSentryAnimation::encode, InitSentryAnimation::decode, InitSentryAnimation::onMessage);
		SecurityCraft.channel.registerMessage(index++, OpenSRATGui.class, OpenSRATGui::encode, OpenSRATGui::decode, OpenSRATGui::onMessage);
		SecurityCraft.channel.registerMessage(index++, PlaySoundAtPos.class, PlaySoundAtPos::encode, PlaySoundAtPos::decode, PlaySoundAtPos::onMessage);
		SecurityCraft.channel.registerMessage(index++, RefreshDisguisableModel.class, RefreshDisguisableModel::encode, RefreshDisguisableModel::decode, RefreshDisguisableModel::onMessage);
		SecurityCraft.channel.registerMessage(index++, SendTip.class, SendTip::encode, SendTip::decode, SendTip::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetTrophySystemTarget.class, SetTrophySystemTarget::encode, SetTrophySystemTarget::decode, SetTrophySystemTarget::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::decode, UpdateLogger::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnClient.class, UpdateNBTTagOnClient::encode, UpdateNBTTagOnClient::decode, UpdateNBTTagOnClient::onMessage);
		//server
		SecurityCraft.channel.registerMessage(index++, AssembleBlockPocket.class, AssembleBlockPocket::encode, AssembleBlockPocket::decode, AssembleBlockPocket::onMessage);
		SecurityCraft.channel.registerMessage(index++, CheckPassword.class, CheckPassword::encode, CheckPassword::decode, CheckPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, ClearLoggerServer.class, ClearLoggerServer::encode, ClearLoggerServer::decode, ClearLoggerServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, GiveNightVision.class, GiveNightVision::encode, GiveNightVision::decode, GiveNightVision::onMessage);
		SecurityCraft.channel.registerMessage(index++, MountCamera.class, MountCamera::encode, MountCamera::decode, MountCamera::onMessage);
		SecurityCraft.channel.registerMessage(index++, OpenBriefcaseGui.class, OpenBriefcaseGui::encode, OpenBriefcaseGui::decode, OpenBriefcaseGui::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemoteControlMine.class, RemoteControlMine::encode, RemoteControlMine::decode, RemoteControlMine::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemoveCameraTag.class, RemoveCameraTag::encode, RemoveCameraTag::decode, RemoveCameraTag::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetCameraPowered.class, SetCameraPowered::encode, SetCameraPowered::decode, SetCameraPowered::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetCameraRotation.class, SetCameraRotation::encode, SetCameraRotation::decode, SetCameraRotation::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetKeycardUses.class, SetKeycardUses::encode, SetKeycardUses::decode, SetKeycardUses::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetPassword.class, SetPassword::encode, SetPassword::decode, SetPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetSentryMode.class, SetSentryMode::encode, SetSentryMode::decode, SetSentryMode::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncBlockPocketManager.class, SyncBlockPocketManager::encode, SyncBlockPocketManager::decode, SyncBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncProjector.class, SyncProjector::encode, SyncProjector::decode, SyncProjector::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncTrophySystem.class, SyncTrophySystem::encode, SyncTrophySystem::decode, SyncTrophySystem::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncIMSTargetingOption.class, SyncIMSTargetingOption::encode, SyncIMSTargetingOption::decode, SyncIMSTargetingOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncKeycardSettings.class, SyncKeycardSettings::encode, SyncKeycardSettings::decode, SyncKeycardSettings::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::decode, ToggleBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleOption.class, ToggleOption::encode, ToggleOption::decode, ToggleOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnServer.class, UpdateNBTTagOnServer::encode, UpdateNBTTagOnServer::decode, UpdateNBTTagOnServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::decode, UpdateSliderValue::onMessage);
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
	public static void registerRecipeSerializer(RegistryEvent.Register<RecipeSerializer<?>> event)
	{
		event.getRegistry().register(new SimpleRecipeSerializer<>(LimitedUseKeycardRecipe::new).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_recipe")));
	}

	@SubscribeEvent
	public static void registerDataSerializerEntries(RegistryEvent.Register<DataSerializerEntry> event)
	{
		event.getRegistry().register(new DataSerializerEntry(new EntityDataSerializer<Owner>() {
			@Override
			public void write(FriendlyByteBuf buf, Owner value)
			{
				buf.writeUtf(value.getName());
				buf.writeUtf(value.getUUID());
			}

			@Override
			public Owner read(FriendlyByteBuf buf)
			{
				String name = buf.readUtf(Integer.MAX_VALUE / 4);
				String uuid = buf.readUtf(Integer.MAX_VALUE / 4);

				return new Owner(name, uuid);
			}

			@Override
			public EntityDataAccessor<Owner> createAccessor(int id)
			{
				return new EntityDataAccessor<>(id, this);
			}

			@Override
			public Owner copy(Owner value)
			{
				return new Owner(value.getName(), value.getUUID());
			}
		}).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "owner")));
	}
}
