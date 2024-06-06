package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.UpdateLaserColors;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RemoveMineFromMRAT;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetDefaultCameraViewingDirection;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncBlockReinforcer;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncSecureRedstoneInterface;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleNightVision;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class RegistrationHandler {
	private RegistrationHandler() {}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		event.register(Keys.ITEMS, helper -> {
			//register item blocks from annotated fields
			for (Field field : SCContent.class.getFields()) {
				try {
					if (field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).registerBlockItem()) {
						SCItemGroup group = field.getAnnotation(Reinforced.class).itemGroup();
						Block block = ((RegistryObject<Block>) field.get(null)).get();
						Item blockItem = new BlockItem(block, new Item.Properties().fireResistant());

						helper.register(Utils.getRegistryName(block), blockItem);

						if (group != SCItemGroup.MANUAL)
							SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(group).add(new ItemStack(blockItem));
					}
					else if (field.isAnnotationPresent(RegisterItemBlock.class)) {
						SCItemGroup group = field.getAnnotation(RegisterItemBlock.class).value();
						Block block = ((RegistryObject<Block>) field.get(null)).get();
						Item blockItem = new BlockItem(block, new Item.Properties());

						helper.register(Utils.getRegistryName(block), blockItem);

						if (group != SCItemGroup.MANUAL)
							SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(group).add(new ItemStack(blockItem));
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		});

		event.register(Keys.SOUND_EVENTS, helper -> {
			for (int i = 0; i < SCSounds.values().length; i++) {
				SCSounds sound = SCSounds.values()[i];

				helper.register(sound.location, sound.event);
			}
		});
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(SCContent.SENTRY_ENTITY.get(), Mob.createMobAttributes().build());
	}

	public static void registerPackets() {
		int id = 0;

		//client
		registerPacket(id++, BlockPocketManagerFailedActivation.class, BlockPocketManagerFailedActivation::encode, BlockPocketManagerFailedActivation::new, BlockPocketManagerFailedActivation::handle);
		registerPacket(id++, OpenScreen.class, OpenScreen::encode, OpenScreen::new, OpenScreen::handle);
		registerPacket(id++, PlayAlarmSound.class, PlayAlarmSound::encode, PlayAlarmSound::new, PlayAlarmSound::handle);
		registerPacket(id++, RefreshDisguisableModel.class, RefreshDisguisableModel::encode, RefreshDisguisableModel::new, RefreshDisguisableModel::handle);
		registerPacket(id++, SendTip.class, SendTip::encode, SendTip::new, SendTip::handle);
		registerPacket(id++, SetCameraView.class, SetCameraView::encode, SetCameraView::new, SetCameraView::handle);
		registerPacket(id++, SetTrophySystemTarget.class, SetTrophySystemTarget::encode, SetTrophySystemTarget::new, SetTrophySystemTarget::handle);
		registerPacket(id++, UpdateLaserColors.class, UpdateLaserColors::encode, UpdateLaserColors::new, UpdateLaserColors::handle);
		registerPacket(id++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::new, UpdateLogger::handle);
		//server
		registerPacket(id++, AssembleBlockPocket.class, AssembleBlockPocket::encode, AssembleBlockPocket::new, AssembleBlockPocket::handle);
		registerPacket(id++, CheckPasscode.class, CheckPasscode::encode, CheckPasscode::new, CheckPasscode::handle);
		registerPacket(id++, ClearChangeDetectorServer.class, ClearChangeDetectorServer::encode, ClearChangeDetectorServer::new, ClearChangeDetectorServer::handle);
		registerPacket(id++, ClearLoggerServer.class, ClearLoggerServer::encode, ClearLoggerServer::new, ClearLoggerServer::handle);
		registerPacket(id++, DismountCamera.class, DismountCamera::encode, DismountCamera::new, DismountCamera::handle);
		registerPacket(id++, MountCamera.class, MountCamera::encode, MountCamera::new, MountCamera::handle);
		registerPacket(id++, CheckBriefcasePasscode.class, CheckBriefcasePasscode::encode, CheckBriefcasePasscode::new, CheckBriefcasePasscode::handle);
		registerPacket(id++, RemoteControlMine.class, RemoteControlMine::encode, RemoteControlMine::new, RemoteControlMine::handle);
		registerPacket(id++, RemoveCameraTag.class, RemoveCameraTag::encode, RemoveCameraTag::new, RemoveCameraTag::handle);
		registerPacket(id++, RemoveMineFromMRAT.class, RemoveMineFromMRAT::encode, RemoveMineFromMRAT::new, RemoveMineFromMRAT::handle);
		registerPacket(id++, RemovePositionFromSSS.class, RemovePositionFromSSS::encode, RemovePositionFromSSS::new, RemovePositionFromSSS::handle);
		registerPacket(id++, RemoveSentryFromSRAT.class, RemoveSentryFromSRAT::encode, RemoveSentryFromSRAT::new, RemoveSentryFromSRAT::handle);
		registerPacket(id++, SyncAlarmSettings.class, SyncAlarmSettings::encode, SyncAlarmSettings::new, SyncAlarmSettings::handle);
		registerPacket(id++, SetBriefcasePasscodeAndOwner.class, SetBriefcasePasscodeAndOwner::encode, SetBriefcasePasscodeAndOwner::new, SetBriefcasePasscodeAndOwner::handle);
		registerPacket(id++, SetCameraPowered.class, SetCameraPowered::encode, SetCameraPowered::new, SetCameraPowered::handle);
		registerPacket(id++, SetGhostSlot.class, SetGhostSlot::encode, SetGhostSlot::new, SetGhostSlot::handle);
		registerPacket(id++, SetKeycardUses.class, SetKeycardUses::encode, SetKeycardUses::new, SetKeycardUses::handle);
		registerPacket(id++, SetListModuleData.class, SetListModuleData::encode, SetListModuleData::new, SetListModuleData::handle);
		registerPacket(id++, SetPasscode.class, SetPasscode::encode, SetPasscode::new, SetPasscode::handle);
		registerPacket(id++, SetSentryMode.class, SetSentryMode::encode, SetSentryMode::new, SetSentryMode::handle);
		registerPacket(id++, SetStateOnDisguiseModule.class, SetStateOnDisguiseModule::encode, SetStateOnDisguiseModule::new, SetStateOnDisguiseModule::handle);
		registerPacket(id++, SyncBlockChangeDetector.class, SyncBlockChangeDetector::encode, SyncBlockChangeDetector::new, SyncBlockChangeDetector::handle);
		registerPacket(id++, SyncBlockReinforcer.class, SyncBlockReinforcer::encode, SyncBlockReinforcer::new, SyncBlockReinforcer::handle);
		registerPacket(id++, SyncBlockPocketManager.class, SyncBlockPocketManager::encode, SyncBlockPocketManager::new, SyncBlockPocketManager::handle);
		registerPacket(id++, SyncKeycardSettings.class, SyncKeycardSettings::encode, SyncKeycardSettings::new, SyncKeycardSettings::handle);
		registerPacket(id++, SyncLaserSideConfig.class, SyncLaserSideConfig::encode, SyncLaserSideConfig::new, SyncLaserSideConfig::handle);
		registerPacket(id++, SyncProjector.class, SyncProjector::encode, SyncProjector::new, SyncProjector::handle);
		registerPacket(id++, SyncRiftStabilizer.class, SyncRiftStabilizer::encode, SyncRiftStabilizer::new, SyncRiftStabilizer::handle);
		registerPacket(id++, SyncSecureRedstoneInterface.class, SyncSecureRedstoneInterface::encode, SyncSecureRedstoneInterface::new, SyncSecureRedstoneInterface::handle);
		registerPacket(id++, SyncSSSSettingsOnServer.class, SyncSSSSettingsOnServer::encode, SyncSSSSettingsOnServer::new, SyncSSSSettingsOnServer::handle);
		registerPacket(id++, SyncTrophySystem.class, SyncTrophySystem::encode, SyncTrophySystem::new, SyncTrophySystem::handle);
		registerPacket(id++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::new, ToggleBlockPocketManager::handle);
		registerPacket(id++, ToggleModule.class, ToggleModule::encode, ToggleModule::new, ToggleModule::handle);
		registerPacket(id++, ToggleNightVision.class, ToggleNightVision::encode, ToggleNightVision::new, ToggleNightVision::handle);
		registerPacket(id++, ToggleOption.class, ToggleOption::encode, ToggleOption::new, ToggleOption::handle);
		registerPacket(id++, SetDefaultCameraViewingDirection.class, SetDefaultCameraViewingDirection::encode, SetDefaultCameraViewingDirection::new, SetDefaultCameraViewingDirection::handle);
		registerPacket(id++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::new, UpdateSliderValue::handle);
	}

	private static <MSG> void registerPacket(int id, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageHandler) {
		SecurityCraft.CHANNEL.messageBuilder(type, id).encoder(encoder).decoder(decoder).consumerMainThread(messageHandler).add();
	}

	@SubscribeEvent
	public static void onCreativeModeTabRegister(BuildCreativeModeTabContentsEvent event) {
		MutableHashedLinkedMap<ItemStack, TabVisibility> entries = event.getEntries();
		ResourceKey<CreativeModeTab> tabKey = event.getTabKey();

		if (tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {
			entries.putAfter(new ItemStack(Items.LEVER), new ItemStack(SCContent.REINFORCED_LEVER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.STONE_BUTTON), new ItemStack(SCContent.REINFORCED_OAK_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.REINFORCED_OAK_BUTTON.get()), new ItemStack(SCContent.REINFORCED_STONE_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.REINFORCED_STONE_BUTTON.get()), new ItemStack(SCContent.PANIC_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.STONE_PRESSURE_PLATE), new ItemStack(SCContent.REINFORCED_OAK_PRESSURE_PLATE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.REINFORCED_OAK_PRESSURE_PLATE.get()), new ItemStack(SCContent.REINFORCED_STONE_PRESSURE_PLATE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.STICKY_PISTON), new ItemStack(SCContent.REINFORCED_PISTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.REINFORCED_PISTON.get()), new ItemStack(SCContent.REINFORCED_STICKY_PISTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.DROPPER), new ItemStack(SCContent.REINFORCED_DISPENSER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.REINFORCED_DISPENSER.get()), new ItemStack(SCContent.REINFORCED_DROPPER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.HOPPER), new ItemStack(SCContent.REINFORCED_HOPPER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.LECTERN), new ItemStack(SCContent.REINFORCED_LECTERN.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if (tabKey == CreativeModeTabs.COLORED_BLOCKS) {
			//@formatter:off
			event.acceptAll(List.of(
					new ItemStack(SCContent.REINFORCED_WHITE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_RED_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_LIME_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_PINK_WOOL.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_RED_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_LIME_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_PINK_CARPET.get()),
					new ItemStack(SCContent.REINFORCED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_RED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIME_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PINK_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_RED_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_LIME_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_PINK_CONCRETE.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_RED_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIME_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_PINK_GLAZED_TERRACOTTA.get()),
					new ItemStack(SCContent.REINFORCED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_TINTED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_RED_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_LIME_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_PINK_STAINED_GLASS.get()),
					new ItemStack(SCContent.REINFORCED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get()),
					new ItemStack(SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get())));
			//@formatter:on
		}
		else if (tabKey == SCCreativeModeTabs.DECORATION_TAB.getKey()) {
			entries.putAfter(new ItemStack(SCContent.REINFORCED_CHISELED_BOOKSHELF.get()), new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_SPRUCE_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_SPRUCE_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BIRCH_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_BIRCH_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_JUNGLE_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_JUNGLE_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_ACACIA_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_ACACIA_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_MANGROVE_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_MANGROVE_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CHERRY_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_CHERRY_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BAMBOO_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_BAMBOO_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CRIMSON_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_CRIMSON_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_WARPED_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(SCContent.SECRET_WARPED_SIGN_ITEM.get()), new ItemStack(SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if (tabKey == CreativeModeTabs.OP_BLOCKS) {
			entries.put(new ItemStack(SCContent.ADMIN_TOOL.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.put(new ItemStack(SCContent.CODEBREAKER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if (tabKey.equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
			entries.putAfter(new ItemStack(Items.OAK_CHEST_BOAT), new ItemStack(SCContent.OAK_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.SPRUCE_CHEST_BOAT), new ItemStack(SCContent.SPRUCE_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.BIRCH_CHEST_BOAT), new ItemStack(SCContent.BIRCH_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.JUNGLE_CHEST_BOAT), new ItemStack(SCContent.JUNGLE_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.ACACIA_CHEST_BOAT), new ItemStack(SCContent.ACACIA_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.DARK_OAK_CHEST_BOAT), new ItemStack(SCContent.DARK_OAK_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.MANGROVE_CHEST_BOAT), new ItemStack(SCContent.MANGROVE_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.CHERRY_CHEST_BOAT), new ItemStack(SCContent.CHERRY_SECURITY_SEA_BOAT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			entries.putAfter(new ItemStack(Items.BAMBOO_CHEST_RAFT), new ItemStack(SCContent.BAMBOO_SECURITY_SEA_RAFT.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}

	public static void registerFakeLiquidRecipes() {
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.WATER_BUCKET), getPotionIngredient(Potions.HARMING, Potions.STRONG_HARMING), new ItemStack(SCContent.FAKE_WATER_BUCKET.get()));
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.LAVA_BUCKET), getPotionIngredient(Potions.HEALING, Potions.STRONG_HEALING), new ItemStack(SCContent.FAKE_LAVA_BUCKET.get()));
	}

	private static Ingredient getPotionIngredient(Potion normalPotion, Potion strongPotion) {
		CompoundTag normalNBT = new CompoundTag();
		CompoundTag strongNBT = new CompoundTag();
		PartialNBTIngredient normalPotions;
		PartialNBTIngredient strongPotions;

		normalNBT.putString("Potion", Utils.getRegistryName(normalPotion).toString());
		strongNBT.putString("Potion", Utils.getRegistryName(strongPotion).toString());
		normalPotions = PartialNBTIngredient.of(normalNBT, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		strongPotions = PartialNBTIngredient.of(strongNBT, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		return CompoundIngredient.of(normalPotions, strongPotions);
	}
}
