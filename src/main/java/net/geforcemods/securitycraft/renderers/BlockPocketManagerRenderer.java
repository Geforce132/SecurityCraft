package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPocketManagerRenderer extends TileEntityRenderer<BlockPocketManagerBlockEntity> {
	public BlockPocketManagerRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(BlockPocketManagerBlockEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// The code below draws the outline border of a block pocket.

		if (!te.showsOutline() || !te.isOwnedBy(Minecraft.getInstance().player))
			return;

		Direction facing = te.getBlockState().getValue(BlockPocketManagerBlock.FACING);
		int offset = facing == Direction.NORTH || facing == Direction.EAST ? -te.getAutoBuildOffset() : te.getAutoBuildOffset(); //keep negative values moving the offset to the left consistent
		int size = te.getSize();
		int half = (size - 1) / 2;
		int leftX = -half + offset;
		int rightX = half + 1 + offset;
		int frontZ = facing == Direction.NORTH || facing == Direction.WEST ? 0 : 1;
		int backZ = facing == Direction.NORTH || facing == Direction.WEST ? size : 1 - size;

		//x- and z-values get switched when the manager's direction is west or east
		if (facing == Direction.EAST || facing == Direction.WEST) {
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half + offset;
			backZ = half + 1 + offset;
		}

		ClientUtils.renderBoxInLevel(buffer, matrix.last().pose(), leftX, rightX, frontZ, backZ, size, te.getColor());
	}

	@Override
	public boolean shouldRenderOffScreen(BlockPocketManagerBlockEntity te) {
		return te.showsOutline();
	}
}
