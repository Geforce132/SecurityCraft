package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.gui.GuiBlockPocketManager;
import net.geforcemods.securitycraft.gui.GuiBlockReinforcer;
import net.geforcemods.securitycraft.gui.GuiBriefcase;
import net.geforcemods.securitycraft.gui.GuiBriefcaseInventory;
import net.geforcemods.securitycraft.gui.GuiBriefcaseSetup;
import net.geforcemods.securitycraft.gui.GuiCameraMonitor;
import net.geforcemods.securitycraft.gui.GuiCheckPassword;
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.geforcemods.securitycraft.gui.GuiDisguiseModule;
import net.geforcemods.securitycraft.gui.GuiEditModule;
import net.geforcemods.securitycraft.gui.GuiEditSecretSign;
import net.geforcemods.securitycraft.gui.GuiIMS;
import net.geforcemods.securitycraft.gui.GuiInventoryScanner;
import net.geforcemods.securitycraft.gui.GuiKeyChanger;
import net.geforcemods.securitycraft.gui.GuiKeycardSetup;
import net.geforcemods.securitycraft.gui.GuiKeypadFurnaceInventory;
import net.geforcemods.securitycraft.gui.GuiLogger;
import net.geforcemods.securitycraft.gui.GuiMRAT;
import net.geforcemods.securitycraft.gui.GuiSCManual;
import net.geforcemods.securitycraft.gui.GuiSetPassword;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderBullet;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.RenderSentry;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityTrophySystemRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class ClientProxy implements IProxy {
	@Override
	public void clientSetup()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, manager -> new RenderBouncingBetty(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, manager -> new RenderIMSBomb(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, manager -> new RenderSentry(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, manager -> new RenderBullet(manager));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophySystem.class, new TileEntityTrophySystemRenderer());
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
		ScreenManager.registerFactory(SCContent.cTypeBlockPocketManager, GuiBlockPocketManager::new);
		KeyBindings.init();
	}

	@Override
	public void registerKeypadChestItem(Register<Item> event)
	{
		event.getRegistry().register(new BlockItem(SCContent.keypadChest, new Item.Properties().group(SecurityCraft.groupSCTechnical).setTEISR(() -> () -> new ItemKeypadChestRenderer())).setRegistryName(SCContent.keypadChest.getRegistryName()));
	}

	@Override
	public void tint()
	{
		Map<Block,Integer> toTint = new HashMap<>();

		for(Field field : SCContent.class.getFields())
		{
			if(field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).hasTint())
			{
				try
				{
					toTint.put((Block)field.get(null), field.getAnnotation(Reinforced.class).tint());
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		toTint.put(SCContent.blockPocketManager, 0x0E7063);
		toTint.put(SCContent.blockPocketWall, 0x0E7063);
		toTint.put(SCContent.chiseledCrystalQuartz, 0x15b3a2);
		toTint.put(SCContent.crystalQuartz, 0x15b3a2);
		toTint.put(SCContent.crystalQuartzPillar, 0x15b3a2);
		toTint.put(SCContent.crystalQuartzSlab, 0x15b3a2);
		toTint.put(SCContent.stairsCrystalQuartz, 0x15b3a2);
		toTint.forEach((block, tint) -> Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> tint, block));
		toTint.forEach((item, tint) -> Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> tint, item));
		Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			Block block = Block.getBlockFromItem(BlockKeypad.getDisguisedStack(world, pos).getItem());

			if(block != Blocks.AIR && !(block instanceof BlockKeypad))
				return Minecraft.getInstance().getBlockColors().getColor(block.getDefaultState(), world, pos, tintIndex);
			else return 0xFFFFFF;
		}, SCContent.keypad);
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

	@Override
	public void displayMRATGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new GuiMRAT(stack));
	}

	@Override
	public void displayEditModuleGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new GuiEditModule(stack));
	}

	@Override
	public void displayCameraMonitorGui(PlayerInventory inv, ItemCameraMonitor item, CompoundNBT stackTag)
	{
		Minecraft.getInstance().displayGuiScreen(new GuiCameraMonitor(inv, item, stackTag));
	}

	@Override
	public void displaySCManualGui()
	{
		Minecraft.getInstance().displayGuiScreen(new GuiSCManual());
	}

	@Override
	public void displayEditSecretSignGui(TileEntitySecretSign te)
	{
		Minecraft.getInstance().displayGuiScreen(new GuiEditSecretSign(te));
	}
}
