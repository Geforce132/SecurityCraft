package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.KeypadTrapDoorBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedMetalsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.icbmclassic.ICBMClassicEMPCompat;
import net.geforcemods.securitycraft.compat.lycanitesmobs.LycanitesMobsCompat;
import net.geforcemods.securitycraft.compat.projecte.ProjectECompat;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.itemgroups.SCDecorationTab;
import net.geforcemods.securitycraft.itemgroups.SCExplosivesTab;
import net.geforcemods.securitycraft.itemgroups.SCTechnicalTab;
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.ConfigAttackTargetCheck;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.screen.ScreenHandler;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = SecurityCraft.MODID, name = "SecurityCraft", dependencies = "required-after:forge@[14.23.5.2826,)", updateJSON = "https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/Forge.json", acceptedMinecraftVersions = "[1.12.2]")
public class SecurityCraft {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "securitycraft";
	public static final Random RANDOM = new Random();
	public static final CreativeTabs TECHNICAL_TAB = new SCTechnicalTab();
	public static final CreativeTabs MINE_TAB = new SCExplosivesTab();
	public static final CreativeTabs DECORATION_TAB = new SCDecorationTab();
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static IProxy proxy;
	@Instance(MODID)
	public static SecurityCraft instance = new SecurityCraft();
	public static SimpleNetworkWrapper network;
	private ScreenHandler guiHandler = new ScreenHandler();

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new SCCommand());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(SecurityCraft.MODID);
		RegistrationHandler.registerPackets(SecurityCraft.network);
		SetupHandler.init();
		proxy.registerEntityRenderingHandlers();

		if (Loader.isModLoaded("icbmclassic"))
			MinecraftForge.EVENT_BUS.register(new ICBMClassicEMPCompat());

		Configuration config = ForgeChunkManager.getConfig();

		if (!config.hasCategory(SecurityCraft.MODID)) {
			config.get(SecurityCraft.MODID, "maximumChunksPerTicket", 1000).setMinValue(0);
			config.get(SecurityCraft.MODID, "maximumTicketCount", 1000).setMinValue(0);
			config.save();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_EXTRACTION_BLOCK_MSG, ReinforcedHopperBlock.ExtractionBlock.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_EXTRACTION_BLOCK_MSG, IMSBlock.ExtractionBlock.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadChestBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadFurnaceBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG, KeypadTrapDoorBlock.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_SENTRY_ATTACK_TARGET_MSG, ConfigAttackTargetCheck.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, CommonDoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, InventoryScannerBlock.DoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, ReinforcedPressurePlateBlock.DoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, ReinforcedMetalsBlock.DoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage(SecurityCraft.MODID, SecurityCraftAPI.IMC_DOOR_ACTIVATOR_MSG, SecureRedstoneInterfaceBlock.DoorActivator.class.getName());
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "net.geforcemods.securitycraft.compat.hudmods.TOPDataProvider");

		if (Loader.isModLoaded("lycanitesmobs"))
			FMLInterModComms.sendFunctionMessage(MODID, SecurityCraftAPI.IMC_SENTRY_ATTACK_TARGET_MSG, LycanitesMobsCompat.class.getName());

		if (ConfigHandler.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();

			if (vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		ModuleType.refresh();
		proxy.registerRenderThings();
		FMLCommonHandler.instance().getDataFixer().init(SecurityCraft.MODID, TileEntityIDDataFixer.VERSION).registerFix(FixTypes.BLOCK_ENTITY, new TileEntityIDDataFixer());
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedCobblestone), new ItemStack(SCContent.reinforcedStone, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedSand, 1, 0), new ItemStack(SCContent.reinforcedGlass, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedSand, 1, 1), new ItemStack(SCContent.reinforcedGlass, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStoneBrick, 1, 0), new ItemStack(SCContent.reinforcedStoneBrick, 1, 2), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedClay, 1, 0), new ItemStack(SCContent.reinforcedHardenedClay, 1, 0), 0.35F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.WHITE.getMetadata()), new ItemStack(SCContent.reinforcedWhiteGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.ORANGE.getMetadata()), new ItemStack(SCContent.reinforcedOrangeGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.MAGENTA.getMetadata()), new ItemStack(SCContent.reinforcedMagentaGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()), new ItemStack(SCContent.reinforcedLightBlueGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.YELLOW.getMetadata()), new ItemStack(SCContent.reinforcedYellowGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.LIME.getMetadata()), new ItemStack(SCContent.reinforcedLimeGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.PINK.getMetadata()), new ItemStack(SCContent.reinforcedPinkGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.GRAY.getMetadata()), new ItemStack(SCContent.reinforcedGrayGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.SILVER.getMetadata()), new ItemStack(SCContent.reinforcedSilverGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.CYAN.getMetadata()), new ItemStack(SCContent.reinforcedCyanGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.PURPLE.getMetadata()), new ItemStack(SCContent.reinforcedPurpleGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.BLUE.getMetadata()), new ItemStack(SCContent.reinforcedBlueGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.BROWN.getMetadata()), new ItemStack(SCContent.reinforcedBrownGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.GREEN.getMetadata()), new ItemStack(SCContent.reinforcedGreenGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.RED.getMetadata()), new ItemStack(SCContent.reinforcedRedGlazedTerracotta), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStainedHardenedClay, 1, EnumDyeColor.BLACK.getMetadata()), new ItemStack(SCContent.reinforcedBlackGlazedTerracotta), 0.1F);
	}

	@EventHandler
	public void onIMC(IMCEvent event) {
		SecurityCraftAPI.onIMC(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (Loader.isModLoaded("cyclicmagic"))
			MinecraftForge.EVENT_BUS.register(new CyclicCompat());

		for (Field field : SCContent.class.getFields()) {
			try {
				if (field.isAnnotationPresent(Reinforced.class)) {
					Block block = (Block) field.get(null);
					IReinforcedBlock rb = (IReinforcedBlock) block;

					for (Block vanillaBlock : rb.getVanillaBlocks()) {
						IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(vanillaBlock, block);
					}
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (Loader.isModLoaded("projecte"))
			ProjectECompat.registerConversions();

		ForgeChunkManager.setForcedChunkLoadingCallback(instance, (tickets, world) -> { //this will only check against SecurityCraft's camera chunks, so no need to add an (instanceof SecurityCameraEntity) somewhere
			tickets.forEach(ticket -> {
				if (ticket.getType() == Type.ENTITY && ((WorldServer) ticket.world).getEntityFromUuid(ticket.getEntity().getPersistentID()) == null)
					ForgeChunkManager.releaseTicket(ticket);
			});
		});
		ConfigHandler.loadEffects();
		OreDictionary.registerOre("securitycraftReinforcedFence", SCContent.reinforcedOakFence);
		OreDictionary.registerOre("securitycraftReinforcedFence", SCContent.reinforcedSpruceFence);
		OreDictionary.registerOre("securitycraftReinforcedFence", SCContent.reinforcedBirchFence);
		OreDictionary.registerOre("securitycraftReinforcedFence", SCContent.reinforcedJungleFence);
		OreDictionary.registerOre("securitycraftReinforcedFence", SCContent.reinforcedDarkOakFence);
		OreDictionary.registerOre("securitycraftReinforcedFence", SCContent.reinforcedAcaciaFence);
		OreDictionary.registerOre("securitycraftReinforcedFenceGate", SCContent.reinforcedOakFenceGate);
		OreDictionary.registerOre("securitycraftReinforcedFenceGate", SCContent.reinforcedSpruceFenceGate);
		OreDictionary.registerOre("securitycraftReinforcedFenceGate", SCContent.reinforcedBirchFenceGate);
		OreDictionary.registerOre("securitycraftReinforcedFenceGate", SCContent.reinforcedJungleFenceGate);
		OreDictionary.registerOre("securitycraftReinforcedFenceGate", SCContent.reinforcedDarkOakFenceGate);
		OreDictionary.registerOre("securitycraftReinforcedFenceGate", SCContent.reinforcedAcaciaFenceGate);
	}

	@EventHandler
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		PasscodeUtils.startHashingThread(event.getServer());
	}

	@EventHandler
	public static void onServerStop(FMLServerStoppedEvent event) {
		PasscodeUtils.stopHashingThread();
	}

	public static String getVersion() {
		return Loader.instance().activeModContainer().getVersion();
	}
}
