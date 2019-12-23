package net.geforcemods.securitycraft.renderers;

import net.minecraft.client.renderer.culling.ClippingHelperImpl;
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
	public boolean func_225626_a_(T entity, ClippingHelperImpl p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) //shouldRender
	{
		return false;
	}

	@Override
	public ResourceLocation getEntityTexture(T entity)
	{
		return null;
	}
}