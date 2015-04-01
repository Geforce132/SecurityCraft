package org.freeforums.geforce.securitycraft.main;

import java.util.Arrays;

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
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.commands.CommandModule;
import org.freeforums.geforce.securitycraft.commands.CommandSCHelp;
import org.freeforums.geforce.securitycraft.commands.CommandSCLog;
import org.freeforums.geforce.securitycraft.gui.GuiHandler;
import org.freeforums.geforce.securitycraft.handlers.ForgeEventHandler;
import org.freeforums.geforce.securitycraft.ircbot.SCIRCBot;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.network.ConfigurationHandler;
import org.freeforums.geforce.securitycraft.network.ServerProxy;

@Mod(modid = mod_SecurityCraft.MODID, name = "SecurityCraft", version = mod_SecurityCraft.VERSION, guiFactory = "org.freeforums.geforce.securitycraft.gui.SecurityCraftGuiFactory", dependencies = mod_SecurityCraft.FORGEVERSION)
@SuppressWarnings({"static-access"})
public class mod_SecurityCraft {
	
	public static boolean debuggingMode;
	
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Thanks for all your suggestions!";
    
	//TODO UPDATE 'RECIPES' and 'HELP' ArrayList's.
	//TODO ********************************* This is v1.7.4 for MC 1.8!
	protected static final String VERSION = "v1.7.4";
	protected static final String FORGEVERSION = "required-after:Forge@[11.14.0.1252,)";
	
	
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
	public static Block keypad;
	public static Block keypadFrame;
	public static Block LaserActive;
	public static Block Mine;
	public static Block DirtMine;
	public static Block StoneMine;
	public static Block CobblestoneMine;
	public static Block SandMine;
	public static Block DiamondOreMine;
	public static Block FurnaceMine;
	public static Block retinalScanner;
    public static Block doorIndestructableIron;
    public static BlockStaticLiquid bogusLava;
    public static Block bogusLavaFlowing;
    public static BlockStaticLiquid bogusWater;
    public static Block bogusWaterFlowing;
    public static Block keycardReader;
    public static Block ironTrapdoor;
    public static Block bouncingBetty;
    public static Block inventoryScanner;
    public static Block inventoryScannerField;
	public static Block trackMine;
	public static Block cageTrap;
	public static Block portableRadar;
	public static Block deactivatedCageTrap;
	public static Block unbreakableIronBars;
	public static Block securityCamera;
	public static Block usernameLogger;
	public static Block keypadChest;
	public static Block reinforcedGlass;
	public static Block alarm;
	public static Block alarmLit;
	public static Block reinforcedStone;
	public static Block reinforcedFencegate;
	public static Block reinforcedPlanks_Oak;
	public static Block reinforcedPlanks_Spruce;
	public static Block reinforcedPlanks_Birch;
	public static Block reinforcedPlanks_Jungle;
	public static Block reinforcedPlanks_Acadia;
	public static Block reinforcedPlanks_DarkOak;
	public static Block keypadFurnace;
	public static Block panicButton;
	public static Block claymore;
	
    //Items
    public static Item Codebreaker;
    public static Item doorIndestructableIronItem;
    public static Item universalBlockRemover;
    public static Item keycardLV1;
    public static Item keycardLV2;
    public static Item keycardLV3;
    public static Item keycardLV4;
    public static Item keycardLV5;
    public static Item limitedUseKeycard;
    public static Item remoteAccessMine;
    public static Item fWaterBucket;
    public static Item fLavaBucket;
    public static Item universalBlockModifier;
    public static Item wireCutters;
    public static Item keypadItem;
    public static Item adminTool;

    //Modules
    public static ItemModule redstoneModule;
    public static ItemModule whitelistModule;
    public static ItemModule blacklistModule;
    public static ItemModule harmingModule;
    public static ItemModule smartModule;
    
    public static Item testItem;
    public static Item cameraMonitor;
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
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler);

		ModMetadata modMeta = event.getModMetadata();
        modMeta.authorList = Arrays.asList(new String[] {
            "Geforce"
        });
        modMeta.autogenerated = false;
        modMeta.credits = "Thanks to all of you guys for your support!";
        modMeta.description = "Adds a load of things to keep your house safe with.\nIf you like this mod, hit the green arrow\nin the corner of the forum thread!\nPlease visit the URL above for help. \n \nMessage of the update: \n" + MOTU;
        modMeta.url = "http://www.github.com/Geforce132/SecurityCraft";
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void init(FMLInitializationEvent event){
		this.configHandler.setupTextureRegistry();
		
		log("Doing registering stuff... (PT 2/2)");
		
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
