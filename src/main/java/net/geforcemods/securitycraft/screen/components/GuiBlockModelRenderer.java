package net.geforcemods.securitycraft.screen.components;

import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class GuiBlockModelRenderer extends PictureInPictureRenderer<GuiBlockModelRenderState> {
	private static final Quaternionf DEFAULT_ROTATION = ClientUtils.fromXYZDegrees(-15.0F, -135.0F, 0.0F).mul(Axis.XP.rotationDegrees(180.0F));

	@Override
	public Class<GuiBlockModelRenderState> getRenderStateClass() {
		return GuiBlockModelRenderState.class;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void renderToTexture(GuiBlockModelRenderState guiRenderState, PoseStack pose, SubmitNodeCollector collector) {
		Minecraft mc = Minecraft.getInstance();
		BlockEntityRenderer beRenderer = guiRenderState.beRenderer();

		pose.scale(-24.0F, 24.0F, -24.0F);
		pose.translate(0.5F, -1.5F, 0.5F);
		pose.mulPose(DEFAULT_ROTATION);
		pose.mulPose(guiRenderState.rotation());
		pose.translate(-0.5F, -0.5F, -0.5F);
		mc.gameRenderer.lighting().setupFor(Entry.ENTITY_IN_UI);
		renderBlockModel(mc, guiRenderState.blockState(), pose, collector);

		if (beRenderer != null) {
			BlockEntityRenderState beRenderState = beRenderer.createRenderState();

			beRenderer.extractRenderState(guiRenderState.be(), beRenderState, mc.getDeltaTracker().getGameTimeDeltaPartialTick(true), mc.gameRenderer.mainCamera().position(), null);
			beRenderState.lightCoords = LightCoordsUtil.FULL_BRIGHT;
			beRenderer.submit(beRenderState, pose, collector, mc.levelRenderer.levelRenderState.cameraRenderState);
		}
	}

	@Override
	protected String getTextureLabel() {
		return "SC block model";
	}

	private void renderBlockModel(Minecraft mc, BlockState state, PoseStack pose, SubmitNodeCollector collector) {
		if (state.getRenderShape() == RenderShape.MODEL) {
			BlockModelRenderState renderState = new BlockModelRenderState();

			mc.getBlockModelResolver().update(renderState, state, BlockDisplayContext.create());
			renderState.submit(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
		}
	}
}
