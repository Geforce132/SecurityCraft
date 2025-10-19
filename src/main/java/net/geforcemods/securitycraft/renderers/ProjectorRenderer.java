package net.geforcemods.securitycraft.renderers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.renderers.state.ProjectorRenderState;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.TriPredicate;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity, ProjectorRenderState> {
	public static final int RENDER_DISTANCE = 100;
	private final TriPredicate<Integer, Boolean, Integer> yLoopBoundary = (projectionHeight, hanging, y) -> hanging ? y > -projectionHeight : y < projectionHeight;

	public ProjectorRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(ProjectorRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);

		for (ProjectionInfo projectionInfo : state.renderPositions) {
			Vec3i positionalOffset = projectionInfo.positionalOffset;

			pose.pushPose();
			pose.translate(positionalOffset.getX(), positionalOffset.getY(), positionalOffset.getZ());
			collector.submitBlock(pose, state.projectedState, projectionInfo.lightCoords, OverlayTexture.NO_OVERLAY, 0);
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
		state.projectedState = be.getProjectedState();

		List<ProjectionInfo> renderPositions = new ArrayList<>();

		if (be.isActive() && !be.isEmpty()) {
			Level level = be.getLevel();
			boolean hanging = be.getBlockState().getValue(ProjectorBlock.HANGING);
			Direction direction = be.getBlockState().getValue(ProjectorBlock.FACING);
			int projectionHeight = be.getProjectionHeight();
			int projectionRange = be.getProjectionRange();
			int projectionOffset = be.getProjectionOffset();
			Vec3i positionalOffset;

			for (int x = 0; x < be.getProjectionWidth(); x++) {
				for (int y = 0; yLoopBoundary.test(projectionHeight, hanging, y); y = hanging ? y - 1 : y + 1) {
					if (!be.isHorizontal())
						positionalOffset = getPositionalOffset(direction, x, y, projectionRange, projectionOffset);
					else
						positionalOffset = getPositionalOffset(direction, x, projectionRange - 16, y + 1, projectionOffset);

					BlockPos renderPos = be.getBlockPos().offset(positionalOffset);

					if (positionalOffset != null && (be.isOverridingBlocks() || level.isEmptyBlock(renderPos)))
						renderPositions.add(new ProjectionInfo(positionalOffset, LevelRenderer.getLightColor(be.getLevel(), renderPos)));
				}
			}
		}

		state.renderPositions = renderPositions;
	}

	/**
	 * Gets the offset of the block position to render a block at based on the projector's settings
	 *
	 * @param direction The direction the projector is facing
	 * @param x The offset from the projectors position on the x-axis of the position at which to draw the fake block
	 * @param y The offset from the projectors position on the y-axis of the position at which to draw the fake block
	 * @param originalDistance The distance in blocks that the fake block is away from the projector (set by player)
	 * @param originalOffset The offset in blocks that the fake block is moved to the side from the projector (set by player)
	 * @return The offset of the fake block to be drawn, null if a non-horizontal direction was given
	 */
	private Vec3i getPositionalOffset(Direction direction, int x, int y, double originalDistance, double originalOffset) {
		int distance = Mth.floor(originalDistance);
		int offset = Mth.floor(originalOffset);

		return switch (direction) {
			case NORTH -> new Vec3i(x + offset, y, distance);
			case SOUTH -> new Vec3i(x + offset, y, -distance);
			case WEST -> new Vec3i(distance, y, x + offset);
			case EAST -> new Vec3i(-distance, y, x + offset);
			default -> null;
		};
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(ProjectorBlockEntity be) {
		return new AABB(be.getBlockPos()).inflate(RENDER_DISTANCE);
	}

	public record ProjectionInfo(Vec3i positionalOffset, int lightCoords) {}
}