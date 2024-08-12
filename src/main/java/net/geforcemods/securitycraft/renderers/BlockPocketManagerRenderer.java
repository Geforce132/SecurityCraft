package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class BlockPocketManagerRenderer implements BlockEntityRenderer<BlockPocketManagerBlockEntity> {
	public static final int RENDER_DISTANCE = 100;

	public BlockPocketManagerRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(BlockPocketManagerBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
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

		//x- and z-values get switched when the manager's direction is west or east
		if (facing == Direction.EAST || facing == Direction.WEST) {
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half + offset;
			backZ = half + 1 + offset;
		}

		ClientUtils.renderBoxInLevel(buffer, pose, leftX, rightX, frontZ, backZ, size, be.getColor());
	}

	@Override
	public boolean shouldRenderOffScreen(BlockPocketManagerBlockEntity be) {
		return be.showsOutline();
	}

	@Override
	public AABB getRenderBoundingBox(BlockPocketManagerBlockEntity be) {
		return new AABB(be.getBlockPos()).inflate(RENDER_DISTANCE);
	}
}
