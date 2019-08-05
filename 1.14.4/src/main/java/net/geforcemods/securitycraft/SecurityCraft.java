package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.CommandModule;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.top.TOPDataProvider;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCDecoration;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCExplosives;
import net.geforcemods.securitycraft.itemgroups.ItemGroupSCTechnical;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeycardConditions;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(SecurityCraft.MODID)
@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	//********************************* This is v1.8.12.1 for MC 1.14.4!
	protected static final String VERSION = "v1.8.12.1";
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	public static SecurityCraft instance;
	public static final String PROTOCOL_VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public HashMap<String, Object[]> cameraUsePositions = new HashMap<String, Object[]>();
	public ArrayList<SCManualPage> manualPages = new ArrayList<SCManualPage>();
	private CompoundNBT savedModule;
	public static ItemGroup groupSCTechnical = new ItemGroupSCTechnical();
	public static ItemGroup groupSCMine = new ItemGroupSCExplosives();
	public static ItemGroup groupSCDecoration = new ItemGroupSCDecoration();

	public SecurityCraft()
	{
		instance = this;
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC);
	}

	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event) //stage 1
	{
		log("Regisering mod content... (PT 1/2)");
		RegistrationHandler.registerPackets();
		KeycardConditions.registerAll();
	}

	//stage 2 is FMLClientSetupEvent/FMLDedicatedServerSetupEvent
	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		proxy.clientSetup();
	}

	@SubscribeEvent
	public static void onInterModEnqueue(InterModEnqueueEvent event){ //stage 3
		log("Setting up inter-mod stuff...");
		if(ModList.get().isLoaded("theoneprobe")) //fix crash without top installed
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPDataProvider::new);

		//		if(ClientConfig.CONFIG.checkForUpdates.get()) {
		//			CompoundNBT vcUpdateTag = VersionUpdateChecker.getCompoundNBT();
		//			if(vcUpdateTag != null)
		//				InterModComms.sendTo("VersionChecker", "addUpdate", () -> vcUpdateTag);
		//		}

		log("Registering mod content... (PT 2/2)");
		EnumCustomModules.refresh();
		proxy.registerKeybindings();
		proxy.getOrPopulateToTint().forEach(block -> Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> 0x999999, block));
		proxy.getOrPopulateToTint().forEach(item -> Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> 0x999999, item));
		proxy.cleanup();
	}

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event){ //stage 4
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

		log("Mod finished loading correctly! :D");
	}

	public void serverStarting(FMLServerStartingEvent event){
		CommandSC.register(event.getCommandDispatcher());
		CommandModule.register(event.getCommandDispatcher());
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

	public CompoundNBT getSavedModule() {
		return savedModule;
	}

	public void setSavedModule(CompoundNBT savedModule) {
		this.savedModule = savedModule;
	}

	/**
	 * Prints a String to the console. Only will print if SecurityCraft is in debug mode.
	 */
	public static void log(String line) {
		log(line, false);
	}

	public static void log(String line, boolean isSevereError) {
		//TODO: find out how to not call this too early (java.lang.NullPointerException: Cannot get config value without assigned Config object present)
		//		if(ServerConfig.CONFIG.debug.get())
		//			System.out.println(isSevereError ? "{SecurityCraft} {" + EffectiveSide.get() + "} {Severe}: " + line : "[SecurityCraft] [" + EffectiveSide.get() + "] " + line);
	}

	public static String getVersion() {
		return VERSION;
	}
}
