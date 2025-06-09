package net.geforcemods.securitycraft.renderers;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.common.util.TriPredicate;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
	public static final int RENDER_DISTANCE = 100;
	private final TriPredicate<ProjectorBlockEntity, Boolean, Integer> yLoopBoundary = (be, hanging, y) -> hanging ? y > -be.getProjectionHeight() : y < be.getProjectionHeight();

	public ProjectorRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(ProjectorBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Vec3 cameraPos) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos);

		if (be.isActive() && !be.isContainerEmpty()) {
			Level level = be.getLevel();
			BlockState state = be.getProjectedState();
			boolean hanging = be.getBlockState().getValue(ProjectorBlock.HANGING);
			BlockPos pos;

			ModelBlockRenderer.enableCaching();

			for (int x = 0; x < be.getProjectionWidth(); x++) {
				for (int y = 0; yLoopBoundary.test(be, hanging, y); y = hanging ? y - 1 : y + 1) {
					pose.pushPose();

					if (!be.isHorizontal())
						pos = translateProjection(be.getBlockPos(), pose, be.getBlockState().getValue(ProjectorBlock.FACING), x, y, be.getProjectionRange(), be.getProjectionOffset());
					else
						pos = translateProjection(be.getBlockPos(), pose, be.getBlockState().getValue(ProjectorBlock.FACING), x, be.getProjectionRange() - 16, y + 1, be.getProjectionOffset());

					if (pos != null && (be.isOverridingBlocks() || level.isEmptyBlock(pos))) {
						BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
						BlockStateModel model = dispatcher.getBlockModel(state);
						Function<ChunkSectionLayer, RenderType> toRenderType = RenderTypeHelper::getMovingBlockRenderType;

						dispatcher.renderBatched(state, pos, level, pose, toRenderType.andThen(buffer::getBuffer), true, model.collectParts(be.getLevel(), pos, state, RandomSource.create(state.getSeed(pos))));
						ClientHandler.PROJECTOR_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos);
					}

					pose.popPose();
				}
			}

			ModelBlockRenderer.clearCache();
		}
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
