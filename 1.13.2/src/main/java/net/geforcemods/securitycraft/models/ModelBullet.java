package net.geforcemods.securitycraft.models;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBullet extends ModelBase
{
	public ModelRenderer bullet;

	public ModelBullet()
	{
		textureWidth = 8;
		textureHeight = 4;
		bullet = new ModelRenderer(this, 0, 0);
		bullet.setRotationPoint(0.0F, 0.0F, 0.0F);
		bullet.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		bullet.render(scale);
	}
}
