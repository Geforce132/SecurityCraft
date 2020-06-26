package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPocketManagerTileEntityRenderer extends TileEntityRenderer<BlockPocketManagerTileEntity> {

	@Override
	public void render(BlockPocketManagerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		// The code below draws the outline border of a block pocket (centered at the manager).

		if(!te.showOutline)
			return;

		Direction facing = te.getWorld().getBlockState(te.getPos()).get(BlockPocketManagerBlock.FACING);
		int size = te.size;
		int half = (size-1)/2;
		int leftX = -half;
		int rightX = half+1;
		int frontZ = facing == Direction.NORTH || facing == Direction.WEST ? 0 : 1;
		int backZ = facing == Direction.NORTH || facing == Direction.WEST ? size : 1-size;

		if(facing == Direction.EAST || facing == Direction.WEST) //x- and z-values get switched when the manager's direction is west or east
		{
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half;
			backZ = half + 1;
		}

		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		//comments are for facing north, other directions work but names don't match
		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);
		GlStateManager.lineWidth(2F);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
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
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(BlockPocketManagerTileEntity te)
	{
		return te.showOutline;
	}
}
