package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.items.KeycardHolderItem;
import net.geforcemods.securitycraft.items.LensItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.BlockMineModel;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.particle.FloorTrapCloudParticle;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticle;
import net.geforcemods.securitycraft.renderers.BlockPocketManagerRenderer;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.ClaymoreRenderer;
import net.geforcemods.securitycraft.renderers.DisguisableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.DisplayCaseRenderer;
import net.geforcemods.securitycraft.renderers.EmptyRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.ProjectorRenderer;
import net.geforcemods.securitycraft.renderers.ReinforcedPistonRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignRenderer;
import net.geforcemods.securitycraft.renderers.SecureRedstoneInterfaceRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.SonicSecuritySystemRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemRenderer;
import net.geforcemods.securitycraft.screen.AlarmScreen;
import net.geforcemods.securitycraft.screen.BlockChangeDetectorScreen;
import net.geforcemods.securitycraft.screen.BlockPocketManagerScreen;
import net.geforcemods.securitycraft.screen.BlockReinforcerScreen;
import net.geforcemods.securitycraft.screen.BriefcasePasscodeScreen;
import net.geforcemods.securitycraft.screen.CameraMonitorScreen;
import net.geforcemods.securitycraft.screen.CheckPasscodeScreen;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.geforcemods.securitycraft.screen.DisguiseModuleScreen;
import net.geforcemods.securitycraft.screen.EditModuleScreen;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.geforcemods.securitycraft.screen.ItemInventoryScreen;
import net.geforcemods.securitycraft.screen.KeyChangerScreen;
import net.geforcemods.securitycraft.screen.KeycardReaderScreen;
import net.geforcemods.securitycraft.screen.KeypadBlastFurnaceScreen;
import net.geforcemods.securitycraft.screen.KeypadFurnaceScreen;
import net.geforcemods.securitycraft.screen.KeypadSmokerScreen;
import net.geforcemods.securitycraft.screen.LaserBlockScreen;
import net.geforcemods.securitycraft.screen.MineRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.ProjectorScreen;
import net.geforcemods.securitycraft.screen.ReinforcedLecternScreen;
import net.geforcemods.securitycraft.screen.RiftStabilizerScreen;
import net.geforcemods.securitycraft.screen.SCManualScreen;
import net.geforcemods.securitycraft.screen.SSSItemScreen;
import net.geforcemods.securitycraft.screen.SecureRedstoneInterfaceScreen;
import net.geforcemods.securitycraft.screen.SentryRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SetPasscodeScreen;
import net.geforcemods.securitycraft.screen.SingleLensScreen;
import net.geforcemods.securitycraft.screen.SonicSecuritySystemScreen;
import net.geforcemods.securitycraft.screen.TrophySystemScreen;
import net.geforcemods.securitycraft.screen.UsernameLoggerScreen;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.Reinforced;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.LecternTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
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
	public static final float EMPTY_STATE = 0.0F, UNKNOWN_STATE = 0.25F, NOT_LINKED_STATE = 0.5F, LINKED_STATE = 0.75F;
	public static final BlockEntityRenderDelegate DISGUISED_BLOCK_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	public static final BlockEntityRenderDelegate PROJECTOR_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	private static Map<Block, Integer> blocksWithReinforcedTint = new HashMap<>();
	private static Map<Block, Integer> blocksWithCustomTint = new HashMap<>();
	//@formatter:off
	private static Supplier<Block[]> disguisableBlocks = () -> new Block[] {
			SCContent.BLOCK_CHANGE_DETECTOR.get(),
			SCContent.CAGE_TRAP.get(),
			SCContent.FLOOR_TRAP.get(),
			SCContent.INVENTORY_SCANNER.get(),
			SCContent.KEYCARD_READER.get(),
			SCContent.KEYPAD.get(),
			SCContent.KEYPAD_BARREL.get(),
			SCContent.KEYPAD_BLAST_FURNACE.get(),
			SCContent.KEYPAD_CHEST.get(),
			SCContent.KEYPAD_DOOR.get(),
			SCContent.KEYPAD_FURNACE.get(),
			SCContent.KEYPAD_SMOKER.get(),
			SCContent.KEYPAD_TRAPDOOR.get(),
			SCContent.LASER_BLOCK.get(),
			SCContent.PROJECTOR.get(),
			SCContent.PROTECTO.get(),
			SCContent.REINFORCED_DISPENSER.get(),
			SCContent.REINFORCED_DROPPER.get(),
			SCContent.REINFORCED_HOPPER.get(),
			SCContent.RETINAL_SCANNER.get(),
			SCContent.RIFT_STABILIZER.get(),
			SCContent.SCANNER_DOOR.get(),
			SCContent.SCANNER_TRAPDOOR.get(),
			SCContent.SECURITY_CAMERA.get(),
			SCContent.SECURE_REDSTONE_INTERFACE.get(),
			SCContent.SENTRY_DISGUISE.get(),
			SCContent.SONIC_SECURITY_SYSTEM.get(),
			SCContent.TROPHY_SYSTEM.get(),
			SCContent.USERNAME_LOGGER.get()
	};
	//@formatter:on
	public static final ResourceLocation LINKING_STATE_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "linking_state");

	private ClientHandler() {}

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
		//@formatter:on

		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

		for (Block block : disguisableBlocks.get()) {
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
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/active"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/inactive"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/left_active"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/left_inactive"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/right_active"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/right_inactive"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/christmas"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/christmas_left"));
			event.addSprite(new ResourceLocation(SecurityCraft.MODID, "entity/chest/christmas_right"));
		}
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutout = RenderType.cutout();
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();

		RenderTypeLookup.setRenderLayer(SCContent.BLOCK_POCKET_MANAGER.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.BLOCK_POCKET_WALL.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.FAKE_WATER.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.FLOOR_TRAP.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.FLOWING_FAKE_WATER.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get(), cutoutMipped);
		RenderTypeLookup.setRenderLayer(SCContent.INVENTORY_SCANNER_FIELD.get(), translucent);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD_DOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.KEYPAD_TRAPDOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.LASER_FIELD.get(), translucent);
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
		RenderTypeLookup.setRenderLayer(SCContent.REINFORCED_LADDER.get(), cutout);
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
		RenderTypeLookup.setRenderLayer(SCContent.SCANNER_DOOR.get(), cutout);
		RenderTypeLookup.setRenderLayer(SCContent.TRACK_MINE.get(), cutout);
		Arrays.stream(disguisableBlocks.get()).forEach(block -> RenderTypeLookup.setRenderLayer(block, translucent));
		RenderingRegistry.registerEntityRenderingHandler(SCContent.BOUNCING_BETTY_ENTITY.get(), BouncingBettyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.IMS_BOMB_ENTITY.get(), IMSBombRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.SECURITY_CAMERA_ENTITY.get(), EmptyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.SENTRY_ENTITY.get(), SentryRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.BULLET_ENTITY.get(), BulletRenderer::new);
		//normal block entity renderers
		ClientRegistry.bindTileEntityRenderer(SCContent.BLOCK_POCKET_MANAGER_BLOCK_ENTITY.get(), BlockPocketManagerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.CLAYMORE_BLOCK_ENTITY.get(), ClaymoreRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), KeypadChestRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.RETINAL_SCANNER_BLOCK_ENTITY.get(), RetinalScannerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get(), SecurityCameraRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SECRET_SIGN_BLOCK_ENTITY.get(), SecretSignRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.TROPHY_SYSTEM_BLOCK_ENTITY.get(), TrophySystemRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.PROJECTOR_BLOCK_ENTITY.get(), ProjectorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get(), ReinforcedPistonRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SONIC_SECURITY_SYSTEM_BLOCK_ENTITY.get(), SonicSecuritySystemRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.DISPLAY_CASE_BLOCK_ENTITY.get(), DisplayCaseRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.REINFORCED_LECTERN_BLOCK_ENTITY.get(), LecternTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY.get(), SecureRedstoneInterfaceRenderer::new);
		//disguisable block entity renderers
		ClientRegistry.bindTileEntityRenderer(SCContent.BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.CAGE_TRAP_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.DISGUISABLE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYCARD_READER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_BARREL_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_DOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_TRAPDOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_SMOKER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.KEYPAD_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.PROTECTO_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.REINFORCED_DISPENSER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.REINFORCED_DROPPER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.REINFORCED_HOPPER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.RIFT_STABILIZER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SCANNER_DOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.SCANNER_TRAPDOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		ScreenManager.register(SCContent.BLOCK_CHANGE_DETECTOR_MENU.get(), BlockChangeDetectorScreen::new);
		ScreenManager.register(SCContent.BLOCK_POCKET_MANAGER_MENU.get(), BlockPocketManagerScreen::new);
		ScreenManager.register(SCContent.BLOCK_REINFORCER_MENU.get(), BlockReinforcerScreen::new);
		ScreenManager.register(SCContent.BRIEFCASE_INVENTORY_MENU.get(), ItemInventoryScreen.Briefcase::new);
		ScreenManager.register(SCContent.CUSTOMIZE_BLOCK_MENU.get(), CustomizeBlockScreen::new);
		ScreenManager.register(SCContent.DISGUISE_MODULE_MENU.get(), DisguiseModuleScreen::new);
		ScreenManager.register(SCContent.INVENTORY_SCANNER_MENU.get(), InventoryScannerScreen::new);
		ScreenManager.register(SCContent.KEYPAD_FURNACE_MENU.get(), KeypadFurnaceScreen::new);
		ScreenManager.register(SCContent.KEYPAD_SMOKER_MENU.get(), KeypadSmokerScreen::new);
		ScreenManager.register(SCContent.KEYPAD_BLAST_FURNACE_MENU.get(), KeypadBlastFurnaceScreen::new);
		ScreenManager.register(SCContent.KEYCARD_READER_MENU.get(), KeycardReaderScreen::new);
		ScreenManager.register(SCContent.PROJECTOR_MENU.get(), ProjectorScreen::new);
		ScreenManager.register(SCContent.KEYCARD_HOLDER_MENU.get(), ItemInventoryScreen.KeycardHolder::new);
		ScreenManager.register(SCContent.TROPHY_SYSTEM_MENU.get(), TrophySystemScreen::new);
		ScreenManager.register(SCContent.SINGLE_LENS_MENU.get(), SingleLensScreen::new);
		ScreenManager.register(SCContent.LASER_BLOCK_MENU.get(), LaserBlockScreen::new);
		ScreenManager.register(SCContent.REINFORCED_LECTERN_MENU.get(), ReinforcedLecternScreen::new);
		ItemModelsProperties.register(SCContent.KEYCARD_HOLDER.get(), KeycardHolderItem.COUNT_PROPERTY, (stack, level, entity) -> KeycardHolderItem.getCardCount(stack) / (float) KeycardHolderMenu.CONTAINER_SIZE);
		ItemModelsProperties.register(SCContent.LENS.get(), LensItem.COLOR_PROPERTY, (stack, level, entity) -> ((IDyeableArmorItem) stack.getItem()).hasCustomColor(stack) ? 1.0F : 0.0F);
		ItemModelsProperties.register(SCContent.CAMERA_MONITOR.get(), LINKING_STATE_PROPERTY, (stack, level, entity) -> {
			if (!(entity instanceof PlayerEntity))
				return EMPTY_STATE;

			PlayerEntity player = (PlayerEntity) entity;
			float linkingState = getLinkingState(level, player, stack, (_level, pos) -> _level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity, 30, (tag, i) -> {
				if (!tag.contains("Camera" + i))
					return null;

				String camera = tag.getString("Camera" + i);

				return Arrays.stream(camera.substring(0, camera.lastIndexOf(' ')).split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
			});

			if (!CameraMonitorItem.hasCameraAdded(stack.getTag())) {
				if (linkingState == NOT_LINKED_STATE)
					return NOT_LINKED_STATE;
				else
					return EMPTY_STATE;
			}
			else
				return linkingState;
		});
		ItemModelsProperties.register(SCContent.MINE_REMOTE_ACCESS_TOOL.get(), LINKING_STATE_PROPERTY, (stack, level, entity) -> {
			if (!(entity instanceof PlayerEntity))
				return EMPTY_STATE;

			PlayerEntity player = (PlayerEntity) entity;
			float linkingState = getLinkingState(level, player, stack, (_level, pos) -> _level.getBlockState(pos).getBlock() instanceof IExplosive, 30, (tag, i) -> {
				if (tag.getIntArray("mine" + i).length > 0)
					return Arrays.stream(tag.getIntArray("mine" + i)).boxed().toArray(Integer[]::new);
				else
					return null;
			});

			if (!MineRemoteAccessToolItem.hasMineAdded(stack.getTag())) {
				if (linkingState == NOT_LINKED_STATE)
					return NOT_LINKED_STATE;
				else
					return EMPTY_STATE;
			}
			else
				return linkingState;
		});
		ItemModelsProperties.register(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(), LINKING_STATE_PROPERTY, (stack, level, entity) -> {
			if (!(entity instanceof PlayerEntity))
				return EMPTY_STATE;

			if (Minecraft.getInstance().crosshairPickEntity instanceof Sentry) {
				float linkingState = loop(12, (tag, i) -> Arrays.stream(tag.getIntArray("sentry" + i)).boxed().toArray(Integer[]::new), stack.getOrCreateTag(), ((Sentry) Minecraft.getInstance().crosshairPickEntity).blockPosition());

				if (!SentryRemoteAccessToolItem.hasSentryAdded(stack.getTag())) {
					if (linkingState == NOT_LINKED_STATE)
						return NOT_LINKED_STATE;
					else
						return EMPTY_STATE;
				}
				else
					return linkingState;
			}
			else
				return (SentryRemoteAccessToolItem.hasSentryAdded(stack.getTag()) ? UNKNOWN_STATE : EMPTY_STATE);
		});
		ItemModelsProperties.register(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(), LINKING_STATE_PROPERTY, (stack, level, entity) -> {
			if (!(entity instanceof PlayerEntity))
				return EMPTY_STATE;

			PlayerEntity player = (PlayerEntity) entity;
			float linkingState = getLinkingState(level, player, stack, (_level, pos) -> {
				TileEntity te = _level.getBlockEntity(pos);

				if (!(te instanceof ILockable))
					return false;

				//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
				if (!(te instanceof IOwnable) || !((IOwnable) te).isOwnedBy(player)) {
					if (IDisguisable.getDisguisedBlockState(_level, pos).isPresent())
						return false;
				}

				return true;
			}, 0, null, false, SonicSecuritySystemItem::isAdded);

			if (!SonicSecuritySystemItem.hasLinkedBlock(stack.getTag())) {
				if (linkingState == NOT_LINKED_STATE)
					return NOT_LINKED_STATE;
				else
					return EMPTY_STATE;
			}
			else
				return linkingState;
		});
		ItemModelsProperties.register(SCContent.CODEBREAKER.get(), CodebreakerItem.STATE_PROPERTY, (stack, level, entity) -> {
			CompoundNBT tag = stack.getTag();
			boolean isPlayer = entity instanceof PlayerEntity;

			if ((!isPlayer || !((PlayerEntity) entity).isCreative()) && CodebreakerItem.wasRecentlyUsed(stack))
				return tag.getBoolean(CodebreakerItem.WAS_SUCCESSFUL) ? 0.75F : 0.5F;

			if (!isPlayer)
				return 0.0F;

			float state = getLinkingState(level, (PlayerEntity) entity, stack, (_level, pos) -> _level.getBlockEntity(pos) instanceof ICodebreakable, 0, null, false, (_tag, pos) -> true);

			if (state == LINKED_STATE || state == NOT_LINKED_STATE)
				return 0.25F;
			else
				return 0.0F;
		});
		KeyBindings.init();
	}

	@SubscribeEvent
	public static void registerParticleProviders(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(SCContent.FLOOR_TRAP_CLOUD.get(), FloorTrapCloudParticle.Provider::new);
		Minecraft.getInstance().particleEngine.register(SCContent.INTERFACE_HIGHLIGHT.get(), InterfaceHighlightParticle.Provider::new);
	}

	private static void initTint() {
		for (Field field : SCContent.class.getFields()) {
			if (field.isAnnotationPresent(Reinforced.class)) {
				try {
					Block block = ((RegistryObject<Block>) field.get(null)).get();
					int customTint = field.getAnnotation(Reinforced.class).customTint();

					if (field.getAnnotation(Reinforced.class).hasReinforcedTint())
						blocksWithReinforcedTint.put(block, customTint);
					else if (customTint != 0xFFFFFF)
						blocksWithCustomTint.put(block, customTint);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		int crystalQuartzTint = 0x15B3A2;

		blocksWithReinforcedTint.put(SCContent.BLOCK_POCKET_MANAGER.get(), crystalQuartzTint);
		blocksWithReinforcedTint.put(SCContent.BLOCK_POCKET_WALL.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_SLAB.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.CHISELED_CRYSTAL_QUARTZ.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_BLOCK.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_BRICKS.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_PILLAR.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_STAIRS.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), crystalQuartzTint);
		blocksWithCustomTint.put(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), crystalQuartzTint);
	}

	@SubscribeEvent
	public static void onColorHandlerBlock(ColorHandlerEvent.Block event) {
		initTint();
		blocksWithReinforcedTint.forEach((block, tint) -> event.getBlockColors().register((state, world, pos, tintIndex) -> {
			if (tintIndex == 0)
				return mixWithReinforcedTintIfEnabled(tint);
			else
				return 0xFFFFFF;
		}, block));
		blocksWithCustomTint.forEach((block, tint) -> event.getBlockColors().register((state, world, pos, tintIndex) -> {
			if (tintIndex == 0)
				return tint;
			else
				return 0xFFFFFF;
		}, block));
		event.getBlockColors().register((state, level, pos, tintIndex) -> {
			Block block = state.getBlock();

			if (block instanceof IDisguisable) {
				Block blockFromItem = Block.byItem(((IDisguisable) block).getDisguisedStack(level, pos).getItem());

				if (blockFromItem != Blocks.AIR && !(blockFromItem instanceof IDisguisable))
					return Minecraft.getInstance().getBlockColors().getColor(blockFromItem.defaultBlockState(), level, pos, tintIndex);
			}

			return 0xFFFFFF;
		}, disguisableBlocks.get());
		event.getBlockColors().register((state, level, pos, tintIndex) -> {
			if (tintIndex == 1 && !state.getValue(SnowyDirtBlock.SNOWY)) {
				int grassTint = level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColors.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return ConfigHandler.CLIENT.reinforcedBlockTintColor.get();
		}, SCContent.REINFORCED_GRASS_BLOCK.get());
		event.getBlockColors().register((state, level, pos, tintIndex) -> {
			Direction direction = LaserFieldBlock.getFieldDirection(state);

			return iterateFields(level, pos, direction, ConfigHandler.SERVER.laserBlockRange.get(), SCContent.LASER_BLOCK.get(), LaserBlockBlockEntity.class::isInstance, be -> ((LaserBlockBlockEntity) be).getLensContainer().getItem(direction.getOpposite().ordinal()));
		}, SCContent.LASER_FIELD.get());
		event.getBlockColors().register((state, level, pos, tintIndex) -> {
			Direction direction = state.getValue(InventoryScannerFieldBlock.FACING);

			return iterateFields(level, pos, direction, ConfigHandler.SERVER.inventoryScannerRange.get(), SCContent.INVENTORY_SCANNER.get(), InventoryScannerBlockEntity.class::isInstance, be -> ((InventoryScannerBlockEntity) be).getLensContainer().getItem(0));
		}, SCContent.INVENTORY_SCANNER_FIELD.get());
	}

	public static int iterateFields(IBlockDisplayReader level, BlockPos pos, Direction direction, int range, Block block, Predicate<TileEntity> beTest, Function<TileEntity, ItemStack> lensGetter) {
		try {
			return iterateFieldsInternal(level, pos, direction, range, block, beTest, lensGetter);
		}
		catch (Exception e1) {
			direction = direction.getOpposite();

			try {
				return iterateFieldsInternal(level, pos, direction, range, block, beTest, lensGetter);
			}
			catch (Exception e2) {}
		}

		return -1;
	}

	private static int iterateFieldsInternal(IBlockDisplayReader level, BlockPos pos, Direction direction, int range, Block block, Predicate<TileEntity> beTest, Function<TileEntity, ItemStack> lensGetter) throws ArrayIndexOutOfBoundsException {
		BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());

		for (int i = 0; i < range; i++) {
			if (level.getBlockState(mutablePos).is(block)) {
				TileEntity be = level.getBlockEntity(mutablePos);

				if (beTest.test(be)) {
					ItemStack stack = lensGetter.apply(be);
					Item item = stack.getItem();

					if (item instanceof DyeableArmorItem)
						return ((DyeableArmorItem) item).getColor(stack);

					break;
				}
			}

			mutablePos.move(direction);
		}

		return -1;
	}

	@SubscribeEvent
	public static void onColorHandlerItem(ColorHandlerEvent.Item event) {
		blocksWithReinforcedTint.forEach((item, tint) -> event.getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 0)
				return mixWithReinforcedTintIfEnabled(tint);
			else
				return 0xFFFFFF;
		}, item));
		blocksWithCustomTint.forEach((item, tint) -> event.getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 0)
				return tint;
			else
				return 0xFFFFFF;
		}, item));
		event.getItemColors().register((stack, tintIndex) -> {
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
		event.getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 0) {
				IDyeableArmorItem item = ((IDyeableArmorItem) stack.getItem());

				if (item.hasCustomColor(stack))
					return item.getColor(stack);
			}

			return -1;
		}, SCContent.LENS.get());
		event.getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				int grassTint = GrassColors.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return ConfigHandler.CLIENT.reinforcedBlockTintColor.get();
		}, SCContent.REINFORCED_GRASS_BLOCK.get());
		blocksWithReinforcedTint = null;
		blocksWithCustomTint = null;
	}

	private static int mixWithReinforcedTintIfEnabled(int tint1) {
		boolean tintReinforcedBlocks;

		if (Minecraft.getInstance().level == null)
			tintReinforcedBlocks = ConfigHandler.CLIENT.reinforcedBlockTint.get();
		else
			tintReinforcedBlocks = ConfigHandler.SERVER.forceReinforcedBlockTint.get() ? ConfigHandler.SERVER.reinforcedBlockTint.get() : ConfigHandler.CLIENT.reinforcedBlockTint.get();

		return tintReinforcedBlocks ? ColorHelper.PackedColor.multiply(tint1, ConfigHandler.CLIENT.reinforcedBlockTintColor.get()) : tint1;
	}

	public static PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public static World getClientLevel() {
		return Minecraft.getInstance().level;
	}

	public static void displayMRATScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new MineRemoteAccessToolScreen(stack));
	}

	public static void displaySRATScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new SentryRemoteAccessToolScreen(stack));
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

	public static void displayBriefcasePasscodeScreen(ITextComponent title) {
		Minecraft.getInstance().setScreen(new BriefcasePasscodeScreen(title, false));
	}

	public static void displayBriefcaseSetupScreen(ITextComponent title) {
		Minecraft.getInstance().setScreen(new BriefcasePasscodeScreen(title, true));
	}

	public static void displayUsernameLoggerScreen(BlockPos pos) {
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

		if (te instanceof UsernameLoggerBlockEntity) {
			UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) te;

			if (be.isDisabled())
				getClientPlayer().displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else
				Minecraft.getInstance().setScreen(new UsernameLoggerScreen(be));
		}
	}

	public static void displayUniversalKeyChangerScreen(TileEntity be) {
		Minecraft.getInstance().setScreen(new KeyChangerScreen(be));
	}

	public static void displayCheckPasscodeScreen(TileEntity be) {
		ITextComponent displayName = be instanceof INameable ? ((INameable) be).getDisplayName() : new TranslationTextComponent(be.getBlockState().getBlock().getDescriptionId());

		Minecraft.getInstance().setScreen(new CheckPasscodeScreen(be, displayName));
	}

	public static void displaySetPasscodeScreen(TileEntity be) {
		ITextComponent displayName = be instanceof INameable ? ((INameable) be).getDisplayName() : new TranslationTextComponent(be.getBlockState().getBlock().getDescriptionId());

		Minecraft.getInstance().setScreen(new SetPasscodeScreen(be, displayName));
	}

	public static void displaySSSItemScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new SSSItemScreen(stack));
	}

	public static void displayRiftStabilizerScreen(RiftStabilizerBlockEntity be) {
		Minecraft.getInstance().setScreen(new RiftStabilizerScreen(be));
	}

	public static void displaySecureRedstoneInterfaceScreen(SecureRedstoneInterfaceBlockEntity be) {
		Minecraft.getInstance().setScreen(new SecureRedstoneInterfaceScreen(be));
	}

	public static void displayAlarmScreen(AlarmBlockEntity be) {
		Minecraft.getInstance().setScreen(new AlarmScreen(be, be.getSound().getLocation()));
	}

	public static void refreshModelData(TileEntity be) {
		BlockPos pos = be.getBlockPos();

		ModelDataManager.requestModelDataRefresh(be);
		Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getInstance().cameraEntity instanceof SecurityCamera;
	}

	public static void putDisguisedBeRenderer(TileEntity disguisableBlockEntity, ItemStack stack) {
		DISGUISED_BLOCK_RENDER_DELEGATE.putDelegateFor(disguisableBlockEntity, NBTUtil.readBlockState(stack.getOrCreateTag().getCompound("SavedState")));
	}

	public static void updateBlockColorAroundPosition(BlockPos pos) {
		Minecraft.getInstance().levelRenderer.blockChanged(Minecraft.getInstance().level, pos, null, null, 0);
	}

	private static float getLinkingState(World level, PlayerEntity player, ItemStack stackInHand, BiPredicate<World, BlockPos> isValidHitResult, int tagSize, BiFunction<CompoundNBT, Integer, Integer[]> getCoords) {
		return getLinkingState(level, player, stackInHand, isValidHitResult, tagSize, getCoords, true, null);
	}

	private static float getLinkingState(World level, PlayerEntity player, ItemStack stackInHand, BiPredicate<World, BlockPos> isValidHitResult, int tagSize, BiFunction<CompoundNBT, Integer, Integer[]> getCoords, boolean loop, BiPredicate<CompoundNBT, BlockPos> useCheckmark) {
		if (level == null)
			level = getClientLevel();

		double reachDistance = Minecraft.getInstance().gameMode.getPickRange();
		double eyeHeight = player.getEyeHeight();
		Vector3d lookVec = new Vector3d(player.getX() + player.getLookAngle().x * reachDistance, eyeHeight + player.getY() + player.getLookAngle().y * reachDistance, player.getZ() + player.getLookAngle().z * reachDistance);
		BlockRayTraceResult hitResult = level.clip(new RayTraceContext(new Vector3d(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

		if (hitResult != null && hitResult.getType() == Type.BLOCK && isValidHitResult.test(level, hitResult.getBlockPos())) {
			if (loop)
				return loop(tagSize, getCoords, stackInHand.getOrCreateTag(), hitResult.getBlockPos());
			else
				return useCheckmark.test(stackInHand.getOrCreateTag(), hitResult.getBlockPos()) ? LINKED_STATE : NOT_LINKED_STATE;
		}

		return UNKNOWN_STATE;
	}

	private static float loop(int tagSize, BiFunction<CompoundNBT, Integer, Integer[]> getCoords, CompoundNBT tag, BlockPos pos) {
		for (int i = 1; i <= tagSize; i++) {
			Integer[] coords = getCoords.apply(tag, i);

			if (coords != null && coords.length == 3 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return LINKED_STATE;
		}

		return NOT_LINKED_STATE;
	}
}
