package net.geforcemods.securitycraft;

import java.util.Random;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.geforcemods.securitycraft.api.SecurityCraftAPI;
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
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.ConfigAttackTargetCheck;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
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
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(SecurityCraft.MODID)
@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class SecurityCraft {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "securitycraft";
	public static final GameRules.Key<GameRules.BooleanValue> RULE_FAKE_WATER_SOURCE_CONVERSION = GameRules.register("fakeWaterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> RULE_FAKE_LAVA_SOURCE_CONVERSION = GameRules.register("fakeLavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false));
	public static final Random RANDOM = new Random();
	public static final TicketController CAMERA_TICKET_CONTROLLER = new TicketController(new ResourceLocation(SecurityCraft.MODID, "camera_chunks"), (level, ticketHelper) -> { //this will only check against SecurityCraft's camera chunks, so no need to add an (instanceof SecurityCamera) somewhere
		ticketHelper.getEntityTickets().forEach(((uuid, chunk) -> {
			if (level.getEntity(uuid) == null)
				ticketHelper.removeAllTickets(uuid);
		}));
	});
	public static final boolean IS_A_SODIUM_MOD_INSTALLED = Util.make(() -> {
		ModList modList = ModList.get();

		return modList.isLoaded("embeddium") || modList.isLoaded("rubidium") || modList.isLoaded("sodium");
	});

	public SecurityCraft(IEventBus modEventBus, ModContainer container) {
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		NeoForge.EVENT_BUS.addListener(RegistrationHandler::registerBrewingRecipes);
		container.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
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
	public static void onInterModEnqueue(InterModEnqueueEvent event) { //stage 3
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
	public static void onInterModProcess(InterModProcessEvent event) { //stage 4
		IReinforcedCauldronInteraction.bootStrap();
	}

	@SubscribeEvent
	public static void onRegisterTicketControllers(RegisterTicketControllersEvent event) {
		event.register(CAMERA_TICKET_CONTROLLER);
	}

	public void registerCommands(RegisterCommandsEvent event) {
		SCCommand.register(event.getDispatcher());
	}

	public static String getVersion() {
		return "v" + ModList.get().getModContainerById(MODID).get().getModInfo().getVersion().toString();
	}
}
