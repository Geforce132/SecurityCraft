package net.geforcemods.securitycraft.renderers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.renderers.state.ProjectorRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity, ProjectorRenderState> {
	public ProjectorRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(ProjectorRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);

		for (ProjectionInfo projectionInfo : state.renderPositions) {
			Vec3i positionalOffset = projectionInfo.positionalOffset;

			pose.pushPose();
			pose.translate(positionalOffset.getX(), positionalOffset.getY(), positionalOffset.getZ());
			collector.submitMovingBlock(pose, projectionInfo.movingBlockRenderState);
			ClientHandler.PROJECTOR_RENDER_DELEGATE.trySubmitDelegate(state.projectedBlockEntityRenderState, pose, collector, camera);
			pose.popPose();
		}
	}

	@Override
	public ProjectorRenderState createRenderState() {
		return new ProjectorRenderState();
	}

	@Override
	public void extractRenderState(ProjectorBlockEntity be, ProjectorRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);
		state.projectedBlockEntityRenderState = ClientHandler.PROJECTOR_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);

		List<ProjectionInfo> renderPositions = new ArrayList<>();

		if (be.isActive() && !be.isEmpty()) {
			Level level = be.getLevel();
			BlockPos projectorPos = be.getBlockPos();
			AABB projectedBlocksArea = be.getProjectedBlocksArea().deflate(0.1D);

			for (BlockPos relativePos : BlockPos.betweenClosed(projectedBlocksArea)) {
				BlockPos projectionPos = projectorPos.offset(relativePos);

				if (be.isOverridingBlocks() || level.isEmptyBlock(projectionPos)) {
					MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();

					movingBlockRenderState.randomSeedPos = projectionPos;
					movingBlockRenderState.blockPos = projectionPos;
					movingBlockRenderState.blockState = be.getProjectedState();
					movingBlockRenderState.biome = level.getBiome(projectionPos);
					movingBlockRenderState.level = level;
					renderPositions.add(new ProjectionInfo(new BlockPos(relativePos), movingBlockRenderState));
				}
			}
		}

		state.renderPositions = renderPositions;
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(ProjectorBlockEntity be) {
		AABB projectorBoundingBox = new AABB(be.getBlockPos());

		return be.isEmpty() ? projectorBoundingBox.inflate(1.0D) : be.getProjectedBlocksArea().move(be.getBlockPos()).minmax(projectorBoundingBox).inflate(1.0D);
	}

	public record ProjectionInfo(Vec3i positionalOffset, MovingBlockRenderState movingBlockRenderState) {}
}