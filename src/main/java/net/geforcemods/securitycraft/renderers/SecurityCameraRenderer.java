package net.geforcemods.securitycraft.renderers;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SecurityCameraRenderer implements BlockEntityRenderer<SecurityCameraBlockEntity> {
	private static final Quaternionf POSITIVE_Y_180 = Axis.YP.rotationDegrees(180.0F);
	private static final Quaternionf POSITIVE_Y_90 = Axis.YP.rotationDegrees(90.0F);
	private static final Quaternionf NEGATIVE_Y_90 = Axis.YN.rotationDegrees(90.0F);
	private static final Quaternionf POSITIVE_X_180 = Axis.XP.rotationDegrees(180.0F);
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/block/security_camera.png");
	private static final ResourceLocation BEING_VIEWED_TEXTURE = SecurityCraft.resLoc("textures/block/security_camera_viewing.png");
	private final SecurityCameraModel model;

	public SecurityCameraRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SecurityCameraModel(ctx.bakeLayer(ClientHandler.SECURITY_CAMERA_LOCATION));
	}

	@Override
	public void render(SecurityCameraBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay, Vec3 cameraPos) {
		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().cameraEntity.blockPosition().equals(be.getBlockPos()))
			return;
		else if (CameraController.currentlyCapturedCamera != null && be.getBlockPos().equals(CameraController.currentlyCapturedCamera.pos()))
			return;

		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, packedLight, packedOverlay, cameraPos);

		if (be.isDown())
			return;

		if (!be.isModuleEnabled(ModuleType.DISGUISE)) {
			pose.translate(0.5D, 1.5D, 0.5D);

			if (be.hasLevel()) {
				BlockState state = be.getLevel().getBlockState(be.getBlockPos());

				if (state.getBlock() == SCContent.SECURITY_CAMERA.get()) {
					Direction side = state.getValue(SecurityCameraBlock.FACING);

					if (side == Direction.NORTH)
						pose.mulPose(POSITIVE_Y_180);
					else if (side == Direction.EAST)
						pose.mulPose(POSITIVE_Y_90);
					else if (side == Direction.WEST)
						pose.mulPose(NEGATIVE_Y_90);
				}
			}

			pose.mulPose(POSITIVE_X_180);
			model.rotateCameraY((float) Mth.lerp(partialTicks, be.getOriginalCameraRotation(), be.getCameraRotation()));

			if (be.isShutDown())
				model.rotateCameraX(0.9F);
			else
				model.rotateCameraX(SecurityCameraModel.DEFAULT_X_ROT);

			ItemStack lens = be.getLensContainer().getItem(0);
			float r = 0.4392156862745098F, g = 1.0F, b = 1.0F;

			if (lens.has(DataComponents.DYED_COLOR)) {
				int color = lens.get(DataComponents.DYED_COLOR).rgb();

				r = ((color >> 0x10) & 0xFF) / 255.0F;
				g = ((color >> 0x8) & 0xFF) / 255.0F;
				b = (color & 0xFF) / 255.0F;
			}
			else
				model.cameraRotationPoint2.visible = false;

			model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(be.getBlockState().getValue(SecurityCameraBlock.BEING_VIEWED) ? BEING_VIEWED_TEXTURE : TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, ARGB.colorFromFloat(1.0F, r, g, b));
			model.cameraRotationPoint2.visible = true;
		}
	}
}
