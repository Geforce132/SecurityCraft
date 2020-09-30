package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.commands.SCCommand;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(SecurityCraft.MODID)
@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	//********************************* This is v1.8.19.3 for MC 1.16.2!
	public static final String VERSION = "v1.8.19.3";
	public static IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	public static SecurityCraft instance;
	public static final String PROTOCOL_VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public ArrayList<SCManualPage> manualPages = new ArrayList<>();
	public static ItemGroup groupSCTechnical = new SCTechnicalGroup();
	public static ItemGroup groupSCMine = new SCExplosivesGroup();
	public static ItemGroup groupSCDecoration = new SCDecorationGroup();
	private static List<IExtractionBlock> registeredExtractionBlocks = new ArrayList<>();
	public static final String IMC_EXTRACTION_BLOCK_MSG = "registerExtractionBlock";

	public SecurityCraft()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		instance = this;
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);
		SCContent.BLOCKS.register(modEventBus);
		SCContent.FLUIDS.register(modEventBus);
		SCContent.ITEMS.register(modEventBus);
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

		InterModComms.sendTo(MODID, IMC_EXTRACTION_BLOCK_MSG, ReinforcedHopperBlock.ExtractionBlock::new);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			CompoundNBT vcUpdateTag = VersionUpdateChecker.getCompoundNBT();

			if(vcUpdateTag != null)
				InterModComms.sendTo("versionchecker", "addUpdate", () -> vcUpdateTag);
		});
		proxy.tint();
	}

	@SubscribeEvent
	public static void onInterModProcess(InterModProcessEvent event){ //stage 4
		event.getIMCStream(s -> s.equals(IMC_EXTRACTION_BLOCK_MSG)).forEach(msg -> registeredExtractionBlocks.add((IExtractionBlock)msg.getMessageSupplier().get()));

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

					SCManualPage page = new SCManualPage(item, new TranslationTextComponent(key));

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

	public void registerCommands(RegisterCommandsEvent event){
		SCCommand.register(event.getDispatcher());
	}

	public static List<IExtractionBlock> getRegisteredExtractionBlocks()
	{
		return registeredExtractionBlocks;
	}
}
