package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.misc.LimitedUseKeycardRecipe;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.misc.conditions.BlockEntityNBTCondition;
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
import net.geforcemods.securitycraft.network.server.RemoveMineFromMRAT;
import net.geforcemods.securitycraft.network.server.RemovePositionFromSSS;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetBriefcaseOwner;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.network.server.SetKeycardUses;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.network.server.SyncBlockChangeDetector;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.network.server.SyncIMSTargetingOption;
import net.geforcemods.securitycraft.network.server.SyncKeycardSettings;
import net.geforcemods.securitycraft.network.server.SyncProjector;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.network.server.SyncSSSSettingsOnServer;
import net.geforcemods.securitycraft.network.server.SyncTrophySystem;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class RegistrationHandler {
	private static final Map<SCItemGroup, List<ItemStack>> STACKS_FOR_ITEM_GROUPS = Util.make(new EnumMap<>(SCItemGroup.class), map -> Arrays.stream(SCItemGroup.values()).forEach(key -> map.put(key, new ArrayList<ItemStack>())));

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		event.register(Keys.ITEMS, helper -> {
			//register item blocks from annotated fields
			for (Field field : SCContent.class.getFields()) {
				try {
					if (field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).registerBlockItem()) {
						Block block = ((RegistryObject<Block>) field.get(null)).get();
						Item blockItem = new BlockItem(block, new Item.Properties().fireResistant());

						helper.register(Utils.getRegistryName(block), blockItem);
						STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION).add(new ItemStack(blockItem));
					}
					else if (field.isAnnotationPresent(RegisterItemBlock.class)) {
						Block block = ((RegistryObject<Block>) field.get(null)).get();
						Item blockItem = new BlockItem(block, new Item.Properties());

						helper.register(Utils.getRegistryName(block), blockItem);
						STACKS_FOR_ITEM_GROUPS.get(field.getAnnotation(RegisterItemBlock.class).value()).add(new ItemStack(blockItem));
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
		event.register(Registries.LOOT_CONDITION_TYPE, helper -> helper.register(new ResourceLocation(SecurityCraft.MODID, "tile_entity_nbt"), new LootItemConditionType(new BlockEntityNBTCondition.ConditionSerializer())));
		event.register(Registries.RECIPE_SERIALIZER, helper -> helper.register(new ResourceLocation(SecurityCraft.MODID, "limited_use_keycard_recipe"), new SimpleCraftingRecipeSerializer<>(LimitedUseKeycardRecipe::new)));
	}

	@SubscribeEvent
	public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(SCContent.SENTRY_ENTITY.get(), Mob.createMobAttributes().build());
	}

	@SubscribeEvent
	public static void onCreativeModeTabRegister(CreativeModeTabEvent.Register event) {
		//@formatter:off
		SecurityCraft.technicalTab = event.registerCreativeModeTab(new ResourceLocation(SecurityCraft.MODID, "technical"), builder -> builder
				.icon(() -> new ItemStack(SCContent.USERNAME_LOGGER.get()))
				.title(Component.translatable("itemGroup.securitycraft.technical"))
				.displayItems((features, output, hasPermissions) -> STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.TECHNICAL).forEach(output::accept)));
		SecurityCraft.mineTab = event.registerCreativeModeTab(new ResourceLocation(SecurityCraft.MODID, "explosives"), List.of(), List.of(SecurityCraft.technicalTab), builder -> builder
				.icon(() -> new ItemStack(SCContent.MINE.get()))
				.title(Component.translatable("itemGroup.securitycraft.explosives"))
				.displayItems((features, output, hasPermissions) -> STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES).forEach(output::accept)));
		SecurityCraft.decorationTab = event.registerCreativeModeTab(new ResourceLocation(SecurityCraft.MODID, "decoration"), List.of(), List.of(SecurityCraft.mineTab), builder -> builder
				.icon(() -> new ItemStack(SCContent.REINFORCED_OAK_STAIRS.get()))
				.title(Component.translatable("itemGroup.securitycraft.decoration"))
				.displayItems((features, output, hasPermissions) -> {
		//@formatter:on
					List<ItemStack> decorationGroupItems = STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION);
					List<Item> vanillaOrderedItems = new ArrayList<>();

					vanillaOrderedItems.addAll(SecurityCraft.getCreativeTabItems(CreativeModeTabs.BUILDING_BLOCKS));
					vanillaOrderedItems.addAll(SecurityCraft.getCreativeTabItems(CreativeModeTabs.COLORED_BLOCKS));
					vanillaOrderedItems.addAll(SecurityCraft.getCreativeTabItems(CreativeModeTabs.NATURAL_BLOCKS));
					vanillaOrderedItems.addAll(SecurityCraft.getCreativeTabItems(CreativeModeTabs.REDSTONE_BLOCKS));
					decorationGroupItems.sort((a, b) -> {
						if (a.getItem() instanceof BlockItem blockItemA && blockItemA.getBlock() instanceof IReinforcedBlock reinforcedBlockA && b.getItem() instanceof BlockItem blockItemB && blockItemB.getBlock() instanceof IReinforcedBlock reinforcedBlockB) {
							int indexA = vanillaOrderedItems.indexOf(reinforcedBlockA.getVanillaBlock().asItem());
							int indexB = vanillaOrderedItems.indexOf(reinforcedBlockB.getVanillaBlock().asItem());

							return Integer.compare(indexA == -1 ? Integer.MAX_VALUE : indexA, indexB == -1 ? Integer.MAX_VALUE : indexB);
						}

						return Integer.MAX_VALUE;
					});
					decorationGroupItems.forEach(output::accept);
				}));
	}

	public static void registerPackets() {
		int index = 0;

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
		SecurityCraft.channel.registerMessage(index++, RemoveMineFromMRAT.class, RemoveMineFromMRAT::encode, RemoveMineFromMRAT::decode, RemoveMineFromMRAT::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemovePositionFromSSS.class, RemovePositionFromSSS::encode, RemovePositionFromSSS::decode, RemovePositionFromSSS::onMessage);
		SecurityCraft.channel.registerMessage(index++, RemoveSentryFromSRAT.class, RemoveSentryFromSRAT::encode, RemoveSentryFromSRAT::decode, RemoveSentryFromSRAT::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetBriefcaseOwner.class, SetBriefcaseOwner::encode, SetBriefcaseOwner::decode, SetBriefcaseOwner::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetCameraPowered.class, SetCameraPowered::encode, SetCameraPowered::decode, SetCameraPowered::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetGhostSlot.class, SetGhostSlot::encode, SetGhostSlot::decode, SetGhostSlot::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetKeycardUses.class, SetKeycardUses::encode, SetKeycardUses::decode, SetKeycardUses::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetListModuleData.class, SetListModuleData::encode, SetListModuleData::decode, SetListModuleData::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetPassword.class, SetPassword::encode, SetPassword::decode, SetPassword::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetSentryMode.class, SetSentryMode::encode, SetSentryMode::decode, SetSentryMode::onMessage);
		SecurityCraft.channel.registerMessage(index++, SetStateOnDisguiseModule.class, SetStateOnDisguiseModule::encode, SetStateOnDisguiseModule::decode, SetStateOnDisguiseModule::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncBlockChangeDetector.class, SyncBlockChangeDetector::encode, SyncBlockChangeDetector::decode, SyncBlockChangeDetector::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncBlockPocketManager.class, SyncBlockPocketManager::encode, SyncBlockPocketManager::decode, SyncBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncIMSTargetingOption.class, SyncIMSTargetingOption::encode, SyncIMSTargetingOption::decode, SyncIMSTargetingOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncKeycardSettings.class, SyncKeycardSettings::encode, SyncKeycardSettings::decode, SyncKeycardSettings::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncProjector.class, SyncProjector::encode, SyncProjector::decode, SyncProjector::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncRiftStabilizer.class, SyncRiftStabilizer::encode, SyncRiftStabilizer::decode, SyncRiftStabilizer::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncSSSSettingsOnServer.class, SyncSSSSettingsOnServer::encode, SyncSSSSettingsOnServer::decode, SyncSSSSettingsOnServer::onMessage);
		SecurityCraft.channel.registerMessage(index++, SyncTrophySystem.class, SyncTrophySystem::encode, SyncTrophySystem::decode, SyncTrophySystem::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::decode, ToggleBlockPocketManager::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleModule.class, ToggleModule::encode, ToggleModule::decode, ToggleModule::onMessage);
		SecurityCraft.channel.registerMessage(index++, ToggleOption.class, ToggleOption::encode, ToggleOption::decode, ToggleOption::onMessage);
		SecurityCraft.channel.registerMessage(index++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::decode, UpdateSliderValue::onMessage);
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
}
