package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.models.ModelBullet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBullet extends Render<EntityBullet>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final ModelBullet MODEL = new ModelBullet();

	public RenderBullet(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void doRender(EntityBullet entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		bindEntityTexture(entity);
		MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBullet entity)
	{
		return TEXTURE;
	}
}
