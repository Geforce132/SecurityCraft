package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.KeypadBarrelBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock.IReinforcedCauldronInteraction;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.hudmods.TOPDataProvider;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.ConfigAttackTargetCheck;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Mod(SecurityCraft.MODID)
@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class SecurityCraft {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "securitycraft";
	public static final Supplier<GameRules.Key<GameRules.BooleanValue>> RULE_FAKE_WATER_SOURCE_CONVERSION = Suppliers.memoize(() -> GameRules.register("fakeWaterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)));
	public static final Supplier<GameRules.Key<GameRules.BooleanValue>> RULE_FAKE_LAVA_SOURCE_CONVERSION = Suppliers.memoize(() -> GameRules.register("fakeLavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false)));
	public static final Random RANDOM = new Random();
	public static final TicketController CAMERA_TICKET_CONTROLLER = new TicketController(resLoc("camera_chunks"), (level, ticketHelper) -> { //this will only check against SecurityCraft's camera chunks, so no need to add an (instanceof SecurityCamera) somewhere
		ticketHelper.getEntityTickets().forEach(((uuid, chunk) -> {
			if (level.getEntity(uuid) == null)
				ticketHelper.removeAllTickets(uuid);
		}));
		ticketHelper.getBlockTickets().forEach((pos, chunk) -> {
			if (!(level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity) || !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getTrackedBlockEntities(level).contains(pos))
				ticketHelper.removeAllTickets(pos);
		});
	});

	public SecurityCraft(IEventBus modEventBus, ModContainer container) {
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		NeoForge.EVENT_BUS.addListener(SecurityCraft::addReloadListener);
		NeoForge.EVENT_BUS.addListener(SecurityCraft::onServerStarted);
		NeoForge.EVENT_BUS.addListener(RegistrationHandler::registerBrewingRecipes);
		container.registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_SPEC);
		SCContent.BLOCKS.register(modEventBus);
		SCContent.BLOCK_ENTITY_TYPES.register(modEventBus);
		SCContent.COMMAND_ARGUMENT_TYPES.register(modEventBus);
		SCContent.DATA_COMPONENTS.register(modEventBus);
		SCContent.DATA_SERIALIZERS.register(modEventBus);
		SCContent.ENTITY_TYPES.register(modEventBus);
		SCContent.FLUIDS.register(modEventBus);
		SCContent.ITEMS.register(modEventBus);
		SCContent.LOOT_ITEM_CONDITION_TYPES.register(modEventBus);
		SCContent.MENU_TYPES.register(modEventBus);
		SCContent.PARTICLE_TYPES.register(modEventBus);
		SCContent.RECIPE_SERIALIZERS.register(modEventBus);
		SCCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
	}

	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			RULE_FAKE_WATER_SOURCE_CONVERSION.get();
			RULE_FAKE_LAVA_SOURCE_CONVERSION.get();
		});
	}

	@SubscribeEvent
	public static void onInterModEnqueue(InterModEnqueueEvent event) {
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_EXTRACTION_BLOCK_MSG, ReinforcedHopperBlock.ExtractionBlock::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_EXTRACTION_BLOCK_MSG, IMSBlock.ExtractionBlock::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadBlock.Convertible::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadBarrelBlock.Convertible::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadChestBlock.Convertible::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadTrapDoorBlock.Convertible::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, () -> new AbstractKeypadFurnaceBlock.Convertible(Blocks.FURNACE, SCContent.KEYPAD_FURNACE.get()));
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, () -> new AbstractKeypadFurnaceBlock.Convertible(Blocks.SMOKER, SCContent.KEYPAD_SMOKER.get()));
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, () -> new AbstractKeypadFurnaceBlock.Convertible(Blocks.BLAST_FURNACE, SCContent.KEYPAD_BLAST_FURNACE.get()));
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_SENTRY_ATTACK_TARGET_MSG, ConfigAttackTargetCheck::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, CommonDoorActivator::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, InventoryScannerBlock.DoorActivator::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, ReinforcedPressurePlateBlock.DoorActivator::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, ReinforcedRedstoneBlock.DoorActivator::new);
		InterModComms.sendTo(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, SecureRedstoneInterfaceBlock.DoorActivator::new);

		if (ModList.get().isLoaded("theoneprobe"))
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPDataProvider::new);
	}

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event) {
		IReinforcedCauldronInteraction.bootStrap();
	}

	public static void onServerStarted(ServerStartedEvent event) {
		collectSCContentData(event.getServer(), true);
	}

	public static void addReloadListener(AddReloadListenerEvent event) {
		event.addListener((barrier, manager, e1, e2) -> CompletableFuture.runAsync(() -> collectSCContentData(true), e1).thenCompose(barrier::wait));
	}

	@SubscribeEvent
	public static void onRegisterTicketControllers(RegisterTicketControllersEvent event) {
		event.register(CAMERA_TICKET_CONTROLLER);
	}

	public static void collectSCContentData(boolean addPages) {
		collectSCContentData(ServerLifecycleHooks.getCurrentServer(), addPages);
	}

	public static void collectSCContentData(MinecraftServer server, boolean addPages) {
		if (addPages && server == null)
			return;

		Map<PageGroup, List<ItemStack>> groupStacks = new EnumMap<>(PageGroup.class);

		IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.clear();
		IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.clear();

		if (addPages)
			SCManualItem.PAGES.clear();

		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(Reinforced.class)) {
					Block block = ((DeferredBlock<Block>) field.get(null)).get();
					IReinforcedBlock rb = (IReinforcedBlock) block;

					IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(rb.getVanillaBlock(), block);
					IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(block, rb.getVanillaBlock());
				}

				if (field.isAnnotationPresent(HasManualPage.class)) {
					Object o = ((DeferredHolder<?, ?>) field.get(null)).get();
					HasManualPage hmp = field.getAnnotation(HasManualPage.class);
					Item item = ((ItemLike) o).asItem();
					PageGroup group = hmp.value();
					boolean wasNotAdded = false;
					Component title = Component.translatable("");
					String key = "help.";

					if (group != PageGroup.NONE) {
						if (!groupStacks.containsKey(group)) {
							groupStacks.put(group, new ArrayList<>());
							title = Utils.localize(group.getTitle());
							key += group.getSpecialInfoKey();
							wasNotAdded = true;
						}

						groupStacks.get(group).add(new ItemStack(item));
					}
					else {
						title = Utils.localize(item.getDescriptionId());
						key += item.getDescriptionId().substring(5) + ".info";
					}

					if (addPages && (group == PageGroup.NONE || wasNotAdded))
						SCManualItem.PAGES.add(new SCManualPage(item, group, title, Component.translatable(key.replace("..", ".")), hmp.designedBy(), hmp.hasRecipeDescription(), Suppliers.memoize(() -> SCManualItem.findRecipes(server, item, group))));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		groupStacks.forEach((group, list) -> group.setItems(list.stream().filter(stack -> !stack.isEmpty()).toList()));
	}

	public void registerCommands(RegisterCommandsEvent event) {
		SCCommand.register(event.getDispatcher());
	}

	public static String getVersion() {
		return "v" + ModList.get().getModContainerById(MODID).get().getModInfo().getVersion().toString();
	}

	public static ResourceLocation resLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static ResourceLocation mcResLoc(String path) {
		return ResourceLocation.withDefaultNamespace(path);
	}
}
