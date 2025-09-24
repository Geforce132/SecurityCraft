package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.renderers.state.SecretSignRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class SecretSignRenderer extends SignRenderer {
	public SecretSignRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void submitSignWithText(SignRenderState renderState, PoseStack pose, BlockState state, SignBlock block, WoodType woodType, Model.Simple model, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, SubmitNodeCollector collector) {
		if (renderState instanceof SecretSignRenderState secretSignRenderState) {
			pose.pushPose();
			translateSign(pose, -block.getYRotationDegrees(state), state);
			submitSign(pose, renderState.lightCoords, woodType, model, crumblingOverlay, collector);

			if (secretSignRenderState.isAllowedToSeeFrontText)
				submitSignText(renderState, pose, collector, true);

			if (secretSignRenderState.isAllowedToSeeBackText)
				submitSignText(renderState, pose, collector, false);

			pose.popPose();
		}
	}

	@Override
	public SignRenderState createRenderState() {
		return new SecretSignRenderState();
	}

	@Override
	public void extractRenderState(SignBlockEntity be, SignRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		if (be instanceof SecretSignBlockEntity secretSignBlockEntity && state instanceof SecretSignRenderState secretSignRenderState) {
			LocalPlayer player = Minecraft.getInstance().player;

			secretSignRenderState.isAllowedToSeeFrontText = secretSignBlockEntity.isPlayerAllowedToSeeText(player, true);
			secretSignRenderState.isAllowedToSeeBackText = secretSignBlockEntity.isPlayerAllowedToSeeText(player, false);
		}
	}
}