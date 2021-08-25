package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.BlockMineModel;
import net.geforcemods.securitycraft.models.BulletModel;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.models.SentryModel;
import net.geforcemods.securitycraft.renderers.BlockPocketManagerTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.EmptyRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.ProjectorTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.ReinforcedPistonHeadRenderer;
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
import net.geforcemods.securitycraft.screen.IMSScreen;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.geforcemods.securitycraft.screen.KeyChangerScreen;
import net.geforcemods.securitycraft.screen.KeycardReaderScreen;
import net.geforcemods.securitycraft.screen.KeypadFurnaceScreen;
import net.geforcemods.securitycraft.screen.MineRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.ProjectorScreen;
import net.geforcemods.securitycraft.screen.SCManualScreen;
import net.geforcemods.securitycraft.screen.SentryRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.geforcemods.securitycraft.screen.TrophySystemScreen;
import net.geforcemods.securitycraft.screen.UsernameLoggerScreen;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD, value=Dist.CLIENT)
public class ClientHandler
{
	public static final ModelLayerLocation BULLET_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "bullet"), "main");
	public static final ModelLayerLocation IMS_BOMB_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "ims_bomb"), "main");
	public static final ModelLayerLocation SENTRY_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "sentry"), "main");
	public static final ModelLayerLocation SECURITY_CAMERA_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "security_camera"), "main");

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
				new ResourceLocation(SecurityCraft.MODID, "projector"),
				new ResourceLocation(SecurityCraft.MODID, "username_logger")
		};
		ResourceLocation[] poweredBlocks = {
				new ResourceLocation(SecurityCraft.MODID, "laser_block")
		};
		String[] mines = {"coal_ore", "cobblestone", "diamond_ore", "dirt", "emerald_ore", "gravel", "gold_ore", "gilded_blackstone", "furnace", "iron_ore", "lapis_ore", "nether_gold_ore", "redstone_ore", "sand", "stone"};

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
		ResourceLocation invScanRL = new ResourceLocation(SecurityCraft.MODID, "inventory_scanner");

		registerDisguisedModel(event, cageTrapRl, "deactivated=true");
		registerDisguisedModel(event, cageTrapRl, "deactivated=false");

		for(String facing : facings)
		{
			registerDisguisedModel(event, invScanRL, "facing=" + facing + ",horizontal=true");
			registerDisguisedModel(event, invScanRL, "facing=" + facing + ",horizontal=false");
		}

		for(String mine : mines)
		{
			registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, mine.replace("_ore", "") + "_mine"), new ResourceLocation(mine));
		}

		registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, "quartz_mine"), new ResourceLocation("nether_quartz_ore"));
	}

	private static void registerDisguisedModel(ModelBakeEvent event, ResourceLocation rl, String stateString)
	{
		ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

		event.getModelRegistry().put(mrl, new DisguisableDynamicBakedModel(rl, event.getModelRegistry().get(mrl)));
	}

	private static void registerBlockMineModel(ModelBakeEvent event, ResourceLocation mineRl, ResourceLocation realBlockRl)
	{
		ModelResourceLocation mineMrl = new ModelResourceLocation(mineRl, "inventory");

		event.getModelRegistry().put(mineMrl, new BlockMineModel(event.getModelRegistry().get(new ModelResourceLocation(realBlockRl, "inventory")), event.getModelRegistry().get(mineMrl)));
	}

	@SubscribeEvent
	public static void onTextureStitchPre(TextureStitchEvent.Pre event)
	{
		if(event.getMap().location().equals(Sheets.CHEST_SHEET))
		{
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/active"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/inactive"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/left_active"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/left_inactive"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/right_active"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/right_inactive"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas_left"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas_right"));
		}
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		RenderType cutout = RenderType.cutout();
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();

		ItemBlockRenderTypes.setRenderLayer(SCContent.BLOCK_POCKET_MANAGER.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.BLOCK_POCKET_WALL.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.CAGE_TRAP.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.FAKE_WATER.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.FLOWING_FAKE_WATER.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.INVENTORY_SCANNER.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.INVENTORY_SCANNER_FIELD.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.KEYCARD_READER.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.KEYPAD.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.KEYPAD_DOOR.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.LASER_BLOCK.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.LASER_FIELD.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_CHAIN.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_COBWEB.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_DOOR.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GLASS.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GLASS_PANE.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GRASS_BLOCK.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_HOPPER.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_ICE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_IRON_BARS.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_IRON_TRAPDOOR.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LANTERN.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_SOUL_LANTERN.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIME_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PINK_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_RED_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.RETINAL_SCANNER.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.SCANNER_DOOR.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.TRACK_MINE.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.TROPHY_SYSTEM.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.USERNAME_LOGGER.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.PROJECTOR.get(), cutoutMipped);
		event.enqueueWork(() -> {
			MenuScreens.register(SCContent.mTypeBlockReinforcer, BlockReinforcerScreen::new);
			MenuScreens.register(SCContent.mTypeBriefcase, BriefcasePasswordScreen::new);
			MenuScreens.register(SCContent.mTypeBriefcaseInventory, BriefcaseInventoryScreen::new);
			MenuScreens.register(SCContent.mTypeBriefcaseSetup, BriefcaseSetupScreen::new);
			MenuScreens.register(SCContent.mTypeCustomizeBlock, CustomizeBlockScreen::new);
			MenuScreens.register(SCContent.mTypeDisguiseModule, DisguiseModuleScreen::new);
			MenuScreens.register(SCContent.mTypeInventoryScanner, InventoryScannerScreen::new);
			MenuScreens.register(SCContent.mTypeKeypadFurnace, KeypadFurnaceScreen::new);
			MenuScreens.register(SCContent.mTypeCheckPassword, CheckPasswordScreen::new);
			MenuScreens.register(SCContent.mTypeSetPassword, SetPasswordScreen::new);
			MenuScreens.register(SCContent.mTypeUsernameLogger, UsernameLoggerScreen::new);
			MenuScreens.register(SCContent.mTypeIMS, IMSScreen::new);
			MenuScreens.register(SCContent.mTypeKeycardReader, KeycardReaderScreen::new);
			MenuScreens.register(SCContent.mTypeKeyChanger, KeyChangerScreen::new);
			MenuScreens.register(SCContent.mTypeBlockPocketManager, BlockPocketManagerScreen::new);
			MenuScreens.register(SCContent.mTypeProjector, ProjectorScreen::new);
			MenuScreens.register(SCContent.mTypeTrophySystem, TrophySystemScreen::new);
		});
		KeyBindings.init();
		OverlayRegistry.registerOverlayTop(SecurityCraft.MODID + ":camera_overlay", SCClientEventHandler::cameraOverlay);
		OverlayRegistry.registerOverlayTop(SecurityCraft.MODID + ":hotbar_bind_overlay", SCClientEventHandler::hotbarBindOverlay);
		tint();
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerEntityRenderer(SCContent.eTypeBouncingBetty, BouncingBettyRenderer::new);
		event.registerEntityRenderer(SCContent.eTypeImsBomb, IMSBombRenderer::new);
		event.registerEntityRenderer(SCContent.eTypeSecurityCamera, EmptyRenderer::new);
		event.registerEntityRenderer(SCContent.eTypeSentry, SentryRenderer::new);
		event.registerEntityRenderer(SCContent.eTypeBullet, BulletRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeBlockPocketManager, BlockPocketManagerTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeKeypadChest, KeypadChestTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeRetinalScanner, RetinalScannerTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeSecurityCamera, SecurityCameraTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeSecretSign, SecretSignTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeTrophySystem, TrophySystemTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeProjector, ProjectorTileEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.beTypeReinforcedPiston, ReinforcedPistonHeadRenderer::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(BULLET_LOCATION, BulletModel::createLayer);
		event.registerLayerDefinition(IMS_BOMB_LOCATION, IMSBombModel::createLayer);
		event.registerLayerDefinition(SENTRY_LOCATION, SentryModel::createLayer);
		event.registerLayerDefinition(SECURITY_CAMERA_LOCATION, SecurityCameraModel::createLayer);
	}

	private static void tint()
	{
		Set<Block> reinforcedTint = new HashSet<>();
		Map<Block, Integer> toTint = new HashMap<>();
		Map<Block, BlockColor> specialBlockTint = new HashMap<>();
		Map<Block, ItemColor> specialItemTint = new HashMap<>();

		for(Field field : SCContent.class.getFields())
		{
			if(field.isAnnotationPresent(Reinforced.class))
			{
				try
				{
					if(field.getAnnotation(Reinforced.class).hasReinforcedTint())
						reinforcedTint.add(((RegistryObject<Block>)field.get(null)).get());

					if(field.getAnnotation(Reinforced.class).hasReinforcedTint() || field.getAnnotation(Reinforced.class).customTint() != 0xFFFFFF)
						toTint.put(((RegistryObject<Block>)field.get(null)).get(), field.getAnnotation(Reinforced.class).customTint());
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		int noTint = 0xFFFFFF;
		int crystalQuartzTint = 0x15B3A2;

		toTint.put(SCContent.BLOCK_POCKET_MANAGER.get(), crystalQuartzTint);
		reinforcedTint.add(SCContent.BLOCK_POCKET_MANAGER.get());
		toTint.put(SCContent.BLOCK_POCKET_WALL.get(), crystalQuartzTint);
		reinforcedTint.add(SCContent.BLOCK_POCKET_WALL.get());
		toTint.put(SCContent.CHISELED_CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		toTint.put(SCContent.CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		toTint.put(SCContent.CRYSTAL_QUARTZ_PILLAR.get(), crystalQuartzTint);
		toTint.put(SCContent.CRYSTAL_QUARTZ_SLAB.get(), crystalQuartzTint);
		toTint.put(SCContent.STAIRS_CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		specialBlockTint.put(SCContent.REINFORCED_GRASS_BLOCK.get(), (state, world, pos, tintIndex) -> {
			if(tintIndex == 1 && !state.getValue(ReinforcedSnowyDirtBlock.SNOWY)) {
				int grassTint = world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return noTint;
		});
		specialBlockTint.put(SCContent.REINFORCED_WATER_CAULDRON.get(), (state, world, pos, tintIndex) -> {
			if(tintIndex == 1)
				return world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) : -1;

			return noTint;
		});
		specialItemTint.put(SCContent.REINFORCED_GRASS_BLOCK.get(), (stack, tintIndex) -> {
			if(tintIndex == 1) {
				int grassTint = GrassColor.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return noTint;
		});
		toTint.forEach((block, tint) -> Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			if(tintIndex == 0)
				return reinforcedTint.contains(block) ? mixWithReinforcedTintIfEnabled(tint) : tint;
			else if(specialBlockTint.containsKey(block))
				return specialBlockTint.get(block).getColor(state, world, pos, tintIndex);
			else
				return noTint;
		}, block));
		toTint.forEach((item, tint) -> Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if(tintIndex == 0)
				return reinforcedTint.contains(item) ? mixWithReinforcedTintIfEnabled(tint) : tint;
			else if(specialItemTint.containsKey(item))
				return specialItemTint.get(item).getColor(stack, tintIndex);
			else
				return noTint;
		}, item));
		Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			Block block = state.getBlock();

			if(block instanceof DisguisableBlock disguisedBlock)
			{
				Block blockFromItem = Block.byItem(disguisedBlock.getDisguisedStack(world, pos).getItem());
				BlockState defaultBlockState = blockFromItem.defaultBlockState();

				if(!defaultBlockState.isAir() && !(blockFromItem instanceof DisguisableBlock))
					return Minecraft.getInstance().getBlockColors().getColor(defaultBlockState, world, pos, tintIndex);
			}

			return noTint;
		}, SCContent.CAGE_TRAP.get(), SCContent.INVENTORY_SCANNER.get(), SCContent.KEYCARD_READER.get(), SCContent.KEYPAD.get(), SCContent.LASER_BLOCK.get(), SCContent.PROJECTOR.get(), SCContent.RETINAL_SCANNER.get(), SCContent.USERNAME_LOGGER.get());
		Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if(tintIndex == 0)
			{
				DyeableLeatherItem item = ((DyeableLeatherItem)stack.getItem());

				if(item.hasCustomColor(stack))
					return item.getColor(stack);
				else
					return 0x333333;
			}
			else
				return -1;
		}, SCContent.BRIEFCASE.get());
	}

	private static int mixWithReinforcedTintIfEnabled(int tint1) {
		boolean tintReinforcedBlocks = ConfigHandler.SERVER.forceReinforcedBlockTint.get() ? ConfigHandler.SERVER.reinforcedBlockTint.get() : ConfigHandler.CLIENT.reinforcedBlockTint.get();

		return tintReinforcedBlocks ? mixTints(tint1, 0x999999) : tint1;
	}

	private static int mixTints(int tint1, int tint2)
	{
		int red = (tint1 >> 0x10) & 0xFF;
		int green = (tint1 >> 0x8) & 0xFF;
		int blue = tint1 & 0xFF;

		red *= (float)(tint2 >> 0x10 & 0xFF) / 0xFF;
		green *= (float)(tint2 >> 0x8 & 0xFF) / 0xFF;
		blue *= (float)(tint2 & 0xFF) / 0xFF;

		return ((red << 8) + green << 8) + blue;
	}

	public static Player getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	public static void displayMRATGui(ItemStack stack)
	{
		Minecraft.getInstance().setScreen(new MineRemoteAccessToolScreen(stack));
	}

	public static void displaySRATGui(ItemStack stack, int viewDistance)
	{
		Minecraft.getInstance().setScreen(new SentryRemoteAccessToolScreen(stack, viewDistance));
	}

	public static void displayEditModuleGui(ItemStack stack)
	{
		Minecraft.getInstance().setScreen(new EditModuleScreen(stack));
	}

	public static void displayCameraMonitorGui(Inventory inv, CameraMonitorItem item, CompoundTag stackTag)
	{
		Minecraft.getInstance().setScreen(new CameraMonitorScreen(inv, item, stackTag));
	}

	public static void displaySCManualGui()
	{
		Minecraft.getInstance().setScreen(new SCManualScreen());
	}

	public static void displayEditSecretSignGui(SecretSignBlockEntity te)
	{
		Minecraft.getInstance().setScreen(new SignEditScreen(te, Minecraft.getInstance().isTextFilteringEnabled()));
	}

	public static void refreshModelData(BlockEntity te)
	{
		BlockPos pos = te.getBlockPos();

		ModelDataManager.requestModelDataRefresh(te);
		Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}
}
