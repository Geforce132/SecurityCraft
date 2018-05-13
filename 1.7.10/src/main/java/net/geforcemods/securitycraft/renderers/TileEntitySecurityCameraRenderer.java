package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelSecurityCamera;
import net.geforcemods.securitycraft.models.ModelSecurityCameraCeiling;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntitySecurityCameraRenderer extends TileEntitySpecialRenderer {

	private ModelSecurityCamera modelSecurityCamera;
	private ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/blocks/securityCamera.png");
	private ModelSecurityCameraCeiling modelSecurityCameraCeiling;
	private ResourceLocation cameraTextureCeiling = new ResourceLocation("securitycraft:textures/blocks/securityCameraCeiling.png");

	public TileEntitySecurityCameraRenderer() {
		modelSecurityCamera = new ModelSecurityCamera();
		modelSecurityCameraCeiling = new ModelSecurityCameraCeiling();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		if(PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().thePlayer) && Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posX) == te.xCoord && Minecraft.getMinecraft().thePlayer.ridingEntity.posY == te.yCoord && Math.floor(Minecraft.getMinecraft().thePlayer.ridingEntity.posZ) == te.zCoord)
			return;

		int meta = te.hasWorldObj() ? te.getBlockMetadata() : te.blockMetadata;
		float rotation = 0F;

		if(te.hasWorldObj()){
			Tessellator tessellator = Tessellator.instance;
			float brightness = te.getWorld().getLightBrightness(te.xCoord, te.yCoord, te.zCoord);
			int skyBrightness = te.getWorld().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			int l1 = skyBrightness % 65536;
			int l2 = skyBrightness / 65536;
			tessellator.setColorOpaque_F(brightness, brightness, brightness);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		if(((TileEntitySecurityCamera)te).down)
			Minecraft.getMinecraft().renderEngine.bindTexture(cameraTextureCeiling);
		else
			Minecraft.getMinecraft().renderEngine.bindTexture(cameraTexture);

		GL11.glPushMatrix();

		if(te.hasWorldObj()){
			if(meta == 1 || meta == 5)
				rotation = -1F;
			else if(meta == 2 || meta == 6)
				rotation = 1F;
			else if(meta == 3 || meta == 7)
				rotation = -10000F;
			else if(meta == 4 || meta == 8)
				rotation = 0F;
		}
		else
			rotation = -10000F;

		GL11.glRotatef(180F, rotation, 0.0F, 1.0F);

		if(!((TileEntitySecurityCamera)te).down)
			modelSecurityCamera.cameraRotationPoint.rotateAngleY = ((TileEntitySecurityCamera) te).cameraRotation;

		if(((TileEntitySecurityCamera)te).down)
			modelSecurityCameraCeiling.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		else
			modelSecurityCamera.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
