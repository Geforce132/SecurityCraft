package net.geforcemods.securitycraft;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
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
			SecurityCameraEntity camera = ((SecurityCameraEntity)player.getRidingEntity());

			if(camera.screenshotSoundCooldown == 0)
			{
				camera.screenshotSoundCooldown = 7;
				Minecraft.getInstance().world.playSound(player.getPosition(), SCSounds.CAMERASNAP.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(event.getEntity() instanceof LivingEntity && PlayerUtils.isPlayerMountedOnCamera((LivingEntity)event.getEntity()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onDrawBlockHighlight(DrawHighlightEvent.HighlightBlock event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(event.getTarget().getPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.EXPERIENCE && Minecraft.getInstance().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player)){
			if(Minecraft.getInstance().world.getBlockState(Minecraft.getInstance().player.getRidingEntity().getPosition()).getBlock() instanceof SecurityCameraBlock)
				drawCameraOverlay(event.getMatrixStack(), Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().getMainWindow(), Minecraft.getInstance().player, Minecraft.getInstance().world, Minecraft.getInstance().player.getRidingEntity().getPosition());
		}
		else if(event.getType() == ElementType.ALL)
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
					Vector3d lookVec = new Vector3d((player.getPosX() + (player.getLookVec().x * 5)), ((eyeHeight + player.getPosY()) + (player.getLookVec().y * 5)), (player.getPosZ() + (player.getLookVec().z * 5)));
					RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

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
					Vector3d lookVec = new Vector3d((player.getPosX() + (player.getLookVec().x * 5)), ((eyeHeight + player.getPosY()) + (player.getLookVec().y * 5)), (player.getPosZ() + (player.getLookVec().z * 5)));
					RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

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
					AbstractGui.blit(event.getMatrixStack(), Minecraft.getInstance().getMainWindow().getScaledWidth() / 2 - 90 + (hand == Hand.MAIN_HAND ? player.inventory.currentItem * 20 : -29), Minecraft.getInstance().getMainWindow().getScaledHeight() - 22, uCoord, 219, 21, 22, 256, 256);
					RenderSystem.disableAlphaTest();
				}
			}
		}
	}

	@SubscribeEvent
	public static void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntity()))
			event.setNewfov(((SecurityCameraEntity) event.getEntity().getRidingEntity()).getZoomAmount());
	}

	@SubscribeEvent
	public static void renderHandEvent(RenderHandEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onMouseClicked(MouseClickedEvent.Pre event) {
		if(Minecraft.getInstance().world != null)
		{
			if(event.getButton() != 1 && Minecraft.getInstance().player.openContainer == null) //anything other than rightclick and only if no gui is open)
			{
				if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.inventory.getCurrentItem().getItem() != SCContent.CAMERA_MONITOR.get())
					event.setCanceled(true);
			}
		}
	}

	private static void drawCameraOverlay(MatrixStack matrix, Minecraft mc, AbstractGui gui, MainWindow resolution, PlayerEntity player, World world, BlockPos pos) {
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		GameSettings settings = Minecraft.getInstance().gameSettings;
		SecurityCameraTileEntity te = (SecurityCameraTileEntity)world.getTileEntity(pos);
		boolean hasRedstoneModule = te.hasModule(ModuleType.REDSTONE);
		ITextComponent lookAround = Utils.localize("gui.securitycraft:camera.lookAround", settings.keyBindForward.func_238171_j_(), settings.keyBindLeft.func_238171_j_(), settings.keyBindBack.func_238171_j_(), settings.keyBindRight.func_238171_j_());
		ITextComponent exit = Utils.localize("gui.securitycraft:camera.exit", settings.keyBindSneak.func_238171_j_());
		ITextComponent zoom = Utils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.func_238171_j_(), KeyBindings.cameraZoomOut.func_238171_j_());
		ITextComponent nightVision = Utils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.func_238171_j_());
		ITextComponent redstone = Utils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.func_238171_j_());
		ITextComponent redstoneNote = Utils.localize("gui.securitycraft:camera.toggleRedstoneNote");
		String time = ClientUtils.getFormattedMinecraftTime();
		int timeY = 25;

		if(te.hasCustomSCName())
		{
			ITextComponent cameraName = te.getCustomSCName();

			font.drawTextWithShadow(matrix, cameraName, resolution.getScaledWidth() - font.getStringPropertyWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		font.drawStringWithShadow(matrix, time, resolution.getScaledWidth() - font.getStringWidth(time) - 4, timeY, 16777215);
		font.drawTextWithShadow(matrix, lookAround, resolution.getScaledWidth() - font.getStringPropertyWidth(lookAround) - 8, resolution.getScaledHeight() - 80, 16777215);
		font.drawTextWithShadow(matrix, exit, resolution.getScaledWidth() - font.getStringPropertyWidth(exit) - 8, resolution.getScaledHeight() - 70, 16777215);
		font.drawTextWithShadow(matrix, zoom, resolution.getScaledWidth() - font.getStringPropertyWidth(zoom) - 8, resolution.getScaledHeight() - 60, 16777215);
		font.drawTextWithShadow(matrix, nightVision, resolution.getScaledWidth() - font.getStringPropertyWidth(nightVision) - 8, resolution.getScaledHeight() - 50, 16777215);
		font.drawTextWithShadow(matrix, redstone, resolution.getScaledWidth() - font.getStringPropertyWidth(redstone) - 8, resolution.getScaledHeight() - 40, hasRedstoneModule ? 16777215 : 16724855);
		font.drawTextWithShadow(matrix, redstoneNote, resolution.getScaledWidth() - font.getStringPropertyWidth(redstoneNote) -8, resolution.getScaledHeight() - 30, hasRedstoneModule ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(matrix, 5, 0, 0, 0, 90, 20);
		gui.blit(matrix, resolution.getScaledWidth() - 70, 5, 190, 0, 65, 30);

		if(!player.isPotionActive(Effects.NIGHT_VISION))
			gui.blit(matrix, 28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(NIGHT_VISION);
			AbstractGui.blit(matrix, 27, -1, 0, 0, 18, 18, 18, 18);
			mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		}

		BlockState state = world.getBlockState(pos);

		if((state.getWeakPower(world, pos, state.get(SecurityCameraBlock.FACING)) == 0) && !te.hasModule(ModuleType.REDSTONE))
			gui.blit(matrix, 12, 2, 104, 0, 12, 12);
		else if((state.getWeakPower(world, pos, state.get(SecurityCameraBlock.FACING)) == 0) && te.hasModule(ModuleType.REDSTONE))
			gui.blit(matrix, 12, 3, 90, 0, 12, 11);
		else
			Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(REDSTONE, 10, 0);
	}
}
