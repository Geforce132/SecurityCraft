package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.models.BulletModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class BulletRenderer extends Render<Bullet> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final BulletModel MODEL = new BulletModel();

	public BulletRenderer(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(Bullet entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		bindEntityTexture(entity);
		MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Bullet entity) {
		return TEXTURE;
	}
}
