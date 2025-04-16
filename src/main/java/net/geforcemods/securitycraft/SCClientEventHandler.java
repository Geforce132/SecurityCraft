package net.geforcemods.securitycraft;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = SecurityCraft.MODID)
public class SCClientEventHandler {
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("minecraft:textures/gui/container/inventory.png");
	public static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
	private static final TextComponentTranslation REDSTONE_NOTE = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");
	private static final TextComponentTranslation SMART_MODULE_NOTE = Utils.localize("gui.securitycraft:camera.smartModuleNote");
	//@formatter:off
	private static final CameraKeyInfoEntry[] CAMERA_KEY_INFO_LIST = {
			new CameraKeyInfoEntry(() -> true, options -> Utils.localize("gui.securitycraft:camera.lookAround", options.keyBindForward.getDisplayName(), options.keyBindLeft.getDisplayName(), options.keyBindBack.getDisplayName(), options.keyBindRight.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> true, options -> Utils.localize("gui.securitycraft:camera.exit", options.keyBindSneak.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.getDisplayName(), KeyBindings.cameraZoomOut.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> ConfigHandler.allowCameraNightVision, $ -> Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.getDisplayName()), $ -> true),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.getDisplayName()), be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> REDSTONE_NOTE, be -> be.isModuleEnabled(ModuleType.REDSTONE)),
			new CameraKeyInfoEntry(() -> true, $ -> Utils.localize("gui.securitycraft:camera.setDefaultViewingDirection", KeyBindings.setDefaultViewingDirection.getDisplayName()), be -> be.isModuleEnabled(ModuleType.SMART)),
			new CameraKeyInfoEntry(() -> true, $ -> SMART_MODULE_NOTE, be -> be.isModuleEnabled(ModuleType.SMART))
	};
	//@formatter:on

	private SCClientEventHandler() {}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		World level = mc.world;
		float partialTicks = event.getPartialTicks();
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		for (BlockPos bcdPos : BlockEntityTracker.BLOCK_CHANGE_DETECTOR.getTrackedTileEntities(level)) {
			TileEntity be = level.getTileEntity(bcdPos);

			if (!(be instanceof BlockChangeDetectorBlockEntity))
				continue;

			BlockChangeDetectorBlockEntity bcd = (BlockChangeDetectorBlockEntity) be;

			if (bcd.isShowingHighlights() && bcd.isOwnedBy(mc.player)) {
				for (ChangeEntry changeEntry : bcd.getFilteredEntries()) {
					BlockPos pos = changeEntry.pos;

					GlStateManager.pushMatrix();
					GlStateManager.disableDepth();
					GlStateManager.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
					ClientUtils.renderBoxInLevel(0, 1, 0, 1, 1, bcd.getColor());
					GlStateManager.enableDepth();
					GlStateManager.popMatrix();
				}
			}
		}
	}

	@SubscribeEvent
	public static void renderHandEvent(RenderSpecificHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if (PlayerUtils.isPlayerMountedOnCamera(player))
			event.setCanceled(true);
		else {
			boolean mainHandTaser = player.getHeldItemMainhand().getItem() instanceof TaserItem;
			boolean offhandTaser = player.getHeldItemOffhand().getItem() instanceof TaserItem;

			if (mainHandTaser || offhandTaser) {
				boolean isRightHanded = mc.gameSettings.mainHand == EnumHandSide.RIGHT;
				boolean isMainHand = event.getHand() == EnumHand.MAIN_HAND;

				if (mainHandTaser && offhandTaser)
					event.setCanceled(!isMainHand);
				else if ((isMainHand && offhandTaser || !isMainHand && mainHandTaser)) {
					event.setCanceled(true);
					return;
				}

				if (isRightHanded == isMainHand)
					GlStateManager.translate(-0.54F, 0.0F, 0.0F);
				else
					GlStateManager.translate(0.58F, 0.0F, 0.0F);
			}
		}
	}

	@SubscribeEvent
	public static void onClickInput(MouseEvent event) {
		if (event.getButton() == 0 && ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getWorld().isRemote) {
			ChunkPos pos = event.getChunk().getPos();

			CameraViewAreaExtension.onChunkUnload(pos.x, pos.z);
		}
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if ((event.getType() == ElementType.EXPERIENCE || event.getType() == ElementType.JUMPBAR || event.getType() == ElementType.POTION_ICONS) && ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL && ClientProxy.isPlayerMountedOnCamera()) {
			Minecraft mc = Minecraft.getMinecraft();

			drawCameraOverlay(mc, mc.ingameGUI, event.getResolution(), mc.player, mc.world, new BlockPos(mc.getRenderViewEntity().getPositionVector()));
		}
	}

	public static void drawCameraOverlay(Minecraft mc, Gui gui, ScaledResolution resolution, EntityPlayer player, World world, BlockPos pos) {
		if (mc.gameSettings.showDebugInfo)
			return;

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof SecurityCameraBlockEntity))
			return;

		int scaledWidth = resolution.getScaledWidth();
		int scaledHeight = resolution.getScaledHeight();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		SecurityCameraBlockEntity te = (SecurityCameraBlockEntity) tile;
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		IBlockState state = world.getBlockState(pos);
		long worldTime = Minecraft.getMinecraft().world.provider.getWorldTime();
		int hours24 = (int) ((float) worldTime / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (worldTime / 16.666666F % 60.0F);
		String time = String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
		int timeY = 25;

		if (te.hasCustomName()) {
			String cameraName = te.getName();

			font.drawStringWithShadow(cameraName, scaledWidth - font.getStringWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		font.drawStringWithShadow(time, scaledWidth - font.getStringWidth(time) - 4, timeY, 16777215);

		int heightOffset = 10;

		for (int i = CAMERA_KEY_INFO_LIST.length - 1; i >= 0; i--) {
			CameraKeyInfoEntry entry = CAMERA_KEY_INFO_LIST[i];

			if (entry.enabled().get()) {
				entry.drawString(settings, font, scaledWidth, scaledHeight, heightOffset, te);
				heightOffset += 10;
			}
		}

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawTexturedModalRect(5, 0, 0, 0, 90, 20);
		gui.drawTexturedModalRect(scaledWidth - 70, 5, 190, 0, 65, 30);

		if (player.getActivePotionEffect(Potion.getPotionFromResourceLocation("night_vision")) == null)
			gui.drawTexturedModalRect(28, 4, 90, 12, 16, 11);
		else {
			mc.getTextureManager().bindTexture(NIGHT_VISION);
			gui.drawTexturedModalRect(25, 2, 70, 218, 19, 16);
		}

		if (state.getWeakPower(world, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
			if (!te.isModuleEnabled(ModuleType.REDSTONE))
				CameraRedstoneModuleState.NOT_INSTALLED.render(gui, 12, 2);
			else
				CameraRedstoneModuleState.DEACTIVATED.render(gui, 12, 2);
		}
		else
			CameraRedstoneModuleState.ACTIVATED.render(gui, 12, 2);
	}

	public static final class CameraKeyInfoEntry {
		private final Supplier<Boolean> enabled;
		private final Function<GameSettings, TextComponentTranslation> text;
		private final Predicate<SecurityCameraBlockEntity> whiteText;

		public CameraKeyInfoEntry(Supplier<Boolean> enabled, Function<GameSettings, TextComponentTranslation> text, Predicate<SecurityCameraBlockEntity> whiteText) {
			this.enabled = enabled;
			this.text = text;
			this.whiteText = whiteText;
		}

		public void drawString(GameSettings options, FontRenderer font, int scaledWidth, int scaledHeight, int heightOffset, SecurityCameraBlockEntity be) {
			String formattedText = text().apply(options).getFormattedText();
			boolean shouldTextBeWhite = whiteText().test(be);

			font.drawStringWithShadow(formattedText, scaledWidth - font.getStringWidth(formattedText) - 8, scaledHeight - heightOffset, shouldTextBeWhite ? 0xFFFFFF : 0xFF3377);
		}

		public Supplier<Boolean> enabled() {
			return enabled;
		}

		public Function<GameSettings, TextComponentTranslation> text() {
			return text;
		}

		public Predicate<SecurityCameraBlockEntity> whiteText() {
			return whiteText;
		}
	}
}
