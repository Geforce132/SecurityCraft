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

public class ReinforcedPistonTileEntityRenderer extends TileEntityRenderer<ReinforcedPistonTileEntity> {
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

	public ReinforcedPistonTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(ReinforcedPistonTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		World world = te.getWorld();
		if (world != null) {
			BlockPos blockpos = te.getPos().offset(te.getMotionDirection().getOpposite());
			BlockState blockstate = te.getPistonState();
			if (!blockstate.isAir()) {
				BlockModelRenderer.enableCache();
				matrixStackIn.push();
				matrixStackIn.translate(te.getOffsetX(partialTicks), te.getOffsetY(partialTicks), te.getOffsetZ(partialTicks));
				if (blockstate.matchesBlock(SCContent.REINFORCED_PISTON_HEAD.get()) && te.getProgress(partialTicks) <= 4.0F) {
					blockstate = blockstate.with(PistonHeadBlock.SHORT, te.getProgress(partialTicks) <= 0.5F);
					this.renderBlocks(blockpos, blockstate, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				} else if (te.shouldPistonHeadBeRendered() && !te.isExtending()) {
					PistonType pistontype = blockstate.matchesBlock(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
					BlockState blockstate1 = SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.TYPE, pistontype).with(PistonHeadBlock.FACING, blockstate.get(PistonBlock.FACING));
					blockstate1 = blockstate1.with(PistonHeadBlock.SHORT, te.getProgress(partialTicks) >= 0.5F);
					this.renderBlocks(blockpos, blockstate1, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
					BlockPos blockpos1 = blockpos.offset(te.getMotionDirection());
					matrixStackIn.pop();
					matrixStackIn.push();
					blockstate = blockstate.with(PistonBlock.EXTENDED, true);
					this.renderBlocks(blockpos1, blockstate, matrixStackIn, bufferIn, world, true, combinedOverlayIn);
				} else {
					this.renderBlocks(blockpos, blockstate, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				}

				matrixStackIn.pop();
				BlockModelRenderer.disableCache();
			}
		}
	}

	private void renderBlocks(BlockPos pos, BlockState state, MatrixStack stack, IRenderTypeBuffer buffer, World world, boolean checkSides, int combinedOverlay) {
		net.minecraftforge.client.ForgeHooksClient.renderPistonMovedBlocks(pos, state, stack, buffer, world, checkSides, combinedOverlay, blockRenderer == null ? blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher() : blockRenderer);
	}
}
