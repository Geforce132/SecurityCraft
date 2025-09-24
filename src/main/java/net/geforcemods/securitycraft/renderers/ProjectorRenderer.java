package net.geforcemods.securitycraft.renderers;

import java.util.function.BiPredicate;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.renderers.state.ProjectorRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity, ProjectorRenderState> {
	public static final int RENDER_DISTANCE = 100;
	private final BiPredicate<ProjectorRenderState, Integer> yLoopBoundary = (state, y) -> state.isHanging ? y > -state.projectionHeight : y < state.projectionHeight;

	public ProjectorRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(ProjectorRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);

		if (state.isActive && state.projectsBlock) {
			BlockState projectedState = state.projectedState;
			BlockPos pos;

			ModelBlockRenderer.enableCaching();

			for (int x = 0; x < state.projectionWidth; x++) {
				for (int y = 0; yLoopBoundary.test(state, y); y = state.isHanging ? y - 1 : y + 1) {
					pose.pushPose();

					if (!state.isHorizontal)
						pos = translateProjection(state.blockPos, pose, state.facing, x, y, state.projectionRange, state.projectionOffset);
					else
						pos = translateProjection(state.blockPos, pose, state.facing, x, state.projectionRange - 16, y + 1, state.projectionOffset);

					if (pos != null && (state.isOverridingBlocks /*|| level.isEmptyBlock(pos)*/)) { //TODO what to do with this level check
						collector.submitBlock(pose, projectedState, state.lightCoords, OverlayTexture.NO_OVERLAY, 0); //TODO hope this works; also I don't know what to insert for overlay; maybe even a level is needed here?

						/*BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
						BlockStateModel model = dispatcher.getBlockModel(projectedState);
						Function<ChunkSectionLayer, RenderType> toRenderType = RenderTypeHelper::getMovingBlockRenderType;

						dispatcher.renderBatched(projectedState, pos, level, pose, toRenderType.andThen(buffer::getBuffer), true, model.collectParts(be.getLevel(), pos, projectedState, RandomSource.create(projectedState.getSeed(pos))));*/
						ClientHandler.PROJECTOR_RENDER_DELEGATE.trySubmitDelegate(state.projectedBlockEntityRenderState, pose, collector, camera);
					}

					pose.popPose();
				}
			}

			ModelBlockRenderer.clearCache();
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
		state.isActive = be.isActive();
		state.projectsBlock = !be.isContainerEmpty();
		state.isHanging = be.getBlockState().getValue(ProjectorBlock.HANGING);
		state.isHorizontal = be.isHorizontal();
		state.isOverridingBlocks = be.isOverridingBlocks();
		state.projectionWidth = be.getProjectionWidth();
		state.projectionHeight = be.getProjectionHeight();
		state.projectionRange = be.getProjectionRange();
		state.projectionOffset = be.getProjectionOffset();
	}

	/**
	 * Shifts the projection depending on the offset and range set in the projector
	 *
	 * @param bePos The position of the projector which draws the fake block
	 * @param pose the MatrixStack of the current render context
	 * @param direction The direction the projector is facing
	 * @param x The offset from the projectors position on the x axis of the position at which to draw the fake block
	 * @param y The offset from the projectors position on the y axis of the position at which to draw the fake block
	 * @param distance The distance in blocks that the fake block is away from the projector (set by player)
	 * @param offset The offset in blocks that the fake block is moved to the side from the projector (set by player)
	 * @return The BlockPos of the fake block to be drawn, null if an invalid direction was given
	 */
	private BlockPos translateProjection(BlockPos bePos, PoseStack pose, Direction direction, int x, int y, double distance, double offset) {
		BlockPos pos = null;

		switch (direction) {
			case NORTH:
				pos = BlockPos.containing(bePos.getX() + x + offset, bePos.getY() + y, bePos.getZ() + distance);
				pose.translate(0.0D + x + offset, 0.0D + y, distance);
				break;
			case SOUTH:
				pos = BlockPos.containing(bePos.getX() + x + offset, bePos.getY() + y, bePos.getZ() + -distance);
				pose.translate(0.0D + x + offset, 0.0D + y, -distance);
				break;
			case WEST:
				pos = BlockPos.containing(bePos.getX() + distance, bePos.getY() + y, bePos.getZ() + x + offset);
				pose.translate(distance, 0.0D + y, 0.0D + x + offset);
				break;
			case EAST:
				pos = BlockPos.containing(bePos.getX() + -distance, bePos.getY() + y, bePos.getZ() + x + offset);
				pose.translate(-distance, 0.0D + y, 0.0D + x + offset);
				break;
			default:
				break;
		}

		return pos;
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(ProjectorBlockEntity be) {
		return new AABB(be.getBlockPos()).inflate(RENDER_DISTANCE);
	}
}
