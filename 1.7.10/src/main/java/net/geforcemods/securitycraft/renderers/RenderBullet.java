package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.models.ModelBullet;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBullet extends Render
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final ModelBullet MODEL = new ModelBullet();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		bindEntityTexture(entity);
		MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return TEXTURE;
	}
}
