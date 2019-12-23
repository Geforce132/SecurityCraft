package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemTileEntityRenderer extends TileEntityRenderer<TrophySystemTileEntity> {

	public TrophySystemTileEntityRenderer()
	{
		super(TileEntityRendererDispatcher.instance);
	}

	@Override
	public void func_225616_a_(TrophySystemTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(te.entityBeingTargeted == null) return;

		BlockPos pos = te.getPos();//new Vec3d(x + 0.5D, y + 0.75D, z + 0.5D);

		RenderSystem.pushMatrix();
		RenderSystem.translated(pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D);
		RenderSystem.lineWidth(2F);
		RenderSystem.disableTexture();
		RenderSystem.disableLighting();

		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		//pos, color (?)
		bb.func_225582_a_(0, 0, 0).func_225586_a_(255, 0, 0, 1).endVertex();
		bb.func_225582_a_(te.entityBeingTargeted.func_226277_ct_() - te.getPos().getX() - 0.5D, te.entityBeingTargeted.func_226278_cu_() - te.getPos().getY() - 0.75D, te.entityBeingTargeted.func_226281_cx_() - te.getPos().getZ() - 0.5D).func_225586_a_(255, 0, 0, 1).endVertex();
		Tessellator.getInstance().draw();

		RenderSystem.enableLighting();
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}

}
