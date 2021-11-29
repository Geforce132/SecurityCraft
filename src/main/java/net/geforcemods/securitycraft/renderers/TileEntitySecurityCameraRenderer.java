package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.models.ModelSecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySecurityCameraRenderer extends TileEntitySpecialRenderer<TileEntitySecurityCamera> {

	private static final ModelSecurityCamera MODEL = new ModelSecurityCamera();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/blocks/security_camera.png");
	private static final ResourceLocation BEING_VIEWED_TEXTURE = new ResourceLocation("securitycraft:textures/blocks/security_camera_viewing.png");

	@Override
	public void render(TileEntitySecurityCamera te, double x, double y, double z, float par5, int par6, float alpha) {
		if(te.down || PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().getRenderViewEntity().getPosition().equals(te.getPos()))
			return;

		float rotation = -10000F;

		if(te.hasWorld()){
			Tessellator tessellator = Tessellator.getInstance();
			float brightness = te.getWorld().getLightBrightness(te.getPos());
			int skyBrightness = te.getWorld().getCombinedLight(te.getPos(), 0);
			int lightmapX = skyBrightness % 65536;
			int lightmapY = skyBrightness / 65536;

			tessellator.getBuffer().putColorRGBA(0, (int)(brightness * 255.0F), (int)(brightness * 255.0F), (int)(brightness * 255.0F), 255);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		Minecraft.getMinecraft().renderEngine.bindTexture(te.isSomeoneViewing() ? BEING_VIEWED_TEXTURE : TEXTURE);
		GlStateManager.pushMatrix();

		if(te.hasWorld())
		{
			IBlockState state = te.getWorld().getBlockState(te.getPos());

			if(state.getBlock() == SCContent.securityCamera){
				EnumFacing side = state.getValue(BlockSecurityCamera.FACING);

				if(side == EnumFacing.EAST)
					rotation = -1F;
				else if(side == EnumFacing.WEST)
					rotation = 1F;
				else if(side == EnumFacing.NORTH)
					rotation = 0F;
			}
		}

		GlStateManager.rotate(180F, rotation, 0.0F, 1.0F);
		MODEL.cameraRotationPoint.rotateAngleY = (float)te.cameraRotation;

		if(te.isShutDown())
			MODEL.cameraRotationPoint.rotateAngleX = 0.9F;
		else
			MODEL.cameraRotationPoint.rotateAngleX = 0.2617993877991494F;

		MODEL.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}


}
