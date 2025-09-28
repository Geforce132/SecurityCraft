package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.items.ColorableItem;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TrophySystemRenderer extends TileEntitySpecialRenderer<TrophySystemBlockEntity> {
	@Override
	public void render(TrophySystemBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockEntityRenderDelegate.DISGUISED_BLOCK.tryRenderDelegate(te, x, y, z, partialTicks, destroyStage, alpha);

		if (te.getTarget() == null)
			return;

		Vec3d blockpos = new Vec3d(x + 0.5D, y + 0.75D, z + 0.5D);
		ItemStack lens = te.getLensContainer().getStackInSlot(0);
		Item item = lens.getItem();
		int r = 255, g = 255, b = 255;

		if (item instanceof ColorableItem && ((ColorableItem) item).hasColor(lens)) {
			int color = ((ColorableItem) item).getColor(lens);

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(blockpos.x, blockpos.y, blockpos.z);
		GlStateManager.glLineWidth(2F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		//draws a line between the trophy system and the projectile that it's targeting
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0, 0, 0).color(r, g, b, 255).endVertex();
		bb.pos(te.getTarget().posX - te.getPos().getX() - 0.5D, te.getTarget().posY - te.getPos().getY() - 0.75D, te.getTarget().posZ - te.getPos().getZ() - 0.5D).color(r, g, b, 255).endVertex();
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
