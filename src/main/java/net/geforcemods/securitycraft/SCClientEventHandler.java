package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");

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
		//calling down() on the render view entity's position because the camera entity sits at y+0.5 by default and getPosition increases y by 0.5 again
		if (event.getType() == ElementType.ALL && ClientProxy.isPlayerMountedOnCamera())
			GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.getResolution(), Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, Minecraft.getMinecraft().getRenderViewEntity().getPosition().down());
	}
}
