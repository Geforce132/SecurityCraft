package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.geforcemods.securitycraft.commands.CommandModule;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.geforcemods.securitycraft.tabs.CreativeTabSCDecoration;
import net.geforcemods.securitycraft.tabs.CreativeTabSCExplosives;
import net.geforcemods.securitycraft.tabs.CreativeTabSCTechnical;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = SecurityCraft.MODID, name = "SecurityCraft", version = SecurityCraft.VERSION, guiFactory = "net.geforcemods.securitycraft.gui.SecurityCraftGuiFactory", dependencies = SecurityCraft.DEPENDENCIES, updateJSON = SecurityCraft.UPDATEJSONURL, acceptedMinecraftVersions = "[1.8.8]")
public class SecurityCraft {
	public static boolean debuggingMode;
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Finally! Cameras!";
	//********************************* This is v1.8.7 for MC 1.8.8/9!
	protected static final String VERSION = "v1.8.7";
	protected static final String DEPENDENCIES = "required-after:Forge@[11.15.0.1655,)";
	protected static final String UPDATEJSONURL = "https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/Forge.json";
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static ServerProxy serverProxy;
	@Instance("securitycraft")
	public static SecurityCraft instance = new SecurityCraft();
	public static ConfigHandler config = new ConfigHandler();
	public static SimpleNetworkWrapper network;
	public static SCEventHandler eventHandler = new SCEventHandler();
	private GuiHandler guiHandler = new GuiHandler();
	public HashMap<String, Object[]> cameraUsePositions = new HashMap<String, Object[]>();
	public ArrayList<SCManualPage> manualPages = new ArrayList<SCManualPage>();
	private NBTTagCompound savedModule;
	public static Configuration configFile;
	public static CreativeTabs tabSCTechnical = new CreativeTabSCTechnical();
	public static CreativeTabs tabSCMine = new CreativeTabSCExplosives();
	public static CreativeTabs tabSCDecoration = new CreativeTabSCDecoration();

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandSC());
		event.registerServerCommand(new CommandModule());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log("Starting to load....");
		log("Loading config file....");
		log(SecurityCraft.VERSION + " of SecurityCraft is for a post MC-1.6.4 version! Configuration files are useless for setting anything besides options.");
		SecurityCraft.configFile = new Configuration(event.getSuggestedConfigurationFile());
		SecurityCraft.config.setupConfiguration();
		log("Config file loaded.");
		log("Setting up handlers!");
		MinecraftForge.EVENT_BUS.register(eventHandler);
		log("Handlers registered.");
		log("Setting up network....");
		SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(SecurityCraft.MODID);
		RegistrationHandler.registerPackets(SecurityCraft.network);
		log("Network setup.");
		log("Loading mod content....");
		SetupHandler.setupBlocks();
		SetupHandler.setupMines();
		SetupHandler.setupItems();
		log("Finished loading mod content.");
		log("Regisering mod content... (PT 1/2)");
		RegistrationHandler.registerContent();
		serverProxy.registerResourceLocations();
		RegistrationHandler.registerTileEntities();
		RegistrationHandler.registerRecipes();
		serverProxy.registerVariants();
		ModMetadata modMeta = event.getModMetadata();
		modMeta.authorList = Arrays.asList(new String[] {
				"Geforce", "bl4ckscor3"
		});
		modMeta.autogenerated = false;
		modMeta.credits = "Thanks to all of you guys for your support!";
		modMeta.description = "Adds a load of things to keep your house safe with.\nIf you like this mod, hit the green arrow\nin the corner of the forum thread!\nPlease visit the URL above for help. \n \nMessage of the update: \n" + MOTU;
		modMeta.url = "http://geforcemods.net";
		modMeta.logoFile = "/scLogo.png";
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		log("Setting up inter-mod stuff...");

		FMLInterModComms.sendMessage("Waila", "register", "net.geforcemods.securitycraft.compat.waila.WailaDataProvider.callbackRegister");

		if(config.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();
			if(vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		log("Registering mod content... (PT 2/2)");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		RegistrationHandler.registerEntities();
		EnumCustomModules.refresh();
		serverProxy.registerRenderThings();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(SecurityCraft.eventHandler);
		log("Mod finished loading correctly! :D");
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
	public static void log(String line){
		log(line, false);
	}

	public static void log(String line, boolean isSevereError){
		if(SecurityCraft.debuggingMode)
			System.out.println(isSevereError ? "{SecurityCraft} {" + FMLCommonHandler.instance().getEffectiveSide() + "} {Severe}: " + line : "[SecurityCraft] [" + FMLCommonHandler.instance().getEffectiveSide() + "] " + line);
	}

	public static String getVersion(){
		return VERSION;
	}

}
