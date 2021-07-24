package net.geforcemods.securitycraft.renderers;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T>
{
	public EmptyRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx);
	}

	@Override
	public boolean shouldRender(T entity, Frustum camera, double camX, double camY, double camZ)
	{
		return false;
	}

	@Override
	public ResourceLocation getTextureLocation(T entity)
	{
		return null;
	}
}