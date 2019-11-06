package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.models.BulletModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BulletRenderer extends EntityRenderer<BulletEntity>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final BulletModel MODEL = new BulletModel();

	public BulletRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void doRender(BulletEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);
		GlStateManager.rotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		bindEntityTexture(entity);
		MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(BulletEntity entity)
	{
		return TEXTURE;
	}
}
