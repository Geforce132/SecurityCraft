package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.models.ModelSentry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSentry extends EntityRenderer<EntitySentry>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/sentry.png");
	private static final ModelSentry MODEL = new ModelSentry();

	public RenderSentry(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void func_76986_a(EntitySentry entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y + 1.5F, z);
		GlStateManager.scalef(-1, -1, 1); //rotate model rightside up
		bindEntityTexture(entity);
		MODEL.func_78088_a(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation func_110775_a(EntitySentry entity)
	{
		return TEXTURE;
	}
}
