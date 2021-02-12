package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blocks.BlockBlockPocketManager;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityBlockPocketManagerRenderer extends TileEntitySpecialRenderer<TileEntityBlockPocketManager> {

	@Override
	public void render(TileEntityBlockPocketManager te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		// The code below draws the outline border of a block pocket.

		if(!te.showOutline)
			return;

		EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockBlockPocketManager.FACING);
		int offset = facing == EnumFacing.NORTH || facing == EnumFacing.EAST ? -te.autoBuildOffset : te.autoBuildOffset; //keep negative values moving the offset to the left consistent
		int size = te.size;
		int half = (size - 1) / 2;
		int leftX = -half + offset;
		int rightX = half + 1 + offset;
		int frontZ = facing == EnumFacing.NORTH || facing == EnumFacing.WEST ? 0 : 1;
		int backZ = facing == EnumFacing.NORTH || facing == EnumFacing.WEST ? size : 1 - size;

		if(facing == EnumFacing.EAST || facing == EnumFacing.WEST) //x- and z-values get switched when the manager's EnumFacing is west or east
		{
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half + offset;
			backZ = half + 1 + offset;
		}

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		//comments are for facing north, other EnumFacings work but names don't match
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glLineWidth(2F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		//bottom points
		builder.pos(leftX, 0.0F, frontZ).color(0, 0, 255, 255).endVertex();
		builder.pos(rightX, 0.0F, frontZ).color(0, 0, 255, 255).endVertex();
		builder.pos(rightX, 0.0F, backZ).color(0, 0, 255, 255).endVertex();
		builder.pos(leftX, 0.0F, backZ).color(0, 0, 255, 255).endVertex();
		builder.pos(leftX, 0.0F, frontZ).color(0, 0, 255, 255).endVertex();
		//remaining lines on the left face
		builder.pos(leftX, size, frontZ).color(0, 0, 255, 255).endVertex();
		builder.pos(leftX, size, backZ).color(0, 0, 255, 255).endVertex();
		builder.pos(leftX, 0, backZ).color(0, 0, 255, 255).endVertex();
		//remaining lines of back face
		builder.pos(leftX, size, backZ).color(0, 0, 0, 0).endVertex(); //going back up, but line is invisible
		builder.pos(rightX, size, backZ).color(0, 0, 255, 255).endVertex();
		builder.pos(rightX, 0, backZ).color(0, 0, 255, 255).endVertex();
		//remaining lines of right face
		builder.pos(rightX, size, backZ).color(0, 0, 0, 0).endVertex(); //going back up, but line is invisible
		builder.pos(rightX, size, frontZ).color(0, 0, 255, 255).endVertex();
		builder.pos(rightX, 0, frontZ).color(0, 0, 255, 255).endVertex();
		//remaining line at the top front
		builder.pos(rightX, size, frontZ).color(0, 0, 0, 0).endVertex(); //going back up, but line is invisible
		builder.pos(leftX, size, frontZ).color(0, 0, 255, 255).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(TileEntityBlockPocketManager te)
	{
		return te.showOutline;
	}
}
