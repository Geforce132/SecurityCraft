package net.geforcemods.securitycraft.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.geforcemods.securitycraft.blocks.mines.BlockMine;
import net.geforcemods.securitycraft.commands.CommandModule;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.handlers.ForgeEventHandler;
import net.geforcemods.securitycraft.imc.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.ircbot.SCIRCBot;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ConfigurationHandler;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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

@Mod(modid = mod_SecurityCraft.MODID, name = "SecurityCraft", version = mod_SecurityCraft.VERSION, guiFactory = "net.geforcemods.securitycraft.gui.SecurityCraftGuiFactory", dependencies = mod_SecurityCraft.DEPENDENCIES, updateJSON = mod_SecurityCraft.UPDATEJSONURL, acceptedMinecraftVersions = "[1.12]")
@SuppressWarnings({})
public class mod_SecurityCraft {
	
	public static boolean debuggingMode;
	
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Finally! Cameras!";
	
	//TODO ********************************* This is v1.8.2.3 for MC 1.12!
	protected static final String VERSION = "v1.8.2.3";
	protected static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,)";
	protected static final String UPDATEJSONURL = "https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/Forge.json";
	
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static ServerProxy serverProxy;
	
	@Instance("securitycraft")
    public static mod_SecurityCraft instance = new mod_SecurityCraft();
		
	public static ConfigurationHandler configHandler = new ConfigurationHandler();
	
	public static SimpleNetworkWrapper network;
	
	public static ForgeEventHandler eventHandler = new ForgeEventHandler();
	
	private GuiHandler GuiHandler = new GuiHandler();
	
	public HashMap<String, SCIRCBot> ircBots = new HashMap<String, SCIRCBot>();
	public HashMap<String, Object[]> cameraUsePositions = new HashMap<String, Object[]>();
	
	public ArrayList<SCManualPage> manualPages = new ArrayList<SCManualPage>();

	private NBTTagCompound savedModule;
	
	public static Configuration configFile;	
	
	//Blocks
	public static Block alarm;
	public static Block alarmLit;
	public static Block bogusLavaFlowing;
	public static Block bogusWaterFlowing;
	public static Block bouncingBetty;
	public static Block cageTrap;
	public static Block claymore;
	public static Block cobblestoneMine;
	public static Block diamondOreMine;
	public static Block dirtMine;
	public static Block frame;
	public static Block furnaceMine;
	public static Block ims;
	public static Block inventoryScanner;
	public static Block inventoryScannerField;
	public static Block ironFence;
	public static Block ironTrapdoor;
	public static Block keycardReader;
	public static Block keypad;
	public static Block keypadChest;
	public static Block keypadFurnace;
	public static Block laser;
	public static Block laserBlock;
	public static Block panicButton;
	public static Block portableRadar;
	public static Block protecto;
	public static Block reinforcedCobblestone;
	public static Block reinforcedDirt;
	public static Block reinforcedDoor;
	public static Block reinforcedDoubleStoneSlabs;
	public static Block reinforcedDoubleWoodSlabs;
	public static Block reinforcedFencegate;
	public static Block reinforcedGlass;
	public static Block reinforcedSandstone;
	public static Block reinforcedStainedGlass;
	public static Block reinforcedStairsAcacia;
	public static Block reinforcedStairsBirch;
	public static Block reinforcedStairsCobblestone;
	public static Block reinforcedStairsDarkoak;
	public static Block reinforcedStairsJungle;
	public static Block reinforcedStairsOak;
	public static Block reinforcedStairsSandstone;
	public static Block reinforcedStairsSpruce;
	public static Block reinforcedStairsStone;
	public static Block reinforcedStone;
	public static Block reinforcedStoneSlabs;
	public static Block reinforcedWoodPlanks;
	public static Block reinforcedWoodSlabs;
	public static Block retinalScanner;
	public static Block sandMine;
	public static Block scannerDoor;
	public static Block securityCamera;
	public static Block stoneMine;
	public static Block trackMine;
	public static Block unbreakableIronBars;
	public static Block usernameLogger;
	public static BlockMine mine;
	public static BlockMine mineCut;
	public static BlockStaticLiquid bogusLava;
	public static BlockStaticLiquid bogusWater;
	
	//Items
	public static Item adminTool;
	public static Item briefcase;
	public static Item cameraMonitor;
	public static Item codebreaker;
	public static Item fLavaBucket;
	public static Item fWaterBucket;
	public static Item keycardLV1;
	public static Item keycardLV2;
	public static Item keycardLV3;
	public static Item keycardLV4;
	public static Item keycardLV5;
	public static Item keyPanel;
	public static Item limitedUseKeycard;
	public static Item reinforcedDoorItem;
	public static Item remoteAccessMine;
	public static Item scannerDoorItem;
	public static Item scManual;
	public static Item taser;
	public static Item universalBlockModifier;
	public static Item universalBlockReinforcerLvL1;
	public static Item universalBlockReinforcerLvL2;
	public static Item universalBlockReinforcerLvL3;
	public static Item universalBlockRemover;
	public static Item universalKeyChanger;
	public static Item universalOwnerChanger;
	public static Item wireCutters;

    //Modules
    public static ItemModule redstoneModule;
    public static ItemModule whitelistModule;
    public static ItemModule blacklistModule;
    public static ItemModule harmingModule;
    public static ItemModule smartModule;
    public static ItemModule storageModule;
    public static ItemModule disguiseModule;

    public static Item testItem;
    
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
    	log(mod_SecurityCraft.VERSION + " of SecurityCraft is for a post MC-1.6.4 version! Configuration files are useless for setting anything besides options.");
		mod_SecurityCraft.configFile = new Configuration(event.getSuggestedConfigurationFile());
    	mod_SecurityCraft.configHandler.setupConfiguration();
		log("Config file loaded.");
		log("Setting up handlers!");
		mod_SecurityCraft.configHandler.setupHandlers(event);
		log("Handlers registered.");
		log("Setting up network....");
		mod_SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(mod_SecurityCraft.MODID);
		mod_SecurityCraft.configHandler.setupPackets(mod_SecurityCraft.network);
		log("Network setup.");
		
		log("Loading mod additions....");
		mod_SecurityCraft.configHandler.setupAdditions();
		
		if(mod_SecurityCraft.debuggingMode){
			mod_SecurityCraft.configHandler.setupDebugAdditions();
		}
				
		log("Finished loading mod additions.");
		log("Doing registering stuff... (PT 1/2)");
		mod_SecurityCraft.configHandler.setupGameRegistry();
		mod_SecurityCraft.serverProxy.setupTextureRegistry();
		serverProxy.registerTextureFiles();

		ModMetadata modMeta = event.getModMetadata();
        modMeta.authorList = Arrays.asList(new String[] {
            "Geforce, bl4ckscor3"
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
		
		FMLInterModComms.sendMessage("waila", "register", "net.geforcemods.securitycraft.imc.waila.WailaDataProvider.callbackRegister");	
						
		if(configHandler.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();
			if(vcUpdateTag != null){
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
			}
		}
		
		log("Doing registering stuff... (PT 2/2)");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler);

		mod_SecurityCraft.configHandler.setupEntityRegistry();
		mod_SecurityCraft.configHandler.setupOtherRegistries();
		serverProxy.registerRenderThings();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(mod_SecurityCraft.eventHandler);
		log("Mod finished loading correctly! :D");
	}
	
	/**
	 * Get the IRC bot for the given player.
	 */
	public SCIRCBot getIrcBot(String playerName) {
		return ircBots.get(playerName);
	}
	
	/**
	 * Create an IRC bot for the given player.
	 */
	public void createIrcBot(String playerName) {
		ircBots.put(playerName, new SCIRCBot("SCUser_" + playerName));
	}
	
	/**
	 * Remove/delete the given player's IRC bot.
	 */
	public void removeIrcBot(String playerName){
		ircBots.remove(playerName);
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
	public static void log(String par1) {
		log(par1, false);
	}
	
	public static void log(String par1, boolean isSevereError) {
		if(mod_SecurityCraft.debuggingMode){
			System.out.println(isSevereError ? "{SecurityCraft} {" + FMLCommonHandler.instance().getEffectiveSide() + "} {Severe}: " + par1 : "[SecurityCraft] [" + FMLCommonHandler.instance().getEffectiveSide() + "] " + par1);
		}
	}
	
	public static String getVersion() {
		return VERSION;
	}
	
}
