package net.geforcemods.securitycraft.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;

public class ReinforcedPistonHeadRenderer implements BlockEntityRenderer<ReinforcedPistonMovingBlockEntity> {
	private BlockRenderDispatcher blockRenderer;

	public ReinforcedPistonHeadRenderer(BlockEntityRendererProvider.Context ctx) {
		blockRenderer = ctx.getBlockRenderDispatcher();
	}

	@Override
	public void render(ReinforcedPistonMovingBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Vec3 cameraPos) {
		Level level = be.getLevel();

		if (level != null) {
			BlockPos oppositePos = be.getBlockPos().relative(be.getMovementDirection().getOpposite());
			BlockState state = be.getMovedState();

			if (!state.isAir()) {
				ModelBlockRenderer.enableCaching();
				poseStack.pushPose();
				poseStack.translate(be.getOffsetX(partialTicks), be.getOffsetY(partialTicks), be.getOffsetZ(partialTicks));

				if (state.is(SCContent.REINFORCED_PISTON_HEAD.get()) && be.getProgress(partialTicks) <= 4.0F) {
					state = state.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTicks) <= 0.5F);
					renderBlocks(oppositePos, state, poseStack, buffer, level, false, combinedOverlay);
				}
				else if (be.isSourcePiston() && !be.isExtending()) {
					PistonType pistonType = state.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState headState = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, pistonType).setValue(DirectionalBlock.FACING, state.getValue(DirectionalBlock.FACING));
					BlockPos renderPos = oppositePos.relative(be.getMovementDirection());

					headState = headState.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTicks) >= 0.5F);
					renderBlocks(oppositePos, headState, poseStack, buffer, level, false, combinedOverlay);
					poseStack.popPose();
					poseStack.pushPose();
					state = state.setValue(PistonBaseBlock.EXTENDED, true);
					renderBlocks(renderPos, state, poseStack, buffer, level, true, combinedOverlay);
				}
				else
					renderBlocks(oppositePos, state, poseStack, buffer, level, false, combinedOverlay);

				poseStack.popPose();
				ModelBlockRenderer.clearCache();
			}
		}
	}

	private void renderBlocks(BlockPos pos, BlockState state, PoseStack poseStack, MultiBufferSource buffer, Level level, boolean extended, int combinedOverlay) {
		if (blockRenderer == null)
			blockRenderer = Minecraft.getInstance().getBlockRenderer();

		List<BlockModelPart> list = blockRenderer.getBlockModel(state).collectParts(level, pos, state, RandomSource.create(state.getSeed(pos)));

		blockRenderer.getModelRenderer().tesselateBlock(level, list, state, pos, poseStack, renderType -> buffer.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)), extended, combinedOverlay);
	}

	@Override
	public int getViewDistance() {
		return 68;
	}
}
