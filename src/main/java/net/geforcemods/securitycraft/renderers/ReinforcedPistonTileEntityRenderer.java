package net.geforcemods.securitycraft.renderers;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.ReinforcedPistonTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class ReinforcedPistonTileEntityRenderer extends TileEntityRenderer<ReinforcedPistonTileEntity> {
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

	public ReinforcedPistonTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(ReinforcedPistonTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		World world = te.getLevel();

		if (world != null) {
			BlockPos oppositePos = te.getBlockPos().relative(te.getMotionDirection().getOpposite());
			BlockState state = te.getPistonState();

			if (!state.isAir()) {
				BlockModelRenderer.enableCaching();
				matrix.pushPose();
				matrix.translate(te.getOffsetX(partialTicks), te.getOffsetY(partialTicks), te.getOffsetZ(partialTicks));

				if (state.getBlock() == SCContent.REINFORCED_PISTON_HEAD.get() && te.getProgress(partialTicks) <= 4.0F) {
					state = state.setValue(PistonHeadBlock.SHORT, te.getProgress(partialTicks) <= 0.5F);
					renderBlocks(oppositePos, state, matrix, buffer, world, false, combinedOverlay);
				}
				else if (te.shouldPistonHeadBeRendered() && !te.isExtending()) {
					PistonType pistonType = state.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState headState = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, pistonType).setValue(PistonHeadBlock.FACING, state.getValue(PistonBlock.FACING));
					BlockPos renderPos = oppositePos.relative(te.getMotionDirection());

					headState = headState.setValue(PistonHeadBlock.SHORT, te.getProgress(partialTicks) >= 0.5F);
					renderBlocks(oppositePos, headState, matrix, buffer, world, false, combinedOverlay);
					matrix.popPose();
					matrix.pushPose();
					state = state.setValue(PistonBlock.EXTENDED, true);
					renderBlocks(renderPos, state, matrix, buffer, world, true, combinedOverlay);
				}
				else
					renderBlocks(oppositePos, state, matrix, buffer, world, false, combinedOverlay);

				matrix.popPose();
				BlockModelRenderer.clearCache();
			}
		}
	}

	private void renderBlocks(BlockPos pos, BlockState state, MatrixStack stack, IRenderTypeBuffer buffer, World world, boolean checkSides, int combinedOverlay) {
		RenderType.chunkBufferLayers().stream().filter(type -> RenderTypeLookup.canRenderInLayer(state, type)).forEach(rendertype -> {
			ForgeHooksClient.setRenderLayer(rendertype);
			IVertexBuilder ivertexbuilder = buffer.getBuffer(rendertype);

			if (blockRenderer == null)
				blockRenderer = Minecraft.getInstance().getBlockRenderer();

			blockRenderer.getModelRenderer().tesselateBlock(world, this.blockRenderer.getBlockModel(state), state, pos, stack, ivertexbuilder, checkSides, new Random(), state.getSeed(pos), combinedOverlay);
		});

		ForgeHooksClient.setRenderLayer(null);
	}
}
