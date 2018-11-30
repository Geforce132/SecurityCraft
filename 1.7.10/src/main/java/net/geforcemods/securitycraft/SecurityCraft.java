package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Ordering;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.geforcemods.securitycraft.commands.CommandModule;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.lookingglass.IWorldViewHelper;
import net.geforcemods.securitycraft.compat.lookingglass.LookingGlassPanelRenderer;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.geforcemods.securitycraft.tabs.CreativeTabSCDecoration;
import net.geforcemods.securitycraft.tabs.CreativeTabSCExplosives;
import net.geforcemods.securitycraft.tabs.CreativeTabSCTechnical;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = SecurityCraft.MODID, name = "SecurityCraft", version = SecurityCraft.VERSION, guiFactory = "net.geforcemods.securitycraft.gui.SecurityCraftGuiFactory", dependencies = SecurityCraft.DEPENDENCIES)
public class SecurityCraft
{
	public static boolean debug;
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Finally! Cameras!";
	//********************************* This is v1.8.10 for MC 1.7.10!
	protected static final String VERSION = "v1.8.10";
	protected static final String DEPENDENCIES = "required-after:Forge@[10.13.4.1558,);after:LookingGlass@[0.2.0.01,);";
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static ServerProxy serverProxy;
	@Instance("securitycraft")
	public static SecurityCraft instance = new SecurityCraft();
	public static ConfigHandler config = new ConfigHandler();
	public static SimpleNetworkWrapper network;
	public static SCEventHandler eventHandler = new SCEventHandler();
	private GuiHandler guiHandler = new GuiHandler();
	public LookingGlassPanelRenderer lgPanelRenderer;
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
		config.setupConfiguration();
		log("Config file loaded.");
		log("Setting up handlers!");
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
		RegistrationHandler.registerTileEntities();
		RegistrationHandler.registerRecipes();
		log("Sorting items...");

		List<Item> technicalItems = new ArrayList<Item>();

		for(SCManualPage page : manualPages)
			technicalItems.add(page.getItem());

		CreativeTabSCTechnical.itemSorter = Ordering.explicit(technicalItems).onResultOf(item -> item.getItem());
		ModMetadata modMeta = event.getModMetadata();
		modMeta.authorList = Arrays.asList(new String[] {
				"Geforce", "bl4ckscor3"
		});
		modMeta.autogenerated = false;
		modMeta.credits = "Thanks to all of you guys for your support!";
		modMeta.description = "Adds a load of things to keep your house safe with.\nIf you like this mod, hit the green arrow\nin the corner of the forum thread!\nPlease visit the URL above for help. \n \nMessage of the update: \n" + MOTU;
		modMeta.url = "http://geforcemods.net";
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		log("Setting up inter-mod stuff...");

		FMLInterModComms.sendMessage("Waila", "register", "net.geforcemods.securitycraft.compat.waila.WailaDataProvider.callbackRegister");
		FMLInterModComms.sendMessage("LookingGlass", "API", "net.geforcemods.securitycraft.compat.lookingglass.LookingGlassAPIProvider.register");

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

	public NBTTagCompound getSavedModule() {
		return savedModule;
	}

	public void setSavedModule(NBTTagCompound savedModule) {
		this.savedModule = savedModule;
	}

	public LookingGlassPanelRenderer getLGPanelRenderer(){
		return instance.lgPanelRenderer;
	}

	/**
	 * Get the IWorldView object for the specified key.
	 */
	public IWorldViewHelper getViewFromCoords(String coords){
		return ((ClientProxy) SecurityCraft.serverProxy).worldViews.get(coords);
	}

	/**
	 * Do we have an IWorldView object for the given key already saved?
	 */
	public boolean hasViewForCoords(String coords){
		return ((ClientProxy) SecurityCraft.serverProxy).worldViews.containsKey(coords);
	}

	/**
	 * Remove the IWorldView object for the specified key.
	 */
	public void removeViewForCoords(String coords){
		((ClientProxy) SecurityCraft.serverProxy).worldViews.remove(coords);
	}

	/**
	 * @return Should SecurityCraft use the LookingGlass API when it can be used?
	 */
	public boolean useLookingGlass(){
		if(Loader.isModLoaded("LookingGlass") && config != null && config.useLookingGlass)
			return true;
		else
			return false;
	}

	/**
	 * Prints a String to the console. Only will print if SecurityCraft is in debug mode.
	 */
	public static void log(String line){
		log(line, false);
	}

	public static void log(String line, boolean isSevereError){
		if(SecurityCraft.debug)
			System.out.println(isSevereError ? "{SecurityCraft} {" + FMLCommonHandler.instance().getEffectiveSide() + "} {Severe}: " + line : "[SecurityCraft] [" + FMLCommonHandler.instance().getEffectiveSide() + "] " + line);
	}

	public static String getVersion(){
		return VERSION;
	}
}
