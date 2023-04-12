package net.geforcemods.securitycraft;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.items.TaserItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = SecurityCraft.MODID)
public class SCClientEventHandler {
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private static final int USE_CHECKMARK = 88, USE_CROSS = 110;

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
	public static void onScreenshot(ScreenshotEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCamera camera = (SecurityCamera) Minecraft.getMinecraft().getRenderViewEntity();

			if (camera.screenshotSoundCooldown == 0) {
				camera.screenshotSoundCooldown = 7;
				Minecraft.getMinecraft().world.playSound(player.getPosition(), SCSounds.CAMERASNAP.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
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
	public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if ((event.getType() == ElementType.EXPERIENCE || event.getType() == ElementType.JUMPBAR || event.getType() == ElementType.POTION_ICONS) && ClientProxy.isPlayerMountedOnCamera())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL) {
			if (ClientProxy.isPlayerMountedOnCamera())
				//calling down() on the render view entity's position because the camera entity sits at y+0.5 by default and getPosition increases y by 0.5 again
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.getResolution(), Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, Minecraft.getMinecraft().getRenderViewEntity().getPosition().down());
			else {
				Minecraft mc = Minecraft.getMinecraft();
				EntityPlayerSP player = mc.player;
				World world = player.getEntityWorld();

				for (EnumHand hand : EnumHand.values()) {
					ItemStack stack = player.getHeldItem(hand);
					int uCoord = 0;

					if (stack.getItem() == SCContent.cameraMonitor) {
						uCoord = getUCoord(world, player, stack, bhr -> world.getTileEntity(bhr.getBlockPos()) instanceof SecurityCameraBlockEntity, 30, (tag, i) -> {
							if (!tag.hasKey("Camera" + i))
								return null;

							String camera = tag.getString("Camera" + i);

							return Arrays.stream(camera.substring(0, camera.lastIndexOf(' ')).split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
						});
					}
					else if (stack.getItem() == SCContent.remoteAccessMine) {
						uCoord = getUCoord(world, player, stack, bhr -> world.getBlockState(bhr.getBlockPos()).getBlock() instanceof IExplosive, 30, (tag, i) -> {
							if (tag.getIntArray("mine" + i).length > 0)
								return Arrays.stream(tag.getIntArray("mine" + i)).boxed().toArray(Integer[]::new);
							else
								return null;
						});
					}
					else if (stack.getItem() == SCContent.remoteAccessSentry) {
						if (Minecraft.getMinecraft().pointedEntity instanceof Sentry) {
							Sentry sentry = (Sentry) Minecraft.getMinecraft().pointedEntity;

							if (!stack.hasTagCompound())
								stack.setTagCompound(new NBTTagCompound());

							uCoord = loop(12, (tag, i) -> Arrays.stream(tag.getIntArray("sentry" + i)).boxed().toArray(Integer[]::new), stack.getTagCompound(), sentry.getPosition());
						}
					}
					else if (stack.getItem() == SCContent.sonicSecuritySystemItem) {
						uCoord = getUCoord(world, player, stack, bhr -> {
							TileEntity tile = world.getTileEntity(bhr.getBlockPos());

							if (!(tile instanceof ILockable))
								return false;

							//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
							if (!(tile instanceof IOwnable) || !((IOwnable) tile).isOwnedBy(player)) {
								if (DisguisableBlock.getDisguisedBlockStateUnknown(world, bhr.getBlockPos()) != null)
									return false;
							}

							return true;
						}, 0, null, false, SonicSecuritySystemItem::isAdded);
					}

					if (uCoord != 0) {
						GlStateManager.enableAlpha();
						Minecraft.getMinecraft().renderEngine.bindTexture(BEACON_GUI);
						drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + (hand == EnumHand.MAIN_HAND ? player.inventory.currentItem * 20 : (mc.gameSettings.mainHand == EnumHandSide.LEFT ? 189 : -29)), event.getResolution().getScaledHeight() - 22, uCoord, 219, 21, 22, 256, 256);
						GlStateManager.disableAlpha();
					}
				}
			}
		}
	}

	private static int getUCoord(World level, EntityPlayer player, ItemStack stackInHand, Predicate<RayTraceResult> isValidHitResult, int tagSize, BiFunction<NBTTagCompound, Integer, Integer[]> getCoords) {
		return getUCoord(level, player, stackInHand, isValidHitResult, tagSize, getCoords, true, null);
	}

	private static int getUCoord(World level, EntityPlayer player, ItemStack stackInHand, Predicate<RayTraceResult> isValidHitResult, int tagSize, BiFunction<NBTTagCompound, Integer, Integer[]> getCoords, boolean loop, BiFunction<NBTTagCompound, BlockPos, Boolean> useCheckmark) {
		double reachDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		double eyeHeight = player.getEyeHeight();
		Vec3d lookVec = new Vec3d(player.posX + player.getLookVec().x * reachDistance, eyeHeight + player.posY + player.getLookVec().y * reachDistance, player.posZ + player.getLookVec().z * reachDistance);
		RayTraceResult hitResult = level.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

		if (hitResult != null && hitResult.typeOfHit == Type.BLOCK && isValidHitResult.test(hitResult)) {
			if (!stackInHand.hasTagCompound())
				stackInHand.setTagCompound(new NBTTagCompound());

			if (loop)
				return loop(tagSize, getCoords, stackInHand.getTagCompound(), hitResult.getBlockPos());
			else
				return useCheckmark.apply(stackInHand.getTagCompound(), hitResult.getBlockPos()) ? USE_CHECKMARK : USE_CROSS;
		}

		return 0;
	}

	private static int loop(int tagSize, BiFunction<NBTTagCompound, Integer, Integer[]> getCoords, NBTTagCompound tag, BlockPos pos) {
		for (int i = 1; i <= tagSize; i++) {
			Integer[] coords = getCoords.apply(tag, i);

			if (coords != null && coords.length == 3 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return USE_CHECKMARK;
		}

		return USE_CROSS;
	}

	private static void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		double z = 200;
		double widthFactor = 1F / (double) textureWidth;
		double heightFactor = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, z).tex(u * widthFactor, (v + height) * heightFactor).endVertex();
		buffer.pos(x + width, y + height, z).tex((u + width) * widthFactor, (v + height) * heightFactor).endVertex();
		buffer.pos(x + width, y, z).tex((u + width) * widthFactor, v * heightFactor).endVertex();
		buffer.pos(x, y, z).tex(u * widthFactor, v * heightFactor).endVertex();
		tessellator.draw();
	}
}
