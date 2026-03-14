package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.SecretHangingSignBlockEntity;
import net.geforcemods.securitycraft.renderers.state.SecretHangingSignRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.state.HangingSignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;

public class SecretHangingSignRenderer extends HangingSignRenderer {
	public SecretHangingSignRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void submitSignText(HangingSignRenderState state, PoseStack pose, SubmitNodeCollector collector, SignText signText) {
		if (state instanceof SecretHangingSignRenderState secretSignRenderState) {
			boolean isSecret = signText == state.frontText ? secretSignRenderState.isFrontSecret : secretSignRenderState.isBackSecret;

			if (!isSecret)
				super.submitSignText(state, pose, collector, signText);
		}
	}

	@Override
	public HangingSignRenderState createRenderState() {
		return new SecretHangingSignRenderState();
	}

	@Override
	public void extractRenderState(SignBlockEntity be, HangingSignRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		if (be instanceof SecretHangingSignBlockEntity secretSignBlockEntity && state instanceof SecretHangingSignRenderState secretSignRenderState) {
			LocalPlayer player = Minecraft.getInstance().player;

			secretSignRenderState.isFrontSecret = secretSignBlockEntity.isPlayerAllowedToSeeText(player, true);
			secretSignRenderState.isBackSecret = secretSignBlockEntity.isPlayerAllowedToSeeText(player, false);
		}
	}
}