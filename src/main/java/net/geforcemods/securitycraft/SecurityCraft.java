package net.geforcemods.securitycraft;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.icbmclassic.ICBMClassicEMPCompat;
import net.geforcemods.securitycraft.compat.lycanitesmobs.LycanitesMobsCompat;
import net.geforcemods.securitycraft.compat.quark.QuarkCompat;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.tabs.CreativeTabSCDecoration;
import net.geforcemods.securitycraft.tabs.CreativeTabSCExplosives;
import net.geforcemods.securitycraft.tabs.CreativeTabSCTechnical;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = SecurityCraft.MODID, name = "SecurityCraft", dependencies = "required-after:forge@[14.23.5.2826,)", updateJSON = "https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/Forge.json", acceptedMinecraftVersions = "[1.12.2]")
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static IProxy proxy;
	@Instance(MODID)
	public static SecurityCraft instance = new SecurityCraft();
	public static SimpleNetworkWrapper network;
	private GuiHandler guiHandler = new GuiHandler();
	public static CreativeTabs tabSCTechnical = new CreativeTabSCTechnical();
	public static CreativeTabs tabSCMine = new CreativeTabSCExplosives();
	public static CreativeTabs tabSCDecoration = new CreativeTabSCDecoration();

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandSC());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(SecurityCraft.MODID);
		RegistrationHandler.registerPackets(SecurityCraft.network);
		SetupHandler.setupBlocks();
		SetupHandler.setupMines();
		SetupHandler.setupItems();
		proxy.registerEntityRenderingHandlers();

		if(Loader.isModLoaded("icbmclassic"))
			MinecraftForge.EVENT_BUS.register(new ICBMClassicEMPCompat());
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		FMLInterModComms.sendMessage("waila", "register", "net.geforcemods.securitycraft.compat.waila.WailaDataProvider.callbackRegister");
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "net.geforcemods.securitycraft.compat.top.TOPDataProvider");
		SecurityCraftAPI.init();

		if(Loader.isModLoaded("lycanitesmobs"))
			FMLInterModComms.sendFunctionMessage(MODID, SecurityCraftAPI.IMC_SENTRY_ATTACK_TARGET_MSG, LycanitesMobsCompat.class.getName());

		if(Loader.isModLoaded("quark"))
			QuarkCompat.registerChestConversion();

		if(ConfigHandler.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();
			if(vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		EnumModuleType.refresh();
		proxy.registerRenderThings();
		FMLCommonHandler.instance().getDataFixer().init(SecurityCraft.MODID, TileEntityIDDataFixer.VERSION).registerFix(FixTypes.BLOCK_ENTITY, new TileEntityIDDataFixer());
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedCobblestone), new ItemStack(SCContent.reinforcedStone, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedSand, 1, 0), new ItemStack(SCContent.reinforcedGlass, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedSand, 1, 1), new ItemStack(SCContent.reinforcedGlass, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStoneBrick, 1, 0), new ItemStack(SCContent.reinforcedStoneBrick, 1, 2), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedClay, 1, 0), new ItemStack(SCContent.reinforcedHardenedClay, 1, 0), 0.35F);
	}

	@EventHandler
	public void onIMC(IMCEvent event)
	{
		SecurityCraftAPI.onIMC(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){

		if(Loader.isModLoaded("cyclicmagic"))
			MinecraftForge.EVENT_BUS.register(new CyclicCompat());

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
	}

	public static String getVersion()
	{
		return Loader.instance().activeModContainer().getVersion();
	}
}
