package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SecretSignRenderer extends SignRenderer {
	public SecretSignRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void renderSignWithText(SignBlockEntity be, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BlockState state, SignBlock block, WoodType woodType, Model model) {
		if (be instanceof SecretSignBlockEntity sign) {
			LocalPlayer player = Minecraft.getInstance().player;

			pose.pushPose();
			translateSign(pose, -block.getYRotationDegrees(state), state);
			renderSign(pose, bufferSource, packedLight, packedOverlay, woodType, model);

			if (sign.isPlayerAllowedToSeeText(player, true))
				renderSignText(be.getBlockPos(), be.getFrontText(), pose, bufferSource, packedLight, be.getTextLineHeight(), be.getMaxTextLineWidth(), true);

			if (sign.isPlayerAllowedToSeeText(player, false))
				renderSignText(be.getBlockPos(), be.getBackText(), pose, bufferSource, packedLight, be.getTextLineHeight(), be.getMaxTextLineWidth(), false);

			pose.popPose();
		}
	}
}