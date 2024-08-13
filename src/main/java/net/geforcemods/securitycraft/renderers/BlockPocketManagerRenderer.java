package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPocketManagerRenderer extends TileEntityRenderer<BlockPocketManagerBlockEntity> {
	public BlockPocketManagerRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(BlockPocketManagerBlockEntity be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		// The code below draws the outline border of a block pocket.

		if (!be.showsOutline() || !be.isOwnedBy(Minecraft.getInstance().player))
			return;

		Direction facing = be.getBlockState().getValue(BlockPocketManagerBlock.FACING);
		int offset = facing == Direction.NORTH || facing == Direction.EAST ? -be.getAutoBuildOffset() : be.getAutoBuildOffset(); //keep negative values moving the offset to the left consistent
		int size = be.getSize();
		int half = (size - 1) / 2;
		int leftX = -half + offset;
		int rightX = half + 1 + offset;
		int frontZ = facing == Direction.NORTH || facing == Direction.WEST ? 0 : 1;
		int backZ = facing == Direction.NORTH || facing == Direction.WEST ? size : 1 - size;
		int packedColor = be.getColor();
		float r = ColorHelper.PackedColor.red(packedColor) / 255.0F;
		float g = ColorHelper.PackedColor.green(packedColor) / 255.0F;
		float b = ColorHelper.PackedColor.blue(packedColor) / 255.0F;

		//x- and z-values get switched when the manager's direction is west or east
		if (facing == Direction.EAST || facing == Direction.WEST) {
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half + offset;
			backZ = half + 1 + offset;
		}

		WorldRenderer.renderLineBox(pose, buffer.getBuffer(RenderType.lines()), leftX, 0, frontZ, rightX, size, backZ, r, g, b, 1.0F);
	}

	@Override
	public boolean shouldRenderOffScreen(BlockPocketManagerBlockEntity te) {
		return te.showsOutline();
	}
}
