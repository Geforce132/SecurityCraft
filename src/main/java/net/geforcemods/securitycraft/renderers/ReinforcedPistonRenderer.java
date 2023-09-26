package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class ReinforcedPistonRenderer extends TileEntityRenderer<ReinforcedPistonBlockEntity> {
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

	public ReinforcedPistonRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(ReinforcedPistonBlockEntity be, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		World world = be.getLevel();

		if (world != null) {
			BlockPos oppositePos = be.getBlockPos().relative(be.getMovementDirection().getOpposite());
			BlockState state = be.getPistonState();

			if (!state.isAir()) {
				BlockModelRenderer.enableCaching();
				poseStack.pushPose();
				poseStack.translate(be.getOffsetX(partialTicks), be.getOffsetY(partialTicks), be.getOffsetZ(partialTicks));

				if (state.is(SCContent.REINFORCED_PISTON_HEAD.get()) && be.getProgress(partialTicks) <= 4.0F) {
					state = state.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTicks) <= 0.5F);
					renderBlocks(oppositePos, state, poseStack, buffer, world, false, combinedOverlay);
				}
				else if (be.isSourcePiston() && !be.isExtending()) {
					PistonType pistonType = state.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState headState = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, pistonType).setValue(DirectionalBlock.FACING, state.getValue(DirectionalBlock.FACING));
					BlockPos renderPos = oppositePos.relative(be.getMovementDirection());

					headState = headState.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTicks) >= 0.5F);
					renderBlocks(oppositePos, headState, poseStack, buffer, world, false, combinedOverlay);
					poseStack.popPose();
					poseStack.pushPose();
					state = state.setValue(PistonBlock.EXTENDED, true);
					renderBlocks(renderPos, state, poseStack, buffer, world, true, combinedOverlay);
				}
				else
					renderBlocks(oppositePos, state, poseStack, buffer, world, false, combinedOverlay);

				poseStack.popPose();
				BlockModelRenderer.clearCache();
			}
		}
	}

	private void renderBlocks(BlockPos pos, BlockState state, MatrixStack poseStack, IRenderTypeBuffer buffer, World world, boolean checkSides, int combinedOverlay) {
		if (blockRenderer == null)
			blockRenderer = Minecraft.getInstance().getBlockRenderer();

		ForgeHooksClient.renderPistonMovedBlocks(pos, state, poseStack, buffer, world, checkSides, combinedOverlay, blockRenderer);
	}
}
