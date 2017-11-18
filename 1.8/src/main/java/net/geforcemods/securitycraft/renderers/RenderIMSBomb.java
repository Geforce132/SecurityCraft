package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.models.ModelIMSBomb;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderIMSBomb extends Render {

	private static final ResourceLocation imsTexture = new ResourceLocation("securitycraft:textures/entity/imsBomb.png");

	/** instance of ModelIMSBomb for rendering */
	protected ModelIMSBomb modelBomb;

	public RenderIMSBomb(RenderManager renderManager){
		super(renderManager);
		modelBomb = new ModelIMSBomb();
	}

	public void doRender(EntityIMSBomb par1EntityIMSBomb, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float)p_76986_2_ - 0.1F, (float)p_76986_4_, (float)p_76986_6_ - 0.1F);
		bindEntityTexture(par1EntityIMSBomb);
		GL11.glScalef(1.4F, 1.4F, 1.4F);
		modelBomb.render(par1EntityIMSBomb, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

	protected ResourceLocation getEntityTexture(EntityIMSBomb p_110775_1_) {
		return imsTexture;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return this.getEntityTexture((EntityIMSBomb) p_110775_1_);
	}

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityIMSBomb) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}

}
