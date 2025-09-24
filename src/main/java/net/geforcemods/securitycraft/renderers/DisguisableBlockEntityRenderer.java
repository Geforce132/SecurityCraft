package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.renderers.state.DisguisableBlockEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class DisguisableBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, DisguisableBlockEntityRenderState> {
	public DisguisableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(DisguisableBlockEntityRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);
	}

	@Override
	public DisguisableBlockEntityRenderState createRenderState() {
		return new DisguisableBlockEntityRenderState();
	}

	@Override
	public void extractRenderState(T be, DisguisableBlockEntityRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);
	}
}
