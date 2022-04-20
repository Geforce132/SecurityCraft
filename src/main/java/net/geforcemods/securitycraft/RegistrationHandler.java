package net.geforcemods.securitycraft;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.network.client.OpenSRATScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.client.SetTrophySystemTarget;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseInventory;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.registries.DataSerializerEntry;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class RegistrationHandler {
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		//register item blocks from annotated fields
		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).registerBlockItem()) {
					Block block = ((RegistryObject<Block>) field.get(null)).get();

					event.getRegistry().register(new BlockItem(block, new Item.Properties().tab(SecurityCraft.groupSCDecoration).fireResistant()).setRegistryName(block.getRegistryName()));
				}
				else if (field.isAnnotationPresent(RegisterItemBlock.class)) {
					int tab = field.getAnnotation(RegisterItemBlock.class).value().ordinal();
					RegistryObject<Block> block = (RegistryObject<Block>) field.get(null);

					event.getRegistry().register(new BlockItem(block.get(), new Item.Properties().tab(tab == 0 ? SecurityCraft.groupSCTechnical : (tab == 1 ? SecurityCraft.groupSCMine : SecurityCraft.groupSCDecoration))).setRegistryName(block.get().getRegistryName()));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(SCContent.eTypeSentry.get(), MobEntity.createMobAttributes().build());
	}

	public static void registerPackets() {
		int index = 0;

		SecurityCraft.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID), SecurityCraft::getVersion, SecurityCraft.getVersion()::equals, SecurityCraft.getVersion()::equals);
		//client
		SecurityCraft.channel.registerMessage(index++, InitSentryAnimation.class, InitSentryAnimation::encode, InitSentryAnimation::decode, InitSentryAnimation::onMessage);
		SecurityCraft.channel.registerMessage(index++, OpenScreen.class, OpenScreen::encode, OpenScreen::decode, OpenScreen::onMessage);
		SecurityCraft.channel.registerMessage(index++, OpenSRATScreen.class, OpenSRATScreen::encode, OpenSRATScreen::decode, OpenSRATScreen::onMessage);
		SecurityCraft.channel.registerMessage(index++, RefreshDisguisableModel.class, RefreshDisguisableModel::encode, RefreshDisguisableModel::decode, RefreshDisguisableModel::onMessage);
		SecurityCraft.channel.registerMessage(index++, SendTip.class, SendTip::encode, SendTip::decode, SendTip::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetCameraView.class, SetCameraView::encode, SetCameraView::decode, SetCameraView::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetTrophySystemTarget.class, SetTrophySystemTarget::encode, SetTrophySystemTarget::decode, SetTrophySystemTarget::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::decode, UpdateLogger::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnClient.class, UpdateNBTTagOnClient::encode, UpdateNBTTagOnClient::decode, UpdateNBTTagOnClient::onMessage);
		//server
		SecurityCraft.channel.registerMessage(index++, AssembleBlockPocket.class, AssembleBlockPocket::encode, AssembleBlockPocket::decode, AssembleBlockPocket::onMessage);
		SecurityCraft.channel.registerMessage(index++, CheckPassword.class, CheckPassword::encode, CheckPassword::decode, CheckPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, ClearChangeDetectorServer.class, ClearChangeDetectorServer::encode, ClearChangeDetectorServer::decode, ClearChangeDetectorServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, ClearLoggerServer.class, ClearLoggerServer::encode, ClearLoggerServer::decode, ClearLoggerServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, DismountCamera.class, DismountCamera::encode, DismountCamera::decode, DismountCamera::onMessage);
		SecurityCraft.channel.registerMessage(index++, GiveNightVision.class, GiveNightVision::encode, GiveNightVision::decode, GiveNightVision::onMessage);
		SecurityCraft.channel.registerMessage(index++, MountCamera.class, MountCamera::encode, MountCamera::decode, MountCamera::onMessage);
		SecurityCraft.channel.registerMessage(index++, OpenBriefcaseInventory.class, OpenBriefcaseInventory::encode, OpenBriefcaseInventory::decode, OpenBriefcaseInventory::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemoteControlMine.class, RemoteControlMine::encode, RemoteControlMine::decode, RemoteControlMine::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemoveCameraTag.class, RemoveCameraTag::encode, RemoveCameraTag::decode, RemoveCameraTag::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetCameraPowered.class, SetCameraPowered::encode, SetCameraPowered::decode, SetCameraPowered::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetKeycardUses.class, SetKeycardUses::encode, SetKeycardUses::decode, SetKeycardUses::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetPassword.class, SetPassword::encode, SetPassword::decode, SetPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetSentryMode.class, SetSentryMode::encode, SetSentryMode::decode, SetSentryMode::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncBlockChangeDetector.class, SyncBlockChangeDetector::encode, SyncBlockChangeDetector::decode, SyncBlockChangeDetector::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncBlockPocketManager.class, SyncBlockPocketManager::encode, SyncBlockPocketManager::decode, SyncBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncIMSTargetingOption.class, SyncIMSTargetingOption::encode, SyncIMSTargetingOption::decode, SyncIMSTargetingOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncKeycardSettings.class, SyncKeycardSettings::encode, SyncKeycardSettings::decode, SyncKeycardSettings::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncProjector.class, SyncProjector::encode, SyncProjector::decode, SyncProjector::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncSSSSettingsOnServer.class, SyncSSSSettingsOnServer::encode, SyncSSSSettingsOnServer::decode, SyncSSSSettingsOnServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncTrophySystem.class, SyncTrophySystem::encode, SyncTrophySystem::decode, SyncTrophySystem::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::decode, ToggleBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleOption.class, ToggleOption::encode, ToggleOption::decode, ToggleOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnServer.class, UpdateNBTTagOnServer::encode, UpdateNBTTagOnServer::decode, UpdateNBTTagOnServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::decode, UpdateSliderValue::onMessage);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		for (int i = 0; i < SCSounds.values().length; i++) {
			event.getRegistry().register(SCSounds.values()[i].event);
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry().register(new SpecialRecipeSerializer<>(LimitedUseKeycardRecipe::new).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_recipe")));
	}

	@SubscribeEvent
	public static void registerDataSerializerEntries(RegistryEvent.Register<DataSerializerEntry> event) {
		event.getRegistry().register(new DataSerializerEntry(new IDataSerializer<Owner>() {
			@Override
			public void write(PacketBuffer buf, Owner value) {
				buf.writeUtf(value.getName());
				buf.writeUtf(value.getUUID());
			}

			@Override
			public Owner read(PacketBuffer buf) {
				String name = buf.readUtf(Integer.MAX_VALUE / 4);
				String uuid = buf.readUtf(Integer.MAX_VALUE / 4);

				return new Owner(name, uuid);
			}

			@Override
			public DataParameter<Owner> createAccessor(int id) {
				return new DataParameter<>(id, this);
			}

			@Override
			public Owner copy(Owner value) {
				return new Owner(value.getName(), value.getUUID());
			}
		}).setRegistryName(new ResourceLocation(SecurityCraft.MODID, "owner")));
	}
}
