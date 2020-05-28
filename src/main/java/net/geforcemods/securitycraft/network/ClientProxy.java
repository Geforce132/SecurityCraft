package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemTileEntityRenderer;
import net.geforcemods.securitycraft.screen.BlockPocketManagerScreen;
import net.geforcemods.securitycraft.screen.BlockReinforcerScreen;
import net.geforcemods.securitycraft.screen.BriefcaseInventoryScreen;
import net.geforcemods.securitycraft.screen.BriefcasePasswordScreen;
import net.geforcemods.securitycraft.screen.BriefcaseSetupScreen;
import net.geforcemods.securitycraft.screen.CameraMonitorScreen;
import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.geforcemods.securitycraft.screen.DisguiseModuleScreen;
import net.geforcemods.securitycraft.screen.EditModuleScreen;
import net.geforcemods.securitycraft.screen.EditSecretSignScreen;
import net.geforcemods.securitycraft.screen.IMSScreen;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.geforcemods.securitycraft.screen.KeyChangerScreen;
import net.geforcemods.securitycraft.screen.KeycardReaderSetupScreen;
import net.geforcemods.securitycraft.screen.KeypadFurnaceScreen;
import net.geforcemods.securitycraft.screen.MineRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SCManualScreen;
import net.geforcemods.securitycraft.screen.SentryRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.geforcemods.securitycraft.screen.UsernameLoggerScreen;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT, bus=Bus.MOD)
public class ClientProxy implements IProxy {
	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event)
	{
		String[] facings = {"east", "north", "south", "west"};
		String[] bools = {"true", "false"};
		ResourceLocation[] facingPoweredBlocks = {
				new ResourceLocation(SecurityCraft.MODID, "keycard_reader"),
				new ResourceLocation(SecurityCraft.MODID, "keypad"),
				new ResourceLocation(SecurityCraft.MODID, "retinal_scanner")
		};
		ResourceLocation[] facingBlocks = {
				new ResourceLocation(SecurityCraft.MODID, "inventory_scanner"),
				new ResourceLocation(SecurityCraft.MODID, "username_logger")
		};
		ResourceLocation[] poweredBlocks = {
				new ResourceLocation(SecurityCraft.MODID, "laser_block")
		};

		for(String facing : facings)
		{
			for(String bool : bools)
			{
				for(ResourceLocation facingPoweredBlock : facingPoweredBlocks)
				{
					registerDisguisedModel(event, facingPoweredBlock, "facing=" + facing + ",powered=" + bool);
				}
			}

			for(ResourceLocation facingBlock : facingBlocks)
			{
				registerDisguisedModel(event, facingBlock, "facing=" + facing);
			}
		}

		for(String bool : bools)
		{
			for(ResourceLocation poweredBlock : poweredBlocks)
			{
				registerDisguisedModel(event, poweredBlock, "powered=" + bool);
			}
		}

		ResourceLocation cageTrapRl = new ResourceLocation(SecurityCraft.MODID, "cage_trap");

		registerDisguisedModel(event, cageTrapRl, "deactivated=true");
		registerDisguisedModel(event, cageTrapRl, "deactivated=false");
	}

	private static void registerDisguisedModel(ModelBakeEvent event, ResourceLocation rl, String stateString)
	{
		ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

		event.getModelRegistry().put(mrl, new DisguisableDynamicBakedModel(rl, event.getModelRegistry().get(mrl)));
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(BouncingBettyEntity.class, BouncingBettyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(IMSBombEntity.class, IMSBombRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SentryEntity.class, SentryRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(BulletEntity.class, BulletRenderer::new);
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadChestTileEntity.class, new KeypadChestTileEntityRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(RetinalScannerTileEntity.class, new RetinalScannerTileEntityRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(SecurityCameraTileEntity.class, new SecurityCameraTileEntityRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(SecretSignTileEntity.class, new SecretSignTileEntityRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TrophySystemTileEntity.class, new TrophySystemTileEntityRenderer());
		ScreenManager.registerFactory(SCContent.cTypeBlockReinforcer, BlockReinforcerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcase, BriefcasePasswordScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcaseInventory, BriefcaseInventoryScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcaseSetup, BriefcaseSetupScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeCustomizeBlock, CustomizeBlockScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeDisguiseModule, DisguiseModuleScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeInventoryScanner, InventoryScannerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeKeypadFurnace, KeypadFurnaceScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeCheckPassword, CheckPasswordScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeSetPassword, SetPasswordScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeUsernameLogger, UsernameLoggerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeIMS, IMSScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeKeycardSetup, KeycardReaderSetupScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeKeyChanger, KeyChangerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBlockPocketManager, BlockPocketManagerScreen::new);
		KeyBindings.init();
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
					toTint.put(((RegistryObject<Block>)field.get(null)).get(), field.getAnnotation(Reinforced.class).tint());
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		boolean tintBlocks = ConfigHandler.CONFIG.reinforcedBlockTint.get();
		int noTint = 0xFFFFFF;
		int crystalQuartzTint = 0x15B3A2;
		int reinforcedCrystalQuartzTint = 0x0E7063;

		toTint.put(SCContent.BLOCK_POCKET_MANAGER.get(), reinforcedCrystalQuartzTint);
		toTint.put(SCContent.BLOCK_POCKET_WALL.get(), reinforcedCrystalQuartzTint);
		toTint.put(SCContent.CHISELED_CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		toTint.put(SCContent.CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		toTint.put(SCContent.CRYSTAL_QUARTZ_PILLAR.get(), crystalQuartzTint);
		toTint.put(SCContent.CRYSTAL_QUARTZ_SLAB.get(), crystalQuartzTint);
		toTint.put(SCContent.STAIRS_CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		toTint.forEach((block, tint) -> Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			if(world == null || pos == null)
				return tint;

			if(block == SCContent.REINFORCED_GRASS_BLOCK.get() && !state.get(ReinforcedSnowyDirtBlock.SNOWY))
			{
				if(tintIndex == 0)
					return tintBlocks ? tint : noTint;

				int grassTint = BiomeColors.getGrassColor(world, pos);

				return tintBlocks ? mixTints(grassTint, tint) : grassTint;
			}
			else if(tintBlocks)
				return tint;
			else if(tint == reinforcedCrystalQuartzTint || tint == crystalQuartzTint)
				return crystalQuartzTint;
			else
				return noTint;
		}, block));
		toTint.forEach((item, tint) -> Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if(item == SCContent.REINFORCED_GRASS_BLOCK.get())
			{
				if(tintIndex == 0)
					return tintBlocks ? tint : noTint;

				int grassTint = GrassColors.get(0.5D, 1.0D);

				return tintBlocks ? mixTints(grassTint, tint) : grassTint;
			}
			else if(tintBlocks)
				return tint;
			else if(tint == reinforcedCrystalQuartzTint || tint == crystalQuartzTint)
				return crystalQuartzTint;
			else return noTint;
		}, item));
		Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			if(tintBlocks)
			{
				Block block = state.getBlock();

				if(block instanceof DisguisableBlock)
				{
					Block blockFromItem = Block.getBlockFromItem(((DisguisableBlock)block).getDisguisedStack(world, pos).getItem());

					if(blockFromItem != Blocks.AIR && !(blockFromItem instanceof DisguisableBlock))
						return Minecraft.getInstance().getBlockColors().getColor(blockFromItem.getDefaultState(), world, pos, tintIndex);
				}
			}

			return noTint;
		}, SCContent.CAGE_TRAP.get(), SCContent.INVENTORY_SCANNER.get(), SCContent.KEYCARD_READER.get(), SCContent.KEYPAD.get(), SCContent.LASER_BLOCK.get(), SCContent.RETINAL_SCANNER.get(), SCContent.USERNAME_LOGGER.get());
	}

	private int mixTints(int tint1, int tint2)
	{
		int red = (tint1 >> 0x10) & 0xFF;
		int green = (tint1 >> 0x8) & 0xFF;
		int blue = tint1 & 0xFF;

		red *= (float)(tint2 >> 0x10 & 0xFF) / 0xFF;
		green *= (float)(tint2 >> 0x8 & 0xFF) / 0xFF;
		blue *= (float)(tint2 & 0xFF) / 0xFF;

		return ((red << 8) + green << 8) + blue;
	}

	@Override
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	@Override
	public void displayMRATGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new MineRemoteAccessToolScreen(stack));
	}

	@Override
	public void displaySRATGui(ItemStack stack, int viewDistance)
	{
		Minecraft.getInstance().displayGuiScreen(new SentryRemoteAccessToolScreen(stack, viewDistance));
	}

	@Override
	public void displayEditModuleGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new EditModuleScreen(stack));
	}

	@Override
	public void displayCameraMonitorGui(PlayerInventory inv, CameraMonitorItem item, CompoundNBT stackTag)
	{
		Minecraft.getInstance().displayGuiScreen(new CameraMonitorScreen(inv, item, stackTag));
	}

	@Override
	public void displaySCManualGui()
	{
		Minecraft.getInstance().displayGuiScreen(new SCManualScreen());
	}

	@Override
	public void displayEditSecretSignGui(SecretSignTileEntity te)
	{
		Minecraft.getInstance().displayGuiScreen(new EditSecretSignScreen(te));
	}
}
