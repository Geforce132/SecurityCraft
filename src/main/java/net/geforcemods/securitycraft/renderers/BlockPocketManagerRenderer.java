package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.renderers.state.BlockPocketManagerRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class BlockPocketManagerRenderer implements BlockEntityRenderer<BlockPocketManagerBlockEntity, BlockPocketManagerRenderState> {
	public static final int RENDER_DISTANCE = 100;

	public BlockPocketManagerRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(BlockPocketManagerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (!state.showsOutline || !state.ownedByPlayer)
			return;

		BlockPos blockPos = state.blockPos;
		Vec3 cameraPos = camera.pos;
		
		collector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, builder) -> {
			ShapeRenderer.renderShape(poseStack, builder, Shapes.block(), blockPos.getX() - cameraPos.x, blockPos.getY() - cameraPos.y, blockPos.getZ() - cameraPos.z, state.color, 1.0F); //TODO test position and line width
		});
	}

	@Override
	public BlockPocketManagerRenderState createRenderState() {
		return new BlockPocketManagerRenderState();
	}

	@Override
	public void extractRenderState(BlockPocketManagerBlockEntity be, BlockPocketManagerRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		Direction facing = be.getBlockState().getValue(BlockPocketManagerBlock.FACING);
		int offset = facing == Direction.NORTH || facing == Direction.EAST ? -be.getAutoBuildOffset() : be.getAutoBuildOffset(); //keep negative values moving the offset to the left consistent
		int size = be.getSize();
		int half = (size - 1) / 2;
		int leftX = -half + offset;
		int rightX = half + 1 + offset;
		int frontZ = facing == Direction.NORTH || facing == Direction.WEST ? 0 : 1;
		int backZ = facing == Direction.NORTH || facing == Direction.WEST ? size : 1 - size;
		int packedColor = be.getColor();

		//x- and z-values get switched when the manager's direction is west or east
		if (facing == Direction.EAST || facing == Direction.WEST) {
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half + offset;
			backZ = half + 1 + offset;
		}

		state.showsOutline = be.showsOutline();
		state.ownedByPlayer = be.isOwnedBy(Minecraft.getInstance().player);
		state.shape = Block.box(leftX, 0, frontZ, rightX, size, backZ);
		state.color = packedColor;
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(BlockPocketManagerBlockEntity be) {
		return new AABB(be.getBlockPos()).inflate(RENDER_DISTANCE);
	}
}
