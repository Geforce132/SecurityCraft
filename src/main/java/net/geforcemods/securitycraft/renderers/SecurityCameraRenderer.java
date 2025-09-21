package net.geforcemods.securitycraft.renderers;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
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
import net.minecraft.world.level.block.state.BlockState;
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
		if (state.isInsideThisCamera)
			return;

		//TODO: render delegate
		//ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, packedLight, packedOverlay, cameraPos);

		if (!state.isDown && !state.hasDisguiseModule) {
			Direction side = state.side;

			pose.translate(0.5D, 1.5D, 0.5D);

			if (side == Direction.NORTH)
				pose.mulPose(POSITIVE_Y_180);
			else if (side == Direction.EAST)
				pose.mulPose(POSITIVE_Y_90);
			else if (side == Direction.WEST)
				pose.mulPose(NEGATIVE_Y_90);
		}

		pose.mulPose(POSITIVE_X_180);
		model.rotateCameraY(state.cameraRotation);

		if (state.isShutDown)
			model.rotateCameraX(0.9F);
		else
			model.rotateCameraX(SecurityCameraModel.DEFAULT_X_ROT);

		if (!state.hasLens)
			model.cameraRotationPoint2.visible = false;

		collector.submitModel(model, null, pose, RenderType.entitySolid(state.isViewed ? BEING_VIEWED_TEXTURE : TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, ARGB.colorFromFloat(1.0F, state.r, state.g, state.b), null, 0, null);
	}

	@Override
	public SecurityCameraRenderState createRenderState() {
		return new SecurityCameraRenderState();
	}

	@Override
	public void extractRenderState(SecurityCameraBlockEntity be, SecurityCameraRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, renderState, partialTick, cameraPos, crumblingOverlay);

		ItemStack lens = be.getLensContainer().getItem(0);

		if (be.hasLevel()) {
			BlockState state = be.getLevel().getBlockState(be.getBlockPos());

			if (state.getBlock() == SCContent.SECURITY_CAMERA.get())
				renderState.side = state.getValue(SecurityCameraBlock.FACING);
		}
		else
			renderState.side = Direction.SOUTH;

		renderState.isInsideThisCamera = FrameFeedHandler.amIBeingCaptured(be) || PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().getCameraEntity().blockPosition().equals(be.getBlockPos());
		renderState.isDown = be.isDown();
		renderState.isShutDown = be.isShutDown();
		renderState.hasDisguiseModule = be.isModuleEnabled(ModuleType.DISGUISE);

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			renderState.hasLens = true;
			renderState.r = ((color >> 0x10) & 0xFF) / 255.0F;
			renderState.g = ((color >> 0x8) & 0xFF) / 255.0F;
			renderState.b = (color & 0xFF) / 255.0F;
		}
		else {
			renderState.hasLens = false;
			renderState.r = 0.4392156862745098F;
			renderState.g = 1.0F;
			renderState.b = 1.0F;
		}

		renderState.isViewed = be.getBlockState().getValue(SecurityCameraBlock.BEING_VIEWED);
		renderState.cameraRotation = (float) Mth.lerp(partialTick, be.getOriginalCameraRotation(), be.getCameraRotation());
	}
}
