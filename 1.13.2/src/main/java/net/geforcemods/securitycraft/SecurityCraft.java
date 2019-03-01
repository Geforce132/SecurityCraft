package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler.ClientConfig;
import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.CommandModule;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCDecoration;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCExplosives;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCTechnical;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Tinted;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(SecurityCraft.MODID)
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Finally! Cameras!";
	//********************************* This is v1.8.11 for MC 1.13.2!
	protected static final String VERSION = "v1.8.11-beta1";
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static IProxy proxy;
	public static SecurityCraft instance;
	public static final String PROTOCOL_VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private GuiHandler guiHandler = new GuiHandler();
	public HashMap<String, Object[]> cameraUsePositions = new HashMap<String, Object[]>();
	public ArrayList<SCManualPage> manualPages = new ArrayList<SCManualPage>();
	private NBTTagCompound savedModule;
	public static ItemGroup groupSCTechnical = new ItemGroupSCTechnical();
	public static ItemGroup groupSCMine = new ItemGroupSCExplosives();
	public static ItemGroup groupSCDecoration = new ItemGroupSCDecoration();
	private final List<Field> toTint = new ArrayList<>();

	public SecurityCraft()
	{
		instance = this;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModProcess);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::registerBlockColorHandler);
		MinecraftForge.EVENT_BUS.addListener(this::registerItemColorHandler);
		MinecraftForge.EVENT_BUS.register(new SCEventHandler());
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.CONFIG_SPEC);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::getClientGuiElement);
	}

	public void serverStarting(FMLServerStartingEvent event){
		CommandSC.register(event.getCommandDispatcher());
		CommandModule.register(event.getCommandDispatcher());
	}

	public void onFMLCommonSetup(FMLCommonSetupEvent event) //preInit
	{
		RegistrationHandler.registerPackets();
	}

	public void registerBlockColorHandler(ColorHandlerEvent.Block event)
	{
		getOrPopulateToTint().forEach(field -> {
			int tint = field.getAnnotation(Tinted.class).tint();

			try
			{
				Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> tint, (Block)field.get(null));
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		});
	}

	public void registerItemColorHandler(ColorHandlerEvent.Item event)
	{
		getOrPopulateToTint().forEach(field -> {
			int tint = field.getAnnotation(Tinted.class).tint();

			try
			{
				Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> tint, (Block)field.get(null));
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		});
	}

	private List<Field> getOrPopulateToTint()
	{
		if(toTint.isEmpty())
		{
			for(Field field : SCContent.class.getFields())
			{
				if(field.isAnnotationPresent(Tinted.class))
					toTint.add(field);
			}
		}

		return toTint;
	}

	public void onInterModProcess(InterModProcessEvent event){ //postInit
		DataSerializers.registerSerializer(Owner.SERIALIZER);

		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					IReinforcedBlock.BLOCKS.add((Block)field.get(null));
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		toTint.clear(); //clear up some unused memory
		log("Mod finished loading correctly! :D");
	}

	@SubscribeEvent
	public void preInit(FMLPreInitializationEvent event){
		log("Starting to load....");
		log("Loading config file....");
		log("Config file loaded.");

		log("Loading mod content....");
		SetupHandler.setupBlocks();
		SetupHandler.setupMines();
		SetupHandler.setupItems();
		log("Finished loading mod content.");
		log("Regisering mod content... (PT 1/2)");
	}

	@SubscribeEvent
	public void init(InterModEnqueueEvent event){
		log("Setting up inter-mod stuff...");

		if(ClientConfig.CONFIG.checkForUpdates.get()) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();
			if(vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		log("Registering mod content... (PT 2/2)");
		EnumCustomModules.refresh();
		proxy.registerRenderThings();
	}

	public Object[] getUsePosition(String playerName) {
		return cameraUsePositions.get(playerName);
	}

	public void setUsePosition(String playerName, double x, double y, double z, float yaw, float pitch) {
		cameraUsePositions.put(playerName, new Object[]{x, y, z, yaw, pitch});
	}

	public boolean hasUsePosition(String playerName) {
		return cameraUsePositions.containsKey(playerName);
	}

	public void removeUsePosition(String playerName){
		cameraUsePositions.remove(playerName);
	}

	public NBTTagCompound getSavedModule() {
		return savedModule;
	}

	public void setSavedModule(NBTTagCompound savedModule) {
		this.savedModule = savedModule;
	}

	/**
	 * Prints a String to the console. Only will print if SecurityCraft is in debug mode.
	 */
	public static void log(String line) {
		log(line, false);
	}

	public static void log(String line, boolean isSevereError) {
		if(ServerConfig.CONFIG.debug.get())
			System.out.println(isSevereError ? "{SecurityCraft} {" + FMLLoader.getDist() + "} {Severe}: " + line : "[SecurityCraft] [" + FMLLoader.getDist() + "] " + line);
	}

	public static String getVersion() {
		return VERSION;
	}
}
