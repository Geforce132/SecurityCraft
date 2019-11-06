package net.geforcemods.securitycraft;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.models.KeypadDynamicBakedModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class SCClientEventHandler {

	@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT, bus=Bus.MOD)
	private static class ModBus
	{
		@SubscribeEvent
		public static void onModelBake(ModelBakeEvent event)
		{
			String[] facings = {"east", "north", "south", "west"};
			String[] bools = {"true", "false"};

			for(String facing : facings)
			{
				for(String bool : bools)
				{
					ModelResourceLocation mrl = new ModelResourceLocation("securitycraft:keypad", "facing=" + facing + ",powered=" + bool);

					event.getModelRegistry().put(mrl, new KeypadDynamicBakedModel(event.getModelRegistry().get(mrl)));
				}
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
	public static void renderGameOverlay(RenderGameOverlayEvent event) {
		if(Minecraft.getInstance().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player)){
			if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ((BlockUtils.getBlock(Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ))) instanceof SecurityCameraBlock)))
				GuiUtils.drawCameraOverlay(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().mainWindow, Minecraft.getInstance().player, Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posX), (int)Minecraft.getInstance().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().posZ)));
		}
		else if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getInstance();
			ClientPlayerEntity player = mc.player;
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
				RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

				if(mop != null && mop.getType() == Type.BLOCK && world.getTileEntity(((BlockRayTraceResult)mop).getPos()) instanceof SecurityCameraTileEntity)
				{
					CompoundNBT cameras = monitor.getTag();

					if(cameras != null)
						for(int i = 1; i < 31; i++)
						{
							if(!cameras.contains("Camera" + i))
								continue;

							String[] coords = cameras.getString("Camera" + i).split(" ");

							if(Integer.parseInt(coords[0]) == ((BlockRayTraceResult)mop).getPos().getX() && Integer.parseInt(coords[1]) == ((BlockRayTraceResult)mop).getPos().getY() && Integer.parseInt(coords[2]) == ((BlockRayTraceResult)mop).getPos().getZ())
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
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && event.getButton() != 1) //anything other than rightclick
			{
				//fix not being able to interact with the pause menu and camera monitor while mounted to a camera
				if((Minecraft.getInstance().player.openContainer != null && !(Minecraft.getInstance().player.openContainer instanceof GenericContainer)) && !Minecraft.getInstance().isGamePaused())
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
