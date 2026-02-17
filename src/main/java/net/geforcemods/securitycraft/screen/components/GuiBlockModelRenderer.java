package net.geforcemods.securitycraft.screen.components;

import java.util.List;

import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BakedQuadOutput;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class GuiBlockModelRenderer extends PictureInPictureRenderer<GuiBlockModelRenderState> {
	private static final Quaternionf DEFAULT_ROTATION = ClientUtils.fromXYZDegrees(-15.0F, -135.0F, 0.0F).mul(Axis.XP.rotationDegrees(180.0F));

	public GuiBlockModelRenderer(BufferSource buffer) {
		super(buffer);
	}

	@Override
	public Class<GuiBlockModelRenderState> getRenderStateClass() {
		return GuiBlockModelRenderState.class;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void renderToTexture(GuiBlockModelRenderState guiRenderState, PoseStack pose) {
		Minecraft mc = Minecraft.getInstance();
		BlockEntityRenderer beRenderer = guiRenderState.beRenderer();

		pose.scale(-24.0F, 24.0F, -24.0F);
		pose.translate(0.5F, -1.5F, 0.5F);
		pose.mulPose(DEFAULT_ROTATION);
		pose.mulPose(guiRenderState.rotation());
		pose.translate(-0.5F, -0.5F, -0.5F);
		mc.gameRenderer.getLighting().setupFor(Entry.ENTITY_IN_UI);
		renderBlockModel(mc, guiRenderState.blockAndTintGetter(), guiRenderState.blockState(), pose, bufferSource);

		if (beRenderer != null) {
			BlockEntityRenderState beRenderState = beRenderer.createRenderState();
			FeatureRenderDispatcher featureRenderDispatcher = mc.gameRenderer.getFeatureRenderDispatcher();

			beRenderer.extractRenderState(guiRenderState.be(), beRenderState, mc.getDeltaTracker().getGameTimeDeltaPartialTick(true), mc.gameRenderer.getMainCamera().position(), null);
			beRenderState.lightCoords = LightCoordsUtil.FULL_BRIGHT;
			beRenderer.submit(beRenderState, pose, featureRenderDispatcher.getSubmitNodeStorage(), mc.levelRenderer.levelRenderState.cameraRenderState);
			featureRenderDispatcher.renderAllFeatures();
		}
	}

	@Override
	protected String getTextureLabel() {
		return "SC block model";
	}

	private void renderBlockModel(Minecraft mc, BlockAndTintGetter blockAndTintGetter, BlockState state, PoseStack pose, MultiBufferSource bufferSource) {
		if (state.getRenderShape() == RenderShape.MODEL) {
			BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
			BlockStateModel blockModel = blockRenderer.getBlockModel(state);
			List<BlockModelPart> parts = blockModel.collectParts(mc.level, BlockPos.ZERO, state, RandomSource.create(42L));
			BakedQuadOutput blockOutput;

			if (ItemBlockRenderTypes.forceOpaque(state)) {
				blockOutput = (lPose, quad, brightness, color, lightmapCoords, overlayCoords) -> {
					VertexConsumer buffer = bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(ChunkSectionLayer.SOLID));

					buffer.putBulkData(lPose, quad, brightness, color, lightmapCoords, overlayCoords);
				};
			}
			else {
				blockOutput = (lPose, quad, brightness, color, lightmapCoords, overlayCoords) -> {
					VertexConsumer buffer = bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(quad.spriteInfo().layer()));

					buffer.putBulkData(lPose, quad, brightness, color, lightmapCoords, overlayCoords);
				};
			}

			blockRenderer.getModelRenderer().tesselateWithoutAO(blockAndTintGetter, parts, state, BlockPos.ZERO, pose, blockOutput, false, OverlayTexture.NO_OVERLAY);
		}
	}
}
