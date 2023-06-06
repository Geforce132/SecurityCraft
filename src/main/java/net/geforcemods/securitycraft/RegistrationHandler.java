package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.network.client.OpenLaserScreen;
import net.geforcemods.securitycraft.network.client.OpenSRATScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.PlayAlarmSound;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.CheckPasscode;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.RemoveMineFromMRAT;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncAlarmSettings;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncLaserSideConfig;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.event.CreativeModeTabEvent;
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
	public static final Map<SCItemGroup, List<ItemStack>> STACKS_FOR_ITEM_GROUPS = Util.make(new EnumMap<>(SCItemGroup.class), map -> Arrays.stream(SCItemGroup.values()).forEach(key -> map.put(key, new ArrayList<ItemStack>())));

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
							STACKS_FOR_ITEM_GROUPS.get(group).add(new ItemStack(blockItem));
					}
					else if (field.isAnnotationPresent(RegisterItemBlock.class)) {
						SCItemGroup group = field.getAnnotation(RegisterItemBlock.class).value();
						Block block = ((RegistryObject<Block>) field.get(null)).get();
						Item blockItem = new BlockItem(block, new Item.Properties());

						helper.register(Utils.getRegistryName(block), blockItem);

						if (group != SCItemGroup.MANUAL)
							STACKS_FOR_ITEM_GROUPS.get(group).add(new ItemStack(blockItem));
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
		registerPacket(id++, InitSentryAnimation.class, InitSentryAnimation::encode, InitSentryAnimation::new, InitSentryAnimation::handle);
		registerPacket(id++, OpenScreen.class, OpenScreen::encode, OpenScreen::new, OpenScreen::handle);
		registerPacket(id++, OpenLaserScreen.class, OpenLaserScreen::encode, OpenLaserScreen::new, OpenLaserScreen::handle);
		registerPacket(id++, OpenSRATScreen.class, OpenSRATScreen::encode, OpenSRATScreen::new, OpenSRATScreen::handle);
		registerPacket(id++, PlayAlarmSound.class, PlayAlarmSound::encode, PlayAlarmSound::new, PlayAlarmSound::handle);
		registerPacket(id++, RefreshDisguisableModel.class, RefreshDisguisableModel::encode, RefreshDisguisableModel::new, RefreshDisguisableModel::handle);
		registerPacket(id++, SendTip.class, SendTip::encode, SendTip::new, SendTip::handle);
		registerPacket(id++, SetCameraView.class, SetCameraView::encode, SetCameraView::new, SetCameraView::handle);
		registerPacket(id++, SetTrophySystemTarget.class, SetTrophySystemTarget::encode, SetTrophySystemTarget::new, SetTrophySystemTarget::handle);
		registerPacket(id++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::new, UpdateLogger::handle);
		registerPacket(id++, UpdateNBTTagOnClient.class, UpdateNBTTagOnClient::encode, UpdateNBTTagOnClient::new, UpdateNBTTagOnClient::handle);
		//server
		registerPacket(id++, AssembleBlockPocket.class, AssembleBlockPocket::encode, AssembleBlockPocket::new, AssembleBlockPocket::handle);
		registerPacket(id++, CheckPasscode.class, CheckPasscode::encode, CheckPasscode::new, CheckPasscode::handle);
		registerPacket(id++, ClearChangeDetectorServer.class, ClearChangeDetectorServer::encode, ClearChangeDetectorServer::new, ClearChangeDetectorServer::handle);
		registerPacket(id++, ClearLoggerServer.class, ClearLoggerServer::encode, ClearLoggerServer::new, ClearLoggerServer::handle);
		registerPacket(id++, DismountCamera.class, DismountCamera::encode, DismountCamera::new, DismountCamera::handle);
		registerPacket(id++, GiveNightVision.class, GiveNightVision::encode, GiveNightVision::new, GiveNightVision::handle);
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
		registerPacket(id++, SyncBlockPocketManager.class, SyncBlockPocketManager::encode, SyncBlockPocketManager::new, SyncBlockPocketManager::handle);
		registerPacket(id++, SyncIMSTargetingOption.class, SyncIMSTargetingOption::encode, SyncIMSTargetingOption::new, SyncIMSTargetingOption::handle);
		registerPacket(id++, SyncKeycardSettings.class, SyncKeycardSettings::encode, SyncKeycardSettings::new, SyncKeycardSettings::handle);
		registerPacket(id++, SyncLaserSideConfig.class, SyncLaserSideConfig::encode, SyncLaserSideConfig::new, SyncLaserSideConfig::handle);
		registerPacket(id++, SyncProjector.class, SyncProjector::encode, SyncProjector::new, SyncProjector::handle);
		registerPacket(id++, SyncRiftStabilizer.class, SyncRiftStabilizer::encode, SyncRiftStabilizer::new, SyncRiftStabilizer::handle);
		registerPacket(id++, SyncSSSSettingsOnServer.class, SyncSSSSettingsOnServer::encode, SyncSSSSettingsOnServer::new, SyncSSSSettingsOnServer::handle);
		registerPacket(id++, SyncTrophySystem.class, SyncTrophySystem::encode, SyncTrophySystem::new, SyncTrophySystem::handle);
		registerPacket(id++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::new, ToggleBlockPocketManager::handle);
		registerPacket(id++, ToggleModule.class, ToggleModule::encode, ToggleModule::new, ToggleModule::handle);
		registerPacket(id++, ToggleOption.class, ToggleOption::encode, ToggleOption::new, ToggleOption::handle);
		registerPacket(id++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::new, UpdateSliderValue::handle);
	}

	private static <MSG> void registerPacket(int id, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageHandler) {
		SecurityCraft.channel.messageBuilder(type, id).encoder(encoder).decoder(decoder).consumerMainThread(messageHandler).add();
	}

	@SubscribeEvent
	public static void onCreativeModeTabRegister(CreativeModeTabEvent.Register event) {
		//@formatter:off
		SecurityCraft.technicalTab = event.registerCreativeModeTab(new ResourceLocation(SecurityCraft.MODID, "technical"), builder -> builder
				.icon(() -> new ItemStack(SCContent.USERNAME_LOGGER.get()))
				.title(Component.translatable("itemGroup.securitycraft.technical"))
				.displayItems((itemDisplayParameters, output) -> {
					output.acceptAll(List.of(
							new ItemStack(SCContent.SC_MANUAL.get()),
							new ItemStack(SCContent.FRAME.get()),
							new ItemStack(SCContent.KEY_PANEL.get()),
							new ItemStack(SCContent.KEYPAD.get()),
							new ItemStack(SCContent.KEYPAD_CHEST.get()),
							new ItemStack(SCContent.KEYPAD_BARREL.get()),
							new ItemStack(SCContent.KEYPAD_FURNACE.get()),
							new ItemStack(SCContent.KEYPAD_SMOKER.get()),
							new ItemStack(SCContent.KEYPAD_BLAST_FURNACE.get()),
							new ItemStack(SCContent.DISPLAY_CASE.get()),
							new ItemStack(SCContent.GLOW_DISPLAY_CASE.get()),
							new ItemStack(SCContent.KEYCARD_READER.get()),
							new ItemStack(SCContent.KEYCARD_LVL_1.get()),
							new ItemStack(SCContent.KEYCARD_LVL_2.get()),
							new ItemStack(SCContent.KEYCARD_LVL_3.get()),
							new ItemStack(SCContent.KEYCARD_LVL_4.get()),
							new ItemStack(SCContent.KEYCARD_LVL_5.get()),
							new ItemStack(SCContent.KEYCARD_HOLDER.get()),
							new ItemStack(SCContent.LIMITED_USE_KEYCARD.get()),
							new ItemStack(SCContent.CODEBREAKER.get()),
							new ItemStack(SCContent.UNIVERSAL_KEY_CHANGER.get()),
							new ItemStack(SCContent.RETINAL_SCANNER.get()),
							new ItemStack(SCContent.LASER_BLOCK.get()),
							new ItemStack(SCContent.INVENTORY_SCANNER.get()),
							new ItemStack(SCContent.USERNAME_LOGGER.get()),
							new ItemStack(SCContent.PORTABLE_RADAR.get()),
							new ItemStack(SCContent.TROPHY_SYSTEM.get()),
							new ItemStack(SCContent.RIFT_STABILIZER.get()),
							new ItemStack(SCContent.BLOCK_CHANGE_DETECTOR.get()),
							new ItemStack(SCContent.PROJECTOR.get()),
							new ItemStack(SCContent.PROTECTO.get()),
							new ItemStack(SCContent.MOTION_ACTIVATED_LIGHT.get()),
							new ItemStack(SCContent.SECURITY_CAMERA.get()),
							new ItemStack(SCContent.CAMERA_MONITOR.get()),
							new ItemStack(SCContent.ALARM.get()),
							new ItemStack(SCContent.PANIC_BUTTON.get()),
							new ItemStack(SCContent.SENTRY.get()),
							new ItemStack(SCContent.REMOTE_ACCESS_SENTRY.get()),
							new ItemStack(SCContent.REMOTE_ACCESS_MINE.get()),
							new ItemStack(SCContent.CAGE_TRAP.get()),
							new ItemStack(SCContent.WIRE_CUTTERS.get()),
							new ItemStack(SCContent.IRON_FENCE.get()),
							new ItemStack(SCContent.REINFORCED_FENCE_GATE.get()),
							new ItemStack(SCContent.REINFORCED_IRON_TRAPDOOR.get()),
							new ItemStack(SCContent.KEYPAD_TRAPDOOR.get()),
							new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()),
							new ItemStack(SCContent.SCANNER_DOOR_ITEM.get()),
							new ItemStack(SCContent.KEYPAD_DOOR_ITEM.get()),
							new ItemStack(SCContent.BLOCK_POCKET_MANAGER.get()),
							new ItemStack(SCContent.BLOCK_POCKET_WALL.get()),
							new ItemStack(SCContent.SONIC_SECURITY_SYSTEM.get()),
							new ItemStack(SCContent.PORTABLE_TUNE_PLAYER.get()),
							new ItemStack(SCContent.REINFORCED_PISTON.get()),
							new ItemStack(SCContent.REINFORCED_STICKY_PISTON.get()),
							new ItemStack(SCContent.REINFORCED_OBSERVER.get()),
							new ItemStack(SCContent.REINFORCED_CAULDRON.get()),
							new ItemStack(SCContent.REINFORCED_HOPPER.get()),
							new ItemStack(SCContent.ALLOWLIST_MODULE.get()),
							new ItemStack(SCContent.DENYLIST_MODULE.get()),
							new ItemStack(SCContent.DISGUISE_MODULE.get()),
							new ItemStack(SCContent.REDSTONE_MODULE.get()),
							new ItemStack(SCContent.SPEED_MODULE.get()),
							new ItemStack(SCContent.SMART_MODULE.get()),
							new ItemStack(SCContent.STORAGE_MODULE.get()),
							new ItemStack(SCContent.HARMING_MODULE.get()),
							new ItemStack(SCContent.UNIVERSAL_BLOCK_MODIFIER.get()),
							new ItemStack(SCContent.UNIVERSAL_OWNER_CHANGER.get()),
							new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()),
							new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get()),
							new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()),
							new ItemStack(SCContent.UNIVERSAL_BLOCK_REMOVER.get()),
							new ItemStack(SCContent.TASER.get()),
							new ItemStack(SCContent.BRIEFCASE.get()),
							new ItemStack(SCContent.FAKE_WATER_BUCKET.get()),
							new ItemStack(SCContent.FAKE_LAVA_BUCKET.get()),
							new ItemStack(SCContent.ADMIN_TOOL.get())));
					output.acceptAll(STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.TECHNICAL));
				}));
		SecurityCraft.mineTab = event.registerCreativeModeTab(new ResourceLocation(SecurityCraft.MODID, "explosives"), List.of(), List.of(SecurityCraft.technicalTab), builder -> builder
				.icon(() -> new ItemStack(SCContent.MINE.get()))
				.title(Component.translatable("itemGroup.securitycraft.explosives"))
				.displayItems((itemDisplayParameters, output) -> {
					//@formatter:on
					List<Item> vanillaOrderedItems = getVanillaOrderedItems();
					List<ItemStack> mineGroupItems = STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES);

					mineGroupItems.sort((a, b) -> {
						//if a isn't an item that has a vanilla counterpart, it should appear at the front
						if (!(a.getItem() instanceof BlockItem blockItemA && blockItemA.getBlock() instanceof BaseFullMineBlock blockMineA))
							return -1;

						//same for b
						if (!(b.getItem() instanceof BlockItem blockItemB && blockItemB.getBlock() instanceof BaseFullMineBlock blockMineB))
							return 1;

						return Integer.compare(vanillaOrderedItems.indexOf(blockMineA.getBlockDisguisedAs().asItem()), vanillaOrderedItems.indexOf(blockMineB.getBlockDisguisedAs().asItem()));
					});
					output.accept(SCContent.REMOTE_ACCESS_MINE.get());
					output.accept(SCContent.WIRE_CUTTERS.get());
					output.accept(Items.FLINT_AND_STEEL);
					output.accept(SCContent.MINE.get());
					output.acceptAll(mineGroupItems);
					output.acceptAll(List.of( //@formatter:off
							new ItemStack(SCContent.ANCIENT_DEBRIS_MINE_ITEM.get()),
							new ItemStack(SCContent.FURNACE_MINE.get()),
							new ItemStack(SCContent.SMOKER_MINE.get()),
							new ItemStack(SCContent.BLAST_FURNACE_MINE.get())));
				}));
		SecurityCraft.decorationTab = event.registerCreativeModeTab(new ResourceLocation(SecurityCraft.MODID, "decoration"), List.of(), List.of(SecurityCraft.mineTab), builder -> builder
				.icon(() -> new ItemStack(SCContent.REINFORCED_OAK_STAIRS.get()))
				.title(Component.translatable("itemGroup.securitycraft.decoration"))
				.displayItems((itemDisplayParameters, output) -> {
					//@formatter:on
					List<Item> vanillaOrderedItems = getVanillaOrderedItems();
					List<ItemStack> decorationGroupItems = STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION);

					decorationGroupItems.sort((a, b) -> {
						//if a isn't an item that has a vanilla counterpart, it should appear at the back
						if (!(a.getItem() instanceof BlockItem blockItemA && blockItemA.getBlock() instanceof IReinforcedBlock reinforcedBlockA))
							return 1;

						//same for b
						if (!(b.getItem() instanceof BlockItem blockItemB && blockItemB.getBlock() instanceof IReinforcedBlock reinforcedBlockB))
							return -1;

						int indexA = vanillaOrderedItems.indexOf(reinforcedBlockA.getVanillaBlock().asItem());

						//items that have no counterpart in any of the above vanilla tabs should appear at the end
						if (indexA == -1)
							return 1;

						int indexB = vanillaOrderedItems.indexOf(reinforcedBlockB.getVanillaBlock().asItem());

						//same here
						if (indexB == -1)
							return -1;

						return Integer.compare(indexA, indexB);
					});

					//loop starts from the back, because the reinforced bookshelf is expected to be towards the end of the list
					//can't use indexOf, because ItemStack does not implement Object#equals
					for (int i = decorationGroupItems.size() - 1; i >= 0; i--) {
						//sort secret signs after the reinforced bookshelf
						if (decorationGroupItems.get(i).getItem() == SCContent.REINFORCED_BOOKSHELF.get().asItem()) {
							decorationGroupItems.addAll(i + 1, List.of( //@formatter:off
									new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_SPRUCE_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_BIRCH_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_JUNGLE_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_ACACIA_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_MANGROVE_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_CHERRY_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_BAMBOO_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_CRIMSON_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_WARPED_SIGN_ITEM.get()),
									new ItemStack(SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.get())));
							//@formatter:on
							break;
						}
					}

					decorationGroupItems.addAll(List.of( //@formatter:off
							new ItemStack(SCContent.CRYSTAL_QUARTZ_BLOCK.get()),
							new ItemStack(SCContent.CRYSTAL_QUARTZ_STAIRS.get()),
							new ItemStack(SCContent.CRYSTAL_QUARTZ_SLAB.get()),
							new ItemStack(SCContent.CHISELED_CRYSTAL_QUARTZ.get()),
							new ItemStack(SCContent.CRYSTAL_QUARTZ_BRICKS.get()),
							new ItemStack(SCContent.CRYSTAL_QUARTZ_PILLAR.get()),
							new ItemStack(SCContent.SMOOTH_CRYSTAL_QUARTZ.get()),
							new ItemStack(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get()),
							new ItemStack(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get()),
							new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get()),
							new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get()),
							new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get()),
							new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()),
							new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS.get()),
							new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()),
							new ItemStack(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get()),
							new ItemStack(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get()),
							new ItemStack(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get()),
							new ItemStack(SCContent.BLOCK_POCKET_WALL.get()),
							new ItemStack(SCContent.IRON_FENCE.get()),
							new ItemStack(SCContent.REINFORCED_FENCE_GATE.get()),
							new ItemStack(SCContent.REINFORCED_IRON_TRAPDOOR.get()),
							new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()),
							new ItemStack(SCContent.SCANNER_DOOR_ITEM.get()),
							new ItemStack(SCContent.KEYPAD_DOOR_ITEM.get()),
							new ItemStack(SCContent.DISPLAY_CASE.get()),
							new ItemStack(SCContent.GLOW_DISPLAY_CASE.get())));
					//@formatter:on
					output.accept(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
					output.accept(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get());
					output.accept(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get());
					output.accept(SCContent.UNIVERSAL_BLOCK_REMOVER.get());
					output.acceptAll(decorationGroupItems);
				}));
	}

	@SubscribeEvent
	public static void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
		if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
			event.getEntries().putAfter(new ItemStack(Items.LEVER), new ItemStack(SCContent.REINFORCED_LEVER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(Items.STONE_BUTTON), new ItemStack(SCContent.REINFORCED_OAK_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(SCContent.REINFORCED_OAK_BUTTON.get()), new ItemStack(SCContent.REINFORCED_STONE_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(SCContent.REINFORCED_STONE_BUTTON.get()), new ItemStack(SCContent.PANIC_BUTTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(Items.STONE_PRESSURE_PLATE), new ItemStack(SCContent.REINFORCED_OAK_PRESSURE_PLATE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(SCContent.REINFORCED_OAK_PRESSURE_PLATE.get()), new ItemStack(SCContent.REINFORCED_STONE_PRESSURE_PLATE.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(Items.STICKY_PISTON), new ItemStack(SCContent.REINFORCED_PISTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(SCContent.REINFORCED_PISTON.get()), new ItemStack(SCContent.REINFORCED_STICKY_PISTON.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putAfter(new ItemStack(Items.HOPPER), new ItemStack(SCContent.REINFORCED_HOPPER.get()), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if (event.getTab() == CreativeModeTabs.COLORED_BLOCKS) {
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
	}

	public static void registerFakeLiquidRecipes() {
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.WATER_BUCKET), getPotionIngredient(Potions.HARMING, Potions.STRONG_HARMING), new ItemStack(SCContent.FAKE_WATER_BUCKET.get()));
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.LAVA_BUCKET), getPotionIngredient(Potions.HEALING, Potions.STRONG_HEALING), new ItemStack(SCContent.FAKE_LAVA_BUCKET.get()));
	}

	private static Ingredient getPotionIngredient(Potion normalPotion, Potion strongPotion) {
		ItemStack normalPotionStack = new ItemStack(Items.POTION);
		ItemStack strongPotionStack = new ItemStack(Items.POTION);
		ItemStack normalSplashPotionStack = new ItemStack(Items.SPLASH_POTION);
		ItemStack strongSplashPotionStack = new ItemStack(Items.SPLASH_POTION);
		ItemStack normalLingeringPotionStack = new ItemStack(Items.LINGERING_POTION);
		ItemStack strongLingeringPotionStack = new ItemStack(Items.LINGERING_POTION);
		CompoundTag normalNBT = new CompoundTag();
		CompoundTag strongNBT = new CompoundTag();

		normalNBT.putString("Potion", Utils.getRegistryName(normalPotion).toString());
		strongNBT.putString("Potion", Utils.getRegistryName(strongPotion).toString());
		normalPotionStack.setTag(normalNBT.copy());
		strongPotionStack.setTag(strongNBT.copy());
		normalSplashPotionStack.setTag(normalNBT.copy());
		strongSplashPotionStack.setTag(strongNBT.copy());
		normalLingeringPotionStack.setTag(normalNBT.copy());
		strongLingeringPotionStack.setTag(strongNBT.copy());

		return Ingredient.of(normalPotionStack, strongPotionStack, normalSplashPotionStack, strongSplashPotionStack, normalLingeringPotionStack, strongLingeringPotionStack);
	}

	private static List<Item> getVanillaOrderedItems() {
		List<Item> vanillaOrderedItems = new ArrayList<>();

		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.BUILDING_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.COLORED_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.NATURAL_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.FUNCTIONAL_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.REDSTONE_BLOCKS));
		return vanillaOrderedItems;
	}

	private static List<Item> getCreativeTabItems(CreativeModeTab tab) {
		return tab.getDisplayItems().stream().map(ItemStack::getItem).toList();
	}
}
