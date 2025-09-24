package net.geforcemods.securitycraft.renderers;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.renderers.state.SecurityCameraRenderState;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SecurityCameraRenderer implements BlockEntityRenderer<SecurityCameraBlockEntity, SecurityCameraRenderState> {
	private static final Quaternionf POSITIVE_Y_180 = Axis.YP.rotationDegrees(180.0F);
	private static final Quaternionf POSITIVE_Y_90 = Axis.YP.rotationDegrees(90.0F);
	private static final Quaternionf NEGATIVE_Y_90 = Axis.YN.rotationDegrees(90.0F);
	private static final Quaternionf POSITIVE_X_180 = Axis.XP.rotationDegrees(180.0F);
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/entity/security_camera/security_camera.png");
	private static final ResourceLocation BEING_VIEWED_TEXTURE = SecurityCraft.resLoc("textures/entity/security_camera/security_camera_viewing.png");
	private final SecurityCameraModel model;

	public SecurityCameraRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SecurityCameraModel(ctx.bakeLayer(ClientHandler.SECURITY_CAMERA_LOCATION));
	}

	@Override
	public void submit(SecurityCameraRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.isBeingCaptured || state.isBeingViewed)
			return;

		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);

		if (state.isDown)
			return;

		if (!state.isDisguised) {
			Direction side = state.direction;

			pose.translate(0.5D, 1.5D, 0.5D);

			if (side == Direction.NORTH)
				pose.mulPose(POSITIVE_Y_180);
			else if (side == Direction.EAST)
				pose.mulPose(POSITIVE_Y_90);
			else if (side == Direction.WEST)
				pose.mulPose(NEGATIVE_Y_90);

			pose.mulPose(POSITIVE_X_180);
			collector.submitModel(model, state, pose, RenderType.entitySolid(state.texture), state.lightCoords, OverlayTexture.NO_OVERLAY, state.lensColor, state.breakProgress);
		}
	}

	@Override
	public SecurityCameraRenderState createRenderState() {
		return new SecurityCameraRenderState();
	}

	@Override
	public void extractRenderState(SecurityCameraBlockEntity be, SecurityCameraRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);
		state.isBeingCaptured = FrameFeedHandler.amIBeingCaptured(be);
		state.isBeingViewed = PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().getCameraEntity().blockPosition().equals(be.getBlockPos());
		state.isDown = be.isDown();
		state.isDisguised = be.isModuleEnabled(ModuleType.DISGUISE);
		state.direction = be.getBlockState().getValue(SecurityCameraBlock.FACING);
		state.isShutDown = be.isShutDown();
		state.cameraYRot = (float) Mth.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation());

		ItemStack lens = be.getLensContainer().getItem(0);
		float r = 0.4392156862745098F, g = 1.0F, b = 1.0F;

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			r = ARGB.redFloat(color);
			g = ARGB.greenFloat(color);
			b = ARGB.blueFloat(color);
			state.hasLens = true;
		}
		else
			state.hasLens = false;

		state.lensColor = ARGB.colorFromFloat(1.0F, r, g, b);
		state.texture = be.getBlockState().getValue(SecurityCameraBlock.BEING_VIEWED) ? BEING_VIEWED_TEXTURE : TEXTURE;
	}
}
