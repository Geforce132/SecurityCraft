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
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraTileEntityRenderer extends TileEntityRenderer<SecurityCameraTileEntity> {
	private static final Quaternion POSITIVE_Y_180 = Vector3f.YP.rotationDegrees(180.0F);
	private static final Quaternion POSITIVE_Y_90 = Vector3f.YP.rotationDegrees(90.0F);
	private static final Quaternion NEGATIVE_Y_90 = Vector3f.YN.rotationDegrees(90.0F);
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);
	private static final SecurityCameraModel MODEL = new SecurityCameraModel();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/block/security_camera.png");
	private static final ResourceLocation BEING_VIEWED_TEXTURE = new ResourceLocation("securitycraft:textures/block/security_camera_viewing.png");

	public SecurityCameraTileEntityRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(SecurityCameraTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		if (te.down || PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().cameraEntity.getCommandSenderBlockPosition().equals(te.getBlockPos()))
			return;

		matrix.translate(0.5D, 1.5D, 0.5D);

		if (te.hasLevel()) {
			BlockState state = te.getLevel().getBlockState(te.getBlockPos());

			if (state.getBlock() == SCContent.SECURITY_CAMERA.get()) {
				Direction side = state.getValue(SecurityCameraBlock.FACING);

				if (side == Direction.NORTH)
					matrix.mulPose(POSITIVE_Y_180);
				else if (side == Direction.EAST)
					matrix.mulPose(POSITIVE_Y_90);
				else if (side == Direction.WEST)
					matrix.mulPose(NEGATIVE_Y_90);
			}
		}

		matrix.mulPose(POSITIVE_X_180);
		MODEL.cameraRotationPoint.yRot = (float) te.cameraRotation;
		MODEL.renderToBuffer(matrix, buffer.getBuffer(RenderType.entitySolid(te.getBlockState().getValue(SecurityCameraBlock.BEING_VIEWED) ? BEING_VIEWED_TEXTURE : TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
