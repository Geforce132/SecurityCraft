package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorRenderer extends TileEntityRenderer<ProjectorBlockEntity> {
	public ProjectorRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(ProjectorBlockEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, int combinedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(te, partialTicks, stack, buffer, packedLight, combinedOverlay);

		if (te.isActive() && !te.isEmpty()) {
			BlockState state = te.getProjectedState();
			BlockPos pos;

			RenderSystem.disableCull();

			for (int x = 0; x < te.getProjectionWidth(); x++) {
				for (int y = 0; y < te.getProjectionHeight(); y++) {
					stack.pushPose();

					if (!te.isHorizontal())
						pos = translateProjection(te.getBlockPos(), stack, te.getBlockState().getValue(ProjectorBlock.FACING), x, y, te.getProjectionRange(), te.getProjectionOffset());
					else
						pos = translateProjection(te.getBlockPos(), stack, te.getBlockState().getValue(ProjectorBlock.FACING), x, te.getProjectionRange() - 16, y + 1, te.getProjectionOffset());

					if (pos != null && te.getLevel().isEmptyBlock(pos)) {
						for (RenderType renderType : RenderType.chunkBufferLayers()) {
							if (RenderTypeLookup.canRenderInLayer(state, renderType))
								Minecraft.getInstance().getBlockRenderer().renderBatched(state, pos, te.getLevel(), stack, buffer.getBuffer(renderType), true, te.level.random);
						}

						ClientHandler.PROJECTOR_RENDER_DELEGATE.tryRenderDelegate(te, partialTicks, stack, buffer, packedLight, combinedOverlay);
					}

					stack.popPose();
				}
			}

			RenderSystem.enableCull();
		}
	}

	/**
	 * Shifts the projection depending on the offset and range set in the projector
	 *
	 * @param tePos The position of the projector which draws the fake block
	 * @param stack the MatrixStack of the current render context
	 * @param direction The direction the projector is facing
	 * @param x The offset from the projectors position on the x axis of the position at which to draw the fake block
	 * @param y The offset from the projectors position on the y axis of the position at which to draw the fake block
	 * @param distance The distance in blocks that the fake block is away from the projector (set by player)
	 * @param offset The offset in blocks that the fake block is moved to the side from the projector (set by player)
	 * @return The BlockPos of the fake block to be drawn, null if an invalid direction was given
	 */
	private BlockPos translateProjection(BlockPos tePos, MatrixStack stack, Direction direction, int x, int y, double distance, double offset) {
		BlockPos pos = null;

		if (direction == Direction.NORTH) {
			pos = new BlockPos(tePos.getX() + x + offset, tePos.getY() + y, tePos.getZ() + distance);
			stack.translate(0.0D + x + offset, 0.0D + y, distance);
		}
		else if (direction == Direction.SOUTH) {
			pos = new BlockPos(tePos.getX() + x + offset, tePos.getY() + y, tePos.getZ() + -distance);
			stack.translate(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if (direction == Direction.WEST) {
			pos = new BlockPos(tePos.getX() + distance, tePos.getY() + y, tePos.getZ() + x + offset);
			stack.translate(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if (direction == Direction.EAST) {
			pos = new BlockPos(tePos.getX() + -distance, tePos.getY() + y, tePos.getZ() + x + offset);
			stack.translate(-distance, 0.0D + y, 0.0D + x + offset);
		}

		return pos;
	}

	@Override
	public boolean shouldRenderOffScreen(ProjectorBlockEntity te) {
		return true;
	}
}
