package net.geforcemods.securitycraft;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
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
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

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
			ResourceLocation[] facingPoweredBlocks = {
					SCContent.keycardReader.getRegistryName(),
					SCContent.keypad.getRegistryName(),
					SCContent.retinalScanner.getRegistryName()
			};
			ResourceLocation[] facingBlocks = {
					SCContent.inventoryScanner.getRegistryName(),
					SCContent.usernameLogger.getRegistryName()
			};
			ResourceLocation[] poweredBlocks = {
					SCContent.laserBlock.getRegistryName()
			};

			for(String facing : facings)
			{
				for(String bool : bools)
				{
					for(ResourceLocation facingPoweredBlock : facingPoweredBlocks)
					{
						register(event, facingPoweredBlock, "facing=" + facing + ",powered=" + bool);
					}
				}

				for(ResourceLocation facingBlock : facingBlocks)
				{
					register(event, facingBlock, "facing=" + facing);
				}
			}

			for(String bool : bools)
			{
				for(ResourceLocation poweredBlock : poweredBlocks)
				{
					register(event, poweredBlock, "powered=" + bool);
				}
			}
		}

		private static void register(ModelBakeEvent event, ResourceLocation rl, String stateString)
		{
			ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

			event.getModelRegistry().put(mrl, new DisguisableDynamicBakedModel(rl, event.getModelRegistry().get(mrl)));
		}

		@SubscribeEvent
		public static void onTextureStitchPre(TextureStitchEvent.Pre event)
		{
			if(event.getMap().func_229223_g_().equals(Atlases.field_228747_f_)) //CHESTS
			{
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/active"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/inactive"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/left_active"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/left_inactive"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/right_active"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/right_inactive"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas_left"));
				event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas_right"));
			}
		}

		@SubscribeEvent
		public static void onFMLClientSetup(FMLClientSetupEvent event)
		{
			RenderTypeLookup.setRenderLayer(SCContent.blockPocketManager, RenderType.func_228641_d_()); //cutoutMipped
			RenderTypeLookup.setRenderLayer(SCContent.blockPocketWall, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.cageTrap, RenderType.func_228641_d_()); //cutoutMipped
			RenderTypeLookup.setRenderLayer(SCContent.inventoryScanner, RenderType.func_228643_e_()); ////cutout
			RenderTypeLookup.setRenderLayer(SCContent.inventoryScannerField, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.keycardReader, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.keypad, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.laserBlock, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.laserField, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.retinalScanner, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.usernameLogger, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedDoor, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedGlass, RenderType.func_228643_e_()); //cutout
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedGlassPane, RenderType.func_228641_d_()); //cutoutMipped
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedWhiteStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedOrangeStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedMagentaStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightBlueStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedYellowStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedLimeStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedPinkStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedGrayStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightGrayStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedCyanStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedPurpleStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlueStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedBrownStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedGreenStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedRedStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlackStainedGlass, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedWhiteStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedOrangeStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedMagentaStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightBlueStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedYellowStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedLimeStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedPinkStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedGrayStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightGrayStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedCyanStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedPurpleStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlueStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedBrownStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedGreenStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedRedStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlackStainedGlassPane, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.trophySystem, RenderType.func_228641_d_()); //cutoutMipped
			RenderTypeLookup.setRenderLayer(SCContent.flowingFakeWater, RenderType.func_228645_f_()); //translucent
			RenderTypeLookup.setRenderLayer(SCContent.fakeWater, RenderType.func_228645_f_()); //translucent

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
	public static void renderGameOverlay(RenderGameOverlayEvent event) {
		if(event.getType() == ElementType.EXPERIENCE && Minecraft.getInstance().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player)){
			if(((BlockUtils.getBlock(Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().func_226277_ct_()), (int)Minecraft.getInstance().player.getRidingEntity().func_226278_cu_(), (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().func_226281_cx_()))) instanceof SecurityCameraBlock)))
				GuiUtils.drawCameraOverlay(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().func_228018_at_(), Minecraft.getInstance().player, Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().func_226277_ct_()), (int)Minecraft.getInstance().player.getRidingEntity().func_226278_cu_(), (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().func_226281_cx_())));
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
				Vec3d lookVec = new Vec3d((player.func_226277_ct_() + (player.getLookVec().x * 5)), ((eyeHeight + player.func_226278_cu_()) + (player.getLookVec().y * 5)), (player.func_226281_cx_() + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vec3d(player.func_226277_ct_(), player.func_226278_cu_() + player.getEyeHeight(), player.func_226281_cx_()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

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

					RenderSystem.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					AbstractGui.blit(Minecraft.getInstance().func_228018_at_().getScaledWidth() / 2 - 90 + held * 20 + 2, Minecraft.getInstance().func_228018_at_().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
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
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && event.getButton() != 1) //anything other than rightclick
			{
				//fix not being able to interact with the pause menu and camera monitor while mounted to a camera
				if((Minecraft.getInstance().player.openContainer != null && !(Minecraft.getInstance().player.openContainer instanceof GenericContainer)) && !Minecraft.getInstance().isGamePaused())
					event.setCanceled(true);
			}
		}
	}
}
