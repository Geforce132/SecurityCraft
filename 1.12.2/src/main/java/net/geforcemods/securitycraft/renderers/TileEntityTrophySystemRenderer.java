package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityTrophySystemRenderer extends TileEntitySpecialRenderer<TileEntityTrophySystem> {
	
	@Override
	public void render(TileEntityTrophySystem tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting. The line seems a bit wonky at the moment (and is always black
		// for some reason?), but I may have done some of the maths wrong. Can always remove
		// if it doesn't look good after more testing
		
		if(tileEntityIn.entityBeingTargeted == null) return;
		
		Vec3d blockpos = new Vec3d(x + 0.5D, y + 0.5D, z + 0.5D);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glLineWidth(2F);
		GlStateManager.color(1, 0, 0, 0);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(blockpos.x, blockpos.y, blockpos.z).color(1, 0, 0, 1F).endVertex();
		bb.pos(tileEntityIn.entityBeingTargeted.posX - tileEntityIn.getPos().getX(), tileEntityIn.entityBeingTargeted.posY - tileEntityIn.getPos().getY(), tileEntityIn.entityBeingTargeted.posZ - tileEntityIn.getPos().getZ()).color(1, 0, 0, 1F).endVertex();
		Tessellator.getInstance().draw();
		
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

}
