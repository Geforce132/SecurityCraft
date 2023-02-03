package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TrophySystemRenderer extends TileEntitySpecialRenderer<TrophySystemBlockEntity> {
	@Override
	public void render(TrophySystemBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (BlockEntityRenderDelegate.DISGUISED_BLOCK.tryRenderDelegate(te, x, y, z, partialTicks, destroyStage, alpha))
			return;

		if (te.entityBeingTargeted == null)
			return;

		Vec3d blockpos = new Vec3d(x + 0.5D, y + 0.75D, z + 0.5D);

		GlStateManager.pushMatrix();
		GlStateManager.translate(blockpos.x, blockpos.y, blockpos.z);
		GlStateManager.glLineWidth(2F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		//draws a line between the trophy system and the projectile that it's targeting
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0, 0, 0).color(1, 0, 0, 1F).endVertex();
		bb.pos(te.entityBeingTargeted.posX - te.getPos().getX() - 0.5D, te.entityBeingTargeted.posY - te.getPos().getY() - 0.75D, te.entityBeingTargeted.posZ - te.getPos().getZ() - 0.5D).color(1, 0, 0, 1F).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(TrophySystemBlockEntity te) {
		return true;
	}
}
