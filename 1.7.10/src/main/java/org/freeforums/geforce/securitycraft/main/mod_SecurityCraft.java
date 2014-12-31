package org.freeforums.geforce.securitycraft.main;


import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.freeforums.geforce.securitycraft.blocks.BlockEMPedWire;
import org.freeforums.geforce.securitycraft.blocks.mines.BlockMine;
import org.freeforums.geforce.securitycraft.commands.CommandModule;
import org.freeforums.geforce.securitycraft.commands.CommandSCHelp;
import org.freeforums.geforce.securitycraft.commands.CommandSCLog;
import org.freeforums.geforce.securitycraft.gui.GuiHandler;
import org.freeforums.geforce.securitycraft.handlers.ForgeEventHandler;
import org.freeforums.geforce.securitycraft.ircbot.SCIRCBot;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.network.ConfigurationHandler;
import org.freeforums.geforce.securitycraft.network.ServerProxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;


@Mod(modid = mod_SecurityCraft.MODID, name = "SecurityCraft", version = mod_SecurityCraft.VERSION, guiFactory = "org.freeforums.geforce.securitycraft.gui.SecurityCraftGuiFactory", dependencies = mod_SecurityCraft.FORGEVERSION)
@SuppressWarnings({"static-access"})
public class mod_SecurityCraft {
	
	public static boolean debuggingMode;
	
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Thanks for all your suggestions!";
	
	//TODO UPDATE 'RECIPES' and 'HELP' ArrayList's.
	//TODO ********************************* This is v1.6.1 for MC 1.7.10!
	protected static final String VERSION = "v1.7.0";
	protected static final String FORGEVERSION = "required-after:Forge@[10.13.0.1180,)";
	
	
	@SidedProxy(clientSide = "org.freeforums.geforce.securitycraft.network.ClientProxy", serverSide = "org.freeforums.geforce.securitycraft.network.ServerProxy")
	public static ServerProxy serverProxy;
	
	@Instance("securitycraft")
    public static mod_SecurityCraft instance = new mod_SecurityCraft();
		
	public static ConfigurationHandler configHandler = new ConfigurationHandler();
	
	public static SimpleNetworkWrapper network;
	
	public static ForgeEventHandler eventHandler = new ForgeEventHandler();
	
	public static CreativeTabs tabSCTechnical = new CreativeTabSCTechnical(CreativeTabs.getNextID(),"tabSecurityCraft");
	public static CreativeTabs tabSCMine = new CreativeTabSCExplosives(CreativeTabs.getNextID(),"tabSecurityCraft");

	private GuiHandler GuiHandler = new GuiHandler();
	
	private SCIRCBot ircBot;
	private NBTTagCompound savedModule;
	
	public static Configuration configFile;
	//public CCTVBase cctvPlugin = new CCTVBase();
	
	
	//Blocks
	public static Block LaserBlock;
	public static Block Laser;
	public static Block Keypad;
	public static Block LaserActive;
	public static BlockMine Mine;
	public static BlockMine MineCut;
	public static Block DirtMine;
	public static Block StoneMine;
	public static Block CobblestoneMine;
	public static Block SandMine;
	public static Block DiamondOreMine;
	public static Block FurnaceMine;
	public static Block retinalScanner;
    public static Block doorIndestructableIron;
    public static Block bogusLava;
    public static Block bogusLavaFlowing;
    public static Block bogusWater;
    public static Block bogusWaterFlowing;
    public static Block keycardReader;
    public static Block ironTrapdoor;
    public static BlockMine bouncingBetty;
    //public static Block doorbell;
    public static Block inventoryScanner;
    public static Block inventoryScannerField;
	public static Block trackMine;
	public static Block cageTrap;
	public static Block portableRadar;
	public static Block deactivatedCageTrap;
	public static Block unbreakableIronBars;
	public static Block securityCamera;
	public static Block empEntity;
	public static Block usernameLogger;
	public static Block keypadChest;
	public static Block reinforcedGlass;
	public static Block alarm;
	public static Block alarmLit;
	public static Block reinforcedStone;
	public static Block reinforcedFencegate;
	public static Block reinforcedWoodPlanks;

	public static BlockEMPedWire empedWire;
	
    //Items
    public static Item Codebreaker;
    public static Item doorIndestructableIronItem;
    public static Item universalBlockRemover;
    public static Item keycards;
    public static Item remoteAccessMine;
    public static Item fWaterBucket;
    public static Item fLavaBucket;
    public static Item universalBlockModifier;
	public static Item wireCutters;
    
    //Modules
    public static ItemModule redstoneModule;
    public static ItemModule whitelistModule;
    public static ItemModule blacklistModule;
    public static ItemModule harmingModule;
    public static ItemModule smartModule;

    public static Item testItem;
    
    //public static Item testChestplate;
    


    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event){
    	event.registerServerCommand(new CommandSCHelp());
    	event.registerServerCommand(new CommandModule());
    	if(this.debuggingMode){
    		event.registerServerCommand(new CommandSCLog());
    	}
    }
   
    @EventHandler
	public void preInit(FMLPreInitializationEvent event){  	
    	log("Starting to load....");
    	log("Loading config file....");
    	log(mod_SecurityCraft.VERSION + " of SecurityCraft is for a post MC-1.6.4 version! Configuration files are useless for setting anything besides options.");
		mod_SecurityCraft.configFile = new Configuration(event.getSuggestedConfigurationFile());
    	this.configHandler.setupConfiguration();
		log("Config file loaded.");
		log("Setting up handlers!");
		this.configHandler.setupHandlers(event);
		log("Handlers registered.");
		log("Setting up network....");
		mod_SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(mod_SecurityCraft.MODID);
		this.configHandler.setupPackets(mod_SecurityCraft.network);
		log("Network setup.");
		log("Loading mod additions...");
		this.configHandler.setupAdditions();
		
		if(this.debuggingMode){
			this.configHandler.setupDebugAdditions();
		}
		
		log("Finished loading mod additions.");
		log("Doing registering stuff... (PT 1/2)");
		this.configHandler.setupGameRegistry();
		
		ModMetadata modMeta = event.getModMetadata();
        modMeta.authorList = Arrays.asList(new String[] {
            "Geforce"
        });
        modMeta.autogenerated = false;
        modMeta.credits = "Thanks to all of you guys for your support!";
        modMeta.description = "Adds a load of things to keep your house safe with.\nIf you like this mod, hit the green arrow\nin the corner of the forum thread!\nPlease visit the URL above for help. \n \nMessage of the update: \n" + MOTU;
        modMeta.url = "http://geforce.freeforums.org";
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		
		log("Doing registering stuff... (PT 2/2)");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler);

		this.configHandler.setupEntityRegistry();
		serverProxy.registerRenderThings();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(mod_SecurityCraft.eventHandler);
		log("Mod finished loading correctly! :D");
	}
	
	public static void log(String par1){
		log(par1, false);
	}
	
	public static void log(String par1, boolean isSevereError){
		if(mod_SecurityCraft.debuggingMode){
			System.out.println(isSevereError ? "{SecurityCraft} {" + FMLCommonHandler.instance().getEffectiveSide() + "} {Severe}: " + par1 : "[SecurityCraft] [" + FMLCommonHandler.instance().getEffectiveSide() + "] " + par1);
		}
	}
	
	public static String getVersion(){
		return VERSION;
	}
	
	public SCIRCBot getIrcBot() {
		return ircBot;
	}

	public void setIrcBot(SCIRCBot ircBot) {
		this.ircBot = ircBot;
	}

	public NBTTagCompound getSavedModule() {
		return savedModule;
	}

	public void setSavedModule(NBTTagCompound savedModule) {
		this.savedModule = savedModule;
	}
	
}
