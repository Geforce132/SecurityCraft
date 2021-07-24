package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraTileEntityRenderer implements BlockEntityRenderer<SecurityCameraTileEntity> {

	private static final Quaternion POSITIVE_Y_180 = Vector3f.YP.rotationDegrees(180.0F);
	private static final Quaternion POSITIVE_Y_90 = Vector3f.YP.rotationDegrees(90.0F);
	private static final Quaternion NEGATIVE_Y_90 = Vector3f.YN.rotationDegrees(90.0F);
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);
	private static final SecurityCameraModel modelSecurityCamera = new SecurityCameraModel();
	private static final ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/block/security_camera1.png");

	public SecurityCameraTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(SecurityCameraTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int p_225616_5_, int p_225616_6_)
	{
		if(te.down || (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getVehicle().blockPosition().equals(te.getBlockPos())))
			return;

		matrix.translate(0.5D, 1.5D, 0.5D);

		if(te.hasLevel())
		{
			BlockState state = te.getLevel().getBlockState(te.getBlockPos());

			if(state.getBlock() == SCContent.SECURITY_CAMERA.get())
			{
				Direction side = state.getValue(SecurityCameraBlock.FACING);

				if(side == Direction.NORTH)
					matrix.mulPose(POSITIVE_Y_180);
				else if(side == Direction.EAST)
					matrix.mulPose(POSITIVE_Y_90);
				else if(side == Direction.WEST)
					matrix.mulPose(NEGATIVE_Y_90);
			}
		}

		matrix.mulPose(POSITIVE_X_180);
		modelSecurityCamera.cameraRotationPoint.yRot = (float)te.cameraRotation;
		modelSecurityCamera.renderToBuffer(matrix, buffer.getBuffer(RenderType.entitySolid(cameraTexture)), p_225616_5_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
