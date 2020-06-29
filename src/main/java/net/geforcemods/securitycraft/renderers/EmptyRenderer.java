package net.geforcemods.securitycraft.renderers;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T>
{
	public EmptyRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public boolean shouldRender(T entity, ClippingHelper camera, double camX, double camY, double camZ)
	{
		return false;
	}

	@Override
	public ResourceLocation getEntityTexture(T entity)
	{
		return null;
	}
}