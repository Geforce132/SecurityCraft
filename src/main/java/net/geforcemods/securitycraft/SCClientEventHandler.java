package net.geforcemods.securitycraft;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class SCClientEventHandler
{
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("textures/mob_effect/night_vision.png");
	private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);

	@SubscribeEvent
	public static void onScreenshot(ScreenshotEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;

		if(PlayerUtils.isPlayerMountedOnCamera(player))
		{
			SecurityCameraEntity camera = (SecurityCameraEntity)Minecraft.getInstance().renderViewEntity;

			if(camera.screenshotSoundCooldown == 0)
			{
				camera.screenshotSoundCooldown = 7;
				Minecraft.getInstance().world.playSound(player.getPosition(), SCSounds.CAMERASNAP.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
			}
		}
	}

	@SubscribeEvent
	public static void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onClickInput(InputEvent.ClickInputEvent event) {
		if(event.isAttack() && ClientHandler.isPlayerMountedOnCamera())
		{
			event.setCanceled(true);
			event.setSwingHand(false);
		}
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		boolean isPlayerMountedOnCamera = PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player);

		if(event.getType() == ElementType.EXPERIENCE && isPlayerMountedOnCamera){
			drawCameraOverlay(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().getMainWindow(), Minecraft.getInstance().player, Minecraft.getInstance().world, Minecraft.getInstance().renderViewEntity.getPosition());
		}
		else if(event.getType() == ElementType.ALL && !isPlayerMountedOnCamera)
		{
			Minecraft mc = Minecraft.getInstance();
			ClientPlayerEntity player = mc.player;
			World world = player.getEntityWorld();

			for (Hand hand : Hand.values()) {
				int uCoord = 0;
				ItemStack stack = player.getHeldItem(hand);

				if(stack.getItem() == SCContent.CAMERA_MONITOR.get())
				{
					double eyeHeight = player.getEyeHeight();
					Vec3d lookVec = new Vec3d((player.getPosX() + (player.getLookVec().x * 5)), ((eyeHeight + player.getPosY()) + (player.getLookVec().y * 5)), (player.getPosZ() + (player.getLookVec().z * 5)));
					RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

					if(mop != null && mop.getType() == Type.BLOCK && world.getTileEntity(((BlockRayTraceResult)mop).getPos()) instanceof SecurityCameraTileEntity)
					{
						CompoundNBT cameras = stack.getTag();
						uCoord = 110;

						if(cameras != null) {
							for(int i = 1; i < 31; i++)
							{
								if(!cameras.contains("Camera" + i))
									continue;

								String[] coords = cameras.getString("Camera" + i).split(" ");

								if(Integer.parseInt(coords[0]) == ((BlockRayTraceResult)mop).getPos().getX() && Integer.parseInt(coords[1]) == ((BlockRayTraceResult)mop).getPos().getY() && Integer.parseInt(coords[2]) == ((BlockRayTraceResult)mop).getPos().getZ())
								{
									uCoord = 88;
									break;
								}
							}
						}
					}
				}
				else if(stack.getItem() == SCContent.REMOTE_ACCESS_MINE.get())
				{
					double eyeHeight = player.getEyeHeight();
					Vec3d lookVec = new Vec3d((player.getPosX() + (player.getLookVec().x * 5)), ((eyeHeight + player.getPosY()) + (player.getLookVec().y * 5)), (player.getPosZ() + (player.getLookVec().z * 5)));
					RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

					if(mop != null && mop.getType() == Type.BLOCK && world.getBlockState(((BlockRayTraceResult)mop).getPos()).getBlock() instanceof IExplosive)
					{
						uCoord = 110;
						CompoundNBT mines = stack.getTag();

						if(mines != null) {
							for(int i = 1; i <= 6; i++)
							{
								if(stack.getTag().getIntArray("mine" + i).length > 0)
								{
									int[] coords = mines.getIntArray("mine" + i);

									if(coords[0] == ((BlockRayTraceResult)mop).getPos().getX() && coords[1] == ((BlockRayTraceResult)mop).getPos().getY() && coords[2] == ((BlockRayTraceResult)mop).getPos().getZ())
									{
										uCoord = 88;
										break;
									}
								}
							}
						}
					}
				}
				else if(stack.getItem() == SCContent.REMOTE_ACCESS_SENTRY.get())
				{
					Entity hitEntity = Minecraft.getInstance().pointedEntity;

					if(hitEntity instanceof SentryEntity)
					{
						uCoord = 110;
						CompoundNBT sentries = stack.getTag();

						if(sentries != null) {
							for(int i = 1; i <= 12; i++)
							{
								if(stack.getTag().getIntArray("sentry" + i).length > 0)
								{
									int[] coords = sentries.getIntArray("sentry" + i);
									if(coords[0] == hitEntity.getPosition().getX() && coords[1] == hitEntity.getPosition().getY() && coords[2] == hitEntity.getPosition().getZ())
									{
										uCoord = 88;
										break;
									}
								}
							}
						}
					}
				}

				if (uCoord != 0) {
					RenderSystem.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(BEACON_GUI);
					AbstractGui.blit(Minecraft.getInstance().getMainWindow().getScaledWidth() / 2 - 90 + (hand == Hand.MAIN_HAND ? player.inventory.currentItem * 20 : -29), Minecraft.getInstance().getMainWindow().getScaledHeight() - 22, uCoord, 219, 21, 22, 256, 256);
					RenderSystem.disableAlphaTest();
				}
			}
		}
	}

	private static void drawCameraOverlay(Minecraft mc, AbstractGui gui, MainWindow resolution, PlayerEntity player, World world, BlockPos pos) {
		if (mc.gameSettings.showDebugInfo)
			return;

		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof SecurityCameraTileEntity))
			return;

		FontRenderer font = Minecraft.getInstance().fontRenderer;
		SecurityCameraTileEntity te = (SecurityCameraTileEntity)tile;
		boolean hasRedstoneModule = te.hasModule(ModuleType.REDSTONE);
		GameSettings settings = Minecraft.getInstance().gameSettings;
		BlockState state = world.getBlockState(pos);
		String lookAround = settings.keyBindForward.getLocalizedName().toUpperCase() + settings.keyBindLeft.getLocalizedName().toUpperCase() + settings.keyBindBack.getLocalizedName().toUpperCase() + settings.keyBindRight.getLocalizedName().toUpperCase() + " - " + Utils.localize("gui.securitycraft:camera.lookAround").getFormattedText();
		int timeY = 25;

		if(te.hasCustomName())
		{
			String cameraName = te.getCustomName().getFormattedText();

			font.drawStringWithShadow(cameraName, resolution.getScaledWidth() - font.getStringWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		font.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() - font.getStringWidth(ClientUtils.getFormattedMinecraftTime()) - 4, timeY, 16777215);
		font.drawStringWithShadow(lookAround, resolution.getScaledWidth() - font.getStringWidth(lookAround) - 8, resolution.getScaledHeight() - 80, 16777215);
		font.drawStringWithShadow(settings.keyBindSneak.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(settings.keyBindSneak.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText()) - 8, resolution.getScaledHeight() - 70, 16777215);
		font.drawStringWithShadow(KeyBindings.cameraZoomIn.getLocalizedName() + "/" + KeyBindings.cameraZoomOut.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(KeyBindings.cameraZoomIn.getLocalizedName() + "/" + Utils.localize(KeyBindings.cameraZoomOut.getTranslationKey()).getFormattedText() + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText()) - 8, resolution.getScaledHeight() - 60, 16777215);
		font.drawStringWithShadow(KeyBindings.cameraActivateNightVision.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(KeyBindings.cameraActivateNightVision.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText()) - 8, resolution.getScaledHeight() - 50, 16777215);
		font.drawStringWithShadow(KeyBindings.cameraEmitRedstone.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(KeyBindings.cameraEmitRedstone.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText()) - 8, resolution.getScaledHeight() - 40, hasRedstoneModule ? 16777215 : 16724855);
		font.drawStringWithShadow(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText(), resolution.getScaledWidth() - font.getStringWidth(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText()) - 8, resolution.getScaledHeight() - 30, hasRedstoneModule ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(5, 0, 0, 0, 90, 20);
		gui.blit(resolution.getScaledWidth() - 70, 5, 190, 0, 65, 30);

		if(!player.isPotionActive(Effects.NIGHT_VISION))
			gui.blit(28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(NIGHT_VISION);
			AbstractGui.blit(27, -1, 0, 0, 18, 18, 18, 18);
			mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		}

		if(state.getWeakPower(world, pos, state.get(SecurityCameraBlock.FACING)) == 0) {
			if(!hasRedstoneModule)
				gui.blit(12, 2, 104, 0, 12, 12);
			else
				gui.blit(12, 3, 90, 0, 12, 11);
		}
		else
			Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(REDSTONE, 10, 0);
	}
}
