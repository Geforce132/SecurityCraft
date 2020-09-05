package net.geforcemods.securitycraft;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
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
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class SCClientEventHandler
{
	public static final ResourceLocation CAMERA_DASHBOARD = new ResourceLocation("securitycraft:textures/gui/camera/camera_dashboard.png");
	public static final ResourceLocation NIGHT_VISION = new ResourceLocation("minecraft:textures/mob_effect/night_vision.png");
	private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);

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
			if((BlockUtils.getBlock(Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().getPosX()), (int)Minecraft.getInstance().player.getRidingEntity().getPosY(), (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().getPosZ()))) instanceof SecurityCameraBlock))
				drawCameraOverlay(event.getMatrixStack(), Minecraft.getInstance(), Minecraft.getInstance().ingameGUI, Minecraft.getInstance().getMainWindow(), Minecraft.getInstance().player, Minecraft.getInstance().world, BlockUtils.toPos((int)Math.floor(Minecraft.getInstance().player.getRidingEntity().getPosX()), (int)Minecraft.getInstance().player.getRidingEntity().getPosY(), (int)Math.floor(Minecraft.getInstance().player.getRidingEntity().getPosZ())));
		}
		else if(event.getType() == ElementType.ALL)
		{
			Minecraft mc = Minecraft.getInstance();
			ClientPlayerEntity player = mc.player;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;

			if(held < 0 || held >= player.inventory.mainInventory.size())
				return;

			ItemStack stack = player.inventory.mainInventory.get(held);

			if(!stack.isEmpty() && stack.getItem() == SCContent.CAMERA_MONITOR.get())
			{
				String textureToUse = "item_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vector3d lookVec = new Vector3d((player.getPosX() + (player.getLookVec().x * 5)), ((eyeHeight + player.getPosY()) + (player.getLookVec().y * 5)), (player.getPosZ() + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

				if(mop != null && mop.getType() == Type.BLOCK && world.getTileEntity(((BlockRayTraceResult)mop).getPos()) instanceof SecurityCameraTileEntity)
				{
					CompoundNBT cameras = stack.getTag();

					if(cameras != null)
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

					RenderSystem.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					AbstractGui.blit(event.getMatrixStack(), Minecraft.getInstance().getMainWindow().getScaledWidth() / 2 - 90 + held * 20 + 2, Minecraft.getInstance().getMainWindow().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					RenderSystem.disableAlphaTest();
				}
			}
			else if(!stack.isEmpty() && stack.getItem() == SCContent.REMOTE_ACCESS_MINE.get())
			{
				String textureToUse = "item_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vector3d lookVec = new Vector3d((player.getPosX() + (player.getLookVec().x * 5)), ((eyeHeight + player.getPosY()) + (player.getLookVec().y * 5)), (player.getPosZ() + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new RayTraceContext(new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ()), lookVec, BlockMode.OUTLINE, FluidMode.NONE, player));

				if(mop != null && mop.getType() == Type.BLOCK && world.getBlockState(((BlockRayTraceResult)mop).getPos()).getBlock() instanceof IExplosive)
				{
					CompoundNBT mines = stack.getTag();

					if(mines != null)
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

					RenderSystem.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					AbstractGui.blit(event.getMatrixStack(), Minecraft.getInstance().getMainWindow().getScaledWidth() / 2 - 90 + held * 20 + 2, Minecraft.getInstance().getMainWindow().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					RenderSystem.disableAlphaTest();
				}
			}
			else if(!stack.isEmpty() && stack.getItem() == SCContent.REMOTE_ACCESS_SENTRY.get())
			{
				String textureToUse = "item_not_bound";
				Entity hitEntity = Minecraft.getInstance().pointedEntity;

				if(hitEntity instanceof SentryEntity)
				{
					CompoundNBT sentries = stack.getTag();

					if(sentries != null)
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

					RenderSystem.enableAlphaTest();
					Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					AbstractGui.blit(event.getMatrixStack(), Minecraft.getInstance().getMainWindow().getScaledWidth() / 2 - 90 + held * 20 + 2, Minecraft.getInstance().getMainWindow().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
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
		boolean hasRedstoneModule = ((IModuleInventory) world.getTileEntity(pos)).hasModule(ModuleType.REDSTONE);
		ITextComponent exit = ClientUtils.localize("gui.securitycraft:camera.exit", settings.keyBindSneak.func_238171_j_());
		ITextComponent zoom = ClientUtils.localize("gui.securitycraft:camera.zoom", KeyBindings.cameraZoomIn.func_238171_j_(), KeyBindings.cameraZoomOut.func_238171_j_());
		ITextComponent nightVision = ClientUtils.localize("gui.securitycraft:camera.activateNightVision", KeyBindings.cameraActivateNightVision.func_238171_j_());
		ITextComponent redstone = ClientUtils.localize("gui.securitycraft:camera.toggleRedstone", KeyBindings.cameraEmitRedstone.func_238171_j_());
		ITextComponent redstoneNote = ClientUtils.localize("gui.securitycraft:camera.toggleRedstoneNote");
		String time = ClientUtils.getFormattedMinecraftTime();

		font.drawStringWithShadow(matrix, time, resolution.getScaledWidth() - font.getStringWidth(time) - 8, 25, 16777215);
		//drawStringWithShadow
		font.func_243246_a(matrix, exit, resolution.getScaledWidth() - font.func_238414_a_(exit) - 8, resolution.getScaledHeight() - 70, 16777215);
		font.func_243246_a(matrix, zoom, resolution.getScaledWidth() - font.func_238414_a_(zoom) - 8, resolution.getScaledHeight() - 60, 16777215);
		font.func_243246_a(matrix, nightVision, resolution.getScaledWidth() - font.func_238414_a_(nightVision) - 8, resolution.getScaledHeight() - 50, 16777215);
		font.func_243246_a(matrix, redstone, resolution.getScaledWidth() - font.func_238414_a_(redstone) - 8, resolution.getScaledHeight() - 40, hasRedstoneModule ? 16777215 : 16724855);
		font.func_243246_a(matrix, redstoneNote, resolution.getScaledWidth() - font.func_238414_a_(redstoneNote) -8, resolution.getScaledHeight() - 30, hasRedstoneModule ? 16777215 : 16724855);

		mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(matrix, 5, 0, 0, 0, 90, 20);
		gui.blit(matrix, resolution.getScaledWidth() - 55, 5, 205, 0, 50, 30);

		if(!player.isPotionActive(Effects.NIGHT_VISION))
			gui.blit(matrix, 28, 4, 90, 12, 16, 11);
		else{
			mc.getTextureManager().bindTexture(NIGHT_VISION);
			AbstractGui.blit(matrix, 27, -1, 0, 0, 18, 18, 18, 18);
			mc.getTextureManager().bindTexture(CAMERA_DASHBOARD);
		}

		if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockProperty(world, pos, SecurityCameraBlock.FACING)) == 0) && (!((IModuleInventory) world.getTileEntity(pos)).hasModule(ModuleType.REDSTONE)))
			gui.blit(matrix, 12, 2, 104, 0, 12, 12);
		else if((world.getBlockState(pos).getWeakPower(world, pos, BlockUtils.getBlockProperty(world, pos, SecurityCameraBlock.FACING)) == 0) && (((IModuleInventory) world.getTileEntity(pos)).hasModule(ModuleType.REDSTONE)))
			gui.blit(matrix, 12, 3, 90, 0, 12, 11);
		else
			Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(REDSTONE, 10, 0);
	}
}
