package net.geforcemods.securitycraft;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
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
public class SCClientEventHandler {

	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("minecraft:textures/mob_effect/night_vision.png");

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
	public static void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(event.getTarget().getType() == Type.BLOCK)
		{
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(((BlockRayTraceResult)event.getTarget()).getPos()))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.EXPERIENCE && Minecraft.getInstance().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player)){
			if((BlockUtils.getBlock(Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ))) instanceof SecurityCameraBlock))
				drawCameraOverlay(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().mainWindow, Minecraft.getInstance().player, Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ)));
		}
		else if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getInstance();
			ClientPlayerEntity player = mc.player;
			World world = player.getEntityWorld();

			for (Hand hand : Hand.values()) {
				String textureToUse = null;
				ItemStack stack = player.getHeldItem(hand);

				if(stack.getItem() == SCContent.CAMERA_MONITOR.get())
				{
					double eyeHeight = player.getEyeHeight();
					Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
					RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

					if(mop != null && mop.getType() == Type.BLOCK && world.getTileEntity(((BlockRayTraceResult)mop).getPos()) instanceof SecurityCameraTileEntity)
					{
						CompoundNBT cameras = stack.getTag();
						textureToUse = "item_not_bound";

						if(cameras != null) {
							for(int i = 1; i < 31; i++)
							{
								if(!cameras.contains("Camera" + i))
									continue;

								String[] coords = cameras.getString("Camera" + i).split(" ");

								if(Integer.parseInt(coords[0]) == ((BlockRayTraceResult)mop).getPos().getX() && Integer.parseInt(coords[1]) == ((BlockRayTraceResult)mop).getPos().getY() && Integer.parseInt(coords[2]) == ((BlockRayTraceResult)mop).getPos().getZ())
								{
									textureToUse = "item_bound";
									break;
								}
							}
						}
					}
				}
				else if(stack.getItem() == SCContent.REMOTE_ACCESS_MINE.get())
				{
					double eyeHeight = player.getEyeHeight();
					Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
					RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

					if(mop != null && mop.getType() == Type.BLOCK && world.getBlockState(((BlockRayTraceResult)mop).getPos()).getBlock() instanceof IExplosive)
					{
						textureToUse = "item_not_bound";
						CompoundNBT mines = stack.getTag();

						if(mines != null) {
							for(int i = 1; i <= 6; i++)
							{
								if(stack.getTag().getIntArray("mine" + i).length > 0)
								{
									int[] coords = mines.getIntArray("mine" + i);

									if(coords[0] == ((BlockRayTraceResult)mop).getPos().getX() && coords[1] == ((BlockRayTraceResult)mop).getPos().getY() && coords[2] == ((BlockRayTraceResult)mop).getPos().getZ())
									{
										textureToUse = "item_bound";
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
						textureToUse = "item_not_bound";
						CompoundNBT sentries = stack.getTag();

						if(sentries != null) {
							for(int i = 1; i <= 12; i++)
							{
								if(stack.getTag().getIntArray("sentry" + i).length > 0)
								{
									int[] coords = sentries.getIntArray("sentry" + i);
									if(coords[0] == hitEntity.getPosition().getX() && coords[1] == hitEntity.getPosition().getY() && coords[2] == hitEntity.getPosition().getZ())
									{
										textureToUse = "item_bound";
										break;
									}
								}
							}
						}
					}
				}

				if (textureToUse != null) {
					GlStateManager.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(Minecraft.getInstance().mainWindow.getScaledWidth() / 2 - 90 + (hand == Hand.MAIN_HAND ? player.inventory.currentItem * 20 : -29) + 2, Minecraft.getInstance().mainWindow.getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlphaTest();
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

	private static void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
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

	private static void drawCameraOverlay(Minecraft mc, AbstractGui gui, MainWindow resolution, PlayerEntity player, World world, BlockPos pos) {
		SecurityCameraTileEntity te = (SecurityCameraTileEntity)world.getTileEntity(pos);
		int timeY = 25;
		GameSettings settings = Minecraft.getInstance().gameSettings;
		String lookAround = settings.keyBindForward.getLocalizedName().toUpperCase() + settings.keyBindLeft.getLocalizedName().toUpperCase() + settings.keyBindBack.getLocalizedName().toUpperCase() + settings.keyBindRight.getLocalizedName().toUpperCase() + " - " + Utils.localize("gui.securitycraft:camera.lookAround").getFormattedText();

		if(te.hasCustomSCName())
		{
			String cameraName = te.getCustomSCName().getFormattedText();

			Minecraft.getInstance().fontRenderer.drawStringWithShadow(cameraName, resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(cameraName) - 8, 25, 16777215);
			timeY += 10;
		}

		Minecraft.getInstance().fontRenderer.drawStringWithShadow(ClientUtils.getFormattedMinecraftTime(), resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(ClientUtils.getFormattedMinecraftTime()) - 8, timeY, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(lookAround, resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(lookAround) - 8, resolution.getScaledHeight() - 80, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(settings.keyBindSneak.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText(), resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(settings.keyBindSneak.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.exit").getFormattedText()) - 8, resolution.getScaledHeight() - 70, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraZoomIn.getLocalizedName() + "/" + KeyBindings.cameraZoomOut.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText(), resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraZoomIn.getLocalizedName() + "/" + Utils.localize(KeyBindings.cameraZoomOut.getTranslationKey()).getFormattedText() + " - " + Utils.localize("gui.securitycraft:camera.zoom").getFormattedText()) - 8, resolution.getScaledHeight() - 60, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraActivateNightVision.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText(), resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraActivateNightVision.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.activateNightVision").getFormattedText()) - 8, resolution.getScaledHeight() - 50, 16777215);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(KeyBindings.cameraEmitRedstone.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText(), resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(KeyBindings.cameraEmitRedstone.getLocalizedName() + " - " + Utils.localize("gui.securitycraft:camera.toggleRedstone").getFormattedText()) - 8, resolution.getScaledHeight() - 40, te.hasModule(ModuleType.REDSTONE) ? 16777215 : 16724855);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText(), resolution.getScaledWidth() - Minecraft.getInstance().fontRenderer.getStringWidth(Utils.localize("gui.securitycraft:camera.toggleRedstoneNote").getFormattedText()) - 8, resolution.getScaledHeight() - 30, te.hasModule(ModuleType.REDSTONE) ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(5, 0, 0, 0, 90, 20);
		gui.blit(resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(!player.isPotionActive(Effects.NIGHT_VISION))
			gui.blit(28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(NIGHT_VISION);
			AbstractGui.blit(27, -1, 0, 0, 18, 18, 18, 18);
			mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		}

		BlockState state = world.getBlockState(pos);

		if((state.getWeakPower(world, pos, state.get(SecurityCameraBlock.FACING)) == 0) && !te.hasModule(ModuleType.REDSTONE))
			gui.blit(12, 2, 104, 0, 12, 12);
		else if((state.getWeakPower(world, pos, state.get(SecurityCameraBlock.FACING)) == 0) && te.hasModule(ModuleType.REDSTONE))
			gui.blit(12, 3, 90, 0, 12, 11);
		else
			GuiUtils.drawItemToGui(Items.REDSTONE, 10, 0, false);
	}
}
