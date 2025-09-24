package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import it.unimi.dsi.fastutil.HashCommon;
import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.models.DisplayCaseModel;
import net.geforcemods.securitycraft.renderers.state.DisplayCaseRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DisplayCaseRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity, DisplayCaseRenderState> {
	private final ResourceLocation texture = SecurityCraft.resLoc("textures/entity/display_case/normal.png");
	private final ResourceLocation glowTexture = SecurityCraft.resLoc("textures/entity/display_case/glow.png");
	private final DisplayCaseModel model;
	private final ItemModelResolver itemModelResolver;
	private final boolean glowing;

	public DisplayCaseRenderer(BlockEntityRendererProvider.Context ctx, boolean glowing) {
		itemModelResolver = ctx.itemModelResolver();
		model = new DisplayCaseModel(ctx.bakeLayer(ClientHandler.DISPLAY_CASE_LOCATION));
		this.glowing = glowing;
	}

	@Override
	public void submit(DisplayCaseRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		float rotation = state.rotation;

		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);

		if (!state.stack.isEmpty()) {
			double insertionAmount = 0.40625D;

			pose.pushPose();

			switch (state.attachFace) {
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

			int lightCoords = state.isGlowing ? LightTexture.FULL_BRIGHT : state.lightCoords;

			pose.scale(0.5F, 0.5F, 0.5F);
			state.stack.submit(pose, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
			pose.popPose();
		}

		pose.mulPose(Axis.YP.rotationDegrees(-rotation));

		switch (state.attachFace) {
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

		RenderType renderType = RenderType.entityCutout(state.isGlowing ? glowTexture : texture);

		pose.scale(-1.0F, 1.0F, -1.0F);
		submitNodeCollector.submitModel(model, state.openness, pose, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
		pose.popPose();
	}

	@Override
	public DisplayCaseRenderState createRenderState() {
		return new DisplayCaseRenderState();
	}

	@Override
	public void extractRenderState(DisplayCaseBlockEntity be, DisplayCaseRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, renderState, partialTick, cameraPos, crumblingOverlay);

		BlockState state = be.getBlockState();
		Direction facing = state.getValue(DisplayCaseBlock.FACING);

		renderState.isGlowing = glowing;
		renderState.openness = be.getOpenness(partialTick);
		renderState.rotation = facing.toYRot();
		renderState.attachFace = state.getValue(DisplayCaseBlock.ATTACH_FACE);
		itemModelResolver.updateForTopItem(renderState.stack, be.getDisplayedStack(), ItemDisplayContext.FIXED, be.getLevel(), null, HashCommon.long2int(be.getBlockPos().asLong()));
	}
}