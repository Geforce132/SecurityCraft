package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.renderers.state.SecretSignRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.Vec3;

public class SecretSignRenderer extends SignRenderer {
	public SecretSignRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void submitSignText(SignRenderState state, PoseStack pose, SubmitNodeCollector collector, boolean isFrontText) {
		if (state instanceof SecretSignRenderState secretSignRenderState) {
			boolean isSecret = isFrontText ? secretSignRenderState.isFrontSecret : secretSignRenderState.isBackSecret;

			if (!isSecret)
				super.submitSignText(state, pose, collector, isFrontText);
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

			secretSignRenderState.isFrontSecret = !secretSignBlockEntity.isPlayerAllowedToSeeText(player, true);
			secretSignRenderState.isBackSecret = !secretSignBlockEntity.isPlayerAllowedToSeeText(player, false);
		}
	}
}