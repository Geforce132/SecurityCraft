package net.geforcemods.securitycraft.renderers;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
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
		pose.mulPose(Axis.YP.rotationDegrees(rotationDirection.toYRot()));
		pose.translate(-0.5D, 0.0D, -0.5D);

		VertexConsumer builder = buffer.getBuffer(RenderType.lines());
		Matrix4f positionMatrix = pose.last().pose();
		Vec3i normal = claymoreFacing.getNormal();
		float multiplier = 0.0625F;
		float xzStart = 9.0F * multiplier;
		float y = 4.5F * multiplier;
		ItemStack lens = be.getLensContainer().getItem(0);
		int r = 255, g = 255, b = 255;

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		builder.addVertex(positionMatrix, xzStart, y, xzStart).setColor(r, g, b, 255).setNormal(normal.getX(), normal.getY(), normal.getZ());
		builder.addVertex(positionMatrix, 11.0F * multiplier, y, 1.0F).setColor(r, g, b, 0).setNormal(normal.getX(), normal.getY(), normal.getZ());
		builder.addVertex(positionMatrix, 7.0F * multiplier, y, xzStart).setColor(r, g, b, 255).setNormal(normal.getX(), normal.getY(), normal.getZ());
		builder.addVertex(positionMatrix, 5.0F * multiplier, y, 1.0F).setColor(r, g, b, 0).setNormal(normal.getX(), normal.getY(), normal.getZ());
		pose.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(ClaymoreBlockEntity be) {
		return true;
	}
}
