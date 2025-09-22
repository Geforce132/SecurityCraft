package net.geforcemods.securitycraft.renderers;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.renderers.state.RetinalScannerRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class RetinalScannerRenderer implements BlockEntityRenderer<RetinalScannerBlockEntity, RetinalScannerRenderState> {
	private static final float CORRECT_FACTOR = 1 / 550F;
	private final PlayerSkinRenderCache playerSkinRenderCache;

	public RetinalScannerRenderer(BlockEntityRendererProvider.Context ctx) {
		playerSkinRenderCache = ctx.playerSkinRenderCache();
	}

	@Override
	public void submit(RetinalScannerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		//TODO disguise render delegate
		//if (ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos))
		//	return;

		Direction direction = state.facing;

		if (direction != null) {
			if (state.hasFilledDisguiseModule)
				return;

			poseStack.pushPose();

			switch (direction) {
				case NORTH:
					poseStack.translate(0.25F, 1.0F / 16.0F, 0.0F);
					break;
				case SOUTH:
					poseStack.translate(0.75F, 1.0F / 16.0F, 1.0F);
					poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
					break;
				case WEST:
					poseStack.translate(0.0F, 1.0F / 16.0F, 0.75F);
					poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
					break;
				case EAST:
					poseStack.translate(1.0F, 1.0F / 16.0F, 0.25F);
					poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
					break;
				default:
					break;
			}

			poseStack.scale(-1.0F, -1.0F, 1.0F);

			Vec3i normalVector = direction.getUnitVec3i();
			int combinedLight = state.combinedFacingLight;

			collector.submitCustomGeometry(poseStack, state.skinRenderType, (pose, vertexBuilder) -> {
				Matrix4f positionMatrix = pose.pose();

				// face
				vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.125F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
				vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).setColor(255, 255, 255, 255).setUv(0.125F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
				vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).setColor(255, 255, 255, 255).setUv(0.25F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
				vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.25F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());

				// helmet
				vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.625F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
				vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).setColor(255, 255, 255, 255).setUv(0.625F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
				vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).setColor(255, 255, 255, 255).setUv(0.75F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
				vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.75F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(pose, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			});

			poseStack.popPose();
		}
	}

	@Override
	public RetinalScannerRenderState createRenderState() {
		return new RetinalScannerRenderState();
	}

	@Override
	public void extractRenderState(RetinalScannerBlockEntity be, RetinalScannerRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		Direction facing = be.getBlockState().getValue(RetinalScannerBlock.FACING);
		BlockPos offsetPos = state.blockPos.relative(state.facing);

		state.skinRenderType = getSkinRenderType(be.getPlayerProfile());
		state.facing = facing;
		state.hasFilledDisguiseModule = be.isModuleEnabled(ModuleType.DISGUISE) && ModuleItem.getBlockAddon(be.getModule(ModuleType.DISGUISE)) != null;
		state.combinedFacingLight = LightTexture.pack(be.getLevel().getBrightness(LightLayer.BLOCK, offsetPos), be.getLevel().getBrightness(LightLayer.SKY, offsetPos));
	}

	private RenderType getSkinRenderType(@Nullable ResolvableProfile profile) {
		if (ConfigHandler.SERVER.retinalScannerFace.get() && profile != null)
			return playerSkinRenderCache.getOrDefault(profile).renderType();
		else
			return RenderType.entityCutout(DefaultPlayerSkin.getDefaultTexture());
	}
}