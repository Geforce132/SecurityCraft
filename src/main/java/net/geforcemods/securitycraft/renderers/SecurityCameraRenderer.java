package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.items.LensItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class SecurityCameraRenderer extends TileEntitySpecialRenderer<SecurityCameraBlockEntity> {
	private static final SecurityCameraModel MODEL = new SecurityCameraModel();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/blocks/security_camera.png");
	private static final ResourceLocation BEING_VIEWED_TEXTURE = new ResourceLocation("securitycraft:textures/blocks/security_camera_viewing.png");

	@Override
	public void render(SecurityCameraBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		//calling down() on the render view entity's position because the camera entity sits at y+0.5 by default and getPosition increases y by 0.5 again
		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().getRenderViewEntity().getPosition().down().equals(te.getPos()))
			return;
		else if (CameraController.currentlyCapturedCamera != null && te.getPos().equals(CameraController.currentlyCapturedCamera.getLeft().pos()))
			return;

		BlockEntityRenderDelegate.DISGUISED_BLOCK.tryRenderDelegate(te, x, y, z, partialTicks, destroyStage, alpha);

		if (te.isDown())
			return;

		if (!te.isModuleEnabled(ModuleType.DISGUISE)) {
			float rotation = -10000F;

			if (te.hasWorld()) {
				Tessellator tessellator = Tessellator.getInstance();
				float brightness = te.getWorld().getLightBrightness(te.getPos());
				int skyBrightness = te.getWorld().getCombinedLight(te.getPos(), 0);
				int lightmapX = skyBrightness % 65536;
				int lightmapY = skyBrightness / 65536;

				tessellator.getBuffer().putColorRGBA(0, (int) (brightness * 255.0F), (int) (brightness * 255.0F), (int) (brightness * 255.0F), 255);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			Minecraft.getMinecraft().renderEngine.bindTexture(te.isSomeoneViewing() ? BEING_VIEWED_TEXTURE : TEXTURE);
			GlStateManager.pushMatrix();

			if (te.hasWorld()) {
				IBlockState state = te.getWorld().getBlockState(te.getPos());

				if (state.getBlock() == SCContent.securityCamera) {
					EnumFacing side = state.getValue(SecurityCameraBlock.FACING);

					if (side == EnumFacing.EAST)
						rotation = -1F;
					else if (side == EnumFacing.WEST)
						rotation = 1F;
					else if (side == EnumFacing.NORTH)
						rotation = 0F;
				}
			}

			GlStateManager.rotate(180F, rotation, 0.0F, 1.0F);
			MODEL.rotateCameraY((float) Utils.lerp(partialTicks, te.getOriginalCameraRotation(), te.getCameraRotation()));

			if (te.isShutDown())
				MODEL.rotateCameraX(0.9F);
			else
				MODEL.rotateCameraX(SecurityCameraModel.DEFAULT_X_ROT);

			ItemStack lens = te.getLensContainer().getStackInSlot(0);
			Item item = lens.getItem();

			if (item instanceof LensItem && ((LensItem) item).hasColor(lens)) {
				int color = ((LensItem) item).getColor(lens);

				MODEL.r = ((color >> 0x10) & 0xFF) / 255.0F;
				MODEL.g = ((color >> 0x8) & 0xFF) / 255.0F;
				MODEL.b = (color & 0xFF) / 255.0F;
			}
			else {
				MODEL.r = 0.4392156862745098F;
				MODEL.g = 1.0F;
				MODEL.b = 1.0F;
				MODEL.cameraRotationPoint2.isHidden = true;
			}

			MODEL.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			MODEL.cameraRotationPoint2.isHidden = false;
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
	}
}
