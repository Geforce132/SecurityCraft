package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.items.ColorableItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ClaymoreRenderer extends TileEntitySpecialRenderer<ClaymoreBlockEntity> {
	@Override
	public void render(ClaymoreBlockEntity be, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState state = be.getWorld().getBlockState(be.getPos());

		if (state.getValue(ClaymoreBlock.DEACTIVATED))
			return;

		EnumFacing claymoreFacing = state.getValue(ClaymoreBlock.FACING);
		EnumFacing rotationDirection = claymoreFacing == EnumFacing.EAST || claymoreFacing == EnumFacing.WEST ? claymoreFacing.getOpposite() : claymoreFacing;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y, z + 0.5D);
		GlStateManager.rotate(rotationDirection.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5D, 0.0D, -0.5D);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		float multiplier = 0.0625F;
		float xzStart = 9.0F * multiplier;
		float yOffset = 4.5F * multiplier;
		ItemStack lens = be.getLensContainer().getStackInSlot(0);
		Item item = lens.getItem();
		int r = 255, g = 255, b = 255;

		if (item instanceof ColorableItem && ((ColorableItem) item).hasColor(lens)) {
			int color = ((ColorableItem) item).getColor(lens);

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		GlStateManager.enableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(3);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		builder.pos(xzStart, yOffset, xzStart).color(r, g, b, 255).endVertex();
		builder.pos(11.0F * multiplier, yOffset, 1.0F).color(r, g, b, 0).endVertex();
		builder.pos(7.0F * multiplier, yOffset, xzStart).color(r, g, b, 255).endVertex();
		builder.pos(5.0F * multiplier, yOffset, 1.0F).color(r, g, b, 0).endVertex();
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(ClaymoreBlockEntity be) {
		return true;
	}
}
