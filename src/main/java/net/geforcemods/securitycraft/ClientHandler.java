package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;

import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretHangingSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
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
import net.geforcemods.securitycraft.misc.FloorTrapCloudParticle;
import net.geforcemods.securitycraft.misc.OverlayToggleHandler;
import net.geforcemods.securitycraft.models.BlockMineModel;
import net.geforcemods.securitycraft.models.BulletModel;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.models.SentryModel;
import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.renderers.BlockPocketManagerRenderer;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.ClaymoreRenderer;
import net.geforcemods.securitycraft.renderers.DisguisableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.DisplayCaseRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.ProjectorRenderer;
import net.geforcemods.securitycraft.renderers.ReinforcedPistonHeadRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.SecretHangingSignRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SecuritySeaBoatRenderer;
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
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
	public static final float EMPTY_STATE = 0.0F, UNKNOWN_STATE = 0.25F, NOT_LINKED_STATE = 0.5F, LINKED_STATE = 0.75F;
	public static final ModelLayerLocation BULLET_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "bullet"), "main");
	public static final ModelLayerLocation IMS_BOMB_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "ims_bomb"), "main");
	public static final ModelLayerLocation DISPLAY_CASE_LOCATION = new ModelLayerLocation(new ResourceLocation("securitycraft", "display_case"), "main");
	public static final ModelLayerLocation GLOW_DISPLAY_CASE_LOCATION = new ModelLayerLocation(new ResourceLocation("securitycraft", "glow_display_case"), "main");
	public static final ModelLayerLocation SENTRY_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "sentry"), "main");
	public static final ModelLayerLocation SECURITY_CAMERA_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "security_camera"), "main");
	public static final ModelLayerLocation SONIC_SECURITY_SYSTEM_LOCATION = new ModelLayerLocation(new ResourceLocation(SecurityCraft.MODID, "sonic_security_system"), "main");
	public static final BlockEntityRenderDelegate DISGUISED_BLOCK_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	public static final BlockEntityRenderDelegate PROJECTOR_RENDER_DELEGATE = new BlockEntityRenderDelegate();
	public static IGuiOverlay cameraOverlay;
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
			SCContent.KEYPAD_FURNACE.get(),
			SCContent.KEYPAD_SMOKER.get(),
			SCContent.LASER_BLOCK.get(),
			SCContent.PROJECTOR.get(),
			SCContent.PROTECTO.get(),
			SCContent.REINFORCED_OBSERVER.get(),
			SCContent.RETINAL_SCANNER.get(),
			SCContent.RIFT_STABILIZER.get(),
			SCContent.SENTRY_DISGUISE.get(),
			SCContent.TROPHY_SYSTEM.get(),
			SCContent.USERNAME_LOGGER.get()
	});
	//@formatter:on
	public static final ArmPose TASER_ARM_POSE = ArmPose.create("securitycraft_taser", true, (model, entity, arm) -> {
		ModelPart leftArm = model.leftArm;
		ModelPart rightArm = model.rightArm;

		leftArm.yRot = 0.5F;
		rightArm.yRot = -0.5F;
		leftArm.xRot = rightArm.xRot = -1.5F;
	});
	public static final ResourceLocation LINKING_STATE_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "linking_state");

	private ClientHandler() {}

	@SubscribeEvent
	public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
		//@formatter:off
		String[] mines = {
				"ancient_debris",
				"blast_furnace",
				"coal_ore",
				"cobbled_deepslate",
				"cobblestone",
				"copper_ore",
				"deepslate",
				"deepslate_coal_ore",
				"deepslate_copper_ore",
				"deepslate_diamond_ore",
				"deepslate_emerald_ore",
				"deepslate_gold_ore",
				"deepslate_iron_ore",
				"deepslate_lapis_ore",
				"deepslate_redstone_ore",
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
				"stone",
				"suspicious_sand"
		};
		//@formatter:on

		Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();

		for (Block block : disguisableBlocks.get()) {
			for (BlockState state : block.getStateDefinition().getPossibleStates()) {
				registerDisguisedModel(modelRegistry, Utils.getRegistryName(block), state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
			}
		}

		for (String mine : mines) {
			registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, mine.replace("_ore", "") + "_mine"), new ResourceLocation(mine));
		}

		registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, "quartz_mine"), new ResourceLocation("nether_quartz_ore"));
	}

	private static void registerDisguisedModel(Map<ResourceLocation, BakedModel> modelRegistry, ResourceLocation rl, String stateString) {
		ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

		modelRegistry.put(mrl, new DisguisableDynamicBakedModel(modelRegistry.get(mrl)));
	}

	private static void registerBlockMineModel(ModelEvent.ModifyBakingResult event, ResourceLocation mineRl, ResourceLocation realBlockRl) {
		ModelResourceLocation mineMrl = new ModelResourceLocation(mineRl, "inventory");

		event.getModels().put(mineMrl, new BlockMineModel(event.getModels().get(new ModelResourceLocation(realBlockRl, "inventory")), event.getModels().get(mineMrl)));
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType translucent = RenderType.translucent();

		ItemBlockRenderTypes.setRenderLayer(SCContent.FAKE_WATER.get(), translucent);
		ItemBlockRenderTypes.setRenderLayer(SCContent.FLOWING_FAKE_WATER.get(), translucent);
		event.enqueueWork(() -> {
			MenuScreens.register(SCContent.BLOCK_REINFORCER_MENU.get(), BlockReinforcerScreen::new);
			MenuScreens.register(SCContent.BRIEFCASE_INVENTORY_MENU.get(), ItemInventoryScreen.Briefcase::new);
			MenuScreens.register(SCContent.CUSTOMIZE_BLOCK_MENU.get(), CustomizeBlockScreen::new);
			MenuScreens.register(SCContent.CUSTOMIZE_ENTITY_MENU.get(), CustomizeBlockScreen::new);
			MenuScreens.register(SCContent.DISGUISE_MODULE_MENU.get(), DisguiseModuleScreen::new);
			MenuScreens.register(SCContent.INVENTORY_SCANNER_MENU.get(), InventoryScannerScreen::new);
			MenuScreens.register(SCContent.KEYPAD_FURNACE_MENU.get(), KeypadFurnaceScreen::new);
			MenuScreens.register(SCContent.KEYPAD_SMOKER_MENU.get(), KeypadSmokerScreen::new);
			MenuScreens.register(SCContent.KEYPAD_BLAST_FURNACE_MENU.get(), KeypadBlastFurnaceScreen::new);
			MenuScreens.register(SCContent.KEYCARD_READER_MENU.get(), KeycardReaderScreen::new);
			MenuScreens.register(SCContent.BLOCK_POCKET_MANAGER_MENU.get(), BlockPocketManagerScreen::new);
			MenuScreens.register(SCContent.PROJECTOR_MENU.get(), ProjectorScreen::new);
			MenuScreens.register(SCContent.BLOCK_CHANGE_DETECTOR_MENU.get(), BlockChangeDetectorScreen::new);
			MenuScreens.register(SCContent.KEYCARD_HOLDER_MENU.get(), ItemInventoryScreen.KeycardHolder::new);
			MenuScreens.register(SCContent.TROPHY_SYSTEM_MENU.get(), TrophySystemScreen::new);
			MenuScreens.register(SCContent.SINGLE_LENS_MENU.get(), SingleLensScreen::new);
			MenuScreens.register(SCContent.LASER_BLOCK_MENU.get(), LaserBlockScreen::new);
			MenuScreens.register(SCContent.REINFORCED_LECTERN_MENU.get(), ReinforcedLecternScreen::new);
			ItemProperties.register(SCContent.KEYCARD_HOLDER.get(), KeycardHolderItem.COUNT_PROPERTY, (stack, level, entity, id) -> KeycardHolderItem.getCardCount(stack) / (float) KeycardHolderMenu.CONTAINER_SIZE);
			ItemProperties.register(SCContent.LENS.get(), LensItem.COLOR_PROPERTY, (stack, level, entity, id) -> ((DyeableLeatherItem) stack.getItem()).hasCustomColor(stack) ? 1.0F : 0.0F);
			ItemProperties.register(SCContent.CAMERA_MONITOR.get(), LINKING_STATE_PROPERTY, (stack, level, entity, id) -> {
				if (!(entity instanceof Player player))
					return EMPTY_STATE;

				float linkingState = getLinkingState(level, player, stack, bhr -> level.getBlockEntity(bhr.getBlockPos()) instanceof SecurityCameraBlockEntity, 30, (tag, i) -> {
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
			ItemProperties.register(SCContent.MINE_REMOTE_ACCESS_TOOL.get(), LINKING_STATE_PROPERTY, (stack, level, entity, id) -> {
				if (!(entity instanceof Player player))
					return EMPTY_STATE;

				float linkingState = getLinkingState(level, player, stack, bhr -> level.getBlockState(bhr.getBlockPos()).getBlock() instanceof IExplosive, 30, (tag, i) -> {
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
			ItemProperties.register(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(), LINKING_STATE_PROPERTY, (stack, level, entity, id) -> {
				if (!(entity instanceof Player))
					return EMPTY_STATE;

				if (Minecraft.getInstance().crosshairPickEntity instanceof Sentry sentry) {
					float linkingState = loop(12, (tag, i) -> Arrays.stream(tag.getIntArray("sentry" + i)).boxed().toArray(Integer[]::new), stack.getOrCreateTag(), sentry.blockPosition());

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
			ItemProperties.register(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(), LINKING_STATE_PROPERTY, (stack, level, entity, id) -> {
				if (!(entity instanceof Player player))
					return EMPTY_STATE;

				float linkingState = getLinkingState(level, player, stack, bhr -> {
					if (!(level.getBlockEntity(bhr.getBlockPos()) instanceof ILockable lockable))
						return false;

					//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
					if (!(lockable instanceof IOwnable ownable) || !ownable.isOwnedBy(player)) {
						if (DisguisableBlock.getDisguisedBlockState(level, bhr.getBlockPos()).isPresent())
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
			ItemProperties.register(SCContent.CODEBREAKER.get(), CodebreakerItem.STATE_PROPERTY, (stack, level, entity, id) -> {
				CompoundTag tag = stack.getTag();

				if (CodebreakerItem.wasRecentlyUsed(stack))
					return tag.getBoolean(CodebreakerItem.WAS_SUCCESSFUL) ? 0.75F : 0.5F;

				if (!(entity instanceof Player player))
					return 0.0F;

				float state = getLinkingState(level, player, stack, bhr -> level.getBlockEntity(bhr.getBlockPos()) instanceof ICodebreakable, 0, null, false, (_tag, pos) -> true);

				if (state == LINKED_STATE || state == NOT_LINKED_STATE)
					return 0.25F;
				else
					return 0.0F;
			});
		});
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
		cameraOverlay = SCClientEventHandler::cameraOverlay;
		event.registerAboveAll("camera_overlay", cameraOverlay);
		OverlayToggleHandler.disable(cameraOverlay);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(SCContent.BOUNCING_BETTY_ENTITY.get(), BouncingBettyRenderer::new);
		event.registerEntityRenderer(SCContent.IMS_BOMB_ENTITY.get(), IMSBombRenderer::new);
		event.registerEntityRenderer(SCContent.SECURITY_CAMERA_ENTITY.get(), NoopRenderer::new);
		event.registerEntityRenderer(SCContent.SENTRY_ENTITY.get(), SentryRenderer::new);
		event.registerEntityRenderer(SCContent.BULLET_ENTITY.get(), BulletRenderer::new);
		event.registerEntityRenderer(SCContent.SECURITY_SEA_BOAT_ENTITY.get(), SecuritySeaBoatRenderer::new);
		//normal renderers
		event.registerBlockEntityRenderer(SCContent.BLOCK_POCKET_MANAGER_BLOCK_ENTITY.get(), BlockPocketManagerRenderer::new);
		event.registerBlockEntityRenderer(SCContent.CLAYMORE_BLOCK_ENTITY.get(), ClaymoreRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_CHEST_BLOCK_ENTITY.get(), KeypadChestRenderer::new);
		event.registerBlockEntityRenderer(SCContent.DISPLAY_CASE_BLOCK_ENTITY.get(), ctx -> new DisplayCaseRenderer(ctx, false));
		event.registerBlockEntityRenderer(SCContent.GLOW_DISPLAY_CASE_BLOCK_ENTITY.get(), ctx -> new DisplayCaseRenderer(ctx, true));
		event.registerBlockEntityRenderer(SCContent.REINFORCED_LECTERN_BLOCK_ENTITY.get(), LecternRenderer::new);
		event.registerBlockEntityRenderer(SCContent.PROJECTOR_BLOCK_ENTITY.get(), ProjectorRenderer::new);
		event.registerBlockEntityRenderer(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get(), ReinforcedPistonHeadRenderer::new);
		event.registerBlockEntityRenderer(SCContent.RETINAL_SCANNER_BLOCK_ENTITY.get(), RetinalScannerRenderer::new);
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
		event.registerBlockEntityRenderer(SCContent.KEYPAD_FURNACE_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.KEYPAD_SMOKER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.LASER_BLOCK_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.PROTECTO_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.RIFT_STABILIZER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), DisguisableBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BULLET_LOCATION, BulletModel::createLayer);
		event.registerLayerDefinition(IMS_BOMB_LOCATION, IMSBombModel::createLayer);
		event.registerLayerDefinition(DISPLAY_CASE_LOCATION, DisplayCaseRenderer::createModelLayer);
		event.registerLayerDefinition(GLOW_DISPLAY_CASE_LOCATION, DisplayCaseRenderer::createModelLayer);
		event.registerLayerDefinition(SENTRY_LOCATION, SentryModel::createLayer);
		event.registerLayerDefinition(SECURITY_CAMERA_LOCATION, SecurityCameraModel::createLayer);
		event.registerLayerDefinition(SONIC_SECURITY_SYSTEM_LOCATION, SonicSecuritySystemModel::createLayer);
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SCContent.FLOOR_TRAP_CLOUD.get(), FloorTrapCloudParticle.Provider::new);
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
	public static void onRegisterColorHandlersBlock(RegisterColorHandlersEvent.Block event) {
		initTint();
		blocksWithReinforcedTint.forEach((block, tint) -> event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 0)
				return mixWithReinforcedTintIfEnabled(tint);
			else
				return 0xFFFFFF;
		}, block));
		blocksWithCustomTint.forEach((block, tint) -> event.register((state, level, pos, tintIndex) -> {
			if (tintIndex == 0)
				return tint;
			else
				return 0xFFFFFF;
		}, block));
		event.register((state, level, pos, tintIndex) -> {
			Block block = state.getBlock();

			if (block instanceof DisguisableBlock disguisedBlock) {
				Block blockFromItem = Block.byItem(disguisedBlock.getDisguisedStack(level, pos).getItem());
				BlockState defaultBlockState = blockFromItem.defaultBlockState();

				if (!defaultBlockState.isAir() && !(blockFromItem instanceof DisguisableBlock))
					return Minecraft.getInstance().getBlockColors().getColor(defaultBlockState, level, pos, tintIndex);
			}

			if (block == SCContent.REINFORCED_OBSERVER.get())
				return mixWithReinforcedTintIfEnabled(0xFFFFFF);
			else
				return 0xFFFFFF;
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
			MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

			for (int i = 0; i < ConfigHandler.SERVER.laserBlockRange.get(); i++) {
				if (level.getBlockState(mutablePos).is(SCContent.LASER_BLOCK.get()) && level.getBlockEntity(mutablePos) instanceof LaserBlockBlockEntity be) {
					ItemStack stack = be.getLensContainer().getItem(direction.getOpposite().ordinal());

					if (stack.getItem() instanceof DyeableLeatherItem lens)
						return lens.getColor(stack);

					break;
				}
				else
					mutablePos.move(direction);
			}

			return -1;
		}, SCContent.LASER_FIELD.get());
		event.register((state, level, pos, tintIndex) -> {
			Direction direction = state.getValue(InventoryScannerFieldBlock.FACING);
			MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

			for (int i = 0; i < ConfigHandler.SERVER.inventoryScannerRange.get(); i++) {
				if (level.getBlockState(mutablePos).is(SCContent.INVENTORY_SCANNER.get()) && level.getBlockEntity(mutablePos) instanceof InventoryScannerBlockEntity be) {
					ItemStack stack = be.getLensContainer().getItem(0);

					if (stack.getItem() instanceof DyeableLeatherItem lens)
						return lens.getColor(stack);

					break;
				}
				else
					mutablePos.move(direction);
			}

			return -1;
		}, SCContent.INVENTORY_SCANNER_FIELD.get());
	}

	@SubscribeEvent
	public static void onRegisterColorHandlersItem(RegisterColorHandlersEvent.Item event) {
		blocksWithReinforcedTint.forEach((item, tint) -> event.register((stack, tintIndex) -> {
			if (tintIndex == 0)
				return mixWithReinforcedTintIfEnabled(tint);
			else
				return 0xFFFFFF;
		}, item));
		blocksWithCustomTint.forEach((item, tint) -> event.register((stack, tintIndex) -> {
			if (tintIndex == 0)
				return tint;
			else
				return 0xFFFFFF;
		}, item));
		event.register((stack, tintIndex) -> {
			if (tintIndex == 0) {
				DyeableLeatherItem item = ((DyeableLeatherItem) stack.getItem());

				if (item.hasCustomColor(stack))
					return item.getColor(stack);
				else
					return 0x333333;
			}
			else
				return -1;
		}, SCContent.BRIEFCASE.get());
		event.register((stack, tintIndex) -> {
			if (tintIndex == 0) {
				DyeableLeatherItem item = ((DyeableLeatherItem) stack.getItem());

				if (item.hasCustomColor(stack))
					return item.getColor(stack);
			}

			return -1;
		}, SCContent.LENS.get());
		event.register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				int grassTint = GrassColor.get(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return ConfigHandler.CLIENT.reinforcedBlockTintColor.get();
		}, SCContent.REINFORCED_GRASS_BLOCK.get());
		blocksWithReinforcedTint = null;
		blocksWithCustomTint = null;
	}

	public static int mixWithReinforcedTintIfEnabled(int tint) {
		boolean tintReinforcedBlocks;

		if (Minecraft.getInstance().level == null)
			tintReinforcedBlocks = ConfigHandler.CLIENT.reinforcedBlockTint.get();
		else
			tintReinforcedBlocks = ConfigHandler.SERVER.forceReinforcedBlockTint.get() ? ConfigHandler.SERVER.reinforcedBlockTint.get() : ConfigHandler.CLIENT.reinforcedBlockTint.get();

		return tintReinforcedBlocks ? FastColor.ARGB32.multiply(tint, ConfigHandler.CLIENT.reinforcedBlockTintColor.get()) : tint;
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

	public static void displayCameraMonitorScreen(Inventory inv, CameraMonitorItem item, CompoundTag stackTag) {
		Minecraft.getInstance().setScreen(new CameraMonitorScreen(inv, item, stackTag));
	}

	public static void displaySCManualScreen() {
		Minecraft.getInstance().setScreen(new SCManualScreen());
	}

	public static void displayEditSecretSignScreen(SecretSignBlockEntity be) {
		Minecraft.getInstance().setScreen(new SignEditScreen(be, Minecraft.getInstance().isTextFilteringEnabled()));
	}

	public static void displayEditSecretHangingSignScreen(SecretHangingSignBlockEntity be) {
		Minecraft.getInstance().setScreen(new HangingSignEditScreen(be, Minecraft.getInstance().isTextFilteringEnabled()));
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

	public static void displayUsernameLoggerScreen(BlockPos pos) {
		if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof UsernameLoggerBlockEntity be) {
			if (be.isDisabled())
				getClientPlayer().displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else
				Minecraft.getInstance().setScreen(new UsernameLoggerScreen(be));
		}
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

	public static void displayAlarmScreen(AlarmBlockEntity be) {
		Minecraft.getInstance().setScreen(new AlarmScreen(be, be.getSound().getLocation()));
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
		DISGUISED_BLOCK_RENDER_DELEGATE.putDelegateFor(disguisableBlockEntity, NbtUtils.readBlockState(disguisableBlockEntity.getLevel().holderLookup(Registries.BLOCK), stack.getOrCreateTag().getCompound("SavedState")));
	}

	public static void updateBlockColorAroundPosition(BlockPos pos) {
		Minecraft.getInstance().levelRenderer.blockChanged(Minecraft.getInstance().level, pos, null, null, 0);
	}

	private static float getLinkingState(Level level, Player player, ItemStack stackInHand, Predicate<BlockHitResult> isValidHitResult, int tagSize, BiFunction<CompoundTag, Integer, Integer[]> getCoords) {
		return getLinkingState(level, player, stackInHand, isValidHitResult, tagSize, getCoords, true, null);
	}

	protected static float getLinkingState(Level level, Player player, ItemStack stackInHand, Predicate<BlockHitResult> isValidHitResult, int tagSize, BiFunction<CompoundTag, Integer, Integer[]> getCoords, boolean loop, BiPredicate<CompoundTag, BlockPos> useCheckmark) {
		double reachDistance = player.getBlockReach();
		double eyeHeight = player.getEyeHeight();
		Vec3 lookVec = new Vec3(player.getX() + player.getLookAngle().x * reachDistance, eyeHeight + player.getY() + player.getLookAngle().y * reachDistance, player.getZ() + player.getLookAngle().z * reachDistance);

		if (level != null) {
			BlockHitResult hitResult = level.clip(new ClipContext(new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), lookVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

			if (hitResult != null && hitResult.getType() == Type.BLOCK && isValidHitResult.test(hitResult)) {
				if (loop)
					return loop(tagSize, getCoords, stackInHand.getOrCreateTag(), hitResult.getBlockPos());
				else
					return useCheckmark.test(stackInHand.getOrCreateTag(), hitResult.getBlockPos()) ? LINKED_STATE : NOT_LINKED_STATE;
			}
		}

		return UNKNOWN_STATE;
	}

	private static float loop(int tagSize, BiFunction<CompoundTag, Integer, Integer[]> getCoords, CompoundTag tag, BlockPos pos) {
		for (int i = 1; i <= tagSize; i++) {
			Integer[] coords = getCoords.apply(tag, i);

			if (coords != null && coords.length == 3 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return LINKED_STATE;
		}

		return NOT_LINKED_STATE;
	}
}
