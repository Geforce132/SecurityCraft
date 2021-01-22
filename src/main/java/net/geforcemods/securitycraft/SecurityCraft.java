package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import net.geforcemods.securitycraft.api.IAttackTargetCheck;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedHopper;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.icbmclassic.ICBMClassicEMPCompat;
import net.geforcemods.securitycraft.compat.lycanitesmobs.LycanitesMobsCompat;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.SCManualPage;
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
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
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
	public static SCEventHandler eventHandler = new SCEventHandler();
	private GuiHandler guiHandler = new GuiHandler();
	public ArrayList<SCManualPage> manualPages = new ArrayList<>();
	public static CreativeTabs tabSCTechnical = new CreativeTabSCTechnical();
	public static CreativeTabs tabSCMine = new CreativeTabSCExplosives();
	public static CreativeTabs tabSCDecoration = new CreativeTabSCDecoration();
	private static List<IExtractionBlock> registeredExtractionBlocks = new ArrayList<>();
	private static List<IAttackTargetCheck> registeredSentryAttackTargetChecks = new ArrayList<>();
	private static List<IPasswordConvertible> registeredPasswordConvertibles = new ArrayList<>();
	public static final String IMC_EXTRACTION_BLOCK_MSG = "registerExtractionBlock";
	public static final String IMC_SENTRY_ATTACK_TARGET_MSG = "registerSentryAttackTargetCheck";
	public static final String IMC_PASSWORD_CONVERTIBLE_MSG = "registerPasswordConvertible";

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
		FMLInterModComms.sendFunctionMessage(MODID, IMC_EXTRACTION_BLOCK_MSG, BlockReinforcedHopper.ExtractionBlock.class.getName());
		FMLInterModComms.sendFunctionMessage(MODID, IMC_PASSWORD_CONVERTIBLE_MSG, BlockKeypad.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(MODID, IMC_PASSWORD_CONVERTIBLE_MSG, BlockKeypadChest.Convertible.class.getName());
		FMLInterModComms.sendFunctionMessage(MODID, IMC_PASSWORD_CONVERTIBLE_MSG, BlockKeypadFurnace.Convertible.class.getName());

		if(Loader.isModLoaded("lycanitesmobs"))
			FMLInterModComms.sendFunctionMessage(MODID, IMC_SENTRY_ATTACK_TARGET_MSG, LycanitesMobsCompat.class.getName());

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
		for(IMCMessage msg : event.getMessages())
		{
			if(msg.key.equals(IMC_EXTRACTION_BLOCK_MSG))
			{
				Optional<Function<Object,IExtractionBlock>> value = msg.getFunctionValue(Object.class, IExtractionBlock.class);

				if(value.isPresent())
					registeredExtractionBlocks.add(value.get().apply(null));
				else
					System.out.println(String.format("[ERROR] Mod %s did not supply sufficient extraction block information.", msg.getSender()));
			}
			else if(msg.key.equals(IMC_SENTRY_ATTACK_TARGET_MSG))
			{
				Optional<Function<Object,IAttackTargetCheck>> value = msg.getFunctionValue(Object.class, IAttackTargetCheck.class);

				if(value.isPresent())
					registeredSentryAttackTargetChecks.add(value.get().apply(null));
				else
					System.out.println(String.format("[ERROR] Mod %s did not supply sufficient sentry attack target information.", msg.getSender()));
			}
			else if(msg.key.equals(IMC_PASSWORD_CONVERTIBLE_MSG))
			{
				Optional<Function<Object,IPasswordConvertible>> value = msg.getFunctionValue(Object.class, IPasswordConvertible.class);

				if(value.isPresent())
					registeredPasswordConvertibles.add(value.get().apply(null));
				else
					System.out.println(String.format("[ERROR] Mod %s did not supply sufficient password convertible information.", msg.getSender()));
			}
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(SecurityCraft.eventHandler);

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

	public static List<IExtractionBlock> getRegisteredExtractionBlocks()
	{
		return registeredExtractionBlocks;
	}

	public static List<IAttackTargetCheck> getRegisteredSentryAttackTargetChecks()
	{
		return registeredSentryAttackTargetChecks;
	}

	public static List<IPasswordConvertible> getRegisteredPasswordConvertibles()
	{
		return registeredPasswordConvertibles;
	}

	public static String getVersion()
	{
		return Loader.instance().activeModContainer().getVersion();
	}
}
