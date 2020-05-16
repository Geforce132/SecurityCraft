package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraTileEntityRenderer extends TileEntityRenderer<SecurityCameraTileEntity> {

	private static final SecurityCameraModel modelSecurityCamera = new SecurityCameraModel();
	private static final ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/block/security_camera1.png");

	@Override
	public void render(SecurityCameraTileEntity te, double x, double y, double z, float par5, int par6) {
		if(te.down || (Minecraft.getInstance().gameSettings.thirdPersonView == 0 && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(te.getPos())))
			return;

		float rotation = 0F;

		if(te.hasWorld()){
			Tessellator tessellator = Tessellator.getInstance();
			float brightness = te.getWorld().getBrightness(te.getPos());
			int skyBrightness = te.getWorld().getCombinedLight(te.getPos(), 0);
			int lightmapX = skyBrightness % 65536;
			int lightmapY = skyBrightness / 65536;
			tessellator.getBuffer().putColorRGBA(0, (int)(brightness * 255.0F), (int)(brightness * 255.0F), (int)(brightness * 255.0F), 255);

			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, lightmapX, lightmapY);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

		Minecraft.getInstance().textureManager.bindTexture(cameraTexture);

		GlStateManager.pushMatrix();

		if(te.hasWorld() && BlockUtils.getBlock(te.getWorld(), te.getPos()) == SCContent.SECURITY_CAMERA.get()){
			Direction side = BlockUtils.getBlockProperty(getWorld(), te.getPos(), SecurityCameraBlock.FACING);

			if(side == Direction.EAST)
				rotation = -1F;
			else if(side == Direction.SOUTH)
				rotation = -10000F;
			else if(side == Direction.WEST)
				rotation = 1F;
			else if(side == Direction.NORTH)
				rotation = 0F;
		}
		else
			rotation = -10000F;

		GlStateManager.rotatef(180F, rotation, 0.0F, 1.0F);

		modelSecurityCamera.cameraRotationPoint.rotateAngleY = (float)te.cameraRotation;

		modelSecurityCamera.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}


}
