package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretHangingSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.components.SavedBlockState;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.properties.BlockLinked;
import net.geforcemods.securitycraft.items.properties.CodebreakerState;
import net.geforcemods.securitycraft.items.properties.KeycardCount;
import net.geforcemods.securitycraft.items.properties.ReinforcedTint;
import net.geforcemods.securitycraft.items.properties.SentryLinked;
import net.geforcemods.securitycraft.misc.LayerToggleHandler;
import net.geforcemods.securitycraft.models.BulletModel;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.models.DisplayCaseModel;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.geforcemods.securitycraft.models.SecureRedstoneInterfaceDishModel;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.models.SentryModel;
import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.particle.FloorTrapCloudParticle;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticle;
import net.geforcemods.securitycraft.renderers.BlockPocketManagerRenderer;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.ClaymoreRenderer;
import net.geforcemods.securitycraft.renderers.DisguisableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.DisplayCaseRenderer;
import net.geforcemods.securitycraft.renderers.DisplayCaseSpecialRenderer;
import net.geforcemods.securitycraft.renderers.FrameBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.OwnableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.ProjectorRenderer;
import net.geforcemods.securitycraft.renderers.ReinforcedPistonHeadRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.SecretHangingSignRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignRenderer;
import net.geforcemods.securitycraft.renderers.SecureRedstoneInterfaceRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SecuritySeaBoatRenderer;
import net.geforcemods.securitycraft.renderers.SecuritySeaRaftRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.SonicSecuritySystemRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemRenderer;
import net.geforcemods.securitycraft.screen.AlarmScreen;
import net.geforcemods.securitycraft.screen.BlockChangeDetectorScreen;
import net.geforcemods.securitycraft.screen.BlockPocketManagerScreen;
import net.geforcemods.securitycraft.screen.BlockReinforcerScreen;
import net.geforcemods.securitycraft.screen.BriefcasePasscodeScreen;
import net.geforcemods.securitycraft.screen.CameraSelectScreen;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
	public static final ModelLayerLocation BULLET_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("bullet"), "main");
	public static final ModelLayerLocation IMS_BOMB_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("ims_bomb"), "main");
	public static final ModelLayerLocation DISPLAY_CASE_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("display_case"), "main");
	public static final ModelLayerLocation SENTRY_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("sentry"), "main");
	public static final ModelLayerLocation SECURE_REDSTONE_INTERFACE_DISH_LAYER_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("secure_redstone_interface_dish"), "main");
	public static final ModelLayerLocation SECURITY_CAMERA_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("security_camera"), "main");
	public static final ModelLayerLocation SONIC_SECURITY_SYSTEM_LOCATION = new ModelLayerLocation(SecurityCraft.resLoc("sonic_security_system"), "main");
	public static final BlockEntityRenderDelegate DISGUISED_BLOCK_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	public static final BlockEntityRenderDelegate PROJECTOR_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	public static final ResourceLocation CAMERA_LAYER = SecurityCraft.resLoc("camera_overlay");
	private static Map<Block, Integer> blocksWithReinforcedTint = new HashMap<>();
	private static Map<Block, Integer> blocksWithCustomTint = new HashMap<>();
	//@formatter:off
	private static Supplier<Block[]> disguisableBlocks = Suppliers.memoize(() -> new Block[] {
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
			SCContent.REINFORCED_OBSERVER.get(),
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
	});
    public static final RenderType.CompositeRenderType OVERLAY_LINES = RenderType.create(
			"overlay_lines",
			DefaultVertexFormat.POSITION_COLOR_NORMAL,
			VertexFormat.Mode.LINES,
			1536,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
				.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setOutputState(RenderStateShard.OUTLINE_TARGET)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setCullState(RenderStateShard.NO_CULL)
				.createCompositeState(false));
	//@formatter:on
	public static final EnumProxy<ArmPose> TASER_ARM_POSE_PARAMS = new EnumProxy<>(ArmPose.class, true, (IArmPoseTransformer) (model, entity, arm) -> {
		ModelPart leftArm = model.leftArm;
		ModelPart rightArm = model.rightArm;

		leftArm.yRot = 0.5F;
		rightArm.yRot = -0.5F;
		leftArm.xRot = rightArm.xRot = -1.5F;
	});

	private ClientHandler() {}

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
		Map<ModelResourceLocation, BakedModel> modelRegistry = event.getBakingResult().blockStateModels();

		for (Block block : disguisableBlocks.get()) {
			for (BlockState state : block.getStateDefinition().getPossibleStates()) {
				registerDisguisedModel(modelRegistry, Utils.getRegistryName(block), state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
			}
		}
	}

	private static void registerDisguisedModel(Map<ModelResourceLocation, BakedModel> modelRegistry, ResourceLocation rl, String stateString) {
		ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

		modelRegistry.put(mrl, new DisguisableDynamicBakedModel(modelRegistry.get(mrl)));
	}

	@SubscribeEvent
	public static void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
		event.register(SecurityCraft.resLoc("keycard_count"), KeycardCount.MAP_CODEC);
	}

	@SubscribeEvent
	public static void onRegisterSelectItemModelProperty(RegisterSelectItemModelPropertyEvent event) {
		event.register(SecurityCraft.resLoc("block_linked"), BlockLinked.TYPE);
		event.register(SecurityCraft.resLoc("codebreaker_state"), CodebreakerState.TYPE);
		event.register(SecurityCraft.resLoc("sentry_linked"), SentryLinked.TYPE);
	}

	@SubscribeEvent
	public static void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
		event.register(SecurityCraft.resLoc("display_case"), DisplayCaseSpecialRenderer.Unbaked.MAP_CODEC);
	}

	@SubscribeEvent
	public static void onRegisterItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(SecurityCraft.resLoc("reinforced"), ReinforcedTint.MAP_CODEC);
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutout = RenderType.cutout();
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();

		ItemBlockRenderTypes.setRenderLayer(SCContent.FAKE_WATER.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.FLOWING_FAKE_WATER.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GLASS.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GLASS_PANE.get(), cutoutMipped);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIME_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PINK_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_RED_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.REINFORCED_PALE_MOSS_CARPET.get(), cutout);
		ItemBlockRenderTypes.setRenderLayer(SCContent.TRACK_MINE.get(), cutout);
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(SCContent.BLOCK_REINFORCER_MENU.get(), BlockReinforcerScreen::new);
		event.register(SCContent.BRIEFCASE_INVENTORY_MENU.get(), ItemInventoryScreen.Briefcase::new);
		event.register(SCContent.CUSTOMIZE_BLOCK_MENU.get(), CustomizeBlockScreen::new);
		event.register(SCContent.CUSTOMIZE_ENTITY_MENU.get(), CustomizeBlockScreen::new);
		event.register(SCContent.DISGUISE_MODULE_MENU.get(), DisguiseModuleScreen::new);
		event.register(SCContent.INVENTORY_SCANNER_MENU.get(), InventoryScannerScreen::new);
		event.register(SCContent.KEYPAD_FURNACE_MENU.get(), KeypadFurnaceScreen::new);
		event.register(SCContent.KEYPAD_SMOKER_MENU.get(), KeypadSmokerScreen::new);
		event.register(SCContent.KEYPAD_BLAST_FURNACE_MENU.get(), KeypadBlastFurnaceScreen::new);
		event.register(SCContent.KEYCARD_READER_MENU.get(), KeycardReaderScreen::new);
		event.register(SCContent.BLOCK_POCKET_MANAGER_MENU.get(), BlockPocketManagerScreen::new);
		event.register(SCContent.PROJECTOR_MENU.get(), ProjectorScreen::new);
		event.register(SCContent.BLOCK_CHANGE_DETECTOR_MENU.get(), BlockChangeDetectorScreen::new);
		event.register(SCContent.KEYCARD_HOLDER_MENU.get(), ItemInventoryScreen.KeycardHolder::new);
		event.register(SCContent.TROPHY_SYSTEM_MENU.get(), TrophySystemScreen::new);
		event.register(SCContent.SINGLE_LENS_MENU.get(), SingleLensScreen::new);
		event.register(SCContent.LASER_BLOCK_MENU.get(), LaserBlockScreen::new);
		event.register(SCContent.REINFORCED_LECTERN_MENU.get(), ReinforcedLecternScreen::new);
	}

	@SubscribeEvent
	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(CAMERA_LAYER, SCClientEventHandler::cameraOverlay);
		LayerToggleHandler.disable(CAMERA_LAYER);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SCContent.BOUNCING_BETTY_ENTITY.get(), BouncingBettyRenderer::new);
		event.registerEntityRenderer(SCContent.IMS_BOMB_ENTITY.get(), IMSBombRenderer::new);
		event.registerEntityRenderer(SCContent.SECURITY_CAMERA_ENTITY.get(), NoopRenderer::new);
		event.registerEntityRenderer(SCContent.SENTRY_ENTITY.get(), SentryRenderer::new);
		event.registerEntityRenderer(SCContent.BULLET_ENTITY.get(), BulletRenderer::new);
		event.registerEntityRenderer(SCContent.OAK_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.OAK_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.SPRUCE_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.SPRUCE_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.BIRCH_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.BIRCH_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.JUNGLE_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.JUNGLE_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.ACACIA_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.ACACIA_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.DARK_OAK_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.DARK_OAK_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.MANGROVE_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.MANGROVE_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.CHERRY_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.CHERRY_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.PALE_OAK_SECURITY_SEA_BOAT_ENTITY.get(), ctx -> new SecuritySeaBoatRenderer(ctx, ModelLayers.PALE_OAK_CHEST_BOAT));
		event.registerEntityRenderer(SCContent.BAMBOO_SECURITY_SEA_RAFT_ENTITY.get(), ctx -> new SecuritySeaRaftRenderer(ctx, ModelLayers.BAMBOO_CHEST_RAFT));
		//normal renderers
		event.registerBlockEntityRenderer(SCContent.BLOCK_POCKET_MANAGER_BLOCK_ENTITY.get(), BlockPocketManagerRenderer::new);
		event.registerBlockEntityRenderer(SCContent.CLAYMORE_BLOCK_ENTITY.get(), ClaymoreRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), KeypadChestRenderer::new);
		event.registerBlockEntityRenderer(SCContent.DISPLAY_CASE_BLOCK_ENTITY.get(), ctx -> new DisplayCaseRenderer(ctx, false));
		event.registerBlockEntityRenderer(SCContent.GLOW_DISPLAY_CASE_BLOCK_ENTITY.get(), ctx -> new DisplayCaseRenderer(ctx, true));
		event.registerBlockEntityRenderer(SCContent.FRAME_BLOCK_ENTITY.get(), FrameBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.OWNABLE_BLOCK_ENTITY.get(), OwnableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.REINFORCED_LECTERN_BLOCK_ENTITY.get(), LecternRenderer::new);
		event.registerBlockEntityRenderer(SCContent.PROJECTOR_BLOCK_ENTITY.get(), ProjectorRenderer::new);
		event.registerBlockEntityRenderer(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get(), ReinforcedPistonHeadRenderer::new);
		event.registerBlockEntityRenderer(SCContent.RETINAL_SCANNER_BLOCK_ENTITY.get(), RetinalScannerRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY.get(), SecureRedstoneInterfaceRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get(), SecurityCameraRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SECRET_HANGING_SIGN_BLOCK_ENTITY.get(), SecretHangingSignRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SECRET_SIGN_BLOCK_ENTITY.get(), SecretSignRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SONIC_SECURITY_SYSTEM_BLOCK_ENTITY.get(), SonicSecuritySystemRenderer::new);
		event.registerBlockEntityRenderer(SCContent.TROPHY_SYSTEM_BLOCK_ENTITY.get(), TrophySystemRenderer::new);
		//disguisable block entity renderers
		event.registerBlockEntityRenderer(SCContent.BLOCK_CHANGE_DETECTOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.CAGE_TRAP_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.DISGUISABLE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.FLOOR_TRAP_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.INVENTORY_SCANNER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYCARD_READER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_BARREL_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_DOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_TRAPDOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_SMOKER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.PROTECTO_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.REINFORCED_DISPENSER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.REINFORCED_DROPPER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.REINFORCED_HOPPER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.RIFT_STABILIZER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SCANNER_DOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.SCANNER_TRAPDOOR_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BULLET_LOCATION, BulletModel::createLayer);
		event.registerLayerDefinition(IMS_BOMB_LOCATION, IMSBombModel::createLayer);
		event.registerLayerDefinition(DISPLAY_CASE_LOCATION, DisplayCaseModel::createModelLayer);
		event.registerLayerDefinition(SENTRY_LOCATION, SentryModel::createLayer);
		event.registerLayerDefinition(SECURITY_CAMERA_LOCATION, SecurityCameraModel::createLayer);
		event.registerLayerDefinition(SONIC_SECURITY_SYSTEM_LOCATION, SonicSecuritySystemModel::createLayer);
		event.registerLayerDefinition(SECURE_REDSTONE_INTERFACE_DISH_LAYER_LOCATION, SecureRedstoneInterfaceDishModel::createLayer);
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SCContent.FLOOR_TRAP_CLOUD.get(), FloorTrapCloudParticle.Provider::new);
		event.registerSpriteSet(SCContent.INTERFACE_HIGHLIGHT.get(), InterfaceHighlightParticle.Provider::new);
	}

	@SubscribeEvent
	public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			private static final ArmPose TASER_ARM_POSE = ClientHandler.TASER_ARM_POSE_PARAMS.getValue();

			//first person
			@Override
			public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equippedProgress, float swingProgress) {
				if (swingProgress < 0.001F) {
					pose.translate(0.02F, -0.4F, -0.5F);
					return true;
				}

				return false;
			}

			//third person
			@Override
			public ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
				return TASER_ARM_POSE;
			}
		}, SCContent.TASER.get(), SCContent.TASER_POWERED.get());
	}

	private static void initTint() {
		for (Field field : SCContent.class.getFields()) {
			if (field.isAnnotationPresent(Reinforced.class)) {
				try {
					Block block = ((DeferredBlock<Block>) field.get(null)).get();
					int customTint = field.getAnnotation(Reinforced.class).customTint();

					if (field.getAnnotation(Reinforced.class).hasReinforcedTint())
						blocksWithReinforcedTint.put(block, customTint);
					else if (customTint != 0xFFFFFFFF)
						blocksWithCustomTint.put(block, customTint);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		blocksWithReinforcedTint.put(SCContent.BLOCK_POCKET_MANAGER.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithReinforcedTint.put(SCContent.BLOCK_POCKET_WALL.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_SLAB.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.CHISELED_CRYSTAL_QUARTZ.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_BLOCK.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_BRICKS.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_PILLAR.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.CRYSTAL_QUARTZ_STAIRS.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), SCContent.CRYSTAL_QUARTZ_TINT);
		blocksWithCustomTint.put(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), SCContent.CRYSTAL_QUARTZ_TINT);
	}

	@SubscribeEvent
	public static void onRegisterBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
		initTint();
		blocksWithReinforcedTint.forEach((block, tint) -> event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 0)
				return mixWithReinforcedTintIfEnabled(tint);
			else
				return 0xFFFFFFFF;
		}, block));
		blocksWithCustomTint.forEach((block, tint) -> event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 0)
				return tint;
			else
				return 0xFFFFFFFF;
		}, block));
		event.register((state, level, pos, tintIndex) -> {
			Block block = state.getBlock();

			if (block instanceof IDisguisable disguisedBlock) {
				Block blockFromItem = Block.byItem(disguisedBlock.getDisguisedStack(level, pos).getItem());
				BlockState defaultBlockState = blockFromItem.defaultBlockState();

				if (!defaultBlockState.isAir() && !(blockFromItem instanceof IDisguisable))
					return Minecraft.getInstance().getBlockColors().getColor(defaultBlockState, level, pos, tintIndex);
			}

			if (block instanceof IReinforcedBlock)
				return mixWithReinforcedTintIfEnabled(0xFFFFFFFF);
			else
				return 0xFFFFFFFF;
		}, disguisableBlocks.get());
		event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 1 && !state.getValue(SnowyDirtBlock.SNOWY)) {
				int grassTint = level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return ConfigHandler.CLIENT.reinforcedBlockTintColor.get();
		}, SCContent.REINFORCED_GRASS_BLOCK.get());
		event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 1)
				return level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1;

			return ConfigHandler.CLIENT.reinforcedBlockTintColor.get();
		}, SCContent.REINFORCED_WATER_CAULDRON.get());
		event.register((state, level, pos, tintIndex) -> {
			Direction direction = LaserFieldBlock.getFieldDirection(state);

			return iterateFields(level, pos, direction, ConfigHandler.SERVER.laserBlockRange.get(), SCContent.LASER_BLOCK.get(), LaserBlockBlockEntity.class::isInstance, be -> ((LaserBlockBlockEntity) be).getLensContainer().getItem(direction.getOpposite().ordinal()));
		}, SCContent.LASER_FIELD.get());
		event.register((state, level, pos, tintIndex) -> {
			Direction direction = state.getValue(InventoryScannerFieldBlock.FACING);

			return iterateFields(level, pos, direction, ConfigHandler.SERVER.inventoryScannerRange.get(), SCContent.INVENTORY_SCANNER.get(), InventoryScannerBlockEntity.class::isInstance, be -> ((InventoryScannerBlockEntity) be).getLensContainer().getItem(0));
		}, SCContent.INVENTORY_SCANNER_FIELD.get());
	}

	public static int iterateFields(BlockAndTintGetter level, BlockPos pos, Direction direction, int range, Block block, Predicate<BlockEntity> beTest, Function<BlockEntity, ItemStack> lensGetter) {
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

	private static int iterateFieldsInternal(BlockAndTintGetter level, BlockPos pos, Direction direction, int range, Block block, Predicate<BlockEntity> beTest, Function<BlockEntity, ItemStack> lensGetter) throws ArrayIndexOutOfBoundsException {
		MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

		for (int i = 0; i < range; i++) {
			if (level.getBlockState(mutablePos).is(block)) {
				BlockEntity be = level.getBlockEntity(mutablePos);

				if (beTest.test(be)) {
					ItemStack stack = lensGetter.apply(be);

					if (stack.has(DataComponents.DYED_COLOR))
						return stack.get(DataComponents.DYED_COLOR).rgb();

					break;
				}
			}

			mutablePos.move(direction);
		}

		return -1;
	}

	public static int mixWithReinforcedTintIfEnabled(int tint) {
		boolean tintReinforcedBlocks;

		if (Minecraft.getInstance().level == null)
			tintReinforcedBlocks = ConfigHandler.CLIENT.reinforcedBlockTint.get();
		else
			tintReinforcedBlocks = ConfigHandler.SERVER.forceReinforcedBlockTint.get() ? ConfigHandler.SERVER.reinforcedBlockTint.get() : ConfigHandler.CLIENT.reinforcedBlockTint.get();

		return tintReinforcedBlocks ? ARGB.multiply(tint, 0xFF000000 | ConfigHandler.CLIENT.reinforcedBlockTintColor.get()) : tint;
	}

	@SubscribeEvent
	public static void onRegisterShaders(RegisterShadersEvent event) {
		ShaderProgram shader = new ShaderProgram(SecurityCraft.resLoc("frame_draw_fb_in_area"), DefaultVertexFormat.POSITION_TEX, ShaderDefines.EMPTY);

		CameraController.cameraMonitorShader = shader;
		event.registerShader(shader);
	}

	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public static Level getClientLevel() {
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

	public static void displayCameraMonitorScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new CameraSelectScreen(stack.getOrDefault(SCContent.BOUND_CAMERAS, CameraMonitorItem.DEFAULT_NAMED_POSITIONS).positions(), pos -> CameraMonitorItem.removeCameraOnClient(pos, stack), pos -> PacketDistributor.sendToServer(new MountCamera(pos.pos())), false, false));
	}

	public static void displayFrameScreen(FrameBlockEntity be, boolean readOnly) {
		Minecraft.getInstance().setScreen(new CameraSelectScreen(be.getCameraPositions(), readOnly ? null : be::removeCameraOnClient, be::setCurrentCameraAndUpdate, true, be.getCurrentCamera() != null));
	}

	public static void displaySCManualScreen() {
		Minecraft.getInstance().setScreen(new SCManualScreen());
	}

	public static void displayEditSecretSignScreen(SecretSignBlockEntity be, boolean isFront) {
		Minecraft.getInstance().setScreen(new SignEditScreen(be, isFront, Minecraft.getInstance().isTextFilteringEnabled()));
	}

	public static void displayEditSecretHangingSignScreen(SecretHangingSignBlockEntity be, boolean isFront) {
		Minecraft.getInstance().setScreen(new HangingSignEditScreen(be, isFront, Minecraft.getInstance().isTextFilteringEnabled()));
	}

	public static void displaySonicSecuritySystemScreen(SonicSecuritySystemBlockEntity be) {
		Minecraft.getInstance().setScreen(new SonicSecuritySystemScreen(be));
	}

	public static void displayBriefcasePasscodeScreen(Component title) {
		Minecraft.getInstance().setScreen(new BriefcasePasscodeScreen(title, false));
	}

	public static void displayBriefcaseSetupScreen(Component title) {
		Minecraft.getInstance().setScreen(new BriefcasePasscodeScreen(title, true));
	}

	public static void displayUsernameLoggerScreen(UsernameLoggerBlockEntity be) {
		Minecraft.getInstance().setScreen(new UsernameLoggerScreen(be));
	}

	public static void displayUniversalKeyChangerScreen(BlockEntity be) {
		Minecraft.getInstance().setScreen(new KeyChangerScreen((IPasscodeProtected) be));
	}

	public static void displayUniversalKeyChangerScreen(Entity entity) {
		Minecraft.getInstance().setScreen(new KeyChangerScreen((IPasscodeProtected) entity));
	}

	public static void displayCheckPasscodeScreen(BlockEntity be) {
		Component displayName = be instanceof Nameable nameable ? nameable.getDisplayName() : Component.translatable(be.getBlockState().getBlock().getDescriptionId());

		Minecraft.getInstance().setScreen(new CheckPasscodeScreen((IPasscodeProtected) be, displayName));
	}

	public static void displayCheckPasscodeScreen(Entity entity) {
		Minecraft.getInstance().setScreen(new CheckPasscodeScreen((IPasscodeProtected) entity, entity.getDisplayName()));
	}

	public static void displaySetPasscodeScreen(BlockEntity be) {
		Component displayName = be instanceof Nameable nameable ? nameable.getDisplayName() : Component.translatable(be.getBlockState().getBlock().getDescriptionId());

		Minecraft.getInstance().setScreen(new SetPasscodeScreen((IPasscodeProtected) be, displayName));
	}

	public static void displaySetPasscodeScreen(Entity entity) {
		Minecraft.getInstance().setScreen(new SetPasscodeScreen((IPasscodeProtected) entity, entity.getDisplayName()));
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
		Minecraft.getInstance().setScreen(new AlarmScreen(be, be.getSound().location()));
	}

	public static void refreshModelData(BlockEntity be) {
		BlockPos pos = be.getBlockPos();

		Minecraft.getInstance().level.getModelDataManager().requestRefresh(be);
		Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getInstance().cameraEntity instanceof SecurityCamera;
	}

	public static void putDisguisedBeRenderer(BlockEntity disguisableBlockEntity, ItemStack stack) {
		DISGUISED_BLOCK_RENDER_DELEGATE.putDelegateFor(disguisableBlockEntity, stack.getOrDefault(SCContent.SAVED_BLOCK_STATE, SavedBlockState.EMPTY).state());
	}

	public static void updateBlockColorAroundPosition(BlockPos pos) {
		Minecraft.getInstance().levelRenderer.blockChanged(Minecraft.getInstance().level, pos, null, null, 0);
	}
}
