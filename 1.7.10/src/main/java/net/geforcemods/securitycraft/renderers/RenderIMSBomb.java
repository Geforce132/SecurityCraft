package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.models.ModelIMSBomb;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderIMSBomb extends Render {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/entity/imsBomb.png");

	/** instance of ModelIMSBomb for rendering */
	protected static final ModelIMSBomb modelBomb = new ModelIMSBomb();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}

	public void doRender(EntityIMSBomb ims, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float)p_76986_2_ - 0.1F, (float)p_76986_4_, (float)p_76986_6_ - 0.1F);
		bindEntityTexture(ims);
		GL11.glScalef(1.4F, 1.4F, 1.4F);
		modelBomb.render(ims, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(Entity entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityIMSBomb) entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}

}
