package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderBullet;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.RenderSentry;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class SCClientEventHandler {

	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event)
	{
		KeyBindings.init();
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, manager -> new RenderBouncingBetty(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, manager -> new RenderIMSBomb(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, manager -> new RenderSentry(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, manager -> new RenderBullet(manager));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());
	}

	@SubscribeEvent
	public static void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(event.getTarget().getBlockPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent event) {
		if(Minecraft.getInstance().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player)){
			if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ((BlockUtils.getBlock(Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ))) instanceof BlockSecurityCamera)))
				GuiUtils.drawCameraOverlay(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().mainWindow, Minecraft.getInstance().player, Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ)));
		}
		else if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getInstance();
			EntityPlayerSP player = mc.player;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;

			if(held < 0 || held >= player.inventory.mainInventory.size())
				return;

			ItemStack monitor = player.inventory.mainInventory.get(held);

			if(!monitor.isEmpty() && monitor.getItem() == SCContent.cameraMonitor)
			{
				String textureToUse = "camera_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.type == Type.BLOCK && world.getTileEntity(mop.getBlockPos()) instanceof TileEntitySecurityCamera)
				{
					NBTTagCompound cameras = monitor.getTag();

					if(cameras != null)
						for(int i = 1; i < 31; i++)
						{
							if(!cameras.contains("Camera" + i))
								continue;

							String[] coords = cameras.getString("Camera" + i).split(" ");

							if(Integer.parseInt(coords[0]) == mop.getBlockPos().getX() && Integer.parseInt(coords[1]) == mop.getBlockPos().getY() && Integer.parseInt(coords[2]) == mop.getBlockPos().getZ())
							{
								textureToUse = "camera_bound";
								break;
							}
						}

					GlStateManager.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(Minecraft.getInstance().mainWindow.getScaledWidth() / 2 - 90 + held * 20 + 2, Minecraft.getInstance().mainWindow.getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlphaTest();
				}
			}
		}
	}

	@SubscribeEvent
	public static void fovUpdateEvent(FOVUpdateEvent event){
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntity()))
			event.setNewfov(((EntitySecurityCamera) event.getEntity().getRidingEntity()).getZoomAmount());
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
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && event.getButton() != 1) //anything other than rightclick
			{
				//fix not being able to interact with the pause menu and camera monitor while mounted to a camera
				if((Minecraft.getInstance().player.openContainer != null && !(Minecraft.getInstance().player.openContainer instanceof ContainerGeneric)) && !Minecraft.getInstance().isGamePaused())
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
}
