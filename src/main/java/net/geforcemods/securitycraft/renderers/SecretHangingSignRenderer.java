package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SecretHangingSignRenderer extends HangingSignRenderer {
	public SecretHangingSignRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void renderSignText(SignBlockEntity be, PoseStack pose, MultiBufferSource bufferSource, int packedLight, float scale) {
		if (be instanceof SecretSignBlockEntity sign && sign.isPlayerAllowedToSeeText(Minecraft.getInstance().player))
			super.renderSignText(be, pose, bufferSource, packedLight, scale);
		else
			pose.popPose();
	}
}