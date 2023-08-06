package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class ClaymoreRenderer implements BlockEntityRenderer<ClaymoreBlockEntity> {
	public ClaymoreRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(ClaymoreBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (be.getBlockState().getValue(ClaymoreBlock.DEACTIVATED))
			return;

		Direction claymoreFacing = be.getBlockState().getValue(ClaymoreBlock.FACING);
		Direction rotationDirection = switch (claymoreFacing) {
			case EAST, WEST -> claymoreFacing.getOpposite();
			default -> claymoreFacing;
		};

		pose.pushPose();
		pose.translate(0.5D, 0.0D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(rotationDirection.toYRot()));
		pose.translate(-0.5D, 0.0D, -0.5D);

		VertexConsumer builder = buffer.getBuffer(RenderType.lines());
		Matrix4f positionMatrix = pose.last().pose();
		Vec3i normal = claymoreFacing.getNormal();
		float multiplier = 0.0625F;
		float xzStart = 9.0F * multiplier;
		float y = 4.5F * multiplier;
		ItemStack lens = be.getLensContainer().getItem(0);
		int r = 255, g = 255, b = 255;

		if (lens.getItem() instanceof DyeableLeatherItem item && item.hasCustomColor(lens)) {
			int color = item.getColor(lens);

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		builder.vertex(positionMatrix, xzStart, y, xzStart).color(r, g, b, 255).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.vertex(positionMatrix, 11.0F * multiplier, y, 1.0F).color(r, g, b, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.vertex(positionMatrix, 7.0F * multiplier, y, xzStart).color(r, g, b, 255).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.vertex(positionMatrix, 5.0F * multiplier, y, 1.0F).color(r, g, b, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		pose.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(ClaymoreBlockEntity be) {
		return true;
	}
}
