package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.top.TOPDataProvider;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.itemgroups.SCDecorationGroup;
import net.geforcemods.securitycraft.itemgroups.SCExplosivesGroup;
import net.geforcemods.securitycraft.itemgroups.SCTechnicalGroup;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
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
	//********************************* This is v1.8.16 for MC 1.15.2!
	protected static final String VERSION = "v1.8.16";
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	public static SecurityCraft instance;
	public static final String PROTOCOL_VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public HashMap<String, Object[]> cameraUsePositions = new HashMap<>();
	public ArrayList<SCManualPage> manualPages = new ArrayList<>();
	private CompoundNBT savedModule;
	public static ItemGroup groupSCTechnical = new SCTechnicalGroup();
	public static ItemGroup groupSCMine = new SCExplosivesGroup();
	public static ItemGroup groupSCDecoration = new SCDecorationGroup();

	public SecurityCraft()
	{
		instance = this;
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		if(ModList.get().isLoaded("cyclic"))
			MinecraftForge.EVENT_BUS.addListener(CyclicCompat::onRightClickBlock);
	}

	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event) //stage 1
	{
		RegistrationHandler.registerPackets();
	}

	//stage 2 is FMLClientSetupEvent/FMLDedicatedServerSetupEvent
	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		proxy.clientSetup();
	}

	@SubscribeEvent
	public static void onInterModEnqueue(InterModEnqueueEvent event){ //stage 3
		if(ModList.get().isLoaded("theoneprobe")) //fix crash without top installed
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPDataProvider::new);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			CompoundNBT vcUpdateTag = VersionUpdateChecker.getCompoundNBT();

			if(vcUpdateTag != null)
				InterModComms.sendTo("versionchecker", "addUpdate", () -> vcUpdateTag);
		});

		CustomModules.refresh();
		proxy.tint();
	}

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event){ //stage 4
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
				{
					Block block = (Block)field.get(null);
					IReinforcedBlock rb = (IReinforcedBlock)block;
					IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(rb.getVanillaBlock(), block);
					IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(block, rb.getVanillaBlock());
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void serverStarting(FMLServerStartingEvent event){
		SCCommand.register(event.getCommandDispatcher());
	}

	public Object[] getUsePosition(String playerName) {
		return cameraUsePositions.get(playerName);
	}

	public boolean hasUsePosition(String playerName) {
		return cameraUsePositions.containsKey(playerName);
	}

	public CompoundNBT getSavedModule() {
		return savedModule;
	}

	public void setSavedModule(CompoundNBT savedModule) {
		this.savedModule = savedModule;
	}

	public static String getVersion() {
		return VERSION;
	}
}
