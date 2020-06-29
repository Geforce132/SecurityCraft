package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.top.TOPDataProvider;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.itemgroups.SCDecorationGroup;
import net.geforcemods.securitycraft.itemgroups.SCExplosivesGroup;
import net.geforcemods.securitycraft.itemgroups.SCTechnicalGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.network.ServerProxy;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(SecurityCraft.MODID)
@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	//********************************* This is v1.8.19 for MC 1.16.1!
	protected static final String VERSION = "v1.8.19";
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	public static SecurityCraft instance;
	public static final String PROTOCOL_VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public ArrayList<SCManualPage> manualPages = new ArrayList<>();
	public static ItemGroup groupSCTechnical = new SCTechnicalGroup();
	public static ItemGroup groupSCMine = new SCExplosivesGroup();
	public static ItemGroup groupSCDecoration = new SCDecorationGroup();

	public SecurityCraft()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		instance = this;
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);
		SCContent.BLOCKS.register(modEventBus);
		SCContent.FLUIDS.register(modEventBus);
		SCContent.ITEMS.register(modEventBus);

		if(ModList.get().isLoaded("cyclic"))
			MinecraftForge.EVENT_BUS.addListener(CyclicCompat::onRightClickBlock);
	}

	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event) //stage 1
	{
		RegistrationHandler.registerPackets();
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
					Block block = ((RegistryObject<Block>)field.get(null)).get();
					IReinforcedBlock rb = (IReinforcedBlock)block;
					IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(rb.getVanillaBlock(), block);
					IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(block, rb.getVanillaBlock());
				}

				if(field.isAnnotationPresent(HasManualPage.class))
				{
					Object o = ((RegistryObject<?>)field.get(null)).get();
					HasManualPage hmp = field.getAnnotation(HasManualPage.class);
					boolean isBlock = true;
					Item item;
					String key;

					if(o instanceof Block)
						item = ((Block)o).asItem();
					else
					{
						item = (Item)o;
						isBlock = false;
					}

					if(hmp.specialInfoKey().isEmpty())
						key = (isBlock ? "help" : "help.") + item.getTranslationKey().substring(5) + ".info";
					else
						key = hmp.specialInfoKey();

					SCManualPage page = new SCManualPage(item, key);

					if(!hmp.designedBy().isEmpty())
						page.setDesignedBy(hmp.designedBy());

					instance.manualPages.add(page);
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

	public static String getVersion() {
		return VERSION;
	}
}
