package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraTileEntityRenderer extends TileEntityRenderer<SecurityCameraTileEntity> {

	private static final Quaternion POSITIVE_Y_180 = Vector3f.YP.rotationDegrees(180.0F);
	private static final Quaternion POSITIVE_Y_90 = Vector3f.YP.rotationDegrees(90.0F);
	private static final Quaternion NEGATIVE_Y_90 = Vector3f.YN.rotationDegrees(90.0F);
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);

	public SecurityCameraTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	private static final SecurityCameraModel modelSecurityCamera = new SecurityCameraModel();
	private static final ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/block/security_camera1.png");

	@Override
	public void render(SecurityCameraTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		if(te.down || (Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON && PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getVehicle().blockPosition().equals(te.getBlockPos())))
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
