package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.gui.GuiBlockReinforcer;
import net.geforcemods.securitycraft.gui.GuiBriefcase;
import net.geforcemods.securitycraft.gui.GuiBriefcaseInventory;
import net.geforcemods.securitycraft.gui.GuiBriefcaseSetup;
import net.geforcemods.securitycraft.gui.GuiCheckPassword;
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.geforcemods.securitycraft.gui.GuiDisguiseModule;
import net.geforcemods.securitycraft.gui.GuiIMS;
import net.geforcemods.securitycraft.gui.GuiInventoryScanner;
import net.geforcemods.securitycraft.gui.GuiKeyChanger;
import net.geforcemods.securitycraft.gui.GuiKeycardSetup;
import net.geforcemods.securitycraft.gui.GuiKeypadFurnaceInventory;
import net.geforcemods.securitycraft.gui.GuiLogger;
import net.geforcemods.securitycraft.gui.GuiSetPassword;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderBullet;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.RenderSentry;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class ClientProxy implements IProxy {
	private final List<Block> toTint = new ArrayList<>();

	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, manager -> new RenderBouncingBetty(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, manager -> new RenderIMSBomb(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, manager -> new RenderSentry(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, manager -> new RenderBullet(manager));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());
	}

	@Override
	public void registerScreens()
	{
		ScreenManager.registerFactory(SCContent.cTypeBlockReinforcer, GuiBlockReinforcer::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcase, GuiBriefcase::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcaseInventory, GuiBriefcaseInventory::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcaseSetup, GuiBriefcaseSetup::new);
		ScreenManager.registerFactory(SCContent.cTypeCustomizeBlock, GuiCustomizeBlock::new);
		ScreenManager.registerFactory(SCContent.cTypeDisguiseModule, GuiDisguiseModule::new);
		ScreenManager.registerFactory(SCContent.cTypeInventoryScanner, GuiInventoryScanner::new);
		ScreenManager.registerFactory(SCContent.cTypeKeypadFurnace, GuiKeypadFurnaceInventory::new);
		ScreenManager.registerFactory(SCContent.cTypeCheckPassword, GuiCheckPassword::new);
		ScreenManager.registerFactory(SCContent.cTypeSetPassword, GuiSetPassword::new);
		ScreenManager.registerFactory(SCContent.cTypeUsernameLogger, GuiLogger::new);
		ScreenManager.registerFactory(SCContent.cTypeIMS, GuiIMS::new);
		ScreenManager.registerFactory(SCContent.cTypeKeycardSetup, GuiKeycardSetup::new);
		ScreenManager.registerFactory(SCContent.cTypeKeyChanger, GuiKeyChanger::new);
	}

	@Override
	public void registerKeybindings()
	{
		KeyBindings.init();
	}

	@Override
	public void registerKeypadChestItem(Register<Item> event)
	{
		event.getRegistry().register(new BlockItem(SCContent.keypadChest, new Item.Properties().group(SecurityCraft.groupSCTechnical).setTEISR(() -> () -> new ItemKeypadChestRenderer())).setRegistryName(SCContent.keypadChest.getRegistryName()));
	}

	@Override
	public List<Block> getOrPopulateToTint()
	{
		if(toTint.isEmpty())
		{
			for(Field field : SCContent.class.getFields())
			{
				if(field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).hasTint())
				{
					try
					{
						toTint.add((Block)field.get(null));
					}
					catch(IllegalArgumentException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return toTint;
	}

	@Override
	public void cleanup()
	{
		toTint.clear();
	}

	@Override
	public World getClientWorld()
	{
		return Minecraft.getInstance().world;
	}

	@Override
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
}
