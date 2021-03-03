package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntitySentry;
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
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value=Side.CLIENT, modid=SecurityCraft.MODID)
public class SCClientEventHandler
{
	@SubscribeEvent
	public static void onPlayerRendered(RenderPlayerEvent.Pre event) {
		if(PlayerUtils.isPlayerMountedOnCamera(event.getEntityPlayer()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().player.getRidingEntity().getPosition().equals(event.getTarget().getBlockPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(Minecraft.getMinecraft().player != null && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player)){
			if(event.getType() == ElementType.EXPERIENCE && (BlockUtils.getBlock(Minecraft.getMinecraft().world, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posX), (int)Minecraft.getMinecraft().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posZ))) instanceof BlockSecurityCamera))
				GuiUtils.drawCameraOverlay(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI, event.getResolution(), Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, BlockUtils.toPos((int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posX), (int)Minecraft.getMinecraft().player.getRidingEntity().posY, (int)Math.floor(Minecraft.getMinecraft().player.getRidingEntity().posZ)));
		}
		else if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			World world = player.getEntityWorld();
			int held = player.inventory.currentItem;

			if(held < 0 || held >= player.inventory.mainInventory.size())
				return;

			ItemStack stack = player.inventory.mainInventory.get(held);

			if(!stack.isEmpty() && stack.getItem() == SCContent.cameraMonitor)
			{
				String textureToUse = "item_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.typeOfHit == Type.BLOCK && world.getTileEntity(mop.getBlockPos()) instanceof TileEntitySecurityCamera)
				{
					NBTTagCompound cameras = stack.getTagCompound();

					if(cameras != null)
						for(int i = 1; i < 31; i++)
						{
							if(!cameras.hasKey("Camera" + i))
								continue;

							String[] coords = cameras.getString("Camera" + i).split(" ");

							if(Integer.parseInt(coords[0]) == mop.getBlockPos().getX() && Integer.parseInt(coords[1]) == mop.getBlockPos().getY() && Integer.parseInt(coords[2]) == mop.getBlockPos().getZ())
							{
								textureToUse = "item_bound";
								break;
							}
						}

					GlStateManager.enableAlpha();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + held * 20 + 2, event.getResolution().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlpha();
				}
			}
			else if(!stack.isEmpty() && stack.getItem() == SCContent.remoteAccessMine)
			{
				String textureToUse = "item_not_bound";
				double eyeHeight = player.getEyeHeight();
				Vec3d lookVec = new Vec3d((player.posX + (player.getLookVec().x * 5)), ((eyeHeight + player.posY) + (player.getLookVec().y * 5)), (player.posZ + (player.getLookVec().z * 5)));
				RayTraceResult mop = world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

				if(mop != null && mop.typeOfHit == Type.BLOCK && world.getBlockState(mop.getBlockPos()).getBlock() instanceof IExplosive)
				{
					NBTTagCompound mines = stack.getTagCompound();

					if(mines != null)
						for(int i = 1; i <= 6; i++)
						{
							if(stack.getTagCompound().getIntArray("mine" + i).length > 0)
							{
								int[] coords = mines.getIntArray("mine" + i);

								if(coords[0] == mop.getBlockPos().getX() && coords[1] == mop.getBlockPos().getY() && coords[2] == mop.getBlockPos().getZ())
								{
									textureToUse = "item_bound";
									break;
								}
							}
						}

					GlStateManager.enableAlpha();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + held * 20 + 2, event.getResolution().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlpha();
				}
			}
			else if(!stack.isEmpty() && stack.getItem() == SCContent.remoteAccessSentry)
			{
				String textureToUse = "item_not_bound";
				Entity hitEntity = Minecraft.getMinecraft().pointedEntity;

				if(hitEntity instanceof EntitySentry)
				{
					NBTTagCompound sentries = stack.getTagCompound();

					if(sentries != null)
						for(int i = 1; i <= 12; i++)
						{
							if(stack.getTagCompound().getIntArray("sentry" + i).length > 0)
							{
								int[] coords = sentries.getIntArray("sentry" + i);
								if(coords[0] == hitEntity.getPosition().getX() && coords[1] == hitEntity.getPosition().getY() && coords[2] == hitEntity.getPosition().getZ())
								{
									textureToUse = "item_bound";
									break;
								}
							}
						}

					GlStateManager.enableAlpha();
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(SecurityCraft.MODID, "textures/gui/" + textureToUse + ".png"));
					drawNonStandardTexturedRect(event.getResolution().getScaledWidth() / 2 - 90 + held * 20 + 2, event.getResolution().getScaledHeight() - 16 - 3, 0, 0, 16, 16, 16, 16);
					GlStateManager.disableAlpha();
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
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onMouseClicked(MouseEvent event) {
		if(Minecraft.getMinecraft().world != null)
		{
			if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player) && event.getButton() != 1) //anything other than rightclick
				event.setCanceled(true);
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
