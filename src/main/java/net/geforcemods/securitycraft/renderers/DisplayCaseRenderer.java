package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.models.DisplayCaseModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayCaseRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity> {
	private final ResourceLocation texture = SecurityCraft.resLoc("textures/entity/display_case/normal.png");
	private final ResourceLocation glowTexture = SecurityCraft.resLoc("textures/entity/display_case/glow.png");
	private final DisplayCaseModel model;
	private final boolean glowing;

	public DisplayCaseRenderer(BlockEntityRendererProvider.Context ctx, boolean glowing) {
		model = new DisplayCaseModel(ctx.bakeLayer(ClientHandler.DISPLAY_CASE_LOCATION));
		this.glowing = glowing;
	}

	@Override
	public void render(DisplayCaseBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		VertexConsumer consumer;
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(DisplayCaseBlock.FACING);
		float rotation = facing.toYRot();
		ItemStack displayedStack = be.getDisplayedStack();
		int light = glowing ? LightTexture.FULL_BRIGHT : packedLight;

		model.setUpAnim(be.getOpenness(partialTick));
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);

		if (!displayedStack.isEmpty()) {
			double insertionAmount = 0.40625D;

			pose.pushPose();

			switch (state.getValue(DisplayCaseBlock.ATTACH_FACE)) {
				case CEILING:
					pose.translate(0.0D, insertionAmount, 0.0D);
					pose.mulPose(Axis.YP.rotationDegrees(-rotation + 180.0F));
					pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
					break;
				case FLOOR:
					pose.translate(0.0D, -insertionAmount, 0.0D);
					pose.mulPose(Axis.YP.rotationDegrees(-rotation + 180.0F));
					pose.mulPose(Axis.XP.rotationDegrees(90.0F));
					break;
				case WALL:
					pose.mulPose(Axis.YP.rotationDegrees(180.0F));
					pose.mulPose(Axis.YP.rotationDegrees(-rotation));
					pose.translate(0.0D, 0.0D, insertionAmount);
					break;
			}

			pose.scale(0.5F, 0.5F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(displayedStack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffer, be.getLevel(), 0);
			pose.popPose();
		}

		pose.mulPose(Axis.YP.rotationDegrees(-rotation));

		switch (state.getValue(DisplayCaseBlock.ATTACH_FACE)) {
			case CEILING:
				pose.translate(0.0D, 0.0D, 1.0D);
				pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
				break;
			case FLOOR:
				pose.translate(0.0D, 0.0D, -1.0D);
				pose.mulPose(Axis.XP.rotationDegrees(90.0F));
				break;
			case WALL:
				pose.translate(0.0D, 1.0D, 0.0D);
				pose.mulPose(Axis.XP.rotationDegrees(180.0F));
				break;
		}

		consumer = buffer.getBuffer(RenderType.entityCutout(glowing ? glowTexture : texture));
		pose.scale(-1.0F, 1.0F, -1.0F);
		model.renderToBuffer(pose, consumer, packedLight, packedOverlay);
		pose.popPose();
	}
}