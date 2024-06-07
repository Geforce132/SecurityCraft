package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.commands.LowercasedEnumArgument;
import net.geforcemods.securitycraft.commands.SingleGameProfileArgument;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.PartialNBTIngredient;
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
import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
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
import net.minecraft.block.Block;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.registries.DataSerializerEntry;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class RegistrationHandler {
	private RegistrationHandler() {}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		//register item blocks from annotated fields
		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).registerBlockItem()) {
					Block block = ((RegistryObject<Block>) field.get(null)).get();

					event.getRegistry().register(new BlockItem(block, new Item.Properties().tab(SecurityCraft.DECORATION_TAB).fireResistant()).setRegistryName(block.getRegistryName()));
				}
				else if (field.isAnnotationPresent(RegisterItemBlock.class)) {
					int tab = field.getAnnotation(RegisterItemBlock.class).value().ordinal();
					RegistryObject<Block> block = (RegistryObject<Block>) field.get(null);
					ItemGroup tabToUse = SecurityCraft.DECORATION_TAB;

					if (tab == 0)
						tabToUse = SecurityCraft.TECHNICAL_TAB;
					else if (tab == 1)
						tabToUse = SecurityCraft.MINE_TAB;

					event.getRegistry().register(new BlockItem(block.get(), new Item.Properties().tab(tabToUse)).setRegistryName(block.get().getRegistryName()));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(SCContent.SENTRY_ENTITY.get(), MobEntity.createMobAttributes().build());
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
		CraftingHelper.register(new ResourceLocation(SecurityCraft.MODID, "partial_nbt"), PartialNBTIngredient.Serializer.INSTANCE);
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

	public static void registerPackets() {
		int id = 0;

		SecurityCraft.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID), SecurityCraft::getVersion, SecurityCraft.getVersion()::equals, SecurityCraft.getVersion()::equals);
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
		registerPacket(id++, UpdateNBTTagOnClient.class, UpdateNBTTagOnClient::encode, UpdateNBTTagOnClient::new, UpdateNBTTagOnClient::handle);
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

	private static <MSG> void registerPacket(int id, Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageHandler) {
		SecurityCraft.channel.messageBuilder(type, id).encoder(encoder).decoder(decoder).consumer((msg, context) -> {
			NetworkEvent.Context ctx = context.get();

			ctx.enqueueWork(() -> messageHandler.accept(msg, context));
			ctx.setPacketHandled(true);
		}).add();
	}

	public static void registerFakeLiquidRecipes() {
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.WATER_BUCKET), getPotionIngredient(Potions.HARMING, Potions.STRONG_HARMING), new ItemStack(SCContent.FAKE_WATER_BUCKET.get()));
		BrewingRecipeRegistry.addRecipe(Ingredient.of(Items.LAVA_BUCKET), getPotionIngredient(Potions.HEALING, Potions.STRONG_HEALING), new ItemStack(SCContent.FAKE_LAVA_BUCKET.get()));
	}

	private static Ingredient getPotionIngredient(Potion normalPotion, Potion strongPotion) {
		CompoundNBT normalNBT = new CompoundNBT();
		CompoundNBT strongNBT = new CompoundNBT();
		PartialNBTIngredient normalPotions;
		PartialNBTIngredient strongPotions;

		normalNBT.putString("Potion", normalPotion.getRegistryName().toString());
		strongNBT.putString("Potion", strongPotion.getRegistryName().toString());
		normalPotions = PartialNBTIngredient.of(normalNBT, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		strongPotions = PartialNBTIngredient.of(strongNBT, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		return PublicCompoundIngredient.of(normalPotions, strongPotions);
	}

	@SuppressWarnings("rawtypes")
	public static void registerArgumentTypes() {
		ArgumentTypes.register(SecurityCraft.MODID + ":single_game_profile", SingleGameProfileArgument.class, new ArgumentSerializer<>(SingleGameProfileArgument::singleGameProfile));
		ArgumentTypes.register(SecurityCraft.MODID + ":lowercased_enum", LowercasedEnumArgument.class, (IArgumentSerializer) new LowercasedEnumArgument.Serializer());
	}

	public static class PublicCompoundIngredient extends CompoundIngredient { //Constructor of CompoundIngredient is protected, so this surrogate class is needed
		public PublicCompoundIngredient(List<Ingredient> children) {
			super(children);
		}

		public static PublicCompoundIngredient of(Ingredient... ingredients) {
			return new PublicCompoundIngredient(Arrays.asList(ingredients));
		}
	}
}
