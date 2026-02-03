package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ReinforcedPistonHeadRenderer implements BlockEntityRenderer<ReinforcedPistonMovingBlockEntity, PistonHeadRenderState> {
	public ReinforcedPistonHeadRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(PistonHeadRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.block != null) {
			pose.pushPose();
			pose.translate(state.xOffset, state.yOffset, state.zOffset);
			collector.submitMovingBlock(pose, state.block);
			pose.popPose();

			if (state.base != null)
				collector.submitMovingBlock(pose, state.base);
		}
	}

	@Override
	public PistonHeadRenderState createRenderState() {
		return new PistonHeadRenderState();
	}

	@Override
	public void extractRenderState(ReinforcedPistonMovingBlockEntity be, PistonHeadRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, renderState, partialTick, cameraPos, crumblingOverlay);
		renderState.xOffset = be.getOffsetX(partialTick);
		renderState.yOffset = be.getOffsetY(partialTick);
		renderState.zOffset = be.getOffsetZ(partialTick);
		renderState.block = null;
		renderState.base = null;

		BlockState state = be.getMovedState();
		Level level = be.getLevel();

		if (level != null && !state.isAir()) {
			BlockPos oppositePos = be.getBlockPos().relative(be.getMovementDirection().getOpposite());
			Holder<Biome> biome = level.getBiome(oppositePos);

			if (state.is(SCContent.REINFORCED_PISTON_HEAD.get()) && be.getProgress(partialTick) <= 4.0F) {
				state = state.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTick) <= 0.5F);
				renderState.block = PistonHeadRenderer.createMovingBlock(oppositePos, state, biome, level);
			}
			else if (be.isSourcePiston() && !be.isExtending()) {
				PistonType pistonType = state.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState headState = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, pistonType).setValue(DirectionalBlock.FACING, state.getValue(DirectionalBlock.FACING));
				BlockPos renderPos = oppositePos.relative(be.getMovementDirection());

				headState = headState.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTick) >= 0.5F);
				renderState.block = PistonHeadRenderer.createMovingBlock(oppositePos, headState, biome, level);
				state = state.setValue(PistonBaseBlock.EXTENDED, true);
				renderState.base = PistonHeadRenderer.createMovingBlock(renderPos, state, biome, level);
			}
			else
				renderState.block = PistonHeadRenderer.createMovingBlock(oppositePos, state, biome, level);
		}
	}

	@Override
	public int getViewDistance() {
		return 68;
	}

	@Override
	public AABB getRenderBoundingBox(ReinforcedPistonMovingBlockEntity be) {
		return AABB.INFINITE;
	}
}
