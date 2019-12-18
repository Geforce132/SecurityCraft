package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.models.SentryModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SentryRenderer extends EntityRenderer<SentryEntity>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/sentry.png");
	private static final SentryModel MODEL = new SentryModel();

	public SentryRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void doRender(SentryEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		RenderSystem.pushMatrix();
		RenderSystem.translated(x, y + 1.5F, z);
		RenderSystem.scalef(-1, -1, 1); //rotate model rightside up
		bindEntityTexture(entity);
		MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		RenderSystem.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(SentryEntity entity)
	{
		return TEXTURE;
	}
}
