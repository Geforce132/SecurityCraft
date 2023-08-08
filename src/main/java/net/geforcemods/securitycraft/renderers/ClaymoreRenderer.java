package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class ClaymoreRenderer extends TileEntityRenderer<ClaymoreBlockEntity> {
	public ClaymoreRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(ClaymoreBlockEntity be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (be.getBlockState().getValue(ClaymoreBlock.DEACTIVATED))
			return;

		Direction claymoreFacing = be.getBlockState().getValue(ClaymoreBlock.FACING);
		Direction rotationDirection = claymoreFacing == Direction.EAST || claymoreFacing == Direction.WEST ? claymoreFacing.getOpposite() : claymoreFacing;

		pose.pushPose();
		pose.translate(0.5D, 0.0D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(rotationDirection.toYRot()));
		pose.translate(-0.5D, 0.0D, -0.5D);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuilder();
		Matrix4f positionMatrix = pose.last().pose();
		float multiplier = 0.0625F;
		float xzStart = 9.0F * multiplier;
		float y = 4.5F * multiplier;
		ItemStack lens = be.getLensContainer().getItem(0);
		Item item = lens.getItem();
		int r = 255, g = 255, b = 255;

		if (item instanceof IDyeableArmorItem && ((IDyeableArmorItem) item).hasCustomColor(lens)) {
			int color = ((IDyeableArmorItem) item).getColor(lens);

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(3);
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		builder.vertex(positionMatrix, xzStart, y, xzStart).color(r, g, b, 255).endVertex();
		builder.vertex(positionMatrix, 11.0F * multiplier, y, 1.0F).color(r, g, b, 0).endVertex();
		builder.vertex(positionMatrix, 7.0F * multiplier, y, xzStart).color(r, g, b, 255).endVertex();
		builder.vertex(positionMatrix, 5.0F * multiplier, y, 1.0F).color(r, g, b, 0).endVertex();
		tessellator.end();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		pose.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(ClaymoreBlockEntity be) {
		return true;
	}
}
