package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSnowyDirtBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.BlockMineModel;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.renderers.BlockPocketManagerRenderer;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.DisguisableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.EmptyRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.ProjectorRenderer;
import net.geforcemods.securitycraft.renderers.ReinforcedPistonRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.SonicSecuritySystemRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemRenderer;
import net.geforcemods.securitycraft.screen.BlockChangeDetectorScreen;
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
import net.geforcemods.securitycraft.screen.KeyChangerScreen;
import net.geforcemods.securitycraft.screen.KeycardReaderScreen;
import net.geforcemods.securitycraft.screen.KeypadBlastFurnaceScreen;
import net.geforcemods.securitycraft.screen.KeypadFurnaceScreen;
import net.geforcemods.securitycraft.screen.KeypadSmokerScreen;
import net.geforcemods.securitycraft.screen.MineRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.ProjectorScreen;
import net.geforcemods.securitycraft.screen.SCManualScreen;
import net.geforcemods.securitycraft.screen.SentryRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.geforcemods.securitycraft.screen.SonicSecuritySystemScreen;
import net.geforcemods.securitycraft.screen.TrophySystemScreen;
import net.geforcemods.securitycraft.screen.UsernameLoggerScreen;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
	public static final BlockEntityRenderDelegate DISGUISED_BLOCK_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	public static final BlockEntityRenderDelegate PROJECTOR_RENDER_DELEGATE = new BlockEntityRenderDelegate();

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		//@formatter:off
		String[] mines = {
				"ancient_debris",
				"blast_furnace",
				"coal_ore",
				"cobblestone",
				"diamond_ore",
				"dirt",
				"emerald_ore",
				"gravel",
				"gold_ore",
				"gilded_blackstone",
				"furnace",
				"iron_ore",
				"lapis_ore",
				"nether_gold_ore",
				"redstone_ore",
				"sand",
				"smoker",
				"stone"
		};
		Block[] disguisableBlocks = {
				SCContent.BLOCK_CHANGE_DETECTOR.get(),
				SCContent.CAGE_TRAP.get(),
				SCContent.INVENTORY_SCANNER.get(),
				SCContent.KEYCARD_READER.get(),
				SCContent.KEYPAD.get(),
				SCContent.KEYPAD_BLAST_FURNACE.get(),
				SCContent.KEYPAD_FURNACE.get(),
				SCContent.KEYPAD_SMOKER.get(),
				SCContent.LASER_BLOCK.get(),
				SCContent.PROJECTOR.get(),
				SCContent.PROTECTO.get(),
				SCContent.RETINAL_SCANNER.get(),
				SCContent.SENTRY_DISGUISE.get(),
				SCContent.TROPHY_SYSTEM.get(),
				SCContent.USERNAME_LOGGER.get()
		};
		//@formatter:on

		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

		for (Block block : disguisableBlocks) {
			for (BlockState state : block.getStateDefinition().getPossibleStates()) {
				registerDisguisedModel(modelRegistry, block.getRegistryName(), state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
			}
		}
		for (String mine : mines) {
			registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, mine.replace("_ore", "") + "_mine"), new ResourceLocation(mine));
		}

		registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, "quartz_mine"), new ResourceLocation("nether_quartz_ore"));
	}

	private static void registerDisguisedModel(Map<ResourceLocation, IBakedModel> modelRegistry, ResourceLocation rl, String stateString) {
		ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

		modelRegistry.put(mrl, new DisguisableDynamicBakedModel(modelRegistry.get(mrl)));
	}

	private static void registerBlockMineModel(ModelBakeEvent event, ResourceLocation mineRl, ResourceLocation realBlockRl) {
		ModelResourceLocation mineMrl = new ModelResourceLocation(mineRl, "inventory");

		event.getModelRegistry().put(mineMrl, new BlockMineModel(event.getModelRegistry().get(new ModelResourceLocation(realBlockRl, "inventory")), event.getModelRegistry().get(mineMrl)));
	}

	@SubscribeEvent
	public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
		if (event.getMap().location().equals(Atlases.CHEST_SHEET)) {
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
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutout = RenderType.cutout();
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();

		RenderTypeLookup.setRenderLayer(SCContent.BLOCK_CHANGE_DETECTOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.BLOCK_POCKET_MANAGER.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.BLOCK_POCKET_WALL.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.CAGE_TRAP.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.FAKE_WATER.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.FLOWING_FAKE_WATER.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.INVENTORY_SCANNER.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.INVENTORY_SCANNER_FIELD.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.KEYCARD_READER.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD_DOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD_FURNACE.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD_SMOKER.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD_BLAST_FURNACE.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.LASER_BLOCK.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.LASER_FIELD.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.PROJECTOR.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.PROTECTO.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_CHAIN.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_COBWEB.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_DOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GLASS.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GLASS_PANE.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GRASS_BLOCK.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_HOPPER.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_ICE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_IRON_BARS.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_IRON_TRAPDOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LANTERN.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_SOUL_LANTERN.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LIME_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_PINK_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_RED_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.RETINAL_SCANNER.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.SCANNER_DOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.SENTRY_DISGUISE.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.TRACK_MINE.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.TROPHY_SYSTEM.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.USERNAME_LOGGER.get(), cutout);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeBouncingBetty.get(), BouncingBettyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeImsBomb.get(), IMSBombRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeSecurityCamera.get(), EmptyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeSentry.get(), SentryRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeBullet.get(), BulletRenderer::new);
		//normal block entity renderers
		ClientRegistry.bindTileEntityRenderer(SCContent.BLOCK_POCKET_MANAGER_BLOCK_ENTITY.get(), BlockPocketManagerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), KeypadChestRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.RETINAL_SCANNER_BLOCK_ENTITY.get(), RetinalScannerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get(), SecurityCameraRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SECRET_SIGN_BLOCK_ENTITY.get(), SecretSignRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.TROPHY_SYSTEM_BLOCK_ENTITY.get(), TrophySystemRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.PROJECTOR_BLOCK_ENTITY.get(), ProjectorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get(), ReinforcedPistonRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SONIC_SECURITY_SYSTEM_BLOCK_ENTITY.get(), SonicSecuritySystemRenderer::new);
		//disguisable block entity renderers
		ClientRegistry.bindTileEntityRenderer(SCContent.BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.CAGE_TRAP_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.DISGUISABLE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYCARD_READER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_SMOKER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.PROTECTO_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ScreenManager.register(SCContent.BLOCK_REINFORCER_MENU.get(), BlockReinforcerScreen::new);
		ScreenManager.register(SCContent.BRIEFCASE_INVENTORY_MENU.get(), BriefcaseInventoryScreen::new);
		ScreenManager.register(SCContent.CUSTOMIZE_BLOCK_MENU.get(), CustomizeBlockScreen::new);
		ScreenManager.register(SCContent.DISGUISE_MODULE_MENU.get(), DisguiseModuleScreen::new);
		ScreenManager.register(SCContent.KEYPAD_FURNACE_MENU.get(), KeypadFurnaceScreen::new);
		ScreenManager.register(SCContent.KEYPAD_SMOKER_MENU.get(), KeypadSmokerScreen::new);
		ScreenManager.register(SCContent.KEYPAD_BLAST_FURNACE_MENU.get(), KeypadBlastFurnaceScreen::new);
		ScreenManager.register(SCContent.KEYCARD_READER_MENU.get(), KeycardReaderScreen::new);
		ScreenManager.register(SCContent.BLOCK_POCKET_MANAGER_MENU.get(), BlockPocketManagerScreen::new);
		ScreenManager.register(SCContent.PROJECTOR_MENU.get(), ProjectorScreen::new);
		ScreenManager.register(SCContent.BLOCK_CHANGE_DETECTOR_MENU.get(), BlockChangeDetectorScreen::new);
		KeyBindings.init();
		tint();
	}

	private static void tint() {
		Set<Block> reinforcedTint = new HashSet<>();
		Map<Block, Integer> toTint = new HashMap<>();
		Map<Block, IBlockColor> specialBlockTint = new HashMap<>();
		Map<Block, IItemColor> specialItemTint = new HashMap<>();

		for (Field field : SCContent.class.getFields()) {
			if (field.isAnnotationPresent(Reinforced.class)) {
				try {
					if (field.getAnnotation(Reinforced.class).hasReinforcedTint())
						reinforcedTint.add(((RegistryObject<Block>) field.get(null)).get());

					if (field.getAnnotation(Reinforced.class).hasReinforcedTint() || field.getAnnotation(Reinforced.class).customTint() != 0xFFFFFF)
						toTint.put(((RegistryObject<Block>) field.get(null)).get(), field.getAnnotation(Reinforced.class).customTint());
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
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
			if (tintIndex == 1 && !state.getValue(ReinforcedSnowyDirtBlock.SNOWY)) {
				int grassTint = world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColors.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return noTint;
		});
		specialBlockTint.put(SCContent.REINFORCED_CAULDRON.get(), (state, world, pos, tintIndex) -> {
			if (tintIndex == 1)
				return world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) : -1;

			return noTint;
		});

		specialItemTint.put(SCContent.REINFORCED_GRASS_BLOCK.get(), (stack, tintIndex) -> {
			if (tintIndex == 1) {
				int grassTint = GrassColors.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return noTint;
		});

		toTint.forEach((block, tint) -> Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			if (tintIndex == 0)
				return reinforcedTint.contains(block) ? mixWithReinforcedTintIfEnabled(tint) : tint;
			else if (specialBlockTint.containsKey(block))
				return specialBlockTint.get(block).getColor(state, world, pos, tintIndex);
			else
				return noTint;
		}, block));
		toTint.forEach((item, tint) -> Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 0)
				return reinforcedTint.contains(item) ? mixWithReinforcedTintIfEnabled(tint) : tint;
			else if (specialItemTint.containsKey(item))
				return specialItemTint.get(item).getColor(stack, tintIndex);
			else
				return noTint;
		}, item));
		Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			Block block = state.getBlock();

			if (block instanceof DisguisableBlock) {
				Block blockFromItem = Block.byItem(((DisguisableBlock) block).getDisguisedStack(world, pos).getItem());

				if (blockFromItem != Blocks.AIR && !(blockFromItem instanceof DisguisableBlock))
					return Minecraft.getInstance().getBlockColors().getColor(blockFromItem.defaultBlockState(), world, pos, tintIndex);
			}

			return noTint;
			//@formatter:off
		}, SCContent.BLOCK_CHANGE_DETECTOR.get(),
				SCContent.CAGE_TRAP.get(),
				SCContent.INVENTORY_SCANNER.get(),
				SCContent.KEYCARD_READER.get(),
				SCContent.KEYPAD.get(),
				SCContent.KEYPAD_FURNACE.get(),
				SCContent.KEYPAD_SMOKER.get(),
				SCContent.KEYPAD_BLAST_FURNACE.get(),
				SCContent.LASER_BLOCK.get(),
				SCContent.PROJECTOR.get(),
				SCContent.PROTECTO.get(),
				SCContent.RETINAL_SCANNER.get(),
				SCContent.SENTRY_DISGUISE.get(),
				SCContent.TROPHY_SYSTEM.get(),
				SCContent.USERNAME_LOGGER.get());
		//@formatter:on
		Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 0) {
				IDyeableArmorItem item = ((IDyeableArmorItem) stack.getItem());

				if (item.hasCustomColor(stack))
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

	private static int mixTints(int tint1, int tint2) {
		int red = (tint1 >> 0x10) & 0xFF;
		int green = (tint1 >> 0x8) & 0xFF;
		int blue = tint1 & 0xFF;

		red *= (float) (tint2 >> 0x10 & 0xFF) / 0xFF;
		green *= (float) (tint2 >> 0x8 & 0xFF) / 0xFF;
		blue *= (float) (tint2 & 0xFF) / 0xFF;

		return ((red << 8) + green << 8) + blue;
	}

	public static PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public static void displayMRATScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new MineRemoteAccessToolScreen(stack));
	}

	public static void displaySRATScreen(ItemStack stack, int viewDistance) {
		Minecraft.getInstance().setScreen(new SentryRemoteAccessToolScreen(stack, viewDistance));
	}

	public static void displayEditModuleScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new EditModuleScreen(stack));
	}

	public static void displayCameraMonitorScreen(PlayerInventory inv, CameraMonitorItem item, CompoundNBT stackTag) {
		Minecraft.getInstance().setScreen(new CameraMonitorScreen(inv, item, stackTag));
	}

	public static void displaySCManualScreen() {
		Minecraft.getInstance().setScreen(new SCManualScreen());
	}

	public static void displayEditSecretSignScreen(SecretSignBlockEntity te) {
		Minecraft.getInstance().setScreen(new EditSignScreen(te));
	}

	public static void displaySonicSecuritySystemScreen(SonicSecuritySystemBlockEntity te) {
		Minecraft.getInstance().setScreen(new SonicSecuritySystemScreen(te));
	}

	public static void displayBriefcasePasswordScreen(ITextComponent title) {
		Minecraft.getInstance().setScreen(new BriefcasePasswordScreen(title));
	}

	public static void displayBriefcaseSetupScreen(ITextComponent title) {
		Minecraft.getInstance().setScreen(new BriefcaseSetupScreen(title));
	}

	public static void displayUsernameLoggerScreen(World level, BlockPos pos) {
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

		if (te instanceof UsernameLoggerBlockEntity)
			Minecraft.getInstance().setScreen(new UsernameLoggerScreen((UsernameLoggerBlockEntity) te));
	}

	public static void displayIMSScreen(IMSBlockEntity te) {
		Minecraft.getInstance().setScreen(new IMSScreen(te));
	}

	public static void displayUniversalKeyChangerScreen(TileEntity be) {
		Minecraft.getInstance().setScreen(new KeyChangerScreen(be));
	}

	public static void displayTrophySystemScreen(TrophySystemBlockEntity te) {
		Minecraft.getInstance().setScreen(new TrophySystemScreen(te));
	}

	public static void displayCheckPasswordScreen(TileEntity te) {
		ITextComponent displayName = te instanceof INameable ? ((INameable) te).getDisplayName() : new TranslationTextComponent(te.getBlockState().getBlock().getDescriptionId());

		Minecraft.getInstance().setScreen(new CheckPasswordScreen(te, displayName));
	}

	public static void displaySetPasswordScreen(TileEntity te) {
		ITextComponent displayName = te instanceof INameable ? ((INameable) te).getDisplayName() : new TranslationTextComponent(te.getBlockState().getBlock().getDescriptionId());

		Minecraft.getInstance().setScreen(new SetPasswordScreen(te, displayName));
	}

	public static void refreshModelData(TileEntity te) {
		BlockPos pos = te.getBlockPos();

		ModelDataManager.requestModelDataRefresh(te);
		Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getInstance().cameraEntity instanceof SecurityCamera;
	}

	public static void putDisguisedBeRenderer(TileEntity disguisableBlockEntity, ItemStack stack) {
		DISGUISED_BLOCK_RENDER_DELEGATE.putDelegateFor(disguisableBlockEntity, NBTUtil.readBlockState(stack.getOrCreateTag().getCompound("SavedState")));
	}
}
