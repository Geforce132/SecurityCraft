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
		this.doRender((EntityIMSBomb) entity, x, y, z, entityYaw, partialTicks);
	}

	public void doRender(EntityIMSBomb ims, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float)x - 0.1F, (float)y, (float)z - 0.1F);
		bindEntityTexture(ims);
		GL11.glScalef(1.4F, 1.4F, 1.4F);
		modelBomb.render(ims, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
