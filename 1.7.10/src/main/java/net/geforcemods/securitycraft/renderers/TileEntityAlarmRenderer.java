package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.models.ModelAlarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityAlarmRenderer extends TileEntitySpecialRenderer {

	private static final ModelAlarm alarmModel = new ModelAlarm();
	private static final ResourceLocation texture = new ResourceLocation("securitycraft:textures/blocks/alarm.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		int meta = te.hasWorldObj() ? te.getBlockMetadata() : te.blockMetadata;
		float rotationX = 0F;
		float rotationY = 0F;
		float rotationZ = 1F;

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

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		GL11.glPushMatrix();

		if(meta == 0){
			rotationX = -1F;
			rotationY = -10000F;
			GL11.glTranslatef(0F, -2F, 0F);
		}else if(meta == 1){
			rotationX = -1F;
			rotationY = 1F;
			rotationZ = 0F;
			GL11.glTranslatef(1F, -1F, 0F);
		}else if(meta == 2){
			rotationX = -1F;
			rotationY = -1F;
			rotationZ = 0F;
			GL11.glTranslatef(-1F, -1F, 0F);
		}else if(meta == 3){
			rotationX = 0F;
			rotationY = -1F;
			rotationZ = 1F;
			GL11.glTranslatef(0F, -1F, 1F);
		}else if(meta == 4){
			rotationX = 0F;
			rotationY = 1F;
			rotationZ = 1F;
			GL11.glTranslatef(0F, -1F, -1F);
		}else if(meta == 5)
			rotationX = 0F;

		GL11.glRotatef(180F, rotationX, rotationY, rotationZ);

		alarmModel.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

}
