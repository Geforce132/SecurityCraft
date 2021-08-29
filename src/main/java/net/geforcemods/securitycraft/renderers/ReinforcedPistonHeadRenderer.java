package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class ReinforcedPistonHeadRenderer implements BlockEntityRenderer<ReinforcedPistonMovingBlockEntity> {
	private BlockRenderDispatcher blockRenderer;

	public ReinforcedPistonHeadRenderer(BlockEntityRendererProvider.Context ctx) {
		this.blockRenderer = ctx.getBlockRenderDispatcher();
	}

	@Override
	public void render(ReinforcedPistonMovingBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Level world = te.getLevel();

		if (world != null) {
			BlockPos blockpos = te.getBlockPos().relative(te.getMovementDirection().getOpposite());
			BlockState blockstate = te.getMovedState();

			if (!blockstate.isAir()) {
				ModelBlockRenderer.enableCaching();
				matrixStack.pushPose();
				matrixStack.translate(te.getOffsetX(partialTicks), te.getOffsetY(partialTicks), te.getOffsetZ(partialTicks));

				if (blockstate.is(SCContent.REINFORCED_PISTON_HEAD.get()) && te.getProgress(partialTicks) <= 4.0F) {
					blockstate = blockstate.setValue(PistonHeadBlock.SHORT, te.getProgress(partialTicks) <= 0.5F);

					this.renderBlocks(blockpos, blockstate, matrixStack, buffer, world, false, combinedOverlay);
				} else if (te.isSourcePiston() && !te.isExtending()) {
					PistonType pistontype = blockstate.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState blockstate1 = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, pistontype).setValue(PistonHeadBlock.FACING, blockstate.getValue(PistonBaseBlock.FACING));

					blockstate1 = blockstate1.setValue(PistonHeadBlock.SHORT, te.getProgress(partialTicks) >= 0.5F);
					this.renderBlocks(blockpos, blockstate1, matrixStack, buffer, world, false, combinedOverlay);
					BlockPos blockpos1 = blockpos.relative(te.getMovementDirection());
					matrixStack.popPose();
					matrixStack.pushPose();
					blockstate = blockstate.setValue(PistonBaseBlock.EXTENDED, true);
					this.renderBlocks(blockpos1, blockstate, matrixStack, buffer, world, true, combinedOverlay);
				} else {
					this.renderBlocks(blockpos, blockstate, matrixStack, buffer, world, false, combinedOverlay);
				}

				matrixStack.popPose();
				ModelBlockRenderer.clearCache();
			}
		}
	}

	private void renderBlocks(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource buffer, Level world, boolean checkSides, int combinedOverlay) {
		net.minecraftforge.client.ForgeHooksClient.renderPistonMovedBlocks(pos, state, stack, buffer, world, checkSides, combinedOverlay, blockRenderer == null ? blockRenderer = Minecraft.getInstance().getBlockRenderer() : blockRenderer);
	}

	@Override
	public int getViewDistance() {
		return 68;
	}
}
