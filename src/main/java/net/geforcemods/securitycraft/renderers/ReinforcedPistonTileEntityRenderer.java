package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.ReinforcedPistonTileEntity;
import net.minecraft.block.BlockState;
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

public class ReinforcedPistonTileEntityRenderer extends TileEntityRenderer<ReinforcedPistonTileEntity> {
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

	public ReinforcedPistonTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(ReinforcedPistonTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		World world = te.getWorld();

		if (world != null) {
			BlockPos oppositePos = te.getPos().offset(te.getMotionDirection().getOpposite());
			BlockState state = te.getPistonState();

			if (!state.isAir()) {
				BlockModelRenderer.enableCache();
				matrix.push();
				matrix.translate(te.getOffsetX(partialTicks), te.getOffsetY(partialTicks), te.getOffsetZ(partialTicks));

				if (state.matchesBlock(SCContent.REINFORCED_PISTON_HEAD.get()) && te.getProgress(partialTicks) <= 4.0F) {
					state = state.with(PistonHeadBlock.SHORT, te.getProgress(partialTicks) <= 0.5F);
					renderBlocks(oppositePos, state, matrix, buffer, world, false, combinedOverlay);
				} else if (te.shouldPistonHeadBeRendered() && !te.isExtending()) {
					PistonType pistonType = state.matchesBlock(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState headState = SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.TYPE, pistonType).with(PistonHeadBlock.FACING, state.get(PistonBlock.FACING));
					BlockPos renderPos = oppositePos.offset(te.getMotionDirection());

					headState = headState.with(PistonHeadBlock.SHORT, te.getProgress(partialTicks) >= 0.5F);
					renderBlocks(oppositePos, headState, matrix, buffer, world, false, combinedOverlay);
					matrix.pop();
					matrix.push();
					state = state.with(PistonBlock.EXTENDED, true);
					renderBlocks(renderPos, state, matrix, buffer, world, true, combinedOverlay);
				} else {
					renderBlocks(oppositePos, state, matrix, buffer, world, false, combinedOverlay);
				}

				matrix.pop();
				BlockModelRenderer.disableCache();
			}
		}
	}

	private void renderBlocks(BlockPos pos, BlockState state, MatrixStack stack, IRenderTypeBuffer buffer, World world, boolean checkSides, int combinedOverlay) {
		ForgeHooksClient.renderPistonMovedBlocks(pos, state, stack, buffer, world, checkSides, combinedOverlay, blockRenderer == null ? blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher() : blockRenderer);
	}
}
