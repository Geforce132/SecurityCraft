package net.geforcemods.securitycraft.screen.components;

import java.util.function.Function;

import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;

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
	protected void renderToTexture(GuiBlockModelRenderState state, PoseStack pose) {
		Minecraft mc = Minecraft.getInstance();

		pose.scale(-24.0F, 24.0F, -24.0F);
		pose.translate(0.5F, -1.5F, 0.5F);
		pose.mulPose(DEFAULT_ROTATION);
		pose.mulPose(state.rotation());
		pose.translate(-0.5F, -0.5F, -0.5F);
		mc.gameRenderer.getLighting().setupFor(Entry.ENTITY_IN_UI);
		renderBlockModel(mc, state.blockAndTintGetter(), state.blockState(), pose, bufferSource);

		if (state.beRenderer() != null)
			state.beRenderer().render(state.be(), mc.getDeltaTracker().getGameTimeDeltaPartialTick(true), pose, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, mc.gameRenderer.getMainCamera().getPosition());
	}

	@Override
	protected String getTextureLabel() {
		return "SC block model";
	}

	private void renderBlockModel(Minecraft mc, BlockAndTintGetter blockAndTintGetter, BlockState state, PoseStack pose, MultiBufferSource bufferSource) {
		if (state.getRenderShape() == RenderShape.MODEL) {
			BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
			BlockStateModel blockModel = blockRenderer.getBlockModel(state);
			Function<ChunkSectionLayer, RenderType> toRenderType = RenderTypeHelper::getMovingBlockRenderType;

			blockRenderer.getModelRenderer().tesselateWithoutAO(blockAndTintGetter, blockModel.collectParts(mc.level, BlockPos.ZERO, state, RandomSource.create(42L)), state, BlockPos.ZERO, pose, toRenderType.andThen(bufferSource::getBuffer), false, OverlayTexture.NO_OVERLAY);
		}
	}
}
