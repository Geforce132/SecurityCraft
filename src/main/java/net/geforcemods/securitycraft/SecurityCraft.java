package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.KeypadBarrelBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRedstoneBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.lycanitesmobs.LycanitesMobsCompat;
import net.geforcemods.securitycraft.compat.top.TOPDataProvider;
import net.geforcemods.securitycraft.itemgroups.SCDecorationTab;
import net.geforcemods.securitycraft.itemgroups.SCExplosivesTab;
import net.geforcemods.securitycraft.itemgroups.SCTechnicalTab;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.BlockEntityNBTCondition;
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.ConfigAttackTargetCheck;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(SecurityCraft.MODID)
@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	public static final Random RANDOM = new Random();
	public static final ItemGroup TECHNICAL_TAB = new SCTechnicalTab();
	public static final ItemGroup MINE_TAB = new SCExplosivesTab();
	public static final ItemGroup DECORATION_TAB = new SCDecorationTab();
	public static final LootConditionType TILE_ENTITY_NBT_LOOT_CONDITION = LootConditionManager.register(SecurityCraft.MODID + ":tile_entity_nbt", new BlockEntityNBTCondition.ConditionSerializer());
	public static SimpleChannel channel;

	public SecurityCraft() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_SPEC);
		SCContent.BLOCKS.register(modEventBus);
		SCContent.BLOCK_ENTITY_TYPES.register(modEventBus);
		SCContent.ENTITY_TYPES.register(modEventBus);
		SCContent.FLUIDS.register(modEventBus);
		SCContent.ITEMS.register(modEventBus);
		SCContent.MENU_TYPES.register(modEventBus);
		SCContent.PARTICLE_TYPES.register(modEventBus);
	}

	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event) { //stage 1
		RegistrationHandler.registerPackets();
		RegistrationHandler.registerFakeLiquidRecipes();
		RegistrationHandler.registerArgumentTypes();
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

		if (ModList.get().isLoaded("theoneprobe"))
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPDataProvider::new);

		if (ModList.get().isLoaded("lycanitesmobs"))
			InterModComms.sendTo(MODID, SecurityCraftAPI.IMC_SENTRY_ATTACK_TARGET_MSG, LycanitesMobsCompat::new);
	}

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event) { //stage 4
		collectSCContentData();
		ForgeChunkManager.setForcedChunkLoadingCallback(SecurityCraft.MODID, (world, ticketHelper) -> { //this will only check against SecurityCraft's camera chunks, so no need to add an (instanceof SecurityCameraEntity) somewhere
			ticketHelper.getEntityTickets().forEach(((uuid, chunk) -> {
				if (world.getEntity(uuid) == null)
					ticketHelper.removeAllTickets(uuid);
			}));
		});
	}

	public static void collectSCContentData() {
		Map<PageGroup, List<ItemStack>> groupStacks = new EnumMap<>(PageGroup.class);

		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(Reinforced.class)) {
					Block block = ((RegistryObject<Block>) field.get(null)).get();
					IReinforcedBlock rb = (IReinforcedBlock) block;

					IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(rb.getVanillaBlock(), block);
					IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(block, rb.getVanillaBlock());
				}

				if (field.isAnnotationPresent(HasManualPage.class)) {
					Object o = ((RegistryObject<?>) field.get(null)).get();
					HasManualPage hmp = field.getAnnotation(HasManualPage.class);
					Item item = ((IItemProvider) o).asItem();
					PageGroup group = hmp.value();
					boolean wasNotAdded = false;
					TranslationTextComponent title = new TranslationTextComponent("");
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

					if (group == PageGroup.NONE || wasNotAdded)
						SCManualItem.PAGES.add(new SCManualPage(item, group, title, new TranslationTextComponent(key.replace("..", ".")), hmp.designedBy(), hmp.hasRecipeDescription()));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		groupStacks.forEach((group, list) -> group.setItems(Ingredient.of(list.stream())));
	}

	public void registerCommands(RegisterCommandsEvent event) {
		SCCommand.register(event.getDispatcher());
	}

	public static String getVersion() {
		return ModList.get().getModContainerById(MODID).get().getModInfo().getVersion().getQualifier();
	}
}
