package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemTileEntityRenderer extends TileEntityRenderer<TrophySystemTileEntity> {

	@Override
	public void render(TrophySystemTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(tileEntityIn.entityBeingTargeted == null) return;

		Vec3d blockpos = new Vec3d(x + 0.5D, y + 0.75D, z + 0.5D);

		RenderSystem.pushMatrix();
		RenderSystem.translated(blockpos.x, blockpos.y, blockpos.z);
		RenderSystem.lineWidth(2F);
		RenderSystem.disableTexture();
		RenderSystem.disableLighting();

		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0, 0, 0).color(1, 0, 0, 1F).endVertex();
		bb.pos(tileEntityIn.entityBeingTargeted.func_226277_ct_() - tileEntityIn.getPos().getX() - 0.5D, tileEntityIn.entityBeingTargeted.func_226278_cu_() - tileEntityIn.getPos().getY() - 0.75D, tileEntityIn.entityBeingTargeted.func_226281_cx_() - tileEntityIn.getPos().getZ() - 0.5D).color(1, 0, 0, 1F).endVertex();
		Tessellator.getInstance().draw();

		RenderSystem.enableLighting();
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}

}
