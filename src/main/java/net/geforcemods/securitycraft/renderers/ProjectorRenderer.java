package net.geforcemods.securitycraft.renderers;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
	public ProjectorRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(ProjectorBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Vec3 cameraPos) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos);

		if (be.isActive() && !be.isContainerEmpty()) {
			Level level = be.getLevel();
			BlockPos projectorPos = be.getBlockPos();
			BlockState state = be.getProjectedState();
			AABB projectedBlocksArea = be.getProjectedBlocksArea().deflate(0.1D);
			BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

			ModelBlockRenderer.enableCaching();

			for (BlockPos relativePos : BlockPos.betweenClosed(projectedBlocksArea)) {
				BlockPos projectionPos = projectorPos.offset(relativePos);

				pose.pushPose();
				pose.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());

				if (be.isOverridingBlocks() || level.isEmptyBlock(projectionPos)) {
					BlockStateModel model = dispatcher.getBlockModel(state);
					Function<ChunkSectionLayer, RenderType> toRenderType = RenderTypeHelper::getMovingBlockRenderType;

					dispatcher.renderBatched(state, projectionPos, level, pose, toRenderType.andThen(buffer::getBuffer), true, model.collectParts(be.getLevel(), projectionPos, state, RandomSource.create(state.getSeed(projectionPos))));
					ClientHandler.PROJECTOR_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos);
				}

				pose.popPose();
			}

			ModelBlockRenderer.clearCache();
		}
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public boolean shouldRender(ProjectorBlockEntity be, Vec3 cameraPosition) {
		return true; //Projected blocks should always render, regardless of distance
	}

	@Override
	public AABB getRenderBoundingBox(ProjectorBlockEntity be) {
		AABB projectorBoundingBox = new AABB(be.getBlockPos());

		return be.isContainerEmpty() ? projectorBoundingBox.inflate(1.0D) : be.getProjectedBlocksArea().move(be.getBlockPos()).minmax(projectorBoundingBox).inflate(1.0D);
	}
}
